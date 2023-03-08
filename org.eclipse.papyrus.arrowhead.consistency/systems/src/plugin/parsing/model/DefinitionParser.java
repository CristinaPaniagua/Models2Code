package plugin.parsing.model;

import java.util.ArrayList;

import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.HttpOperation;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.util.UMLUtil;

import plugin.PluginExecution;
import plugin.pojo.InterfaceDesignDescription;
import plugin.pojo.SystemDesignDescription;
import plugin.pojo.InterfaceDesignDescription.ServiceDescription;
import plugin.pojo.InterfaceDesignDescription.ServiceDescription.Payload;

/**
 *
 * This class parses the definition elements of the model, which are those blocks implementing
 * the SystemDesignDescription (SysDD) or InterfaceDesignDescription (IDD) stereotypes.
 * 
 * @author fernand0labra
 * 
 */
public class DefinitionParser {

	/**
	 * Parses a system from a packageable element that potentially implements the SystemDesignDescription
	 * (SysDD) stereotype.
	 * 
	 * @param element The packageable element of the block
	 * @return The parsed SystemDesignDescription (SysDD)
	 */
	public static SystemDesignDescription parseSystem(PackageableElement element) {
		Classifier classifier = (Classifier) element;

		SystemDesignDescription systemDescription = new SystemDesignDescription();
		org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.SysDD modelSystem = 
				UMLUtil.getStereotypeApplication(classifier, org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.SysDD.class);

		// Set name of the system
		systemDescription.setName(element.getName());

		// Get the ports of the system (implemented interfaces)
		EList<Port> modelPorts = modelSystem.getBase_Class().getOwnedPorts();
		for (Port modelPort : modelPorts) { // For each of the interfaces
			
			// Get a copy of the interface from the parsed interfaces map
			InterfaceDesignDescription portInterface = new InterfaceDesignDescription(PluginExecution.modelInterfaceDescriptionMap.get(modelPort.getType().getName()));

			// Set role of the interface
			portInterface.setRole(modelPort.isConjugated() ? "Consumer" : "Provider");
			
			// Set role of the system (once a provider always a provider)
			systemDescription.setRole(portInterface.getRole().equals("Provider") ? "Provider" : "Consumer");
			
			// Add interface as long as it's not included already
			if(!systemDescription.getIDDs().contains(portInterface))
				systemDescription.getIDDs().add(portInterface);			
		}

		return systemDescription;
	}

	/**
	 * Parses an interface from a packageable element that potentially implements the InterfaceDesignDescription
	 * (IDD) stereotype.
	 * 
	 * @param element The packageable element of the block
	 * @return The parsed InterfaceDesignDescription (IDD)
	 */
	public static InterfaceDesignDescription parseInterface(PackageableElement element) {
		Classifier classifier = (Classifier) element;

		InterfaceDesignDescription interfaceDescription = new InterfaceDesignDescription();
		org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.InterfaceDesignDescription modelInterface = 
				UMLUtil.getStereotypeApplication(classifier, org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.InterfaceDesignDescription.class);

		// Set name, protocol and encoding of the interface
		interfaceDescription.setName(element.getName());
		interfaceDescription.setProtocol(modelInterface.getProtocol().toString());
		interfaceDescription.setEncoding(modelInterface.getEncoding().toString());

		// Get the operations of the interface
		EList<Operation> modelOperations = classifier.getAllOperations();
		ArrayList<ServiceDescription> operationList = new ArrayList<ServiceDescription>();

		for (Operation modelOperation : modelOperations) { // For each of the operations		
			ServiceDescription operation = interfaceDescription . new ServiceDescription();
			modelOperation = UMLUtil.getStereotypeApplication(modelOperation, HttpOperation.class).getBase_Operation();
			
			// Set name and method of the operation
			operation.setName(modelOperation.getName());
			operation.setMethod(UMLUtil.getStereotypeApplication(modelOperation, HttpOperation.class).getKind().toString());

			// Get the parameters of the operation
			EList<org.eclipse.uml2.uml.Parameter> modelParameters = modelOperation.getOwnedParameters(); 

			for(org.eclipse.uml2.uml.Parameter modelParameter : modelParameters) { // For each of the parameters
				Payload payload = operation . new Payload();
				Type parameterType = modelParameter.getType();

				if(parameterType != null) {

					// Set name and type of the parameter
					payload.setName(modelParameter.getName());
					payload.setType(parameterType.getName());

					// If it is a request parameter
					if(modelParameter.getDirection().getName().equals("in")) {
						if(operation.getRequestType().equals("")) // If the request type was not set
							operation.setRequestType(operation.getName().substring(0, 1).toUpperCase() + operation.getName().substring(1) + "RequestDTO");
						operation.getRequestPayload().add(payload);
					}

					// If it is a response parameter
					else if(modelParameter.getDirection().getName().equals("return")) { // Response
						if(operation.getResponseType().equals("")) // If the response type was not set
							operation.setResponseType(operation.getName().substring(0, 1).toUpperCase() + operation.getName().substring(1) + "ResponseDTO");
						operation.getResponsePayload().add(payload);
					}		

				} else // If the parameter object is not defined set response to string 
					operation.setResponseType("String"); // TODO Check validity
			}

			operationList.add(operation);
		}

		interfaceDescription.setOperations(operationList);
		
		return interfaceDescription;
	}

}
