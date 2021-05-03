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
import java.util.stream.Collectors;
import java.util.List;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

public class MainWIndow {

	protected static Shell shell;
	private Text Directory;
	protected String directory=null;
	
	 
	 @Execute
	    public void execute(Shell shell) {
	
		 DialogWindow dialog= new DialogWindow(shell);
         //dialog.open();
			//dialog.createDialogArea(shell);
         if (dialog.open() == Window.OK) {
            System.out.println("OK");
            directory=dialog.getDirectory();

			if(!(directory == null || directory.isEmpty())) {
			   VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty( "resource.loader", "class" );
			   velocityEngine.setProperty( "class.resource.loader.class"," org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
			      velocityEngine.init();
			      Template t = velocityEngine.getTemplate("main/resources/templates/coreSysJava.vm");
			       VelocityContext context = new VelocityContext();
			       context.put("outputDirectory", directory);
			       try{
			        Writer writer = new FileWriter (new File("D:\\SysMLPlugins\\Code\\eu.arrowhead.coreSystems.deployment\\testscript.bat"));
			           t.merge(context, writer);
			           writer.flush();
			           writer.close();
			           
			           
			           executebat();
			           
			           } catch (IOException e) {
			        	   e.printStackTrace();
			           } catch (InterruptedException e) {
			        	   e.printStackTrace();
			           }
			            
			}else dialog.open();
			
            }
     
	 }
	 

	   //EXECUTE BAT
	   
	   public static void executebat() throws InterruptedException, IOException {
	        
	       
	       ExecutorService executor = Executors.newSingleThreadExecutor();
	        ProcessBuilder processBuilder = new ProcessBuilder();
	        System.out.println("Script generated");
	        processBuilder.command("D:\\SysMLPlugins\\Scripts\\CoreSystems\\init.bat");
	        
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
	
	



	

	}

