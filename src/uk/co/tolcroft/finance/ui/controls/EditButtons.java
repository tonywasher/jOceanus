package uk.co.tolcroft.finance.ui.controls;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.models.*;

public class EditButtons implements ActionListener,
									ItemListener {
	/* Members */
	private JPanel					thePanel		= null;
	private JPanel					theHistPanel	= null;
	private JPanel					theEditPanel	= null;
	private JPanel					theDelPanel		= null;
	private JPanel					theInsPanel		= null;
	private financeTable			theParent		= null;
	private JButton               	theInsCrButton 	= null;
	private JButton               	theInsDbButton	= null;
	private JButton               	theDelButton 	= null;
	private JButton               	theRecovButton	= null;
	private JButton               	theUndoButton 	= null;
	private JButton               	theValButton 	= null;
	private JButton               	theResetButton 	= null;
	private JButton               	theNextButton	= null;
	private JButton               	thePrevButton	= null;
	private JCheckBox				theShowDeleted  = null;
	private boolean					doShowDeleted	= false;
	
	/* Access methods */
	public	JPanel           		getPanel()      { return thePanel; }
	public 	boolean					getShowDel()	{ return doShowDeleted; }
				
	/* Constructor */
	public EditButtons(financeTable pParent, 
			           InsertStyle  pStyle) {
		GroupLayout panelLayout;
		
		/* Create the buttons */
		switch (pStyle)	{
			case CREDITDEBIT:
				theInsCrButton = new JButton("Insert Credit");
				theInsDbButton = new JButton("Insert Debit");
				break;
			case INSERT:	
				theInsCrButton = new JButton("Insert");
				break;
		}
		
		theDelButton   = new JButton("Delete");
		theRecovButton = new JButton("Recover");
		theUndoButton  = new JButton("Undo");
		theResetButton = new JButton("Reset");
		theValButton   = new JButton("Validate");
		theNextButton  = new JButton("Next");
		thePrevButton  = new JButton("Prev");
		theShowDeleted = new JCheckBox();
		theParent	   = pParent;
		
		/* Set the text for the check-box */
		theShowDeleted.setText("Show Deleted");
		theShowDeleted.setSelected(doShowDeleted);
		
		/* Add the listener for item changes */
		if (theInsCrButton != null)
			theInsCrButton.addActionListener(this);
		if (theInsDbButton != null)
			theInsDbButton.addActionListener(this);
		theDelButton.addActionListener(this);
		theRecovButton.addActionListener(this);
		theUndoButton.addActionListener(this);
		theResetButton.addActionListener(this);
		theValButton.addActionListener(this);
		theNextButton.addActionListener(this);
		thePrevButton.addActionListener(this);
		theShowDeleted.addItemListener(this);
		
	    /* If we have a credit option */
	    if (pStyle != InsertStyle.NONE) {
	    	/* Create the insert panel */
	    	theInsPanel = new JPanel();
	    	theInsPanel.setBorder(javax.swing.BorderFactory
	    								.createTitledBorder("Insert"));

	    	/* Create the layout for the insert panel */
	    	panelLayout = new GroupLayout(theInsPanel);
	    	theInsPanel.setLayout(panelLayout);
	    
	    	/* If we have a credit option */
	    	if (pStyle == InsertStyle.CREDITDEBIT) {
	    		/* Set the layout */
	    		panelLayout.setHorizontalGroup(
	    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	    				.addGroup(panelLayout.createSequentialGroup()
	    					.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	    						.addComponent(theInsCrButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	    						.addComponent(theInsDbButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    		);
	    		panelLayout.setVerticalGroup(
	    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	        				.addComponent(theInsCrButton)
	        				.addComponent(theInsDbButton))
	    		);
	    	}
        
	    	/* else we have no credit option */
	    	else if (pStyle == InsertStyle.INSERT) {
	    		/* Set the layout */
	    		panelLayout.setHorizontalGroup(
	    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addGroup(panelLayout.createSequentialGroup()
	        				.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	        					.addComponent(theInsCrButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    		);
	    		panelLayout.setVerticalGroup(
	    			panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        			.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	        				.addComponent(theInsCrButton))
	    		);
	    	}
	    }
	    
		/* Create the edit panel */
		theEditPanel = new JPanel();
		theEditPanel.setBorder(javax.swing.BorderFactory
								.createTitledBorder("Edit"));

		/* Create the layout for the edit panel */
	    panelLayout = new GroupLayout(theEditPanel);
	    theEditPanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(panelLayout.createSequentialGroup()
	        		.addContainerGap()
	                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	                	.addComponent(theUndoButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theValButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theResetButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addContainerGap())
	    );
        panelLayout.setVerticalGroup(
        	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	        		.addComponent(theUndoButton)
	                .addComponent(theValButton)
	                .addComponent(theResetButton))
	    );
	    
		/* Create the history panel */
		theHistPanel = new JPanel();
		theHistPanel.setBorder(javax.swing.BorderFactory
								.createTitledBorder("History"));

		/* Create the layout for the history panel */
	    panelLayout = new GroupLayout(theHistPanel);
	    theHistPanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(panelLayout.createSequentialGroup()
	                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	                	.addComponent(theNextButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(thePrevButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    );
        panelLayout.setVerticalGroup(
        	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	        		.addComponent(theNextButton)
	                .addComponent(thePrevButton))
	    );
	    
		/* Create the delete panel */
		theDelPanel = new JPanel();
		theDelPanel.setBorder(javax.swing.BorderFactory
								.createTitledBorder("Delete"));

		/* Create the layout for the delete panel */
	    panelLayout = new GroupLayout(theDelPanel);
	    theDelPanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
	    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(panelLayout.createSequentialGroup()
	                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
	                	.addComponent(theDelButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theRecovButton, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(theShowDeleted, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    );
        panelLayout.setVerticalGroup(
        	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
	        		.addComponent(theDelButton)
	                .addComponent(theRecovButton)
	                .addComponent(theShowDeleted))
	    );

    	/* Create the main panel */
		thePanel = new JPanel();
		thePanel.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Row Options"));

		/* Create the layout for the main panel */
	    panelLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(panelLayout);
	    
	    /* If we have a credit option */
	    if (pStyle != InsertStyle.NONE) {
	    	/* Set the layout */
	    	panelLayout.setHorizontalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createSequentialGroup()
	            		.addComponent(theInsPanel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	            		.addComponent(theDelPanel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	            		.addComponent(theEditPanel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	            		.addComponent(theHistPanel))
	    	);
	    	panelLayout.setVerticalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	            		.addComponent(theInsPanel)
	            		.addComponent(theDelPanel)
	            		.addComponent(theEditPanel)
	            		.addComponent(theHistPanel))
	    	);
	    }
	    
	    /* else no insert option */
	    else {
	    	/* Set the layout */
	    	panelLayout.setHorizontalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createSequentialGroup()
	            		.addComponent(theDelPanel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	            		.addComponent(theEditPanel)
	            		.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	            		.addComponent(theHistPanel))
	    	);
	    	panelLayout.setVerticalGroup(
	    		panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	            	.addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
	            		.addComponent(theDelPanel)
	            		.addComponent(theEditPanel)
	            		.addComponent(theHistPanel))
	    	);			    	
	    }

		/* Initiate lock-down mode */
		setLockDown();
	}
				
	/* Lock/Unlock the selection */
	public void setLockDown() {
		boolean 			enableRecov 	= false;
		boolean 			enableDel		= false;
		boolean 			enableUndo	= false;
		boolean 			enableReset	= false;
		boolean 			enableValid	= false;
		boolean 			enableNext	= false;
		boolean 			enablePrev	= false;
		boolean				enableIns		= true;
		boolean				enableShow	= true;
		DataItem 			myRow;
		
		/* If the table is locked */
		if (theParent.isLocked()) {
			/* Disable all buttons */
			enableUndo 	= false;
			enableReset	= false;
			enableValid	= false;
			enableNext	= false;
			enablePrev	= false;
			enableRecov	= false;
			enableDel	= false;
			enableIns	= false;
			enableShow	= false;
		}

		/* else not locked */
		else {
			/* Loop through the selected rows */
			for (int row : theParent.getSelectedRows()) {
				/* If we have a header decrement the row */
				if (theParent.hasHeader()) row--;
				
				/* Ignore negative rows (the header is selected) */
				if (row < 0) continue;
				
				/* Access the row */
				myRow = theParent.extractItemAt(row);
				
				/* Ignore locked rows */
				if ((myRow == null) || (myRow.isLocked())) continue;
				
				/* Determine which options are allowed */
				if (myRow.hasHistory())  { 
					enableUndo 	= true;
				  	enableReset	= true;
				  	if (myRow.getEditState() != EditState.VALID)
				  		enableValid  = true;
				}
				if (myRow.hasFurther(theParent)) enableNext 	= true;
				if (myRow.hasPrevious()) 		enablePrev 	= true;
				if (myRow.isDeleted())   {
					if (theParent.isValidObj(myRow, myRow.getObj()))
						enableRecov	= true;
					enableShow  = false;
				}
				else							enableDel	= true;
			}
		}
		
		/* Enable/Disable the buttons */
		theRecovButton.setEnabled(enableRecov);
		theDelButton.setEnabled(enableDel);
		theUndoButton.setEnabled(enableUndo);
		theResetButton.setEnabled(enableReset);
		theValButton.setEnabled(enableValid);
		theNextButton.setEnabled(enableNext);
		thePrevButton.setEnabled(enablePrev);
		theShowDeleted.setEnabled(enableShow);
		if (theInsCrButton != null)
			theInsCrButton.setEnabled(enableIns);
		if (theInsDbButton != null)
			theInsDbButton.setEnabled(enableIns);
	}
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {

		/* If this event relates to the showDeleted box */
		if (evt.getSource() == (Object)theShowDeleted) {
			/* Note the new criteria and re-build lists */
			doShowDeleted = theShowDeleted.isSelected();
			theParent.notifySelection(this);
		}
	}
	
	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {

		/* If this event relates to the InsCr box */
		if (evt.getSource() == (Object)theInsCrButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.INSERTCR);
		}
		
		/* If this event relates to the InsDb box */
		else if (evt.getSource() == (Object)theInsDbButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.INSERTDB);
		}
		
		/* If this event relates to the Del box */
		else if (evt.getSource() == (Object)theDelButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.DELETE);
		}
		
		/* If this event relates to the Recover box */
		else if (evt.getSource() == (Object)theRecovButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.RECOVER);
		}
		
		/* If this event relates to the Undo box */
		else if (evt.getSource() == (Object)theUndoButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.UNDO);
		}
		
		/* If this event relates to the Reset box */
		else if (evt.getSource() == (Object)theResetButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.RESET);
		
		}
		
		/* If this event relates to the Validate box */
		else if (evt.getSource() == (Object)theValButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.VALIDATE);
		}
		
		/* If this event relates to the Next box */
		else if (evt.getSource() == (Object)theNextButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.NEXT);
		}
		
		/* If this event relates to the Previous box */
		else if (evt.getSource() == (Object)thePrevButton) {
			/* Pass command to the table */
			theParent.performCommand(financeCommand.PREV);
		}
	}
	
	public enum InsertStyle {
		INSERT,
		CREDITDEBIT,
		NONE;
	}	
}
