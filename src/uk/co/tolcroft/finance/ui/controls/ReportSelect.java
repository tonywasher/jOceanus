package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.views.EventAnalysis.AnalysisYear;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

public class ReportSelect implements	ActionListener,
										ItemListener,
										ChangeListener {
	/* Members */
	private JPanel				thePanel		= null;
	private financePanel		theParent		= null;
	private View				theView			= null;
	private SpinnerDateModel    theModel        = null;
	private JSpinner            theDateBox      = null;
	private JComboBox           theReportBox 	= null;
	private JComboBox           theYearsBox 	= null;
	private JLabel				theRepLabel		= null;
	private JLabel				theYearLabel	= null;
	private JLabel				theDateLabel	= null;
	private JButton				thePrintButton	= null;
	private TaxYear.List		theYears		= null;
	private ReportState			theState		= null;
	private ReportState			theSavePoint	= null;
	private boolean				yearsPopulated 	= false;
	private boolean				refreshingData  = false;
	
	/* Access methods */
	public  JPanel      getPanel()      { return thePanel; }
	public 	ReportType 	getReportType() { return theState.getType(); }
	public 	TaxYear 	getTaxYear()    { return theState.getYear(); }
	public	Date 	    getReportDate() { return theState.getDate(); }
				
	/* Report descriptions */
	private static final String Assets    	= "Asset";
	private static final String IncomeExp 	= "Income/Expense";
	private static final String Transaction	= "Transaction";
	private static final String Taxation  	= "Taxation";
	private static final String Instant   	= "Instant";
	private static final String Market    	= "Market";

	/* Constructor */
	public ReportSelect(View pView, financePanel pReport) {
		
		/* Store table and view details */
		theView 	  = pView;
		theParent	  = pReport;
		
		/* Create the boxes */
		theReportBox   = new JComboBox();
		theYearsBox    = new JComboBox();
		
		/* Create the DateSpinner Model and Box */
		theModel   = new SpinnerDateModel();
		theDateBox = new JSpinner(theModel);
		
		/* Create initial state */
		theState = new ReportState();
		
		/* Initialise the data from the view */
		refreshData(null);
		
		/* Add the ReportTypes to the report box */
		theReportBox.addItem(Instant);
		theReportBox.addItem(Assets);
		theReportBox.addItem(IncomeExp);
		theReportBox.addItem(Transaction);
		theReportBox.addItem(Taxation);
		theReportBox.addItem(Market);
		theReportBox.setSelectedItem(Instant);
		
		/* Create the labels */
		theRepLabel  = new JLabel("Report:");
		theYearLabel = new JLabel("Year:");
		theDateLabel = new JLabel("Date:");
		
		/* Create the print button */
		thePrintButton = new JButton("Print");
		thePrintButton.addActionListener(this);
		
		/* Set the format of the date */
		theDateBox.setEditor(new JSpinner.DateEditor(theDateBox, "dd-MMM-yyyy"));
	
		/* Create the panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Report Selection"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(theRepLabel)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theReportBox, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(theYearLabel)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theYearsBox)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theDateLabel)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(thePrintButton))
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(theRepLabel)
	                .addComponent(theReportBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theYearLabel)
	                .addComponent(theYearsBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	                .addComponent(theDateLabel)
	                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(thePrintButton)
	    );

		/* Apply the current state */
		theState.applyState();

		/* Add the listener for item changes */
		theReportBox.addItemListener(this);
		theYearsBox.addItemListener(this);
		theModel.addChangeListener(this);
	}
	
	/* refresh data */
	public void refreshData(EventAnalysis pAnalysis) {
		DataSet			myData;
		AnalysisYear  	myYear;
		Date.Range  	myRange;
		TaxYear 		myTaxYear = theState.getYear();
		
		DataList<AnalysisYear>.ListIterator myIterator;
		
		/* Access the data */
		myData  = theView.getData();
		myRange = theView.getRange();
		
		/* Access tax Years */
		theYears    = myData.getTaxYears();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* Set the range for the Date Spinner */
		setRange(myRange);
		
		/* If we have years already populated */
		if (yearsPopulated) {	
			/* If we have a selected year */
			if (myTaxYear != null) {
				/* Find it in the new list */
				myTaxYear = theYears.searchFor(myTaxYear.getDate());
			}
			
			/* Remove the types */
			theYearsBox.removeAllItems();
			yearsPopulated = false;
		}
		
		/* If we have an analysis */
		if (pAnalysis != null) {
			/* Access the iterator */
			myIterator = pAnalysis.getAnalysisYears().listIterator();
		
			/* Add the Year values to the years box in reverse order */
			while ((myYear  = myIterator.previous()) != null) {
				/* Add the item to the list */
				theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
				yearsPopulated = true;
			}
		
			/* If we have a selected year */
			if (myTaxYear != null) {
				/* Select it in the new list */
				theYearsBox.setSelectedItem(Integer.toString(myTaxYear.getDate().getYear()));
			}
		
			/* Else we have no year currently selected */
			else if (yearsPopulated) {
				/* Select the first year */
				theYearsBox.setSelectedIndex(0);
				theState.setYear(myIterator.peekLast().getTaxYear());
			}
		}

		/* Note that we have finished refreshing data */
		refreshingData = false;
	}

	/* Set the range for the date box */
	public  void setRange(Date.Range pRange) {
		Date myStart = null;
		Date myFirst;
		Date myLast;
		
		myFirst = (pRange == null) ? null : pRange.getStart();
		myLast = (pRange == null) ? null : pRange.getEnd();
		if (myFirst != null) {
			myStart = new Date(myFirst);
			myStart.adjustDay(-1);
		}
		theModel.setStart((myFirst == null) ? null : myStart.getDate());
		theModel.setEnd((myLast == null) ? null : myLast.getDate());
	}
	
	/**
	 *  Create SavePoint
	 */
	public void createSavePoint() {
		/* Create the savePoint */
		theSavePoint = new ReportState(theState);
	}

	/**
	 *  Restore SavePoint
	 */
	public void restoreSavePoint() {
		/* Restore the savePoint */
		theState = new ReportState(theSavePoint);
		
		/* Apply the state */
		theState.applyState();		
	}

	/* Lock/Unlock the selection */
	public void setLockDown() {
		ReportType myType = theState.getType();
		
		boolean isDate    = ((myType == ReportType.INSTANT) ||
				             (myType == ReportType.MARKET));
		boolean isNull    = (myType == null);
		boolean isYear    = (!isNull && !isDate);
		
		theDateBox.setEnabled(isDate);
		theDateLabel.setEnabled(isDate);
		theYearsBox.setEnabled(isYear);
		theYearLabel.setEnabled(isYear);				
	}
	
	/* actionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {
		/* If this event relates to the Print button */
		if (evt.getSource() == (Object)thePrintButton) {
			/* Pass command to the table */
			theParent.printIt();
		}
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String      myName;
		ReportType	myType	= null;
		boolean   	bChange	= false;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the years box */
		if (evt.getSource() == (Object)theYearsBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Select the new year */
				theState.setYear(theYears.searchFor(myName));
				bChange = true;
			}
		}
						
		/* If this event relates to the report box */
		if (evt.getSource() == (Object)theReportBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Determine the new report */
				bChange = true;
				if (myName == Assets)	      	myType = ReportType.ASSET;
				else if (myName == IncomeExp) 	myType = ReportType.INCOME;
				else if (myName == Transaction)	myType = ReportType.TRANSACTION;
				else if (myName == Taxation)  	myType = ReportType.TAX;
				else if (myName == Instant)   	myType = ReportType.INSTANT;
				else if (myName == Market)    	myType = ReportType.MARKET;
				else bChange = false;
				
				/* Update state if we have a change */
				if (bChange) theState.setType(myType);
			}
		}
		
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
	}
	
	/* stateChanged listener event */
	public void stateChanged(ChangeEvent evt) {
		boolean bChange = false;
		
		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the start box */
		if (evt.getSource() == (Object)theModel) {
			/* Adjust the date according to the model */
			theState.setDate(theModel);
			bChange    = true;
		}			
				
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
	}
	
	/* SavePoint values */
	private class ReportState {
		/* Members */
		private Date				theDate		= null;
		private TaxYear	 			theYear		= null;
		private ReportType			theType		= null;
		
		/* Access methods */
		private Date 		getDate() 	{ return theDate; }
		private TaxYear 	getYear() 	{ return theYear; }
		private ReportType 	getType() 	{ return theType; }

		/**
		 * Constructor
		 */
		private ReportState() {
			theDate = new Date();
			theYear = null;
			theType = ReportType.INSTANT;
		}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private ReportState(ReportState pState) {
			theDate = new Date(pState.getDate());
			theYear = pState.getYear();
			theType	= pState.getType();
		}
		
		/**
		 * Set new Date
		 * @param pModel the Spinner with the new date 
		 */
		private void setDate(SpinnerDateModel pModel) {
			/* Adjust the date */
			theDate = new Date(theModel.getDate());
		}
		
		/**
		 * Set new Tax Year
		 * @param pYear the new Tax Year 
		 */
		private void setYear(TaxYear pYear) {
			/* Set the new year and apply State */
			theYear = pYear;
			applyState();
		}
		
		/**
		 * Set new Report Type
		 * @param pType the new type 
		 */
		private void setType(ReportType pType) {
			/* Set the new type and apply State */
			theType = pType;
			applyState();
		}
		
		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			setLockDown();
			theModel.setValue(theDate.getDate());
			if (theYear != null)
				theYearsBox.setSelectedItem(Integer.toString(theYear.getDate().getYear()));
			else 
				theYearsBox.setSelectedItem(null);
		}
	}
	
	/* Report Types */
	public enum ReportType {
		ASSET,
		INCOME,
		TAX,
		TRANSACTION,
		INSTANT,
		MARKET;
	}
}
