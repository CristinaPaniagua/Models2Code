package handlers;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
		
		ArrayList<String> nonSavedSystems = new ArrayList<String>();
		for(APXSystemDesignDescription system : parsing.model.ParsingSetup.modelSystemDescriptionMap.values())
			if(databaseSystems.contains(system.getName()))
				nonSavedSystems.add(system.getName());

		String documentPath = modelPath.split(".uml")[0] + ".notation";
		
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
		documentBuilderFactory.setNamespaceAware(true);
		
		Document document = builder.parse(new InputSource(documentPath));
		
		Node child = document.getDocumentElement().getFirstChild();
		Node papyrusClassDiagram = null;
		ArrayList<Node> compositeStructures = new ArrayList<Node>(); // Local Clouds
		
		while(child.getNextSibling() != null) {
			child = child.getNextSibling();
			if(child.getNodeName().equals("#text"))
				continue;
			
			if(child.getAttributes().getNamedItem("type") != null) {
				String type = child.getAttributes().getNamedItem("type").getNodeValue();
				if(type.equals("PapyrusUMLClassDiagram"))
					papyrusClassDiagram = child.cloneNode(true);
				
				if(type.equals("CompositeStructure"))
					compositeStructures.add(child.cloneNode(true));
			}
		}
		
		child = papyrusClassDiagram.getFirstChild();
		
		while(child.getNextSibling() != null) {
			child = child.getNextSibling();
			if(child.getNodeName().equals("#text"))
				continue;
			
			if(child.getAttributes().getNamedItem("type") != null) 
				if(child.getAttributes().getNamedItem("type").getNodeValue().equals("Class_Shape"))
					((Element) child).setAttribute("fillColor", "13420443");
		}
		
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		Result output = new StreamResult(new File("C:\\Users\\usuario\\Documents\\ltu\\2022-support-software-engineer\\sysml-plugin-development\\workspace\\arrowhead-papyrus-plugin\\example.notation"));
		Source input = new DOMSource(document);
		
		transformer.transform(input, output);
				
		System.exit(0);
	}
	
}
