package generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import deployment.ExecutionUtils;

/**
 * 
 * Generation of the ConsumerApplicationInitListener.java file
 * 
 * @author cripan
 *
 */
public class ConsumerAppList {

	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generation of Consumer Application Listener
	 * 
	 * @param directory The path to the file
	 * @param name The name of the local cloud
	 * @param SysName The name of the system
	 */
	public static void GenerateAppList(String directory, String name, String SysName) {
		// Initialise Velocity Engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		velocityEngine.init();
		
		try {
			// Generate consumer application listener from template
			Template t = velocityEngine.getTemplate("templates/consumer/applicationListener.vm");
			VelocityContext context = new VelocityContext();
			context.put("packagename", "consumer");
			Writer writer = new FileWriter(new File(directory + "\\arrowhead\\" + name + "\\cloud-systems\\" + SysName + "\\src\\main\\java\\eu\\arrowhead\\consumer\\ConsumerApplicationInitListener.java"));
			t.merge(context, writer);
			writer.flush();
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
