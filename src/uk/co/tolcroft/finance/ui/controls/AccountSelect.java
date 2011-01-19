package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

public class AccountSelect implements ItemListener {
	/* Members */
	private JPanel				thePanel		= null;
	private financePanel		theParent		= null;
	private View				theView			= null;
	private JComboBox           theTypesBox 	= null;
	private JComboBox           theAccountBox	= null;
	private JLabel              theTypeLabel    = null;
	private JLabel              theAccountLabel = null;
	private JCheckBox			theShowClosed   = null;
	private JCheckBox			theShowDeleted  = null;
	private AccountType.List	theTypes		= null;
	private Account.List     	theAccounts     = null;
	private AccountType   		theType			= null;
	private Account         	theSelected 	= null;
	private boolean				doShowClosed	= false;
	private boolean				doShowDeleted	= false;
	private boolean				acctsPopulated 	= false;
	private boolean				typesPopulated 	= false;
	private boolean				refreshingData  = false;
	
	/* Access methods */
	public 	JPanel      getPanel()      { return thePanel; }
	public	Account		getSelected()   { return theSelected; }
	public	AccountType	getType()	   	{ return theType; }
	public	boolean		doShowClosed()	{ return doShowClosed; }
	public	boolean		doShowDeleted()	{ return doShowDeleted; }
				
