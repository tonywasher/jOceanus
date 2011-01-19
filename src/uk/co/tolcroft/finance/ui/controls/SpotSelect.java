package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;

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
	private Date					theSpotDate		= null;
	private Date					theNextDate		= null;
	private Date					thePrevDate		= null;
	private boolean					doShowClosed	= false;
	
	/* Access methods */
	public JPanel   getPanel()  	{ return thePanel; }
	public Date		getDate()		{ return theSpotDate; }
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
		
		/* Limit the spinner to the Range */
		theModel.setValue(new java.util.Date());
		theSpotDate = new Date(theModel.getDate());
	
		/* Set the format of the date */
		theDateBox.setEditor(new JSpinner.DateEditor(theDateBox, "dd-MMM-yyyy"));
	
		/* Add the listener for item changes */
		theModel.addChangeListener(this);
		theShowClosed.addItemListener(this);
		theNext.addActionListener(this);
		thePrev.addActionListener(this);
		
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

		/* Initiate lock-down mode */
		setLockDown();
	}
	
	/* refresh data */
	public void refreshData() {
		Date.Range   myRange;
		
		/* Access the data */
		myRange = theView.getRange();
		
		/* Set the range for the Date Spinner */
		setRange(myRange);				
	}

	/* Set Adjacent dates */
	public void setAdjacent(Date pPrev, Date pNext) {
		/* Record the dates */
		thePrevDate = pPrev;
		theNextDate = pNext;
		
		/* Adjust values */
		setLockDown();				
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
		
		theNext.setEnabled(theNextDate != null);
		thePrev.setEnabled(thePrevDate != null);
		
		theDateBox.setEnabled(!bLock);
	}
	
	/* actionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {
		boolean     bChange = false;
		Calendar	myDate;

		/* If this event relates to the Next button */
		if (evt.getSource() == (Object)theNext) {
			/* Access new date */
			myDate = Calendar.getInstance();
			myDate.setTime(theNextDate.getDate());
			
			/* Set the new date */
			theModel.setValue(myDate.getTime());
			theSpotDate = new Date(theModel.getDate());
			bChange = true;
		}
		
		/* If this event relates to the previous button */
		else if (evt.getSource() == (Object)thePrev) {
			/* Access new date */
			myDate = Calendar.getInstance();
			myDate.setTime(thePrevDate.getDate());
			
			/* Set the new date */
			theModel.setValue(myDate.getTime());
			theSpotDate = new Date(theModel.getDate());
			bChange = true;
		}

		/* If we have a change, alert the table */
		if (bChange) { theParent.notifySelection(this); }
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
			theSpotDate = new Date(theModel.getDate());
			bChange    = true;
		}			
				
		/* If we have a change, notify the main program */
		if (bChange) { theParent.notifySelection(this); }
	}
}
