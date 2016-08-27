/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.tax.uk;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Tax configuration.
 */
public class MoneyWiseTaxConfig
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxConfig.class.getSimpleName());

    /**
     * TaxYear Field Id.
     */
    private static final MetisField FIELD_TAXYEAR = FIELD_DEFS.declareEqualityField("TaxYear");

    /**
     * TaxBasis Field Id.
     */
    private static final MetisField FIELD_TAXBASIS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getListName());

    /**
     * GrossTaxable Field Id.
     */
    private static final MetisField FIELD_GROSS = FIELD_DEFS.declareEqualityField("GrossTaxable");

    /**
     * Birthday Field Id.
     */
    private static final MetisField FIELD_BIRTHDAY = FIELD_DEFS.declareEqualityField("Birthday");

    /**
     * ClientAge Field Id.
     */
    private static final MetisField FIELD_AGE = FIELD_DEFS.declareEqualityField("Age");

    /**
     * AgeRelatedAllowances Field Id.
     */
    private static final MetisField FIELD_AGERELATED = FIELD_DEFS.declareEqualityField("AgeRelatedAllowances");

    /**
     * Allowance Field Id.
     */
    private static final MetisField FIELD_ALLOWANCE = FIELD_DEFS.declareEqualityField("Allowance");

    /**
     * Rental Allowance Field Id.
     */
    private static final MetisField FIELD_RENTAL = FIELD_DEFS.declareEqualityField("RentalAllowance");

    /**
     * Savings Allowance Field Id.
     */
    private static final MetisField FIELD_SAVINGS = FIELD_DEFS.declareEqualityField("SavingsAllowance");

    /**
     * Dividend Allowance Field Id.
     */
    private static final MetisField FIELD_DIVIDEND = FIELD_DEFS.declareEqualityField("DividendsAllowance");

    /**
     * Capital Allowance Field Id.
     */
    private static final MetisField FIELD_CAPITAL = FIELD_DEFS.declareEqualityField("CapitalAllowance");

    /**
     * TaxBands Field Id.
     */
    private static final MetisField FIELD_TAXBANDS = FIELD_DEFS.declareEqualityField("TaxBands");

    /**
     * TaxYear.
     */
    private final MoneyWiseTaxYear theTaxYear;

    /**
     * The taxBasis list.
     */
    private final TaxBasisBucketList theTaxBases;

    /**
     * Gross Taxable Income.
     */
    private final TethysMoney theGrossTaxable;

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
    private final MoneyWiseTaxBands theTaxBands;

    /**
     * Constructor.
     * @param pTaxYear the taxYear
     * @param pTaxBasis the tax basis list
     * @param pBirthday the client birthday
     */
    protected MoneyWiseTaxConfig(final MoneyWiseTaxYear pTaxYear,
                                 final TaxBasisBucketList pTaxBasis,
                                 final TethysDate pBirthday) {
        /* Store details */
        theTaxYear = pTaxYear;
        theTaxBases = pTaxBasis;
        theBirthday = pBirthday;
        theClientAge = theBirthday.ageOn(pTaxYear.getYear());

        /* calculate the gross taxable income */
        theGrossTaxable = determineGrossTaxableIncome();

        /* Access the basic allowances */
        MoneyWiseBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Calculate the allowances */
        theAllowance = myAllowances.calculateBasicAllowance(this);
        theRentalAllowance = myAllowances.getRentalAllowance();
        theSavingsAllowance = myAllowances.calculateSavingsAllowance(this);
        theDividendAllowance = myAllowances.calculateDividendAllowance();
        theCapitalAllowance = myAllowances.getCapitalAllowance();

        /* Access the taxBands */
        theTaxBands = theTaxYear.getStandardBands();
    }

    /**
     * Constructor.
     * @param pSource the source configuration
     */
    protected MoneyWiseTaxConfig(final MoneyWiseTaxConfig pSource) {
        /* Copy basic details */
        theTaxYear = pSource.getTaxYear();
        theTaxBases = pSource.getTaxBases();
        theGrossTaxable = pSource.getGrossTaxable();
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
        theTaxBands = new MoneyWiseTaxBands(pSource.getTaxBands());
    }

    /**
     * Obtain the tax year.
     * @return the tax year
     */
    public MoneyWiseTaxYear getTaxYear() {
        return theTaxYear;
    }

    /**
     * Obtain the tax bases.
     * @return the bases
     */
    public TaxBasisBucketList getTaxBases() {
        return theTaxBases;
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
    public MoneyWiseTaxBands getTaxBands() {
        return theTaxBands;
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
        if (FIELD_TAXBASIS.equals(pField)) {
            return theTaxBases;
        }
        if (FIELD_GROSS.equals(pField)) {
            return theGrossTaxable;
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

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * Determine the gross taxable income.
     * @return the gross taxable income
     */
    private TethysMoney determineGrossTaxableIncome() {
        /* Access the basic allowances */
        MoneyWiseBasicAllowance myAllowances = theTaxYear.getAllowances();

        /* Initialise income to correct currency */
        TethysMoney myIncome = new TethysMoney(myAllowances.getAllowance());
        myIncome.setZero();

        /* Access the salary bucket and add to income */
        TaxBasisBucket mySrcBucket = theTaxBases.getBucket(TaxBasisClass.SALARY);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the rental bucket */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.RENTALINCOME);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the room rental bucket */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.ROOMRENTAL);
        TethysMoney myChargeable = new TethysMoney(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* If we have a chargeable element */
        TethysMoney myAllowance = myAllowances.getRentalAllowance();
        if (myChargeable.compareTo(myAllowance) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(myAllowance);
            myIncome.addAmount(myChargeable);
        }

        /* Access the taxed interest bucket and add to income */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.TAXEDINTEREST);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the unTaxed interest bucket and add to income */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.UNTAXEDINTEREST);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the dividends bucket and add to income */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.DIVIDEND);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the unit trust dividends bucket and add to income */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.UNITTRUSTDIVIDEND);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the foreign dividends bucket and add to income */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.FOREIGNDIVIDEND);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Access the taxable gains bucket and add to income */
        mySrcBucket = theTaxBases.getBucket(TaxBasisClass.TAXABLEGAINS);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.GROSS));

        /* Capital Gains does not class as taxable income */

        /* Return the gross taxable amount */
        return myIncome;
    }
}
