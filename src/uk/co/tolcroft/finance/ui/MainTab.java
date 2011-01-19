package uk.co.tolcroft.finance.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.security.*;

public class MainTab implements ActionListener,
								ChangeListener,
								WindowListener {
	private View      		theView         = null;
	private Properties	  	theProperties	= null;
	private JFrame          theFrame  		= null;
	private JPanel          thePanel 		= null;
	private JTabbedPane     theTabs  		= null;
	private Extract      	theExtract      = null;
	private AccountTab    	theAccountCtl	= null;
	private ReportTab		theReportTab	= null;
	private PricePoint	  	theSpotView		= null;
	private MaintenanceTab	theMaint		= null;
	private Debug  		  	theDebug		= null;
	private Component		theLastFocus	= null;
	private	Font 			theStdFont 		= null;
	private	Font 			theNumFont 		= null;
	private	Font 			theChgFont 		= null;
	private	Font 			theChgNumFont 	= null;
	private StatusBar 		theStatusBar    = null;
	private ComboSelect     theComboList    = null;	
	private JMenuBar		theMainMenu		= null;
	private JMenu			theDataMenu		= null;
	private JMenu			theBackupMenu	= null;
	private JMenuItem		theLoadSheet	= null;
	private JMenuItem		theCreateDBase	= null;
	private JMenuItem		thePurgeDBase	= null;
	private JMenuItem		theLoadDBase	= null;
	private JMenuItem		theSaveDBase	= null;
	private JMenuItem		theWriteBackup	= null;
	private JMenuItem		theLoadBackup	= null;
	private ThreadControl	theThread		= null;
	private SecurityControl	theSecurity		= null;
	
	/* Access methods */
	public 		View			getView()  		{ return theView; }
	public 		Properties		getProperties() { return theProperties; }
	public 		JFrame      	getFrame()      { return theFrame; }
	public 		JPanel      	getPanel()      { return thePanel; }
	public 		StatusBar   	getStatusBar()  { return theStatusBar; }
	public 		SecurityControl	getSecurity()	{ return theSecurity; }
	protected 	ComboSelect		getComboList()	{ return theComboList; }

	/* Get explicit font */
	protected Font getFont(boolean	isNumeric,
					  	   boolean	isChanged) {
		/* Return the appropriate font */
		if (isNumeric)
			return (isChanged) ? theChgNumFont 	: theNumFont;
		else
			return (isChanged) ? theChgFont 	: theStdFont;
	}
	
	/* Tab headers */
	private static final String titleExtract   	= "Extract";
	private static final String titleAccount   	= "Account";
	private static final String titleReport    	= "Report";
	private static final String titleSpotView  	= "SpotPrices";
	private static final String titleMaint  	= "Maintenance";
	private static final String titleDebug  	= "Debug";
		
	/* Constructor */
	public MainTab() throws Exception {
		JPanel  myProgress;
		JPanel  myStatus;
		
		/* Create the view */
		theView       = new View(this);
		
		/* Access properties */
		theProperties = new Properties();
		
		/* Create standard colour selections */
		theStdFont    = new Font("Arial", Font.PLAIN, 12);
		theNumFont    = new Font("Courier", Font.PLAIN, 12);
		theChgFont    = new Font("Arial", Font.ITALIC, 12);
		theChgNumFont = new Font("Courier", Font.ITALIC, 12);
			
		/* Create the Tabbed Pane */
		theTabs = new JTabbedPane();
		theTabs.addChangeListener(this);
			
		/* Create the extract table and add to tabbed pane */
		theExtract = new Extract(this);
		theTabs.addTab(titleExtract, theExtract.getPanel());
						
		/* Create the accounts control and add to tabbed pane */
		theAccountCtl = new AccountTab(this);
		theTabs.addTab(titleAccount, theAccountCtl.getPanel());
			
		/* Create the Report Tab */
		theReportTab = new ReportTab(this);
		theTabs.addTab(titleReport, theReportTab.getPanel());
		
		/* Create the SpotView Tab */
		theSpotView = new PricePoint(this);
		theTabs.addTab(titleSpotView, theSpotView.getPanel());
		
		/* Create the Maintenance Tab */
		theMaint = new MaintenanceTab(this);
		theTabs.addTab(titleMaint, theMaint.getPanel());
		
		/* Create the Debug Tab */
		theDebug = new Debug(this);
		theTabs.addTab(titleDebug, theDebug.getPanel());
		
		/* Create the new status bar */
		theStatusBar = new StatusBar(this);
		myProgress   = theStatusBar.getProgressPanel();
		myProgress.setVisible(false);
		myStatus     = theStatusBar.getStatusPanel();
		myStatus.setVisible(false);
		
		/* Create the panel */
		thePanel = new JPanel();
		
		/* Create the layout for the panel */
	    GroupLayout myLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(myLayout);
		    
	    /* Set the layout */
	    myLayout.setHorizontalGroup(
	    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addContainerGap()
	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	                    .addComponent(theTabs, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(myProgress, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(myStatus, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(myStatus)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(myProgress)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theTabs)
	                .addContainerGap())
	    );

        /* Create the frame */
		theFrame = new JFrame("Finance");
		
		/* Attach the panel to the frame */
		thePanel.setOpaque(true);
		theFrame.setContentPane(thePanel);
		
		/* Create the menu bar */
		theMainMenu   = new JMenuBar();
		theDataMenu   = new JMenu("Data");
		theMainMenu.add(theDataMenu);
		theBackupMenu = new JMenu("Backup");
		theMainMenu.add(theBackupMenu);
		theFrame.setJMenuBar(theMainMenu);
		
		/* Create the file menu items */
		theLoadSheet = new JMenuItem("Load Spreadsheet");
		theLoadSheet.addActionListener(this);
		theDataMenu.add(theLoadSheet);
		theLoadDBase = new JMenuItem("Load Database");
		theLoadDBase.addActionListener(this);
		theDataMenu.add(theLoadDBase);
		theSaveDBase = new JMenuItem("Store to Database");
		theSaveDBase.addActionListener(this);
		theDataMenu.add(theSaveDBase);
		theCreateDBase = new JMenuItem("Create Database Tables");
		theCreateDBase.addActionListener(this);
		theDataMenu.add(theCreateDBase);
		thePurgeDBase = new JMenuItem("Purge Database");
		thePurgeDBase.addActionListener(this);
		theDataMenu.add(thePurgeDBase);
		theWriteBackup = new JMenuItem("Create Backup");
		theWriteBackup.addActionListener(this);
		theBackupMenu.add(theWriteBackup);
		theLoadBackup = new JMenuItem("Restore from Backup");
		theLoadBackup.addActionListener(this);
		theBackupMenu.add(theLoadBackup);
		
		/* Initialise the data */
		refreshData();
	}		
		
	/* Make the frame */
	public void makeFrame() throws Exception {
		/* Show the frame */
		theFrame.pack();
		theFrame.setLocationRelativeTo(null);
		theFrame.setVisible(true);
		
		/* Add a window listener */
		theFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		theFrame.addWindowListener(this);

		/* Prompt for the password */
		PasswordDialog 	myPass 			= new PasswordDialog(theFrame, false);
		boolean 		isPasswordOk 	= false;
		while (myPass.showDialog()) {
			/* Access the password */
			char[] myPassword = myPass.getPassword();
			
			try {
				String myKey = theProperties.getSecurityKey();
				
				/* Check the password */
				theSecurity 	= new SecurityControl(myKey, myPassword);
				isPasswordOk 	= true;
				
				/* Store new value if required */
				if (myKey == null) { 
					theProperties.setSecurityKey(theSecurity.getSecurityKey());
					theProperties.flushChanges();
				}
				break;
			}
			catch (WrongPasswordException e) {
				myPass = new PasswordDialog(theFrame, true);
				continue;
			}
		}
		
		/* If we have cancelled the operation, then exit */
		if (!isPasswordOk) System.exit(0);
		
		/* Load data from the spreadsheet */
		loadSpreadsheet();
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		/* If we have updates or changes */
		if ((hasUpdates()) || (hasChanges())) {
			/* Ask whether to continue */
			int myOption = JOptionPane.showConfirmDialog(theFrame,
														 "Discard unsaved data changes?",
														 "Confirm Close",
														 JOptionPane.YES_NO_OPTION);
			
			/* Ignore if no was responded */
			if (myOption != JOptionPane.YES_OPTION) return;
		}
		
		/* Dispose of the frame */
		theFrame.dispose();
	}

	public boolean hasUpdates() {
		/* Determine whether we have edit session updates */
		return theExtract.hasUpdates()    ||
		 	   theAccountCtl.hasUpdates() ||
		 	   theSpotView.hasUpdates()   ||
		   	   theMaint.hasUpdates();		
	}

	public boolean hasChanges() {
		/* Determine whether we have core level changes */
		return theView.getData().hasUpdates();
	}

	public void actionPerformed(ActionEvent evt) {
		/* If this event relates to the Write Backup item */
		if (evt.getSource() == (Object)theWriteBackup) {
			/* Start a write backup operation */
			writeBackup();
		}
		
		/* If this event relates to the Save Database item */
		if (evt.getSource() == (Object)theSaveDBase) {
			/* Start a store database operation */
			storeDatabase();
		}		
		
		/* If this event relates to the Load Database item */
		if (evt.getSource() == (Object)theLoadDBase) {
			/* Start a load database operation */
			loadDatabase();
		}		
		
		/* If this event relates to the Create Database item */
		if (evt.getSource() == (Object)theCreateDBase) {
			/* Start a load database operation */
			createDatabase();
		}		
		
		/* If this event relates to the Purge Database item */
		if (evt.getSource() == (Object)thePurgeDBase) {
			/* Start a load database operation */
			purgeDatabase();
		}		
		
		/* If this event relates to the Load spreadsheet item */
		if (evt.getSource() == (Object)theLoadSheet) {
			/* Start a write backup operation */
			loadSpreadsheet();
		}		
		
		/* If this event relates to the Load backup item */
		if (evt.getSource() == (Object)theLoadBackup) {
			/* Start a restore backup operation */
			restoreBackup();
		}		
	}
	
	/* Change listener */
	public void stateChanged(ChangeEvent e) {
		Component myComponent = theTabs.getSelectedComponent();
		if (theDebug == null) return;
		if (myComponent == theDebug.getPanel()) {
			/* Get the debug window to build its report */
			theDebug.buildReport();
		}
		else if (myComponent != theReportTab.getPanel()) {
			/* Note the last focus */
			theLastFocus = myComponent;
		}
	}
	
	/* Load Spreadsheet */
	public void loadSpreadsheet() {
		loadSpreadsheet myThread;

		/* Create the worker thread */
		myThread = new loadSpreadsheet(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Load Database */
	public void loadDatabase() {
		loadDatabase myThread;

		/* Create the worker thread */
		myThread = new loadDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Store Database */
	public void storeDatabase() {
		storeDatabase myThread;

		/* Create the worker thread */
		myThread = new storeDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Create Database */
	public void createDatabase() {
		createDatabase myThread;

		/* Create the worker thread */
		myThread = new createDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Purge Database */
	public void purgeDatabase() {
		purgeDatabase myThread;

		/* Create the worker thread */
		myThread = new purgeDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Write Backup */
	public void writeBackup() {
		writeBackup myThread;
		
		/* Create the worker thread */
		myThread = new writeBackup(theView, this);
		theThread = myThread;
		
		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Restore Backup */
	public void restoreBackup() {
		restoreBackup myThread;

		/* Create the worker thread */
		myThread = new restoreBackup(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Finish Thread */
	public void finishThread() {
		theThread = null;
		setVisibleTabs();
	}
	
	/* Handle cancel command */
	public void performCancel() {
		if (theThread != null) theThread.cancel(false);
	}

	/* Select an explicit account and period */
	public void selectAccount(Account  	pAccount,
							  DateRange pSource) {
		/* Pass through to the Account control */
		theAccountCtl.selectAccount(pAccount, pSource);
		
		/* Goto the Accounts tab */
		gotoNamedTab(titleAccount);
	}
	
	/* Select an explicit extract period */
	public void selectPeriod(DateRange pSource) {
		/* Pass through to the Extract */
		theExtract.selectPeriod(pSource);
		
		/* Goto the Extract tab */
		gotoNamedTab(titleExtract);
	}
	
	/* Select an explicit account for maintenance */
	public void selectAccountMaint(Account  pAccount) {
		/* Pass through to the Account control */
		theMaint.selectAccount(pAccount);
		
		/* Goto the Accounts tab */
		gotoNamedTab(titleMaint);
	}
	
	/* Goto the specific tab */
	public void gotoNamedTab(String pTabName) {
		/* Access the Named index */
		int iIndex = theTabs.indexOfTab(pTabName);
	
		/* Select the required tab */
		theTabs.setSelectedIndex(iIndex);
	}
	
	/* refresh data */
	public void refreshData() {
		/* Create the combo list */
		theComboList  = new ComboSelect(theView);
			
		/* Refresh the windows */
		theExtract.refreshData();
		theAccountCtl.refreshData();
		theReportTab.refreshData();
		theSpotView.refreshData();
		theMaint.refreshData();
			
		/* Sort out visible tabs */
		setVisibleTabs();
	}

	/* Set visible tabs */
	public void setVisibleTabs() {
		int         iIndex;
		boolean     hasUpdates;
		boolean		hasWorker;
		boolean		showTab;
		
		/* Determine whether we have any updates */
		hasUpdates = hasUpdates();
		
		/* Note whether we have a worker thread */
		hasWorker = (theThread != null);
		
		/* Access the Extract panel and determine its status */
		iIndex  = theTabs.indexOfTab(titleExtract);
		showTab = (!hasWorker && (!hasUpdates || theExtract.hasUpdates())); 
		
		/* Enable/Disable the extract tab */
		if (iIndex != -1) theTabs.setEnabledAt(iIndex, showTab);
		
		/* Access the AccountCtl panel and determine its status */
		iIndex  = theTabs.indexOfTab(titleAccount);
		showTab = (!hasWorker && (!hasUpdates || theAccountCtl.hasUpdates())); 
		
		/* Enable/Disable the account control tab */
		if (iIndex != -1) theTabs.setEnabledAt(iIndex, showTab);
		
		/* Access the Report panel */
		iIndex = theTabs.indexOfTab(titleReport);
		
		/* Enable/Disable the reports tab */
		if (iIndex != -1) theTabs.setEnabledAt(iIndex, !hasUpdates);		
		
		/* Access the SpotView panel and determine its status */
		iIndex = theTabs.indexOfTab(titleSpotView);
		showTab = (!hasWorker && (!hasUpdates || theSpotView.hasUpdates())); 
		
		/* Enable/Disable the spotView tab */
		if (iIndex != -1) theTabs.setEnabledAt(iIndex, showTab);		
		
		/* Access the Maintenance panel */
		iIndex = theTabs.indexOfTab(titleMaint);
		showTab = (!hasWorker && (!hasUpdates || theMaint.hasUpdates())); 
		
		/* Enable/Disable the maintenance tab */
		if (iIndex != -1) theTabs.setEnabledAt(iIndex, showTab);
		
		/* Access the Debug panel */
		iIndex = theTabs.indexOfTab(titleDebug);
		showTab = theProperties.doShowDebug(); 
		
		/* Enable/Disable the debug tab */
		if (iIndex != -1) theTabs.setEnabledAt(iIndex, showTab);
		
		/* Disable menus if we have a worker thread */
		theDataMenu.setEnabled(!hasWorker);
		theBackupMenu.setEnabled(!hasWorker);
		
		/* TODO disable load/backup if we have changes */
	}

	/* Get Formatted Debug output */
	protected String getDebugText() {
		Exception 	myError;
		DataList<?>	myList;
		String		myText = "";
		
		/* Determine whether we have an active error */
		myError = theStatusBar.getError();
		
		/* If we have an error */
		if (myError != null) {
			/* Access the formatted error */
			myText = myError.toHTMLString().toString();
		}
		
		/* Else if the last focus was the extracts tab */
		else if ((theLastFocus == null) ||
				 (theLastFocus == theExtract.getPanel())) {
			/* Access the formatted text */
			myList = theExtract.getList();
			if (myList != null) myText = myList.toHTMLString().toString();
		}
		
		/* Else if the last focus was the accounts tab */
		else if (theLastFocus == theAccountCtl.getPanel()) {
			/* Access the formatted text */
			myText = theAccountCtl.getDebugText();
		}
		
		/* Else if the last focus was the accounts tab */
		else if (theLastFocus == theSpotView.getPanel()) {
			/* Access the formatted text */
			myList = theSpotView.getList();
			if (myList != null) myText = myList.toHTMLString().toString();
		}
		
		/* Else if the last focus was the maintenance tab */
		else if (theLastFocus == theMaint.getPanel()) {
			/* Access the formatted text */
			myText = theMaint.getDebugText();
		}
		
		/* Return to caller */
		return myText;
	}	
}
