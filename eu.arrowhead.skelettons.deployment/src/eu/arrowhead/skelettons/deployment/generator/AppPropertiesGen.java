package eu.arrowhead.skelettons.deployment.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

public class AppPropertiesGen {

	public AppPropertiesGen() {
		// TODO Auto-generated constructor stub
	}

	public void GenerateAppProperties(String directory, String name, String SysName, String SysType) {
		 VelocityEngine velocityEngine = new VelocityEngine();

		   velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
		   velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
		   velocityEngine.init();
		try {
		   Template t=velocityEngine.getTemplate("templates/properties.vm");
		   VelocityContext context = new VelocityContext();
		   context.put("type",SysType);
		   context.put("sysName",SysName);
		   Writer writer = new FileWriter (new File(directory+"\\"+name+"_ApplicationSystems\\"+SysName+"\\src\\main\\resources\\application.properties"));
		   t.merge(context,writer);
		   writer.flush();
		   writer.close();
		      
       } catch (IOException e) {
    	   e.printStackTrace();}
		
	}
	
}
