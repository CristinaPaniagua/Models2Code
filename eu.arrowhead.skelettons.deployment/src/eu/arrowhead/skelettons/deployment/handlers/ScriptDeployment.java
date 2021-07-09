package eu.arrowhead.skelettons.deployment.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import eu.arrowhead.skelettons.deployment.handlers.ScriptDeployment;
import eu.arrowhead.skelettons.deployment.dto.LocalCloudDTO;
import eu.arrowhead.skelettons.deployment.generator.AppPropertiesGen;
import eu.arrowhead.skelettons.deployment.generator.ConsumerGenAppList;
import eu.arrowhead.skelettons.deployment.generator.ConsumerGenMain;
import eu.arrowhead.skelettons.deployment.generator.InterfaceMetadata;
import eu.arrowhead.skelettons.deployment.generator.ProviderGenMain;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class ScriptDeployment {

	protected static Shell shell;
	private Text Directory;
	private String directory = "";
	private String name = "";
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
            name=dialog.getName();
            selectedSys=dialog.getSelectedSys();
            selectedSysType= new int[selectedSys.length];
            selectedLC=dialog.getSelectedLC();
            disk=dialog.getDisk();
            final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

			if(!(directory == null || directory.isEmpty())) {
			   VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			   Template t =velocityEngine.getTemplate("templates/folderGen.vm");
			   Template tpom= velocityEngine.getTemplate("templates/pom.vm");
			   
			       VelocityContext context = new VelocityContext();
			       context.put("name", name);
			       context.put("outputDirectory", directory);
			       
			       VelocityContext contextpom = new VelocityContext();
			       contextpom.put("name", name);
			       String modules= "";
			       String folders= "";
			       String type="";
			       System.out.println("size systems:"+localClouds.get(selectedLC).getSystems().size());
		    	   System.out.println("size selection:"+selectedSys.length);
		    	   
			       for (int j=0; j<selectedSys.length;j++) {
			    	  
			    	   for (int i=0; i<localClouds.get(selectedLC).getSystems().size();i++) {
			    		   
			    		   if(selectedSys[j].equals(localClouds.get(selectedLC).getSystems().get(i)[0])) {
			    			   System.out.println(localClouds.get(selectedLC).getSystems().get(i)[0]+":"+localClouds.get(selectedLC).getSystems().get(i)[1]);
			    			   if(localClouds.get(selectedLC).getSystems().get(i)[1].equals("Provider")) {
			    				   selectedSysType[j]=0;
					    		   type="_Provider";
					    	   }else if(localClouds.get(selectedLC).getSystems().get(i)[1].equals("ProviderConsumer")) {
					    		   selectedSysType[j]=2;
					    		   type="_Provider";
					    	   }else {
					    		   selectedSysType[j]=1;
					    		   type="_Consumer";
					    	   }
			    		   }
			    	   }
			    	  
			    	   modules=modules+"    <module>"+selectedSys[j]+type+"</module>\r\n";
			    	   folders=folders+ "mkdir "+ selectedSys[j]+type+"\r\n";
			       }
			       context.put("createFolders",folders);
			       contextpom.put("modules", modules);
			       try{
			    	   Writer writer = new FileWriter ("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\folderGen.bat");
			    	   t.merge(context, writer);
			           writer.flush();
			           writer.close();
			           executebat("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\folderGen.bat");
			           
			         
			           while(!new File(directory+"\\"+name+"_ApplicationSystems").exists()) {}
			           
			    	   Writer writerpom = new FileWriter (new File(directory+"\\"+name+"_ApplicationSystems"+"\\pom.xml"));
					  
			           tpom.merge(contextpom, writerpom);
			           writerpom.flush();
			           writerpom.close();
			           
			           ConsumerGenAppList gen=new ConsumerGenAppList();
			           for (int j=0; j<selectedSys.length;j++) {
				    	   if(selectedSysType[j]==0) {
				    		   //pom
				    		   Template tpomPro=velocityEngine.getTemplate("templates/pomProvider.vm");
				    		   while(!new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Provider").exists()) {}
				    		   VelocityContext contextpomPro = new VelocityContext();
				    		   contextpomPro.put("name", name);
				    		   contextpomPro.put("sysName",selectedSys[j]);
				    		   Writer  writerpomPro = new FileWriter (new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Provider\\pom.xml"));
				    		   tpomPro.merge(contextpomPro,writerpomPro);
				    		   writerpomPro.flush();
				    		   writerpomPro.close();
				    		   
				    		   //Folder structure script
				    		   Template tFoldPro=velocityEngine.getTemplate("templates/providerStructure.vm");
				    		   VelocityContext contextFoldPro = new VelocityContext();
				    		   contextFoldPro.put("outputDirectory", directory);
				    		   contextFoldPro.put("name", name);
				    		   contextFoldPro.put("sysName",selectedSys[j]);
				    		   contextFoldPro.put("disk",disk);
				    		   Writer writerFoldPro = new FileWriter (new File("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat"));
				    		   tFoldPro.merge(contextFoldPro,writerFoldPro);
				    		   writerFoldPro.flush();
				    		   writerFoldPro.close();
				    		   executebat("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat");
				    		   while(!new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+selectedSys[j]+"_Provider").exists()) {}
				    		   //gen.GenerateAppList(directory, name,selectedSys[j]+"_Consumer");
				    		   ProviderGenMain genMainP =new ProviderGenMain();
				               genMainP.generateProviderMain(directory,name,selectedSys[j], systemServiceRegistry, interfaces);
				               AppPropertiesGen genpro = new AppPropertiesGen();
				               genpro.GenerateAppProperties(directory,name,selectedSys[j]+"_Provider","provider"); 
				    	   }else if(selectedSysType[j]==2) {
				    		 //pom
				    		   Template tpomPro=velocityEngine.getTemplate("templates/pomProvider.vm");
				    		   while(!new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Provider").exists()) {}
				    		   VelocityContext contextpomPro = new VelocityContext();
				    		   contextpomPro.put("name", name);
				    		   contextpomPro.put("sysName",selectedSys[j]);
				    		   Writer  writerpomPro = new FileWriter (new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Provider\\pom.xml"));
				    		   tpomPro.merge(contextpomPro,writerpomPro);
				    		   writerpomPro.flush();
				    		   writerpomPro.close();
				    		   
				    		   //Folder structure script
				    		   Template tFoldPro=velocityEngine.getTemplate("templates/providerStructure.vm");
				    		   VelocityContext contextFoldPro = new VelocityContext();
				    		   contextFoldPro.put("outputDirectory", directory);
				    		   contextFoldPro.put("name", name);
				    		   contextFoldPro.put("sysName",selectedSys[j]);
				    		   contextFoldPro.put("disk",disk);
				    		   Writer writerFoldPro = new FileWriter (new File("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat"));
				    		   tFoldPro.merge(contextFoldPro,writerFoldPro);
				    		   writerFoldPro.flush();
				    		   writerFoldPro.close();
				    		   executebat("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat");
				    		   while(!new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+selectedSys[j]+"_Provider").exists()) {}
				    		   //gen.GenerateAppList(directory, name,selectedSys[j]+"_Consumer");
				    		   ProviderGenMain genMainP =new ProviderGenMain();
				               genMainP.generateProvConsMain(directory,name,selectedSys[j], systemServiceRegistry, interfaces);
				               AppPropertiesGen genpro = new AppPropertiesGen();
				               genpro.GenerateAppProperties(directory,name,selectedSys[j]+"_Provider","provider");
				    		   
				    	   } else {
				    		   //pom
				    		   Template tpomcon=velocityEngine.getTemplate("templates/pomConsumer.vm");
				    		   while(!new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Consumer").exists()) {}
				    		   VelocityContext contextpomCons = new VelocityContext();
				    		   contextpomCons.put("name", name);
				    		   contextpomCons.put("sysName",selectedSys[j]);
				    		   Writer writerpomCons = new FileWriter (new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Consumer\\pom.xml"));
				    		   tpomcon.merge(contextpomCons,writerpomCons);
				    		   writerpomCons.flush();
				    		   writerpomCons.close();
				    		   
				    		   //Folder structure script
				    		   Template tFoldCon=velocityEngine.getTemplate("templates/consumerStructure.vm");
				    		   VelocityContext contextFoldCon = new VelocityContext();
				    		   contextFoldCon.put("outputDirectory", directory);
				    		   contextFoldCon.put("name", name);
				    		   contextFoldCon.put("sysName",selectedSys[j]);
				    		   contextFoldCon.put("disk",disk);
				    		   Writer writerFoldCon = new FileWriter (new File("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\"+selectedSys[j]+"ConsumerStructure.bat"));
				    		   tFoldCon.merge(contextFoldCon,writerFoldCon);
				    		   writerFoldCon.flush();
				    		   writerFoldCon.close();
				    		   executebat("D:\\SysMLPlugins\\Code\\eu.arrowhead.skelettons.deployment\\src\\resources\\"+selectedSys[j]+"ConsumerStructure.bat");
				    		   System.out.println(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Consumer\\src\\main\\java\\eu\\arrowhead\\"+selectedSys[j]+"_Consumer");		
				    		   while(!new File(directory+"\\"+name+"_ApplicationSystems\\"+selectedSys[j]+"_Consumer\\src\\main\\java\\eu\\arrowhead\\"+selectedSys[j]+"_Consumer").exists()) {}
				    		   gen.GenerateAppList(directory, name,selectedSys[j]+"_Consumer");
				    		   ConsumerGenMain genMainC =new ConsumerGenMain();
				               genMainC.generateConsumerMain(directory,name,selectedSys[j], systemServiceRegistry, interfaces);
				               AppPropertiesGen genpro = new AppPropertiesGen();
				               genpro.GenerateAppProperties(directory,name,selectedSys[j]+"_Consumer","consumer"); 
				    	   
				    	   }
				    	   
				           
				    	  
				       }
			           
			           
			           
			           
			           
			           } catch (IOException e) {
			        	   e.printStackTrace();
			         } catch (InterruptedException e) {
			          e.printStackTrace();
			           }
			            
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

