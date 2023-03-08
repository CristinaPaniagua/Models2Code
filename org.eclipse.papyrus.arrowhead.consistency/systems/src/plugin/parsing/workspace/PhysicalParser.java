package plugin.parsing.workspace;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import plugin.pojo.DeployedEntity;
import plugin.pojo.LocalCloudDesignDescription;

/**
 * 
 * This class parses the physical elements of the workspace, which are those files describing
 * the LocalCloudDesignDescription or DeployedEntity.
 * 
 * @author fernand0labra
 *
 */
public class PhysicalParser {
	
	/**
	 * Parses a local cloud from the workspace folder that contains the files defining the LocalCloudDesignDescription.
	 * 
	 * @param workspace The path of the workspace
	 * @param localCloudPath The path of the local cloud folder
	 * @return The parsed LocalCloudDesignDescription object
	 */
	public static LocalCloudDesignDescription parseLocalCloud(String workspace, String localCloudPath){

		LocalCloudDesignDescription localCloud = new LocalCloudDesignDescription();
		localCloud.setName(localCloudPath.split("_")[0]);

		// Identify deployed entities 
		ArrayList<String> deployedEntitiesImplementation = new ArrayList<String>();
		for (String deployedEntityPath : plugin.parsing.workspace.ParsingUtils.readWorkspace(workspace + localCloudPath + "\\", true))
			if(deployedEntityPath.endsWith("_Consumer") || deployedEntityPath.endsWith("_Provider"))
				deployedEntitiesImplementation.add(deployedEntityPath);

		for (String deployedEntityPath : deployedEntitiesImplementation) // For each provider and consumer folders
			// Parse system into a DeployedEntity
			localCloud.getDeployedEntities().add(parseDeployedEntity(workspace + localCloudPath + "\\", deployedEntityPath));

		return localCloud;
	}

	/**
	 * Parses a deployed entity from the workspace folder that contains the files defining the DeployedEntity
	 * 
	 * @param workspace The path of the workspace
	 * @param deployedEntityPath The path of the deployed entity folder
	 * @return The parsed DeploydEntity object
	 */
	public static DeployedEntity parseDeployedEntity(String workspace, String deployedEntityPath) {

		DeployedEntity deployedEntity = new DeployedEntity();
		deployedEntity.setName(deployedEntityPath.split("_")[0]); // Obtain name of the deployed entity

		BufferedReader reader;

		try {
			// Open info.txt file
			reader = new BufferedReader(new FileReader(workspace + deployedEntityPath + "\\src\\main\\java\\eu\\arrowhead\\info.txt"));
			String line = reader.readLine();

			// Obtain description of the deployed entity
			String description = "";
			while (line != null) {
				description += line;
				line = reader.readLine();
			}

			reader.close();

			deployedEntity.setDescription(description);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Obtain the SystemDesignDescription (SysDD) of the deployed entity
		deployedEntity.setSysDD(DefinitionParser.parseSystem(workspace + deployedEntityPath + "\\", deployedEntityPath));

		return deployedEntity;
	}
}
