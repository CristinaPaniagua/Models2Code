package org.eclipse.papyrus.arrowhead.common.dto;

import java.util.ArrayList;

public class LocalCloudDTO {


	private String lcName=" ";
	private ArrayList<String[]> systems = new ArrayList<String[]>();
	private ArrayList<String[]> connections = new ArrayList<String[]>();

	public LocalCloudDTO(String lcName, ArrayList<String[]> systems,ArrayList<String[]> connections) {
		super();
		this.lcName = lcName;
		this.systems = systems;
		this.connections = connections;
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

	
}
