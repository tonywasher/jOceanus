package uk.co.tolcroft.finance.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;

import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

public abstract class FinanceTableModel<T extends DataItem> extends JTable
															implements financeTable {
	/* Members */
	private static final long serialVersionUID 	= 5175290992863678655L;
	private AbstractTableModel  theModel     	= null;
	private DataList<T>	        theList	  		= null;
	private boolean             doShowDel    	= false;
	private boolean             isEnabled    	= false;
	private Font		      	theStdFont    	= null;
	private Font		  		theNumFont    	= null;
	private Font		      	theChgFont	    = null;
	private Font		      	theChgNumFont	= null;
	private MainTab				theMainTab		= null;

	/* Access methods */
	public AbstractTableModel getTableModel() { return theModel; }
	
	public boolean hasCreditChoice() 	{ return false; }
	public boolean needsMembers()		{ return false; }
	public boolean hasUpdates() 		{ return (theList != null) &&
												 theList.hasUpdates(); }
	public boolean hasErrors()  		{ return (theList != null) &&
												 theList.hasErrors(); }
	public boolean isActive()			{ return isEnabled; }
	public void    notifySelection(Object obj)    { }
	public boolean calculateTable()  	{ return false; }
		
	public void    setActive(boolean isActive)	{ isEnabled = isActive; }
	public JComboBox getComboBox(int row, int col) { return null; }
	public boolean isValidObj(DataItem pItem, DataItem.histObject  pObj) { return true; }
	public DataList<T> getList() { return theList; }
	public void 	printIt()			{ }
	public DebugManager getDebugManager() { return theMainTab.getDebugMgr(); }
		
	/* Abstract methods */
	public abstract int  	getFieldForCol(int iField);
	public abstract void 	notifyChanges();
	public abstract void 	saveData();
	public abstract boolean hasHeader();
		
	/* Constructor */
	FinanceTableModel(MainTab pMainTab) {
		theMainTab 		= pMainTab;
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		theStdFont 		= pMainTab.getFont(false, false);
		theChgFont 		= pMainTab.getFont(false, true);
		theNumFont 		= pMainTab.getFont(true, false);
		theChgNumFont 	= pMainTab.getFont(true, true);
	}
	
	/* Set the model */
	public void setModel(AbstractTableModel pModel) {
		super.setModel(pModel);
		theModel = pModel;
	}
	
	/* Get the Edit State */
	public EditState getEditState() {
		if (theList == null) return EditState.CLEAN;
		return theList.getEditState();
	}
		
	/* Set List */
	public void setList(DataList<T> pList) {
		/* Store list and select correct mode */
		theList = pList;
		if (pList != null) pList.setShowDeleted(doShowDel);
			
		/* If we have elements then set the selection */
		clearSelection();
		if (theModel.getRowCount() > 0)
			setRowSelectionInterval(0, 0);
		
		/* Redraw the table */
		theModel.fireTableDataChanged();
	}
				
	/* is this table locked */
	public boolean isLocked() {
		/* Store list and select correct mode */
		return ((theList == null) || theList.isLocked());
	}
				
	/* Extract Item */
	public T extractItemAt(int uIndex) {
		return((theList == null) ? null
								 : theList.get(uIndex));
	}
		
	/* Change showDeleted */
	public void setShowDeleted(boolean doShowDel) {
		T	myRow = null;
		int	row;

		/* If we are changing the value */
		if (this.doShowDel != doShowDel) {
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
				
			/* Determine the selected row */
			row = getSelectedRow();
			if ((row >= 1) && (hasHeader())) row--;
			if (row >= 0) myRow = theList.get(row);
				
			/* Store the new status */
			this.doShowDel = doShowDel;
			theList.setShowDeleted(doShowDel);
				
			/* Redraw the table */
			theModel.fireTableDataChanged();
				
			/* Reselect the row */
			if (row >= 0) {
				if (myRow != null) row = theList.indexOf(myRow);
				if (hasHeader()) row++;
				selectRow(row);
			}
		}
	}
				
	/* Get render data for row */
	public void getRenderData(RenderData pData) {
		T			myRow;
		String     	myTip = null;
		int			iRow;
		int         iField;
		Color		myFore;
		Color		myBack;
		Font		myFont;
		boolean 	isChanged = false;
		
		/* If we have a header decrement the row */
		iRow = pData.getRow();
		if (hasHeader()) iRow--;
		
		/* Default is black on white */
		myBack = Color.white;
		myFore = Color.black;

		/* If this is a data row */
		if (iRow >= 0) {
			/* Access the row */
			myRow  = theList.get(iRow);
			iField = getFieldForCol(pData.getCol());
			
			/* Has the field changed */
			isChanged = myRow.fieldChanged(iField);
			
			/* Determine the colour */
			if (myRow.isDeleted()) {
				myFore = Color.lightGray;
			}
			else if ((myRow.hasErrors()) &&
					 (myRow.hasErrors(iField))) {
				myFore = Color.red;
				myTip = myRow.getFieldError(iField);
			}
			else if (isChanged)
				myFore = Color.magenta;
			else if (myRow.getState() == DataState.NEW)
				myFore = Color.blue;
			else if (myRow.getState() == DataState.RECOVERED)
				myFore = Color.darkGray;
		}
			
		/* For selected items flip the foreground/background */
		if (pData.isSelected()){
			Color myTemp = myFore;
			myFore = myBack;
			myBack = myTemp;
		}
			
		/* Select the font */
		if (pData.isFixed())
			myFont = isChanged	? theChgNumFont 
								: theNumFont;
		else
			myFont = isChanged	? theChgFont 
								: theStdFont;
		
		/* Set the data */
		pData.setData(myFore, myBack, myFont, myTip);
			
		/* return the data */
		return;
	}
		
	/* Select a row and ensure that it is visible */
	private void selectRowWithScroll(int row) {		
		Rectangle 	rect;
		Point		pt;
		JViewport	viewport;
		  
		/* Shift display to line */
		rect = getCellRect(row, 0, true);
		viewport = (JViewport)getParent();
		pt = viewport.getViewPosition();
		rect.setLocation(rect.x - pt.x, rect.y - pt.y);
		viewport.scrollRectToVisible(rect);
		
		/* clear existing selection and select the row */
		selectRow(row);
	}
		
	/* Select a row */
	protected void selectRow(int row) {		
		/* clear existing selection and select the row */
		clearSelection();
		changeSelection(row, 0, false, false);
		requestFocusInWindow();
	}
		
	/* Insert an item */
	public void insertRow(boolean isCredit) {		
		int 		row;
		  
		/* Determine the row number */
		row = theList.size();
		
		/* Create the new Item */
		theList.addNewItem(isCredit);
		
		/* If we have a header increment the row */
		if (hasHeader()) row++;
		
		/* Notify of the insertion of the column */
		theModel.fireTableRowsInserted(row, row);
			
		/* Shift display to line */
		selectRowWithScroll(row);
	}
		
	/* Delete the item at row */
	public boolean deleteRow(int row) {		
		T	myRow;
		int numRows = theList.size();
		int	numHdrs = (hasHeader() ? 1 : 0);
		
		/* Access the row */
		myRow = theList.get(row);
			
		/* If the row is not deleted */
		if (!myRow.isDeleted()) {
			/* If we have a header increment the row */
			if (hasHeader()) { row++; numRows++; }
		
			/* Mark the row as deleted */
			myRow.setState(DataState.DELETED);
			
			/* If we are recalculating the table */
			if (calculateTable()) 
				theModel.fireTableDataChanged();
			
			/* If we are showing deleted items */
			else if (theList.getShowDeleted()) {
				/* Re-draw the row */
				for (int i=0; i<theModel.getColumnCount(); i++)
					theModel.fireTableCellUpdated(row, i);
					
				/* Reselect the row and notify changes */
				selectRow(row);
				return false;
			}
			
			/* Notify of the deletion of the row */
			else theModel.fireTableRowsDeleted(row, row);
				
			/* If we are the final row shift back one*/
			if (row == numRows-1) row--;
			if (row < numHdrs) row++;
			selectRow(row);
			return true;
		}
			
		/* Say the we have not removed this item from visible */
		return false;
	}
		
	/* Recover the item at row */
	public void recoverRow(int row) {		
		T	myRow;
		
		/* Access the row */
		myRow = theList.get(row);
		
		/* If the row is deleted */
		if (myRow.isDeleted()) {
			/* If we have a header increment the row */
			if (hasHeader()) row++;
		
			/* Mark the row as recovered */
			myRow.setState(DataState.RECOVERED);
			myRow.clearErrors();
			myRow.validate();
			
			/* If we are recalculating the table */
			if (calculateTable()) 
				theModel.fireTableDataChanged();
							
			/* Re-draw the row */
			else for (int i=0; i<theModel.getColumnCount(); i++)
				theModel.fireTableCellUpdated(row, i);
			
			/* Reselect the row and notify changes */
			selectRow(row);
		}
	}
		
	/* Validate the item at row */
	public void validateRow(int row) {		
		T	myRow;
			
		/* Access the row */
		myRow = theList.get(row);
		
		/* If we have a header increment the row */
		if (hasHeader()) row++;
			
		/* Clear errors and re-validate */
		myRow.clearErrors();
		myRow.validate();
		
		/* Re-draw the row */
		for (int i=0; i<theModel.getColumnCount(); i++)
			theModel.fireTableCellUpdated(row, i);
			
		/* Reselect the row and notify changes */
		selectRow(row);
	}
		
	/* Reset the item at row */
	public void resetRow(int row) {		
		T	myRow;
		
		/* Access the row */
		myRow = theList.get(row);
		
		/* Clear errors and re-validate */
		myRow.clearErrors();
		myRow.resetHistory();
		myRow.validate();
			
		/* Mark the row as clean */
		myRow.setState(myRow.isCoreDeleted()
				? DataState.RECOVERED
				: DataState.CLEAN);
		calculateTable();
		
		/* Determine new row number */
		row = theList.indexOf(myRow);
		
		/* Re-draw the row */
		theModel.fireTableDataChanged();
		
		/* Reselect the row and notify changes */
		if (hasHeader()) row++;
		selectRow(row);
	}
		
	/* Validate all the items */
	public void validateAll() {		
		/* Validate the list */
		theList.validate();
		theList.findEditState();
			
		/* Re-draw the table */
		theModel.fireTableDataChanged();
	}
		
	/* Undo changes to a row */
	public void unDoRow(int row) {		
		T	myRow;
			
		/* Access the row */
		myRow = theList.get(row);
		
		/* If the row has changes */
		if (myRow.hasHistory()) {
			/* Pop last value */
			myRow.popHistory();
			
			/* Resort the item */
			myRow.reSort();
			myRow.clearErrors();
			myRow.validate();
			calculateTable();
			
			/* Determine new row number */
			row = theList.indexOf(myRow);
				
			/* If the item is now clean */
			if (!myRow.hasHistory()) {
				/* Set the new status */
				myRow.setState(myRow.isCoreDeleted()
								? DataState.RECOVERED
								: DataState.CLEAN);
			}
			
			/* Re-draw the table due to the changed data */
			theModel.fireTableDataChanged();
		}
			
		/* Reselect the row and notify changes */
		if (hasHeader()) row++;
		selectRow(row);
	}
		
	/* restore changes to a row */
	public void restoreNextRow(int row) {		
		T myRow;
			
		/* Access the row */
		myRow = theList.get(row);
			
		/* If the row has changes */
		if (myRow.hasFurther(this)) {
			/* Pop last value */
			myRow.peekFurther();
		
			/* Resort the item */
			myRow.reSort();
			myRow.clearErrors();
			myRow.validate();
			calculateTable();

			/* Determine new row number */
			row = theList.indexOf(myRow);
				
			/* Set the new status */
			myRow.setState(DataState.CHANGED);
							
			/* Re-draw the table due to the changed data */
			theModel.fireTableDataChanged();
		}
		
		/* Reselect the row and notify changes */
		if (hasHeader()) row++;
		selectRow(row);
	}
		
	/* restore changes to a row */
	public void restorePrevRow(int row) {		
		T myRow;
		
		/* Access the row */
		myRow = theList.get(row);
		
		/* If the row has changes */
		if (myRow.hasPrevious()) {
			/* Pop last value */
			myRow.peekPrevious();
		
			/* Resort the item */
			myRow.reSort();
			myRow.clearErrors();
			myRow.validate();
			calculateTable();
			
			/* Determine new row number */
			row = theList.indexOf(myRow);
			
			/* Set the new status */
			if (myRow.hasHistory())  myRow.setState(DataState.CHANGED);
								
			/* Re-draw the table due to the changed data */
			theModel.fireTableDataChanged();
		}
			
		/* Reselect the row and notify changes */
		if (hasHeader()) row++;
		selectRow(row);
	}
		
	/* resetData */
	public void resetData() {
		if (theList != null) { 
			theList.resetChanges();
			theList.findEditState();
		}
		calculateTable();
		theModel.fireTableDataChanged();
	}
		
	/* Perform command function */
	public void performCommand(financeCommand pCmd) {
		int rowsdel = 0;
			
		/* Cancel any editing */
		if (isEditing()) cellEditor.cancelCellEditing();
			
		/* Switch on command */
		switch (pCmd) {
			/* Handle the commands that operate on selection */
			case RECOVER:
			case DELETE:
			case UNDO:
			case NEXT:
			case PREV:
			case RESET:
			case VALIDATE:
				/* Loop through the selected rows */
				for (int row : getSelectedRows()) {
					/* If we have a header decrement the row */
					if (hasHeader()) row--;
					
					/* Switch on command */
					switch (pCmd) {
						case RECOVER:
							recoverRow(row);
							break;
						case DELETE:
							if (deleteRow(row-rowsdel))
								rowsdel++;
							break;
						case UNDO:
							unDoRow(row);
							break;
						case VALIDATE:
							validateRow(row);
							break;
						case RESET:
							resetRow(row);
							break;
						case NEXT:
							restoreNextRow(row);
							break;
						case PREV:
							restorePrevRow(row);
							break;
					}
				}
				break;
			case OK:
				saveData();
				break;
			case RESETALL:
				resetData();
				break;
			case VALIDATEALL:
				validateAll();
				break;
			case INSERTCR:
				insertRow(true);
				break;
			case INSERTDB:
				insertRow(false);
				break;
		}
	}
		
	/* Perform command function */
	public void cancelEditing() {		
		/* Cancel any editing */
		if (isEditing()) cellEditor.cancelCellEditing();
	}
	
	/* valueChanged listener event */
	public void valueChanged(ListSelectionEvent evt) {
		super.valueChanged(evt);
        if (evt.getValueIsAdjusting()) {
            return;
        }
        notifyChanges();
	}
}
