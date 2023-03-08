package plugin.pojo;

import java.util.Comparator;

/**
 * 
 * The deployed entity is a SysML stereotype for a certain block type, it represents any of
 * the sensors or microprocessors included in the environment.
 * 
 * @author fernand0labra
 *
 */
public class DeployedEntity {	
	private String name; // Name of the deployed entity
	private String description; // Description of the deployed entity
	private SystemDesignDescription sysDD; // Block type of the deployed entity
	
	public String getName() { return name; }
	public String getDescription() { return description; }
	public SystemDesignDescription getSysDD() { return sysDD; }
	
	public void setName(String name) { this.name = name; }
	public void setDescription(String description) { this.description = description; }
	public void setSysDD(SystemDesignDescription sysDD) { this.sysDD = sysDD; }
	
	public DeployedEntity() {
		this.name = "";
		this.description = "";
		this.sysDD = new SystemDesignDescription();
	}
	
	public DeployedEntity(DeployedEntity other) {
		this.name = other.getName();
		this.description = other.getDescription();
		this.sysDD = new SystemDesignDescription(other.getSysDD());
	}
	
	@Override
	public String toString() {
		return "\n\t" + name + "\n\t\tDescription: " + description + "\n\t\tSystemDesignDescription: " + sysDD.toString(); 
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof DeployedEntity))
			return false;
		
		DeployedEntity entity = (DeployedEntity) o;
		
		return entity.getName().equals(this.getName());
	}
	
	public boolean checkConsistency(DeployedEntity other) {
		return false; // TODO Complete
	}
	
	public static class DeployedEntityComparator implements Comparator<DeployedEntity> {

		@Override
		public int compare(DeployedEntity o1, DeployedEntity o2) {
			return o1.getName().compareTo(o2.getName());
		}
		
	}
}
