package eu.arrowhead.skelettons.deployment.generator;

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

public class ProviderGenMain {


	private static InterfaceMetadata MD=null;
	private static ArrayList<ArrayList<String>> classesRequest= new ArrayList<ArrayList<String>>();
	private static ArrayList<String> classesResponse= new ArrayList<String>();
	
	
	public ProviderGenMain() {
		
	}
	
public void generateProviderMain(String Directory, String name, String system) {
	 	
		
		String service="serviceA";
		
		
		ReadInterface inter=new ReadInterface();
		MD=inter.readInterfaceDescription(system,service);
		System.out.println(MD.toString());
		
		//GENERATION PAYLOAD JAVA OBJECTS
		if(MD.getRequest()){
	           ClassGenSimple Request=new ClassGenSimple();
	       
	             for(int i=0; i<MD.elements_request.size();i++)
	             {
	                ArrayList<String[]> elements_request=MD.elements_request.get(i).getElements();
	               classesRequest.add(Request.classGen(elements_request,"RequestDTO"+i,Directory, name, system+"_Provider"));
	             }
	            //for(int h=0;h<classesRequest.size();h++)
	                //System.out.println("..........."+classesRequest.get(h));
	  
	       }
	        
	       
	        if(MD.getResponse()){
	            ClassGenSimple Response=new ClassGenSimple();
	      
	                 for(int j=0; j<MD.elements_response.size();j++)
	                 {
	                    ArrayList<String[]> elements_response=MD.elements_response.get(j).getElements();
	                    classesResponse=Response.classGen(elements_response,"ResponseDTO"+j,Directory, name, system+"_Provider");
	                 }
	           
	        }
	        
	//TODO GENERATE MAIN
			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
			   Template t=velocityEngine.getTemplate("templates/providerMain.vm");
			   VelocityContext context = new VelocityContext();
			   context.put("packagename",system+"_Provider");
			   context.put("sysName", system);
			 
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\ProviderMain.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
			
		}
		
	}

