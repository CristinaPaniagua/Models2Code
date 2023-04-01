package handlers;

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
import modelling.DefinitionModeller;
import modelling.PhysicalModeller;
import parsing.model.DefinitionParser;
import parsing.model.PhysicalParser;
import parsing.workspace.ParsingUtils;

import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.LocalCLoudDesignDescription;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.SysDD;

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

		Object objectModel = parsing.model.ParsingSetup.parseModel(modelPath);
		
		// Save the stereotypes for block modelling
		PhysicalModeller.localCloudDescriptionStereotypeList = parsing.model.ParsingSetup.packageLocalCloudDescriptionMap.
				values().iterator().next().getAppliedStereotypes();
		// Save the stereotypes for block modelling
		DefinitionModeller.systemDescriptionStereotypeList = parsing.model.ParsingSetup.packageSystemDescriptionMap.
				values().iterator().next().getAppliedStereotypes();
		// Save the stereotypes for block modelling
		DefinitionModeller.interfaceDescriptionStereotypeList = parsing.model.ParsingSetup.packageInterfaceDescriptionMap.
				values().iterator().next().getAppliedStereotypes();
		
		parsing.workspace.ParsingSetup.parseWorkspace(workspace);
		
		System.out.println(parsing.workspace.ParsingSetup.workspaceLocalCloudList); // TODO Remove trace
		System.out.println(parsing.model.ParsingSetup.modelLocalCloudList); // TODO Remove trace
		
		// ************************************************************************************************ //
		//								 	 Definition Additions Check										//
		// ************************************************************************************************ //
		
		ArrayList<APXSystemDesignDescription> modelSystemList = new ArrayList<APXSystemDesignDescription>(parsing.model.ParsingSetup.modelSystemDescriptionMap.values());
		ArrayList<APXSystemDesignDescription> workspaceSystemList = new ArrayList<APXSystemDesignDescription>(parsing.workspace.ParsingSetup.workspaceSystemDescriptionMap.values());
		
		ArrayList<APXInterfaceDesignDescription> modelInterfaceList = new ArrayList<APXInterfaceDesignDescription>(parsing.model.ParsingSetup.modelInterfaceDescriptionMap.values());
		ArrayList<APXInterfaceDesignDescription> workspaceInterfaceList = new ArrayList<APXInterfaceDesignDescription>(parsing.workspace.ParsingSetup.workspaceInterfaceDescriptionMap.values());
		
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
				parsing.model.ParsingSetup.packageInterfaceDescriptionMap.put(
						workspaceInterfaceList.get(workspaceIndex).getName(),
						DefinitionModeller.addInterfaceDesignDescription(workspaceInterfaceList.get(workspaceIndex), (Model) objectModel));
				
				APXInterfaceDesignDescription newInterface = workspaceInterfaceList.get(workspaceIndex);
				
				// Save the workspace Interface Design Description object
				modelInterfaceList.add(newInterface);
				parsing.model.ParsingSetup.modelInterfaceDescriptionMap.put(newInterface.getName(), newInterface);
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
				parsing.model.ParsingSetup.packageSystemDescriptionMap.put(
						workspaceSystemList.get(workspaceIndex).getName(),
						DefinitionModeller.addSystemDesignDescription(workspaceSystemList.get(workspaceIndex), (Model) objectModel));
				
				APXSystemDesignDescription newSystem = workspaceSystemList.get(workspaceIndex);
				
				// Save the workspace System Design Description object
				modelSystemList.add(newSystem);
				parsing.model.ParsingSetup.modelSystemDescriptionMap.put(newSystem.getName(), newSystem);
			}
				
			workspaceIndex++;
		}
		
		// Local Cloud
		modelIndex = 0; workspaceIndex = 0;
		
		while(workspaceIndex < parsing.workspace.ParsingSetup.workspaceLocalCloudList.size()) { // For each local cloud design description in the workspace
			modelIndex = 0;
			
			// While no local cloud design description model has the same name
			while(modelIndex < parsing.model.ParsingSetup.modelLocalCloudList.size() && 
					!parsing.model.ParsingSetup.modelLocalCloudList.get(modelIndex).getName().equals(parsing.workspace.ParsingSetup.workspaceLocalCloudList.get(workspaceIndex).getName()))
				modelIndex++;

			if(modelIndex == parsing.model.ParsingSetup.modelLocalCloudList.size()) { // If the workspace local cloud is not in the model
				// Create a packageable element from the workspace object
				parsing.model.ParsingSetup.packageLocalCloudDescriptionMap.put(
						parsing.workspace.ParsingSetup.workspaceLocalCloudList.get(workspaceIndex).getName(),
						PhysicalModeller.addLocalCloud(parsing.workspace.ParsingSetup.workspaceLocalCloudList.get(workspaceIndex), (Model) objectModel));
				
				// Save the workspace Local Cloud Description Object
				parsing.model.ParsingSetup.modelLocalCloudList.add(parsing.workspace.ParsingSetup.workspaceLocalCloudList.get(workspaceIndex));
			}
				
			workspaceIndex++;
		}
		
		// ************************************************************************************************ //
		//									  Definition Update Check										//
		// ************************************************************************************************ //
		
		// Order model and workspace local cloud list
		Comparator comparator = new APXLocalCloudDesignDescription.LocalCloudComparator();
		parsing.model.ParsingSetup.modelLocalCloudList.sort(comparator);
		parsing.workspace.ParsingSetup.workspaceLocalCloudList.sort(comparator);

		int localCloudIndex = 0;

		// TODO Currently assumed that there is the same number of local clouds
		while(localCloudIndex < parsing.workspace.ParsingSetup.workspaceLocalCloudList.size()) { // For each local cloud in the workspace
			APXLocalCloudDesignDescription modelLocalCloud = parsing.model.ParsingSetup.modelLocalCloudList.get(localCloudIndex);
			APXLocalCloudDesignDescription workspaceLocalCloud = parsing.workspace.ParsingSetup.workspaceLocalCloudList.get(localCloudIndex);		
			
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
						DefinitionModeller.updateInternalSystemDesignDescription(workspaceSystem, parsing.model.ParsingSetup.packageSystemDescriptionMap.get(modelSystem.getName()));
						
					comparator = new APXInterfaceDesignDescription.InterfaceComparator();
					modelSystem.getIDDs().sort(comparator);
					workspaceSystem.getIDDs().sort(comparator);
					
					for(APXInterfaceDesignDescription workspaceInterface : workspaceSystem.getIDDs()) { // For each interface in the workspace
						// If the interface is not in the model
						if(!modelSystem.getIDDs().contains(workspaceInterface)) {							
							// Add the interface
							DefinitionModeller.updateSystemDesignDescription(workspaceSystem.getIDDs(), parsing.model.ParsingSetup.packageSystemDescriptionMap.get(modelSystem.getName()));
							System.out.println(workspaceInterface); // TODO Remove trace
						}
						else {
							// Check the basic attributes of the interface
							APXInterfaceDesignDescription modelInterface = modelSystem.getIDDs().get(modelSystem.getIDDs().indexOf(workspaceInterface));
							if(modelInterface.checkConsistency(workspaceInterface)) // Update internal elements of block
								DefinitionModeller.updateInternalInterfaceDesignDescription(workspaceInterface, parsing.model.ParsingSetup.packageInterfaceDescriptionMap.get(modelInterface.getName()));
					
							comparator = modelInterface.getOperations().get(0) . new OperationComparator();
							modelInterface.getOperations().sort(comparator);
							workspaceInterface.getOperations().sort(comparator);
							
							for(APXInterfaceDesignDescription.APXServiceDescription workspaceService : workspaceInterface.getOperations()) {
								// If the operation is not in the model
								if(!modelInterface.getOperations().contains(workspaceService))
									// Add the operation
									DefinitionModeller.updateInterfaceDesignDescription(workspaceInterface.getOperations(), parsing.model.ParsingSetup.packageInterfaceDescriptionMap.get(modelInterface.getName()));
								
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
		
		parsing.model.ParsingUtils.saveModel(((Model) objectModel));
		
		// TODO Display information tab with identified changes (Static Analysis)
		// TODO If the user selected dynamic analysis generate a periodic task for code checking

	}

}
