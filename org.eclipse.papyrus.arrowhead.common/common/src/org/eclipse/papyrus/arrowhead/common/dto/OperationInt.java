package org.eclipse.papyrus.arrowhead.common.dto;

import java.util.ArrayList;

public class OperationInt {
	
	ArrayList<ElementsPayload> elements_request ; 
    ArrayList<ElementsPayload> elements_response ;
    String PathResource;
    String Method;
    String Mediatype_request;
    String Mediatype_response;
    String complexType_request;
    String complexType_response;
    boolean request;
    boolean response;
    boolean param; 
    ArrayList<Param> parameters;
   ArrayList<String> subpaths;
   String opName;

	public OperationInt(ArrayList<ElementsPayload> elements_request, ArrayList<ElementsPayload> elements_response,
			String pathResource, String method, String mediatype_request, String mediatype_response,
			String complexType_request, String complexType_response, boolean request, boolean response, boolean param,
			ArrayList<Param> parameters, ArrayList<String> subpaths, String opName) {
		super();
		this.elements_request = elements_request;
		this.elements_response = elements_response;
		PathResource = pathResource;
		Method = method;
		Mediatype_request = mediatype_request;
		Mediatype_response = mediatype_response;
		this.complexType_request = complexType_request;
		this.complexType_response = complexType_response;
		this.request = request;
		this.response = response;
		this.param = param;
		this.parameters = parameters;
		this.subpaths = subpaths;
		this.opName=opName;
	}


	
	public OperationInt() {
		// TODO Auto-generated constructor stub
	}

	public ArrayList<ElementsPayload> getElements_request() {
		return elements_request;
	}

	public void setElements_request(ArrayList<ElementsPayload> elements_request) {
		this.elements_request = elements_request;
	}

	public ArrayList<ElementsPayload> getElements_response() {
		return elements_response;
	}

	public void setElements_response(ArrayList<ElementsPayload> elements_response) {
		this.elements_response = elements_response;
	}

	public String getPathResource() {
		return PathResource;
	}

	public void setPathResource(String pathResource) {
		PathResource = pathResource;
	}

	public String getMethod() {
		return Method;
	}

	public void setMethod(String method) {
		Method = method;
	}

	public String getMediatype_request() {
		return Mediatype_request;
	}

	public void setMediatype_request(String mediatype_request) {
		Mediatype_request = mediatype_request;
	}

	public String getMediatype_response() {
		return Mediatype_response;
	}

	public void setMediatype_response(String mediatype_response) {
		Mediatype_response = mediatype_response;
	}

	public String getComplexType_request() {
		return complexType_request;
	}

	public void setComplexType_request(String complexType_request) {
		this.complexType_request = complexType_request;
	}

	public String getComplexType_response() {
		return complexType_response;
	}

	public void setComplexType_response(String complexType_response) {
		this.complexType_response = complexType_response;
	}

	public boolean isRequest() {
		return request;
	}

	public void setRequest(boolean request) {
		this.request = request;
	}

	public boolean isResponse() {
		return response;
	}

	public void setResponse(boolean response) {
		this.response = response;
	}

	public boolean isParam() {
		return param;
	}

	public void setParam(boolean param) {
		this.param = param;
	}

	public ArrayList<Param> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<Param> parameters) {
		this.parameters = parameters;
	}

	public ArrayList<String> getSubpaths() {
		return subpaths;
	}

	public void setSubpaths(ArrayList<String> subpaths) {
		this.subpaths = subpaths;
	}

	@Override
	public String toString() {
		return "Operation [elements_request=" + elements_request + ", elements_response=" + elements_response
				+ ", PathResource=" + PathResource + ", Method=" + Method + ", Mediatype_request=" + Mediatype_request
				+ ", Mediatype_response=" + Mediatype_response + ", complexType_request=" + complexType_request
				+ ", complexType_response=" + complexType_response + ", request=" + request + ", response=" + response
				+ ", param=" + param + ", parameters=" + parameters + ", subpaths=" + subpaths + "]";
	}



	public String getOpName() {
		return opName;
	}

	public String getOpNameCapitalize() {
		String name= getOpName();
		String Name=name.substring(0, 1).toUpperCase()+ name.substring(1,name.length());
		return Name;
	}
	
	public void setOpName(String opName) {
		this.opName = opName;
	}

}