	/* Constructor */
	public AccountSelect(View      		pView, 
						 financePanel 	pParent,
						 boolean	    showDeleted) {
		
		/* Store table and view details */
		theParent	  = pParent;
		theView 	  = pView;
		
		/* Create the boxes */
		theTypesBox    = new JComboBox();
		theAccountBox  = new JComboBox();
		theShowClosed  = new JCheckBox();
		theShowDeleted = new JCheckBox();
							
		/* Initialise the data from the view */
		refreshData();
		
		/* Set the text for the check-box */
		theShowClosed.setText("Show Closed");
		theShowClosed.setSelected(doShowClosed);
		
		/* Set the text for the check-box */
		theShowDeleted.setText("Show Deleted");
		theShowDeleted.setSelected(doShowDeleted);
		
		/* Create the labels */
		theTypeLabel = new JLabel("Account Type:");
		theAccountLabel = new JLabel("Account:");
		
		/* Add the listener for item changes */
		theTypesBox.addItemListener(this);
		theAccountBox.addItemListener(this);
		theShowClosed.addItemListener(this);
		theShowDeleted.addItemListener(this);
		
		/* Create the panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Account Selection"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* If we are showing deleted */
	    if (showDeleted) {
	    	/* Set the layout */
	    	panelLayout.setHorizontalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createSequentialGroup()
	            		.addContainerGap()
	            		.addComponent(theTypeLabel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            		.addComponent(theAccountLabel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            		.addComponent(theAccountBox)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            		.addComponent(theShowClosed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            		.addComponent(theShowDeleted, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            		.addContainerGap())
	    	);
	    	panelLayout.setVerticalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	            		.addComponent(theTypeLabel)
	            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            		.addComponent(theAccountLabel)
	            		.addComponent(theAccountBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	            		.addComponent(theShowClosed)
	            		.addComponent(theShowDeleted)
	    	);
	    }

	    else {
	    	/* Set the layout */
	    	panelLayout.setHorizontalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createSequentialGroup()
	            		.addContainerGap()
	            		.addComponent(theTypeLabel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            		.addComponent(theAccountLabel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	            		.addComponent(theAccountBox)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            		.addComponent(theShowClosed, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            		.addContainerGap())
	    	);
	    	panelLayout.setVerticalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	            		.addComponent(theTypeLabel)
	            		.addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	            		.addComponent(theAccountLabel)
	            		.addComponent(theAccountBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	            		.addComponent(theShowClosed)
	    	);
	    }

	    /* Initiate lock-down mode */
		setLockDown();
	}
	
	/* refresh data */
	public void refreshData() {
		DataSet			myData;
		AccountType 	myType;
		
		DataList<AccountType>.ListIterator	myIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access types and accounts */
		theTypes    = myData.getAccountTypes();
		theAccounts = myData.getAccounts();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have types already populated */
		if (typesPopulated) {	
			/* If we have a selected type */
			if (theType != null) {
				/* Find it in the new list */
				theType = theTypes.searchFor(theType.getName());
			}
			
			/* Remove the types */
			theTypesBox.removeAllItems();
			typesPopulated = false;
		}
		
		/* Access the iterator */
		myIterator = theTypes.listIterator();
		
		/* Add the AccountType values to the types box */
		while ((myType  = myIterator.next()) != null) {
			/* Add the item to the list */
			theTypesBox.addItem(myType.getName());
			typesPopulated = true;
		}
		
		/* If we have a selected type */
		if (theType != null) {
			/* Select it in the new list */
			theTypesBox.setSelectedItem(theType.getName());
		}
		
		/* Else we have no type currently selected */
		else if (typesPopulated) {
			/* Select the first account type */
			theTypesBox.setSelectedIndex(0);
			theType = myIterator.peekFirst();
		}

		/* Note that we have finished refreshing data */
		refreshingData = false;

		/* Build the account list for the type */ 
		buildAccounts();	
	}
	
	/* build the accounts comboBox */
	private boolean buildAccounts() {
		Account       myAcct;
		Account       myFirst = null;
		Account       myOld   = theSelected;
		
		DataList<Account>.ListIterator	myIterator;
		
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have accounts already populated */
		if (acctsPopulated) {	
			/* If we have a selected account */
			if (theSelected != null) {
				/* Find it in the new list */
				theSelected = theAccounts.searchFor(theSelected.getName());
			}
			
			/* Remove the accounts from the box */
			theAccountBox.removeAllItems();
			acctsPopulated = false;
		}
		
		/* If the selected item is no longer valid */
		if ((theSelected != null) &&
			(((!doShowDeleted) &&
			  (theSelected.isDeleted())) ||
			 ((!doShowClosed) &&
			  (theSelected.isClosed())) ||
			 (theType.compareTo(theSelected.getActType()) != 0))) {
			/* Remove selection */
			theSelected = null;
		}
		
		/* Access the iterator */
		myIterator = theAccounts.listIterator(true);
		
		/* Add the Account values to the types box */
		while ((myAcct  = myIterator.next()) != null) {
			/* Skip deleted items */
			if ((!doShowDeleted) &&
				(myAcct.isDeleted())) continue;
			
			/* Skip closed items if required */
			if ((!doShowClosed) && 
				(myAcct.isClosed())) continue;
			
			/* Skip items that are the wrong type */
			if (theType.compareTo(myAcct.getActType()) != 0)
			  continue;
			
			/* Note the first in the list */
			if (myFirst == null) myFirst = myAcct;
			
			/* Add the item to the list */
			theAccountBox.addItem(myAcct.getName());
			acctsPopulated = true;
		}
					
		/* If we have a selected account */
		if (theSelected != null) {
			/* Select it in the new list */
			theAccountBox.setSelectedItem(theSelected.getName());
		}
		
		/* Else we have no account currently selected */
		else if (acctsPopulated) {
			/* Select the first account */
			theAccountBox.setSelectedIndex(0);
			theSelected = myFirst;
		}

		/* Note that we have finished refreshing data */
		refreshingData = false;
		
		/* Return whether we have changed selection */
		return Utils.differs(theSelected, myOld);
	}
	
	/* Set account explicitly */
	public void setSelection(Account pAccount) {
		Account myAccount;
		
		/* Set the refreshing data flag */
		refreshingData = true;
		
		/* Access the editable account */
		myAccount = theAccounts.searchFor(pAccount.getName());
		
		/* Select the correct account type */
		theType = pAccount.getActType();
		theTypesBox.setSelectedItem(theType.getName());
		
		/* If we need to show closed items */
		if ((!doShowClosed) && (myAccount != null) && (myAccount.isClosed())) {
			/* Set the flag correctly */
			doShowClosed = true;
			theShowClosed.setSelected(doShowClosed);
		}
		
		/* If we need to show deleted items */
		if ((!doShowDeleted) && (myAccount != null) && (myAccount.isDeleted())) {
			/* Set the flag correctly */
			doShowDeleted = true;
			theShowDeleted.setSelected(doShowDeleted);
		}
		
		/* Select the account */
		theSelected = myAccount;
		
		/* Reset the refreshing data flag */
		refreshingData = false;

		/* Build the accounts */
		buildAccounts();
	}

	/* Lock/Unlock the selection */
	public void setLockDown() {
		boolean bLock = theParent.hasUpdates();
		
		/* Lock/Unlock the selection */
		theTypesBox.setEnabled(!bLock);
		theAccountBox.setEnabled(!bLock);
		
		/* Can't switch off show closed if account is closed */
		if ((theSelected != null) &&
			(theSelected.isClosed()))
			bLock = true;
		
		/* Lock Show Closed */
		theShowClosed.setEnabled(!bLock);
		
		/* Reset the lock */
		bLock = theParent.hasUpdates();
		
		/* Can't switch off show deleted if account is deleted */
		if ((theSelected != null) &&
			(theSelected.isDeleted()))
			bLock = true;
		
		/* Lock Show Deleted */
		theShowDeleted.setEnabled(!bLock);
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String                myName;
		boolean               bChange = false;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the types box */
		if (evt.getSource() == (Object)theTypesBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Select the new type and rebuild account list */
				theType = theTypes.searchFor(myName);
				bChange = buildAccounts();
			}
		}
		
		/* If this event relates to the account box */
		if (evt.getSource() == (Object)theAccountBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Select the new account */						
				theSelected = theAccounts.searchFor(myName);
				bChange     = true;
			}
		}
		
		/* If this event relates to the showClosed box */
		if (evt.getSource() == (Object)theShowClosed) {
			/* Note the new criteria and re-build lists */
			doShowClosed = theShowClosed.isSelected();
			bChange = buildAccounts();
		}
		
		/* If this event relates to the showDeleted box */
		if (evt.getSource() == (Object)theShowDeleted) {
			/* Note the new criteria and re-build lists */
			doShowDeleted = theShowDeleted.isSelected();
			bChange = buildAccounts();
		}
		
		/* If we have a change, alert the table */
		if (bChange) { theParent.notifySelection(this); }
	}
}
