package handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.Shell;

import deployment.CodgenUtil;
import deployment.TypeSafeProperties;

public class ScriptDeployment {

	
	private static TypeSafeProperties configuration = CodgenUtil.getProp("WorkSpaceConfiguration");
	private String workspace = configuration.getProperty("workspace");

	@Execute
	public void execute(Shell shell) throws Exception {
		
	}
}
