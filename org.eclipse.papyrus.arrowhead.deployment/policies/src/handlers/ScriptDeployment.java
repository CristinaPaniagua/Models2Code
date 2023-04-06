package handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
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
					
			ArrayList<String[]> systemServiceRegistry = MP.getSystemServiceRegistry();
			ArrayList<APXInterfaceDesignDescription> interfaces = MP.getInterfaces(); // TODO Not Used
			ArrayList<APXLocalCloudDesignDescription> localClouds = MP.getLocalClouds();

			// Display Local Clouds and open a dialog window
			DialogWindow dialog = new DialogWindow(shell);
			dialog.setWorkDirectory(workspace);
			dialog.setLocalClouds(localClouds);

			if (dialog.open() == Window.OK) {

				if (!dialog.getBadDirectory()) {
					System.out.println("OK"); // TODO Remove Trace

					// Obtain information from dialog window
					directory = dialog.getDirectory();
					policyType = dialog.getPolicyType();
					System.out.println("POLICY TYPE: " + policyType); // TODO Remove Trace
					selectedLC = dialog.getSelectedLC();
					disk = dialog.getDisk();
					
					final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
					Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

					if (!(directory == null || directory.isEmpty())) {
						// Obtain information about selected local cloud
						System.out.println(localClouds.size()); // TODO Remove Trace
						String LCname = ParsingUtils.toKebabCase(localClouds.get(selectedLC).getName());
						ParsingUtils.newFolder(directory, "arrowhead");
						ParsingUtils.newFolder(directory + "/arrowhead/", LCname);
						ParsingUtils.newFolder(directory + "/arrowhead/" + LCname + "/", "db-rules");
						ArrayList<String[]> connectionsLC = localClouds.get(selectedLC).getConnections();
						System.out.println(selectedLC); // TODO Remove Trace
						for (int j = 0; j < connectionsLC.size(); j++) { // TODO Remove Trace
							System.out.println("connector: " + connectionsLC.get(j)[0]);
							System.out.println("service: " + connectionsLC.get(j)[1]);
							System.out.println("sys1: " + connectionsLC.get(j)[2]);
							System.out.println("sys2: " + connectionsLC.get(j)[3]);
						}

						// Initialise Velocity Engine
						VelocityEngine velocityEngine = new VelocityEngine();
						velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
						velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
						velocityEngine.init();

						// Order array so that second item is always the provider.
						for (int k = 0; k < connectionsLC.size(); k++)
							for (int m = 0; m < systemServiceRegistry.size(); m++) {
								String[] SSR = systemServiceRegistry.get(m);
								if (connectionsLC.get(k)[2].equals(SSR[0]) && connectionsLC.get(k)[1].equals(SSR[1])) {
									if (SSR[3].equalsIgnoreCase("consumer")) {
										String consumer = connectionsLC.get(k)[2];
										connectionsLC.get(k)[2] = connectionsLC.get(k)[3];
										connectionsLC.get(k)[3] = consumer;
									}
								}
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
