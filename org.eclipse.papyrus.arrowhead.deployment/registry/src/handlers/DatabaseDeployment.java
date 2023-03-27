package handlers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Pattern;
import org.eclipse.jface.window.Window;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

public class DatabaseDeployment {

	@Execute
	public void execute(Shell shell) throws Exception {
		// Check java/maven/mysql requirements
		String[] versionCheck = { "11#java -version#Java#2", "3.5#mvn -version#Maven#2", "5.7#mysql -V#MySQL#3" };
		for (String requirement : versionCheck) {
			float requiredVersion = Float.parseFloat(requirement.split("#")[0]);
			String command = requirement.split("#")[1];
			String library = requirement.split("#")[2];
			int position = Integer.parseInt(requirement.split("#")[3]);

			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", command);
			builder.redirectErrorStream(true);
			Process p = builder.start();

			BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = r.readLine();
			String[] splittedVersion = line.split(" ")[position].replaceAll("\"", "").split(Pattern.quote("."));
			float installedVersion = Float.parseFloat(splittedVersion[0] + "." + splittedVersion[1]);
			
			if (installedVersion < requiredVersion) { // Get version and compare
				String errorMessage = "The required version of " + library + " is " + String.valueOf(requiredVersion);
				MessageBox messageBox = new MessageBox(shell, SWT.ERROR);
				messageBox.setMessage(errorMessage);
				messageBox.open();

				throw new Exception(errorMessage);
			}
		}

		DialogWindow projWin = new DialogWindow(null);
		if(projWin.open() == Window.OK) {
			
		}

		// Connect and check if database already exists
		
		// If so check users (run sql on privileges for arrowhead database)
		// If no users show dialog for creation of user
		// Else display dialog with options core and/or support systems
		// Obtain selection and execute scripts
	}

}
