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
	public static ArrayList<APXLocalCloudDesignDescription> workspaceLocalCloudList = new ArrayList<APXLocalCloudDesignDescription>();
	
	// System Design Description objects parsed from the workspace
	public static HashMap<String, APXSystemDesignDescription> workspaceSystemDescriptionMap = new HashMap<String, APXSystemDesignDescription>();
	
	// Interface Design Description objects parsed from the workspace
	public static HashMap<String, APXInterfaceDesignDescription> workspaceInterfaceDescriptionMap = new HashMap<String, APXInterfaceDesignDescription>();
	
	public static void parseWorkspace(String workspace) {
		// Identify local clouds in workspace
		ArrayList<String> localCloudsImplementation = new ArrayList<String>();
		for (String project : parsing.workspace.ParsingUtils.readWorkspace(workspace, true))
			if(project.endsWith("_ApplicationSystems"))
				localCloudsImplementation.add(project);

		// For each local cloud (project folder)
		for (String localCloud : localCloudsImplementation)
			workspaceLocalCloudList.add(parsing.workspace.PhysicalParser.parseLocalCloud(workspace, localCloud));
	}
}
