package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.data.DataSet;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.DataList;

public class TaxYearSelect implements ItemListener {
	/* Members */
	private JPanel			thePanel		= null;
	private financePanel	theParent		= null;
	private View			theView			= null;
	private JComboBox		theYearsBox		= null;
	private JCheckBox		theShowDeleted	= null;
	private TaxYear.List	theTaxYears		= null;
	private YearState 		theState   	    = null;
	private YearState      	theSavePoint    = null;
	private boolean			yearsPopulated	= false;
	private boolean			refreshingData	= false;
	
	/* Access methods */
	public JPanel	getPanel()		{ return thePanel; }
	public TaxYear	getTaxYear()	{ return theState.getTaxYear(); }
	public boolean	doShowDeleted()	{ return theState.doShowDeleted(); }
	
	/* Constructor */
	public TaxYearSelect(View pView, financePanel pMaint) {
		JLabel mySelect;
		
		/* Store table and view details */
		theView 	  = pView;
		theParent	  = pMaint;
		
		/* Create initial state */
		theState = new YearState();

		/* Initialise the data from the view */
		refreshData();
		
		/* Create the labels */
		mySelect 		= new JLabel("Select Year:");

		/* Create the combo boxes */
		theYearsBox  	= new JComboBox();

		/* Create the combo boxes */
		theShowDeleted  = new JCheckBox("ShowDeleted");
		theShowDeleted.setSelected(theState.doShowDeleted());
		
		/* Add item listeners */
		theYearsBox.addItemListener(this);
		theShowDeleted.addItemListener(this);

		/* Create the selection panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
				  	.createTitledBorder("Selection"));
		
		/* Create the layout for the panel */
	    GroupLayout myLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addContainerGap()
    			.addComponent(mySelect)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    			.addComponent(theYearsBox)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    			.addComponent(theShowDeleted)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(mySelect)
	                .addComponent(theYearsBox)
	                .addComponent(theShowDeleted)
        );            
	}
	
	/**
	 *  Create SavePoint
	 */
	public void createSavePoint() {
		/* Create the savePoint */
		theSavePoint = new YearState(theState);
	}

	/**
	 *  Restore SavePoint
	 */
	public void restoreSavePoint() {
		/* Restore the savePoint */
		theState = new YearState(theSavePoint);
		
		/* Apply the state */
		theState.applyState();		
	}

	/* refreshData */
	public void refreshData() {
		DataSet		myData;
		TaxYear  	myYear;
		TaxYear		myFirst;

		DataList<TaxYear>.ListIterator 		myYearIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access years and regimes */
		theTaxYears 	= myData.getTaxYears();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have years already populated */
		if (yearsPopulated) {	
			/* If we have a selected year */
			if (getTaxYear() != null) {
				/* Find it in the new list */
				theState.setTaxYear(theTaxYears.searchFor(getTaxYear().getDate()));
			}
			
			/* Remove the years */
			theYearsBox.removeAllItems();
			yearsPopulated = false;
		}
		
		/* Create a Tax Year iterator */
		myYearIterator 	= theTaxYears.listIterator(true);
		myFirst 		= null;
		
		/* Add the Tax Years to the years box in reverse order */
		while ((myYear  = myYearIterator.previous()) != null) {
			/* If the year is not deleted */
			if ((!doShowDeleted()) &&
				(myYear.isDeleted())) continue;
			
			/* Note the first in the list */
			if (myFirst == null) myFirst = myYear;
			
			/* Add the item to the list */
			theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
			yearsPopulated = true;
		}
		
		/* If we have a selected year */
		if (getTaxYear() != null) {
			/* Select it in the new list */
			theYearsBox.setSelectedItem(Integer.toString(getTaxYear().getDate().getYear()));
		}
		
		/* Else we have no year currently selected */
		else if (yearsPopulated) {
			/* Select the first account */
			theYearsBox.setSelectedIndex(0);
			theState.setTaxYear(myFirst);
		}
		
		/* Note that we have finished refreshing data */
		refreshingData = false;
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		String                myName;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the years box */
		if (evt.getSource() == (Object)theYearsBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Select the new year and notify the change */
				theState.setTaxYear(theTaxYears.searchFor(myName));
				theParent.notifySelection(this);
			}
		}
		
		/* If this event relates to the showDeleted box */
		if (evt.getSource() == (Object)theShowDeleted) {
			/* Note the new criteria and re-build lists */
			theState.setDoShowDeleted(theShowDeleted.isSelected());
			refreshData();
		}
	}

	/* SavePoint values */
	private class YearState {
		/* Members */
		private TaxYear	theTaxYear		= null;
		private boolean	doShowDeleted	= false;
		
		/* Access methods */
		private TaxYear 	getTaxYear() 	{ return theTaxYear; }
		private boolean 	doShowDeleted() { return doShowDeleted; }

		/**
		 * Constructor
		 */
		private YearState() {
			theTaxYear = null;
		}
		
		/**
		 * Constructor
		 * @param pState state to copy from
		 */
		private YearState(YearState pState) {
			theTaxYear 		= pState.getTaxYear();
			doShowDeleted	= pState.doShowDeleted();
		}
		
		/**
		 * Set new Tax Year
		 * @param pYear the new Tax Year 
		 */
		private void setTaxYear(TaxYear pYear) {
			/* Set the new year and apply State */
			theTaxYear = pYear;
		}
		
		/**
		 * Set doShowDeleted indication
		 */
		private void setDoShowDeleted(boolean doShowDeleted) {
			/* Adjust the flag */
			this.doShowDeleted = doShowDeleted;
		}
		
		/**
		 *  Apply the State
		 */
		private void applyState() {
			/* Adjust the lock-down */
			if (theTaxYear != null)
				theYearsBox.setSelectedItem(Integer.toString(theTaxYear.getDate().getYear()));
			else 
				theYearsBox.setSelectedItem(null);
			theShowDeleted.setSelected(doShowDeleted);
		}
	}	
}
