package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.ui.controls.FinanceInterfaces.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.views.DebugManager.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;

public class MaintTaxYear implements ActionListener,
									 ItemListener,
									 financePanel {
	/* Properties */
	private MaintenanceTab		theParent			= null;
	private JPanel          	thePanel			= null;
	private JPanel				theButtons			= null;
	private JPanel				theSelect			= null;
	private JPanel				theRegime			= null;
	private JPanel				theAllows			= null;
	private JPanel				theBands			= null;
	private JPanel				theLimits			= null;
	private JPanel				theStdRates			= null;
	private JPanel				theXtraRates		= null;
	private JPanel				theCapRates			= null;
	private SaveButtons  		theSaveButs   		= null;
	private JComboBox			theYearsBox			= null;
	private JComboBox			theRegimesBox		= null;
	private JCheckBox			theShowDeleted		= null;
	private JTextField			theYear				= null;
	private JTextField			theAllowance		= null;
	private JTextField			theLoAgeAllow		= null;
	private JTextField			theHiAgeAllow		= null;
	private JTextField			theCapitalAllow		= null;
	private JTextField			theAgeAllowLimit	= null;
	private JTextField			theAddAllowLimit	= null;
	private JTextField			theAddIncomeBndry	= null;
	private JTextField			theRental			= null;
	private JTextField			theLoTaxBand		= null;
	private JTextField			theBasicTaxBand		= null;
	private JTextField			theLoTaxRate		= null;
	private JTextField			theBasicTaxRate		= null;
	private JTextField			theHiTaxRate		= null;
	private JTextField			theIntTaxRate		= null;
	private JTextField			theDivTaxRate		= null;
	private JTextField			theHiDivTaxRate		= null;
	private JTextField			theAddTaxRate		= null;
	private JTextField			theAddDivTaxRate	= null;
	private JTextField			theCapTaxRate		= null;
	private JTextField			theHiCapTaxRate		= null;
	private JButton				theInsButton		= null;
	private JButton				theDelButton		= null;
	private JButton				theUndoButton		= null;
	private TaxYear				theTaxYear			= null;
	private TaxYear.List		theTaxYears			= null;
	private TaxRegime.List		theTaxRegimes		= null;
	private View.ViewTaxYear	theTaxView			= null;
	private DebugEntry			theDebugEntry		= null;
	private boolean				refreshingData		= false;
	private boolean				yearsPopulated		= false;
	private boolean				regimesPopulated	= false;
	private boolean				doShowDeleted		= false;
	private View				theView				= null;
	
	/* Access methods */
	public JPanel   getPanel()      { return thePanel; }
	public TaxYear	getTaxYear()	{ return theTaxYear; }
	
	/* Access the debug entry */
	protected DebugEntry getDebugEntry()	{ return theDebugEntry; }
	
	/* Constructor */
	public MaintTaxYear(MaintenanceTab pParent) {
		JLabel	mySelect;
		JLabel  myYear;
		JLabel  myRegime;
		JLabel  myAllow;
		JLabel  myLoAgeAllow;
		JLabel  myHiAgeAllow;
		JLabel  myCapitalAllow;
		JLabel	myRental;
		JLabel  myAgeAllowLimit;
		JLabel  myAddAllowLimit;
		JLabel  myAddIncBndry;
		JLabel  myLoBand;
		JLabel	myBasicBand;
		JLabel  myLoTaxRate;
		JLabel  myBasicTaxRate;
		JLabel	myHiTaxRate;
		JLabel  myIntTaxRate;
		JLabel  myDivTaxRate;
		JLabel	myHiDivTaxRate;
		JLabel  myAddTaxRate;
		JLabel	myAddDivTaxRate;
		JLabel  myCapTaxRate;
		JLabel	myHiCapTaxRate;
		
		/* Store passed data */
		theParent = pParent;
		
		/* Access the view */
		theView 	= pParent.getView();
		
		/* Create the labels */
		mySelect 		= new JLabel("Select Year:");
		myYear 	 		= new JLabel("Year:");
		myRegime		= new JLabel("Tax Regime:");
		myAllow	 		= new JLabel("Personal Allowance:");
		myLoAgeAllow	= new JLabel("Age 65-74 Allowance:");
		myHiAgeAllow	= new JLabel("Age 75+ Allowance:");
		myCapitalAllow	= new JLabel("Capital Allowance:");
		myAgeAllowLimit	= new JLabel("Age Allowance Limit:");
		myAddAllowLimit	= new JLabel("Additnl Allow Limit:");
		myAddIncBndry	= new JLabel("Additnl Tax Boundary:");
		myRental		= new JLabel("Rental Allowance:");
		myLoBand		= new JLabel("Low Tax Band:");
		myBasicBand		= new JLabel("Basic Tax Band:");
		myLoTaxRate		= new JLabel("Low Rate:");
		myBasicTaxRate	= new JLabel("Basic Rate:");
		myHiTaxRate		= new JLabel("High Rate:");
		myIntTaxRate	= new JLabel("Interest Rate:");
		myDivTaxRate	= new JLabel("Dividend Rate:");
		myHiDivTaxRate	= new JLabel("High Dividend Rate:");
		myAddTaxRate	= new JLabel("Additnl Rate:");
		myAddDivTaxRate	= new JLabel("Additnl Dividend Rate:");
		myCapTaxRate	= new JLabel("Capital Rate:");
		myHiCapTaxRate	= new JLabel("High Capital Rate:");
		
		/* Create the combo boxes */
		theYearsBox  	= new JComboBox();
		theRegimesBox  	= new JComboBox();
		
		/* Create the combo boxes */
		theShowDeleted  = new JCheckBox("ShowDeleted");
		theShowDeleted.setSelected(doShowDeleted);
		
		/* Create the text fields */
		theYear				= new JTextField();
		theAllowance 		= new JTextField();
		theLoAgeAllow 		= new JTextField();
		theHiAgeAllow 		= new JTextField();
		theCapitalAllow		= new JTextField();
		theAgeAllowLimit	= new JTextField();
		theAddAllowLimit	= new JTextField();
		theAddIncomeBndry	= new JTextField();
		theLoTaxBand 		= new JTextField();
		theBasicTaxBand		= new JTextField();
		theRental			= new JTextField();
		theLoTaxRate 		= new JTextField();
		theBasicTaxRate 	= new JTextField();
		theHiTaxRate		= new JTextField();
		theIntTaxRate		= new JTextField();
		theDivTaxRate		= new JTextField();
		theHiDivTaxRate		= new JTextField();
		theAddTaxRate		= new JTextField();
		theAddDivTaxRate	= new JTextField();
		theCapTaxRate		= new JTextField();
		theHiCapTaxRate		= new JTextField();
		
		/* Set alignment for the text fields */
		theAllowance.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theLoAgeAllow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theHiAgeAllow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theCapitalAllow.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theAgeAllowLimit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theAddAllowLimit.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theAddIncomeBndry.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theRental.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theLoTaxBand.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theBasicTaxBand.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theLoTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theBasicTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theHiTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theIntTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theDivTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theHiDivTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theAddTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theAddDivTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theCapTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		theHiCapTaxRate.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		
		/* Create the buttons */
		theInsButton  = new JButton("New");
		theDelButton  = new JButton();
		theUndoButton = new JButton("Undo");
		
		/* Add listeners */
		theYearsBox.addItemListener(this);
		theRegimesBox.addItemListener(this);
		theAllowance.addActionListener(this);
		theLoAgeAllow.addActionListener(this);
		theHiAgeAllow.addActionListener(this);
		theCapitalAllow.addActionListener(this);
		theAgeAllowLimit.addActionListener(this);
		theAddAllowLimit.addActionListener(this);
		theAddIncomeBndry.addActionListener(this);
		theRental.addActionListener(this);
		theLoTaxBand.addActionListener(this);
		theBasicTaxBand.addActionListener(this);
		theLoTaxRate.addActionListener(this);
		theBasicTaxRate.addActionListener(this);
		theHiTaxRate.addActionListener(this);
		theIntTaxRate.addActionListener(this);
		theDivTaxRate.addActionListener(this);
		theHiDivTaxRate.addActionListener(this);
		theAddTaxRate.addActionListener(this);
		theAddDivTaxRate.addActionListener(this);
		theCapTaxRate.addActionListener(this);
		theHiCapTaxRate.addActionListener(this);
		theInsButton.addActionListener(this);
		theDelButton.addActionListener(this);
		theUndoButton.addActionListener(this);
		theShowDeleted.addItemListener(this);

		/* Create the Table buttons panel */
		theSaveButs = new SaveButtons(this);
		
		/* Create the buttons panel */
		theButtons = new JPanel();
		theButtons.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Actions"));
		
		/* Create the layout for the panel */
	    GroupLayout myLayout = new GroupLayout(theButtons);
	    theButtons.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addContainerGap()
                .addComponent(theInsButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theUndoButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theDelButton)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(theInsButton)
            .addComponent(theUndoButton)
            .addComponent(theDelButton)
        );
            
		/* Create the selection panel */
		theSelect = new JPanel();
		theSelect.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Selection"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theSelect);
	    theSelect.setLayout(myLayout);

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
            
		/* Create the regime panel */
		theRegime = new JPanel();
		theRegime.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Tax Regime"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theRegime);
	    theRegime.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addContainerGap()
    			.addComponent(myYear)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    			.addComponent(theYear)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
    			.addComponent(myRegime)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
    			.addComponent(theRegimesBox)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myYear)
	                .addComponent(theYear)
	                .addComponent(myRegime)
	                .addComponent(theRegimesBox)
        );
            
		/* Create the allowances panel */
		theAllows = new JPanel();
		theAllows.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Allowances"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theAllows);
	    theAllows.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(myAllow)
                    .addComponent(myLoAgeAllow)
                    .addComponent(myHiAgeAllow)
                    .addComponent(myRental)
                    .addComponent(myCapitalAllow))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(theAllowance)
                    .addComponent(theLoAgeAllow)
                    .addComponent(theHiAgeAllow)
                    .addComponent(theRental)
                    .addComponent(theCapitalAllow))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myAllow)
	                .addComponent(theAllowance))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myLoAgeAllow)
   	                .addComponent(theLoAgeAllow))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myHiAgeAllow)
   	                .addComponent(theHiAgeAllow))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myRental)
   	                .addComponent(theRental))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myCapitalAllow)
   	                .addComponent(theCapitalAllow))
   	            .addContainerGap())
        );
            
		/* Create the limits panel */
		theLimits = new JPanel();
		theLimits.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Limits"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theLimits);
	    theLimits.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(myAgeAllowLimit)
                    .addComponent(myAddAllowLimit))
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(theAgeAllowLimit)
                    .addComponent(theAddAllowLimit))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myAgeAllowLimit)
	                .addComponent(theAgeAllowLimit))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myAddAllowLimit)
   	                .addComponent(theAddAllowLimit))
   	            .addContainerGap(80,80))
        );
            
		/* Create the bands panel */
		theBands = new JPanel();
		theBands.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Tax Bands"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theBands);
	    theBands.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(myLoBand)
                    .addComponent(myBasicBand)
                    .addComponent(myAddIncBndry))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(theLoTaxBand)
                    .addComponent(theBasicTaxBand)
                    .addComponent(theAddIncomeBndry))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myLoBand)
	                .addComponent(theLoTaxBand))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myBasicBand)
   	                .addComponent(theBasicTaxBand))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myAddIncBndry)
   	                .addComponent(theAddIncomeBndry))
   	            .addContainerGap(50,50))
        );
            
		/* Create the standard rates panel */
		theStdRates = new JPanel();
		theStdRates.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Standard Rates"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theStdRates);
	    theStdRates.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(myLoTaxRate)
                    .addComponent(myBasicTaxRate)
                    .addComponent(myHiTaxRate)
                    .addComponent(myAddTaxRate))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(theLoTaxRate)
                    .addComponent(theBasicTaxRate)
                    .addComponent(theHiTaxRate)
                    .addComponent(theAddTaxRate))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myLoTaxRate)
	                .addComponent(theLoTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myBasicTaxRate)
   	                .addComponent(theBasicTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myHiTaxRate)
   	                .addComponent(theHiTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myAddTaxRate)
   	                .addComponent(theAddTaxRate))
   	            .addContainerGap())
        );
            
		/* Create the extra rates panel */
		theXtraRates = new JPanel();
		theXtraRates.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Interest/Dividend Rates"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theXtraRates);
	    theXtraRates.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(myIntTaxRate)
                    .addComponent(myDivTaxRate)
                    .addComponent(myHiDivTaxRate)
                    .addComponent(myAddDivTaxRate))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(theIntTaxRate)
                    .addComponent(theDivTaxRate)
                    .addComponent(theHiDivTaxRate)
                    .addComponent(theAddDivTaxRate))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myIntTaxRate)
	                .addComponent(theIntTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myDivTaxRate)
   	                .addComponent(theDivTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myHiDivTaxRate)
   	                .addComponent(theHiDivTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myAddDivTaxRate)
   	                .addComponent(theAddDivTaxRate))
   	            .addContainerGap())
        );
            
		/* Create the capital rates panel */
		theCapRates = new JPanel();
		theCapRates.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Capital Rates"));
		
		/* Create the layout for the panel */
	    myLayout = new GroupLayout(theCapRates);
	    theCapRates.setLayout(myLayout);

	    /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addGroup(myLayout.createSequentialGroup()
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
        			.addComponent(myCapTaxRate)
                    .addComponent(myHiCapTaxRate))
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
        		.addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        			.addComponent(theCapTaxRate)
                    .addComponent(theHiCapTaxRate))
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
	            .addContainerGap()
	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
	                .addComponent(myCapTaxRate)
	                .addComponent(theCapTaxRate))
   	            .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
   	                .addComponent(myHiCapTaxRate)
   	                .addComponent(theHiCapTaxRate))
   	            .addContainerGap(50,50))
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
                	.addComponent(theSaveButs.getPanel())
                    .addComponent(theButtons)
                    .addComponent(theSelect)
                    .addComponent(theRegime)
                    .addGroup(myLayout.createSequentialGroup()
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(theAllows)
                            .addComponent(theStdRates))
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(theLimits)
                            .addComponent(theXtraRates))
                        .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                            .addComponent(theBands)
                            .addComponent(theCapRates))))
  	            .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
		   	.addGroup(myLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(theSelect)
                .addContainerGap(10,30)
                .addComponent(theRegime)
                .addContainerGap(10,30)
	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(theAllows)
                    .addComponent(theLimits)
                    .addComponent(theBands))
                .addContainerGap(10,30)
	                .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(theStdRates)
                    .addComponent(theXtraRates)
                    .addComponent(theCapRates))
                .addContainerGap()
            	.addComponent(theButtons)
                .addContainerGap()
            	.addComponent(theSaveButs.getPanel()))
            );
        
        /* Create the debug entry, attach to MaintenanceDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("TaxYear");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
      
        /* Set initial display */
        showTaxYear();
	}
	
	/* hasUpdates */
	public boolean hasUpdates() {
		return ((theTaxYear != null) && (theTaxYear.hasChanges()));
	}
	
	/* hasErrors */
	public boolean hasErrors() {
		return ((theTaxYear != null) && (theTaxYear.hasErrors()));
	}
	
	/* isLocked */
	public boolean isLocked() { return false; }
	
	/* getEditState */
	public EditState getEditState() {
		if (theTaxYear == null) return EditState.CLEAN;
		return theTaxYear.getEditState();
	}
	
	/* print */
	public void printIt() {}
	
	/* performCommand */
	public void performCommand(financeCommand pCmd) {
		/* Switch on command */
		switch (pCmd) {
			case OK:
				saveData();
				break;
			case RESETALL:
				resetData();
				break;
			case VALIDATEALL:
				validate();
				break;
		}
		notifyChanges();			
	}
	
	/* Note that changes have been made */
	public void notifyChanges() {
		/* Lock down the table buttons and the selection */
		theSaveButs.setLockDown();
		theYearsBox.setEnabled(!hasUpdates());
		theShowDeleted.setEnabled(!hasUpdates());
		
		/* Show the Tax Year */
		showTaxYear();
		
		/* Adjust visible tabs */
		theParent.setVisibleTabs();
	}	
		
	/* Note that there has been a selection change */
	public void    notifySelection(Object obj)    {}
	
	/* resetData */
	public void resetData() {
		theTaxYear.clearErrors();
		theTaxYear.resetHistory();
		theTaxYear.validate();
		
		/* if this is a new Tax Year */
		if (theTaxYear.getState() == DataState.NEW) {
			/* Delete the new tax year */
			delNewTaxYear();
		}
	}
	
	/* validate */
	public void validate() {
		theTaxYear.clearErrors();
		theTaxYear.validate();
	}
	
	/* saveData */
	public void saveData() {
		/* Validate the data */
		validate();
		if (!hasErrors()) {
			/* Save details for the tax year */
			if (theTaxView != null)	theTaxView.applyChanges();
		}
	}
		
	/* refreshData */
	public void refreshData() {
		DataSet		myData;
		TaxYear  	myYear;
		TaxRegime	myRegime;

		DataList<TaxYear>.ListIterator 		myYearIterator;
		DataList<TaxRegime>.ListIterator	myRegIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access years and regimes */
		theTaxYears 	= myData.getTaxYears();
		theTaxRegimes 	= myData.getTaxRegimes();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* If we have years already populated */
		if (yearsPopulated) {	
			/* If we have a selected year */
			if (theTaxYear != null) {
				/* Find it in the new list */
				theTaxYear = theTaxYears.searchFor(theTaxYear.getDate());
			}
			
			/* Remove the years */
			theYearsBox.removeAllItems();
			yearsPopulated = false;
		}
		
		/* Create a Tax Year iterator */
		myYearIterator = theTaxYears.listIterator(true);
		
		/* Add the Tax Years to the years box in reverse order */
		while ((myYear  = myYearIterator.previous()) != null) {
			/* If the year is not deleted */
			if ((!doShowDeleted) &&
				(myYear.isDeleted())) continue;
			
			/* Add the item to the list */
			theYearsBox.addItem(Integer.toString(myYear.getDate().getYear()));
			yearsPopulated = true;
		}
		
		/* If we have a selected year */
		if (theTaxYear != null) {
			/* Select it in the new list */
			theYearsBox.setSelectedItem(Integer.toString(theTaxYear.getDate().getYear()));
		}
		
		/* If we have regimes already populated */
		if (regimesPopulated) {	
			/* Remove the types */
			theRegimesBox.removeAllItems();
			regimesPopulated = false;
		}
		
		/* Create a Tax Year iterator */
		myRegIterator = theTaxRegimes.listIterator();
		
		/* Add the Tax Regimes to the regimes box */
		while ((myRegime  = myRegIterator.next()) != null) {
			/* Add the item to the list */
			theRegimesBox.addItem(myRegime.getName());
			regimesPopulated = true;
		}
		
		/* Note that we have finished refreshing data */
		refreshingData = false;
		
		/* Show the current tax year */
		String myName = (String)theYearsBox.getSelectedItem();
		setSelection((myName != null) ? theTaxYears.searchFor(myName) : null);
	}
	
	/* Set Selection */
	public void setSelection(TaxYear pTaxYear) {
		/* Reset controls */
		theTaxView = null;
		theTaxYear = null;
		
		/* If we have a selected tax year */
		if (pTaxYear != null) {
			/* If we need to show deleted items */
			if ((!doShowDeleted) && (pTaxYear.isDeleted())) {
				/* Set the flag correctly */
				doShowDeleted = true;
				theShowDeleted.setSelected(doShowDeleted);
			}
			
			/* Create the view of the tax year */
			theTaxView = theView.new ViewTaxYear(pTaxYear);
			theDebugEntry.setObject(theTaxView);
		
			/* Access the tax year */
			theTaxYear = theTaxView.getTaxYear();
		}
		
		/* notify changes */
		notifyChanges();
	}

	/* Show the tax year */
	private void showTaxYear() {
		TaxRegime 	myRegime;
		DataList<TaxYear>.ListIterator myIterator;
		
		/* If we have an active year */
		if (theTaxYear != null) {
			/* Access the tax regime */
			myRegime = theTaxYear.getTaxRegime();
			
			/* Set the Year */
			theYear.setText(Integer.toString(theTaxYear.getDate().getYear()));
			theYear.setEnabled(!theTaxYear.isDeleted());
			theYear.setEditable(false);

			/* Set the Regime */
			theRegimesBox.setSelectedItem(myRegime.getName());
			theRegimesBox.setEnabled(!theTaxYear.isDeleted());

			/* Set the Allowance */
			theAllowance.setText(theTaxYear.getAllowance().format(true));
			theAllowance.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theAllowance, TaxYear.FIELD_ALLOW, theTaxYear, true, false);

			/* Set the LoAge Allowance */
			theLoAgeAllow.setText(theTaxYear.getLoAgeAllow().format(true));
			theLoAgeAllow.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theLoAgeAllow, TaxYear.FIELD_LOAGAL, theTaxYear, true, false);
		
			/* Set the HiAge Allowance */
			theHiAgeAllow.setText(theTaxYear.getHiAgeAllow().format(true));
			theHiAgeAllow.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theHiAgeAllow, TaxYear.FIELD_HIAGAL, theTaxYear, true, false);
		
			/* Set the Capital Allowance */
			theCapitalAllow.setText(theTaxYear.getCapitalAllow().format(true));
			theCapitalAllow.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theCapitalAllow, TaxYear.FIELD_CAPALW, theTaxYear, true, false);
		
			/* Set the Rental Allowance */
			theRental.setText(theTaxYear.getRentalAllowance().format(true));
			theRental.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theRental, TaxYear.FIELD_RENTAL, theTaxYear, true, false);
		
			/* Set the Age Allowance Limit */
			theAgeAllowLimit.setText(theTaxYear.getAgeAllowLimit().format(true));
			theAgeAllowLimit.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theAgeAllowLimit, TaxYear.FIELD_AGELMT, theTaxYear, true, false);
		
			/* Set the Additional Allowance Limit */
			theAddAllowLimit.setText((theTaxYear.hasAdditionalTaxBand() &&
					 				  theTaxYear.getAddAllowLimit() != null) 
										? theTaxYear.getAddAllowLimit().format(true)
										: "");
			theAddAllowLimit.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
			theParent.formatComponent(theAddAllowLimit, TaxYear.FIELD_ADDLMT, theTaxYear,
									  true, (theTaxYear.getAddAllowLimit() == null));
		
			/* Set the Additional Income Boundary */
			theAddIncomeBndry.setText((theTaxYear.hasAdditionalTaxBand() &&
					 				   theTaxYear.getAddIncBound() != null) 
											? theTaxYear.getAddIncBound().format(true)
											: "");
			theAddIncomeBndry.setEnabled(!theTaxYear.isDeleted()  && theTaxYear.hasAdditionalTaxBand());
			theParent.formatComponent(theAddIncomeBndry, TaxYear.FIELD_ADDBDY, theTaxYear, 
									  true, (theTaxYear.getAddAllowLimit() == null));
		
			/* Set the Low Tax Band */
			theLoTaxBand.setText(theTaxYear.getLoBand().format(true));
			theLoTaxBand.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theLoTaxBand, TaxYear.FIELD_LOBAND, theTaxYear, true, false);
		
			/* Set the Basic Tax Band */
			theBasicTaxBand.setText(theTaxYear.getBasicBand().format(true));
			theBasicTaxBand.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theBasicTaxBand, TaxYear.FIELD_BSBAND, theTaxYear, true, false);
		
			/* Set the Low Tax Rate */
			theLoTaxRate.setText(theTaxYear.getLoTaxRate().format(true));
			theLoTaxRate.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theLoTaxRate, TaxYear.FIELD_LOTAX, theTaxYear, true, false);
		
			/* Set the Basic Tax Rate */
			theBasicTaxRate.setText(theTaxYear.getBasicTaxRate().format(true));
			theBasicTaxRate.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theBasicTaxRate, TaxYear.FIELD_BASTAX, theTaxYear, true, false);
		
			/* Set the High Tax Rate */
			theHiTaxRate.setText(theTaxYear.getHiTaxRate().format(true));
			theHiTaxRate.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theHiTaxRate, TaxYear.FIELD_HITAX, theTaxYear, true, false);
						
			/* Set the Interest Tax Rate */
			theIntTaxRate.setText(theTaxYear.getIntTaxRate().format(true));
			theIntTaxRate.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theIntTaxRate, TaxYear.FIELD_INTTAX, theTaxYear, true, false);
		
			/* Set the Dividend Tax Rate */
			theDivTaxRate.setText(theTaxYear.getDivTaxRate().format(true));
			theDivTaxRate.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theDivTaxRate, TaxYear.FIELD_DIVTAX, theTaxYear, true, false);
		
			/* Set the High Dividend Tax Rate */
			theHiDivTaxRate.setText(theTaxYear.getHiDivTaxRate().format(true));
			theHiDivTaxRate.setEnabled(!theTaxYear.isDeleted());
			theParent.formatComponent(theHiDivTaxRate, TaxYear.FIELD_HDVTAX, theTaxYear, true, false);
		
			/* Set the Additional Tax Rate */
			theAddTaxRate.setText((theTaxYear.hasAdditionalTaxBand() &&
					 			   theTaxYear.getAddTaxRate() != null) 
										? theTaxYear.getAddTaxRate().format(true)
										: "");
			theAddTaxRate.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
			theParent.formatComponent(theAddTaxRate, TaxYear.FIELD_ADDTAX, theTaxYear, 
									  true, (theTaxYear.getAddTaxRate() == null));
		
			/* Set the Additional Dividend Tax Rate */
			theAddDivTaxRate.setText((theTaxYear.hasAdditionalTaxBand() &&
					 				  theTaxYear.getAddDivTaxRate() != null) 
										? theTaxYear.getAddDivTaxRate().format(true)
										: "");
			theAddDivTaxRate.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
			theParent.formatComponent(theAddDivTaxRate, TaxYear.FIELD_ADVTAX, theTaxYear, 
									  true, (theTaxYear.getAddDivTaxRate() == null));
		
			/* Set the Capital Tax Rate */
			theCapTaxRate.setText((!theTaxYear.hasCapitalGainsAsIncome()  &&
					 			   theTaxYear.getCapTaxRate() != null)
										? theTaxYear.getCapTaxRate().format(true)
										: "");
			theCapTaxRate.setEnabled(!theTaxYear.isDeleted() && !theTaxYear.hasCapitalGainsAsIncome());
			theParent.formatComponent(theCapTaxRate, TaxYear.FIELD_CAPTAX, theTaxYear, 
									  true, (theTaxYear.getCapTaxRate() == null));
		
			/* Set the High Capital Tax Rate */
			theHiCapTaxRate.setText((!theTaxYear.hasCapitalGainsAsIncome() &&
									 theTaxYear.getHiCapTaxRate() != null)
										? theTaxYear.getHiCapTaxRate().format(true)
										: null);
			theHiCapTaxRate.setEnabled(!theTaxYear.isDeleted() && !theTaxYear.hasCapitalGainsAsIncome());
			theParent.formatComponent(theHiCapTaxRate, TaxYear.FIELD_HCPTAX, theTaxYear, 
									  true, (theTaxYear.getHiCapTaxRate() == null));
		
			/* Make sure buttons are visible */
			theDelButton.setVisible(theTaxYear.isDeleted() || 
									((!theTaxYear.isActive()) &&
									 ((theTaxYears.peekPrevious(theTaxYear) == null) ||
									  (theTaxYears.peekNext(theTaxYear) == null))));
			theDelButton.setText(theTaxYear.isDeleted() ? "Recover" : "Delete");
			
			/* Enable buttons */
			myIterator = theTaxYears.listIterator(true);
			theInsButton.setEnabled(!theTaxYear.hasChanges() &&
									myIterator.peekLast().isActive());
			theUndoButton.setEnabled(theTaxYear.hasChanges());
		}
		
		/* else no account */
		else {
			/* Set blank text */
			theYear.setText("");
			theAllowance.setText("");
			theLoAgeAllow.setText("");
			theHiAgeAllow.setText("");
			theRental.setText("");
			theCapitalAllow.setText("");
			theAgeAllowLimit.setText("");
			theAddAllowLimit.setText("");
			theAddIncomeBndry.setText("");
			theLoTaxBand.setText("");
			theBasicTaxBand.setText("");
			theLoTaxRate.setText("");
			theBasicTaxRate.setText("");
			theHiTaxRate.setText("");
			theIntTaxRate.setText("");
			theDivTaxRate.setText("");
			theHiDivTaxRate.setText("");
			theCapTaxRate.setText("");
			theHiCapTaxRate.setText("");
			theAddTaxRate.setText("");
			theAddDivTaxRate.setText("");
			
			/* Disable data entry */
			theYear.setEnabled(false);
			theAllowance.setEnabled(false);
			theLoAgeAllow.setEnabled(false);
			theHiAgeAllow.setEnabled(false);
			theRental.setEnabled(false);
			theCapitalAllow.setEnabled(false);
			theAgeAllowLimit.setEnabled(false);
			theAddAllowLimit.setEnabled(false);
			theAddIncomeBndry.setEnabled(false);
			theLoTaxBand.setEnabled(false);
			theBasicTaxBand.setEnabled(false);
			theLoTaxRate.setEnabled(false);
			theBasicTaxRate.setEnabled(false);
			theHiTaxRate.setEnabled(false);
			theIntTaxRate.setEnabled(false);
			theDivTaxRate.setEnabled(false);
			theHiDivTaxRate.setEnabled(false);
			theCapTaxRate.setEnabled(false);
			theHiCapTaxRate.setEnabled(false);
			theAddTaxRate.setEnabled(false);
			theAddDivTaxRate.setEnabled(false);
			
			/* Handle buttons */
			theUndoButton.setEnabled(false);
			theInsButton.setEnabled(false);
			theDelButton.setVisible(false);
		}
	}
	
	/* Update text */
	private void updateText() {
		String			myText;
		Money 			myMoney;
		Rate  			myRate;

		/* Access the value */
		myText  = theAllowance.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setAllowance(myMoney);    

		/* Access the value */
		myText  = theLoAgeAllow.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setLoAgeAllow(myMoney);    

		/* Access the value */
		myText  = theHiAgeAllow.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setHiAgeAllow(myMoney);    

		/* Access the value */
		myText  = theCapitalAllow.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setCapitalAllow(myMoney);    

		/* Access the value */
		myText  = theRental.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setRentalAllowance(myMoney);    

		/* Access the value */
		myText  = theAgeAllowLimit.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setAgeAllowLimit(myMoney);    

		/* Access the value */
		myText  = theAddAllowLimit.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setAddAllowLimit(myMoney);    

		/* Access the value */
		myText  = theAddIncomeBndry.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setAddIncBound(myMoney);    

		/* Access the value */
		myText  = theLoTaxBand.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setLoBand(myMoney);    

		/* Access the value */
		myText  = theBasicTaxBand.getText();
		myMoney = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myMoney = Money.Parse(myText); 
		
		/* Store the appropriate value */
		if (myMoney != null) theTaxYear.setBasicBand(myMoney);    

		/* Access the value */
		myText  = theLoTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setLoTaxRate(myRate);    

		/* Access the value */
		myText  = theBasicTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setBasicTaxRate(myRate);    

		/* Access the value */
		myText  = theHiTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setHiTaxRate(myRate);    

		/* Access the value */
		myText  = theIntTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setIntTaxRate(myRate);    

		/* Access the value */
		myText  = theDivTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setDivTaxRate(myRate);    

		/* Access the value */
		myText  = theHiDivTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setHiDivTaxRate(myRate);    

		/* Access the value */
		myText  = theAddTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setAddTaxRate(myRate);    

		/* Access the value */
		myText  = theAddDivTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setAddDivTaxRate(myRate);    

		/* Access the value */
		myText  = theCapTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value */
		if (myRate != null) theTaxYear.setCapTaxRate(myRate);    

		/* Access the value */
		myText  = theHiCapTaxRate.getText();
		myRate = null;
		if (myText.length() == 0) myText = null;
		if (myText != null) myRate = Rate.Parse(myText); 
		
		/* Store the appropriate value (allow null) */
		theTaxYear.setHiCapTaxRate(myRate);    
	}
	
	/* Undo changes */
	private void undoChanges() {
		/* If the account has changes */
		if (theTaxYear.hasHistory()) {
			/* Pop last value */
			theTaxYear.popHistory();
			
			/* Re-validate the item */
			theTaxYear.clearErrors();
			theTaxYear.validate();
		
			/* If the item is now clean */
			if (!theTaxYear.hasHistory()) {
				/* Set the new status */
				theTaxYear.setState(DataState.CLEAN);
			}
			
			/* Notify changes */
			notifyChanges();
		}
		
		/* else if this is a new tax year */
		else if (theTaxYear.getState() == DataState.NEW) {
			/* Delete the new tax year */
			delNewTaxYear();
		}
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
				setSelection(theTaxYears.searchFor(myName));
				notifyChanges();
			}
		}
		
		/* If this event relates to the regimes box */
		if (evt.getSource() == (Object)theRegimesBox) {
			myName = (String)evt.getItem();
			if (evt.getStateChange() == ItemEvent.SELECTED) {
				/* Push history */
				theTaxYear.pushHistory();
				
				/* Select the new regime */
				theTaxYear.setTaxRegime(theTaxRegimes.searchFor(myName));
				
				/* Clear Capital tax rates if required */
				if (theTaxYear.hasCapitalGainsAsIncome()) {
					theTaxYear.setCapTaxRate(null);
					theTaxYear.setHiCapTaxRate(null);
				}

				/* Clear Additional values if required */
				if (!theTaxYear.hasAdditionalTaxBand()) {
					theTaxYear.setAddAllowLimit(null);
					theTaxYear.setAddIncBound(null);
					theTaxYear.setAddTaxRate(null);
					theTaxYear.setAddDivTaxRate(null);
				}

				/* Check for changes */
				if (theTaxYear.checkForHistory()) {
					/* Note that the item has changed */
					theTaxYear.setState(DataState.CHANGED);
					
					/* Note that changes have occurred */
					notifyChanges();
				}
			}
		}
		
		/* If this event relates to the showDeleted box */
		if (evt.getSource() == (Object)theShowDeleted) {
			/* Note the new criteria and re-build lists */
			doShowDeleted = theShowDeleted.isSelected();
			refreshData();
		}
	}

	/* Delete New Account */
	private void delNewTaxYear() {
		DataList<TaxYear>.ListIterator myIterator;
		
		/* Access the iterator */
		myIterator = theTaxYears.listIterator();
		
		/* Select the last tax year */
		setSelection(myIterator.peekLast());
	}
	
	/* New Account */
	private void newTaxYear() {
		/* Create a tax view for a new tax year */
		theTaxView = theView.new ViewTaxYear();
		theDebugEntry.setObject(theTaxView);
	
		/* Access the account */
		theTaxYear = theTaxView.getTaxYear();			
		
		/* Notify changes */
		notifyChanges();
	}
	
	/* ActionPerformed listener event */
	public void actionPerformed(ActionEvent evt) {			
		/* If this event relates to the new button */
		if (evt.getSource() == (Object)theInsButton) {
			/* Create the new tax year */
			newTaxYear();
			return;
		}
		
		/* If this event relates to the delete button */
		else if (evt.getSource() == (Object)theDelButton) {
			/* else if this is a new account */
			if (theTaxYear.getState() == DataState.NEW) {
				/* Delete the new tax year */
				delNewTaxYear();
			}
			
			/* Else we should just delete/recover the year */
			else {
				/* Set the appropriate state */
				theTaxYear.setState(theTaxYear.isDeleted() ? DataState.RECOVERED
														   : DataState.DELETED);
				
				/* Notify changes */
				notifyChanges();
			}
		}
		
		/* If this event relates to the undo button */
		else if (evt.getSource() == (Object)theUndoButton) {
			/* Undo the changes */
			undoChanges();
			return;
		}
		
		/* Push history */
		theTaxYear.pushHistory();
		
		/* If this event relates to the update-able fields */
		if ((evt.getSource() == (Object)theAllowance)  		||
			(evt.getSource() == (Object)theLoAgeAllow) 		||
			(evt.getSource() == (Object)theHiAgeAllow) 		||
			(evt.getSource() == (Object)theRental)     		||
			(evt.getSource() == (Object)theCapitalAllow)	||
			(evt.getSource() == (Object)theAgeAllowLimit)	||
			(evt.getSource() == (Object)theAddAllowLimit)	||
			(evt.getSource() == (Object)theAddIncomeBndry)	||
		    (evt.getSource() == (Object)theLoTaxBand)  		||
		    (evt.getSource() == (Object)theBasicTaxBand)	||
		    (evt.getSource() == (Object)theLoTaxRate)  		||
		    (evt.getSource() == (Object)theBasicTaxRate)	||
			(evt.getSource() == (Object)theHiTaxRate)  		||
		    (evt.getSource() == (Object)theIntTaxRate) 		||
		    (evt.getSource() == (Object)theDivTaxRate) 		||
			(evt.getSource() == (Object)theHiDivTaxRate)	||
		    (evt.getSource() == (Object)theCapTaxRate) 		||
			(evt.getSource() == (Object)theHiCapTaxRate)	||
		    (evt.getSource() == (Object)theAddTaxRate) 		||
			(evt.getSource() == (Object)theAddDivTaxRate)) {
			/* Update the text */
			updateText();
		}
		
		/* Check for changes */
		if (theTaxYear.checkForHistory()) {
			/* Note that the item has changed */
			theTaxYear.setState(DataState.CHANGED);
			
			/* Note that changes have occurred */
			notifyChanges();
		}
	}			
}
