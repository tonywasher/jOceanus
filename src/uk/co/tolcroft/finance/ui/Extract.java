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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.EditButtons.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.*;

public class Extract extends FinanceTableModel<Event> implements ActionListener {
	/* Members */
	private static final long serialVersionUID = -5531752729052421790L;

	private View					theView				= null;
	private ExtractModel			theModel			= null;
	private View.ViewEvents         theExtract   		= null;
	private Event.List  	        theEvents	 		= null;
	private Account.List			theAccounts			= null;
	private TransactionType.List	theTransTypes		= null;
	private MainTab					theParent	 		= null;
	private JPanel					thePanel	 		= null;
	private JComboBox				theTranBox			= null;
	private Extract				 	theTable	 		= this;
	private extractMouse			theMouse	 		= null;
	private Date.Range				theRange	 		= null;
	private DateRange 				theSelect	 		= null;
	private EditButtons    			theRowButs   		= null;
	private SaveButtons  			theTabButs   		= null;
	private Renderer.DateCell 		theDateRenderer   	= null;
	private Editor.DateCell 		theDateEditor     	= null;
	private Renderer.MoneyCell 		theMoneyRenderer  	= null;
	private Editor.MoneyCell 		theMoneyEditor    	= null;
	private Renderer.UnitCell 		theUnitsRenderer  	= null;
	private Editor.UnitCell 		theUnitsEditor    	= null;
	private Renderer.IntegerCell	theIntegerRenderer 	= null;
	private Editor.IntegerCell 		theIntegerEditor   	= null;
	private Renderer.StringCell 	theStringRenderer 	= null;
	private Editor.StringCell 		theStringEditor   	= null;
	private Editor.ComboBoxCell 	theComboEditor    	= null;
	private ComboSelect				theComboList    	= null;
	private boolean					tranPopulated    	= false;

	/* Access methods */
	public JPanel  	getPanel()			{ return thePanel; }
	public boolean 	hasHeader()			{ return false; }
	
	/* Table headers */
	private static final String titleDate    = "Date";
	private static final String titleDesc    = "Description";
	private static final String titleTrans   = "TransactionType";
	private static final String titleAmount  = "Amount";
	private static final String titleDebit   = "Debit";
	private static final String titleCredit  = "Credit";
	private static final String titleUnits   = "Units";
	private static final String titleTaxCred = "TaxCredit";
	private static final String titleYears   = "Years";
		
	/* Table columns */
	private static final int COLUMN_DATE 	 = 0;
	private static final int COLUMN_DESC 	 = 1;
	private static final int COLUMN_TRANTYP  = 2;
	private static final int COLUMN_AMOUNT	 = 3;
	private static final int COLUMN_DEBIT	 = 4;
	private static final int COLUMN_CREDIT	 = 5;
	private static final int COLUMN_UNITS	 = 6;
	private static final int COLUMN_TAXCRED	 = 7;
	private static final int COLUMN_YEARS	 = 8;
	private static final int NUM_COLUMNS	 = 9;
		
	/* Constructor */
	public Extract(MainTab pParent) {
		/* Initialise superclass */
		super(pParent);
		
		/* Declare variables */
		TableColumnModel    myColModel;
		TableColumn			myCol;
		JScrollPane			myScroll;
		GroupLayout			myLayout;
			
		/* Record the passed details */
		theParent = pParent;
		theView   = pParent.getView();

		/* Create the model and declare it to our superclass */
		theModel  = new ExtractModel();
		setModel(theModel);
			
		/* Access the column model */
		myColModel = getColumnModel();
		
		/* Create the relevant formatters/editors */
		theDateRenderer   	= new Renderer.DateCell();
		theDateEditor     	= new Editor.DateCell();
		theMoneyRenderer  	= new Renderer.MoneyCell();
		theMoneyEditor    	= new Editor.MoneyCell();
		theUnitsRenderer  	= new Renderer.UnitCell();
		theUnitsEditor    	= new Editor.UnitCell();
		theIntegerRenderer 	= new Renderer.IntegerCell();
		theIntegerEditor   	= new Editor.IntegerCell();
		theStringRenderer 	= new Renderer.StringCell();
		theStringEditor   	= new Editor.StringCell();
		theComboEditor    	= new Editor.ComboBoxCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setCellEditor(theDateEditor);
		myCol.setPreferredWidth(80);
			
		myCol = myColModel.getColumn(COLUMN_DESC);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theStringEditor);
		myCol.setPreferredWidth(150);
		
