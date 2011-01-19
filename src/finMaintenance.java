package finance;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import finance.finData.TaxParms;
import finance.finObject.EditState;
import finance.finObject.State;
import finance.finSwing.financePanel;
import finance.finUtils.RenderData;
import finance.finUtils.Controls.tableCommand;

public class finMaintenance {
	/* properties */
	private finView				theView 	  	= null;
	private finSwing			theParent	  	= null;
	private JPanel            	thePanel      	= null;
	private JTabbedPane       	theTabs       	= null;
	private finData.AccountList theAccounts   	= null;
	private AccountTab			theAccountTab	= null;
	private TaxYearTab			theTaxYearTab	= null;
	private PropertiesTab		theProperties	= null;
	private PatternYearTable  	thePatternYear	= null;
	private Font		        theStdFont    	= null;
	private Font		        theChgFont    	= null;
	private Font				theNumFont		= null;
	private Font				theChgNumFont	= null;

	/* Access methods */
	public JPanel       getPanel()       { return thePanel; }
	
	/* Tab titles */
	private static final String titleAccounts 	= "Accounts";
	private static final String titleTaxYear  	= "TaxYears";
	private static final String titleProperties	= "Properties";
	private static final String titlePattern  	= "PatternYear";
	
	/* Constructor */
	public finMaintenance(finSwing pTop) {
		/* Store details */
		theView 	= pTop.getView();
		theParent 	= pTop;
		
		/* Access the fonts */
		theStdFont    = theParent.getStdFont();
		theChgFont    = theParent.getChgFont();
		theNumFont    = theParent.getNumFont();
		theChgNumFont = theParent.getChgNumFont();
		
		/* Create the Tabbed Pane */
		theTabs = new JTabbedPane();
			
		/* Create the account Tab and add it */
		theAccountTab = new AccountTab(this);
		theTabs.addTab(titleAccounts, theAccountTab.getPanel());
		
		/* Create the TaxYears Tab */
		theTaxYearTab = new TaxYearTab(this);
		theTabs.addTab(titleTaxYear, theTaxYearTab.getPanel());
		
		/* Create the Properties Tab */
		theProperties = new PropertiesTab(this);
		theTabs.addTab(titleProperties, theProperties.getPanel());
		
		/* Create the PatternYear Tab */
		thePatternYear = new PatternYearTable(this);
		theTabs.addTab(titlePattern, thePatternYear.getPanel());
		
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
	                    .addComponent(theTabs, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theTabs))
	    );
	}
	
	/* refresh data */
	public void refreshData() {
		finData				  myData;
		/* Access the data */
		myData = theView.getData();
		
		/* Access Accounts */
		theAccounts = myData.getAccounts();
		
		/* Refresh sub-panels */
		theAccountTab.refreshData();
		theTaxYearTab.refreshData();
		theProperties.refreshData();
		thePatternYear.refreshData();
	}
	
	/* Has this set of tables got updates */
	public boolean hasUpdates() {
		boolean hasUpdates = false;
		
		/* Determine whether we have updates */
		hasUpdates = theAccountTab.hasUpdates();
        if (!hasUpdates) hasUpdates = theTaxYearTab.hasUpdates();
        if (!hasUpdates) hasUpdates = theProperties.hasUpdates();
 			
		/* Return to caller */
		return hasUpdates;
	}
		
	/* Select an explicit account for maintenance */
	public void selectAccount(finData.Account  pAccount) {
		/* Pass through to the Account control */
		theAccountTab.selectAccount(pAccount);
		
		/* Goto the Accounts tab */
		gotoNamedTab(titleAccounts);
	}
	
	/* Goto the specific tab */
	private void gotoNamedTab(String pTabName) {
		/* Access the Named index */
		int iIndex = theTabs.indexOfTab(pTabName);
	
		/* Select the required tab */
		theTabs.setSelectedIndex(iIndex);
	}
	
	/* Set visible tabs */
	private void setVisibleTabs() {
		int         iIndex;
		boolean     hasUpdates;
		
		/* Determine whether we have any updates */
		hasUpdates = theAccountTab.hasUpdates();
		
		/* Access the Accounts index */
		iIndex = theTabs.indexOfTab(titleAccounts);
		
		/* Enable/Disable the Accounts tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates || theAccountTab.hasUpdates());
		
		/* Access the TaxYear index */
		iIndex = theTabs.indexOfTab(titleTaxYear);
		
		/* Enable/Disable the TaxYear tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates || theTaxYearTab.hasUpdates());
		
		/* Access the Properties panel */
		iIndex = theTabs.indexOfTab(titleProperties);
		
		/* Enable/Disable the Properties tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates || theProperties.hasUpdates());
		
		/* Access the PatternYear panel */
		iIndex = theTabs.indexOfTab(titlePattern);
		
		/* Enable/Disable the patternYear tab */
		if (iIndex != -1) 
			theTabs.setEnabledAt(iIndex, !hasUpdates);
		
		/* Update the top level tabs */
		theParent.setVisibleTabs();
	}
	
	/* Get Formatted Debug output */
	protected String getDebugText() {
		Component 			myComp;
		finData.Account		myAccount;
		finData.TaxParms	myTaxYear;
		String				myText = "";
		
		/* Determine the active tab */
		myComp = theTabs.getSelectedComponent();

		/* If the accounts panel is active */
		if (myComp == (Component)theAccountTab.getPanel()) {
			/* Access the formatted output */
			myAccount = theAccountTab.getAccount();
			if (myAccount != null) myText = myAccount.toHTMLString().toString();
		}
		
		/* If the TaxYearTab is active */
		else if (myComp == (Component)theTaxYearTab.getPanel()) {
			/* Access the formatted output */
			myTaxYear = theTaxYearTab.getTaxYear();
			if (myTaxYear != null) myText = myTaxYear.toHTMLString().toString();
		}
		
		/* Return to caller */
		return myText;
	}
	
	/* Account Tab */
	public class AccountTab implements financePanel,
									   ItemListener,
									   ActionListener,
									   ChangeListener {
		/* Properties */
		private finMaintenance						theParent		= null;
		private JPanel                          	thePanel		= null;
		private JPanel								theButtons		= null;
		private finUtils.Controls.AccountSelection 	theSelect		= null;
		private finUtils.Controls.TableButtons  	theTabButs   	= null;
		private JTextField							theName			= null;
		private JTextField							theDesc			= null;
		private JLabel								theFirst		= null;
		private JLabel								theLast			= null;
		private JComboBox							theTypesBox		= null;
		private JComboBox							theParentBox	= null;
		private JSpinner							theSpinner		= null;
		private SpinnerDateModel					theModel		= null;
		private JLabel								theTypLabel		= null;
		private JLabel								theParLabel		= null;
		private JLabel								theMatLabel		= null;
		private JButton								theInsButton	= null;
		private JButton								theDelButton	= null;
		private JButton								theClsButton	= null;
		private JButton								theUndoButton	= null;
		private finData.Account						theAccount		= null;
		private finView.AccountView					theActView		= null;
		private finStatic.ActTypeList				theAcTypList	= null;
		private boolean								refreshingData	= false;
		private boolean								typesPopulated	= false;
		private boolean								parPopulated	= false;
		
		/* Access methods */
		public JPanel       	getPanel()       { return thePanel; }
		public finData.Account	getAccount()	 { return theAccount; }
		
		/* Constructor */
		public AccountTab(finMaintenance pParent) {
			JLabel	myName;
			JLabel  myDesc;
			JLabel  myFirst;
			JLabel	myLast;
			
			/* Store passed data */
			theParent = pParent;
			
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
			theSelect = new finUtils.Controls.AccountSelection(theView, this, true);
			
			/* Create the Table buttons panel */
			theTabButs = new finUtils.Controls.TableButtons(this);
			
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
	                	.addComponent(theTabButs.getPanel())
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
	                    .addComponent(theTabButs.getPanel())
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
		public void performCommand(tableCommand pCmd) {
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
			theTabButs.setLockDown();
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
			if (theAccount.getState() == State.NEW) {
				/* Delete the new account */
				delNewAccount();
			}
		}
		
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
			finData				  myData;
			finData.Account	  	  myAcct;
			finStatic.AccountType myType;
			
			/* Access the data */
			myData = theView.getData();
			
			/* Access type */
			theAcTypList = myData.getActTypes();
		
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
			
			/* Add the AccountType values to the types box */
			for (myType  = theAcTypList.getFirst();
			     myType != null;
			     myType  = myType.getNext()) {
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
			
			/* Add the Account values to the parents box */
			for (myAcct  = theAccounts.getFirst();
			     myAcct != null;
			     myAcct  = myAcct.getNext()) {
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
		public void selectAccount(finData.Account  pAccount) {
			/* Adjust the account selection */
			theSelect.setSelection(pAccount);
			
			/* Redraw selection */
			setSelection(theSelect.getSelected());
		}
		
		/* Set Selection */
		public void setSelection(finData.Account pAccount) {
			/* Reset controls */
			theActView = null;
			theAccount = null;
			
			/* If we have a selected account */
			if (pAccount != null) {
				/* Create the view of the account */
				theActView = theView.new AccountView(pAccount);
			
				/* Access the account */
				theAccount = theActView.getAccount();
			}
			
			/* notify changes */
			notifyChanges();
		}
			
		private void showAccount() {
			finStatic.AccountType 	myType;
			boolean					isClosed;
			boolean					isChanged;
			boolean					isError;
			Color					myFore;
			String					myTip = null;
			
			/* If we have an active account */
			if (theAccount != null) {
				/* Determine the standard colour */
				myFore = Color.black;
				if (theAccount.getState() == finObject.State.DELETED)
					myFore = Color.lightGray;
				else if (theAccount.getState() == finObject.State.NEW)
					myFore = Color.blue;
				else if (theAccount.getState() == finObject.State.RECOVERED)
					myFore = Color.darkGray;
				
				/* Access details */
				myType 	 = theAccount.getActType();
				isClosed = theAccount.isClosed();
				
				/* Set the name */
				theName.setText(theAccount.getName() != null ? 
									theAccount.getName() : "");
				theName.setEnabled(!isClosed && !theAccount.isDeleted());
				isChanged = theAccount.fieldChanged(finData.Account.FIELD_NAME);
				theName.setFont((isChanged) ? theChgFont : theStdFont);
				if (isError = theAccount.hasErrors(finData.Account.FIELD_NAME)) 
					myTip  = theAccount.getFieldError(finData.Account.FIELD_NAME);
				if ((isError) && (theAccount.getName() == null)) {
					theName.setBackground(Color.red);
					theName.setForeground(myFore);
				}	
				else {
					theName.setForeground((isError) ? Color.red  
						 						  	: (isChanged) ? Color.magenta
						 						  		  	      : myFore);
					theName.setBackground(Color.white);
				}
				theName.setToolTipText((isError) ? myTip : null);
			
				/* Set the description */
				theDesc.setText(theAccount.getDesc() != null ? 
									theAccount.getDesc() : "");
				theDesc.setEnabled(!isClosed && !theAccount.isDeleted());
				isChanged = theAccount.fieldChanged(finData.Account.FIELD_DESC);
				theDesc.setFont((isChanged) ? theChgFont : theStdFont);
				if (isError = theAccount.hasErrors(finData.Account.FIELD_DESC)) 
					myTip  = theAccount.getFieldError(finData.Account.FIELD_DESC);
				if ((isError) && (theAccount.getDesc() == null)) {
					theDesc.setBackground(Color.red);
					theDesc.setForeground(myFore);
				}	
				else {
					theDesc.setForeground((isError) ? Color.red  
						 						  	: (isChanged) ? Color.magenta
						 						  		  	      : myFore);
					theDesc.setBackground(Color.white);
				}
				theDesc.setToolTipText((isError) ? myTip : null);
			
				/* Set the type */
				theTypesBox.setSelectedItem(myType.getName());
				theTypesBox.setVisible(theAccount.isDeletable() && (theAccount.getState() == State.NEW));
				theTypLabel.setVisible(theAccount.isDeletable() && (theAccount.getState() == State.NEW));
				isChanged = theAccount.fieldChanged(finData.Account.FIELD_TYPE);
				theTypesBox.setFont((isChanged) ? theChgFont : theStdFont);
				if (isError = theAccount.hasErrors(finData.Account.FIELD_TYPE)) 
					myTip  = theAccount.getFieldError(finData.Account.FIELD_TYPE);
				theTypesBox.setForeground((isError) ? Color.red  
						 							: (isChanged) ? Color.magenta
						 										  : myFore);
				theTypesBox.setToolTipText((isError) ? myTip : null);
			
				/* Handle maturity */
				if (myType.isBond()) {
					theModel.setValue(theAccount.getMaturity().getDate());
					theSpinner.setVisible(true);
					theMatLabel.setVisible(true);
				}
				else {
					theSpinner.setVisible(false);
					theMatLabel.setVisible(false);
				}
				theSpinner.setEnabled(!isClosed);
				isChanged = theAccount.fieldChanged(finData.Account.FIELD_MATURITY);
				theSpinner.setFont((isChanged) ? theChgFont : theStdFont);
				if (isError = theAccount.hasErrors(finData.Account.FIELD_MATURITY)) 
					myTip  = theAccount.getFieldError(finData.Account.FIELD_MATURITY);
				theSpinner.setForeground((isError) ? Color.red  
						 						   : (isChanged) ? Color.magenta
						 								   		 : myFore);
				theSpinner.setToolTipText((isError) ? myTip : null);
			
				/* Handle parent */
				if (myType.isChild()) {
					if (theAccount.getParent() != null)
						theParentBox.setSelectedItem(theAccount.getParent().getName());
					else
						theParentBox.setSelectedItem(null);
					theParentBox.setVisible(true);
					theParLabel.setVisible(true);
					isChanged = theAccount.fieldChanged(finData.Account.FIELD_PARENT);
					theParentBox.setFont((isChanged) ? theChgFont : theStdFont);
					if (isError = theAccount.hasErrors(finData.Account.FIELD_PARENT)) 
						myTip  = theAccount.getFieldError(finData.Account.FIELD_PARENT);
					if ((isError) && (theAccount.getParent() == null)) {
						theParentBox.setBackground(Color.red);
						theParentBox.setForeground(myFore);
					}	
					else {
						theParentBox.setForeground((isError) ? Color.red  
							 				  			  	 : (isChanged) ? Color.magenta
							 						  					   : myFore);
						theParentBox.setBackground(Color.white);
					}
					theParentBox.setToolTipText((isError) ? myTip : null);
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
							theAccount.setMaturity(new finObject.Date());
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
				theAccount.setState(State.CHANGED);
				
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
			theActView = theView.new AccountView(theSelect.getType());
		
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
					theAccount.setState(finObject.State.CLEAN);
				}
				
				/* Notify changes */
				notifyChanges();
			}
			
			/* else if this is a new account */
			else if (theAccount.getState() == State.NEW) {
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
				if (theAccount.getState() == State.NEW) {
					/* Delete the new account */
					delNewAccount();
				}
				
				/* Else we should just delete/recover the account */
				else {
					/* Set the appropriate state */
					theAccount.setState(theAccount.isDeleted() ? State.RECOVERED
															   : State.DELETED);
					
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
				theAccount.setState(State.CHANGED);
				
				/* Note that changes have occurred */
				notifyChanges();
			}
		}	
		
		/* stateChanged listener event */
		public void stateChanged(ChangeEvent evt) {
			finObject.Date myDate;

			/* Ignore selection if refreshing data */
			if (refreshingData) return;
			
			/* If this event relates to the maturity box */
			if (evt.getSource() == (Object)theModel) {
				/* Access the value */
				myDate = new finObject.Date(theModel.getDate());

				/* Push history */
				theAccount.pushHistory();
				
				/* Store the appropriate value */
				theAccount.setMaturity(myDate);    
			
				/* Update the text */
				updateText();
				
				/* Check for changes */
				if (theAccount.checkForHistory()) {
					/* Note that the item has changed */
					theAccount.setState(State.CHANGED);
					
					/* Note that changes have occurred */
					notifyChanges();
				}
			}								
		}
	}	

	/* TaxYear Tab */
	public class TaxYearTab implements financePanel,
									   ItemListener,
									   ActionListener {
		/* Properties */
		private finMaintenance						theParent			= null;
		private JPanel                          	thePanel			= null;
		private JPanel								theButtons			= null;
		private JPanel								theSelect			= null;
		private JPanel								theRegime			= null;
		private JPanel								theAllows			= null;
		private JPanel								theBands			= null;
		private JPanel								theLimits			= null;
		private JPanel								theStdRates			= null;
		private JPanel								theXtraRates		= null;
		private JPanel								theCapRates			= null;
		private finUtils.Controls.TableButtons  	theTabButs   		= null;
		private JComboBox							theYearsBox			= null;
		private JComboBox							theRegimesBox		= null;
		private JCheckBox							theShowDeleted		= null;
		private JTextField							theYear				= null;
		private JTextField							theAllowance		= null;
		private JTextField							theLoAgeAllow		= null;
		private JTextField							theHiAgeAllow		= null;
		private JTextField							theCapitalAllow		= null;
		private JTextField							theAgeAllowLimit	= null;
		private JTextField							theAddAllowLimit	= null;
		private JTextField							theAddIncomeBndry	= null;
		private JTextField							theRental			= null;
		private JTextField							theLoTaxBand		= null;
		private JTextField							theBasicTaxBand		= null;
		private JTextField							theLoTaxRate		= null;
		private JTextField							theBasicTaxRate		= null;
		private JTextField							theHiTaxRate		= null;
		private JTextField							theIntTaxRate		= null;
		private JTextField							theDivTaxRate		= null;
		private JTextField							theHiDivTaxRate		= null;
		private JTextField							theAddTaxRate		= null;
		private JTextField							theAddDivTaxRate	= null;
		private JTextField							theCapTaxRate		= null;
		private JTextField							theHiCapTaxRate		= null;
		private JButton								theInsButton		= null;
		private JButton								theDelButton		= null;
		private JButton								theUndoButton		= null;
		private finData.TaxParms					theTaxYear			= null;
		private finData.TaxParmList					theTaxYears			= null;
		private finStatic.TaxRegimeList				theTaxRegimes		= null;
		private finView.TaxParmView					theTaxView			= null;
		private boolean								refreshingData		= false;
		private boolean								yearsPopulated		= false;
		private boolean								regimesPopulated	= false;
		private boolean								doShowDeleted		= false;
		
		/* Access methods */
		public JPanel       	getPanel()      { return thePanel; }
		public finData.TaxParms getTaxYear()	{ return theTaxYear; }
		
		/* Constructor */
		public TaxYearTab(finMaintenance pParent) {
			JLabel	mySelect;
			JLabel  myYear;
			JLabel  myRegime;
			JLabel  myAllow;
			JLabel  myLoAgeAllow;
			JLabel  myHiAgeAllow;
			JLabel  myCapitalAllow;
			JLabel	myRental;
			JLabel  myAgeAllowLimit;
			JLabel  myAddAllowLimit;
			JLabel  myAddIncBndry;
			JLabel  myLoBand;
			JLabel	myBasicBand;
			JLabel  myLoTaxRate;
			JLabel  myBasicTaxRate;
			JLabel	myHiTaxRate;
			JLabel  myIntTaxRate;
			JLabel  myDivTaxRate;
			JLabel	myHiDivTaxRate;
			JLabel  myAddTaxRate;
			JLabel	myAddDivTaxRate;
			JLabel  myCapTaxRate;
			JLabel	myHiCapTaxRate;
			
			/* Store passed data */
			theParent = pParent;
			
			/* Create the labels */
			mySelect 		= new JLabel("Select Year:");
			myYear 	 		= new JLabel("Year:");
			myRegime		= new JLabel("Tax Regime:");
			myAllow	 		= new JLabel("Personal Allowance:");
			myLoAgeAllow	= new JLabel("Age 65-74 Allowance:");
			myHiAgeAllow	= new JLabel("Age 75+ Allowance:");
			myCapitalAllow	= new JLabel("Capital Allowance:");
			myAgeAllowLimit	= new JLabel("Age Allowance Limit:");
			myAddAllowLimit	= new JLabel("Additnl Allow Limit:");
			myAddIncBndry	= new JLabel("Additnl Tax Boundary:");
			myRental		= new JLabel("Rental Allowance:");
			myLoBand		= new JLabel("Low Tax Band:");
			myBasicBand		= new JLabel("Basic Tax Band:");
			myLoTaxRate		= new JLabel("Low Rate:");
			myBasicTaxRate	= new JLabel("Basic Rate:");
			myHiTaxRate		= new JLabel("High Rate:");
			myIntTaxRate	= new JLabel("Interest Rate:");
			myDivTaxRate	= new JLabel("Dividend Rate:");
			myHiDivTaxRate	= new JLabel("High Dividend Rate:");
			myAddTaxRate	= new JLabel("Additnl Rate:");
			myAddDivTaxRate	= new JLabel("Additnl Dividend Rate:");
			myCapTaxRate	= new JLabel("Capital Rate:");
			myHiCapTaxRate	= new JLabel("High Capital Rate:");
			
			/* Create the combo boxes */
			theYearsBox  	= new JComboBox();
			theRegimesBox  	= new JComboBox();
			
			/* Create the combo boxes */
			theShowDeleted  = new JCheckBox("ShowDeleted");
			theShowDeleted.setSelected(doShowDeleted);
			
			/* Create the text fields */
			theYear				= new JTextField();
			theAllowance 		= new JTextField();
			theLoAgeAllow 		= new JTextField();
			theHiAgeAllow 		= new JTextField();
			theCapitalAllow		= new JTextField();
			theAgeAllowLimit	= new JTextField();
			theAddAllowLimit	= new JTextField();
			theAddIncomeBndry	= new JTextField();
			theLoTaxBand 		= new JTextField();
			theBasicTaxBand		= new JTextField();
			theRental			= new JTextField();
			theLoTaxRate 		= new JTextField();
			theBasicTaxRate 	= new JTextField();
			theHiTaxRate		= new JTextField();
			theIntTaxRate		= new JTextField();
			theDivTaxRate		= new JTextField();
			theHiDivTaxRate		= new JTextField();
			theAddTaxRate		= new JTextField();
			theAddDivTaxRate	= new JTextField();
			theCapTaxRate		= new JTextField();
			theHiCapTaxRate		= new JTextField();
			
			/* Set alignment for the text fields */
			theAllowance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theLoAgeAllow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theHiAgeAllow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theCapitalAllow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theAgeAllowLimit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theAddAllowLimit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theAddIncomeBndry.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theRental.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theLoTaxBand.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theBasicTaxBand.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theLoTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theBasicTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theHiTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theIntTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theDivTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theHiDivTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theAddTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theAddDivTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theCapTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			theHiCapTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			
			/* Create the buttons */
			theInsButton  = new JButton("New");
			theDelButton  = new JButton();
			theUndoButton = new JButton("Undo");
			
			/* Add listeners */
			theYearsBox.addItemListener(this);
			theRegimesBox.addItemListener(this);
			theAllowance.addActionListener(this);
			theLoAgeAllow.addActionListener(this);
			theHiAgeAllow.addActionListener(this);
			theCapitalAllow.addActionListener(this);
			theAgeAllowLimit.addActionListener(this);
			theAddAllowLimit.addActionListener(this);
			theAddIncomeBndry.addActionListener(this);
			theRental.addActionListener(this);
			theLoTaxBand.addActionListener(this);
			theBasicTaxBand.addActionListener(this);
			theLoTaxRate.addActionListener(this);
			theBasicTaxRate.addActionListener(this);
			theHiTaxRate.addActionListener(this);
			theIntTaxRate.addActionListener(this);
			theDivTaxRate.addActionListener(this);
			theHiDivTaxRate.addActionListener(this);
			theAddTaxRate.addActionListener(this);
			theAddDivTaxRate.addActionListener(this);
			theCapTaxRate.addActionListener(this);
			theHiCapTaxRate.addActionListener(this);
			theInsButton.addActionListener(this);
			theDelButton.addActionListener(this);
			theUndoButton.addActionListener(this);
			theShowDeleted.addItemListener(this);

			/* Create the Table buttons panel */
			theTabButs = new finUtils.Controls.TableButtons(this);
			
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
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theInsButton)
                .addComponent(theUndoButton)
                .addComponent(theDelButton)
            );
	            
			/* Create the selection panel */
			theSelect = new JPanel();
			theSelect.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Selection"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theSelect);
		    theSelect.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addContainerGap()
        			.addComponent(mySelect)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        			.addComponent(theYearsBox)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        			.addComponent(theShowDeleted)
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
 	                .addComponent(mySelect)
   	                .addComponent(theYearsBox)
   	                .addComponent(theShowDeleted)
            );
	            
			/* Create the regime panel */
			theRegime = new JPanel();
			theRegime.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Tax Regime"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theRegime);
		    theRegime.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addContainerGap()
        			.addComponent(myYear)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        			.addComponent(theYear)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
        			.addComponent(myRegime)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        			.addComponent(theRegimesBox)
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myYear)
   	                .addComponent(theYear)
   	                .addComponent(myRegime)
   	                .addComponent(theRegimesBox)
            );
	            
			/* Create the allowances panel */
			theAllows = new JPanel();
			theAllows.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Allowances"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theAllows);
		    theAllows.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	        			.addComponent(myAllow)
	                    .addComponent(myLoAgeAllow)
	                    .addComponent(myHiAgeAllow)
	                    .addComponent(myRental)
	                    .addComponent(myCapitalAllow))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addComponent(theAllowance)
	                    .addComponent(theLoAgeAllow)
	                    .addComponent(theHiAgeAllow)
	                    .addComponent(theRental)
	                    .addComponent(theCapitalAllow))
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    		   	.addGroup(myLayout.createSequentialGroup()
    	            .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	                .addComponent(myAllow)
    	                .addComponent(theAllowance))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myLoAgeAllow)
       	                .addComponent(theLoAgeAllow))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myHiAgeAllow)
       	                .addComponent(theHiAgeAllow))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myRental)
       	                .addComponent(theRental))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myCapitalAllow)
       	                .addComponent(theCapitalAllow))
       	            .addContainerGap())
            );
	            
			/* Create the limits panel */
			theLimits = new JPanel();
			theLimits.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Limits"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theLimits);
		    theLimits.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	        			.addComponent(myAgeAllowLimit)
	                    .addComponent(myAddAllowLimit))
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addComponent(theAgeAllowLimit)
	                    .addComponent(theAddAllowLimit))
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    		   	.addGroup(myLayout.createSequentialGroup()
    	            .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	                .addComponent(myAgeAllowLimit)
    	                .addComponent(theAgeAllowLimit))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myAddAllowLimit)
       	                .addComponent(theAddAllowLimit))
       	            .addContainerGap(80,80))
            );
	            
			/* Create the bands panel */
			theBands = new JPanel();
			theBands.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Tax Bands"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theBands);
		    theBands.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	        			.addComponent(myLoBand)
	                    .addComponent(myBasicBand)
	                    .addComponent(myAddIncBndry))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addComponent(theLoTaxBand)
	                    .addComponent(theBasicTaxBand)
	                    .addComponent(theAddIncomeBndry))
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    		   	.addGroup(myLayout.createSequentialGroup()
    	            .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	                .addComponent(myLoBand)
    	                .addComponent(theLoTaxBand))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myBasicBand)
       	                .addComponent(theBasicTaxBand))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myAddIncBndry)
       	                .addComponent(theAddIncomeBndry))
       	            .addContainerGap(50,50))
            );
	            
			/* Create the standard rates panel */
			theStdRates = new JPanel();
			theStdRates.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Standard Rates"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theStdRates);
		    theStdRates.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	        			.addComponent(myLoTaxRate)
	                    .addComponent(myBasicTaxRate)
	                    .addComponent(myHiTaxRate)
	                    .addComponent(myAddTaxRate))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addComponent(theLoTaxRate)
	                    .addComponent(theBasicTaxRate)
	                    .addComponent(theHiTaxRate)
	                    .addComponent(theAddTaxRate))
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    		   	.addGroup(myLayout.createSequentialGroup()
    	            .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	                .addComponent(myLoTaxRate)
    	                .addComponent(theLoTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myBasicTaxRate)
       	                .addComponent(theBasicTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myHiTaxRate)
       	                .addComponent(theHiTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myAddTaxRate)
       	                .addComponent(theAddTaxRate))
       	            .addContainerGap())
            );
	            
			/* Create the extra rates panel */
			theXtraRates = new JPanel();
			theXtraRates.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Interest/Dividend Rates"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theXtraRates);
		    theXtraRates.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	        			.addComponent(myIntTaxRate)
	                    .addComponent(myDivTaxRate)
	                    .addComponent(myHiDivTaxRate)
	                    .addComponent(myAddDivTaxRate))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addComponent(theIntTaxRate)
	                    .addComponent(theDivTaxRate)
	                    .addComponent(theHiDivTaxRate)
	                    .addComponent(theAddDivTaxRate))
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    		   	.addGroup(myLayout.createSequentialGroup()
    	            .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	                .addComponent(myIntTaxRate)
    	                .addComponent(theIntTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myDivTaxRate)
       	                .addComponent(theDivTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myHiDivTaxRate)
       	                .addComponent(theHiDivTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myAddDivTaxRate)
       	                .addComponent(theAddDivTaxRate))
       	            .addContainerGap())
            );
	            
			/* Create the capital rates panel */
			theCapRates = new JPanel();
			theCapRates.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Capital Rates"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theCapRates);
		    theCapRates.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	        			.addComponent(myCapTaxRate)
	                    .addComponent(myHiCapTaxRate))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addComponent(theCapTaxRate)
	                    .addComponent(theHiCapTaxRate))
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    		   	.addGroup(myLayout.createSequentialGroup()
    	            .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	                .addComponent(myCapTaxRate)
    	                .addComponent(theCapTaxRate))
       	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
       	                .addComponent(myHiCapTaxRate)
       	                .addComponent(theHiCapTaxRate))
       	            .addContainerGap(50,50))
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
	                	.addComponent(theTabButs.getPanel())
	                    .addComponent(theButtons)
	                    .addComponent(theSelect)
	                    .addComponent(theRegime)
	                    .addGroup(myLayout.createSequentialGroup()
	                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	                            .addComponent(theAllows)
	                            .addComponent(theStdRates))
	                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	                            .addComponent(theLimits)
	                            .addComponent(theXtraRates))
	                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	                            .addComponent(theBands)
	                            .addComponent(theCapRates))))
      	            .addContainerGap())
	        );
	        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			   	.addGroup(myLayout.createSequentialGroup()
	                .addContainerGap()
                    .addComponent(theSelect)
                    .addContainerGap(10,30)
                    .addComponent(theRegime)
                    .addContainerGap(10,30)
   	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(theAllows)
                        .addComponent(theLimits)
                        .addComponent(theBands))
                    .addContainerGap(10,30)
  	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(theStdRates)
                        .addComponent(theXtraRates)
                        .addComponent(theCapRates))
	                .addContainerGap()
                	.addComponent(theButtons)
	                .addContainerGap()
                	.addComponent(theTabButs.getPanel()))
                );
            
            /* Set initial display */
            showTaxYear();
		}
		
		/* hasUpdates */
		public boolean hasUpdates() {
			return ((theTaxYear != null) && (theTaxYear.hasChanges()));
		}
		
		/* hasErrors */
		public boolean hasErrors() {
			return ((theTaxYear != null) && (theTaxYear.hasErrors()));
		}
		
		/* isLocked */
		public boolean isLocked() { return false; }
		
		/* getEditState */
		public EditState getEditState() {
			if (theTaxYear == null) return EditState.CLEAN;
			return theTaxYear.getEditState();
		}
		
		/* performCommand */
		public void performCommand(tableCommand pCmd) {
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
			theTabButs.setLockDown();
			theYearsBox.setEnabled(!hasUpdates());
			theShowDeleted.setEnabled(!hasUpdates());
			
			/* Show the Tax Year */
			showTaxYear();
			
			/* Adjust visible tabs */
			theParent.setVisibleTabs();
		}	
			
		/* Note that there has been a selection change */
		public void    notifySelection(Object obj)    {}
		
		/* resetData */
		public void resetData() {
			theTaxYear.clearErrors();
			theTaxYear.resetHistory();
			theTaxYear.validate();
			
			/* if this is a new Tax Year */
			if (theTaxYear.getState() == State.NEW) {
				/* Delete the new tax year */
				delNewTaxYear();
			}
		}
		
		/* validate */
		public void validate() {
			theTaxYear.clearErrors();
			theTaxYear.validate();
		}
		
		/* saveData */
		public void saveData() {
			/* Validate the data */
			validate();
			if (!hasErrors()) {
				/* Save details for the tax year */
				if (theTaxView != null)	theTaxView.applyChanges();
			}
		}
			
		/* refreshData */
		public void refreshData() {
			finData				  myData;
			finData.TaxParms  	  myYear;
			finStatic.TaxRegime	  myRegime;
			
			/* Access the data */
			myData = theView.getData();
			
			/* Access years and regimes */
			theTaxYears 	= myData.getTaxYears();
			theTaxRegimes 	= myData.getTaxRegimes();
		
			/* Note that we are refreshing data */
			refreshingData = true;
			
			/* If we have years already populated */
			if (yearsPopulated) {	
				/* If we have a selected year */
				if (theTaxYear != null) {
					/* Find it in the new list */
					theTaxYear = theTaxYears.searchFor(theTaxYear.getDate());
				}
				
				/* Remove the years */
				theYearsBox.removeAllItems();
				yearsPopulated = false;
			}
			
			/* Add the Tax Years to the years box */
			for (myYear  = theTaxYears.getLast();
			     myYear != null;
			     myYear  = myYear.getPrev()) {
				/* If the year is not deleted */
				if ((!doShowDeleted) &&
					(myYear.isDeleted())) continue;
				
				/* Add the item to the list */
				theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
				yearsPopulated = true;
			}
			
			/* If we have a selected year */
			if (theTaxYear != null) {
				/* Select it in the new list */
				theYearsBox.setSelectedItem(Integer.toString(theTaxYear.getDate().getYear()));
			}
			
			/* If we have regimes already populated */
			if (regimesPopulated) {	
				/* Remove the types */
				theRegimesBox.removeAllItems();
				regimesPopulated = false;
			}
			
			/* Add the Tax Regimes to the regimes box */
			for (myRegime  = theTaxRegimes.getLast();
			     myRegime != null;
			     myRegime  = myRegime.getPrev()) {
				/* Add the item to the list */
				theRegimesBox.addItem(myRegime.getName());
				regimesPopulated = true;
			}
			
			/* Note that we have finished refreshing data */
			refreshingData = false;
			
			/* Show the current tax year */
			String myName = (String)theYearsBox.getSelectedItem();
			setSelection((myName != null) ? theTaxYears.searchFor(myName) : null);
		}
		
		/* Set Selection */
		public void setSelection(finData.TaxParms pTaxYear) {
			/* Reset controls */
			theTaxView = null;
			theTaxYear = null;
			
			/* If we have a selected tax year */
			if (pTaxYear != null) {
				/* If we need to show deleted items */
				if ((!doShowDeleted) && (pTaxYear.isDeleted())) {
					/* Set the flag correctly */
					doShowDeleted = true;
					theShowDeleted.setSelected(doShowDeleted);
				}
				
				/* Create the view of the tax year */
				theTaxView = theView.new TaxParmView(pTaxYear);
			
				/* Access the tax year */
				theTaxYear = theTaxView.getTaxYear();
			}
			
			/* notify changes */
			notifyChanges();
		}

		/* Show the tax year */
		private void showTaxYear() {
			boolean 			isChanged;
			boolean 			isError;
			finStatic.TaxRegime myRegime;
			Color				myFore;
			String  			myTip = null;
			
			/* If we have an active year */
			if (theTaxYear != null) {
				/* Access the tax regime */
				myRegime = theTaxYear.getTaxRegime();
				
				/* Determine the standard colour */
				myFore = Color.black;
				if (theTaxYear.getState() == finObject.State.DELETED)
					myFore = Color.lightGray;
				else if (theTaxYear.getState() == finObject.State.NEW)
					myFore = Color.blue;
				else if (theTaxYear.getState() == finObject.State.RECOVERED)
					myFore = Color.darkGray;
				
				/* Set the Year */
				theYear.setText(Integer.toString(theTaxYear.getDate().getYear()));
				theYear.setEnabled(!theTaxYear.isDeleted());
				theYear.setEditable(false);

				/* Set the Regime */
				theRegimesBox.setSelectedItem(myRegime.getName());
				theRegimesBox.setEnabled(!theTaxYear.isDeleted());

				/* Set the Allowance */
				theAllowance.setText(theTaxYear.getAllowance().format(true));
				theAllowance.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_ALLOW);
				theAllowance.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_ALLOW)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_ALLOW);
				theAllowance.setForeground((isError) ? Color.red 
													 : (isChanged) ? Color.magenta
													 		  	   : myFore);
				theAllowance.setToolTipText((isError) ? myTip : null);

				/* Set the LoAge Allowance */
				theLoAgeAllow.setText(theTaxYear.getLoAgeAllow().format(true));
				theLoAgeAllow.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_LOAGAL);
				theLoAgeAllow.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_LOAGAL)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_LOAGAL);
				theLoAgeAllow.setForeground((isError) ? Color.red  
						 						  : (isChanged) ? Color.magenta
						 								  		: myFore);
				theLoAgeAllow.setToolTipText((isError) ? myTip : null);
			
				/* Set the HiAge Allowance */
				theHiAgeAllow.setText(theTaxYear.getHiAgeAllow().format(true));
				theHiAgeAllow.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_HIAGAL);
				theHiAgeAllow.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_HIAGAL)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_HIAGAL);
				theHiAgeAllow.setForeground((isError) ? Color.red  
						 						  : (isChanged) ? Color.magenta
						 								  		: myFore);
				theHiAgeAllow.setToolTipText((isError) ? myTip : null);
			
				/* Set the Capital Allowance */
				theCapitalAllow.setText(theTaxYear.getCapitalAllow().format(true));
				theCapitalAllow.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_CAPALW);
				theCapitalAllow.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_CAPALW)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_CAPALW);
				theCapitalAllow.setForeground((isError) ? Color.red  
						 						  : (isChanged) ? Color.magenta
						 								  		: myFore);
				theCapitalAllow.setToolTipText((isError) ? myTip : null);
			
				/* Set the Rental Allowance */
				theRental.setText(theTaxYear.getRentalAllowance().format(true));
				theRental.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_RENTAL);
				theRental.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_RENTAL)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_RENTAL);
				theRental.setForeground((isError) ? Color.red  
						 						  : (isChanged) ? Color.magenta
						 								  		: myFore);
				theRental.setToolTipText((isError) ? myTip : null);
			
				/* Set the Age Allowance Limit */
				theAgeAllowLimit.setText(theTaxYear.getAgeAllowLimit().format(true));
				theAgeAllowLimit.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_AGELMT);
				theAgeAllowLimit.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_AGELMT)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_AGELMT);
				theAgeAllowLimit.setForeground((isError) ? Color.red  
						 						  : (isChanged) ? Color.magenta
						 								  		: myFore);
				theAgeAllowLimit.setToolTipText((isError) ? myTip : null);
			
				/* Set the Additional Allowance Limit */
				theAddAllowLimit.setText((theTaxYear.hasAdditionalTaxBand() &&
						 				  theTaxYear.getAddAllowLimit() != null) 
											? theTaxYear.getAddAllowLimit().format(true)
											: "");
				theAddAllowLimit.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_ADDLMT);
				theAddAllowLimit.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_ADDLMT)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_ADDLMT);
				if ((isError) && (theTaxYear.getAddAllowLimit() == null)) {
					theAddAllowLimit.setBackground(Color.red);
					theAddAllowLimit.setForeground(myFore);
				}	
				else {
					theAddAllowLimit.setForeground((isError) ? Color.red  
						 						  			 : (isChanged) ? Color.magenta
						 						  				  	       : myFore);
					theAddAllowLimit.setBackground(Color.white);
				}
				theAddAllowLimit.setToolTipText((isError) ? myTip : null);
			
				/* Set the Additional Income Boundary */
				theAddIncomeBndry.setText((theTaxYear.hasAdditionalTaxBand() &&
						 				   theTaxYear.getAddIncBound() != null) 
												? theTaxYear.getAddIncBound().format(true)
												: "");
				theAddIncomeBndry.setEnabled(!theTaxYear.isDeleted()  && theTaxYear.hasAdditionalTaxBand());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_ADDBDY);
				theAddIncomeBndry.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_ADDBDY)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_ADDBDY);
				if ((isError) && (theTaxYear.getAddIncBound() == null)) {
					theAddIncomeBndry.setBackground(Color.red);
					theAddIncomeBndry.setForeground(myFore);
				}	
				else {
					theAddIncomeBndry.setForeground((isError) ? Color.red  
						 						  			  : (isChanged) ? Color.magenta
						 						  					  	    : myFore);
					theAddIncomeBndry.setBackground(Color.white);
				}
				
				theAddIncomeBndry.setBackground(((isError) && (theTaxYear.getAddIncBound() == null))
													? Color.red : Color.white);
				theAddIncomeBndry.setToolTipText((isError) ? myTip : null);
			
				/* Set the Low Tax Band */
				theLoTaxBand.setText(theTaxYear.getLoBand().format(true));
				theLoTaxBand.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_LOBAND);
				theLoTaxBand.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_LOBAND)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_LOBAND);
				theLoTaxBand.setForeground((isError) ? Color.red  
						 							 : (isChanged) ? Color.magenta
						 									 	   : myFore);
				theLoTaxBand.setToolTipText((isError) ? myTip : null);
			
				/* Set the Basic Tax Band */
				theBasicTaxBand.setText(theTaxYear.getBasicBand().format(true));
				theBasicTaxBand.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_BSBAND);
				theBasicTaxBand.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_BSBAND)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_BSBAND);
				theBasicTaxBand.setForeground((isError) ? Color.red  
						 							  : (isChanged) ? Color.magenta
						 									  		: myFore);
				theBasicTaxBand.setToolTipText((isError) ? myTip : null);
			
				/* Set the Low Tax Rate */
				theLoTaxRate.setText(theTaxYear.getLoTaxRate().format(true));
				theLoTaxRate.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_LOTAX);
				theLoTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_LOTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_LOTAX);
				theLoTaxRate.setForeground((isError) ? Color.red  
						 							 : (isChanged) ? Color.magenta
						 									 	   : myFore);
				theLoTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the Basic Tax Rate */
				theBasicTaxRate.setText(theTaxYear.getBasicTaxRate().format(true));
				theBasicTaxRate.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_BASTAX);
				theBasicTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_BASTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_BASTAX);
				theBasicTaxRate.setForeground((isError) ? Color.red  
						 							  : (isChanged) ? Color.magenta
						 									  		: myFore);
				theBasicTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the High Tax Rate */
				theHiTaxRate.setText(theTaxYear.getHiTaxRate().format(true));
				theHiTaxRate.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_HITAX);
				theHiTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_HITAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_HITAX);
				theHiTaxRate.setForeground((isError) ? Color.red  
						 							 : (isChanged) ? Color.magenta
						 									 	   : myFore);
				theHiTaxRate.setToolTipText((isError) ? myTip : null);
							
				/* Set the Interest Tax Rate */
				theIntTaxRate.setText(theTaxYear.getIntTaxRate().format(true));
				theIntTaxRate.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_INTTAX);
				theIntTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_INTTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_INTTAX);
				theIntTaxRate.setForeground((isError) ? Color.red  
						 							  : (isChanged) ? Color.magenta
						 									  		: myFore);
				theIntTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the Dividend Tax Rate */
				theDivTaxRate.setText(theTaxYear.getDivTaxRate().format(true));
				theDivTaxRate.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_DIVTAX);
				theDivTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_DIVTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_DIVTAX);
				theDivTaxRate.setForeground((isError) ? Color.red  
						 							  : (isChanged) ? Color.magenta
						 									  		: myFore);
				theDivTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the High Dividend Tax Rate */
				theHiDivTaxRate.setText(theTaxYear.getHiDivTaxRate().format(true));
				theHiDivTaxRate.setEnabled(!theTaxYear.isDeleted());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_HDVTAX);
				theHiDivTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_HDVTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_HDVTAX);
				theHiDivTaxRate.setForeground((isError) ? Color.red  
						 								: (isChanged) ? Color.magenta
						 											  : myFore);
				theHiDivTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the Additional Tax Rate */
				theAddTaxRate.setText((theTaxYear.hasAdditionalTaxBand() &&
						 			   theTaxYear.getAddTaxRate() != null) 
											? theTaxYear.getAddTaxRate().format(true)
											: "");
				theAddTaxRate.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_ADDTAX);
				theAddTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_ADDTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_ADDTAX);
				if ((isError) && (theTaxYear.getAddTaxRate() == null)) {
					theAddTaxRate.setBackground(Color.red);
					theAddTaxRate.setForeground(myFore);
				}	
				else {
					theAddTaxRate.setForeground((isError) ? Color.red  
						 						  		  : (isChanged) ? Color.magenta
						 						  					  	: myFore);
					theAddTaxRate.setBackground(Color.white);
				}
				theAddTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the Additional Dividend Tax Rate */
				theAddDivTaxRate.setText((theTaxYear.hasAdditionalTaxBand() &&
						 				  theTaxYear.getAddDivTaxRate() != null) 
											? theTaxYear.getAddDivTaxRate().format(true)
											: "");
				theAddDivTaxRate.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_ADVTAX);
				theAddDivTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_ADVTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_ADVTAX);
				if ((isError) && (theTaxYear.getAddDivTaxRate() == null)) {
					theAddDivTaxRate.setBackground(Color.red);
					theAddDivTaxRate.setForeground(myFore);
				}	
				else {
					theAddDivTaxRate.setForeground((isError) ? Color.red  
						 						  			 : (isChanged) ? Color.magenta
						 						  				  	       : myFore);
					theAddDivTaxRate.setBackground(Color.white);
				}
				theAddDivTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the Capital Tax Rate */
				theCapTaxRate.setText((!theTaxYear.hasCapitalGainsAsIncome()  &&
						 			   theTaxYear.getCapTaxRate() != null)
											? theTaxYear.getCapTaxRate().format(true)
											: "");
				theCapTaxRate.setEnabled(!theTaxYear.isDeleted() && !theTaxYear.hasCapitalGainsAsIncome());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_CAPTAX);
				theCapTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_CAPTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_CAPTAX);
				if ((isError) && (theTaxYear.getCapTaxRate() == null)) {
					theCapTaxRate.setBackground(Color.red);
					theCapTaxRate.setForeground(myFore);
				}	
				else {
					theCapTaxRate.setForeground((isError) ? Color.red  
						 						  			  : (isChanged) ? Color.magenta
						 						  					  	    : myFore);
					theCapTaxRate.setBackground(Color.white);
				}
				theCapTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Set the High Capital Tax Rate */
				theHiCapTaxRate.setText((!theTaxYear.hasCapitalGainsAsIncome() &&
										 theTaxYear.getHiCapTaxRate() != null)
											? theTaxYear.getHiCapTaxRate().format(true)
											: null);
				theHiCapTaxRate.setEnabled(!theTaxYear.isDeleted() && !theTaxYear.hasCapitalGainsAsIncome());
				isChanged = theTaxYear.fieldChanged(TaxParms.FIELD_HCPTAX);
				theHiCapTaxRate.setFont((isChanged) ? theChgNumFont : theNumFont);
				if (isError = theTaxYear.hasErrors(TaxParms.FIELD_HCPTAX)) 
					myTip  = theTaxYear.getFieldError(TaxParms.FIELD_HCPTAX);
				if ((isError) && (theTaxYear.getHiCapTaxRate() == null)) {
					theHiCapTaxRate.setBackground(Color.red);
					theHiCapTaxRate.setForeground(myFore);
				}	
				else {
					theHiCapTaxRate.setForeground((isError) ? Color.red  
						 						  			: (isChanged) ? Color.magenta
						 						  				  	      : myFore);
					theHiCapTaxRate.setBackground(Color.white);
				}
				theHiCapTaxRate.setToolTipText((isError) ? myTip : null);
			
				/* Make sure buttons are visible */
				theDelButton.setVisible(theTaxYear.isDeleted() || 
										((!theTaxYear.isActive()) &&
										 ((theTaxYear.getPrev() == null) ||
										  (theTaxYear.getNext() == null))));
				theDelButton.setText(theTaxYear.isDeleted() ? "Recover" : "Delete");
				
				/* Enable buttons */
				theInsButton.setEnabled(!theTaxYear.hasChanges() &&
										theTaxYears.getLast().isActive());
				theUndoButton.setEnabled(theTaxYear.hasChanges());
			}
			
			/* else no account */
			else {
				/* Set blank text */
				theYear.setText("");
				theAllowance.setText("");
				theLoAgeAllow.setText("");
				theHiAgeAllow.setText("");
				theRental.setText("");
				theCapitalAllow.setText("");
				theAgeAllowLimit.setText("");
				theAddAllowLimit.setText("");
				theAddIncomeBndry.setText("");
				theLoTaxBand.setText("");
				theBasicTaxBand.setText("");
				theLoTaxRate.setText("");
				theBasicTaxRate.setText("");
				theHiTaxRate.setText("");
				theIntTaxRate.setText("");
				theDivTaxRate.setText("");
				theHiDivTaxRate.setText("");
				theCapTaxRate.setText("");
				theHiCapTaxRate.setText("");
				theAddTaxRate.setText("");
				theAddDivTaxRate.setText("");
				
				/* Disable data entry */
				theYear.setEnabled(false);
				theAllowance.setEnabled(false);
				theLoAgeAllow.setEnabled(false);
				theHiAgeAllow.setEnabled(false);
				theRental.setEnabled(false);
				theCapitalAllow.setEnabled(false);
				theAgeAllowLimit.setEnabled(false);
				theAddAllowLimit.setEnabled(false);
				theAddIncomeBndry.setEnabled(false);
				theLoTaxBand.setEnabled(false);
				theBasicTaxBand.setEnabled(false);
				theLoTaxRate.setEnabled(false);
				theBasicTaxRate.setEnabled(false);
				theHiTaxRate.setEnabled(false);
				theIntTaxRate.setEnabled(false);
				theDivTaxRate.setEnabled(false);
				theHiDivTaxRate.setEnabled(false);
				theCapTaxRate.setEnabled(false);
				theHiCapTaxRate.setEnabled(false);
				theAddTaxRate.setEnabled(false);
				theAddDivTaxRate.setEnabled(false);
				
				/* Handle buttons */
				theUndoButton.setEnabled(false);
				theInsButton.setEnabled(false);
				theDelButton.setVisible(false);
			}
		}
		
		/* Update text */
		private void updateText() {
			String          myText;
			finObject.Money myMoney;
			finObject.Rate  myRate;

			/* Access the value */
			myText  = theAllowance.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setAllowance(myMoney);    

			/* Access the value */
			myText  = theLoAgeAllow.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setLoAgeAllow(myMoney);    

			/* Access the value */
			myText  = theHiAgeAllow.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setHiAgeAllow(myMoney);    

			/* Access the value */
			myText  = theCapitalAllow.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setCapitalAllow(myMoney);    

			/* Access the value */
			myText  = theRental.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setRentalAllowance(myMoney);    

			/* Access the value */
			myText  = theAgeAllowLimit.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setAgeAllowLimit(myMoney);    

			/* Access the value */
			myText  = theAddAllowLimit.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setAddAllowLimit(myMoney);    

			/* Access the value */
			myText  = theAddIncomeBndry.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setAddIncBound(myMoney);    

			/* Access the value */
			myText  = theLoTaxBand.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setLoBand(myMoney);    

			/* Access the value */
			myText  = theBasicTaxBand.getText();
			myMoney = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myMoney = finObject.Money.Parse(myText); 
			
			/* Store the appropriate value */
			if (myMoney != null) theTaxYear.setBasicBand(myMoney);    

			/* Access the value */
			myText  = theLoTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setLoTaxRate(myRate);    

			/* Access the value */
			myText  = theBasicTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setBasicTaxRate(myRate);    

			/* Access the value */
			myText  = theHiTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setHiTaxRate(myRate);    

			/* Access the value */
			myText  = theIntTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setIntTaxRate(myRate);    

			/* Access the value */
			myText  = theDivTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setDivTaxRate(myRate);    

			/* Access the value */
			myText  = theHiDivTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setHiDivTaxRate(myRate);    

			/* Access the value */
			myText  = theAddTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setAddTaxRate(myRate);    

			/* Access the value */
			myText  = theAddDivTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setAddDivTaxRate(myRate);    

			/* Access the value */
			myText  = theCapTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value */
			if (myRate != null) theTaxYear.setCapTaxRate(myRate);    

			/* Access the value */
			myText  = theHiCapTaxRate.getText();
			myRate = null;
			if (myText.length() == 0) myText = null;
			if (myText != null) myRate = finObject.Rate.Parse(myText); 
			
			/* Store the appropriate value (allow null) */
			theTaxYear.setHiCapTaxRate(myRate);    
		}
		
		/* Undo changes */
		private void undoChanges() {
			/* If the account has changes */
			if (theTaxYear.hasHistory()) {
				/* Pop last value */
				theTaxYear.popHistory();
				
				/* Re-validate the item */
				theTaxYear.clearErrors();
				theTaxYear.validate();
			
				/* If the item is now clean */
				if (!theTaxYear.hasHistory()) {
					/* Set the new status */
					theTaxYear.setState(finObject.State.CLEAN);
				}
				
				/* Notify changes */
				notifyChanges();
			}
			
			/* else if this is a new tax year */
			else if (theTaxYear.getState() == State.NEW) {
				/* Delete the new tax year */
				delNewTaxYear();
			}
		}
		
		/* ItemStateChanged listener event */
		public void itemStateChanged(ItemEvent evt) {
			String                myName;

			/* Ignore selection if refreshing data */
			if (refreshingData) return;
			
			/* If this event relates to the years box */
			if (evt.getSource() == (Object)theYearsBox) {
				myName = (String)evt.getItem();
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					/* Select the new year and notify the change */
					setSelection(theTaxYears.searchFor(myName));
					notifyChanges();
				}
			}
			
			/* If this event relates to the regimes box */
			if (evt.getSource() == (Object)theRegimesBox) {
				myName = (String)evt.getItem();
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					/* Push history */
					theTaxYear.pushHistory();
					
					/* Select the new regime */
					theTaxYear.setTaxRegime(theTaxRegimes.searchFor(myName));
					
					/* Clear Capital tax rates if required */
					if (theTaxYear.hasCapitalGainsAsIncome()) {
						theTaxYear.setCapTaxRate(null);
						theTaxYear.setHiCapTaxRate(null);
					}

					/* Clear Additional values if required */
					if (!theTaxYear.hasAdditionalTaxBand()) {
						theTaxYear.setAddAllowLimit(null);
						theTaxYear.setAddIncBound(null);
						theTaxYear.setAddTaxRate(null);
						theTaxYear.setAddDivTaxRate(null);
					}

					/* Check for changes */
					if (theTaxYear.checkForHistory()) {
						/* Note that the item has changed */
						theTaxYear.setState(State.CHANGED);
						
						/* Note that changes have occurred */
						notifyChanges();
					}
				}
			}
			
			/* If this event relates to the showDeleted box */
			if (evt.getSource() == (Object)theShowDeleted) {
				/* Note the new criteria and re-build lists */
				doShowDeleted = theShowDeleted.isSelected();
				refreshData();
			}
		}

		/* Delete New Account */
		private void delNewTaxYear() {
			/* Select the last tax year */
			setSelection(theTaxYears.getLast());
		}
		
		/* New Account */
		private void newTaxYear() {
			/* Create a tax view for a new tax year */
			theTaxView = theView.new TaxParmView();
		
			/* Access the account */
			theTaxYear = theTaxView.getTaxYear();			
			
			/* Notify changes */
			notifyChanges();
		}
		
		/* ActionPerformed listener event */
		public void actionPerformed(ActionEvent evt) {			
			/* If this event relates to the new button */
			if (evt.getSource() == (Object)theInsButton) {
				/* Create the new tax year */
				newTaxYear();
				return;
			}
			
			/* If this event relates to the delete button */
			else if (evt.getSource() == (Object)theDelButton) {
				/* else if this is a new account */
				if (theTaxYear.getState() == State.NEW) {
					/* Delete the new tax year */
					delNewTaxYear();
				}
				
				/* Else we should just delete/recover the year */
				else {
					/* Set the appropriate state */
					theTaxYear.setState(theTaxYear.isDeleted() ? State.RECOVERED
															   : State.DELETED);
					
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
			theTaxYear.pushHistory();
			
			/* If this event relates to the update-able fields */
			if ((evt.getSource() == (Object)theAllowance)  		||
				(evt.getSource() == (Object)theLoAgeAllow) 		||
				(evt.getSource() == (Object)theHiAgeAllow) 		||
				(evt.getSource() == (Object)theRental)     		||
				(evt.getSource() == (Object)theCapitalAllow)	||
				(evt.getSource() == (Object)theAgeAllowLimit)	||
				(evt.getSource() == (Object)theAddAllowLimit)	||
				(evt.getSource() == (Object)theAddIncomeBndry)	||
			    (evt.getSource() == (Object)theLoTaxBand)  		||
			    (evt.getSource() == (Object)theBasicTaxBand)	||
			    (evt.getSource() == (Object)theLoTaxRate)  		||
			    (evt.getSource() == (Object)theBasicTaxRate)	||
				(evt.getSource() == (Object)theHiTaxRate)  		||
			    (evt.getSource() == (Object)theIntTaxRate) 		||
			    (evt.getSource() == (Object)theDivTaxRate) 		||
				(evt.getSource() == (Object)theHiDivTaxRate)	||
			    (evt.getSource() == (Object)theCapTaxRate) 		||
				(evt.getSource() == (Object)theHiCapTaxRate)	||
			    (evt.getSource() == (Object)theAddTaxRate) 		||
				(evt.getSource() == (Object)theAddDivTaxRate)) {
				/* Update the text */
				updateText();
			}
			
			/* Check for changes */
			if (theTaxYear.checkForHistory()) {
				/* Note that the item has changed */
				theTaxYear.setState(State.CHANGED);
				
				/* Note that changes have occurred */
				notifyChanges();
			}
		}			
	}
	
	/* PatternYear table management */
	public class PatternYearTable extends 		JTable
								  implements 	finSwing.financeView,
												ActionListener {
		/* Members */
		private static final long serialVersionUID = 7406051901546832781L;
		private finView.Extract             	theExtract	= null;
		private finData.EventList	        	theEvents	= null;
		private finMaintenance					theParent	= null;
		private JPanel						 	thePanel	= null;
		private finData.TaxParms				theYear		= null;
		private patternYearModel				theModel	= null;
		private JButton							thePattern	= null;
		private finUtils.DateUtil.Renderer 		theDateRenderer   = null;
		private finUtils.MoneyUtil.Renderer 	theMoneyRenderer  = null;
		private finUtils.StringUtil.Renderer 	theStringRenderer = null;

		/* Access methods */
		public JPanel  getPanel()			{ return thePanel; }
		
		/* Table headers */
		private static final String titleDate    = "Date";
		private static final String titleDesc    = "Description";
		private static final String titleTrans   = "TransactionType";
		private static final String titleAmount  = "Amount";
		private static final String titleDebit   = "Debit";
		private static final String titleCredit  = "Credit";
			
		/* Table columns */
		private static final int COLUMN_DATE 	 = 0;
		private static final int COLUMN_DESC 	 = 1;
		private static final int COLUMN_TRANTYP  = 2;
		private static final int COLUMN_AMOUNT	 = 3;
		private static final int COLUMN_DEBIT	 = 4;
		private static final int COLUMN_CREDIT	 = 5;
		private static final int NUM_COLUMNS	 = 6;
			
		/* Constructor */
		public PatternYearTable(finMaintenance pParent) {
			TableColumnModel    myColModel;
			TableColumn			myCol;
			JScrollPane			myScroll;
			GroupLayout			myLayout;
				
			/* Record the passed details */
			theParent = pParent;
			
			/* Set the table model */
			theModel  = new patternYearModel();
				
			/* Set the table model */
			setModel(theModel);
				
			/* Access the column model */
			myColModel = getColumnModel();
			
			/* Create the relevant formatters/editors */
			theDateRenderer   = new finUtils.DateUtil.Renderer();
			theMoneyRenderer  = new finUtils.MoneyUtil.Renderer();
			theStringRenderer = new finUtils.StringUtil.Renderer();
			
			/* Set the relevant formatters/editors */
			myCol = myColModel.getColumn(COLUMN_DATE);
			myCol.setCellRenderer(theDateRenderer);
			myCol.setPreferredWidth(80);
				
			myCol = myColModel.getColumn(COLUMN_DESC);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setPreferredWidth(150);
			
			myCol = myColModel.getColumn(COLUMN_TRANTYP);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setPreferredWidth(110);
			
			myCol = myColModel.getColumn(COLUMN_AMOUNT);
			myCol.setCellRenderer(theMoneyRenderer);
			myCol.setPreferredWidth(90);
				
			myCol= myColModel.getColumn(COLUMN_DEBIT);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setPreferredWidth(130);
				
			myCol = myColModel.getColumn(COLUMN_CREDIT);
			myCol.setCellRenderer(theStringRenderer);
			myCol.setPreferredWidth(130);
				
			getTableHeader().setReorderingAllowed(false);
			setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

			/* Set the number of visible rows */
			setPreferredScrollableViewportSize(new Dimension(800, 200));
			
			/* Create a new Scroll Pane and add this table to it */
			myScroll     = new JScrollPane();
			myScroll.setViewportView(this);
			
			/* Create the button */
			thePattern = new JButton("Apply");
			thePattern.addActionListener(this);
				
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
		                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
		                    .addComponent(thePattern))
		                .addContainerGap())
		    );
            myLayout.setVerticalGroup(
            	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
		                .addComponent(myScroll)
		                .addComponent(thePattern))
		    );
		}
			
		/* saveData */
		public void saveData() {}
		public void notifyChanges() {}
		public boolean hasUpdates() { return false; }
		public boolean isLocked() { return false; }
		public EditState getEditState() { return EditState.CLEAN; }
		public void performCommand(tableCommand pCmd) {}
		public void notifySelection(Object pObj) {}
			
		/* Get render data for row */
		public void getRenderData(RenderData pData) {
			String     	   myTip = null;
			Color		   myFore;
			Color		   myBack;
			Font		   myFont;
			
			/* Default is black on white */
			myBack = Color.white;
			myFore = Color.black;
			myFont = ((pData.getCol() == COLUMN_AMOUNT) ||
					  (pData.getCol() == COLUMN_DATE))
						? theNumFont : theStdFont;

			/* Set the data */
			pData.setData(myFore, myBack, myFont, myTip);
				
			/* return the data */
			return;
		}
			
		/* refresh data */
		public void refreshData() {
			finData myData = theView.getData();
			finData.TaxParmList myList = myData.getTaxYears();
			theYear = myList.getLast();
			setSelection();
		}
		
		/* Set Selection */
		public void setSelection() {
			if (theYear != null) {
				theExtract = theView.new Extract(theYear);
				theEvents  = theExtract.getEvents();
				thePattern.setVisible(true);
				thePattern.setEnabled(!theYear.isActive() &&
									  theEvents.hasMembers());
			}
			else {
				theExtract = null;
				theEvents  = null;			
				thePattern.setVisible(false);
			}
			theModel.fireTableDataChanged();
			theParent.setVisibleTabs();
		}
			
		/* ActionPerformed listener event */
		public void actionPerformed(ActionEvent evt) {			
			/* If this event relates to the pattern button */
			if (evt.getSource() == (Object)thePattern) {
				/* Apply the extract changes */
				theExtract.applyChanges();
			}
		}
		
		/* Get field for column */
		public int getFieldForCol(int column) {
			/* Switch on column */
			switch (column) {
				default: 				return -1;
			}
		}
			
		/* PatternYear table model */
		public class patternYearModel extends AbstractTableModel {
			private static final long serialVersionUID = 4796112294536415723L;

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
					case COLUMN_DATE: 		return titleDate;
					case COLUMN_DESC: 		return titleDesc;
					case COLUMN_TRANTYP: 	return titleTrans;
					case COLUMN_AMOUNT:		return titleAmount;
					case COLUMN_CREDIT: 	return titleCredit;
					case COLUMN_DEBIT: 		return titleDebit;
					default: 				return null;
				}
			}
			
			/* is get column class */
			public Class<?> getColumnClass(int col) {				
				switch (col) {
					case COLUMN_DESC: 		return String.class;
					case COLUMN_TRANTYP:	return String.class;
					case COLUMN_CREDIT: 	return String.class;
					case COLUMN_DEBIT: 		return String.class;
					default: 				return Object.class;
				}
			}
				
			/* is cell edit-able */
			public boolean isCellEditable(int row, int col) {
				return false;
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
		}
	}
	
	/**
	 * Properties maintenance tab
	 */
	public class PropertiesTab implements ActionListener,
										  ItemListener,
										  ChangeListener {
		/* Properties */
		private finMaintenance						theParent			= null;
		private JPanel                          	thePanel			= null;
		private JPanel                          	theButtons			= null;
		private JPanel                          	theChecks			= null;
		private JTextField							theDBDriver			= null;
		private JTextField							theDBConnect		= null;
		private JTextField							theBaseSSheet		= null;
		private JTextField							theBackupDir		= null;
		private JTextField							theBackupFile		= null;
		private JSpinner							theSpinner			= null;
		private SpinnerDateModel					theModel			= null;
		private JCheckBox							theShowDebug		= null;
		private JCheckBox							theEncryptBackup	= null;
		private JButton								theOKButton			= null;
		private JButton								theResetButton		= null;
		private finView.Properties					theExtract			= null;
		private boolean								refreshingData		= false;
		
		/* Access methods */
		public JPanel       	getPanel()       { return thePanel; }
		
		/* Constructor */
		public PropertiesTab(finMaintenance pParent) {
			JLabel	myDBDriver;
			JLabel	myDBConnect;
			JLabel	myBaseSSheet;
			JLabel	myBackupDir;
			JLabel	myBackupFile;
			JLabel	myBirthDate;
			
			/* Store parent */
			theParent = pParent;
			
			/* Create the labels */
			myDBDriver 		= new JLabel("Driver String:");
			myDBConnect 	= new JLabel("Connection String:");
			myBaseSSheet	= new JLabel("Base Spreadsheet:");
			myBackupDir		= new JLabel("Backup Directory:");
			myBackupFile	= new JLabel("Backup FileName:");
			myBirthDate		= new JLabel("BirthDate:");

			/* Create the text fields */
			theDBDriver 	= new JTextField();
			theDBConnect 	= new JTextField();
			theBaseSSheet	= new JTextField();
			theBackupDir	= new JTextField();
			theBackupFile	= new JTextField();
			
			/* Create the check boxes */
			theShowDebug		= new JCheckBox("Show Debug");
			theEncryptBackup	= new JCheckBox("Encrypt Backups");

			/* Create the spinner */
			theModel    = new SpinnerDateModel();
			theSpinner  = new JSpinner(theModel);
			theSpinner.setEditor(new JSpinner.DateEditor(theSpinner, "dd-MMM-yyyy"));
			
			/* Create the buttons */
			theOKButton 	= new JButton("OK");
			theResetButton 	= new JButton("Reset");
			
			/* Add listeners */
			theDBDriver.addActionListener(this);
			theDBConnect.addActionListener(this);
			theBaseSSheet.addActionListener(this);
			theBackupDir.addActionListener(this);
			theBackupFile.addActionListener(this);
			theModel.addChangeListener(this);
			theOKButton.addActionListener(this);
			theResetButton.addActionListener(this);
			theShowDebug.addItemListener(this);
			theEncryptBackup.addItemListener(this);
			
			/* Create the buttons panel */
			theButtons = new JPanel();
			theButtons.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Save Options"));
			
			/* Create the layout for the panel */
		    GroupLayout myLayout = new GroupLayout(theButtons);
		    theButtons.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addContainerGap()
                    .addComponent(theOKButton)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(theResetButton)
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theOKButton)
                .addComponent(theResetButton)
            );
	            
			/* Create the buttons panel */
			theChecks = new JPanel();
			theChecks.setBorder(javax.swing.BorderFactory
					.createTitledBorder("Options"));
			
			/* Create the layout for the panel */
		    myLayout = new GroupLayout(theChecks);
		    theChecks.setLayout(myLayout);

		    /* Set the layout */
	        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(myLayout.createSequentialGroup()
	        		.addContainerGap()
                    .addComponent(theShowDebug)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(theEncryptBackup)
                    .addContainerGap())
            );
            myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theShowDebug)
                .addComponent(theEncryptBackup)
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
	                	.addComponent(theButtons)
	                	.addComponent(theChecks)
	                    .addGroup(myLayout.createSequentialGroup()
	                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
	                            .addComponent(myDBDriver)
	                            .addComponent(myDBConnect)
	                            .addComponent(myBaseSSheet)
	                            .addComponent(myBackupDir)
	                            .addComponent(myBackupFile)
	                            .addComponent(myBirthDate))
	                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                            .addComponent(theDBDriver, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
	                            .addComponent(theDBConnect, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
	                            .addComponent(theBaseSSheet, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
	                            .addComponent(theBackupDir, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
	                            .addComponent(theBackupFile, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
	                            .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))))
      	            .addContainerGap())
	        );
	        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
			   	.addGroup(myLayout.createSequentialGroup()
	                .addContainerGap()
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	               	.addComponent(myDBDriver)
                        .addComponent(theDBDriver, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        	        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            	       	.addComponent(myDBConnect)
                        .addComponent(theDBConnect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	               	.addComponent(myBaseSSheet)
                        .addComponent(theBaseSSheet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	               	.addComponent(myBackupDir)
                        .addComponent(theBackupDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	               	.addComponent(myBackupFile)
                        .addComponent(theBackupFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
    	               	.addComponent(myBirthDate)
                        .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
   	               	.addComponent(theChecks)
   	               	.addComponent(theButtons))
            );            
		}
		
		/* hasUpdates */
		public boolean hasUpdates() {
			return ((theExtract != null) && (theExtract.hasChanges()));
		}
		
		/* performCommand */
		public void performCommand(tableCommand pCmd) {
			/* Switch on command */
			switch (pCmd) {
				case OK:
					try { theExtract.applyChanges(); } catch (Exception e) {}
					break;
				case RESETALL:
					theExtract.resetData();
					break;
			}
			
			/* Notify Status changes */
			notifyChanges();			
		}
		
		/* Note that changes have been made */
		public void notifyChanges() {
			/* Show the account */
			showProperties();
			
			/* Adjust visible tabs */
			theParent.setVisibleTabs();
		}
		
		/* refreshData */
		public void refreshData() {
			/* Create a new view */
			theExtract = theView.new Properties();

			/* Note that we are refreshing Data */
			refreshingData = true;
			
			/* Show the BirthDate */
			theModel.setValue(theExtract.getBirthDate().getDate());
			
			/* Set the check boxes */
			theShowDebug.setSelected(theExtract.doShowDebug());
			theEncryptBackup.setSelected(theExtract.doEncryptBackups());
			
			/* Notify the changes */
			notifyChanges();

			/* Note that we are finished refreshing Data */
			refreshingData = false;
		}
		
		/* Show Properties */
		public void showProperties() {
			/* Check for changes */
			theExtract.checkChanges();
			
			/* Show the DB details */
			theDBDriver.setText(theExtract.getDBDriver());
			theDBConnect.setText(theExtract.getDBConnection());
			
			/* Show the DB details */
			theBackupDir.setText(theExtract.getBackupDir());
			theBackupFile.setText(theExtract.getBackupFile());
			
			/* Show the BaseSpreadsheet */
			theBaseSSheet.setText(theExtract.getBaseSpreadSheet());
			
			/* Enable the buttons */
			theOKButton.setEnabled(theExtract.hasChanges());
			theResetButton.setEnabled(theExtract.hasChanges());
		}
		
		/* Update text */
		private void updateText() {
			String  myText;

			/* Access the value */
			myText = theDBDriver.getText();
			if (myText.length() != 0) theExtract.setDBDriver(myText);    

			/* Access the value */
			myText = theDBConnect.getText();
			if (myText.length() != 0) theExtract.setDBConnection(myText);

			/* Access the value */
			myText = theBaseSSheet.getText();
			if (myText.length() != 0) theExtract.setBaseSpreadSheet(myText);

			/* Access the value */
			myText = theBackupDir.getText();
			if (myText.length() != 0) theExtract.setBackupDir(myText);

			/* Access the value */
			myText = theBackupFile.getText();
			if (myText.length() != 0) theExtract.setBackupFile(myText);
		}
		
		/* stateChanged listener event */
		public void stateChanged(ChangeEvent evt) {
			finObject.Date myDate;

			/* Ignore selection if refreshing data */
			if (refreshingData) return;
			
			/* If this event relates to the maturity box */
			if (evt.getSource() == (Object)theModel) {
				/* Access the value */
				myDate = new finObject.Date(theModel.getDate());

				/* Store the value */
				theExtract.setBirthDate(myDate);
				
				/* Update the text */
				updateText();
				
				/* Note that changes have occurred */
				notifyChanges();
			}								
		}
		
		/* ActionPerformed listener event */
		public void actionPerformed(ActionEvent evt) {			
			/* If this event relates to the OK button */
			if (evt.getSource() == (Object)theOKButton) {
				/* Perform the command */
				performCommand(tableCommand.OK);
			}
			
			/* If this event relates to the reset button */
			else if (evt.getSource() == (Object)theResetButton) {
				/* Perform the command */
				performCommand(tableCommand.RESETALL);
			}
			
			/* If this event relates to the name field */
			else if ((evt.getSource() == (Object)theDBDriver)   ||
			         (evt.getSource() == (Object)theDBConnect)  ||
			         (evt.getSource() == (Object)theBaseSSheet) ||
			         (evt.getSource() == (Object)theBackupDir)  ||
			         (evt.getSource() == (Object)theBackupFile)) {
				/* Update the text */
				updateText();
				
				/* Notify changes */
				notifyChanges();
			}			
		}			
		
		/* ItemStateChanged listener event */
		public void itemStateChanged(ItemEvent evt) {
			/* Ignore selection if refreshing data */
			if (refreshingData) return;
						
			/* If this event relates to the showDebug box */
			if (evt.getSource() == (Object)theShowDebug) {
				/* Note the new criteria and re-build lists */
				theExtract.setDoShowDebug(theShowDebug.isSelected());

				/* Update the text */
				updateText();
				
				/* Notify changes */
				notifyChanges();
			}

			/* If this event relates to the encryptBackup box */
			if (evt.getSource() == (Object)theEncryptBackup) {
				/* Note the new criteria and re-build lists */
				theExtract.setDoEncryptBackups(theEncryptBackup.isSelected());

				/* Update the text */
				updateText();
				
				/* Notify changes */
				notifyChanges();
			}
		}
	}
}