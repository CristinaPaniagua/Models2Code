package handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import dialog.ModelSelectWindow;
import dialog.ProjectSelectWindow;
import dto.APXInterfaceDesignDescription;
import dto.APXLocalCloudDesignDescription;
import parsing.model.ModelParser;
import parsing.workspace.ParsingUtils;
import utils.CodgenUtil;
import utils.ExecutionUtils;

/**
 * 
 * Generation and Execution of Scripts
 * 
 * @author cripan
 *
 */
public class ScriptDeployment {

	// =================================================================================================
	// attributes
	
	protected static Shell shell;
	private static Properties configuration = CodgenUtil.getProperties("WorkSpaceConfiguration");
	
	private Text Directory; // TODO Not Used
	private String directory = "";
	private String policyType = "";
	private String disk = ""; // TODO Not Used
	private int selectedLC;
	private String workspace = configuration.getProperty("workspace");

	
	// =================================================================================================
	// methods
	
	// -------------------------------------------------------------------------------------------------
	@Execute
	public void execute(Shell shell) throws Exception {

		// Read workspace projects and show them in the dialog window
		IProject[] projects = ParsingUtils.readWorkspace();
		ProjectSelectWindow projWin = new ProjectSelectWindow(null);
		projWin.setProjects(projects);

		if (projWin.open() == Window.OK) {

			// Get selected project and display the contents
			IProject selectedProject = projWin.getSelectedProject();
			IPath projectLocation = selectedProject.getLocation();
			
			String[] projectFiles = ParsingUtils.readWorkspace(projectLocation.toString(), false);
			String selectedPathModel = "";
			
			for(String file : projectFiles)
				if(file.endsWith(".uml"))
					selectedPathModel = projectLocation.toString() + "/" + file;

			if(selectedPathModel.equals("")) // If there is no .uml file
				throw new Exception("The selected project does not have an .uml file.");

			// Read model and parse information
			ModelParser MP = new ModelParser();
			MP.modelReader(selectedPathModel);
					
			HashMap<String, HashMap<String, ArrayList<String>>> systemServiceRegistry = MP.getSystemServiceRegistry();
			ArrayList<APXInterfaceDesignDescription> interfaces = MP.getInterfaces(); // TODO Not Used
			ArrayList<APXLocalCloudDesignDescription> localClouds = MP.getLocalClouds();

			// Display Local Clouds and open a dialog window
			DialogWindow dialog = new DialogWindow(shell);
			dialog.setWorkDirectory(workspace);
			dialog.setLocalClouds(localClouds);

			if (dialog.open() == Window.OK) {

				if (!dialog.getBadDirectory()) {
					// Obtain information from dialog window
					directory = dialog.getDirectory();
					policyType = dialog.getPolicyType();
					selectedLC = dialog.getSelectedLC();
					disk = dialog.getDisk();
					
					final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
					Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

					if (!(directory == null || directory.isEmpty())) {
						// Obtain information about selected local cloud
						String LCname = ParsingUtils.toKebabCase(localClouds.get(selectedLC).getName());
						ParsingUtils.newFolder(directory, "arrowhead");
						ParsingUtils.newFolder(directory + "/arrowhead/", LCname);
						ParsingUtils.newFolder(directory + "/arrowhead/" + LCname + "/", "db-rules");
						ArrayList<ArrayList<String>> connectionsLC = new ArrayList<ArrayList<String>>(localClouds.get(selectedLC).getConnections().values());

						// Initialise Velocity Engine
						VelocityEngine velocityEngine = new VelocityEngine();
						velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
						velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
						velocityEngine.init();

						// TODO The priority needs to change when the interface ID, consumer ID and service ID are the same
						for (int k = 0; k < connectionsLC.size(); k++) {
							ArrayList<String> connectionEntry = connectionsLC.get(k);
							HashMap<String, ArrayList<String>> providerSystemServices = systemServiceRegistry.get(connectionEntry.get(2));
							HashMap<String, ArrayList<String>> consumerSystemServices = systemServiceRegistry.get(connectionEntry.get(3));

							String providerSystemBehavior = providerSystemServices.get("provider").isEmpty() ? "-consumer" : "-provider";
							String consumerSystemBehavior = consumerSystemServices.get("provider").isEmpty() ? "-consumer" : "-provider";
							
							connectionEntry.set(2, connectionEntry.get(2) + providerSystemBehavior);
							connectionEntry.set(3, connectionEntry.get(3) + consumerSystemBehavior);
						}
						
						// Orchestration DB Rule Generation
						if (policyType.equalsIgnoreCase("orchestration")) {
							
							Template t = velocityEngine.getTemplate("templates/orchstore.vm");
							VelocityContext context = new VelocityContext();
							context.put("connectionsLCs", connectionsLC);
									
							try {
								Writer writer = new FileWriter(directory + "/arrowhead/" + ParsingUtils.toKebabCase(LCname) + "/db-rules/orchstore-rules.sql");
								t.merge(context, writer);
								writer.flush();
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						} 
						
						// Security DB Rule Generation
						else { 

							Template t = velocityEngine.getTemplate("templates/security.vm");
							VelocityContext context = new VelocityContext();
							context.put("connectionsLCs", connectionsLC);

							try {
								Writer writer = new FileWriter(directory + "/arrowhead/" + ParsingUtils.toKebabCase(LCname) + "/db-rules/security-rules.sql");
								t.merge(context, writer);
								writer.flush();
								writer.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}

					} else // If the directory is not valid show dialog window again
						dialog.open();

					// Set back default class loader
					Thread.currentThread().setContextClassLoader(oldContextClassLoader);

				} else
					System.err.println("ERROR: Directory no correct"); // TODO Remove Trace
			}
		}
	}


	// =================================================================================================
	// auxiliary methods



}
