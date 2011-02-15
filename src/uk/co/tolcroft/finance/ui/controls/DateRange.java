package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
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
import uk.co.tolcroft.models.*;

public class DateRange  implements 	ItemListener,
									ActionListener,
									ChangeListener{
	/* Members */
	private financePanel		theParent  	   = null;
	private SpinnerDateModel    theModel       = null;
	private JSpinner            theStartBox    = null;
	private JComboBox           thePeriodBox   = null;
	private JPanel              thePanel       = null;
	private JButton             theNextButton  = null;
	private JButton             thePrevButton  = null;
	private JLabel              theStartLabel  = null;
	private JLabel              thePeriodLabel = null;
	private Date          		theStartDate   = null;
	private Date          		theFirstDate   = null;
	private Date          		theFinalDate   = null;
	private DatePeriod          thePeriod      = DatePeriod.ONEMONTH;
	private Date.Range			theRange       = null;
	private boolean        		isPrevOK	   = true;
	private boolean				isNextOK	   = true;
	private boolean				refreshingData = false;

	/* Access methods */
	public 	JPanel            	getPanel()   { return thePanel; }
	public	Date.Range      	getRange()   { return theRange; }

	/* Period descriptions */
	private static final String OneMonth    = "One Month";
	private static final String QuarterYear = "Quarter Year";
	private static final String HalfYear    = "Half Year";
	private static final String OneYear     = "One Year";
	private static final String Unlimited   = "Unlimited";

	/* Constructor */
	public DateRange(financePanel pParent) {
		
		/* Create the DateSpinner Model */
		theModel 	= new SpinnerDateModel();
		theParent 	= pParent;
	
		/* Create the boxes */
		theStartBox   = new JSpinner(theModel);
		thePeriodBox  = new JComboBox();
	
		/* Limit the spinner to the Range */
		theModel.setValue(new java.util.Date());
		theStartDate = new Date(theModel.getDate());
		setRange(null);
	
		/* Set the format of the date */
		theStartBox.setEditor(new JSpinner.DateEditor(theStartBox, "dd-MMM-yyyy"));
	
		/* Add the PeriodTypes to the period box */
		thePeriodBox.addItem(OneMonth);
		thePeriodBox.addItem(QuarterYear);
		thePeriodBox.addItem(HalfYear);
		thePeriodBox.addItem(OneYear);
		thePeriodBox.addItem(Unlimited);
		thePeriodBox.setSelectedIndex(0);
		
		/* Create the labels */
		theStartLabel = new JLabel("Start Date:");
		thePeriodLabel = new JLabel("Period:");
	
		/* Create the buttons */
		theNextButton = new JButton("Next");
		thePrevButton = new JButton("Prev");
	
		/* Add the listener for item changes */
		thePeriodBox.addItemListener(this);
		theNextButton.addActionListener(this);
		thePrevButton.addActionListener(this);
		theModel.addChangeListener(this);
	
		/* Create the panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Date Range Selection"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(theStartLabel)
	                .addGap(18, 18, 18)
	                .addComponent(theStartBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
	                .addGap(29, 29, 29)
	                .addComponent(theNextButton)
	                .addGap(26, 26, 26)
	                .addComponent(thePrevButton)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
	                .addComponent(thePeriodLabel)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addGap(25, 25, 25))
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(theStartLabel)
	                .addComponent(theStartBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theNextButton)
	                .addComponent(thePrevButton)
	                .addComponent(thePeriodLabel)
	                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	    );

		/* Initiate lock-down mode */
		setLockDown();
	}

	public  void setRange(Date.Range pRange) {
		Date myStart = null;
		
		theFirstDate = (pRange == null) ? null : pRange.getStart();
		theFinalDate = (pRange == null) ? null : pRange.getEnd();
		if (theFirstDate != null) {
			myStart = new Date(theFirstDate);
			myStart.adjustDay(-1);
		}
		theModel.setStart((theFirstDate == null) ? null : myStart.getDate());
		theModel.setEnd((theFinalDate == null) ? null : theFinalDate.getDate());
	}
	

	/* Copy date selection from other box */
	public void setSelection(DateRange pSource) {
		DatePeriod myPeriod = pSource.thePeriod;
		
		/* Set the refreshing data flag */
		refreshingData = true;
		
		/* Set the new date */
		theModel.setValue(pSource.theStartDate.getDate());
		theStartDate = new Date(pSource.theStartDate);
		
		/* Select the correct period */
		thePeriodBox.setSelectedItem(getPeriodName(myPeriod));
		thePeriod = myPeriod;
		
		/* Build the range */
		theRange = buildRange();
		
		/* Reset the refreshing data flag */
		refreshingData = false;
	}

	/* ItemStateChanged listener event */
	public String getPeriodName(DatePeriod pPeriod) {
		String myName = null;
		
		/* Switch on period to select name */
		switch(pPeriod) {
			case ONEMONTH:    myName = OneMonth;    break;
			case QUARTERYEAR: myName = QuarterYear; break;
			case HALFYEAR:    myName = HalfYear;    break;
			case ONEYEAR:     myName = OneYear;     break;
			case UNLIMITED:   myName = Unlimited;   break;
		}
		
		/* Return the name */
		return myName;
	}
	
	/* Lock/Unlock the selection */
	public void setLockDown() {
		boolean bLock = theParent.hasUpdates();
		
		/* Lock/Unlock the selection */
		theStartBox.setEnabled(!bLock);
		thePeriodBox.setEnabled(!bLock);
		theNextButton.setEnabled((!bLock) && isNextOK);
		thePrevButton.setEnabled((!bLock) && isPrevOK);
	}

	/* adjust a date by a period */
	private Date adjustDate(Date 	pDate,
			                boolean bForward) {
		Date myDate;
	
		/* Initialise the date */
		myDate = new Date(pDate);
	
		/* Switch on the period */
		switch (thePeriod) {
			case ONEMONTH:
				myDate.adjustMonth((bForward) ? 1 : -1);
				break;
			case QUARTERYEAR:
				myDate.adjustMonth((bForward) ? 3 : -3);
				break;
			case HALFYEAR:
				myDate.adjustMonth((bForward) ? 6 : -6);
				break;
			case ONEYEAR:
				myDate.adjustYear((bForward) ? 1 : -1);
				break;
			case UNLIMITED:
				if (!bForward) myDate = new Date(theFirstDate);
				break;
		}
		
		/* Make sure that we do not go beyond the date range */
		if ((theFirstDate != null) &&
			(myDate.compareTo(theFirstDate) < 0)) myDate = theFirstDate;
		if ((theFinalDate != null) &&
			(myDate.compareTo(theFinalDate) > 0)) myDate = theFinalDate;
	
		/* Return the date */
		return myDate;
	}

	/* build the range represented by the selection */
	private Date.Range buildRange() {
		Date myEnd;
	
		/* If we are unlimited */
		if (thePeriod == DatePeriod.UNLIMITED) {
			/* Set end date as last possible date*/
			myEnd = theFinalDate;
			
			/* Note that previous and next are not allowed */
			isNextOK = false;
			isPrevOK = (Date.differs(theStartDate, theFirstDate));
		}
		
		/* else we have to calculate the date */
		else{
			/* Initialise the end date */
			myEnd = new Date(theStartDate);
	
			/* Adjust the date */
			myEnd = adjustDate(myEnd, true);
			
			/* Assume that both next and prev are OK */
			isNextOK = isPrevOK = true;
			
			/* If we have not hit the start date disable prev */
			if (theStartDate.compareTo(theFirstDate) == 0)
				isPrevOK = false;
			
			/* If we have not hit the final date shift back one day */
			if (myEnd.compareTo(theFinalDate) != 0)
				myEnd.adjustDay(-1);
			else
				isNextOK = false;
		}
		
		/* Adjust the lock-down */
		setLockDown();
		theModel.setValue(theStartDate.getDate());
		
		/* Return the date range */
		return new Date.Range(theStartDate, myEnd);
	}

	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String                myName;
		boolean               bChange = false;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the period box */
		if (evt.getSource() == (Object)thePeriodBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Determine the new period */
				bChange = true;
				if (myName == OneMonth)	        thePeriod = DatePeriod.ONEMONTH;
				else if (myName == QuarterYear) thePeriod = DatePeriod.QUARTERYEAR;
				else if (myName == HalfYear)    thePeriod = DatePeriod.HALFYEAR;
				else if (myName == OneYear)     thePeriod = DatePeriod.ONEYEAR;
				else if (myName == Unlimited)   thePeriod = DatePeriod.UNLIMITED;
				else bChange = false;
			
				/* Build the new range */
				if (bChange) theRange = buildRange();
			}
		}
	
		/* If we have a change, alert the tab group */
		if (bChange) { theParent.notifySelection(this); }
	}

	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {
		boolean               bChange = false;

		/* If this event relates to the next button */
		if (evt.getSource() == (Object)theNextButton) {
			/* Calculate the new start date */
			theStartDate = adjustDate(theStartDate, true);
			bChange      = true;
			
			/* Build the new range */
			if (bChange) theRange = buildRange();
		}
		
		/* If this event relates to the previous button */
		else if (evt.getSource() == (Object)thePrevButton) {
			/* Calculate the new start date */
			theStartDate = adjustDate(theStartDate, false);
			bChange      = true;
			
			/* Build the new range */
			if (bChange) theRange = buildRange();
		}
		
		/* If we have a change, alert the tab group */
		if (bChange) { theParent.notifySelection(this); }
	}

	/* stateChanged listener event */
	public void stateChanged(ChangeEvent evt) {
		boolean bChange = false;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the start box */
		if (evt.getSource() == (Object)theModel) {
			theStartDate = new Date(theModel.getDate());
			bChange      = true;
		}			
				
		/* Build the new range */
		if (bChange) theRange = buildRange();
	
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
	}

	/* DatePeriod values */
	private enum DatePeriod {
		ONEMONTH,
		QUARTERYEAR,
		HALFYEAR,
		ONEYEAR,
		UNLIMITED;
	}	
}
