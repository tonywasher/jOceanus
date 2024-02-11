/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet.MoneyWiseXTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxConfig;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxSource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Tax configuration.
 */
public class MoneyWiseXUKTaxConfig
        implements MetisFieldItem, MoneyWiseXTaxConfig {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKTaxConfig> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKTaxConfig.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_NAME, MoneyWiseXUKTaxConfig::getTaxYear);
        FIELD_DEFS.declareLocalField(MoneyWiseDataType.TAXBASIS, MoneyWiseXUKTaxConfig::getTaxSource);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_GROSS, MoneyWiseXUKTaxConfig::getGrossTaxable);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_PRESAVINGS, MoneyWiseXUKTaxConfig::getGrossPreSavings);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_BIRTHDAY, MoneyWiseXUKTaxConfig::getBirthday);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_AGE, MoneyWiseXUKTaxConfig::getClientAge);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_AGEALLOWANCE, MoneyWiseXUKTaxConfig::hasAgeRelatedAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_BASIC, MoneyWiseXUKTaxConfig::getAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_RENTAL, MoneyWiseXUKTaxConfig::getRentalAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_SAVINGS, MoneyWiseXUKTaxConfig::getSavingsAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_DIVIDEND, MoneyWiseXUKTaxConfig::getDividendAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.ALLOWANCE_CAPITAL, MoneyWiseXUKTaxConfig::getCapitalAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_BANDS, MoneyWiseXUKTaxConfig::getTaxBands);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_LOSAVINGS, MoneyWiseXUKTaxConfig::getLoSavingsBand);
    }

    /**
     * TaxYear.
     */
    private final MoneyWiseXUKTaxYear theTaxYear;

    /**
     * The taxSource.
     */
    private final MoneyWiseXTaxSource theTaxSource;

    /**
     * Gross Taxable Income.
     */
    private final TethysMoney theGrossTaxable;

    /**
     * Gross PreSavings.
     */
    private final TethysMoney theGrossPreSavings;

    /**
     * The client birthday.
     */
    private final TethysDate theBirthday;

    /**
     * The client age in the tax year.
     */
    private final Integer theClientAge;

    /**
     * Do we have an age related allowance.
     */
    private boolean hasAgeRelatedAllowance;

    /**
     * Basic Allowance.
     */
    private final TethysMoney theAllowance;

    /**
     * Rental Allowance.
     */
    private final TethysMoney theRentalAllowance;

    /**
     * Savings Allowance.
     */
    private final TethysMoney theSavingsAllowance;

    /**
     * Dividend Allowance.
     */
    private final TethysMoney theDividendAllowance;

    /**
     * Capital Allowance.
     */
    private final TethysMoney theCapitalAllowance;

    /**
     * Tax Bands.
     */
    private final MoneyWiseXTaxBandSet theTaxBands;

    /**
     * LoSavings Band.
     */
    private final MoneyWiseXTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pTaxYear the taxYear
     * @param pTaxSource the tax source
     * @param pBirthday the client birthday
     */
    protected MoneyWiseXUKTaxConfig(final MoneyWiseXUKTaxYear pTaxYear,
                                    final MoneyWiseXTaxSource pTaxSource,
                                    final TethysDate pBirthday) {
        /* Store details */
        theTaxYear = pTaxYear;
        theTaxSource = pTaxSource;
        theBirthday = pBirthday;
        theClientAge = theBirthday.ageOn(pTaxYear.getYearEnd());

        /* calculate the gross taxable income and preSavings */
        theGrossPreSavings = determineGrossPreSavings();
        theGrossTaxable = determineGrossTaxableIncome();

        /* Access the basic allowances */
        final MoneyWiseXUKBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Calculate the allowances */
        theAllowance = new TethysMoney(myAllowances.calculateBasicAllowance(this));
        theRentalAllowance = new TethysMoney(myAllowances.getRentalAllowance());
        theSavingsAllowance = new TethysMoney(myAllowances.calculateSavingsAllowance(this));
        theDividendAllowance = new TethysMoney(myAllowances.calculateDividendAllowance());
        theCapitalAllowance = new TethysMoney(myAllowances.getCapitalAllowance());

        /* Access the taxBands */
        final MoneyWiseXUKTaxBands myBands = theTaxYear.getTaxBands();
        theTaxBands = new MoneyWiseXTaxBandSet(myBands.getStandardSet());

        /* Calculate the loSavings band */
        theLoSavings = myAllowances.calculateLoSavingsBand(this, myBands.getLoSavings());
    }

    /**
     * Constructor.
     * @param pSource the source configuration
     */
    private MoneyWiseXUKTaxConfig(final MoneyWiseXUKTaxConfig pSource) {
        /* Copy basic details */
        theTaxYear = pSource.getTaxYear();
        theTaxSource = pSource.getTaxSource();
        theGrossTaxable = pSource.getGrossTaxable();
        theGrossPreSavings = pSource.getGrossPreSavings();
        theBirthday = pSource.getBirthday();
        theClientAge = pSource.getClientAge();
        hasAgeRelatedAllowance = pSource.hasAgeRelatedAllowance();

        /* Copy the allowances */
        theAllowance = new TethysMoney(pSource.getAllowance());
        theRentalAllowance = new TethysMoney(pSource.getRentalAllowance());
        theSavingsAllowance = new TethysMoney(pSource.getSavingsAllowance());
        theDividendAllowance = new TethysMoney(pSource.getDividendAllowance());
        theCapitalAllowance = new TethysMoney(pSource.getCapitalAllowance());

        /* Copy the taxBands */
        theTaxBands = new MoneyWiseXTaxBandSet(pSource.getTaxBands());
        theLoSavings = new MoneyWiseXTaxBand(pSource.getLoSavingsBand());
    }

    /**
     * Obtain the tax year.
     * @return the tax year
     */
    public MoneyWiseXUKTaxYear getTaxYear() {
        return theTaxYear;
    }

    /**
     * Obtain the tax bases.
     * @return the bases
     */
    public MoneyWiseXTaxSource getTaxSource() {
        return theTaxSource;
    }

    /**
     * Obtain the gross preSavings income.
     * @return the gross preSavings
     */
    public TethysMoney getGrossPreSavings() {
        return theGrossPreSavings;
    }

    /**
     * Obtain the gross taxable income.
     * @return the gross taxable
     */
    public TethysMoney getGrossTaxable() {
        return theGrossTaxable;
    }

    /**
     * Obtain the client birthday.
     * @return the birthday
     */
    public TethysDate getBirthday() {
        return theBirthday;
    }

    /**
     * Obtain the client age.
     * @return the gross taxable
     */
    public Integer getClientAge() {
        return theClientAge;
    }

    /**
     * Do we have an age related allowance?
     * @return true/false
     */
    public boolean hasAgeRelatedAllowance() {
        return hasAgeRelatedAllowance;
    }

    /**
     * Set whether we have an age related allowance?
     * @param pFlag true/false
     */
    protected void setHasAgeRelatedAllowance(final boolean pFlag) {
        hasAgeRelatedAllowance = pFlag;
    }

    /**
     * Obtain the allowance.
     * @return the allowance
     */
    public TethysMoney getAllowance() {
        return theAllowance;
    }

    /**
     * Obtain the rental allowance.
     * @return the rental allowance
     */
    public TethysMoney getRentalAllowance() {
        return theRentalAllowance;
    }

    /**
     * Obtain the savings allowance.
     * @return the allowance
     */
    public TethysMoney getSavingsAllowance() {
        return theSavingsAllowance;
    }

    /**
     * Obtain the dividend allowance.
     * @return the allowance
     */
    public TethysMoney getDividendAllowance() {
        return theDividendAllowance;
    }

    /**
     * Obtain the capital allowance.
     * @return the allowance
     */
    public TethysMoney getCapitalAllowance() {
        return theCapitalAllowance;
    }

    /**
     * Obtain the tax bands.
     * @return the tax bands
     */
    public MoneyWiseXTaxBandSet getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the low savings band.
     * @return the low savings band
     */
    public MoneyWiseXTaxBand getLoSavingsBand() {
        return theLoSavings;
    }

    @Override
    public MetisFieldSet<MoneyWiseXUKTaxConfig> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public MoneyWiseXUKTaxConfig cloneIt() {
        return new MoneyWiseXUKTaxConfig(this);
    }

    /**
     * Determine the gross taxable income.
     * @return the gross taxable income
     */
    private TethysMoney determineGrossTaxableIncome() {
        /* Initialise income to preSavings */
        final TethysMoney myIncome = new TethysMoney(theGrossPreSavings);

        /* Add taxed interest to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.TAXEDINTEREST));

        /* Add unTaxed interest to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.UNTAXEDINTEREST));

        /* Add share dividends to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.DIVIDEND));

        /* Add unit trust dividends to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.UNITTRUSTDIVIDEND));

        /* Add foreign dividends to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.FOREIGNDIVIDEND));

        /* Add chargeable gains to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.CHARGEABLEGAINS));

        /* Capital Gains does not class as taxable income */

        /* Return the gross taxable amount */
        return myIncome;
    }

    /**
     * Determine the gross preSavings income.
     * @return the gross preSavings income
     */
    private TethysMoney determineGrossPreSavings() {
        /* Access the basic allowances */
        final MoneyWiseXUKBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Initialise income to correct currency */
        final TethysMoney myIncome = new TethysMoney(myAllowances.getAllowance());
        myIncome.setZero();

        /* Add the salary to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.SALARY));

        /* Add the other income to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.OTHERINCOME));

        /* Add the rental to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.RENTALINCOME));

        /* Access the room rental income */
        final TethysMoney myChargeable = theTaxSource.getAmountForTaxBasis(TaxBasisClass.ROOMRENTAL);

        /* If we have a chargeable element */
        final TethysMoney myAllowance = myAllowances.getRentalAllowance();
        if (myChargeable.compareTo(myAllowance) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(myAllowance);
            myIncome.addAmount(myChargeable);
        }

        /* Return the gross preSavings amount */
        return myIncome;
    }
}