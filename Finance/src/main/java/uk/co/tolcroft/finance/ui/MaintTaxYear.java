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
package uk.co.tolcroft.finance.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Rate;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxRegime;
import uk.co.tolcroft.finance.data.TaxRegime.TaxRegimeList;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.finance.ui.controls.TaxYearSelect;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.ItemField;
import uk.co.tolcroft.models.ui.ItemField.FieldSet;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.ui.StdInterfaces.StdPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;
import uk.co.tolcroft.models.ui.ValueField;
import uk.co.tolcroft.models.ui.ValueField.ValueClass;
import uk.co.tolcroft.models.views.UpdateSet;
import uk.co.tolcroft.models.views.UpdateSet.UpdateEntry;

/**
 * TaxYear maintenance panel.
 * @author Tony Washer
 */
public class MaintTaxYear implements StdPanel {
    /**
     * Container gap 1.
     */
    private static final int GAP_TEN = 10;

    /**
     * Container gap 2.
     */
    private static final int GAP_THIRTY = 30;

    /**
     * Container gap 3.
     */
    private static final int GAP_FIFTY = 50;

    /**
     * Container gap 4.
     */
    private static final int GAP_EIGHTY = 80;

    /**
     * The parent.
     */
    private final MaintenanceTab theParent;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The Buttons panel.
     */
    private final JPanel theButtons;

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
     * The Xtra Rates panel.
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
    private final FieldSet theFieldSet;

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
     * The undo button.
     */
    private final JButton theUndoButton;

    /**
     * The tax year.
     */
    private TaxYear theTaxYear = null;

    /**
     * The tax year view.
     */
    private TaxYearList theTaxView = null;

    /**
     * The tax year list.
     */
    private TaxYearList theTaxYears = null;

    /**
     * The data entry.
     */
    private final JDataEntry theDataEntry;

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
    private final View theView;

    /**
     * The Update Set.
     */
    private final UpdateSet theUpdateSet;

    /**
     * The Update Entry.
     */
    private final UpdateEntry theUpdateEntry;

    /**
     * Obtain the panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Obtain the tax Year.
     * @return the tax year
     */
    public TaxYear getTaxYear() {
        return theTaxYear;
    }

    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    @Override
    public JDataManager getDataManager() {
        return theParent.getDataManager();
    }

