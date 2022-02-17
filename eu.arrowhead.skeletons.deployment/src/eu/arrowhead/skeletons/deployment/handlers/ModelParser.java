package eu.arrowhead.skeletons.deployment.handlers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.DeployedEntity;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.InterfaceDesignDescription;
//import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.IDD;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.LocalCLoudDesignDescription;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.impl.InterfaceDesignDescriptionImpl;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.Port;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

import eu.arrowhead.skeletons.deployment.dto.LocalCloudDTO;
import eu.arrowhead.skeletons.deployment.generator.ElementsPayload;
import eu.arrowhead.skeletons.deployment.generator.InterfaceMetadata;
import eu.arrowhead.skeletons.deployment.generator.OperationInt;

public class ModelParser {

	public ModelParser() {
		super();
		// TODO Auto-generated constructor stub
	}



	private ArrayList<LocalCloudDTO> localClouds= new ArrayList<LocalCloudDTO>();
	private Set<Classifier> systems = new HashSet<>();
	private boolean isProvider = false;
	private boolean isConsumer = false;
	private ArrayList<InterfaceMetadata> interfaces= new ArrayList<InterfaceMetadata>();
	private ArrayList<String []> systemServiceRegistry= new  ArrayList<String []>();
	
	
	public  void modelReader(String modelLocation) {
		
		 ModelLoader umlModel= new ModelLoader();
		 
		 
		 //modelLocation="D:\\SysMLPlugins\\Code\\Papyrus_new_profile\\SysML-AHT-master\\Example Models\\Studio4EducationAHTModel\\Studio4EducationAHTModel.uml";
		
		        
		        Object objModel = umlModel.loadModel(modelLocation);
		        Model sourceModel;
		        EList<PackageableElement> sourcePackagedElements = null;
		        
		        if (objModel instanceof Model) {
		            sourceModel = (Model) objModel;
		            sourcePackagedElements = sourceModel.getPackagedElements();
		        } else if (objModel instanceof Package) {
		            Package sourcePackage = (Package) objModel;
		           // sourcePackagedElements = sourcePackage.getPackagedElements();
		        }
		         
		        for (PackageableElement element : sourcePackagedElements){
		        	
		            //for nested package
		            if(element.eClass() == UMLPackage.Literals.PACKAGE){
		                org.eclipse.uml2.uml.Package nestedPackage = 
		             (org.eclipse.uml2.uml.Package) element;
		                EList<PackageableElement> nestedPackagedElements = 
		           nestedPackage.getPackagedElements();
		               
		                for (PackageableElement nestedElement : nestedPackagedElements){
		                  //  printModelDetails(nestedElement);
		                	System.out.println(element.getName());
		                }
		            }
		            else {
		            	
		            	System.out.println(element.getName());
		            	getDetails(element);
		            	getInterface(element);
		           
		            }
		        }

	}
	
	public void getDetails(PackageableElement element) {
		 LocalCloudDTO localcloud=new LocalCloudDTO();
		 ArrayList<String[]> sysList = new ArrayList<String[]>();
		 sysList.clear();
		if (element instanceof Classifier) {
			Classifier classifier = (Classifier) element;

			if (classifier instanceof Class) {

				LocalCLoudDesignDescription lc = UMLUtil.getStereotypeApplication(classifier, LocalCLoudDesignDescription.class);
				
				if (lc != null) {
					System.out.println("local cloud "+ lc.getBase_Class());
					
					 localcloud.setLcName(element.getName());
					
					 
				EList<Property> system_parts = classifier.getAllAttributes();

				
					for (Property system_part : system_parts) {
						isProvider=false;
						isConsumer=false;
						
						DeployedEntity depSys = UMLUtil.getStereotypeApplication(system_part, DeployedEntity.class);
						if (depSys != null) {
							
							
							//String id = depSys.getIdentifier();
							String name = system_part.getName();
							String[] sysdetails= new String[2];
							sysdetails [0]=name;
							
							String description = "";
							for (Iterator<Comment> iterator = system_part.getOwnedComments().iterator(); iterator.hasNext();) {
								
								Comment comment = iterator.next();
								description += comment.getBody();
							}
														
							 
							if (name != null && name.length() > 0) {
									
								System.out.println("systemName: "+name);
							}
						
							
							Classifier system = (Classifier) system_part.getType();
							EList<Property> atts = system.getAttributes();
						
							
								for (Property att : atts) {
									if (att instanceof Port) {
										Type AttType = att.getType();
										if (AttType!=null && name!=null) {
											String[] registry= new String [3]; 
											registry[0]=name;
											registry[1]=AttType.getName();
											
											
										
										
										if (!((Port) att).isConjugated()) {
											isProvider = true;
											registry[2]="provider";
										} else {
											isConsumer = true;
											registry[2]="consumer";
										}
										
										
										if(isProvider && isConsumer) {
											registry[2]="provider";
										}
										System.out.println("System:"+registry[0]+ "--Service: "+registry[1]+"--type: "+registry[2]);
										systemServiceRegistry.add(registry);
																				
										} 
									}
									
								}
						
								
						System.out.println("P: "+ isProvider +"--C: "+isConsumer);
							if(isProvider && isConsumer) {
								sysdetails [1]="ProviderConsumer";
							}else if(isProvider) { sysdetails [1]="Provider";}
							else {sysdetails [1]="Consumer";}
							
							sysList.add(sysdetails);
							System.out.println(sysdetails [0]+"---"+sysdetails [1]);
						}
						
					}
					
				
					localcloud.setSystems(sysList);
					localClouds.add(localcloud);
					
					System.out.println( "number of local clouds:"+	localClouds.size());
					System.out.println( "number of system-service register:"+	systemServiceRegistry.size());
				
				} else System.out.println("No local cloud" );
			}else System.out.println("No class" );
		}else System.out.println("No classifier");
	}
	
