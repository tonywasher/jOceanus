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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a TaxYear.
 */
public class TaxYearPanel
        extends MoneyWiseDataItemPanel<TaxYear> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -3376054362500655788L;

    /**
     * Field Height.
     */
    private static final int FIELD_HEIGHT = 20;

    /**
     * Field Width.
     */
    private static final int FIELD_WIDTH = 200;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<TaxYear> theFieldSet;

    /**
     * The tax year field.
     */
    private final JTextField theYear;

    /**
     * The regimes button.
     */
    private final JScrollButton<TaxRegime> theRegimeButton;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public TaxYearPanel(final JFieldManager pFieldMgr,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet,
                        final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theYear = new JTextField();

        /* Create the buttons */
        theRegimeButton = new JScrollButton<TaxRegime>();

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the allowances panel */
        JPanel myPanel = buildAllowPanel();
        myTabs.add("Allowances", myPanel);

        /* Build the detail panel */
        myPanel = buildAllowLimitPanel();
        myTabs.add("Limits", myPanel);

        /* Build the bands panel */
        myPanel = buildTaxBandsPanel();
        myTabs.add("TaxBands", myPanel);

        /* Build the rates panel */
        myPanel = buildRatesPanel();
        myTabs.add("TaxRates", myPanel);

        /* Build the extra rates panel */
        myPanel = buildXtraRatesPanel();
        myTabs.add("ExtraRates", myPanel);

        /* Build the capital rates panel */
        myPanel = buildCapRatesPanel();
        myTabs.add("CapitalRates", myPanel);

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new TaxYearListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Create the label */
        JLabel myLabel = new JLabel("Year");

        /* Allocate Dimension */
        Dimension myDims = new Dimension(FIELD_WIDTH, FIELD_HEIGHT);

        /* restrict the field */
        theYear.setMaximumSize(myDims);
        theYear.setEditable(false);
        theYear.setBorder(BorderFactory.createEmptyBorder());
        theRegimeButton.setPreferredSize(myDims);

        /* Adjust FieldSet */
        theFieldSet.addFieldElement(TaxYear.FIELD_REGIME, TaxRegime.class, theRegimeButton);

        /* Create the allow panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the allow panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myLabel);
        myPanel.add(theYear);
        theFieldSet.addFieldToPanel(TaxYear.FIELD_REGIME, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Create a panel to contain this panel */
        JEnablePanel myXtraPanel = new JEnablePanel();
        myXtraPanel.setLayout(new BoxLayout(myXtraPanel, BoxLayout.Y_AXIS));
        myXtraPanel.add(myPanel);
        myXtraPanel.add(Box.createVerticalGlue());

        /* Return the new panel */
        return myXtraPanel;
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
    public void refreshData() {
        /* If we have an item */
        TaxYear myItem = getItem();
        if (myItem != null) {
            TaxYearList myYears = findDataList(MoneyWiseDataType.TAXYEAR, TaxYearList.class);
            setItem(myYears.findItemById(myItem.getId()));
        }

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        TaxYear myYear = getItem();

        /* Access the tax regime */
        TaxRegime myRegime = myYear.getTaxRegime();
        boolean hasAdditionalTaxBand = myRegime.hasAdditionalTaxBand();
        boolean hasCapitalGainsAsIncome = myRegime.hasCapitalGainsAsIncome();

        /* Set the Year */
        theYear.setText(Integer.toString(myYear.getTaxYear().getYear()));

        /* Set field visibility */
        theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD), hasAdditionalTaxBand);
        theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT), hasAdditionalTaxBand);
        theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALTAXRATE), hasAdditionalTaxBand);
        theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE), hasAdditionalTaxBand);
        theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.CAPITALTAXRATE), !hasCapitalGainsAsIncome);
        theFieldSet.setVisibility(TaxInfoSet.getFieldForClass(TaxYearInfoClass.HICAPITALTAXRATE), !hasCapitalGainsAsIncome);
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        TaxYear myYear = getItem();

        if (myField.equals(TaxYear.FIELD_REGIME)) {
            /* Update the Value */
            myYear.setTaxRegime(pUpdate.getValue(TaxRegime.class));
            myYear.adjustForTaxRegime();
        } else {
            /* Switch on the field */
            switch (TaxInfoSet.getClassForField(myField)) {
                case ALLOWANCE:
                    myYear.setAllowance(pUpdate.getMoney());
                    break;
                case LOAGEALLOWANCE:
                    myYear.setLoAgeAllow(pUpdate.getMoney());
                    break;
                case HIAGEALLOWANCE:
                    myYear.setHiAgeAllow(pUpdate.getMoney());
                    break;
                case CAPITALALLOWANCE:
                    myYear.setCapitalAllow(pUpdate.getMoney());
                    break;
                case RENTALALLOWANCE:
                    myYear.setRentalAllowance(pUpdate.getMoney());
                    break;
                case AGEALLOWANCELIMIT:
                    myYear.setAgeAllowLimit(pUpdate.getMoney());
                    break;
                case ADDITIONALALLOWANCELIMIT:
                    myYear.setAddAllowLimit(pUpdate.getMoney());
                    break;
                case LOTAXBAND:
                    myYear.setLoBand(pUpdate.getMoney());
                    break;
                case BASICTAXBAND:
                    myYear.setBasicBand(pUpdate.getMoney());
                    break;
                case ADDITIONALINCOMETHRESHOLD:
                    myYear.setAddIncBound(pUpdate.getMoney());
                    break;
                case LOTAXRATE:
                    myYear.setLoTaxRate(pUpdate.getRate());
                    break;
                case BASICTAXRATE:
                    myYear.setBasicTaxRate(pUpdate.getRate());
                    break;
                case HITAXRATE:
                    myYear.setHiTaxRate(pUpdate.getRate());
                    break;
                case ADDITIONALTAXRATE:
                    myYear.setAddTaxRate(pUpdate.getRate());
                    break;
                case INTERESTTAXRATE:
                    myYear.setIntTaxRate(pUpdate.getRate());
                    break;
                case DIVIDENDTAXRATE:
                    myYear.setDivTaxRate(pUpdate.getRate());
                    break;
                case HIDIVIDENDTAXRATE:
                    myYear.setHiDivTaxRate(pUpdate.getRate());
                    break;
                case ADDITIONALDIVIDENDTAXRATE:
                    myYear.setAddDivTaxRate(pUpdate.getRate());
                    break;
                case CAPITALTAXRATE:
                    myYear.setCapTaxRate(pUpdate.getRate());
                    break;
                case HICAPITALTAXRATE:
                    myYear.setHiCapTaxRate(pUpdate.getRate());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void buildGoToMenu() {
        TaxYear myItem = getItem();
        TaxRegime myRegime = myItem.getTaxRegime();
        buildGoToEvent(myRegime);
    }

    /**
     * TaxYear Listener.
     */
    private final class TaxYearListener
            implements ChangeListener {
        /**
         * The Regime Menu Builder.
         */
        private final JScrollMenuBuilder<TaxRegime> theMenuBuilder;

        /**
         * Constructor.
         */
        private TaxYearListener() {
            /* Access the MenuBuilders */
            theMenuBuilder = theRegimeButton.getMenuBuilder();
            theMenuBuilder.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theMenuBuilder.equals(o)) {
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
            TaxYear myYear = getItem();
            TaxRegime myCurr = myYear.getTaxRegime();
            JMenuItem myActive = null;

            /* Access PayeeTypes */
            MoneyWiseData myData = myYear.getDataSet();
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
