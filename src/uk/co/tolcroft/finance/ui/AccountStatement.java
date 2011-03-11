package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.EditButtons.*;
import uk.co.tolcroft.finance.ui.controls.StatementSelect.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.*;

public class AccountStatement extends FinanceTableModel<Statement.Line> implements ActionListener {
	/* Members */
	private static final long serialVersionUID = -9123840084764342499L;

	private View					theView				= null;
	private StatementModel			theModel			= null;
	private Account					theAccount   		= null;
	private Statement            	theStatement 		= null;
	private Statement.List  	 	theLines 	 		= null;
	private Account.List			theAccounts			= null;
	private TransactionType.List	theTransTypes		= null;
	private JPanel					thePanel	 		= null;
	private JScrollPane				theScroll			= null;
	private AccountStatement		theTable	 		= this;
	private statementMouse			theMouse	 		= null;
	private AccountTab				theParent    		= null;
	private MainTab          		theTopWindow      	= null;
	private Date.Range				theRange	 		= null;
	private DateRange 				theSelect	 		= null;
	private StatementSelect			theStateBox 		= null;
	private EditButtons    			theRowButs   		= null;
	private Renderer.DateCell 		theDateRenderer   	= null;
	private Editor.DateCell 		theDateEditor     	= null;
	private Renderer.MoneyCell 		theMoneyRenderer  	= null;
	private Editor.MoneyCell 		theMoneyEditor    	= null;
	private Renderer.UnitCell 		theUnitsRenderer  	= null;
	private Editor.UnitCell 		theUnitsEditor    	= null;
	private Renderer.StringCell 	theStringRenderer 	= null;
	private Editor.StringCell 		theStringEditor   	= null;
	private Renderer.DilutionCell 	theDilutionRenderer	= null;
	private Editor.DilutionCell 	theDilutionEditor  	= null;
	private Renderer.IntegerCell 	theIntegerRenderer 	= null;
	private Editor.IntegerCell 		theIntegerEditor   	= null;
	private Editor.ComboBoxCell 	theComboEditor    	= null;
	private DebugEntry				theDebugEntry		= null;
	private ErrorPanel				theError			= null;
	private boolean					hasBalance		  	= true;
	private boolean					hasDilution		  	= true;
	private boolean					hasTaxCredit	  	= true;
	private boolean					hasYears	  		= true;
	private TableColumn				theCreditCol	  	= null;
	private TableColumn				theDebitCol	  		= null;
	private TableColumn				theBalanceCol	  	= null;
	private TableColumn				theDiluteCol	  	= null;
	private TableColumn				theTaxCredCol	  	= null;
	private TableColumn				theYearsCol	  		= null;
	private ComboSelect				theComboList    	= null;
	private StatementType			theStateType		= null;

	/* Access methods */
	public boolean hasHeader()		 	{ return true; }
	public boolean hasCreditChoice() 	{ return true; }
	public JPanel  getPanel()			{ return thePanel; }

	/* Access the debug entry */
	public DebugEntry getDebugEntry()	{ return theDebugEntry; }
	
	/* Table headers */
	private static final String titleDate      = "Date";
	private static final String titleDesc      = "Description";
	private static final String titleTrans     = "TransactionType";
	private static final String titlePartner   = "Partner";
	private static final String titleCredit    = "Credit";
	private static final String titleDebit     = "Debit";
	private static final String titleBalance   = "Balance";
	private static final String titleDilution  = "Dilution";
	private static final String titleTaxCredit = "TaxCredit";
	private static final String titleYears     = "Years";
	
	/* Table columns */
	private static final int COLUMN_DATE 	 	= 0;
	private static final int COLUMN_TRANTYP  	= 1;
	private static final int COLUMN_DESC 	 	= 2;
	private static final int COLUMN_PARTNER	 	= 3;
	private static final int COLUMN_CREDIT	 	= 4;
	private static final int COLUMN_DEBIT 	 	= 5;
	private static final int COLUMN_BALANCE	 	= 6;
	private static final int COLUMN_DILUTION	= 7;
	private static final int COLUMN_TAXCREDIT 	= 8;
	private static final int COLUMN_YEARS	 	= 9;
	private static final int NUM_COLUMNS	 	= 10;
				
