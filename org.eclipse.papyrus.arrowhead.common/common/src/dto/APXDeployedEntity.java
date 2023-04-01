package dto;

import java.util.Comparator;

/**
 * 
 * The deployed entity is a SysML stereotype for a certain block type, it represents any of
 * the sensors or microprocessors included in the environment.
 * 
 * @author fernand0labra
 *
 */
public class APXDeployedEntity {	
	
	//=================================================================================================
	// attributes
	
	private String name; // Name of the deployed entity
	private String description; // Description of the deployed entity
	private APXSystemDesignDescription sysDD; // Block type of the deployed entity
	
	
	//=================================================================================================
	// auxiliary methods
	
	//-------------------------------------------------------------------------------------------------
	public String getName() { return name; }
	public String getDescription() { return description; }
	public APXSystemDesignDescription getSysDD() { return sysDD; }
	
	//-------------------------------------------------------------------------------------------------
	public void setName(String name) { this.name = name; }
	public void setDescription(String description) { this.description = description; }
	public void setSysDD(APXSystemDesignDescription sysDD) { this.sysDD = sysDD; }
	
	//-------------------------------------------------------------------------------------------------
	public APXDeployedEntity() {
		this.name = "";
		this.description = "";
		this.sysDD = new APXSystemDesignDescription();
	}
	
	public APXDeployedEntity(APXDeployedEntity other) {
		this.name = other.getName();
		this.description = other.getDescription();
		this.sysDD = new APXSystemDesignDescription(other.getSysDD());
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "\n\t" + name + "\n\t\tDescription: " + description + "\n\t\tSystemDesignDescription: " + sysDD.toString(); 
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof APXDeployedEntity))
			return false;
		
		APXDeployedEntity entity = (APXDeployedEntity) o;
		
		return entity.getName().equals(this.getName());
	}
	
	//-------------------------------------------------------------------------------------------------
	public boolean checkConsistency(APXDeployedEntity other) {
		return false; // TODO Complete
	}
	
	
	//=================================================================================================
	// auxiliary classes
	
	//-------------------------------------------------------------------------------------------------
	public static class DeployedEntityComparator implements Comparator<APXDeployedEntity> {

		//=================================================================================================
		// methods
		
		//-------------------------------------------------------------------------------------------------
		@Override
		public int compare(APXDeployedEntity o1, APXDeployedEntity o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}
