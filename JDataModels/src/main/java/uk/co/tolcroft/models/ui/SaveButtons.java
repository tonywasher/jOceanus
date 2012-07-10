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

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.models.ui.StdInterfaces.*;

public class SaveButtons implements ActionListener {
	/* Members */
	private JPanel					thePanel		= null;
	private stdPanel				theParent		= null;
	private JButton               	theOKButton 	= null;
	private JButton                 theResetButton  = null;
	
	/* Access methods */
	public	JPanel           	   	getPanel()      { return thePanel; }
				
	/* Constructor */
	public SaveButtons(stdPanel pParent) {
		GroupLayout panelLayout;
		
		/* Create the boxes */
		theOKButton    = new JButton("OK");
		theResetButton = new JButton("Reset");
		theParent	   = pParent;
		
		/* Add the listener for item changes */
		theOKButton.addActionListener(this);
		//theValidButton.addActionListener(this);
		theResetButton.addActionListener(this);
		
		/* Create the save panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Save Options"));

		/* Create the layout for the save panel */
	    panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* Set the layout */
        panelLayout.setHorizontalGroup(
        	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	                .addContainerGap()
	        		.addComponent(theOKButton)
	                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(theResetButton)
	                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
		    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(panelLayout.createSequentialGroup()
		                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                	.addComponent(theOKButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theResetButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    );

		/* Initiate lock-down mode */
		setLockDown();
	}
				
	/* Lock/Unlock the selection */
	public void setLockDown() {
		/* If the table is locked clear the buttons */
		if (theParent.isLocked()) {
			theOKButton.setEnabled(false);
			theResetButton.setEnabled(false);
			
		/* Else look at the edit state */
		} else {
			switch (theParent.getEditState()) {
				case CLEAN:
					theOKButton.setEnabled(false);
					theResetButton.setEnabled(false);
					break;
				case DIRTY:
				case ERROR:
					theOKButton.setEnabled(false);
					theResetButton.setEnabled(true);
					break;
				case VALID:
					theOKButton.setEnabled(true);
					theResetButton.setEnabled(true);
					break;
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent evt) {

		/* If this event relates to the OK box */
		if (evt.getSource() == (Object)theOKButton) {
			/* Pass command to the table */
			theParent.performCommand(stdCommand.OK);
		}
		
		/* If this event relates to the Reset box */
		else if (evt.getSource() == (Object)theResetButton) {
			/* Pass command to the table */
			theParent.performCommand(stdCommand.RESETALL);
		}
		
		/* Set the lockDown Status */
		setLockDown();
	}
}
