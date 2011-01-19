package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;

public class MaintNewYear extends FinanceTableModel<Event> implements ActionListener {
	private static final long serialVersionUID = 7406051901546832781L;
	
	private View				theView				= null;
	private View.ViewEvents     theExtract			= null;
	private Event.List	        theEvents			= null;
	private MaintenanceTab		theParent			= null;
	private JPanel				thePanel			= null;
	private TaxYear				theYear				= null;
	private patternYearModel	theModel			= null;
	private JButton				thePattern			= null;
	private Renderer.DateCell 	theDateRenderer   	= null;
	private Renderer.MoneyCell 	theMoneyRenderer  	= null;
	private Renderer.StringCell	theStringRenderer 	= null;

	/* Access methods */
	public JPanel  getPanel()			{ return thePanel; }
	public boolean hasHeader()		 	{ return false; }
	
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
	private static final int NUM_COLUMNS	 = 6;
		
	/* Constructor */
	public MaintNewYear(MaintenanceTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel    myColModel;
		TableColumn			myCol;
		JScrollPane			myScroll;
		GroupLayout			myLayout;
			
		/* Record the passed details */
		theParent = pParent;
		theView	  = pParent.getView();
		
		/* Set the table model */
		theModel  = new patternYearModel();
			
		/* Set the table model */
		setModel(theModel);
			
		/* Access the column model */
		myColModel = getColumnModel();
		
		/* Create the relevant formatters/editors */
		theDateRenderer   = new Renderer.DateCell();
		theMoneyRenderer  = new Renderer.MoneyCell();
		theStringRenderer = new Renderer.StringCell();
		
		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setPreferredWidth(80);
			
		myCol = myColModel.getColumn(COLUMN_DESC);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setPreferredWidth(150);
		
		myCol = myColModel.getColumn(COLUMN_TRANTYP);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setPreferredWidth(110);
		
		myCol = myColModel.getColumn(COLUMN_AMOUNT);
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setPreferredWidth(90);
			
		myCol= myColModel.getColumn(COLUMN_DEBIT);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setPreferredWidth(130);
			
		myCol = myColModel.getColumn(COLUMN_CREDIT);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setPreferredWidth(130);
			
		getTableHeader().setReorderingAllowed(false);
		setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);

		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(800, 200));
		
		/* Create a new Scroll Pane and add this table to it */
		myScroll     = new JScrollPane();
		myScroll.setViewportView(this);
		
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
	                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
	                    .addComponent(thePattern))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(myScroll)
	                .addComponent(thePattern))
	    );
	}
		
	/* saveData */
	public void saveData() {}
	public void notifyChanges() {}
	public boolean hasUpdates() { return false; }
	public boolean isLocked() { return false; }
	public EditState getEditState() { return EditState.CLEAN; }
	public void performCommand(financeCommand pCmd) {}
	public void notifySelection(Object pObj) {}
		
	/* refresh data */
	public void refreshData() {
		DataSet 		myData = theView.getData();
		TaxYear.List 	myList = myData.getTaxYears();
		DataList<TaxYear>.ListIterator myIterator;
		
		myIterator 	= myList.listIterator();
		theYear 	= myIterator.peekLast();
		setSelection();
	}
	
	/* Set Selection */
	public void setSelection() {
		if (theYear != null) {
			theExtract = theView.new ViewEvents(theYear);
			theEvents  = theExtract.getEvents();
			setList(theEvents);
			thePattern.setVisible(true);
			thePattern.setEnabled(!theYear.isActive() &&
								  !theEvents.isEmpty());
		}
		else {
			theExtract = null;
			theEvents  = null;			
			thePattern.setVisible(false);
		}
		theModel.fireTableDataChanged();
		theParent.setVisibleTabs();
	}
		
	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {			
		/* If this event relates to the pattern button */
		if (evt.getSource() == (Object)thePattern) {
			/* Apply the extract changes */
			theExtract.applyChanges();
		}
	}
	
	/* Get field for column */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			default: 				return -1;
		}
	}
		
	/* PatternYear table model */
	public class patternYearModel extends AbstractTableModel {
		private static final long serialVersionUID = 4796112294536415723L;

			/* get column count */
		public int getColumnCount() { return NUM_COLUMNS; }
		
		/* get row count */
		public int getRowCount() { 
			return (theEvents == null) ? 0
					                   : theEvents.size();
		}
		
		/* get column name */
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
		
		/* is get column class */
		public Class<?> getColumnClass(int col) {				
			switch (col) {
				case COLUMN_DESC: 		return String.class;
				case COLUMN_TRANTYP:	return String.class;
				case COLUMN_CREDIT: 	return String.class;
				case COLUMN_DEBIT: 		return String.class;
				default: 				return Object.class;
			}
		}
			
		/* is cell edit-able */
		public boolean isCellEditable(int row, int col) {
			return false;
		}
			
		/* get value At */
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
			if ((o == null) && (myEvent.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}				
	}
}
