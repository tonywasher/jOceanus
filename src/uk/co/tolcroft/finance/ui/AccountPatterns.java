package uk.co.tolcroft.finance.ui;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class AccountPatterns extends FinanceTable<Pattern> {
	/* Members */
	private static final long serialVersionUID = 1968946370981616222L;

	private View					theView				= null;
	private PatternsModel			theModel			= null;
	private Pattern.List  			thePatterns 		= null;
	private Account.List			theAccounts			= null;
	private Frequency.List			theFreqs			= null;
	private TransactionType.List	theTransTypes		= null;
	private JPanel					thePanel			= null;
	private JComboBox				theFreqBox			= null;
	private AccountPatterns		 	theTable	 		= this;
	private patternMouse			theMouse	 		= null;
	private DataColumnModel			theColumns			= null;
	private AccountTab				theParent   		= null;
	private Account                 theAccount  		= null;
	private View.ViewPatterns		theExtract			= null;
	private ComboSelect				theComboList    	= null;
	private DebugEntry				theDebugEntry		= null;
	private ErrorPanel				theError			= null;
	private boolean					freqsPopulated    	= false;

	/* Access methods */
	public JPanel  getPanel()			{ return thePanel; }
	public boolean hasCreditChoice() 	{ return true; }
	public boolean hasHeader()			{ return false; }
	
	/* Access the debug entry */
	public DebugEntry getDebugEntry()	{ return theDebugEntry; }
	
	/* Table headers */
	private static final String titleDate    = "Date";
	private static final String titleDesc    = "Description";
	private static final String titleTrans   = "TransactionType";
	private static final String titlePartner = "Partner";
	private static final String titleCredit  = "Credit";
	private static final String titleDebit   = "Debit";
	private static final String titleFreq    = "Frequency";
		
	/* Table columns */
	private static final int COLUMN_DATE 	 = 0;
	private static final int COLUMN_TRANTYP  = 1;
	private static final int COLUMN_DESC 	 = 2;
	private static final int COLUMN_PARTNER	 = 3;
	private static final int COLUMN_CREDIT	 = 4;
	private static final int COLUMN_DEBIT 	 = 5;
	private static final int COLUMN_FREQ 	 = 6;
				
	/**
	 * Constructor for Patterns Window
	 * @param pParent the parent window
	 */
	public AccountPatterns(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		GroupLayout		 	myLayout;
			
		/* Store details about the parent */
		theParent = pParent;
		theView   = pParent.getView();

		/* Create the model and declare it to our superclass */
		theModel  = new PatternsModel();
		setModel(theModel);
		
		/* Create the data column model and declare it */
		theColumns = new DataColumnModel();
		setColumnModel(theColumns);

		/* Prevent reordering of columns */
		getTableHeader().setReorderingAllowed(false);
		
		/* Build the combo box */
		theFreqBox    = new JComboBox();

        /* Create the debug entry, attach to AccountDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("Patterns");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
        theDebugEntry.hideEntry();
			
		/* Add the mouse listener */
		theMouse = new patternMouse();
		addMouseListener(theMouse);
		
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
	                    .addComponent(getScrollPane(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theError.getPanel())
	                .addComponent(getScrollPane()))
	    );
	}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() {
		DataSet		myData;
		Frequency   myFreq;
		
		DataList<Frequency>.ListIterator myIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access Frequencies, TransTypes and Accounts */
		theFreqs    	= myData.getFrequencys();
		theTransTypes 	= myData.getTransTypes();
		theAccounts 	= myData.getAccounts();

		/* Access the combo list from parent */
		theComboList 	= theParent.getComboList();
		
		/* If we have frequencies already populated */
		if (freqsPopulated) {	
			/* Remove the frequencies */
			theFreqBox.removeAllItems();
			freqsPopulated = false;
		}
	
		/* Access the frequency iterator */
		myIterator = theFreqs.listIterator();
		
		/* Add the Frequency values to the frequencies box */
		while ((myFreq  = myIterator.next()) != null) {
			/* Add the item to the list */
			theFreqBox.addItem(myFreq.getName());
			freqsPopulated = true;
		}
	}
	
	/**
	 * Save changes from the view into the underlying data
	 */
	public void saveData() {
		if (theExtract != null) {
			theExtract.applyChanges();
		}
	}
	
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Lock scroll-able area */
		getScrollPane().setEnabled(!isError);
	}
	
	/**
	 * Call underlying controls to take notice of changes in view/selection
	 */
	public void notifyChanges() {
		/* Find the edit state */
		if (thePatterns != null)
			thePatterns.findEditState();
		
		/* Update the parent panel */
		theParent.notifyChanges(); 
	}
	
	/**
	 * Set Selection to the specified account
	 * @param pAccount the Account for the extract
	 */
	public void setSelection(Account pAccount) throws Exception {
		theExtract  = theView.new ViewPatterns(pAccount);
		theAccount  = pAccount;
		thePatterns = theExtract.getPatterns();
		super.setList(thePatterns);
		theDebugEntry.setObject(theExtract);
	}
		
	/**
	 * Obtain the Field id associated with the column
	 * @param column the column
	 */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 		return Pattern.FIELD_DATE;
			case COLUMN_DESC:		return Pattern.FIELD_DESC;
			case COLUMN_TRANTYP:	return Pattern.FIELD_TRNTYP;
			case COLUMN_CREDIT: 	return Pattern.FIELD_AMOUNT;
			case COLUMN_DEBIT: 		return Pattern.FIELD_AMOUNT;
			case COLUMN_PARTNER:	return Pattern.FIELD_PARTNER;
			case COLUMN_FREQ:  		return Pattern.FIELD_FREQ;
			default:				return -1;
		}
	}
		
	/**
	 * Obtain the correct ComboBox for the given row/column
	 */
	public JComboBox getComboBox(int row, int column) {
		Pattern 			myPattern;
		ComboSelect.Item    mySelect;
		
		/* Access the pattern */
		myPattern = thePatterns.get(row);

		/* Switch on column */
		switch (column) {
			case COLUMN_FREQ:	
				return theFreqBox;
			case COLUMN_TRANTYP:		
				mySelect = theComboList.searchFor(myPattern.getActType());
				return (myPattern.isCredit()) ? mySelect.getCredit()
						                      : mySelect.getDebit();
			case COLUMN_PARTNER:
				mySelect = theComboList.searchFor(myPattern.getTransType());
				return (myPattern.isCredit()) ? mySelect.getDebit()
						                      : mySelect.getCredit();
			default: 				
				return null;
		}
	}
		
	/**
	 * Add a pattern based on a statement line
	 * @param pLine the statement line
	 */
	public void addPattern(Statement.Line pLine) {
		Pattern myPattern;
		int		myRow;
		
		/* Create the new Item */
		myPattern = new Pattern(thePatterns, pLine);
		myPattern.addToList();
	
		/* Note the changes */
		notifyChanges();
		
		/* Access the row # */
		myRow = myPattern.indexOf();
		
		/* Notify of the insertion of the row */
		theModel.fireTableRowsInserted(myRow, myRow);
	}
	
	/* Patterns table model */
	public class PatternsModel extends DataTableModel {
		private static final long serialVersionUID = -8445100544184045930L;

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
			return (thePatterns == null) ? 0
					                     : thePatterns.size();
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
				case COLUMN_FREQ:		return titleFreq;
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
				case COLUMN_CREDIT:  	return String.class;
				case COLUMN_DEBIT:  	return String.class;
				case COLUMN_FREQ:  		return String.class;
				default: 				return Object.class;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
		public boolean isCellEditable(int row, int col) {
			Pattern myPattern;
			
			/* If the account is not editable */
			if (theAccount.isLocked()) return false;
			
			/* Access the pattern */
			myPattern = thePatterns.get(row);
			
			/* Cannot edit if row is deleted or locked */
			if (myPattern.isDeleted() || myPattern.isLocked())
				return false;
			
			switch (col) {
				case COLUMN_CREDIT:		return myPattern.isCredit();
				case COLUMN_DEBIT:		return !myPattern.isCredit();
				default: return true;
			}
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) {
			Pattern myPattern;
			Object  o;
				
			/* Access the pattern */
			myPattern = thePatterns.get(row);
				
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_DATE:  		
					o = myPattern.getDate();
					break;
				case COLUMN_DESC:	 	
					o = myPattern.getDesc();
					if ((o != null) && (((String)o).length() == 0))
						o = null;
					break;
				case COLUMN_TRANTYP:  	
					o = (myPattern.getTransType() == null) 
								? null : myPattern.getTransType().getName();
					break;
				case COLUMN_PARTNER:
					o = (myPattern.getPartner() == null) 
								? null : myPattern.getPartner().getName();
					break;
				case COLUMN_CREDIT:  	
					o = (myPattern.isCredit()) ? myPattern.getAmount()
					   					  	   : null;
					break;
				case COLUMN_DEBIT:
					o =(!myPattern.isCredit()) ? myPattern.getAmount()
											   : null;
					break;
				case COLUMN_FREQ:		
					o = (myPattern.getFrequency() == null) 
								? null : myPattern.getFrequency().getName();
					break;
				default:				
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myPattern.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/**
		 * Set the value at (row, col)
		 * @param obj the object value to set
		 */
		public void setValueAt(Object obj, int row, int col) {
			Pattern myPattern;
			
			/* Access the pattern */
			myPattern = thePatterns.get(row);
				
			/* Push history */
			myPattern.pushHistory();
			
			/* Process errors caught here */
			try {
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  
						myPattern.setDate((Date)obj);    
						break;
					case COLUMN_DESC:  
						myPattern.setDesc((String)obj);            
						break;
					case COLUMN_TRANTYP:  
						myPattern.setTransType(theTransTypes.searchFor((String)obj));    
						break;
					case COLUMN_CREDIT:
					case COLUMN_DEBIT:
						myPattern.setAmount((Money)obj); 
						break;
					case COLUMN_PARTNER:  
						myPattern.setPartner(theAccounts.searchFor((String)obj));    
						break;
					case COLUMN_FREQ:
					default: 
						myPattern.setFrequency(theFreqs.searchFor((String)obj));    
						break;
				}
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				myPattern.popHistory();
				myPattern.pushHistory();
				
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field at ("
										          + row + "," + col +")",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}
			
			/* Check for changes */
			if (myPattern.checkForHistory()) {
				/* Note that the item has changed */
				myPattern.setState(DataState.CHANGED);
				thePatterns.findEditState();
				
				/* Switch on the updated column */
				switch (col) {
					/* if we have updated a sort col */
					case COLUMN_DATE:
					case COLUMN_DESC:
					case COLUMN_TRANTYP: 
						/* Re-Sort the row */
						myPattern.reSort();

						/* Determine new row # */
						int myNewRowNo = myPattern.indexOf();
						
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
						fireTableCellUpdated(row, col);
						break;
				}
			
				/* Update components to reflect changes */
				notifyChanges();
			}
		}
	}

	/**
	 *  Pattern mouse listener
	 */
	private class patternMouse extends FinanceMouse<Pattern> {
		/**
		 * Constructor
		 */
		private patternMouse() {
			/* Call super-constructor */
			super(theTable);
		}		
	}		

	/**
	 * Column Model class
	 */
	private class DataColumnModel extends DefaultTableColumnModel {
		private static final long serialVersionUID = 520785956133901998L;

		/* Renderers/Editors */
		private Renderer.DateCell 		theDateRenderer   	= null;
		private Editor.DateCell 		theDateEditor     	= null;
		private Renderer.MoneyCell 		theMoneyRenderer  	= null;
		private Editor.MoneyCell 		theMoneyEditor    	= null;
		private Renderer.StringCell 	theStringRenderer 	= null;
		private Editor.StringCell 		theStringEditor   	= null;
		private Editor.ComboBoxCell		theComboEditor    	= null;

		/**
		 * Constructor 
		 */
		private DataColumnModel() {		
			/* Create the relevant formatters/editors */
			theDateRenderer   = new Renderer.DateCell();
			theDateEditor     = new Editor.DateCell();
			theMoneyRenderer  = new Renderer.MoneyCell();
			theMoneyEditor    = new Editor.MoneyCell();
			theStringRenderer = new Renderer.StringCell();
			theStringEditor   = new Editor.StringCell();
			theComboEditor    = new Editor.ComboBoxCell();
			
			/* Set the date editor to show no years */
			theDateEditor.setNoYear();
			
			/* Create the columns */
			addColumn(new DataColumn(COLUMN_DATE,     80, theDateRenderer,   theDateEditor));
			addColumn(new DataColumn(COLUMN_TRANTYP, 110, theStringRenderer, theComboEditor));
			addColumn(new DataColumn(COLUMN_DESC,    150, theStringRenderer, theStringEditor));
			addColumn(new DataColumn(COLUMN_PARTNER, 130, theStringRenderer, theComboEditor));
			addColumn(new DataColumn(COLUMN_CREDIT,   90, theMoneyRenderer,  theMoneyEditor));
			addColumn(new DataColumn(COLUMN_DEBIT,    90, theMoneyRenderer,  theMoneyEditor));
			addColumn(new DataColumn(COLUMN_FREQ,    110, theStringRenderer, theComboEditor));
		}
		
		/**
		 * Add a column to the end of the model 
		 * @param pColumn
		 */
		private void addColumn(DataColumn pColumn) {
			/* Set the range */
			super.addColumn(pColumn);
			pColumn.setMember(true);
		}
	}
}
