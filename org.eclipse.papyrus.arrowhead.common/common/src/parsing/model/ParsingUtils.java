package parsing.model;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;

import com.google.common.base.CaseFormat;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

/**
 * This class offers a set of utils for managing UML/Ecore models
 * 
 * @author fernand0labra
 *
 */
public class ParsingUtils {

	//=================================================================================================
	// attributes
	
	private static Resource resource = null;

	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	/**
	 * A method that loads UML/Ecore model from input path.
	 * 
	 * @param modelPath The path of the model
	 * @return The parsed object of class type Model
	 */
	public static Object loadModel(String modelPath){		
		ResourceSet set = new ResourceSetImpl();

		set.getPackageRegistry()
			.put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);

		set.getResourceFactoryRegistry()
			.getExtensionToFactoryMap()
			.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap()
			.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);

		Object loadedModel = null;

		try {
			resource = set.getResource(URI.createFileURI(modelPath), true);
			Model model = (Model) EcoreUtil.getObjectByType(resource.getContents(), UMLPackage.Literals.MODEL);
			loadedModel = (model == null) ? resource.getContents().get(0) : model;         
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return loadedModel;
	}

	//-------------------------------------------------------------------------------------------------
	/**
	 * A method that saves UML/Ecore model from input object.
	 * 
	 * @param model The UML Model object
	 */
	public static void saveModel(Model model) {
		resource.getContents().set(0, model);

		try {
			resource.save(null);
		} catch (IOException e) {
			System.err.print(e.getMessage());
		}
	}
}
