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
package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ui.StdInterfaces.stdPanel;

public class StatementSelect {
	/* Members */
	private StatementSelect		theSelf			= this;
	private JPanel				thePanel		= null;
	private stdPanel			theParent		= null;
	private JComboBox           theStateBox 	= null;
	private JLabel				theStateLabel	= null;
	private StatementType		theType			= null;
	private boolean				statePopulated	= false;
	private boolean				refreshingData  = false;
	
	/* Access methods */
	public  JPanel      	getPanel()      	{ return thePanel; }
	public 	StatementType 	getStatementType() 	{ return theType; }
				
	/* Statement descriptions */
	private static final String Extract    	= "Extract";
	private static final String Value 		= "Value";
	private static final String Units		= "Units";

	/* Constructor */
	public StatementSelect(stdPanel pParent) {
		StatementListener myListener = new StatementListener();
		
		/* Store table and view details */
		theParent	  = pParent;
		
		/* Create the boxes */
		theStateBox   = new JComboBox();
		
		/* Create the labels */
		theStateLabel  = new JLabel("View:");

		/* Add the listener for item changes */
		theStateBox.addItemListener(myListener);
		
		/* Create the panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Statement View"));

		/* Create the layout for the panel */
	    GroupLayout panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(theStateLabel)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(theStateBox, GroupLayout.PREFERRED_SIZE, 150, GroupLayout.PREFERRED_SIZE)
	                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
	        panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	                .addComponent(theStateLabel)
	                .addComponent(theStateBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	    );
	}
	
	/* Lock/Unlock the selection */
	public void setLockDown() {
		/* Lock/Unlock the selection */
		theStateBox.setEnabled(theType != StatementType.NULL);
	}

	/* setSelection */
	public void setSelection(Account pAccount) {
		String myText = null;
		
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have state already populated */
		if (statePopulated) {	
			/* Remove the types */
			theStateBox.removeAllItems();
			statePopulated = false;
		}
		
		/* If we have an account */
		if (pAccount != null) {
			/* Add Value if the account is Money/Debt */
			if (pAccount.isMoney() || pAccount.isDebt()) 
				theStateBox.addItem(Value);
			else if (theType == StatementType.VALUE)
				theType = StatementType.NULL;

			/* Add Units if the account is Priced */
			if (pAccount.isPriced())
				theStateBox.addItem(Units);
			else if (theType == StatementType.UNITS)
				theType = StatementType.NULL;

			/* Always add Extract */	
			theStateBox.addItem(Extract);
			statePopulated = true;
			
			/* If we have no type */
			if (theType == StatementType.NULL) {
				/* Select the default type for the account */
				if      (pAccount.isMoney())  theType = StatementType.VALUE;
				else if (pAccount.isDebt())   theType = StatementType.VALUE;
				else if (pAccount.isPriced()) theType = StatementType.UNITS;
				else theType = StatementType.EXTRACT;
			}
		
			/* Obtain the text value for the type */
			switch (theType) {
				case VALUE: myText = Value; break;
				case UNITS: myText = Units; break;
				default: 	myText = Extract; break;
			}
			
			/* Select the correct type */
			theStateBox.setSelectedItem(myText);			
		}

		/* Else we have no selected type */
		else theType = StatementType.NULL;
		
		/* Enable/Disable the box */
		setLockDown();

		/* Note that we have finished refreshing data */
		refreshingData = false;
	}
	
	/**
	 * TaxYear Listener class
	 */
	private class StatementListener implements ItemListener {
		/* ItemStateChanged listener event */
		public void itemStateChanged(ItemEvent evt) {
			boolean               bChange = false;

			/* Ignore selection if refreshing data */
			if (refreshingData) return;
		
			/* If this event relates to the Statement box */
			if (evt.getSource() == theStateBox) {
				if (evt.getStateChange() == ItemEvent.SELECTED) {
					String myName = (String)evt.getItem();

					/* Determine the new report */
					bChange = true;
					if (myName == Value)	    theType = StatementType.VALUE;
					else if (myName == Units) 	theType = StatementType.UNITS;
					else if (myName == Extract)	theType = StatementType.EXTRACT;
					else bChange = false;
				}
			}
		
			/* If we have a change, alert the table */
			if (bChange) { theParent.notifySelection(theSelf); }
		}
	}
	
	/* Statement Types */
	public enum StatementType {
		NULL,
		EXTRACT,
		VALUE,
		UNITS;
	}
}
