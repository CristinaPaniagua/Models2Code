package eu.arrowhead.skeletons.deployment.handlers;

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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eu.arrowhead.skeletons.deployment.common.CodgenUtil;
import eu.arrowhead.skeletons.deployment.common.TypeSafeProperties;
import eu.arrowhead.skeletons.deployment.dto.LocalCloudDTO;
import eu.arrowhead.skeletons.deployment.generator.AppPropertiesGen;
import eu.arrowhead.skeletons.deployment.generator.ConsumerGenAppList;
import eu.arrowhead.skeletons.deployment.generator.ConsumerGenMain;
import eu.arrowhead.skeletons.deployment.generator.InterfaceMetadata;
import eu.arrowhead.skeletons.deployment.generator.ProviderGenMain;
import eu.arrowhead.skeletons.deployment.handlers.ScriptDeployment;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;

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
	private String os="";
	private static TypeSafeProperties configuration = CodgenUtil.getProp("WorkSpaceConfiguration");
	private String workspace= configuration.getProperty("workspace");
	//private String workspace="/Users/cristina.paniagua/Desktop/EclipseWorkSpace";
	
	 @Execute
	    public void execute(Shell shell) {
	
		 System.out.println("WORSPACE SELECTED: "+ workspace);
		 IProject[] projects= readWorkspace();
		 Shell pshell = null;
		 ProjectSelecWindow projWin= new ProjectSelecWindow(pshell);
		 projWin.setProjects(projects);
		 
		
		 
		 if(projWin.open()==Window.OK) {
			 
			 	 
			 IProject selectedProject=projWin.getSelectedProject() ;
				IPath projectLocation=selectedProject.getLocation();
				Shell mshell = null;
				ModelSelectWindow modelWin= new ModelSelectWindow(mshell);
				modelWin.setPathModel(projectLocation.toString());
				
				
			if(modelWin.open()==Window.OK) {
				
			
				if(modelWin.getExtensionFlag()) {
					
					String selectedPathModel= modelWin.getSelectedPath();
					
				
				 ModelParser MP= new ModelParser();
				 System.out.println("MODEL FILE SELETEC: "+ projectLocation.toString()+"/"+selectedPathModel);
				 MP.modelReader(projectLocation.toString()+"/"+selectedPathModel);
			 ArrayList<String []> systemServiceRegistry= MP.getSystemServiceRegistry();
			 ArrayList<InterfaceMetadata> interfaces= MP.getInterfaces();
			 ArrayList<LocalCloudDTO> localClouds= MP.getLocalClouds();	 
			 
			
			 DialogWindow dialog= new DialogWindow(shell);
			 dialog.setWorkDirectory(workspace);
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
            os=dialog.getOs();
            final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());

			if(!(directory == null || directory.isEmpty())) {
			   VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			   
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
			    	   if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
			    		   folders=folders+ "mkdir "+ selectedSys[j]+type+"\n";
			    	   }else {
			    		   folders=folders+ "mkdir "+ selectedSys[j]+type+"\r\n";
			    	   }
			    		   
			    	   
			       }
			       context.put("createFolders",folders);
			       contextpom.put("modules", modules);
			       try{
			    	   
			    	   if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
			    		 
			    		   Template t =velocityEngine.getTemplate("templates/folderGenUnix.vm");
			    		   Writer writer = new FileWriter (workspace +"/eu.arrowhead.skeletons.deployment/src/resources/folderGenUnix.sh");
			    		   context.put("workspace", workspace);
			    		   t.merge(context, writer);
				           writer.flush();
				           writer.close();
				          			    		   
			    		   executesh(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/","folderGenUnix.sh");
			    	   }else {
			    		   Writer writer = new FileWriter (workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\folderGenWin.bat"); 
			    		   Template t =velocityEngine.getTemplate("templates/folderGenWin.vm");
			    		   context.put("workspace", workspace);
			    		   context.put("disk", disk);
			    		   t.merge(context, writer);
				           writer.flush();
				           writer.close();
				           
				           executebat(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\folderGenWin.bat");
			    	   }
			    	
			    	   
			           
			    	   System.out.println(directory+File.separator+name+"_ApplicationSystems");
			           while(!new File(directory+File.separator+name+"_ApplicationSystems").exists()) {}
			           
			    	   Writer writerpom = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+"pom.xml"));
					  
			           tpom.merge(contextpom, writerpom);
			           writerpom.flush();
			           writerpom.close();
			           
			           ConsumerGenAppList gen=new ConsumerGenAppList();
			           for (int j=0; j<selectedSys.length;j++) {
				    	   if(selectedSysType[j]==0) {
				    		   //pom
				    		   Template tpomPro=velocityEngine.getTemplate("templates/pomProvider.vm");
				    		   while(!new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider").exists()) {}
				    		   VelocityContext contextpomPro = new VelocityContext();
				    		   contextpomPro.put("name", name);
				    		   contextpomPro.put("sysName",selectedSys[j]);
				    		   Writer  writerpomPro = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"pom.xml"));
				    		   tpomPro.merge(contextpomPro,writerpomPro);
				    		   writerpomPro.flush();
				    		   writerpomPro.close();
				    		   
				    		   //Folder structure script
				    		  
				    		   VelocityContext contextFoldPro = new VelocityContext();
				    		   contextFoldPro.put("outputDirectory", directory);
				    		   contextFoldPro.put("name", name);
				    		   contextFoldPro.put("disk", disk);
				    		   contextFoldPro.put("workspace", workspace);
				    		   contextFoldPro.put("sysName",selectedSys[j]);
				    		
				    		   
				    		   System.out.println("provider 1");
				    		   
				    		   if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
						    		 
				    			   Writer writerFoldPro = new FileWriter (new File(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/"+selectedSys[j]+"ProviderStructure.sh"));
				    			   Template tFoldPro=velocityEngine.getTemplate("templates/providerStructureUnix.vm");
				    			   tFoldPro.merge(contextFoldPro,writerFoldPro);
					    		   writerFoldPro.flush();
					    		   writerFoldPro.close();
					    		   executesh(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/",selectedSys[j]+"ProviderStructure.sh");
					    	   
				    		   }else {
				    			   contextFoldPro.put("disk",disk);
				    			   Writer writerFoldPro = new FileWriter (new File(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat"));
				    			   Template tFoldPro=velocityEngine.getTemplate("templates/providerStructureWin.vm");
				    			   tFoldPro.merge(contextFoldPro,writerFoldPro);
				    			   writerFoldPro.flush();
				    			   writerFoldPro.close();
				    			   executebat(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat");
					    	   }
				    		   
				    		   
				    		  while(!new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"eu"+File.separator+"arrowhead"+File.separator+selectedSys[j]+"_Provider").exists()) {}
				    		   //gen.GenerateAppList(directory, name,selectedSys[j]+"_Consumer");
				    		   ProviderGenMain genMainP =new ProviderGenMain();
				               genMainP.generateProviderMain(directory,name,selectedSys[j], systemServiceRegistry, interfaces);
				               AppPropertiesGen genpro = new AppPropertiesGen();
				               genpro.GenerateAppProperties(directory,name,selectedSys[j]+"_Provider","provider"); 
				               
				               // security
				               VelocityContext contextSecurity= new VelocityContext();
				               Writer writerSecurity1 = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"eu"+File.separator+"arrowhead"+File.separator+"security"+File.separator+"ProviderTokenSecurityFilter.java"));
			    			   Template tsec1=velocityEngine.getTemplate("templates/ProviderTokenSecurityFilter.vm");
			    			   tsec1.merge(contextSecurity,writerSecurity1);
			    			   writerSecurity1.flush();
			    			   writerSecurity1.close();
			    			   
			    			   Writer writerSecurity2 = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"eu"+File.separator+"arrowhead"+File.separator+"security"+File.separator+"ProviderSecurityConfig.java"));
			    			   Template tsec2=velocityEngine.getTemplate("templates/ProviderSecurityConfig.vm");
			    			   tsec2.merge(contextSecurity,writerSecurity2);
			    			   writerSecurity2.flush();
			    			   writerSecurity2.close();
			    			   
			    			   Writer writerSecurity3 = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"eu"+File.separator+"arrowhead"+File.separator+"security"+File.separator+"ProviderAccessControlFilter.java"));
			    			   Template tsec3=velocityEngine.getTemplate("templates/ProviderAccessControlFilter.vm");
			    			   tsec3.merge(contextSecurity,writerSecurity3);
			    			   writerSecurity3.flush();
			    			   writerSecurity3.close();
				               
				               
				    	   }else if(selectedSysType[j]==2) {
				    		 //pom
				    		   Template tpomPro=velocityEngine.getTemplate("templates/pomProvider.vm");
				    		   while(!new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider").exists()) {}
				    		   VelocityContext contextpomPro = new VelocityContext();
				    		   contextpomPro.put("name", name);
				    		   contextpomPro.put("sysName",selectedSys[j]);
				    		   Writer  writerpomPro = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"pom.xml"));
				    		   tpomPro.merge(contextpomPro,writerpomPro);
				    		   writerpomPro.flush();
				    		   writerpomPro.close();
				    		   
				    		   //Folder structure script
				    		  
				    		   VelocityContext contextFoldPro = new VelocityContext();
				    		   contextFoldPro.put("outputDirectory", directory);
				    		   contextFoldPro.put("name", name);
				    		   contextFoldPro.put("disk", disk);
				    		   contextFoldPro.put("workspace", workspace);
				    		   contextFoldPro.put("sysName",selectedSys[j]);
				    		  
				    		   System.out.println("provider 2");
				    		   if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
						    		 
				    			   Writer writerFoldPro = new FileWriter (new File(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/"+selectedSys[j]+"ProviderStructure.sh"));
				    			   Template tFoldPro=velocityEngine.getTemplate("templates/providerStructureUnix.vm");
				    			   tFoldPro.merge(contextFoldPro,writerFoldPro);
					    		   writerFoldPro.flush();
					    		   writerFoldPro.close();
					    		   executesh(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/",selectedSys[j]+"ProviderStructure.sh");
					    	   
				    		   }else {
				    			   contextFoldPro.put("disk",disk);
				    			   Writer writerFoldPro = new FileWriter (new File(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat"));
				    			   Template tFoldPro=velocityEngine.getTemplate("templates/providerStructureWin.vm");
				    			   tFoldPro.merge(contextFoldPro,writerFoldPro);
				    			   writerFoldPro.flush();
				    			   writerFoldPro.close();
				    			   executebat(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\"+selectedSys[j]+"ProviderStructure.bat");
					    	   }
				    		   
				    		   while(!new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Provider"+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"eu"+File.separator+"arrowhead"+File.separator+selectedSys[j]+"_Provider").exists()) {}
				    		   //gen.GenerateAppList(directory, name,selectedSys[j]+"_Consumer");
				    		   ProviderGenMain genMainP =new ProviderGenMain();
				               genMainP.generateProvConsMain(directory,name,selectedSys[j], systemServiceRegistry, interfaces);
				               AppPropertiesGen genpro = new AppPropertiesGen();
				               genpro.GenerateAppProperties(directory,name,selectedSys[j]+"_Provider","provider");
				    		   
				    	   } else {
				    		   //pom
				    		   Template tpomcon=velocityEngine.getTemplate("templates/pomConsumer.vm");
				    		   while(!new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Consumer").exists()) {}
				    		   VelocityContext contextpomCons = new VelocityContext();
				    		   contextpomCons.put("name", name);
				    		   contextpomCons.put("sysName",selectedSys[j]);
				    		   Writer writerpomCons = new FileWriter (new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Consumer"+File.separator+"pom.xml"));
				    		   tpomcon.merge(contextpomCons,writerpomCons);
				    		   writerpomCons.flush();
				    		   writerpomCons.close();
				    		   
				    		   //Folder structure script
				    		   
				    		   VelocityContext contextFoldCon = new VelocityContext();
				    		   contextFoldCon.put("outputDirectory", directory);
				    		   contextFoldCon.put("name", name);
				    		   contextFoldCon.put("disk", disk);
				    		   contextFoldCon.put("workspace", workspace);
				    		   contextFoldCon.put("sysName",selectedSys[j]);
				    		   
				    		   System.out.println("consumer 1");
				    		   if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
				    			   Template tFoldCon=velocityEngine.getTemplate("templates/consumerStructureUnix.vm");
				    			   Writer writerFoldCon = new FileWriter (new File(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/"+selectedSys[j]+"ConsumerStructure.sh"));
					    		   tFoldCon.merge(contextFoldCon,writerFoldCon);
					    		   writerFoldCon.flush();
					    		   writerFoldCon.close();
					    		   executesh(workspace +"/eu.arrowhead.skeletons.deployment/src/resources/",selectedSys[j]+"ConsumerStructure.sh");
					    	   
				    		   }else {
				    			   Template tFoldCon=velocityEngine.getTemplate("templates/consumerStructureWin.vm");
				    			   Writer writerFoldCon = new FileWriter (new File(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\"+selectedSys[j]+"ConsumerStructure.bat"));
				    			   contextFoldCon.put("disk",disk);
					    		   tFoldCon.merge(contextFoldCon,writerFoldCon);
					    		   writerFoldCon.flush();
					    		   writerFoldCon.close();
					    		   executebat(workspace +"\\eu.arrowhead.skeletons.deployment\\src\\resources\\"+selectedSys[j]+"ConsumerStructure.bat");
					    	   } 
				    		   
				    		   
				    		  
				    		   System.out.println(directory+"/"+name+"_ApplicationSystems/"+selectedSys[j]+"_Consumer/src/main/java/eu/arrowhead/"+selectedSys[j]+"_Consumer");		
				    		   while(!new File(directory+File.separator+name+"_ApplicationSystems"+File.separator+selectedSys[j]+"_Consumer"+File.separator+"src"+File.separator+"main"+File.separator+"java"+File.separator+"eu"+File.separator+"arrowhead"+File.separator+selectedSys[j]+"_Consumer").exists()) {}
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
        }//dialogwindow  OK
				} else System.out.println("File extension no correct");
					
		 }// modelwindow OK
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
	
	   
	 //EXECUTE SH
		  
	   public  void executesh(String dir, String file) throws InterruptedException, IOException {
	        
	       
	       ExecutorService executor = Executors.newSingleThreadExecutor();
	        ProcessBuilder processBuilder = new ProcessBuilder();
	        System.out.println("Script generated");
	      
			
	        processBuilder.command("sh", "-c", "sh ./"+file);
		  
	        processBuilder.directory(new File(dir));
		 
		  try {

	            Process process = processBuilder.start();
	            System.out.println("Script executed");
	            executor.submit(new ProcessTask(process.getInputStream()));
	           

	           
	        } finally {
	        	
	            executor.shutdown();
	            System.out.println("Script closed");
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
		     String path = Directory+"/"+ FolderName;  
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

