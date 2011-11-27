package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.ui.DateSelect.CalendarButton;
import uk.co.tolcroft.models.ui.DateSelect.DateModel;
import uk.co.tolcroft.models.ui.ValueField;
import uk.co.tolcroft.models.ui.FileSelector.*;
import uk.co.tolcroft.models.ui.StdInterfaces.*;

public class MaintProperties {
	/* Properties */
	private View				theView				= null;
	private MaintenanceTab		theParent			= null;
	private JPanel              thePanel			= null;
	private JPanel              theButtons			= null;
	private JPanel              theChecks			= null;
	//private JTextField			theDBDriver			= null;
	private ValueField			theDBDriver			= null;
	private JTextField			theDBConnect		= null;
	private JTextField			theBaseSSheet		= null;
	private JTextField			theRepoDir			= null;
	private JTextField			theBackupDir		= null;
	private JTextField			theBackupPrefix		= null;
	private CalendarButton		theBirthButton		= null;
	private DateModel			theDateModel		= null;
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
		theDBDriver 	= new ValueField();
		theDBConnect 	= new JTextField();
		theBaseSSheet	= new JTextField();
		theRepoDir		= new JTextField();
		theBackupDir	= new JTextField();
		theBackupPrefix	= new JTextField();
		
		/* Create the check boxes */
		theShowDebug		= new JCheckBox("Show Debug");

		/* Create the Date Button */
		theBirthButton = new CalendarButton();
		theDateModel   = theBirthButton.getDateModel();
		
		/* Create the buttons */
		theOKButton 	= new JButton("OK");
		theResetButton 	= new JButton("Reset");
		theBaseSel		= new JButton("Choose");
		theBackupSel	= new JButton("Choose");
		theRepoSel		= new JButton("Choose");
		
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
                            .addComponent(theBirthButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
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
                    .addComponent(theBirthButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
	            .addComponent(theChecks)
	            .addComponent(theButtons))
        );            

        /* Create listener */
        PropertyListener myListener = new PropertyListener();
        
        /* Add listeners */
		theDBDriver.addActionListener(myListener);
		theDBDriver.addPropertyChangeListener(ValueField.valueName, myListener);
		theDBConnect.addActionListener(myListener);
		theBaseSSheet.addActionListener(myListener);
		theRepoDir.addActionListener(myListener);
		theBackupDir.addActionListener(myListener);
		theBackupPrefix.addActionListener(myListener);
		theBirthButton.addPropertyChangeListener(CalendarButton.valueDATE, myListener);
		theOKButton.addActionListener(myListener);
		theResetButton.addActionListener(myListener);
		theBaseSel.addActionListener(myListener);
		theBackupSel.addActionListener(myListener);
		theRepoSel.addActionListener(myListener);
		theShowDebug.addItemListener(myListener);
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
		
		/* Show the BirthDate */
		theDateModel.setSelectedDate(theExtract.getBirthDate().getDate());
		
		/* Set the check boxes */
		theShowDebug.setSelected(theExtract.doShowDebug());
		
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
	
	/**
	 * PropertyListener class
	 */
	private class PropertyListener implements ActionListener,
											  ItemListener,
											  PropertyChangeListener {
		@Override
		public void actionPerformed(ActionEvent evt) {
			Object o = evt.getSource();
			
			/* If this event relates to the OK button */
			if (o == theOKButton) {
				/* Perform the command */
				performCommand(stdCommand.OK);
			}
		
			/* If this event relates to the reset button */
			else if (o == theResetButton) {
				/* Perform the command */
				performCommand(stdCommand.RESETALL);
			}
		
			/* If this event relates to the Base Select button */
			else if (o == theBaseSel) {
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
			else if (o == theRepoSel) {
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
			else if (o == theBackupSel) {
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
			else if ((o == theDBDriver)   ||
					 (o == theDBConnect)  ||
					 (o == theBaseSSheet) ||
					 (o == theRepoDir)    ||
					 (o == theBackupDir)  ||
					 (o == theBackupPrefix)) {
				/* Update the text */
				updateText();
			
				/* Notify changes */
				notifyChanges();
			}
		}			
	
		@Override
		public void itemStateChanged(ItemEvent evt) {
			/* Ignore selection if refreshing data */
			if (refreshingData) return;
					
			/* If this event relates to the showDebug box */
			if (evt.getSource() == theShowDebug) {
				/* Note the new criteria and re-build lists */
				theExtract.setDoShowDebug(theShowDebug.isSelected());

				/* Update the text */
				updateText();
			
				/* Notify changes */
				notifyChanges();
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object o = evt.getSource();
			
			/* if this date relates to the Date button */
			if (o == theBirthButton) {
				/* Access the value */
				Date myDate = new Date(theDateModel.getSelectedDate());

				/* Store the value */
				theExtract.setBirthDate(myDate);
				
				/* Update the text */
				updateText();
				
				/* Note that changes have occurred */
				notifyChanges();
			}			

			/* If this is our component */
			else if (o == theDBDriver) {
				//System.out.println("Hello");
			}
		}
	}
}
