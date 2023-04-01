package dto;

import java.util.ArrayList;

/**
 * 
 * The System Design Description is a SysML stereotype that describes the properties of a 
 * system regarding every interface that might work with
 *
 * @author fernand0labra
 *
 */
public class APXSystemDesignDescription {
	
	//=================================================================================================
	// attributes
	
	private String name; // Name of the System
	private String role; // Role of the System (Consumer, Provider, Provider_Consumer)
	
	private String serverAddress; // Server address of the (provider) system
	private String serverPort; // Server port of the (provider) system
	
	private ArrayList<APXInterfaceDesignDescription> IDDs; // List of implementing interfaces
	
	
	//=================================================================================================
	// auxiliary methods
	
	//-------------------------------------------------------------------------------------------------	
	public String getName() { return name; }
	public String getRole() { return role; }
	public String getServerAddress() { return serverAddress; }
	public String getServerPort() { return serverPort; }
	public ArrayList<APXInterfaceDesignDescription> getIDDs() { return IDDs; }
	
	public void setName(String name) { this.name = name; }
	public void setRole(String role) { this.role = role; }
	public void setServerAddress(String serverAddress) { this.serverAddress = serverAddress; }
	public void setServerPort(String serverPort) { this.serverPort = serverPort; }
	public void setIDDs(ArrayList<APXInterfaceDesignDescription> iDDs) { IDDs = iDDs; }
	
	//-------------------------------------------------------------------------------------------------
	public APXSystemDesignDescription() {
		this.name = "";
		this.role = "";
		this.serverAddress = "";
		this.serverPort = "";
		this.IDDs = new ArrayList<APXInterfaceDesignDescription>();
	}
	
	public APXSystemDesignDescription(APXSystemDesignDescription other) {
		this.name = other.getName();
		this.role = other.getRole();
		this.serverAddress = other.getServerAddress();
		this.serverPort = other.getServerPort();
		this.IDDs = new ArrayList<APXInterfaceDesignDescription>();
		
		for(APXInterfaceDesignDescription idd : other.getIDDs())
			this.IDDs.add(new APXInterfaceDesignDescription(idd));
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		String interfaceDesignString = "";
		for(APXInterfaceDesignDescription idd : IDDs)
			interfaceDesignString += idd.toString();
		
		return name + "\n\t\t\tRole: " + role + "\n\t\t\tServer Address: " + serverAddress + "\n\t\t\tserverPort: " + serverPort + "\n\t\t\tInterfaceDesignDescriptions:" + interfaceDesignString; 
	}
	
	//-------------------------------------------------------------------------------------------------
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof APXSystemDesignDescription))
			return false;
		
		APXSystemDesignDescription system = (APXSystemDesignDescription) o;
		
		return system.getName().equals(this.getName());
	}
	
	//-------------------------------------------------------------------------------------------------
	public boolean checkConsistency(APXSystemDesignDescription other) {
		return
				this.getName() == other.getName() &&
				this.getRole() == other.getRole() &&
				this.getServerAddress() == other.getServerAddress() &&
				this.getServerPort() == other.getServerPort() &&
				this.getIDDs().size() == other.getIDDs().size();
	}
}
