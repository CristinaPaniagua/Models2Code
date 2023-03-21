package generator;

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

import deployment.ExecutionUtils;
import dto.InterfaceMetadata;
import dto.OperationInt;

/**
 * 
 * Generation of the ProviderMain.java, ServerApplication.java  and ApplicationInitListener.java files
 * 
 * @author cripan
 *
 */
public class ProviderMain {

	// =================================================================================================
	// attributes

	public static ArrayList<ArrayList<String>> classesRequest = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> classesResponse = new ArrayList<String>();


	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generation of the Provider Main and Server Application for providing system
	 * 
	 * @param Directory The path to the file
	 * @param name The name of the local cloud
	 * @param system The name of the system
	 * @param systemServiceRegistry List of systems in the service registry
	 * @param interfaces List of interfaces of the consumer
	 */
	public static void generateProviderMain(String Directory, String name, String system, ArrayList<String[]> systemServiceRegistry, ArrayList<InterfaceMetadata> interfaces) {

		ArrayList<InterfaceMetadata> serviceInterfaces = new ArrayList<InterfaceMetadata>();
		System.out.println("START GENERATION  PROVIDER: **" + system + "** " + systemServiceRegistry.size() + "--" + interfaces.size());

		for (int m = 0; m < systemServiceRegistry.size(); m++) { // For each entry in the service registry
			String[] systemService = systemServiceRegistry.get(m);
			System.out.println("sys: " + systemService[0]); // TODO Remove Trace
			System.out.println("serv: " + systemService[1]); // TODO Remove Trace

			// If the entry is for this system
			if (systemService[2].equals("provider") && systemService[0].equals(system)) {
				System.out.println("MATCH:" + m); // TODO Remove Trace
				// Find the matching service interface
				for (int n = 0; n < interfaces.size(); n++)
					if (interfaces.get(n).getID().equals(systemService[1])) {
						System.out.println("MATCH interface number:" + n); // TODO Remove Trace
						serviceInterfaces.add(interfaces.get(n));
					}
			}
		}

		for (int h = 0; h < serviceInterfaces.size(); h++) { // For each registered service interface
			InterfaceMetadata MD = serviceInterfaces.get(h);

			ArrayList<OperationInt> operations = MD.getOperations();
			// Generate response and request payload
			for (int i = 0; i < operations.size(); i++) {
				OperationInt op = operations.get(i);
				GenerationUtils.objectClassGen(Directory, name, system, op, "provider");
			}
		}

		// Check protocol type
		boolean coap = GenerationUtils.checkCoapProtocol(serviceInterfaces);

		// Initialise Velocity Engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		try {
			// Create and write Provider Main class file
			Template t = velocityEngine.getTemplate("templates/providerMain.vm");
			VelocityContext context = new VelocityContext();
			context.put("packagename", "provider"); // _Provider
			context.put("sysName", system);
			context.put("coap", coap);

			// Writer writer = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/" + system + "ProviderMain.java"));
			Writer writer = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\" + system + "ProviderMain.java"));
			t.merge(context, writer);
			writer.flush();
			writer.close();

			if (coap) {
				// Create and write Coap Server Application class file
				Template tc = velocityEngine.getTemplate("templates/coapServer.vm");
				VelocityContext contextc = new VelocityContext();
				contextc.put("packagename", "provider"); // _Provider
				
				// Writer writerc = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/ServerApplication.java"));
				Writer writerc = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java/eu\\arrowhead\\provider\\ServerApplication.java"));
				tc.merge(contextc, writerc);
				writerc.flush();
				writerc.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Generate the Application Listener
		providerGenAppListener(serviceInterfaces, system, Directory, name);
		// Generate the Controller
		providerController(serviceInterfaces, system, Directory, name);

	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generation of the Provider Main and Server Application for consuming and providing system
	 * 
	 * @param Directory The path to the file
	 * @param name The name of the local cloud
	 * @param system The name of the system
	 * @param systemServiceRegistry List of systems in the service registry
	 * @param interfaces List of interfaces of the consumer
	 */
	public static void generateProvConsMain(String Directory, String name, String system, ArrayList<String[]> systemServiceRegistry, ArrayList<InterfaceMetadata> interfaces) {

		ArrayList<InterfaceMetadata> serviceInterfacesProvider = new ArrayList<InterfaceMetadata>();
		ArrayList<InterfaceMetadata> serviceInterfacesConsumer = new ArrayList<InterfaceMetadata>();
		System.out.println("START GENERATION  PROVIDER-CONSUMER: **" + system + "** " + systemServiceRegistry.size() + "--" + interfaces.size()); // TODO Remove Trace

		for (int m = 0; m < systemServiceRegistry.size(); m++) { // For each entry in the service registry
			String[] systemService = systemServiceRegistry.get(m);
			System.out.println("sys: " + systemService[0]); // TODO Remove Trace
			System.out.println("serv: " + systemService[1]); // TODO Remove Trace
			System.out.println("type: " + systemService[2]); // TODO Remove Trace

			// If the entry is for this system
			if (systemService[0].equals(system)) {
				System.out.println("MATCH:" + m); // TODO Remove Trace
				String serv = systemService[1];

				// If it acts as provider
				if (systemService[2].equalsIgnoreCase("provider")) { 
					for (int n = 0; n < interfaces.size(); n++)
						if (interfaces.get(n).getID().equals(serv)) {
							System.out.println("MATCH interface number:" + n); // TODO Remove Trace
							serviceInterfacesProvider.add(interfaces.get(n));
						}
				}
				// If it acts as consumer
				else
					for (int n = 0; n < interfaces.size(); n++) {
						if (interfaces.get(n).getID().equals(serv)) {
							System.out.println("MATCH interface number:" + n + interfaces.get(n).getID()); // TODO Remove Trace
							serviceInterfacesConsumer.add(interfaces.get(n));
						}
					}
			}
		}
		
		// For each service that the system provides
		for (int l = 0; l < serviceInterfacesProvider.size(); l++) {
			InterfaceMetadata MDP = serviceInterfacesProvider.get(l);
			System.out.println(MDP.toString()); // TODO Remove Trace
			String service = MDP.getID(); // TODO Not Used

			// Generate response and request payload
			ArrayList<OperationInt> operations = MDP.getOperations();
			for (int i = 0; i < operations.size(); i++) {
				OperationInt op = operations.get(i);
				GenerationUtils.objectClassGen(Directory, name, system, op, "provider");
			}

			// Generate the Controller
			providerController(serviceInterfacesProvider, system, Directory, name);

		}

		// For each service that the system consumes
		for (int p = 0; p < serviceInterfacesConsumer.size(); p++) {
			InterfaceMetadata MDC = serviceInterfacesConsumer.get(p);
			System.out.println(MDC.toString()); // TODO Remove Trace
			String service = MDC.getID(); // TODO Not Used

			// Generate response and request payload
			ArrayList<OperationInt> operations = MDC.getOperations();
			for (int i = 0; i < operations.size(); i++) {
				OperationInt op = operations.get(i);
				GenerationUtils.objectClassGen(Directory, name, system, op, "provider-consumer");
			}
		}

		// Check protocol type
		boolean providerCoap = GenerationUtils.checkCoapProtocol(serviceInterfacesProvider);
		boolean consumerCoap = GenerationUtils.checkCoapProtocol(serviceInterfacesConsumer);
		boolean consumerHttp = GenerationUtils.checkHttpProtocol(serviceInterfacesConsumer);

		// Initialise Velocity Engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		try {
			serviceInterfacesConsumer = GenerationUtils.removeRepetitions(serviceInterfacesConsumer);

			// Create and write Provider Main class file
			Template t = velocityEngine.getTemplate("templates/providerConsumerMain.vm");
			VelocityContext context = new VelocityContext();
			context.put("packagename", "provider"); // _Provider
			context.put("sysName", system);
			context.put("interfaces", serviceInterfacesConsumer);
			context.put("address", "http://127.0.0.1:8888"); // TODO Update from service registry
			context.put("httpFlag", consumerHttp);
			context.put("coapFlag", consumerCoap);

			// Writer writer = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/" + system + "ProviderMain.java"));
			Writer writer = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\" + system + "ProviderMain.java"));
			t.merge(context, writer);
			writer.flush();
			writer.close();

			if (providerCoap) {
				// Create and write Coap Server Application class file
				Template tc = velocityEngine.getTemplate("templates/coapServer.vm");
				VelocityContext contextc = new VelocityContext();
				contextc.put("packagename", "provider"); // _Provider

				// Writer writerc = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/ServerApplication.java"));
				Writer writerc = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\ServerApplication.java"));
				tc.merge(contextc, writerc);
				writerc.flush();
				writerc.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// Generate Application Listener
		providerGenAppListener(serviceInterfacesProvider, system, Directory, name);

	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generation of the Application Listeners
	 * 
	 * @param serviceInterfaces List of service interfaces of the system
	 * @param system The name of the system
	 * @param Directory The path of the file
	 * @param name The name of the local cloud
	 */
	public static void providerGenAppListener(ArrayList<InterfaceMetadata> serviceInterfaces, String system, String Directory, String name) {
		serviceInterfaces = GenerationUtils.removeRepetitions(serviceInterfaces);

		// Initialise VelocityEngine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		try {
			// Create and write the Application Listener
			Template t = velocityEngine.getTemplate("templates/providerAppListener.vm");
			VelocityContext context = new VelocityContext();
			context.put("packagename", "provider");
			context.put("interfaces", serviceInterfaces);

			// Writer writer = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/ProviderApplicationInitListener.java"));
			Writer writer = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\ProviderApplicationInitListener.java"));
			t.merge(context, writer);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generation of the Service Controller for providing system
	 * 
	 * @param serviceInterfaces List of service interfaces of the system
	 * @param system The name of the system
	 * @param Directory The path of the file
	 * @param name The name of the local cloud
	 */
	public static void providerController(ArrayList<InterfaceMetadata> serviceInterfaces, String system, String Directory, String name) {
		serviceInterfaces = GenerationUtils.removeRepetitions(serviceInterfaces);
		
		ArrayList<InterfaceMetadata> serviceInterfacesCoap = new ArrayList<InterfaceMetadata>();
		ArrayList<InterfaceMetadata> serviceInterfacesHttp = new ArrayList<InterfaceMetadata>();
		
		// Assign interface with type of protocol
		for (int i = 0; i < serviceInterfaces.size(); i++) {
			InterfaceMetadata interfaceM = serviceInterfaces.get(i);
			if (interfaceM.getProtocol().toLowerCase().contains("CoAP".toLowerCase()))
				serviceInterfacesCoap.add(interfaceM);
			if(interfaceM.getProtocol().toLowerCase().contains("HTTP".toLowerCase()))
				serviceInterfacesHttp.add(interfaceM);
		}

		// Initialise Velocity Engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		try {
			if (serviceInterfacesHttp.size() > 0) {
				// Create and write Service Controller file for HTTP
				Template th = velocityEngine.getTemplate("templates/providerController.vm");
				VelocityContext contexth = new VelocityContext();
				contexth.put("packagename", "provider");
				contexth.put("interfaces", serviceInterfacesHttp);

				// Writer writerh = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/ServiceControllerHttp.java"));
				Writer writerh = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\ServiceControllerHttp.java"));
				th.merge(contexth, writerh);
				writerh.flush();
				writerh.close();
			}

			if (serviceInterfacesCoap.size() > 0) {
				// Create and write Service Controller file for CoAP
				Template tc = velocityEngine.getTemplate("templates/providerControllerCoap.vm");
				VelocityContext contextc = new VelocityContext();
				contextc.put("packagename", "provider");
				contextc.put("interfaces", serviceInterfacesCoap);

				// Writer writerc = new FileWriter(new File(Directory + "/" + name + "_ApplicationSystems/" + system + "_Provider/src/main/java/eu/arrowhead/" + system + "_Provider/ServiceControllerCoap.java"));
				Writer writerc = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-provider\\src\\main\\java\\eu\\arrowhead\\provider\\ServiceControllerCoap.java"));
				tc.merge(contextc, writerc);
				writerc.flush();
				writerc.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
