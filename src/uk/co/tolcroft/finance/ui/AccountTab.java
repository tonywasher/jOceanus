package uk.co.tolcroft.finance.ui;

import java.awt.Component;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.LayoutStyle;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.DataList.*;

public class AccountTab implements ChangeListener,
								   financePanel {
	/* Members */
	private static final long serialVersionUID  = 7682053546233794088L;

	private View				theView		 = null;
	private JPanel				thePanel	 = null;
	private MainTab				theParent    = null;
	private JTabbedPane       	theTabs      = null;
	private AccountSelect  		theSelect	 = null;
	private AccountStatement	theStatement = null;
	private AccountPatterns		thePatterns  = null;
	private AccountPrices		thePrices    = null;
	private AccountRates		theRates     = null;
	private Account				theAccount   = null;
	private Account.List		theAcList	 = null;
	private SaveButtons  		theTabButs   = null;
	private Component		  	theLastFocus = null;
		
	/* Access methods */
	public View			getView()				  { return theView; }
	public MainTab		getTopWindow()			  { return theParent; }
	public JPanel  		getPanel()   			  { return thePanel; }
	public int  		getFieldForCol(int col)   { return -1; }
	public ComboSelect	getComboList()			  { return theParent.getComboList(); }
	public void 		printIt()				  { }
		
	/* Tab headers */
	private static final String titleStatement = "Statement";
	private static final String titlePatterns  = "Patterns";
	private static final String titlePrices    = "Prices";
	private static final String titleRates     = "Rates";
		
	/* Constructor */
	public AccountTab(MainTab pParent) {
		GroupLayout			myLayout;
		
		/* Record passed details */
		theParent = pParent;
		theView   = pParent.getView();
		
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
		theStatement.refreshData();
			
		/* Redraw selection */
		setSelection(theSelect.getSelected());
	}
		
	/* Has this set of tables got updates */
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
		
	/* Has this set of tables got errors */
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
	 * Determine the precedence for a {@link finObject.EditState} value.
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
	
	/* Get the Edit State */
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
		
	/* Perform command function */
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
		
	/* is this table locked */
	public boolean isLocked() {
		/* State whether account is locked */
		return ((theAccount == null) || theAccount.isClosed());
	}
				
	/* validateAll */
	public void validateAll() {
		/* Validate the data */
		theStatement.validateAll();
		thePatterns.validateAll();
		theRates.validateAll();
		thePrices.validateAll();
		notifyChanges();
	}
		
	/* cancel editing */
	public void cancelEditing() {
		/* cancel editing */
		theStatement.cancelEditing();
		thePatterns.cancelEditing();
		theRates.cancelEditing();
		thePrices.cancelEditing();
	}
		
	/* resetData */
	public void resetData() {
		/* reset the data */
		theStatement.resetData();
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
			theStatement.saveData();
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
		DataList<?>			myList;
		
		/* If the statement panel is active */
		if ((theLastFocus == null) ||
			(theLastFocus == theStatement.getPanel())) {
			/* Access the formatted output */
			myList = theStatement.getList();
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
	public void setSelection(Account pAccount) {
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
		
	/* Set visible tabs */
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
	
		/* If the account is not closed */
		if ((theAccount != null) &&
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
	public void selectAccount(Account  	pAccount,
							  DateRange	pSource) {
		/* Adjust the date selection for the statements appropriately */
		theStatement.selectPeriod(pSource);
		
		/* Adjust the account selection */
		theSelect.setSelection(pAccount);
		
		/* Redraw selection */
		setSelection(theSelect.getSelected());
	}
	
	/* Add a pattern from a statement line */
	public void addPattern(Statement.Line pLine) {
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
