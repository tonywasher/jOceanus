package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.DataItem;
import uk.co.tolcroft.models.Date;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.*;

public class Extract extends FinanceTable<Event> {
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
	private Extract				 	theTable	 		= this;
	private extractMouse			theMouse	 		= null;
	private extractColumnModel		theColumns			= null;
	private Date.Range				theRange	 		= null;
	private DateRange 				theSelect	 		= null;
	private SaveButtons  			theTabButs   		= null;
	private DebugEntry				theDebugExtract		= null;
	private ErrorPanel				theError			= null;
	private ComboSelect				theComboList    	= null;

	/* Access methods */
	public JPanel  	getPanel()			{ return thePanel; }
	public boolean 	hasHeader()			{ return false; }
	
	/* Access the debug entry */
	public DebugEntry getDebugEntry()	{ return theDebugExtract; }
	
	/* Table headers */
	private static final String titleDate    = "Date";
	private static final String titleDesc    = "Description";
	private static final String titleTrans   = "TransactionType";
	private static final String titleAmount  = "Amount";
	private static final String titleDebit   = "Debit";
	private static final String titleCredit  = "Credit";
	private static final String titleUnits   = "Units";
	private static final String titleDilute  = "Dilution";
	private static final String titleTaxCred = "TaxCredit";
	private static final String titleYears   = "Yrs";
		
	/* Table columns */
	private static final int COLUMN_DATE 	 = 0;
	private static final int COLUMN_TRANTYP  = 1;
	private static final int COLUMN_DESC 	 = 2;
	private static final int COLUMN_AMOUNT	 = 3;
	private static final int COLUMN_DEBIT	 = 4;
	private static final int COLUMN_CREDIT	 = 5;
	private static final int COLUMN_UNITS	 = 6;
	private static final int COLUMN_DILUTE	 = 7;
	private static final int COLUMN_TAXCRED	 = 8;
	private static final int COLUMN_YEARS	 = 9;
		
	/**
	 * Constructor for Extract Window
	 * @param pParent the parent window
	 */
	public Extract(MainTab pParent) {
		/* Initialise superclass */
		super(pParent);
		
		/* Declare variables */
		GroupLayout			myLayout;
		DebugEntry			mySection;
			
		/* Record the passed details */
		theParent = pParent;
		theView   = pParent.getView();

		/* Create the top level debug entry for this view  */
		DebugManager myDebugMgr = theView.getDebugMgr();
		mySection = myDebugMgr.getViews();
        theDebugExtract = myDebugMgr.new DebugEntry("Extract");
        theDebugExtract.addAsChildOf(mySection);
		
		/* Create the model and declare it to our superclass */
		theModel  = new ExtractModel();
		setModel(theModel);
			
		/* Create the data column model and declare it */
		theColumns = new extractColumnModel();
		setColumnModel(theColumns);
		
		/* Prevent reordering of columns and auto-resizing */			
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(900, 200));
		
		/* Add the mouse listener */
		theMouse = new extractMouse();
		addMouseListener(theMouse);
		
