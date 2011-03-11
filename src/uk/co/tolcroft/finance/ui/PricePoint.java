package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.GroupLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;

public class PricePoint extends FinanceTableModel<SpotPrices.SpotPrice> implements ActionListener {
	/* Members */
	private static final long serialVersionUID = 5826211763056873599L;
	
	private View					theView				= null;
	private spotViewModel			theModel			= null;
	private SpotPrices             	theSnapshot			= null;
	private SpotPrices.List        	thePrices			= null;
	private MainTab					theParent			= null;
	private JPanel					thePanel			= null;
	private JScrollPane				theScroll			= null;
	private PricePoint			 	theTable			= this;
	private spotViewMouse			theMouse			= null;
	private Date					theDate				= null;
	private SpotSelect				theSelect	 		= null;
	private SaveButtons  			theTabButs   		= null;
	private DebugEntry				theDebugPrice		= null;
	private ErrorPanel				theError			= null;
	private Renderer.DateCell 		theDateRenderer  	= null;
	private Renderer.PriceCell 		thePriceRenderer  	= null;
	private Editor.PriceCell 		thePriceEditor    	= null;
	private Renderer.StringCell 	theStringRenderer 	= null;

	/* Access methods */
	public JPanel  	getPanel()			{ return thePanel; }
	public boolean	hasHeader()			{ return false; }
	
	/* Access the debug entry */
	public DebugEntry 	getDebugEntry()		{ return theDebugPrice; }
	public DebugManager getDebugManager() 	{ return theParent.getDebugMgr(); }
	
	/* Table headers */
	private static final String titleAsset   	= "Asset";
	private static final String titlePrice   	= "Price";
	private static final String titlePrevPrice	= "Previous Price";
	private static final String titlePrevDate	= "Previous Date";
		
	/* Table columns */
	private static final int COLUMN_ASSET 	 	= 0;
	private static final int COLUMN_PRICE	 	= 1;
	private static final int COLUMN_PREVPRICE	= 2;
	private static final int COLUMN_PREVDATE	= 3;
	private static final int NUM_COLUMNS	 	= 4;
		
	/* Constructor */
	public PricePoint(MainTab pParent) {
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
        theDebugPrice = myDebugMgr.new DebugEntry("SpotPrices");
        theDebugPrice.addAsChildOf(mySection);
		
		/* Create the model and declare it to our superclass */
		theModel  = new spotViewModel();
		setModel(theModel);
			
		/* Access the column model */
		myColModel = getColumnModel();
		
		/* Create the relevant formatters/editors */
		theDateRenderer    = new Renderer.DateCell();
		thePriceRenderer   = new Renderer.PriceCell();
		thePriceEditor     = new Editor.PriceCell();
		theStringRenderer  = new Renderer.StringCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_ASSET);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setPreferredWidth(130);
			
		myCol = myColModel.getColumn(COLUMN_PRICE);
		myCol.setCellRenderer(thePriceRenderer);
		myCol.setCellEditor(thePriceEditor);
		myCol.setPreferredWidth(130);
		
		myCol = myColModel.getColumn(COLUMN_PREVPRICE);
		myCol.setCellRenderer(thePriceRenderer);
		myCol.setPreferredWidth(130);
		