	public void getInterface(PackageableElement element) {
		InterfaceMetadata interfaceDescription= new InterfaceMetadata();
		ArrayList<OperationInt> opList= new ArrayList<OperationInt>();
	
		if (element instanceof Classifier) {
			Classifier classifier = (Classifier) element;

			if (classifier instanceof Class) {
				InterfaceDesignDescription idd = UMLUtil.getStereotypeApplication(classifier, InterfaceDesignDescription.class);
				
				if (idd != null) {
					
					
					System.out.println("SERVICE NAME:"+element.getName());
					interfaceDescription.setID(element.getName());
					System.out.println("SERVICE PROTOCOL:"+idd.getProtocol());
					interfaceDescription.setProtocol(idd.getProtocol().toString());
					
					 
				EList<Operation> operations = classifier.getAllOperations();
				
				System.out.println("number of operations:"+operations.size());
				
				//PAYLOAD INFORMATION
				for (Operation operation : operations) {
					
					ArrayList<ElementsPayload> elements_request = new ArrayList<ElementsPayload>(); 
				    ArrayList<ElementsPayload> elements_response = new ArrayList<ElementsPayload>();
				    ArrayList<String[]> payload_request = new ArrayList<String[]>(); 
				    ArrayList<String[]> payload_response = new ArrayList<String[]>();
				    ArrayList<String[]>  metadata_request = new ArrayList<String[]>();
				    ArrayList<String[]> metadata_response = new ArrayList<String[]>();
					Boolean request=false;
					Boolean response=false;
					OperationInt op=new OperationInt();
					
					System.out.println("SERVICE ENCODING:"+idd.getEncoding());
					op.setMediatype_request(idd.getEncoding().toString());
					op.setMediatype_response(idd.getEncoding().toString());
					
					System.out.println("Operation Name:"+ operation.getName());
					op.setOpName(operation.getName());
					op.setPathResource("/"+operation.getName());
					
					//TODO READ FROM operationKind
					if(op.getOpName().startsWith("get")||op.getOpName().startsWith("open")||op.getOpName().startsWith("close")) {
						op.setMethod("GET");
					}else if(op.getOpName().startsWith("set")||op.getOpName().startsWith("post")) {
						op.setMethod("POST");
					}else {
						op.setMethod("POST");
					}
					
					
					 
					
					if(op.getMethod().equalsIgnoreCase("GET")) response=true;
					else if(op.getMethod().equalsIgnoreCase("POST")) request=true;
					else {
						request=true;
						response=true;
					}
					
					op.setRequest(request);
					op.setResponse(response);
					EList<Parameter> parameters =operation.getOwnedParameters(); 
					System.out.println("number of parameters:"+ parameters.size());
					for(Parameter parameter : parameters) {
						
						 
						Type parameterType = parameter.getType();
						if (parameterType!=null) {
							System.out.println("Parameter Name:"+parameter.getName());
							 String[] ele=new String[2];
							 ele[0]=parameter.getName();
							System.out.println("Parameter Type:"+parameterType.getName());
							ele[1]=parameterType.getName();
						
						String[] metadata=new String[4];
						metadata[0]=ele[0];
				        metadata[1]=ele[1];
				        /* 
				         NO METEDATA IN THE MODEL
				         
				         metadata[2]= "temp";
				         metadata[3]= "celsius";
				         */
				        
				        
				        
				        if(response) {
				        	payload_response.add(ele);
							metadata_response.add(metadata);
							ElementsPayload elementsResponse = new ElementsPayload(payload_response,metadata_response);
							elements_response.add(elementsResponse);
							op.setElements_response(elements_response); 
				        }
				        if (request) {
				        	payload_request.add(ele);
							metadata_request.add(metadata);
							ElementsPayload elementsrequest = new ElementsPayload(payload_request,metadata_request);
							elements_request.add(elementsrequest);
							op.setElements_request(elements_request); 
				        }
						
						} 
						
						
					}
					if (elements_response.size()==0) { op.setResponse(false);}
					if (elements_request.size()==0) {op.setRequest(false);}
					
					opList.add(op);
					
					
				}
				interfaceDescription.setOperations(opList);
				System.out.println("INTERFACE:"+interfaceDescription.toString());
				interfaces.add(interfaceDescription);
				
				} else {
					
					System.out.println("********* NO IDD ********");
				}
				
			}
			}
		
		}
	
	
	
	

	
	
	
	public  ArrayList<LocalCloudDTO> getLocalClouds() {
		return localClouds;
	}



	public ArrayList<InterfaceMetadata> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(ArrayList<InterfaceMetadata> interfaces) {
		this.interfaces = interfaces;
	}

	public ArrayList<String[]> getSystemServiceRegistry() {
		return systemServiceRegistry;
	}

	public void setSystemServiceRegistry(ArrayList<String[]> systemServiceRegistry) {
		this.systemServiceRegistry = systemServiceRegistry;
	}
}
