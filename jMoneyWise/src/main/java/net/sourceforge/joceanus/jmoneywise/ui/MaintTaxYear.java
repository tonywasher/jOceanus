/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jdatamanager.DataType;
import net.sourceforge.joceanus.jdatamanager.EditState;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jdatamodels.ui.ErrorPanel;
import net.sourceforge.joceanus.jdatamodels.ui.SaveButtons;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdatamodels.views.UpdateEntry;
import net.sourceforge.joceanus.jdatamodels.views.UpdateSet;
import net.sourceforge.joceanus.jeventmanager.ActionDetailEvent;
import net.sourceforge.joceanus.jeventmanager.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jeventmanager.JEventPanel;
import net.sourceforge.joceanus.jfieldset.JFieldManager;
import net.sourceforge.joceanus.jfieldset.JFieldSet;
import net.sourceforge.joceanus.jfieldset.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jlayoutmanager.SpringUtilities;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.data.TaxInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.TaxYearSelect;
import net.sourceforge.joceanus.jmoneywise.views.View;

/**
 * TaxYear maintenance panel.
 * @author Tony Washer
 */
public class MaintTaxYear
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 6563675457151000036L;

    /**
     * Padding size.
     */
    private static final int PADDING_SIZE = 5;

    /**
     * Field Height.
     */
    private static final int FIELD_HEIGHT = 20;

    /**
     * Field Width.
     */
    private static final int FIELD_WIDTH = 200;

    /**
     * Grid rows.
     */
    private static final int GRID_ROWS = 2;

    /**
     * Grid Columns.
     */
    private static final int GRID_COLS = 3;

    /**
     * The tax year select panel.
     */
    private final TaxYearSelect theSelect;

    /**
     * The Regime panel.
     */
    private final JEnablePanel theRegime;

    /**
     * The Allowances panel.
     */
    private final JPanel theAllows;

    /**
     * The Bands panel.
     */
    private final JPanel theBands;

    /**
     * The Limits panel.
     */
    private final JPanel theLimits;

    /**
     * The Standard rates panel.
     */
    private final JPanel theStdRates;

    /**
     * The Extra Rates panel.
     */
    private final JPanel theXtraRates;

    /**
     * The Capital Rates panel.
     */
    private final JPanel theCapRates;

    /**
     * The save buttons panel.
     */
    private final SaveButtons theSaveButs;

    /**
     * The regimes comboBox.
     */
    private final JComboBox<TaxRegime> theRegimesBox;

    /**
     * The tax year field.
     */
    private final JTextField theYear;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<TaxYear> theFieldSet;

    /**
     * The delete button.
     */
    private final JButton theDelButton;

    /**
     * The tax year.
     */
    private transient TaxYear theTaxYear = null;

    /**
     * The tax year view.
     */
    private transient TaxYearList theTaxView = null;

    /**
     * The tax year list.
     */
    private transient TaxYearList theTaxYears = null;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The Error panel.
     */
    private final ErrorPanel theError;

    /**
     * Do we show deleted years.
     */
    private boolean doShowDeleted = false;

    /**
     * The view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Update Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The TaxYear Update Entry.
     */
    private final transient UpdateEntry<TaxYear> theYearsEntry;

    /**
     * The TaxInfo Update Entry.
     */
    private final transient UpdateEntry<TaxYearInfo> theInfoEntry;

    /**
     * Obtain the tax Year.
     * @return the tax year
     */
    public TaxYear getTaxYear() {
        return theTaxYear;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public MaintTaxYear(final View pView) {
        /* Record the view */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet(theView);
        theYearsEntry = theUpdateSet.registerClass(TaxYear.class);
        theInfoEntry = theUpdateSet.registerClass(TaxYearInfo.class);

        /* Create the New FieldSet */
        theFieldSet = new JFieldSet<TaxYear>(theFieldMgr);

        /* Create the Year fields and add to field set */
        JLabel myYearLabel = new JLabel("Year:");
        theYear = new JTextField();
        theYear.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theYear.setEditable(false);

        /* Create the combo box and add to the field set */
        JLabel myRegime = new JLabel("Tax Regime:");
        theRegimesBox = new JComboBox<TaxRegime>();
        theRegimesBox.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theFieldSet.addFieldElement(TaxYear.FIELD_REGIME, TaxRegime.class, myRegime, theRegimesBox);

        /* Create the buttons */
        theDelButton = new JButton();

        /* Create listener */
        TaxYearListener myListener = new TaxYearListener();
        theDelButton.addActionListener(myListener);
        theFieldSet.addActionListener(myListener);

        /* Create the TaxYearSelect panel */
        theSelect = new TaxYearSelect(theView);
        theSelect.addChangeListener(myListener);

        /* Create the Table buttons panel */
        theSaveButs = new SaveButtons(theUpdateSet);
        theSaveButs.addActionListener(myListener);

        /* Create the debug entry, attach to MaintenanceDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(TaxYear.class.getSimpleName());
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataEntry);
        theError.addChangeListener(myListener);

        /* Create the regime panel */
        theRegime = new JEnablePanel();
        theRegime.setBorder(BorderFactory.createTitledBorder("Tax Year"));

        /* Create the layout for the panel */
        theRegime.setLayout(new BoxLayout(theRegime, BoxLayout.X_AXIS));
        theRegime.add(Box.createHorizontalGlue());
        theRegime.add(myYearLabel);
        theRegime.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));
        theRegime.add(theYear);
        theRegime.add(Box.createHorizontalGlue());
        theRegime.add(myRegime);
        theRegime.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));
        theRegime.add(theRegimesBox);
        theRegime.add(Box.createHorizontalGlue());
        theRegime.add(theDelButton);
        theRegime.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));

        /* Create the subPanels */
        theAllows = buildAllowPanel();
        theLimits = buildAllowLimitPanel();
        theBands = buildTaxBandsPanel();
        theStdRates = buildRatesPanel();
        theXtraRates = buildXtraRatesPanel();
        theCapRates = buildCapRatesPanel();

        /* Create a grid panel for the details */
        JPanel myGridPanel = new JPanel(new GridLayout(GRID_ROWS, GRID_COLS, PADDING_SIZE, PADDING_SIZE));
        myGridPanel.add(theAllows);
        myGridPanel.add(theLimits);
        myGridPanel.add(theBands);
        myGridPanel.add(theStdRates);
        myGridPanel.add(theXtraRates);
        myGridPanel.add(theCapRates);

        /* Add the components */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theError);
        add(Box.createVerticalGlue());
        add(theSelect);
        add(Box.createVerticalGlue());
        add(theRegime);
        add(Box.createVerticalGlue());
        add(myGridPanel);
        add(Box.createVerticalGlue());
        add(theSaveButs);

        /* Set initial display */
        showTaxYear();

        /* Add listener to updateSet */
        theUpdateSet.addActionListener(myListener);
    }

    /**
     * Build Allowances subPanel.
     * @return the panel
     */
    private JPanel buildAllowPanel() {
        /* Allocate labels */
        JLabel myAllowLabel = new JLabel("Personal Allowance:", SwingConstants.TRAILING);
        JLabel myLoAgeLabel = new JLabel("Age 65-74 Allowance:", SwingConstants.TRAILING);
        JLabel myHiAgeLabel = new JLabel("Age 75+ Allowance:", SwingConstants.TRAILING);
        JLabel myCapLabel = new JLabel("Capital Allowance:", SwingConstants.TRAILING);
        JLabel myRentalLabel = new JLabel("Rental Allowance:", SwingConstants.TRAILING);

        /* Allocate text fields */
        JTextField myAllowance = new JTextField();
        JTextField myLoAgeAllow = new JTextField();
        JTextField myHiAgeAllow = new JTextField();
        JTextField myCapitalAllow = new JTextField();
        JTextField myRentalAllow = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myAllowance.setMaximumSize(myDims);
        myLoAgeAllow.setMaximumSize(myDims);
        myHiAgeAllow.setMaximumSize(myDims);
        myCapitalAllow.setMaximumSize(myDims);
        myRentalAllow.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.Allowance), DataType.MONEY, myAllowLabel, myAllowance);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LoAgeAllowance), DataType.MONEY, myLoAgeLabel, myLoAgeAllow);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HiAgeAllowance), DataType.MONEY, myHiAgeLabel, myHiAgeAllow);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CapitalAllowance), DataType.MONEY, myCapLabel, myCapitalAllow);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.RentalAllowance), DataType.MONEY, myRentalLabel, myRentalAllow);

        /* Create the allow panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Allowances"));

        /* Layout the allow panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myAllowLabel);
        myPanel.add(myAllowance);
        myPanel.add(myLoAgeLabel);
        myPanel.add(myLoAgeAllow);
        myPanel.add(myHiAgeLabel);
        myPanel.add(myHiAgeAllow);
        myPanel.add(myCapLabel);
        myPanel.add(myCapitalAllow);
        myPanel.add(myRentalLabel);
        myPanel.add(myRentalAllow);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Allowance Limits subPanel.
     * @return the panel
     */
    private JPanel buildAllowLimitPanel() {
        /* Allocate labels */
        JLabel myAgeLabel = new JLabel("Age Allowance Limit:", SwingConstants.TRAILING);
        JLabel myAddLabel = new JLabel("Additnl Allow Limit:", SwingConstants.TRAILING);

        /* Allocate text fields */
        JTextField myAgeLimit = new JTextField();
        JTextField myAddLimit = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myAgeLimit.setMaximumSize(myDims);
        myAddLimit.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AgeAllowanceLimit), DataType.MONEY, myAgeLabel, myAgeLimit);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalAllowanceLimit), DataType.MONEY, myAddLabel, myAddLimit);

        /* Create the limits panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Allowance Limits"));

        /* Layout the limits panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myAgeLabel);
        myPanel.add(myAgeLimit);
        myPanel.add(myAddLabel);
        myPanel.add(myAddLimit);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build TaxBands subPanel.
     * @return the panel
     */
    private JPanel buildTaxBandsPanel() {
        /* Allocate labels */
        JLabel myLoBandLabel = new JLabel("Low Tax Band:", SwingConstants.TRAILING);
        JLabel myBasicBandLabel = new JLabel("Basic Tax Band:", SwingConstants.TRAILING);
        JLabel myAddIncLabel = new JLabel("Additnl Tax Boundary:", SwingConstants.TRAILING);

        /* Allocate text fields */
        JTextField myLoBand = new JTextField();
        JTextField myBasicBand = new JTextField();
        JTextField myAddIncBdy = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myLoBand.setMaximumSize(myDims);
        myBasicBand.setMaximumSize(myDims);
        myAddIncBdy.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LoTaxBand), DataType.MONEY, myLoBandLabel, myLoBand);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.BasicTaxBand), DataType.MONEY, myBasicBandLabel, myBasicBand);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalIncomeThreshold), DataType.MONEY, myAddIncLabel, myAddIncBdy);

        /* Create the bands panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Tax Bands"));

        /* Layout the bands panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myLoBandLabel);
        myPanel.add(myLoBand);
        myPanel.add(myBasicBandLabel);
        myPanel.add(myBasicBand);
        myPanel.add(myAddIncLabel);
        myPanel.add(myAddIncBdy);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Rates subPanel.
     * @return the panel
     */
    private JPanel buildRatesPanel() {
        /* Allocate labels */
        JLabel myLoTaxLabel = new JLabel("Low Rate:", SwingConstants.TRAILING);
        JLabel myBasicTaxLabel = new JLabel("Basic Rate:", SwingConstants.TRAILING);
        JLabel myHiTaxLabel = new JLabel("High Rate:", SwingConstants.TRAILING);
        JLabel myAddTaxLabel = new JLabel("Additnl Rate:", SwingConstants.TRAILING);

        /* Allocate text fields */
        JTextField myLoTaxRate = new JTextField();
        JTextField myBasicTaxRate = new JTextField();
        JTextField myHiTaxRate = new JTextField();
        JTextField myAddTaxRate = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myLoTaxRate.setMaximumSize(myDims);
        myBasicTaxRate.setMaximumSize(myDims);
        myHiTaxRate.setMaximumSize(myDims);
        myAddTaxRate.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LoTaxRate), DataType.RATE, myLoTaxLabel, myLoTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.BasicTaxRate), DataType.RATE, myBasicTaxLabel, myBasicTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HiTaxRate), DataType.RATE, myHiTaxLabel, myHiTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalTaxRate), DataType.RATE, myAddTaxLabel, myAddTaxRate);

        /* Create the rates panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Standard Rates"));

        /* Layout the rates panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myLoTaxLabel);
        myPanel.add(myLoTaxRate);
        myPanel.add(myBasicTaxLabel);
        myPanel.add(myBasicTaxRate);
        myPanel.add(myHiTaxLabel);
        myPanel.add(myHiTaxRate);
        myPanel.add(myAddTaxLabel);
        myPanel.add(myAddTaxRate);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extra Rates subPanel.
     * @return the panel
     */
    private JPanel buildXtraRatesPanel() {
        /* Allocate labels */
        JLabel myIntTaxLabel = new JLabel("Interest Rate:", SwingConstants.TRAILING);
        JLabel myDivTaxLabel = new JLabel("Dividend Rate:", SwingConstants.TRAILING);
        JLabel myHiDivTaxLabel = new JLabel("High Dividend Rate:", SwingConstants.TRAILING);
        JLabel myAddDivTaxLabel = new JLabel("Additnl Dividend Rate:", SwingConstants.TRAILING);

        /* Allocate text fields */
        JTextField myIntTaxRate = new JTextField();
        JTextField myDivTaxRate = new JTextField();
        JTextField myHiDivTaxRate = new JTextField();
        JTextField myAddDivTaxRate = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myIntTaxRate.setMaximumSize(myDims);
        myDivTaxRate.setMaximumSize(myDims);
        myHiDivTaxRate.setMaximumSize(myDims);
        myAddDivTaxRate.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.InterestTaxRate), DataType.RATE, myIntTaxLabel, myIntTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.DividendTaxRate), DataType.RATE, myDivTaxLabel, myDivTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HiDividendTaxRate), DataType.RATE, myHiDivTaxLabel, myHiDivTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalDividendTaxRate), DataType.RATE, myAddDivTaxLabel, myAddDivTaxRate);

        /* Create the rates panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Interest/Dividend Rates"));

        /* Layout the rates panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myIntTaxLabel);
        myPanel.add(myIntTaxRate);
        myPanel.add(myDivTaxLabel);
        myPanel.add(myDivTaxRate);
        myPanel.add(myHiDivTaxLabel);
        myPanel.add(myHiDivTaxRate);
        myPanel.add(myAddDivTaxLabel);
        myPanel.add(myAddDivTaxRate);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build capital Rates subPanel.
     * @return the panel
     */
    private JPanel buildCapRatesPanel() {
        /* Allocate labels */
        JLabel myCapTaxLabel = new JLabel("Capital Rate:", SwingConstants.TRAILING);
        JLabel myHiCapTaxLabel = new JLabel("High Capital Rate:", SwingConstants.TRAILING);

        /* Allocate text fields */
        JTextField myCapTaxRate = new JTextField();
        JTextField myHiCapTaxRate = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myCapTaxRate.setMaximumSize(myDims);
        myHiCapTaxRate.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CapitalTaxRate), DataType.RATE, myCapTaxLabel, myCapTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HiCapitalTaxRate), DataType.RATE, myHiCapTaxLabel, myHiCapTaxRate);

        /* Create the rates panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Capital Rates"));

        /* Layout the rates panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myCapTaxLabel);
        myPanel.add(myCapTaxRate);
        myPanel.add(myHiCapTaxLabel);
        myPanel.add(myHiCapTaxRate);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theSelect.setEnabled(bEnabled);
        theError.setEnabled(bEnabled);
        theRegime.setEnabled(bEnabled);
        theAllows.setEnabled(bEnabled);
        theLimits.setEnabled(bEnabled);
        theBands.setEnabled(bEnabled);
        theStdRates.setEnabled(bEnabled);
        theXtraRates.setEnabled(bEnabled);
        theCapRates.setEnabled(bEnabled);
        theSaveButs.setEnabled(bEnabled);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataEntry.setFocus();
    }

    /**
     * Does this panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return ((theTaxYear != null) && (theTaxYear.hasChanges()));
    }

    /**
     * Does the panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return ((theTaxYear != null) && (theTaxYear.hasErrors()));
    }

    /**
     * Obtain the EditState.
     * @return the EditState
     */
    public EditState getEditState() {
        return (theTaxYear == null)
                ? EditState.CLEAN
                : theTaxYear.getEditState();
    }

    /**
     * Notify changes.
     */
    public void notifyChanges() {
        /* Lock down the table buttons and the selection */
        theSaveButs.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Show the Tax Year */
        showTaxYear();

        /* Alert listeners that there has been a change */
        fireStateChanged();
    }

    /**
     * RefreshData.
     */
    public void refreshData() {
        /* Access the data */
        FinanceData myData = theView.getData();

        /* Access years and regimes */
        theTaxYears = myData.getTaxYears();
        TaxRegimeList myRegimes = myData.getTaxRegimes();

        /* Note that we are refreshing data */
        theFieldSet.setRefreshingData(true);

        /* Refresh the year selection */
        theSelect.refreshData();

        /* If we have regimes already populated */
        if (theRegimesBox.getItemCount() > 0) {
            /* Remove the types */
            theRegimesBox.removeAllItems();
        }

        /* Create a Tax Year iterator */
        Iterator<TaxRegime> myRegIterator = myRegimes.iterator();

        /* Add the Tax Regimes to the regimes box */
        while (myRegIterator.hasNext()) {
            TaxRegime myRegime = myRegIterator.next();

            /* Skip regime if not enabled */
            if (!myRegime.getEnabled()) {
                continue;
            }

            /* Add the item to the list */
            theRegimesBox.addItem(myRegime);
        }

        /* Note that we have finished refreshing data */
        theFieldSet.setRefreshingData(false);

        /* Show the account */
        setSelection(theSelect.getTaxYear());
    }

    /**
     * Set Selection.
     * @param pTaxYear the tax year
     */
    public void setSelection(final TaxYear pTaxYear) {
        /* Reset controls */
        theTaxView = null;
        theTaxYear = null;
        TaxInfoList myInfo = null;

        /* If we have a selected tax year */
        if (pTaxYear != null) {
            /* If we need to show deleted items */
            if ((!doShowDeleted)
                && (pTaxYear.isDeleted())) {
                /* Set the flag correctly */
                doShowDeleted = true;
            }

            /* Create the view of the tax year */
            theTaxView = theTaxYears.deriveEditList(pTaxYear);
            myInfo = theTaxView.getTaxInfo();

            /* Access the tax year */
            theTaxYear = theTaxView.findTaxYearForDate(pTaxYear.getTaxYear());
        }

        /* Store list */
        theYearsEntry.setDataList(theTaxView);
        theInfoEntry.setDataList(myInfo);

        /* notify changes */
        notifyChanges();
    }

    /**
     * Show the tax year.
     */
    private void showTaxYear() {
        /* If we have an active year */
        if (theTaxYear != null) {
            /* Access the tax regime */
            TaxRegime myRegime = theTaxYear.getTaxRegime();
            boolean bActive = !theTaxYear.isDeleted();
            boolean hasAdditionalTaxBand = myRegime.hasAdditionalTaxBand();
            boolean hasCapitalGainsAsIncome = myRegime.hasCapitalGainsAsIncome();

            /* Set the Year */
            theYear.setText(Integer.toString(theTaxYear.getTaxYear().getYear()));
            theYear.setEnabled(bActive);

            /* Set field visibility */
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalIncomeThreshold), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalAllowanceLimit), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalTaxRate), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AdditionalDividendTaxRate), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CapitalTaxRate), !hasCapitalGainsAsIncome);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HiCapitalTaxRate), !hasCapitalGainsAsIncome);

            /* Render the FieldSet */
            theFieldSet.renderSet(theTaxYear);

            /* Make sure delete buttons are visible */
            boolean isEndOfList = ((theTaxYears.peekPrevious(theTaxYear) == null) || (theTaxYears.peekNext(theTaxYear) == null));
            theDelButton.setVisible(!bActive
                                    || ((!theTaxYear.isActive()) && (isEndOfList)));
            theDelButton.setText(!bActive
                    ? "Recover"
                    : "Delete");

            /* else no account */
        } else {
            /* Set blank text */
            theYear.setText("");

            /* Disable data entry */
            theYear.setEnabled(false);

            /* Hide the delete button */
            theDelButton.setVisible(false);

            /* Render the Null Field Set */
            theFieldSet.renderNullSet();
        }
    }

    /**
     * TaxYearListener class.
     */
    private final class TaxYearListener
            implements ActionListener, ChangeListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If the event relates to the Field Set */
            if ((theFieldSet.equals(o))
                && (e instanceof ActionDetailEvent)) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) e;
                Object dtl = evt.getDetails();
                if (dtl instanceof FieldUpdate) {
                    /* Update the field */
                    updateField((FieldUpdate) dtl);
                }

                /* If this event relates to the save buttons */
            } else if (theSaveButs.equals(o)) {
                /* Perform the action */
                theUpdateSet.processCommand(e.getActionCommand(), theError);

                /* Notify of any changes */
                notifyChanges();

                /* If this event relates to the delete button */
            } else if (theDelButton.equals(o)) {
                /* Flip the deletion status */
                theTaxYear.setDeleted(!theTaxYear.isDeleted());

                /* Increment the update version */
                theUpdateSet.incrementVersion();

                /* Notify of any changes */
                notifyChanges();

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Refresh the Tax Year */
                showTaxYear();
            }
        }

        /**
         * Update field.
         * @param pUpdate the update
         */
        private void updateField(final FieldUpdate pUpdate) {
            JDataField myField = pUpdate.getField();

            /* Push history */
            theTaxYear.pushHistory();

            /* Protect against exceptions */
            try {
                if (myField.equals(TaxYear.FIELD_REGIME)) {
                    /* Update the Value */
                    theTaxYear.setTaxRegime(pUpdate.getValue(TaxRegime.class));
                    theTaxYear.adjustForTaxRegime();
                } else {
                    /* Switch on the field */
                    switch (TaxInfoSet.getClassForField(myField)) {
                        case Allowance:
                            theTaxYear.setAllowance(pUpdate.getMoney());
                            break;
                        case LoAgeAllowance:
                            theTaxYear.setLoAgeAllow(pUpdate.getMoney());
                            break;
                        case HiAgeAllowance:
                            theTaxYear.setHiAgeAllow(pUpdate.getMoney());
                            break;
                        case CapitalAllowance:
                            theTaxYear.setCapitalAllow(pUpdate.getMoney());
                            break;
                        case RentalAllowance:
                            theTaxYear.setRentalAllowance(pUpdate.getMoney());
                            break;
                        case AgeAllowanceLimit:
                            theTaxYear.setAgeAllowLimit(pUpdate.getMoney());
                            break;
                        case AdditionalAllowanceLimit:
                            theTaxYear.setAddAllowLimit(pUpdate.getMoney());
                            break;
                        case LoTaxBand:
                            theTaxYear.setLoBand(pUpdate.getMoney());
                            break;
                        case BasicTaxBand:
                            theTaxYear.setBasicBand(pUpdate.getMoney());
                            break;
                        case AdditionalIncomeThreshold:
                            theTaxYear.setAddIncBound(pUpdate.getMoney());
                            break;
                        case LoTaxRate:
                            theTaxYear.setLoTaxRate(pUpdate.getRate());
                            break;
                        case BasicTaxRate:
                            theTaxYear.setBasicTaxRate(pUpdate.getRate());
                            break;
                        case HiTaxRate:
                            theTaxYear.setHiTaxRate(pUpdate.getRate());
                            break;
                        case AdditionalTaxRate:
                            theTaxYear.setAddTaxRate(pUpdate.getRate());
                            break;
                        case InterestTaxRate:
                            theTaxYear.setIntTaxRate(pUpdate.getRate());
                            break;
                        case DividendTaxRate:
                            theTaxYear.setDivTaxRate(pUpdate.getRate());
                            break;
                        case HiDividendTaxRate:
                            theTaxYear.setHiDivTaxRate(pUpdate.getRate());
                            break;
                        case AdditionalDividendTaxRate:
                            theTaxYear.setAddDivTaxRate(pUpdate.getRate());
                            break;
                        case CapitalTaxRate:
                            theTaxYear.setCapTaxRate(pUpdate.getRate());
                            break;
                        case HiCapitalTaxRate:
                            theTaxYear.setHiCapTaxRate(pUpdate.getRate());
                            break;
                        default:
                            break;
                    }
                }

                /* Handle Exceptions */
            } catch (JDataException e) {
                /* Reset values */
                theTaxYear.popHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

                /* Show the error */
                theError.addError(myError);
                return;
            }

            /* Check for changes */
            if (theTaxYear.checkForHistory()) {
                /* Increment the update version */
                theUpdateSet.incrementVersion();

                /* Note that changes have occurred */
                notifyChanges();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            Object o = evt.getSource();

            /* If this is the selection box */
            if (theSelect.equals(o)) {
                /* Set the new account */
                setSelection(theSelect.getTaxYear());

                /* If this is the error panel reporting */
            } else if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel */
                theSelect.setVisible(!isError);

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

                /* Lock Save Buttons */
                theSaveButs.setEnabled(!isError);
            }
        }
    }
}
