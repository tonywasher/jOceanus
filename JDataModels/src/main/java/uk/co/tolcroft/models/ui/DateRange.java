/*******************************************************************************
 * Copyright 2012 Tony Washer
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.models.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import net.sourceforge.JDateButton.JDateButton;

import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ui.StdInterfaces.*;

public class DateRange {
	/* Members */
	private	DateRange			theSelf		   	= this;
	private stdPanel			theParent  	   	= null;
	private JDateButton			theDateButton 	= null;
	private JComboBox           thePeriodBox   	= null;
	private JPanel              thePanel       	= null;
	private JButton             theNextButton  	= null;
	private JButton             thePrevButton  	= null;
	private JLabel              theStartLabel  	= null;
	private JLabel              thePeriodLabel 	= null;
	private DateDay          	theFirstDate   	= null;
	private DateDay          	theFinalDate   	= null;
	private DateRangeState 		theState   	   	= null;
	private DateRangeState      theSavePoint   	= null;
	private boolean				refreshingData 	= false;

	/* Access methods */
	public 	JPanel            	getPanel()   	{ return thePanel; }
	public	DateDay.Range      	getRange()   	{ return theState.getRange(); }

	/* Constructor */
	public DateRange(stdPanel pParent) {
		DateListener myListener = new DateListener();
		
		/* Store parameters */
		theParent 	= pParent;
	
		/* Create the boxes */
		thePeriodBox  = new JComboBox();
	
		/* Create the DateButton */
		theDateButton = new JDateButton();
		
		/* Create initial state and limit the spinner to the Range */
		theState = new DateRangeState();
		setOverallRange(null);
		theState.buildRange();
	
		/* Add the PeriodTypes to the period box */
		for (DatePeriod myPeriod : DatePeriod.values()) { thePeriodBox.addItem(myPeriod); }
		
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
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(thePeriodLabel)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theNextButton)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(thePrevButton)
	                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(theStartLabel)
	                .addComponent(theDateButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(thePeriodLabel)
	                .addComponent(thePeriodBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theNextButton)
	                .addComponent(thePrevButton))
	    );

		/* Apply the current state */
		theState.applyState();

		/* Add the listeners for item changes */
		thePeriodBox.addItemListener(myListener);
		theNextButton.addActionListener(myListener);
		thePrevButton.addActionListener(myListener);
		theDateButton.addPropertyChangeListener(JDateButton.valueDATE, myListener);
	}

	/**
	 * Set the overall range for the control
	 * @param pRange
	 */
	public  void setOverallRange(DateDay.Range pRange) {
		/* Record total possible range */
		theFirstDate = (pRange == null) ? null : pRange.getStart();
		theFinalDate = (pRange == null) ? null : pRange.getEnd();
		
		/* Set up range */
		theDateButton.setSelectableRange((theFirstDate == null) ? null : theFirstDate.getDate(),
										 (theFinalDate == null) ? null : theFinalDate.getDate());
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

	/* getPeriodName event */
	public String getPeriodName(DatePeriod pPeriod) { return pPeriod.toString(); }
	
	/* Lock/Unlock the selection */
	public void setLockDown() {
		boolean bLock = theParent.hasUpdates();
		
		/* Lock/Unlock the selection */
		theDateButton.setEnabled(!bLock);
		thePeriodBox.setEnabled(!bLock);
		theNextButton.setEnabled((!bLock) && theState.isNextOK());
		thePrevButton.setEnabled((!bLock) && theState.isPrevOK());
	}

	/**
	 * The Date Listener
	 */
	private class DateListener implements ActionListener,
										  PropertyChangeListener,
										  ItemListener {
		@Override
		public void itemStateChanged(ItemEvent evt) {
			DatePeriod	myPeriod	= null;

			/* Ignore selection if refreshing data */
			if (refreshingData) return;
		
			/* If this event relates to the period box */
			if (evt.getSource() == (Object)thePeriodBox) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					/* Determine the new period */
					myPeriod = (DatePeriod)evt.getItem();

					/* Apply period and build the range */
					theState.setPeriod(myPeriod);
					theParent.notifySelection(theSelf);
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			Object o = evt.getSource();
			
			/* If this event relates to the next button */
			if (o == theNextButton) {
				/* Set the next date */
				theState.setNextDate();
				theParent.notifySelection(theSelf);
			}
		
			/* If this event relates to the previous button */
			else if (o == thePrevButton) {
				/* Set the previous date */
				theState.setPreviousDate();
				theParent.notifySelection(theSelf);
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			/* if this date relates to the Date button */
			if (evt.getSource() == theDateButton) {
				/* Access the value */
				if (theState.setDate(theDateButton))
					theParent.notifySelection(theSelf);
			}			
		}
	}
	
	/* SavePoint values */
	private class DateRangeState {
		/* Members */
		private DateDay 		theStartDate 	= null;
		private DatePeriod 		thePeriod 		= null;
		private DateDay.Range	theRange		= null;
		private boolean			isNextOK		= false;
		private boolean			isPrevOK		= false;
		
		/* Access methods */
		private DateDay 		getStartDate()	{ return theStartDate; }
		private DatePeriod 		getPeriod() 	{ return thePeriod; }
		private DateDay.Range 	getRange() 		{ return theRange; }
		private boolean 		isNextOK() 		{ return isNextOK; }
		private boolean 		isPrevOK() 		{ return isPrevOK; }

		/**
		 * Constructor
		 */
		private DateRangeState() {
			theStartDate = new DateDay();
			thePeriod	 = DatePeriod.OneMonth;
		}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private DateRangeState(DateRangeState pState) {
			theStartDate = new DateDay(pState.getStartDate());
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
		 * @param pButton the Button with the new date
		 * @return has the date changed 
		 */
		private boolean setDate(JDateButton pButton) {
			/* Adjust the date and build the new range */
			DateDay myDate = new DateDay(pButton.getSelectedDate());
			if (DateDay.differs(myDate, theStartDate).isDifferent()) {
				theStartDate = myDate;
				buildRange();
				return true;
			}
			return false;
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
			DateDay myEnd;
		
			/* If we are unlimited */
			if (thePeriod == DatePeriod.Unlimited) {
				/* Set end date as last possible date*/
				myEnd = theFinalDate;
				
				/* Note that next is not allowed */
				isNextOK = false;
				
				/* Previous is only allowed if we are later than the first date */
				isPrevOK = (DateDay.differs(theStartDate, theFirstDate).isDifferent());
			}
			
			/* else we have to calculate the date */
			else{
				/* Initialise the end date */
				myEnd = new DateDay(theStartDate);
		
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
			theRange = new DateDay.Range(theStartDate, myEnd);
		}
		
		/* adjust a date by a period */
		private DateDay adjustDate(DateDay 	pDate,
				               	   boolean 	bForward) {
			DateDay myDate;
		
			/* Initialise the date */
			myDate = new DateDay(pDate);

			/* If the period is unlimited */
			if (thePeriod == DatePeriod.Unlimited) {
				/* Shift back to first date if required */
				if (!bForward) myDate = new DateDay(theFirstDate);				
			}
			
			/* else we should adjust the date */
			else {
				/* Adjust the date appropriately */
				myDate = thePeriod.adjustDate(pDate, bForward);

				/* Make sure that we do not go beyond the date range */
				if ((theFirstDate != null) &&
					(myDate.compareTo(theFirstDate) < 0)) myDate = theFirstDate;
				if ((theFinalDate != null) &&
					(myDate.compareTo(theFinalDate) > 0)) myDate = theFinalDate;
			}
			
			/* Return the date */
			return myDate;
		}

		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			setLockDown();
			theDateButton.setSelectedDate(theStartDate.getDate());
			thePeriodBox.setSelectedItem(thePeriod);
		}
	}
}
