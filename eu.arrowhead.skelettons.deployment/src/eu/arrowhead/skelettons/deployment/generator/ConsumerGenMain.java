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
		
		
		for (int p=0; p<serviceInterfaces.size(); p++) {
			 InterfaceMetadata MDC=serviceInterfaces.get(p);
				System.out.println(MDC.toString());
				String service=MDC.getID();
				
					 ArrayList<OperationInt> operations = MDC.getOperations();
					for(int i=0; i< operations.size(); i++) {
						OperationInt op = operations.get(i);	
						objectClassGen(Directory,  name,  system, op);
					}
	
		}
		        
			
			
			
		
		
		
	// GENERATE MAIN 
		
		boolean httpFlag=false;
		boolean coapFlag=false;
		
		httpFlag=checkHttpProtocol(serviceInterfaces);
		coapFlag=checkCoapProtocol(serviceInterfaces);
		
			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
				 serviceInterfaces=removeRepetitions( serviceInterfaces);
			
				 Template t=velocityEngine.getTemplate("templates/consumerMainHttpCoap.vm");
				
					
				   
				   VelocityContext context = new VelocityContext();
				   context.put("packagename",system+"_Consumer");
				   context.put("sysName", system);
				   context.put("interfaces", serviceInterfaces);
				   context.put("address", "http://127.0.0.1:8888");
				   context.put("httpFlag", httpFlag);
				   context.put("coapFlag", coapFlag);
			 
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Consumer\\src\\main\\java\\eu\\arrowhead\\"+system+"_Consumer\\"+system+"ConsumerMain.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
			
		}
	//GENERATION PAYLOAD JAVA OBJECTS
	public void objectClassGen(String Directory, String name, String system, OperationInt op) {

		if(op.isRequest()){
			ClassGenSimple Request=new ClassGenSimple();

     for(int k=0; k<op.elements_request.size();k++)
     	{
    	 ArrayList<String[]> elements_request=op.elements_request.get(k).getElements();
    	 classesRequest.add(Request.classGen(elements_request,op.getOpName()+"RequestDTO",Directory, name, system+"_Consumer"));
     	}
    //for(int h=0;h<classesRequest.size();h++)
        //System.out.println("..........."+classesRequest.get(h));

		}


		if(op.isResponse()){
			ClassGenSimple Response=new ClassGenSimple();

				for(int j=0; j<op.elements_response.size();j++)
				{
					ArrayList<String[]> elements_response=op.elements_response.get(j).getElements();
					classesResponse=Response.classGen(elements_response,op.getOpName()+"ResponseDTO",Directory, name, system+"_Consumer");
				}
   
		}
	}

	//TODO: Look if this is correct or the problem  is the service name convention.
	public ArrayList<InterfaceMetadata> removeRepetitions(ArrayList<InterfaceMetadata> serviceInterfaces) {
			for(int i=0; i<serviceInterfaces.size();i++) {
				InterfaceMetadata inter = serviceInterfaces.get(i);
				for (int j=i+1; j<serviceInterfaces.size();j++) {
					InterfaceMetadata interNext = serviceInterfaces.get(j);
					if(inter.getID().equals(interNext.getID())) {
						serviceInterfaces.remove(j);
		
					}
	
				}

			}

			return serviceInterfaces;
		}	

	
	
	 
	


public boolean checkCoapProtocol(ArrayList<InterfaceMetadata> serviceInterfaces) {
	boolean coap=false;
	for(int i=0; i<serviceInterfaces.size();i++) {
		InterfaceMetadata inter = serviceInterfaces.get(i);
		if(inter.getProtocol().equalsIgnoreCase("CoAP")) {
			coap=true;
		}
			
		}
	return coap;
	}



public boolean checkHttpProtocol(ArrayList<InterfaceMetadata> serviceInterfaces) {
	boolean http=false;
	for(int i=0; i<serviceInterfaces.size();i++) {
		InterfaceMetadata inter = serviceInterfaces.get(i);
		if(inter.getProtocol().startsWith("HTTP")) {
		http=true;
		}
			
		}
	return http;
	}

}