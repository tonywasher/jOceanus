package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.ui.FinanceInterfaces.*;

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
	private AccountState 		theState   	    = null;
	private AccountState      	theSavePoint    = null;
	private boolean				acctsPopulated 	= false;
	private boolean				typesPopulated 	= false;
	private boolean				refreshingData  = false;
	
	/* Access methods */
	public 	JPanel      getPanel()      { return thePanel; }
	public	Account		getSelected()   { return theState.getSelected(); }
	public	AccountType	getType()	   	{ return theState.getType(); }
	public	boolean		doShowClosed()	{ return theState.doShowClosed(); }
	public	boolean		doShowDeleted()	{ return theState.doShowDeleted(); }
				
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
							
		/* Create initial state */
		theState = new AccountState();

		/* Initialise the data from the view */
		refreshData();
		
		/* Set the text for the check-box */
		theShowClosed.setText("Show Closed");
		theShowClosed.setSelected(doShowClosed());
		
		/* Set the text for the check-box */
		theShowDeleted.setText("Show Deleted");
		theShowDeleted.setSelected(doShowDeleted());
		
		/* Create the labels */
		theTypeLabel = new JLabel("Account Type:");
		theAccountLabel = new JLabel("Account:");
		
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

		/* Apply the current state */
		theState.applyState();

		/* Add the listener for item changes */
		theTypesBox.addItemListener(this);
		theAccountBox.addItemListener(this);
		theShowClosed.addItemListener(this);
		theShowDeleted.addItemListener(this);		
	}
	
	/**
	 *  Create SavePoint
	 */
	public void createSavePoint() {
		/* Create the savePoint */
		theSavePoint = new AccountState(theState);
	}

	/**
	 *  Restore SavePoint
	 */
	public void restoreSavePoint() {
		/* Restore the savePoint */
		theState = new AccountState(theSavePoint);
		
		/* Build the range and apply the state */
		buildAccounts();
		theState.applyState();		
	}

	/**
	 *  refresh data
	 */
	public void refreshData() {
		/* Build the account types */
		buildAccountTypes();
		
		/* Build the account list for the type */ 
		buildAccounts();	
	}
	
	/**
	 *  build the account types
	 */
	private void buildAccountTypes() {
		FinanceData	myData;
		AccountType myType  = null;
		AccountType myFirst = null;
		Account		myAccount;
		boolean		doShowDeleted;
		boolean		doShowClosed;
		
		DataList<Account>.ListIterator	myIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access types and accounts */
		theTypes    = myData.getAccountTypes();
		theAccounts = myData.getAccounts();
	
		/* Access current values */
		doShowDeleted = doShowDeleted();
		doShowClosed  = doShowClosed();

		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have types already populated */
		if (typesPopulated) {	
			/* If we have a selected type */
			if (getType() != null) {
				/* Find it in the new list */
				theState.setType(theTypes.searchFor(getType().getName()));
			}
			
			/* Remove the types */
			theTypesBox.removeAllItems();
			typesPopulated = false;
		}
		
		/* Access the iterator */
		myIterator = theAccounts.listIterator(true);
		
		/* Loop through the non-owner accounts */
		while ((myAccount = myIterator.next()) != null) {
			/* Skip owner items */
			if (myAccount.isOwner()) continue;
			
			/* Skip deleted items */
			if ((!doShowDeleted) &&
				(myAccount.isDeleted())) continue;
			
			/* Skip closed items if required */
			if ((!doShowClosed) && 
				(myAccount.isClosed())) continue;
			
			/* If the type of this account is new */
			if (AccountType.differs(myType, myAccount.getActType())) {
				/* Note the type */
				myType = myAccount.getActType();
				if (myFirst == null) myFirst = myType;
			
				/* Add the item to the list */
				theTypesBox.addItem(myType.getName());
				typesPopulated = true;
			}
		}
		
		/* Access the iterator */
		myIterator = theAccounts.listIterator(true);
		
		/* Loop through the owner accounts */
		while ((myAccount = myIterator.next()) != null) {
			/* Skip child items */
			if (!myAccount.isOwner()) continue;
			
			/* Skip deleted items */
			if ((!doShowDeleted) &&
				(myAccount.isDeleted())) continue;
			
			/* Skip closed items if required */
			if ((!doShowClosed) && 
				(myAccount.isClosed())) continue;
			
			/* If the type of this account is new */
			if (AccountType.differs(myType, myAccount.getActType())) {
				/* Note the type */
				myType = myAccount.getActType();
				if (myFirst == null) myFirst = myType;
			
				/* Add the item to the list */
				theTypesBox.addItem(myType.getName());
				typesPopulated = true;
			}
		}
		
		/* If we have a selected type */
		if (getType() != null) {
			/* Select it in the new list */
			theTypesBox.setSelectedItem(getType().getName());
		}
		
		/* Else we have no type currently selected */
		else if (typesPopulated) {
			/* Select the first account type */
			theTypesBox.setSelectedIndex(0);
			theState.setType(myFirst);
		}

		/* Note that we have finished refreshing data */
		refreshingData = false;
	}
	
	/**
	 * build the accounts comboBox 
	 */
	private boolean buildAccounts() {
		Account     myAcct;
		Account     myFirst;
		Account     mySelected;
		Account     myOld;
		boolean		doShowDeleted;
		boolean		doShowClosed;
		AccountType	myType;
		
		DataList<Account>.ListIterator	myIterator;
		
		/* Access current values */
		doShowDeleted = doShowDeleted();
		doShowClosed  = doShowClosed();
		myType		  = getType();
		mySelected	  = getSelected();
		myOld	  	  = mySelected;

		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have accounts already populated */
		if (acctsPopulated) {	
			/* If we have a selected account */
			if (mySelected != null) {
				/* Find it in the new list */
				theState.setSelected(theAccounts.searchFor(mySelected.getName()));
				mySelected = getSelected();
			}
			
			/* Remove the accounts from the box */
			theAccountBox.removeAllItems();
			acctsPopulated = false;
		}
		
		/* If the selected item is no longer valid */
		if ((mySelected != null) &&
			(((doShowDeleted) &&
			  (mySelected.isDeleted())) ||
			 ((!doShowClosed) &&
			  (mySelected.isClosed())) ||
			 (myType.compareTo(mySelected.getActType()) != 0))) {
			/* Remove selection */
			theState.setSelected(null);
			mySelected = null;
		}
		
		/* Access the iterator */
		myIterator  = theAccounts.listIterator(true);
		myFirst		= null;
		
		/* Add the Account values to the types box */
		while ((myAcct  = myIterator.next()) != null) {
			/* Skip deleted items */
			if ((!doShowDeleted) &&
				(myAcct.isDeleted())) continue;
			
			/* Skip closed items if required */
			if ((!doShowClosed) && 
				(myAcct.isClosed())) continue;
			
			/* Skip items that are the wrong type */
			if (myType.compareTo(myAcct.getActType()) != 0)
			  continue;
			
			/* Note the first in the list */
			if (myFirst == null) myFirst = myAcct;
			
			/* Add the item to the list */
			theAccountBox.addItem(myAcct.getName());
			acctsPopulated = true;
		}
					
		/* If we have a selected account */
		if (mySelected != null) {
			/* Select it in the new list */
			theAccountBox.setSelectedItem(mySelected.getName());
		}
		
		/* Else we have no account currently selected */
		else if (acctsPopulated) {
			/* Select the first account */
			theAccountBox.setSelectedIndex(0);
			theState.setSelected(myFirst);
		}

		/* Note that we have finished refreshing data */
		refreshingData = false;
		
		/* Return whether we have changed selection */
		return Account.differs(getSelected(), myOld);
	}
	
	/**
	 *  Set account explicitly
	 *  @param pAccount the account
	 */
	public void setSelection(Account pAccount) {
		Account myAccount;
		
		/* Set the refreshing data flag */
		refreshingData = true;
		
		/* Access the edit-able account */
		myAccount = theAccounts.searchFor(pAccount.getName());
		
		/* Select the correct account type */
		theState.setType(pAccount.getActType());
		theTypesBox.setSelectedItem(getType().getName());
		
		/* If we need to show closed items */
		if ((!doShowClosed()) && (myAccount != null) && (myAccount.isClosed())) {
			/* Set the flag correctly */
			theState.setDoShowClosed(true);
			theShowClosed.setSelected(true);
		}
		
		/* If we need to show deleted items */
		if ((!doShowDeleted()) && (myAccount != null) && (myAccount.isDeleted())) {
			/* Set the flag correctly */
			theState.setDoShowDeleted(true);
			theShowDeleted.setSelected(true);
		}
		
		/* Select the account */
		theState.setSelected(myAccount);
		
		/* Reset the refreshing data flag */
		refreshingData = false;

		/* Build the accounts */
		buildAccounts();
	}

	/**
	 * Lock/Unlock the selection
	 */
	public void setLockDown() {
		boolean bLock 		= theParent.hasUpdates();
		Account mySelected 	= getSelected();
		
		/* Lock/Unlock the selection */
		theTypesBox.setEnabled(!bLock);
		theAccountBox.setEnabled(!bLock);
		
		/* Can't switch off show closed if account is closed */
		if ((mySelected != null) &&
			(mySelected.isClosed()))
			bLock = true;
		
		/* Lock Show Closed */
		theShowClosed.setEnabled(!bLock);
		
		/* Reset the lock */
		bLock = theParent.hasUpdates();
		
		/* Can't switch off show deleted if account is deleted */
		if ((mySelected != null) &&
			(mySelected.isDeleted()))
			bLock = true;
		
		/* Lock Show Deleted */
		theShowDeleted.setEnabled(!bLock);
	}
	
	/**
	 *  ItemStateChanged listener event
	 */
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
				theState.setType(theTypes.searchFor(myName));
				bChange = buildAccounts();
			}
		}
		
		/* If this event relates to the account box */
		if (evt.getSource() == (Object)theAccountBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Select the new account */						
				theState.setSelected(theAccounts.searchFor(myName));
				bChange     = true;
			}
		}
		
		/* If this event relates to the showClosed box */
		if (evt.getSource() == (Object)theShowClosed) {
			/* Note the new criteria and re-build lists */
			theState.setDoShowClosed(theShowClosed.isSelected());
			buildAccountTypes();
			bChange = buildAccounts();
		}
		
		/* If this event relates to the showDeleted box */
		if (evt.getSource() == (Object)theShowDeleted) {
			/* Note the new criteria and re-build lists */
			theState.setDoShowDeleted(theShowDeleted.isSelected());
			buildAccountTypes();
			bChange = buildAccounts();
		}
		
		/* If we have a change, alert the table */
		if (bChange) { theParent.notifySelection(this); }
	}
	
	/* SavePoint values */
	private class AccountState {
		/* Members */
		private AccountType   		theType			= null;
		private Account         	theSelected 	= null;
		private boolean				doShowClosed	= false;
		private boolean				doShowDeleted	= false;
		
		/* Access methods */
		private AccountType	getType()		{ return theType; }
		private Account		getSelected()	{ return theSelected; }
		private boolean		doShowClosed()  { return doShowClosed; }
		private boolean		doShowDeleted() { return doShowDeleted; }

		/**
		 * Constructor
		 */
		private AccountState() {}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private AccountState(AccountState pState) {
			theType 	= pState.getType();
			theSelected = pState.getSelected();
		}
		
		/**
		 * Set new Account Type
		 * @param pType the AccountType 
		 */
		private void setType(AccountType pType) {
			/* Adjust the type */
			theType = pType;
		}
		
		/**
		 * Set new Account
		 * @param pAccount the Account 
		 */
		private void setSelected(Account pAccount) {
			/* Adjust the selected account */
			theSelected = pAccount;
		}
		
		/**
		 * Set doShowClosed indication
		 */
		private void setDoShowClosed(boolean doShowClosed) {
			/* Adjust the flag */
			this.doShowClosed = doShowClosed;
		}
		
		/**
		 * Set doShowDeleted indication
		 */
		private void setDoShowDeleted(boolean doShowDeleted) {
			/* Adjust the flag */
			this.doShowDeleted = doShowDeleted;
		}
		
		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			setLockDown();
			theShowClosed.setSelected(doShowClosed);
			theShowDeleted.setSelected(doShowDeleted);
			theTypesBox.setSelectedItem((theType == null) ? null : theType.getName());
			theAccountBox.setSelectedItem((theSelected == null) ? null : theSelected.getName());
		}
	}
}
