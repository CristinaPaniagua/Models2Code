package generator;

import java.util.ArrayList;

import java.io.IOException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import deployment.ExecutionUtils;
import dto.APXInterfaceDesignDescription;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

/**
 * 
 * Generation of the ConsumerMain.java file
 * 
 * @author cripan
 *
 */
public class ConsumerMain {

	// =================================================================================================
	// attributes

	private static APXInterfaceDesignDescription MD = null; // TODO Not Used
	public static ArrayList<ArrayList<String>> classesRequest = new ArrayList<ArrayList<String>>();
	public static ArrayList<String> classesResponse = new ArrayList<String>(); // TODO Not Used


	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generation of the Consumer Main
	 * 
	 * @param Directory The path to the file
	 * @param name The name of the local cloud
	 * @param system The name of the system
	 * @param systemServiceRegistry List of systems in the service registry
	 * @param interfaces List of interfaces of the consumer
	 */
	public static void generateConsumerMain(String Directory, String name, String system, ArrayList<String[]> systemServiceRegistry, ArrayList<APXInterfaceDesignDescription> interfaces) {

		classesRequest.clear();
		classesResponse.clear();
		
		ArrayList<APXInterfaceDesignDescription> serviceInterfaces = new ArrayList<APXInterfaceDesignDescription>();

		for (int m = 0; m < systemServiceRegistry.size(); m++) { // For each entry in the service registry
			String[] systemService = systemServiceRegistry.get(m);

			// If the entry is for this system
			if (systemService[2].equals("consumer") && systemService[0].equals(system)) {
				// Find the matching service interface
				for (int n = 0; n < interfaces.size(); n++)
					if (interfaces.get(n).getName().equals(systemService[1])) {
						serviceInterfaces.add(interfaces.get(n));
					}
			}
		}

		for (int p = 0; p < serviceInterfaces.size(); p++) { // For each registered service interface
			APXInterfaceDesignDescription MDC = serviceInterfaces.get(p);
			String service = MDC.getName(); // TODO Not Used

			ArrayList<APXInterfaceDesignDescription.APXServiceDescription> operations = MDC.getOperations();
			// Generate response and request payload
			for (int i = 0; i < operations.size(); i++) {
				APXInterfaceDesignDescription.APXServiceDescription op = operations.get(i);
				GenerationUtils.objectClassGen(Directory, name, system, op, "consumer");
			}
		}

		// Check protocol type
		boolean httpFlag = GenerationUtils.checkHttpProtocol(serviceInterfaces);
		boolean coapFlag = GenerationUtils.checkCoapProtocol(serviceInterfaces);

		// Initialise Velocity Engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();

		try {
			serviceInterfaces = GenerationUtils.removeRepetitions(serviceInterfaces);

			// Create and write Consumer Main class file
			Template t = velocityEngine.getTemplate("templates/consumer/consumerMain.vm");
			VelocityContext context = new VelocityContext();
			context.put("packagename", "consumer"); // _Consumer
			context.put("sysName", system);
			context.put("interfaces", serviceInterfaces);
			context.put("address", "http://127.0.0.1:8888"); // TODO Update from service registry
			context.put("httpFlag", httpFlag);
			context.put("coapFlag", coapFlag);
			
			ArrayList<String> dtos = new ArrayList<String>(classesResponse);
			for(ArrayList<String> classRequest : classesRequest)
				dtos.add(classRequest.get(0).split(" ")[0]);
			for(String classResponse : classesResponse)
				dtos.add(classResponse.split(" ")[0]);
			context.put("dtos", dtos);

			Writer writer = new FileWriter(new File(Directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "-consumer\\src\\main\\java\\eu\\arrowhead\\consumer\\" + system + "ConsumerMain.java"));
			t.merge(context, writer);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}