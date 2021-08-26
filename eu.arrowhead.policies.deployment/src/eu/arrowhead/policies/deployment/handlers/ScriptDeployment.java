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
//import eu.arrowhead.skelettons.deployment.generator.AppPropertiesGen;
//import eu.arrowhead.skelettons.deployment.generator.ConsumerGenAppList;
//import eu.arrowhead.skelettons.deployment.generator.ConsumerGenMain;
//import eu.arrowhead.skelettons.deployment.generator.InterfaceMetadata;
//import eu.arrowhead.skelettons.deployment.generator.ProviderGenMain;

public class ScriptDeployment {

	protected static Shell shell;
	private Text Directory;
	private String directory = "";
	private String policyType = "";
	private String language = "";
	private Boolean mandatorySys = false;
	private Boolean supportSys = false;
	private String disk = "";
	private String[] selectedSys= null;
	private int[] selectedSysType= null;
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
            selectedSys=dialog.getSelectedSys();
            selectedSysType= new int[selectedSys.length];
            selectedLC=dialog.getSelectedLC();
            disk=dialog.getDisk();
            final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

			if(!(directory == null || directory.isEmpty())) {
			   
			            
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
	



	

	}

