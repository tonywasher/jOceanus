package uk.co.tolcroft.finance.ui.controls;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.table.AbstractTableModel;

import uk.co.tolcroft.models.*;

public abstract class FinanceMouse<T extends DataItem<T>> extends MouseAdapter
													      implements ActionListener {
	/* Members */
	private FinanceTable<T> 	theTable 		= null;
	private boolean				doShowDeleted	= false;
	private int					theRow			= -1;
	private int					theCol			= -1;
	private boolean				isHeader		= false;

	/* Access methods */
	protected int		getPopupRow()	{ return theRow; }
	protected int		getPopupCol()	{ return theCol; }
	protected boolean	isHeader()		{ return isHeader; }
	
	/* Pop-up Menu items */
	private static final String popupInsertCredit  = "Insert Credit";
	private static final String popupInsertDebit   = "Insert Debit";
	private static final String popupInsertItem    = "Insert Item";
	private static final String popupDeleteItems   = "Delete Item(s)";
	private static final String popupDuplItems     = "Duplicate Item(s)";
	private static final String popupRecoverItems  = "Recover Item(s)";
	private static final String popupShowDeleted   = "Show Deleted";
	private static final String popupUndoChange    = "Undo";
	private static final String popupValidate      = "Validate Item(s)";
	private static final String popupResetItems    = "Reset Item(s)";
	private static final String popupNextHistory   = "Next History";
	private static final String popupPrevHistory   = "Previous History";

	/**
	 * Constructor
	 * @param pTable the table
	 */
	public FinanceMouse(FinanceTable<T> pTable) {
		/* Store parameters */
		theTable = pTable;
		
		/* Add as listener to the header */
		theTable.getTableHeader().addMouseListener(this);
	}
	/**
	 * Handle mouse Pressed event
	 */
	public void mousePressed(MouseEvent e) {
		maybeShowPopup(e);
	}

	/**
	 * Handle mouse Released event
	 */
	public void mouseReleased(MouseEvent e) {
		maybeShowPopup(e);
	}
		
	/**
	 * Maybe show the PopUp
	 */
	public void maybeShowPopup(MouseEvent e) {
		JPopupMenu      myMenu;
		int				myRow;
				
		/* If we can trigger a PopUp menu */
		if ((e.isPopupTrigger()) && 
			(theTable.isEnabled())) {
			/* Note if this is a header PopUp */
			isHeader = (e.getComponent() == theTable.getTableHeader());
			
			/* Access the point that the mouse was clicked at */
			Point p = new Point(e.getX(), e.getY());
			
			/* If we are in the table */
			if (!isHeader) {
				/* Access column and row */
				theRow = theTable.rowAtPoint(p);
				theCol = theTable.columnAtPoint(p);
				myRow  = theRow;
				
				/* Adjust column for view differences */
				theCol = theTable.convertColumnIndexToModel(theCol);
				
				/* If the table has a header */
				if (theTable.hasHeader()) {
					/* Row zero is the same as header */
					if (theRow == 0) isHeader = true;
			
					/* else adjust row for header */
					else theRow--;
				}
			
				/* If we are on a valid row  */
				if ((!isHeader) && (theRow >= 0)) {
					/* Ensure that this row is selected */
					if (!theTable.isRowSelected(myRow))
						theTable.setRowSelectionInterval(myRow, myRow);
				}
			}

			/* Create the pop-up menu */
			myMenu = new JPopupMenu();
							
			/* Add special commands to menu */
			addSpecialCommands(myMenu);
			
			/* Add navigation commands to menu */
			addNavigationCommands(myMenu);
			
			/* Add insert/delete commands to menu */
			addInsertDelete(myMenu);
			
			/* Add edit commands to menu */
			addEditCommands(myMenu);
			
			/* Add null commands to menu */
			addNullCommands(myMenu);
			
			/* If we have items in the menu */
			if (myMenu.getComponentCount() > 0) {
				/* Show the pop-up menu */
				myMenu.show(e.getComponent(),
						    e.getX(), e.getY());
			}
		}
	}
	
	/**
	 * Add Insert/Delete commands to menu
	 * Should be overridden if insert/delete is not required  
	 * @param pMenu the menu to add to
	 */
	protected void addInsertDelete(JPopupMenu pMenu) {
		JMenuItem 			myItem;
		JCheckBoxMenuItem	myCheckBox;
		boolean				enableRecov = false;
		boolean				enableDel	= false;
		boolean				enableShow	= true;
		boolean				enableDupl	= true;
		
		/* Nothing to do if the table is locked */
		if (theTable.isLocked()) return;
		
		/* Add separator if there are already items in menu */
		if (pMenu.getComponentCount() > 0)
			pMenu.addSeparator();
		
		/* If the table has a credit choice */
		if (theTable.hasCreditChoice()) {
			/* Add the insert Credit choice */
			myItem = new JMenuItem(popupInsertCredit);
			myItem.setActionCommand(popupInsertCredit);
			myItem.addActionListener(this);
			pMenu.add(myItem);

			/* Add the insert Debit choice */
			myItem = new JMenuItem(popupInsertDebit);
			myItem.setActionCommand(popupInsertDebit);
			myItem.addActionListener(this);
			pMenu.add(myItem);
		}
		
		/* else it just uses insert item */
		else {
			/* Add the insert item choice */
			myItem = new JMenuItem(popupInsertItem);
			myItem.setActionCommand(popupInsertItem);
			myItem.addActionListener(this);
			pMenu.add(myItem);
		}
		
		/* Loop through the selected rows */
		for (T myRow : theTable.cacheSelectedRows()) {
			/* Ignore locked rows */
			if ((myRow == null) || (myRow.isLocked())) continue;
			
			/* If the row is Deleted */
			if (myRow.isDeleted()) {
				/* Note if we can recover a row */
				if (theTable.isValidHistory(myRow, myRow.getCurrentValues()))
					enableRecov	= true;
				
				/* Don't allow switching off of showDeleted */
				enableShow  = false;
			}
			
			/* else row is not deleted */
			else {
				/* If we can delete this element */
				if ((!theTable.needsMembers()) || 
					(theTable.getList().sizeNormal() > 1))
					enableDel	= true;
				
				/* Say that we can duplicate the item */
				enableDupl = true;
			}
		}			
		
		/* If we can duplicate a row */
		if (enableDupl) {
			/* Add the duplicate items choice */
			myItem = new JMenuItem(popupDuplItems);
			myItem.setActionCommand(popupDuplItems);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}
		
		/* If we can delete a row */
		if (enableDel) {
			/* Add the delete items choice */
			myItem = new JMenuItem(popupDeleteItems);
			myItem.setActionCommand(popupDeleteItems);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}
		
		/* If we can recover a row */
		if (enableRecov) {
			/* Add the delete items choice */
			myItem = new JMenuItem(popupRecoverItems);
			myItem.setActionCommand(popupRecoverItems);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}
		
		/* If we can change the show deleted indication */
		if (enableShow) {
			/* Add the CheckBox items choice */
			myCheckBox = new JCheckBoxMenuItem(popupShowDeleted);
			myCheckBox.setSelected(doShowDeleted);
			myCheckBox.setActionCommand(popupShowDeleted);
			myCheckBox.addActionListener(this);
			pMenu.add(myCheckBox);			
		}
	}

	/**
	 * Add Edit commands to menu
	 * Should be overridden if edit is not required  
	 * @param pMenu the menu to add to
	 */
	protected void addEditCommands(JPopupMenu pMenu) {
		JMenuItem 			myItem;
		boolean				rowSelected = false;
		boolean				enableUndo	= false;
		boolean				enableReset	= false;
		boolean				enableValid	= false;
		boolean				enablePrev	= false;
		boolean				enableNext	= false;
		
		/* Nothing to do if the table is locked */
		if (theTable.isLocked()) return;
		
		/* Loop through the selected rows */
		for (T myRow : theTable.cacheSelectedRows()) {
			if ((myRow == null) || (myRow.isLocked())) continue;
			
			/* Ignore deleted rows */
			if (myRow.isDeleted()) continue;
			
			/* If the row has Changes */
			if (myRow.hasHistory()) {
				/* Note that we can  reset */
				enableReset = true;
				
				/* Enable validate if required */
			  	if (myRow.getEditState() != EditState.VALID)
			  		enableValid  = true;
			}
			
			/* If this is a second (or later selection) */
			if (rowSelected) {
				/* Disable Undo/Next/Previous */
				enableUndo = false;
				enableNext = false;
				enablePrev = false;
			}
			
			/* else this is the first selection */
			else {
				/* Determine whether we can undo*/
				if (myRow.hasHistory())	enableUndo = true;

				/* Determine whether the row has further history */
				if (myRow.hasFurther(theTable)) enableNext = true;
			
				/* Determine whether the row has previous history */
				if (myRow.hasPrevious()) enablePrev = true;
			
				/* Note that we have selected a row */
				rowSelected = true;
			}
		}			
		
		/* If there is something to add and there are already items in the menu */
		if ((enableUndo || enableReset || enableValid || enableNext || enablePrev) &&
		    (pMenu.getComponentCount() > 0)) {
			/* Add a separator */
			pMenu.addSeparator();
		}
		
		/* If we can undo changes */
		if (enableUndo) {
			/* Add the undo change choice */
			myItem = new JMenuItem(popupUndoChange);
			myItem.setActionCommand(popupUndoChange);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}
		
		/* If we can reset changes */
		if (enableReset) {
			/* Add the reset items choice */
			myItem = new JMenuItem(popupResetItems);
			myItem.setActionCommand(popupResetItems);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}
		
		/* If we can validate changes */
		if (enableValid) {
			/* Add the reset items choice */
			myItem = new JMenuItem(popupValidate);
			myItem.setActionCommand(popupValidate);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}

		/* If we can pick up further history */
		if (enableNext) {
			/* Add the reset items choice */
			myItem = new JMenuItem(popupNextHistory);
			myItem.setActionCommand(popupNextHistory);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}

		/* If we can pick up previous history */
		if (enablePrev) {
			/* Add the reset items choice */
			myItem = new JMenuItem(popupPrevHistory);
			myItem.setActionCommand(popupPrevHistory);
			myItem.addActionListener(this);
			pMenu.add(myItem);			
		}
	}

	/**
	 * Add Null commands to menu
	 * Should be overridden if null values are required  
	 * @param pMenu the menu to add to
	 */
	protected void addNullCommands(JPopupMenu pMenu) {}

	/**
	 * Add Special commands to menu
	 * Should be overridden if special commands are required  
	 * @param pMenu the menu to add to
	 */
	protected void addSpecialCommands(JPopupMenu pMenu) {}

	/**
	 * Add Navigation commands to menu
	 * Should be overridden if navigation commands are required  
	 * @param pMenu the menu to add to
	 */
	protected void addNavigationCommands(JPopupMenu pMenu) {}

	/**
	 * Set the specified column to null if non-null for selected rows
	 * @param col the column
	 */
	protected void setColumnToNull(int col) {
		AbstractTableModel	myModel;
		int					row;

		/* Access the table model */
		myModel = theTable.getTableModel();
		
		/* Loop through the selected rows */
		for (T myRow : theTable.cacheSelectedRows()) {
			/* Ignore locked rows */
			if ((myRow == null) || (myRow.isLocked())) continue;
			
			/* Ignore deleted rows */
			if (myRow.isDeleted()) continue;

			/* Determine row */
			row = myRow.indexOf();
			if (theTable.hasHeader()) row--;
			
			/* Ignore null rows */
			if (myModel.getValueAt(row, col) == null) continue;
			
			/* set the null value */
			myModel.setValueAt(null, row, col);
			myModel.fireTableCellUpdated(row, col);
		}
	}
	
	/**
	 * Perform actions for controls/pop-ups on this table
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {
		String myCmd = evt.getActionCommand();
		Object mySrc = evt.getSource();
		
		/* Cancel any editing */
		theTable.cancelEditing();
		
		/* If this is a generic insert item command */
		if (myCmd.equals(popupInsertItem)) {
			/* Insert a row into the table */
			theTable.insertRow(false);			
		}
		
		/* if this is an insert credit command */
		else if (myCmd.equals(popupInsertCredit)) {
			/* Insert a credit row into the table */
			theTable.insertRow(true);						
		}
		
		/* if this is an insert debit command */
		else if (myCmd.equals(popupInsertDebit)) {
			/* Insert a debit row into the table */
			theTable.insertRow(false);						
		}
		
		/* if this is a duplicate command */
		else if (myCmd.equals(popupDuplItems)) {
			/* Duplicate selected items */
			theTable.duplicateRows();						
		}
		
		/* if this is a delete items command */
		else if (myCmd.equals(popupDeleteItems)) {
			/* Delete selected rows */
			theTable.deleteRows();									
		}
		
		/* if this is a recover items command */
		else if (myCmd.equals(popupRecoverItems)) {
			/* Recover selected rows */
			theTable.recoverRows();
		}
		
		/* if this is a show deleted command */
		else if (myCmd.equals(popupShowDeleted)) {
			/* Note the new criteria */
			doShowDeleted = ((JCheckBoxMenuItem)mySrc).isSelected();
			
			/* Notify the table */
			theTable.setShowDeleted(doShowDeleted);
		}
		
		/* if this is a reset changes command */
		else if (myCmd.equals(popupResetItems)) {
			/* Reset selected rows */
			theTable.resetRows();						
		}
		
		/* if this is a validate items command */
		else if (myCmd.equals(popupValidate)) {
			/* Validate selected rows */
			theTable.validateRows();			
		}

		/* if this is an undo change command */
		else if (myCmd.equals(popupUndoChange)) {
			/* Undo selected rows */
			theTable.unDoRows();						
		}
		
		/* if this is a next history command */
		else if (myCmd.equals(popupNextHistory)) {
			/* Restore previous history rows */
			theTable.restoreNextHistoryRows();									
		}
		
		/* if this is a previous history command */
		else if (myCmd.equals(popupPrevHistory)) {
			/* Restore previous history rows */
			theTable.restorePrevHistoryRows();			
		}
		
		/* Notify of any changes */
		theTable.notifyChanges();
		theTable.updateDebug();
	}
}
