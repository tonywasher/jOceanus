package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.GroupLayout;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.data.StaticData.StaticList;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.ui.Editor;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.ui.StdMouse;
import uk.co.tolcroft.models.ui.StdTable;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class MaintStaticData<L extends StaticList<L,T,?>,
							 T extends StaticData<T,?>>  extends StdTable<T> {
	private static final long serialVersionUID = -8747707037700378702L;

	/* Properties */
	private View					theView				= null;
	private JPanel					thePanel			= null;
	private MaintStatic				theParent			= null;
	private Class<L>				theClass			= null;
	private L						theStatic			= null;
	private MaintStaticData<L,T>	theTable	 		= this;
	private staticModel				theModel			= null;
	private staticMouse				theMouse	 		= null;
	private staticColumnModel		theColumns			= null;
	private ViewList				theViewSet			= null;
	private ListClass				theDataView			= null;
	private DebugEntry				theDebugData		= null;
	private SaveButtons  			theTabButs   		= null;
	private ErrorPanel				theError			= null;

	/* Access methods */
	public JPanel  getPanel()			{ return thePanel; }
	public boolean hasHeader()		 	{ return false; }

	/* Table headers */
	private static final String titleClass   = "Class";
	private static final String titleName    = "Name";
	private static final String titleDesc    = "Description";
	private static final String titleOrder   = "SortOrder";
	private static final String titleEnabled = "Enabled";
	private static final String titleActive  = "Active";

	/* Table columns */
	private static final int COLUMN_CLASS 	 = 0;
	private static final int COLUMN_NAME  	 = 1;
	private static final int COLUMN_DESC 	 = 2;
	private static final int COLUMN_ORDER    = 3;
	private static final int COLUMN_ENABLED  = 4;
	private static final int COLUMN_ACTIVE   = 5;

	public MaintStaticData(MaintStatic	pParent,
						   Class<L>		pClass) {
		/* Call super constructor */
		super(pParent.getTopWindow());
		
		/* Declare variables */
		GroupLayout			myLayout;
			
		/* Record the passed details */
		theParent = pParent;
		theClass  = pClass;
		theView	  = pParent.getView();
		
		/* Build the View set and List */
		theViewSet		= theParent.getViewSet();
		theDataView		= theViewSet.registerClass(pClass);

		/* Create the top level debug entry for this view  */
		DebugManager 	myDebugMgr 	= theView.getDebugMgr();
		theDebugData = myDebugMgr.new DebugEntry(pClass.getEnclosingClass().getSimpleName());
        theDebugData.addAsChildOf(pParent.getDebugEntry());

        /* Set the table model */
		theModel  = new staticModel();
		setModel(theModel);
			
		/* Create the data column model and declare it */
		theColumns = new staticColumnModel();
		setColumnModel(theColumns);
		
		/* Prevent reordering of columns and auto-resizing */
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(800, 200));
		
		/* Add the mouse listener */
		theMouse = new staticMouse();
		addMouseListener(theMouse);
		
		/* Create the sub panels */
		theTabButs   = new SaveButtons(this);
			
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
	                    .addComponent(getScrollPane(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
	                    .addComponent(theTabButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	        		.addComponent(theError.getPanel())
	                .addComponent(getScrollPane())
	                .addComponent(theTabButs.getPanel()))
	    );
	}

	@Override
	public DebugEntry getDebugEntry() {
		return theDebugData;
	}

 	/**
	 * Update Debug view 
	 */
	public void updateDebug() {			
		theDebugData.setObject(theStatic);
	}
		
	@Override
	public void lockOnError(boolean isError) {
		/* Lock scroll-able area */
		getScrollPane().setEnabled(!isError);

		/* Lock row/tab buttons area */
		theTabButs.getPanel().setEnabled(!isError);
	}

	@Override
	public void notifyChanges() {
		/* Find the edit state */
		if (theStatic != null)
			theStatic.findEditState();
		
		/* Update the table buttons */
		theTabButs.setLockDown();
		
		/* Update the top level tabs */
		theParent.setVisibility();
	}

	@Override
	public void saveData() {
		if (theStatic != null) {
			super.validateAll();
			if (!hasErrors()) theViewSet.applyChanges();
		}
	}

	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws Exception {
		FinanceData	myData;
		
		/* Access data */
		myData = theView.getData();
		
		/* Access edit list */
		theStatic = myData.getDataList(theClass);
		theStatic = theStatic.getEditList();
		
		/* Update the Data View */
		setList(theStatic);
		theDataView.setDataList(theStatic);

		/* Update the table buttons */
		theTabButs.setLockDown();
	}
	
	/* Static table model */
	public class staticModel extends DataTableModel {
		private static final long serialVersionUID = -6428052539280821038L;

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
			return (theStatic == null) ? 0
					                 : theStatic.size();
		}
		
		/**
		 * Get the name of the column
		 * @param col the column
		 * @return the name of the column
		 */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_CLASS:		return titleClass;
				case COLUMN_NAME: 		return titleName;
				case COLUMN_DESC: 		return titleDesc;
				case COLUMN_ORDER: 		return titleOrder;
				case COLUMN_ENABLED: 	return titleEnabled;
				case COLUMN_ACTIVE: 	return titleActive;
				default: 				return null;
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
				case COLUMN_CLASS:		return StaticData.FIELD_CLASS;
				case COLUMN_NAME: 		return StaticData.FIELD_NAME;
				case COLUMN_DESC:		return StaticData.FIELD_DESC;
				case COLUMN_ENABLED:	return StaticData.FIELD_ENABLED;
				case COLUMN_ORDER:		return StaticData.FIELD_ORDER;
				default: 				return -1;
			}
		}
			
		/**
		 * Get the object class of the column
		 * @param col the column
		 * @return the class of the objects associated with the column
		 */
		public Class<?> getColumnClass(int col) {				
			switch (col) {
				case COLUMN_DESC: 		return String.class;
				case COLUMN_CLASS:		return String.class;
				case COLUMN_NAME: 		return String.class;
				case COLUMN_ORDER: 		return Integer.class;
				case COLUMN_ENABLED: 	return String.class;
				case COLUMN_ACTIVE: 	return String.class;
				default: 				return Object.class;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
		public boolean isCellEditable(int row, int col) {
			T myData;
			
			/* Access the data */
			myData = theStatic.get(row);
			
			/* Not edit-able is not enabled */
			if (!myData.getEnabled()) return false;
			
			/* switch on column */
			switch (col) {
				case COLUMN_NAME:
				case COLUMN_DESC:
					return true;
				case COLUMN_CLASS:
				case COLUMN_ORDER:
				case COLUMN_ENABLED:
				case COLUMN_ACTIVE:
				default:
					return false;
			}
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) {
			T 		myData;
			Object  o;
											
			/* Access the data */
			myData = theStatic.get(row);
			
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_CLASS:  	
					o = myData.getStaticClass().toString();
					break;
				case COLUMN_NAME:  	
					o = myData.getName();
					break;
				case COLUMN_DESC:
					o = myData.getDesc();
					if ((o != null) && (((String)o).length() == 0))
						o = null;
					break;
				case COLUMN_ENABLED:
					o = myData.getEnabled() ? "true" : "false";
					break;
				case COLUMN_ORDER:  	
					o = myData.getOrder();
					break;
				case COLUMN_ACTIVE:
					o = myData.isActive() ? "true" : "false";
					break;
				default:	
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myData.hasErrors(getFieldForCell(row, col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}				
		
		/**
		 * Set the value at (row, col)
		 * @param obj the object value to set
		 */
		public void setValueAt(Object obj, int row, int col) {
			T myData;
			
			/* Access the line */
			myData = theStatic.get(row);
			
			/* Push history */
			myData.pushHistory();

			/* Protect against Exceptions */
			try {
				/* Store the appropriate value */
				switch (col) {
					case COLUMN_NAME:  
						myData.setName((String)obj);    
						break;
					case COLUMN_DESC:  
						myData.setDescription((String)obj);            
						break;
				}
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				myData.popHistory();
				myData.pushHistory();
				
				/* Build the error */
				Exception myError = new Exception(ExceptionClass.DATA,
										          "Failed to update field at ("
										          + row + "," + col +")",
										          e);
				
				/* Show the error */
				theError.setError(myError);
			}
			
			/* Check for changes */
			if (myData.checkForHistory()) {
				/* Note that the item has changed */
				myData.clearErrors();
				myData.setState(DataState.CHANGED);

				/* Validate the item and update the edit state */
				myData.validate();
				theStatic.findEditState();
			
				/* Switch on the updated column */
				switch (col) {
					/* note that we have updated this cell */
					default:
						fireTableRowsUpdated(row, row);
						break;
				}
				
				/* Note that changes have occurred */
				notifyChanges();
				updateDebug();
			}
		}
	}
	
	/**
	 *  Static mouse listener
	 */
	private class staticMouse extends StdMouse<T> {
			
		/* Pop-up Menu items */
		private static final String popupSetEnabled		= "Set As Enabled";
		private static final String popupSetDisabled	= "Set As Disabled";
			
		/**
		 * Constructor
		 */
		private staticMouse() {
			/* Call super-constructor */
			super(theTable);
		}
		
		/**
		 * Disable Insert/Delete
		 * @param pMenu the menu to add to
		 */
		protected void addInsertDelete(JPopupMenu pMenu) {}
		
		/**
		 * Add Special commands to menu
		 * @param pMenu the menu to add to
		 */
		protected void addSpecialCommands(JPopupMenu pMenu) {
			JMenuItem 		myItem;
			T				myData;
			Class<T>		myClass			= (theStatic != null) ? theStatic.getBaseClass() : null;
			boolean			enableEnable	= false;
			boolean			enableDisable	= false;
			boolean			isEnabled;
			boolean			isActive;

			/* Nothing to do if the table is locked */
			if (theTable.isLocked()) return;
			
			/* Loop through the selected rows */
			for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
				/* Ignore locked rows */
				if ((myRow == null) || (myRow.isLocked())) continue;
				
				/* Ignore deleted rows */
				if (myRow.isDeleted()) continue;
				
				/* Access as data */
				myData  = myClass.cast(myRow);
				
				/* Determine flags */
				isEnabled = myData.getEnabled();
				isActive  = myData.isActive();
				
				/* Determine whether we can enable/disable */
				if (!isEnabled) 	enableEnable = true;
				else if (!isActive) enableDisable = true;
			}
			
			/* If there is something to add and there are already items in the menu */
			if (((enableEnable) || (enableDisable)) &&
			    (pMenu.getComponentCount() > 0)) {
				/* Add a separator */
				pMenu.addSeparator();
			}
			
			/* If we can Enable the item */
			if (enableEnable) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupSetEnabled);
				myItem.setActionCommand(popupSetEnabled);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}
			
			/* If we can Disable the item */
			if (enableDisable) {
				/* Add the undo change choice */
				myItem = new JMenuItem(popupSetDisabled);
				myItem.setActionCommand(popupSetDisabled);
				myItem.addActionListener(this);
				pMenu.add(myItem);			
			}
		}

		/**
		 * Perform actions for controls/pop-ups on this table
		 * @param evt the event
		 */
		public void actionPerformed(ActionEvent evt) {
			String myCmd = evt.getActionCommand();
			
			/* Cancel any editing */
			theTable.cancelEditing();
			
			/* If this is a set null units command */
			if (myCmd.equals(popupSetEnabled)) {
				/* Enable disabled rows */
				setEnabledRows(true);
			}
			
			/* else if this is a set null tax command */
			else if (myCmd.equals(popupSetDisabled)) {
				/* Disable rows */
				setEnabledRows(false);				
			}

			/* else we do not recognise the action */
			else {
				/* Pass it to the superclass */
				super.actionPerformed(evt);
				return;
			}
			
			/* Notify of any changes */
			notifyChanges();
			updateDebug();
		}
		
		/**
		 * Enable/Disable Rows
		 */
		private void setEnabledRows(boolean doEnable) {
			T			myData;
			Class<T>	myClass	= (theStatic != null) ? theStatic.getBaseClass() : null;
			boolean		isEnabled;
			boolean		isActive;
			int			row;
			
			/* Loop through the selected rows */
			for (DataItem<?> myRow : theTable.cacheSelectedRows()) {
				/* Ignore locked rows */
				if ((myRow == null) || (myRow.isLocked())) continue;
				
				/* Ignore deleted rows */
				if (myRow.isDeleted()) continue;

				/* Determine row */
				row = myRow.indexOf();
				
				/* Access as data */
				myData  = myClass.cast(myRow);
				
				/* Determine flags */
				isEnabled = myData.getEnabled();
				isActive  = myData.isActive();

				/* If we are enabling */
				if (doEnable) {
					/* Ignore enabled rows */
					if (isEnabled) continue;
					
					/* Enable the row */
					myData.setEnabled(true);
				}
				
				/* If we are disabling */
				else {
					/* Ignore disabled/active rows */
					if ((!isEnabled) || (isActive)) continue;
					
					/* Enable the row */
					myData.setEnabled(false);
				}
				
				/* set the tax credit value */
				theModel.fireTableCellUpdated(row, COLUMN_ENABLED);
			}
		}
	}
	
	/**
	 * Column Model class
	 */
	private class staticColumnModel extends DataColumnModel {
		private static final long serialVersionUID = 676363206266447113L;

		/* Renderers/Editors */
		private Renderer.IntegerCell	theIntegerRenderer 	= null;
		private Renderer.StringCell		theStringRenderer 	= null;
		private Editor.StringCell 		theStringEditor   	= null;

		/**
		 * Constructor 
		 */
		private staticColumnModel() {		
			/* Create the relevant formatters/editors */
			theIntegerRenderer 	= new Renderer.IntegerCell();
			theStringRenderer 	= new Renderer.StringCell();
			theStringEditor   	= new Editor.StringCell();
			
			/* Create the columns */
			addColumn(new DataColumn(COLUMN_CLASS,    90, theStringRenderer, null));
			addColumn(new DataColumn(COLUMN_NAME,     80, theStringRenderer, theStringEditor));
			addColumn(new DataColumn(COLUMN_DESC,    200, theStringRenderer, theStringEditor));
			addColumn(new DataColumn(COLUMN_ORDER,    20, theIntegerRenderer, null));
			addColumn(new DataColumn(COLUMN_ENABLED,  20, theStringRenderer, null));
			addColumn(new DataColumn(COLUMN_ACTIVE,   20, theStringRenderer, null));
		}
	}
}
