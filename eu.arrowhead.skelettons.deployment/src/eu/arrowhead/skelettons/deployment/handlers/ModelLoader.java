package eu.arrowhead.skelettons.deployment.handlers;

import java.io.File;
import java.io.IOException;
import java.util.Map;


import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.mapping.ecore2xml.Ecore2XMLPackage;
import org.eclipse.emf.mapping.ecore2xml.util.Ecore2XMLResource;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UML212UMLResource;
import org.eclipse.uml2.uml.resource.UML22UMLResource;
import org.eclipse.uml2.uml.resource.UMLResource;

public class ModelLoader {

	 private final ResourceSet RESOURCE_SET;
	 
     public ModelLoader() {
     RESOURCE_SET = new ResourceSetImpl();
   }

/**
* A method that converts input path to uri and return uri.
* 
* @param path
* @return uri
* @throws Exception
*/
public String getFileURI(String path) throws Exception {
   File f = new File(path);
   String uri = f.toURI().toString();
   return uri; 
}



/**
* A method that loads UML/Ecore model from input URI.
* 
* @param uri:String
* @return model:Object
*/
public Object loadModel(String uri){
	System.out.println("URI:"+uri);
	ResourceSet set = new ResourceSetImpl();
	set.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
	set.getResourceFactoryRegistry().getExtensionToFactoryMap()
	   .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
	Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
	   .put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

	
   Resource resource = null;
   try {   
	   System.out.println(uri.toString());
       resource= set.getResource(URI.createFileURI(uri), true);
   }
   catch (Exception e) {
       e.printStackTrace();
   }

   Object result;

   Model _model = (Model) EcoreUtil.getObjectByType(resource.getContents(), 
   UMLPackage.Literals.MODEL);
   result = _model;
   //org.eclipse.uml2.uml.Package _model = (org.eclipse.uml2.uml.Package)EcoreUtil.getObjectByType(resource.getContents(),UMLPackage.Literals.PACKAGE);
   if (_model == null) {
       result = resource.getContents().get(0);         
   }
   return result;
   }   






    }
