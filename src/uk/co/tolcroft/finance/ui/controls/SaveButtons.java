package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;

public class SaveButtons implements ActionListener {
	/* Members */
	private JPanel					thePanel		= null;
	private financePanel			theParent		= null;
	private JButton               	theOKButton 	= null;
	//private JButton               	theValidButton	= null;
	private JButton                 theResetButton  = null;
	
	/* Access methods */
	public	JPanel           	   	getPanel()      { return thePanel; }
				
	/* Constructor */
	public SaveButtons(financePanel pParent) {
		GroupLayout panelLayout;
		
		/* Create the boxes */
		theOKButton    = new JButton("OK");
		//theValidButton = new JButton("Validate");
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
	                //.addComponent(theValidButton)
	                //.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(theResetButton)
	                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
		    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(panelLayout.createSequentialGroup()
		                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                	.addComponent(theOKButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    //.addComponent(theValidButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
			//theValidButton.setEnabled(false);
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
	
	/* actionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {

		/* If this event relates to the OK box */
		if (evt.getSource() == (Object)theOKButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.OK);
		}
		
		/* If this event relates to the Reset box */
		else if (evt.getSource() == (Object)theResetButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.RESETALL);
		}
		
		/* Set the lockDown Status */
		setLockDown();
	}
}
