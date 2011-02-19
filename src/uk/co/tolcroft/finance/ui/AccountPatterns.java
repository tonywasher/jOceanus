package uk.co.tolcroft.finance.ui;

import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.EditButtons.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.*;

public class AccountPatterns extends FinanceTableModel<Pattern> {
	/* Members */
	private static final long serialVersionUID = 1968946370981616222L;

	private View					theView				= null;
	private PatternsModel			theModel			= null;
	private Pattern.List  			thePatterns 		= null;
	private Account.List			theAccounts			= null;
	private Frequency.List			theFreqs			= null;
	private TransactionType.List	theTransTypes		= null;
	private JPanel					thePanel			= null;
	private JComboBox				theFreqBox			= null;
	private AccountTab				theParent   		= null;
	private EditButtons    			theRowButs  		= null;
	private Account                 theAccount  		= null;
	private View.ViewPatterns		theExtract			= null;
	private Renderer.DateCell 		theDateRenderer   	= null;
	private Editor.DateCell 		theDateEditor     	= null;
	private Renderer.MoneyCell 		theMoneyRenderer  	= null;
	private Editor.MoneyCell 		theMoneyEditor    	= null;
	private Renderer.StringCell 	theStringRenderer 	= null;
	private Editor.StringCell 		theStringEditor   	= null;
	private Editor.ComboBoxCell		theComboEditor    	= null;
	private ComboSelect				theComboList    	= null;
	private boolean					freqsPopulated    	= false;

	/* Access methods */
	public JPanel  getPanel()			{ return thePanel; }
	public boolean hasCreditChoice() 	{ return true; }
	public boolean hasHeader()			{ return false; }
		
	/* Table headers */
	private static final String titleDate    = "Date";
	private static final String titleDesc    = "Description";
	private static final String titleTrans   = "TransactionType";
	private static final String titlePartner = "Partner";
	private static final String titleCredit  = "Credit";
	private static final String titleDebit   = "Debit";
	private static final String titleFreq    = "Frequency";
		
	/* Table columns */
	private static final int COLUMN_DATE 	 = 0;
	private static final int COLUMN_TRANTYP  = 1;
	private static final int COLUMN_DESC 	 = 2;
	private static final int COLUMN_PARTNER	 = 3;
	private static final int COLUMN_CREDIT	 = 4;
	private static final int COLUMN_DEBIT 	 = 5;
	private static final int COLUMN_FREQ 	 = 6;
	private static final int NUM_COLUMNS	 = 7;
				
	/* Constructor */
	public AccountPatterns(AccountTab pParent) {
		/* Initialise superclass */
		super(pParent.getTopWindow());

		/* Declare variables */
		TableColumnModel    myColModel;
		TableColumn			myCol;
		JScrollPane		 	myScroll;
		GroupLayout		 	myLayout;
			
		/* Store details about the parent */
		theParent = pParent;
		theView   = pParent.getView();

		/* Create the model and declare it to our superclass */
		theModel  = new PatternsModel();
		setModel(theModel);
		
		/* Access the column model */
		myColModel = getColumnModel();
			
		/* Create the relevant formatters/editors */
		theDateRenderer   = new Renderer.DateCell();
		theDateEditor     = new Editor.DateCell();
		theMoneyRenderer  = new Renderer.MoneyCell();
		theMoneyEditor    = new Editor.MoneyCell();
		theStringRenderer = new Renderer.StringCell();
		theStringEditor   = new Editor.StringCell();
		theComboEditor    = new Editor.ComboBoxCell();
		
		/* Build the combo box */
		theFreqBox    = new JComboBox();

		/* Set the relevant formatters/editors */
		myCol = myColModel.getColumn(COLUMN_DATE);
		myCol.setCellRenderer(theDateRenderer);
		myCol.setCellEditor(theDateEditor);
		myCol.setPreferredWidth(80);
		
		myCol = myColModel.getColumn(COLUMN_TRANTYP);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(110);
		
		myCol = myColModel.getColumn(COLUMN_DESC);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theStringEditor);
		myCol.setPreferredWidth(150);
		
		myCol = myColModel.getColumn(COLUMN_PARTNER);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(130);
		
		myCol = myColModel.getColumn(COLUMN_CREDIT);
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setCellEditor(theMoneyEditor);
		myCol.setPreferredWidth(90);
		
		myCol = myColModel.getColumn(COLUMN_DEBIT);
		myCol.setCellRenderer(theMoneyRenderer);
		myCol.setCellEditor(theMoneyEditor);
		myCol.setPreferredWidth(90);
		
		myCol = myColModel.getColumn(COLUMN_FREQ);
		myCol.setCellRenderer(theStringRenderer);
		myCol.setCellEditor(theComboEditor);
		myCol.setPreferredWidth(110);
		
		getTableHeader().setReorderingAllowed(false);
		
