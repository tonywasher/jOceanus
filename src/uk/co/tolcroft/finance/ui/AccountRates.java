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

public class AccountRates extends FinanceTableModel<AcctRate> implements ActionListener {
	/* Members */
	private static final long serialVersionUID = 36193763696660335L;
	
	private View					theView			= null;
	private RatesModel				theModel		= null;
	private AcctRate.List		    theRates   		= null;
	private JPanel					thePanel		= null;
	private AccountRates			theTable    	= this;
	private ratesMouse				theMouse		= null;
	private AccountTab				theParent   	= null;
	private Date.Range				theRange		= null;
	private Account                 theAccount  	= null;
	private View.ViewRates			theExtract		= null;
	private EditButtons    			theRowButs  	= null;
	private DebugEntry				theDebugEntry	= null;
	private Renderer.DateCell 		theDateRenderer = null;
	private Editor.DateCell 		theDateEditor   = null;
	private Renderer.RateCell 		theRateRenderer = null;
	private Editor.RateCell 		theRateEditor   = null;
		
	/* Access methods */
	public JPanel  	getPanel()			{ return thePanel; }
	public boolean 	hasHeader()			{ return false; }

	/* Access the debug entry */
	protected DebugEntry getDebugEntry()	{ return theDebugEntry; }
	
	/* Table headers */
	private static final String titleRate  = "Rate";
	private static final String titleBonus = "Bonus";
	private static final String titleDate  = "EndDate";
		
	/* Table columns */
	private static final int COLUMN_RATE  = 0;
	private static final int COLUMN_BONUS = 1;
	private static final int COLUMN_DATE  = 2;
	private static final int NUM_COLUMNS  = 3;
		
	/* Constructor */
	public AccountRates(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel 	myColModel;
		TableColumn		 	myCol;
		JScrollPane		 	myScroll;
		GroupLayout		 	myLayout;
		
		/* Store details about the parent */
		theParent = pParent;
		theView   = pParent.getView();

		/* Create the model and declare it to our superclass */
		theModel  = new RatesModel();
		setModel(theModel);
		
		/* Access the column model */
		myColModel = getColumnModel();
			
		/* Create the relevant formatters/editors */
		theDateRenderer = new Renderer.DateCell();
		theDateEditor   = new Editor.DateCell();
		theRateRenderer = new Renderer.RateCell();
		theRateEditor   = new Editor.RateCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_RATE);
		myCol.setCellRenderer(theRateRenderer);
		myCol.setCellEditor(theRateEditor);
		myCol.setPreferredWidth(90);
		