		myCol = myColModel.getColumn(COLUMN_PREVDATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setPreferredWidth(130);
		
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);

		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(900, 200));
		
		/* Add the mouse listener */
		theMouse = new spotViewMouse();
		addMouseListener(theMouse);
		
		/* Create the sub panels */
		theSelect    = new SpotSelect(theView, this);
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
	                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	        		.addComponent(theError.getPanel())
	        		.addComponent(theSelect.getPanel())
	                .addComponent(theScroll)
	                .addComponent(theTabButs.getPanel()))
	    );
	}
		
	/**
	 * Save changes from the view into the underlying data
	 */
	public void saveData() {
		if (theSnapshot != null) {
			validateAll();
			if (!hasErrors()) theSnapshot.applyChanges();
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

		/* Lock tab buttons area */
		theTabButs.getPanel().setEnabled(!isError);
	}
	
	/**
	 *  Notify table that there has been a change in selection by an underlying control
	 *  @param obj the underlying control that has changed selection
	 */
	public void    notifySelection(Object obj)    {
		/* if this is a change from the date */
		if (obj == (Object) theSelect) {
			/* Set the deleted option */
			if (getList().getShowDeleted() != theSelect.getShowClosed())
				setShowDeleted(theSelect.getShowClosed());
			
			/* Set the new date */
			if (Date.differs(theDate, theSelect.getDate())) {
				/* Protect against exceptions */
				try {
					setSelection(theSelect.getDate());
					
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
	}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws Exception {
		Date.Range myRange = theView.getRange();
		theSelect.setRange(myRange);
		theDate = theSelect.getDate();
		setSelection(theDate);
		
		/* Create SavePoint */
		theSelect.createSavePoint();
	}
	
	/**
	 * Call underlying controls to take notice of changes in view/selection
	 */
	public void notifyChanges() {
		/* Find the edit state */
		if (thePrices != null)
			thePrices.findEditState();
		
		/* Update the table buttons */
		theTabButs.setLockDown();
		theSelect.setLockDown();
		
		/* Update the top level tabs */
		theParent.setVisibleTabs();
	}
		
	/**
	 * Set Selection to the specified date
	 * @param pDate the Date for the extract
	 */
	public void setSelection(Date pDate) throws Exception {
		theDate = pDate;
		if (theDate != null) {
			theSnapshot = new SpotPrices(theView, pDate);
			thePrices   = theSnapshot.getPrices();
			theSelect.setAdjacent(theSnapshot.getPrev(), 
								  theSnapshot.getNext());
		}
		else {
			theSnapshot = null;
			thePrices   = null;				
			theSelect.setAdjacent(null, null);
		}
		setList(thePrices);
		theDebugPrice.setObject(theSnapshot);
		theModel.fireTableDataChanged();
		theTabButs.setLockDown();
		theSelect.setLockDown();
		theParent.setVisibleTabs();
	}
		
	/**
	 * Obtain the Field id associated with the column
	 * @param column the column
	 */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_ASSET: 		return AcctPrice.FIELD_ACCOUNT;
			case COLUMN_PRICE:		return AcctPrice.FIELD_PRICE;
			default: 				return -1;
		}
	}
		
	/**
	 * Check whether the restoration of the passed object is compatible with the current selection
	 * @param pItem the current item
	 * @param pObj the potential object for restoration
	 */
	public boolean isValidObj(DataItem 				pItem,
							  DataItem.histObject  	pObj) {
		SpotPrices.SpotPrice	mySpot  = (SpotPrices.SpotPrice) pItem;
		AcctPrice.Values          	myPrice = (AcctPrice.Values) pObj;
		
		/* Check whether the date is the same */
		if (Date.differs(mySpot.getDate(), myPrice.getDate()))
			return false;

		/* Otherwise OK */
		return true;
	}
		
	/**
	 * Perform actions for controls/pop-ups on this table
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {
		String          myCmd;
		String          tokens[];
		String          myName = null;
		int             row;

		/* Access the action command */
		myCmd  = evt.getActionCommand();
		tokens = myCmd.split(":");
		myCmd  = tokens[0];
		if (tokens.length > 1) myName = tokens[1];
		
		/* Cancel any editing */
		if (isEditing()) cellEditor.cancelCellEditing();
		
		/* If this is a set Null request */
		if (myCmd.compareTo(spotViewMouse.popupSetNull) == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_PRICE);
			theModel.fireTableCellUpdated(row, COLUMN_PRICE);
		}
	}
		
	/* SpotView table model */
	public class spotViewModel extends AbstractTableModel {
		private static final long serialVersionUID = 2520681944053000625L;

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
				case COLUMN_ASSET: 		return titleAsset;
				case COLUMN_PRICE: 		return titlePrice;
				case COLUMN_PREVPRICE: 	return titlePrevPrice;
				case COLUMN_PREVDATE: 	return titlePrevDate;
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
				case COLUMN_ASSET: 		return String.class;
				default: 				return Object.class;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
		public boolean isCellEditable(int row, int col) {
			/* switch on column */
			switch (col) {
				case COLUMN_ASSET:
				case COLUMN_PREVPRICE:
				case COLUMN_PREVDATE:
					return false;
				case COLUMN_PRICE:
				default:
					return true;
			}
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) {
			SpotPrices.SpotPrice 	mySpot;
			Object         			o;
											
			/* Access the spot price */
			mySpot = thePrices.get(row);
			
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_ASSET:  	
					o = mySpot.getAccount().getName();
					break;
				case COLUMN_PRICE:  	
					o = mySpot.getPrice();
					break;
				case COLUMN_PREVPRICE:  	
					o = mySpot.getPrevPrice();
					break;
				case COLUMN_PREVDATE:  	
					o = mySpot.getPrevDate();
					break;
				default:	
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (mySpot.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/**
		 * Set the value at (row, col)
		 * @param obj the object value to set
		 */
		public void setValueAt(Object obj, int row, int col) {
			SpotPrices.SpotPrice mySpot;
			
			/* Access the line */
			mySpot = thePrices.get(row);
			
			/* Push history */
			mySpot.pushHistory();
			
			/* Protect against Exceptions */
			try {
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_PRICE:  
						mySpot.setPrice((Price)obj);    
						break;
				}
				
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				mySpot.popHistory();
				mySpot.pushHistory();
				
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field at ("
										          + row + "," + col +")",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}

			/* Check for changes */
			if (mySpot.checkForHistory()) {
				/* Note that the item has changed */
				mySpot.setState(DataState.CHANGED);
				thePrices.findEditState();
				
				/* note that we have updated this cell */
				fireTableCellUpdated(row, col);
				
				/* Note that changes have occurred */
				notifyChanges();
			}
		}
	}
	
	/* spotView mouse listener */
	public class spotViewMouse extends MouseAdapter {
			
		/* Pop-up Menu items */
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
			JPopupMenu              myMenu;
			JMenuItem               myItem;
			SpotPrices.SpotPrice	myRow;
			boolean                 isPrice  = false;
				
			if (e.isPopupTrigger() && 
				(theTable.isEnabled())) {
				/* Calculate the row/column that the mouse was clicked at */
				Point p = new Point(e.getX(), e.getY());
				int row = theTable.rowAtPoint(p);
				int col = theTable.columnAtPoint(p);
				
				/* Adjust column for view differences */
				col = theTable.convertColumnIndexToModel(col);
				
				/* Access the row */
				myRow = thePrices.get(row);
					
				/* If the column is Price */
				if (col == COLUMN_PRICE)
					isPrice = true;
				
				/* If we are pointing to units then determine whether we can set null */
				if ((isPrice) && 
					(myRow.getPrice() == null))
					isPrice = false;
				
				/* If we can set null price */
				if (isPrice) {
					/* Create the pop-up menu */
					myMenu = new JPopupMenu();
					
					/* If we have price */
					if (isPrice) {
						/* Create the View account choice */
						myItem = new JMenuItem(popupSetNull);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNull + ":" + row);
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