		myCol = myColModel.getColumn(COLUMN_TRANTYP);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(110);
		
		myCol = myColModel.getColumn(COLUMN_AMOUNT);
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setCellEditor(theMoneyEditor);
		myCol.setPreferredWidth(90);
			
		myCol= myColModel.getColumn(COLUMN_DEBIT);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(130);
			
		myCol = myColModel.getColumn(COLUMN_CREDIT);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(130);
			
		myCol = myColModel.getColumn(COLUMN_UNITS);
		myCol.setCellRenderer(theUnitsRenderer);
		myCol.setCellEditor(theUnitsEditor);
		myCol.setPreferredWidth(80);
			
		myCol = myColModel.getColumn(COLUMN_TAXCRED);
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setCellEditor(theMoneyEditor);
		myCol.setPreferredWidth(90);
			
		myCol = myColModel.getColumn(COLUMN_YEARS);
		myCol.setCellRenderer(theIntegerRenderer);
		myCol.setCellEditor(theIntegerEditor);
		myCol.setPreferredWidth(50);
			
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(900, 200));
		
		/* Build the combo box */
		theTranBox	  = new JComboBox();
			
		/* Add the mouse listener */
		theMouse = new extractMouse();
		addMouseListener(theMouse);
		
		/* Create the sub panels */
		theSelect    = new DateRange(this);
		theRowButs   = new EditButtons(this, InsertStyle.INSERT);
		theTabButs   = new SaveButtons(this);
			
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
	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
	                	.addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	        		.addComponent(theSelect.getPanel())
	                .addComponent(myScroll)
	                .addComponent(theRowButs.getPanel())
	                .addComponent(theTabButs.getPanel()))
	    );
	}
		
	/* saveData */
	public void saveData() {
		if (theExtract != null) {
			super.validateAll();
			if (!hasErrors()) theExtract.applyChanges();
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
		DataSet 		myData;
		TransactionType	myType;
		
		DataList<TransactionType>.ListIterator myIterator;
		
		/* Access data */
		myData = theView.getData();
		
		/* Access lists */
		theTransTypes = myData.getTransTypes();
		theAccounts	  = myData.getAccounts();
		
		/* Access the combo list from parent */
		theComboList 	= theParent.getComboList();
		
		/* If we have frequencies already populated */
		if (tranPopulated) {	
			/* Remove the frequencies */
			theTranBox.removeAllItems();
			tranPopulated = false;
		}
	
		/* Access the frequency iterator */
		myIterator = theTransTypes.listIterator();
		
		/* Add the Transaction values to the frequencies box */
		while ((myType  = myIterator.next()) != null) {
			/* Skip hidden values */
			if (myType.isHidden()) continue;
			
			/* Add the item to the list */
			theTranBox.addItem(myType.getName());
			tranPopulated = true;
		}
		
		/* Access range */
		Date.Range myRange = theView.getRange();
		theSelect.setRange(myRange);
		theRange = theSelect.getRange();
		setSelection(theRange);
	}
	
	/* Note that there has been a list selection change */
	public void notifyChanges() {
		/* Update the row buttons */
		theRowButs.setLockDown();
		
		/* Find the edit state */
		if (theEvents != null)
			theEvents.findEditState();
		
		/* Update the table buttons */
		theTabButs.setLockDown();
		theSelect.setLockDown();
		
		/* Update the top level tabs */
		theParent.setVisibleTabs();
	}
		
	/* Set Selection */
	public void setSelection(Date.Range pRange) {
		theRange   = pRange;
		if (theRange != null) {
			theDateEditor.setRange(pRange);
			theExtract = theView.new ViewEvents(pRange);
			theEvents  = theExtract.getEvents();
		}
		else {
			theExtract = null;
			theEvents  = null;				
		}
		setList(theEvents);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
		theTabButs.setLockDown();
		theSelect.setLockDown();
		theParent.setVisibleTabs();
	}
		
	/* Select an explicit period */
	public void selectPeriod(DateRange pSource) {
		/* Adjust the period selection */
		theSelect.setSelection(pSource);
		
		/* Explicitly redraw the table */
		setSelection(theSelect.getRange());
	}
	
	/* Get field for column */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 		return Event.FIELD_DATE;
			case COLUMN_DESC:		return Event.FIELD_DESC;
			case COLUMN_TRANTYP:	return Event.FIELD_TRNTYP;
			case COLUMN_AMOUNT:		return Event.FIELD_AMOUNT;
			case COLUMN_CREDIT:		return Event.FIELD_CREDIT;
			case COLUMN_DEBIT:		return Event.FIELD_DEBIT;
			case COLUMN_UNITS: 		return Event.FIELD_UNITS;
			case COLUMN_TAXCRED: 	return Event.FIELD_TAXCREDIT;
			case COLUMN_YEARS: 		return Event.FIELD_YEARS;
			default: 				return -1;
		}
	}
		
	/* Get combo box for cell */
	public JComboBox getComboBox(int row, int column) {
		Event myEvent;
		
		/* Access the event */
		myEvent = theEvents.get(row);

		/* Switch on column */
		switch (column) {
			case COLUMN_TRANTYP:	
				return theTranBox;
			case COLUMN_CREDIT:		
				return theComboList.searchFor(myEvent.getTransType())
							.getCredit();
			case COLUMN_DEBIT:
				return theComboList.searchFor(myEvent.getTransType())
							.getDebit();
			default: 				
				return null;
		}
	}
		
	/* Check whether this is a valid Object for selection */
	public boolean isValidObj(DataItem 				pItem,
							  DataItem.histObject  	pObj) {
		Event.Values myEvent = (Event.Values) pObj;
		
		/* Check whether the date is in range */
		if (theRange.compareTo(myEvent.getDate()) != 0)
			return false;

		/* Check whether the credit account is Locked/Deleted */
		if ((myEvent.getCredit().isLocked()) ||
			(myEvent.getCredit().isDeleted()))
			return false;

		/* Check whether the debit account is Locked/Deleted */
		if ((myEvent.getDebit().isLocked()) ||
			(myEvent.getDebit().isDeleted()))
			return false;

		/* Otherwise OK */
		return true;
	}
		
	/* action performed listener event */
	public void actionPerformed(ActionEvent evt) {
		String      myCmd;
		String      tokens[];
		String      myName = null;
		Account 	myAccount;
		int         row;

		/* Access the action command */
		myCmd  = evt.getActionCommand();
		tokens = myCmd.split(":");
		myCmd  = tokens[0];
		if (tokens.length > 1) myName = tokens[1];
		
		/* Cancel any editing */
		if (isEditing()) cellEditor.cancelCellEditing();
		
		/* If this is an account view request */
		if (myCmd.compareTo(extractMouse.popupView) == 0) {
			/* Access the correct account */
			myAccount = theView.getData().getAccounts().searchFor(myName);
		
			/* Handle commands */
			theParent.selectAccount(myAccount, theSelect);
		}
		
		/* If this is an account maintenance request */
		else if (myCmd.compareTo(extractMouse.popupMaint) == 0) {
			/* Access the correct account */
			myAccount = theView.getData().getAccounts().searchFor(myName);
		
			/* Handle commands */
			theParent.selectAccountMaint(myAccount);
		}
		
		/* If this is a set Null Units request */
		else if (myCmd.compareTo(extractMouse.popupSetNull + "Unit") == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_UNITS);
			theModel.fireTableCellUpdated(row, COLUMN_UNITS);
		}
		
		/* If this is a set Null TaxCredit request */
		else if (myCmd.compareTo(extractMouse.popupSetNull + "Credit") == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_TAXCRED);
			theModel.fireTableCellUpdated(row, COLUMN_UNITS);
		}
		
		/* If this is a set Null Years request */
		else if (myCmd.compareTo(extractMouse.popupSetNull + "Year") == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_YEARS);
			theModel.fireTableCellUpdated(row, COLUMN_UNITS);
		}
	}
		
	/* Extract table model */
	public class ExtractModel extends AbstractTableModel {
		private static final long serialVersionUID = 7997087757206121152L;
			/* get column count */
		public int getColumnCount() { return NUM_COLUMNS; }
		
		/* get row count */
		public int getRowCount() { 
			return (theEvents == null) ? 0
					                   : theEvents.size();
		}
		
		/* get column name */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_DATE:  		return titleDate;
				case COLUMN_DESC:  		return titleDesc;
				case COLUMN_TRANTYP:  	return titleTrans;
				case COLUMN_AMOUNT:  	return titleAmount;
				case COLUMN_CREDIT:  	return titleCredit;
				case COLUMN_DEBIT:	 	return titleDebit;
				case COLUMN_UNITS: 		return titleUnits;
				case COLUMN_TAXCRED: 	return titleTaxCred;
				case COLUMN_YEARS: 		return titleYears;
				default: 				return null;
			}
		}
		
		/* is get column class */
		public Class<?> getColumnClass(int col) {				
			switch (col) {
				case COLUMN_DESC:  		return String.class;
				case COLUMN_TRANTYP:  	return String.class;
				case COLUMN_CREDIT:  	return String.class;
				case COLUMN_DEBIT:  	return String.class;
				default: 				return Object.class;
			}
		}
			
		/* is cell edit-able */
		public boolean isCellEditable(int row, int col) {
			Event myEvent;
			
			/* Access the event */
			myEvent = theEvents.get(row);
			
			/* Cannot edit if row is deleted or locked */
			if (myEvent.isDeleted() || myEvent.isLocked())
				return false;
				
			/* switch on column */
			switch (col) {
				case COLUMN_DATE:
					return true;
				case COLUMN_DESC:
					return (myEvent.getDate() != null);
				case COLUMN_TRANTYP:
					return ((myEvent.getDate() != null) &&
							(myEvent.getDesc() != null));
				default:
					if ((myEvent.getDate() == null) &&
						(myEvent.getDesc() == null) &&
						(myEvent.getTransType() == null))
						return false;
					switch (col) {
						case COLUMN_UNITS: 
							return ((myEvent.getDebit() != null) && 
									(myEvent.getCredit() != null) &&
									(myEvent.getCredit().isPriced() 
										!= myEvent.getDebit().isPriced()));
						case COLUMN_YEARS:
							return myEvent.getTransType().isTaxableGain();
						case COLUMN_TAXCRED:
							return myEvent.needsTaxCredit();
						default:	
							return true;
					}
			}
		}
			
		/* get value At */
		public Object getValueAt(int row, int col) {
			Event 	myEvent;
			Object  o;
											
			/* Access the event */
			myEvent = theEvents.get(row);
			
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
				case COLUMN_TAXCRED:		
					o = myEvent.getTaxCredit();
					break;
				case COLUMN_UNITS:		
					o = myEvent.getUnits();
					break;
				case COLUMN_YEARS:		
					o = myEvent.getYears();
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
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/* set value At */
		public void setValueAt(Object obj, int row, int col) {
			Event myEvent;
			
			/* Access the line */
			myEvent = theEvents.get(row);
			
			/* Push history */
			myEvent.pushHistory();
			
			/* Store the appropriate value */
			switch (col) {
				case COLUMN_DATE:  
					myEvent.setDate((Date)obj);    
					break;
				case COLUMN_DESC:  
					myEvent.setDescription((String)obj);            
					break;
				case COLUMN_TRANTYP:  
					myEvent.setTransType(theTransTypes.searchFor((String)obj));    
					break;
				case COLUMN_AMOUNT:
					myEvent.setAmount((Number.Money)obj); 
					break;
				case COLUMN_TAXCRED:
					myEvent.setTaxCredit((Number.Money)obj); 
					break;
				case COLUMN_YEARS:
					myEvent.setYears((Integer)obj); 
					break;
				case COLUMN_UNITS:
					myEvent.setUnits((Number.Units)obj); 
					break;
				case COLUMN_CREDIT:
					myEvent.setCredit(theAccounts.searchFor((String)obj));    
					break;
				case COLUMN_DEBIT:
					myEvent.setDebit(theAccounts.searchFor((String)obj));    
					break;
			}
				
			/* Check for changes */
			if (myEvent.checkForHistory()) {
				/* Note that the item has changed */
				myEvent.setState(DataState.CHANGED);
				theEvents.findEditState();
				
				/* Switch on the updated column */
				switch (col) {
					/* redraw whole table if we have updated a sort col */
					case COLUMN_DATE:
					case COLUMN_DESC:
					case COLUMN_TRANTYP:
						myEvent.reSort();
						fireTableDataChanged();
						row = theEvents.indexOf(myEvent);
						selectRow(row);
						break;
						
					/* else note that we have updated this cell */
					default:
						fireTableCellUpdated(row, col);
						break;
				}
				
				/* Note that changes have occurred */
				notifyChanges();
			}
		}
	}
	
	/* Extract mouse listener */
	public class extractMouse extends MouseAdapter {
			
		/* Pop-up Menu items */
		private static final String popupView    = "View Account";
		private static final String popupMaint   = "Maintain Account";
		private static final String popupSetNull = "Set Null";
			
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
			Event           myRow     = null;
			Account  		myAccount = null;
			boolean         isUnits   = false;
			boolean         isTaxCred = false;
			boolean         isYears   = false;
				
			if (e.isPopupTrigger() && 
				(theTable.isEnabled())) {
				/* Calculate the row/column that the mouse was clicked at */
				Point p = new Point(e.getX(), e.getY());
				int row = theTable.rowAtPoint(p);
				int col = theTable.columnAtPoint(p);
				
				/* Access the row */
				myRow = theEvents.get(row);
					
				/* If the column is Credit */
				if (col == COLUMN_CREDIT)
					myAccount = myRow.getCredit();
				else if (col == COLUMN_DEBIT)
					myAccount = myRow.getDebit();
				else if (col == COLUMN_UNITS)
					isUnits = true;
				else if (col == COLUMN_TAXCRED)
					isTaxCred = true;
				else if (col == COLUMN_YEARS)
					isYears = true;
				
				/* If we have updates then ignore the account */
				if (theTable.hasUpdates()) myAccount = null;
				
				/* If we are pointing to units then determine whether we can set null */
				if ((isUnits) && 
					((myRow.isLocked()) ||
					 (myRow.getUnits() == null)))
					isUnits = false;
				
				/* If we are pointing to TaxCredit then determine whether we can set null */
				if ((isTaxCred) && 
					((myRow.isLocked()) ||
					 (myRow.getTaxCredit() == null)))
					isTaxCred = false;
				
				/* If we are pointing to years then determine whether we can set null */
				if ((isYears) && 
					((myRow.isLocked()) ||
					 (myRow.getYears() == null)))
					isYears = false;
				
				/* If we have an account or can set null units/years/tax */
				if ((myAccount != null) || (isUnits) || (isTaxCred) || (isYears)) {
					/* Create the pop-up menu */
					myMenu = new JPopupMenu();
					
					/* If we have an account name */
					if (myAccount != null) {
						/* Create the View account choice */
						myItem = new JMenuItem(popupView + ": " + myAccount.getName());
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupView + ":" + myAccount.getName());
						myItem.addActionListener(theTable);
						myMenu.add(myItem);
						
						/* Create the Maintain account choice */
						myItem = new JMenuItem(popupMaint + ": " + myAccount.getName());
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupMaint + ":" + myAccount.getName());
						myItem.addActionListener(theTable);
						myMenu.add(myItem);
					}
					
					/* If we have units */
					if (isUnits) {
						/* Create the set null choice */
						myItem = new JMenuItem(popupSetNull);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNull + "Unit:" + row);
						myItem.addActionListener(theTable);
						myMenu.add(myItem);
					}
					
					/* If we have years */
					if (isYears) {
						/* Create the set null choice */
						myItem = new JMenuItem(popupSetNull);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNull + "Year:" + row);
						myItem.addActionListener(theTable);
						myMenu.add(myItem);
					}
					
					/* If we have Tax Credit */
					if (isTaxCred) {
						/* Create the set null choice */
						myItem = new JMenuItem(popupSetNull);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNull + "Credit:" + row);
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
