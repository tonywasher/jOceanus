package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.ui.StdTable;
import uk.co.tolcroft.models.ui.Renderer;
import uk.co.tolcroft.models.ui.StdInterfaces.*;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class MaintNewYear extends StdTable<Event> implements ActionListener {
	private static final long serialVersionUID = 7406051901546832781L;
	
	private View				theView				= null;
	private TaxYear.List	    theTaxYears			= null;
	private Event.List	        theEvents			= null;
	private MaintenanceTab		theParent			= null;
	private JPanel				thePanel			= null;
	private yearColumnModel		theColumns			= null;
	private TaxYear				theYear				= null;
	private patternYearModel	theModel			= null;
	private JButton				thePattern			= null;
	private DebugEntry			theDebugYear		= null;
	private DebugEntry			theDebugEvents		= null;
	private ViewList			theViewSet			= null;
	private ListClass			theYearView			= null;
	private ListClass			theEventView		= null;

	/* Access methods */
	public JPanel  getPanel()			{ return thePanel; }
	public boolean hasHeader()		 	{ return false; }
	
	/* Access the debug entry */
	public DebugEntry getDebugEntry()	{ return theDebugYear; }

	/* Table headers */
	private static final String titleDate    = "Date";
	private static final String titleDesc    = "Description";
	private static final String titleTrans   = "TransactionType";
	private static final String titleAmount  = "Amount";
	private static final String titleDebit   = "Debit";
	private static final String titleCredit  = "Credit";
		
	/* Table columns */
	private static final int COLUMN_DATE 	 = 0;
	private static final int COLUMN_DESC 	 = 1;
	private static final int COLUMN_TRANTYP  = 2;
	private static final int COLUMN_AMOUNT	 = 3;
	private static final int COLUMN_DEBIT	 = 4;
	private static final int COLUMN_CREDIT	 = 5;
		
	/**
	 * Constructor for New Year Window
	 * @param pParent the parent window
	 */
	public MaintNewYear(MaintenanceTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		GroupLayout			myLayout;
			
		/* Record the passed details */
		theParent = pParent;
		theView	  = pParent.getView();
		
		/* Build the View set and List */
		theViewSet		= new ViewList(theView);
		theYearView		= theViewSet.registerClass(TaxYear.class);
		theEventView	= theViewSet.registerClass(Event.class);

		/* Set the table model */
		theModel  = new patternYearModel();
			
		/* Set the table model */
		setModel(theModel);
			
		/* Create the data column model and declare it */
		theColumns = new yearColumnModel();
		setColumnModel(theColumns);
		
		/* Prevent reordering of columns and auto-resizing */
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(800, 200));
		
		/* Create the button */
		thePattern = new JButton("Apply");
		thePattern.addActionListener(this);
			
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
	                    .addComponent(getScrollPane(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
	                    .addComponent(thePattern))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(getScrollPane())
	                .addComponent(thePattern))
	    );

        /* Create the debug entry, attach to MaintenanceDebug entry */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        DebugEntry myEntry = myDebugMgr.new DebugEntry("NewYear");
        myEntry.addAsChildOf(pParent.getDebugEntry());
        theDebugYear = myDebugMgr.new DebugEntry("Year");
        theDebugYear.addAsChildOf(myEntry);
        theDebugEvents = myDebugMgr.new DebugEntry("Events");
        theDebugEvents.addAsChildOf(myEntry);
	}
		
	/* Stubs */
	public void saveData() {}
	public void notifyChanges() {}
	public boolean hasUpdates() { return false; }
	public boolean isLocked() { return false; }
	public EditState getEditState() { return EditState.CLEAN; }
	public void performCommand(stdCommand pCmd) {}
	public void notifySelection(Object pObj) {}
	public void lockOnError(boolean isError) {}
		
	/**
	 * Refresh views/controls after a load/update of underlying data
	 */
	public void refreshData() throws Exception {
		FinanceData		myData = theView.getData();
		TaxYear.List 	myList = myData.getTaxYears();
		DataList<TaxYear>.ListIterator myIterator;
		
		myIterator 	= myList.listIterator();
		theYear 	= myIterator.peekLast();
		setSelection();
	}
	
	/**
	 * Set Selection
	 */
	public void setSelection() throws Exception {
		theTaxYears	= null;
		theEvents  	= null;			
		thePattern.setVisible(false);
		if (theYear != null) {
			FinanceData myData = theView.getData();
			theTaxYears	= myData.getTaxYears().getNewEditList();
			theEvents	= myData.getEvents().getEditList(theYear);
			thePattern.setVisible(true);
			thePattern.setEnabled(!theYear.isActive() &&
								  !theEvents.isEmpty());
		}
		setList(theEvents);
		theYearView.setDataList(theTaxYears);
		theEventView.setDataList(theEvents);
		theDebugYear.setObject(theTaxYears);
		theDebugEvents.setObject(theEvents);
		theParent.setVisibleTabs();
	}
		
	/**
	 * Perform actions for controls/pop-ups on this table
	 * @param evt the event
	 */
	public void actionPerformed(ActionEvent evt) {			
		/* If this event relates to the pattern button */
		if (evt.getSource() == (Object)thePattern) {
			/* Apply the extract changes */
			theViewSet.applyChanges();
		}
	}
	
	/* PatternYear table model */
	public class patternYearModel extends DataTableModel {
		private static final long serialVersionUID = 4796112294536415723L;

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
			return (theEvents == null) ? 0
					                   : theEvents.size();
		}
		
		/**
		 * Get the name of the column
		 * @param col the column
		 * @return the name of the column
		 */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_DATE: 		return titleDate;
				case COLUMN_DESC: 		return titleDesc;
				case COLUMN_TRANTYP: 	return titleTrans;
				case COLUMN_AMOUNT:		return titleAmount;
				case COLUMN_CREDIT: 	return titleCredit;
				case COLUMN_DEBIT: 		return titleDebit;
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
				case COLUMN_TRANTYP:	return String.class;
				case COLUMN_CREDIT: 	return String.class;
				case COLUMN_DEBIT: 		return String.class;
				default: 				return Object.class;
			}
		}
			
		/**
		 * Is the cell at (row, col) editable
		 */
		public boolean isCellEditable(int row, int col) {
			return false;
		}
			
		/**
		 * Get the value at (row, col)
		 * @return the object value
		 */
		public Object getValueAt(int row, int col) {
			Event 	myEvent;
			Object  o;
											
			/* Access the event */
			myEvent = theEvents.get(row);
			
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_DATE:  	
					o = myEvent.getDate();
					break;
				case COLUMN_TRANTYP:  	
					o = (myEvent.getTransType() == null)
							? null : myEvent.getTransType().getName();
					break;
				case COLUMN_CREDIT:		
					o = (myEvent.getCredit() == null) 
							? null : myEvent.getCredit().getName();
					break;
				case COLUMN_DEBIT:		
					o = (myEvent.getDebit() == null)
							? null : myEvent.getDebit().getName();
					break;
				case COLUMN_AMOUNT:		
					o = myEvent.getAmount();
					break;
				case COLUMN_DESC:
					o = myEvent.getDesc();
					if ((o != null) & (((String)o).length() == 0))
						o = null;
					break;
				default:	
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myEvent.hasErrors(getFieldForCell(row, col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}				
	}

	/**
	 * Column Model class
	 */
	private class yearColumnModel extends DataColumnModel {
		private static final long serialVersionUID = -894489367275603586L;

		/* Renderers/Editors */
		private Renderer.DateCell 	theDateRenderer   	= null;
		private Renderer.MoneyCell 	theMoneyRenderer  	= null;
		private Renderer.StringCell	theStringRenderer 	= null;

		/**
		 * Constructor 
		 */
		private yearColumnModel() {		
			/* Create the relevant formatters/editors */
			theDateRenderer   = new Renderer.DateCell();
			theMoneyRenderer  = new Renderer.MoneyCell();
			theStringRenderer = new Renderer.StringCell();
			
			/* Create the columns */
			addColumn(new DataColumn(COLUMN_DATE,     80, theDateRenderer,   null));
			addColumn(new DataColumn(COLUMN_DESC,    150, theStringRenderer, null));
			addColumn(new DataColumn(COLUMN_TRANTYP, 110, theStringRenderer, null));
			addColumn(new DataColumn(COLUMN_AMOUNT,   90, theMoneyRenderer,  null));
			addColumn(new DataColumn(COLUMN_DEBIT,   130, theStringRenderer, null));
			addColumn(new DataColumn(COLUMN_CREDIT,  130, theStringRenderer, null));
		}
	}
}
