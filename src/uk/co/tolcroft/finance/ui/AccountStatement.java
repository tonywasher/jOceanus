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
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number;
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
	private AccountStatement		theTable	 		= this;
	private statementMouse			theMouse	 		= null;
	private AccountTab				theParent    		= null;
	private MainTab          		theTopWindow      	= null;
	private Date.Range				theRange	 		= null;
	private DateRange 				theSelect	 		= null;
	private EditButtons    			theRowButs   		= null;
	private Renderer.DateCell 		theDateRenderer   	= null;
	private Editor.DateCell 		theDateEditor     	= null;
	private Renderer.MoneyCell 		theMoneyRenderer  	= null;
	private Editor.MoneyCell 		theMoneyEditor    	= null;
	private Renderer.UnitCell 		theUnitsRenderer  	= null;
	private Editor.UnitCell 		theUnitsEditor    	= null;
	private Renderer.StringCell 	theStringRenderer 	= null;
	private Editor.StringCell 		theStringEditor   	= null;
	private Editor.ComboBoxCell 	theComboEditor    	= null;
	private boolean					hasBalance		  	= true;
	private TableColumn				theBalanceCol	  	= null;
	private ComboSelect				theComboList    	= null;
	private boolean					isUnits			  	= false;

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
	public AccountStatement(AccountTab pParent, boolean isUnits) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel myColModel;
		TableColumn		 myCol;
		JScrollPane		 myScroll;
		GroupLayout		 myLayout;
		
		/* Store passed details */
		theParent    = pParent;
		this.isUnits = isUnits;
		theView   	 = pParent.getView();
		theTopWindow = pParent.getTopWindow();

		/* Create the model and declare it to our superclass */
		theModel  = new StatementModel();
		setModel(theModel);
		
		/* Access the column model */
		myColModel = getColumnModel();
		
		/* Create the relevant formatters/editors */
		theDateRenderer   = new Renderer.DateCell();
		theDateEditor     = new Editor.DateCell();
		theMoneyRenderer  = new Renderer.MoneyCell();
		theMoneyEditor    = new Editor.MoneyCell();
		theUnitsRenderer  = new Renderer.UnitCell();
		theUnitsEditor    = new Editor.UnitCell();
		theStringRenderer = new Renderer.StringCell();
		theStringEditor   = new Editor.StringCell();
		theComboEditor    = new Editor.ComboBoxCell();
		
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
		theSelect    = new DateRange(this);
		theRowButs   = new EditButtons(this, InsertStyle.CREDITDEBIT);
		
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
		theSelect.setRange(myRange);
	}
	
	/* Set Selection */
	public void setSelection(Account pAccount) {
		theRange     = theSelect.getRange();
		theDateEditor.setRange(theRange);
		theAccount   = pAccount;
		if (theAccount != null) {
			theStatement = new Statement(theView, pAccount, theRange, isUnits);
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
		theModel.fireTableDataChanged();
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
	public void setSelection(Date.Range pRange) {
		if (theAccount != null) {
			theStatement = new Statement(theView, theAccount, pRange, isUnits);
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
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
		theSelect.setLockDown();
	}
		
	/* Select an explicit period */
	public void selectPeriod(DateRange pSource) {
		/* Adjust the period selection */
		theSelect.setSelection(pSource);
	}
	
	/* Get field for column */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 		return Statement.Line.FIELD_DATE;
			case COLUMN_DESC:		return Statement.Line.FIELD_DESC;
			case COLUMN_TRANTYP:	return Statement.Line.FIELD_TRNTYP;
			case COLUMN_PARTNER:	return Statement.Line.FIELD_PARTNER;
			case COLUMN_CREDIT:		return Statement.Line.FIELD_AMOUNT;
			case COLUMN_DEBIT:		return Statement.Line.FIELD_AMOUNT;
			default:				return -1; 
		}
	}
		
	/* Get combo box for cell */
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
		
	/* action performed listener event */
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
	}
		
	/* Check whether this is a valid Object for selection */
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
			isCredit  = Utils.differs(myEvent.getDebit(), theAccount);
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
		if (Utils.differs(mySelf, theAccount)) 
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
					                  : theLines.size() + 1;
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
						return (isUnits) ? theStatement.getStartUnits() 
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
						(!Utils.differs(myNext.getDate(), myLine.getDate())))
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
					if ((o != null) && (((String)o).length() == 0))
						o = null;
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
		
		/* set value At */
		public void setValueAt(Object obj, int row, int col) {
			Statement.Line myLine;
			
			/* Access the line */
			myLine = theLines.get(row-1);
			
			/* Push history */
			myLine.pushHistory();
			
			/* Store the appropriate value */
			switch (col) {
				case COLUMN_DATE:  
					myLine.setDate((Date)obj);    
					break;
				case COLUMN_DESC:  
					myLine.setDescription((String)obj);            
					break;
				case COLUMN_TRANTYP:  
					myLine.setTransType(theTransTypes.searchFor((String)obj));    
					break;
				case COLUMN_CREDIT:
				case COLUMN_DEBIT:
					if (isUnits) myLine.setUnits((Number.Units)obj);
					else         myLine.setAmount((Number.Money)obj); 
					break;
				case COLUMN_PARTNER:
					myLine.setPartner(theAccounts.searchFor((String)obj));    
					break;
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
						calculateTable();
						fireTableDataChanged();
						row = theLines.indexOf(myLine);
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
			JPopupMenu      myMenu;
			JMenuItem       myItem;
			Statement.Line	myRow     = null;
					
			if (e.isPopupTrigger() && 
				(theTable.isEnabled())) {
				/* Calculate the row/column that the mouse was clicked at */
				Point p = new Point(e.getX(), e.getY());
				int row = theTable.rowAtPoint(p);
					
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
					}
						
					/* Show the pop-up menu */
					myMenu.show(e.getComponent(),
								e.getX(), e.getY());
				}					
			}
		}
	}			
}
