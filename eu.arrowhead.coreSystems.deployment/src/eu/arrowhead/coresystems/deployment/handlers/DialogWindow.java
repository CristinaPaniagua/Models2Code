package eu.arrowhead.coresystems.deployment.handlers;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;


public class DialogWindow extends TitleAreaDialog{
	private Text txtDirectory;
	
	private String directory = "";
	private static String os = "";
	private static String language = "";
	private static Boolean mandatorySys = false;
	private static Boolean supportSys = false;
	private GridData gridData_1;

	
	public  DialogWindow(Shell parentShell) {
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
        gd_container.heightHint = 322;
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
        lbltitle.setText("Systems:");
        
        List list = new List(container,SWT.MULTI |  SWT.BORDER | SWT.V_SCROLL);
        list.setItems(new String[] {"Mandatory Core Systems", "Support Systems"});
        GridData gd_list = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        gd_list.widthHint = 299;
        gd_list.heightHint = 47;
        list.setLayoutData(gd_list);
        new Label(container, SWT.NONE);
        new Label(container, SWT.NONE);
        
        //OS
        Group device = new Group(container, SWT.NULL);
        device.setText("Operating System");
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        device.setLayout(gridLayout);
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        gridData.horizontalSpan = 3;
        device.setLayoutData(gridData);
        
        Button btnRadioButton = new Button(device, SWT.RADIO);
        btnRadioButton.setText("Windows");
        
        Button btnRadioButton_1 = new Button(device, SWT.RADIO);
        btnRadioButton_1.setText("Linux");
        
        Button btnRadioButton_2 = new Button(device, SWT.RADIO);
        btnRadioButton_2.setText("Mac");
        //language
        Group grpLanguage = new Group(container, SWT.NULL);
        grpLanguage.setText("Programming Language");
        gridLayout = new GridLayout();
        gridLayout.numColumns = 3;
        grpLanguage.setLayout(gridLayout);
        gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
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

}
