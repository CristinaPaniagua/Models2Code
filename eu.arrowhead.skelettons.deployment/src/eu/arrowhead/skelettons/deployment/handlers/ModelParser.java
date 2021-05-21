package eu.arrowhead.skelettons.deployment.handlers;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.papyrus.designer.languages.arrowhead.codegen.profile.ahtsysmlprofile.DeployedSystem;
import org.eclipse.papyrus.designer.languages.arrowhead.codegen.profile.ahtsysmlprofile.LocalCloud;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.util.UMLUtil;

import eu.arrowhead.skelettons.deployment.dto.LocalCloudDTO;

public class ModelParser {

	private static ArrayList<LocalCloudDTO> localClouds= new ArrayList<LocalCloudDTO>();
	
	
	public static void modelReader() {
		
		 ModelLoader umlModel= new ModelLoader();
		  
		        
		        Object objModel = umlModel.loadModel("D:\\SysMLPlugins\\Code\\SysML-AHT-master\\Example Models\\Studio4EducationAHTModel\\Studio4EducationAHTModel.uml");
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
		            }
		        }

	}
	
	public static void getDetails(PackageableElement element) {
		 LocalCloudDTO localcloud=new LocalCloudDTO();
		 ArrayList<String> sysList = new ArrayList<String>();
		if (element instanceof Classifier) {
			Classifier classifier = (Classifier) element;

			if (classifier instanceof Class) {

				LocalCloud lc = UMLUtil.getStereotypeApplication(classifier, LocalCloud.class);
				if (lc != null) {
					System.out.println("local cloud "+ lc.getBase_Class());
					
					 localcloud.setLcName(element.getName());
					 
				EList<Property> system_parts = classifier.getAllAttributes();

				
					for (Property system_part : system_parts) {
						DeployedSystem depSys = UMLUtil.getStereotypeApplication(system_part, DeployedSystem.class);
						if (depSys != null) {
							
							
							String id = depSys.getIdentifier();
							String name = system_part.getName();
							sysList.add(name);
							String description = "";
							for (Iterator<Comment> iterator = system_part.getOwnedComments().iterator(); iterator.hasNext();) {
								
								Comment comment = iterator.next();
								description += comment.getBody();
							}
														
							 
							if (name != null && name.length() > 0) {
									
								System.out.println("system "+name);
							}
						
						}
						localcloud.setSystems(sysList);
						localClouds.add(localcloud);
					}
				} else System.out.println("No local cloud" );
			}else System.out.println("No class" );
		}else System.out.println("No classifier");
	}
	
	

	public static ArrayList<LocalCloudDTO> getLocalClouds() {
		return localClouds;
	}
}
