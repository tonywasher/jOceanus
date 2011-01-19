package finance;

import javax.swing.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.Component;

import javax.swing.table.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import finance.finLink.itemCtl.ListStyle;
import finance.finLink.histObject;
import finance.finObject.State;
import finance.finObject.EditState;
import finance.finReport.ReportTab;
import finance.finUtils.RenderData;
import finance.finUtils.ComboSelect;
import finance.finUtils.Controls.tableCommand;
import finance.finUtils.Controls.AccountSelection;
import finance.finUtils.Controls.DateSelection;
import finance.finUtils.Controls.StatusBar;
import finance.finUtils.Controls.InsertStyle;

public class finSwing  implements ActionListener,
								  ChangeListener,
 								  WindowListener {
	/* Members */
	private finView           theView           = null;
	private finProperties	  theProperties		= null;
	private finSwing          theTopWindow      = this;
	private JFrame            theFrame          = null;
	private JPanel            thePanel          = null;
	private JTabbedPane       theTabs           = null;
	private ExtractTable      theExtract        = null;
	private AccountControl    theAccountCtl	    = null;
	private ReportTab		  theReportTab		= null;
	private SpotViewTable	  theSpotView		= null;
	private finMaintenance	  theMaint			= null;
	private finDebug  		  theDebug			= null;
	private Font		      theStdFont    	= null;
	private Font		      theNumFont    	= null;
	private Font		      theChgFont	    = null;
	private Font		      theChgNumFont   	= null;
	private Component		  theLastFocus		= null;

	private JMenuBar			theMainMenu		= null;
	private JMenu				theDataMenu		= null;
	private JMenu				theBackupMenu	= null;
	private JMenuItem			theLoadSheet	= null;
	private JMenuItem			theLoadDBase	= null;
	private JMenuItem			theSaveDBase	= null;
	private JMenuItem			theWriteBackup	= null;
	private JMenuItem			theLoadBackup	= null;
	
	private StatusBar 				theStatusBar    = null;
	private ComboSelect       		theComboList    = null;
	private JComboBox				theFreqBox		= null;
	private JComboBox				theTranBox		= null;
	private finStatic.FreqList		theFreqs    	= null;
	private finStatic.TransTypeList	theTranTyps 	= null;
	private finData.AccountList		theAccounts 	= null;
	private boolean					freqsPopulated  = false;
	private boolean					transPopulated  = false;
	
	private finThread.ThreadControl	theThread		= null;
		
	/* Access methods */
	public JFrame       	getFrame()       { return theFrame; }
	public JPanel       	getPanel()       { return thePanel; }
	public StatusBar    	getStatusBar()   { return theStatusBar; }
	public finView			getView()  		 { return theView; }
	public finProperties	getProperties()  { return theProperties; }
	public Font				getStdFont()	 { return theStdFont; }
	public Font				getChgFont()     { return theChgFont; }
	public Font				getNumFont()	 { return theNumFont; }
	public Font				getChgNumFont()  { return theChgNumFont; }
		
	/* Tab headers */
	private static final String titleExtract   	= "Extract";
	private static final String titleAccount   	= "Account";
	private static final String titleReport    	= "Report";
	private static final String titleSpotView  	= "SpotPrices";
	private static final String titleMaint  	= "Maintenance";
	private static final String titleDebug  	= "Debug";
		
	/* Constructor */
	public finSwing() throws finObject.Exception {
		JPanel  myProgress;
		JPanel  myStatus;
		
		/* Create the view */
		theView       = new finView(this);
		
		/* Access properties */
		theProperties = new finProperties();
		
		/* Create standard colour selections */
		theStdFont    = new Font("Arial", Font.PLAIN, 12);
		theNumFont    = new Font("Courier", Font.PLAIN, 12);
		theChgFont    = new Font("Arial", Font.ITALIC, 12);
		theChgNumFont = new Font("Courier", Font.ITALIC, 12);
			
		/* Build the combo boxes */
		theFreqBox    = new JComboBox();
		theTranBox	  = new JComboBox();
			
		/* Create the Tabbed Pane */
		theTabs = new JTabbedPane();
		theTabs.addChangeListener(this);
			
		/* Create the extract table and add to tabbed pane */
		theExtract = new ExtractTable(this);
		theTabs.addTab(titleExtract, theExtract.getPanel());
						
		/* Create the accounts control and add to tabbed pane */
		theAccountCtl = new AccountControl(this);
		theTabs.addTab(titleAccount, theAccountCtl.getPanel());
			
		/* Create the Report Tab */
		theReportTab = new ReportTab(this);
		theTabs.addTab(titleReport, theReportTab.getPanel());
		
		/* Create the SpotView Tab */
		theSpotView = new SpotViewTable(this);
		theTabs.addTab(titleSpotView, theSpotView.getPanel());
		
		/* Create the Maintenance Tab */
		theMaint = new finMaintenance(this);
		theTabs.addTab(titleMaint, theMaint.getPanel());
		
		/* Create the Debug Tab */
		theDebug = new finDebug(this);
		theTabs.addTab(titleDebug, theDebug.getPanel());
		
		/* Create the new status bar */
		theStatusBar = new finUtils.Controls.StatusBar(this);
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
	public void makeFrame() throws finObject.Exception {		
		/* Show the frame */
		theFrame.pack();
		theFrame.setLocationRelativeTo(null);
		theFrame.setVisible(true);
		
		/* Add a window listener */
		theFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		theFrame.addWindowListener(this);

		/* Initialise encryption services */
		finEncryption.initialise(this);
		
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
		finThread.loadSpreadsheet myThread;

		/* Create the worker thread */
		myThread = new finThread.loadSpreadsheet(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Load Database */
	public void loadDatabase() {
		finThread.loadDatabase myThread;

		/* Create the worker thread */
		myThread = new finThread.loadDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Store Database */
	public void storeDatabase() {
		finThread.storeDatabase myThread;

		/* Create the worker thread */
		myThread = new finThread.storeDatabase(theView, this);
		theThread = myThread;

		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Write Backup */
	public void writeBackup() {
		finThread.writeBackup myThread;
		
		/* Create the worker thread */
		myThread = new finThread.writeBackup(theView, this);
		theThread = myThread;
		
		/* Execute it and lock tabs */
		myThread.execute();	
		setVisibleTabs();
	}
	
	/* Restore Backup */
	public void restoreBackup() {
		finThread.restoreBackup myThread;

		/* Create the worker thread */
		myThread = new finThread.restoreBackup(theView, this);
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
	public void selectAccount(finData.Account  pAccount,
							  DateSelection    pSource) {
		/* Pass through to the Account control */
		theAccountCtl.selectAccount(pAccount, pSource);
		
		/* Goto the Accounts tab */
		gotoNamedTab(titleAccount);
	}
	
	/* Select an explicit extract period */
	public void selectPeriod(DateSelection    pSource) {
		/* Pass through to the Extract */
		theExtract.selectPeriod(pSource);
		
		/* Goto the Extract tab */
		gotoNamedTab(titleExtract);
	}
	
	/* Select an explicit account for maintenance */
	public void selectAccountMaint(finData.Account  pAccount) {
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
		finData				  myData;
		finStatic.Frequency   myFreq;
		finStatic.TransType   myTran;
		
		/* Create the combo list */
		theComboList  = new ComboSelect(theView);
			
		/* Access the data */
		myData = theView.getData();
		
		/* Access Frequencies, TransTypes and Accounts */
		theFreqs    = myData.getFrequencys();
		theTranTyps = myData.getTransTypes();
		theAccounts = myData.getAccounts();
	
		/* If we have frequencies already populated */
		if (freqsPopulated) {	
			/* Remove the frequencies */
			theFreqBox.removeAllItems();
			freqsPopulated = false;
		}
		
		/* Add the Frequency values to the frequencies box */
		for (myFreq  = theFreqs.getFirst();
		     myFreq != null;
		     myFreq  = myFreq.getNext()) {
			/* Add the item to the list */
			theFreqBox.addItem(myFreq.getName());
			freqsPopulated = true;
		}
		
		/* If we have types already populated */
		if (transPopulated) {	
			/* Remove the frequencies */
			theTranBox.removeAllItems();
			transPopulated = false;
		}
		
		/* Add the TransType values to the transtype box */
		for (myTran  = theTranTyps.getFirst();
		     myTran != null;
		     myTran  = myTran.getNext()) {
			/* Add the item to the list */
			theTranBox.addItem(myTran.getName());
			transPopulated = true;
		}
		
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
		finObject.Exception myError;
		tableList			myList;
		String				myText = "";
		
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
	
	protected interface financePanel {
		abstract boolean 		isLocked();
		abstract boolean 		hasUpdates();
		abstract EditState      getEditState();
		abstract void			notifySelection(Object obj);
		abstract void  			performCommand(tableCommand pCmd);
	}
	protected interface financeView extends financePanel {
		abstract void 			getRenderData(RenderData pData);	
	}
	protected interface financeTable extends financeView {
		abstract boolean 		hasErrors();
		abstract boolean 		hasHeader();
		abstract boolean 		hasCreditChoice();
		abstract boolean 		needsMembers();
		abstract boolean 		isEnabled();
		abstract boolean 		isValidObj(finLink.itemElement pItem, finLink.histObject pObj);
		abstract int     		rowAtPoint(Point p);
		abstract int			getFieldForCol(int col);
		abstract JComboBox  	getComboBox(int row, int column);
		abstract void			notifyChanges();
		abstract void  			saveData();
		abstract void  			resetData();
		abstract void  			cancelEditing();
		abstract boolean		calculateTable();
		abstract int[]			getSelectedRows();
		abstract tableElement 	extractItemAt(long iIndex);
	}
	protected interface tableList {
		abstract boolean      	getShowDeleted();
		abstract boolean 		hasUpdates();
		abstract boolean      	hasErrors();
		abstract boolean      	hasMembers();
		abstract boolean 		isLocked();
		abstract void         	validate();
		abstract void         	findEditState();
		abstract void 			resetChanges();
		abstract void		    setShowDeleted(boolean bShow);
		abstract void			addNewItem(boolean isCredit);
		abstract tableElement 	extractItemAt(long iIndex);
		abstract EditState      getEditState();
		abstract int			countItems();
		abstract StringBuilder	toHTMLString();
	}	
	protected interface tableElement {
		abstract boolean 		   	hasHistory();
		abstract boolean 		   	hasFurther(financeTable pTable);
		abstract boolean 		   	hasPrevious();
		abstract boolean 		   	hasErrors();
		abstract boolean 		   	hasErrors(int iField);
		abstract boolean 		   	fieldChanged(int iField);
		abstract boolean 		   	isDeleted();
		abstract boolean 		   	isCoreDeleted();
		abstract boolean 		   	isLocked();
		abstract boolean			isListLocked();
		abstract EditState      	getEditState();
		abstract finObject.State 	getState();
		abstract finObject.Date  	getDate();
		abstract String 		   	getFieldError(int iField);
		abstract tableList			getList();
		abstract histObject			getObj();
		abstract int				indexOfItem();
		abstract void    		   	clearErrors();
		abstract void    		   	popHistory();
		abstract void    		   	resetHistory();
		abstract void    		   	peekFurther();
		abstract void    		   	peekPrevious();
		abstract void    		   	reSort();	
		abstract void    		   	setState(finObject.State pState);
		abstract void    		   	validate();
	}

	/* Base table class */
	public abstract class financeBaseTable extends    JTable
	                   	   		  		   implements financeTable,
	                   	   		  		   			  ListSelectionListener {
		/* Members */
		private static final long serialVersionUID = 5175290992863678655L;
		private AbstractTableModel           theModel     = null;
		private financeTable				 theMaster    = null;
		private tableList  	         		 theList	  = null;
		private boolean                      doShowDel    = false;
		private boolean                      isEnabled    = false;

		/* Access methods */
		public boolean hasHeader() 			{ return false; }
		public boolean hasCreditChoice() 	{ return false; }
		public boolean needsMembers()		{ return false; }
		public boolean hasUpdates() 		{ return (theList != null) &&
													 theList.hasUpdates(); }
		public boolean hasErrors()  		{ return (theList != null) &&
													 theList.hasErrors(); }
		public boolean isActive()			{ return isEnabled; }
		public void    notifySelection(Object obj)    { }
		public boolean calculateTable()				  { return false; }
			
		public void    setActive(boolean isActive)	{ isEnabled = isActive; }
		public JComboBox getComboBox(int row, int col) { return null; }
		public boolean isValidObj(finLink.itemElement pItem,
				                  finLink.histObject  pObj) { return true; }
		public tableList getList() { return theList; }
			
		/* Abstract methods */
		public abstract int  getFieldForCol(int iField);
		public abstract void notifyChanges();
			
		/* Constructor */
		financeBaseTable() {
			setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
			
		/* Get the Edit State */
		public EditState getEditState() {
			if (theList == null) return EditState.CLEAN;
			return theList.getEditState();
		}
			
		/* Set List */
		public void setList(tableList pList) {
			/* Store list and select correct mode */
			theList = pList;
			if (pList != null) pList.setShowDeleted(doShowDel);
				
			/* If we have elements then set the selection */
			clearSelection();
			if (theModel.getRowCount() > 0)
				setRowSelectionInterval(0, 0);
			
			/* Redraw the table */
			theModel.fireTableDataChanged();
		}
					
		/* is this table locked */
		public boolean isLocked() {
			/* Store list and select correct mode */
			return ((theList == null) || theList.isLocked());
		}
					
		/* Extract Item */
		public tableElement extractItemAt(long uIndex) {
			return((theList == null) ? null
									 : theList.extractItemAt(uIndex));
		}
			
		/* Change showDeleted */
		public void setShowDeleted(boolean doShowDel) {
			tableElement 	myRow;
			int		  		row;

			/* If we are changing the value */
			if (this.doShowDel != doShowDel) {
				/* Cancel any editing */
				if (isEditing()) cellEditor.cancelCellEditing();
					
				/* Determine the selected row */
				row = getSelectedRow();
				if ((row >= 1) && (theMaster.hasHeader())) row--;
				myRow = theList.extractItemAt(row);
					
				/* Store the new status */
				this.doShowDel = doShowDel;
				theList.setShowDeleted(doShowDel);
					
				/* Redraw the table */
				theModel.fireTableDataChanged();
					
				/* Reselect the row */
				if (row >= 0) {
					if (myRow != null) row = myRow.indexOfItem();
					if (theMaster.hasHeader()) row++;
					selectRow(row);
				}
			}
		}
					
		/* Get render data for row */
		public void getRenderData(RenderData pData) {
			tableElement   myRow;
			String     	   myTip = null;
			int			   iRow;
			int            iField;
			Color		   myFore;
			Color		   myBack;
			Font		   myFont;
			boolean 	   isChanged = false;
			
			/* If we have a header decrement the row */
			iRow = pData.getRow();
			if (theMaster.hasHeader()) iRow--;
			
			/* Default is black on white */
			myBack = Color.white;
			myFore = Color.black;

			/* If this is a data row */
			if (iRow >= 0) {
				/* Access the row */
				myRow  = theList.extractItemAt(iRow);
				iField = theMaster.getFieldForCol(pData.getCol());
				
				/* Has the field changed */
				isChanged = myRow.fieldChanged(iField);
				
				/* Determine the colour */
				if (myRow.isDeleted()) {
					myFore = Color.lightGray;
				}
				else if ((myRow.hasErrors()) &&
						 (myRow.hasErrors(iField))) {
					myFore = Color.red;
					myTip = myRow.getFieldError(iField);
				}
				else if (isChanged)
					myFore = Color.magenta;
				else if (myRow.getState() == finObject.State.NEW)
					myFore = Color.blue;
				else if (myRow.getState() == finObject.State.RECOVERED)
					myFore = Color.darkGray;
			}
				
			/* For selected items flip the foreground/background */
			if (pData.isSelected()){
				Color myTemp = myFore;
				myFore = myBack;
				myBack = myTemp;
			}
				
			/* Select the font */
			if (pData.isFixed())
				myFont = isChanged	? theChgNumFont 
									: theNumFont;
			else
				myFont = isChanged	? theChgFont 
									: theStdFont;
			
			/* Set the data */
			pData.setData(myFore, myBack, myFont, myTip);
				
			/* return the data */
			return;
		}
			
		/* Select a row and ensure that it is visible */
		private void selectRowWithScroll(int row) {		
			Rectangle 	rect;
			Point		pt;
			JViewport	viewport;
			  
			/* Shift display to line */
			rect = getCellRect(row, 0, true);
			viewport = (JViewport)getParent();
			pt = viewport.getViewPosition();
			rect.setLocation(rect.x - pt.x, rect.y - pt.y);
			viewport.scrollRectToVisible(rect);
			
			/* clear existing selection and select the row */
			selectRow(row);
		}
			
		/* Select a row */
		protected void selectRow(int row) {		
			/* clear existing selection and select the row */
			clearSelection();
			changeSelection(row, 0, false, false);
			requestFocusInWindow();
		}
			
		/* Insert an item */
		public void insertRow(boolean isCredit) {		
			int 		row;
			  
			/* Determine the row number */
			row = theList.countItems();
			
			/* Create the new Item */
			theList.addNewItem(isCredit);
			
			/* If we have a header increment the row */
			if (theMaster.hasHeader()) row++;
			
			/* Notify of the insertion of the column */
			theModel.fireTableRowsInserted(row, row);
				
			/* Shift display to line */
			selectRowWithScroll(row);
		}
			
		/* Delete the item at row */
		public boolean deleteRow(int row) {		
			tableElement myRow;
			int          numRows = theList.countItems();
			int			 numHdrs = (theMaster.hasHeader() ? 1 : 0);
			
			/* Access the row */
			myRow = theList.extractItemAt(row);
				
			/* If the row is not deleted */
			if (!myRow.isDeleted()) {
				/* If we have a header increment the row */
				if (theMaster.hasHeader()) { row++; numRows++; }
			
				/* Mark the row as deleted */
				myRow.setState(finObject.State.DELETED);
				
				/* If we are recalculating the table */
				if (calculateTable()) 
					theModel.fireTableDataChanged();
				
				/* If we are showing deleted items */
				else if (theList.getShowDeleted()) {
					/* Re-draw the row */
					for (int i=0; i<theModel.getColumnCount(); i++)
						theModel.fireTableCellUpdated(row, i);
						
					/* Reselect the row and notify changes */
					selectRow(row);
					return false;
				}
				
				/* Notify of the deletion of the row */
				else theModel.fireTableRowsDeleted(row, row);
					
				/* If we are the final row shift back one*/
				if (row == numRows-1) row--;
				if (row < numHdrs) row++;
				selectRow(row);
				return true;
			}
				
			/* Say the we have not removed this item from visible */
			return false;
		}
			
		/* Recover the item at row */
		public void recoverRow(int row) {		
			tableElement myRow;
			
			/* Access the row */
			myRow = theList.extractItemAt(row);
			
			/* If the row is deleted */
			if (myRow.isDeleted()) {
				/* If we have a header increment the row */
				if (theMaster.hasHeader()) row++;
			
				/* Mark the row as recovered */
				myRow.setState(finObject.State.RECOVERED);
				myRow.clearErrors();
				myRow.validate();
				
				/* If we are recalculating the table */
				if (calculateTable()) 
					theModel.fireTableDataChanged();
								
				/* Re-draw the row */
				else for (int i=0; i<theModel.getColumnCount(); i++)
					theModel.fireTableCellUpdated(row, i);
				
				/* Reselect the row and notify changes */
				selectRow(row);
			}
		}
			
		/* Validate the item at row */
		public void validateRow(int row) {		
			tableElement myRow;
				
			/* Access the row */
			myRow = theList.extractItemAt(row);
			
			/* If we have a header increment the row */
			if (theMaster.hasHeader()) row++;
				
			/* Clear errors and re-validate */
			myRow.clearErrors();
			myRow.validate();
			
			/* Re-draw the row */
			for (int i=0; i<theModel.getColumnCount(); i++)
				theModel.fireTableCellUpdated(row, i);
				
			/* Reselect the row and notify changes */
			selectRow(row);
		}
			
		/* Reset the item at row */
		public void resetRow(int row) {		
			tableElement myRow;
			
			/* Access the row */
			myRow = theList.extractItemAt(row);
			
			/* Clear errors and re-validate */
			myRow.clearErrors();
			myRow.resetHistory();
			myRow.validate();
				
			/* Mark the row as clean */
			myRow.setState(myRow.isCoreDeleted()
					? finObject.State.RECOVERED
					: finObject.State.CLEAN);
			calculateTable();
			
			/* Determine new row number */
			row = myRow.indexOfItem();
			
			/* Re-draw the row */
			theModel.fireTableDataChanged();
			
			/* Reselect the row and notify changes */
			if (theMaster.hasHeader()) row++;
			selectRow(row);
		}
			
		/* Validate all the items */
		public void validateAll() {		
			/* Validate the list */
			theList.validate();
			theList.findEditState();
				
			/* Re-draw the table */
			theModel.fireTableDataChanged();
		}
			
		/* Undo changes to a row */
		public void unDoRow(int row) {		
			tableElement myRow;
				
			/* Access the row */
			myRow = theList.extractItemAt(row);
			
			/* If the row has changes */
			if (myRow.hasHistory()) {
				/* Pop last value */
				myRow.popHistory();
				
				/* Resort the item */
				myRow.reSort();
				myRow.clearErrors();
				myRow.validate();
				calculateTable();
				
				/* Determine new row number */
				row = myRow.indexOfItem();
					
				/* If the item is now clean */
				if (!myRow.hasHistory()) {
					/* Set the new status */
					myRow.setState(myRow.isCoreDeleted()
									? finObject.State.RECOVERED
									: finObject.State.CLEAN);
				}
				
				/* Re-draw the table due to the changed data */
				theModel.fireTableDataChanged();
			}
				
			/* Reselect the row and notify changes */
			if (theMaster.hasHeader()) row++;
			selectRow(row);
		}
			
		/* restore changes to a row */
		public void restoreNextRow(int row) {		
			tableElement myRow;
				
			/* Access the row */
			myRow = theList.extractItemAt(row);
				
			/* If the row has changes */
			if (myRow.hasFurther(this)) {
				/* Pop last value */
				myRow.peekFurther();
			
				/* Resort the item */
				myRow.reSort();
				myRow.clearErrors();
				myRow.validate();
				calculateTable();

				/* Determine new row number */
				row = myRow.indexOfItem();
					
				/* Set the new status */
				myRow.setState(finObject.State.CLEAN);
								
				/* Re-draw the table due to the changed data */
				theModel.fireTableDataChanged();
			}
			
			/* Reselect the row and notify changes */
			if (theMaster.hasHeader()) row++;
			selectRow(row);
		}
			
		/* restore changes to a row */
		public void restorePrevRow(int row) {		
			tableElement myRow;
			
			/* Access the row */
			myRow = theList.extractItemAt(row);
			
			/* If the row has changes */
			if (myRow.hasPrevious()) {
				/* Pop last value */
				myRow.peekPrevious();
			
				/* Resort the item */
				myRow.reSort();
				myRow.clearErrors();
				myRow.validate();
				calculateTable();
				
				/* Determine new row number */
				row = myRow.indexOfItem();
				
				/* Set the new status */
				myRow.setState(finObject.State.CLEAN);
									
				/* Re-draw the table due to the changed data */
				theModel.fireTableDataChanged();
			}
				
			/* Reselect the row and notify changes */
			if (theMaster.hasHeader()) row++;
			selectRow(row);
		}
			
		/* resetData */
		public void resetData() {
			if (theList != null) { 
				theList.resetChanges();
				theList.findEditState();
			}
			calculateTable();
			theModel.fireTableDataChanged();
		}
			
		/* Perform command function */
		public void performCommand(tableCommand pCmd) {
			int rowsdel = 0;
				
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
				
			/* Switch on command */
			switch (pCmd) {
				/* Handle the commands that operate on selection */
				case RECOVER:
				case DELETE:
				case UNDO:
				case NEXT:
				case PREV:
				case RESET:
				case VALIDATE:
					/* Loop through the selected rows */
					for (int row : getSelectedRows()) {
						/* If we have a header decrement the row */
						if (theMaster.hasHeader()) row--;
						
						/* Switch on command */
						switch (pCmd) {
							case RECOVER:
								recoverRow(row);
								break;
							case DELETE:
								if (deleteRow(row-rowsdel))
									rowsdel++;
								break;
							case UNDO:
								unDoRow(row);
								break;
							case VALIDATE:
								validateRow(row);
								break;
							case RESET:
								resetRow(row);
								break;
							case NEXT:
								restoreNextRow(row);
								break;
							case PREV:
								restorePrevRow(row);
								break;
						}
					}
					break;
				case OK:
					saveData();
					break;
				case RESETALL:
					resetData();
					break;
				case VALIDATEALL:
					validateAll();
					break;
				case INSERTCR:
					insertRow(true);
					break;
				case INSERTDB:
					insertRow(false);
					break;
			}
		}
			
		/* Perform command function */
		public void cancelEditing() {		
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
		}
		
		/* valueChanged listener event */
		public void valueChanged(ListSelectionEvent evt) {
			super.valueChanged(evt);
            if (evt.getValueIsAdjusting()) {
                return;
            }
            notifyChanges();
		}
	}
		
	/* Top-level account tab management */
	public class AccountControl extends financeBaseTable
								implements ChangeListener {
		/* Members */
		private static final long serialVersionUID  = 7682053546233794088L;
		private JPanel							thePanel	 = null;
		private finSwing						theParent    = null;
		private JTabbedPane       				theTabs      = null;
		private AccountSelection  				theSelect	 = null;
		private StatementTable    				theStatement = null;
		private StatementTable    				theUnits     = null;
		private PatternsTable     				thePatterns  = null;
		private PricesTable       				thePrices    = null;
		private RatesTable        				theRates     = null;
		private finData.Account					theAccount   = null;
		private finData.AccountList				theAcList	 = null;
		private finUtils.Controls.TableButtons  theTabButs   = null;
		private Component		  				theLastFocus = null;
			
		/* Access methods */
		public JPanel  		getPanel()   			  { return thePanel; }
		public int  		getFieldForCol(int col)   { return -1; }
			
		/* Tab headers */
		private static final String titleStatement = "Statement";
		private static final String titleUnits     = "UnitsStatement";
		private static final String titlePatterns  = "Patterns";
		private static final String titlePrices    = "Prices";
		private static final String titleRates     = "Rates";
			
		/* Constructor */
		public AccountControl(finSwing pParent) {
			GroupLayout			myLayout;
			
			/* Record passed details */
			theParent = pParent;
			
			/* Create the Tabbed Pane */
			theTabs = new JTabbedPane();
		
			/* Create the Statement table and add to tabbed pane */
			theStatement = new StatementTable(this, false);
			theTabs.addTab(titleStatement, theStatement.getPanel());
			theTabs.addChangeListener(this);
											
			/* Create the optional tables */
			theUnits    = new StatementTable(this, true);
			thePatterns = new PatternsTable(this);
			theRates    = new RatesTable(this);
			thePrices   = new PricesTable(this);
											
			/* Create the Account selection panel */
			theSelect = new AccountSelection(theView, this, false);
				
			/* Create the table buttons */
			theTabButs   = new finUtils.Controls.TableButtons(this);
				
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
			    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                    .addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theTabs, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		                .addComponent(theSelect.getPanel())
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                .addComponent(theTabs)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theTabButs.getPanel())
		                .addContainerGap())
		    );
		}
	
		/* Change listener */
		public void stateChanged(ChangeEvent e) {
			Component myComponent = theTabs.getSelectedComponent();

			/* Note the last focus */
			theLastFocus = myComponent;
		}
		
		/* RefreshData */
		public void refreshData() {
			/* Refresh the account selection */
			theSelect.refreshData();
				
			/* Refresh the child tables */
			theRates.refreshData();
			thePrices.refreshData();
			thePatterns.refreshData();
			theUnits.refreshData();
			theStatement.refreshData();
				
			/* Redraw selection */
			setSelection(theSelect.getSelected());
		}
			
		/* Has this set of tables got updates */
		public boolean hasUpdates() {
			boolean hasUpdates = false;
			
			/* Determine whether we have updates */
			hasUpdates = theStatement.hasUpdates();
            if (!hasUpdates) hasUpdates = theUnits.hasUpdates();
            if (!hasUpdates) hasUpdates = thePatterns.hasUpdates();
            if (!hasUpdates) hasUpdates = theRates.hasUpdates();
            if (!hasUpdates) hasUpdates = thePrices.hasUpdates();
				
			/* Return to caller */
			return hasUpdates;
		}
			
		/* Has this set of tables got errors */
		public boolean hasErrors() {
			boolean hasErrors = false;
			
			/* Determine whether we have updates */
			hasErrors = theStatement.hasErrors() ||
						theUnits.hasErrors()     ||
						thePatterns.hasErrors()  ||
						theRates.hasErrors()     ||
						thePrices.hasErrors();
				
			/* Return to caller */
			return hasErrors;
		}
			
		/* Get the Edit State */
		public EditState getEditState() {
			EditState myState;
			EditState myNewState;

			/* Access the State of the Statement */
			myState = theStatement.getEditState();
			
			/* Access and combine the State of the Units */
			myNewState = theUnits.getEditState();
			myState    = finObject.editCombine(myState, myNewState);
			
			/* Access and combine the State of the Patterns */
			myNewState = thePatterns.getEditState();
			myState    = finObject.editCombine(myState, myNewState);
			
			/* Access and combine the State of the Rates */
			myNewState = theRates.getEditState();
			myState    = finObject.editCombine(myState, myNewState);
			
			/* Access and combine the State of the Prices */
			myNewState = thePrices.getEditState();
			myState    = finObject.editCombine(myState, myNewState);
			
			/* Return the state */
			return myState;
		}
			
		/* Perform command function */
		public void performCommand(tableCommand pCmd) {
			/* Cancel any editing */
			cancelEditing();
				
			/* Switch on command */
			switch (pCmd) {
				case OK:
					saveData();
					break;
				case RESETALL:
					resetData();
					break;
				case VALIDATEALL:
					validateAll();
					break;
			}
			notifyChanges();
		}
			
		/* is this table locked */
		public boolean isLocked() {
			/* State whether account is locked */
			return ((theAccount == null) || theAccount.isClosed());
		}
					
		/* validateAll */
		public void validateAll() {
			/* Validate the data */
			theStatement.validateAll();
			theUnits.validateAll();
			thePatterns.validateAll();
			theRates.validateAll();
			thePrices.validateAll();
			notifyChanges();
		}
			
		/* cancel editing */
		public void cancelEditing() {
			/* cancel editing */
			theStatement.cancelEditing();
			theUnits.cancelEditing();
			thePatterns.cancelEditing();
			theRates.cancelEditing();
			thePrices.cancelEditing();
		}
			
		/* resetData */
		public void resetData() {
			/* reset the data */
			theStatement.resetData();
			theUnits.resetData();
			thePatterns.resetData();
			theRates.resetData();
			thePrices.resetData();
		}
			
		/* saveData */
		public void saveData() {
			/* Validate the data */
			validateAll();
			if (!hasErrors()) {
				/* Save details for the account */
				if (theAccount != null)
					theAccount.getBase().applyChanges(theAccount);
				
				/* Save details for the Rates/Prices/Patterns */
				theRates.saveData();
				thePrices.saveData();
				thePatterns.saveData();
				
				/* Save the statement last since this will cascade data */
				if (theUnits.hasUpdates()) theUnits.saveData();
				else theStatement.saveData();
			}
		}
			
		/* Note that there has been a selection change */
		public void notifySelection(Object obj)    {
			/* If this is a change from the account selection */
			if (obj == (Object) theSelect) {
				/* Set the new account */
				setSelection(theSelect.getSelected());
			}
		}
			
		/* Note that changes have been made */
		public void notifyChanges() {
			/* Lock down the table buttons and the selection */
			theTabButs.setLockDown();
			theSelect.setLockDown();
			
			/* Adjust the visibility of the tabs */
			setVisibleTabs();
		}	
			
		/* Get Formatted Debug output */
		protected String getDebugText() {
			String				myText = "";
			tableList			myList;
			
			/* If the statement panel is active */
			if ((theLastFocus == null) ||
				(theLastFocus == theStatement.getPanel())) {
				/* Access the formatted output */
				myList = theStatement.getList();
				if (myList != null) myText = myList.toHTMLString().toString();
			}
			
			/* If the Units is active */
			else if (theLastFocus == theUnits.getPanel()) {
				/* Access the formatted output */
				myList = theUnits.getList();							
				if (myList != null) myText = myList.toHTMLString().toString();
			}
			
			/* If the rates is active */
			else if (theLastFocus == theRates.getPanel()) {
				/* Access the formatted output */
				myList = theRates.getList();			
				if (myList != null) myText = myList.toHTMLString().toString();
			}
			
			/* If the prices is active */
			else if (theLastFocus == thePrices.getPanel()) {
				/* Access the formatted output */
				myList = thePrices.getList();							
				if (myList != null) myText = myList.toHTMLString().toString();
			}
			
			/* If the patterns is active */
			else if (theLastFocus == thePatterns.getPanel()) {
				/* Access the formatted output */
				myList = thePatterns.getList();							
				if (myList != null) myText = myList.toHTMLString().toString();
			}
			
			/* Return to caller */
			return myText;
		}
		
		/* Set Selection */
		public void setSelection(finData.Account pAccount) {
			finData myData = theView.getData();
			
			/* Release old list */
			if (theAcList != null) theAcList.purge();
			
			/* Reset controls */
			theAcList  = null;
			theAccount = null;
			
			/* If we have a selected account */
			if (pAccount != null) {
				/* Create the edit account list */
				theAcList = myData.new AccountList(ListStyle.EDIT);
			
				/* Create an edit copy of the account */
				theAccount = myData.new Account(theAcList, pAccount);
			}
			
			/* Alert the different tables to the change */
			theStatement.setSelection(theAccount);
			theUnits.setSelection(theAccount);
			thePatterns.setSelection(theAccount);
			thePrices.setSelection(theAccount);
			theRates.setSelection(theAccount);
				
			/* Note the changes */
			notifyChanges();
		}
			
		/* Set visible tabs */
		private void setVisibleTabs() {
			int         iIndex;
			boolean     isPatternsSelected = false;
			boolean     isPricesSelected   = false;
			
			/* Access the Units index */
			iIndex = theTabs.indexOfTab(titleUnits);
			
			/* If the account has prices */
			if ((theAccount != null) &&
				(theAccount.isPriced())) {
				
				/* Add the UnitsStatement if not present */
				if (iIndex == -1) {
					theTabs.addTab(titleUnits, theUnits.getPanel());

					/* Remove the prices tab if present */
					iIndex = theTabs.indexOfTab(titlePrices);
					if (iIndex != -1) {
						/* Remember if Prices are selected since we need to restore this */
						if (iIndex == theTabs.getSelectedIndex())
							isPricesSelected = true;
					
						/* Remove the prices tab */
						theTabs.removeTabAt(iIndex);
					}
					
					/* Access the index of the Units tab */
					iIndex = theTabs.indexOfTab(titleUnits);
				}
				
				/* Disable the Units if statement has updates */
				theTabs.setEnabledAt(iIndex, !theStatement.hasUpdates());
				
				/* Access the Statements index */
				iIndex = theTabs.indexOfTab(titleStatement);
				
				/* Disable the Statement if units has updates */
				theTabs.setEnabledAt(iIndex, !theUnits.hasUpdates());
			}
				
			/* else if not units but tab is present */
			else if (iIndex != -1) {
				/* If the tab is selected then set statement as selected */ 
				if (iIndex == theTabs.getSelectedIndex())
					theTabs.setSelectedIndex(0);
				
				/* Remove the units tab */
				theTabs.removeTabAt(iIndex);
			}
			
			/* Access the Rates index */
			iIndex = theTabs.indexOfTab(titleRates);
			
			/* If the account has rates */
			if ((theAccount != null) &&
				(theAccount.isMoney())) {
				
				/* Add the Rates if not present */
				if (iIndex == -1) {
					theTabs.addTab(titleRates, theRates.getPanel());
				
					/* Remove the patterns tab if present */
					iIndex = theTabs.indexOfTab(titlePatterns);
					if (iIndex != -1) {
						/* Remember if Patterns are selected since we need to restore this */
						if ((iIndex == theTabs.getSelectedIndex()) &&
							(!isPricesSelected))
							isPatternsSelected = true;
				
						/* Remove the patterns tab */
						theTabs.removeTabAt(iIndex);
					}
				}
			}
				
			/* else if not rates but tab is present */
			else if (iIndex != -1) {
				/* If the tab is selected then set statement as selected */ 
				if (iIndex == theTabs.getSelectedIndex())
					theTabs.setSelectedIndex(0);
				
				/* Remove the units tab */
				theTabs.removeTabAt(iIndex);
			}
			
			/* Access the Prices index */
			iIndex = theTabs.indexOfTab(titlePrices);
			
			/* If the account has prices */
			if ((theAccount != null) &&
				(theAccount.isPriced())) {
				
				/* Add the Prices if not present */
				if (iIndex == -1) {
					theTabs.addTab(titlePrices, thePrices.getPanel());
									
					/* If the prices were selected  */
					if (isPricesSelected) {
						/* Re-select the prices */
						iIndex = theTabs.indexOfTab(titlePrices);
						theTabs.setSelectedIndex(iIndex);
					}
					
					/* Remove the patterns tab if present */
					iIndex = theTabs.indexOfTab(titlePatterns);
					if (iIndex != -1) {
						/* Remember if Patterns are selected since we need to restore this */
						if (iIndex == theTabs.getSelectedIndex())
							isPatternsSelected = true;
				
						/* Remove the patterns tab */
						theTabs.removeTabAt(iIndex);
					}
				}
			}
				
			/* else if not rates but tab is present */
			else if (iIndex != -1) {
				/* If the tab is selected then set statement as selected */ 
				if (iIndex == theTabs.getSelectedIndex())
					theTabs.setSelectedIndex(0);
				
				/* Remove the units tab */
				theTabs.removeTabAt(iIndex);
			}
		
			/* Access the Patterns index */
			iIndex = theTabs.indexOfTab(titlePatterns);
		
			/* If the account is not closed and is external/debt */
			if ((theAccount != null) &&
				(theAccount.isExternal() || theAccount.isDebt()) && 
				(!theAccount.isClosed())) {
			
				/* Add the Patterns if not present */
				if (iIndex == -1) {
					theTabs.addTab(titlePatterns, thePatterns.getPanel());
				
					/* If the patterns were selected  */
					if (isPatternsSelected) {
						/* Re-select the patterns */
						iIndex = theTabs.indexOfTab(titlePatterns);
						theTabs.setSelectedIndex(iIndex);
					}
				}
			}
			
			/* else if not patterned but tab is present */
			else if (iIndex != -1) {
				/* If the tab is selected then set statement as selected */ 
				if (iIndex == theTabs.getSelectedIndex())
					theTabs.setSelectedIndex(0);
				
				/* Remove the units tab */
				theTabs.removeTabAt(iIndex);
			}
			
			/* Update the top level tabs */
			theParent.setVisibleTabs();
		}
		
		/* Select an explicit account and period */
		public void selectAccount(finData.Account  pAccount,
								  DateSelection    pSource) {
			/* Adjust the date selection for the statements appropriately */
			theStatement.selectPeriod(pSource);
			theUnits.selectPeriod(pSource);
			
			/* Adjust the account selection */
			theSelect.setSelection(pAccount);
			
			/* Redraw selection */
			setSelection(theSelect.getSelected());
		}
		
		/* Add a pattern from a statement line */
		public void addPattern(finView.Statement.Line pLine) {
			/* Pass through to the Patterns table */
			thePatterns.addPattern(pLine);
			
			/* Change focus to the Patterns */
			gotoNamedTab(titlePatterns);
		}
		
		/* Goto the specific tab */
		public void gotoNamedTab(String pTabName) {
			/* Access the required index */
			int iIndex = theTabs.indexOfTab(pTabName);
		
			/* Select the required tab */
			theTabs.setSelectedIndex(iIndex);
		}
	}
			
	/* Prices table management */
	public class PricesTable extends financeBaseTable {
		/* Members */
		private static final long serialVersionUID = 1035380774297559650L;
		private finData.PriceList  				thePrices  	= null;
		private JPanel						 	thePanel	= null;
		private financeTable					theParent   = null;
		private finObject.Range					theRange	= null;
		private finData.Account                 theAccount  = null;
		private finView.PricesView				theExtract	= null;
		private finUtils.Controls.RowButtons    theRowButs  = null;
		private finUtils.DateUtil.Renderer 		theDateRenderer  = null;
		private finUtils.DateUtil.Editor 		theDateEditor    = null;
		private finUtils.PriceUtil.Renderer 	thePriceRenderer = null;
		private finUtils.PriceUtil.Editor 		thePriceEditor   = null;

		/* Access methods */
		public JPanel  getPanel()			{ return thePanel; }
			
		/* Hooks */
		public boolean needsMembers() 	{ return true; }
			
		/* Table headers */
		private static final String titleDate  = "Date";
		private static final String titlePrice = "Price";
			
		/* Table columns */
		private static final int COLUMN_DATE  = 0;
		private static final int COLUMN_PRICE = 1;
		private static final int NUM_COLUMNS  = 2;
						
		/* Constructor */
		public PricesTable(financeTable pParent) {
			TableColumnModel 			myColModel;
			TableColumn		 			myCol;
			JScrollPane		     		myScroll;
			GroupLayout		 	       	myLayout;
				
			/* Store details about the parent */
			theParent = pParent;
				
			/* Tell the superclass about us */
			super.theMaster = this;
			super.theModel  = new PricesModel();
				
			/* Set the table model */
			setModel(super.theModel);
				
			/* Access the column model */
			myColModel = getColumnModel();
				
			/* Create the relevant formatters/editors */
			theDateRenderer  = new finUtils.DateUtil.Renderer();
			theDateEditor    = new finUtils.DateUtil.Editor();
			thePriceRenderer = new finUtils.PriceUtil.Renderer();
			thePriceEditor   = new finUtils.PriceUtil.Editor();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_DATE);
			myCol.setCellRenderer(theDateRenderer);
			myCol.setCellEditor(theDateEditor);
			myCol.setPreferredWidth(80);
			
			myCol = myColModel.getColumn(COLUMN_PRICE);
			myCol.setCellRenderer(thePriceRenderer);
			myCol.setCellEditor(thePriceEditor);
			myCol.setPreferredWidth(90);
			
			getTableHeader().setReorderingAllowed(false);
			setAutoResizeMode(AUTO_RESIZE_OFF);
				
			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(800, 200));
			
			/* Create the sub panels */
			theRowButs   = new finUtils.Controls.RowButtons(this, InsertStyle.INSERT);
				
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
				
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
			    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		                .addComponent(myScroll)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theRowButs.getPanel())
		                .addContainerGap())
		    );
		}

		
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {
			/* If this is a change from the buttons */
			if (obj == (Object) theRowButs) {
				/* Set the correct show selected value */
				super.setShowDeleted(theRowButs.getShowDel());
			}
		}
			
		/* Refresh the data */
		public void refreshData() {			
			theRange = theView.getRange();
			theDateEditor.setRange(theRange);
		}
			
		/* saveData */
		public void saveData() {
			if (theExtract != null) {
				theExtract.applyChanges();
			}
		}
		
		/* Note that there has been a change */
		public void notifyChanges() {
			/* Update the row buttons */
			theRowButs.setLockDown();
			
			/* Find the edit state */
			if (thePrices != null)
				thePrices.findEditState();
			
			/* Update the parent panel */
			theParent.notifyChanges(); 
		}
			
		/* Set Selection */
		public void setSelection(finData.Account pAccount) {
			theExtract = theView.new PricesView(pAccount);
			theAccount = pAccount;
			thePrices  = theExtract.getPrices();
			super.setList(thePrices);
			super.theModel.fireTableDataChanged();
			theRowButs.setLockDown();
		}
			
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_DATE: 	return finData.Price.FIELD_DATE;
				case COLUMN_PRICE: 	return finData.Price.FIELD_PRICE;
				default:			return -1;
			}
		}
			
		/* Prices table model */
		public class PricesModel extends AbstractTableModel {
			private static final long serialVersionUID = -2613779599240142148L;

			/* get column count */
			public int getColumnCount() { return NUM_COLUMNS; }
				
			/* get row count */
			public int getRowCount() { 
				return (thePrices == null) ? 0
						                   : thePrices.countItems();
			}
				
			/* get column name */
			public String getColumnName(int col) {
				switch (col) {
					case COLUMN_DATE:  	return titleDate;
					case COLUMN_PRICE:	return titlePrice;
					default:			return null;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {				
				/* Locked if the account is closed */
				if (theAccount.isClosed()) return false;
				
				switch (col) {
					case COLUMN_DATE:  	return (theRange != null);
					case COLUMN_PRICE:	return true;
					default:			return true;
				}
			}
				
			/* get value At */
			public Object getValueAt(int row, int col) {
				finData.Price myPrice;
				Object		  o;
				
				/* Access the price */
				myPrice = thePrices.extractItemAt(row);
					
				/* Return the appropriate value */
				switch (col) {
					case COLUMN_DATE:  	o = myPrice.getDate();	break;
					case COLUMN_PRICE:	o = myPrice.getPrice();	break;
					default:			o = null;				break;
				}
				
				/* If we have a null value for an error field,  set error description */
				if ((o == null) && (myPrice.hasErrors(getFieldForCol(col))))
					o = finUtils.getError();
				
				/* Return to caller */
				return o;
			}
				
			/* set value At */
			public void setValueAt(Object obj, int row, int col) {
				finData.Price myPrice;
					
				/* Access the price */
				myPrice = thePrices.extractItemAt(row);
					
				/* Push history */
				myPrice.pushHistory();
				
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  	myPrice.setDate((finObject.Date)obj);  break;
					case COLUMN_PRICE:	myPrice.setPrice((finObject.Price)obj); break;
				}
					
				/* If we have changes */
				if (myPrice.checkForHistory()) {
					/* Set new state */
					myPrice.setState(State.CHANGED);
					thePrices.findEditState();
					
					/* If we may have re-sorted re-Draw the table */
					if (col == COLUMN_DATE) {
						myPrice.reSort();
						fireTableDataChanged();
						row = myPrice.indexOfItem();
						selectRow(row);
					}
					
					/* else note that we have updated this cell */
					else fireTableCellUpdated(row, col);
				
					/* Update components to reflect changes */
					notifyChanges();
				}
			}
		}			
	}
		
	/* Rates table management */
	public class RatesTable extends financeBaseTable implements ActionListener {
		/* Members */
		private static final long serialVersionUID = 36193763696660335L;
		private finData.RateList		    	theRates   	= null;
		private JPanel						 	thePanel	= null;
		private RatesTable						theTable    = this;
		private ratesMouse						theMouse	= null;
		private financeTable					theParent   = null;
		private finObject.Range					theRange	= null;
		private finData.Account                 theAccount  = null;
		private finView.RatesView				theExtract	= null;
		private finUtils.Controls.RowButtons    theRowButs  = null;
		private finUtils.DateUtil.Renderer 		theDateRenderer = null;
		private finUtils.DateUtil.Editor 		theDateEditor   = null;
		private finUtils.RateUtil.Renderer 		theRateRenderer = null;
		private finUtils.RateUtil.Editor 		theRateEditor   = null;
			
		/* Access methods */
		public JPanel  getPanel()			{ return thePanel; }

		/* Table headers */
		private static final String titleRate  = "Rate";
		private static final String titleBonus = "Bonus";
		private static final String titleDate  = "EndDate";
			
		/* Table columns */
		private static final int COLUMN_RATE  = 0;
		private static final int COLUMN_BONUS = 1;
		private static final int COLUMN_DATE  = 2;
		private static final int NUM_COLUMNS  = 3;
			
		/* Constructor */
		public RatesTable(financeTable pParent) {
			TableColumnModel 	myColModel;
			TableColumn		 	myCol;
			JScrollPane		 	myScroll;
			GroupLayout		 	myLayout;
			
			/* Store details about the parent */
			theParent = pParent;
				
			/* Tell the superclass about us */
			super.theMaster = this;
			super.theModel  = new RatesModel();
				
			/* Set the table model */
			setModel(super.theModel);
				
			/* Access the column model */
			myColModel = getColumnModel();
				
			/* Create the relevant formatters/editors */
			theDateRenderer = new finUtils.DateUtil.Renderer();
			theDateEditor   = new finUtils.DateUtil.Editor();
			theRateRenderer = new finUtils.RateUtil.Renderer();
			theRateEditor   = new finUtils.RateUtil.Editor();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_RATE);
			myCol.setCellRenderer(theRateRenderer);
			myCol.setCellEditor(theRateEditor);
			myCol.setPreferredWidth(90);
			
			myCol = myColModel.getColumn(COLUMN_BONUS);
			myCol.setCellRenderer(theRateRenderer);
			myCol.setCellEditor(theRateEditor);
			myCol.setPreferredWidth(90);
			
			myCol = myColModel.getColumn(COLUMN_DATE);
			myCol.setCellRenderer(theDateRenderer);
			myCol.setCellEditor(theDateEditor);
			myCol.setPreferredWidth(100);
			
			getTableHeader().setReorderingAllowed(false);
			setAutoResizeMode(AUTO_RESIZE_OFF);
				
			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(800, 200));
			
			/* Add the mouse listener */
			theMouse = new ratesMouse();
			addMouseListener(theMouse);
			
			/* Create the sub panels */
			theRowButs   = new finUtils.Controls.RowButtons(this, InsertStyle.INSERT);
			
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
				
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
			    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
	        myLayout.setVerticalGroup(
	       	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	       		.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		                .addComponent(myScroll)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theRowButs.getPanel())
		                .addContainerGap())
		    );
		}
			
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {
			/* If this is a change from the buttons */
			if (obj == (Object) theRowButs) {
				/* Set the correct show selected value */
				super.setShowDeleted(theRowButs.getShowDel());
			}
		}
			
		/* saveData */
		public void saveData() {
			if (theExtract != null) {
				theExtract.applyChanges();
			}
		}
		
		/* Refresh the data */
		public void refreshData() {			
			theRange = theView.getRange();
			theRange = new finObject.Range(theRange.getStart(), null);
			theDateEditor.setRange(theRange);
		}
			
		/* Note that there has been a change */
		public void notifyChanges() {
			/* Update the row buttons */
			theRowButs.setLockDown();
			
			/* Find the edit state */
			if (theRates != null)
				theRates.findEditState();
			
			/* Update the parent panel */
			theParent.notifyChanges(); 
		}
			
		/* Set Selection */
		public void setSelection(finData.Account pAccount) {
			theExtract = theView.new RatesView(pAccount);
			theAccount = pAccount;
			theRates   = theExtract.getRates();
			super.setList(theRates);
			super.theModel.fireTableDataChanged();
			theRowButs.setLockDown();
		}
			
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_RATE: 	return finData.Rate.FIELD_RATE;
				case COLUMN_BONUS:	return finData.Rate.FIELD_BONUS;
				case COLUMN_DATE:  	return finData.Rate.FIELD_ENDDATE;
				default:			return -1;
			}
		}
			
		/* action performed listener event */
		public void actionPerformed(ActionEvent evt) {
			String          myCmd;
			String          tokens[];
			String          myName = null;
			int             row;

			/* Access the action command */
			myCmd  = evt.getActionCommand();
			tokens = myCmd.split(":");
			myCmd  = tokens[0];
			if (tokens.length > 1) myName = tokens[1];
			
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
			
			/* If this is a set Null Date request */
			if (myCmd.compareTo(ratesMouse.popupSetNullDate) == 0) {
				/* Access the correct row */
				row = Integer.parseInt(myName);
			
				/* set the null value */
				super.theModel.setValueAt(null, row, COLUMN_DATE);
				super.theModel.fireTableCellUpdated(row, COLUMN_DATE);
			}
			
			/* If this is a set Null Bonus request */
			else if (myCmd.compareTo(ratesMouse.popupSetNullBonus) == 0) {
				/* Access the correct row */
				row = Integer.parseInt(myName);
			
				/* set the null value */
				super.theModel.setValueAt(null, row, COLUMN_BONUS);
				super.theModel.fireTableCellUpdated(row, COLUMN_BONUS);
			}
		}
			
		/* Rates table model */
		public class RatesModel extends AbstractTableModel {
			private static final long serialVersionUID = 296797947278000196L;

			/* get column count */
			public int getColumnCount() { return NUM_COLUMNS; }
				
			/* get row count */
			public int getRowCount() { 
				return (theRates == null) ? 0
						                  : theRates.countItems();
			}
				
			/* get column name */
			public String getColumnName(int col) {
				switch (col) {
					case COLUMN_RATE:  	return titleRate;
					case COLUMN_BONUS:  return titleBonus;
					case COLUMN_DATE:	return titleDate;
					default:			return null;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {				
				/* Locked if the account is closed */
				if (theAccount.isClosed()) return false;
				
				/* Otherwise edit-able */
				return true;
			}
				
			/* get value At */
			public Object getValueAt(int row, int col) {
				finData.Rate myRate;
				Object       o;
				
				/* Access the rate */
				myRate = theRates.extractItemAt(row);
				
				/* Return the appropriate value */
				switch (col) {
					case COLUMN_RATE:  	o = myRate.getRate();		break;
					case COLUMN_BONUS:	o = myRate.getBonus();		break;
					case COLUMN_DATE:	o = myRate.getEndDate();	break;
					default:			o = null; 					break;
				}
				
				/* If we have a null value for an error field,  set error description */
				if ((o == null) && (myRate.hasErrors(getFieldForCol(col))))
					o = finUtils.getError();
				
				/* Return to caller */
				return o;
			}
				
			/* set value At */
			public void setValueAt(Object obj, int row, int col) {
				finData.Rate myRate;
				
				/* Access the rate */
				myRate = theRates.extractItemAt(row);
				
				/* Push history */
				myRate.pushHistory();
				
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_RATE:  	myRate.setRate((finObject.Rate)obj);  break;
					case COLUMN_BONUS:  myRate.setBonus((finObject.Rate)obj); break;
					case COLUMN_DATE:	myRate.setEndDate((finObject.Date)obj);  break;
				}
					
				/* reset history if no change */
				if (myRate.checkForHistory()) {
					/* Set changed status */
					myRate.setState(State.CHANGED);
					theRates.findEditState();
				
					/* If we may have re-sorted re-Draw the table */
					if (col == COLUMN_DATE) {
						myRate.reSort();
						fireTableDataChanged();
						row = myRate.indexOfItem();
						selectRow(row);
					}
				
					/* else note that we have updated this cell */
					else fireTableCellUpdated(row, col);
				
					/* Update components to reflect changes */
					notifyChanges();
				}
			}
		}
		
		/* Rates mouse listener */
		public class ratesMouse extends MouseAdapter {
				
			/* Pop-up Menu items */
			private static final String popupSetNullDate  = "Set Null Date";
			private static final String popupSetNullBonus = "Set Null Bonus";
				
			/* handle mouse Pressed event */
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			/* handle mouse Released event */
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
				
			/* Maybe show the pop-up */
			public void maybeShowPopup(MouseEvent e) {
				JPopupMenu      myMenu;
				JMenuItem       myItem;
				finData.Rate    myRow     = null;
				finData.Rate    myCurr;
				boolean         isBonus   = false;
				boolean         isDate    = false;
					
				if (e.isPopupTrigger() && 
					(theTable.isEnabled())) {
					/* Calculate the row/column that the mouse was clicked at */
					Point p = new Point(e.getX(), e.getY());
					int row = theTable.rowAtPoint(p);
					int col = theTable.columnAtPoint(p);
					
					/* Access the row */
					myRow = theRates.extractItemAt(row);
						
					/* Determine column */
					if (col == COLUMN_DATE)
						isDate = true;
					else if (col == COLUMN_BONUS)
						isBonus = true;
					
					/* If we are pointing to date then determine whether we can set null */
					if (isDate) {
						/* Handle null date */
						if ((myRow.isLocked()) ||
						    (myRow.getDate() == null) ||
						    (myRow.getDate().isNull()))
							isDate = false;
						
						/* Loop to the next valid element */
						for (myCurr = myRow.getNext();
						     (myCurr != null) && (myCurr.isDeleted());
						     myCurr = myCurr.getNext()){}
						
						/* Handle not last */
						if (myCurr != null) isDate = false;
					}
					
					/* If we are pointing to date then determine whether we can set null */
					if ((isBonus) && 
						((myRow.isLocked()) ||
						 (myRow.getBonus() == null)))
						isBonus = false;
					
					/* If we can set null value */
					if ((isBonus) || (isDate)) {
						/* Create the pop-up menu */
						myMenu = new JPopupMenu();
						
						/* If we have date */
						if (isDate) {
							/* Create the View account choice */
							myItem = new JMenuItem(popupSetNullDate);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNullDate + ":" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have bonus */
						if (isBonus) {
							/* Create the View account choice */
							myItem = new JMenuItem(popupSetNullBonus);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNullBonus + ":" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* Show the pop-up menu */
						myMenu.show(e.getComponent(),
									e.getX(), e.getY());
					}					
				}
			}
		}
	}
		
	/* Patterns table management */
	public class PatternsTable extends financeBaseTable {
		/* Members */
		private static final long serialVersionUID = 1968946370981616222L;
		private finData.PatternList  			thePatterns = null;
		private JPanel						 	thePanel	= null;
		private financeTable					theParent   = null;
		private finUtils.Controls.RowButtons    theRowButs  = null;
		private finData.Account                 theAccount  = null;
		private finView.PatternsView			theExtract	= null;
		private finUtils.DateUtil.Renderer 		theDateRenderer   = null;
		private finUtils.DateUtil.Editor 		theDateEditor     = null;
		private finUtils.MoneyUtil.Renderer 	theMoneyRenderer  = null;
		private finUtils.MoneyUtil.Editor 		theMoneyEditor    = null;
		private finUtils.StringUtil.Renderer 	theStringRenderer = null;
		private finUtils.StringUtil.Editor 		theStringEditor   = null;
		private finUtils.ComboUtil.Editor 		theComboEditor    = null;

		/* Access methods */
		public JPanel  getPanel()			{ return thePanel; }
		public boolean hasCreditChoice() 	{ return true; }
			
		/* Table headers */
		private static final String titleDate    = "Date";
		private static final String titleDesc    = "Description";
		private static final String titleTrans   = "TransactionType";
		private static final String titlePartner = "Partner";
		private static final String titleCredit  = "Credit";
		private static final String titleDebit   = "Debit";
		private static final String titleFreq    = "Frequency";
			
		/* Table columns */
		private static final int COLUMN_DATE 	 = 0;
		private static final int COLUMN_DESC 	 = 1;
		private static final int COLUMN_TRANTYP  = 2;
		private static final int COLUMN_PARTNER	 = 3;
		private static final int COLUMN_CREDIT	 = 4;
		private static final int COLUMN_DEBIT 	 = 5;
		private static final int COLUMN_FREQ 	 = 6;
		private static final int NUM_COLUMNS	 = 7;
					
		/* Constructor */
		public PatternsTable(financeTable pParent) {
			TableColumnModel    myColModel;
			TableColumn			myCol;
			JScrollPane		 	myScroll;
			GroupLayout		 	myLayout;
				
			/* Store details about the parent */
			theParent = pParent;
			
			/* Tell the superclass about us */
			super.theMaster = this;
			super.theModel  = new PatternsModel();
				
			/* Set the table model */
			setModel(super.theModel);
			
			/* Access the column model */
			myColModel = getColumnModel();
				
			/* Create the relevant formatters/editors */
			theDateRenderer   = new finUtils.DateUtil.Renderer();
			theDateEditor     = new finUtils.DateUtil.Editor();
			theMoneyRenderer  = new finUtils.MoneyUtil.Renderer();
			theMoneyEditor    = new finUtils.MoneyUtil.Editor();
			theStringRenderer = new finUtils.StringUtil.Renderer();
			theStringEditor   = new finUtils.StringUtil.Editor();
			theComboEditor    = new finUtils.ComboUtil.Editor();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_DATE);
			myCol.setCellRenderer(theDateRenderer);
			myCol.setCellEditor(theDateEditor);
			myCol.setPreferredWidth(80);
			
			myCol = myColModel.getColumn(COLUMN_DESC);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theStringEditor);
			myCol.setPreferredWidth(150);
			
			myCol = myColModel.getColumn(COLUMN_TRANTYP);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(110);
			
			myCol = myColModel.getColumn(COLUMN_PARTNER);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(130);
			
			myCol = myColModel.getColumn(COLUMN_CREDIT);
			myCol.setCellRenderer(theMoneyRenderer);
			myCol.setCellEditor(theMoneyEditor);
			myCol.setPreferredWidth(90);
			
			myCol = myColModel.getColumn(COLUMN_DEBIT);
			myCol.setCellRenderer(theMoneyRenderer);
			myCol.setCellEditor(theMoneyEditor);
			myCol.setPreferredWidth(90);
			
			myCol = myColModel.getColumn(COLUMN_FREQ);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(110);
			
			getTableHeader().setReorderingAllowed(false);
			
			/* Set the date editor to show no years */
			theDateEditor.setNoYear();
			
			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(900, 200));

			/* Create the sub panels */
			theRowButs   = new finUtils.Controls.RowButtons(this, InsertStyle.NONE);
				
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
				
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
			    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
		                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		                .addComponent(myScroll)
		                .addComponent(theRowButs.getPanel()))
		    );
		}
			
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {
			/* If this is a change from the buttons */
			if (obj == (Object) theRowButs) {
				/* Set the correct show selected value */
				super.setShowDeleted(theRowButs.getShowDel());
			}
		}
			
		/* refresh data */
		public void refreshData() {
		}
		
		/* saveData */
		public void saveData() {
			if (theExtract != null) {
				theExtract.applyChanges();
			}
		}
		
		/* Note that there has been a list selection change */
		public void notifyChanges() {
			/* Update the row buttons */
			theRowButs.setLockDown();
			
			/* Find the edit state */
			if (thePatterns != null)
				thePatterns.findEditState();
			
			/* Update the parent panel */
			theParent.notifyChanges(); 
		}
		
		/* Set Selection */
		public void setSelection(finData.Account pAccount) {
			theExtract  = theView.new PatternsView(pAccount);
			theAccount  = pAccount;
			thePatterns = theExtract.getPatterns();
			super.setList(thePatterns);
			super.theModel.fireTableDataChanged();
			theRowButs.setLockDown();
		}
			
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_DATE: 		return finData.Pattern.FIELD_DATE;
				case COLUMN_DESC:		return finData.Pattern.FIELD_DESC;
				case COLUMN_TRANTYP:	return finData.Pattern.FIELD_TRNTYP;
				case COLUMN_CREDIT: 	return finData.Pattern.FIELD_AMOUNT;
				case COLUMN_DEBIT: 		return finData.Pattern.FIELD_AMOUNT;
				case COLUMN_PARTNER:	return finData.Pattern.FIELD_PARTNER;
				case COLUMN_FREQ:  		return finData.Pattern.FIELD_FREQ;
				default:				return -1;
			}
		}
			
		/* Get combo box for cell */
		public JComboBox getComboBox(int row, int column) {
			finData.Pattern 	myPattern;
			ComboSelect.Item    mySelect;
			
			/* Access the pattern */
			myPattern = thePatterns.extractItemAt(row);

			/* Switch on column */
			switch (column) {
				case COLUMN_FREQ:	
					return theFreqBox;
				case COLUMN_TRANTYP:		
					mySelect = theComboList.searchFor(myPattern.getActType());
					return (myPattern.isCredit()) ? mySelect.getCredit()
							                      : mySelect.getDebit();
				case COLUMN_PARTNER:
					mySelect = theComboList.searchFor(myPattern.getTransType());
					return (myPattern.isCredit()) ? mySelect.getDebit()
							                      : mySelect.getCredit();
				default: 				
					return null;
			}
		}
			
		/* Add a pattern based on a statement line */
		public void addPattern(finView.Statement.Line pLine) {
			finData.Pattern myPattern;
			
			/* Create the new Item */
			myPattern = theView.getData().new Pattern(thePatterns, pLine);
			myPattern.addToList();
		
			/* Note the changes */
			notifyChanges();
			
			/* Notify of the insertion of the column */
			super.theModel.fireTableDataChanged();
		}
		
		/* Patterns table model */
		public class PatternsModel extends AbstractTableModel {
			private static final long serialVersionUID = -8445100544184045930L;

			/* get column count */
			public int getColumnCount() { return NUM_COLUMNS; }
			
			/* get row count */
			public int getRowCount() { 
				return (thePatterns == null) ? 0
						                     : thePatterns.countItems();
			}
				
			/* get column name */
			public String getColumnName(int col) {
				switch (col) {
					case COLUMN_DATE:  		return titleDate;
					case COLUMN_DESC:  		return titleDesc;
					case COLUMN_TRANTYP:  	return titleTrans;
					case COLUMN_PARTNER:  	return titlePartner;
					case COLUMN_CREDIT:  	return titleCredit;
					case COLUMN_DEBIT:	 	return titleDebit;
					case COLUMN_FREQ:		return titleFreq;
					default:				return null;
				}
			}
				
			/* is get column class */
			public Class<?> getColumnClass(int col) {				
				switch (col) {
					case COLUMN_DESC:  		return String.class;
					case COLUMN_TRANTYP:  	return String.class;
					case COLUMN_PARTNER:  	return String.class;
					case COLUMN_CREDIT:  	return String.class;
					case COLUMN_DEBIT:  	return String.class;
					case COLUMN_FREQ:  		return String.class;
					default: 				return Object.class;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {
				finData.Pattern myPattern;
				
				/* If the account is not editable */
				if (theAccount.isLocked()) return false;
				
				/* Access the pattern */
				myPattern = thePatterns.extractItemAt(row);
				
				/* Cannot edit if row is deleted or locked */
				if (myPattern.isDeleted() || myPattern.isLocked())
					return false;
				
				switch (col) {
					case COLUMN_CREDIT:		return myPattern.isCredit();
					case COLUMN_DEBIT:		return !myPattern.isCredit();
					default: return true;
				}
			}
				
			/* get value At */
			public Object getValueAt(int row, int col) {
				finData.Pattern myPattern;
				Object          o;
					
				/* Access the pattern */
				myPattern = thePatterns.extractItemAt(row);
					
				/* Return the appropriate value */
				switch (col) {
					case COLUMN_DATE:  		
						o = myPattern.getDate();
						break;
					case COLUMN_DESC:	 	
						o = myPattern.getDesc();
						if ((o != null) & (((String)o).length() == 0))
							o = null;
						break;
					case COLUMN_TRANTYP:  	
						o = (myPattern.getTransType() == null) 
									? null : myPattern.getTransType().getName();
						break;
					case COLUMN_PARTNER:
						o = (myPattern.getPartner() == null) 
									? null : myPattern.getPartner().getName();
						break;
					case COLUMN_CREDIT:  	
						o = (myPattern.isCredit()) ? myPattern.getAmount()
						   					  	   : null;
						break;
					case COLUMN_DEBIT:
						o =(!myPattern.isCredit()) ? myPattern.getAmount()
												   : null;
						break;
					case COLUMN_FREQ:		
						o = (myPattern.getFrequency() == null) 
									? null : myPattern.getFrequency().getName();
						break;
					default:				
						o = null;
						break;
				}
				
				/* If we have a null value for an error field,  set error description */
				if ((o == null) && (myPattern.hasErrors(getFieldForCol(col))))
					o = finUtils.getError();
				
				/* Return to caller */
				return o;
			}
				
			/* set value At */
			public void setValueAt(Object obj, int row, int col) {
				finData.Pattern myPattern;
				
				/* Access the pattern */
				myPattern = thePatterns.extractItemAt(row);
					
				/* Push history */
				myPattern.pushHistory();
				
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  
						myPattern.setDate((finObject.Date)obj);    
						break;
					case COLUMN_DESC:  
						myPattern.setDesc((String)obj);            
						break;
					case COLUMN_TRANTYP:  
						myPattern.setTransType(theTranTyps.searchFor((String)obj));    
						break;
					case COLUMN_CREDIT:
					case COLUMN_DEBIT:
						myPattern.setAmount((finObject.Money)obj); 
						break;
					case COLUMN_PARTNER:  
						myPattern.setPartner(theAccounts.searchFor((String)obj));    
						break;
					case COLUMN_FREQ:
					default: 
						myPattern.setFrequency(theFreqs.searchFor((String)obj));    
						break;
				}
					
				/* Check for changes */
				if (myPattern.checkForHistory()) {
					/* Note that the item has changed */
					myPattern.setState(State.CHANGED);
					thePatterns.findEditState();
					
					/* Switch on the updated column */
					switch (col) {
						/* redraw whole table if we have updated a sort col */
						case COLUMN_DATE:
						case COLUMN_DESC:
						case COLUMN_TRANTYP: 
							myPattern.reSort();
							fireTableDataChanged();
							row = myPattern.indexOfItem();
							selectRow(row);
							break;
							
						/* else note that we have updated this cell */
						default:
							fireTableCellUpdated(row, col);
							break;
					}
				
					/* Update components to reflect changes */
					notifyChanges();
				}
			}
		}
	}
		
	/* Statement table management */
	public class StatementTable extends financeBaseTable implements ActionListener {
		/* Members */
		private static final long serialVersionUID = -9123840084764342499L;
		private finData.Account					theAccount   = null;
		private finView.Statement            	theStatement = null;
		private finView.Statement.List  	 	theLines 	 = null;
		private JPanel						 	thePanel	 = null;
		private StatementTable				 	theTable	 = this;
		private statementMouse					theMouse	 = null;
		private financeTable					theParent    = null;
		private finObject.Range					theRange	 = null;
		private finUtils.Controls.DateSelection theSelect	 = null;
		private finUtils.Controls.RowButtons    theRowButs   = null;
		private finUtils.DateUtil.Renderer 		theDateRenderer   = null;
		private finUtils.DateUtil.Editor 		theDateEditor     = null;
		private finUtils.MoneyUtil.Renderer 	theMoneyRenderer  = null;
		private finUtils.MoneyUtil.Editor 		theMoneyEditor    = null;
		private finUtils.UnitsUtil.Renderer 	theUnitsRenderer  = null;
		private finUtils.UnitsUtil.Editor 		theUnitsEditor    = null;
		private finUtils.StringUtil.Renderer 	theStringRenderer = null;
		private finUtils.StringUtil.Editor 		theStringEditor   = null;
		private finUtils.ComboUtil.Editor 		theComboEditor    = null;
		private boolean							hasBalance		  = true;
		private TableColumn						theBalanceCol	  = null;
		private boolean							isUnits			  = false;

		/* Access methods */
		public boolean hasHeader()		 	{ return true; }
		public boolean hasCreditChoice() 	{ return true; }
		public JPanel  getPanel()			{ return thePanel; }

		/* Table headers */
		private static final String titleDate    = "Date";
		private static final String titleDesc    = "Description";
		private static final String titleTrans   = "TransactionType";
		private static final String titlePartner = "Partner";
		private static final String titleCredit  = "Credit";
		private static final String titleDebit   = "Debit";
		private static final String titleBalance = "Balance";
		
		/* Table columns */
		private static final int COLUMN_DATE 	 = 0;
		private static final int COLUMN_DESC 	 = 1;
		private static final int COLUMN_TRANTYP  = 2;
		private static final int COLUMN_PARTNER	 = 3;
		private static final int COLUMN_CREDIT	 = 4;
		private static final int COLUMN_DEBIT 	 = 5;
		private static final int COLUMN_BALANCE	 = 6;
		private static final int NUM_COLUMNS	 = 7;
					
		/* Constructor */
		public StatementTable(financeTable pMaster, boolean isUnits) {
			TableColumnModel myColModel;
			TableColumn		 myCol;
			JScrollPane		 myScroll;
			GroupLayout		 myLayout;
			
			/* Store passed details */
			theParent    = pMaster;
			this.isUnits = isUnits;
			
			/* Tell the superclass about us */
			super.theMaster = this;
			super.theModel  = new StatementModel();
			
			/* Set the table model */
			setModel(super.theModel);
			
			/* Access the column model */
			myColModel = getColumnModel();
			
			/* Create the relevant formatters/editors */
			theDateRenderer   = new finUtils.DateUtil.Renderer();
			theDateEditor     = new finUtils.DateUtil.Editor();
			theMoneyRenderer  = new finUtils.MoneyUtil.Renderer();
			theMoneyEditor    = new finUtils.MoneyUtil.Editor();
			theUnitsRenderer  = new finUtils.UnitsUtil.Renderer();
			theUnitsEditor    = new finUtils.UnitsUtil.Editor();
			theStringRenderer = new finUtils.StringUtil.Renderer();
			theStringEditor   = new finUtils.StringUtil.Editor();
			theComboEditor    = new finUtils.ComboUtil.Editor();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_DATE);
			myCol.setCellRenderer(theDateRenderer);
			myCol.setCellEditor(theDateEditor);
			myCol.setPreferredWidth(80);
			
			myCol = myColModel.getColumn(COLUMN_DESC); 
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theStringEditor);
			myCol.setPreferredWidth(150);
			
			myCol =	myColModel.getColumn(COLUMN_TRANTYP); 
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(110);
			
			myCol = myColModel.getColumn(COLUMN_PARTNER); 
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(130);
			
			myCol = myColModel.getColumn(COLUMN_CREDIT); 
			myCol.setCellRenderer((isUnits) ? theUnitsRenderer : theMoneyRenderer);
			myCol.setCellEditor((isUnits) ? theUnitsEditor : theMoneyEditor);
			myCol.setPreferredWidth(90);
			
			myCol = myColModel.getColumn(COLUMN_DEBIT);
			myCol.setCellRenderer((isUnits) ? theUnitsRenderer : theMoneyRenderer);
			myCol.setCellEditor((isUnits) ? theUnitsEditor : theMoneyEditor);
			myCol.setPreferredWidth(90);
			
			myCol = myColModel.getColumn(COLUMN_BALANCE);
			myCol.setCellRenderer((isUnits) ? theUnitsRenderer : theMoneyRenderer);
			myCol.setPreferredWidth(90);
			theBalanceCol = myCol;
			
			getTableHeader().setReorderingAllowed(false);
				
			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(900, 200));
			
			/* Add the mouse listener */
			theMouse = new statementMouse();
			addMouseListener(theMouse);
			
			/* Create the sub panels */
			theSelect    = new finUtils.Controls.DateSelection(this);
			theRowButs   = new finUtils.Controls.RowButtons(this, InsertStyle.CREDITDEBIT);
			
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
			
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
			    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                	.addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
		                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		        		.addComponent(theSelect.getPanel())
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                .addComponent(myScroll)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theRowButs.getPanel())
		                .addContainerGap())
		    );
		}

		/* Calculate table */
		public boolean calculateTable() {
			/* Reset the balance */
			if (theStatement != null) 
				theStatement.resetBalance();
			
			/* Return that table refresh is required */
			return true;
		}
		
		/* saveData */
		public void saveData() {
			if (theStatement != null) {
				theStatement.applyChanges();
			}
		}
		
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {
			/* If this is a change from the buttons */
			if (obj == (Object) theRowButs) {
				/* Set the correct show selected value */
				super.setShowDeleted(theRowButs.getShowDel());
			}
			
			/* else if this is a change from the range */
			else if (obj == (Object) theSelect) {
				/* Set the new range */
				setSelection(theSelect.getRange());
			}
		}
			
		/* refresh data */
		public void refreshData() {
			finObject.Range myRange = theView.getRange();
			theSelect.setRange(myRange);
		}
		
		/* Set Selection */
		public void setSelection(finData.Account pAccount) {
			theRange     = theSelect.getRange();
			theDateEditor.setRange(theRange);
			theAccount   = pAccount;
			if (theAccount != null) {
				theStatement = theView.new Statement(pAccount, theRange, isUnits);
				theLines     = theStatement.getLines();
				if ((hasBalance) && 
					(!theStatement.hasBalance()) && 
					(!isUnits)) {
					hasBalance = false;
					removeColumn(theBalanceCol);
				}
				else if ((!hasBalance) && 
						 ((theStatement.hasBalance()) ||
						  (isUnits))) {
					hasBalance = true;
					addColumn(theBalanceCol);
				}
			}
			else {
				theStatement = null;
				theLines     = null;
			}
			super.setList(theLines);
			super.theModel.fireTableDataChanged();
			theRowButs.setLockDown();
			theSelect.setLockDown();
		}
			
		/* Note that there has been a list selection change */
		public void notifyChanges() {
			/* Update the row buttons */
			theRowButs.setLockDown();
			
			/* Find the edit state */
			if (theLines != null)
				theLines.findEditState();
			
			/* Update the parent panel */
			theParent.notifyChanges(); 
		}
			
		/* Set Selection */
		public void setSelection(finObject.Range pRange) {
			if (theAccount != null) {
				theStatement = theView.new Statement(theAccount, pRange, isUnits);
				theLines     = theStatement.getLines();
				if ((hasBalance) && 
					(!theStatement.hasBalance()) && 
					(!isUnits)) {
					hasBalance = false;
					removeColumn(theBalanceCol);
				}
				else if ((!hasBalance) && 
						 ((theStatement.hasBalance()) ||
						  (isUnits))) {
					hasBalance = true;
					addColumn(theBalanceCol);
				}
			}
			else {
				theStatement = null;
				theLines     = null;
			}
			theRange = pRange;
			theDateEditor.setRange(theRange);
			super.setList(theLines);
			super.theModel.fireTableDataChanged();
			theRowButs.setLockDown();
			theSelect.setLockDown();
		}
			
		/* Select an explicit period */
		public void selectPeriod(DateSelection    pSource) {
			/* Adjust the period selection */
			theSelect.setSelection(pSource);
		}
		
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_DATE: 		return finView.Statement.Line.FIELD_DATE;
				case COLUMN_DESC:		return finView.Statement.Line.FIELD_DESC;
				case COLUMN_TRANTYP:	return finView.Statement.Line.FIELD_TRNTYP;
				case COLUMN_PARTNER:	return finView.Statement.Line.FIELD_PARTNER;
				case COLUMN_CREDIT:		return finView.Statement.Line.FIELD_AMOUNT;
				case COLUMN_DEBIT:		return finView.Statement.Line.FIELD_AMOUNT;
				default:				return -1; 
			}
		}
			
		/* Get combo box for cell */
		public JComboBox getComboBox(int row, int column) {
			finView.Statement.Line myLine;
			ComboSelect.Item       mySelect;
			
			/* Access the line */
			myLine = theLines.extractItemAt(row-1);

			/* Switch on column */
			switch (column) {
				case COLUMN_TRANTYP:		
					mySelect = theComboList.searchFor(theStatement.getActType());
					return (myLine.isCredit()) ? mySelect.getCredit()
							                   : mySelect.getDebit();
				case COLUMN_PARTNER:
					mySelect = theComboList.searchFor(myLine.getTransType());
					return (myLine.isCredit()) ? mySelect.getDebit()
							                   : mySelect.getCredit();
				default: 				
					return null;
			}
		}
			
		/* action performed listener event */
		public void actionPerformed(ActionEvent evt) {
			String                  myCmd;
			String                  tokens[];
			int                     row = 0;
			finView.Statement.Line  myRow;

			/* Access the action command */
			myCmd  = evt.getActionCommand();
			tokens = myCmd.split(":");
			myCmd  = tokens[0];
			if (tokens.length > 1) row = Integer.parseInt(tokens[1]);
			
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
			
			/* Handle commands */
			if (myCmd.compareTo(statementMouse.popupExtract) == 0)
				theTopWindow.selectPeriod(theSelect);
			else if (myCmd.compareTo(statementMouse.popupMaint) == 0)
				theTopWindow.selectAccountMaint(theAccount);
			else if (myCmd.compareTo(statementMouse.popupParent) == 0)
				theTopWindow.selectAccount(theAccount.getParent(), theSelect);
			else if (myCmd.compareTo(statementMouse.popupMParent) == 0)
				theTopWindow.selectAccountMaint(theAccount.getParent());
			else if (myCmd.compareTo(statementMouse.popupPartner) == 0) {
				myRow = theLines.extractItemAt(row);
				theTopWindow.selectAccount(myRow.getPartner(), theSelect);
			}
			else if (myCmd.compareTo(statementMouse.popupMPartner) == 0) {
				myRow = theLines.extractItemAt(row);
				theTopWindow.selectAccountMaint(myRow.getPartner());
			}
			else if (myCmd.compareTo(statementMouse.popupPattern) == 0) {
				myRow = theLines.extractItemAt(row);
				((AccountControl)theParent).addPattern(myRow);
			}
		}
			
		/* Check whether this is a valid Object for selection */
		public boolean isValidObj(finLink.itemElement pItem,
				  				  finLink.histObject  pObj) {
			finView.Statement.Line 	myLine;
			finView.LineValues		myLineVals; 
			finData.Event		   	myEvent;
			finData.EventValues    	myEventVals;
			finObject.Date			myDate;
			boolean					isCredit;
			finData.Account			myPartner;			
			finData.Account			mySelf;
			
			/* If this is an Event item */
			if (pItem instanceof finData.Event) {
				/* Access the element and the values */
				myEvent     = (finData.Event)pItem;
				myEventVals = (finData.EventValues) pObj;
				
				/* Access the values */
				myDate    = myEventVals.getDate();
				isCredit  = finObject.differs(myEvent.getDebit(), theAccount);
				myPartner = (isCredit) ? myEventVals.getDebit()
									   : myEventVals.getCredit();
				mySelf 	  = (isCredit) ? myEventVals.getCredit()
									   : myEventVals.getDebit(); 
			}
			
			/* else it is a line item */
			else {
				/* Access the element and the values */
				myLine     = (finView.Statement.Line)pItem;
				myLineVals = (finView.LineValues) pObj;
				
				/* Access the values */
				myDate    = myLineVals.getDate();
				isCredit  = myLine.isCredit();
				myPartner = myLineVals.getPartner();
				mySelf	  = theAccount;
			}
			
			/* Check whether the date is in range */
			if (theRange.compareTo(myDate) != 0)
				return false;

			/* Check whether the partner account is Locked/Deleted */
			if ((myPartner.isLocked()) ||
				(myPartner.isDeleted()))
				return false;
			
			/* Check that line is still related */
			if (finObject.differs(mySelf, theAccount)) 
				return false;
			
			/* Otherwise OK */
			return true;
		}
			
		/* Statement table model */
		public class StatementModel extends AbstractTableModel {
			private static final long serialVersionUID = 269477444398236458L;
				/* get column count */

			public int getColumnCount() { return (hasBalance) ? NUM_COLUMNS : NUM_COLUMNS-1; }
				
			/* get row count */
			public int getRowCount() { 
				return (theLines == null) ? 0
						                  : theLines.countItems() + 1;
			}
			
			/* get column name */
			public String getColumnName(int col) {
				switch (col) {
					case COLUMN_DATE:  		return titleDate;
					case COLUMN_DESC:  		return titleDesc;
					case COLUMN_TRANTYP:  	return titleTrans;
					case COLUMN_PARTNER:  	return titlePartner;
					case COLUMN_CREDIT:  	return titleCredit;
					case COLUMN_DEBIT:	 	return titleDebit;
					case COLUMN_BALANCE:	return titleBalance;
					default:				return null;
				}
			}
				
			/* is get column class */
			public Class<?> getColumnClass(int col) {				
				switch (col) {
					case COLUMN_DESC:  		return String.class;
					case COLUMN_TRANTYP:  	return String.class;
					case COLUMN_PARTNER:  	return String.class;
					default: 				return Object.class;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {
				finView.Statement.Line myLine;
				
				/* Locked if the account is closed */
				if (theStatement.getAccount().isClosed()) return false;
				
				/* Lock the start balance */
				if (row == 0) return false;
				
				/* Access the line */
				myLine = theLines.extractItemAt(row-1);
				
				/* Cannot edit if row is deleted or locked */
				if (myLine.isDeleted() || myLine.isLocked())
					return false;
				
				/* switch on column */
				switch (col) {
					case COLUMN_BALANCE:
						return false;
					case COLUMN_DATE:
						return true;
					case COLUMN_DESC:
						return (myLine.getDate() != null);
					case COLUMN_TRANTYP:
						return ((myLine.getDate() != null) &&
								(myLine.getDesc() != null));
					default:
						if ((myLine.getDate() == null) &&
							(myLine.getDesc() == null) &&
							(myLine.getTransType() == null))
							return false;
						
						/* Handle columns */
						switch (col) {
							case COLUMN_CREDIT:		return myLine.isCredit();
							case COLUMN_DEBIT:		return !myLine.isCredit();
							default: 				return true;
						}
				}
				
			}
				
			/* get value At */
			public Object getValueAt(int row, int col) {
				finView.Statement.Line myLine;
				finView.Statement.Line myNext;
				Object                 o;
				
				/* If this is the first row */
				if (row == 0) { 
					switch (col) {
						case COLUMN_DATE:  		
							return theStatement.getDateRange().getStart();
						case COLUMN_DESC:  		
							return "Starting Balance";
						case COLUMN_BALANCE:  		
							return (isUnits) ? theStatement.getStartUnits() 
									         : theStatement.getStartBalance();
						default: return null;
					}
				}

				/* Access the line */
				myLine = theLines.extractItemAt(row-1);
				myNext = theLines.extractItemAt(row);
				
				/* Return the appropriate value */
				switch (col) {
					case COLUMN_DATE:  		
						o = myLine.getDate();
						break;
					case COLUMN_TRANTYP:  	
						o = (myLine.getTransType() == null) 
									? null : myLine.getTransType().getName();
						break;
					case COLUMN_PARTNER:	
						o = (myLine.getPartner() == null) 
									? null : myLine.getPartner().getName();
						break;
					case COLUMN_BALANCE:
						if ((myNext != null) && 
							(!finObject.differs(myNext.getDate(), myLine.getDate())))
							o = null;
						else o = (isUnits) ? myLine.getBalanceUnits() : myLine.getBalance();
						break;
					case COLUMN_CREDIT:  	
						o = (myLine.isCredit()) ? ((isUnits) ? myLine.getUnits()
							 								 : myLine.getAmount())
												: null;
						break;
					case COLUMN_DEBIT:
						o = (!myLine.isCredit()) ? ((isUnits) ? myLine.getUnits()
															  : myLine.getAmount())
												 : null;
						break;
					case COLUMN_DESC:
						o = myLine.getDesc();
						if ((o != null) & (((String)o).length() == 0))
							o = null;
						break;
					default:
						o = null;
						break;
				}
				
				/* If we have a null value for an error field,  set error description */
				if ((o == null) && (myLine.hasErrors(getFieldForCol(col))))
					o = finUtils.getError();
				
				/* Return to caller */
				return o;
			}
			
			/* set value At */
			public void setValueAt(Object obj, int row, int col) {
				finView.Statement.Line myLine;
				
				/* Access the line */
				myLine = theLines.extractItemAt(row-1);
				
				/* Push history */
				myLine.pushHistory();
				
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  
						myLine.setDate((finObject.Date)obj);    
						break;
					case COLUMN_DESC:  
						myLine.setDescription((String)obj);            
						break;
					case COLUMN_TRANTYP:  
						myLine.setTransType(theTranTyps.searchFor((String)obj));    
						break;
					case COLUMN_CREDIT:
					case COLUMN_DEBIT:
						if (isUnits) myLine.setUnits((finObject.Units)obj);
						else         myLine.setAmount((finObject.Money)obj); 
						break;
					case COLUMN_PARTNER:
						myLine.setPartner(theAccounts.searchFor((String)obj));    
						break;
				}
				
				/* Check for changes */
				if (myLine.checkForHistory()) {
					/* Note that the item has changed */
					myLine.setState(State.CHANGED);
					theLines.findEditState();
				
					/* Switch on the updated column */
					switch (col) {
						/* redraw whole table if we have updated a sort col */
						case COLUMN_DATE:
						case COLUMN_DESC:
						case COLUMN_TRANTYP: 
							myLine.reSort();
							calculateTable();
							fireTableDataChanged();
							row = myLine.indexOfItem();
							selectRow(row);
							break;
							
						/* Recalculate balance if required */	
						case COLUMN_CREDIT:
						case COLUMN_DEBIT:
							calculateTable();
							fireTableDataChanged();
							break;
							
						/* else note that we have updated this cell */
						default:
							fireTableCellUpdated(row, col);
							break;
					}

					/* Update components to reflect changes */
					notifyChanges();
				}
			}
		}
			
		/* Statement mouse listener */
		public class statementMouse extends MouseAdapter {
					
			/* Pop-up Menu items */
			private static final String popupExtract  = "View Extract";
			private static final String popupMaint    = "Maintain Account";
			private static final String popupParent   = "View Parent";
			private static final String popupMParent  = "Maintain Parent";
			private static final String popupPartner  = "View Parther";
			private static final String popupMPartner = "Maintain Parther";
			private static final String popupPattern  = "Add to Pattern";
					
			/* handle mouse Pressed event */
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			/* handle mouse Released event */
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
					
			/* Maybe show the pop-up */
			public void maybeShowPopup(MouseEvent e) {
				JPopupMenu              myMenu;
				JMenuItem               myItem;
				finView.Statement.Line  myRow     = null;
						
				if (e.isPopupTrigger() && 
					(theTable.isEnabled())) {
					/* Calculate the row/column that the mouse was clicked at */
					Point p = new Point(e.getX(), e.getY());
					int row = theTable.rowAtPoint(p);
						
					/* Access the row */
					myRow = theLines.extractItemAt(row-1);
							
					/* If we have an account */
					if ((row > 0) &&
						((!theTable.hasUpdates()) ||
						 (!theTable.isLocked()))) {
						/* Create the pop-up menu */
						myMenu = new JPopupMenu();
							
						/* If the table has no updates */
						if (!theTable.hasUpdates()) {
							/* Create the View extract choice */
							myItem = new JMenuItem(popupExtract);
							
							/* Set the command and add to menu */
							myItem.setActionCommand(popupExtract);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
							
							/* Create the Maintain account choice */
							myItem = new JMenuItem(popupMaint);
							
							/* Set the command and add to menu */
							myItem.setActionCommand(popupMaint);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
							
							/* If we have a partner */
							if (myRow.getPartner() != null) {
								/* Create the View Partner choice */
								myItem = new JMenuItem(popupPartner + " " + myRow.getPartner().getName());
							
								/* Set the command and add to menu */
								myItem.setActionCommand(popupPartner + ":" + (row-1));
								myItem.addActionListener(theTable);
								myMenu.add(myItem);
								
								/* Create the Maintain Partner choice */
								myItem = new JMenuItem(popupMPartner + " " + myRow.getPartner().getName());
							
								/* Set the command and add to menu */
								myItem.setActionCommand(popupMPartner + ":" + (row-1));
								myItem.addActionListener(theTable);
								myMenu.add(myItem);
							}
							
							/* If we have a parent */
							if (theAccount.getParent() != null) {
								/* Create the View Parent choice */
								myItem = new JMenuItem(popupParent + " " + theAccount.getParent().getName());
							
								/* Set the command and add to menu */
								myItem.setActionCommand(popupParent);
								myItem.addActionListener(theTable);
								myMenu.add(myItem);
								
								/* Create the Maintain Parent choice */
								myItem = new JMenuItem(popupMParent + " " + theAccount.getParent().getName());
							
								/* Set the command and add to menu */
								myItem.setActionCommand(popupMParent);
								myItem.addActionListener(theTable);
								myMenu.add(myItem);
							}
						}
							
						/* If the table is not locked and the Account is external */
						if ((!theTable.isLocked()) &&
							(theAccount.isExternal())) {
							/* Create the Add to Pattern choice */
							myItem = new JMenuItem(popupPattern);
							
							/* Set the command and add to menu */
							myItem.setActionCommand(popupPattern + ":" + (row-1));
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
							
						/* Show the pop-up menu */
						myMenu.show(e.getComponent(),
									e.getX(), e.getY());
					}					
				}
			}
		}			
	}
		
	/* Extract table management */
	public class ExtractTable extends financeBaseTable implements ActionListener {
		/* Members */
		private static final long serialVersionUID = -5531752729052421790L;
		private finView.Extract              	theExtract   = null;
		private finData.EventList  	         	theEvents	 = null;
		private finSwing						theParent	 = null;
		private JPanel						 	thePanel	 = null;
		private ExtractTable				 	theTable	 = this;
		private extractMouse					theMouse	 = null;
		private finObject.Range					theRange	 = null;
		private finUtils.Controls.DateSelection theSelect	 = null;
		private finUtils.Controls.RowButtons    theRowButs   = null;
		private finUtils.Controls.TableButtons  theTabButs   = null;
		private finUtils.DateUtil.Renderer 		theDateRenderer   = null;
		private finUtils.DateUtil.Editor 		theDateEditor     = null;
		private finUtils.MoneyUtil.Renderer 	theMoneyRenderer  = null;
		private finUtils.MoneyUtil.Editor 		theMoneyEditor    = null;
		private finUtils.UnitsUtil.Renderer 	theUnitsRenderer  = null;
		private finUtils.UnitsUtil.Editor 		theUnitsEditor    = null;
		private finUtils.IntegerUtil.Renderer 	theIntegerRenderer = null;
		private finUtils.IntegerUtil.Editor 	theIntegerEditor   = null;
		private finUtils.StringUtil.Renderer 	theStringRenderer = null;
		private finUtils.StringUtil.Editor 		theStringEditor   = null;
		private finUtils.ComboUtil.Editor 		theComboEditor    = null;

		/* Access methods */
		public JPanel  getPanel()			{ return thePanel; }
		
		/* Table headers */
		private static final String titleDate    = "Date";
		private static final String titleDesc    = "Description";
		private static final String titleTrans   = "TransactionType";
		private static final String titleAmount  = "Amount";
		private static final String titleDebit   = "Debit";
		private static final String titleCredit  = "Credit";
		private static final String titleUnits   = "Units";
		private static final String titleTaxCred = "TaxCredit";
		private static final String titleYears   = "Years";
			
		/* Table columns */
		private static final int COLUMN_DATE 	 = 0;
		private static final int COLUMN_DESC 	 = 1;
		private static final int COLUMN_TRANTYP  = 2;
		private static final int COLUMN_AMOUNT	 = 3;
		private static final int COLUMN_DEBIT	 = 4;
		private static final int COLUMN_CREDIT	 = 5;
		private static final int COLUMN_UNITS	 = 6;
		private static final int COLUMN_TAXCRED	 = 7;
		private static final int COLUMN_YEARS	 = 8;
		private static final int NUM_COLUMNS	 = 9;
			
		/* Constructor */
		public ExtractTable(finSwing pParent) {
			TableColumnModel    myColModel;
			TableColumn			myCol;
			JScrollPane			myScroll;
			GroupLayout			myLayout;
				
			/* Record the passed details */
			theParent = pParent;
			
			/* Tell the superclass about us */
			super.theMaster = this;
			super.theModel  = new ExtractModel();
			
			/* Set the table model */
			setModel(super.theModel);
				
			/* Access the column model */
			myColModel = getColumnModel();
			
			/* Create the relevant formatters/editors */
			theDateRenderer   = new finUtils.DateUtil.Renderer();
			theDateEditor     = new finUtils.DateUtil.Editor();
			theMoneyRenderer  = new finUtils.MoneyUtil.Renderer();
			theMoneyEditor    = new finUtils.MoneyUtil.Editor();
			theUnitsRenderer  = new finUtils.UnitsUtil.Renderer();
			theUnitsEditor    = new finUtils.UnitsUtil.Editor();
			theIntegerRenderer = new finUtils.IntegerUtil.Renderer();
			theIntegerEditor   = new finUtils.IntegerUtil.Editor();
			theStringRenderer = new finUtils.StringUtil.Renderer();
			theStringEditor   = new finUtils.StringUtil.Editor();
			theComboEditor    = new finUtils.ComboUtil.Editor();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_DATE);
			myCol.setCellRenderer(theDateRenderer);
			myCol.setCellEditor(theDateEditor);
			myCol.setPreferredWidth(80);
				
			myCol = myColModel.getColumn(COLUMN_DESC);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theStringEditor);
			myCol.setPreferredWidth(150);
			
			myCol = myColModel.getColumn(COLUMN_TRANTYP);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(110);
			
			myCol = myColModel.getColumn(COLUMN_AMOUNT);
			myCol.setCellRenderer(theMoneyRenderer);
			myCol.setCellEditor(theMoneyEditor);
			myCol.setPreferredWidth(90);
				
			myCol= myColModel.getColumn(COLUMN_DEBIT);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(130);
				
			myCol = myColModel.getColumn(COLUMN_CREDIT);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setCellEditor(theComboEditor);
			myCol.setPreferredWidth(130);
				
			myCol = myColModel.getColumn(COLUMN_UNITS);
			myCol.setCellRenderer(theUnitsRenderer);
			myCol.setCellEditor(theUnitsEditor);
			myCol.setPreferredWidth(80);
				
			myCol = myColModel.getColumn(COLUMN_TAXCRED);
			myCol.setCellRenderer(theMoneyRenderer);
			myCol.setCellEditor(theMoneyEditor);
			myCol.setPreferredWidth(90);
				
			myCol = myColModel.getColumn(COLUMN_YEARS);
			myCol.setCellRenderer(theIntegerRenderer);
			myCol.setCellEditor(theIntegerEditor);
			myCol.setPreferredWidth(50);
				
			getTableHeader().setReorderingAllowed(false);
			setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(900, 200));
			
			/* Add the mouse listener */
			theMouse = new extractMouse();
			addMouseListener(theMouse);
			
			/* Create the sub panels */
			theSelect    = new finUtils.Controls.DateSelection(this);
			theRowButs   = new finUtils.Controls.RowButtons(this, InsertStyle.INSERT);
			theTabButs   = new finUtils.Controls.TableButtons(this);
				
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
				
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
		    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
		                	.addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
		                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		        		.addComponent(theSelect.getPanel())
		                .addComponent(myScroll)
		                .addComponent(theRowButs.getPanel())
		                .addComponent(theTabButs.getPanel()))
		    );
		}
			
		/* saveData */
		public void saveData() {
			if (theExtract != null) {
				super.validateAll();
				if (!hasErrors()) theExtract.applyChanges();
			}
		}
			
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {
			/* If this is a change from the buttons */
			if (obj == (Object) theRowButs) {
				/* Set the correct show selected value */
				super.setShowDeleted(theRowButs.getShowDel());
			}
			
			/* else if this is a change from the range */
			else if (obj == (Object) theSelect) {
				/* Set the new range */
				setSelection(theSelect.getRange());
			}
		}
			
		/* refresh data */
		public void refreshData() {
			finObject.Range myRange = theView.getRange();
			theSelect.setRange(myRange);
			theRange = theSelect.getRange();
			setSelection(theRange);
		}
		
		/* Note that there has been a list selection change */
		public void notifyChanges() {
			/* Update the row buttons */
			theRowButs.setLockDown();
			
			/* Find the edit state */
			if (theEvents != null)
				theEvents.findEditState();
			
			/* Update the table buttons */
			theTabButs.setLockDown();
			theSelect.setLockDown();
			
			/* Update the top level tabs */
			theParent.setVisibleTabs();
		}
			
		/* Set Selection */
		public void setSelection(finObject.Range pRange) {
			theRange   = pRange;
			if (theRange != null) {
				theDateEditor.setRange(pRange);
				theExtract = theView.new Extract(pRange);
				theEvents  = theExtract.getEvents();
			}
			else {
				theExtract = null;
				theEvents  = null;				
			}
			super.setList(theEvents);
			super.theModel.fireTableDataChanged();
			theRowButs.setLockDown();
			theTabButs.setLockDown();
			theSelect.setLockDown();
			theParent.setVisibleTabs();
		}
			
		/* Select an explicit period */
		public void selectPeriod(DateSelection    pSource) {
			/* Adjust the period selection */
			theSelect.setSelection(pSource);
			
			/* Explicitly redraw the table */
			setSelection(theSelect.getRange());
		}
		
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_DATE: 		return finData.Event.FIELD_DATE;
				case COLUMN_DESC:		return finData.Event.FIELD_DESC;
				case COLUMN_TRANTYP:	return finData.Event.FIELD_TRNTYP;
				case COLUMN_AMOUNT:		return finData.Event.FIELD_AMOUNT;
				case COLUMN_CREDIT:		return finData.Event.FIELD_CREDIT;
				case COLUMN_DEBIT:		return finData.Event.FIELD_DEBIT;
				case COLUMN_UNITS: 		return finData.Event.FIELD_UNITS;
				case COLUMN_TAXCRED: 	return finData.Event.FIELD_TAXCREDIT;
				case COLUMN_YEARS: 		return finData.Event.FIELD_YEARS;
				default: 				return -1;
			}
		}
			
		/* Get combo box for cell */
		public JComboBox getComboBox(int row, int column) {
			finData.Event myEvent;
			
			/* Access the event */
			myEvent = theEvents.extractItemAt(row);

			/* Switch on column */
			switch (column) {
				case COLUMN_TRANTYP:	
					return theTranBox;
				case COLUMN_CREDIT:		
					return theComboList.searchFor(myEvent.getTransType())
								.getCredit();
				case COLUMN_DEBIT:
					return theComboList.searchFor(myEvent.getTransType())
								.getDebit();
				default: 				
					return null;
			}
		}
			
		/* Check whether this is a valid Object for selection */
		public boolean isValidObj(finLink.itemElement pItem,
								  finLink.histObject  pObj) {
			finData.EventValues myEvent = (finData.EventValues) pObj;
			
			/* Check whether the date is in range */
			if (theRange.compareTo(myEvent.getDate()) != 0)
				return false;

			/* Check whether the credit account is Locked/Deleted */
			if ((myEvent.getCredit().isLocked()) ||
				(myEvent.getCredit().isDeleted()))
				return false;

			/* Check whether the debit account is Locked/Deleted */
			if ((myEvent.getDebit().isLocked()) ||
				(myEvent.getDebit().isDeleted()))
				return false;

			/* Otherwise OK */
			return true;
		}
			
		/* action performed listener event */
		public void actionPerformed(ActionEvent evt) {
			String          myCmd;
			String          tokens[];
			String          myName = null;
			finData.Account myAccount;
			int             row;

			/* Access the action command */
			myCmd  = evt.getActionCommand();
			tokens = myCmd.split(":");
			myCmd  = tokens[0];
			if (tokens.length > 1) myName = tokens[1];
			
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
			
			/* If this is an account view request */
			if (myCmd.compareTo(extractMouse.popupView) == 0) {
				/* Access the correct account */
				myAccount = theView.getData().getAccounts().searchFor(myName);
			
				/* Handle commands */
				theTopWindow.selectAccount(myAccount, theSelect);
			}
			
			/* If this is an account maintenance request */
			else if (myCmd.compareTo(extractMouse.popupMaint) == 0) {
				/* Access the correct account */
				myAccount = theView.getData().getAccounts().searchFor(myName);
			
				/* Handle commands */
				theTopWindow.selectAccountMaint(myAccount);
			}
			
			/* If this is a set Null Units request */
			else if (myCmd.compareTo(extractMouse.popupSetNull + "Unit") == 0) {
				/* Access the correct row */
				row = Integer.parseInt(myName);
			
				/* set the null value */
				super.theModel.setValueAt(null, row, COLUMN_UNITS);
				super.theModel.fireTableCellUpdated(row, COLUMN_UNITS);
			}
			
			/* If this is a set Null TaxCredit request */
			else if (myCmd.compareTo(extractMouse.popupSetNull + "Credit") == 0) {
				/* Access the correct row */
				row = Integer.parseInt(myName);
			
				/* set the null value */
				super.theModel.setValueAt(null, row, COLUMN_TAXCRED);
				super.theModel.fireTableCellUpdated(row, COLUMN_UNITS);
			}
			
			/* If this is a set Null Years request */
			else if (myCmd.compareTo(extractMouse.popupSetNull + "Year") == 0) {
				/* Access the correct row */
				row = Integer.parseInt(myName);
			
				/* set the null value */
				super.theModel.setValueAt(null, row, COLUMN_YEARS);
				super.theModel.fireTableCellUpdated(row, COLUMN_UNITS);
			}
		}
			
		/* Extract table model */
		public class ExtractModel extends AbstractTableModel {
			private static final long serialVersionUID = 7997087757206121152L;
				/* get column count */
			public int getColumnCount() { return NUM_COLUMNS; }
			
			/* get row count */
			public int getRowCount() { 
				return (theEvents == null) ? 0
						                   : theEvents.countItems();
			}
			
			/* get column name */
			public String getColumnName(int col) {
				switch (col) {
					case COLUMN_DATE:  		return titleDate;
					case COLUMN_DESC:  		return titleDesc;
					case COLUMN_TRANTYP:  	return titleTrans;
					case COLUMN_AMOUNT:  	return titleAmount;
					case COLUMN_CREDIT:  	return titleCredit;
					case COLUMN_DEBIT:	 	return titleDebit;
					case COLUMN_UNITS: 		return titleUnits;
					case COLUMN_TAXCRED: 	return titleTaxCred;
					case COLUMN_YEARS: 		return titleYears;
					default: 				return null;
				}
			}
			
			/* is get column class */
			public Class<?> getColumnClass(int col) {				
				switch (col) {
					case COLUMN_DESC:  		return String.class;
					case COLUMN_TRANTYP:  	return String.class;
					case COLUMN_CREDIT:  	return String.class;
					case COLUMN_DEBIT:  	return String.class;
					default: 				return Object.class;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {
				finData.Event myEvent;
				
				/* Access the event */
				myEvent = theEvents.extractItemAt(row);
				
				/* Cannot edit if row is deleted or locked */
				if (myEvent.isDeleted() || myEvent.isLocked())
					return false;
					
				/* switch on column */
				switch (col) {
					case COLUMN_DATE:
						return true;
					case COLUMN_DESC:
						return (myEvent.getDate() != null);
					case COLUMN_TRANTYP:
						return ((myEvent.getDate() != null) &&
								(myEvent.getDesc() != null));
					default:
						if ((myEvent.getDate() == null) &&
							(myEvent.getDesc() == null) &&
							(myEvent.getTransType() == null))
							return false;
						switch (col) {
							case COLUMN_UNITS: 
								return ((myEvent.getDebit() != null) && 
										(myEvent.getCredit() != null) &&
										(myEvent.getCredit().isPriced() 
											!= myEvent.getDebit().isPriced()));
							case COLUMN_YEARS:
								return myEvent.getTransType().isTaxableGain();
							case COLUMN_TAXCRED:
								return myEvent.getTransType().needsTaxCredit();
							default:	
								return true;
						}
				}
			}
				
			/* get value At */
			public Object getValueAt(int row, int col) {
				finData.Event myEvent;
				Object        o;
												
				/* Access the event */
				myEvent = theEvents.extractItemAt(row);
				
				/* Return the appropriate value */
				switch (col) {
					case COLUMN_DATE:  	
						o = myEvent.getDate();
						break;
					case COLUMN_TRANTYP:  	
						o = (myEvent.getTransType() == null)
								? null : myEvent.getTransType().getName();
						break;
					case COLUMN_CREDIT:		
						o = (myEvent.getCredit() == null) 
								? null : myEvent.getCredit().getName();
						break;
					case COLUMN_DEBIT:		
						o = (myEvent.getDebit() == null)
								? null : myEvent.getDebit().getName();
						break;
					case COLUMN_AMOUNT:		
						o = myEvent.getAmount();
						break;
					case COLUMN_TAXCRED:		
						o = myEvent.getTaxCredit();
						break;
					case COLUMN_UNITS:		
						o = myEvent.getUnits();
						break;
					case COLUMN_YEARS:		
						o = myEvent.getYears();
						break;
					case COLUMN_DESC:
						o = myEvent.getDesc();
						if ((o != null) & (((String)o).length() == 0))
							o = null;
						break;
					default:	
						o = null;
						break;
				}
				
				/* If we have a null value for an error field,  set error description */
				if ((o == null) && (myEvent.hasErrors(getFieldForCol(col))))
					o = finUtils.getError();
				
				/* Return to caller */
				return o;
			}
				
			/* set value At */
			public void setValueAt(Object obj, int row, int col) {
				finData.Event myEvent;
				
				/* Access the line */
				myEvent = theEvents.extractItemAt(row);
				
				/* Push history */
				myEvent.pushHistory();
				
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  
						myEvent.setDate((finObject.Date)obj);    
						break;
					case COLUMN_DESC:  
						myEvent.setDescription((String)obj);            
						break;
					case COLUMN_TRANTYP:  
						myEvent.setTransType(theTranTyps.searchFor((String)obj));    
						break;
					case COLUMN_AMOUNT:
						myEvent.setAmount((finObject.Money)obj); 
						break;
					case COLUMN_TAXCRED:
						myEvent.setTaxCredit((finObject.Money)obj); 
						break;
					case COLUMN_YEARS:
						myEvent.setYears((Integer)obj); 
						break;
					case COLUMN_UNITS:
						myEvent.setUnits((finObject.Units)obj); 
						break;
					case COLUMN_CREDIT:
						myEvent.setCredit(theAccounts.searchFor((String)obj));    
						break;
					case COLUMN_DEBIT:
						myEvent.setDebit(theAccounts.searchFor((String)obj));    
						break;
				}
					
				/* Check for changes */
				if (myEvent.checkForHistory()) {
					/* Note that the item has changed */
					myEvent.setState(State.CHANGED);
					theEvents.findEditState();
					
					/* Switch on the updated column */
					switch (col) {
						/* redraw whole table if we have updated a sort col */
						case COLUMN_DATE:
						case COLUMN_DESC:
						case COLUMN_TRANTYP:
							myEvent.reSort();
							fireTableDataChanged();
							row = myEvent.indexOfItem();
							selectRow(row);
							break;
							
						/* else note that we have updated this cell */
						default:
							fireTableCellUpdated(row, col);
							break;
					}
					
					/* Note that changes have occurred */
					notifyChanges();
				}
			}
		}
		
		/* Extract mouse listener */
		public class extractMouse extends MouseAdapter {
				
			/* Pop-up Menu items */
			private static final String popupView    = "View Account";
			private static final String popupMaint   = "Maintain Account";
			private static final String popupSetNull = "Set Null";
				
			/* handle mouse Pressed event */
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			/* handle mouse Released event */
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
				
			/* Maybe show the pop-up */
			public void maybeShowPopup(MouseEvent e) {
				JPopupMenu              myMenu;
				JMenuItem               myItem;
				finData.Event           myRow     = null;
				finData.Account         myAccount = null;
				boolean                 isUnits   = false;
				boolean                 isTaxCred = false;
				boolean                 isYears   = false;
					
				if (e.isPopupTrigger() && 
					(theTable.isEnabled())) {
					/* Calculate the row/column that the mouse was clicked at */
					Point p = new Point(e.getX(), e.getY());
					int row = theTable.rowAtPoint(p);
					int col = theTable.columnAtPoint(p);
					
					/* Access the row */
					myRow = theEvents.extractItemAt(row);
						
					/* If the column is Credit */
					if (col == COLUMN_CREDIT)
						myAccount = myRow.getCredit();
					else if (col == COLUMN_DEBIT)
						myAccount = myRow.getDebit();
					else if (col == COLUMN_UNITS)
						isUnits = true;
					else if (col == COLUMN_TAXCRED)
						isTaxCred = true;
					else if (col == COLUMN_YEARS)
						isYears = true;
					
					/* If we have updates then ignore the account */
					if (theTable.hasUpdates()) myAccount = null;
					
					/* If we are pointing to units then determine whether we can set null */
					if ((isUnits) && 
						((myRow.isLocked()) ||
						 (myRow.getUnits() == null)))
						isUnits = false;
					
					/* If we are pointing to TaxCredit then determine whether we can set null */
					if ((isTaxCred) && 
						((myRow.isLocked()) ||
						 (myRow.getTaxCredit() == null)))
						isTaxCred = false;
					
					/* If we are pointing to years then determine whether we can set null */
					if ((isYears) && 
						((myRow.isLocked()) ||
						 (myRow.getYears() == null)))
						isYears = false;
					
					/* If we have an account or can set null units/years/tax */
					if ((myAccount != null) || (isUnits) || (isTaxCred) || (isYears)) {
						/* Create the pop-up menu */
						myMenu = new JPopupMenu();
						
						/* If we have an account name */
						if (myAccount != null) {
							/* Create the View account choice */
							myItem = new JMenuItem(popupView + ": " + myAccount.getName());
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupView + ":" + myAccount.getName());
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
							
							/* Create the Maintain account choice */
							myItem = new JMenuItem(popupMaint + ": " + myAccount.getName());
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupMaint + ":" + myAccount.getName());
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have units */
						if (isUnits) {
							/* Create the set null choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + "Unit:" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have years */
						if (isYears) {
							/* Create the set null choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + "Year:" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have Tax Credit */
						if (isTaxCred) {
							/* Create the set null choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + "Credit:" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* Show the pop-up menu */
						myMenu.show(e.getComponent(),
									e.getX(), e.getY());
					}					
				}
			}
		}
	}
	
	/* SpotView table management */
	public class SpotViewTable extends financeBaseTable implements ActionListener {
		/* Members */
		private static final long serialVersionUID = 5826211763056873599L;
		private finView.SpotPrices             	theSnapshot	= null;
		private finView.SpotPrices.List        	thePrices	= null;
		private finSwing						theParent	= null;
		private JPanel						 	thePanel	= null;
		private SpotViewTable				 	theTable	= this;
		private spotViewMouse					theMouse	= null;
		private finObject.Date					theDate		= null;
		private finUtils.Controls.SpotSelection theSelect	 = null;
		private finUtils.Controls.TableButtons  theTabButs   = null;
		private finUtils.PriceUtil.Renderer 	thePriceRenderer  = null;
		private finUtils.PriceUtil.Editor 		thePriceEditor    = null;
		private finUtils.StringUtil.Renderer 	theStringRenderer = null;

		/* Access methods */
		public JPanel  getPanel()			{ return thePanel; }
		
		/* Table headers */
		private static final String titleAsset   = "Asset";
		private static final String titlePrice   = "Price";
			
		/* Table columns */
		private static final int COLUMN_ASSET 	 = 0;
		private static final int COLUMN_PRICE	 = 1;
		private static final int NUM_COLUMNS	 = 2;
			
		/* Constructor */
		public SpotViewTable(finSwing pParent) {
			TableColumnModel    myColModel;
			TableColumn			myCol;
			JScrollPane			myScroll;
			GroupLayout			myLayout;
				
			/* Record the passed details */
			theParent = pParent;
			
			/* Tell the superclass about us */
			super.theMaster = this;
			super.theModel  = new spotViewModel();
			
			/* Set the table model */
			setModel(super.theModel);
				
			/* Access the column model */
			myColModel = getColumnModel();
			
			/* Create the relevant formatters/editors */
			thePriceRenderer   = new finUtils.PriceUtil.Renderer();
			thePriceEditor     = new finUtils.PriceUtil.Editor();
			theStringRenderer  = new finUtils.StringUtil.Renderer();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_ASSET);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setPreferredWidth(130);
				
			myCol = myColModel.getColumn(COLUMN_PRICE);
			myCol.setCellRenderer(thePriceRenderer);
			myCol.setCellEditor(thePriceEditor);
			myCol.setPreferredWidth(130);
			
			getTableHeader().setReorderingAllowed(false);
			setAutoResizeMode(AUTO_RESIZE_OFF);

			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(900, 200));
			
			/* Add the mouse listener */
			theMouse = new spotViewMouse();
			addMouseListener(theMouse);
			
			/* Create the sub panels */
			theSelect    = new finUtils.Controls.SpotSelection(theView, this);
			theTabButs   = new finUtils.Controls.TableButtons(this);
				
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
				
			/* Create the panel */
			thePanel = new JPanel();

			/* Create the layout for the panel */
		    myLayout = new GroupLayout(thePanel);
		    thePanel.setLayout(myLayout);
		    
		    /* Set the layout */
		    myLayout.setHorizontalGroup(
		    	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(myLayout.createSequentialGroup()
		        		.addContainerGap()
		                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
		                	.addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
		                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		        		.addComponent(theSelect.getPanel())
		                .addComponent(myScroll)
		                .addComponent(theTabButs.getPanel()))
		    );
		}
			
		/* saveData */
		public void saveData() {
			if (theSnapshot != null) {
				super.validateAll();
				if (!hasErrors()) theSnapshot.applyChanges();
			}
		}
			
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {
			/* if this is a change from the date */
			if (obj == (Object) theSelect) {
				/* Set the new range */
				super.setShowDeleted(theSelect.getShowClosed());
				setSelection(theSelect.getDate());
			}			
		}
			
		/* refresh data */
		public void refreshData() {
			finObject.Range myRange = theView.getRange();
			theSelect.setRange(myRange);
			theDate = theSelect.getDate();
			setSelection(theDate);
		}
		
		/* Note that there has been a list selection change */
		public void notifyChanges() {
			/* Find the edit state */
			if (thePrices != null)
				thePrices.findEditState();
			
			/* Update the table buttons */
			theTabButs.setLockDown();
			theSelect.setLockDown();
			
			/* Update the top level tabs */
			theParent.setVisibleTabs();
		}
			
		/* Set Selection */
		public void setSelection(finObject.Date pDate) {
			theDate = pDate;
			if (theDate != null) {
				theSnapshot = theView.new SpotPrices(pDate);
				thePrices   = theSnapshot.getPrices();
			}
			else {
				theSnapshot = null;
				thePrices   = null;				
			}
			super.setList(thePrices);
			super.theModel.fireTableDataChanged();
			theTabButs.setLockDown();
			theSelect.setLockDown();
			theParent.setVisibleTabs();
		}
			
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_ASSET: 		return finData.Price.FIELD_ACCOUNT;
				case COLUMN_PRICE:		return finData.Price.FIELD_PRICE;
				default: 				return -1;
			}
		}
			
		/* Check whether this is a valid Object for selection */
		public boolean isValidObj(finLink.itemElement pItem,
								  finLink.histObject  pObj) {
			finView.SpotPrices.SpotPrice mySpot  = (finView.SpotPrices.SpotPrice) pItem;
			finData.PriceValues          myPrice = (finData.PriceValues) pObj;
			
			/* Check whether the date is the same */
			if (finObject.differs(mySpot.getDate(), myPrice.getDate()))
				return false;

			/* Otherwise OK */
			return true;
		}
			
		/* action performed listener event */
		public void actionPerformed(ActionEvent evt) {
			String          myCmd;
			String          tokens[];
			String          myName = null;
			int             row;

			/* Access the action command */
			myCmd  = evt.getActionCommand();
			tokens = myCmd.split(":");
			myCmd  = tokens[0];
			if (tokens.length > 1) myName = tokens[1];
			
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
			
			/* If this is a set Null request */
			if (myCmd.compareTo(spotViewMouse.popupSetNull) == 0) {
				/* Access the correct row */
				row = Integer.parseInt(myName);
			
				/* set the null value */
				super.theModel.setValueAt(null, row, COLUMN_PRICE);
				super.theModel.fireTableCellUpdated(row, COLUMN_PRICE);
			}
		}
			
		/* SpotView table model */
		public class spotViewModel extends AbstractTableModel {
			private static final long serialVersionUID = 2520681944053000625L;

				/* get column count */
			public int getColumnCount() { return NUM_COLUMNS; }
			
			/* get row count */
			public int getRowCount() { 
				return (thePrices == null) ? 0
						                   : thePrices.countItems();
			}
			
			/* get column name */
			public String getColumnName(int col) {
				switch (col) {
					case COLUMN_ASSET: 		return titleAsset;
					case COLUMN_PRICE: 		return titlePrice;
					default: 				return null;
				}
			}
			
			/* is get column class */
			public Class<?> getColumnClass(int col) {				
				switch (col) {
					case COLUMN_ASSET: 		return String.class;
					default: 				return Object.class;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {
				/* switch on column */
				switch (col) {
					case COLUMN_ASSET:
						return false;
					case COLUMN_PRICE:
					default:
						return true;
				}
			}
				
			/* get value At */
			public Object getValueAt(int row, int col) {
				finView.SpotPrices.SpotPrice mySpot;
				Object                       o;
												
				/* Access the spot price */
				mySpot = thePrices.extractItemAt(row);
				
				/* Return the appropriate value */
				switch (col) {
					case COLUMN_ASSET:  	
						o = mySpot.getAccount().getName();
						break;
					case COLUMN_PRICE:  	
						o = mySpot.getPrice();
						break;
					default:	
						o = null;
						break;
				}
				
				/* If we have a null value for an error field,  set error description */
				if ((o == null) && (mySpot.hasErrors(getFieldForCol(col))))
					o = finUtils.getError();
				
				/* Return to caller */
				return o;
			}
				
			/* set value At */
			public void setValueAt(Object obj, int row, int col) {
				finView.SpotPrices.SpotPrice mySpot;
				
				/* Access the line */
				mySpot = thePrices.extractItemAt(row);
				
				/* Push history */
				mySpot.pushHistory();
				
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_PRICE:  
						mySpot.setPrice((finObject.Price)obj);    
						break;
				}
					
				/* Check for changes */
				if (mySpot.checkForHistory()) {
					/* Note that the item has changed */
					mySpot.setState(State.CHANGED);
					thePrices.findEditState();
					
					/* note that we have updated this cell */
					fireTableCellUpdated(row, col);
					
					/* Note that changes have occurred */
					notifyChanges();
				}
			}
		}
		
		/* spotView mouse listener */
		public class spotViewMouse extends MouseAdapter {
				
			/* Pop-up Menu items */
			private static final String popupSetNull = "Set Null";
				
			/* handle mouse Pressed event */
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}
			/* handle mouse Released event */
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}
				
			/* Maybe show the pop-up */
			public void maybeShowPopup(MouseEvent e) {
				JPopupMenu                   myMenu;
				JMenuItem                    myItem;
				finView.SpotPrices.SpotPrice myRow;
				boolean                      isPrice  = false;
					
				if (e.isPopupTrigger() && 
					(theTable.isEnabled())) {
					/* Calculate the row/column that the mouse was clicked at */
					Point p = new Point(e.getX(), e.getY());
					int row = theTable.rowAtPoint(p);
					int col = theTable.columnAtPoint(p);
					
					/* Access the row */
					myRow = thePrices.extractItemAt(row);
						
					/* If the column is Price */
					if (col == COLUMN_PRICE)
						isPrice = true;
					
					/* If we are pointing to units then determine whether we can set null */
					if ((isPrice) && 
						(myRow.getPrice() == null))
						isPrice = false;
					
					/* If we can set null price */
					if (isPrice) {
						/* Create the pop-up menu */
						myMenu = new JPopupMenu();
						
						/* If we have price */
						if (isPrice) {
							/* Create the View account choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + ":" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* Show the pop-up menu */
						myMenu.show(e.getComponent(),
									e.getX(), e.getY());
					}					
				}
			}
		}
	}	
}
