/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.tax.uk;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxConfig;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIDataFormatter;

/**
 * Tax configuration.
 */
public class MoneyWiseUKTaxConfig
        implements MetisFieldItem, MoneyWiseTaxConfig {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKTaxConfig> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKTaxConfig.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_NAME, MoneyWiseUKTaxConfig::getTaxYear);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticDataType.TAXBASIS, MoneyWiseUKTaxConfig::getTaxSource);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_GROSS, MoneyWiseUKTaxConfig::getGrossTaxable);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_PRESAVINGS, MoneyWiseUKTaxConfig::getGrossPreSavings);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_BIRTHDAY, MoneyWiseUKTaxConfig::getBirthday);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_AGE, MoneyWiseUKTaxConfig::getClientAge);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_AGEALLOWANCE, MoneyWiseUKTaxConfig::hasAgeRelatedAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_BASIC, MoneyWiseUKTaxConfig::getAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_RENTAL, MoneyWiseUKTaxConfig::getRentalAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_SAVINGS, MoneyWiseUKTaxConfig::getSavingsAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_DIVIDEND, MoneyWiseUKTaxConfig::getDividendAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.ALLOWANCE_CAPITAL, MoneyWiseUKTaxConfig::getCapitalAllowance);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_BANDS, MoneyWiseUKTaxConfig::getTaxBands);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_LOSAVINGS, MoneyWiseUKTaxConfig::getLoSavingsBand);
    }

    /**
     * TaxYear.
     */
    private final MoneyWiseUKTaxYear theTaxYear;

    /**
     * The taxSource.
     */
    private final MoneyWiseTaxSource theTaxSource;

    /**
     * Gross Taxable Income.
     */
    private final OceanusMoney theGrossTaxable;

    /**
     * Gross PreSavings.
     */
    private final OceanusMoney theGrossPreSavings;

    /**
     * The client birthday.
     */
    private final OceanusDate theBirthday;

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
    private final OceanusMoney theAllowance;

    /**
     * Rental Allowance.
     */
    private final OceanusMoney theRentalAllowance;

    /**
     * Savings Allowance.
     */
    private final OceanusMoney theSavingsAllowance;

    /**
     * Dividend Allowance.
     */
    private final OceanusMoney theDividendAllowance;

    /**
     * Capital Allowance.
     */
    private final OceanusMoney theCapitalAllowance;

    /**
     * Tax Bands.
     */
    private final MoneyWiseTaxBandSet theTaxBands;

    /**
     * LoSavings Band.
     */
    private final MoneyWiseTaxBand theLoSavings;

    /**
     * Constructor.
     * @param pTaxYear the taxYear
     * @param pTaxSource the tax source
     * @param pBirthday the client birthday
     */
    protected MoneyWiseUKTaxConfig(final MoneyWiseUKTaxYear pTaxYear,
                                   final MoneyWiseTaxSource pTaxSource,
                                   final OceanusDate pBirthday) {
        /* Store details */
        theTaxYear = pTaxYear;
        theTaxSource = pTaxSource;
        theBirthday = pBirthday;
        theClientAge = theBirthday.ageOn(pTaxYear.getYearEnd());

        /* calculate the gross taxable income and preSavings */
        theGrossPreSavings = determineGrossPreSavings();
        theGrossTaxable = determineGrossTaxableIncome();

        /* Access the basic allowances */
        final MoneyWiseUKBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Calculate the allowances */
        theAllowance = new OceanusMoney(myAllowances.calculateBasicAllowance(this));
        theRentalAllowance = new OceanusMoney(myAllowances.getRentalAllowance());
        theSavingsAllowance = new OceanusMoney(myAllowances.calculateSavingsAllowance(this));
        theDividendAllowance = new OceanusMoney(myAllowances.calculateDividendAllowance());
        theCapitalAllowance = new OceanusMoney(myAllowances.getCapitalAllowance());

        /* Access the taxBands */
        final MoneyWiseUKTaxBands myBands = theTaxYear.getTaxBands();
        theTaxBands = new MoneyWiseTaxBandSet(myBands.getStandardSet());

        /* Calculate the loSavings band */
        theLoSavings = myAllowances.calculateLoSavingsBand(this, myBands.getLoSavings());
    }

    /**
     * Constructor.
     * @param pSource the source configuration
     */
    private MoneyWiseUKTaxConfig(final MoneyWiseUKTaxConfig pSource) {
        /* Copy basic details */
        theTaxYear = pSource.getTaxYear();
        theTaxSource = pSource.getTaxSource();
        theGrossTaxable = pSource.getGrossTaxable();
        theGrossPreSavings = pSource.getGrossPreSavings();
        theBirthday = pSource.getBirthday();
        theClientAge = pSource.getClientAge();
        hasAgeRelatedAllowance = pSource.hasAgeRelatedAllowance();

        /* Copy the allowances */
        theAllowance = new OceanusMoney(pSource.getAllowance());
        theRentalAllowance = new OceanusMoney(pSource.getRentalAllowance());
        theSavingsAllowance = new OceanusMoney(pSource.getSavingsAllowance());
        theDividendAllowance = new OceanusMoney(pSource.getDividendAllowance());
        theCapitalAllowance = new OceanusMoney(pSource.getCapitalAllowance());

        /* Copy the taxBands */
        theTaxBands = new MoneyWiseTaxBandSet(pSource.getTaxBands());
        theLoSavings = new MoneyWiseTaxBand(pSource.getLoSavingsBand());
    }

    /**
     * Obtain the tax year.
     * @return the tax year
     */
    public MoneyWiseUKTaxYear getTaxYear() {
        return theTaxYear;
    }

    /**
     * Obtain the tax bases.
     * @return the bases
     */
    public MoneyWiseTaxSource getTaxSource() {
        return theTaxSource;
    }

    /**
     * Obtain the gross preSavings income.
     * @return the gross preSavings
     */
    public OceanusMoney getGrossPreSavings() {
        return theGrossPreSavings;
    }

    /**
     * Obtain the gross taxable income.
     * @return the gross taxable
     */
    public OceanusMoney getGrossTaxable() {
        return theGrossTaxable;
    }

    /**
     * Obtain the client birthday.
     * @return the birthday
     */
    public OceanusDate getBirthday() {
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
    public OceanusMoney getAllowance() {
        return theAllowance;
    }

    /**
     * Obtain the rental allowance.
     * @return the rental allowance
     */
    public OceanusMoney getRentalAllowance() {
        return theRentalAllowance;
    }

    /**
     * Obtain the savings allowance.
     * @return the allowance
     */
    public OceanusMoney getSavingsAllowance() {
        return theSavingsAllowance;
    }

    /**
     * Obtain the dividend allowance.
     * @return the allowance
     */
    public OceanusMoney getDividendAllowance() {
        return theDividendAllowance;
    }

    /**
     * Obtain the capital allowance.
     * @return the allowance
     */
    public OceanusMoney getCapitalAllowance() {
        return theCapitalAllowance;
    }

    /**
     * Obtain the tax bands.
     * @return the tax bands
     */
    public MoneyWiseTaxBandSet getTaxBands() {
        return theTaxBands;
    }

    /**
     * Obtain the low savings band.
     * @return the low savings band
     */
    public MoneyWiseTaxBand getLoSavingsBand() {
        return theLoSavings;
    }

    @Override
    public MetisFieldSet<MoneyWiseUKTaxConfig> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public MoneyWiseUKTaxConfig cloneIt() {
        return new MoneyWiseUKTaxConfig(this);
    }

    /**
     * Determine the gross taxable income.
     * @return the gross taxable income
     */
    private OceanusMoney determineGrossTaxableIncome() {
        /* Initialise income to preSavings */
        final OceanusMoney myIncome = new OceanusMoney(theGrossPreSavings);

        /* Add taxed interest to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.TAXEDINTEREST));

        /* Add unTaxed interest to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.UNTAXEDINTEREST));

        /* Add share dividends to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.DIVIDEND));

        /* Add unit trust dividends to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.UNITTRUSTDIVIDEND));

        /* Add foreign dividends to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.FOREIGNDIVIDEND));

        /* Add chargeable gains to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.CHARGEABLEGAINS));

        /* Capital Gains does not class as taxable income */

        /* Return the gross taxable amount */
        return myIncome;
    }

    /**
     * Determine the gross preSavings income.
     * @return the gross preSavings income
     */
    private OceanusMoney determineGrossPreSavings() {
        /* Access the basic allowances */
        final MoneyWiseUKBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Initialise income to correct currency */
        final OceanusMoney myIncome = new OceanusMoney(myAllowances.getAllowance());
        myIncome.setZero();

        /* Add the salary to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.SALARY));

        /* Add the other income to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.OTHERINCOME));

        /* Add the rental to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.RENTALINCOME));

        /* Access the room rental income */
        final OceanusMoney myChargeable = theTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.ROOMRENTAL);

        /* If we have a chargeable element */
        final OceanusMoney myAllowance = myAllowances.getRentalAllowance();
        if (myChargeable.compareTo(myAllowance) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(myAllowance);
            myIncome.addAmount(myChargeable);
        }

        /* Return the gross preSavings amount */
        return myIncome;
    }
}
