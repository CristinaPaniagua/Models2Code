package eu.arrowhead.coresystems.deployment.handlers;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;

public class ScriptDeployment {

	protected static Shell shell;
	private Text Directory;
	private String directory = "";
	private String os = "";
	private String disk = "";
	private String language = "";
	private Boolean mandatorySys = false;
	private Boolean supportSys = false;
	private Boolean skipTest = false;
	private String workspace="/Users/cristina.paniagua/Desktop/EclipseWorkSpace";
	
	 
	 @Execute
	    public void execute(Shell shell) {
	
		 DialogWindow dialog= new DialogWindow(shell);
		 dialog.setWorkspace(workspace);
         if (dialog.open() == Window.OK) {
        	 
        	if(!dialog.getBadDirectory()){
            System.out.println("OK");
            
            directory=dialog.getDirectory();
            os=dialog.getOs();
            System.out.println("OS: "+os);
            language=dialog.getLanguage();
            mandatorySys=dialog.getMandatorySys();
            supportSys=dialog.getSupportSys();
            disk=dialog.getDisk();
            skipTest=dialog.getSkipTest();
            
            final ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(ScriptDeployment.class.getClassLoader());
 
			if(!(directory == null || directory.isEmpty())) {
			   VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			   Template t=null;
			   VelocityContext context = new VelocityContext();
			   context.put("outputDirectory", directory);
			  if(mandatorySys) {
				  if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
					   t = velocityEngine.getTemplate("main/resources/templates/coreSysJavaLinux.vm");
				   }else {
					   disk=dialog.getDisk();
					   t = velocityEngine.getTemplate("main/resources/templates/coreSysJavaWindows.vm");
					   context.put("disk", disk);
				   }
			  }else {
				  if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
					   t = velocityEngine.getTemplate("main/resources/templates/AllSysJavaLinux.vm");
				   }else {
					   disk=dialog.getDisk();
					   t = velocityEngine.getTemplate("main/resources/templates/allSysJavaWindows.vm");
					   context.put("disk", disk);
				   }
			  }
			   context.put("workSpace", workspace);
			   
			      if(skipTest) {
			    	  context.put("skipTest","-DskipTests");
			    	  
			      }else {
			    	  context.put("skipTest","  ");
			      }
			       
			      
			       try{
			    	   Writer writer=null;
			    	   if(os.equalsIgnoreCase("linux")||os.equalsIgnoreCase("mac") ) {
			    		   context.put("fileEnd", "sh");
						   
						writer = new FileWriter (new File(workspace+"/eu.arrowhead.coreSystems.deployment/src/main/resources/scripts/corescript.sh"));
					   }else {
						   context.put("fileEnd", "bat");
						   writer = new FileWriter (new File(workspace+"\\eu.arrowhead.coreSystems.deployment\\src\\main\\resources\\scripts\\corescript.bat"));
					   }   
			       
			           t.merge(context, writer);
			           writer.flush();
			           writer.close();
			           
			           if(os.equalsIgnoreCase("windows")) {
			        	   executebat(workspace+"\\eu.arrowhead.coreSystems.deployment\\src\\main\\resources\\scripts\\init.bat"); 
			           }else
			        	   executesh();
			           
			           
			           } catch (IOException e) {
			        	   e.printStackTrace();
			           } catch (InterruptedException e) {
			        	   e.printStackTrace();
			           }
			            
			}else dialog.open();
			
			
			// set back default class loader
	         Thread.currentThread().setContextClassLoader(oldContextClassLoader);
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
	 

	   //EXECUTE SH
	  
	   public  void executesh() throws InterruptedException, IOException {
	        
	       
	       ExecutorService executor = Executors.newSingleThreadExecutor();
	        ProcessBuilder processBuilder = new ProcessBuilder();
	        System.out.println("Script generated");
	      
			
	        processBuilder.command("sh", "-c", "sh ./init.sh");
		  
	        processBuilder.directory(new File("/Users/cristina.paniagua/Desktop/EclipseWorkSpace/eu.arrowhead.coreSystems.deployment/src/main/resources/scripts/"));
		 
		  try {

	            Process process = processBuilder.start();
	            System.out.println("Script executed");
	            executor.submit(new ProcessTask(process.getInputStream()));
	           

	           
	        } finally {
	            executor.shutdown();
	        }

	   }
	   
	   
	}