	/**
	 * Constructor for Statement Window
	 * @param pParent the parent window
	 */
	public AccountStatement(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel myColModel;
		TableColumn		 myCol;
		GroupLayout		 myLayout;
		
		/* Store passed details */
		theParent    = pParent;
		theView   	 = pParent.getView();
		theTopWindow = pParent.getTopWindow();

		/* Create the model and declare it to our superclass */
		theModel  = new StatementModel();
		setModel(theModel);
		
		/* Access the column model */
		myColModel = getColumnModel();
		
		/* Create the relevant formatters/editors */
		theDateRenderer     = new Renderer.DateCell();
		theDateEditor       = new Editor.DateCell();
		theMoneyRenderer    = new Renderer.MoneyCell();
		theMoneyEditor      = new Editor.MoneyCell();
		theUnitsRenderer    = new Renderer.UnitCell();
		theUnitsEditor      = new Editor.UnitCell();
		theStringRenderer   = new Renderer.StringCell();
		theStringEditor     = new Editor.StringCell();
		theDilutionRenderer = new Renderer.DilutionCell();
		theDilutionEditor   = new Editor.DilutionCell();
		theIntegerRenderer  = new Renderer.IntegerCell();
		theIntegerEditor    = new Editor.IntegerCell();
		theComboEditor      = new Editor.ComboBoxCell();
		
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
		myCol.setPreferredWidth(90);
		theCreditCol = myCol;
		
		myCol = myColModel.getColumn(COLUMN_DEBIT);
		myCol.setPreferredWidth(90);
		theDebitCol = myCol;
		
		myCol = myColModel.getColumn(COLUMN_BALANCE);
		myCol.setPreferredWidth(90);
		theBalanceCol = myCol;
		
		myCol = myColModel.getColumn(COLUMN_DILUTION); 
		myCol.setCellRenderer(theDilutionRenderer);
		myCol.setCellEditor(theDilutionEditor);
		myCol.setPreferredWidth(80);
		theDiluteCol = myCol;
		
		myCol = myColModel.getColumn(COLUMN_TAXCREDIT); 
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setCellEditor(theMoneyEditor);
		myCol.setPreferredWidth(90);
		theTaxCredCol = myCol;
		
		myCol = myColModel.getColumn(COLUMN_YEARS); 
		myCol.setCellRenderer(theIntegerRenderer);
		myCol.setCellEditor(theIntegerEditor);
		myCol.setPreferredWidth(50);
		theYearsCol = myCol;
		
		getTableHeader().setReorderingAllowed(false);
			
		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(900, 200));
		
		/* Add the mouse listener */
		theMouse = new statementMouse();
		addMouseListener(theMouse);
		
		/* Create the sub panels */
		theSelect    = new DateRange(this);
		theStateBox  = new StatementSelect(this);
		theRowButs   = new EditButtons(this, InsertStyle.CREDITDEBIT);
		
		/* Create a new Scroll Pane and add this table to it */
		theScroll     = new JScrollPane();
		theScroll.setViewportView(this);
		        
        /* Create the debug entry, attach to AccountDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("Statement");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
 
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
        	        	.addGroup(myLayout.createSequentialGroup()
                                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                		.addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        	        			.addContainerGap()
                                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                		.addComponent(theStateBox.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	                    .addComponent(theScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theError.getPanel())
       	        	.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
       	        			.addComponent(theSelect.getPanel())
      	        			.addComponent(theStateBox.getPanel()))
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(theScroll)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theRowButs.getPanel())
	                .addContainerGap())
	    );
	}

	/**
	 * Save changes from the view into the underlying data
	 */
	public boolean calculateTable() {
		/* Reset the balance */
		if (theStatement != null) {
			/* Protect against exceptions */
			try {
				/* Reset the balances */
				theStatement.resetBalances();
			}
			/* Catch Exceptions */
			catch (Exception e) {
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to calculate table",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}
		}
		/* Return that table refresh is required */
		return true;
	}
	
	/**
	 * Save changes from the view into the underlying data
	 */
	public void saveData() {
		if (theStatement != null) {
			theStatement.applyChanges();
		}
	}
	
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);
		theStateBox.getPanel().setVisible(!isError);

