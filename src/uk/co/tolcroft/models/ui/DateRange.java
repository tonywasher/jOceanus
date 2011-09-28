package uk.co.tolcroft.models.ui;

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

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ui.StdInterfaces.*;

public class DateRange  implements 	ItemListener,
									ActionListener,
									ChangeListener{
	/* Members */
	private stdPanel			theParent  	   = null;
	private SpinnerDateModel    theModel       = null;
	private JSpinner            theStartBox    = null;
	private JComboBox           thePeriodBox   = null;
	private JPanel              thePanel       = null;
	private JButton             theNextButton  = null;
	private JButton             thePrevButton  = null;
	private JLabel              theStartLabel  = null;
	private JLabel              thePeriodLabel = null;
	private Date          		theFirstDate   = null;
	private Date          		theFinalDate   = null;
	private DateRangeState 		theState   	   = null;
	private DateRangeState      theSavePoint   = null;
	private boolean				refreshingData = false;

	/* Access methods */
	public 	JPanel            	getPanel()   	{ return thePanel; }
	public	Date.Range      	getRange()   	{ return theState.getRange(); }

	/* Period descriptions */
	private static final String OneMonth    = "One Month";
	private static final String QuarterYear = "Quarter Year";
	private static final String HalfYear    = "Half Year";
	private static final String OneYear     = "One Year";
	private static final String Unlimited   = "Unlimited";

	/* Constructor */
	public DateRange(stdPanel pParent) {
		
		/* Create the DateSpinner Model */
		theModel 	= new SpinnerDateModel();
		theParent 	= pParent;
	
		/* Create the boxes */
		theStartBox   = new JSpinner(theModel);
		thePeriodBox  = new JComboBox();
	
		/* Set the format of the date */
		theStartBox.setEditor(new JSpinner.DateEditor(theStartBox, "dd-MMM-yyyy"));
	
		/* Create initial state and limit the spinner to the Range */
		theState = new DateRangeState();
		setOverallRange(null);
	
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

		/* Apply the current state */
		theState.applyState();

		/* Add the listeners for item changes */
		thePeriodBox.addItemListener(this);
		theNextButton.addActionListener(this);
		thePrevButton.addActionListener(this);
		theModel.addChangeListener(this);
	}

	/**
	 * Set the overall range for the control
	 * @param pRange
	 */
	public  void setOverallRange(Date.Range pRange) {
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
	

	/**
	 *  Copy date selection from other box
	 *  @param pSource the source box
	 */
	public void setSelection(DateRange pSource) {
		DateRangeState myState = pSource.theState;
		
		/* Set the refreshing data flag */
		refreshingData = true;
		
		/* Accept this state */
		theState = new DateRangeState(myState);
		
		/* Build the range and apply the state */
		theState.buildRange();
		theState.applyState();
		
		/* Reset the refreshing data flag */
		refreshingData = false;
	}

	/**
	 *  Create SavePoint
	 */
	public void createSavePoint() {
		/* Create the savePoint */
		theSavePoint = new DateRangeState(theState);
	}

	/**
	 *  Restore SavePoint
	 */
	public void restoreSavePoint() {
		/* Restore the savePoint */
		theState = new DateRangeState(theSavePoint);
		
		/* Build the range and apply the state */
		theState.buildRange();
		theState.applyState();		
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
		theNextButton.setEnabled((!bLock) && theState.isNextOK());
		thePrevButton.setEnabled((!bLock) && theState.isPrevOK());
	}

	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String      myName;
		DatePeriod	myPeriod	= null;
		boolean     bChange 	= false;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the period box */
		if (evt.getSource() == (Object)thePeriodBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Determine the new period */
				bChange = true;
				if (myName == OneMonth)	        myPeriod = DatePeriod.ONEMONTH;
				else if (myName == QuarterYear) myPeriod = DatePeriod.QUARTERYEAR;
				else if (myName == HalfYear)    myPeriod = DatePeriod.HALFYEAR;
				else if (myName == OneYear)     myPeriod = DatePeriod.ONEYEAR;
				else if (myName == Unlimited)   myPeriod = DatePeriod.UNLIMITED;
				else bChange = false;
			
				/* If we have a change */
				if (bChange) {
					/* Apply period and build the range */
					theState.setPeriod(myPeriod);
					theState.buildRange();
				}
			}
		}
	
		/* If we have a change, alert the tab group */
		if (bChange) { theParent.notifySelection(this); }
	}

	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {
		/* If this event relates to the next button */
		if (evt.getSource() == (Object)theNextButton) {
			/* Set the next date */
			theState.setNextDate();
		}
		
		/* If this event relates to the previous button */
		else if (evt.getSource() == (Object)thePrevButton) {
			/* Set the previous date */
			theState.setPreviousDate();
		}
		
		/* No need to notify the parent since this will have been done by state update */
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
			bChange      = true;
		}			
				
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
	}

	/* SavePoint values */
	private class DateRangeState {
		/* Members */
		private Date 		theStartDate 	= null;
		private DatePeriod 	thePeriod 		= null;
		private Date.Range	theRange		= null;
		private boolean		isNextOK		= false;
		private boolean		isPrevOK		= false;
		
		/* Access methods */
		private Date 		getStartDate()	{ return theStartDate; }
		private DatePeriod 	getPeriod() 	{ return thePeriod; }
		private Date.Range 	getRange() 		{ return theRange; }
		private boolean 	isNextOK() 		{ return isNextOK; }
		private boolean 	isPrevOK() 		{ return isPrevOK; }

		/**
		 * Constructor
		 */
		private DateRangeState() {
			theStartDate = new Date();
			thePeriod	 = DatePeriod.ONEMONTH;
		}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private DateRangeState(DateRangeState pState) {
			theStartDate = new Date(pState.getStartDate());
			thePeriod	 = pState.getPeriod();
		}
		
		/**
		 * Set new Period
		 * @param pPeriod the new period 
		 */
		private void setPeriod(DatePeriod pPeriod) {
			/* Adjust the period and build the new range */
			thePeriod = pPeriod;
			buildRange();
		}
		
		/**
		 * Set new Date
		 * @param pModel the Spinner with the new date 
		 */
		private void setDate(SpinnerDateModel pModel) {
			/* Adjust the date and build the new range */
			theStartDate = new Date(theModel.getDate());
			buildRange();
		}
		
		/**
		 * Set next Date
		 */
		private void setNextDate() {
			/* Adjust the date and build the new range */
			theStartDate = adjustDate(theStartDate, true);
			buildRange();
			applyState();
		}
		
		/**
		 * Set previous Date
		 */
		private void setPreviousDate() {
			/* Adjust the date and build the new range */
			theStartDate = adjustDate(theStartDate, false);
			buildRange();
			applyState();
		}
		
		/**
		 * Build the range represented by the selection
		 */
		private void buildRange() {
			Date myEnd;
		
			/* If we are unlimited */
			if (thePeriod == DatePeriod.UNLIMITED) {
				/* Set end date as last possible date*/
				myEnd = theFinalDate;
				
				/* Note that next is not allowed */
				isNextOK = false;
				
				/* Previous is only allowed if we are later than the first date */
				isPrevOK = (Date.differs(theStartDate, theFirstDate).isDifferent());
			}
			
			/* else we have to calculate the date */
			else{
				/* Initialise the end date */
				myEnd = new Date(theStartDate);
		
				/* Adjust the date */
				myEnd = adjustDate(myEnd, true);
				
				/* Assume that both next and prev are OK */
				isNextOK = isPrevOK = true;
				
				/* Previous is only allowed if we are later than the first date */
				if (theStartDate.compareTo(theFirstDate) == 0)
					isPrevOK = false;
				
				/* If we have not hit the final date shift back one day */
				if (myEnd.compareTo(theFinalDate) != 0)
					myEnd.adjustDay(-1);
				
				/* Else we are the final date so disable Next */
				else isNextOK = false;
			}
			
			/* Create the range */
			theRange = new Date.Range(theStartDate, myEnd);
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

		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			setLockDown();
			theModel.setValue(theStartDate.getDate());
			thePeriodBox.setSelectedItem(getPeriodName(thePeriod));
		}
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
