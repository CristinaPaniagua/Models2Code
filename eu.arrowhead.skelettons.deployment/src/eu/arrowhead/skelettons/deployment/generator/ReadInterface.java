package eu.arrowhead.skelettons.deployment.generator;

import java.util.ArrayList;

public class ReadInterface {

	public ReadInterface() {
		// TODO Auto-generated constructor stub
	}
	public InterfaceMetadata readInterfaceDescription(String System, String Service) {
		InterfaceMetadata interfaceDescription = new InterfaceMetadata();
		
		 	ArrayList<ElementsPayload> elements_request = new ArrayList<ElementsPayload>(); 
		    ArrayList<ElementsPayload> elements_response = new ArrayList<ElementsPayload>();
		    ArrayList<String[]> payload_request = new ArrayList<String[]>(); 
		    ArrayList<String[]> payload_response = new ArrayList<String[]>();
		    ArrayList<String[]>  metadata_request = new ArrayList<String[]>();
		    ArrayList<String[]> metadata_response = new ArrayList<String[]>();
		
		
		
		interfaceDescription.setProtocol("HTTP");
		interfaceDescription.setID(Service);
		interfaceDescription.setMethod("GET");
		interfaceDescription.setPathResource("/servicepath");
		interfaceDescription.setMediatype_request("JSON");
		interfaceDescription.setMediatype_response("JSON");
		interfaceDescription.setComplexType_request(null);
		interfaceDescription.setComplexType_response(null);
		interfaceDescription.setRequest(false);
		interfaceDescription.setResponse(true);
		interfaceDescription.setParam(false); 
		interfaceDescription.setParameters(null);
		interfaceDescription.setSubpaths(null);
		
		interfaceDescription.setElements_request(null);  
		 String[] ele=new String[2];
		 ele[0]="temperature";
         ele[1]="integer";
         String[] metadata=new String[4];
         metadata[0]=ele[0];
         metadata[1]=ele[1];
         metadata[2]= "temp";
         metadata[3]= "celsius";
         
		payload_response.add(ele);
		metadata_response.add(metadata);
		ElementsPayload elementsResponse = new ElementsPayload(payload_response,metadata_response);
		elements_response.add(elementsResponse);
		interfaceDescription.setElements_response(elements_response); 
		
		return interfaceDescription;
	
}
}
