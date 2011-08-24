package uk.co.tolcroft.finance.ui.controls;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseListener;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import uk.co.tolcroft.finance.ui.MainTab;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.help.DebugManager;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.data.HistoryCheck;
import uk.co.tolcroft.models.data.HistoryValues;

public abstract class FinanceTable<T extends DataItem<T>> extends JTable 
														  implements financePanel,
																  	 HistoryCheck<T> {
	/* Members */
	private static final long serialVersionUID = 1258025191244933784L;
	private JTable				theRowHdrTable	= null;
	private DataTableModel  	theModel     	= null;
	private rowTableModel		theRowHdrModel  = null;
	private DataList<T>	        theList	  		= null;
	private JScrollPane			theScroll		= null;
	private boolean             doShowDel    	= false;
	private boolean             isEnabled    	= false;
	private Font		      	theStdFont    	= null;
	private Font		  		theNumFont    	= null;
	private Font		      	theChgFont	    = null;
	private Font		      	theChgNumFont	= null;
	private MainTab				theMainTab		= null;

	/* Access methods */
	public boolean 	hasHeader() 		{ return false; }
	public boolean 	hasCreditChoice() 	{ return false; }
	public boolean 	needsMembers()		{ return false; }
	public boolean 	hasUpdates() 		{ return (theList != null) &&
												 theList.hasUpdates(); }
	public boolean 	hasErrors()  		{ return (theList != null) &&
												 theList.hasErrors(); }
	public boolean 	isActive()			{ return isEnabled; }
	public boolean 	calculateTable()  	{ return false; }
	public void    	printIt()			{ }
		
	public AbstractTableModel getTableModel() 			{ return theModel; }

	public DataList<T> 	getList() 						{ return theList; }
	public JScrollPane 	getScrollPane()					{ return theScroll; }
	public void    		notifySelection(Object obj)    	{ }
	public void    		updateDebug()			    	{ }
	public void    		setActive(boolean isActive)		{ isEnabled = isActive; }
	public JComboBox 	getComboBox(int row, int col) 	{ return null; }
	public boolean 		isValidHistory(DataItem<T> pItem, HistoryValues<?>  pValues) { return true; }
	public DebugManager getDebugManager() 				{ return theMainTab.getDebugMgr(); }
		
	/* Abstract methods */
	public abstract void 	notifyChanges();
	public abstract void 	saveData();

	/**
	 * Constructor
	 * @param pMainTab the main window
	 */
	public FinanceTable(MainTab pMainTab) {
		/* Store parameters */
		theMainTab 		= pMainTab;
		theRowHdrModel  = new rowTableModel();
		
		/* Set the selection mode */
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		/* Access the standard fonts */
		theStdFont 		= pMainTab.getFont(false, false);
		theChgFont 		= pMainTab.getFont(false, true);
		theNumFont 		= pMainTab.getFont(true, false);
		theChgNumFont 	= pMainTab.getFont(true, true);
	}
	
	/**
	 * Set the table model
	 * @param pModel the table model
	 */
	public void setModel(DataTableModel pModel) {
		/* Declare to the super class */
		super.setModel(pModel);
		
		/* Record the model */
		theModel = pModel;

		/* Create a row Header table */
		theRowHdrTable = new JTable(theRowHdrModel, new rowColumnModel());
		theRowHdrTable.setBackground(getTableHeader().getBackground());
		theRowHdrTable.setColumnSelectionAllowed(false);
		theRowHdrTable.setCellSelectionEnabled(false);
		
		/* Set the selection model */
		theRowHdrTable.setSelectionModel(getSelectionModel());
		
		/* Create a new Scroll Pane and add this table to it */
		theScroll     = new JScrollPane();
		theScroll.setViewportView(this);
		
		/* Add as the row header */
		theScroll.setRowHeaderView(theRowHdrTable);
		theScroll.getRowHeader().setPreferredSize(new Dimension(30, 200));
		theScroll.setCorner(ScrollPaneConstants.UPPER_LEFT_CORNER, 
							theRowHdrTable.getTableHeader());
	}
	
	/**
	 * Set the table model
	 * @param pModel the table model
	 */
	public void addMouseListener(MouseListener pListener) {
		/* Pass call on */
		super.addMouseListener(pListener);
		
		/* Listen for the row header table as well */
		if (theRowHdrTable != null)
			theRowHdrTable.addMouseListener(pListener);
	}
	
	/**
	 * Get the Edit State 
	 * @return the edit state
	 */
	public EditState getEditState() {
		if (theList == null) return EditState.CLEAN;
		return theList.getEditState();
	}
		
	/**
	 * Set the list for the table
	 * @param pList the list
	 */
	public void setList(DataList<T> pList) {
		int myZeroRow = hasHeader() ? 1 : 0;
		
		/* Store list and select correct mode */
		theList = pList;
		if (pList != null) pList.setShowDeleted(doShowDel);
		updateDebug();
			
		/* Redraw the table and row headers */
		theModel.fireNewDataEvents();

		/* If we have elements then set the selection to the first item */
		clearSelection();
		if (theModel.getRowCount() > myZeroRow) 
			selectRowWithScroll(myZeroRow);		
	}
				
	/**
	 * Is the table locked
	 * @return true/false
	 */
	public boolean isLocked() {
		/* Store list and select correct mode */
		return ((theList == null) || theList.isLocked());
	}
				
	/**
	 * Extract the item at the given index
	 * @param uIndex the index
	 * @return the item
	 */
	public T extractItemAt(int uIndex) {
		return((theList == null) ? null
								 : theList.get(uIndex));
	}

	/**
	 * reset the data
	 */
	public void resetData() {
		/* If we have a list */
		if (theList != null) { 
			/* Reset all changes */
			theList.resetChanges();
			
			/* Recalculate edit state */
			theList.findEditState();
		}
		
		/* Recalculate the table if required */
		calculateTable();
		updateDebug();
		
		/* Notify that the entire table has changed */
		theModel.fireNewDataEvents();
	}
		
	/**
	 * Validate all the items
	 */
	public void validateAll() {		
		/* Validate the list */
		theList.validate();
		theList.findEditState();
		updateDebug();
			
		/* Re-draw the table */
		theModel.fireNewDataEvents();
	}
		
	/**
	 * Cancel any editing that is occurring
	 */
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
	
	/**
	 * Select a row and ensure that it is visible
	 * @param row the row to select
	 */
	protected void selectRowWithScroll(int row) {		
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
		
	/**
	 * select an explicit row
	 * @param row the row to select
	 */
	protected void selectRow(int row) {		
		/* clear existing selection and select the row */
		clearSelection();
		changeSelection(row, 0, false, false);
		requestFocusInWindow();
	}
		
	/**
	 * Get an array of the selected rows
	 * @return array of selected rows 
	 */
	@SuppressWarnings("unchecked")
	protected T[] cacheSelectedRows() {
		T[]		myRows;
		int[]	mySelected;
		int		myIndex;
		int		i,j;

		/* Determine the selected rows */
		mySelected = getSelectedRows();
	
		/* Create a row array relating to the selections */
		myRows = (T[])new DataItem[mySelected.length];
		Arrays.fill(myRows, null);
	
		/* Loop through the selection indices */
		for (i=0, j=0; i<mySelected.length; i++) {
			/* Access the index and adjust for header */
			myIndex = mySelected[i];
			if (hasHeader()) myIndex--;
			if (myIndex < 0) continue;
		
			/* Store the row */
			myRows[j] = theList.get(myIndex);
			j++;
		}
		
		/* Return the rows */
		return myRows;
	}
	
	
	/**
	 * Set the show deleted indication
	 * @param doShowDel the new setting
	 */
	protected void setShowDeleted(boolean doShowDel) {
		T[]	myRows;
		int	myRowNo;

		/* If we are changing the value */
		if (this.doShowDel != doShowDel) {
			/* Cancel any editing */
			if (isEditing()) cellEditor.cancelCellEditing();
				
			/* Access a cache of the selected rows */
			myRows = cacheSelectedRows();
			clearSelection();
			
			/* Store the new status */
			this.doShowDel = doShowDel;
			theList.setShowDeleted(doShowDel);
				
			/* Redraw the table */
			theModel.fireNewDataEvents();
				
			/* Loop through the selected rows */
			for (T myRow : myRows) {
				/* Ignore null/deleted entries */
				if ((myRow == null) || (myRow.isDeleted())) continue;				

				/* Access the row # and adjust for header */
				myRowNo = myRow.indexOf();
				if (hasHeader()) myRowNo++;
				
				/* Select the row */
				addRowSelectionInterval(myRowNo, myRowNo);
			}
		}
	}
	
	/**
	 * Insert an item 
	 * @param isCredit is this a credit item
	 */
	protected void insertRow(boolean isCredit) {		
		int 	myRowNo;
		T		myItem;
		  
		/* Create the new Item */
		myItem = theList.addNewItem(isCredit);
		
		/* Determine the row # allowing for header */
		myRowNo = myItem.indexOf();
		if (hasHeader()) myRowNo++;

		/* Validate the new item */
		myItem.validate();
		
		/* Notify of the insertion of the row */
		theModel.fireInsertRowEvents(myRowNo);
			
		/* Shift display to line */
		selectRowWithScroll(myRowNo);
	}
	
	/**
	 * Delete the selected items
	 */
	protected void deleteRows() {
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null/deleted entries */
			if ((myRow == null) || (myRow.isDeleted())) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;
			
			/* Mark the row as deleted */
			myRow.setState(DataState.DELETED);

			/* If we are showing deleted items */
			if (doShowDel) {
				/* Notify of the update of the row */
				theModel.fireUpdateRowEvents(myRowNo);
			}
			
			/* else we are not showing deleted items */
			else {
				/* Notify of the deletion of the row and remove from list */
				myRows[i] = null;
				theModel.fireDeleteRowEvents(myRowNo);
			}
		}
			
		/* Recalculate the table if required */
		calculateTable();
	}
		
	/**
	 * Duplicate the selected items
	 */
	protected void duplicateRows() {
		T[]	myRows;
		T	myRow;
		T	myItem;
		int	myRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null/deleted entries */
			if ((myRow == null) || (myRow.isDeleted())) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;

			/* Create the new Item */
			myItem = theList.addNewItem(myRow);
			
			/* Determine the row # allowing for header */
			myRowNo = myItem.indexOf();
			if (hasHeader()) myRowNo++;

			/* Validate the new item */
			myItem.validate();
			
			/* Notify of the insertion of the row */
			theModel.fireInsertRowEvents(myRowNo);				
		}
			
		/* Recalculate the table if required */
		calculateTable();
	}
		
	/**
	 * Recover the selected items
	 */
	protected void recoverRows() {		
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null/non-deleted entries */
			if ((myRow == null) || (!myRow.isDeleted())) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;
			
			/* Mark the row as recovered */
			myRow.setState(DataState.RECOVERED);
			myRow.clearErrors();
			myRow.validate();
			
			/* Notify of the update of the row */
			theModel.fireUpdateRowEvents(myRowNo);
		}
					
		/* Recalculate the table if required */
		calculateTable();
	}
	
	/**
	 * Validate the selected items
	 */
	protected void validateRows() {		
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null/deleted entries */
			if ((myRow == null) || (myRow.isDeleted())) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;

			/* Clear errors and re-validate */
			myRow.clearErrors();
			myRow.validate();
						
			/* Notify of the update of the row */
			theModel.fireUpdateRowEvents(myRowNo);
		}
	}
		
	/**
	 * Reset the selected rows
	 */
	protected void resetRows() {		
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		int	myNewRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null entries */
			if (myRow == null) continue;				

			/* Access the row # adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;

			/* Mark the row as clean */
			myRow.setState(myRow.isCoreDeleted()
					? DataState.RECOVERED
					: DataState.CLEAN);

			/* Clear errors and re-validate */
			myRow.clearErrors();
			myRow.resetHistory();	
			myRow.validate();
			
			/* Determine new row # */
			myNewRowNo = myRow.indexOf();
			if (hasHeader()) myNewRowNo++;
			
			/* If the row # has changed */
			if (myRowNo != myNewRowNo) {
				/* Report the deletion and insertion */
				theModel.fireMoveRowEvents(myRowNo, myNewRowNo);					
				addRowSelectionInterval(myNewRowNo, myNewRowNo);
			}
			
			/* else the row has just been updated */
			else {
				/* Report the update */
				theModel.fireUpdateRowEvents(myRowNo);
			}
		}

		/* Recalculate the table if required */
		calculateTable();
	}
	
	/**
	 * Undo changes to rows
	 */
	protected void unDoRows() {		
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		int	myNewRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null entries */
			if (myRow == null) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;

			/* If the row has changes */
			if (myRow.hasHistory()) {
				/* Pop last value */
				myRow.popHistory();
			
				/* Resort the item */
				theList.reSort(myRow);
				myRow.clearErrors();
				myRow.validate();
			
				/* If the item is now clean */
				if (!myRow.hasHistory()) {
					/* Set the new status */
					myRow.setState(myRow.isCoreDeleted()
									? DataState.RECOVERED
									: DataState.CLEAN);
				}
			
				/* Determine new row # */
				myNewRowNo = myRow.indexOf();
				if (hasHeader()) myNewRowNo++;
				
				/* If the row # has changed */
				if (myRowNo != myNewRowNo) {
					/* Report the deletion and insertion */
					theModel.fireMoveRowEvents(myRowNo, myNewRowNo);					
					addRowSelectionInterval(myNewRowNo, myNewRowNo);
				}
				
				/* else the row has just been updated */
				else {
					/* Report the update */
					theModel.fireUpdateRowEvents(myRowNo);
				}
			}
		}
			
		/* Recalculate the table if required */
		calculateTable();
	}
		
	/**
	 *  Restore next history row
	 */
	public void restoreNextHistoryRows() {		
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		int	myNewRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null entries */
			if (myRow == null) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;

			/* If the row has changes */
			if (myRow.hasFurther(this)) {
				/* Pop last value */
				myRow.peekFurther();
		
				/* Set the new status */
				myRow.setState(DataState.CHANGED);
								
				/* Resort the item */
				theList.reSort(myRow);
				myRow.clearErrors();
				myRow.validate();

				/* Determine new row # */
				myNewRowNo = myRow.indexOf();
				if (hasHeader()) myNewRowNo++;
				
				/* If the row # has changed */
				if (myRowNo != myNewRowNo) {
					/* Report the deletion and insertion */
					theModel.fireMoveRowEvents(myRowNo, myNewRowNo);					
					addRowSelectionInterval(myNewRowNo, myNewRowNo);
				}
				
				/* else the row has just been updated */
				else {
					/* Report the update */
					theModel.fireUpdateRowEvents(myRowNo);
				}
			}
		}

		/* Recalculate the table if required */
		calculateTable();
	}
		
	/**
	 * Restore a previous history row
	 */
	public void restorePrevHistoryRows() {		
		T[]	myRows;
		T	myRow;
		int	myRowNo;
		int	myNewRowNo;
		
		/* Access the selected rows */
		myRows = cacheSelectedRows();
					
		/* Loop through the selected rows */
		for (int i=0; i<myRows.length; i++) {
			/* Access the row */
			myRow = myRows[i];
			
			/* Ignore null entries */
			if (myRow == null) continue;				

			/* Access the row # and adjust for header */
			myRowNo = myRow.indexOf();
			if (hasHeader()) myRowNo++;

			/* If the row has changes */
			if (myRow.hasPrevious()) {
				/* Pop last value */
				myRow.peekPrevious();
		
				/* Resort the item */
				theList.reSort(myRow);
				myRow.clearErrors();

				/* Set the new status */
				if (myRow.hasHistory()) {
					myRow.setState(DataState.CHANGED);
				}
				myRow.validate();

				/* Determine new row # */
				myNewRowNo = myRow.indexOf();
				if (hasHeader()) myNewRowNo++;
				
				/* If the row # has changed */
				if (myRowNo != myNewRowNo) {
					/* Report the deletion and insertion */
					theModel.fireMoveRowEvents(myRowNo, myNewRowNo);					
					addRowSelectionInterval(myNewRowNo, myNewRowNo);
				}
				
				/* else the row has just been updated */
				else {
					/* Report the update */
					theModel.fireUpdateRowEvents(myRowNo);
				}
			}				
		}

		/* Recalculate the table if required */
		calculateTable();
	}		

	/**
	 * Perform command function
	 */
	public void performCommand(financeCommand pCmd) {
			
		/* Cancel any editing */
		if (isEditing()) cellEditor.cancelCellEditing();
			
		/* Switch on command */
		switch (pCmd) {
			case OK:
				saveData();
				break;
			case RESETALL:
				resetData();
				break;
		}
		
		/* Notify changes */
		notifyChanges();
	}
	
	/**
	 * Row Table model class
	 */
	public class rowTableModel extends AbstractTableModel {
		private static final long serialVersionUID = -7172213268168894124L;

		/* Table headers */
		private static final String titleRow	= "Row";

		/**
		 * Get the number of display columns
		 * @return the columns
		 */
		public int getColumnCount() { return 1; }
		
		/**
		 * Get the number of rows in the current table
		 * @return the number of rows
		 */
		public int getRowCount() { return theModel.getRowCount(); }		

		/**
		 * Get the name of the column
		 * @param col the column
		 * @return the name of the column
		 */
		public String getColumnName(int col) {	return titleRow; }

		/**
		 * Get the object class of the column
		 * @param col the column
		 * @return the class of the objects associated with the column
		 */
		public Class<?> getColumnClass(int col) { return Integer.class; }

		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) { return hasHeader() ? row : row+1; }
		
		/**
		 * Get render data for row
		 * @param pData the Render details
		 */
		@SuppressWarnings("unchecked")
		public void getRenderData(RenderData pData) {
			T				myRow;
			String     		myTip = null;
			int				iRow;
			int				myIndex;
			int[]       	iFields;
			Color			myFore;
			Color			myBack;
			Font			myFont;
			DataColumnModel myColModel;
			boolean 		isChanged = false;
			
			/* If we have a header decrement the index */
			iRow = pData.getRow();
			myIndex = iRow;
			if (hasHeader()) myIndex--;
			
			/* Obtain defaults from table header */
			JTableHeader myHeader = getTableHeader();
			myBack = myHeader.getBackground();
			myFore = myHeader.getForeground();
			myFont = myHeader.getFont();

			/* If this is a data row */
			if (myIndex >= 0) {
				/* Access the row */
				myRow   	= theList.get(myIndex);
				myColModel 	= (DataColumnModel)getColumnModel();
				iFields 	= myColModel.getColumnFields();
				
				/* Has the row changed */
				isChanged = myRow.hasHistory();
				
				/* Determine the colour */
				if (myRow.isDeleted()) {
					myFore = Color.lightGray;
				}
				else if (myRow.hasErrors()) {
					myFore = Color.black;
					myBack = Color.red;
					myTip  = myRow.getFieldErrors(iFields);
				}
				else if (isChanged)
					myFore = Color.magenta;
				else if (myRow.getState() == DataState.NEW)
					myFore = Color.blue;
				else if (myRow.getState() == DataState.RECOVERED)
					myFore = Color.darkGray;
			}
				
			/* Set the data */
			pData.setData(myFore, myBack, myFont, myTip);
				
			/* return to the caller */
			return;
		}		
	}
	
	/**
	 * Data Table model class
	 */
	protected abstract class DataTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 3815818983288519203L;

		/* Abstract methods */
		public abstract int  	getFieldForCell(int row, int col);

		/**
		 * fire events for moving of a row
		 * @param pFromRow the original row
		 * @param pToRow the new row 
		 */
		protected void fireMoveRowEvents(int pFromRow, int pToRow) {
			/* Report the deletion and insertion */
			theModel.fireTableRowsDeleted(pFromRow, pFromRow);					
			theModel.fireTableRowsInserted(pToRow, pToRow);					

			/* If To Row is earlier */
			if (pToRow > pFromRow) {
				/* Report the change of headers in the region */
				theRowHdrModel.fireTableRowsUpdated(pFromRow, pToRow);
			}
			
			/* else from row is earlier */
			else {
				/* Report the change of headers in the region */
				theRowHdrModel.fireTableRowsUpdated(pToRow, pFromRow);
			}
			
		}

		/**
		 * fire events for insertion of a row
		 * @param pNewRow the inserted row
		 */
		protected void fireInsertRowEvents(int pNewRow) {
			/* Note that we have an inserted row */
			theModel.fireTableRowsInserted(pNewRow, pNewRow);
			theRowHdrModel.fireTableRowsInserted(pNewRow, pNewRow);
			
			/* Access the row count */
			int iNumRows = theModel.getRowCount();
			
			/* If we have rows subsequent to the inserted row */
			if (iNumRows > pNewRow+1) {
				/* Note that we need to rebuild subsequent rows in row header */ 
				theRowHdrModel.fireTableRowsUpdated(pNewRow+1, iNumRows-1);
			}
		}

		/**
		 * fire events for deletion of a row
		 * @param pOldRow the deleted row
		 */
		protected void fireDeleteRowEvents(int pOldRow) {
			/* Note that we have an deleted row */
			theModel.fireTableRowsDeleted(pOldRow, pOldRow);
			theRowHdrModel.fireTableRowsInserted(pOldRow, pOldRow);
			
			/* Access the row count */
			int iNumRows = theModel.getRowCount();
			
			/* If we have rows subsequent to the deleted row */
			if (iNumRows > pOldRow) {
				/* Note that we need to rebuild subsequent rows in row header */ 
				theRowHdrModel.fireTableRowsUpdated(pOldRow, iNumRows-1);
			}			
		}
 
		/**
		 * fire row updated events
		 * @param pRow the updated row
		 */
		protected void fireUpdateRowEvents(int pRow) {
			/* Note that the data for this row and header has changed */
			fireTableRowsUpdated(pRow, pRow);
			theRowHdrModel.fireTableRowsUpdated(pRow, pRow);	
		}
 
		/**
		 * fire events for new data view
		 */
		protected void fireNewDataEvents() {
			/* Note that the data for table and row header has changed */
			fireTableDataChanged();
			theRowHdrModel.fireTableDataChanged();
		}
		
		/**
		 * Get render data for row
		 * @param pData the Render details
		 */
		public void getRenderData(RenderData pData) {
			T			myRow;
			String     	myTip = null;
			int			iRow;
			int			myIndex;
			int         iField;
			Color		myFore;
			Color		myBack;
			Font		myFont;
			boolean 	isChanged = false;
			
			/* If we have a header decrement the index */
			iRow = pData.getRow();
			myIndex = iRow;
			if (hasHeader()) myIndex--;
			
			/* Default is black on white */
			myBack = Color.white;
			myFore = Color.black;

			/* If this is a data row */
			if (myIndex >= 0) {
				/* Access the row */
				myRow  = theList.get(myIndex);
				iField = getFieldForCell(iRow, pData.getCol());
				
				/* Has the field changed */
				isChanged = myRow.fieldChanged(iField);
				
				/* Determine the colour */
				if (myRow.isDeleted()) {
					myFore = Color.lightGray;
				}
				else if ((myRow.hasErrors()) &&
						 (myRow.hasErrors(iField))) {
					myFore = Color.red;
					myTip = myRow.getFieldErrors(iField);
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
				
			/* return to the caller */
			return;
		}		
	}
	
	/**
	 * TableColumn extension class
	 */
	protected class DataColumn extends TableColumn {
		private static final long serialVersionUID = 6117303771805259099L;

		/* Is the column currently in the model */
		private boolean isMember = false;
		
		/* Is the column a row header */
		private boolean isHeader = false;
		
		/* Access methods */
		public	boolean	isMember() 					{ return isMember; }
		public	void	setMember(boolean isMember) { this.isMember = isMember; }
		public	void	setHeader(boolean isHeader) { this.isHeader = isHeader; }
		
		/**
		 * Constructor
		 */
		public DataColumn(int modelIndex, 	int width, 
						  TableCellRenderer cellRenderer,
						  TableCellEditor   cellEditor) {
			/* Call super-constructor */
			super(modelIndex, width, cellRenderer, cellEditor);
		}

		/**
		 * Obtain Header name (necessary to rebuild the JTableHeader)
		 */
		public Object getHeaderValue() {
			/* Return the column name according to the model */
			return (isHeader) ? theRowHdrModel.getColumnName(getModelIndex())
							  : theModel.getColumnName(getModelIndex());
		}
	}
	
	/**
	 * Column Model class
	 */
	protected class DataColumnModel extends DefaultTableColumnModel {
		private static final long serialVersionUID = -5503203201580691221L;

		/**
		 * Add a column to the end of the model 
		 * @param pColumn
		 */
		protected void addColumn(DataColumn pColumn) {
			/* Set the range */
			super.addColumn(pColumn);
			pColumn.setMember(true);
		}

		/**
		 * Remove a column from the model 
		 * @param pColumn
		 */
		protected void removeColumn(DataColumn pColumn) {
			/* Set the range */
			super.removeColumn(pColumn);
			pColumn.setMember(false);
		}
		
		/**
		 * Access the array of displayed column indices
		 */
		protected int[] getColumnFields() {
			/* Declare the field array */
			int[] 		myFields = new int[getColumnCount()];
			int   		myCol;

			/* Loop through the columns */
			for(int i=0; i<myFields.length; i++){
				/* Access the column index for this column */
				myCol = getColumn(i).getModelIndex();
				
				/* Store the field # */
				myFields[i] = theModel.getFieldForCell(-1, myCol);			
			}
			
			/* return the fields */
			return myFields;
		}
	}
	
	/**
	 * Row Column Model class
	 */
	private class rowColumnModel extends DataColumnModel {
		private static final long serialVersionUID = -579928883936388389L;

		/* Renderers/Editors */
		private Renderer.RowCell	theRowRenderer  	= null;

		/**
		 * Constructor 
		 */
		private rowColumnModel() {		
			DataColumn myCol;
			
			/* Create the relevant formatters/editors */
			theRowRenderer  	= new Renderer.RowCell();

			/* Create the columns */
			addColumn(myCol = new DataColumn(0, 30, theRowRenderer, null));
			myCol.setHeader(true);
		}
	}
}