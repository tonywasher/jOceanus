/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.tax.uk;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet.MoneyWiseTaxBand;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxConfig;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Tax configuration.
 */
public class MoneyWiseUKTaxConfig
        implements MetisDataContents, MoneyWiseTaxConfig {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseUKTaxConfig.class.getSimpleName());

    /**
     * TaxYear Field Id.
     */
    private static final MetisField FIELD_TAXYEAR = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_NAME.getValue());

    /**
     * TaxBasis Field Id.
     */
    private static final MetisField FIELD_TAXSOURCE = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getListName());

    /**
     * GrossTaxable Field Id.
     */
    private static final MetisField FIELD_GROSS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_GROSS.getValue());

    /**
     * GrossPreSavings Field Id.
     */
    private static final MetisField FIELD_PRESAVINGS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_PRESAVINGS.getValue());

    /**
     * Birthday Field Id.
     */
    private static final MetisField FIELD_BIRTHDAY = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_BIRTHDAY.getValue());

    /**
     * ClientAge Field Id.
     */
    private static final MetisField FIELD_AGE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_AGE.getValue());

    /**
     * AgeRelatedAllowances Field Id.
     */
    private static final MetisField FIELD_AGERELATED = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_AGEALLOWANCE.getValue());

    /**
     * Allowance Field Id.
     */
    private static final MetisField FIELD_ALLOWANCE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_BASIC.getValue());

    /**
     * Rental Allowance Field Id.
     */
    private static final MetisField FIELD_RENTAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_RENTAL.getValue());

    /**
     * Savings Allowance Field Id.
     */
    private static final MetisField FIELD_SAVINGS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_SAVINGS.getValue());

    /**
     * Dividend Allowance Field Id.
     */
    private static final MetisField FIELD_DIVIDEND = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_DIVIDEND.getValue());

    /**
     * Capital Allowance Field Id.
     */
    private static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.ALLOWANCE_CAPITAL.getValue());

    /**
     * TaxBands Field Id.
     */
    private static final MetisField FIELD_TAXBANDS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_BANDS.getValue());

    /**
     * Low Savings Band Field Id.
     */
    private static final MetisField FIELD_LOSAVINGS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_LOSAVINGS.getValue());

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
        MoneyWiseUKBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Calculate the allowances */
        theAllowance = new TethysMoney(myAllowances.calculateBasicAllowance(this));
        theRentalAllowance = new TethysMoney(myAllowances.getRentalAllowance());
        theSavingsAllowance = new TethysMoney(myAllowances.calculateSavingsAllowance(this));
        theDividendAllowance = new TethysMoney(myAllowances.calculateDividendAllowance());
        theCapitalAllowance = new TethysMoney(myAllowances.getCapitalAllowance());

        /* Access the taxBands */
        MoneyWiseUKTaxBands myBands = theTaxYear.getTaxBands();
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
        theAllowance = new TethysMoney(pSource.getAllowance());
        theRentalAllowance = new TethysMoney(pSource.getRentalAllowance());
        theSavingsAllowance = new TethysMoney(pSource.getSavingsAllowance());
        theDividendAllowance = new TethysMoney(pSource.getDividendAllowance());
        theCapitalAllowance = new TethysMoney(pSource.getCapitalAllowance());

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
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_TAXYEAR.equals(pField)) {
            return theTaxYear;
        }
        if (FIELD_TAXSOURCE.equals(pField)) {
            return theTaxSource;
        }
        if (FIELD_GROSS.equals(pField)) {
            return theGrossTaxable;
        }
        if (FIELD_PRESAVINGS.equals(pField)) {
            return theGrossPreSavings;
        }
        if (FIELD_BIRTHDAY.equals(pField)) {
            return theBirthday;
        }
        if (FIELD_AGE.equals(pField)) {
            return theClientAge;
        }
        if (FIELD_AGERELATED.equals(pField)) {
            return hasAgeRelatedAllowance
                                          ? Boolean.TRUE
                                          : MetisFieldValue.SKIP;
        }
        if (FIELD_ALLOWANCE.equals(pField)) {
            return theAllowance.isNonZero()
                                            ? theAllowance
                                            : MetisFieldValue.SKIP;
        }
        if (FIELD_RENTAL.equals(pField)) {
            return theRentalAllowance.isNonZero()
                                                  ? theRentalAllowance
                                                  : MetisFieldValue.SKIP;
        }
        if (FIELD_SAVINGS.equals(pField)) {
            return theSavingsAllowance.isNonZero()
                                                   ? theSavingsAllowance
                                                   : MetisFieldValue.SKIP;
        }
        if (FIELD_DIVIDEND.equals(pField)) {
            return theDividendAllowance.isNonZero()
                                                    ? theDividendAllowance
                                                    : MetisFieldValue.SKIP;
        }
        if (FIELD_CAPITAL.equals(pField)) {
            return theCapitalAllowance.isNonZero()
                                                   ? theCapitalAllowance
                                                   : MetisFieldValue.SKIP;
        }
        if (FIELD_TAXBANDS.equals(pField)) {
            return theTaxBands;
        }
        if (FIELD_LOSAVINGS.equals(pField)) {
            return theLoSavings.getAmount().isNonZero()
                                                        ? theLoSavings
                                                        : MetisFieldValue.SKIP;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
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
    private TethysMoney determineGrossTaxableIncome() {
        /* Initialise income to preSavings */
        TethysMoney myIncome = new TethysMoney(theGrossPreSavings);

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
        MoneyWiseUKBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Initialise income to correct currency */
        TethysMoney myIncome = new TethysMoney(myAllowances.getAllowance());
        myIncome.setZero();

        /* Add the salary to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.SALARY));

        /* Add the other income to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.OTHERINCOME));

        /* Add the rental to income */
        myIncome.addAmount(theTaxSource.getAmountForTaxBasis(TaxBasisClass.RENTALINCOME));

        /* Access the room rental income */
        TethysMoney myChargeable = theTaxSource.getAmountForTaxBasis(TaxBasisClass.ROOMRENTAL);

        /* If we have a chargeable element */
        TethysMoney myAllowance = myAllowances.getRentalAllowance();
        if (myChargeable.compareTo(myAllowance) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(myAllowance);
            myIncome.addAmount(myChargeable);
        }

        /* Return the gross preSavings amount */
        return myIncome;
    }
}
