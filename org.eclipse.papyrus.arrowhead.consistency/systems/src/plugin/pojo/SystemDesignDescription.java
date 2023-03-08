package plugin.pojo;

import java.util.ArrayList;

/**
 * 
 * The System Design Description is a SysML stereotype that describes the properties of a 
 * system regarding every interface that might work with
 *
 * @author fernand0labra
 *
 */
public class SystemDesignDescription {
	private String name; // Name of the System
	private String role; // Role of the System (Consumer, Provider, Provider_Consumer)
	
	private String serverAddress; // Server address of the (provider) system
	private String serverPort; // Server port of the (provider) system
	
	private ArrayList<InterfaceDesignDescription> IDDs; // List of implementing interfaces
	
	
	public String getName() { return name; }
	public String getRole() { return role; }
	public String getServerAddress() { return serverAddress; }
	public String getServerPort() { return serverPort; }
	public ArrayList<InterfaceDesignDescription> getIDDs() { return IDDs; }
	
	public void setName(String name) { this.name = name; }
	public void setRole(String role) { this.role = role; }
	public void setServerAddress(String serverAddress) { this.serverAddress = serverAddress; }
	public void setServerPort(String serverPort) { this.serverPort = serverPort; }
	public void setIDDs(ArrayList<InterfaceDesignDescription> iDDs) { IDDs = iDDs; }
	
	public SystemDesignDescription() {
		this.name = "";
		this.role = "";
		this.serverAddress = "";
		this.serverPort = "";
		this.IDDs = new ArrayList<InterfaceDesignDescription>();
	}
	
	public SystemDesignDescription(SystemDesignDescription other) {
		this.name = other.getName();
		this.role = other.getRole();
		this.serverAddress = other.getServerAddress();
		this.serverPort = other.getServerPort();
		this.IDDs = new ArrayList<InterfaceDesignDescription>();
		
		for(InterfaceDesignDescription idd : other.getIDDs())
			this.IDDs.add(new InterfaceDesignDescription(idd));
	}
	
	@Override
	public String toString() {
		String interfaceDesignString = "";
		for(InterfaceDesignDescription idd : IDDs)
			interfaceDesignString += idd.toString();
		
		return name + "\n\t\t\tRole: " + role + "\n\t\t\tServer Address: " + serverAddress + "\n\t\t\tserverPort: " + serverPort + "\n\t\t\tInterfaceDesignDescriptions:" + interfaceDesignString; 
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof SystemDesignDescription))
			return false;
		
		SystemDesignDescription system = (SystemDesignDescription) o;
		
		return system.getName().equals(this.getName());
	}
	
	public boolean checkConsistency(SystemDesignDescription other) {
		return
				this.getName() == other.getName() &&
				this.getRole() == other.getRole() &&
				this.getServerAddress() == other.getServerAddress() &&
				this.getServerPort() == other.getServerPort() &&
				this.getIDDs().size() == other.getIDDs().size();
	}
}
