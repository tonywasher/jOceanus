package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import uk.co.tolcroft.finance.ui.controls.*;
import uk.co.tolcroft.finance.views.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.Decimal.*;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.help.DebugManager;
import uk.co.tolcroft.models.help.DebugManager.*;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.ItemField;
import uk.co.tolcroft.models.ui.ItemField.FieldSet;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.ui.ValueField;
import uk.co.tolcroft.models.ui.StdInterfaces.*;
import uk.co.tolcroft.models.ui.ValueField.ValueClass;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class MaintTaxYear implements stdPanel {
	/* Properties */
	private MaintenanceTab		theParent			= null;
	private JPanel          	thePanel			= null;
	private JPanel				theButtons			= null;
	private TaxYearSelect		theSelect			= null;
	private JPanel				theRegime			= null;
	private JPanel				theAllows			= null;
	private JPanel				theBands			= null;
	private JPanel				theLimits			= null;
	private JPanel				theStdRates			= null;
	private JPanel				theXtraRates		= null;
	private JPanel				theCapRates			= null;
	private SaveButtons  		theSaveButs   		= null;
	private JComboBox			theRegimesBox		= null;
	private JTextField			theYear				= null;
	private FieldSet			theFieldSet			= null;
	private ItemField			theAllowance		= null;
	private ItemField			theLoAgeAllow		= null;
	private ItemField			theHiAgeAllow		= null;
	private ItemField			theCapitalAllow		= null;
	private ItemField			theAgeAllowLimit	= null;
	private ItemField			theAddAllowLimit	= null;
	private ItemField			theAddIncomeBndry	= null;
	private ItemField			theRental			= null;
	private ItemField			theLoTaxBand		= null;
	private ItemField			theBasicTaxBand		= null;
	private ItemField			theLoTaxRate		= null;
	private ItemField			theBasicTaxRate		= null;
	private ItemField			theHiTaxRate		= null;
	private ItemField			theIntTaxRate		= null;
	private ItemField			theDivTaxRate		= null;
	private ItemField			theHiDivTaxRate		= null;
	private ItemField			theAddTaxRate		= null;
	private ItemField			theAddDivTaxRate	= null;
	private ItemField			theCapTaxRate		= null;
	private ItemField			theHiCapTaxRate		= null;
	private JButton				theDelButton		= null;
	private JButton				theUndoButton		= null;
	private TaxYear				theTaxYear			= null;
	private TaxYear.List		theTaxView			= null;
	private TaxYear.List		theTaxYears			= null;
	private TaxRegime.List		theTaxRegimes		= null;
	private DebugEntry			theDebugEntry		= null;
	private ErrorPanel			theError			= null;
	private boolean				refreshingData		= false;
	private boolean				regimesPopulated	= false;
	private boolean				doShowDeleted		= false;
	private View				theView				= null;
	private ViewList			theViewSet			= null;
	private ListClass			theViewList			= null;
	
	/* Access methods */
	public JPanel   getPanel()      { return thePanel; }
	public TaxYear	getTaxYear()	{ return theTaxYear; }
	
	/* Access the debug entry */
	public DebugEntry 	getDebugEntry()		{ return theDebugEntry; }
	public DebugManager getDebugManager() 	{ return theParent.getDebugManager(); }
	
	/* Constructor */
	public MaintTaxYear(MaintenanceTab pParent) {
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
		
		/* Build the View set and List */
		theViewSet	= new ViewList(theView);
		theViewList = theViewSet.registerClass(TaxYear.class);

		/* Create the labels */
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
		
		/* Build the field set */
		theFieldSet		= new FieldSet();
		
		/* Create the combo box and add to the field set */
		theRegimesBox  	= new JComboBox();
		theFieldSet.addItemField(new ItemField(theRegimesBox, TaxYear.FIELD_REGIME));
		
		/* Create the TaxYearSelect panel */
		theSelect = new TaxYearSelect(theView, this);
		
		/* Create the text fields */
		theYear				= new JTextField();
		theAllowance 		= new ItemField(ValueClass.Money, TaxYear.FIELD_ALLOW, 	theFieldSet);
		theLoAgeAllow 		= new ItemField(ValueClass.Money, TaxYear.FIELD_LOAGAL, theFieldSet);
		theHiAgeAllow 		= new ItemField(ValueClass.Money, TaxYear.FIELD_HIAGAL, theFieldSet);
		theCapitalAllow		= new ItemField(ValueClass.Money, TaxYear.FIELD_CAPALW, theFieldSet);
		theAgeAllowLimit	= new ItemField(ValueClass.Money, TaxYear.FIELD_AGELMT, theFieldSet);
		theAddAllowLimit	= new ItemField(ValueClass.Money, TaxYear.FIELD_ADDLMT, theFieldSet);
		theAddIncomeBndry	= new ItemField(ValueClass.Money, TaxYear.FIELD_ADDBDY, theFieldSet);
		theLoTaxBand 		= new ItemField(ValueClass.Money, TaxYear.FIELD_LOBAND, theFieldSet);
		theBasicTaxBand		= new ItemField(ValueClass.Money, TaxYear.FIELD_BSBAND, theFieldSet);
		theRental			= new ItemField(ValueClass.Money, TaxYear.FIELD_RENTAL, theFieldSet);
		theLoTaxRate 		= new ItemField(ValueClass.Rate, TaxYear.FIELD_LOTAX,	theFieldSet);
		theBasicTaxRate 	= new ItemField(ValueClass.Rate, TaxYear.FIELD_BASTAX, 	theFieldSet);
		theHiTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_HITAX, 	theFieldSet);
		theIntTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_INTTAX, 	theFieldSet);
		theDivTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_DIVTAX, 	theFieldSet);
		theHiDivTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_HDVTAX, 	theFieldSet);
		theAddTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_ADDTAX, 	theFieldSet);
		theAddDivTaxRate	= new ItemField(ValueClass.Rate, TaxYear.FIELD_ADVTAX, 	theFieldSet);
		theCapTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_CAPTAX, 	theFieldSet);
		theHiCapTaxRate		= new ItemField(ValueClass.Rate, TaxYear.FIELD_HCPTAX, 	theFieldSet);

		/* The Year field is not edit-able */
		theYear.setEditable(false);
		
		/* Create the buttons */
		theDelButton  = new JButton();
		theUndoButton = new JButton("Undo");
		
		/* Create listener */
		TaxYearListener myListener = new TaxYearListener();
		
		/* Add listeners */
		theRegimesBox.addItemListener(myListener);
		theAllowance.addPropertyChangeListener(ValueField.valueName, myListener);
		theLoAgeAllow.addPropertyChangeListener(ValueField.valueName, myListener);
		theHiAgeAllow.addPropertyChangeListener(ValueField.valueName, myListener);
		theCapitalAllow.addPropertyChangeListener(ValueField.valueName, myListener);
		theAgeAllowLimit.addPropertyChangeListener(ValueField.valueName, myListener);
		theAddAllowLimit.addPropertyChangeListener(ValueField.valueName, myListener);
		theAddIncomeBndry.addPropertyChangeListener(ValueField.valueName, myListener);
		theRental.addPropertyChangeListener(ValueField.valueName, myListener);
		theLoTaxBand.addPropertyChangeListener(ValueField.valueName, myListener);
		theBasicTaxBand.addPropertyChangeListener(ValueField.valueName, myListener);
		theLoTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theBasicTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theHiTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theIntTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theDivTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theHiDivTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theAddTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theAddDivTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theCapTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theHiCapTaxRate.addPropertyChangeListener(ValueField.valueName, myListener);
		theDelButton.addActionListener(myListener);
		theUndoButton.addActionListener(myListener);

		/* Create the Table buttons panel */
		theSaveButs = new SaveButtons(this);
        
        /* Create the debug entry, attach to MaintenanceDebug entry and hide it */
        DebugManager myDebugMgr	= theView.getDebugMgr();
        theDebugEntry = myDebugMgr.new DebugEntry("TaxYear");
        theDebugEntry.addAsChildOf(pParent.getDebugEntry());
		
        /* Create the error panel for this view */
        theError = new ErrorPanel(this);
        
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
                .addComponent(theUndoButton)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(theDelButton)
                .addContainerGap())
        );
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
        	.addComponent(theUndoButton)
            .addComponent(theDelButton)
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
                    .addComponent(theError.getPanel())
                    .addComponent(theSelect.getPanel())
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
                .addComponent(theError.getPanel())
                .addComponent(theSelect.getPanel())
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
	public void performCommand(stdCommand pCmd) {
		/* Switch on command */
		switch (pCmd) {
			case OK:
				saveData();
				break;
			case RESETALL:
				resetData();
				break;
		}
		notifyChanges();			
	}
	
	/* Note that changes have been made */
	public void notifyChanges() {
		/* Lock down the table buttons and the selection */
		theSaveButs.setLockDown();
		theSelect.getPanel().setEnabled(!hasUpdates());
		
		/* Show the Tax Year */
		showTaxYear();
		
		/* Adjust visible tabs */
		theParent.setVisibility();
	}	
		
	/* Note that there has been a selection change */
	public void    notifySelection(Object obj)    {
		/* If this is a change from the year selection */
		if (obj == (Object) theSelect) {
			/* Set the new account */
			setSelection(theSelect.getTaxYear());
		}		
	}
	
 	/**
	 * Update Debug view 
	 */
	public void updateDebug() {			
		theDebugEntry.setObject(theTaxView);
	}
		
	/* resetData */
	public void resetData() {
		theTaxYear.clearErrors();
		theTaxYear.resetHistory();
		theTaxYear.validate();
		
		/* Recalculate edit state */
		theTaxView.findEditState();
		
		/* Notify changes */
		notifyChanges();
		updateDebug();
	}
	
	/* validate */
	public void validate() {
		theTaxYear.clearErrors();
		theTaxYear.validate();
		updateDebug();
	}
	
	/* saveData */
	public void saveData() {
		/* Validate the data */
		validate();
		if (!hasErrors()) {
			/* Save details for the tax year */
			theViewSet.applyChanges();
		}
	}
		
	/**
	 * Lock on error
	 * @param isError is there an error (True/False)
	 */
	public void lockOnError(boolean isError) {
		/* Hide selection panel */
		theSelect.getPanel().setVisible(!isError);

		/* Lock regime areas */
		theRegime.setEnabled(!isError);

		/* Lock bands areas */
		theAllows.setEnabled(!isError);
		theLimits.setEnabled(!isError);
		theBands.setEnabled(!isError);

		/* Lock rates areas */
		theStdRates.setEnabled(!isError);
		theXtraRates.setEnabled(!isError);
		theCapRates.setEnabled(!isError);

		/* Lock row/tab buttons area */
		theButtons.setEnabled(!isError);
		theSaveButs.getPanel().setEnabled(!isError);
	}
	
	/* refreshData */
	public void refreshData() {
		FinanceData	myData;
		TaxRegime	myRegime;

		TaxRegime.List.ListIterator	myRegIterator;
		
		/* Access the data */
		myData = theView.getData();
		
		/* Access years and regimes */
		theTaxYears 	= myData.getTaxYears();
		theTaxRegimes 	= myData.getTaxRegimes();
	
		/* Note that we are refreshing data */
		refreshingData = true;
		
		/* Refresh the year selection */
		theSelect.refreshData();
			
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
			/* Skip regime if not enabled */
			if (!myRegime.getEnabled()) continue;
			
			/* Add the item to the list */
			theRegimesBox.addItem(myRegime.getName());
			regimesPopulated = true;
		}
		
		/* Note that we have finished refreshing data */
		refreshingData = false;
		
		/* Show the account */
		setSelection(theSelect.getTaxYear());
	}
	
	/* Set Selection */
	public void setSelection(TaxYear pTaxYear) {
		/* Reset controls */
		theTaxView	= null;
		theTaxYear 	= null;
		
		/* If we have a selected tax year */
		if (pTaxYear != null) {
			/* If we need to show deleted items */
			if ((!doShowDeleted) && (pTaxYear.isDeleted())) {
				/* Set the flag correctly */
				doShowDeleted = true;
			}
			
			/* Create the view of the tax year */
			theTaxView = theTaxYears.getEditList(pTaxYear);
		
			/* Access the tax year */
			theTaxYear = theTaxView.searchFor(pTaxYear.getDate());
		}
		
		/* Store list */
		theViewList.setDataList(theTaxView);
		
		/* notify changes */
		notifyChanges();
		updateDebug();
	}

	/* Show the tax year */
	private void showTaxYear() {
		TaxRegime 	myRegime;
		
		/* If we have an active year */
		if (theTaxYear != null) {
			/* Access the tax regime */
			myRegime = theTaxYear.getTaxRegime();
			
			/* Set the Year */
			theYear.setText(Integer.toString(theTaxYear.getDate().getYear()));
			theYear.setEnabled(!theTaxYear.isDeleted());

			/* Set the Regime */
			theRegimesBox.setSelectedItem(myRegime.getName());
			theRegimesBox.setEnabled(!theTaxYear.isDeleted());

			/* Set the Allowance */
			theAllowance.setValue(theTaxYear.getAllowance());
			theAllowance.setEnabled(!theTaxYear.isDeleted());
			
			/* Set the LoAge Allowance */
			theLoAgeAllow.setValue(theTaxYear.getLoAgeAllow());
			theLoAgeAllow.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the HiAge Allowance */
			theHiAgeAllow.setValue(theTaxYear.getHiAgeAllow());
			theHiAgeAllow.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Capital Allowance */
			theCapitalAllow.setValue(theTaxYear.getCapitalAllow());
			theCapitalAllow.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Rental Allowance */
			theRental.setValue(theTaxYear.getRentalAllowance());
			theRental.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Age Allowance Limit */
			theAgeAllowLimit.setValue(theTaxYear.getAgeAllowLimit());
			theAgeAllowLimit.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Additional Allowance Limit */
			theAddAllowLimit.setValue(theTaxYear.getAddAllowLimit());
			theAddAllowLimit.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
		
			/* Set the Additional Income Boundary */
			theAddIncomeBndry.setValue(theTaxYear.getAddIncBound());
			theAddIncomeBndry.setEnabled(!theTaxYear.isDeleted()  && theTaxYear.hasAdditionalTaxBand());
		
			/* Set the Low Tax Band */
			theLoTaxBand.setValue(theTaxYear.getLoBand());
			theLoTaxBand.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Basic Tax Band */
			theBasicTaxBand.setValue(theTaxYear.getBasicBand());
			theBasicTaxBand.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Low Tax Rate */
			theLoTaxRate.setValue(theTaxYear.getLoTaxRate());
			theLoTaxRate.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Basic Tax Rate */
			theBasicTaxRate.setValue(theTaxYear.getBasicTaxRate());
			theBasicTaxRate.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the High Tax Rate */
			theHiTaxRate.setValue(theTaxYear.getHiTaxRate());
			theHiTaxRate.setEnabled(!theTaxYear.isDeleted());
						
			/* Set the Interest Tax Rate */
			theIntTaxRate.setValue(theTaxYear.getIntTaxRate());
			theIntTaxRate.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Dividend Tax Rate */
			theDivTaxRate.setValue(theTaxYear.getDivTaxRate());
			theDivTaxRate.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the High Dividend Tax Rate */
			theHiDivTaxRate.setValue(theTaxYear.getHiDivTaxRate());
			theHiDivTaxRate.setEnabled(!theTaxYear.isDeleted());
		
			/* Set the Additional Tax Rate */
			theAddTaxRate.setValue(theTaxYear.getAddTaxRate());
			theAddTaxRate.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
		
			/* Set the Additional Dividend Tax Rate */
			theAddDivTaxRate.setValue(theTaxYear.getAddDivTaxRate());
			theAddDivTaxRate.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());
		
			/* Set the Capital Tax Rate */
			theCapTaxRate.setValue(theTaxYear.getCapTaxRate());
			theCapTaxRate.setEnabled(!theTaxYear.isDeleted() && !theTaxYear.hasCapitalGainsAsIncome());
		
			/* Set the High Capital Tax Rate */
			theHiCapTaxRate.setValue(theTaxYear.getHiCapTaxRate());
			theHiCapTaxRate.setEnabled(!theTaxYear.isDeleted() && !theTaxYear.hasCapitalGainsAsIncome());

			/* Render all fields in the set */
			theFieldSet.renderSet(theTaxYear);
			
			/* Make sure buttons are visible */
			theDelButton.setVisible(theTaxYear.isDeleted() || 
									((!theTaxYear.isActive()) &&
									 ((theTaxYears.peekPrevious(theTaxYear) == null) ||
									  (theTaxYears.peekNext(theTaxYear) == null))));
			theDelButton.setText(theTaxYear.isDeleted() ? "Recover" : "Delete");
			
			/* Enable buttons */
			theUndoButton.setEnabled(theTaxYear.hasChanges());
		}
		
		/* else no account */
		else {
			/* Set blank text */
			theYear.setText("");
			theAllowance.setValue(null);
			theLoAgeAllow.setValue(null);
			theHiAgeAllow.setValue(null);
			theRental.setValue(null);
			theCapitalAllow.setValue(null);
			theAgeAllowLimit.setValue(null);
			theAddAllowLimit.setValue(null);
			theAddIncomeBndry.setValue(null);
			theLoTaxBand.setValue(null);
			theBasicTaxBand.setValue(null);
			theLoTaxRate.setValue(null);
			theBasicTaxRate.setValue(null);
			theHiTaxRate.setValue(null);
			theIntTaxRate.setValue(null);
			theDivTaxRate.setValue(null);
			theHiDivTaxRate.setValue(null);
			theCapTaxRate.setValue(null);
			theHiCapTaxRate.setValue(null);
			theAddTaxRate.setValue(null);
			theAddDivTaxRate.setValue(null);
			
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
			theDelButton.setVisible(false);
		}
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
			updateDebug();
		}
	}
	
	/**
	 * TaxYearListener class 
	 */
	private class TaxYearListener implements ActionListener,
	 										 ItemListener,
	 										 PropertyChangeListener {
		@Override
		public void itemStateChanged(ItemEvent evt) {
			/* Ignore selection if refreshing data */
			if (refreshingData) return;
		
			/* If this event relates to the regimes box */
			if (evt.getSource() == theRegimesBox) {
				String myName = (String)evt.getItem();
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
					
						/* validate it */
						theTaxYear.clearErrors();
						theTaxYear.validate();
					
						/* Note that changes have occurred */
						notifyChanges();
						updateDebug();
					}
				}
			}
		}

		@Override
		public void actionPerformed(ActionEvent evt) {	
			Object o = evt.getSource();
			
			/* If this event relates to the undo button */
			if (o == theUndoButton) {
				/* Undo the changes */
				undoChanges();
			}
		}
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			Object 	o 		= evt.getSource();
			
			/* Push history */
			theTaxYear.pushHistory();
		
			/* Protect against exceptions */
			try {
				/* If this is our the Allowance */
				if (o == theAllowance) {
					/* Update the Tax Year */
					Money myValue = (Money)theAllowance.getValue();
					theTaxYear.setAllowance(myValue);    
				}	
			
				/* If this is our LoAge Allowance */
				else if (o == theLoAgeAllow) {
					/* Update the Tax Year */
					Money myValue = (Money)theLoAgeAllow.getValue();
					theTaxYear.setLoAgeAllow(myValue);    
				}
			
				/* If this is our HiAge Allowance */
				else if (o == theHiAgeAllow) {
					/* Update the Tax Year */
					Money myValue = (Money)theHiAgeAllow.getValue();
					theTaxYear.setHiAgeAllow(myValue);    
				}
				
				/* If this is our Rental */
				else if (o == theRental) {
					/* Update the Tax Year */
					Money myValue = (Money)theRental.getValue();
					theTaxYear.setRentalAllowance(myValue);    
				}
			
				/* If this is our Capital Allowance */
				else if (o == theCapitalAllow) {
					/* Update the Tax Year */
					Money myValue = (Money)theCapitalAllow.getValue();
					theTaxYear.setCapitalAllow(myValue);    
				}
				
				/* If this is our Age Allowance Limit*/
				else if (o == theAgeAllowLimit) {
					/* Update the Tax Year */
					Money myValue = (Money)theAgeAllowLimit.getValue();
					theTaxYear.setAgeAllowLimit(myValue);    
				}
			
				/* If this is our Additional Allowance Limit */
				else if (o == theAddAllowLimit) {
					/* Update the Tax Year */
					Money myValue = (Money)theAddAllowLimit.getValue();
					theTaxYear.setAddAllowLimit(myValue);    
				}
			
				/* If this is our Additional Income Boundary */
				else if (o == theAddIncomeBndry) {
					/* Update the Tax Year */
					Money myValue = (Money)theAddIncomeBndry.getValue();
					theTaxYear.setAddIncBound(myValue);    
				}
						
				/* If this is our LoTaxBand */
				else if (o == theLoTaxBand) {
					/* Update the Tax Year */
					Money myValue = (Money)theLoTaxBand.getValue();
					theTaxYear.setLoBand(myValue);    
				}
			
				/* If this is our Basic Tax Band */
				else if (o == theBasicTaxBand) {
					/* Update the Tax Year */
					Money myValue = (Money)theBasicTaxBand.getValue();
					theTaxYear.setBasicBand(myValue);    
				}
						
				/* If this is our Low Tax Rate */
				else if (o == theLoTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theLoTaxRate.getValue();
					theTaxYear.setLoTaxRate(myValue);    
				}
			
				/* If this is our Basic Tax Rate */
				else if (o == theBasicTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theBasicTaxRate.getValue();
					theTaxYear.setBasicTaxRate(myValue);    
				}
			
				/* If this is our High Tax Rate */
				else if (o == theHiTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theHiTaxRate.getValue();
					theTaxYear.setHiTaxRate(myValue);    
				}
			
				/* If this is our Additional Tax Rate */
				else if (o == theAddTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theAddTaxRate.getValue();
					theTaxYear.setAddTaxRate(myValue);    
				}
				
				/* If this is our Interest Tax Rate */
				else if (o == theIntTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theIntTaxRate.getValue();
					theTaxYear.setIntTaxRate(myValue);    
				}
			
				/* If this is our Dividend Tax Rate */
				else if (o == theDivTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theDivTaxRate.getValue();
					theTaxYear.setDivTaxRate(myValue);    
				}
			
				/* If this is our High Dividend Tax Rate */
				else if (o == theHiDivTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theHiDivTaxRate.getValue();
					theTaxYear.setHiDivTaxRate(myValue);    
				}
			
				/* If this is our Additional Tax Rate */
				else if (o == theAddDivTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theAddDivTaxRate.getValue();
					theTaxYear.setAddDivTaxRate(myValue);    
				}
			
				/* If this is our Capital Tax Rate */
				else if (o == theCapTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theCapTaxRate.getValue();
					theTaxYear.setCapTaxRate(myValue);    
				}
			
				/* If this is our High Capital Tax Rate */
				else if (o == theHiCapTaxRate) {
					/* Update the Tax Year */
					Rate myValue = (Rate)theHiCapTaxRate.getValue();
					theTaxYear.setHiCapTaxRate(myValue);    
				}
			}
			
			/* Handle Exceptions */
			catch (Throwable e) {
				/* Reset values */
				theTaxYear.popHistory();
				theTaxYear.pushHistory();
			
				/* Build the error */
				ModelException myError = new ModelException(ExceptionClass.DATA,
												  "Failed to update field",
												  e);
			
				/* Show the error */
				theError.setError(myError);
			}
			
			/* Check for changes */
			if (theTaxYear.checkForHistory()) {
				/* Note that the item has changed */
				theTaxYear.setState(DataState.CHANGED);
			
				/* validate it */
				theTaxYear.clearErrors();
				theTaxYear.validate();
			
				/* Note that changes have occurred */
				notifyChanges();
				updateDebug();
			}
		}
	}			
}
