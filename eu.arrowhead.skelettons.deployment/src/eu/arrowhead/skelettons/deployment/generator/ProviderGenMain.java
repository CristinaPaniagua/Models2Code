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
	
	
	
for(int h=0;h<serviceInterfaces.size();h++) {
	InterfaceMetadata MD= serviceInterfaces.get(h);
	
	
		 ArrayList<OperationInt> operations = MD.getOperations();
		for(int i=0; i< operations.size(); i++) {
			OperationInt op = operations.get(i);
			//GENERATION PAYLOAD JAVA OBJECTS
			if(op.isRequest()){
		           ClassGenSimple Request=new ClassGenSimple();
		       
		             for(int k=0; k<op.elements_request.size();k++)
		             {
		                ArrayList<String[]> elements_request=op.elements_request.get(k).getElements();
		               classesRequest.add(Request.classGen(elements_request,op.getOpName()+"RequestDTO",Directory, name, system+"_Provider"));
		             }
		            //for(int h=0;h<classesRequest.size();h++)
		                //System.out.println("..........."+classesRequest.get(h));
		  
		       }
		        
		       
		        if(op.isResponse()){
		            ClassGenSimple Response=new ClassGenSimple();
		      
		                 for(int j=0; j<op.elements_response.size();j++)
		                 {
		                    ArrayList<String[]> elements_response=op.elements_response.get(j).getElements();
		                    classesResponse=Response.classGen(elements_response,op.getOpName()+"ResponseDTO",Directory, name, system+"_Provider");
		                 }
		           
		        }
		        

			
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
			 
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\"+system+"ProviderMain.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
			
	// GENERATION APPLICATION LISTENER  
			providerGenAppListener( serviceInterfaces, system,  Directory, name);
	// GENERATION CONTROLLER
			providerController( serviceInterfaces, system,  Directory, name);
						
			
		}
		
//MAIN FOR PROVIDERS THAN INCLUDE CONSUMERS

public void generateProvConsMain(String Directory, String name, String system, ArrayList<String []> systemServiceRegistry, ArrayList<InterfaceMetadata> interfaces) {
 	
	

	ArrayList<InterfaceMetadata> serviceInterfacesProvider= new ArrayList<InterfaceMetadata>();
	ArrayList<InterfaceMetadata> serviceInterfacesConsumer= new ArrayList<InterfaceMetadata>();
	
	
	System.out.println("START GENERATION  PROVIDER-CONSUMER: **"+ system+"** "+systemServiceRegistry.size()+"--"+interfaces.size());
	
	for (int m=0; m<systemServiceRegistry.size(); m++) {
		String[] systemService= systemServiceRegistry.get(m);
		System.out.println("sys: "+systemService[0]);
		System.out.println("serv: "+systemService[1]);
		System.out.println("type: "+systemService[2]);
	
			if(systemService[0].equals(system)) {
				System.out.println("MATCH:"+m);
				String serv= systemService[1];
				
				//for each port-service
				
				if(systemService[2].equalsIgnoreCase("provider")) {
					
					for (int n=0; n<interfaces.size(); n++) {
						if(interfaces.get(n).getID().equals(serv)) {
							System.out.println("MATCH interface number:"+n);
							serviceInterfacesProvider.add(interfaces.get(n));
							}	
					}	
					
				}else {
					for (int n=0; n<interfaces.size(); n++) {
						if(interfaces.get(n).getID().equals(serv)) {
							System.out.println("MATCH interface number:"+n+interfaces.get(n).getID());
							serviceInterfacesConsumer.add(interfaces.get(n));
							}	
					}	

				}	
				
		
	}
	}
	//For each service that provide the system: generation of payload classes and the controller
			for (int l=0; l<serviceInterfacesProvider.size(); l++) {
				 InterfaceMetadata MDP=serviceInterfacesProvider.get(l);
					System.out.println(MDP.toString());
					String service=MDP.getID();
					
						 ArrayList<OperationInt> operations = MDP.getOperations();
						for(int i=0; i< operations.size(); i++) {
							OperationInt op = operations.get(i);	
							objectClassGen(Directory,  name,  system, op);
						}
				
						// GENERATION CONTROLLER
			providerController( serviceInterfacesProvider, system,  Directory, name);
		
			}

			
			
			//For each service that consume the system: generation of payload classes and the main with the client-methods
			for (int p=0; p<serviceInterfacesConsumer.size(); p++) {
				 InterfaceMetadata MDC=serviceInterfacesConsumer.get(p);
					System.out.println(MDC.toString());
					String service=MDC.getID();
					
						 ArrayList<OperationInt> operations = MDC.getOperations();
						for(int i=0; i< operations.size(); i++) {
							OperationInt op = operations.get(i);	
							objectClassGen(Directory,  name,  system, op);
						}
		
			}
		
		
	// MAIN 
		
			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
				 serviceInterfacesConsumer=removeRepetitions( serviceInterfacesConsumer);
				
			   Template t=velocityEngine.getTemplate("templates/providerConsumerMain.vm");
			   VelocityContext context = new VelocityContext();
			   context.put("packagename",system+"_Provider");
			   context.put("sysName", system);
			   context.put("interfaces", serviceInterfacesConsumer);
			   context.put("address", "http://127.0.0.1:8888");
			 
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\"+system+"ProviderMain.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
			
	// GENERATION APPLICATION LISTENER  
			providerGenAppListener( serviceInterfacesProvider, system,  Directory, name);
	
			
		}


//METHOD GENERATION APPLICATION LISTENER

 		public void providerGenAppListener(ArrayList<InterfaceMetadata> serviceInterfaces, String system, String Directory, String name) {
 			
 			
 			InterfaceMetadata MD=serviceInterfaces.get(0);
 			 ArrayList<OperationInt> operations = MD.getOperations();
 			
 		
 			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
			   Template t=velocityEngine.getTemplate("templates/providerAppListener.vm");
			   VelocityContext context = new VelocityContext();
			   context.put("packagename",system+"_Provider");
			   context.put("serviceName", MD.getID());
			  
			   context.put("operations", operations);
			  
			 
			   Writer writer = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\ProviderApplicationInitListener.java"));
			   t.merge(context,writer);
			   writer.flush();
			   writer.close();
			      
	        } catch (IOException e) {
	     	   e.printStackTrace();}
	 
 		}

 		
 		
 		//METHOD GENERATION CONTROLLER

 		public void providerController(ArrayList<InterfaceMetadata> serviceInterfaces, String system, String Directory, String name) {
 			
 			
 			ArrayList<InterfaceMetadata> serviceInterfacesCoap= new ArrayList<InterfaceMetadata>();
 			ArrayList<InterfaceMetadata> serviceInterfacesHttp= new ArrayList<InterfaceMetadata>();
 			for(int i=0;i<serviceInterfaces.size();i++) {
 				InterfaceMetadata interfaceM = serviceInterfaces.get(i);
 				if(interfaceM.getProtocol().equalsIgnoreCase("CoAP")) {
 					serviceInterfacesCoap.add(interfaceM);
 				}else {
 					serviceInterfacesHttp.add(interfaceM);
 				}
 			}
 			
 			
 			
 		
 			 VelocityEngine velocityEngine = new VelocityEngine();

			   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			   velocityEngine.init();
			try {
			//HTTP: 
			if(serviceInterfacesHttp.size()>0) {
				   Template th=velocityEngine.getTemplate("templates/providerController.vm");
				   VelocityContext contexth = new VelocityContext();
				   contexth.put("packagename",system+"_Provider");
				   contexth.put("interfaces", serviceInterfacesHttp);
				  
				 
				   Writer writerh = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\ServiceControllerHttp.java"));
				   th.merge(contexth,writerh);
				   writerh.flush();
				   writerh.close();
			}
			
			//Coap: 
			if(serviceInterfacesCoap.size()>0) {
				   Template tc=velocityEngine.getTemplate("templates/providerControllerCoap.vm");
				   VelocityContext contextc = new VelocityContext();
				   contextc.put("packagename",system+"_Provider");
				   contextc.put("interfaces", serviceInterfacesCoap);
				  
				 
				   Writer writerc = new FileWriter (new File(Directory+"\\"+name+"_ApplicationSystems\\"+system+"_Provider\\src\\main\\java\\eu\\arrowhead\\"+system+"_Provider\\ServiceControllerCoap.java"));
				   tc.merge(contextc,writerc);
				   writerc.flush();
				   writerc.close();
			}
			
			      
			   
			   
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
            	 classesRequest.add(Request.classGen(elements_request,op.getOpName()+"RequestDTO",Directory, name, system+"_Provider"));
             	}
            //for(int h=0;h<classesRequest.size();h++)
                //System.out.println("..........."+classesRequest.get(h));
  
 			}
        
       
 			if(op.isResponse()){
 				ClassGenSimple Response=new ClassGenSimple();
      
 					for(int j=0; j<op.elements_response.size();j++)
 					{
 						ArrayList<String[]> elements_response=op.elements_response.get(j).getElements();
 						classesResponse=Response.classGen(elements_response,op.getOpName()+"ResponseDTO",Directory, name, system+"_Provider");
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

}//END CLASS

