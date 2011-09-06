package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.*;
import uk.co.tolcroft.models.ui.FinanceInterfaces.financePanel;

public class ErrorPanel implements ActionListener {
	/* Members */
	private financePanel		theParent			= null;
	private JPanel				thePanel			= null;
	private JLabel				theErrorField		= null;
	private JButton 			theClearButton		= null;
	private DebugEntry			theDebugError		= null;
	private Exception			theError			= null;

	/* Access methods */
	public JPanel getPanel() { return thePanel; }
	
	/**
	 * Constructor
	 */
	public ErrorPanel(financePanel pParent) {
		/* Store parent */
		theParent = pParent;
		
        /* Create the error debug entry for this view */
		DebugManager myDebugMgr = theParent.getDebugManager();
        theDebugError = myDebugMgr.new DebugEntry("Error");
        theDebugError.addAsChildOf(theParent.getDebugEntry());
        theDebugError.hideEntry();
        
		/* Create the error field */
		theErrorField	= new JLabel();
		
		/* Create the clear button */
		theClearButton	= new JButton("Clear");
		
		/* Add the listener for item changes */
		theClearButton.addActionListener(this);

		/* Create the error panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Error"));
		
		/* Create the layout for the panel */
		GroupLayout myLayout = new GroupLayout(thePanel);
		thePanel.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
           		.addContainerGap()
                .addComponent(theClearButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(theErrorField)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(theClearButton)
            .addComponent(theErrorField)
        );
            
		/* Set the Error panel to be red and invisible */
		theErrorField.setForeground(Color.red);
		thePanel.setVisible(false);
	}
	
	/**
	 * Set error indication for window 
	 * @param pError the error
	 */
	public void setError(Exception pException) {
		/* Record the error */
		theError = pException;
		
		/* Set the string for the error field */
		theErrorField.setText(pException.getMessage());
		
		/* Make the panel visible */
		thePanel.setVisible(true);
		
		/* Show the debug */
		theDebugError.setObject(theError);
		theDebugError.showEntry();
		
		/* Call the parent to lock other windows */
		theParent.lockOnError(true);
	}
	
	/**
	 * Clear error indication for this window 
	 */
	public void clearError() {
		/* If we currently have an error */
		if (theError != null) {
			/* Clear the error */
			theError = null;
			theDebugError.setObject(theError);
			theDebugError.hideEntry();
		}		

		/* Make the panel invisible */
		thePanel.setVisible(false);

		/* Call the parent to unlock other windows */
		theParent.lockOnError(false);
	}	
	/**
	 * Perform a requested action
	 * @param evt the action event
	 */
	public void actionPerformed(ActionEvent evt) {
		/* If this event relates to the Clear box */
		if (evt.getSource() == (Object)theClearButton) {
			/* Clear the error */
			clearError();
		}
	}
}
