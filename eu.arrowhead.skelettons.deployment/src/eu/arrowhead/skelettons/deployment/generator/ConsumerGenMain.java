package eu.arrowhead.skelettons.deployment.generator;

import java.util.ArrayList;

import java.io.IOException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;



public class ConsumerGenMain {

	
	private static InterfaceMetadata MD=null;
	private static ArrayList<ArrayList<String>> classesRequest= new ArrayList<ArrayList<String>>();
	private static ArrayList<String> classesResponse= new ArrayList<String>();
	
	public ConsumerGenMain() {
		// TODO Auto-generated constructor stub
	}

	
	public void generateConsumerMain(String Directory, String name, String system) {
	 	
		
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
	               classesRequest.add(Request.classGen(elements_request,"RequestDTO"+i,Directory, name, system+"_Consumer"));
	             }
	            //for(int h=0;h<classesRequest.size();h++)
	                //System.out.println("..........."+classesRequest.get(h));
	  
	       }
	        
	       
	        if(MD.getResponse()){
	            ClassGenSimple Response=new ClassGenSimple();
	      
	                 for(int j=0; j<MD.elements_response.size();j++)
	                 {
	                    ArrayList<String[]> elements_response=MD.elements_response.get(j).getElements();
	                    classesResponse=Response.classGen(elements_response,"ResponseDTO"+j,Directory, name, system+"_Consumer");
	                 }
	           
	        }
	        
	//TODO GENERATE MAIN
			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
			   Template t=velocityEngine.getTemplate("templates/consumerMain.vm");
			   VelocityContext context = new VelocityContext();
			   context.put("packagename",system+"_Consumer");
			   context.put("sysName", system);
			   context.put("serviceName", service);
			   context.put("serviceDefinition", MD.getID());
			   context.put("method", MD.getMethod());
			   context.put("address", "http://192.168.1.36:8088"+MD.getPathResource());
			   context.put("encoding", MD.getMediatype_request());
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Consumer\\src\\main\\java\\eu\\arrowhead\\"+system+"_Consumer\\ConsumerMain.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
			
		}
		
	}
	
	
	 
	

