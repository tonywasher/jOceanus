package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ui.DateButton;
import uk.co.tolcroft.models.ui.StdInterfaces.*;

public class SpotSelect {
	/* Members */
	private SpotSelect				theSelf			= this;
	private JPanel					thePanel		= null;
	private stdPanel  				theParent		= null;
	private View					theView			= null;
	private DateButton				theDateButton	= null;
	private JCheckBox				theShowClosed   = null;
	private JButton					theNext   		= null;
	private JButton					thePrev   		= null;
	private JComboBox				theTypesBox 	= null;
	private AccountType.List		theTypes 		= null;
	private SpotState				theState		= null;
	private SpotState				theSavePoint	= null;
	private boolean					doShowClosed	= false;
	private boolean					typesPopulated	= false;
	private boolean					refreshingData	= false;
	
	/* Access methods */
	public JPanel   	getPanel()  		{ return thePanel; }
	public DateDay		getDate()			{ return theState.getDate(); }
	public AccountType	getAccountType()	{ return theState.getAccountType(); }
	public boolean		getShowClosed() 	{ return doShowClosed; }
				
	/* Constructor */
	public SpotSelect(View pView, stdPanel pTable) {
		/* Create listener */
		SpotListener myListener = new SpotListener();
		
		/* Store table and view details */
		theView 	  = pView;
		theParent	  = pTable;
		
		/* Create Labels */
		JLabel myDate	= new JLabel("Date:");
		JLabel myAct	= new JLabel("AccountType:");
		
		/* Create the check box */
		theShowClosed = new JCheckBox("Show Closed");
		theShowClosed.setSelected(doShowClosed);
		
		/* Create the DateButton */
		theDateButton	= new DateButton();
		
		/* Create the Buttons */
		theNext   	= new JButton("Next");
		thePrev		= new JButton("Prev");
		
		/* Create the Type box */
		theTypesBox	= new JComboBox();
		
		/* Create initial state */
		theState = new SpotState();
	
		/* Initialise the data from the view */
		refreshData();
		
		/* Create the panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Spot Selection"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(myDate, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(theNext)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(thePrev)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(myAct)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theTypesBox)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(theShowClosed))
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(myDate)
	                .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theNext)
	                .addComponent(thePrev)
	                .addComponent(myAct)
	                .addComponent(theTypesBox)
	                .addComponent(theShowClosed))
	    );

		/* Apply the current state */
		theState.applyState();

		/* Add the listener for item changes */
		theDateButton.addPropertyChangeListener(DateButton.valueDATE, myListener);
		theShowClosed.addItemListener(myListener);
		theNext.addActionListener(myListener);
		thePrev.addActionListener(myListener);
		theTypesBox.addItemListener(myListener);
	}
	
	/**
	 * Refresh data 
	 */
	public void refreshData() {
		DateDay.Range   			myRange;
		AccountType					myType	= null;
		AccountType					myFirst	= null;
		Account						myAccount;
		Account.List				myAccounts;
		Account.List.ListIterator	myIterator;
		
		/* Access the data */
		myRange = theView.getRange();
		
		/* Set the range for the Date Button */
		setRange(myRange);				

		/* Access the data */
		FinanceData myData = theView.getData();
		
		/* Access types and accounts */
		theTypes    = myData.getAccountTypes();
		myAccounts 	= myData.getAccounts();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have types already populated */
		if (typesPopulated) {	
			/* If we have a selected type */
			if (getAccountType() != null) {
				/* Find it in the new list */
				theState.setType(theTypes.searchFor(getAccountType().getName()));
			}
			
			/* Remove the types */
			theTypesBox.removeAllItems();
			typesPopulated = false;
		}
		
		/* Access the iterator */
		myIterator = myAccounts.listIterator(true);
		
		/* Loop through the non-owner accounts */
		while ((myAccount = myIterator.next()) != null) {
			/* Skip non-priced items */
			if (!myAccount.isPriced()) continue;
			
			/* Skip deleted items */
			if (myAccount.isDeleted()) continue;
			
			/* Skip alias items */
			if (myAccount.isAlias()) continue;
			
			/* Skip closed items if required */
			if ((!doShowClosed) && 
				(myAccount.isClosed())) continue;
			
			/* If the type of this account is new */
			if (AccountType.differs(myType, myAccount.getActType()).isDifferent()) {
				/* Note the type */
				myType = myAccount.getActType();
				if (myFirst == null) myFirst = myType;
			
				/* Add the item to the list */
				theTypesBox.addItem(myType.getName());
				typesPopulated = true;
			}
		}
		
		/* If we have a selected type */
		if (getAccountType() != null) {
			/* Select it in the new list */
			theTypesBox.setSelectedItem(getAccountType().getName());
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
	 * Set the range for the date box
	 * @param pRange the Range to set
	 */
	public  void setRange(DateDay.Range pRange) {
		DateDay myStart = (pRange == null) ? null : pRange.getStart();
		DateDay myEnd   = (pRange == null) ? null : pRange.getEnd();
		
		/* Set up range */
		theDateButton.setSelectableRange((myStart == null) ? null : myStart.getDate(),
										 (myEnd == null) ? null : myEnd.getDate());
	}
	
	/* Lock/Unlock the selection */
	public void setLockDown() {
		boolean bLock = theParent.hasUpdates();
		
		theNext.setEnabled(theState.getNextDate() != null);
		thePrev.setEnabled(theState.getPrevDate() != null);
		
		theDateButton.setEnabled(!bLock);
		theTypesBox.setEnabled(!bLock);
	}
	
	/**
	 *  Create SavePoint
	 */
	public void createSavePoint() {
		/* Create the savePoint */
		theSavePoint = new SpotState(theState);
	}

	/**
	 *  Restore SavePoint
	 */
	public void restoreSavePoint() {
		/* Restore the savePoint */
		theState = new SpotState(theSavePoint);
		
		/* Apply the state */
		theState.applyState();		
	}

	/**
	 * Set Adjacent dates
	 * @param pPrev the previous Date
	 * @param pNext the next Date
	 */
	public void setAdjacent(DateDay pPrev, DateDay pNext) {
		/* Record the dates */
		theState.setAdjacent(pPrev, pNext);
	}

	/**
	 * Listener class
	 */
	private class SpotListener implements ActionListener, 
										  PropertyChangeListener,
										  ItemListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			Object o = evt.getSource();

			/* If this event relates to the Next button */
			if (o == theNext) {
				/* Set the Date to be the Next date */
				theState.setNext();
			}
		
			/* If this event relates to the previous button */
			else if (o == thePrev) {
				/* Set the Date to be the Previous date */
				theState.setPrev();
			}

			/* No need to notify the parent since this will have been done by state update */
		}
	
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			/* if this date relates to the Date button */
			if (evt.getSource() == theDateButton) {
				/* Access the value */
				if (theState.setDate(theDateButton))
					theParent.notifySelection(theSelf);
			}			
		}
		
