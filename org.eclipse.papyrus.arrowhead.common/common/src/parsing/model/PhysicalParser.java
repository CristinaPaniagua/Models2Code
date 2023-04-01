package parsing.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.emf.common.util.EList;
import org.eclipse.papyrus.arrowhead.profile.arrowheadsysmlprofile.DeployedEntity;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.util.UMLUtil;

import dto.APXDeployedEntity;
import dto.APXLocalCloudDesignDescription;

/**
 * This class parses the physical elements of the model, which are those blocks implementing
 * the LocalCloudDesignDescription or DeployedEntity stereotypes.
 * 
 * @author fernand0labra
 *
 */
public class PhysicalParser {

	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Parses a local cloud from a packageable element that potentially implements the 
	 * LocalCloudDesignDescription stereotype.
	 * 
	 * @param element The packageable element of the block
	 * @return The parsed LocalCloudDesignDescription
	 */
	public static APXLocalCloudDesignDescription parseLocalCloud(PackageableElement element){
		Classifier classifier = (Classifier) element;

		APXLocalCloudDesignDescription localCloud = new APXLocalCloudDesignDescription();

		// Set the name of the local cloud
		localCloud.setName(element.getName());
		
		// Get the attributes of the local cloud (deployed entities)
		EList<Property> umlDeployedEntities = classifier.getAllAttributes();
		ArrayList<APXDeployedEntity> deployedEntities = new ArrayList<APXDeployedEntity>();

		for (Property umlDeployedEntity : umlDeployedEntities) { // For each deployed entity
			APXDeployedEntity deployedEntity = new APXDeployedEntity();
			DeployedEntity modelDeployedEntity = UMLUtil.getStereotypeApplication(umlDeployedEntity, DeployedEntity.class);

			if (modelDeployedEntity != null) { // If it has the DeployedEntity stereotype

				// Build the description of the deployed entity
				String description = "";
				for (Iterator<Comment> iterator = umlDeployedEntity.getOwnedComments().iterator(); iterator.hasNext();)
					description += ((Comment) (iterator.next())).getBody() + "\n";

				// Set the name, description and SystemDesignDescription
				deployedEntity.setName(umlDeployedEntity.getName());						
				deployedEntity.setDescription(description);
				deployedEntity.setSysDD(ParsingSetup.modelSystemDescriptionMap.get(umlDeployedEntity.getType().getName()));
				
				deployedEntities.add(deployedEntity);
			}						
		}

		localCloud.setDeployedEntities(deployedEntities);

		return localCloud;
	}
	
	
}
