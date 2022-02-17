/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.skeletons.deployment.generator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import eu.arrowhead.skeletons.deployment.common.CodgenUtil;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;
import java.util.List;
import javax.ws.rs.core.MediaType;
/**
 *
 * @author cripan
 */
public class ClassGenSimple {

 
	private String directory="";
	private String system="";
	private String foldername="";
	
    ArrayList<String> ListofDeclarations=new ArrayList<>();
    
    public ClassGenSimple() {
    }
    
    public ClassGenSimple(ArrayList<String> ListofDeclarations) {
        this.ListofDeclarations=ListofDeclarations;
    }

    public ArrayList<String> getListofDeclarations() {
        return ListofDeclarations;
    }

    public void setListofDeclarations(ArrayList<String> ListofDeclarations) {
        this.ListofDeclarations = ListofDeclarations;
    }
    
    
    
    
    
    public  MethodSpec  constructor (String name){
        
     MethodSpec consructor = MethodSpec.constructorBuilder()
     .addModifiers(Modifier.PUBLIC)
    .build();
     
    
     return consructor;
    }
    
        
    public MethodSpec  fullConstructor ( ArrayList<String[]> elements,String className){
       
        //readList(elements);
        ClassGenNested  cc = new ClassGenNested();
        cc.setDirectory(directory);
        cc.setFoldername(foldername);
        cc.setSystem(system);
        ListofDeclarations=cc.complexelement(elements,className);
        //readList(elements);
      ArrayList<String[]> var= new ArrayList<>();
       
      String[] ele2= new String[2];  
     MethodSpec.Builder BFullConsructor = MethodSpec.constructorBuilder()
     .addModifiers(Modifier.PUBLIC);
   
      for (int i = 0; i < elements.size(); i++){ 
        String name=elements.get(i)[0];
        String type=elements.get(i)[1];
        
        //System.out.println("fullConstructor"+i+" "+name+" "+type );

        
            if(name.equals("Newclass")){
                    name=elements.get(i)[1];
                    type=elements.get(i)[2];
                      ele2[0]=name;
                      ele2[1]=type;
                     var.add(ele2);
                    
         if(type.equalsIgnoreCase("single")||type.startsWith("List")){

             
             
               TypeName t= CodgenUtil.getTypeCom(name, type);
                 BFullConsructor
                    .addParameter(t,name)
                    .addStatement("this.$N = $N", name, name);
            }
            else{  
    
                    BFullConsructor
                    .addParameter(CodgenUtil.getType(type),name)
                    .addStatement("this.$N = $N", name, name);
                    
              }
                }else if(name.equals("child")||name.equals("child:Newclass")|| name.equals("StopClass") ){
           //NOTHING :D
                }else{
                String[] ele= new String[2]; 
                ele[0]=name;
                ele[1]=type;
                //System.out.println(i+" "+ele[0]);
                var.add(ele); 
                //System.out.println(i+" "+ele[0]);
                   BFullConsructor
                      .addParameter(CodgenUtil.getType(type),name)
                     .addStatement("this.$N = $N", name, name);
                     
        }
            

     }
      
        //System.out.println(var.size());
        String CS =dummyobject(className,var);
        ListofDeclarations.add(CS);
        
       
        
        
        
       MethodSpec FullConsructor = BFullConsructor.build();
      return FullConsructor;
    }
    
    public  MethodSpec  get (String name, String type){
        
     MethodSpec.Builder get = MethodSpec.methodBuilder("get"+name)
     .addModifiers(Modifier.PUBLIC);
     
      if(type.equalsIgnoreCase("single")||type.startsWith("List")){
               
                get.returns(CodgenUtil.getTypeCom(name, type))
                .addStatement("return "+name);
                
      }else {
                get.returns(CodgenUtil.getType(type))
                .addStatement("return "+name);
                
      }

     
     
      
     return get.build();
    }
    
    
    public  MethodSpec  toString (ArrayList<String[]> elements){
        
          String S="";
          
     for (int i = 0; i < elements.size(); i++){ 
         String name=elements.get(i)[0];
         if(name.equals("Newclass")){
             name=elements.get(i)[1];
             S=S+"+ \""+name+"=\" + "+name;
          }else if(name.equals("child")||name.equals("child:Newclass")|| name.equals("StopClass") ){
           //NOTHING :D
         }else{
        
        S=S+"+ \""+name+"=\" + "+name+"+ \",  \"";
     }
     }
        
     MethodSpec toString  = MethodSpec.methodBuilder("toString")
    .addModifiers(Modifier.PUBLIC)
     .addAnnotation(Override.class)
     .returns(String.class)
     .addStatement("return \"ProviderPayload{\" $L +\"}\"",S)
     .build();
    
     
      
     return toString;
    }
    
    
    public MethodSpec  set (String name, String type){
        
     MethodSpec.Builder set  = MethodSpec.methodBuilder("set"+name)
    .addModifiers(Modifier.PUBLIC);
     
      if(type.equalsIgnoreCase("single")||type.startsWith("List")){
               
                set.addParameter(CodgenUtil.getTypeCom(name, type),name)
                .addStatement("this."+name+"="+name);
                
      }else {
     set.addParameter(CodgenUtil.getType(type),name)
     .addStatement("this."+name+"="+name);
      }
     
     
      
     return set.build();
    }
    
    
    
