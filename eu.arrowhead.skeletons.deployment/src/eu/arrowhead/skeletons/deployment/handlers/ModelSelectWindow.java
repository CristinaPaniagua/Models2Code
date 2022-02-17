package eu.arrowhead.skeletons.deployment.handlers;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class ModelSelectWindow extends TitleAreaDialog{
	
	
	
	private String selectedModel =null; 
	private String selectedPath =null; 
	private String pathModel=null;
	
	


	public ModelSelectWindow(Shell parentShell) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	
	public void create() {
		super.create();
        setTitle("Select working project");
        setMessage("Select the project.", IMessageProvider.INFORMATION);
        
        
    }
	

	

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
        GridData gd_container = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_container.widthHint = 325;
        gd_container.heightHint = 200;
        container.setLayoutData(gd_container);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        new Label(container, SWT.NONE);
        
        Label lblProjects = new Label(container, SWT.NONE);
        GridData gd_lblProjects = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_lblProjects.heightHint = 16;
        lblProjects.setLayoutData(gd_lblProjects);
        lblProjects.setText("Projects:");
        new Label(container, SWT.NONE);
        
        
        //tree
        
        final Map<String, TreeItem> nodes = new HashMap<>();
        Map<TreeItem, ArrayList<String>> children = new HashMap<>();

       Tree tree = new Tree(container, SWT.BORDER | SWT.FULL_SELECTION);
       GridData gd_tree = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
       gd_tree.widthHint = 357;
       gd_tree.heightHint = 300;
       tree.setLayoutData(gd_tree);

        Path path = FileSystems.getDefault().getPath(pathModel, new String[] {});
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                {
                    TreeItem parent = nodes.get(dir.getParent().toString());
                    TreeItem item = null;
                    if(parent == null)
                    {
                        item = new TreeItem(tree, SWT.NONE);
                    }
                    else
                    {
                        item = new TreeItem(parent, SWT.NONE);
                    }
                    item.setText(dir.getFileName().toString());

                    nodes.put(dir.toString(), item);

                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path dir, BasicFileAttributes attrs)
                {
                    TreeItem parent = nodes.get(dir.getParent().toString());

                    if(children.get(parent) == null)
                        children.put(parent, new ArrayList<String>());

                    children.get(parent).add(dir.getFileName().toString());

                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(TreeItem parentTree : children.keySet())
        {
            for(String child : children.get(parentTree))
            {
                TreeItem item = new TreeItem(parentTree, SWT.NONE);
                item.setText(child);
            }

        }

        tree.layout();
        
      //listener
        tree.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
            	
            
        	selectedPath=buildTreePath(tree);
            	
            	 
            	
            }
        });
        
		return container;
	}
	
	public String buildTreePath( Tree tree) {
		String path="";
		
		
		System.out.println("PathModel: " +pathModel);
		
		if(tree.getSelection()[0]!=null && tree.getSelection()[0].getText().endsWith(".uml")) {
		TreeItem item=tree.getSelection()[0];
		
		path=item.getText();
		System.out.println("PATH: "+path);
		item=item.getParentItem();
		System.out.println("ITEM:"+item.getText());
	
		while(!pathModel.endsWith(item.getText())) {
			
			path=item.getText()+"/"+path;
			item=item.getParentItem();
			System.out.println("PATH: "+path);
			System.out.println("ITEM:"+item.getText());
			
			
			
		}
		}
   	 	
		return path; 
	}
	
	


	@Override
    protected void okPressed() {
	
      		
   System.out.println("Model path:" +pathModel);
  

        super.okPressed();
    }

	
	public String getSelectedModel() {
		return selectedModel;
	}
	public void setSelectedModel(String selectedModel) {
		this.selectedModel = selectedModel;
	}
	
	
	public String getPathModel() {
		return pathModel;
	}


	public void setPathModel(String pathModel) {
		this.pathModel = pathModel;
	}
	
	public String getSelectedPath() {
		return selectedPath;
	}


	public void setSelectedPath(String selectedPath) {
		this.selectedPath = selectedPath;
	}

}
