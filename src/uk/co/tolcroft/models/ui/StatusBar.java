package uk.co.tolcroft.models.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.Timer;
import javax.swing.plaf.ProgressBarUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.help.DebugManager.DebugEntry;
import uk.co.tolcroft.models.views.DataControl;

public class StatusBar implements ActionListener {
	/* Members */
	private JPanel					theProgPanel	= null;
	private JPanel					theStatPanel	= null;
	private JProgressBar			theSteps		= null;
	private JProgressBar			theStages		= null;
	private JButton               	theCancel	 	= null;
	private JButton               	theClear	 	= null;
	private JLabel               	theStageLabel	= null;
	private JLabel                  theOpnLabel  	= null;
	private JLabel                  theStatusLabel  = null;
	private MainWindow<?>			theControl		= null;
	private Exception				theError		= null;
	private Timer					theTimer		= null;
    private DebugEntry				theDebug		= null;
	
	/* Access methods */
	public  JPanel           	   	getProgressPanel()  { return theProgPanel; }
	public  JPanel           	   	getStatusPanel()	{ return theStatPanel; }
	public  Exception	   			getError()			{ return theError; }
				
	/* Constructor */
	public StatusBar(MainWindow<?> pControl) {
		GroupLayout 	panelLayout;
		ProgressBarUI	myUI;
	
		/* Record passed parameters */
		theControl 	= pControl;
		
		/* Store access to the Debug Entry */
		theDebug	= theControl.getView().getDebugEntry(DataControl.DebugError);
		
		/* Create the boxes */
		theCancel      = new JButton("Cancel");
		theClear       = new JButton("Clear");
		theOpnLabel    = new JLabel();
		theStageLabel  = new JLabel();
		theStatusLabel = new JLabel();
		theStages	   = new JProgressBar();
		theSteps	   = new JProgressBar();

		/* Create look/feel for progress bars */
		myUI = new BasicProgressBarUI() { protected Color getSelectionBackground()  { return Color.black; } 
										  protected Color getSelectionForeground()  { return Color.black; } 
										};
		
		/* Set backgrounds */
		theStages.setForeground(Color.green);
		theSteps.setForeground(Color.green);
		theSteps.setUI(myUI);
		theStages.setUI(myUI);
		
		/* Initialise progress bars */
		theStages.setMaximum(100);
		theStages.setMinimum(0);
		theStages.setValue(0);
		theStages.setStringPainted(true);
		theSteps.setMaximum(100);
		theSteps.setMinimum(0);
		theSteps.setValue(0);
		theSteps.setStringPainted(true);
		
		/* Add the listener for item changes */
		theCancel.addActionListener(this);
		theClear.addActionListener(this);
		
		/* Create the progress panel */
		theProgPanel = new JPanel();
		theProgPanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Progress"));

		/* Create the layout for the save panel */
	    panelLayout = new GroupLayout(theProgPanel);
	    theProgPanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
		    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		            .addGroup(panelLayout.createSequentialGroup()
		                .addContainerGap()
		                .addComponent(theOpnLabel)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theStages)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		                .addComponent(theStageLabel, GroupLayout.PREFERRED_SIZE, 130, GroupLayout.PREFERRED_SIZE)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theSteps)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
		                .addComponent(theCancel)
		                .addContainerGap())
	    );
	    panelLayout.setVerticalGroup(
		    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(panelLayout.createSequentialGroup()
		                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                	.addComponent(theOpnLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theStages, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                	.addComponent(theStageLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theSteps, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theCancel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    );
	    
		/* Create the status panel */
		theStatPanel = new JPanel();
		theStatPanel.setBorder(javax.swing.BorderFactory
							.createTitledBorder("Status"));

		/* Create the layout for the save panel */
	    panelLayout = new GroupLayout(theStatPanel);
	    theStatPanel.setLayout(panelLayout);
	    
	    /* Set the layout */
	    panelLayout.setHorizontalGroup(
		    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(GroupLayout.Alignment.TRAILING, panelLayout.createSequentialGroup()
		                .addContainerGap()
		                .addComponent(theClear)
		                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
		                .addComponent(theStatusLabel))
	    );
	    panelLayout.setVerticalGroup(
		    	panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		        	.addGroup(panelLayout.createSequentialGroup()
		                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING, false)
		                	.addComponent(theStatusLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		                    .addComponent(theClear, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
	    );
	}
	
	/* actionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {

		/* If this event relates to the Cancel box */
		if (evt.getSource() == (Object)theCancel) {
			/* Pass command to the table */
			theControl.performCancel();
		}
		
		/* If this event relates to the Clear box */
		if (evt.getSource() == (Object)theClear) {
			/* Stop any timer */
			if (theTimer != null) theTimer.stop();
			
			/* Make the Status window invisible */
			theStatPanel.setVisible(false);
			theError = null;
			
			/* Finish the thread */
			theControl.finishThread();
		}
		
		/* If this event relates to the Clear or the timer box */
		if (evt.getSource() == (Object)theTimer) {
			/* Make the Status window invisible */
			theStatPanel.setVisible(false);
			
			/* Clear the error */
			theError = null;
			theDebug.hideEntry();
			
			/* Finish the thread */
			theControl.finishThread();
		}
	}
	
	/* Set Operation string */
	public void setOperation(String pStatus) {
		/* Set the label field */
		theOpnLabel.setText(pStatus);
	}
	
	/* Set Stage */
	public void setStage(String pStage,
			             int    pStagesDone,
			             int    pNumStages) {
		/* Expand stage text to 20 */
		String myStage = pStage + "                              ";
		myStage = myStage.substring(0, 20);
		
		/* Set the Stage progress */
		theStageLabel.setText(pStage);
		theStages.setMaximum(pNumStages);
		theStages.setValue(pStagesDone);
	}
	
	/* Set Steps */
	public void setSteps(int    pStepsDone,
			             int    pNumSteps) {
		/* Set the steps progress */
		theSteps.setMaximum(pNumSteps);
		theSteps.setValue(pStepsDone);
	}
	
	/* Set Success string */
	public void setSuccess(String pOperation) {
		/* Set the status text field */
		theStatusLabel.setText(pOperation + " succeeded");
		
		/* Show the status window rather than the progress window */
		theStatPanel.setVisible(true);
		theProgPanel.setVisible(false);
		
		/* Set up a timer for 5 seconds and no repeats */
		if (theTimer == null) theTimer = new Timer(5000, this);
		theTimer.setRepeats(false);
		theTimer.start();
	}
				
	/* Set Failure string */
	public void setFailure(String		pOperation,
			               Exception 	pError) {
		/* Initialise the message */
		String myText = pOperation + " failed";
		
		/* If there is an error detail */
		if (pError != null) {
			/* Add the error detail */
			myText += ". " + pError.getMessage();
		}
		
		/* else no failure - must have cancelled */
		else myText += ". Operation cancelled";
		
		/* Store the error */
		theError = pError;
		
		/* Enable debug for this error */
		theDebug.setObject(theError);
		theDebug.showPrimeEntry();
		theDebug.setFocus();
		
		/* Set the status text field */
		theStatusLabel.setText(myText);
		
		/* Show the status window rather than the progress window */
		theStatPanel.setVisible(true);
		theProgPanel.setVisible(false);
	}
}
