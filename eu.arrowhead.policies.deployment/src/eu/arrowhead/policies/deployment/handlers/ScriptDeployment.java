 package eu.arrowhead.policies.deployment.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

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

import eu.arrowhead.policies.deployment.dto.InterfaceMetadata;
import eu.arrowhead.policies.deployment.dto.LocalCloudDTO;


public class ScriptDeployment {

	protected static Shell shell;
	private Text Directory;
	private String directory = "";
	private String policyType = "";
	private String disk = "";
	private int selectedLC;

	 @Execute
	    public void execute(Shell shell) {
	
		 
		 IProject[] projects= readWorkspace();
		 Shell pshell = null;
		 ProjectSelecWindow projWin= new ProjectSelecWindow(pshell);
		 projWin.setProjects(projects);
		 
		
		 
		 if(projWin.open()==Window.OK) {
			 
			 	 
			IProject selectedProject=projWin.getSelectedProject() ;
			IPath projectLocation=selectedProject.getLocation();
			 ModelParser MP= new ModelParser();
			 MP.modelReader(projectLocation.toString()+"/"+selectedProject.getName()+".uml");
			 ArrayList<String []> systemServiceRegistry= MP.getSystemServiceRegistry();
			 ArrayList<InterfaceMetadata> interfaces= MP.getInterfaces();
			 ArrayList<LocalCloudDTO> localClouds= MP.getLocalClouds();	 
			 
			
		 DialogWindow dialog= new DialogWindow(shell);	
		 dialog.setLocalClouds(localClouds);
		 
         if (dialog.open() == Window.OK) {
        	 
        	 
        	 if(!dialog.getBadDirectory()){	 
            System.out.println("OK");
             
            directory=dialog.getDirectory();
            policyType=dialog.getPolicyType();
            System.out.println("POLICY TYPE: "+ policyType);
            selectedLC=dialog.getSelectedLC();
            disk=dialog.getDisk();
            final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

			if(!(directory == null || directory.isEmpty())) {
				System.out.println(localClouds.size());
				for(int i=0; i<localClouds.size();i++) {
					if(i==selectedLC) {
						String LCname=localClouds.get(i).getLcName();
						 newFolder(directory, LCname+"_Policies");
						ArrayList<String []> connectionsLC=localClouds.get(i).getConnections();
						System.out.println(selectedLC);
						for(int j=0; j<connectionsLC.size();j++) {
							System.out.println("connector: "+connectionsLC.get(j)[0]);
							System.out.println("service: "+connectionsLC.get(j)[1]);
							System.out.println("sys1: "+connectionsLC.get(j)[2]);
							System.out.println("sys2: "+connectionsLC.get(j)[3]);
						
						}
						
						 VelocityEngine velocityEngine = new VelocityEngine();

						   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
						   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
						   velocityEngine.init();
						   Template t =velocityEngine.getTemplate("templates/orchPolicy.vm");
						 
						   //ORDER THE ARRAY SO 2 item is always provider. 
						for(int k=0; k<connectionsLC.size();k++) {
						   for(int m=0; m<systemServiceRegistry.size();m++) {
							   String [] SSR= systemServiceRegistry.get(m);
							   if(connectionsLC.get(k)[2].equals(SSR[0])&& connectionsLC.get(k)[1].equals(SSR[1])) {
								   if(SSR[3].equalsIgnoreCase("consumer")) {
									   String consumer=connectionsLC.get(k)[2];
									   connectionsLC.get(k)[2]=connectionsLC.get(k)[3];
									   connectionsLC.get(k)[3]=consumer;
								   }
							   }
						   }
							
							}
						       VelocityContext context = new VelocityContext();
						       context.put( "connectionsLCs",  connectionsLC);
						      // context.put("serviceName", connectionsLC.get(0)[1]);
						       
						       //context.put("providerName", connectionsLC.get(0)[2]); 
						      // context.put("consumerName", connectionsLC.get(0)[3]);
						      
						       try{
						    	   Writer writer = new FileWriter (directory+"\\"+LCname+"_Policies\\"+ LCname+"_OrchStorePolicy.sql");
						    	   t.merge(context, writer);
						           writer.flush();
						           writer.close();
						       } catch (IOException e) {
					        	   e.printStackTrace();
						         } 
					}
				}
				//GENERATION OF THE DATABASE SCRIPT
				
			            
			}else dialog.open();
			
        	

			// set back default class loader
	         Thread.currentThread().setContextClassLoader(oldContextClassLoader);
			
            }else System.out.println("Directory no correct");
         }
		 }
	 }
	 

	   //EXECUTE BAT
	   
	   public static void executebat(String directory) throws InterruptedException, IOException {
	        
	       
	       ExecutorService executor = Executors.newSingleThreadExecutor();
	        ProcessBuilder processBuilder = new ProcessBuilder();
	        System.out.println("Script generated");
	        processBuilder.command(directory);
	        
	        try {

	            Process process = processBuilder.start();
	            System.out.println("Script executed");
	            executor.submit(new ProcessTask(process.getInputStream()));
	           

	           
	        } finally {
	            executor.shutdown();
	        }
	    
	      
	   }
	   
	   
	   
	   private static class ProcessTask implements Callable<List<String>> {

	        private InputStream inputStream;

	        public ProcessTask(InputStream inputStream) {
	            this.inputStream = inputStream;
	        }

	        @Override
	        public List<String> call() {
	            return new BufferedReader(new InputStreamReader(inputStream))
	                    .lines()
	                    .collect(Collectors.toList());
	        }
	    }
	
	   
		public IProject[] readWorkspace()  {
		    // Get the root of the workspace
		    IWorkspace workspace = ResourcesPlugin.getWorkspace();
		    IWorkspaceRoot root = workspace.getRoot();
		    // Get all projects in the workspace
		    IProject[] projects = root.getProjects();
		    // Loop over all projects
		    IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		    String workspacePath=  path.toString();
		    System.out.println("Projects in the workspace:"+workspacePath);
		    for (IProject project : projects) {
		        System.out.println(project.getName());
		        System.out.println(project.getLocation());
		    }
		   
		   return projects;
		} 
	


 public void newFolder(String Directory, String FolderName) {
     String path = Directory+"\\"+ FolderName;  
     //Instantiate the File class   
     File f1 = new File(path);  
     //Creating a folder using mkdir() method  
     boolean bool = f1.mkdir();  
     if(bool){  
        System.out.println("Folder is created successfully");  
     }else{  
        System.out.println("Error Found!");  
     }  
 }
	

	}

