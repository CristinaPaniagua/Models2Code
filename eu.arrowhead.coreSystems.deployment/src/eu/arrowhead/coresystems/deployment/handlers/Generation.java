package eu.arrowhead.coresystems.deployment.handlers;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.swt.widgets.Shell;

public class Generation extends AbstractHandler {
	protected Shell shell;
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
			
		//ModelParser.readmodel();
		
		ScriptDeployment window = new ScriptDeployment();

			window.execute(shell);
	
		return null;
	}
}
