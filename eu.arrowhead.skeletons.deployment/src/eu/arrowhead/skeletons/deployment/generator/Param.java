/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.skeletons.deployment.generator;

/**
 *
 * @author cripan
 */
public class Param {
    
    
    String name; 
    String type; 
    String style; 
    String required; 

    public Param() {
    }

    
    
    
    public Param(String name, String type, String style, String required) {
        this.name = name;
        this.type = type;
        this.style = style;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getRequired() {
        return required;
    }

    public void setRequired(String required) {
        this.required = required;
    }

    

    @Override
    public String toString() {
        return "Param{" + "name=" + name + ", type=" + type + ", style=" + style + ", required=" + required + '}';
    }
    
    
    
    
}
