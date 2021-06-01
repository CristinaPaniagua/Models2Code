/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.skelettons.deployment.generator;


import java.util.ArrayList;

/**
 *
 * @author cripan
 */
public class InterfaceMetadata {
    String Protocol;
    String PathResource;
    String Method;
    String Mediatype_request;
    String Mediatype_response;
    String ID;
    String complexType_request;
    String complexType_response;
    ArrayList<ElementsPayload> elements_request ; 
    ArrayList<ElementsPayload> elements_response ;
    boolean request;
    boolean response;
    boolean param; 
    ArrayList<Param> parameters;
   ArrayList<String> subpaths;
    
    
    public InterfaceMetadata() {
    }

    public InterfaceMetadata(String Protocol, String PathResource, String Method, String Mediatype_request, String Mediatype_response, String ID, String complexType_request, String complexType_response, ArrayList<ElementsPayload> elements_request, ArrayList<ElementsPayload> elements_response, boolean request, boolean response, boolean param, ArrayList<Param> parameters, ArrayList<String> subpaths) {
        this.Protocol = Protocol;
        this.PathResource = PathResource;
        this.Method = Method;
        this.Mediatype_request = Mediatype_request;
        this.Mediatype_response = Mediatype_response;
        this.ID = ID;
        this.complexType_response = complexType_response;
        this.complexType_request = complexType_request;
        this.elements_request = elements_request;
        this.elements_response = elements_response;
        this.request = request;
        this.response = response;
        this.param = param;
        this.parameters = parameters;
        this.subpaths = subpaths;
    }

 
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setProtocol(String Protocol) {
        this.Protocol = Protocol;
    }

    public void setPathResource(String PathResource) {
        this.PathResource = PathResource;
    }

    public void setMethod(String Method) {
        this.Method = Method;
    }

    public String getProtocol() {
        return Protocol;
    }

    public String getPathResource() {
        return PathResource;
    }

    public String getMethod() {
        return Method;
    }

    public String getMediatype_request() {
        return Mediatype_request;
    }

    public String getMediatype_response() {
        return Mediatype_response;
    }

  
    public void setMediatype_request(String Mediatype_request) {
        this.Mediatype_request = Mediatype_request;
    }

    public void setMediatype_response(String Mediatype_response) {
        this.Mediatype_response = Mediatype_response;
    }


    public ArrayList<ElementsPayload> getElements_request() {
        return elements_request;
    }

    public ArrayList<ElementsPayload> getElements_response() {
        return elements_response;
    }

    public void setElements_request(ArrayList<ElementsPayload> elements_request) {
        this.elements_request = elements_request;
    }

    public void setElements_response(ArrayList<ElementsPayload> elements_response) {
        this.elements_response = elements_response;
    }

    public String getComplexType_response() {
        return complexType_response;
    }

    public String getComplexType_request() {
        return complexType_request;
    }

    public void setComplexType_response(String complexType_response) {
        this.complexType_response = complexType_response;
    }

    public void setComplexType_request(String complexType_request) {
        this.complexType_request = complexType_request;
    }

    public boolean getRequest() {
        return request;
    }

    public boolean getResponse() {
        return response;
    }



    public void setRequest(boolean request) {
        this.request = request;
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
        return "InterfaceMetadata{" + "Protocol=" + Protocol + ", PathResource=" + PathResource + ", Method=" + Method + ", Mediatype_request=" + Mediatype_request + ", Mediatype_response=" + Mediatype_response + ", ID=" + ID + ", complexType_response=" + complexType_response + ", complexType_request=" + complexType_request + ", elements_request=" + elements_request + ", elements_response=" + elements_response + ", request=" + request + ", response=" + response + ", param=" + param + ", parameters=" + parameters + ", subpaths=" + subpaths + '}';
    }
   
}

  



    
    
   
  

    
    
 







  
