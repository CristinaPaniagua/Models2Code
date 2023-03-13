package deployment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

import com.google.common.base.CaseFormat;

/**
 * 
 * Utils for the execution of scripts
 * 
 * @author cripan
 *
 */
public class ExecutionUtils {

	// =================================================================================================
	// methods
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Executes a .bat windows file
	 * 
	 * @param directory The path to the file
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void executebat(String directory) throws InterruptedException, IOException {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		ProcessBuilder processBuilder = new ProcessBuilder();
		System.out.println("Script generated"); // TODO Remove Trace
		processBuilder.command(directory);

		try {
			Process process = processBuilder.start();
			System.out.println("Script executed"); // TODO Remove Trace
			executor.submit(new ProcessTask(process.getInputStream()));
		} finally {
			executor.shutdown();
		}

	}

	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Executes a .sh linux file
	 * 
	 * @param directory The path to the file
	 * @param file The name of the file
	 * @throws InterruptedException
	 * @throws IOException
	 */
	public static void executesh(String directory, String file) throws InterruptedException, IOException {

		ExecutorService executor = Executors.newSingleThreadExecutor();
		ProcessBuilder processBuilder = new ProcessBuilder();
		System.out.println("Script generated"); // TODO Remove Trace
		processBuilder.command("sh", "-c", "sh ./" + file + ".sh");
		processBuilder.directory(new File(directory + "\\src\\scripts\\"));

		try {
			Process process = processBuilder.start();
			System.out.println("Script executed"); // TODO Remove Trace
			executor.submit(new ProcessTask(process.getInputStream()));
		} finally {
			executor.shutdown();
		}

	}
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Creates a new folder in the workspace
	 * 
	 * @param Directory The path to the directory
	 * @param FolderName The name of the folder
	 */
	public static void newFolder(String Directory, String FolderName) {
		String path = Directory + "/" + FolderName;
		File f1 = new File(path); // Instantiate the File class
		boolean bool = f1.mkdir(); // Creating a folder using mkdir() method
		System.out.println(bool ? "Folder is created successfully" : "Error Found!"); // TODO Remove Trace
	}
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Read workspace projects
	 * 
	 * @return List of the workspace projects
	 */
	public static IProject[] readWorkspace() {
		// Get the root of the workspace
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		System.out.println("WORKSPACE:"); // TODO Remove Trace
		
		IWorkspaceRoot root = workspace.getRoot();
		System.out.println(workspace.toString()); // TODO Remove Trace
		System.out.println(root.toString()); // TODO Remove Trace
		
		// Get all projects in the workspace
		IProject[] projects = root.getProjects();
		
		// Loop over all projects
		IPath path = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String workspacePath = path.toString();
		System.out.println("Projects in the workspace:" + workspacePath); // TODO Remove Trace
		for (IProject project : projects) { // TODO Remove Trace
			System.out.println(project.getName());
			System.out.println(project.getLocation());
		}

		return projects;
	}
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Transforms a string from UpperCamel to kebab-case
	 * 
	 * @param str The string to transforme
	 * @return The transformed string
	 */
	public static String toKebabCase(String str) {
		return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
	}
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Transforms a string from kebab-case to snake_case
	 * 
	 * @param str The string to transforme
	 * @return The transformed string
	 */
	public static String toSnakeCase(String str) {
		return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_UNDERSCORE, str);
	}
	
	// =================================================================================================
	// auxiliary classes
	
	// -------------------------------------------------------------------------------------------------
	/**
	 * 
	 * Thread process task for the parallel execution of scripts
	 * 
	 * @author cripan
	 *
	 */
	private static class ProcessTask implements Callable<List<String>> {

		// =================================================================================================
		// attributes
		
		private InputStream inputStream;

		
		// =================================================================================================
		// methods

		// -------------------------------------------------------------------------------------------------
		@Override
		public List<String> call() {
			return new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.toList());
		}
		
		
		// =================================================================================================
		// auxiliary methods
		
		// -------------------------------------------------------------------------------------------------
		public ProcessTask(InputStream inputStream) { this.inputStream = inputStream; }
	}
	
}
