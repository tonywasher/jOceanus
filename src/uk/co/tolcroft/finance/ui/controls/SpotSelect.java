package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.ui.FinanceInterfaces.*;

public class SpotSelect implements ItemListener,
								   ActionListener,
								   ChangeListener {
	/* Members */
	private JPanel					thePanel		= null;
	private financePanel  			theParent		= null;
	private View					theView			= null;
	private SpinnerDateModel        theModel        = null;
	private JSpinner                theDateBox      = null;
	private JCheckBox				theShowClosed   = null;
	private JButton					theNext   		= null;
	private JButton					thePrev   		= null;
	private SpotState				theState		= null;
	private SpotState				theSavePoint	= null;
	private boolean					doShowClosed	= false;
	
	/* Access methods */
	public JPanel   getPanel()  	{ return thePanel; }
	public Date		getDate()		{ return theState.getDate(); }
	public boolean	getShowClosed() { return doShowClosed; }
				
	/* Constructor */
	public SpotSelect(View pView, financePanel pTable) {
		
		/* Store table and view details */
		theView 	  = pView;
		theParent	  = pTable;
		
		/* Create the check box */
		theShowClosed = new JCheckBox("Show Closed");
		theShowClosed.setSelected(doShowClosed);
		
		/* Create the DateSpinner Model and Box */
		theModel   	= new SpinnerDateModel();
		theDateBox 	= new JSpinner(theModel);
		
		/* Create the Buttons */
		theNext   	= new JButton("Next");
		thePrev		= new JButton("Prev");
		
		/* Initialise the data from the view */
		refreshData();
		
		/* Create initial state */
		theState = new SpotState();
	
		/* Set the format of the date */
		theDateBox.setEditor(new JSpinner.DateEditor(theDateBox, "dd-MMM-yyyy"));
			
		/* Create the panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Spot Selection"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theNext)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(thePrev)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	                .addComponent(theShowClosed))
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(theDateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theNext)
	                .addComponent(thePrev)
	                .addComponent(theShowClosed))
	    );

		/* Apply the current state */
		theState.applyState();

		/* Add the listener for item changes */
		theModel.addChangeListener(this);
		theShowClosed.addItemListener(this);
		theNext.addActionListener(this);
		thePrev.addActionListener(this);
	}
	
	/* refresh data */
	public void refreshData() {
		Date.Range   myRange;
		
		/* Access the data */
		myRange = theView.getRange();
		
		/* Set the range for the Date Spinner */
		setRange(myRange);				
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
		boolean bLock = theParent.hasUpdates();
		
		theNext.setEnabled(theState.getNextDate() != null);
		thePrev.setEnabled(theState.getPrevDate() != null);
		
		theDateBox.setEnabled(!bLock);
	}
	
	/**
	 *  Create SavePoint
	 */
	public void createSavePoint() {
		/* Create the savePoint */
		theSavePoint = new SpotState(theState);
	}

	/**
	 *  Restore SavePoint
	 */
	public void restoreSavePoint() {
		/* Restore the savePoint */
		theState = new SpotState(theSavePoint);
		
		/* Apply the state */
		theState.applyState();		
	}

	/**
	 * Set Adjacent dates
	 * @param pPrev the previous Date
	 * @param pNext the next Date
	 */
	public void setAdjacent(Date pPrev, Date pNext) {
		/* Record the dates */
		theState.setAdjacent(pPrev, pNext);
	}

	/* actionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {

		/* If this event relates to the Next button */
		if (evt.getSource() == (Object)theNext) {
			/* Set the Date to be the Next date */
			theState.setNext();
		}
		
		/* If this event relates to the previous button */
		else if (evt.getSource() == (Object)thePrev) {
			/* Set the Date to be the Previous date */
			theState.setPrev();
		}
		
		/* No need to notify the parent since this will have been done by state update */
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		boolean               bChange = false;

		/* If this event relates to the showClosed box */
		if (evt.getSource() == (Object)theShowClosed) {
			/* Note the new criteria and re-build lists */
			doShowClosed = theShowClosed.isSelected();
			bChange      = true;
		}
		
		/* If we have a change, alert the table */
		if (bChange) { theParent.notifySelection(this); }
	}
	
	/* stateChanged listener event */
	public void stateChanged(ChangeEvent evt) {
		boolean bChange = false;
		
		/* If this event relates to the start box */
		if (evt.getSource() == (Object)theModel) {
			theState.setDate(theModel);
			bChange    = true;
		}			
				
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
	}
	
	/* SavePoint values */
	private class SpotState {
		/* Members */
		private Date	theDate		= null;
		private Date	theNextDate	= null;
		private Date	thePrevDate	= null;
		
		/* Access methods */
		private Date	getDate() 		{ return theDate; }
		private Date 	getNextDate() 	{ return theNextDate; }
		private Date 	getPrevDate() 	{ return thePrevDate; }

		/**
		 * Constructor
		 */
		private SpotState() {
			theDate = new Date();
		}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private SpotState(SpotState pState) {
			theDate 	= new Date(pState.getDate());
			if (pState.getNextDate() != null)
				theNextDate = new Date(pState.getNextDate());
			if (pState.getPrevDate() != null)
				thePrevDate = new Date(pState.getPrevDate());
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
		 * Set Next Date
		 */
		private void setNext() {
			/* Copy date */
			theDate = new Date(theNextDate);
			applyState();
		}

		/**
		 * Set Previous Date
		 */
		private void setPrev() {
			/* Copy date */
			theDate = new Date(thePrevDate);
			applyState();
		}

		/**
		 * Set Adjacent dates
		 * @param pPrev the previous Date
		 * @param pNext the next Date
		 */
		private void setAdjacent(Date pPrev, Date pNext) {
			/* Record the dates */
			thePrevDate = pPrev;
			theNextDate = pNext;
			
			/* Adjust values */
			setLockDown();				
		}

		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			setLockDown();
			theModel.setValue(theDate.getDate());
		}
	}	
}
