package eu.arrowhead.skelettons.deployment.handlers;

import java.util.ArrayList;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import eu.arrowhead.skelettons.deployment.dto.LocalCloudDTO;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Combo;


public class DialogWindow extends TitleAreaDialog{
	private Text txtDirectory;
	
	private String directory = "";
	private static String os = "";
	private static String language = "";
	private static Boolean mandatorySys = false;
	private static Boolean supportSys = false;
	private GridData gridData_1;
	private  ArrayList<LocalCloudDTO> localClouds= new ArrayList<LocalCloudDTO>();
	private int selectedLC;
	
	public  DialogWindow(Shell parentShell ) {
		super(parentShell);
		// TODO Auto-generated constructor stub
	}
	
	
	public void create() {
		super.create();
        setTitle("Arrowhead Deployment Generation Plugin");
        setMessage("Select the configuration.", IMessageProvider.INFORMATION);
        
        
    }
	

	

	@Override
    protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
        GridData gd_container = new GridData(SWT.FILL, SWT.FILL, true, true);
        gd_container.heightHint = 550;
        container.setLayoutData(gd_container);
        GridLayout layout = new GridLayout(2, false);
        container.setLayout(layout);
        
        //Description
        Label lbldescription = new Label(container, SWT.NONE);
        lbldescription.setText("Directory:");
        
        txtDirectory = new Text(container, SWT.BORDER);
        txtDirectory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtDirectory.setText(directory);
        txtDirectory.addModifyListener(e -> {
            Text textWidget = (Text) e.getSource();
            String descriptionText = textWidget.getText();
            directory = descriptionText;
        });
        
        
        
        //Systems
        Label lbltitle = new Label(container, SWT.NONE);
        lbltitle.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lbltitle.setText("Local Cloud:");
        
        List list = new List(container, SWT.BORDER | SWT.V_SCROLL);
        
        String[] lcNames = new String[localClouds.size()];
        System.out.println(localClouds.size());
        for(int i=0; i<localClouds.size();i++) {
        	
        	lcNames[i]=localClouds.get(i).getLcName();
        }
        
        list.setItems(lcNames);
        GridData gd_list = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_list.widthHint = 299;
        gd_list.heightHint = 100;
        list.setLayoutData(gd_list);
        

        
       
        Label lblSystems = new Label(container, SWT.NONE);
        lblSystems.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblSystems.setText("Systems:");
        List listsys = new List(container, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        listsys.setItems(" ");
        GridData gd_listsys = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_listsys.widthHint = 299;
        gd_listsys.heightHint = 100;
        listsys.setLayoutData(gd_listsys);
        
        //listener
        list.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
            	 selectedLC=list.getSelectionIndex();
            	listsys.removeAll();
                 
                 String[] sysNames = new String[localClouds.get(selectedLC).getSystems().size()];
                 System.out.println(selectedLC+" "+localClouds.get(selectedLC).getSystems().size());
                 for(int i=0; i<localClouds.get(selectedLC).getSystems().size();i++) {
                 	
                 	sysNames[i]=localClouds.get(selectedLC).getSystems().get(i);
                 	listsys.add(localClouds.get(selectedLC).getSystems().get(i));
                 }
                 
                 
            }
        });
        
        //language
        Group grpLanguage = new Group(container, SWT.NULL);
        grpLanguage.setText("Programming Language");
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        grpLanguage.setLayout(gridLayout);
        gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData_1.widthHint = 510;
        gridData_1.verticalAlignment = SWT.TOP;
        gridData_1.horizontalAlignment = SWT.LEFT;
        gridData_1.heightHint = 69;
        gridData_1.verticalSpan = 2;
        gridData_1.horizontalSpan = 3;
        grpLanguage.setLayoutData(gridData_1);
        
        Button btnRadioButton_3 = new Button(grpLanguage, SWT.RADIO);
        btnRadioButton_3.setText("Java");
        
        Button btnRadioButton_4 = new Button(grpLanguage, SWT.RADIO);
        btnRadioButton_4.setText("C++");
        new Label(grpLanguage, SWT.NONE);
        
        
 
        return container;
    }
	 @Override
	    protected void okPressed() {
		 Shell shell= new Shell();
	       if(directory == null || directory.isEmpty()) {
	      		 MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_ERROR);
	      		messageBox.setMessage("Please enter directory"+ directory);
	              messageBox.open();
	      	}else {
	      		MessageBox messageBox = new MessageBox(shell, SWT.OK | SWT.ICON_WORKING);
	              messageBox.setText("Info");
	              messageBox.setMessage(directory);
	              messageBox.open();
	      	}
	        super.okPressed();
	    }
	 
	 
	 //GETS
		public String getDirectory() {
			return directory; 
		}
		
		public String getOs() {
			return os; 
		}
		
		public String getLanguage() {
			return language; 
		}
		
		public Boolean getMandatorySys() {
			return mandatorySys; 
		}
		public Boolean getSupportSys() {
			return supportSys; 
		}


		public ArrayList<LocalCloudDTO> getLocalClouds() {
			return localClouds;
		}


		public void setLocalClouds(ArrayList<LocalCloudDTO> localClouds) {
			this.localClouds = localClouds;
		}

}
