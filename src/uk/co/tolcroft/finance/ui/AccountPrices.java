package uk.co.tolcroft.finance.ui;

import javax.swing.GroupLayout;
import javax.swing.JPanel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.*;
import uk.co.tolcroft.models.ui.Editor;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.views.ViewList.ListClass;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class AccountPrices extends FinanceTable<ViewPrice> {
	/* Members */
	private static final long serialVersionUID 		= 1035380774297559650L;

	private View						theView				= null;
	private PricesModel					theModel			= null;
	private ViewPrice.List 				thePrices  			= null;
	private JPanel						thePanel			= null;
	private AccountTab					theParent   		= null;
	private Date.Range					theRange			= null;
	private Account             		theAccount  		= null;
	private ListClass					theViewList			= null;
	private AccountPrices				theTable	    	= this;
	private pricesMouse					theMouse			= null;
	private pricesColumnModel			theColumns			= null;
	private DebugEntry					theDebugEntry		= null;
	private ErrorPanel					theError			= null;

	/* Access methods */
	public JPanel  	getPanel()			{ return thePanel; }
	public boolean 	hasHeader()			{ return false; }
		
	/* Access the debug entry */
	public DebugEntry getDebugEntry()	{ return theDebugEntry; }
	
	/* Hooks */
	public boolean needsMembers() 	{ return true; }
		
	/* Table headers */
	private static final String titleDate  		= "Date";
	private static final String titlePrice 		= "Price";
	private static final String titleDilution 	= "Dilution";
	private static final String titleDilPrice 	= "DilutedPrice";
		
	/* Table columns */
	private static final int COLUMN_DATE  		 = 0;
	private static final int COLUMN_PRICE 		 = 1;
	private static final int COLUMN_DILUTION 	 = 2;
	private static final int COLUMN_DILUTEDPRICE = 3;
					
	/**
	 * Constructor for Prices Window
	 * @param pParent the parent window
	 */
	public AccountPrices(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		GroupLayout		 	       	myLayout;
			
		/* Store details about the parent */
		theParent  	= pParent;
		theView    	= pParent.getView();
		theViewList = pParent.getViewSet().registerClass(ViewPrice.class);

		/* Create the model and declare it to our superclass */
		theModel  = new PricesModel();
		setModel(theModel);
			
		/* Create the data column model and declare it */
		theColumns = new pricesColumnModel();
		setColumnModel(theColumns);

		/* Prevent reordering of columns and auto-resizing */
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
			
        /* Create the debug entry, attach to AccountDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("Prices");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
        theDebugEntry.hideEntry();
			
		/* Add the mouse listener */
		theMouse = new pricesMouse();
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
	                    .addComponent(getScrollPane(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theError.getPanel())
	                .addComponent(getScrollPane())
	                .addContainerGap())
	    );
	}

	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() {			
		theRange = theView.getRange();
		theColumns.setDateEditorRange(theRange);
	}
		
 	/**
	 * Update Debug view 
	 */
	public void updateDebug() {			
		theDebugEntry.setObject(thePrices);
	}
		
	/**
	 * Save changes from the view into the underlying data
	 */
	public void saveData() {
		/* Just update the debug, save has already been done */
		updateDebug();
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
		if (thePrices != null)
			thePrices.findEditState();
		
		/* Update the parent panel */
		theParent.notifyChanges(); 
	}
		
	/**
	 * Set Selection to the specified account
	 * @param pAccount the Account for the extract
	 */
	public void setSelection(Account pAccount) throws Exception {
		/* Record the account */
		theAccount 	= pAccount;
		thePrices 	= null;
		
		/* If we have an account */
		if (theAccount != null) {
			/* Obtain the Prices extract */
			thePrices  = new ViewPrice.List(theView, pAccount);
		}
		
		/* Declare the list */
		theColumns.setColumnSelection();
		super.setList(thePrices);
		theViewList.setDataList(thePrices);
	}
		
	/* Prices table model */
	public class PricesModel extends DataTableModel {
		private static final long serialVersionUID = -2613779599240142148L;

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
			return (thePrices == null) ? 0
					                   : thePrices.size();
		}
			
		/**
		 * Get the name of the column
		 * @param col the column
		 * @return the name of the column
		 */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_DATE:  			return titleDate;
				case COLUMN_PRICE:			return titlePrice;
				case COLUMN_DILUTION:		return titleDilution;
				case COLUMN_DILUTEDPRICE:	return titleDilPrice;
				default:					return null;
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
				case COLUMN_DATE: 	return AcctPrice.FIELD_DATE;
				case COLUMN_PRICE: 	return AcctPrice.FIELD_PRICE;
				default:			return -1;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
		public boolean isCellEditable(int row, int col) {				
			/* Locked if the account is closed */
			if (theAccount.isClosed()) return false;
			
			switch (col) {
				case COLUMN_DATE:  			return (theRange != null);
				case COLUMN_PRICE:			return true;
				case COLUMN_DILUTION:		return false;
				case COLUMN_DILUTEDPRICE:	return false;
				default:					return true;
			}
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) {
			ViewPrice 	myPrice;
			Object	o;
			
			/* Access the price */
			myPrice = thePrices.get(row);
				
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_DATE:  			o = myPrice.getDate();			break;
				case COLUMN_PRICE:			o = myPrice.getPrice();			break;
				case COLUMN_DILUTION:		o = myPrice.getDilution();		break;
				case COLUMN_DILUTEDPRICE:	o = myPrice.getDilutedPrice();	break;
				default:					o = null;						break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myPrice.hasErrors(getFieldForCell(row, col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/**
		 * Set the value at (row, col)
		 * @param obj the object value to set
		 */
		public void setValueAt(Object obj, int row, int col) {
			ViewPrice myPrice;
				
			/* Access the price */
			myPrice = thePrices.get(row);
				
			/* Push history */
			myPrice.pushHistory();
			
			/* Protect against Exceptions */
			try {
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_DATE:  	myPrice.setDate((Date)obj);  break;
					case COLUMN_PRICE:	myPrice.setPrice((Price)obj); break;
				}	
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				myPrice.popHistory();
				myPrice.pushHistory();
				
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field at ("
										          + row + "," + col +")",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}

			/* If we have changes */
			if (myPrice.checkForHistory()) {
				/* Set new state */
				myPrice.setState(DataState.CHANGED);
				
				/* Validate the item and update the edit state */
				myPrice.clearErrors();
				myPrice.validate();
				thePrices.findEditState();
				
				/* Switch on the updated column */
				switch (col) {
					case COLUMN_DATE:
						/* Re-Sort the row */
						thePrices.reSort(myPrice);

						/* Determine new row # */
						int myNewRowNo = myPrice.indexOf();
					
						/* If the row # has changed */
						if (myNewRowNo != row) {
							/* Report the move of the row */
							fireMoveRowEvents(row, myNewRowNo);					
							selectRowWithScroll(myNewRowNo);
							break;
						}
					
						/* else fall through */
		
					/* else note that we have updated this row */
					default:
						fireTableRowsUpdated(row, row);
						break;
				}
			
				/* Update components to reflect changes */
				notifyChanges();
				updateDebug();
			}
		}
	}			

	/**
	 *  Prices mouse listener
	 */
	private class pricesMouse extends FinanceMouse<ViewPrice> {
		/**
		 * Constructor
		 */
		private pricesMouse() {
			/* Call super-constructor */
			super(theTable);
		}		
	}		

	/**
	 * Column Model class
	 */
	private class pricesColumnModel extends DataColumnModel {
		private static final long serialVersionUID = -851990835577845594L;

		/* Renderers/Editors */
		private Renderer.DateCell 			theDateRenderer  	= null;
		private Editor.DateCell 			theDateEditor    	= null;
		private Renderer.PriceCell 			thePriceRenderer 	= null;
		private Editor.PriceCell			thePriceEditor   	= null;
		private Renderer.DilutionCell 		theDiluteRenderer 	= null;
		private Renderer.DilutedPriceCell	theDilPriceRenderer	= null;
		private DataColumn					theDiluteCol		= null;
		private DataColumn					theDilPriceCol		= null;
		private boolean						hasDilutions		= true;

		/**
		 * Constructor 
		 */
		private pricesColumnModel() {		
			/* Create the relevant formatters/editors */
			theDateRenderer  	= new Renderer.DateCell();
			theDateEditor    	= new Editor.DateCell();
			thePriceRenderer 	= new Renderer.PriceCell();
			thePriceEditor  	= new Editor.PriceCell();
			theDiluteRenderer 	= new Renderer.DilutionCell();
			theDilPriceRenderer	= new Renderer.DilutedPriceCell();
			
			/* Create the columns */
			addColumn(new DataColumn(COLUMN_DATE,          80, theDateRenderer, theDateEditor));
			addColumn(new DataColumn(COLUMN_PRICE, 	       90, thePriceRenderer, thePriceEditor));
			addColumn(theDiluteCol   = new DataColumn(COLUMN_DILUTION,      90, theDiluteRenderer, null));
			addColumn(theDilPriceCol = new DataColumn(COLUMN_DILUTEDPRICE, 100, theDilPriceRenderer, null));
		}
		
		/**
		 * Set the date editor range 
		 * @param pRange
		 */
		private void setDateEditorRange(Date.Range pRange) {
			/* Set the range */
			theDateEditor.setRange(pRange);			
		}

		/**
		 * Set column selection for this view
		 */
		private void setColumnSelection() {
			/* If we should show dilutions */
			if ((thePrices != null) && (thePrices.hasDilutions())) {
				/* If we are not showing dilutions */
				if (!hasDilutions) {
					/* Add the dilutions columns and record the fact */
					addColumn(theDiluteCol);
					addColumn(theDilPriceCol);
					hasDilutions = true;
				}
			}
			
			/* else If we are showing dilutions */
			else if (hasDilutions) {
				/* Remove the dilutions columns and record the fact */
				removeColumn(theDiluteCol);
				removeColumn(theDilPriceCol);
				hasDilutions = false;
			}
		}
	}
}
