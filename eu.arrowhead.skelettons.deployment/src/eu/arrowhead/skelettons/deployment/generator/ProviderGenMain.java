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

import eu.arrowhead.skelettons.deployment.handlers.ModelParser;

public class ProviderGenMain {


	
	private static ArrayList<ArrayList<String>> classesRequest= new ArrayList<ArrayList<String>>();
	private static ArrayList<String> classesResponse= new ArrayList<String>();
	
	
	public ProviderGenMain() {
		
	}
	
public void generateProviderMain(String Directory, String name, String system, ArrayList<String []> systemServiceRegistry, ArrayList<InterfaceMetadata> interfaces) {
	 	
		

	ArrayList<InterfaceMetadata> serviceInterfaces= new ArrayList<InterfaceMetadata>();
	
	
	System.out.println("START GENERATION  PROVIDER: **"+ system+"** "+systemServiceRegistry.size()+"--"+interfaces.size());
	
	for (int m=0; m<systemServiceRegistry.size(); m++) {
		String[] systemService= systemServiceRegistry.get(m);
		System.out.println("sys: "+systemService[0]);
		System.out.println("serv: "+systemService[1]);
		if(systemService[2].equals("provider")) {
			if(systemService[0].equals(system)) {
				System.out.println("MATCH:"+m);
				String serv= systemService[1];
				for (int n=0; n<interfaces.size(); n++) {
					if(interfaces.get(n).getID().equals(serv)) {
						System.out.println("MATCH interface number:"+n);
						serviceInterfaces.add(interfaces.get(n));
				
					}	
				}	
			}	
		}
	}
	
	
	
 InterfaceMetadata MD=serviceInterfaces.get(0);
	System.out.println(MD.toString());
	String service=MD.getID();
	
		 ArrayList<OperationInt> operations = MD.getOperations();
		for(int i=0; i< operations.size(); i++) {
			OperationInt op = operations.get(i);
			//GENERATION PAYLOAD JAVA OBJECTS
			if(op.isRequest()){
		           ClassGenSimple Request=new ClassGenSimple();
		       
		             for(int k=0; k<op.elements_request.size();k++)
		             {
		                ArrayList<String[]> elements_request=op.elements_request.get(k).getElements();
		               classesRequest.add(Request.classGen(elements_request,op.getOpName()+"RequestDTO"+k,Directory, name, system+"_Provider"));
		             }
		            //for(int h=0;h<classesRequest.size();h++)
		                //System.out.println("..........."+classesRequest.get(h));
		  
		       }
		        
		       
		        if(op.isResponse()){
		            ClassGenSimple Response=new ClassGenSimple();
		      
		                 for(int j=0; j<op.elements_response.size();j++)
		                 {
		                    ArrayList<String[]> elements_response=op.elements_response.get(j).getElements();
		                    classesResponse=Response.classGen(elements_response,op.getOpName()+"ResponseDTO"+j,Directory, name, system+"_Provider");
		                 }
		           
		        }
		        
			
			
			
		}
		
		
	//TODO GENERATE MAIN --only first operation... change the template
		OperationInt firstOp =operations.get(0);
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
			
	// GENERATION APPLICATION LISTENER  (only first operation)
			providerGenAppListener( serviceInterfaces, system,  Directory, name);
			
		}
		

//METHOD GENERATION APPLICATION LISTENER

 		public void providerGenAppListener(ArrayList<InterfaceMetadata> serviceInterfaces, String system, String Directory, String name) {
 			
 			
 			InterfaceMetadata MD=serviceInterfaces.get(0);
 			 ArrayList<OperationInt> operations = MD.getOperations();
 			OperationInt firstOp =operations.get(0);
 			
 			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
			   Template t=velocityEngine.getTemplate("templates/providerAppListener.vm");
			   VelocityContext context = new VelocityContext();
			   context.put("packagename",system+"_Provider");
			   context.put("serviceName", MD.getID());
			  // context.put("opName", firstOp.getOpName());
			   context.put("opPath", firstOp.getPathResource());
			   context.put("Method", firstOp.getMethod());
			 
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\ProviderApplicationInitListener.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
	 
 		}

	}

