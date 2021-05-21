package eu.arrowhead.skelettons.deployment.dto;

import java.util.ArrayList;

public class LocalCloudDTO {


	private String lcName=" ";
	private ArrayList<String> systems = new ArrayList<String>();

	public LocalCloudDTO(String lcName, ArrayList<String> systems) {
		super();
		this.lcName = lcName;
		this.systems = systems;
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

	public ArrayList<String> getSystems() {
		return systems;
	}

	public void setSystems(ArrayList<String> systems) {
		this.systems = systems;
	}






	
}
