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

import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Tax configuration.
 */
public class MoneyWiseTaxConfig {
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
