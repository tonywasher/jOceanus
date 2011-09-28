package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerDateModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.ui.FileSelector.*;
import uk.co.tolcroft.models.ui.StdInterfaces.*;

public class MaintProperties implements ActionListener,
										ItemListener,
										ChangeListener {
	/* Properties */
	private View				theView				= null;
	private MaintenanceTab		theParent			= null;
	private JPanel              thePanel			= null;
	private JPanel              theButtons			= null;
	private JPanel              theChecks			= null;
	private JTextField			theDBDriver			= null;
	private JTextField			theDBConnect		= null;
	private JTextField			theBaseSSheet		= null;
	private JTextField			theRepoDir			= null;
	private JTextField			theBackupDir		= null;
	private JTextField			theBackupPrefix		= null;
	private JSpinner			theSpinner			= null;
	private SpinnerDateModel	theModel			= null;
	private JCheckBox			theShowDebug		= null;
	private JButton				theOKButton			= null;
	private JButton				theResetButton		= null;
	private JButton				theBaseSel			= null;
	private JButton				theBackupSel		= null;
	private JButton				theRepoSel			= null;
	private View.ViewProperties	theExtract			= null;
	private boolean				refreshingData		= false;
	
	/* Access methods */
	public JPanel       	getPanel()       { return thePanel; }
	
	/* Constructor */
	public MaintProperties(MaintenanceTab pParent) {
		JLabel	myDBDriver;
		JLabel	myDBConnect;
		JLabel	myBaseSSheet;
		JLabel	myRepoDir;
		JLabel	myBackupDir;
		JLabel	myBackupPrefix;
		JLabel	myBirthDate;
		
		/* Store parent */
		theParent = pParent;
		theView	  = pParent.getView();
		
		/* Create the labels */
		myDBDriver 		= new JLabel("Driver String:");
		myDBConnect 	= new JLabel("Connection String:");
		myBaseSSheet	= new JLabel("Base Spreadsheet:");
		myRepoDir		= new JLabel("Repository Directory:");
		myBackupDir		= new JLabel("Backup Directory:");
		myBackupPrefix	= new JLabel("Backup Prefix:");
		myBirthDate		= new JLabel("BirthDate:");

		/* Create the text fields */
		theDBDriver 	= new JTextField();
		theDBConnect 	= new JTextField();
		theBaseSSheet	= new JTextField();
		theRepoDir		= new JTextField();
		theBackupDir	= new JTextField();
		theBackupPrefix	= new JTextField();
		
		/* Create the check boxes */
		theShowDebug		= new JCheckBox("Show Debug");

		/* Create the spinner */
		theModel    = new SpinnerDateModel();
		theSpinner  = new JSpinner(theModel);
		theSpinner.setEditor(new JSpinner.DateEditor(theSpinner, "dd-MMM-yyyy"));
		
		/* Create the buttons */
		theOKButton 	= new JButton("OK");
		theResetButton 	= new JButton("Reset");
		theBaseSel		= new JButton("Choose");
		theBackupSel	= new JButton("Choose");
		theRepoSel		= new JButton("Choose");
		
		/* Add listeners */
		theDBDriver.addActionListener(this);
		theDBConnect.addActionListener(this);
		theBaseSSheet.addActionListener(this);
		theRepoDir.addActionListener(this);
		theBackupDir.addActionListener(this);
		theBackupPrefix.addActionListener(this);
		theModel.addChangeListener(this);
		theOKButton.addActionListener(this);
		theResetButton.addActionListener(this);
		theBaseSel.addActionListener(this);
		theBackupSel.addActionListener(this);
		theRepoSel.addActionListener(this);
		theShowDebug.addItemListener(this);
		
		/* Create the buttons panel */
		theButtons = new JPanel();
		theButtons.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Save Options"));
		
		/* Create the layout for the panel */
	    GroupLayout myLayout = new GroupLayout(theButtons);
	    theButtons.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addContainerGap()
                .addComponent(theOKButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theResetButton)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(theOKButton)
            .addComponent(theResetButton)
        );
            
		/* Create the buttons panel */
		theChecks = new JPanel();
		theChecks.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Options"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theChecks);
	    theChecks.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addContainerGap()
                .addComponent(theShowDebug)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(theShowDebug)
        );
            
		/* Create the panel */
		thePanel = new JPanel();
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(thePanel);
	    thePanel.setLayout(myLayout);
		    
	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	    	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                	.addComponent(theButtons)
                	.addComponent(theChecks)
                    .addGroup(myLayout.createSequentialGroup()
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(myDBDriver)
                            .addComponent(myDBConnect)
                            .addComponent(myBaseSSheet)
                            .addComponent(myRepoDir)
                            .addComponent(myBackupDir)
                            .addComponent(myBackupPrefix)
                            .addComponent(myBirthDate))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(theDBDriver, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theDBConnect, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theBaseSSheet, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theRepoDir, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theBackupDir, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theBackupPrefix, GroupLayout.PREFERRED_SIZE, 600, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(theRepoSel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theBackupSel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE)
                            .addComponent(theBaseSel, GroupLayout.PREFERRED_SIZE, 100, GroupLayout.PREFERRED_SIZE))))
  	            .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	               	.addComponent(myDBDriver)
                    .addComponent(theDBDriver, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    	        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	       	.addComponent(myDBConnect)
                    .addComponent(theDBConnect, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	               	.addComponent(myBaseSSheet)
                    .addComponent(theBaseSSheet, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                    .addComponent(theBaseSel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	               	.addComponent(myRepoDir)
                    .addComponent(theRepoDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theRepoSel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	               	.addComponent(myBackupDir)
                    .addComponent(theBackupDir, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
	                .addComponent(theBackupSel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	               	.addComponent(myBackupPrefix)
                    .addComponent(theBackupPrefix, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	               	.addComponent(myBirthDate)
                    .addComponent(theSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	            .addComponent(theChecks)
	            .addComponent(theButtons))
        );            
	}
	
	/* hasUpdates */
	public boolean hasUpdates() {
		return ((theExtract != null) && (theExtract.hasChanges()));
	}
	
	/* performCommand */
	public void performCommand(stdCommand pCmd) {
		/* Switch on command */
		switch (pCmd) {
			case OK:
				try { theExtract.applyChanges(); } catch (Exception e) {}
				break;
			case RESETALL:
				theExtract.resetData();
				break;
		}
		
		/* Notify Status changes */
		notifyChanges();			
	}
	
	/* Note that changes have been made */
	public void notifyChanges() {
		/* Show the account */
		showProperties();
		
		/* Adjust visible tabs */
		theParent.setVisibility();
	}
	
	/* refreshData */
	public void refreshData() {
		/* Create a new view */
		theExtract = theView.new ViewProperties();

		/* Note that we are refreshing Data */
		refreshingData = true;
		
		/* Show the BirthDate */
		theModel.setValue(theExtract.getBirthDate().getDate());
		
		/* Set the check boxes */
		theShowDebug.setSelected(theExtract.doShowDebug());
		
		/* Notify the changes */
		notifyChanges();

		/* Note that we are finished refreshing Data */
		refreshingData = false;
	}
	
	/* Show Properties */
	public void showProperties() {
		/* Check for changes */
		theExtract.checkChanges();
		
		/* Show the DB details */
		theDBDriver.setText(theExtract.getDBDriver());
		theDBConnect.setText(theExtract.getDBConnection());
		
		/* Show the Backup details */
		theBackupDir.setText(theExtract.getBackupDir());
		theBackupPrefix.setText(theExtract.getBackupPrefix());
		
		/* Show the Repository details */
		theRepoDir.setText(theExtract.getRepoDir());

		/* Show the BaseSpreadsheet */
		theBaseSSheet.setText(theExtract.getBaseSpreadSheet());
		
		/* Enable the buttons */
		theOKButton.setEnabled(theExtract.hasChanges());
		theResetButton.setEnabled(theExtract.hasChanges());
	}
	
	/* Update text */
	private void updateText() {
		String  myText;

		/* Access the value */
		myText = theDBDriver.getText();
		if (myText.length() != 0) theExtract.setDBDriver(myText);    

		/* Access the value */
		myText = theDBConnect.getText();
		if (myText.length() != 0) theExtract.setDBConnection(myText);

		/* Access the value */
		myText = theBaseSSheet.getText();
		if (myText.length() != 0) theExtract.setBaseSpreadSheet(myText);

		/* Access the value */
		myText = theRepoDir.getText();
		if (myText.length() != 0) theExtract.setRepoDir(myText);

		/* Access the value */
		myText = theBackupDir.getText();
		if (myText.length() != 0) theExtract.setBackupDir(myText);

		/* Access the value */
		myText = theBackupPrefix.getText();
		if (myText.length() != 0) theExtract.setBackupPrefix(myText);
	}
	
	/* stateChanged listener event */
	public void stateChanged(ChangeEvent evt) {
		Date myDate;

		/* Ignore selection if refreshing data */
		if (refreshingData) return;
		
		/* If this event relates to the maturity box */
		if (evt.getSource() == (Object)theModel) {
			/* Access the value */
			myDate = new Date(theModel.getDate());

			/* Store the value */
			theExtract.setBirthDate(myDate);
			
			/* Update the text */
			updateText();
			
			/* Note that changes have occurred */
			notifyChanges();
		}								
	}
	
	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {			
		/* If this event relates to the OK button */
		if (evt.getSource() == (Object)theOKButton) {
			/* Perform the command */
			performCommand(stdCommand.OK);
		}
		
		/* If this event relates to the reset button */
		else if (evt.getSource() == (Object)theResetButton) {
			/* Perform the command */
			performCommand(stdCommand.RESETALL);
		}
		
		/* If this event relates to the Base Select button */
		else if (evt.getSource() == (Object)theBaseSel) {
			/* Create the and run the dialog */
			ArchiveLoad myDialog = new ArchiveLoad(theView);
			myDialog.selectFile();
			File myFile = myDialog.getSelectedFile();
			if (myFile != null)
				theExtract.setBaseSpreadSheet(myFile.getPath());
			
			/* Note that changes have occurred */
			notifyChanges();
		}
		
		/* If this event relates to the Repo Select button */
		else if (evt.getSource() == (Object)theRepoSel) {
			/* Create the and run the dialog */
			BackupDirectory myDialog = new BackupDirectory(theView);
			myDialog.selectFile();
			File myFile = myDialog.getSelectedFile();
			if (myFile != null)
				theExtract.setRepoDir(myFile.getPath());
			
			/* Note that changes have occurred */
			notifyChanges();
		}
		
		/* If this event relates to the Backup Select button */
		else if (evt.getSource() == (Object)theBackupSel) {
			/* Create the and run the dialog */
			BackupDirectory myDialog = new BackupDirectory(theView);
			myDialog.selectFile();
			File myFile = myDialog.getSelectedFile();
			if (myFile != null)
				theExtract.setBackupDir(myFile.getPath());
			
			/* Note that changes have occurred */
			notifyChanges();
		}
		
		/* If this event relates to the name field */
		else if ((evt.getSource() == (Object)theDBDriver)   ||
		         (evt.getSource() == (Object)theDBConnect)  ||
		         (evt.getSource() == (Object)theBaseSSheet) ||
		         (evt.getSource() == (Object)theRepoDir)    ||
		         (evt.getSource() == (Object)theBackupDir)  ||
		         (evt.getSource() == (Object)theBackupPrefix)) {
			/* Update the text */
			updateText();
			
			/* Notify changes */
			notifyChanges();
		}			
	}			
	
	/* ItemStateChanged listener event */
	public void itemStateChanged(ItemEvent evt) {
		/* Ignore selection if refreshing data */
		if (refreshingData) return;
					
		/* If this event relates to the showDebug box */
		if (evt.getSource() == (Object)theShowDebug) {
			/* Note the new criteria and re-build lists */
			theExtract.setDoShowDebug(theShowDebug.isSelected());

			/* Update the text */
			updateText();
			
			/* Notify changes */
			notifyChanges();
		}
	}
}
