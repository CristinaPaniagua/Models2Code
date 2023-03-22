/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package generator;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import deployment.CodgenUtil;
import deployment.ExecutionUtils;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.lang.model.element.Modifier;
import java.lang.reflect.Type;

/**
 *
 * Generate simple class with constructor, getters, setters and toString methods
 *
 * @author cripan
 * 
 */
public class ClassSimple {

	// =================================================================================================
	// attributes

	private String directory = "";
	private String system = "";
	private String foldername = "";

	ArrayList<String> ListofDeclarations = new ArrayList<>();

	
	// =================================================================================================
	// methods

	// -------------------------------------------------------------------------------------------------
	/**
	 * Build simple constructor method
	 * 
	 * @return Constructor object
	 */
	public MethodSpec constructor() {
		MethodSpec consructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).build();
		return consructor;
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * Build full constructor method
	 * 
	 * @param elements List of attributes of the class
	 * @param className Name of the class
	 * @return Constructor object
	 */
	public MethodSpec fullConstructor(ArrayList<String[]> elements, String className) {

		ClassNested cc = new ClassNested();
		
		cc.setDirectory(directory);
		cc.setFoldername(foldername);
		cc.setSystem(system);
		
		ListofDeclarations = cc.complexelement(elements, className);
		ArrayList<String[]> var = new ArrayList<>();
		MethodSpec.Builder BFullConsructor = MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC);

		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			String type = elements.get(i)[1];

