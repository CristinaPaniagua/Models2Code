package eu.arrowhead.skelettons.deployment.generator;

import java.util.ArrayList;

import java.io.IOException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import eu.arrowhead.skelettons.deployment.handlers.ModelParser;

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

	
	public void generateConsumerMain(String Directory, String name, String system, ArrayList<String []> systemServiceRegistry, ArrayList<InterfaceMetadata> interfaces) {
	 	
		
		
	
		ArrayList<InterfaceMetadata> serviceInterfaces= new ArrayList<InterfaceMetadata>();
		
		
		System.out.println("START GENERATION CONSUMER: **"+ system+"** "+systemServiceRegistry.size()+"--"+interfaces.size());
		
		for (int m=0; m<systemServiceRegistry.size(); m++) {
			String[] systemService= systemServiceRegistry.get(m);
			System.out.println("sys: "+systemService[0]);
			System.out.println("serv: "+systemService[1]);
			if(systemService[2].equals("consumer")) {
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
		
		
		
		MD=serviceInterfaces.get(0);
		System.out.println(MD.toString());
		String service=MD.getID();
		 ArrayList<OperationInt> operations = MD.getOperations();
		for(int i=0; i< operations.size(); i++) {
			OperationInt op = operations.get(i);
			System.out.println("operation:"+op.getOpName());
			//GENERATION PAYLOAD JAVA OBJECTS
			if(op.isRequest()){
		           ClassGenSimple Request=new ClassGenSimple();
		       
		             for(int k=0; k<op.elements_request.size();k++)
		             {
		                ArrayList<String[]> elements_request=op.elements_request.get(k).getElements();
		               classesRequest.add(Request.classGen(elements_request,op.getOpName()+"RequestDTO"+k,Directory, name, system+"_Consumer"));
		             }
		            
		  
		       }
		        
		       
		        if(op.isResponse()){
		            ClassGenSimple Response=new ClassGenSimple();
		      
		                 for(int j=0; j<op.elements_response.size();j++)
		                 {
		                    ArrayList<String[]> elements_response=op.elements_response.get(j).getElements();
		                    classesResponse=Response.classGen(elements_response,op.getOpName()+"ResponseDTO"+j,Directory, name, system+"_Consumer");
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
			   Template t=velocityEngine.getTemplate("templates/consumerMain.vm");
			   VelocityContext context = new VelocityContext();
			   context.put("packagename",system+"_Consumer");
			   context.put("sysName", system);
			   context.put("serviceName", service);
			   context.put("serviceDefinition", MD.getID());
			   context.put("method", firstOp.getMethod());
			   context.put("address", "http://192.168.1.36:8088"+firstOp.getPathResource());
			   context.put("encoding", firstOp.getMediatype_request());
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Consumer\\src\\main\\java\\eu\\arrowhead\\"+system+"_Consumer\\ConsumerMain.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
			
		}
		
	}
	
	
	 
	