    /**
     * Constructor.
     * @param pParent the parent
     */
    public MaintTaxYear(final MaintenanceTab pParent) {
        JLabel myYear;
        JLabel myRegime;
        JLabel myAllow;
        JLabel myLoAgeAllow;
        JLabel myHiAgeAllow;
        JLabel myCapitalAllow;
        JLabel myRental;
        JLabel myAgeAllowLimit;
        JLabel myAddAllowLimit;
        JLabel myAddIncBndry;
        JLabel myLoBand;
        JLabel myBasicBand;
        JLabel myLoTaxRate;
        JLabel myBasicTaxRate;
        JLabel myHiTaxRate;
        JLabel myIntTaxRate;
        JLabel myDivTaxRate;
        JLabel myHiDivTaxRate;
        JLabel myAddTaxRate;
        JLabel myAddDivTaxRate;
        JLabel myCapTaxRate;
        JLabel myHiCapTaxRate;

        /* Store passed data */
        theParent = pParent;

        /* Access the view */
        theView = pParent.getView();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet(theView);
        theUpdateEntry = theUpdateSet.registerClass(TaxYear.class);

        /* Create the labels */
        myYear = new JLabel("Year:");
        myRegime = new JLabel("Tax Regime:");
        myAllow = new JLabel("Personal Allowance:");
        myLoAgeAllow = new JLabel("Age 65-74 Allowance:");
        myHiAgeAllow = new JLabel("Age 75+ Allowance:");
        myCapitalAllow = new JLabel("Capital Allowance:");
        myAgeAllowLimit = new JLabel("Age Allowance Limit:");
        myAddAllowLimit = new JLabel("Additnl Allow Limit:");
        myAddIncBndry = new JLabel("Additnl Tax Boundary:");
        myRental = new JLabel("Rental Allowance:");
        myLoBand = new JLabel("Low Tax Band:");
        myBasicBand = new JLabel("Basic Tax Band:");
        myLoTaxRate = new JLabel("Low Rate:");
        myBasicTaxRate = new JLabel("Basic Rate:");
        myHiTaxRate = new JLabel("High Rate:");
        myIntTaxRate = new JLabel("Interest Rate:");
        myDivTaxRate = new JLabel("Dividend Rate:");
        myHiDivTaxRate = new JLabel("High Dividend Rate:");
        myAddTaxRate = new JLabel("Additnl Rate:");
        myAddDivTaxRate = new JLabel("Additnl Dividend Rate:");
        myCapTaxRate = new JLabel("Capital Rate:");
        myHiCapTaxRate = new JLabel("High Capital Rate:");

        /* Build the field set */
        theFieldSet = new FieldSet();

        /* Create the combo box and add to the field set */
        theRegimesBox = new JComboBox();
        theFieldSet.addItemField(new ItemField(theRegimesBox, TaxYear.FIELD_REGIME));

        /* Create the TaxYearSelect panel */
        theSelect = new TaxYearSelect(theView);

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

        /* The Year field is not edit-able */
        theYear.setEditable(false);

        /* Create the buttons */
        theDelButton = new JButton();
        theUndoButton = new JButton("Undo");

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
        theUndoButton.addActionListener(myListener);

        /* Create the Table buttons panel */
        theSaveButs = new SaveButtons(this);

        /* Create the debug entry, attach to MaintenanceDebug entry and hide it */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("TaxYear");
        theDataEntry.addAsChildOf(pParent.getDataEntry());

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the buttons panel */
        theButtons = new JPanel();
        theButtons.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(theButtons);
        theButtons.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theUndoButton)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theDelButton).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theUndoButton).addComponent(theDelButton));

        /* Create the regime panel */
        theRegime = new JPanel();
        theRegime.setBorder(javax.swing.BorderFactory.createTitledBorder("Tax Regime"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theRegime);
        theRegime.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup().addContainerGap().addComponent(myYear)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theYear)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addComponent(myRegime)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addComponent(theRegimesBox).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(myYear).addComponent(theYear).addComponent(myRegime)
                .addComponent(theRegimesBox));

        /* Create the allowances panel */
        theAllows = new JPanel();
        theAllows.setBorder(javax.swing.BorderFactory.createTitledBorder("Allowances"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theAllows);
        theAllows.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(myAllow).addComponent(myLoAgeAllow)
                                                    .addComponent(myHiAgeAllow).addComponent(myRental)
                                                    .addComponent(myCapitalAllow))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theAllowance).addComponent(theLoAgeAllow)
                                                    .addComponent(theHiAgeAllow).addComponent(theRental)
                                                    .addComponent(theCapitalAllow)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myAllow).addComponent(theAllowance))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myLoAgeAllow).addComponent(theLoAgeAllow))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myHiAgeAllow).addComponent(theHiAgeAllow))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myRental).addComponent(theRental))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myCapitalAllow)
                                                    .addComponent(theCapitalAllow)).addContainerGap()));

        /* Create the limits panel */
        theLimits = new JPanel();
        theLimits.setBorder(javax.swing.BorderFactory.createTitledBorder("Limits"));

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
                                                    .addComponent(theAddAllowLimit)).addContainerGap()));
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
                                  .addContainerGap(GAP_EIGHTY, GAP_EIGHTY)));

        /* Create the bands panel */
        theBands = new JPanel();
        theBands.setBorder(javax.swing.BorderFactory.createTitledBorder("Tax Bands"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theBands);
        theBands.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(myLoBand).addComponent(myBasicBand)
                                                    .addComponent(myAddIncBndry))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theLoTaxBand).addComponent(theBasicTaxBand)
                                                    .addComponent(theAddIncomeBndry)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myLoBand).addComponent(theLoTaxBand))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myBasicBand).addComponent(theBasicTaxBand))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myAddIncBndry)
                                                    .addComponent(theAddIncomeBndry))
                                  .addContainerGap(GAP_FIFTY, GAP_FIFTY)));

        /* Create the standard rates panel */
        theStdRates = new JPanel();
        theStdRates.setBorder(javax.swing.BorderFactory.createTitledBorder("Standard Rates"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theStdRates);
        theStdRates.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(myLoTaxRate).addComponent(myBasicTaxRate)
                                                    .addComponent(myHiTaxRate).addComponent(myAddTaxRate))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theLoTaxRate).addComponent(theBasicTaxRate)
                                                    .addComponent(theHiTaxRate).addComponent(theAddTaxRate))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myLoTaxRate).addComponent(theLoTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myBasicTaxRate)
                                                    .addComponent(theBasicTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myHiTaxRate).addComponent(theHiTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myAddTaxRate).addComponent(theAddTaxRate))
                                  .addContainerGap()));

        /* Create the extra rates panel */
        theXtraRates = new JPanel();
        theXtraRates.setBorder(javax.swing.BorderFactory.createTitledBorder("Interest/Dividend Rates"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theXtraRates);
        theXtraRates.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(myIntTaxRate).addComponent(myDivTaxRate)
                                                    .addComponent(myHiDivTaxRate)
                                                    .addComponent(myAddDivTaxRate))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theIntTaxRate).addComponent(theDivTaxRate)
                                                    .addComponent(theHiDivTaxRate)
                                                    .addComponent(theAddDivTaxRate)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myIntTaxRate).addComponent(theIntTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myDivTaxRate).addComponent(theDivTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myHiDivTaxRate)
                                                    .addComponent(theHiDivTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myAddDivTaxRate)
                                                    .addComponent(theAddDivTaxRate)).addContainerGap()));

        /* Create the capital rates panel */
        theCapRates = new JPanel();
        theCapRates.setBorder(javax.swing.BorderFactory.createTitledBorder("Capital Rates"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theCapRates);
        theCapRates.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                    .addComponent(myCapTaxRate).addComponent(myHiCapTaxRate))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theCapTaxRate)
                                                    .addComponent(theHiCapTaxRate)).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myCapTaxRate).addComponent(theCapTaxRate))
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(myHiCapTaxRate)
                                                    .addComponent(theHiCapTaxRate))
                                  .addContainerGap(GAP_FIFTY, GAP_FIFTY)));

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theSaveButs)
                                                    .addComponent(theButtons)
                                                    .addComponent(theError)
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
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theError)
                                  .addComponent(theSelect)
                                  .addContainerGap(GAP_TEN, GAP_THIRTY)
                                  .addComponent(theRegime)
                                  .addContainerGap(GAP_TEN, GAP_THIRTY)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theAllows).addComponent(theLimits)
                                                    .addComponent(theBands))
                                  .addContainerGap(GAP_TEN, GAP_THIRTY)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theStdRates).addComponent(theXtraRates)
                                                    .addComponent(theCapRates)).addContainerGap()
                                  .addComponent(theButtons).addContainerGap().addComponent(theSaveButs)));

        /* Set initial display */
        showTaxYear();
    }

    @Override
    public boolean hasUpdates() {
        return ((theTaxYear != null) && (theTaxYear.hasChanges()));
    }

    /**
     * Does the item have error?
     * @return true/false
     */
    public boolean hasErrors() {
        return ((theTaxYear != null) && (theTaxYear.hasErrors()));
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public EditState getEditState() {
        if (theTaxYear == null) {
            return EditState.CLEAN;
        }
        return theTaxYear.getEditState();
    }

    @Override
    public void printIt() {
    }

    @Override
    public void performCommand(final stdCommand pCmd) {
        /* Switch on command */
        switch (pCmd) {
            case OK:
                saveData();
                break;
            case RESETALL:
                resetData();
                break;
            default:
                break;
        }
        notifyChanges();
    }

    @Override
    public void notifyChanges() {
        /* Lock down the table buttons and the selection */
        theSaveButs.setLockDown();
        theSelect.setEnabled(!hasUpdates());

        /* Show the Tax Year */
        showTaxYear();

        /* Adjust visible tabs */
        theParent.setVisibility();
    }

    @Override
    public void notifySelection(final Object obj) {
        /* If this is a change from the year selection */
        if (theSelect.equals(obj)) {
            /* Set the new account */
            setSelection(theSelect.getTaxYear());
        }
    }

    /**
     * Update Debug view.
     */
    public void updateDebug() {
        theDataEntry.setObject(theTaxView);
    }

    /**
     * resetData.
     */
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

    /**
     * validate.
     */
    public void validate() {
        theTaxYear.clearErrors();
        theTaxYear.validate();
        updateDebug();
    }

    /**
     * saveData.
     */
    public void saveData() {
        /* Validate the data */
        validate();
        if (!theUpdateSet.hasErrors()) {
            /* Save details for the tax year */
            theUpdateSet.applyChanges();
        }
    }

    /**
     * Lock on error.
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(final boolean isError) {
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

        /* Lock row/tab buttons area */
        theButtons.setEnabled(!isError);
        theSaveButs.setEnabled(!isError);
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
            theTaxView = theTaxYears.getEditList(pTaxYear);

            /* Access the tax year */
            theTaxYear = theTaxView.findTaxYearForDate(pTaxYear.getTaxYear());
        }

        /* Store list */
        theUpdateEntry.setDataList(theTaxView);

        /* notify changes */
        notifyChanges();
        updateDebug();
    }

    /**
     * Show the tax year.
     */
    private void showTaxYear() {
        TaxRegime myRegime;

        /* If we have an active year */
        if (theTaxYear != null) {
            /* Access the tax regime */
            myRegime = theTaxYear.getTaxRegime();

            /* Set the Year */
            theYear.setText(Integer.toString(theTaxYear.getTaxYear().getYear()));
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

            /* Make sure buttons are visible */
            theDelButton
                    .setVisible(theTaxYear.isDeleted()
                            || ((!theTaxYear.isActive()) && ((theTaxYears.peekPrevious(theTaxYear) == null) || (theTaxYears
                                    .peekNext(theTaxYear) == null))));
            theDelButton.setText(theTaxYear.isDeleted() ? "Recover" : "Delete");

            /* Enable buttons */
            theUndoButton.setEnabled(theTaxYear.hasChanges());

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

            /* Handle buttons */
            theUndoButton.setEnabled(false);
            theDelButton.setVisible(false);
        }
    }

    /**
     * Undo changes.
     */
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
     * TaxYearListener class.
     */
    private final class TaxYearListener implements ActionListener, ItemListener, PropertyChangeListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            /* Ignore selection if refreshing data/not selected */
            if ((refreshingData) || (evt.getStateChange() == ItemEvent.SELECTED)) {
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

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the undo button */
            if (theUndoButton.equals(o)) {
                /* Undo the changes */
                undoChanges();
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
                    Money myValue = (Money) theAllowance.getValue();
                    theTaxYear.setAllowance(myValue);

                    /* If this is our LoAge Allowance */
                } else if (theLoAgeAllow.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theLoAgeAllow.getValue();
                    theTaxYear.setLoAgeAllow(myValue);

                    /* If this is our HiAge Allowance */
                } else if (theHiAgeAllow.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theHiAgeAllow.getValue();
                    theTaxYear.setHiAgeAllow(myValue);

                    /* If this is our Rental */
                } else if (theRental.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theRental.getValue();
                    theTaxYear.setRentalAllowance(myValue);

                    /* If this is our Capital Allowance */
                } else if (theCapitalAllow.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theCapitalAllow.getValue();
                    theTaxYear.setCapitalAllow(myValue);

                    /* If this is our Age Allowance Limit */
                } else if (theAgeAllowLimit.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theAgeAllowLimit.getValue();
                    theTaxYear.setAgeAllowLimit(myValue);

                    /* If this is our Additional Allowance Limit */
                } else if (theAddAllowLimit.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theAddAllowLimit.getValue();
                    theTaxYear.setAddAllowLimit(myValue);

                    /* If this is our Additional Income Boundary */
                } else if (theAddIncomeBndry.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theAddIncomeBndry.getValue();
                    theTaxYear.setAddIncBound(myValue);

                    /* If this is our LoTaxBand */
                } else if (theLoTaxBand.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theLoTaxBand.getValue();
                    theTaxYear.setLoBand(myValue);

                    /* If this is our Basic Tax Band */
                } else if (theBasicTaxBand.equals(o)) {
                    /* Update the Tax Year */
                    Money myValue = (Money) theBasicTaxBand.getValue();
                    theTaxYear.setBasicBand(myValue);

                    /* If this is our Low Tax Rate */
                } else if (theLoTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theLoTaxRate.getValue();
                    theTaxYear.setLoTaxRate(myValue);

                    /* If this is our Basic Tax Rate */
                } else if (theBasicTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theBasicTaxRate.getValue();
                    theTaxYear.setBasicTaxRate(myValue);

                    /* If this is our High Tax Rate */
                } else if (theHiTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theHiTaxRate.getValue();
                    theTaxYear.setHiTaxRate(myValue);

                    /* If this is our Additional Tax Rate */
                } else if (theAddTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theAddTaxRate.getValue();
                    theTaxYear.setAddTaxRate(myValue);

                    /* If this is our Interest Tax Rate */
                } else if (theIntTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theIntTaxRate.getValue();
                    theTaxYear.setIntTaxRate(myValue);

                    /* If this is our Dividend Tax Rate */
                } else if (theDivTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theDivTaxRate.getValue();
                    theTaxYear.setDivTaxRate(myValue);

                    /* If this is our High Dividend Tax Rate */
                } else if (theHiDivTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theHiDivTaxRate.getValue();
                    theTaxYear.setHiDivTaxRate(myValue);

                    /* If this is our Additional Tax Rate */
                } else if (theAddDivTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theAddDivTaxRate.getValue();
                    theTaxYear.setAddDivTaxRate(myValue);

                    /* If this is our Capital Tax Rate */
                } else if (theCapTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theCapTaxRate.getValue();
                    theTaxYear.setCapTaxRate(myValue);

                    /* If this is our High Capital Tax Rate */
                } else if (theHiCapTaxRate.equals(o)) {
                    /* Update the Tax Year */
                    Rate myValue = (Rate) theHiCapTaxRate.getValue();
                    theTaxYear.setHiCapTaxRate(myValue);
                }

                /* Handle Exceptions */
            } catch (Exception e) {
                /* Reset values */
                theTaxYear.popHistory();
                theTaxYear.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

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
