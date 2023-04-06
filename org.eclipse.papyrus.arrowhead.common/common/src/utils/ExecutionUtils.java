package utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
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
