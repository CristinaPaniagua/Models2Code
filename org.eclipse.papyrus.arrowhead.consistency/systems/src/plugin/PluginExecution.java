package plugin;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.util.UMLUtil;

import deployment.CodgenUtil;
import deployment.ExecutionUtils;
import deployment.ProjectSelectWindow;
import dto.APXDeployedEntity;
import dto.APXInterfaceDesignDescription;
import dto.APXLocalCloudDesignDescription;
import dto.APXSystemDesignDescription;

import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.LocalCLoudDesignDescription;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.SysDD;

import plugin.modelling.DefinitionModeller;
import plugin.modelling.PhysicalModeller;
import plugin.parsing.model.DefinitionParser;
import plugin.parsing.model.PhysicalParser;
import plugin.parsing.workspace.ParsingUtils;

/**
 * 
 * The main function coordinates the parsing from workspace and model, the addition and update checking and the modelling of the 
 * new .uml file. There are two approaches to this implementation, the static approach consists of selecting a certain model and 
 * its relative code and perform the analysis whereas the dynamic approach generates a task that periodically will update 
 * the model.
 * 
 * @author fernand0labra
 *
 */
public class PluginExecution {
	
	//=================================================================================================
	// attributes
		
	// Local Cloud objects parsed from the model and workspace
	private ArrayList<APXLocalCloudDesignDescription> modelLocalCloudList = new ArrayList<APXLocalCloudDesignDescription>();
	public static ArrayList<APXLocalCloudDesignDescription> workspaceLocalCloudList = new ArrayList<APXLocalCloudDesignDescription>();
	
	// System Design Description objects parsed from the model and workspace
	public static HashMap<String, APXSystemDesignDescription> modelSystemDescriptionMap = new HashMap<String, APXSystemDesignDescription>();
	public static HashMap<String, APXSystemDesignDescription> workspaceSystemDescriptionMap = new HashMap<String, APXSystemDesignDescription>();
	
	// Interface Design Description objects parsed from the model and workspace
	public static HashMap<String, APXInterfaceDesignDescription> modelInterfaceDescriptionMap = new HashMap<String, APXInterfaceDesignDescription>();
	public static HashMap<String, APXInterfaceDesignDescription> workspaceInterfaceDescriptionMap = new HashMap<String, APXInterfaceDesignDescription>();
	
	// Packaged Model Elements
	public static HashMap<String, PackageableElement> packageLocalCloudDescriptionMap = new HashMap<String, PackageableElement>();
	public static HashMap<String, PackageableElement> packageSystemDescriptionMap = new HashMap<String, PackageableElement>();
	public static HashMap<String, PackageableElement> packageInterfaceDescriptionMap = new HashMap<String, PackageableElement>();
	