		/* Create the sub panels */
		theSelect    = new DateRange(this);
		theTabButs   = new SaveButtons(this);
			
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
	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, true)
		                .addComponent(theError.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                	.addComponent(theSelect.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(getScrollPane(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	        		.addComponent(theError.getPanel())
	        		.addComponent(theSelect.getPanel())
	                .addComponent(getScrollPane())
	                .addComponent(theTabButs.getPanel()))
	    );
	}
		
	/**
	 * Save changes from the view into the underlying data
	 */
	public void saveData() {
		if (theExtract != null) {
			super.validateAll();
			if (!hasErrors()) theExtract.applyChanges();
		}
	}
		
 	/**
	 * Update Debug view 
	 */
	public void updateDebug() {			
		theDebugExtract.setObject(theExtract);
	}
		
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);

		/* Lock scroll-able area */
		getScrollPane().setEnabled(!isError);

		/* Lock row/tab buttons area */
		theTabButs.getPanel().setEnabled(!isError);
	}
	
	/**
	 *  Notify table that there has been a change in selection by an underlying control
	 *  @param obj the underlying control that has changed selection
	 */
	public void    notifySelection(Object obj)    {
		/* if this is a change from the range */
		if (obj == (Object) theSelect) {
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
	}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws Exception {
		DataSet 		myData;
		
		/* Access data */
		myData = theView.getData();
		
		/* Access lists */
		theTransTypes = myData.getTransTypes();
		theAccounts	  = myData.getAccounts();
		
		/* Access the combo list from parent */
		theComboList 	= theParent.getComboList();
		
		/* Access range */
		Date.Range myRange = theView.getRange();
		theSelect.setOverallRange(myRange);
		theRange = theSelect.getRange();
		setSelection(theRange);
		
		/* Create SavePoint */
		theSelect.createSavePoint();
	}
	
	/**
	 * Call underlying controls to take notice of changes in view/selection
	 */
	public void notifyChanges() {
		/* Find the edit state */
		if (theEvents != null)
			theEvents.findEditState();
		
		/* Update the table buttons */
		theTabButs.setLockDown();
		theSelect.setLockDown();
		
		/* Update the top level tabs */
		theParent.setVisibleTabs();
	}
		
	/**
	 * Set Selection to the specified date range
	 * @param pRange the Date range for the extract
	 */
	public void setSelection(Date.Range pRange) throws Exception {
		theRange   = pRange;
		if (theRange != null) {
			theColumns.setDateEditorRange(theRange);
			theExtract = theView.new ViewEvents(pRange);
			theEvents  = theExtract.getEvents();
		}
		else {
			theExtract = null;
			theEvents  = null;				
		}
		setList(theEvents);
		theTabButs.setLockDown();
		theSelect.setLockDown();
		theParent.setVisibleTabs();
	}
		
	/**
	 * Set selection to the period designated by the referenced control
	 * @param pSource the source control
	 */
	public void selectPeriod(DateRange pSource) {
		/* Protect against exceptions */
		try {
			/* Adjust the period selection (this will not call back) */
			theSelect.setSelection(pSource);
		
			/* Utilise the selection */
			setSelection(theSelect.getRange());
		}
		
		/* Catch exceptions */
		catch (Exception e) {
			/* Build the error */
			Exception myError = new Exception(ExceptionClass.DATA,
									          "Failed to select Period",
									          e);
			
			/* Show the error */
			theError.setError(myError);
			
			/* Restore the original selection */
			theSelect.restoreSavePoint();
		}
	}
	
	/**
	 * Obtain the correct ComboBox for the given row/column
	 */
	public JComboBox getComboBox(int row, int column) {
		Event myEvent;
		
		/* Access the event */
		myEvent = theEvents.get(row);

		/* Switch on column */
		switch (column) {
			case COLUMN_TRANTYP:	
				return theComboList.getAllTransTypes();
			case COLUMN_CREDIT:		
				return theComboList.getCreditAccounts(myEvent.getTransType(), myEvent.getDebit());
			case COLUMN_DEBIT:
				return theComboList.getDebitAccounts(myEvent.getTransType());
			default: 				
				return null;
		}
	}
		
	/**
	 * Check whether the restoration of the passed object is compatible with the current selection
	 * @param pItem the current item
	 * @param pObj the potential object for restoration
	 */
	public boolean isValidObj(DataItem<?>			pItem,
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
				
	/* Extract table model */
	public class ExtractModel extends DataTableModel {
		private static final long serialVersionUID = 7997087757206121152L;
		
		/**
		 * Get the number of display columns
		 * @return the columns
		 */
		public int getColumnCount() { return (theColumns == null) ? 0 : theColumns.getColumnCount(); }
		
		/**
		 * Get the number of rows in the current table
		 * @return the number of rows
		 */
		public int getRowCount() { 
			return (theEvents == null) ? 0
					                   : theEvents.size();
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
				case COLUMN_AMOUNT:  	return titleAmount;
				case COLUMN_CREDIT:  	return titleCredit;
				case COLUMN_DEBIT:	 	return titleDebit;
				case COLUMN_UNITS: 		return titleUnits;
				case COLUMN_DILUTE: 	return titleDilute;
				case COLUMN_TAXCRED: 	return titleTaxCred;
				case COLUMN_YEARS: 		return titleYears;
				default: 				return null;
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
				case COLUMN_CREDIT:  	return String.class;
				case COLUMN_DEBIT:  	return String.class;
				default: 				return Object.class;
			}
		}
			
		/**
		 * Obtain the Field id associated with the column
		 * @param row the row
		 * @param column the column
		 */
		public int getFieldForCell(int row, int column) {
			/* Switch on column */
			switch (column) {
				case COLUMN_DATE: 		return Event.FIELD_DATE;
				case COLUMN_DESC:		return Event.FIELD_DESC;
				case COLUMN_TRANTYP:	return Event.FIELD_TRNTYP;
				case COLUMN_AMOUNT:		return Event.FIELD_AMOUNT;
				case COLUMN_CREDIT:		return Event.FIELD_CREDIT;
				case COLUMN_DEBIT:		return Event.FIELD_DEBIT;
				case COLUMN_UNITS: 		return Event.FIELD_UNITS;
				case COLUMN_DILUTE: 	return Event.FIELD_DILUTION;
				case COLUMN_TAXCRED: 	return Event.FIELD_TAXCREDIT;
				case COLUMN_YEARS: 		return Event.FIELD_YEARS;
				default: 				return -1;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
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
				case COLUMN_TRANTYP:
					return (myEvent.getDate() != null);
				case COLUMN_DESC:
					return ((myEvent.getDate() != null) &&
							(myEvent.getTransType() != null));
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
							return ((myEvent.getTransType() != null) &&
									(myEvent.getTransType().isTaxableGain()));
						case COLUMN_TAXCRED:
							return myEvent.needsTaxCredit();
						case COLUMN_DILUTE:
							return myEvent.needsDilution();
						default:	
							return true;
					}
			}
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
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
				case COLUMN_DILUTE:		
					o = myEvent.getDilution();
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
					if ((o != null) && (((String)o).length() == 0))
						o = null;
					break;
				default:	
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myEvent.hasErrors(getFieldForCell(row, col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/**
		 * Set the value at (row, col)
		 * @param obj the object value to set
		 */
		public void setValueAt(Object obj, int row, int col) {
			Event myEvent;
			
			/* Access the line */
			myEvent = theEvents.get(row);
			
			/* Push history */
			myEvent.pushHistory();

			/* Protect against Exceptions */
			try {
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
						myEvent.setAmount((Money)obj); 
						break;
					case COLUMN_DILUTE:
						myEvent.setDilution((Dilution)obj); 
						break;
					case COLUMN_TAXCRED:
						myEvent.setTaxCredit((Money)obj); 
						break;
					case COLUMN_YEARS:
						myEvent.setYears((Integer)obj); 
						break;
					case COLUMN_UNITS:
						myEvent.setUnits((Units)obj); 
						break;
					case COLUMN_CREDIT:
						myEvent.setCredit(theAccounts.searchFor((String)obj));    
						break;
					case COLUMN_DEBIT:
						myEvent.setDebit(theAccounts.searchFor((String)obj));    
						break;
				}
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				myEvent.popHistory();
				myEvent.pushHistory();
				
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field at ("
										          + row + "," + col +")",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}
			
			/* Check for changes */
			if (myEvent.checkForHistory()) {
				/* Note that the item has changed */
				myEvent.setState(DataState.CHANGED);

				/* Validate the item and update the edit state */
				myEvent.clearErrors();
				myEvent.validate();
				theEvents.findEditState();
			
				/* Switch on the updated column */
				switch (col) {
					/* If we have changed a sorting column */
					case COLUMN_DATE:
					case COLUMN_DESC:
					case COLUMN_TRANTYP:
						/* Re-Sort the row */
						theEvents.reSort(myEvent);
						
						/* Determine new row # */
						int myNewRowNo = myEvent.indexOf();
						
						/* If the row # has changed */
						if (myNewRowNo != row) {
							/* Report the move of the row */
							fireMoveRowEvents(row, myNewRowNo);					
							selectRowWithScroll(myNewRowNo);
							break;
						}
						
						/* else fall through */
						
					/* else note that we have updated this cell */
					default:
						fireTableRowsUpdated(row, row);
						break;
				}
				
				/* Note that changes have occurred */
				notifyChanges();
				updateDebug();
			}
		}
	}
	
	/**
	 *  Extract mouse listener
	 */
	private class extractMouse extends FinanceMouse<Event> {
			
		/* Pop-up Menu items */
		private static final String popupView    		= "View Account";
		private static final String popupMaint  		= "Maintain Account";
		private static final String popupSetNullUnits 	= "Set Null Units";
		private static final String popupSetNullTax 	= "Set Null TaxCredit";
		private static final String popupSetNullYears 	= "Set Null Years";
		private static final String popupSetNullDilute 	= "Set Null Dilution";
		private static final String popupCalcTax 		= "Calculate Tax Credit";
			
		/**
		 * Constructor
		 */
		private extractMouse() {
			/* Call super-constructor */
			super(theTable);
		}
		
		/**
		 * Add Null commands to menu
		 * @param pMenu the menu to add to
		 */
		protected void addNullCommands(JPopupMenu pMenu) {
			JMenuItem 	myItem;
			Event		myEvent;
			boolean		enableNullUnits 	= false;
			boolean		enableNullTax 		= false;
			boolean		enableNullYears 	= false;
			boolean		enableNullDilution 	= false;
			
			/* Nothing to do if the table is locked */
			if (theTable.isLocked()) return;
			
			/* Loop through the selected rows */
			for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
				/* Ignore locked rows */
				if ((myRow == null) || (myRow.isLocked())) continue;
				
				/* Ignore deleted rows */
				if (myRow.isDeleted()) continue;
				
				/* Access as event */
				myEvent = (Event)myRow;
				
				/* Enable null Units if we have units */
				if (myEvent.getUnits()     != null) 	enableNullUnits	= true;
				
				/* Enable null Tax if we have tax */
				if (myEvent.getTaxCredit() != null) 	enableNullTax = true;
				
				/* Enable null Years if we have years */
				if (myEvent.getYears()     != null) 	enableNullYears = true;
				
				/* Enable null Dilution if we have dilution */
				if (myEvent.getDilution()  != null) 	enableNullDilution = true;
			}	

			/* If there is something to add and there are already items in the menu */
			if ((enableNullUnits || enableNullTax || enableNullYears || enableNullDilution) &&
			    (pMenu.getComponentCount() > 0)) {
				/* Add a separator */
				pMenu.addSeparator();
			}
			
			/* If we can set null units */
			if (enableNullUnits) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupSetNullUnits);
				myItem.setActionCommand(popupSetNullUnits);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}

			/* If we can set null tax */
			if (enableNullTax) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupSetNullTax);
				myItem.setActionCommand(popupSetNullTax);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}

			/* If we can set null years */
			if (enableNullYears) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupSetNullYears);
				myItem.setActionCommand(popupSetNullYears);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}

			/* If we can set null dilution */
			if (enableNullDilution) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupSetNullDilute);
				myItem.setActionCommand(popupSetNullDilute);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}
		}

		/**
		 * Add Special commands to menu
		 * @param pMenu the menu to add to
		 */
		protected void addSpecialCommands(JPopupMenu pMenu) {
			JMenuItem 		myItem;
			Event			myEvent;
			Money			myTax;
			TransactionType	myTrans;
			boolean			enableCalcTax	= false;
			
			/* Nothing to do if the table is locked */
			if (theTable.isLocked()) return;
			
			/* Loop through the selected rows */
			for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
				/* Ignore locked rows */
				if ((myRow == null) || (myRow.isLocked())) continue;
				
				/* Ignore deleted rows */
				if (myRow.isDeleted()) continue;
				
				/* Access as event */
				myEvent = (Event)myRow;
				myTax	= myEvent.getTaxCredit();
				myTrans = myEvent.getTransType();

				/* If we have a calculable tax credit */
				if ((myTrans != null) &&
					((myTrans.isInterest()) ||
					 (myTrans.isDividend()))) {
					/* Enable calculation of tax if tax is null or zero */
					if  ((myTax == null) || (!myTax.isNonZero()))
						enableCalcTax = true;
				}				
			}
			
			/* If there is something to add and there are already items in the menu */
			if ((enableCalcTax) &&
			    (pMenu.getComponentCount() > 0)) {
				/* Add a separator */
				pMenu.addSeparator();
			}
			
			/* If we can calculate tax */
			if (enableCalcTax) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupCalcTax);
				myItem.setActionCommand(popupCalcTax);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}
		}

		/**
		 * Add Navigation commands to menu
		 * @param pMenu the menu to add to
		 */
		protected void addNavigationCommands(JPopupMenu pMenu) {
			JMenuItem 		myItem;
			Event			myEvent;
			Account			myAccount;
			int				myRow;
			int				myCol;
			boolean			enableNavigate	= false;
			
			/* Nothing to do if the table is locked */
			if (theTable.isLocked()) return;
			
			/* Access the popUp row/column and ignore if not valid */
			myRow = getPopupRow();
			myCol = getPopupCol();
			if (myRow < 0) return;
			
			/* Access the event */
			myEvent = theTable.extractItemAt(myRow);
			
			/* If the column is Credit */
			if (myCol == COLUMN_CREDIT)
				myAccount = myEvent.getCredit();
			else if (myCol == COLUMN_DEBIT)
				myAccount = myEvent.getDebit();
			else
				myAccount = null;
			
			/* If we have an account we can navigate */ 
			if (myAccount != null) enableNavigate = true;
			
			/* If there is something to add and there are already items in the menu */
			if ((enableNavigate) &&
			    (pMenu.getComponentCount() > 0)) {
				/* Add a separator */
				pMenu.addSeparator();
			}
			
			/* If we can navigate */
			if (enableNavigate) {
				/* Create the View account choice */
				myItem = new JMenuItem(popupView + ": " + myAccount.getName());
			
				/* Set the command and add to menu */
				myItem.setActionCommand(popupView + ":" + myAccount.getName());
				myItem.addActionListener(this);
				pMenu.add(myItem);
				
				/* Create the Maintain account choice */
				myItem = new JMenuItem(popupMaint + ": " + myAccount.getName());
			
				/* Set the command and add to menu */
				myItem.setActionCommand(popupMaint + ":" + myAccount.getName());
				myItem.addActionListener(this);
				pMenu.add(myItem);
			}			
		}

		/**
		 * Perform actions for controls/pop-ups on this table
		 * @param evt the event
		 */
		public void actionPerformed(ActionEvent evt) {
			String myCmd = evt.getActionCommand();
			
			/* Cancel any editing */
			theTable.cancelEditing();
			
			/* If this is a set null units command */
			if (myCmd.equals(popupSetNullUnits)) {
				/* Set Units column to null */
				setColumnToNull(COLUMN_UNITS);
			}
			
			/* else if this is a set null tax command */
			else if (myCmd.equals(popupSetNullTax)) {
				/* Set Tax column to null */
				setColumnToNull(COLUMN_TAXCRED);				
			}
			
			/* If this is a set null years command */
			else if (myCmd.equals(popupSetNullYears)) {
				/* Set Years column to null */
				setColumnToNull(COLUMN_YEARS);								
			}
			
			/* If this is a set null dilute command */
			else if (myCmd.equals(popupSetNullDilute)) {
				/* Set Dilution column to null */
				setColumnToNull(COLUMN_DILUTE);				
			}
			
			/* If this is a calculate Tax Credits command */
			else if (myCmd.equals(popupCalcTax)) {
				/* Calculate the tax credits */
				calculateTaxCredits();				
			}
			
			/* If this is a navigate command */
			else if ((myCmd.startsWith(popupView)) ||
     			     (myCmd.startsWith(popupMaint))) {
				/* perform the navigation */
				performNavigation(myCmd);				
			}
			
			/* else we do not recognise the action */
			else {
				/* Pass it to the superclass */
				super.actionPerformed(evt);
				return;
			}
			
			/* Notify of any changes */
			notifyChanges();
			updateDebug();
		}
		
		/**
		 * Calculate tax credits
		 */
		private void calculateTaxCredits() {
			Event				myEvent;
			TransactionType		myTrans;
			Money				myTax;
			int					row;

			/* Loop through the selected rows */
			for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
				/* Ignore locked rows */
				if ((myRow == null) || (myRow.isLocked())) continue;
				
				/* Ignore deleted rows */
				if (myRow.isDeleted()) continue;

				/* Determine row */
				row = myRow.indexOf();
				if (theTable.hasHeader()) row--;
				
				/* Access the event */
				myEvent = (Event)myRow;
				myTrans	= myEvent.getTransType();
				myTax	= myEvent.getTaxCredit();
				
				/* Ignore rows with invalid transaction type */
				if ((myTrans == null) ||
					((!myTrans.isInterest()) &&
					 (!myTrans.isDividend()))) continue;
				
				/* Ignore rows with tax credit already set */
				if  ((myTax != null) && (myTax.isNonZero())) continue;

				/* Calculate the tax credit */
				myTax = myEvent.calculateTaxCredit();
				
				/* set the tax credit value */
				theModel.setValueAt(myTax, row, COLUMN_TAXCRED);
				theModel.fireTableCellUpdated(row, COLUMN_TAXCRED);
			}
		}
		
		/**
		 * Perform a navigation command
		 * @param pCmd the navigation command
		 */
		private void performNavigation(String pCmd) {
			String      tokens[];
			String      myName = null;
			Account 	myAccount;

			/* Access the action command */
			tokens = pCmd.split(":");
			pCmd   = tokens[0];
			if (tokens.length > 1) myName = tokens[1];
			
			/* Access the correct account */
			myAccount = theView.getData().getAccounts().searchFor(myName);
		
			/* If this is an account view request */
			if (pCmd.compareTo(popupView) == 0) {
				/* Switch view */
				theParent.selectAccount(myAccount, theSelect);
			}
			
			/* If this is an account maintenance request */
			else if (pCmd.compareTo(popupMaint) == 0) {
				/* Switch view */
				theParent.selectAccountMaint(myAccount);
			}
		}			
	}
	
	/**
	 * Column Model class
	 */
	private class extractColumnModel extends DataColumnModel {
		private static final long serialVersionUID = -7502445487118370020L;

		/* Renderers/Editors */
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
		private Renderer.DilutionCell 	theDiluteRenderer 	= null;
		private Editor.DilutionCell 	theDiluteEditor   	= null;
		private Editor.ComboBoxCell 	theComboEditor    	= null;

		/**
		 * Constructor 
		 */
		private extractColumnModel() {		
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
			theDiluteRenderer 	= new Renderer.DilutionCell();
			theDiluteEditor   	= new Editor.DilutionCell();
			theComboEditor    	= new Editor.ComboBoxCell();
			
			/* Create the columns */
			addColumn(new DataColumn(COLUMN_DATE,     90, theDateRenderer,    theDateEditor));
			addColumn(new DataColumn(COLUMN_TRANTYP, 110, theStringRenderer,  theComboEditor));
			addColumn(new DataColumn(COLUMN_DESC,    140, theStringRenderer,  theStringEditor));
			addColumn(new DataColumn(COLUMN_AMOUNT,   90, theMoneyRenderer,   theMoneyEditor));
			addColumn(new DataColumn(COLUMN_DEBIT,   130, theStringRenderer,  theComboEditor));
			addColumn(new DataColumn(COLUMN_CREDIT,  130, theStringRenderer,  theComboEditor));
			addColumn(new DataColumn(COLUMN_UNITS,    80, theUnitsRenderer,   theUnitsEditor));
			addColumn(new DataColumn(COLUMN_DILUTE,   70, theDiluteRenderer,  theDiluteEditor));
			addColumn(new DataColumn(COLUMN_TAXCRED,  90, theMoneyRenderer,   theMoneyEditor));
			addColumn(new DataColumn(COLUMN_YEARS,    30, theIntegerRenderer, theIntegerEditor));
		}
		
		/**
		 * Set the date editor range 
		 * @param pRange
		 */
		private void setDateEditorRange(Date.Range pRange) {
			/* Set the range */
			theDateEditor.setRange(pRange);			
		}
	}
}
