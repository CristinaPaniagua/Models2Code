package parsing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.InterfaceDesignDescription;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.LocalCLoudDesignDescription;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.SysDD;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.util.UMLUtil;

import dto.APXInterfaceDesignDescription;
import dto.APXLocalCloudDesignDescription;
import dto.APXSystemDesignDescription;

public class ParsingSetup {

	//=================================================================================================
	// attributes
		
	// Local Cloud objects parsed from the model
	public static HashMap<String, APXLocalCloudDesignDescription> modelLocalCloudMap = new HashMap<String, APXLocalCloudDesignDescription>();
	
	// System Design Description objects parsed from the model
	public static HashMap<String, APXSystemDesignDescription> modelSystemDescriptionMap = new HashMap<String, APXSystemDesignDescription>();
	
	// Interface Design Description objects parsed from the model
	public static HashMap<String, APXInterfaceDesignDescription> modelInterfaceDescriptionMap = new HashMap<String, APXInterfaceDesignDescription>();
	
	// Packaged Model Elements
	public static HashMap<String, PackageableElement> packageLocalCloudDescriptionMap = new HashMap<String, PackageableElement>();
	public static HashMap<String, PackageableElement> packageSystemDescriptionMap = new HashMap<String, PackageableElement>();
	public static HashMap<String, PackageableElement> packageInterfaceDescriptionMap = new HashMap<String, PackageableElement>();
	
	public static Object parseModel(String modelPath) throws Exception{
		modelLocalCloudMap.clear();
		modelSystemDescriptionMap.clear();
		modelInterfaceDescriptionMap.clear();
		
		packageLocalCloudDescriptionMap.clear();
		packageSystemDescriptionMap.clear();
		packageInterfaceDescriptionMap.clear();
		
		// Open model file
		Object objectModel = parsing.model.ParsingUtils.loadModel(modelPath);
		if (!(objectModel instanceof Model))
			throw new Exception("The uml selected does not follow the modelling convention");
		
		// Obtain packaged elements
		EList<PackageableElement> sourcePackagedElements = null;
		sourcePackagedElements = ((Model) objectModel).getPackagedElements();

		for (PackageableElement element : sourcePackagedElements)  // For each block

			if (element instanceof Classifier) {
				Classifier classifier = (Classifier) element;
				
				if (classifier instanceof Class) {
					// Check if the block implements the local cloud stereotype			
					if(UMLUtil.getStereotypeApplication(classifier, LocalCLoudDesignDescription.class) != null)
						packageLocalCloudDescriptionMap.put(element.getName(), element);

					// Check if the block implements the system design stereotype				
					if(UMLUtil.getStereotypeApplication(classifier, SysDD.class) != null)
						packageSystemDescriptionMap.put(element.getName(), element);

					// Check if the block implements the interface design stereotype
					if(UMLUtil.getStereotypeApplication(classifier, InterfaceDesignDescription.class) != null)
						packageInterfaceDescriptionMap.put(element.getName(), element);
				}
			}

		// Parse the Interface Design Descriptions
		for(Entry<String, PackageableElement> packageInterface : packageInterfaceDescriptionMap.entrySet())
			if(!modelInterfaceDescriptionMap.containsKey(packageInterface.getKey())) // If the interface is not saved
				modelInterfaceDescriptionMap.put(packageInterface.getKey(), DefinitionParser.parseInterface(packageInterface.getValue()));
		
		// Parse the System Design Descriptions using the Interface Design Descriptions
		for(Entry<String, PackageableElement> packageSystem : packageSystemDescriptionMap.entrySet())
			if(!modelSystemDescriptionMap.containsKey(packageSystem.getKey())) // If the system is not saved
				modelSystemDescriptionMap.put(packageSystem.getKey(), DefinitionParser.parseSystem(packageSystem.getValue()));
		
		// Parse the LocalCloudDescriptions using the SystemDescriptions
		for(Entry<String, PackageableElement> packageLocalCloud : packageLocalCloudDescriptionMap.entrySet()) {
			APXLocalCloudDesignDescription localCloud = PhysicalParser.parseLocalCloud(packageLocalCloud.getValue());
			if(modelLocalCloudMap.get(localCloud.getName()) == null)
				modelLocalCloudMap.put(localCloud.getName(), localCloud);
		}
		
		return objectModel;
	}
	
}
