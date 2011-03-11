package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class MaintAccount implements ActionListener,
									 ItemListener,
									 ChangeListener,
									 financePanel {
	/* Properties */
	private MaintenanceTab		theParent		= null;
	private JPanel              thePanel		= null;
	private JPanel              theDetail		= null;
	private JPanel              theStatus		= null;
	private JPanel				theButtons		= null;
	private JPanel				theSecure		= null;
	private AccountSelect 		theSelect		= null;
	private SaveButtons  		theSaveButs   	= null;
	private JTextField			theName			= null;
	private JTextField			theDesc			= null;
	private JPasswordField		theWebSite		= null;
	private JPasswordField		theCustNo		= null;
	private JPasswordField		theUserId		= null;
	private JPasswordField		thePassword		= null;
	private JPasswordField		theActDetail	= null;
	private JPasswordField		theNotes		= null;
	private JLabel				theFirst		= null;
	private JLabel				theLast			= null;
	private JComboBox			theTypesBox		= null;
	private JComboBox			theParentBox	= null;
	private JComboBox			theAliasBox		= null;
	private JSpinner			theSpinner		= null;
	private SpinnerDateModel	theModel		= null;
	private JLabel				theTypLabel		= null;
	private JLabel				theParLabel		= null;
	private JLabel				theAlsLabel		= null;
	private JLabel				theMatLabel		= null;
	private JButton				theInsButton	= null;
	private JButton				theDelButton	= null;
	private JButton				theClsButton	= null;
	private JButton				theUndoButton	= null;
	private Account				theAccount		= null;
	private Account.List		theAccounts		= null;
	private View.ViewAccount	theActView		= null;
	private AccountType.List	theAcTypList	= null;
	private DebugEntry			theDebugEntry	= null;
	private ErrorPanel			theError		= null;
	private boolean				refreshingData	= false;
	private boolean				typesPopulated	= false;
	private boolean				parPopulated	= false;
	private boolean				alsPopulated	= false;
	private View				theView			= null;
	
	/* Access methods */
	public JPanel       getPanel()       { return thePanel; }
	public Account		getAccount()	 { return theAccount; }
	
	/* Access the debug entry */
	public DebugEntry 	getDebugEntry()		{ return theDebugEntry; }
	public DebugManager getDebugManager() 	{ return theParent.getDebugManager(); }
	
	/* Constructor */
	public MaintAccount(MaintenanceTab pParent) {
		JLabel	myName;
		JLabel  myDesc;
		JLabel  myFirst;
		JLabel	myLast;
		JLabel	myWebSite;
		JLabel  myCustNo;
		JLabel	myUserId;
		JLabel  myPassword;
		JLabel	myAccount;
		JLabel  myNotes;
		String	myDefValue;
		char[]	myDefChars;
		
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
		theAlsLabel	= new JLabel("Alias:");
		myFirst	 	= new JLabel("FirstEvent:");
		myLast 	 	= new JLabel("LastEvent:");
		theFirst 	= new JLabel("01-01-2000");
		theLast  	= new JLabel("01-01-2000");
		myWebSite 	= new JLabel("WebSite:");
		myCustNo 	= new JLabel("CustomerNo:");
		myUserId 	= new JLabel("UserId:");
		myPassword 	= new JLabel("Password:");
		myAccount 	= new JLabel("Account:");
		myNotes  	= new JLabel("Notes:");
		
		/* Create the text fields */
		theName  = new JTextField(Account.NAMELEN);
		theDesc  = new JTextField(Account.DESCLEN);
		
		/* Create the password fields */
		theWebSite		= new JPasswordField(Account.WSITELEN);
		theCustNo  		= new JPasswordField(Account.CUSTLEN);
		theUserId		= new JPasswordField(Account.UIDLEN);
		thePassword		= new JPasswordField(Account.PWDLEN);
		theActDetail	= new JPasswordField(Account.ACTLEN);
		theNotes	  	= new JPasswordField(Account.WSITELEN);
		
		/* Display the values in the password fields */
		theWebSite.setEchoChar((char)0);
		theCustNo.setEchoChar((char)0);
		theUserId.setEchoChar((char)0);
		thePassword.setEchoChar((char)0);
		theActDetail.setEchoChar((char)0);
		theNotes.setEchoChar((char)0);
		
		/* Create the combo boxes */
		theTypesBox  = new JComboBox();
		theParentBox = new JComboBox();
		theAliasBox  = new JComboBox();
		
		/* Dimension the account boxes correctly */
		myDefChars = new char[Account.NAMELEN];
		Arrays.fill(myDefChars, 'X');
		myDefValue = new String(myDefChars);
		theParentBox.setPrototypeDisplayValue(myDefValue);
		theAliasBox.setPrototypeDisplayValue(myDefValue);
		
		/* Dimension the account type box correctly */
		myDefChars = new char[AccountType.NAMELEN];
		Arrays.fill(myDefChars, 'X');
		myDefValue = new String(myDefChars);
		theTypesBox.setPrototypeDisplayValue(myDefValue);
		
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
		theWebSite.addActionListener(this);
		theCustNo.addActionListener(this);
		theUserId.addActionListener(this);
		thePassword.addActionListener(this);
		theActDetail.addActionListener(this);
		theNotes.addActionListener(this);
		theTypesBox.addItemListener(this);
		theParentBox.addItemListener(this);
		theAliasBox.addItemListener(this);
		theModel.addChangeListener(this);
		theInsButton.addActionListener(this);
		theDelButton.addActionListener(this);
		theClsButton.addActionListener(this);
		theUndoButton.addActionListener(this);
		
		/* Create the Account Selection panel */
		theSelect = new AccountSelect(theView, this, true);
		
		/* Create the Save buttons panel */
		theSaveButs = new SaveButtons(this);
		
        /* Create the debug entry, attach to MaintenanceDebug entry */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("Account");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
      
        /* Create the error panel for this view */
        theError = new ErrorPanel(this);
        
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
            
		/* Create the secure panel */
		theSecure = new JPanel();
		theSecure.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Security Details"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theSecure);
	    theSecure.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
           	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(myLayout.createSequentialGroup()
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(myWebSite)
                            .addComponent(myCustNo)
                            .addComponent(myUserId)
                            .addComponent(myNotes))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(theWebSite)
                            .addComponent(theNotes)
                            .addGroup(myLayout.createSequentialGroup()
                                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                	.addComponent(theCustNo, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                	.addComponent(theUserId, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                	.addComponent(myAccount, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                	.addComponent(myPassword, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                	.addComponent(theActDetail, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
                                	.addComponent(thePassword, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(myLayout.createSequentialGroup()
            	.addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                	.addComponent(myWebSite)
                    .addComponent(theWebSite, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myCustNo)
                    .addComponent(theCustNo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(myAccount)
                    .addComponent(theActDetail, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myUserId)
                    .addComponent(theUserId, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                	.addComponent(myPassword)
                    .addComponent(thePassword, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myNotes)
                    .addComponent(theNotes, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
        );
            
		/* Create the detail panel */
		theDetail = new JPanel();
		theDetail.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Account Details"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theDetail);
	    theDetail.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                     .addGroup(myLayout.createSequentialGroup()
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(theTypLabel)
                            .addComponent(myName)
                            .addComponent(myDesc)
                            .addComponent(theParLabel))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theDesc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                            .addGroup(myLayout.createSequentialGroup()
                               	.addComponent(theName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(theMatLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                            .addGroup(myLayout.createSequentialGroup()
                               	.addComponent(theParentBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(theAlsLabel)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(theAliasBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(theTypLabel)
                    .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myName)
                    .addComponent(theName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(theMatLabel)
                    .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myDesc)
                    .addComponent(theDesc, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(theParLabel)
                    .addComponent(theParentBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(theAlsLabel)
                    .addComponent(theAliasBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
               .addContainerGap())
        );
            
		/* Create the status panel */
		theStatus = new JPanel();
		theStatus.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Account Status"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theStatus);
	    theStatus.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                     .addGroup(myLayout.createSequentialGroup()
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(myFirst)
                            .addComponent(myLast))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(theFirst)
                            .addComponent(theLast))))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myFirst)
                    .addComponent(theFirst, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                 .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(myLast)
                    .addComponent(theLast, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
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
                   	.addComponent(theError.getPanel())
                   	.addComponent(theSelect.getPanel())
                	.addComponent(theSaveButs.getPanel())
                    .addGroup(myLayout.createSequentialGroup()
                    		.addComponent(theDetail)
                    		.addComponent(theStatus))
                	.addComponent(theSecure)
                    .addComponent(theButtons))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(theError.getPanel())
                .addComponent(theSelect.getPanel())
                .addContainerGap(10,30)
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                	.addComponent(theDetail)
                	.addComponent(theStatus))
                .addContainerGap(10,30)
                .addComponent(theSecure)
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
	
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);

		/* Lock data areas */
		theDetail.setEnabled(!isError);
		theSecure.setEnabled(!isError);

		/* Lock row/tab buttons area */
		theButtons.setEnabled(!isError);
		theSaveButs.getPanel().setEnabled(!isError);
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
			theDebugEntry.setObject(theActView);
			
			/* Access the account */
			theAccount = theActView.getAccount();
		}
		
		/* notify changes */
		notifyChanges();
	}
	
	private void showAccount() {
		AccountType 	myType;
		boolean			isClosed;

		/* Access the type from the selection */
		myType = theSelect.getType();
		
		/* If we have an active account */
		if (theAccount != null) {
			/* Note that we are refreshing data */
			refreshingData = true;
			
			/* Access details */
			isClosed = theAccount.isClosed();
			
			/* Set the name */
			theName.setText(theAccount.getName() != null ? 
								theAccount.getName() : "");
			theName.setEnabled(true);
			theParent.formatComponent(theName, Account.FIELD_NAME, theAccount, false, (theAccount.getName() == null));
		
			/* Set the description */
			theDesc.setText(theAccount.getDesc() != null ? 
								theAccount.getDesc() : "");
			theDesc.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theDesc, Account.FIELD_DESC, theAccount, false, (theAccount.getDesc() == null));
		
			/* Set the WebSite */
			theWebSite.setText(theAccount.getWebSite() != null ? 
							   new String(theAccount.getWebSite()) : "");
			theWebSite.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theWebSite, Account.FIELD_WEBSITE, theAccount, false, (theAccount.getWebSite() == null));
		
			/* Set the CustNo */
			theCustNo.setText(theAccount.getCustNo() != null ? 
							  new String(theAccount.getCustNo()) : "");
			theCustNo.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theCustNo, Account.FIELD_CUSTNO, theAccount, false, (theAccount.getCustNo() == null));
		
			/* Set the UserId */
			theUserId.setText(theAccount.getUserId() != null ? 
							  new String(theAccount.getUserId()) : "");
			theUserId.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theUserId, Account.FIELD_USERID, theAccount, false, (theAccount.getUserId() == null));
		
			/* Set the Password */
			thePassword.setText(theAccount.getPassword() != null ? 
							    new String(theAccount.getPassword()) : "");
			thePassword.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(thePassword, Account.FIELD_PASSWORD, theAccount, false, (theAccount.getPassword() == null));
		
			/* Set the WebSite */
			theActDetail.setText(theAccount.getAccount() != null ? 
							     new String(theAccount.getAccount()) : "");
			theActDetail.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theActDetail, Account.FIELD_ACCOUNT, theAccount, false, (theAccount.getAccount() == null));
		
			/* Set the Notes */
			theNotes.setText(theAccount.getNotes() != null ? 
							   new String(theAccount.getNotes()) : "");
			theNotes.setEnabled(!isClosed && !theAccount.isDeleted());
			theParent.formatComponent(theNotes, Account.FIELD_NOTES, theAccount, false, (theAccount.getNotes() == null));
		
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

			/* Handle alias */
			if (myType.canAlias() && (!theAccount.isAliasedTo())) {
				/* If we have alias already populated */
				if (alsPopulated) {	
					/* Remove the items */
					theAliasBox.removeAllItems();
					alsPopulated = false;
				}
				
				/* Create an account iterator */
				DataList<Account>.ListIterator  myActIterator = theAccounts.listIterator();
				Account							myAcct;
				
				/* Add the Account values to the parents box */
				while ((myAcct  = myActIterator.next()) != null) {
					/* Access the type */
					myType = myAcct.getActType();
					
					/* Ignore the account if it cannot alias */
					if (!myType.canAlias()) continue;

					/* Ignore the account if it is same type */
					if (myType.equals(theAccount.getActType())) continue;

					/* Ignore the account if it is an alias */
					if (myAcct.getAlias() != null) continue;

					/* Ignore the account if it is us */
					if (myAcct.compareTo(theAccount) == 0) continue;

					/* Add the item to the list */
					theAliasBox.addItem(myAcct.getName());
					alsPopulated = true;
				}
				
				/* Set up the aliases */
				if (theAccount.getAlias() != null)
					theAliasBox.setSelectedItem(theAccount.getAlias().getName());
				else
					theAliasBox.setSelectedItem(null);
				theAliasBox.setVisible(true);
				theAlsLabel.setVisible(true);
				theParent.formatComponent(theAliasBox, Account.FIELD_ALIAS, theAccount, 
										  false, (theAccount.getAlias() == null));
			}
			else {
				theAliasBox.setVisible(false);
				theAlsLabel.setVisible(false);
			}
			theAliasBox.setEnabled(!isClosed && !theAccount.isDeleted());

			/* Set the First Event */
			theFirst.setText((theAccount.getEarliest() != null)
								? theAccount.getEarliest().getDate().formatDate(false) 
								: "N/A");
			
			/* Set the First Event */
			theLast.setText((theAccount.getLatest() != null)
								? theAccount.getLatest().getDate().formatDate(false) 
								: "N/A");
			
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

			/* Note that we are finished refreshing data */
			refreshingData = false;
		}
		
		/* else no account selected */
		else {
			/* Clear details */
			theName.setText("");
			theDesc.setText("");
			theFirst.setText("");
			theLast.setText("");
			theWebSite.setText("");
			theCustNo.setText("");
			theUserId.setText("");
			thePassword.setText("");
			theActDetail.setText("");
			theNotes.setText("");
			
			/* Disable field entry */
			theName.setEnabled(false);
			theDesc.setEnabled(false);
			theWebSite.setEnabled(false);
			theCustNo.setEnabled(false);
			theUserId.setEnabled(false);
			thePassword.setEnabled(false);
			theActDetail.setEnabled(false);
			theNotes.setEnabled(false);
			theClsButton.setVisible(false);
			theUndoButton.setEnabled(false);
			
			theInsButton.setEnabled(myType != null);
			theDelButton.setVisible(false);
			theDelButton.setText("Recover");
			
			/* Hide parent and maturity */
			theParLabel.setVisible(false);
			theParentBox.setVisible(false);
			theAlsLabel.setVisible(false);
			theAliasBox.setVisible(false);
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
		
		/* Protect against exceptions */
		try {
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
	
			/* If this event relates to the parent box */
			else if (evt.getSource() == (Object)theParentBox) {
				myName = (String)evt.getItem();
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					/* Store the appropriate value */
					theAccount.setParent(theAccounts.searchFor(myName));
				
					/* Update text */
					updateText();
				}
			}
			
			/* If this event relates to the alias box */
			else if (evt.getSource() == (Object)theAliasBox) {
				myName = (String)evt.getItem();
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					/* Store the appropriate value */
					theAccount.setAlias(theAccounts.searchFor(myName));
				
					/* Update text */
					updateText();
				}
			}
		}
		
		
		/* Handle Exceptions */
		catch (Throwable e) {
			/* Reset values */
			theAccount.popHistory();
			theAccount.pushHistory();
			
			/* Build the error */
			Exception myError = new Exception(ExceptionClass.DATA,
									          "Failed to update field",
									          e);
			
			/* Show the error */
			theError.setError(myError);
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
		theDebugEntry.setObject(theActView);
	
		/* Access the account */
		theAccount = theActView.getAccount();			
		
		/* Notify changes */
		notifyChanges();
	}
	
	/* Update text */
	private void updateText() throws Exception {
		String  myText;
		char[]	myArray;

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

		/* Access the value */
		myArray = theWebSite.getPassword();
		if (myArray.length == 0) myArray = null;
		
		/* Store the appropriate value */
		theAccount.setWebSite(myArray);

		/* Access the value */
		myArray = theCustNo.getPassword();
		if (myArray.length == 0) myArray = null;
		
		/* Store the appropriate value */
		theAccount.setCustNo(myArray);

		/* Access the value */
		myArray = theUserId.getPassword();
		if (myArray.length == 0) myArray = null;
		
		/* Store the appropriate value */
		theAccount.setUserId(myArray);

		/* Access the value */
		myArray = thePassword.getPassword();
		if (myArray.length == 0) myArray = null;
		
		/* Store the appropriate value */
		theAccount.setPassword(myArray);

		/* Access the value */
		myArray = theActDetail.getPassword();
		if (myArray.length == 0) myArray = null;
		
		/* Store the appropriate value */
		theAccount.setAccount(myArray);

		/* Access the value */
		myArray = theNotes.getPassword();
		if (myArray.length == 0) myArray = null;
		
		/* Store the appropriate value */
		theAccount.setNotes(myArray);
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
			return;
		}
		
		/* If this event relates to the undo button */
		else if (evt.getSource() == (Object)theUndoButton) {
			/* Undo the changes */
			undoChanges();
			return;
		}
		
		/* Push history */
		theAccount.pushHistory();
		
		/* Protect against exceptions */
		try {
			/* If this event relates to the name field */
			if ((evt.getSource() == (Object)theName)      ||
				(evt.getSource() == (Object)theDesc)      ||
				(evt.getSource() == (Object)theWebSite)   ||
				(evt.getSource() == (Object)theCustNo)    ||
				(evt.getSource() == (Object)theUserId)    ||
				(evt.getSource() == (Object)thePassword)  ||
				(evt.getSource() == (Object)theActDetail) ||
				(evt.getSource() == (Object)theNotes)) {
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
		}
		
		/* Handle Exceptions */
		catch (Throwable e) {
			/* Reset values */
			theAccount.popHistory();
			theAccount.pushHistory();
			
			/* Build the error */
			Exception myError = new Exception(ExceptionClass.DATA,
									          "Failed to update field",
									          e);
			
			/* Show the error */
			theError.setError(myError);
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
			
			/* Protect against exceptions */
			try {
				/* Store the appropriate value */
				theAccount.setMaturity(myDate);    
		
				/* Update the text */
				updateText();
			}	
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				theAccount.popHistory();
				theAccount.pushHistory();
				
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}
			
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
