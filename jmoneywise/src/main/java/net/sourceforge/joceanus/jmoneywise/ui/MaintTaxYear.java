/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
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
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.ui.SaveButtons;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

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
     * The regimes button.
     */
    private final JScrollButton<TaxRegime> theRegimesButton;

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
     * The Update Set.
     */
    private final transient UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The TaxYear Update Entry.
     */
    private final transient UpdateEntry<TaxYear, MoneyWiseDataType> theYearsEntry;

    /**
     * The TaxInfo Update Entry.
     */
    private final transient UpdateEntry<TaxYearInfo, MoneyWiseDataType> theInfoEntry;

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
        JFieldManager myFieldMgr = theView.getFieldMgr();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet<MoneyWiseDataType>(theView);
        theYearsEntry = theUpdateSet.registerClass(TaxYear.class);
        theInfoEntry = theUpdateSet.registerClass(TaxYearInfo.class);

        /* Create the New FieldSet */
        theFieldSet = new JFieldSet<TaxYear>(myFieldMgr);

        /* Create the Year fields and add to field set */
        JLabel myYearLabel = new JLabel("Year:");
        theYear = new JTextField();
        theYear.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theYear.setEditable(false);

        /* Create the combo box and add to the field set */
        theRegimesButton = new JScrollButton<TaxRegime>();
        theFieldSet.addFieldElement(TaxYear.FIELD_REGIME, TaxRegime.class, theRegimesButton);

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

        /* Create a simple regime panel */
        JEnablePanel myPanel = new JEnablePanel();
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxYear.FIELD_REGIME, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, 1, 2, PADDING_SIZE);
        theRegimesButton.setPreferredSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));

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
        theRegime.add(myPanel);
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
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ALLOWANCE), DataType.MONEY, myAllowance);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LOAGEALLOWANCE), DataType.MONEY, myLoAgeAllow);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HIAGEALLOWANCE), DataType.MONEY, myHiAgeAllow);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CAPITALALLOWANCE), DataType.MONEY, myCapitalAllow);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.RENTALALLOWANCE), DataType.MONEY, myRentalAllow);

        /* Create the allow panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Allowances"));

        /* Layout the allow panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ALLOWANCE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LOAGEALLOWANCE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HIAGEALLOWANCE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CAPITALALLOWANCE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.RENTALALLOWANCE), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Allowance Limits subPanel.
     * @return the panel
     */
    private JPanel buildAllowLimitPanel() {
        /* Allocate text fields */
        JTextField myAgeLimit = new JTextField();
        JTextField myAddLimit = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myAgeLimit.setMaximumSize(myDims);
        myAddLimit.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AGEALLOWANCELIMIT), DataType.MONEY, myAgeLimit);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT), DataType.MONEY, myAddLimit);

        /* Create the limits panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Allowance Limits"));

        /* Layout the limits panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.AGEALLOWANCELIMIT), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build TaxBands subPanel.
     * @return the panel
     */
    private JPanel buildTaxBandsPanel() {
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
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LOTAXBAND), DataType.MONEY, myLoBand);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.BASICTAXBAND), DataType.MONEY, myBasicBand);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD), DataType.MONEY, myAddIncBdy);

        /* Create the bands panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Tax Bands"));

        /* Layout the bands panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LOTAXBAND), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.BASICTAXBAND), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Rates subPanel.
     * @return the panel
     */
    private JPanel buildRatesPanel() {
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
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LOTAXRATE), DataType.RATE, myLoTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.BASICTAXRATE), DataType.RATE, myBasicTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HITAXRATE), DataType.RATE, myHiTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALTAXRATE), DataType.RATE, myAddTaxRate);

        /* Create the rates panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Standard Rates"));

        /* Layout the rates panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.LOTAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.BASICTAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HITAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALTAXRATE), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build extra Rates subPanel.
     * @return the panel
     */
    private JPanel buildXtraRatesPanel() {
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
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.INTERESTTAXRATE), DataType.RATE, myIntTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.DIVIDENDTAXRATE), DataType.RATE, myDivTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HIDIVIDENDTAXRATE), DataType.RATE, myHiDivTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE), DataType.RATE, myAddDivTaxRate);

        /* Create the rates panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Interest/Dividend Rates"));

        /* Layout the rates panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.INTERESTTAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.DIVIDENDTAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HIDIVIDENDTAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build capital Rates subPanel.
     * @return the panel
     */
    private JPanel buildCapRatesPanel() {
        /* Allocate text fields */
        JTextField myCapTaxRate = new JTextField();
        JTextField myHiCapTaxRate = new JTextField();

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* Adjust maximum sizes */
        myCapTaxRate.setMaximumSize(myDims);
        myHiCapTaxRate.setMaximumSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CAPITALTAXRATE), DataType.RATE, myCapTaxRate);
        theFieldSet.addFieldElement(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HICAPITALTAXRATE), DataType.RATE, myHiCapTaxRate);

        /* Create the rates panel */
        JEnablePanel myPanel = new JEnablePanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Capital Rates"));

        /* Layout the rates panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CAPITALTAXRATE), myPanel);
        theFieldSet.addFieldToPanel(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HICAPITALTAXRATE), myPanel);
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
        return (theTaxYear != null) && (theTaxYear.hasChanges());
    }

    /**
     * Does the panel have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return (theTaxYear != null) && (theTaxYear.hasErrors());
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
        MoneyWiseData myData = theView.getData();

        /* Access years and regimes */
        theTaxYears = myData.getTaxYears();

        /* Note that we are refreshing data */
        theFieldSet.setRefreshingData(true);

        /* Refresh the year selection */
        theSelect.refreshData();

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
        TaxYearList myTaxView = null;
        theTaxYear = null;
        TaxInfoList myInfo = null;

        /* If we have a selected tax year */
        if (pTaxYear != null) {
            /* If we need to show deleted items */
            if ((!doShowDeleted) && (pTaxYear.isDeleted())) {
                /* Set the flag correctly */
                doShowDeleted = true;
            }

            /* Create the view of the tax year */
            myTaxView = theTaxYears.deriveEditList(pTaxYear);
            myInfo = myTaxView.getTaxInfo();

            /* Access the tax year */
            theTaxYear = myTaxView.findTaxYearForDate(pTaxYear.getTaxYear());
        }

        /* Store list */
        theYearsEntry.setDataList(myTaxView);
        theInfoEntry.setDataList(myInfo);

        /* notify changes */
        notifyChanges();
    }

    /**
     * Select taxYear.
     * @param pYear the taxYear to select
     */
    protected void selectTaxYear(final TaxYear pYear) {
        /* Intentionally null */
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
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALTAXRATE), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE), hasAdditionalTaxBand);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CAPITALTAXRATE), !hasCapitalGainsAsIncome);
            theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HICAPITALTAXRATE), !hasCapitalGainsAsIncome);

            /* Render the FieldSet */
            theFieldSet.renderSet(theTaxYear);

            /* Make sure delete buttons are visible */
            boolean isEndOfList = (theTaxYears.peekPrevious(theTaxYear) == null) || (theTaxYears.peekNext(theTaxYear) == null);
            theDelButton.setVisible(!bActive || ((!theTaxYear.isActive()) && (isEndOfList)));
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
        /**
         * Menu Builder.
         */
        private final JScrollMenuBuilder<TaxRegime> theMenuBuilder;

        /**
         * Constructor
         */
        private TaxYearListener() {
            /* Access builder */
            theMenuBuilder = theRegimesButton.getMenuBuilder();
            theMenuBuilder.addChangeListener(this);
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If the event relates to the Field Set */
            if ((theFieldSet.equals(o)) && (e instanceof ActionDetailEvent)) {
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
                        case ALLOWANCE:
                            theTaxYear.setAllowance(pUpdate.getMoney());
                            break;
                        case LOAGEALLOWANCE:
                            theTaxYear.setLoAgeAllow(pUpdate.getMoney());
                            break;
                        case HIAGEALLOWANCE:
                            theTaxYear.setHiAgeAllow(pUpdate.getMoney());
                            break;
                        case CAPITALALLOWANCE:
                            theTaxYear.setCapitalAllow(pUpdate.getMoney());
                            break;
                        case RENTALALLOWANCE:
                            theTaxYear.setRentalAllowance(pUpdate.getMoney());
                            break;
                        case AGEALLOWANCELIMIT:
                            theTaxYear.setAgeAllowLimit(pUpdate.getMoney());
                            break;
                        case ADDITIONALALLOWANCELIMIT:
                            theTaxYear.setAddAllowLimit(pUpdate.getMoney());
                            break;
                        case LOTAXBAND:
                            theTaxYear.setLoBand(pUpdate.getMoney());
                            break;
                        case BASICTAXBAND:
                            theTaxYear.setBasicBand(pUpdate.getMoney());
                            break;
                        case ADDITIONALINCOMETHRESHOLD:
                            theTaxYear.setAddIncBound(pUpdate.getMoney());
                            break;
                        case LOTAXRATE:
                            theTaxYear.setLoTaxRate(pUpdate.getRate());
                            break;
                        case BASICTAXRATE:
                            theTaxYear.setBasicTaxRate(pUpdate.getRate());
                            break;
                        case HITAXRATE:
                            theTaxYear.setHiTaxRate(pUpdate.getRate());
                            break;
                        case ADDITIONALTAXRATE:
                            theTaxYear.setAddTaxRate(pUpdate.getRate());
                            break;
                        case INTERESTTAXRATE:
                            theTaxYear.setIntTaxRate(pUpdate.getRate());
                            break;
                        case DIVIDENDTAXRATE:
                            theTaxYear.setDivTaxRate(pUpdate.getRate());
                            break;
                        case HIDIVIDENDTAXRATE:
                            theTaxYear.setHiDivTaxRate(pUpdate.getRate());
                            break;
                        case ADDITIONALDIVIDENDTAXRATE:
                            theTaxYear.setAddDivTaxRate(pUpdate.getRate());
                            break;
                        case CAPITALTAXRATE:
                            theTaxYear.setCapTaxRate(pUpdate.getRate());
                            break;
                        case HICAPITALTAXRATE:
                            theTaxYear.setHiCapTaxRate(pUpdate.getRate());
                            break;
                        default:
                            break;
                    }
                }

                /* Handle Exceptions */
            } catch (JOceanusException e) {
                /* Reset values */
                theTaxYear.popHistory();

                /* Build the error */
                JOceanusException myError = new JMoneyWiseDataException("Failed to update field", e);

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
            } else if (theMenuBuilder.equals(o)) {
                buildRegimeMenu();
            }
        }

        /**
         * Build the regimes menu.
         */
        private void buildRegimeMenu() {
            /* Reset the popUp menu */
            theMenuBuilder.clearMenu();

            /* Record active item */
            TaxRegime myCurr = theTaxYear.getTaxRegime();
            JMenuItem myActive = null;

            /* Access years and regimes */
            MoneyWiseData myData = theTaxYears.getDataSet();
            TaxRegimeList myRegimes = myData.getTaxRegimes();

            /* Loop through the panels */
            Iterator<TaxRegime> myIterator = myRegimes.iterator();
            while (myIterator.hasNext()) {
                TaxRegime myRegime = myIterator.next();

                /* Skip regime if not enabled */
                if (!myRegime.getEnabled()) {
                    continue;
                }

                /* Create a new JMenuItem and add it to the popUp */
                JMenuItem myItem = theMenuBuilder.addItem(myRegime);

                /* If this is the active regime */
                if (myRegime.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theMenuBuilder.showItem(myActive);
        }
    }
}
