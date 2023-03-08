package plugin.parsing.workspace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import plugin.PluginExecution;
import plugin.pojo.InterfaceDesignDescription;
import plugin.pojo.SystemDesignDescription;
import plugin.pojo.InterfaceDesignDescription.ServiceDescription;
import plugin.pojo.InterfaceDesignDescription.ServiceDescription.Payload;

/**
*
* This class parses the definition elements of the workspace, which are those files describing
* the SystemDesignDescription (SysDD) or InterfaceDesignDescription (IDD).
* 
* @author fernand0labra
* 
*/
public class DefinitionParser {
	
	private static String systemPath = ""; // Path to the system's folder
	
	/**
	 * Parses a system from the workspace folder that contains the files defining the 
	 * SystemDesignDescription (SysDD).
	 * 
	 * @param workspace Workspace path
	 * @param deployedEntityPath Path of the deployed entity folder
	 * @return The parsed SystemDesignDescription (SysDD)
	 */
	public static SystemDesignDescription parseSystem(String workspace, String deployedEntityPath) {		
		SystemDesignDescription systemDesignDescription = new SystemDesignDescription();	

		// Set role of the system from the name of the folder (_Consumer || _Provider)
		systemDesignDescription.setRole(deployedEntityPath.split("_")[1]);

		BufferedReader reader;

		try {
			// Open and read application.properties file
			reader = new BufferedReader(new FileReader(workspace + "\\src\\main\\resources\\application.properties"));
			String line = reader.readLine();

			while (line != null) {
				if(line.contains("client_system_name"))
					// Set name of the system
					systemDesignDescription.setName(line.split("=")[1]);

				// Save the server address for the provider
				if(deployedEntityPath.endsWith("_Provider"))
					if(line.contains("server.address"))
						systemDesignDescription.setServerAddress(line.split("=")[1]);
					else if(line.contains("server.port"))
						systemDesignDescription.setServerPort(line.split("=")[1]);

				line = reader.readLine();
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Build the system path
		systemPath = workspace + "\\src\\main\\java\\eu\\arrowhead\\" + deployedEntityPath + "\\";
		
		// Set the InterfaceDesignDescription list
		systemDesignDescription.setIDDs(parseInterface());

		// TODO Check if it is necessary
		if(!PluginExecution.workspaceSystemDescriptionMap.containsKey(systemDesignDescription.getName()))
			PluginExecution.workspaceSystemDescriptionMap.put(systemDesignDescription.getName(), systemDesignDescription);
		
		return systemDesignDescription;
	}

	/**
	 * Parses an interface from the workspace folder that contains the files defining the 
	 * InterfaceDesignDescription (IDD) of a specific system.
	 * 
	 * @return The parsed InterfaceDesignDescription (IDD)
	 */
	public static ArrayList<InterfaceDesignDescription> parseInterface() {
		ArrayList<String> dtoPaths = new ArrayList<String>();
		String mainPath = "";
		String serviceControllerPath = "";

		// Identify IDDs based on the file names of the folder
		for (String filePath : plugin.parsing.workspace.ParsingUtils.readWorkspace(systemPath, false))
			if (filePath.contains("Main")) // Consumer or Consumer/Provider operations
				mainPath = filePath;
			else if (filePath.contains("DTO")) // Consumer or Provider payload
				dtoPaths.add(filePath);
			else if (filePath.contains("ServiceController")) // Provider operations
				serviceControllerPath = filePath;

		ArrayList<InterfaceDesignDescription> interfaceDesignList = new ArrayList<InterfaceDesignDescription>();

		if(!mainPath.equals("")) // If the system behaves as a consumer
			interfaceDesignList.addAll(InterfaceParsingUtils.parseMain(systemPath + mainPath));

		if(!serviceControllerPath.equals("")) // If the system behaves as a provider
			interfaceDesignList.addAll(InterfaceParsingUtils.parseServiceController(systemPath + serviceControllerPath));
		
		return interfaceDesignList;
	}

	/**
	 * This class offers a set of utils for the parsing of the files describing the IDD operations and payload.
	 * 
	 * @author fernand0labra
	 *
	 */
	private static class InterfaceParsingUtils {

		/**
		 * Parse the file containing the main method and the consumer operations performed by the specific system
		 * 
		 * @param mainPath The path to the __Main.java file
		 * @return A list of consumer InterfaceDesignDescription objects
		 */
		public static ArrayList<InterfaceDesignDescription> parseMain(String mainPath) {
			BufferedReader reader;
			ArrayList<InterfaceDesignDescription> interfaceDesignList = new ArrayList<InterfaceDesignDescription>();
			HashMap<String, HashMap<String, HashMap<String, String>>> consumerInterfaces = new HashMap<String, HashMap<String, HashMap<String, String>>>();

			try {
				// Open ___Main.java file
				reader = new BufferedReader(new FileReader(mainPath));
				String line = reader.readLine();

				String currentInterface = "";
				String currentOperation = "";

				while (line != null) { // Parse information into POJO object

					if (line.contains("public void") && line.contains("()")) {
						String[] splitMethod = line.split(" ")[6].split("_"); // TODO Not very consistent to use the space as delimiter
						currentInterface = splitMethod[0];
						currentOperation = splitMethod[1];

						// If the interface is registered
						if (consumerInterfaces.containsKey(currentInterface))
							// Add a new operation structure
							consumerInterfaces.get(currentInterface).put(currentOperation, new HashMap<String, String>());

						else { // If the interface is not registered
							HashMap<String, HashMap<String, String>> operationMap = new HashMap<String, HashMap<String, String>>();														
							operationMap.put(currentOperation, new HashMap<String, String>());
							
							// Add a new interface structure with a new operation structure
							consumerInterfaces.put(currentInterface, operationMap);
						}
					}

					// Obtain method of the operation and response type
					if (line.contains("result") && line.contains("send")) {
						consumerInterfaces.get(currentInterface).get(currentOperation).put("responseType", line.split("=")[0].strip().split(" ")[0]);
						consumerInterfaces.get(currentInterface).get(currentOperation).put("method", line.split("send")[1].split("\\(")[0]);
					}

					// Obtain request and response payload (if any)
					if(line.contains("DTO") && (line.contains("request") || line.contains("response")) && line.contains("new"))
						if(line.contains("request"))
							consumerInterfaces.get(currentInterface).get(currentOperation).put("requestType", line.split("request")[0].strip());
						else
							consumerInterfaces.get(currentInterface).get(currentOperation).put("responseType", line.split("response")[0].strip());

					line = reader.readLine();
				}

				reader.close(); // Close info.txt file (IO operations)

			} catch (IOException e) {
				e.printStackTrace();
			}

			for (String interfaceName : consumerInterfaces.keySet()) { // For each of the interfaces
				// Parse the interface to an InterfaceDesignDescription object
				InterfaceDesignDescription interfaceDesign = new InterfaceDesignDescription();
				interfaceDesign.setName(interfaceName);
				interfaceDesign.setRole("Consumer");
				interfaceDesign.setProtocol("HTTP"); // TODO Code not adjusted for COAP so far
				interfaceDesign.setEncoding("JSON"); // TODO HTTP-SECURE-JSON  HTTP-INSECURE-JSON

				ArrayList<InterfaceDesignDescription.ServiceDescription> operationList = new ArrayList<InterfaceDesignDescription.ServiceDescription>();
				for (String serviceName : consumerInterfaces.get(interfaceName).keySet()) { // For each of the operations
					// Parse the operation to a ServiceDescription object
					InterfaceDesignDescription.ServiceDescription operation = interfaceDesign . new ServiceDescription ();
					operation.setName(serviceName);
					operation.setMethod(consumerInterfaces.get(interfaceName).get(serviceName).get("method"));

					// Set request type
					String requestType = consumerInterfaces.get(interfaceName).get(serviceName).get("requestType");
					operation.setRequestType(requestType != null ? requestType : "");

					// Set response type
					String responseType = consumerInterfaces.get(interfaceName).get(serviceName).get("responseType");
					operation.setResponseType(responseType != null ? responseType : "");

					operationList.add(operation);
				}

				interfaceDesign.setOperations(operationList);
				interfaceDesignList.add(interfaceDesign);
			}

			return interfaceDesignList;
		}

		/**
		 * Parse the file containing the provider operations performed by the specific system
		 * 
		 * @param serviceControllerPath The path to the ServiceController__.java file
		 * @return A list of provider InterfaceDesignDescription objects
		 */
		public static ArrayList<InterfaceDesignDescription> parseServiceController(String serviceControllerPath){
			BufferedReader reader;
			HashMap<String, HashMap<String, HashMap<String, String>>> providerInterfaces = new HashMap<String, HashMap<String, HashMap<String, String>>>();
			ArrayList<InterfaceDesignDescription> interfaceDesignList = new ArrayList<InterfaceDesignDescription>();

			try {
				// Open ServiceController__.java file
				reader = new BufferedReader(new FileReader(serviceControllerPath));
				String line = reader.readLine();

				String currentInterface = "";
				String currentOperation = "";

				while (line != null) { // Parse information into POJO object

					// Obtain the operation and associated interface
					if(line.contains("@") && line.contains("Mapping") && !line.contains("Request")) {
						String nextLine = reader.readLine();
						currentInterface = nextLine.split("public")[1].split(" ")[2].split("_")[0];
						currentOperation = line.split("=")[1].split("\"")[1].split("/")[1];

						// If the interface is registered
						if (providerInterfaces.containsKey(currentInterface))
							// Add a new operation structure
							providerInterfaces.get(currentInterface).put(currentOperation, new HashMap<String, String>());

						else { // If the interface is not registered
							HashMap<String, HashMap<String, String>> operationMap = new HashMap<String, HashMap<String, String>>();														
							operationMap.put(currentOperation, new HashMap<String, String>());
							
							// Add a new interface structure with a new operation structure
							providerInterfaces.put(currentInterface, operationMap);
						}

						// Obtain the method and the encoding of the operation
						providerInterfaces.get(currentInterface).get(currentOperation).put("method", line.split("@")[1].split("Mapping")[0]);
						providerInterfaces.get(currentInterface).get(currentOperation).put("encoding", line.split("MediaType")[1].split("_")[1]);

						// Obtain the response type (if any)
						providerInterfaces.get(currentInterface).get(currentOperation).put("responseType", nextLine.split("public")[1].split(" ")[1]);			

						// Obtain the request type (if any)
						if(nextLine.contains("RequestBody"))
							providerInterfaces.get(currentInterface).get(currentOperation).put("requestType", nextLine.split("final")[1].split(" ")[1]);
					}

					line = reader.readLine();
				}

				reader.close(); // Close ServiceController__.java file (IO operations)

			} catch (IOException e) {
				e.printStackTrace();
			}

			for (String interfaceName : providerInterfaces.keySet()) { // For each of the interfaces
				// Parse the interface into a InterfaceDesignDescription object
				InterfaceDesignDescription interfaceDesign = new InterfaceDesignDescription();
				
				// Obtain the name, protocol and encoding of the interface
				interfaceDesign.setName(interfaceName);
				interfaceDesign.setProtocol(serviceControllerPath.split("ServiceController")[1].split("\\.")[0].toUpperCase());
				interfaceDesign.setEncoding("JSON"); // TODO HTTP-SECURE-JSON  HTTP-INSECURE-JSON

				ArrayList<InterfaceDesignDescription.ServiceDescription> operationList	= new ArrayList<InterfaceDesignDescription.ServiceDescription>();

				for(String operationName : providerInterfaces.get(interfaceName).keySet()) { // For each of the operations
					// Parse the operation into a ServiceDescription object
					InterfaceDesignDescription.ServiceDescription operation = interfaceDesign . new ServiceDescription ();

					// Set name and method of the operation
					operation.setName(operationName);
					operation.setMethod(providerInterfaces.get(interfaceName).get(operationName).get("method"));
					
					// Obtain request type of the operation
					String requestType = providerInterfaces.get(interfaceName).get(operationName).get("requestType");
					operation.setRequestType(requestType == null ? "" : requestType);
					
					// If the request type is not a base type (e.g. String, int, float, etc.)
					if(requestType != null && !requestType.equals("String") && !requestType.equals("")) // TODO Check if "String" condition is necessary
						// Parse the payload into a Payload object
						operation.setRequestPayload(
								InterfaceParsingUtils.parsePayload(operation, systemPath + operationName.substring(0, 1).toUpperCase() + operationName.substring(1) + "RequestDTO.java"));

					// Obtain response type of the operation
					String responseType = providerInterfaces.get(interfaceName).get(operationName).get("responseType");
					operation.setResponseType(responseType == null ? "" : responseType);
					
					// If the request type is not a base type (e.g. String, int, float, etc.)
					if(responseType != null && !responseType.equals("String") && !responseType.equals(""))
						// Parse the payload into a Payload object
						operation.setResponsePayload(
								InterfaceParsingUtils.parsePayload(operation, systemPath + operationName.substring(0, 1).toUpperCase() + operationName.substring(1) + "ResponseDTO.java"));
								
					operationList.add(operation);
				}
				
				interfaceDesign.setOperations(operationList);
				
				// TODO Check if it is necessary
				if(!PluginExecution.workspaceInterfaceDescriptionMap.containsKey(interfaceName))
					PluginExecution.workspaceInterfaceDescriptionMap.put(interfaceName, new InterfaceDesignDescription(interfaceDesign));
				
				// Set role of the interface (if it has one provided service it's considered a provider)
				interfaceDesign.setRole("Provider");
				interfaceDesignList.add(interfaceDesign);
			}

			return interfaceDesignList;
		}

		/**
		 * Parse the file containing the payload of a specific operation
		 * 
		 * @param service The ServiceDescription object describing the operation
		 * @param pathDTO The path to the __DTO.java file
		 * @return A list of Payload objects
		 */
		public static ArrayList<Payload> parsePayload(ServiceDescription service, String pathDTO) {
			BufferedReader reader;
			ArrayList<Payload> payload = new ArrayList<Payload>();

			try {
				// Open ___DTO.java file
				reader = new BufferedReader(new FileReader(pathDTO));
				String line = reader.readLine();

				while (line != null) { // Parse information into POJO object

					if (line.contains("private") && !line.contains("\\(\\)")) {
						Payload element = service . new Payload();
						
						// Obtain name and type of the payload element
						element.setName(line.split("private")[1].strip().split(" ")[1].split(";")[0]);
						element.setType(line.split("private")[1].strip().split(" ")[0]);
						
						payload.add(element);
					}

					line = reader.readLine();
				}

				reader.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

			return payload;

		}
	}
}
