package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.EditButtons.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;

public class AccountPrices extends FinanceTableModel<ViewPrice> {
	/* Members */
	private static final long serialVersionUID 		= 1035380774297559650L;

	private View						theView				= null;
	private PricesModel					theModel			= null;
	private ViewPrice.List 				thePrices  			= null;
	private JPanel						thePanel			= null;
	private JScrollPane					theScroll			= null;
	private AccountTab					theParent   		= null;
	private Date.Range					theRange			= null;
	private Account             		theAccount  		= null;
	private EditButtons    				theRowButs  		= null;
	private Renderer.DateCell 			theDateRenderer  	= null;
	private Editor.DateCell 			theDateEditor    	= null;
	private Renderer.PriceCell 			thePriceRenderer 	= null;
	private Editor.PriceCell			thePriceEditor   	= null;
	private Renderer.DilutionCell 		theDiluteRenderer 	= null;
	private Renderer.DilutedPriceCell	theDilPriceRenderer	= null;
	private boolean						hasDilutions		= true;
	private DebugEntry					theDebugEntry		= null;
	private ErrorPanel					theError			= null;
	private TableColumn					theDiluteCol		= null;
	private TableColumn					theDilPriceCol		= null;

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
	private static final int NUM_COLUMNS  		 = 4;
					
	/**
	 * Constructor for Prices Window
	 * @param pParent the parent window
	 */
	public AccountPrices(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel 			myColModel;
		TableColumn		 			myCol;
		GroupLayout		 	       	myLayout;
			
		/* Store details about the parent */
		theParent = pParent;
		theView   = pParent.getView();

		/* Create the model and declare it to our superclass */
		theModel  = new PricesModel();
		setModel(theModel);
			
		/* Access the column model */
		myColModel = getColumnModel();
			
		/* Create the relevant formatters/editors */
		theDateRenderer  	= new Renderer.DateCell();
		theDateEditor    	= new Editor.DateCell();
		thePriceRenderer 	= new Renderer.PriceCell();
		thePriceEditor  	= new Editor.PriceCell();
		theDiluteRenderer 	= new Renderer.DilutionCell();
		theDilPriceRenderer	= new Renderer.DilutedPriceCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setCellEditor(theDateEditor);
		myCol.setPreferredWidth(80);
		
		myCol = myColModel.getColumn(COLUMN_PRICE);
		myCol.setCellRenderer(thePriceRenderer);
		myCol.setCellEditor(thePriceEditor);
		myCol.setPreferredWidth(90);
		
		myCol = myColModel.getColumn(COLUMN_DILUTION);
		myCol.setCellRenderer(theDiluteRenderer);
		myCol.setPreferredWidth(90);
		theDiluteCol = myCol;
		
		myCol = myColModel.getColumn(COLUMN_DILUTEDPRICE);
		myCol.setCellRenderer(theDilPriceRenderer);
		myCol.setPreferredWidth(110);
		theDilPriceCol = myCol;
		
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
			
		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(800, 200));
		
		/* Create the sub panels */
		theRowButs   = new EditButtons(this, InsertStyle.INSERT);
			
		/* Create a new Scroll Pane and add this table to it */
		theScroll     = new JScrollPane();
		theScroll.setViewportView(this);
        
        /* Create the debug entry, attach to AccountDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("Prices");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
        theDebugEntry.hideEntry();
			
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
	                    .addComponent(theScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(theError.getPanel())
	                .addComponent(theScroll)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theRowButs.getPanel())
	                .addContainerGap())
	    );
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
	}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() {			
		theRange = theView.getRange();
		theDateEditor.setRange(theRange);
	}
		
	/**
	 * Save changes from the view into the underlying data
	 */
	public void saveData() {
		if (thePrices != null) {
			thePrices.applyChanges();
		}
	}
	
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Lock scroll-able area */
		theScroll.setEnabled(!isError);

		/* Lock row/tab buttons area */
		theRowButs.getPanel().setEnabled(!isError);
	}
	
	/**
	 * Call underlying controls to take notice of changes in view/selection
	 */
	public void notifyChanges() {
		/* Update the row buttons */
		theRowButs.setLockDown();
		
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
		theAccount = pAccount;
		thePrices  = new ViewPrice.List(theView, pAccount);
		setColSelection();
		super.setList(thePrices);
		theDebugEntry.setObject(thePrices);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
	}
		
	/**
	 * Set column selection for this view
	 */
	public void setColSelection() {
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
		
	/**
	 * Obtain the Field id associated with the column
	 * @param column the column
	 */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 	return AcctPrice.FIELD_DATE;
			case COLUMN_PRICE: 	return AcctPrice.FIELD_PRICE;
			default:			return -1;
		}
	}
		
	/* Prices table model */
	public class PricesModel extends AbstractTableModel {
		private static final long serialVersionUID = -2613779599240142148L;

		/**
		 * Get the number of display columns
		 * @return the columns
		 */
		public int getColumnCount() { return (hasDilutions) ? NUM_COLUMNS : NUM_COLUMNS-2; }
			
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
			if ((o == null) && (myPrice.hasErrors(getFieldForCol(col))))
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
				thePrices.findEditState();
				
				/* If we may have re-sorted re-Draw the table */
				if (col == COLUMN_DATE) {
					myPrice.reSort();
					fireTableDataChanged();
					row = thePrices.indexOf(myPrice);
					selectRow(row);
				}
				
				/* else note that we have updated this cell */
				else fireTableCellUpdated(row, col);
			
				/* Update components to reflect changes */
				notifyChanges();
			}
		}
	}			
}
