package uk.co.tolcroft.finance.ui;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class AccountTab implements financePanel,
								   ChangeListener {
	/* Members */
	private static final long serialVersionUID  = 7682053546233794088L;

	private View				theView		 	= null;
	private JPanel				thePanel	 	= null;
	private MainTab				theParent    	= null;
	private JTabbedPane       	theTabs      	= null;
	private AccountSelect  		theSelect	 	= null;
	private AccountStatement	theStatement 	= null;
	private AccountPatterns		thePatterns  	= null;
	private AccountPrices		thePrices    	= null;
	private AccountRates		theRates     	= null;
	private Account				theAccount   	= null;
	private Account.List		theAcList	 	= null;
	private SaveButtons  		theTabButs   	= null;
	private DebugEntry			theDebugEntry	= null;
	private ErrorPanel			theError		= null;
		
	/* Access methods */
	public View			getView()				  { return theView; }
	public MainTab		getTopWindow()			  { return theParent; }
	public JPanel  		getPanel()   			  { return thePanel; }
	public int  		getFieldForCol(int col)   { return -1; }
	public ComboSelect	getComboList()			  { return theParent.getComboList(); }
	public DebugEntry	getDebugEntry()			  { return theDebugEntry; }
	public DebugManager getDebugManager() 		  { return theParent.getDebugMgr(); }
	public void 		printIt()				  { }
		
	/* Tab headers */
	private static final String titleStatement = "Statement";
	private static final String titlePatterns  = "Patterns";
	private static final String titlePrices    = "Prices";
	private static final String titleRates     = "Rates";
		
	/**
	 * Constructor for Account Window
	 * @param pParent the parent window
	 */
	public AccountTab(MainTab pParent) {
		GroupLayout			myLayout;
		DebugEntry			mySection;
		
		/* Record passed details */
		theParent = pParent;
		theView   = pParent.getView();
		
		/* Create the top level debug entry for this view  */
		DebugManager myDebugMgr = theView.getDebugMgr();
		mySection = myDebugMgr.getViews();
        theDebugEntry = myDebugMgr.new DebugEntry("Account");
        theDebugEntry.addAsChildOf(mySection);
		
		/* Create the Tabbed Pane */
		theTabs = new JTabbedPane();
	
		/* Create the Statement table and add to tabbed pane */
		theStatement = new AccountStatement(this);
		theTabs.addTab(titleStatement, theStatement.getPanel());
		theTabs.addChangeListener(this);
		
		/* Create the optional tables */
		thePatterns = new AccountPatterns(this);
		theRates    = new AccountRates(this);
		thePrices   = new AccountPrices(this);
										
		/* Create the Account selection panel */
		theSelect 	= new AccountSelect(theView, this, false);
			
		/* Create the table buttons */
		theTabButs  = new SaveButtons(this);
			
        /* Create the error panel for this view */
        theError = new ErrorPanel(this);
        
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
	                    .addComponent(theError.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theTabs, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theError.getPanel())
	                .addComponent(theSelect.getPanel())
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(theTabs)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theTabButs.getPanel())
	                .addContainerGap())
	    );
	}

	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);

		/* Lock tabs and buttons area */
		theTabs.setEnabled(!isError);
		theTabButs.getPanel().setEnabled(!isError);
	}
	
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws Exception {
		/* Refresh the account selection */
		theSelect.refreshData();
			
		/* Refresh the child tables */
		theRates.refreshData();
		thePrices.refreshData();
		thePatterns.refreshData();
		theStatement.refreshData();
			
		/* Redraw selection */
		setSelection(theSelect.getSelected());
		
		/* Create SavePoint */
		theSelect.createSavePoint();
	}
		
	/**
	 * Has this set of tables got updates
	 */
	public boolean hasUpdates() {
		boolean hasUpdates = false;
		
		/* Determine whether we have updates */
		hasUpdates = theStatement.hasUpdates();
        if (!hasUpdates) hasUpdates = thePatterns.hasUpdates();
        if (!hasUpdates) hasUpdates = theRates.hasUpdates();
        if (!hasUpdates) hasUpdates = thePrices.hasUpdates();
			
		/* Return to caller */
		return hasUpdates;
	}
		
	/**
	 * Has this set of tables got errors
	 */
	public boolean hasErrors() {
		boolean hasErrors = false;
		
		/* Determine whether we have updates */
		hasErrors = theStatement.hasErrors() ||
					thePatterns.hasErrors()  ||
					theRates.hasErrors()     ||
					thePrices.hasErrors();
			
		/* Return to caller */
		return hasErrors;
	}
		
	/**
	 * Determine the precedence for a {@link EditState} value.
	 * 
	 * @param pTest The EditState 
	 * @return the precedence 
	 */	
	private static int editOrder(EditState pTest) {
		switch (pTest) {
			case ERROR: return 3;
			case DIRTY: return 2;
			case VALID: return 1;
			default:    return 0;
		}
	}

	/**
	 * Combine two EditState values into a single value, using the value of greater precedence.
	 * 
	 * @param pThis The First EditState 
	 * @param pThat The Second EditState 
	 * @return the combined EditState 
	 */	
	static EditState editCombine(EditState pThis, EditState pThat) {
		if (editOrder(pThis) > editOrder(pThat))
			return(pThis);
		else 
			return(pThat);
	}
	
	/**
	 * Get the edit state of this set of tables
	 * @return the edit state
	 */
	public EditState getEditState() {
		EditState myState;
		EditState myNewState;

		/* Access the State of the Statement */
		myState = theStatement.getEditState();
		
		/* Access and combine the State of the Patterns */
		myNewState = thePatterns.getEditState();
		myState    = editCombine(myState, myNewState);
		
		/* Access and combine the State of the Rates */
		myNewState = theRates.getEditState();
		myState    = editCombine(myState, myNewState);
		
		/* Access and combine the State of the Prices */
		myNewState = thePrices.getEditState();
		myState    = editCombine(myState, myNewState);
		
		/* Return the state */
		return myState;
	}
		
	/**
	 * Perform a command
	 * @param pCmd the command to perform
	 */
	public void performCommand(financeCommand pCmd) {
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
		
	/**
	 * Is this table locked
	 */
	public boolean isLocked() {
		/* State whether account is locked */
		return ((theAccount == null) || theAccount.isClosed());
	}
				
	/**
	 * Validate all tables
	 */
	public void validateAll() {
		/* Validate the data */
		theStatement.validateAll();
		thePatterns.validateAll();
		theRates.validateAll();
		thePrices.validateAll();
		notifyChanges();
	}
		
	/**
	 * Cancel all editing
	 */
	public void cancelEditing() {
		/* cancel editing */
		theStatement.cancelEditing();
		thePatterns.cancelEditing();
		theRates.cancelEditing();
		thePrices.cancelEditing();
	}
		
	/**
	 * Reset all table data
	 */
	public void resetData() {
		/* reset the data */
		theStatement.resetData();
		thePatterns.resetData();
		theRates.resetData();
		thePrices.resetData();
	}
		
	/**
	 * Save changes from the view into the underlying data
	 */
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
			theStatement.saveData();
		}
	}
		
	/**
	 *  Notify table that there has been a change in selection by an underlying control
	 *  @param obj the underlying control that has changed selection
	 */
	public void notifySelection(Object obj)    {
		/* If this is a change from the account selection */
		if (obj == (Object) theSelect) {
			/* Protect against exceptions */
			try {
				/* Select the account */
				setSelection(theSelect.getSelected());
					
				/* Create SavePoint */
				theSelect.createSavePoint();
			} 
			
			catch (Throwable e) {
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to change selection",
										          e);
				
				/* Show the error */
				theError.setError(myError);
				
				/* Restore SavePoint */
				theSelect.restoreSavePoint();			
			} 
		}
	}
		
	/**
	 * Call underlying controls to take notice of changes in view/selection
	 */
	public void notifyChanges() {
		/* Lock down the table buttons and the selection */
		theTabButs.setLockDown();
		theSelect.setLockDown();
		
		/* Adjust the visibility of the tabs */
		setVisibleTabs();
	}	
		
	/**
	 * Select an explicit account
	 * @param pAccount the account to select
	 */
	public void setSelection(Account pAccount) throws Exception {
		DataSet myData = theView.getData();
		
		/* Release old list */
		if (theAcList != null) theAcList.clear();
		
		/* Reset controls */
		theAcList  = null;
		theAccount = null;
		
		/* If we have a selected account */
		if (pAccount != null) {
			/* Create the edit account list */
			theAcList = new Account.List(myData, ListStyle.EDIT);
		
			/* Create an edit copy of the account */
			theAccount = new Account(theAcList, pAccount);
		}
		
		/* Alert the different tables to the change */
		theStatement.setSelection(theAccount);
		thePatterns.setSelection(theAccount);
		thePrices.setSelection(theAccount);
		theRates.setSelection(theAccount);
			
		/* Note the changes */
		notifyChanges();
	}
		
	/**
	 * Set tabs to be visible or not depending on the type of account
	 */
	private void setVisibleTabs() {
		int         iIndex;
		boolean     isPatternsSelected = false;
		boolean     isPricesSelected   = false;
		
		/* Access the Rates index */
		iIndex = theTabs.indexOfTab(titleRates);
		
		/* If the account has rates */
		if ((theAccount != null) &&
			(theAccount.isMoney())) {
			
			/* Add the Rates if not present */
			if (iIndex == -1) {
				theTabs.addTab(titleRates, theRates.getPanel());
				theRates.getDebugEntry().showEntry();
			
				/* Remove the patterns tab if present */
				iIndex = theTabs.indexOfTab(titlePatterns);
				if (iIndex != -1) {
					/* Remember if Patterns are selected since we need to restore this */
					if ((iIndex == theTabs.getSelectedIndex()) &&
						(!isPricesSelected))
						isPatternsSelected = true;
			
					/* Remove the patterns tab */
					theTabs.removeTabAt(iIndex);
					thePatterns.getDebugEntry().hideEntry();
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
			theRates.getDebugEntry().hideEntry();
		}
		
		/* Access the Prices index */
		iIndex = theTabs.indexOfTab(titlePrices);
		
		/* If the account has prices */
		if ((theAccount != null) &&
			(theAccount.isPriced())) {
			
			/* Add the Prices if not present */
			if (iIndex == -1) {
				theTabs.addTab(titlePrices, thePrices.getPanel());
				thePrices.getDebugEntry().showEntry();
								
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
					thePatterns.getDebugEntry().hideEntry();
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
			thePrices.getDebugEntry().hideEntry();
		}
	
		/* Access the Patterns index */
		iIndex = theTabs.indexOfTab(titlePatterns);
	
		/* If the account is not closed */
		if ((theAccount != null) &&
			(!theAccount.isClosed())) {
		
			/* Add the Patterns if not present */
			if (iIndex == -1) {
				theTabs.addTab(titlePatterns, thePatterns.getPanel());
				thePatterns.getDebugEntry().showEntry();
			
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
			thePatterns.getDebugEntry().hideEntry();
		}
		
		/* Update the top level tabs */
		theParent.setVisibleTabs();
	}
	
	/**
	 * Select an explicit account and period
	 * @param pAccount the account to select
	 * @param pSource the period source
	 */
	public void selectAccount(Account  	pAccount,
							  DateRange	pSource) {
		/* Protect against exceptions */
		try {
			/* Adjust the date selection for the statements appropriately */
			theStatement.selectPeriod(pSource);
		
			/* Adjust the account selection */
			theSelect.setSelection(pAccount);
			setSelection(theSelect.getSelected());
			
			/* Create SavePoint */
			theSelect.createSavePoint();
		} 

		catch (Throwable e) {
			/* Build the error */
			Exception myError = new Exception(ExceptionClass.DATA,
									          "Failed to change selection",
									          e);
			
			/* Show the error */
			theError.setError(myError);
			
			/* Restore SavePoint */
			theSelect.restoreSavePoint();						
		} 
	}
	
	/**
	 * Add a pattern from a statement line
	 * @param pLine the line to add
	 */
	public void addPattern(Statement.Line pLine) {
		/* Pass through to the Patterns table */
		thePatterns.addPattern(pLine);
		
		/* Change focus to the Patterns */
		gotoNamedTab(titlePatterns);
	}
	
	/**
	 * Select the explicitly named tab
	 * @param pTabName the tab to select
	 */
	public void gotoNamedTab(String pTabName) {
		/* Access the required index */
		int iIndex = theTabs.indexOfTab(pTabName);
	
		/* Select the required tab */
		theTabs.setSelectedIndex(iIndex);
	}

	/**
	 * Handle state changed events (change of select tab)
	 * @param e the change event
	 */
	public void stateChanged(ChangeEvent e) {
		/* Ignore if it is not the tabs */
		if (e.getSource() != theTabs) return;
		
		/* Determine the focus */
		determineFocus();
	}

	/**
	 * Determine the current focus
	 */
	protected void determineFocus() {
		/* Access the selected component */
		Component myComponent = theTabs.getSelectedComponent();
		
		/* If the selected component is Statement */
		if (myComponent == (Component)theStatement.getPanel()) {
			/* Set the debug focus */
			theStatement.getDebugEntry().setFocus();
			theStatement.requestFocusInWindow();
		}
		
		/* If the selected component is Rates */
		else if (myComponent == (Component)theRates.getPanel()) {
			/* Set the debug focus */
			theRates.getDebugEntry().setFocus();
			theRates.requestFocusInWindow();
		}

		/* If the selected component is Prices */
		else if (myComponent == (Component)thePrices.getPanel()) {
			/* Set the debug focus */
			thePrices.getDebugEntry().setFocus();
			thePrices.requestFocusInWindow();
		}
		
		/* If the selected component is Patterns */
		else if (myComponent == (Component)thePatterns.getPanel()) {
			/* Set the debug focus */
			thePatterns.getDebugEntry().setFocus();
			thePatterns.requestFocusInWindow();
		}
	}
}
