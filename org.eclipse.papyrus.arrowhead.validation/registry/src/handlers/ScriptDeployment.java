package handlers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.eclipse.jface.window.Window;

import dialog.ProjectSelectWindow;
import dto.APXDeployedEntity;
import dto.APXLocalCloudDesignDescription;
import dto.APXSystemDesignDescription;
import parsing.workspace.ParsingUtils;
import utils.CodgenUtil;
import utils.ExecutionUtils;

public class ScriptDeployment {

	private static Properties configuration = CodgenUtil.getProperties("WorkSpaceConfiguration");
	private String workspace = configuration.getProperty("workspace");

	@Execute
	public void execute(Shell shell) throws Exception {
		IProject[] projects= ParsingUtils.readWorkspace(); // Read projects from workspace
		Shell projectShell = null;
		ProjectSelectWindow projectWindow = new ProjectSelectWindow(projectShell);
		projectWindow.setProjects(projects);

		IPath projectLocation = null;
		String modelPath = "";

		if(projectWindow.open() == Window.OK) { // Select a project
			projectLocation = projectWindow.getSelectedProject().getLocation();
			String[] projectFiles = ParsingUtils.readWorkspace(projectLocation.toString(), false);
			
			for(String file : projectFiles)
				if(file.endsWith(".uml"))
					modelPath = projectLocation.toString() + "/" + file;

			if(modelPath.equals("")) // If there is no .uml file
				throw new Exception("The selected project does not have an .uml file.");
			
		}
		
		parsing.model.ParsingSetup.parseModel(modelPath);
		System.out.println(parsing.model.ParsingSetup.modelSystemDescriptionMap);
		
		ArrayList<String> databaseSystems = new ArrayList<String>();
		
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/arrowhead", "arrowhead", "");
			Statement stmt  = conn.createStatement();
			ResultSet result = stmt.executeQuery("SELECT * FROM arrowhead.system_;");

			while(result.next())
				databaseSystems.add(result.getString("system_name"));
				
		} catch(Exception e) {
			MessageBox messageBox = new MessageBox(new Shell(), SWT.ERROR);
			messageBox.setMessage(e.getMessage());
			messageBox.open();
		}
		
		ArrayList<String> localClouds = new ArrayList<String>();
		ArrayList<String> nonSavedSystems = new ArrayList<String>();
		HashMap<String, String> deployedEntityID = new HashMap<String, String>();
		
		for(APXLocalCloudDesignDescription localCloud : parsing.model.ParsingSetup.modelLocalCloudList) {
			localClouds.add(localCloud.getName());
			for(APXDeployedEntity deployedEntity : localCloud.getDeployedEntities().values()) {
				String deployedEntityKebab = ParsingUtils.toKebabCase(deployedEntity.getName());
				
				int index = 0;
				while(index < databaseSystems.size() && !databaseSystems.get(index).contains(deployedEntityKebab))
					index ++;
				
				if(index == databaseSystems.size())
					nonSavedSystems.add(deployedEntity.getName());
			}
		}

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		documentBuilderFactory.setNamespaceAware(true);
		
		Document model = builder.parse(new InputSource(modelPath));
		Node child = model.getDocumentElement().getFirstChild().getNextSibling();
		child = child.getFirstChild();
		
		while(child.getNextSibling() != null) {
			child = child.getNextSibling();
			
			if(child.getNodeName().equals("#text")) // Skip text nodes
				continue;
			
			Node name = child.getAttributes().getNamedItem("name");
			if(name != null)
				// If we have found the local cloud tag
				if(localClouds.contains(name.getNodeValue())) {
					child = child.getFirstChild(); // Update the node
					continue;
				}
			
			Node umlProperty = child.getAttributes().getNamedItem("xmi:type");
			if(umlProperty != null)
				// If we have found the uml:Property tag of the local cloud
				if(umlProperty.getNodeValue().equals("uml:Property"))
					deployedEntityID.put( // Save the system identifier
							child.getAttributes().getNamedItem("name").getNodeValue(), 
							child.getAttributes().getNamedItem("xmi:id").getNodeValue());
		}
		
		
		// ############################################################################################################
		
		
		String notationPath = modelPath.split(".uml")[0] + ".notation";
		Document notation = builder.parse(new InputSource(notationPath));
		
		child = notation.getDocumentElement().getFirstChild();
		ArrayList<Node> compositeStructures = new ArrayList<Node>(); // Local Clouds
		
		while(child.getNextSibling() != null) {
			child = child.getNextSibling();
			
			if(child.getNodeName().equals("#text")) // Skip text nodes
				continue;
			
			if(child.getAttributes().getNamedItem("type") != null)
				if(child.getAttributes().getNamedItem("type").getNodeValue().equals("CompositeStructure"))
					compositeStructures.add(child);
		}

		HashMap<String, Node> classShapeID = new HashMap<String, Node>();
		
		for(Node compositeStructure : compositeStructures) { // For each local cloud
			child = compositeStructure.getFirstChild().getNextSibling();
			child = child.getFirstChild();
			
			Node propertyNode = null;
			
			while(child.getNextSibling() != null) {
				child = child.getNextSibling();
				
				if(child.getNodeName().equals("#text"))
					continue;
				
				if(child.getAttributes().getNamedItem("type") != null) {
					String type = child.getAttributes().getNamedItem("type").getNodeValue();
					
					if(type.equals("Class_StructureCompartment"))
						child = child.getFirstChild();
					
					else if(type.equals("Property_Shape")) {
						propertyNode = child;
						child = child.getFirstChild();
					}
				}
				
				else if(child.getAttributes().getNamedItem("xmi:type") != null && child.getAttributes().getNamedItem("href") != null) {
					String xmiType = child.getAttributes().getNamedItem("xmi:type").getNodeValue();
					String[] href = child.getAttributes().getNamedItem("href").getNodeValue().split("#");
				
					if(xmiType.equals("uml:Property") && localClouds.contains(href[0].split(".uml")[0])) {
						classShapeID.put(href[1], propertyNode);
						child = child.getParentNode().getNextSibling();
					}
				}
			}	
		}

		for(String deployedEntity : deployedEntityID.keySet())
			((Element)classShapeID.get(deployedEntityID.get(deployedEntity))).setAttribute("fillColor", // "13420443"); // Default
					nonSavedSystems.contains(deployedEntity) ? "10265827" : "10011046");
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File(notationPath));
		Source input = new DOMSource(notation);
		
		transformer.transform(input, output);
	}
	
}
