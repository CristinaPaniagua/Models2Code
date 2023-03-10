package plugin.pojo;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * The Interface Design Description (IDD) is a SysML stereotype that displays every service that a
 * certain system provides including networking, format and security parameters.
 *
 * @author fernand0labra
 *
 */
public class InterfaceDesignDescription {
	
	//=================================================================================================
	// attributes
	
	private String name; // Interface Name
	private String role; // Role of the system with the interface (Consumer or Provider)
	
	private String encoding; // Encoding of the payload
	private String protocol; // Communication Protocol
	
	private ArrayList<ServiceDescription> operations; // List of operations that the interface serves
	
	
	//=================================================================================================
	// auxiliary methods
	
	//-------------------------------------------------------------------------------------------------	
	public String getName() { return name; }
	public String getRole() { return role; }
	public String getEncoding() { return encoding; }
	public String getProtocol() { return protocol; }
	public ArrayList<ServiceDescription> getOperations() { return operations; }

	//-------------------------------------------------------------------------------------------------
	public void setName(String name) { this.name = name; }
	public void setRole(String role) { this.role = role; }
	public void setEncoding(String encoding) { this.encoding = encoding; }
	public void setProtocol(String protocol) { this.protocol = protocol; }
	public void setOperations(ArrayList<ServiceDescription> operations) { this.operations = operations; }	
	
	//-------------------------------------------------------------------------------------------------
	public InterfaceDesignDescription() {
		this.name = "";
		this.role = "";
		this.encoding = "";
		this.protocol = "";
		this.operations = new ArrayList<ServiceDescription>();
	}
	
