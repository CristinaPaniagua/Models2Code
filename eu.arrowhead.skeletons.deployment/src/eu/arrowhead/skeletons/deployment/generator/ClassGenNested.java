/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.skeletons.deployment.generator;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import eu.arrowhead.skeletons.deployment.common.CodgenUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.lang.model.element.Modifier;

/**
 *
 * @author cripan
 */


public class ClassGenNested {
    
	
	
private String Directory="";
private String system="";
private String foldername="";

   public  ArrayList<String> classesDummy= new ArrayList<String>();

    public ClassGenNested() {
    }

    public ArrayList<String> getClassesDummy() {
        return classesDummy;
    }

    public void setClassesDummy(ArrayList<String> classesDummy) {
        this.classesDummy = classesDummy;
    }
   
   
    
    public ArrayList<String> complexelement (ArrayList<String[]> elements, String parentClass){
        boolean boo=false;
      // System.out.println("\n\nARRAYLIST SIZE:"+elements.size());
       for (int i = 0; i < elements.size(); i++){ 
        String e =elements.get(i)[0];
        //System.out.println("complexElement"+i+" 1 " + elements.get(i)[0]);
        //System.out.println("complexElement"+i+" 2 " +elements.get(i)[1]);
        
        if(e!=null){
         if(e.equals("Newclass")){
             if(("StopClass".equals(elements.get(i+1)[0]))){
                 i=elements.size()+1;
             }else{
               i=genClomplex (elements,i,parentClass);  
             }
             boo=true;
            
         }
        } 
     
         }
        return classesDummy;
              
    
    }
    
    
    public  int genClomplex (ArrayList<String[]> elements, int i, String parentClass){
        //ArrayList<String[]> var= new ArrayList<String[]>();
        ArrayList<String[]> newclass = new ArrayList<String[]>();
        boolean out=false;
              int j=i+1;
              
            String className=elements.get(i)[1];  
            //System.out.println("class name "+className);
           //while(!((className.equals(elements.get(j)[0]))&& ("StopClass".equals(elements.get(j+1)[0])))){ 
          // while((!(className.equals(elements.get(j)[1])))&& out==false){
          while(out==false){
              if("StopClass".equals(elements.get(j)[0])){
                  out=true;
                  }else{
                  
            
                  
               //System.out.println(elements.get(j)[0]);
              if("child:Newclass".equals(elements.get(j)[0])){
                  int current_j=genClomplex (elements,j,parentClass);
                  //System.out.println("Clase creada vuelvo "+current_j);
                  String[] ele=new String[2];
                  if("single".equals(elements.get(j)[2])){
                  
                  ele[0]=elements.get(j)[1];
                  ele[1]=elements.get(j)[1];
                 // System.out.println("ele0:" +ele[0]);
                 // System.out.println("ele1:" +ele[1]); 
                  
                  newclass.add(ele);
                  
                  }else if ("list".equals(elements.get(j)[2])){
                  
                  ele[0]=elements.get(j)[1];
                  ele[1]="List";
                  //System.out.println("ele0:" +ele[0]);
                  //System.out.println("ele1:" +ele[1]); 
                  newclass.add(ele);
                  
                  }
                  
                  
                  
                  if("StopClass".equals(elements.get(current_j)[0])){
                        out=true;
                   }else{
                        j++;
                  }
                  
              }else{
                  
                  String value=elements.get(j)[0];
                 while("child".equals(value)){
                   //System.out.println(elements.get(j)[1]);
                   //System.out.println(elements.get(j)[2]);
                  
                  String[] ele=new String[2];
                  ele[0]=elements.get(j)[1];
                  ele[1]=elements.get(j)[2];
                  //System.out.println("ele0:" +ele[0]);
                 // System.out.println("ele1:" +ele[1]); 
                  newclass.add(ele);
                  // System.out.println(j);
               
                        j++;
                  
                 value=elements.get(j)[0];
                // System.out.println("new value: "+value);
              }
                
              }
                }
           
        }   
        
        ClassGenSimple c=new ClassGenSimple();
        String CS =c.dummyobject(className,newclass);
        classesDummy.add(CS); 
       // System.out.println("ADDTION TO CS:"+CS);
        //System.out.println(newclass.get(0)[0]+" , "+newclass.get(1)[0]);
        classGen(newclass,className,parentClass);
        return j;
    }
    

    
        
    public  MethodSpec  fullConstructor ( ArrayList<String[]> elements,String className){
        
        
     MethodSpec.Builder BFullConsructor = MethodSpec.constructorBuilder()
     .addModifiers(Modifier.PUBLIC);
   
      for (int i = 0; i < elements.size(); i++){ 
        String name=elements.get(i)[0];
        String type=elements.get(i)[1];
        
        
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
        
     
       
     }
       MethodSpec FullConsructor = BFullConsructor.build();
      return FullConsructor;
    }
    

    
   /* 
    public  MethodSpec  toString (ArrayList<String[]> elements){
        
          String S="";
     for (int i = 0; i < elements.size(); i++){ 
        String name=elements.get(i)[0];
        S=S+"+ \""+name+"=\" + "+name+"+ \", \"";
     }
        
     MethodSpec toString  = MethodSpec.methodBuilder("toString")
    .addModifiers(Modifier.PUBLIC)
     .addAnnotation(Override.class)
     .returns(String.class)
     .addStatement("return \"{\" $L +\"}\"",S)
     .build();
    
     
      
     return toString;
    }
    */

    
    
    public  void classGen ( ArrayList<String[]> elements , String className, String parentClass){
         
     ClassGenSimple cg=new ClassGenSimple();   
     String ClassName =className.substring(0, 1).toUpperCase() + className.substring(1,className.length()); 
     
        MethodSpec constructor= cg.constructor(ClassName);
        MethodSpec fullConstructor=fullConstructor(elements,ClassName);
        MethodSpec toString=cg.toString(elements);
      
     TypeSpec.Builder BclassGen =TypeSpec.classBuilder(ClassName)
              .addModifiers(Modifier.PUBLIC)
             .addMethod(constructor)
             .addMethod(fullConstructor)
             .addMethod(toString); 
               
      
      
      
 for (int i = 0; i < elements.size(); i++){ 
        String name=elements.get(i)[0];
        String type=elements.get(i)[1];
        MethodSpec methodget= cg.get(name,type);
        MethodSpec methodset= cg.set(name,type);
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
          
       
        
    }   
     
         
   
        TypeSpec classGen  = BclassGen.build();
        
     
             
               
 
        String packageName="eu.arrowhead."+system;    
        
        JavaFile javaFile2 = JavaFile.builder(packageName,classGen)
                .addFileComment("Auto generated")
                .build();
        try{
        System.out.println(Directory+"/"+foldername+"_ApplicationSystems/"+system+"/src/main/java/eu/arrowhead/"+system);
            javaFile2.writeTo(Paths.get(Directory+"/"+foldername+"_ApplicationSystems/"+system+"/src/main/java/"));
        }catch (IOException ex){
           System.out.print("Exception:" + ex.getMessage());
        }
        
     }

	public String getDirectory() {
		return Directory;
	}

	public void setDirectory(String directory) {
		Directory = directory;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getFoldername() {
		return foldername;
	}

	public void setFoldername(String foldername) {
		this.foldername = foldername;
	}
     

        
    
}
