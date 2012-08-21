/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.ui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataManager.SpringUtilities;
import net.sourceforge.JDataModels.ui.ErrorPanel;
import net.sourceforge.JDataModels.ui.SaveButtons;
import net.sourceforge.JDataModels.views.DataControl;
import net.sourceforge.JDataModels.views.UpdateEntry;
import net.sourceforge.JDataModels.views.UpdateSet;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JRate;
import net.sourceforge.JEventManager.JEventPanel;
import net.sourceforge.JFieldSet.EditState;
import net.sourceforge.JFieldSet.ItemField;
import net.sourceforge.JFieldSet.ItemField.FieldSet;
import net.sourceforge.JFieldSet.RenderManager;
import net.sourceforge.JFieldSet.ValueField;
import net.sourceforge.JFieldSet.ValueField.ValueClass;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.TaxYear;
import net.sourceforge.JFinanceApp.data.TaxYear.TaxYearList;
import net.sourceforge.JFinanceApp.data.statics.TaxRegime;
import net.sourceforge.JFinanceApp.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.JFinanceApp.ui.controls.TaxYearSelect;
import net.sourceforge.JFinanceApp.views.View;

/**
 * TaxYear maintenance panel.
 * @author Tony Washer
 */
public class MaintTaxYear extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 4527130528913817296L;

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
     * Resource Bundle.
     */
    // private static final ResourceBundle NLS_BUNDLE =
    // ResourceBundle.getBundle(MaintTaxYear.class.getName());

    /**
     * Text for PopUpEnabled.
     */
    // private static final String NLS_ENABLED = NLS_BUNDLE.getString("PopUpEnabled");

    /**
     * The tax year select panel.
     */
    private final TaxYearSelect theSelect;

    /**
     * The Regime panel.
     */
    private final JPanel theRegime;

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
    private final JComboBox theRegimesBox;

    /**
     * The tax year field.
     */
    private final JTextField theYear;

    /**
     * The Field Set.
     */
    private final transient FieldSet theFieldSet;

    /**
     * The Allowance field.
     */
    private final ItemField theAllowance;

    /**
     * The LoAge Allowance field.
     */
    private final ItemField theLoAgeAllow;

    /**
     * The HiAge Allowance field.
     */
    private final ItemField theHiAgeAllow;

    /**
     * The Capital Allowance field.
     */
    private final ItemField theCapitalAllow;

    /**
     * The Age Allowance Limit field.
     */
    private final ItemField theAgeAllowLimit;

    /**
     * The Add Allowance Limit field.
     */
    private final ItemField theAddAllowLimit;

    /**
     * The Add Income Boundary field.
     */
    private final ItemField theAddIncomeBndry;

    /**
     * The Rental Allowance field.
     */
    private final ItemField theRental;

    /**
     * The Low Tax band field.
     */
    private final ItemField theLoTaxBand;

    /**
     * The Basic Tax band field.
     */
    private final ItemField theBasicTaxBand;

    /**
     * The Low Tax rate field.
     */
    private final ItemField theLoTaxRate;

    /**
     * The Basic Tax rate field.
     */
    private final ItemField theBasicTaxRate;

    /**
     * The High Tax rate field.
     */
    private final ItemField theHiTaxRate;

    /**
     * The Interest Tax rate field.
     */
    private final ItemField theIntTaxRate;

    /**
     * The Dividend Tax rate field.
     */
    private final ItemField theDivTaxRate;

    /**
     * The HiDiv Tax rate field.
     */
    private final ItemField theHiDivTaxRate;

    /**
     * The Add Tax rate field.
     */
    private final ItemField theAddTaxRate;

    /**
     * The AddDiv Tax rate field.
     */
    private final ItemField theAddDivTaxRate;

    /**
     * The Cap Tax rate field.
     */
    private final ItemField theCapTaxRate;

    /**
     * The HiCap Tax rate field.
     */
    private final ItemField theHiCapTaxRate;

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
     * Are we refreshing data?
     */
    private boolean refreshingData = false;

    /**
     * Do we show deleted years.
     */
    private boolean doShowDeleted = false;

    /**
     * The view.
     */
    private final transient View theView;

    /**
     * The render manager.
     */
    private final transient RenderManager theRenderMgr;

    /**
     * The Update Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The Update Entry.
     */
    private final transient UpdateEntry<TaxYear> theUpdateEntry;

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
        theRenderMgr = theView.getRenderMgr();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet(theView);
        theUpdateEntry = theUpdateSet.registerClass(TaxYear.class);

        /* Create the labels */
        JLabel myYear = new JLabel("Year:");
        JLabel myRegime = new JLabel("Tax Regime:");
        JLabel myAllow = new JLabel("Personal Allowance:", SwingConstants.TRAILING);
        JLabel myLoAgeAllow = new JLabel("Age 65-74 Allowance:", SwingConstants.TRAILING);
        JLabel myHiAgeAllow = new JLabel("Age 75+ Allowance:", SwingConstants.TRAILING);
        JLabel myCapitalAllow = new JLabel("Capital Allowance:", SwingConstants.TRAILING);
        JLabel myAgeAllowLimit = new JLabel("Age Allowance Limit:", SwingConstants.TRAILING);
        JLabel myAddAllowLimit = new JLabel("Additnl Allow Limit:", SwingConstants.TRAILING);
        JLabel myAddIncBndry = new JLabel("Additnl Tax Boundary:", SwingConstants.TRAILING);
        JLabel myRental = new JLabel("Rental Allowance:", SwingConstants.TRAILING);
        JLabel myLoBand = new JLabel("Low Tax Band:", SwingConstants.TRAILING);
        JLabel myBasicBand = new JLabel("Basic Tax Band:", SwingConstants.TRAILING);
        JLabel myLoTaxRate = new JLabel("Low Rate:", SwingConstants.TRAILING);
        JLabel myBasicTaxRate = new JLabel("Basic Rate:", SwingConstants.TRAILING);
        JLabel myHiTaxRate = new JLabel("High Rate:", SwingConstants.TRAILING);
        JLabel myIntTaxRate = new JLabel("Interest Rate:", SwingConstants.TRAILING);
        JLabel myDivTaxRate = new JLabel("Dividend Rate:", SwingConstants.TRAILING);
        JLabel myHiDivTaxRate = new JLabel("High Dividend Rate:", SwingConstants.TRAILING);
        JLabel myAddTaxRate = new JLabel("Additnl Rate:", SwingConstants.TRAILING);
        JLabel myAddDivTaxRate = new JLabel("Additnl Dividend Rate:", SwingConstants.TRAILING);
        JLabel myCapTaxRate = new JLabel("Capital Rate:", SwingConstants.TRAILING);
        JLabel myHiCapTaxRate = new JLabel("High Capital Rate:", SwingConstants.TRAILING);

        /* Build the field set */
        theFieldSet = new FieldSet(theRenderMgr);

        /* Create the combo box and add to the field set */
        theRegimesBox = new JComboBox();
        theFieldSet.addItemField(theRegimesBox, TaxYear.FIELD_REGIME);

        /* Create the text fields */
        theYear = new JTextField();
        theAllowance = new ItemField(ValueClass.Money, TaxYear.FIELD_ALLOW, theFieldSet);
        theLoAgeAllow = new ItemField(ValueClass.Money, TaxYear.FIELD_LOAGAL, theFieldSet);
        theHiAgeAllow = new ItemField(ValueClass.Money, TaxYear.FIELD_HIAGAL, theFieldSet);
        theCapitalAllow = new ItemField(ValueClass.Money, TaxYear.FIELD_CAPALW, theFieldSet);
        theAgeAllowLimit = new ItemField(ValueClass.Money, TaxYear.FIELD_AGELMT, theFieldSet);
        theAddAllowLimit = new ItemField(ValueClass.Money, TaxYear.FIELD_ADDLMT, theFieldSet);
        theAddIncomeBndry = new ItemField(ValueClass.Money, TaxYear.FIELD_ADDBDY, theFieldSet);
        theLoTaxBand = new ItemField(ValueClass.Money, TaxYear.FIELD_LOBAND, theFieldSet);
        theBasicTaxBand = new ItemField(ValueClass.Money, TaxYear.FIELD_BSBAND, theFieldSet);
        theRental = new ItemField(ValueClass.Money, TaxYear.FIELD_RENTAL, theFieldSet);
        theLoTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_LOTAX, theFieldSet);
        theBasicTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_BASTAX, theFieldSet);
        theHiTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_HITAX, theFieldSet);
        theIntTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_INTTAX, theFieldSet);
        theDivTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_DIVTAX, theFieldSet);
        theHiDivTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_HDVTAX, theFieldSet);
        theAddTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_ADDTAX, theFieldSet);
        theAddDivTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_ADVTAX, theFieldSet);
        theCapTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_CAPTAX, theFieldSet);
        theHiCapTaxRate = new ItemField(ValueClass.Rate, TaxYear.FIELD_HCPTAX, theFieldSet);

        /* Limit sizes */
        theYear.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theRegimesBox.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theAllowance.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theLoAgeAllow.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theHiAgeAllow.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theRental.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theCapitalAllow.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theAddIncomeBndry.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theAgeAllowLimit.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theAddAllowLimit.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theLoTaxBand.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theBasicTaxBand.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theLoTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theBasicTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theHiTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theAddTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theIntTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theDivTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theHiDivTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theAddDivTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theCapTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));
        theHiCapTaxRate.setMaximumSize(new Dimension(FIELD_WIDTH, FIELD_HEIGHT));

        /* The Year field is not edit-able */
        theYear.setEditable(false);

        /* Create the buttons */
        theDelButton = new JButton();

        /* Create listener */
        TaxYearListener myListener = new TaxYearListener();

        /* Add listeners */
        theRegimesBox.addItemListener(myListener);
        theAllowance.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theLoAgeAllow.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theHiAgeAllow.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theCapitalAllow.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theAgeAllowLimit.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theAddAllowLimit.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theAddIncomeBndry.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theRental.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theLoTaxBand.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theBasicTaxBand.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theLoTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theBasicTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theHiTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theIntTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theDivTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theHiDivTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theAddTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theAddDivTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theCapTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theHiCapTaxRate.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theDelButton.addActionListener(myListener);

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
        theRegime = new JPanel();
        theRegime.setBorder(BorderFactory.createTitledBorder("Tax Year"));

        /* Create the layout for the panel */
        theRegime.setLayout(new BoxLayout(theRegime, BoxLayout.X_AXIS));
        theRegime.add(Box.createHorizontalGlue());
        theRegime.add(myYear);
        theRegime.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));
        theRegime.add(theYear);
        theRegime.add(Box.createHorizontalGlue());
        theRegime.add(myRegime);
        theRegime.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));
        theRegime.add(theRegimesBox);
        theRegime.add(Box.createHorizontalGlue());
        theRegime.add(theDelButton);
        theRegime.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));

        /* Create the allowances panel */
        theAllows = new JPanel();
        theAllows.setBorder(BorderFactory.createTitledBorder("Allowances"));

        /* Layout the allowances panel */
        SpringLayout mySpring = new SpringLayout();
        theAllows.setLayout(mySpring);
        theAllows.add(myAllow);
        theAllows.add(theAllowance);
        theAllows.add(myLoAgeAllow);
        theAllows.add(theLoAgeAllow);
        theAllows.add(myHiAgeAllow);
        theAllows.add(theHiAgeAllow);
        theAllows.add(myRental);
        theAllows.add(theRental);
        theAllows.add(myCapitalAllow);
        theAllows.add(theCapitalAllow);
        SpringUtilities.makeCompactGrid(theAllows, mySpring, theAllows.getComponentCount() >> 1, 2,
                                        PADDING_SIZE);

        /* Create the limits panel */
        theLimits = new JPanel();
        theLimits.setBorder(BorderFactory.createTitledBorder("Limits"));

        /* Layout the limits panel */
        mySpring = new SpringLayout();
        theLimits.setLayout(mySpring);
        theLimits.add(myAgeAllowLimit);
        theLimits.add(theAgeAllowLimit);
        theLimits.add(myAddAllowLimit);
        theLimits.add(theAddAllowLimit);
        SpringUtilities.makeCompactGrid(theLimits, mySpring, theLimits.getComponentCount() >> 1, 2,
                                        PADDING_SIZE);

        /* Create the bands panel */
        theBands = new JPanel();
        theBands.setBorder(BorderFactory.createTitledBorder("Tax Bands"));

        /* Layout the bands panel */
        mySpring = new SpringLayout();
        theBands.setLayout(mySpring);
        theBands.add(myLoBand);
        theBands.add(theLoTaxBand);
        theBands.add(myBasicBand);
        theBands.add(theBasicTaxBand);
        theBands.add(myAddIncBndry);
        theBands.add(theAddIncomeBndry);
        SpringUtilities.makeCompactGrid(theBands, mySpring, theBands.getComponentCount() >> 1, 2,
                                        PADDING_SIZE);

        /* Create the standard rates panel */
        theStdRates = new JPanel();
        theStdRates.setBorder(BorderFactory.createTitledBorder("Standard Rates"));

        /* Layout the stdRates panel */
        mySpring = new SpringLayout();
        theStdRates.setLayout(mySpring);
        theStdRates.add(myLoTaxRate);
        theStdRates.add(theLoTaxRate);
        theStdRates.add(myBasicTaxRate);
        theStdRates.add(theBasicTaxRate);
        theStdRates.add(myHiTaxRate);
        theStdRates.add(theHiTaxRate);
        theStdRates.add(myAddTaxRate);
        theStdRates.add(theAddTaxRate);
        SpringUtilities.makeCompactGrid(theStdRates, mySpring, theStdRates.getComponentCount() >> 1, 2,
                                        PADDING_SIZE);

        /* Create the extra rates panel */
        theXtraRates = new JPanel();
        theXtraRates.setBorder(BorderFactory.createTitledBorder("Interest/Dividend Rates"));

        /* Layout the xtraRates panel */
        mySpring = new SpringLayout();
        theXtraRates.setLayout(mySpring);
        theXtraRates.add(myIntTaxRate);
        theXtraRates.add(theIntTaxRate);
        theXtraRates.add(myDivTaxRate);
        theXtraRates.add(theDivTaxRate);
        theXtraRates.add(myHiDivTaxRate);
        theXtraRates.add(theHiDivTaxRate);
        theXtraRates.add(myAddDivTaxRate);
        theXtraRates.add(theAddDivTaxRate);
        SpringUtilities.makeCompactGrid(theXtraRates, mySpring, theXtraRates.getComponentCount() >> 1, 2,
                                        PADDING_SIZE);

        /* Create the capital rates panel */
        theCapRates = new JPanel();
        theCapRates.setBorder(BorderFactory.createTitledBorder("Capital Rates"));

        /* Layout the capRates panel */
        mySpring = new SpringLayout();
        theCapRates.setLayout(mySpring);
        theCapRates.add(myCapTaxRate);
        theCapRates.add(theCapTaxRate);
        theCapRates.add(myHiCapTaxRate);
        theCapRates.add(theHiCapTaxRate);
        SpringUtilities.makeCompactGrid(theCapRates, mySpring, theCapRates.getComponentCount() >> 1, 2,
                                        PADDING_SIZE);

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
        return (theTaxYear == null) ? EditState.CLEAN : theTaxYear.getEditState();
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
     * Update Debug view.
     */
    public void updateDebug() {
        theDataEntry.setObject(theTaxView);
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
        refreshingData = true;

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
        refreshingData = false;

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

        /* If we have a selected tax year */
        if (pTaxYear != null) {
            /* If we need to show deleted items */
            if ((!doShowDeleted) && (pTaxYear.isDeleted())) {
                /* Set the flag correctly */
                doShowDeleted = true;
            }

            /* Create the view of the tax year */
            theTaxView = theTaxYears.deriveEditList(pTaxYear);

            /* Access the tax year */
            theTaxYear = theTaxView.findTaxYearForDate(pTaxYear.getTaxYear());
        }

        /* Store list */
        theUpdateEntry.setDataList(theTaxView);

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

            /* Set the Year */
            theYear.setText(Integer.toString(theTaxYear.getTaxYear().getYear()));
            theYear.setEnabled(!theTaxYear.isDeleted());

            /* Set the Regime */
            theRegimesBox.setSelectedItem(myRegime);
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
            theAddIncomeBndry.setEnabled(!theTaxYear.isDeleted() && theTaxYear.hasAdditionalTaxBand());

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

            /* Make sure delete buttons are visible */
            boolean isEndOfList = ((theTaxYears.peekPrevious(theTaxYear) == null) || (theTaxYears
                    .peekNext(theTaxYear) == null));
            theDelButton.setVisible(theTaxYear.isDeleted() || ((!theTaxYear.isActive()) && (isEndOfList)));
            theDelButton.setText(theTaxYear.isDeleted() ? "Recover" : "Delete");

            /* else no account */
        } else {
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

            /* Hide the delete button */
            theDelButton.setVisible(false);
        }
    }

    /**
     * TaxYearListener class.
     */
    private final class TaxYearListener implements ActionListener, ItemListener, ChangeListener,
            PropertyChangeListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            /* Ignore selection if refreshing data/not selected */
            if ((refreshingData) || (evt.getStateChange() == ItemEvent.DESELECTED)) {
                return;
            }

            /* If this event relates to the regimes box */
            if (theRegimesBox.equals(evt.getSource())) {
                /* Push history */
                theTaxYear.pushHistory();

                /* Select the new regime */
                TaxRegime myRegime = (TaxRegime) evt.getItem();
                theTaxYear.setTaxRegime(myRegime);

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
                    /* Increment the update version */
                    theUpdateSet.incrementVersion();

                    /* Note that changes have occurred */
                    notifyChanges();
                }
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the save buttons */
            if (theSaveButs.equals(o)) {
                /* Perform the action */
                theUpdateSet.processCommand(evt.getActionCommand(), theError);

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

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            Object o = evt.getSource();

            /* Push history */
            theTaxYear.pushHistory();

            /* Protect against exceptions */
            try {
                /* If this is our the Allowance */
                if (theAllowance.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theAllowance.getValue();
                    theTaxYear.setAllowance(myValue);

                    /* If this is our LoAge Allowance */
                } else if (theLoAgeAllow.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theLoAgeAllow.getValue();
                    theTaxYear.setLoAgeAllow(myValue);

                    /* If this is our HiAge Allowance */
                } else if (theHiAgeAllow.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theHiAgeAllow.getValue();
                    theTaxYear.setHiAgeAllow(myValue);

                    /* If this is our Rental */
                } else if (theRental.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theRental.getValue();
                    theTaxYear.setRentalAllowance(myValue);

                    /* If this is our Capital Allowance */
                } else if (theCapitalAllow.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theCapitalAllow.getValue();
                    theTaxYear.setCapitalAllow(myValue);

                    /* If this is our Age Allowance Limit */
                } else if (theAgeAllowLimit.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theAgeAllowLimit.getValue();
                    theTaxYear.setAgeAllowLimit(myValue);

                    /* If this is our Additional Allowance Limit */
                } else if (theAddAllowLimit.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theAddAllowLimit.getValue();
                    theTaxYear.setAddAllowLimit(myValue);

                    /* If this is our Additional Income Boundary */
                } else if (theAddIncomeBndry.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theAddIncomeBndry.getValue();
                    theTaxYear.setAddIncBound(myValue);

                    /* If this is our LoTaxBand */
                } else if (theLoTaxBand.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theLoTaxBand.getValue();
                    theTaxYear.setLoBand(myValue);

                    /* If this is our Basic Tax Band */
                } else if (theBasicTaxBand.equals(o)) {
                    /* Update the Tax Year */
                    JMoney myValue = (JMoney) theBasicTaxBand.getValue();
                    theTaxYear.setBasicBand(myValue);

                    /* If this is our Low Tax Rate */
                } else if (theLoTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theLoTaxRate.getValue();
                    theTaxYear.setLoTaxRate(myValue);

                    /* If this is our Basic Tax Rate */
                } else if (theBasicTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theBasicTaxRate.getValue();
                    theTaxYear.setBasicTaxRate(myValue);

                    /* If this is our High Tax Rate */
                } else if (theHiTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theHiTaxRate.getValue();
                    theTaxYear.setHiTaxRate(myValue);

                    /* If this is our Additional Tax Rate */
                } else if (theAddTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theAddTaxRate.getValue();
                    theTaxYear.setAddTaxRate(myValue);

                    /* If this is our Interest Tax Rate */
                } else if (theIntTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theIntTaxRate.getValue();
                    theTaxYear.setIntTaxRate(myValue);

                    /* If this is our Dividend Tax Rate */
                } else if (theDivTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theDivTaxRate.getValue();
                    theTaxYear.setDivTaxRate(myValue);

                    /* If this is our High Dividend Tax Rate */
                } else if (theHiDivTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theHiDivTaxRate.getValue();
                    theTaxYear.setHiDivTaxRate(myValue);

                    /* If this is our Additional Tax Rate */
                } else if (theAddDivTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theAddDivTaxRate.getValue();
                    theTaxYear.setAddDivTaxRate(myValue);

                    /* If this is our Capital Tax Rate */
                } else if (theCapTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theCapTaxRate.getValue();
                    theTaxYear.setCapTaxRate(myValue);

                    /* If this is our High Capital Tax Rate */
                } else if (theHiCapTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    JRate myValue = (JRate) theHiCapTaxRate.getValue();
                    theTaxYear.setHiCapTaxRate(myValue);
                }

                /* Handle Exceptions */
            } catch (ClassCastException e) {
                /* Reset values */
                theTaxYear.popHistory();
                theTaxYear.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

                /* Show the error */
                theError.setError(myError);
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
