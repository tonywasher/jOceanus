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
	private Date				theRepDate		= null;
	private TaxYear	 			theYear			= null;
	private ReportType			theReport		= null;
	private TaxYear.List		theYears		= null;
	private boolean				yearsPopulated 	= false;
	private boolean				refreshingData  = false;
	
	/* Access methods */
	public  JPanel      getPanel()      { return thePanel; }
	public 	ReportType 	getReportType() { return theReport; }
	public 	TaxYear 	getTaxYear()    { return theYear; }
	public	Date 	    getReportDate() { return theRepDate; }
				
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
		theReport = ReportType.INSTANT;
		
		/* Create the labels */
		theRepLabel  = new JLabel("Report:");
		theYearLabel = new JLabel("Year:");
		theDateLabel = new JLabel("Date:");
		
		/* Create the print button */
		thePrintButton = new JButton("Print");
		thePrintButton.addActionListener(this);
		
		/* Limit the spinner to the Range */
		theModel.setValue(new java.util.Date());
		theRepDate = new Date(theModel.getDate());
	
		/* Set the format of the date */
		theDateBox.setEditor(new JSpinner.DateEditor(theDateBox, "dd-MMM-yyyy"));
	
		/* Add the listener for item changes */
		theReportBox.addItemListener(this);
		theYearsBox.addItemListener(this);
		theModel.addChangeListener(this);
		
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

		/* Initiate lock-down mode */
		setLockDown();
	}
	
	/* refresh data */
	public void refreshData(AnalysisYear.List pList) {
		DataSet			myData;
		AnalysisYear  	myYear;
		Date.Range  	myRange;
		
		SortedList<AnalysisYear>.ListIterator myIterator;
		
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
			if (theYear != null) {
				/* Find it in the new list */
				theYear = theYears.searchFor(theYear.getDate());
			}
			
			/* Remove the types */
			theYearsBox.removeAllItems();
			yearsPopulated = false;
		}
		
		/* If we have a list */
		if (pList != null) {
			/* Access the iterator */
			myIterator = pList.listIterator();
		
			/* Add the Year values to the years box in reverse order */
			while ((myYear  = myIterator.previous()) != null) {
				/* Add the item to the list */
				theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
				yearsPopulated = true;
			}
		
			/* If we have a selected year */
			if (theYear != null) {
				/* Select it in the new list */
				theYearsBox.setSelectedItem(Integer.toString(theYear.getDate().getYear()));
			}
		
			/* Else we have no year currently selected */
			else if (yearsPopulated) {
				/* Select the first year */
				theYearsBox.setSelectedIndex(0);
				theYear = myIterator.peekLast().getYear();
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
	
	/* Lock/Unlock the selection */
	public void setLockDown() {
		boolean isDate    = ((theReport == ReportType.INSTANT) ||
				             (theReport == ReportType.MARKET));
		boolean isNull    = (theReport == null);
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
		String                myName;
		boolean               bChange = false;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the years box */
		if (evt.getSource() == (Object)theYearsBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Select the new year */
				theYear = theYears.searchFor(myName);
				bChange = true;
			}
		}
						
		/* If this event relates to the report box */
		if (evt.getSource() == (Object)theReportBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Determine the new report */
				bChange = true;
				if (myName == Assets)	      	theReport = ReportType.ASSET;
				else if (myName == IncomeExp) 	theReport = ReportType.INCOME;
				else if (myName == Transaction)	theReport = ReportType.TRANSACTION;
				else if (myName == Taxation)  	theReport = ReportType.TAX;
				else if (myName == Instant)   	theReport = ReportType.INSTANT;
				else if (myName == Market)    	theReport = ReportType.MARKET;
				else bChange = false;
			}
		}
		
		/* If we have a change, alert the table */
		if (bChange) { theParent.notifySelection(this); }
	}
	
	/* stateChanged listener event */
	public void stateChanged(ChangeEvent evt) {
		boolean bChange = false;
		
		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the start box */
		if (evt.getSource() == (Object)theModel) {
			theRepDate = new Date(theModel.getDate());
			bChange    = true;
		}			
				
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
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