			if (name.equals("Newclass")) {
				name = elements.get(i)[1];
				type = elements.get(i)[2];
				var.add(new String[] {name, type});

				if (type.equalsIgnoreCase("single") || type.startsWith("List")) {
					TypeName t = CodgenUtil.getTypeCom(name, type);
					BFullConsructor.addParameter(t, name).addStatement("this.$N = $N", name, name);
				} else
					BFullConsructor.addParameter(CodgenUtil.getType(type), name).addStatement("this.$N = $N", name, name);
				
			} else if (!name.equals("child") && !name.equals("child:Newclass") && !name.equals("StopClass")) {
				var.add(new String[] {name, type});
				BFullConsructor.addParameter(CodgenUtil.getType(type), name).addStatement("this.$N = $N", name, name);
			}
		}

		String CS = dummyobject(className, var);
		ListofDeclarations.add(CS);

		MethodSpec FullConsructor = BFullConsructor.build();
		return FullConsructor;
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * Build get method
	 * 
	 * @param name Name of the attribute
	 * @param type Type of the attribute (single/List)
	 * @return Get method object
	 */
	public MethodSpec get(String name, String type) {

		// Add method definition
		MethodSpec.Builder get = MethodSpec.methodBuilder("get" + name).addModifiers(Modifier.PUBLIC);

		// Add return type
		if (type.equalsIgnoreCase("single") || type.startsWith("List"))
			get.returns(CodgenUtil.getTypeCom(name, type)).addStatement("return " + name);
		else
			get.returns(CodgenUtil.getType(type)).addStatement("return " + name);

		return get.build();
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Build toString operation
	 * 
	 * @param elements Class attributes
	 * @return ToString method object
	 */
	public MethodSpec toString(ArrayList<String[]> elements) {

		String S = "";

		// Build method content
		for (int i = 0; i < elements.size(); i++) {
			String name = elements.get(i)[0];
			if (name.equals("Newclass")) {
				name = elements.get(i)[1];
				S = S + "+ \"" + name + "=\" + " + name;
			} else if (!name.equals("child") && !name.equals("child:Newclass") && !name.equals("StopClass")) {
				S = S + "+ \"" + name + "=\" + " + name + "+ \",  \"";
			}
		}

		// Add annotations and return type
		MethodSpec toString = MethodSpec.methodBuilder("toString").addModifiers(Modifier.PUBLIC).addAnnotation(Override.class)
				.returns(String.class).addStatement("return \"ProviderPayload{\" $L +\"}\"", S).build();

		return toString;
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Build set method
	 * 
	 * @param name Name of the attribute
	 * @param type Type of the attribute
	 * @return Set method object
	 */
	public MethodSpec set(String name, String type) {

		// Add method definition
		MethodSpec.Builder set = MethodSpec.methodBuilder("set" + name).addModifiers(Modifier.PUBLIC);

		// Add assign sentences
		if (type.equalsIgnoreCase("single") || type.startsWith("List"))
			set.addParameter(CodgenUtil.getTypeCom(name, type), name).addStatement("this." + name + "=" + name);
		else
			set.addParameter(CodgenUtil.getType(type), name).addStatement("this." + name + "=" + name);

		return set.build();
	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Generate simple class with constructor, getters, setters and toString methods
	 * 
	 * @param elements List of payload elements
	 * @param className Name of the class
	 * @param Directory Path of the file
	 * @param Foldername Name of the folder
	 * @param Sys Name of the system
	 * @return
	 */
	public ArrayList<String> classGen(ArrayList<String[]> elements, String className, String Directory, String Foldername, String Sys) {

		directory = Directory;
		foldername = Foldername;
		system = Sys;

		String ClassName = className.substring(0, 1).toUpperCase() + className.substring(1, className.length());

		MethodSpec constructor = constructor(); // Generate simple constructor
		MethodSpec fullConstructor = fullConstructor(elements, ClassName); // Generate full constructor
		MethodSpec toString = toString(elements); // Generate toString

		AnnotationSpec Jackson = AnnotationSpec.builder(JsonIgnoreProperties.class).addMember("ignoreUnknown", "true").build();
		
		// Add generated methods
		TypeSpec.Builder BclassGen = TypeSpec.classBuilder(ClassName).addModifiers(Modifier.PUBLIC).addAnnotation(Jackson)
				.addMethod(constructor).addMethod(fullConstructor).addMethod(toString);

		for (int i = 0; i < elements.size(); i++) { // For each of the elements of the payload
			String name = elements.get(i)[0];
			String type = elements.get(i)[1];

			if (name.equals("Newclass")) {
				name = elements.get(i)[1];
				type = elements.get(i)[2];

				MethodSpec methodget = get(name, type); // Generate getter method
				MethodSpec methodset = set(name, type); // Generate setter method
				
				// Add generated methods
				if (type.equalsIgnoreCase("single") || type.startsWith("List")) {
					TypeName t = CodgenUtil.getTypeCom(name, type);
					BclassGen.addField(t, name, Modifier.PRIVATE).addMethod(methodget).addMethod(methodset);
				} else {
					Type T = CodgenUtil.getType(type);
					BclassGen.addField(T, name, Modifier.PRIVATE).addMethod(methodget).addMethod(methodset);
				}

			} else if (!name.equals("child") && !name.equals("child:Newclass") && !name.equals("StopClass")) {
				MethodSpec methodget = get(name, type); // Generate  getter method for attribute
				MethodSpec methodset = set(name, type); // Generate setter method for attribute
				
				// Add generated methods
				Type T = CodgenUtil.getType(type);
				BclassGen.addField(T, name, Modifier.PRIVATE).addMethod(methodget).addMethod(methodset);
			}
		}

		// Create and write the new class
		TypeSpec classGen = BclassGen.build();
		String packageName = "eu.arrowhead." + system.split("-")[system.split("-").length - 1];
		JavaFile javaFile = JavaFile.builder(packageName, classGen).addFileComment("Auto generated").build();
		
		try {
			javaFile.writeTo(Paths.get(directory + "\\arrowhead\\" + foldername + "\\cloud-systems\\" + ExecutionUtils.toKebabCase(system) + "\\src\\main\\java\\"));
		} catch (IOException ex) {
			System.err.print("ERROR:" + ex.getMessage());
		}

		return ListofDeclarations;
	}

	
	// =================================================================================================
	// auxiliary methods

	// -------------------------------------------------------------------------------------------------
	public ClassSimple() { }
	public ClassSimple(ArrayList<String> ListofDeclarations) { this.ListofDeclarations = ListofDeclarations; }
	
	// -------------------------------------------------------------------------------------------------
	public ArrayList<String> getListofDeclarations() { return ListofDeclarations; }
	
	// -------------------------------------------------------------------------------------------------
	public void setListofDeclarations(ArrayList<String> ListofDeclarations) { this.ListofDeclarations = ListofDeclarations; }
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * Generate dummy object definition
	 * 
	 * @param name Name of the object
	 * @param var List of attributes of the object
	 * @return A string with the definition of the object
	 */
	public String dummyobject(String name, ArrayList<String[]> var) {

		String s = "" + name + " OBJ" + name + " = new " + name + "( ";
		boolean ListFlag = false;
		int a = 0;
		
		if (var.size() > 1)
			for (int i = 0; i < var.size(); i++) {

				if (var.get(i)[1].equalsIgnoreCase("String"))
					s = s + " \"" + var.get(i)[0] + "\"";
				else if (var.get(i)[1].equalsIgnoreCase("Double") || var.get(i)[1].equalsIgnoreCase("Float"))
					s = s + "" + 0.0 + "";
				else if (var.get(i)[1].equalsIgnoreCase("Integer") || var.get(i)[1].equalsIgnoreCase("Short") || var.get(i)[1].equalsIgnoreCase("Long"))
					s = s + "" + 0 + "";
				else if (var.get(i)[1].equalsIgnoreCase("Boolean"))
					s = s + "" + true + "";
				else if (var.get(i)[1].startsWith("List")) {
					s = s + "ListObject";
					ListFlag = true;
					a = i;
				}
				else
					s = s + "OBJ" + var.get(i)[0] + "";

				if ((i + 1) < var.size())
					s = s + " , ";
			}
		s = s + ")";

		if (ListFlag)
			s = "List<" + var.get(a)[0] + "> ListObject=new ArrayList<>(); \n ListObject.add(OBJ" + var.get(a)[0] + "); \n" + s;
		
		return s;
	}
	
}
