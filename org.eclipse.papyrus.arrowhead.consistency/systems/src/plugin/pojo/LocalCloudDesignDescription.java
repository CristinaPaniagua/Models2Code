package plugin.pojo;

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
public class LocalCloudDesignDescription {
	private String name = ""; // Name of the Local Cloud
	private ArrayList<DeployedEntity> deployedEntities = new ArrayList<DeployedEntity>(); // List of deployed entities of the Local Cloud
	
	
	public String getName() { return name; }
	public ArrayList<DeployedEntity> getDeployedEntities() { return deployedEntities; }
	
	public void setName(String name) { this.name = name; }
	public void setDeployedEntities(ArrayList<DeployedEntity> deployedEntities) { this.deployedEntities = deployedEntities; }
	
	@Override
	public String toString() {
		String deployedEntitiesString = "";
		for (DeployedEntity entity : deployedEntities)
			deployedEntitiesString += entity.toString();
		
		return name + "\n" + deployedEntitiesString;
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof LocalCloudDesignDescription))
			return false;
		
		LocalCloudDesignDescription lc = (LocalCloudDesignDescription) o;
		
		return lc.getName().equals(this.getName());
	}
	
	public static class LocalCloudComparator implements Comparator<LocalCloudDesignDescription> {

		@Override
		public int compare(LocalCloudDesignDescription o1, LocalCloudDesignDescription o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}