		@Override
		public void itemStateChanged(ItemEvent evt) {
			boolean	bChange = false;
			Object	o		= evt.getSource();

			/* Ignore selection if refreshing data */
			if (refreshingData) return;
			
			/* If this event relates to the showClosed box */
			if (o == theShowClosed) {
				/* Note the new criteria and re-build lists */
				doShowClosed = theShowClosed.isSelected();
				bChange      = true;
			}
		
			/* If this event relates to the Account Type box */
			else if ((o == theTypesBox) &&
					 (evt.getStateChange() == ItemEvent.SELECTED)) {
				String myName = (String)evt.getItem();

				/* Select the new type */
				theState.setType(theTypes.searchFor(myName));
				bChange = true;
			}
			
			/* If we have a change, alert the table */
			if (bChange) { theParent.notifySelection(theSelf); }
		}
	}
	
	/**
	 *  SavePoint values
	 */
	private class SpotState {
		/* Members */
		private AccountType	theType		= null;
		private DateDay		theDate		= null;
		private DateDay		theNextDate	= null;
		private DateDay		thePrevDate	= null;
		
		/* Access methods */
		private AccountType	getAccountType() 	{ return theType; }
		private DateDay		getDate() 			{ return theDate; }
		private DateDay 	getNextDate() 		{ return theNextDate; }
		private DateDay 	getPrevDate() 		{ return thePrevDate; }

		/**
		 * Constructor
		 */
		private SpotState() {
			theDate = new DateDay();
		}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private SpotState(SpotState pState) {
			theType		= pState.getAccountType();
			theDate 	= new DateDay(pState.getDate());
			if (pState.getNextDate() != null)
				theNextDate = new DateDay(pState.getNextDate());
			if (pState.getPrevDate() != null)
				thePrevDate = new DateDay(pState.getPrevDate());
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
		 * Set new Date
		 * @param pButton the Button with the new date 
		 */
		private boolean setDate(DateButton pButton) {
			/* Adjust the date and build the new range */
			DateDay myDate = new DateDay(pButton.getSelectedDate());
			if (DateDay.differs(myDate, theDate).isDifferent()) {
				theDate = myDate;
				return true;
			}
			return false;
		}
		
		/**
		 * Set Next Date
		 */
		private void setNext() {
			/* Copy date */
			theDate = new DateDay(theNextDate);
			applyState();
		}

		/**
		 * Set Previous Date
		 */
		private void setPrev() {
			/* Copy date */
			theDate = new DateDay(thePrevDate);
			applyState();
		}

		/**
		 * Set Adjacent dates
		 * @param pPrev the previous Date
		 * @param pNext the next Date
		 */
		private void setAdjacent(DateDay pPrev, DateDay pNext) {
			/* Record the dates */
			thePrevDate = pPrev;
			theNextDate = pNext;
			
			/* Adjust values */
			setLockDown();				
		}

		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			setLockDown();
			theDateButton.setSelectedDate(theDate.getDate());
			theTypesBox.setSelectedIndex(-1);
			if (theType != null) theTypesBox.setSelectedItem(theType.getName());
		}
	}	
}
