package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;

public class MaintAccount implements ActionListener,
									 ItemListener,
									 ChangeListener,
									 financePanel {
	/* Properties */
	private MaintenanceTab		theParent		= null;
	private JPanel              thePanel		= null;
	private JPanel				theButtons		= null;
	private AccountSelect 		theSelect		= null;
	private SaveButtons  		theSaveButs   	= null;
	private JTextField			theName			= null;
	private JTextField			theDesc			= null;
	private JLabel				theFirst		= null;
	private JLabel				theLast			= null;
	private JComboBox			theTypesBox		= null;
	private JComboBox			theParentBox	= null;
	private JSpinner			theSpinner		= null;
	private SpinnerDateModel	theModel		= null;
	private JLabel				theTypLabel		= null;
	private JLabel				theParLabel		= null;
	private JLabel				theMatLabel		= null;
	private JButton				theInsButton	= null;
	private JButton				theDelButton	= null;
	private JButton				theClsButton	= null;
	private JButton				theUndoButton	= null;
	private Account				theAccount		= null;
	private Account.List		theAccounts		= null;
	private View.ViewAccount	theActView		= null;
	private AccountType.List	theAcTypList	= null;
	private boolean				refreshingData	= false;
	private boolean				typesPopulated	= false;
	private boolean				parPopulated	= false;
	private View				theView			= null;
	
	/* Access methods */
	public JPanel       getPanel()       { return thePanel; }
	public Account		getAccount()	 { return theAccount; }
	
	/* Constructor */
	public MaintAccount(MaintenanceTab pParent) {
		JLabel	myName;
		JLabel  myDesc;
		JLabel  myFirst;
		JLabel	myLast;
		
		/* Store passed data */
		theParent = pParent;

		/* Access the view */
		theView 	= pParent.getView();
				
		/* Create the labels */
		myName 	 	= new JLabel("Name:");
		myDesc 	 	= new JLabel("Description:");
		theTypLabel = new JLabel("AccountType:");
		theMatLabel	= new JLabel("Maturity:");
		theParLabel	= new JLabel("Parent:");
		myFirst	 	= new JLabel("FirstEvent:");
		myLast 	 	= new JLabel("LastEvent:");
		theFirst 	= new JLabel();
		theLast  	= new JLabel();
		
		/* Create the text fields */
		theName  = new JTextField();
		theDesc  = new JTextField();
		
		/* Create the combo boxes */
		theTypesBox  = new JComboBox();
		theParentBox = new JComboBox();
		
		/* Create the spinner */
		theModel    = new SpinnerDateModel();
		theSpinner  = new JSpinner(theModel);
		theSpinner.setEditor(new JSpinner.DateEditor(theSpinner, "dd-MMM-yyyy"));
		
		/* Create the buttons */
		theInsButton  = new JButton("New");
		theDelButton  = new JButton();
		theClsButton  = new JButton();
		theUndoButton = new JButton("Undo");
		
		/* Add listeners */
		theName.addActionListener(this);
		theDesc.addActionListener(this);
		theTypesBox.addItemListener(this);
		theParentBox.addItemListener(this);
		theModel.addChangeListener(this);
		theInsButton.addActionListener(this);
		theDelButton.addActionListener(this);
		theClsButton.addActionListener(this);
		theUndoButton.addActionListener(this);
		
		/* Create the Account Selection panel */
		theSelect = new AccountSelect(theView, this, true);
		
		/* Create the Save buttons panel */
		theSaveButs = new SaveButtons(this);
		
		/* Create the buttons panel */
		theButtons = new JPanel();
		theButtons.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Actions"));
		
		/* Create the layout for the panel */
	    GroupLayout myLayout = new GroupLayout(theButtons);
	    theButtons.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addContainerGap()
                .addComponent(theInsButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theUndoButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theDelButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theClsButton)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(theInsButton)
            .addComponent(theUndoButton)
            .addComponent(theDelButton)
            .addComponent(theClsButton)
        );
            
		/* Create the panel */
		thePanel = new JPanel();
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(myLayout);
		    
	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(theSelect.getPanel())
                	.addComponent(theSaveButs.getPanel())
                    .addComponent(theButtons)
                    .addGroup(myLayout.createSequentialGroup()
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(theTypLabel)
                            .addComponent(myName)
                            .addComponent(myDesc)
                            .addComponent(theMatLabel)
                            .addComponent(myFirst))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                            .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(myLayout.createSequentialGroup()
                            	.addComponent(theName, GroupLayout.PREFERRED_SIZE, 200, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(theParLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(theParentBox, 75, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(theDesc, GroupLayout.PREFERRED_SIZE, 650, GroupLayout.PREFERRED_SIZE)
                            .addGroup(myLayout.createSequentialGroup()
                            	.addComponent(theFirst, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(myLast)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(theLast, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE))
                            .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(theSelect.getPanel())
                    .addContainerGap(10,30)
                    .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(theTypLabel)
                        .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(myName)
                        .addComponent(theName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(theParLabel)
                        .addComponent(theParentBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(myDesc)
                        .addComponent(theDesc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(theMatLabel)
                        .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(10,30)
                    .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(myFirst)
                        .addComponent(theFirst, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(myLast)
                        .addComponent(theLast, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(10,30)
                    .addComponent(theButtons)
                    .addContainerGap(10,30)
                    .addComponent(theSaveButs.getPanel())
                    .addContainerGap())
            );
            
            /* Set initial display */
            showAccount();
	}
	
	/* hasUpdates */
	public boolean hasUpdates() {
		return ((theAccount != null) && (theAccount.hasChanges()));
	}
	
	/* hasErrors */
	public boolean hasErrors() {
		return ((theAccount != null) && (theAccount.hasErrors()));
	}
	
	/* isLocked */
	public boolean isLocked() { return false; }
	
	/* getEditState */
	public EditState getEditState() {
		if (theAccount == null) return EditState.CLEAN;
		return theAccount.getEditState();
	}
	
	/* performCommand */
	public void performCommand(financeCommand pCmd) {
		/* Switch on command */
		switch (pCmd) {
			case OK:
				saveData();
				break;
			case RESETALL:
				resetData();
				break;
			case VALIDATEALL:
				validate();
				break;
		}
		notifyChanges();			
	}
	
	/* Note that changes have been made */
	public void notifyChanges() {
		/* Lock down the table buttons and the selection */
		theSaveButs.setLockDown();
		theSelect.setLockDown();
		
		/* Show the account */
		showAccount();
		
		/* Adjust visible tabs */
		theParent.setVisibleTabs();
	}	
		
	/* resetData */
	public void resetData() {
		theAccount.clearErrors();
		theAccount.resetHistory();
		theAccount.validate();
		
		/* if this is a new account */
		if (theAccount.getState() == DataState.NEW) {
			/* Delete the new account */
			delNewAccount();
		}
	}
	
	/* print */
	public void printIt() {}
	
	/* validate */
	public void validate() {
		theAccount.clearErrors();
		theAccount.validate();
	}
	
	/* saveData */
	public void saveData() {
		/* Validate the data */
		validate();
		if (!hasErrors()) {
			/* Save details for the account */
			if (theActView != null)	theActView.applyChanges();
		}
	}
		
	/* refreshData */
	public void refreshData() {
		DataSet			myData;
		Account	  	  	myAcct;
		AccountType 	myType;
	
		DataList<AccountType>.ListIterator 	myTypeIterator;
		DataList<Account>.ListIterator		myActIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access type */
		theAcTypList = myData.getAccountTypes();
		theAccounts	 = myData.getAccounts();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* Refresh the account selection */
		theSelect.refreshData();
			
		/* If we have types already populated */
		if (typesPopulated) {	
			/* Remove the types */
			theTypesBox.removeAllItems();
			typesPopulated = false;
		}
		
		/* Create an account type iterator */
		myTypeIterator = theAcTypList.listIterator();
		
		/* Add the AccountType values to the types box */
		while ((myType  = myTypeIterator.next()) != null) {
			/* Ignore the account if it is reserved */
			if (myType.isReserved()) continue;
			
			/* Add the item to the list */
			theTypesBox.addItem(myType.getName());
			typesPopulated = true;
		}
		
		/* If we have parents already populated */
		if (parPopulated) {	
			/* Remove the types */
			theParentBox.removeAllItems();
			parPopulated = false;
		}
		
		/* Create an account iterator */
		myActIterator = theAccounts.listIterator();
		
		/* Add the Account values to the parents box */
		while ((myAcct  = myActIterator.next()) != null) {
			/* Access the type */
			myType = myAcct.getActType();
			
			/* Ignore the account if it is not internal */
			if (myType.isInternal()) continue;
			
			/* Ignore the account if it is special external */
			if (myType.isSpecial()) continue;
			
			/* Ignore the account if it is closed */
			if (myAcct.isClosed()) continue;
			
			/* Add the item to the list */
			theParentBox.addItem(myAcct.getName());
			parPopulated = true;
		}
					
		/* Note that we have finished refreshing data */
		refreshingData = false;
		
		/* Adjust the account selection */
		if (theAccount != null) theSelect.setSelection(theAccount);

		/* Show the account */
		setSelection(theSelect.getSelected());
	}
	
	/* Note that there has been a selection change */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the account selection */
		if (obj == (Object) theSelect) {
			/* Set the new account */
			setSelection(theSelect.getSelected());
		}
	}
	
	/* Select an explicit account and period */
	public void selectAccount(Account  pAccount) {
		/* Adjust the account selection */
		theSelect.setSelection(pAccount);
		
		/* Redraw selection */
		setSelection(theSelect.getSelected());
	}
	
	/* Set Selection */
	public void setSelection(Account pAccount) {
		/* Reset controls */
		theActView = null;
		theAccount = null;
		
		/* If we have a selected account */
		if (pAccount != null) {
			/* Create the view of the account */
			theActView = theView.new ViewAccount(pAccount);
		
			/* Access the account */
			theAccount = theActView.getAccount();
		}
		
		/* notify changes */
		notifyChanges();
	}
	
	private void showAccount() {
		AccountType 	myType;
		boolean			isClosed;
		
		/* If we have an active account */
		if (theAccount != null) {
			/* Access details */
			myType 	 = theAccount.getActType();
			isClosed = theAccount.isClosed();
			
			/* Set the name */
			theName.setText(theAccount.getName() != null ? 
								theAccount.getName() : "");
			theParent.formatComponent(theName, Account.FIELD_NAME, theAccount, false, (theAccount.getName() == null));
		
			/* Set the description */
			theDesc.setText(theAccount.getDesc() != null ? 
								theAccount.getDesc() : "");
			theDesc.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theDesc, Account.FIELD_DESC, theAccount, false, (theAccount.getDesc() == null));
		
			/* Set the type */
			theTypesBox.setSelectedItem(myType.getName());
			theTypesBox.setVisible(theAccount.isDeletable() && (theAccount.getState() == DataState.NEW));
			theTypLabel.setVisible(theAccount.isDeletable() && (theAccount.getState() == DataState.NEW));
			theParent.formatComponent(theTypesBox, Account.FIELD_TYPE, theAccount, false, false);
		
			/* Handle maturity */
			if (myType.isBond()) {
				theModel.setValue(theAccount.getMaturity().getDate());
				theSpinner.setVisible(true);
				theMatLabel.setVisible(true);
				theSpinner.setEnabled(!isClosed);
				theParent.formatComponent(theSpinner, Account.FIELD_MATURITY, theAccount, false, false);
			}
			else {
				theSpinner.setVisible(false);
				theMatLabel.setVisible(false);
			}
		
			/* Handle parent */
			if (myType.isChild()) {
				if (theAccount.getParent() != null)
					theParentBox.setSelectedItem(theAccount.getParent().getName());
				else
					theParentBox.setSelectedItem(null);
				theParentBox.setVisible(true);
				theParLabel.setVisible(true);
				theParent.formatComponent(theParentBox, Account.FIELD_PARENT, theAccount, 
										  false, (theAccount.getParent() == null));
			}
			else {
				theParentBox.setVisible(false);
				theParLabel.setVisible(false);
			}
			theParentBox.setEnabled(!isClosed && !theAccount.isDeleted());

			/* Set the First Event */
			theFirst.setText((theAccount.getEarliest() != null)
								? theAccount.getEarliest().getDate().formatDate(false) 
								: "");
			
			/* Set the First Event */
			theLast.setText((theAccount.getLatest() != null)
								? theAccount.getLatest().getDate().formatDate(false) 
								: "");
			
			/* Set text for close button */
			theClsButton.setText((isClosed) ? "ReOpen" : "Close");
			
			/* Make sure buttons are visible */
			theDelButton.setVisible(theAccount.isDeletable());
			theDelButton.setText("Delete");
			theClsButton.setVisible(true);
			
			/* Enable buttons */
			theInsButton.setEnabled(!theAccount.hasChanges() && (!theAccount.getActType().isReserved()));
			theClsButton.setEnabled((isClosed) || (theAccount.isCloseable()));
			theUndoButton.setEnabled(theAccount.hasChanges());
		}
		
		/* else no account selected */
		else {
			/* Clear details */
			theName.setText("");
			theDesc.setText("");
			theFirst.setText("");
			theLast.setText("");
			
			/* Disable field entry */
			theName.setEnabled(false);
			theDesc.setEnabled(false);
			theClsButton.setVisible(false);
			theUndoButton.setEnabled(false);
			
			theInsButton.setEnabled(theAccount != null);
			theDelButton.setVisible(theAccount != null);
			theDelButton.setText("Recover");
			
			/* Hide parent and maturity */
			theParLabel.setVisible(false);
			theParentBox.setVisible(false);
			theSpinner.setVisible(false);
			theMatLabel.setVisible(false);
			theTypLabel.setVisible(false);
			theTypesBox.setVisible(false);
		}
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String                myName;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* Push history */
		theAccount.pushHistory();
		
		/* If this event relates to the period box */
		if (evt.getSource() == (Object)theTypesBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Store the appropriate value */
				theAccount.setActType(theAcTypList.searchFor(myName));
				
				/* If the account is now a bond */
				if (theAccount.isBond()) {
					/* If it doesn't have a maturity */
					if (theAccount.getMaturity() == null) {
						/* Create a default maturity */
						theAccount.setMaturity(new Date());
						theAccount.getMaturity().adjustYear(1);
					}
				}
				
				/* Else set maturity to null for non-bonds */
				else theAccount.setMaturity(null);	
				
				/* Set parent to null for non-child accounts */
				if (!theAccount.isChild()) theAccount.setParent(null);
				
				/* Update text */
				updateText();
			}
		}
	
		/* If this event relates to the period box */
		else if (evt.getSource() == (Object)theParentBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Store the appropriate value */
				theAccount.setParent(theAccounts.searchFor(myName));
				
				/* Update text */
				updateText();
			}
		}

		/* Check for changes */
		if (theAccount.checkForHistory()) {
			/* Note that the item has changed */
			theAccount.setState(DataState.CHANGED);
			
			/* Note that changes have occurred */
			notifyChanges();
		}
	}

	/* Delete New Account */
	private void delNewAccount() {
		/* Set the previously selected account */
		setSelection(theSelect.getSelected());
	}
	
	/* New Account */
	private void newAccount() {
		/* Create a account View for an empty account */
		theActView = theView.new ViewAccount(theSelect.getType());
	
		/* Access the account */
		theAccount = theActView.getAccount();			
		
		/* Notify changes */
		notifyChanges();
	}
	
	/* Update text */
	private void updateText() {
		String  myText;

		/* Access the value */
		myText = theName.getText();
		if (myText.length() == 0) myText = null;
		
		/* Store the appropriate value */
		theAccount.setAccountName(myText);    

		/* Access the value */
		myText = theDesc.getText();
		if (myText.length() == 0) myText = null;
		
		/* Store the appropriate value */
		theAccount.setDescription(myText);    			
	}
	
	/* Undo changes */
	private void undoChanges() {
		/* If the account has changes */
		if (theAccount.hasHistory()) {
			/* Pop last value */
			theAccount.popHistory();
			
			/* Re-validate the item */
			theAccount.clearErrors();
			theAccount.validate();
		
			/* If the item is now clean */
			if (!theAccount.hasHistory()) {
				/* Set the new status */
				theAccount.setState(DataState.CLEAN);
			}
			
			/* Notify changes */
			notifyChanges();
		}
		
		/* else if this is a new account */
		else if (theAccount.getState() == DataState.NEW) {
			/* Delete the new account */
			delNewAccount();
		}
	}
	
	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {			
		/* If this event relates to the new button */
		if (evt.getSource() == (Object)theInsButton) {
			/* Create the new account */
			newAccount();
			return;
		}
		
		/* If this event relates to the del button */
		else if (evt.getSource() == (Object)theDelButton) {
			/* else if this is a new account */
			if (theAccount.getState() == DataState.NEW) {
				/* Delete the new account */
				delNewAccount();
			}
			
			/* Else we should just delete/recover the account */
			else {
				/* Set the appropriate state */
				theAccount.setState(theAccount.isDeleted() ? DataState.RECOVERED
														   : DataState.DELETED);
				
				/* Notify changes */
				notifyChanges();
			}
		}
		
		/* If this event relates to the undo button */
		else if (evt.getSource() == (Object)theUndoButton) {
			/* Undo the changes */
			undoChanges();
			return;
		}
		
		/* Push history */
		theAccount.pushHistory();
		
		/* If this event relates to the name field */
		if ((evt.getSource() == (Object)theName) ||
		    (evt.getSource() == (Object)theDesc)) {
			/* Update the text */
			updateText();
		}
		
		/* If this event relates to the close button */
		else if (evt.getSource() == (Object)theClsButton) {
			/* Re-open or close the account as required */
			if (theAccount.isClosed()) 	theAccount.reOpenAccount();
			else						theAccount.closeAccount();
			
			/* Update the text */
			updateText();
		}
		
		/* Check for changes */
		if (theAccount.checkForHistory()) {
			/* Note that the item has changed */
			theAccount.setState(DataState.CHANGED);
			
			/* Note that changes have occurred */
			notifyChanges();
		}
	}	
	
	/* stateChanged listener event */
	public void stateChanged(ChangeEvent evt) {
		Date myDate;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the maturity box */
		if (evt.getSource() == (Object)theModel) {
			/* Access the value */
			myDate = new Date(theModel.getDate());

			/* Push history */
			theAccount.pushHistory();
			
			/* Store the appropriate value */
			theAccount.setMaturity(myDate);    
		
			/* Update the text */
			updateText();
			
			/* Check for changes */
			if (theAccount.checkForHistory()) {
				/* Note that the item has changed */
				theAccount.setState(DataState.CHANGED);
				
				/* Note that changes have occurred */
				notifyChanges();
			}
		}								
	}
}