		myCol = myColModel.getColumn(COLUMN_BONUS);
		myCol.setCellRenderer(theRateRenderer);
		myCol.setCellEditor(theRateEditor);
		myCol.setPreferredWidth(90);
		
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setCellEditor(theDateEditor);
		myCol.setPreferredWidth(100);
		
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_OFF);
			
		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(800, 200));
		
		/* Add the mouse listener */
		theMouse = new ratesMouse();
		addMouseListener(theMouse);
		
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
        
        /* Create the debug entry, attach to AccountDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("Rates");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
        theDebugEntry.hideEntry();
	}
		
	/* Note that there has been a selection change */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the buttons */
		if (obj == (Object) theRowButs) {
			/* Set the correct show selected value */
			super.setShowDeleted(theRowButs.getShowDel());
		}
	}
		
	/* saveData */
	public void saveData() {
		if (theExtract != null) {
			theExtract.applyChanges();
		}
	}
	
	/* Refresh the data */
	public void refreshData() {			
		theRange = theView.getRange();
		theRange = new Date.Range(theRange.getStart(), null);
		theDateEditor.setRange(theRange);
	}
		
	/* Note that there has been a change */
	public void notifyChanges() {
		/* Update the row buttons */
		theRowButs.setLockDown();
		
		/* Find the edit state */
		if (theRates != null)
			theRates.findEditState();
		
		/* Update the parent panel */
		theParent.notifyChanges(); 
	}
		
	/* Set Selection */
	public void setSelection(Account pAccount) {
		theExtract = theView.new ViewRates(pAccount);
		theAccount = pAccount;
		theRates   = theExtract.getRates();
		setList(theRates);
		theDebugEntry.setObject(theExtract);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
	}
		
	/* Get field for column */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_RATE: 	return AcctRate.FIELD_RATE;
			case COLUMN_BONUS:	return AcctRate.FIELD_BONUS;
			case COLUMN_DATE:  	return AcctRate.FIELD_ENDDATE;
			default:			return -1;
		}
	}
		
	/* action performed listener event */
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
		
		/* If this is a set Null Date request */
		if (myCmd.compareTo(ratesMouse.popupSetNullDate) == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_DATE);
			theModel.fireTableCellUpdated(row, COLUMN_DATE);
		}
		
		/* If this is a set Null Bonus request */
		else if (myCmd.compareTo(ratesMouse.popupSetNullBonus) == 0) {
			/* Access the correct row */
			row = Integer.parseInt(myName);
		
			/* set the null value */
			theModel.setValueAt(null, row, COLUMN_BONUS);
			theModel.fireTableCellUpdated(row, COLUMN_BONUS);
		}
	}
		
	/* Rates table model */
	public class RatesModel extends AbstractTableModel {
		private static final long serialVersionUID = 296797947278000196L;

		/* get column count */
		public int getColumnCount() { return NUM_COLUMNS; }
			
		/* get row count */
		public int getRowCount() { 
			return (theRates == null) ? 0
					                  : theRates.size();
		}
			
		/* get column name */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_RATE:  	return titleRate;
				case COLUMN_BONUS:  return titleBonus;
				case COLUMN_DATE:	return titleDate;
				default:			return null;
			}
		}
			
		/* is cell edit-able */
		public boolean isCellEditable(int row, int col) {				
			/* Locked if the account is closed */
			if (theAccount.isClosed()) return false;
			
			/* Otherwise edit-able */
			return true;
		}
			
		/* get value At */
		public Object getValueAt(int row, int col) {
			AcctRate 	myRate;
			Object  o;
			
			/* Access the rate */
			myRate = theRates.get(row);
			
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_RATE:  	o = myRate.getRate();		break;
				case COLUMN_BONUS:	o = myRate.getBonus();		break;
				case COLUMN_DATE:	o = myRate.getEndDate();	break;
				default:			o = null; 					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myRate.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/* set value At */
		public void setValueAt(Object obj, int row, int col) {
			AcctRate 	myRate;
			
			/* Access the rate */
			myRate = theRates.get(row);
			
			/* Push history */
			myRate.pushHistory();
			
			/* Store the appropriate value */
			switch (col) {
				case COLUMN_RATE:  	myRate.setRate((Rate)obj);  break;
				case COLUMN_BONUS:  myRate.setBonus((Rate)obj); break;
				case COLUMN_DATE:	myRate.setEndDate((Date)obj);  break;
			}
				
			/* reset history if no change */
			if (myRate.checkForHistory()) {
				/* Set changed status */
				myRate.setState(DataState.CHANGED);
				theRates.findEditState();
			
				/* If we may have re-sorted re-Draw the table */
				if (col == COLUMN_DATE) {
					myRate.reSort();
					fireTableDataChanged();
					row = theRates.indexOf(myRate);
					selectRow(row);
				}
			
				/* else note that we have updated this cell */
				else fireTableCellUpdated(row, col);
			
				/* Update components to reflect changes */
				notifyChanges();
			}
		}
	}
	
	/* Rates mouse listener */
	public class ratesMouse extends MouseAdapter {
			
		/* Pop-up Menu items */
		private static final String popupSetNullDate  = "Set Null Date";
		private static final String popupSetNullBonus = "Set Null Bonus";
			
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
			AcctRate    		myRow     = null;
			AcctRate    		myCurr;
			boolean         isBonus   = false;
			boolean         isDate    = false;
				
			if (e.isPopupTrigger() && 
				(theTable.isEnabled())) {
				/* Calculate the row/column that the mouse was clicked at */
				Point p = new Point(e.getX(), e.getY());
				int row = theTable.rowAtPoint(p);
				int col = theTable.columnAtPoint(p);
				
				/* Adjust column for view differences */
				col = theTable.convertColumnIndexToModel(col);
				
				/* Access the row */
				myRow = theRates.get(row);
					
				/* Determine column */
				if (col == COLUMN_DATE)
					isDate = true;
				else if (col == COLUMN_BONUS)
					isBonus = true;
				
				/* If we are pointing to date then determine whether we can set null */
				if (isDate) {
					/* Handle null date */
					if ((myRow.isLocked()) ||
					    (myRow.getDate() == null) ||
					    (myRow.getDate().isNull()))
						isDate = false;
					
					/* Access the next valid element */
					myCurr = theRates.peekNext(myRow);
					
					/* Handle not last */
					if (myCurr != null) isDate = false;
				}
				
				/* If we are pointing to date then determine whether we can set null */
				if ((isBonus) && 
					((myRow.isLocked()) ||
					 (myRow.getBonus() == null)))
					isBonus = false;
				
				/* If we can set null value */
				if ((isBonus) || (isDate)) {
					/* Create the pop-up menu */
					myMenu = new JPopupMenu();
					
					/* If we have date */
					if (isDate) {
						/* Create the View account choice */
						myItem = new JMenuItem(popupSetNullDate);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNullDate + ":" + row);
						myItem.addActionListener(theTable);
						myMenu.add(myItem);
					}
					
					/* If we have bonus */
					if (isBonus) {
						/* Create the View account choice */
						myItem = new JMenuItem(popupSetNullBonus);
					
						/* Set the command and add to menu */
						myItem.setActionCommand(popupSetNullBonus + ":" + row);
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