	public InterfaceDesignDescription(InterfaceDesignDescription other) {
		this.name = other.name;
		this.role = other.role;
		this.encoding = other.encoding;
		this.protocol = other.protocol;
		this.operations = new ArrayList<ServiceDescription>();
		
		for(ServiceDescription service : other.getOperations())
			this.operations.add(new ServiceDescription(service));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		String operationsString = "";
		for (ServiceDescription operation : operations)
			operationsString += operation.toString();
		
		return "\n\t\t\t\t" + name + "\n\t\t\t\t\tRole: " + role + "\n\t\t\t\t\tProtocol: " + protocol + "\n\t\t\t\t\tEncoding: " + encoding + "\n\t\t\t\t\tOperations:" + operationsString;
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof InterfaceDesignDescription))
			return false;
		
		InterfaceDesignDescription idd = (InterfaceDesignDescription) o;
		
		return idd.getName().equals(this.getName()) && idd.getRole().equals(this.getRole());
	}
	
	//-------------------------------------------------------------------------------------------------
	public boolean checkConsistency(InterfaceDesignDescription other) {
		return 
				this.getName() == other.getName() &&
				this.getProtocol() == other.getProtocol() &&
				this.getEncoding() == other.getEncoding() && 
				this.getOperations().size() == other.getOperations().size();
	}
	
	
	//=================================================================================================
	// auxiliary classes
		
	//-------------------------------------------------------------------------------------------------
	public static class InterfaceComparator implements Comparator<InterfaceDesignDescription> {

		//=================================================================================================
		// methods
		
		//-------------------------------------------------------------------------------------------------
		@Override
		public int compare(InterfaceDesignDescription o1, InterfaceDesignDescription o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * 
	 * The ServiceDescription is a SysML stereotype that displays the type and payload of an operation.
	 * 
	 * @author fernand0labra
	 *
	 */
	public class ServiceDescription {
		
		//=================================================================================================
		// attributes
		
		private String name;  // Name of the operation
		private String method; // Method of the operation
		private String requestType;
		private ArrayList<Payload> requestPayload; // Request payload of the operation
		private String responseType;
		private ArrayList<Payload> responsePayload; // Response payload of the operation
		
		
		//=================================================================================================
		// auxiliary methods
		
		//-------------------------------------------------------------------------------------------------
		public String getName() { return name; }
		public String getMethod() { return method; }
		public String getRequestType() { return requestType; }
		public ArrayList<Payload> getRequestPayload() { return requestPayload; }
		public String getResponseType() { return responseType; }
		public ArrayList<Payload> getResponsePayload() { return responsePayload; }
		
		//-------------------------------------------------------------------------------------------------
		public void setName(String name) { this.name = name; }
		public void setMethod(String method) { this.method = method; }
		public void setRequestType(String requestType) { this.requestType = requestType; }
		public void setRequestPayload(ArrayList<Payload> requestPayload) { this.requestPayload = requestPayload; }
		public void setResponseType(String responseType) { this.responseType = responseType; }
		public void setResponsePayload(ArrayList<Payload> responsePayload) { this.responsePayload = responsePayload; }
		
		//-------------------------------------------------------------------------------------------------
		public ServiceDescription() {
			this.name = "";
			this.method = "";
			this.requestType = "";
			this.requestPayload = new ArrayList<Payload>();
			this.responseType = "";
			this.responsePayload = new ArrayList<Payload>();
		}
		
		public ServiceDescription(ServiceDescription other) {
			this.name = other.name;
			this.method = other.method;
			this.requestType = other.requestType;
			this.responseType = other .responseType;
			
			this.requestPayload = new ArrayList<Payload>();
			for(Payload payload : other.getRequestPayload())
				this.requestPayload.add(new Payload(payload));
			
			this.responsePayload = new ArrayList<Payload>();
			for(Payload payload : other.getResponsePayload())
				this.responsePayload.add(new Payload(payload));
		}
		
		//-------------------------------------------------------------------------------------------------
		@Override
		public String toString() {
			String type = "";
			type += !requestType.equals("") ? "\n\t\t\t\t\t\t\t Request Type: " + requestType : "";
			type += !responseType.equals("") ? "\n\t\t\t\t\t\t\t Response Type: " + responseType : "";
			
			String payloadString = "";
			
			String requestPayloadString = "";
			for (Payload payload : requestPayload)
				requestPayloadString += payload.toString();
			payloadString += !requestPayloadString.equals("") ? "\n\t\t\t\t\t\t\t Request Payload: " + requestPayloadString : "";
			
			String responsePayloadString = "";
			for (Payload payload : responsePayload)
				responsePayloadString += payload.toString();
			payloadString += !responsePayloadString.equals("")? "\n\t\t\t\t\t\t\t Response Payload: " + responsePayloadString : "";
			
			return "\n\t\t\t\t\t\t" + name + "\n\t\t\t\t\t\t\t Method: " + method + type + payloadString + "\n";
		}
		
		//-------------------------------------------------------------------------------------------------
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof ServiceDescription))
				return false;
			
			ServiceDescription sd = (ServiceDescription) o;
			
			return sd.getName().equals(this.getName());
		}
		
		//-------------------------------------------------------------------------------------------------
		public boolean checkConsistency(ServiceDescription other) {
			return 
					this.getName() == other.getName() &&
					this.getMethod() == other.getMethod() &&
					this.getRequestType() == other.getRequestType() && 
					this.getResponseType() == other.getResponseType();
		}
		
		
		//=================================================================================================
		// auxiliary classes
			
		//-------------------------------------------------------------------------------------------------
		public class OperationComparator implements Comparator<ServiceDescription> {

			//=================================================================================================
			// methods
				
			//-------------------------------------------------------------------------------------------------
			@Override
			public int compare(ServiceDescription o1, ServiceDescription o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		}
		
		//-------------------------------------------------------------------------------------------------
		/**
		 * 
		 * The payload of a certain ServiceDescription stereotype
		 * 
		 * @author fernand0labra
		 *
		 */
		public class Payload {
			
			//=================================================================================================
			// attributes
			
			private String name; // Name of the parameter
			private String type; // Type of the parameter
			
			
			//=================================================================================================
			// auxiliary methods
				
			//-------------------------------------------------------------------------------------------------
			public String getName() { return name; }
			public String getType() { return type; }
			
			//-------------------------------------------------------------------------------------------------
			public void setName(String name) { this.name = name; }
			public void setType(String type) { this.type = type; }
			
			//-------------------------------------------------------------------------------------------------
			public Payload() {
				this.name = "";
				this.type = "";
			}
			
			public Payload(Payload other) {
				this.name = other.name;
				this.type = other.type;
			}
			
			//-------------------------------------------------------------------------------------------------
			@Override
			public String toString() {
				return "\n\t\t\t\t\t\t\t\t" + name + " - " + type; 
			}
			
			
			//=================================================================================================
			// auxiliary classes
				
			//-------------------------------------------------------------------------------------------------
			public class PayloadComparator implements Comparator<Payload> { // TODO Use at some point

				//=================================================================================================
				// methods
					
				//-------------------------------------------------------------------------------------------------
				@Override
				public int compare(Payload o1, Payload o2) {
					return o1.getName().compareTo(o2.getName());
				}
				
			}
		}
		
	}
}
