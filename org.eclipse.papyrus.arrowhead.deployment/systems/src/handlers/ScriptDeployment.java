package handlers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import deployment.CodgenUtil;
import deployment.ExecutionUtils;
import deployment.ModelParser;
import deployment.ProjectSelectWindow;
import deployment.TypeSafeProperties;
import dto.InterfaceMetadata;
import dto.LocalCloudDTO;
import generator.ApplicationProperties;
import generator.ConsumerAppList;
import generator.ConsumerMain;
import generator.ProviderMain;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;

import org.apache.commons.io.FileUtils;

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
	private static TypeSafeProperties configuration = CodgenUtil.getProp("WorkSpaceConfiguration");

	private String directory = "";
	private String name = "";
	private String language = ""; // TODO Not Used
	private Boolean mandatorySys = false; // TODO Not Used
	private Boolean supportSys = false; // TODO Not Used
	private String disk = "";
	private String[] selectedSys = null;
	private int[] selectedSysType = null;
	private int selectedLC;
	private String os = "";
	private String workspace = configuration.getProperty("workspace");

	
	// =================================================================================================
	// methods
	
	// -------------------------------------------------------------------------------------------------
	@Execute
	public void execute(Shell shell) throws Exception {

		// Read workspace projects and show them in the dialog window
		IProject[] projects = ExecutionUtils.readWorkspace();
		ProjectSelectWindow projWin = new ProjectSelectWindow(null);
		projWin.setProjects(projects);

		if (projWin.open() == Window.OK) {

			// Get selected project and display the contents
			IProject selectedProject = projWin.getSelectedProject();
			IPath projectLocation = selectedProject.getLocation();
			
			String[] projectFiles = ExecutionUtils.readWorkspace(projectLocation.toString(), false);
			String selectedPathModel = "";
			
			for(String file : projectFiles)
				if(file.endsWith(".uml"))
					selectedPathModel = projectLocation.toString() + "/" + file;

			if(selectedPathModel.equals("")) // If there is no .uml file
				throw new Exception("The selected project does not have an .uml file.");

			// Read model and parse information
			ModelParser MP = new ModelParser();
			MP.modelReader(selectedPathModel);

			ArrayList<InterfaceMetadata> interfaces = MP.getInterfaces();
			ArrayList<LocalCloudDTO> localClouds = MP.getLocalClouds();

			// Display Local Clouds and open a dialog window
			DialogWindow dialog = new DialogWindow(shell);
			dialog.setWorkDirectory(workspace);
			dialog.setLocalClouds(localClouds);

			if (dialog.open() == Window.OK) {

				if (!dialog.getBadDirectory()) {

					// Obtain information from dialog window
					directory = dialog.getDirectory();
					name = ExecutionUtils.toKebabCase(dialog.getName());
					selectedSys = dialog.getSelectedSys();
					selectedSysType = new int[selectedSys.length];
					selectedLC = dialog.getSelectedLC();
					disk = dialog.getDisk();
					os = dialog.getOs();
					
					// Obtain selected local cloud and the associated connections
					LocalCloudDTO LC= localClouds.get(selectedLC);
		            ArrayList<String []> systemServiceRegistry= LC.getSystemServiceRegistry();

					final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
					Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

					if (!(directory == null || directory.isEmpty())) {
						// Initialise Velocity Engine
						VelocityEngine velocityEngine = new VelocityEngine();
						velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
						velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
						velocityEngine.init();

						// Maven pom file generation
						Template tpom = velocityEngine.getTemplate("templates/general/pom.vm");
						VelocityContext context = new VelocityContext();
						context.put("name", name);
						context.put("outputDirectory", directory);

						VelocityContext contextpom = new VelocityContext();
						contextpom.put("name", name);
						String modules = "";
						String folders = "";
						String type = "";

						for (int j = 0; j < selectedSys.length; j++) { // For each of the systems
							for (int i = 0; i < localClouds.get(selectedLC).getSystems().size(); i++) {

								if (selectedSys[j].equals(localClouds.get(selectedLC).getSystems().get(i)[0])) {
									
									// Identify its type (provider/consumer/both)
									if (localClouds.get(selectedLC).getSystems().get(i)[1].equals("Provider")) {
										selectedSysType[j] = 0;
										type = "-provider";
									} else if (localClouds.get(selectedLC).getSystems().get(i)[1].equals("ProviderConsumer")) {
										selectedSysType[j] = 2;
										type = "-provider";
									} else {
										selectedSysType[j] = 1;
										type = "-consumer";
									}
								}
							}

							modules = modules + "    <module>" + ExecutionUtils.toKebabCase(selectedSys[j]) + type + "</module>\r\n";
							folders = os.equalsIgnoreCase("linux") || os.equalsIgnoreCase("mac") 
									? folders + "mkdir " + ExecutionUtils.toKebabCase(selectedSys[j]) + type + "\n"
									: folders + "mkdir " + ExecutionUtils.toKebabCase(selectedSys[j]) + type + "\r\n";
						}
						context.put("createFolders", folders);
						contextpom.put("modules", modules);
						
						String scriptPath = "";
						FileUtils.forceMkdir(new File(workspace + "\\.temp\\"));						
						
						try {

							if (os.equalsIgnoreCase("linux") || os.equalsIgnoreCase("mac")) { // Linux Folder Generation
								scriptPath = workspace + "\\.temp\\folderGenUnix.sh";
								
								Template t = velocityEngine.getTemplate("templates/general/folderGenUnix.vm");
								Writer writer = new FileWriter(scriptPath);
								context.put("workspace", workspace);
								t.merge(context, writer);
								writer.flush();
								writer.close();

								ExecutionUtils.executesh(workspace + "\\.temp\\", "folderGenUnix.sh");
							} 
							
							else { // Windows Folder Generation
								scriptPath = workspace + "\\.temp\\folderGenWin.bat";
								
								Writer writer = new FileWriter(scriptPath);
								Template t = velocityEngine.getTemplate("templates/general/folderGenWin.vm");
								context.put("workspace", workspace);
								context.put("disk", disk);
								t.merge(context, writer);
								writer.flush();
								writer.close();

								ExecutionUtils.executebat(scriptPath);
							}

							String kebabCaseSystem = ExecutionUtils.toKebabCase(selectedSys[selectedSys.length - 1]);
							
							// While the local cloud directory hasn't been created wait
							while (!new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + kebabCaseSystem + type + "\\").exists()) {}

							Writer writerpom = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\pom.xml"));
							tpom.merge(contextpom, writerpom);
							writerpom.flush();
							writerpom.close();
							
							// Project file generation
							Template tProject = velocityEngine.getTemplate("templates/general/cloudProject.vm");
							VelocityContext projectContext = new VelocityContext();
							projectContext.put("name", dialog.getName());
							Writer writerProject = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\.project"));
							tProject.merge(projectContext, writerProject);
							writerProject.flush();
							writerProject.close();
							
							for (int j = 0; j < selectedSys.length; j++) {
								
								// If the system is a provider
								if (selectedSysType[j] == 0) {
									
									// Maven pom file generation
									Template tpomPro = velocityEngine.getTemplate("templates/general/pomProvider.vm");
									VelocityContext contextpomPro = new VelocityContext();
									contextpomPro.put("name", name);
									contextpomPro.put("sysName", ExecutionUtils.toKebabCase(selectedSys[j]));
									Writer writerpomPro = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\pom.xml"));
									tpomPro.merge(contextpomPro, writerpomPro);
									writerpomPro.flush();
									writerpomPro.close();

									// Folder Structure Generation
									VelocityContext contextFoldPro = new VelocityContext();
									contextFoldPro.put("outputDirectory", directory);
									contextFoldPro.put("name", name);
									contextFoldPro.put("disk", disk);
									contextFoldPro.put("workspace", workspace);
									contextFoldPro.put("sysName", ExecutionUtils.toKebabCase(selectedSys[j]));

									if (os.equalsIgnoreCase("linux") || os.equalsIgnoreCase("mac")) { // Linux Provider Folder Generation
										scriptPath = workspace + "\\.temp\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "ProviderStructure.sh";
										
										Writer writerFoldPro = new FileWriter(new File(scriptPath));
										Template tFoldPro = velocityEngine.getTemplate("templates/provider/structureUnix.vm");
										tFoldPro.merge(contextFoldPro, writerFoldPro);
										writerFoldPro.flush();
										writerFoldPro.close();
										
										ExecutionUtils.executesh(workspace + "\\.temp\\", ExecutionUtils.toKebabCase(selectedSys[j]) + "ProviderStructure.sh");
									} 
									else { // Windows Provider Folder Generation
										scriptPath = workspace + "\\.temp\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "ProviderStructure.bat";
										
										contextFoldPro.put("disk", disk);
										Writer writerFoldPro = new FileWriter(new File(scriptPath));
										Template tFoldPro = velocityEngine.getTemplate("templates/provider/structureWin.vm");
										tFoldPro.merge(contextFoldPro, writerFoldPro);
										writerFoldPro.flush();
										writerFoldPro.close();
										
										ExecutionUtils.executebat(scriptPath);
									}
									
									while (!new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\dto\\").exists()) {}
									
									// Project file generation
									tProject = velocityEngine.getTemplate("templates/general/systemProject.vm");
									projectContext.put("name", selectedSys[j] + "Provider");
									writerProject = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\.project"));
									tProject.merge(projectContext, writerProject);
									writerProject.flush();
									writerProject.close();
									
									// Generate the Provider Main
									ProviderMain.generateProviderMain(directory, name, selectedSys[j], systemServiceRegistry, interfaces);
									// Generate the Application Properties
									ApplicationProperties.GenerateAppProperties(directory, name, ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider", "provider");

									// Security files generation
									VelocityContext contextSecurity = new VelocityContext();
									Writer writerSecurity1 = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\security\\ProviderTokenSecurityFilter.java"));
									Template tsec1 = velocityEngine.getTemplate("templates/provider/tokenSecurityFilter.vm");
									tsec1.merge(contextSecurity, writerSecurity1);
									writerSecurity1.flush();
									writerSecurity1.close();

									Writer writerSecurity2 = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\security\\ProviderSecurityConfig.java"));
									Template tsec2 = velocityEngine.getTemplate("templates/provider/securityConfig.vm");
									tsec2.merge(contextSecurity, writerSecurity2);
									writerSecurity2.flush();
									writerSecurity2.close();

									Writer writerSecurity3 = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\security\\ProviderAccessControlFilter.java"));
									Template tsec3 = velocityEngine.getTemplate("templates/provider/accessControlFilter.vm");
									tsec3.merge(contextSecurity, writerSecurity3);
									writerSecurity3.flush();
									writerSecurity3.close();

								} 
								
								// If the system is a provider/consumer
								else if (selectedSysType[j] == 2) {
									
									// Maven pom file generation
									Template tpomPro = velocityEngine.getTemplate("templates/general/pomProvider.vm");
									VelocityContext contextpomPro = new VelocityContext();
									contextpomPro.put("name", name);
									contextpomPro.put("sysName", ExecutionUtils.toKebabCase(selectedSys[j]));
									Writer writerpomPro = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\pom.xml"));
									tpomPro.merge(contextpomPro, writerpomPro);
									writerpomPro.flush();
									writerpomPro.close();

									// Folder Structure Generation
									VelocityContext contextFoldPro = new VelocityContext();
									contextFoldPro.put("outputDirectory", directory);
									contextFoldPro.put("name", name);
									contextFoldPro.put("disk", disk);
									contextFoldPro.put("workspace", workspace);
									contextFoldPro.put("sysName", ExecutionUtils.toKebabCase(selectedSys[j]));

									if (os.equalsIgnoreCase("linux") || os.equalsIgnoreCase("mac")) { // Linux Provider/Consumer Folder Generation
										scriptPath = workspace + "\\.temp\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "ProviderStructure.sh";
												
										Writer writerFoldPro = new FileWriter(new File(scriptPath));
										Template tFoldPro = velocityEngine.getTemplate("templates/provider/structureUnix.vm");
										tFoldPro.merge(contextFoldPro, writerFoldPro);
										writerFoldPro.flush();
										writerFoldPro.close();
										
										ExecutionUtils.executesh(workspace + "\\.temp\\", ExecutionUtils.toKebabCase(selectedSys[j]) + "ProviderStructure.sh");
									} 
									
									else { // Windows Provider/Consumer Folder Generation
										scriptPath = workspace + "\\.temp\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "ProviderStructure.bat";
										
										contextFoldPro.put("disk", disk);
										Writer writerFoldPro = new FileWriter(new File(scriptPath));
										Template tFoldPro = velocityEngine.getTemplate("templates/provider/structureWin.vm");
										tFoldPro.merge(contextFoldPro, writerFoldPro);
										writerFoldPro.flush();
										writerFoldPro.close();
										ExecutionUtils.executebat(scriptPath);
									}

									// While the provider/consumer directories haven't been created wait
									while (!new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\dto\\").exists()) {}
					
									// Project file generation
									tProject = velocityEngine.getTemplate("templates/general/systemProject.vm");
									projectContext.put("name", selectedSys[j] + "ProviderConsumer");
									writerProject = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\.project"));
									tProject.merge(projectContext, writerProject);
									writerProject.flush();
									writerProject.close();

									// Generate Provider/Consumer Main
									ProviderMain.generateProvConsMain(directory, name, selectedSys[j], systemServiceRegistry, interfaces);
									// Generate Application Properties
									ApplicationProperties.GenerateAppProperties(directory, name, ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider", "provider");
									
									// Security files generation
									VelocityContext contextSecurity = new VelocityContext();
									Writer writerSecurity1 = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\security\\ProviderTokenSecurityFilter.java"));
									Template tsec1 = velocityEngine.getTemplate("templates/provider/tokenSecurityFilter.vm");
									tsec1.merge(contextSecurity, writerSecurity1);
									writerSecurity1.flush();
									writerSecurity1.close();

									Writer writerSecurity2 = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\security\\ProviderSecurityConfig.java"));
									Template tsec2 = velocityEngine.getTemplate("templates/provider/securityConfig.vm");
									tsec2.merge(contextSecurity, writerSecurity2);
									writerSecurity2.flush();
									writerSecurity2.close();

									Writer writerSecurity3 = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\security\\ProviderAccessControlFilter.java"));
									Template tsec3 = velocityEngine.getTemplate("templates/provider/accessControlFilter.vm");
									tsec3.merge(contextSecurity, writerSecurity3);
									writerSecurity3.flush();
									writerSecurity3.close();
								} 
								
								// If the system is a consumer
								else {
									
									// Maven pom file generation
									Template tpomcon = velocityEngine.getTemplate("templates/general/pomConsumer.vm");
									VelocityContext contextpomCons = new VelocityContext();
									contextpomCons.put("name", name);
									contextpomCons.put("sysName", ExecutionUtils.toKebabCase(selectedSys[j]));
									Writer writerpomCons = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-consumer\\pom.xml"));
									tpomcon.merge(contextpomCons, writerpomCons);
									writerpomCons.flush();
									writerpomCons.close();

									// Folder Structure Generation
									VelocityContext contextFoldCon = new VelocityContext();
									contextFoldCon.put("outputDirectory", directory);
									contextFoldCon.put("name", name);
									contextFoldCon.put("disk", disk);
									contextFoldCon.put("workspace", workspace);
									contextFoldCon.put("sysName", ExecutionUtils.toKebabCase(selectedSys[j]));

									if (os.equalsIgnoreCase("linux") || os.equalsIgnoreCase("mac")) { // Linux Consumer Folder Generation
										scriptPath = workspace + "\\.temp\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "ConsumerStructure.sh";
										
										Template tFoldCon = velocityEngine.getTemplate("templates/consumer/structureUnix.vm");
										Writer writerFoldCon = new FileWriter(new File(scriptPath));
										tFoldCon.merge(contextFoldCon, writerFoldCon);
										writerFoldCon.flush();
										writerFoldCon.close();
										
										ExecutionUtils.executesh(workspace + "\\.temp\\", ExecutionUtils.toKebabCase(selectedSys[j]) + "ConsumerStructure.sh");
									} else { // Windows Consumer Folder Generation
										scriptPath = workspace + "\\.temp\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "ConsumerStructure.bat";
										
										Template tFoldCon = velocityEngine.getTemplate("templates/consumer/structureWin.vm");
										Writer writerFoldCon = new FileWriter(new File(scriptPath));
										contextFoldCon.put("disk", disk);
										tFoldCon.merge(contextFoldCon, writerFoldCon);
										writerFoldCon.flush();
										writerFoldCon.close();
										
										ExecutionUtils.executebat(scriptPath);
									}

									// While the provider/consumer directories haven't been created wait
									while (!new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-consumer\\src\\main\\java\\eu\\arrowhead\\consumer\\").exists()) {}
									
									// new File(scriptPath).delete();
									
									// Project file generation
									tProject = velocityEngine.getTemplate("templates/general/systemProject.vm");
									projectContext.put("name", selectedSys[j] + "Consumer");
									writerProject = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(selectedSys[j]) + "-consumer\\.project"));
									tProject.merge(projectContext, writerProject);
									writerProject.flush();
									writerProject.close();
									
									// Generate Application Listener
									ConsumerAppList.GenerateAppList(directory, name, ExecutionUtils.toKebabCase(selectedSys[j]) + "-consumer");
									// Generate Consumer Main
									ConsumerMain.generateConsumerMain(directory, name, selectedSys[j], systemServiceRegistry, interfaces);
									// Generate Application Properties
									ApplicationProperties.GenerateAppProperties(directory, name, ExecutionUtils.toKebabCase(selectedSys[j]) + "-consumer", "consumer");
								}
							}
						
							FileUtils.forceDelete(new File(workspace + "\\.temp\\"));
						} catch (IOException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else // If the directory is not valid show dialog window again
						dialog.open();

					// Set back default class loader
					Thread.currentThread().setContextClassLoader(oldContextClassLoader);

				}
			}
		}
	}
}
