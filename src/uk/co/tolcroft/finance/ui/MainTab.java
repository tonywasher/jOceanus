package uk.co.tolcroft.finance.ui;

import java.util.concurrent.*;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Box;
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
import uk.co.tolcroft.finance.help.*;
import uk.co.tolcroft.help.*;
import uk.co.tolcroft.models.Exception;

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
	private	Font 			theStdFont 		= null;
	private	Font 			theNumFont 		= null;
	private	Font 			theChgFont 		= null;
	private	Font 			theChgNumFont 	= null;
	private StatusBar 		theStatusBar    = null;
	private ComboSelect     theComboList    = null;	
	private JMenuBar		theMainMenu		= null;
	private JMenu			theDataMenu		= null;
	private JMenu			theBackupMenu	= null;
	private JMenu			theHelpMenu		= null;
	private JMenuItem		theLoadSheet	= null;
	private JMenuItem		theCreateDBase	= null;
	private JMenuItem		thePurgeDBase	= null;
	private JMenuItem		theLoadDBase	= null;
	private JMenuItem		theSaveDBase	= null;
	private JMenuItem		theWriteBackup	= null;
	private JMenuItem		theLoadBackup	= null;
	private JMenuItem		theShowDebug	= null;
	private JMenuItem		theHelpMgr		= null;
	private ThreadControl	theThread		= null;
	private ExecutorService theExecutor		= null;
	private HelpWindow		theHelpWdw		= null;
	private DebugManager	theDebugMgr		= null;
	private DebugWindow		theDebugWdw		= null;
	
	/* Access methods */
	public 		View			getView()  		{ return theView; }
	public 		Properties		getProperties() { return theProperties; }
	public 		JFrame      	getFrame()      { return theFrame; }
	public 		JPanel      	getPanel()      { return thePanel; }
	public 		StatusBar   	getStatusBar()  { return theStatusBar; }
	public 		DebugManager  	getDebugMgr()  	{ return theDebugMgr; }
	protected 	ComboSelect		getComboList()	{ return theComboList; }

	/* Get explicit font */
	public Font getFont(boolean	isNumeric,
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
		
	/* Constructor */
	public MainTab() throws Exception {
		JPanel  myProgress;
		JPanel  myStatus;
		
		/* Create the debug manager */
		theDebugMgr	  = new DebugManager();
		
		/* Create the view */
		theView       = new View(this);
		
		/* Access properties */
		theProperties = new Properties();
		
		/* Create the Executor service */
		theExecutor = Executors.newSingleThreadExecutor();
		
		/* Create standard font selections */
		theStdFont    = new Font("Arial", Font.PLAIN, 12);
		theNumFont    = new Font("Courier", Font.PLAIN, 12);
		theChgFont    = new Font("Arial", Font.ITALIC, 12);
		theChgNumFont = new Font("Courier", Font.ITALIC, 12);
			
		/* Create the Tabbed Pane */
		theTabs = new JTabbedPane();
			
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
		
		/* Add change listener */
		theTabs.addChangeListener(this);
		determineFocus();
		
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
		theMainMenu.add(Box.createHorizontalGlue());
		theHelpMenu = new JMenu("Help");
		theMainMenu.add(theHelpMenu);
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
		theHelpMgr = new JMenuItem("Help");
		theHelpMgr.addActionListener(this);
		theHelpMenu.add(theHelpMgr);
		theShowDebug = new JMenuItem("Debug");
		theShowDebug.addActionListener(this);
		theHelpMenu.add(theShowDebug);
		
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

		/* Load data from the database */
		loadDatabase();
	}
	
	public void windowClosed(WindowEvent e) {}
	public void windowOpened(WindowEvent e) {}
	public void windowIconified(WindowEvent e) {}
	public void windowDeiconified(WindowEvent e) {}
	public void windowActivated(WindowEvent e) {}
	public void windowDeactivated(WindowEvent e) {}
	
	public void windowClosing(WindowEvent e) {
		/* If this is the frame that is closing down */
		if (e.getSource() == theFrame) {
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
		
			/* terminate the executor */
			theExecutor.shutdown();
		
			/* Dispose of the debug/help Windows if they exist */
			if (theDebugWdw != null) theDebugWdw.dispose();
			if (theHelpWdw  != null) theHelpWdw.dispose();

			/* Dispose of the frame */
			theFrame.dispose();
		}
		
		/* else if this is the Debug Window shutting down */
		else if (e.getSource() == theDebugWdw) {
			/* Re-enable the help menu item */
			theShowDebug.setEnabled(true);
			theDebugWdw.dispose();
			theDebugWdw = null;
			
			/* Notify debug manager */
			theDebugMgr.declareWindow(null);
		}

		/* else if this is the Help Window shutting down */
		else if (e.getSource() == theHelpWdw) {
			/* Re-enable the help menu item */
			theHelpMgr.setEnabled(true);
			theHelpWdw.dispose();
			theHelpWdw = null;
		}
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
		
		/* If this event relates to the Display Debug item */
		if (evt.getSource() == (Object)theShowDebug) {
			/* Open the debug window */
			displayDebug();
		}		
		
		/* If this event relates to the Display Help item */
		if (evt.getSource() == (Object)theHelpMgr) {
			/* Open the help window */
			displayHelp();
		}		
	}
	
	/* Load Spreadsheet */
	public void loadSpreadsheet() {
		loadSpreadsheet myThread;

		/* Create the worker thread */
		myThread = new loadSpreadsheet(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Load Database */
	public void loadDatabase() {
		loadDatabase myThread;

		/* Create the worker thread */
		myThread = new loadDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Store Database */
	public void storeDatabase() {
		storeDatabase myThread;

		/* Create the worker thread */
		myThread = new storeDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Create Database */
	public void createDatabase() {
		createDatabase myThread;

		/* Create the worker thread */
		myThread = new createDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Purge Database */
	public void purgeDatabase() {
		purgeDatabase myThread;
		
		/* Create the worker thread */
		myThread = new purgeDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Write Backup */
	public void writeBackup() {
		writeBackup myThread;
		
		/* Create the worker thread */
		myThread = new writeBackup(theView, this);
		theThread = myThread;
		
		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Restore Backup */
	public void restoreBackup() {
		restoreBackup myThread;

		/* Create the worker thread */
		myThread = new restoreBackup(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		theExecutor.execute(myThread);	
		setVisibleTabs();
	}
	
	/* Display Debug */
	public void displayDebug() {
		try { 
			/* Create the debug window */
			theDebugWdw = new DebugWindow(theFrame, theDebugMgr);
			
			/* Listen for its closure */
			theDebugWdw.addWindowListener(this);
			
			/* Disable the menu item */
			theShowDebug.setEnabled(false);
			
			/* Display it */
			theDebugWdw.showDialog();
		}
		catch (Throwable e) {}
	}
	
	/* Display Help */
	public void displayHelp() {
		try { 
			/* Create the help window */
			theHelpWdw = new HelpWindow(theFrame, new FinanceHelp());
			
			/* Listen for its closure */
			theHelpWdw.addWindowListener(this);
			
			/* Disable the menu item */
			theHelpMgr.setEnabled(false);
			
			/* Display it */
			theHelpWdw.showDialog();
		}
		catch (Throwable e) {}
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
	public void refreshData() throws Exception {
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
		boolean		hasChanges;
		boolean		hasWorker;
		boolean		showTab;
		
		/* Determine whether we have any updates */
		hasUpdates = hasUpdates();
		hasChanges = hasChanges();
		
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
		
		/* Disable menus if we have a worker thread */
		theDataMenu.setEnabled(!hasWorker);
		theBackupMenu.setEnabled(!hasWorker);
		
		/* Enable/Disable the debug menu item */
		theShowDebug.setVisible(theProperties.doShowDebug());
		
		/* If we have changes disable the create backup option */
		theWriteBackup.setEnabled(!hasChanges && !hasUpdates);

		/* If we have updates disable the load backup/database option */
		theLoadBackup.setEnabled(!hasUpdates);
		theLoadDBase.setEnabled(!hasUpdates);
		theLoadSheet.setEnabled(!hasUpdates);

		/* If we have updates or no changes disable the save database */
		theSaveDBase.setEnabled(!hasUpdates && hasChanges);
	}
	
	/* Change listener */
	public void stateChanged(ChangeEvent e) {
		/* Ignore if it is not the tabs */
		if (e.getSource() != theTabs) return;
		
		/* Determine the focus */
		determineFocus();		
	}

	/* Change listener */
	private void determineFocus() {
		/* Access the selected component */
		Component myComponent = theTabs.getSelectedComponent();
		
		/* If the selected component is extract */
		if (myComponent == (Component)theExtract.getPanel()) {
			/* Set the debug focus */
			theExtract.getDebugEntry().setFocus();
			theExtract.requestFocusInWindow();
		}
		
		/* If the selected component is account */
		else if (myComponent == (Component)theAccountCtl.getPanel()) {
			/* Determine focus of accounts */
			theAccountCtl.determineFocus();
		}

		/* If the selected component is SpotView */
		else if (myComponent == (Component)theSpotView.getPanel()) {
			/* Set the debug focus */
			theSpotView.getDebugEntry().setFocus();			
			theSpotView.requestFocusInWindow();
		}
		
		/* If the selected component is Maintenance */
		else if (myComponent == (Component)theMaint.getPanel()) {
			/* Determine focus of maintenance */
			theMaint.determineFocus();			
		}
	}
}
