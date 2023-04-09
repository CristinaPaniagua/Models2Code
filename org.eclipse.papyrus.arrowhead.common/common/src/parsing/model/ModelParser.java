package parsing.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.DeployedEntity;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.InterfaceDesignDescription;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.HttpOperation;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.LocalCLoudDesignDescription;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.ConnectableElement;
import org.eclipse.uml2.uml.Connector;
import org.eclipse.uml2.uml.ConnectorEnd;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.util.UMLUtil;

import dto.APXLocalCloudDesignDescription;
import dto.APXInterfaceDesignDescription;

/**
 * 
 * Parses the model from a .uml file
 * 
 * @author cripan
 *
 */
public class ModelParser {

	// =================================================================================================
	// attributes

	// -------------------------------------------------------------------------------------------------
	private ArrayList<APXLocalCloudDesignDescription> localClouds = new ArrayList<APXLocalCloudDesignDescription>();
	private Set<Classifier> systems = new HashSet<>();
	private boolean isProvider = false;
	private boolean isConsumer = false;
	private ArrayList<APXInterfaceDesignDescription> interfaces = new ArrayList<APXInterfaceDesignDescription>();
	private ArrayList<ArrayList<String>> systemServiceRegistry = new ArrayList<ArrayList<String>>();
	
	
	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Read model and parse information
	 * 
	 * @param modelLocation Path of the .uml file
	 */
	public void modelReader(String modelLocation) {

		Object objModel = parsing.model.ParsingUtils.loadModel(modelLocation);
		Model sourceModel;
		EList<PackageableElement> sourcePackagedElements = null;

		if (objModel instanceof Model) {
			sourceModel = (Model) objModel;
			sourcePackagedElements = sourceModel.getPackagedElements();
		} else if (objModel instanceof Package) {
			Package sourcePackage = (Package) objModel;
		}

		for (PackageableElement element : sourcePackagedElements)
			if (element.eClass() == UMLPackage.Literals.PACKAGE) { // TODO Not Used
				org.eclipse.uml2.uml.Package nestedPackage = (org.eclipse.uml2.uml.Package) element;
				EList<PackageableElement> nestedPackagedElements = nestedPackage.getPackagedElements();
			} else { 
				getDetails(element); // Obtain details
				getInterface(element);
			}
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Obtain local cloud and system details from a packageable element
	 * 
	 * @param element The packageable element implementing the LocalCloudDesignDescription Stereotype Application
	 */
	public void getDetails(PackageableElement element) {
		APXLocalCloudDesignDescription localcloud = new APXLocalCloudDesignDescription();
		ArrayList<ArrayList<String>> sysList = new ArrayList<ArrayList<String>>();
		HashMap<String, ArrayList<String>> connectorsSystems= new HashMap<String, ArrayList<String>>();
		connectorsSystems = getConnections(element);	

		sysList.clear();
		if (element instanceof Classifier) {
			Classifier classifier = (Classifier) element;

			if (classifier instanceof Class) {

				LocalCLoudDesignDescription lc = UMLUtil.getStereotypeApplication(classifier, LocalCLoudDesignDescription.class);

				if (lc != null) {
					localcloud.setName(element.getName());

					EList<Property> system_parts = classifier.getAllAttributes();

					for (Property system_part : system_parts) {
						isProvider = false;
						isConsumer = false;

						DeployedEntity depSys = UMLUtil.getStereotypeApplication(system_part, DeployedEntity.class);
						if (depSys != null) {
							String name = system_part.getName();
							ArrayList<String> sysdetails = new ArrayList<String>();
							sysdetails.add(name);

							String description = "";
							for (Iterator<Comment> iterator = system_part.getOwnedComments().iterator(); iterator.hasNext();) {
								Comment comment = iterator.next();
								description += comment.getBody();
							}

							Classifier system = (Classifier) system_part.getType();
							EList<Property> atts = system.getAttributes();

							for (Property att : atts)
								if (att instanceof Port) {
									Type AttType = att.getType();
									if (AttType != null && name != null) {
										ArrayList<String> registry = new ArrayList<String>();
										registry.add(name);
										registry.add(AttType.getName());

										if (!((Port) att).isConjugated()) {
											isProvider = true;
											registry.add("provider");
										} else {
											isConsumer = true;
											registry.add("consumer");
										}

										if (isProvider && isConsumer)
											registry.add("provider");

										systemServiceRegistry.add(registry);
									}
								}

							if (isProvider && isConsumer)
								sysdetails.add("ProviderConsumer");
							else if (isProvider)
								sysdetails.add("Provider");
							else
								sysdetails.add("Consumer");

							sysList.add(sysdetails);
						}

					}

					localcloud.setSystemsModel(sysList);
					localcloud.setConnections(connectorsSystems);
					localcloud.setSystemsSR(systemServiceRegistry);
					localClouds.add(localcloud);

				}
			}
		}
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Obtain interface details from a packageable element
	 * 
	 * @param element The packageable element implementing the InterfaceDesignDescription Stereotype Application
	 */
	public void getInterface(PackageableElement element) {
		APXInterfaceDesignDescription interfaceDescription = new APXInterfaceDesignDescription();
		ArrayList<APXInterfaceDesignDescription.APXServiceDescription> opList = new ArrayList<APXInterfaceDesignDescription.APXServiceDescription>();

		if (element instanceof Classifier) {
			Classifier classifier = (Classifier) element;

			if (classifier instanceof Class) {
				InterfaceDesignDescription idd = UMLUtil.getStereotypeApplication(classifier, InterfaceDesignDescription.class);

				if (idd != null) {

					interfaceDescription.setName(element.getName());
					interfaceDescription.setProtocol(idd.getProtocol().toString());

					EList<Operation> operations = classifier.getAllOperations();

					// Obtain payload information
					for (Operation operation : operations) {

						ArrayList<APXInterfaceDesignDescription.APXServiceDescription.APXPayload> elements_request = new ArrayList<APXInterfaceDesignDescription.APXServiceDescription.APXPayload>();
						ArrayList<APXInterfaceDesignDescription.APXServiceDescription.APXPayload> elements_response = new ArrayList<APXInterfaceDesignDescription.APXServiceDescription.APXPayload>();
						ArrayList<String[]> payload_request = new ArrayList<String[]>();
						ArrayList<String[]> payload_response = new ArrayList<String[]>();
						ArrayList<String[]> metadata_request = new ArrayList<String[]>();
						ArrayList<String[]> metadata_response = new ArrayList<String[]>();
						Boolean request = false;
						Boolean response = false;
						APXInterfaceDesignDescription.APXServiceDescription op = interfaceDescription . new APXServiceDescription();

						op.setRequestEncoding(idd.getEncoding().toString());
						op.setResponseEncoding(idd.getEncoding().toString());
						op.setName(operation.getName());
						op.setPath("/" + operation.getName());

						op.setMethod(UMLUtil.getStereotypeApplication(operation, HttpOperation.class).getKind().toString());

						if (op.getMethod().equalsIgnoreCase("GET"))
							response = true;
						else if (op.getMethod().equalsIgnoreCase("POST"))
							request = true;
						else {
							request = true;
							response = true;
						}

						op.setRequest(request);
						op.setResponse(response);
						EList<Parameter> parameters = operation.getOwnedParameters();
						
						// Obtain parameter information
						for (Parameter parameter : parameters) {

							Type parameterType = parameter.getType();
							if (parameterType != null) {
								String[] ele = new String[2];
								ele[0] = parameter.getName();
								ele[1] = parameterType.getName();

								String[] metadata = new String[4];
								metadata[0] = ele[0];
								metadata[1] = ele[1];

								if (response) {
									payload_response.add(ele);
									metadata_response.add(metadata);
									APXInterfaceDesignDescription.APXServiceDescription.APXPayload elementsResponse =  op . new APXPayload(payload_response, metadata_response);
									elements_response.add(elementsResponse);
									op.setResponsePayload(elements_response);
								}
								if (request) {
									payload_request.add(ele);
									metadata_request.add(metadata);
									APXInterfaceDesignDescription.APXServiceDescription.APXPayload elementsrequest = op . new APXPayload(payload_request, metadata_request);
									elements_request.add(elementsrequest);
									op.setRequestPayload(elements_request);
								}
							}
						}
						
						if (elements_response.size() == 0)
							op.setResponse(false);

						if (elements_request.size() == 0)
							op.setRequest(false);

						opList.add(op);

					}
					interfaceDescription.setOperations(opList);
					interfaces.add(interfaceDescription);

				}
			}
		}
	}
	
	/**
	 * 
	 * Obtain connection details from packageable element
	 * 
	 * @param element The packageable element defining the connections
	 * @return A list with the name of the connector/service and the systems with their roles
	 */
	public HashMap<String, ArrayList<String>> getConnections(PackageableElement element) {

		HashMap<String, ArrayList<String>> connectorsSystems = new HashMap<String, ArrayList<String>>();
		ArrayList<ArrayList<String>> sysList = new ArrayList<ArrayList<String>>();
		ArrayList<String> connector;
		sysList.clear();

		if (element instanceof Classifier) {
			Classifier classifier = (Classifier) element;

			if (classifier instanceof Class) {

				Class LoCl = (Class) classifier;

				if (LoCl != null) {
					EList<Connector> connectorsList = LoCl.getOwnedConnectors();
					for (int j = 0; j < connectorsList.size(); j++) {
						// Obtain connector and end systems
						Connector c = connectorsList.get(j);
						EList<ConnectorEnd> connectorsEndList = c.getEnds();
						
						connector = new ArrayList<String>();
						connector.add(c.getName()); // Name of the connector/service
						
						ArrayList<String> connectorRole = new ArrayList<String>();
						ArrayList<String> connectorName = new ArrayList<String>();
						ArrayList<String> connectorPort = new ArrayList<String>();
						
						for (ConnectorEnd ce : connectorsEndList) { // For each end system
							ConnectableElement role = ce.getRole();
							connectorRole.add(role.getType().getName()); // Role of the system
														
							Element SysElement = role.getOwner();
							Class SysClass = (Class) SysElement;
							connectorName.add(SysClass.getName()); // Name of the system
							connectorPort.add(role.getName()); // Port of the system
						}
						
						connector.addAll(connectorRole);
						connector.addAll(connectorName);
						connector.addAll(connectorPort);
						
						connectorsSystems.put(connectorName.get(0) + "-" + connectorName.get(1), connector);
					}
				}
			}
		}
		return connectorsSystems;
	}
	
	
	// =================================================================================================
	// auxiliary methods

	// -------------------------------------------------------------------------------------------------
	public ModelParser() { super(); }

	// -------------------------------------------------------------------------------------------------
	public ArrayList<APXLocalCloudDesignDescription> getLocalClouds() { return localClouds; }
	public ArrayList<APXInterfaceDesignDescription> getInterfaces() { return interfaces; }
	public ArrayList<ArrayList<String>> getSystemServiceRegistry() { return systemServiceRegistry; }
	
	// -------------------------------------------------------------------------------------------------
	public void setInterfaces(ArrayList<APXInterfaceDesignDescription> interfaces) { this.interfaces = interfaces; }

}