	// Configuration parameters of runtime-EclipseApplication
	private static deployment.TypeSafeProperties configuration = CodgenUtil.getProp("WorkSpaceConfiguration");
	private String workspace = configuration.getProperty("workspace");

	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	@Execute
	public void execute(Shell shell) throws Exception { 

		// ************************************************************************************************ //
		//										Pre-processing Steps										//
		// ************************************************************************************************ //

		IProject[] projects= ExecutionUtils.readWorkspace(); // Read projects from workspace
		Shell projectShell = null;
		ProjectSelectWindow projectWindow = new ProjectSelectWindow(projectShell);
		projectWindow.setProjects(projects);

		IPath projectLocation = null;
		String modelPath = "";

		if(projectWindow.open() == Window.OK) { // Select a project
			projectLocation = projectWindow.getSelectedProject().getLocation();
			String[] projectFiles = ParsingUtils.readWorkspace(projectLocation.toString(), false);
			
			for(String file : projectFiles)
				if(file.endsWith(".uml"))
					modelPath = projectLocation.toString() + "/" + file;

			if(modelPath.equals("")) // If there is no .uml file
				throw new Exception("The selected project does not have an .uml file.");
		}
		else // If there is no project
			throw new Exception("The selected project does not have an .uml file.");

		
		// ************************************************************************************************ //
		//										  Model Parsing												//
		// ************************************************************************************************ //

		// Open model file
		Object objectModel = plugin.parsing.model.ParsingUtils.loadModel(modelPath);
		if (!(objectModel instanceof Model))
			throw new Exception("The uml selected does not follow the modelling convention");
		
		// Obtain packaged elements
		EList<PackageableElement> sourcePackagedElements = null;
		sourcePackagedElements = ((Model) objectModel).getPackagedElements();

		for (PackageableElement element : sourcePackagedElements)  // For each block

			if (element instanceof Classifier) {
				Classifier classifier = (Classifier) element;
				
				if (classifier instanceof Class) {
					// Check if the block implements the local cloud stereotype			
					if(UMLUtil.getStereotypeApplication(classifier, LocalCLoudDesignDescription.class) != null) {
						packageLocalCloudDescriptionMap.put(element.getName(), element);
						
						if(PhysicalModeller.localCloudDescriptionStereotypeList == null)
							// Save the stereotypes for block modelling
							PhysicalModeller.localCloudDescriptionStereotypeList = element.getAppliedStereotypes();
					}

					// Check if the block implements the system design stereotype				
					if(UMLUtil.getStereotypeApplication(classifier, SysDD.class) != null) {
						packageSystemDescriptionMap.put(element.getName(), element);
						
						if(DefinitionModeller.systemDescriptionStereotypeList == null)
							// Save the stereotypes for block modelling
							DefinitionModeller.systemDescriptionStereotypeList = element.getAppliedStereotypes();
					}

					// Check if the block implements the interface design stereotype
					if(UMLUtil.getStereotypeApplication(classifier, org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.InterfaceDesignDescription.class) != null) {
						packageInterfaceDescriptionMap.put(element.getName(), element);
						
						if(DefinitionModeller.interfaceDescriptionStereotypeList == null)
							// Save the stereotypes for block modelling
							DefinitionModeller.interfaceDescriptionStereotypeList = element.getAppliedStereotypes();
					}
				}
			}

		// Parse the Interface Design Descriptions
		for(Entry<String, PackageableElement> packageInterface : packageInterfaceDescriptionMap.entrySet())
			if(!modelInterfaceDescriptionMap.containsKey(packageInterface.getKey())) // If the interface is not saved
				modelInterfaceDescriptionMap.put(packageInterface.getKey(), DefinitionParser.parseInterface(packageInterface.getValue()));
		
		// Parse the System Design Descriptions using the Interface Design Descriptions
		for(Entry<String, PackageableElement> packageSystem : packageSystemDescriptionMap.entrySet())
			if(!modelSystemDescriptionMap.containsKey(packageSystem.getKey())) // If the system is not saved
				modelSystemDescriptionMap.put(packageSystem.getKey(), DefinitionParser.parseSystem(packageSystem.getValue()));
		
		// Parse the LocalCloudDescriptions using the SystemDescriptions
		for(Entry<String, PackageableElement> packageLocalCloud : packageLocalCloudDescriptionMap.entrySet())
			modelLocalCloudList.add(PhysicalParser.parseLocalCloud(packageLocalCloud.getValue()));		


		// ************************************************************************************************ //
		//										  Workspace Parsing											//
		// ************************************************************************************************ //

		// Identify local clouds in workspace
		ArrayList<String> localCloudsImplementation = new ArrayList<String>();
		for (String project : plugin.parsing.workspace.ParsingUtils.readWorkspace(workspace, true))
			if(project.endsWith("_ApplicationSystems"))
				localCloudsImplementation.add(project);

		// For each local cloud (project folder)
		for (String localCloud : localCloudsImplementation)
			workspaceLocalCloudList.add(plugin.parsing.workspace.PhysicalParser.parseLocalCloud(workspace, localCloud));

		System.out.println(workspaceLocalCloudList); // TODO Remove trace
		System.out.println(modelLocalCloudList); // TODO Remove trace
		
		// ************************************************************************************************ //
		//								 	 Definition Additions Check										//
		// ************************************************************************************************ //
		
		ArrayList<APXSystemDesignDescription> modelSystemList = new ArrayList<APXSystemDesignDescription>(modelSystemDescriptionMap.values());
		ArrayList<APXSystemDesignDescription> workspaceSystemList = new ArrayList<APXSystemDesignDescription>(workspaceSystemDescriptionMap.values());
		
		ArrayList<APXInterfaceDesignDescription> modelInterfaceList = new ArrayList<APXInterfaceDesignDescription>(modelInterfaceDescriptionMap.values());
		ArrayList<APXInterfaceDesignDescription> workspaceInterfaceList = new ArrayList<APXInterfaceDesignDescription>(workspaceInterfaceDescriptionMap.values());
		
		// Interface Design Description
		int modelIndex = 0, workspaceIndex = 0;
		
		while(workspaceIndex < workspaceInterfaceList.size()) { // For each interface design description in the workspace
			modelIndex = 0;
			
			// While no interface design description model has the same name
			while(modelIndex < modelInterfaceList.size() && 
					!modelInterfaceList.get(modelIndex).getName().equals(workspaceInterfaceList.get(workspaceIndex).getName()))
				modelIndex++;

			if(modelIndex == modelInterfaceList.size()) { // If the workspace interface is not in the model
				// Create a packageable element from the workspace object
				PluginExecution.packageInterfaceDescriptionMap.put(
						workspaceInterfaceList.get(workspaceIndex).getName(),
						DefinitionModeller.addInterfaceDesignDescription(workspaceInterfaceList.get(workspaceIndex), (Model) objectModel));
				
				APXInterfaceDesignDescription newInterface = workspaceInterfaceList.get(workspaceIndex);
				
				// Save the workspace Interface Design Description object
				modelInterfaceList.add(newInterface);
				modelInterfaceDescriptionMap.put(newInterface.getName(), newInterface);
			}
				
			workspaceIndex++;
		}
		
		// System Design Description
		modelIndex = 0; workspaceIndex = 0;
		
		while(workspaceIndex < workspaceSystemList.size()) { // For each system design description in the workspace
			modelIndex = 0;
			
			// While no system design description model has the same name
			while(modelIndex < modelSystemList.size() && 
					!modelSystemList.get(modelIndex).getName().equals(workspaceSystemList.get(workspaceIndex).getName()))
				modelIndex++;

			if(modelIndex == modelSystemList.size()) { // If the workspace system is not in the model
				// Create a packageable element from the workspace object
				PluginExecution.packageSystemDescriptionMap.put(
						workspaceSystemList.get(workspaceIndex).getName(),
						DefinitionModeller.addSystemDesignDescription(workspaceSystemList.get(workspaceIndex), (Model) objectModel));
				
				APXSystemDesignDescription newSystem = workspaceSystemList.get(workspaceIndex);
				
				// Save the workspace System Design Description object
				modelSystemList.add(newSystem);
				modelSystemDescriptionMap.put(newSystem.getName(), newSystem);
			}
				
			workspaceIndex++;
		}
		
		// Local Cloud
		modelIndex = 0; workspaceIndex = 0;
		
		while(workspaceIndex < workspaceLocalCloudList.size()) { // For each local cloud design description in the workspace
			modelIndex = 0;
			
			// While no local cloud design description model has the same name
			while(modelIndex < modelLocalCloudList.size() && 
					!modelLocalCloudList.get(modelIndex).getName().equals(workspaceLocalCloudList.get(workspaceIndex).getName()))
				modelIndex++;

			if(modelIndex == modelLocalCloudList.size()) { // If the workspace local cloud is not in the model
				// Create a packageable element from the workspace object
				PluginExecution.packageLocalCloudDescriptionMap.put(
						workspaceLocalCloudList.get(workspaceIndex).getName(),
						PhysicalModeller.addLocalCloud(workspaceLocalCloudList.get(workspaceIndex), (Model) objectModel));
				
				// Save the workspace Local Cloud Description Object
				modelLocalCloudList.add(workspaceLocalCloudList.get(workspaceIndex));
			}
				
			workspaceIndex++;
		}
		
		// ************************************************************************************************ //
		//									  Definition Update Check										//
		// ************************************************************************************************ //
		
		// Order model and workspace local cloud list
		Comparator comparator = new APXLocalCloudDesignDescription.LocalCloudComparator();
		modelLocalCloudList.sort(comparator);
		workspaceLocalCloudList.sort(comparator);

		int localCloudIndex = 0;

		// TODO Currently assumed that there is the same number of local clouds
		while(localCloudIndex < workspaceLocalCloudList.size()) { // For each local cloud in the workspace
			APXLocalCloudDesignDescription modelLocalCloud = modelLocalCloudList.get(localCloudIndex);
			APXLocalCloudDesignDescription workspaceLocalCloud = workspaceLocalCloudList.get(localCloudIndex);		
			
			// Order model and workspace deployed entity list
			comparator = new APXDeployedEntity.DeployedEntityComparator();
			modelLocalCloud.getDeployedEntities().sort(comparator);
			workspaceLocalCloud.getDeployedEntities().sort(comparator);

			for(APXDeployedEntity workspaceEntity : workspaceLocalCloud.getDeployedEntities()) { // For each deployed entity in the workspace
				// If the deployed entity is not in the model
				if(!modelLocalCloud.getDeployedEntities().contains(workspaceEntity))
					// Add the deployed entity
					PhysicalModeller.addDeployedEntity(workspaceEntity, (Model) objectModel);
				
				else {
					// Check the basic attributes of the deployed entity
					APXDeployedEntity modelEntity = modelLocalCloud.getDeployedEntities().get(modelLocalCloud.getDeployedEntities().indexOf(workspaceEntity));
					if(modelEntity.checkConsistency(workspaceEntity))
						PhysicalModeller.updateInternalDeployedEntity(workspaceEntity, null); // TODO Update basic elements of block
					
					// Check the basic attributes of the system
					APXSystemDesignDescription modelSystem = modelEntity.getSysDD();
					APXSystemDesignDescription workspaceSystem = workspaceEntity.getSysDD();
					if(modelSystem.checkConsistency(workspaceSystem))// Update internal elements of block
						DefinitionModeller.updateInternalSystemDesignDescription(workspaceSystem, packageSystemDescriptionMap.get(modelSystem.getName()));
						
					comparator = new APXInterfaceDesignDescription.InterfaceComparator();
					modelSystem.getIDDs().sort(comparator);
					workspaceSystem.getIDDs().sort(comparator);
					
					for(APXInterfaceDesignDescription workspaceInterface : workspaceSystem.getIDDs()) { // For each interface in the workspace
						// If the interface is not in the model
						if(!modelSystem.getIDDs().contains(workspaceInterface)) {							
							// Add the interface
							DefinitionModeller.updateSystemDesignDescription(workspaceSystem.getIDDs(), packageSystemDescriptionMap.get(modelSystem.getName()));
							System.out.println(workspaceInterface); // TODO Remove trace
						}
						else {
							// Check the basic attributes of the interface
							APXInterfaceDesignDescription modelInterface = modelSystem.getIDDs().get(modelSystem.getIDDs().indexOf(workspaceInterface));
							if(modelInterface.checkConsistency(workspaceInterface)) // Update internal elements of block
								DefinitionModeller.updateInternalInterfaceDesignDescription(workspaceInterface, packageInterfaceDescriptionMap.get(modelInterface.getName()));
					
							comparator = modelInterface.getOperations().get(0) . new OperationComparator();
							modelInterface.getOperations().sort(comparator);
							workspaceInterface.getOperations().sort(comparator);
							
							for(APXInterfaceDesignDescription.APXServiceDescription workspaceService : workspaceInterface.getOperations()) {
								// If the operation is not in the model
								if(!modelInterface.getOperations().contains(workspaceService))
									// Add the operation
									DefinitionModeller.updateInterfaceDesignDescription(workspaceInterface.getOperations(), packageInterfaceDescriptionMap.get(modelInterface.getName()));
								
								else {
									// Check the basic attributes of the operation
									APXInterfaceDesignDescription.APXServiceDescription modelService = modelInterface.getOperations().get(modelInterface.getOperations().indexOf(workspaceService));
									modelService.checkConsistency(workspaceService); // TODO Update basic elements of block
								}
							}
						}
					}
				}
			}
			localCloudIndex ++;
		}
		
		plugin.parsing.model.ParsingUtils.saveModel(((Model) objectModel));
		
		// TODO Display information tab with identified changes (Static Analysis)
		// TODO If the user selected dynamic analysis generate a periodic task for code checking

	}

}
