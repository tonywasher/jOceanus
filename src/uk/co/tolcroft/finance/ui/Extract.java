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
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.*;
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
	private JScrollPane				theScroll			= null;
	private JComboBox				theTranBox			= null;
	private Extract				 	theTable	 		= this;
	private extractMouse			theMouse	 		= null;
	private Date.Range				theRange	 		= null;
	private DateRange 				theSelect	 		= null;
	private EditButtons    			theRowButs   		= null;
	private SaveButtons  			theTabButs   		= null;
	private DebugEntry				theDebugExtract		= null;
	private ErrorPanel				theError			= null;
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
	private ComboSelect				theComboList    	= null;
	private boolean					tranPopulated    	= false;

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
	private static final int NUM_COLUMNS	 = 10;
		
	/**
	 * Constructor for Extract Window
	 * @param pParent the parent window
	 */
	public Extract(MainTab pParent) {
		/* Initialise superclass */
		super(pParent);
		
		/* Declare variables */
		TableColumnModel    myColModel;
		TableColumn			myCol;
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
		theDiluteRenderer 	= new Renderer.DilutionCell();
		theDiluteEditor   	= new Editor.DilutionCell();
		theComboEditor    	= new Editor.ComboBoxCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setCellEditor(theDateEditor);
		myCol.setPreferredWidth(90);
			
		myCol = myColModel.getColumn(COLUMN_TRANTYP);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(110);
		
		myCol = myColModel.getColumn(COLUMN_DESC);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theStringEditor);
		myCol.setPreferredWidth(140);
		
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
			
		myCol = myColModel.getColumn(COLUMN_DILUTE);
		myCol.setCellRenderer(theDiluteRenderer);
		myCol.setCellEditor(theDiluteEditor);
		myCol.setPreferredWidth(70);
			
		myCol = myColModel.getColumn(COLUMN_TAXCRED);
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setCellEditor(theMoneyEditor);
		myCol.setPreferredWidth(90);
			
		myCol = myColModel.getColumn(COLUMN_YEARS);
		myCol.setCellRenderer(theIntegerRenderer);
		myCol.setCellEditor(theIntegerEditor);
		myCol.setPreferredWidth(30);
			
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
		theScroll     = new JScrollPane();
		theScroll.setViewportView(this);
			
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
	                    .addComponent(theScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	        		.addComponent(theError.getPanel())
	        		.addComponent(theSelect.getPanel())
	                .addComponent(theScroll)
	                .addComponent(theRowButs.getPanel())
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
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);

		/* Lock scroll-able area */
		theScroll.setEnabled(!isError);

		/* Lock row/tab buttons area */
		theRowButs.getPanel().setEnabled(!isError);
		theTabButs.getPanel().setEnabled(!isError);
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
	}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws Exception {
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
		
	/**
	 * Set Selection to the specified date range
	 * @param pRange the Date range for the extract
	 */
	public void setSelection(Date.Range pRange) throws Exception {
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
		theDebugExtract.setObject(theExtract);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
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
	 * Obtain the Field id associated with the column
	 * @param column the column
	 */
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
			case COLUMN_DILUTE: 	return Event.FIELD_DILUTION;
			case COLUMN_TAXCRED: 	return Event.FIELD_TAXCREDIT;
			case COLUMN_YEARS: 		return Event.FIELD_YEARS;
			default: 				return -1;
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
		
	/**
	 * Check whether the restoration of the passed object is compatible with the current selection
	 * @param pItem the current item
	 * @param pObj the potential object for restoration
	 */
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
		
	/**
	 * Perform actions for controls/pop-ups on this table
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {
		String      myCmd;
		String      tokens[];
		String      myName = null;
		Account 	myAccount;
		Event		myEvent;
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
		
			/* Switch view */
			theParent.selectAccount(myAccount, theSelect);
		}
		
		/* If this is an account maintenance request */
		else if (myCmd.compareTo(extractMouse.popupMaint) == 0) {
			/* Access the correct account */
			myAccount = theView.getData().getAccounts().searchFor(myName);
		
			/* Switch view */
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
		
		/* If this is a set Null Dilute request */
		else if (myCmd.compareTo(extractMouse.popupSetNull + "Dilute") == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_DILUTE);
			theModel.fireTableCellUpdated(row, COLUMN_DILUTE);
		}
		
		/* If this is a set Null TaxCredit request */
		else if (myCmd.compareTo(extractMouse.popupSetNull + "Credit") == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_TAXCRED);
			theModel.fireTableCellUpdated(row, COLUMN_TAXCRED);
		}
		
		/* If this is a set Null Years request */
		else if (myCmd.compareTo(extractMouse.popupSetNull + "Year") == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
			
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_YEARS);
			theModel.fireTableCellUpdated(row, COLUMN_YEARS);
		}
		
		/* If this is a calculate Tax Credit request */
		else if (myCmd.compareTo(extractMouse.popupCalcTax) == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
			
			/* Access the Event */
			myEvent = theEvents.get(row);

			/* Calculate the tax credit */
			Money myCredit = myEvent.calculateTaxCredit();
			
			/* set the null value */
			theModel.setValueAt(myCredit, row, COLUMN_TAXCRED);
			theModel.fireTableCellUpdated(row, COLUMN_TAXCRED);
		}
	}
		
	/* Extract table model */
	public class ExtractModel extends AbstractTableModel {
		private static final long serialVersionUID = 7997087757206121152L;
		
		/**
		 * Get the number of display columns
		 * @return the columns
		 */
		public int getColumnCount() { return NUM_COLUMNS; }
		
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
			if ((o == null) && (myEvent.hasErrors(getFieldForCol(col))))
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
		private static final String popupCalcTax = "Calculate Tax Credit";
			
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
			boolean         isDilute  = false;
			boolean         isTaxCred = false;
			boolean         isTaxCalc = false;
			boolean         isYears   = false;
				
			if (e.isPopupTrigger() && 
				(theTable.isEnabled())) {
				/* Calculate the row/column that the mouse was clicked at */
				Point p = new Point(e.getX(), e.getY());
				int row = theTable.rowAtPoint(p);
				int col = theTable.columnAtPoint(p);
				
				/* Adjust column for view differences */
				col = theTable.convertColumnIndexToModel(col);
				
				/* Access the row */
				myRow = theEvents.get(row);
					
				/* If the column is Credit */
				if (col == COLUMN_CREDIT)
					myAccount = myRow.getCredit();
				else if (col == COLUMN_DEBIT)
					myAccount = myRow.getDebit();
				else if (col == COLUMN_UNITS)
					isUnits = true;
				else if (col == COLUMN_DILUTE)
					isDilute = true;
				else if (col == COLUMN_TAXCRED)
					{ isTaxCred = true; isTaxCalc = true; }
				else if (col == COLUMN_YEARS)
					isYears = true;
				
				/* If we have updates then ignore the account */
				if (theTable.hasUpdates()) myAccount = null;
				
				/* If we are pointing to units then determine whether we can set null */
				if ((isUnits) && 
					((myRow.isLocked()) ||
					 (myRow.getUnits() == null)))
					isUnits = false;
				
				/* If we are pointing to Dilute then determine whether we can set null */
				if ((isDilute) && 
					((myRow.isLocked()) ||
					 (myRow.getDilution() == null)))
					isDilute = false;
				
				/* If we are pointing to TaxCredit then determine whether we can set null */
				if ((isTaxCred) && 
					((myRow.isLocked()) ||
					 (myRow.getTaxCredit() == null)))
					isTaxCred = false;
				
				/* If we are pointing to TaxCredit then determine whether we can calculate tax */
				if ((isTaxCalc) && 
					((myRow.isLocked()) ||
					 ((myRow.getTaxCredit() != null) &&
					  (myRow.getTaxCredit().isNonZero())) ||
					 ((myRow.getTransType() == null) ||
					  ((!myRow.getTransType().isInterest()) &&
					   (!myRow.getTransType().isDividend())))))
					isTaxCalc = false;
				
				/* If we are pointing to years then determine whether we can set null */
				if ((isYears) && 
					((myRow.isLocked()) ||
					 (myRow.getYears() == null)))
					isYears = false;
				
				/* If we have an account or can set null units/dilution/years/tax */
				if ((myAccount != null) || 
					(isUnits) || (isDilute) || 
					(isTaxCred) || (isTaxCalc) || (isYears)) {
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
					
					/* If we have dilute */
					if (isDilute) {
						/* Create the set null choice */
						myItem = new JMenuItem(popupSetNull);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNull + "Dilute:" + row);
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
					
					/* If we have Tax Calculation */
					if (isTaxCalc) {
						/* Create the set tax choice */
						myItem = new JMenuItem(popupCalcTax);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupCalcTax + ":" + row);
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
					
					/* Show the pop-up menu */
					myMenu.show(e.getComponent(),
								e.getX(), e.getY());
				}					
			}
		}
	}
}
