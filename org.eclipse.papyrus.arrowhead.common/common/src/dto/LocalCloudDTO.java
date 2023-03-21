package dto;

import java.util.ArrayList;

public class LocalCloudDTO {


	private String lcName=" ";
	private ArrayList<String[]> systems = new ArrayList<String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();
	private ArrayList<String []> systemServiceRegistry = new  ArrayList<String []>();

	public LocalCloudDTO(String lcName, ArrayList<String[]> systems, ArrayList<String[]> connections, ArrayList<String []> registry) {
		super();
		this.lcName = lcName;
		this.systems = systems;
		this.connections = connections;
		this.systemServiceRegistry = registry;
	}

	public LocalCloudDTO() {
		// TODO Auto-generated constructor stub
	}
	
 
	public String getLcName() {
		return lcName;
	}

	public void setLcName(String lcName) {
		this.lcName = lcName;
	}

	public ArrayList<String[]> getSystems() {
		return systems;
	}

	public void setSystems(ArrayList<String[]> systems) {
		this.systems = systems;
	}

	public ArrayList<String[]> getConnections() {
		return connections;
	}

	public void setConnections(ArrayList<String[]> connections) {
		this.connections = connections;
	}

	public ArrayList<String[]> getSystemServiceRegistry() {
		return systemServiceRegistry;
	}

	public void setSystemServiceRegistry(ArrayList<String[]> systemServiceRegistry) {
		this.systemServiceRegistry = systemServiceRegistry;
	}
	
}
