package dto;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * 
 * The Local Cloud Design Description is a SysML stereotype that includes a list
 * of deployed entities with their associated System Design Descriptions
 *
 * @author fernand0labra
 *
 */
public class APXLocalCloudDesignDescription {
	
	//=================================================================================================
	// attributes
	
	private String name = ""; // Name of the Local Cloud
	private ArrayList<APXDeployedEntity> deployedEntities = new ArrayList<APXDeployedEntity>(); // List of deployed entities of the Local Cloud
	
	// Legacy
	private ArrayList<String[]> systemsModel = new ArrayList<String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();
	private ArrayList<String []> systemsSR = new  ArrayList<String []>();
	
	
	//=================================================================================================
	// auxiliary methods
	
	//-------------------------------------------------------------------------------------------------	
	public String getName() { return name; }
	public ArrayList<APXDeployedEntity> getDeployedEntities() { return deployedEntities; }
	public ArrayList<String[]> getSystemsModel() { return systemsModel; }
	public ArrayList<String[]> getConnections() { return connections; }
	public ArrayList<String[]> getSystemsSR() { return systemsSR; }
	
	//-------------------------------------------------------------------------------------------------
	public void setName(String name) { this.name = name; }
	public void setDeployedEntities(ArrayList<APXDeployedEntity> deployedEntities) { this.deployedEntities = deployedEntities; }
	public void setSystemsModel(ArrayList<String[]> systemsModel) { this.systemsModel = systemsModel; }
	public void setConnections(ArrayList<String[]> connections) { this.connections = connections; }
	public void setSystemsSR(ArrayList<String[]> systemsSR) { this.systemsSR = systemsSR; }

	//-------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		String deployedEntitiesString = "";
		for (APXDeployedEntity entity : deployedEntities)
			deployedEntitiesString += entity.toString();
		
		return name + "\n" + deployedEntitiesString;
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof APXLocalCloudDesignDescription))
			return false;
		
		APXLocalCloudDesignDescription lc = (APXLocalCloudDesignDescription) o;
		
		return lc.getName().equals(this.getName());
	}
	
	
	//=================================================================================================
	// auxiliary classes
	
	//-------------------------------------------------------------------------------------------------	
	public static class LocalCloudComparator implements Comparator<APXLocalCloudDesignDescription> {

		//=================================================================================================
		// methods
		
		//-------------------------------------------------------------------------------------------------			
		@Override
		public int compare(APXLocalCloudDesignDescription o1, APXLocalCloudDesignDescription o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}
