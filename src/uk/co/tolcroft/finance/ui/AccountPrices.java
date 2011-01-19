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
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number;
import uk.co.tolcroft.models.*;

public class AccountPrices extends FinanceTableModel<Price> {
	/* Members */
	private static final long serialVersionUID 		= 1035380774297559650L;

	private View				theView				= null;
	private PricesModel			theModel			= null;
	private Price.List  		thePrices  			= null;
	private JPanel				thePanel			= null;
	private AccountTab			theParent   		= null;
	private Date.Range			theRange			= null;
	private Account             theAccount  		= null;
	private View.ViewPrices		theExtract			= null;
	private EditButtons    		theRowButs  		= null;
	private Renderer.DateCell 	theDateRenderer  	= null;
	private Editor.DateCell 	theDateEditor    	= null;
	private Renderer.PriceCell 	thePriceRenderer 	= null;
	private Editor.PriceCell	thePriceEditor   	= null;

	/* Access methods */
	public JPanel  	getPanel()			{ return thePanel; }
	public boolean 	hasHeader()			{ return false; }
		
	/* Hooks */
	public boolean needsMembers() 	{ return true; }
		
	/* Table headers */
	private static final String titleDate  = "Date";
	private static final String titlePrice = "Price";
		
	/* Table columns */
	private static final int COLUMN_DATE  = 0;
	private static final int COLUMN_PRICE = 1;
	private static final int NUM_COLUMNS  = 2;
					
	/* Constructor */
	public AccountPrices(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel 			myColModel;
		TableColumn		 			myCol;
		JScrollPane		     		myScroll;
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
		theDateRenderer  = new Renderer.DateCell();
		theDateEditor    = new Editor.DateCell();
		thePriceRenderer = new Renderer.PriceCell();
		thePriceEditor   = new Editor.PriceCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setCellEditor(theDateEditor);
		myCol.setPreferredWidth(80);
		
		myCol = myColModel.getColumn(COLUMN_PRICE);
		myCol.setCellRenderer(thePriceRenderer);
		myCol.setCellEditor(thePriceEditor);
		myCol.setPreferredWidth(90);
		
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
			
		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(800, 200));
		
		/* Create the sub panels */
		theRowButs   = new EditButtons(this, InsertStyle.INSERT);
			
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
	                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(myScroll)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theRowButs.getPanel())
	                .addContainerGap())
	    );
	}

	
	/* Note that there has been a selection change */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the buttons */
		if (obj == (Object) theRowButs) {
			/* Set the correct show selected value */
			super.setShowDeleted(theRowButs.getShowDel());
		}
	}
		
	/* Refresh the data */
	public void refreshData() {			
		theRange = theView.getRange();
		theDateEditor.setRange(theRange);
	}
		
	/* saveData */
	public void saveData() {
		if (theExtract != null) {
			theExtract.applyChanges();
		}
	}
	
	/* Note that there has been a change */
	public void notifyChanges() {
		/* Update the row buttons */
		theRowButs.setLockDown();
		
		/* Find the edit state */
		if (thePrices != null)
			thePrices.findEditState();
		
		/* Update the parent panel */
		theParent.notifyChanges(); 
	}
		
	/* Set Selection */
	public void setSelection(Account pAccount) {
		theExtract = theView.new ViewPrices(pAccount);
		theAccount = pAccount;
		thePrices  = theExtract.getPrices();
		super.setList(thePrices);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
	}
		
	/* Get field for column */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 	return Price.FIELD_DATE;
			case COLUMN_PRICE: 	return Price.FIELD_PRICE;
			default:			return -1;
		}
	}
		
	/* Prices table model */
	public class PricesModel extends AbstractTableModel {
		private static final long serialVersionUID = -2613779599240142148L;

		/* get column count */
		public int getColumnCount() { return NUM_COLUMNS; }
			
		/* get row count */
		public int getRowCount() { 
			return (thePrices == null) ? 0
					                   : thePrices.size();
		}
			
		/* get column name */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_DATE:  	return titleDate;
				case COLUMN_PRICE:	return titlePrice;
				default:			return null;
			}
		}
			
		/* is cell edit-able */
		public boolean isCellEditable(int row, int col) {				
			/* Locked if the account is closed */
			if (theAccount.isClosed()) return false;
			
			switch (col) {
				case COLUMN_DATE:  	return (theRange != null);
				case COLUMN_PRICE:	return true;
				default:			return true;
			}
		}
			
		/* get value At */
		public Object getValueAt(int row, int col) {
			Price 	myPrice;
			Object	o;
			
			/* Access the price */
			myPrice = thePrices.get(row);
				
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_DATE:  	o = myPrice.getDate();	break;
				case COLUMN_PRICE:	o = myPrice.getPrice();	break;
				default:			o = null;				break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myPrice.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/* set value At */
		public void setValueAt(Object obj, int row, int col) {
			Price myPrice;
				
			/* Access the price */
			myPrice = thePrices.get(row);
				
			/* Push history */
			myPrice.pushHistory();
			
			/* Store the appropriate value */
			switch (col) {
				case COLUMN_DATE:  	myPrice.setDate((Date)obj);  break;
				case COLUMN_PRICE:	myPrice.setPrice((Number.Price)obj); break;
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
