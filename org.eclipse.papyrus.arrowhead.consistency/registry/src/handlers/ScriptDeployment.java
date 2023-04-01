package handlers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.window.Window;

import deployment.CodgenUtil;
import deployment.ExecutionUtils;
import deployment.ProjectSelectWindow;
import deployment.TypeSafeProperties;
import dto.APXSystemDesignDescription;

public class ScriptDeployment {

	private static TypeSafeProperties configuration = CodgenUtil.getProp("WorkSpaceConfiguration");
	private String workspace = configuration.getProperty("workspace");

	@Execute
	public void execute(Shell shell) throws Exception {
		IProject[] projects= ExecutionUtils.readWorkspace(); // Read projects from workspace
		Shell projectShell = null;
		ProjectSelectWindow projectWindow = new ProjectSelectWindow(projectShell);
		projectWindow.setProjects(projects);

		IPath projectLocation = null;
		String modelPath = "";

		if(projectWindow.open() == Window.OK) { // Select a project
			projectLocation = projectWindow.getSelectedProject().getLocation();
			String[] projectFiles = ExecutionUtils.readWorkspace(projectLocation.toString(), false);
			
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

		
		
	}
	
}