		/* Set the date editor to show no years */
		theDateEditor.setNoYear();
		
		/* Set the number of visible rows */
		setPreferredScrollableViewportSize(new Dimension(900, 200));

		/* Create the sub panels */
		theRowButs   = new EditButtons(this, InsertStyle.NONE);
			
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
	                    .addComponent(myScroll, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 900, Short.MAX_VALUE)
	                    .addComponent(theRowButs.getPanel(), GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        myLayout.setVerticalGroup(
        	myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, myLayout.createSequentialGroup()
	                .addComponent(myScroll)
	                .addComponent(theRowButs.getPanel()))
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
		
	/* refresh data */
	public void refreshData() {
		DataSet		myData;
		Frequency   myFreq;
		
		DataList<Frequency>.ListIterator myIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access Frequencies, TransTypes and Accounts */
		theFreqs    	= myData.getFrequencys();
		theTransTypes 	= myData.getTransTypes();
		theAccounts 	= myData.getAccounts();

		/* Access the combo list from parent */
		theComboList 	= theParent.getComboList();
		
		/* If we have frequencies already populated */
		if (freqsPopulated) {	
			/* Remove the frequencies */
			theFreqBox.removeAllItems();
			freqsPopulated = false;
		}
	
		/* Access the frequency iterator */
		myIterator = theFreqs.listIterator();
		
		/* Add the Frequency values to the frequencies box */
		while ((myFreq  = myIterator.next()) != null) {
			/* Add the item to the list */
			theFreqBox.addItem(myFreq.getName());
			freqsPopulated = true;
		}
	}
	
	/* saveData */
	public void saveData() {
		if (theExtract != null) {
			theExtract.applyChanges();
		}
	}
	
	/* Note that there has been a list selection change */
	public void notifyChanges() {
		/* Update the row buttons */
		theRowButs.setLockDown();
		
		/* Find the edit state */
		if (thePatterns != null)
			thePatterns.findEditState();
		
		/* Update the parent panel */
		theParent.notifyChanges(); 
	}
	
	/* Set Selection */
	public void setSelection(Account pAccount) {
		theExtract  = theView.new ViewPatterns(pAccount);
		theAccount  = pAccount;
		thePatterns = theExtract.getPatterns();
		super.setList(thePatterns);
		theModel.fireTableDataChanged();
		theRowButs.setLockDown();
	}
		
	/* Get field for column */
	public int getFieldForCol(int column) {
		/* Switch on column */
		switch (column) {
			case COLUMN_DATE: 		return Pattern.FIELD_DATE;
			case COLUMN_DESC:		return Pattern.FIELD_DESC;
			case COLUMN_TRANTYP:	return Pattern.FIELD_TRNTYP;
			case COLUMN_CREDIT: 	return Pattern.FIELD_AMOUNT;
			case COLUMN_DEBIT: 		return Pattern.FIELD_AMOUNT;
			case COLUMN_PARTNER:	return Pattern.FIELD_PARTNER;
			case COLUMN_FREQ:  		return Pattern.FIELD_FREQ;
			default:				return -1;
		}
	}
		
	/* Get combo box for cell */
	public JComboBox getComboBox(int row, int column) {
		Pattern 			myPattern;
		ComboSelect.Item    mySelect;
		
		/* Access the pattern */
		myPattern = thePatterns.get(row);

		/* Switch on column */
		switch (column) {
			case COLUMN_FREQ:	
				return theFreqBox;
			case COLUMN_TRANTYP:		
				mySelect = theComboList.searchFor(myPattern.getActType());
				return (myPattern.isCredit()) ? mySelect.getCredit()
						                      : mySelect.getDebit();
			case COLUMN_PARTNER:
				mySelect = theComboList.searchFor(myPattern.getTransType());
				return (myPattern.isCredit()) ? mySelect.getDebit()
						                      : mySelect.getCredit();
			default: 				
				return null;
		}
	}
		
	/* Add a pattern based on a statement line */
	public void addPattern(Statement.Line pLine) {
		Pattern myPattern;
		
		/* Create the new Item */
		myPattern = new Pattern(thePatterns, pLine);
		myPattern.addToList();
	
		/* Note the changes */
		notifyChanges();
		
		/* Notify of the insertion of the column */
		theModel.fireTableDataChanged();
	}
	
	/* Patterns table model */
	public class PatternsModel extends AbstractTableModel {
		private static final long serialVersionUID = -8445100544184045930L;

		/* get column count */
		public int getColumnCount() { return NUM_COLUMNS; }
		
		/* get row count */
		public int getRowCount() { 
			return (thePatterns == null) ? 0
					                     : thePatterns.size();
		}
			
		/* get column name */
		public String getColumnName(int col) {
			switch (col) {
				case COLUMN_DATE:  		return titleDate;
				case COLUMN_DESC:  		return titleDesc;
				case COLUMN_TRANTYP:  	return titleTrans;
				case COLUMN_PARTNER:  	return titlePartner;
				case COLUMN_CREDIT:  	return titleCredit;
				case COLUMN_DEBIT:	 	return titleDebit;
				case COLUMN_FREQ:		return titleFreq;
				default:				return null;
			}
		}
			
		/* is get column class */
		public Class<?> getColumnClass(int col) {				
			switch (col) {
				case COLUMN_DESC:  		return String.class;
				case COLUMN_TRANTYP:  	return String.class;
				case COLUMN_PARTNER:  	return String.class;
				case COLUMN_CREDIT:  	return String.class;
				case COLUMN_DEBIT:  	return String.class;
				case COLUMN_FREQ:  		return String.class;
				default: 				return Object.class;
			}
		}
			
		/* is cell edit-able */
		public boolean isCellEditable(int row, int col) {
			Pattern myPattern;
			
			/* If the account is not editable */
			if (theAccount.isLocked()) return false;
			
			/* Access the pattern */
			myPattern = thePatterns.get(row);
			
			/* Cannot edit if row is deleted or locked */
			if (myPattern.isDeleted() || myPattern.isLocked())
				return false;
			
			switch (col) {
				case COLUMN_CREDIT:		return myPattern.isCredit();
				case COLUMN_DEBIT:		return !myPattern.isCredit();
				default: return true;
			}
		}
			
		/* get value At */
		public Object getValueAt(int row, int col) {
			Pattern myPattern;
			Object  o;
				
			/* Access the pattern */
			myPattern = thePatterns.get(row);
				
			/* Return the appropriate value */
			switch (col) {
				case COLUMN_DATE:  		
					o = myPattern.getDate();
					break;
				case COLUMN_DESC:	 	
					o = myPattern.getDesc();
					if ((o != null) && (((String)o).length() == 0))
						o = null;
					break;
				case COLUMN_TRANTYP:  	
					o = (myPattern.getTransType() == null) 
								? null : myPattern.getTransType().getName();
					break;
				case COLUMN_PARTNER:
					o = (myPattern.getPartner() == null) 
								? null : myPattern.getPartner().getName();
					break;
				case COLUMN_CREDIT:  	
					o = (myPattern.isCredit()) ? myPattern.getAmount()
					   					  	   : null;
					break;
				case COLUMN_DEBIT:
					o =(!myPattern.isCredit()) ? myPattern.getAmount()
											   : null;
					break;
				case COLUMN_FREQ:		
					o = (myPattern.getFrequency() == null) 
								? null : myPattern.getFrequency().getName();
					break;
				default:				
					o = null;
					break;
			}
			
			/* If we have a null value for an error field,  set error description */
			if ((o == null) && (myPattern.hasErrors(getFieldForCol(col))))
				o = Renderer.getError();
			
			/* Return to caller */
			return o;
		}
			
		/* set value At */
		public void setValueAt(Object obj, int row, int col) {
			Pattern myPattern;
			
			/* Access the pattern */
			myPattern = thePatterns.get(row);
				
			/* Push history */
			myPattern.pushHistory();
			
			/* TODO process errors caught here */
			try {
			/* Store the appropriate value */
			switch (col) {
				case COLUMN_DATE:  
					myPattern.setDate((Date)obj);    
					break;
				case COLUMN_DESC:  
					myPattern.setDesc((String)obj);            
					break;
				case COLUMN_TRANTYP:  
					myPattern.setTransType(theTransTypes.searchFor((String)obj));    
					break;
				case COLUMN_CREDIT:
				case COLUMN_DEBIT:
					myPattern.setAmount((Money)obj); 
					break;
				case COLUMN_PARTNER:  
					myPattern.setPartner(theAccounts.searchFor((String)obj));    
					break;
				case COLUMN_FREQ:
				default: 
					myPattern.setFrequency(theFreqs.searchFor((String)obj));    
					break;
			}
			/* TODO Catch errors */
			} catch(Throwable e) {}
				
			/* Check for changes */
			if (myPattern.checkForHistory()) {
				/* Note that the item has changed */
				myPattern.setState(DataState.CHANGED);
				thePatterns.findEditState();
				
				/* Switch on the updated column */
				switch (col) {
					/* redraw whole table if we have updated a sort col */
					case COLUMN_DATE:
					case COLUMN_DESC:
					case COLUMN_TRANTYP: 
						myPattern.reSort();
						fireTableDataChanged();
						row = thePatterns.indexOf(myPattern);
						selectRow(row);
						break;
						
					/* else note that we have updated this cell */
					default:
						fireTableCellUpdated(row, col);
						break;
				}
			
				/* Update components to reflect changes */
				notifyChanges();
			}
		}
	}
}