		/* Lock scroll-able area */
		theScroll.setEnabled(!isError);

		/* Lock row/tab buttons area */
		theRowButs.getPanel().setEnabled(!isError);
	}
	
	/**
	 *  Notify table that there has been a change in selection by an underlying control
	 *  @param obj the underlying control that has changed selection
	 */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the buttons */
		if (obj == (Object) theRowButs) {
			/* Set the correct show selected value */
			super.setShowDeleted(theRowButs.getShowDel());
		}
		
		/* else if this is a change from the range */
		else if (obj == (Object) theSelect) {
			/* Protect against exceptions */
			try {
				/* Set the new range */
				setSelection(theSelect.getRange());
				
				/* Create SavePoint */
				theSelect.createSavePoint();
			}
			
			/* Catch Exceptions */
			catch (Exception e) {
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

		/* else if this is a change from the type */
		else if (obj == (Object) theStateBox) {
			/* Reset the account */
			try {setSelection(theAccount); } catch (Throwable e) {}
		}
	}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() {
		DataSet myData;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access TransTypes and Accounts */
		theTransTypes 	= myData.getTransTypes();
		theAccounts 	= myData.getAccounts();
		
		/* Access the combo list from parent */
		theComboList 	= theParent.getComboList();
		
		/* Update the possible date range */
		Date.Range myRange = theView.getRange();
		theSelect.setOverallRange(myRange);
		
		/* Create SavePoint */
		theSelect.createSavePoint();
	}
	
	/**
	 * Set Selection to the specified account
	 * @param pAccount the Account for the extract
	 */
	public void setSelection(Account pAccount) throws Exception {
		theRange     = theSelect.getRange();
		theDateEditor.setRange(theRange);
		theAccount   = pAccount;
		theStateBox.setSelection(pAccount);
		theStateType = theStateBox.getStatementType();
		if (theAccount != null) {
			theStatement = new Statement(theView, pAccount, theRange);
			theLines     = theStatement.getLines();
		}
		else {
			theStatement = null;
			theLines     = null;
		}
		setColumns();
		super.setList(theLines);
		theDebugEntry.setObject(theStatement);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
		theSelect.setLockDown();
		theStateBox.setLockDown();
	}
	
	/**
	 * Set the column to be visible or not
	 * @param pColumn the column
	 * @param bVisible should column be visible or not
	 */
	private void setVisibleColumn(int pColumn, boolean bVisible) {
		boolean hideSubsequent = false;
		
		/* Switch on the column id */
		switch (pColumn) {
			case COLUMN_BALANCE:
				/* If we are in the wrong state */
				if (hasBalance != bVisible) {
					/* If we are setting to visible */
					if (bVisible) {
						/* Set the column to visible */
						addColumn(theBalanceCol);
						hasBalance = true;
						
						/* Hide subsequent columns */
						hideSubsequent = true;
					}
					
					/* else we are setting to invisible */
					else {
						/* Set the column to invisible */
						removeColumn(theBalanceCol);
						hasBalance = false;
					}
				}
				break;
			case COLUMN_DILUTION:
				/* If we are in the wrong state */
				if (hasDilution != bVisible) {
					/* If we are setting to visible */
					if (bVisible) {
						/* Set the column to visible */
						addColumn(theDiluteCol);
						hasDilution = true;
						
						/* Hide subsequent columns */
						hideSubsequent = true;
					}
					
					/* else we are setting to invisible */
					else {
						/* Set the column to invisible */
						removeColumn(theDiluteCol);
						hasDilution = false;
					}
				}
				break;
			case COLUMN_TAXCREDIT:
				/* If we are in the wrong state */
				if (hasTaxCredit != bVisible) {
					/* If we are setting to visible */
					if (bVisible) {
						/* Set the column to visible */
						addColumn(theTaxCredCol);
						hasTaxCredit = true;

						/* Hide subsequent columns */
						hideSubsequent = true;
					}
					
					/* else we are setting to invisible */
					else {
						/* Set the column to invisible */
						removeColumn(theTaxCredCol);
						hasTaxCredit = false;
					}
				}
				break;
			case COLUMN_YEARS:
				/* If we are in the wrong state */
				if (hasYears != bVisible) {
					/* If we are setting to visible */
					if (bVisible) {
						/* Set the column to visible */
						addColumn(theYearsCol);
						hasYears = true;

						/* Hide subsequent columns */
						hideSubsequent = true;
					}
					
					/* else we are setting to invisible */
					else {
						/* Set the column to invisible */
						removeColumn(theYearsCol);
						hasYears = false;
					}
				}
				break;
		}
		
		/* If we need to hide subsequent columns */
		if (hideSubsequent) {
			/* Loop through subsequent columns */
			for (int col = pColumn+1;
				 col < NUM_COLUMNS;
				 col++) {
				/* Hide the column */
				setVisibleColumn(col, false);
			}
		}
	}
	
	/**
	 * Set visible columns according to the statement type
	 */
	public void setColumns() {
		AccountType myType;
		
		/* Switch on statement type */
		switch (theStateType) {
			case EXTRACT:
				/* Access account type */
				myType = theAccount.getActType();
				
				/* Hide Balance column */
				setVisibleColumn(COLUMN_BALANCE, false);
				
				/* Hide/Show Dilution column */
				setVisibleColumn(COLUMN_DILUTION, myType.isShares());
				
				/* Hide/Show the TaxCredit column as required */
				setVisibleColumn(COLUMN_TAXCREDIT, 
								 (myType.isMoney() && !myType.isTaxFree()));
				
				/* Hide/Show the years column as required */
				setVisibleColumn(COLUMN_YEARS, myType.isLifeBond());
				
				/* Set money Renderers */
				theCreditCol.setCellRenderer(theMoneyRenderer);
				theDebitCol.setCellRenderer(theMoneyRenderer);
				theBalanceCol.setCellRenderer(theMoneyRenderer);

				/* Set money Editors */
				theCreditCol.setCellEditor(theMoneyEditor);
				theDebitCol.setCellEditor(theMoneyEditor);
				break;				
			case VALUE:
				/* Access account type */
				myType = theAccount.getActType();
				
				/* Hide Dilution column */
				setVisibleColumn(COLUMN_DILUTION, false);
				
				/* Show Balance column */
				setVisibleColumn(COLUMN_BALANCE, true);
				
				/* Hide/Show the TaxCredit column as required */
				setVisibleColumn(COLUMN_TAXCREDIT, 
								 (myType.isMoney() && !myType.isTaxFree()));

				/* Hide/Show the years column as required */
				setVisibleColumn(COLUMN_YEARS, myType.isLifeBond());
				
				/* Set money Renderers */
				theCreditCol.setCellRenderer(theMoneyRenderer);
				theDebitCol.setCellRenderer(theMoneyRenderer);
				theBalanceCol.setCellRenderer(theMoneyRenderer);

				/* Set money Editors */
				theCreditCol.setCellEditor(theMoneyEditor);
				theDebitCol.setCellEditor(theMoneyEditor);
				break;				
			case UNITS:
				/* Hide TaxCredit and Years columns */
				setVisibleColumn(COLUMN_TAXCREDIT, false);
				setVisibleColumn(COLUMN_YEARS, false);
				
				/* Show Balance and dilution columns */
				setVisibleColumn(COLUMN_BALANCE, true);
				setVisibleColumn(COLUMN_DILUTION, true);
				
				/* Set units Renderers */
				theCreditCol.setCellRenderer(theUnitsRenderer);
				theDebitCol.setCellRenderer(theUnitsRenderer);
				theBalanceCol.setCellRenderer(theUnitsRenderer);

				/* Set units Editors */
				theCreditCol.setCellEditor(theUnitsEditor);
				theDebitCol.setCellEditor(theUnitsEditor);
				break;
			case NULL:
				/* Hide all optional columns */
				setVisibleColumn(COLUMN_BALANCE,   false);
				setVisibleColumn(COLUMN_DILUTION,  false);
				setVisibleColumn(COLUMN_TAXCREDIT, false);
				setVisibleColumn(COLUMN_YEARS,     false);
				break;
		}
	}
	
	/**
	 * Call underlying controls to take notice of changes in view/selection
	 */
	public void notifyChanges() {
		/* Update the date range and the state box */
		theSelect.setLockDown();
		theStateBox.setLockDown();
		
		/* Update the row buttons */
		theRowButs.setLockDown();
		
		/* Find the edit state */
		if (theLines != null)
			theLines.findEditState();
		
		/* Update the parent panel */
		theParent.notifyChanges(); 
	}
		
	/**
	 * Set Selection to the specified date range
	 * @param pRange the Date range for the extract
	 */
	public void setSelection(Date.Range pRange) throws Exception {
		if (theAccount != null) {
			theStatement = new Statement(theView, theAccount, pRange);
			theLines     = theStatement.getLines();
		}
		else {
			theStatement = null;
			theLines     = null;
		}
		theRange = pRange;
		theDateEditor.setRange(theRange);
		theStateBox.setSelection(theAccount);
		theStateType = theStateBox.getStatementType();
		setColumns();
		super.setList(theLines);
		theDebugEntry.setObject(theStatement);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
		theSelect.setLockDown();
		theStateBox.setLockDown();
	}
		
	/**
	 * Set selection to the period designated by the referenced control
	 * @param pSource the source control
	 */
	public void selectPeriod(DateRange pSource) {
		/* Adjust the period selection */
		theSelect.setSelection(pSource);
	}
	
	/**
	 * Obtain the Field id associated with the column
	 * @param column the column
	 */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 		return Statement.Line.FIELD_DATE;
			case COLUMN_DESC:		return Statement.Line.FIELD_DESC;
			case COLUMN_TRANTYP:	return Statement.Line.FIELD_TRNTYP;
			case COLUMN_PARTNER:	return Statement.Line.FIELD_PARTNER;
			case COLUMN_CREDIT:		return Statement.Line.FIELD_AMOUNT;
			case COLUMN_DEBIT:		return Statement.Line.FIELD_AMOUNT;
			case COLUMN_DILUTION:	return Statement.Line.FIELD_DILUTION;
			case COLUMN_TAXCREDIT:	return Statement.Line.FIELD_TAXCREDIT;
			case COLUMN_YEARS:		return Statement.Line.FIELD_YEARS;
			default:				return -1; 
		}
	}
		
	/**
	 * Obtain the correct ComboBox for the given row/column
	 */
	public JComboBox getComboBox(int row, int column) {
		Statement.Line 		myLine;
		ComboSelect.Item    mySelect;
		
		/* Access the line */
		myLine = theLines.get(row-1);

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
		
	/**
	 * Perform actions for controls/pop-ups on this table
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {
		String          myCmd;
		String          tokens[];
		int             row = 0;
		Statement.Line  myRow;

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
			myRow = theLines.get(row);
			theTopWindow.selectAccount(myRow.getPartner(), theSelect);
		}
		else if (myCmd.compareTo(statementMouse.popupMPartner) == 0) {
			myRow = theLines.get(row);
			theTopWindow.selectAccountMaint(myRow.getPartner());
		}
		else if (myCmd.compareTo(statementMouse.popupPattern) == 0) {
			myRow = theLines.get(row);
			theParent.addPattern(myRow);
		}
		/* If this is a set Null Dilute request */
		else if (myCmd.compareTo(statementMouse.popupSetNull + "Dilute") == 0) {
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_DILUTION);
			theModel.fireTableCellUpdated(row, COLUMN_DILUTION);
		}
		
		/* If this is a set Null TaxCredit request */
		else if (myCmd.compareTo(statementMouse.popupSetNull + "Credit") == 0) {
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_TAXCREDIT);
			theModel.fireTableCellUpdated(row, COLUMN_TAXCREDIT);
		}
		
		/* If this is a set Null Years request */
		else if (myCmd.compareTo(statementMouse.popupSetNull + "Year") == 0) {
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_YEARS);
			theModel.fireTableCellUpdated(row, COLUMN_YEARS);
		}

		/* If this is a set calculate Tax Credit request */
		else if (myCmd.compareTo(statementMouse.popupCalcTax) == 0) {
			/* Access the Line */
			myRow = theLines.get(row);

			/* Calculate the tax credit */
			Money myCredit = myRow.calculateTaxCredit();
			
			/* set the null value */
			theModel.setValueAt(myCredit, row, COLUMN_TAXCREDIT);
			theModel.fireTableCellUpdated(row, COLUMN_TAXCREDIT);
		}
	}
		
	/**
	 * Check whether the restoration of the passed object is compatible with the current selection
	 * @param pItem the current item
	 * @param pObj the potential object for restoration
	 */
	public boolean isValidObj(DataItem 				pItem,
			  				  DataItem.histObject  	pObj) {
		Statement.Line 		myLine;
		Statement.Values	myLineVals; 
		Event		   		myEvent;
		Event.Values    	myEventVals;
		Date				myDate;
		boolean				isCredit;
		Account				myPartner;			
		Account				mySelf;
		
		/* If this is an Event item */
		if (pItem instanceof Event) {
			/* Access the element and the values */
			myEvent     = (Event)pItem;
			myEventVals = (Event.Values) pObj;
			
			/* Access the values */
			myDate    = myEventVals.getDate();
			isCredit  = Account.differs(myEvent.getDebit(), theAccount);
			myPartner = (isCredit) ? myEventVals.getDebit()
								   : myEventVals.getCredit();
			mySelf 	  = (isCredit) ? myEventVals.getCredit()
								   : myEventVals.getDebit(); 
		}
		
		/* else it is a line item */
		else {
			/* Access the element and the values */
			myLine     = (Statement.Line)pItem;
			myLineVals = (Statement.Values) pObj;
			
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
		if (Account.differs(mySelf, theAccount)) 
			return false;
		
		/* Otherwise OK */
		return true;
	}
		
	/* Statement table model */
	public class StatementModel extends AbstractTableModel {
		private static final long serialVersionUID = 269477444398236458L;

		/**
		 * Get the number of display columns
		 * @return the columns
		 */
		public int getColumnCount() {
			int myCount = NUM_COLUMNS;
			if (!hasBalance) myCount--;
			if (!hasDilution) myCount--;
			if (!hasTaxCredit) myCount--;
			if (!hasYears) myCount--;
			return myCount;
		}
			
		/**
		 * Get the number of rows in the current table
		 * @return the number of rows
		 */
		public int getRowCount() { 
			return (theLines == null) ? 0
					                  : theLines.size() + 1;
		}
		
		/**
		 * Get the name of the column
		 * @param col the column
		 * @return the name of the column
		 */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_DATE:  		return titleDate;
				case COLUMN_DESC:  		return titleDesc;
				case COLUMN_TRANTYP:  	return titleTrans;
				case COLUMN_PARTNER:  	return titlePartner;
				case COLUMN_CREDIT:  	return titleCredit;
				case COLUMN_DEBIT:	 	return titleDebit;
				case COLUMN_BALANCE:	return titleBalance;
				case COLUMN_DILUTION:	return titleDilution;
				case COLUMN_TAXCREDIT:	return titleTaxCredit;
				case COLUMN_YEARS:		return titleYears;
				default:				return null;
			}
		}
			
		/**
		 * Get the object class of the column
		 * @param col the column
		 * @return the class of the objects associated with the column
		 */
		public Class<?> getColumnClass(int col) {				
			switch (col) {
				case COLUMN_DESC:  		return String.class;
				case COLUMN_TRANTYP:  	return String.class;
				case COLUMN_PARTNER:  	return String.class;
				default: 				return Object.class;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
		public boolean isCellEditable(int row, int col) {
			Statement.Line myLine;
			
			/* Locked if the account is closed */
			if (theStatement.getAccount().isClosed()) return false;
			
			/* Lock the start balance */
			if (row == 0) return false;
			
			/* Access the line */
			myLine = theLines.get(row-1);
			
			/* Cannot edit if row is deleted or locked */
			if (myLine.isDeleted() || myLine.isLocked())
				return false;
			
			/* switch on column */
			switch (col) {
				case COLUMN_BALANCE:
					return false;
				case COLUMN_DATE:
					return true;
				case COLUMN_TRANTYP:
					return (myLine.getDate() != null);
				case COLUMN_DESC:
					return ((myLine.getDate() != null) &&
							(myLine.getTransType() != null));
				default:
					if ((myLine.getDate() == null) &&
						(myLine.getDesc() == null) &&
						(myLine.getTransType() == null))
						return false;
					
					/* Access the transaction type */
					TransactionType myType = myLine.getTransType();
					
					/* Handle columns */
					switch (col) {
						case COLUMN_CREDIT:		return myLine.isCredit();
						case COLUMN_DEBIT:		return !myLine.isCredit();
						case COLUMN_YEARS:		return myType.isTaxableGain();
						case COLUMN_TAXCREDIT:	return myType.needsTaxCredit();
						case COLUMN_DILUTION:	return myType.isDilutable();
						default: 				return true;
					}
			}
			
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) {
			Statement.Line 	myLine;
			Statement.Line	myNext;
			Object          o;
			
			/* If this is the first row */
			if (row == 0) { 
				switch (col) {
					case COLUMN_DATE:  		
						return theStatement.getDateRange().getStart();
					case COLUMN_DESC:  		
						return "Starting Balance";
					case COLUMN_BALANCE:  		
						return (theStateType == StatementType.UNITS)
										? theStatement.getStartUnits() 
								        : theStatement.getStartBalance();
					default: return null;
				}
			}

			/* Access the line */
			myLine = theLines.get(row-1);
			myNext = theLines.peekNext(myLine);
			
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
						(!Date.differs(myNext.getDate(), myLine.getDate())))
						o = null;
					else o = (theStateType == StatementType.UNITS)
									? myLine.getBalanceUnits() 
									: myLine.getBalance();
					break;
				case COLUMN_CREDIT:  	
					o = (myLine.isCredit()) ? ((theStateType == StatementType.UNITS)
													? myLine.getUnits()
						 							: myLine.getAmount())
											: null;
					break;
				case COLUMN_DEBIT:
					o = (!myLine.isCredit()) ? ((theStateType == StatementType.UNITS)
													? myLine.getUnits()
													: myLine.getAmount())
											 : null;
					break;
				case COLUMN_DESC:
					o = myLine.getDesc();
					if ((o != null) && (((String)o).length() == 0))
						o = null;
					break;
				case COLUMN_DILUTION:	
					o = myLine.getDilution();
					break;
				case COLUMN_TAXCREDIT:	
					o = myLine.getTaxCredit();
					break;
				case COLUMN_YEARS:	
					o = myLine.getYears();
					break;
				default:
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myLine.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
		
		/**
		 * Set the value at (row, col)
		 * @param obj the object value to set
		 */
		public void setValueAt(Object obj, int row, int col) {
			Statement.Line myLine;
			
			/* Access the line */
			myLine = theLines.get(row-1);
			
			/* Push history */
			myLine.pushHistory();

			/* Protect against exceptions */
			try {
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  
						myLine.setDate((Date)obj);    
						calculateTable();
						break;
					case COLUMN_DESC:  
						myLine.setDescription((String)obj);            
						calculateTable();
						break;
					case COLUMN_TRANTYP:  
						myLine.setTransType(theTransTypes.searchFor((String)obj));    
						calculateTable();
						break;
					case COLUMN_CREDIT:
					case COLUMN_DEBIT:
						if (theStateType == StatementType.UNITS)
							myLine.setUnits((Units)obj);
						else
							myLine.setAmount((Money)obj); 
						calculateTable();
						break;
					case COLUMN_PARTNER:
						myLine.setPartner(theAccounts.searchFor((String)obj));    
						break;
					case COLUMN_DILUTION:  
						myLine.setDilution((Dilution)obj);            
						break;
					case COLUMN_TAXCREDIT:  
						myLine.setTaxCredit((Money)obj);            
						break;
					case COLUMN_YEARS:  
						myLine.setYears((Integer)obj);            
						break;
				}
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				myLine.popHistory();
				myLine.pushHistory();
								
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field at ("
										          + row + "," + col +")",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}
			
			/* Check for changes */
			if (myLine.checkForHistory()) {
				/* Note that the item has changed */
				myLine.setState(DataState.CHANGED);
				theLines.findEditState();
			
				/* Switch on the updated column */
				switch (col) {
					/* redraw whole table if we have updated a sort col */
					case COLUMN_DATE:
					case COLUMN_DESC:
					case COLUMN_TRANTYP: 
						myLine.reSort();
						fireTableDataChanged();
						row = theLines.indexOf(myLine);
						selectRow(row);
						break;
						
					/* Recalculate balance if required */	
					case COLUMN_CREDIT:
					case COLUMN_DEBIT:
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
		private static final String popupSetNull  = "Set Null";
		private static final String popupCalcTax   = "Calculate Tax Credit";
				
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
			Statement.Line	myRow     = null;
					
			if (e.isPopupTrigger() && 
				(theTable.isEnabled())) {
				/* Calculate the row/column that the mouse was clicked at */
				Point p = new Point(e.getX(), e.getY());
				int row = theTable.rowAtPoint(p);
				int col = theTable.columnAtPoint(p);
					
				/* Adjust column for view differences */
				col = theTable.convertColumnIndexToModel(col);
				
				/* If we have an account */
				if ((row > 0) &&
					((!theTable.hasUpdates()) ||
					 (!theTable.isLocked()))) {
					/* Access the row */
					myRow = theLines.get(row-1);
							
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
						
					/* If the table is not locked */
					if (!theTable.isLocked()) {
						/* Create the Add to Pattern choice */
						myItem = new JMenuItem(popupPattern);
						
						/* Set the command and add to menu */
						myItem.setActionCommand(popupPattern + ":" + (row-1));
						myItem.addActionListener(theTable);
						myMenu.add(myItem);

						/* If we have dilute */
						if ((col == COLUMN_DILUTION) &&
							(myRow.getDilution() != null)) {
							/* Create the set null choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + "Dilute:" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have tax credit */
						if ((col == COLUMN_TAXCREDIT) &&
							(myRow.getTaxCredit() != null)) {
							/* Create the set null choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + "Credit:" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have tax credit */
						if ((col == COLUMN_TAXCREDIT) &&
						    ((myRow.getTaxCredit() == null) ||
							 (!myRow.getTaxCredit().isNonZero())) &&
							((myRow.getTransType() != null) &&
							 ((myRow.getTransType().isInterest()) ||
							  (myRow.getTransType().isDividend())))) {
							/* Create the calculate tax choice */
							myItem = new JMenuItem(popupCalcTax);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupCalcTax + ":" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}
						
						/* If we have years */
						if ((col == COLUMN_YEARS) &&
							(myRow.getYears() != null)) {
							/* Create the set null choice */
							myItem = new JMenuItem(popupSetNull);
						
							/* Set the command and add to menu */
							myItem.setActionCommand(popupSetNull + "Year:" + row);
							myItem.addActionListener(theTable);
							myMenu.add(myItem);
						}						
					}
						
					/* Show the pop-up menu */
					myMenu.show(e.getComponent(),
								e.getX(), e.getY());
				}					
			}
		}
	}			
}
