package parsing.workspace;

import java.util.ArrayList;
import java.util.HashMap;

import dto.APXInterfaceDesignDescription;
import dto.APXLocalCloudDesignDescription;
import dto.APXSystemDesignDescription;

public class ParsingSetup {

	//=================================================================================================
	// attributes
		
	// Local Cloud objects parsed from the workspace
	public static HashMap<String, APXLocalCloudDesignDescription> workspaceLocalCloudMap = new HashMap<String, APXLocalCloudDesignDescription>();
	
	// System Design Description objects parsed from the workspace
	public static HashMap<String, APXSystemDesignDescription> workspaceSystemDescriptionMap = new HashMap<String, APXSystemDesignDescription>();
	
	// Interface Design Description objects parsed from the workspace
	public static HashMap<String, APXInterfaceDesignDescription> workspaceInterfaceDescriptionMap = new HashMap<String, APXInterfaceDesignDescription>();
	
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	/**
	 * Reads workspace, identifies providing/consuming projects and parses into DTOs
	 * 
	 * @param workspace The path to the workspace
	 */
	public static void parseWorkspace(String workspace) {
		workspaceLocalCloudMap.clear();
		workspaceSystemDescriptionMap.clear();
		workspaceInterfaceDescriptionMap.clear();
		
		// Identify local clouds in workspace
		ArrayList<String> localCloudsImplementation = new ArrayList<String>();
		for (String project : parsing.workspace.ParsingUtils.readWorkspace(workspace, true))
			if(!project.toLowerCase().contains("systems"))
				localCloudsImplementation.add(project);

		// For each local cloud (project folder)
		for (String localCloudName : localCloudsImplementation) {
			APXLocalCloudDesignDescription localCloud = parsing.workspace.PhysicalParser.parseLocalCloud(workspace, localCloudName);
			workspaceLocalCloudMap.put(ParsingUtils.toCamelCase(localCloudName), localCloud);
			
		}
	}
}
