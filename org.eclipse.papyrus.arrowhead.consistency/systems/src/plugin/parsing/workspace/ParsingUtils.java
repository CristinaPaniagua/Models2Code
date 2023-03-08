package plugin.parsing.workspace;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

import plugin.pojo.LocalCloudDesignDescription;

/**
 * This class offers a set of utils for the reading of files and folders.
 * 
 * @author fernand0labra
 *
 */
public class ParsingUtils {
	
	/**
	 * Reads the workspace defined by the path and returns the folders or files
	 * in the given directory
	 * 
	 * @param path The path to the directory
	 * @param directoryCheck Check the folders when True
	 * @return A list of files or folders in the given directory
	 */
	public static String[] readWorkspace(String path, boolean directoryCheck)  {
		File file = new File(path);
		return directoryCheck
		
			? file.list(new FilenameFilter() { // Return a list of directories if directoryCheck
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			})
					
			: file.list(); // Return a list of files if !directoryCheck
	}
}
