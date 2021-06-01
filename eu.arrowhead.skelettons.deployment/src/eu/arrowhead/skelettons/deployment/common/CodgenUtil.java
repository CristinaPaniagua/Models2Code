/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.
 */

package eu.arrowhead.skelettons.deployment.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeVariableName;

import eu.arrowhead.skelettons.deployment.generator.GenerationException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.Set;
import javax.net.ssl.HostnameVerifier;
import javax.ws.rs.core.UriBuilder;

//Contains static utility methods for the project, most important one is the sendRequest method!
public final class CodgenUtil {

 

 
  private static final HostnameVerifier allHostsValid = (hostname, session) -> {
    // Decide whether to allow the connection...
    return true;
  };


  private CodgenUtil() throws AssertionError {
    throw new AssertionError("Arrowhead Common:Utility is a non-instantiable class");
  }




 

  public static String getUri(String address, int port, String serviceUri, boolean isSecure, boolean serverStart) {
    if (address == null) {
      throw new NullPointerException("Address can not be null (Utility:getUri throws NPE)");
    }

    UriBuilder ub = UriBuilder.fromPath("").host(address);
    if (isSecure) {
      ub.scheme("https");
    } else {
      ub.scheme("http");
    }
    if (port > 0) {
      ub.port(port);
    }
    if (serviceUri != null) {
      ub.path(serviceUri);
    }

    String url = ub.toString();
    try {
      new URI(url);
    } catch (URISyntaxException e) {
      if (serverStart) {
        throw new ServiceConfigurationError(url + " is not a valid URL to start a HTTP server! Please fix the address field in the properties file.");
      } else {
    	  System.out.println(url + " is not a valid URL!");
      }
    }

    return url;
  }

  public static String stripEndSlash(String uri) {
    if (uri != null && uri.endsWith("/")) {
      return uri.substring(0, uri.length() - 1);
    }
    return uri;
  }


  

  public static String loadJsonFromFile(String pathName) {
    StringBuilder sb;
    try {
      File file = new File(pathName);
      FileInputStream is = new FileInputStream(file);

      BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
      sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line).append("\n");
      }
      br.close();
    } catch (IOException e) {
      throw new RuntimeException(e.getClass().toString() + ": " + e.getMessage(), e);
    }

    if (!sb.toString().isEmpty()) {
      return sb.toString();
    } else {
      return null;
    }
  }

  public static TypeSafeProperties getProp(String fileName) {
    TypeSafeProperties prop = new TypeSafeProperties();
    try {
      File file = new File("config" + File.separator + fileName);
      FileInputStream inputStream = new FileInputStream(file);
      prop.load(inputStream);
    } catch (FileNotFoundException ex) {
      throw new ServiceConfigurationError(fileName + " file not found, make sure you have the correct working directory set! (directory where the config folder can be found)", ex);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    return prop;
  }

  public static void checkProperties(Set<String> propertyNames, List<String> mandatoryProperties) {
    if (mandatoryProperties == null || mandatoryProperties.isEmpty()) {
      return;
    }
    //Arrays.asList() returns immutable lists, so we have to copy it first
    List<String> properties = new ArrayList<>(mandatoryProperties);
    if (!propertyNames.containsAll(mandatoryProperties)) {
      properties.removeIf(propertyNames::contains);
      throw new ServiceConfigurationError("Missing field(s) from app.properties file: " + properties.toString());
    }
  }

  
    public static  Type getType(String type){
         Type t;
         
         if(type.equalsIgnoreCase("String")) t=String.class;
         else if (type.equalsIgnoreCase("Boolean")) t=Boolean.class;
         else if (type.equalsIgnoreCase("Integer")) t=int.class;
         else if (type.equalsIgnoreCase("Byte")) t=Byte.class;
         else if (type.equalsIgnoreCase("Double")) t=double.class;
         else if (type.equalsIgnoreCase("Float")) t=float.class;
         else if (type.equalsIgnoreCase("Short")) t=short.class;
         else if (type.equalsIgnoreCase("Long")) t=long.class;
          else if (type.equalsIgnoreCase("Single")) t=Object.class;
         else if (type.startsWith("List")){
             //ParameterizedTypeName ListType =ParameterizedTypeName.get(List.class, Object.class);
             t=List.class;
         }
         
        
         
         //TODO: ADD MORE TYPES
        
         else t=Object.class;
         return t;
     }
     
    public static TypeName getTypeCom(String name, String type){
         TypeName t;
         String Name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length()); 
         
         if (type.equalsIgnoreCase("List"))   t = ParameterizedTypeName.get(ClassName.get(List.class),TypeVariableName.get(name) );
         else if (type.equalsIgnoreCase("single"))  t =TypeVariableName.get(Name); 
         else  t =TypeVariableName.get(Name);
         
         //TODO: ADD MORE COMPLEX TYPES
             return t;
             }
    
    
   public static void readList (ArrayList<String[]> elements){
        
        for (int i = 0; i < elements.size(); i++){ 
            String[] ele=elements.get(i);
            for (int j = 0; j < ele.length; j++){
                System.out.println(i+"."+j+" :"+elements.get(i)[j]);
            }
            
        }
    }
  
}