     public  ArrayList<String> classGen ( ArrayList<String[]> elements , String className,String Directory, String Foldername, String Sys){
         
         directory=Directory;
         foldername=Foldername;
         system=Sys;
         //ListDeclarations.clear();
        
      String ClassName =className.substring(0, 1).toUpperCase() + className.substring(1,className.length()); 
        
        
         
        MethodSpec constructor= constructor(ClassName);
        MethodSpec fullConstructor=fullConstructor(elements,ClassName);
        MethodSpec toString=toString(elements);
        
        
         AnnotationSpec Jackson= AnnotationSpec
                 .builder(JsonIgnoreProperties.class)
                 .addMember("ignoreUnknown", "true")
                 .build();
      
     TypeSpec.Builder BclassGen =TypeSpec.classBuilder(ClassName)
              .addModifiers(Modifier.PUBLIC)
             .addAnnotation(Jackson)
             .addMethod(constructor)
             .addMethod(fullConstructor)
             .addMethod(toString); 
               
      
      
      
    for (int i = 0; i < elements.size(); i++){ 
        String name=elements.get(i)[0];
        String type=elements.get(i)[1];
        //System.out.println(type);
        
        if(name.equals("Newclass")){
            name=elements.get(i)[1];
            type=elements.get(i)[2];
            
            //ele[0]=name;
            //ele[1]=type;
            //var.add(ele);
            MethodSpec methodget= get(name,type);
            MethodSpec methodset= set(name,type);
             //System.out.println("type " +type+" name "+name);
            if(type.equalsIgnoreCase("single")||type.startsWith("List")){

               TypeName t= CodgenUtil.getTypeCom(name, type);
                  BclassGen.addField(t, name, Modifier.PRIVATE)
                 .addMethod(methodget)
                 .addMethod(methodset);
                  
                  
            }
            else{  
            
             Type T=CodgenUtil.getType(type);
            BclassGen.addField(T,name, Modifier.PRIVATE)
                 .addMethod(methodget)
                 .addMethod(methodset);
            }
          
        }else if(name.equals("child")||name.equals("child:Newclass")|| name.equals("StopClass") ){
           //NOTHING :D
        }else{
           
        MethodSpec methodget= get(name,type);
        MethodSpec methodset= set(name,type);
        Type T=CodgenUtil.getType(type);
        BclassGen.addField(T, name, Modifier.PRIVATE)
                 .addMethod(methodget)
                 .addMethod(methodset);
        
        }
        
       
    }   
     
         
   
        TypeSpec classGen  = BclassGen.build();

          String packageName="eu.arrowhead."+system;    
 
        JavaFile javaFile2 = JavaFile.builder(packageName,classGen)
                .addFileComment("Auto generated")
                .build();
        try{
        System.out.println(directory+"/"+foldername+"_ApplicationSystems/"+system+"/src/main/java/eu/arrowhead/"+system);
            javaFile2.writeTo(Paths.get(directory+"/"+foldername+"_ApplicationSystems/"+system+"/src/main/java/"));
        }catch (IOException ex){
           System.out.print("Exception:" + ex.getMessage());
        }
        
        //if(className.equals("RequestDTO")) return Declaration_Request;
        //else return Declaration_Response;
        
        return ListofDeclarations;
        
       
     }
     
     public  String dummyobject (String name, ArrayList<String[]> var ){
        
         String s=null;
         s=""+name+" OBJ"+name+" = new "+name+"( ";
         int a=0;
         boolean ListFlag=false;
         //System.out.println(var.size());
         if(var.size()>1){
         for (int i=0;i<var.size();i++){
          
             if(var.get(i)[1].equalsIgnoreCase("String")){
                s=s+" \""+var.get(i)[0]+"\""; 
             }else if(var.get(i)[1].equalsIgnoreCase("Double")||var.get(i)[1].equalsIgnoreCase("Float")){
                  s=s+""+0.0+"";
             }else if(var.get(i)[1].equalsIgnoreCase("Integer")||var.get(i)[1].equalsIgnoreCase("Short")||var.get(i)[1].equalsIgnoreCase("Long")){
                 s=s+""+0+"";
             }else if(var.get(i)[1].equalsIgnoreCase("Boolean")){
                      s=s+""+true+"";
             }else if(var.get(i)[1].startsWith("List")){
                 s=s+"ListObject";
                 ListFlag=true;
                 a=i;
             }else 
                 
                     s=s+"OBJ"+var.get(i)[0]+"";
                     
            if((i+1)<var.size()) s=s+" , ";
         }
         
         }
         s=s+")";
         
         if(ListFlag){
             s="List<"+var.get(a)[0]+"> ListObject=new ArrayList<>(); \n ListObject.add(OBJ"+var.get(a)[0]+"); \n"+s;
    
         }
        //System.out.println("Storage of the object instance:"+s);
         return s;
        
     }
     
     
     
      
      
 
}
