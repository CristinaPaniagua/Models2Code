/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.skeletons.deployment.generator;


import java.util.ArrayList;

/**
 *
 * @author cripan
 */
public class InterfaceMetadata {



	
	String Protocol;
    String ID;
    String URL;
    ArrayList<OperationInt> operations ; 
   
    public InterfaceMetadata() {
    }
    
	public InterfaceMetadata( String protocol, String iD, String uRL, ArrayList<OperationInt> operations) {
		super();
		Protocol = protocol;
		ID = iD;
		URL = uRL;
		this.operations = operations;
	}



	public String getProtocol() {
		return Protocol;
	}

	public void setProtocol(String protocol) {
		Protocol = protocol;
	}

	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String uRL) {
		URL = uRL;
	}

	public ArrayList<OperationInt> getOperations() {
		return operations;
	}

	public void setOperations(ArrayList<OperationInt> operations) {
		this.operations = operations;
	}

	@Override
	public String toString() {
		return "InterfaceMetadata [Protocol=" + Protocol + ", ID=" + ID + ", URL=" + URL + ", operations=" + operations.toString()
				+ "]";
	}







}

  



    
    
   
  

    
    
 







  
