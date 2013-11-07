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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Calendar;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.analysis.ChargeableEvent.ChargeableEventList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisBucketList;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCalcBucket.TaxAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxCalcBucket.TaxCalcBucketList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jpreferenceset.PreferenceSet;

/**
 * Class to further analyse an analysis, primarily to calculate tax liability.
 * @author Tony Washer
 */
public class MetaAnalysis {
    /**
     * Low Age Limit.
     */
    private static final int LIMIT_AGE_LO = 65;

    /**
     * High Age Limit.
     */
    private static final int LIMIT_AGE_HI = 75;

    /**
     * Allowance Quotient.
     */
    private static final JMoney ALLOWANCE_QUOTIENT = JMoney.getWholeUnits(2);

    /**
     * Allowance Multiplier.
     */
    private static final JMoney ALLOWANCE_MULTIPLIER = JMoney.getWholeUnits(1);

    /**
     * Analysis.
     */
    private final Analysis theAnalysis;

    /**
     * Chargeable events.
     */
    private final ChargeableEventList theCharges;

    /**
     * The date range of the analysis.
     */
    // private final JDateDayRange theDateRange;

    /**
     * The TaxYear of the analysis.
     */
    private final TaxYear theYear;

    /**
     * Do we have an age allowance?
     */
    private boolean hasAgeAllowance = false;

    /**
     * Do we have Gains slices?
     */
    private Boolean hasGainsSlices = false;

    /**
     * Do we have a reduced allowance?
     */
    private Boolean hasReducedAllow = false;

    /**
     * Age of User.
     */
    private Integer theAge = 0;

    /**
     * Taxation Preferences.
     */
    public static class TaxationPreferences
            extends PreferenceSet {
        /**
         * Registry name for BirthDate.
         */
        public static final String NAME_BIRTHDATE = "BirthDate";

        /**
         * Display name for BirthDate.
         */
        private static final String DISPLAY_BIRTHDATE = "Birth Date";

        /**
         * Default value for BirthDate.
         */
        private static final JDateDay DEFAULT_BIRTHDATE = new JDateDay(1970, Calendar.JANUARY, 1);

        /**
         * Constructor.
         * @throws JDataException on error
         */
        public TaxationPreferences() throws JDataException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            defineDatePreference(NAME_BIRTHDATE, DEFAULT_BIRTHDATE);
        }

        @Override
        protected String getDisplayName(final String pName) {
            /* Handle default values */
            if (pName.equals(NAME_BIRTHDATE)) {
                return DISPLAY_BIRTHDATE;
            }
            return null;
        }
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    protected MetaAnalysis(final Analysis pAnalysis) {
        /* Store the analysis */
        theAnalysis = pAnalysis;
        // theDateRange = theAnalysis.getDateRange();
        theCharges = theAnalysis.getCharges();

        /* Determine tax details */
        TaxCalcBucketList myTax = theAnalysis.getTaxCalculations();
        theYear = (myTax != null)
                ? myTax.getTaxYear()
                : null;
    }

    /**
     * Calculate tax.
     * @param pManager the preference manager
     */
    protected void calculateTax(final PreferenceManager pManager) {
        /* Access Tax Categories */
        TaxBasisBucketList myBasis = theAnalysis.getTaxBasis();
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Calculate the gross income */
        calculateGrossIncome();
        JMoney myIncome = new JMoney();
        JMoney myTax = new JMoney();

        /* Calculate the allowances and tax bands */
        TaxBands myBands = calculateAllowances(pManager);

        /* Calculate the salary taxation */
        TaxCalcBucket myBucket = calculateSalaryTax(myBands);
        myIncome.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Amount));
        myTax.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Taxation));

        /* Calculate the rental taxation */
        myBucket = calculateRentalTax(myBands);
        myIncome.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Amount));
        myTax.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Taxation));

        /* Calculate the interest taxation */
        myBucket = calculateInterestTax(myBands);
        myIncome.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Amount));
        myTax.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Taxation));

        /* Calculate the dividends taxation */
        myBucket = calculateDividendsTax(myBands);
        myIncome.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Amount));
        myTax.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Taxation));

        /* Calculate the taxable gains taxation */
        myBucket = calculateTaxableGainsTax(myBands);
        myIncome.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Amount));
        myTax.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Taxation));

        /* Calculate the capital gains taxation */
        myBucket = calculateCapitalGainsTax(myBands);
        myIncome.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Amount));
        myTax.addAmount(myBucket.getMoneyAttribute(TaxAttribute.Taxation));

        /* Build the TotalTaxBucket */
        myBucket = myList.getBucket(TaxCategoryClass.TotalTaxationDue);
        myBucket.setAmount(myIncome);
        myBucket.setTaxation(myTax);

        /* Access the tax paid bucket */
        TaxBasisBucket mySrcBucket = myBasis.getBucket(TaxCategoryClass.TaxPaid);

        /* Calculate the tax profit */
        myTax.subtractAmount(mySrcBucket.getValues().getMoneyValue(TaxBasisAttribute.Gross));

        /* Build the TaxProfitBucket */
        myBucket = myList.getBucket(TaxCategoryClass.TaxProfitLoss);
        myBucket.setAmount(new JMoney());
        myBucket.setTaxation(myTax);

        /* Prune the tax category list */
        myList.prune();

        /* Set the state to taxed and record values */
        myList.setHasReducedAllow(hasReducedAllow);
        myList.setHasGainsSlices(hasGainsSlices);
        myList.setAge(theAge);
    }

    /**
     * Calculate the gross income for tax purposes.
     */
    private void calculateGrossIncome() {
        /* Access Tax Basis and Calculations */
        TaxBasisBucketList myBasis = theAnalysis.getTaxBasis();
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();
        JMoney myIncome = new JMoney();

        /* Access the salary bucket and add to income */
        TaxBasisBucket mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossSalary);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.Gross));

        /* Access the rental bucket */
        mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossRental);
        JMoney myChargeable = new JMoney(mySrcBucket.getMoneyValue(TaxBasisAttribute.Gross));

        /* If we have a chargeable element */
        if (myChargeable.compareTo(theYear.getRentalAllowance()) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(theYear.getRentalAllowance());
            myIncome.addAmount(myChargeable);
        }

        /* Access the interest bucket and add to income */
        mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossInterest);
        myIncome.addAmount(mySrcBucket.getValues().getMoneyValue(TaxBasisAttribute.Gross));

        /* Access the dividends bucket and add to income */
        mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossDividend);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.Gross));

        /* Access the unit trust dividends bucket and add to income */
        mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossUTDividend);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.Gross));

        /* Access the taxable gains bucket and add to income */
        mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossTaxableGains);
        myIncome.addAmount(mySrcBucket.getMoneyValue(TaxBasisAttribute.Gross));

        /* Access the capital gains bucket */
        mySrcBucket = myBasis.getBucket(TaxCategoryClass.GrossCapitalGains);
        myChargeable = new JMoney(mySrcBucket.getMoneyValue(TaxBasisAttribute.Gross));

        /* If we have a chargeable element */
        if (myChargeable.compareTo(theYear.getCapitalAllow()) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(theYear.getCapitalAllow());
            myIncome.addAmount(myChargeable);
        }

        /* Access the Gross Income bucket and set the amount */
        TaxCalcBucket myBucket = myList.getBucket(TaxCategoryClass.GrossIncome);
        myBucket.setAmount(myIncome);
    }

    /**
     * Calculate the allowances and tax bands.
     * @param pManager the preference manager
     * @return the taxBands
     */
    private TaxBands calculateAllowances(final PreferenceManager pManager) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Allocate the tax bands class */
        TaxBands myBands = new TaxBands();
        JMoney myAllowance;
        JMoney myAdjust;

        /* Access the taxation properties */
        TaxationPreferences myPreferences = pManager.getPreferenceSet(TaxationPreferences.class);

        /* Determine the relevant age for this tax year */
        theAge = myPreferences.getDateValue(TaxationPreferences.NAME_BIRTHDATE).ageOn(theYear.getTaxYear());

        /* Determine the relevant allowance */
        if (theAge >= LIMIT_AGE_HI) {
            myAllowance = theYear.getHiAgeAllow();
            hasAgeAllowance = true;
        } else if (theAge >= LIMIT_AGE_LO) {
            myAllowance = theYear.getLoAgeAllow();
            hasAgeAllowance = true;
        } else {
            myAllowance = theYear.getAllowance();
        }

        /* Record the Original allowance */
        TaxCalcBucket myParentBucket = myList.getBucket(TaxCategoryClass.OriginalAllowance);
        myParentBucket.setAmount(myAllowance);

        /* Access the gross income */
        TaxCalcBucket myBucket = myList.getBucket(TaxCategoryClass.GrossIncome);
        JMoney myGrossIncome = myBucket.getMoneyAttribute(TaxAttribute.Amount);
        myBucket.setParent(myParentBucket);

        /* If we are using age allowance and the gross income is above the Age Allowance Limit */
        if ((hasAgeAllowance)
            && (myGrossIncome.compareTo(theYear.getAgeAllowLimit()) > 0)) {
            /* Calculate the margin by which we exceeded the limit */
            myAdjust = new JMoney(myGrossIncome);
            myAdjust.subtractAmount(theYear.getAgeAllowLimit());

            /* Calculate the allowance reduction by dividing by �2 and then multiply up by �1 */
            myAdjust.divide(ALLOWANCE_QUOTIENT.unscaledValue());
            myAdjust.multiply(ALLOWANCE_MULTIPLIER.unscaledValue());

            /* Adjust the allowance by this value */
            myAllowance = new JMoney(myAllowance);
            myAllowance.subtractAmount(myAdjust);

            /* If we have reduced below the standard allowance */
            if (myAllowance.compareTo(theYear.getAllowance()) < 0) {
                /* Reset the allowance to the standard value */
                myAllowance = theYear.getAllowance();
                hasAgeAllowance = false;
            }

            /* Record the adjusted allowance */
            myBucket = myList.getBucket(TaxCategoryClass.AdjustedAllowance);
            myBucket.setAmount(myBands.theAllowance);
            myBucket.setParent(myParentBucket);
            hasReducedAllow = true;
        }

        /* Set Allowance and Tax Bands */
        myBands.theAllowance = new JMoney(myAllowance);
        myBands.theLoBand = new JMoney(theYear.getLoBand());
        myBands.theBasicBand = new JMoney(theYear.getBasicBand());

        /* If we have an additional tax band */
        if (theYear.hasAdditionalTaxBand()) {
            /* Set the High tax band */
            myBands.theHiBand = new JMoney(theYear.getAddIncBound());

            /* Remove the basic band from this one */
            myBands.theHiBand.subtractAmount(myBands.theBasicBand);

            /* Record the High tax band */
            myBucket = myList.getBucket(TaxCategoryClass.HiTaxBand);
            myBucket.setAmount(myBands.theHiBand);
            myBucket.setParent(myParentBucket);

            /* If the gross income is above the Additional Allowance Limit */
            if (myGrossIncome.compareTo(theYear.getAddAllowLimit()) > 0) {
                /* Calculate the margin by which we exceeded the limit */
                myAdjust = new JMoney(myGrossIncome);
                myAdjust.subtractAmount(theYear.getAddAllowLimit());

                /* Calculate the allowance reduction by dividing by �2 and then multiply up by �1 */
                myAdjust.divide(ALLOWANCE_QUOTIENT.unscaledValue());
                myAdjust.multiply(ALLOWANCE_MULTIPLIER.unscaledValue());

                /* Adjust the allowance by this value */
                myAllowance = new JMoney(myAllowance);
                myAllowance.subtractAmount(myAdjust);

                /* If we have used up the entire allowance */
                if (!myAllowance.isPositive()) {
                    /* Personal allowance is reduced to zero */
                    myBands.theAllowance = new JMoney();
                }

                /* Record the adjusted allowance */
                myBucket = myList.getBucket(TaxCategoryClass.AdjustedAllowance);
                myBucket.setAmount(myBands.theAllowance);
                myBucket.setParent(myParentBucket);
                hasReducedAllow = true;
            }
        }

        /* Return to caller */
        return myBands;
    }

    /**
     * Calculate the tax due on salary.
     * @param pBands the remaining allowances and tax bands
     * @return the salary taxation bucket
     */
    private TaxCalcBucket calculateSalaryTax(final TaxBands pBands) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Access Salary */
        TaxCalcBucket mySrcBucket = myList.getBucket(TaxCategoryClass.GrossSalary);
        JMoney mySalary = new JMoney(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Store the total into the TaxDueSalary Bucket */
        TaxCalcBucket myTopBucket = myList.getBucket(TaxCategoryClass.TaxDueSalary);
        myTopBucket.setAmount(mySalary);

        /* Access the FreeSalaryBucket */
        TaxCalcBucket myTaxBucket = myList.getBucket(TaxCategoryClass.SalaryNilRate);
        myTaxBucket.setParent(myTopBucket);

        /* If the salary is greater than the remaining allowance */
        if (mySalary.compareTo(pBands.theAllowance) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));

            /* Adjust the salary to remove allowance */
            mySalary.subtractAmount(pBands.theAllowance);
            pBands.theAllowance.setZero();

            /* else still have allowance left after salary */
        } else {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(mySalary));

            /* Adjust the allowance to remove salary and note that we have finished */
            pBands.theAllowance.subtractAmount(mySalary);
            isFinished = true;
        }

        /* If we have salary left */
        if (!isFinished) {
            /* If we have a low salary band */
            if (theYear.hasLoSalaryBand()) {
                /* Access the LowSalaryBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.SalaryLoRate);
                myTaxBucket.setRate(theYear.getLoTaxRate());
                myTaxBucket.setParent(myTopBucket);

                /* If the salary is greater than the Low Tax Band */
                if (mySalary.compareTo(pBands.theLoBand) > 0) {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));

                    /* Adjust the salary to remove LoBand */
                    mySalary.subtractAmount(pBands.theLoBand);
                    pBands.theLoBand.setZero();

                    /* else we still have band left after salary */
                } else {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(mySalary));

                    /* Adjust the loBand to remove salary and note that we have finished */
                    pBands.theLoBand.subtractAmount(mySalary);
                    isFinished = true;
                }

                /* Else use up the Low Tax band */
            } else {
                /* If the salary is greater than the Low Tax Band */
                if (mySalary.compareTo(pBands.theLoBand) > 0) {
                    /* We have used up the band */
                    pBands.theLoBand.setZero();
                } else {
                    /* Adjust the band to remove salary */
                    pBands.theLoBand.subtractAmount(mySalary);
                }
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* Access the BasicSalaryBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.SalaryBasicRate);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the salary is greater than the Basic Tax Band */
            if (mySalary.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the salary to remove BasicBand */
                mySalary.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after salary */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(mySalary));

                /* Adjust the basicBand to remove salary and note that we have finished */
                pBands.theBasicBand.subtractAmount(mySalary);
                isFinished = true;
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* Access the HiSalaryBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.SalaryHiRate);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the salary is greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand())
                && (mySalary.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the salary to remove HiBand */
                mySalary.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after salary */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(mySalary));

                /* Adjust the hiBand to remove salary and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(mySalary);
                }
                isFinished = true;
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* Access the AdditionalSalaryBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.SalaryAdditionalRate);
            myTaxBucket.setRate(theYear.getAddTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(mySalary));
        }

        /* Store the taxation value into the top bucket */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * Calculate the tax due on rental.
     * @param pBands the remaining allowances and tax bands
     * @return the rental tax bucket
     */
    private TaxCalcBucket calculateRentalTax(final TaxBands pBands) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Access Rental */
        TaxCalcBucket mySrcBucket = myList.getBucket(TaxCategoryClass.GrossRental);
        JMoney myRental = new JMoney(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Store the total into the TaxDueRental Bucket */
        TaxCalcBucket myTopBucket = myList.getBucket(TaxCategoryClass.TaxDueRental);
        myTopBucket.setAmount(myRental);

        /* Access the FreeRentalBucket */
        TaxCalcBucket myTaxBucket = myList.getBucket(TaxCategoryClass.RentalNilRate);
        myTaxBucket.setParent(myTopBucket);

        /* Pick up the rental allowance */
        JMoney myAllowance = theYear.getRentalAllowance();

        /* If the rental is less than the rental allowance */
        if (myRental.compareTo(myAllowance) < 0) {
            /* All of the rental is free so record it and note that we have finished */
            myTax.addAmount(myTaxBucket.setAmount(myRental));
            isFinished = true;
        }

        /* If we have not finished */
        if (!isFinished) {
            /* Remove allowance from rental figure */
            myRental.subtractAmount(myAllowance);

            /* If the rental is greater than the remaining allowance */
            if (myRental.compareTo(pBands.theAllowance) > 0) {
                /* Determine the remaining allowance */
                myAllowance.addAmount(pBands.theAllowance);

                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myAllowance));

                /* Adjust the rental to remove allowance */
                myRental.subtractAmount(pBands.theAllowance);
                pBands.theAllowance.setZero();

                /* else still have allowance left after rental */
            } else {
                /* Determine the remaining allowance */
                myAllowance.addAmount(myRental);

                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myAllowance));

                /* Adjust the allowance to remove rental and note that we have finished */
                pBands.theAllowance.subtractAmount(myRental);
                isFinished = true;
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* If we have a low salary band */
            if (theYear.hasLoSalaryBand()) {
                /* Access the LowRentalBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.RentalLoRate);
                myTaxBucket.setRate(theYear.getLoTaxRate());
                myTaxBucket.setParent(myTopBucket);

                /* If the rental is greater than the Low Tax Band */
                if (myRental.compareTo(pBands.theLoBand) > 0) {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));

                    /* Adjust the rental to remove LoBand */
                    myRental.subtractAmount(pBands.theLoBand);
                    pBands.theLoBand.setZero();

                    /* else we still have band left after salary */
                } else {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(myRental));

                    /* Adjust the loBand to remove rental and note that we have finished */
                    pBands.theLoBand.subtractAmount(myRental);
                    isFinished = true;
                }

                /* Else use up the Low Tax band */
            } else {
                /* If the rental is greater than the Low Tax Band */
                if (myRental.compareTo(pBands.theLoBand) > 0) {
                    /* We have used up the band */
                    pBands.theLoBand.setZero();
                } else {
                    /* Adjust the band to remove rental */
                    pBands.theLoBand.subtractAmount(myRental);
                }
            }
        }

        /* If we have Rental left */
        if (!isFinished) {
            /* Access the BasicRentalBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.RentalBasicRate);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the rental is greater than the Basic Tax Band */
            if (myRental.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the rental to remove BasicBand */
                myRental.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after rental */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myRental));

                /* Adjust the basicBand to remove salary and note that we have finished */
                pBands.theBasicBand.subtractAmount(myRental);
                isFinished = true;
            }
        }

        /* If we have rental left */
        if (!isFinished) {
            /* Access the HiRentalBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.RentalHiRate);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the rental is greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand())
                && (myRental.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the rental to remove HiBand */
                myRental.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after rental */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myRental));

                /* Adjust the hiBand to remove rental and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myRental);
                }
                isFinished = true;
            }
        }

        /* If we have rental left */
        if (!isFinished) {
            /* Access the AdditionalRentalBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.RentalAdditionalRate);
            myTaxBucket.setRate(theYear.getAddTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myRental));
        }

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * Calculate the tax due on Interest.
     * @param pBands the remaining allowances and tax bands
     * @return the interest tax bucket
     */
    private TaxCalcBucket calculateInterestTax(final TaxBands pBands) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* If we do not have a Low salary band */
        if (!theYear.hasLoSalaryBand()) {
            /* Remove LoTaxBand from BasicTaxBand */
            pBands.theBasicBand.subtractAmount(pBands.theLoBand);
        }

        /* Access Interest */
        TaxCalcBucket mySrcBucket = myList.getBucket(TaxCategoryClass.GrossInterest);
        JMoney myInterest = new JMoney(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Store the total into the TaxDueInterest Bucket */
        TaxCalcBucket myTopBucket = myList.getBucket(TaxCategoryClass.TaxDueInterest);
        myTopBucket.setAmount(myInterest);

        /* Access the FreeInterestBucket */
        TaxCalcBucket myTaxBucket = myList.getBucket(TaxCategoryClass.InterestNilRate);
        myTaxBucket.setParent(myTopBucket);

        /* If the interest is greater than the remaining allowance */
        if (myInterest.compareTo(pBands.theAllowance) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));

            /* Adjust the interest to remove allowance */
            myInterest.subtractAmount(pBands.theAllowance);
            pBands.theAllowance.setZero();

            /* else still have allowance left after interest */
        } else {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myInterest));

            /* Adjust the allowance to remove interest and note that we have finished */
            pBands.theAllowance.subtractAmount(myInterest);
            isFinished = true;
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the LowInterestBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.InterestLoRate);
            myTaxBucket.setRate(theYear.getLoTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the interest is greater than the Low Tax Band */
            if (myInterest.compareTo(pBands.theLoBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));

                /* Adjust the interest to remove LoBand */
                myInterest.subtractAmount(pBands.theLoBand);
                pBands.theLoBand.setZero();

                /* else we still have band left after interest */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myInterest));

                /* Adjust the loBand to remove interest and note that we have finished */
                pBands.theLoBand.subtractAmount(myInterest);
                isFinished = true;
            }
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the BasicInterestBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.InterestBasicRate);
            myTaxBucket.setRate(theYear.getIntTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the interest is greater than the Basic Tax Band */
            if (myInterest.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the interest to remove BasicBand */
                myInterest.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after interest */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myInterest));

                /* Adjust the basicBand to remove interest and note that we have finished */
                pBands.theBasicBand.subtractAmount(myInterest);
                isFinished = true;
            }
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the HiInterestBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.InterestHiRate);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the interest is greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand())
                && (myInterest.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the interest to remove HiBand */
                myInterest.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after interest */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myInterest));

                /* Adjust the hiBand to remove interest and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myInterest);
                }
                isFinished = true;
            }
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the AdditionalInterestBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.InterestAdditionalRate);
            myTaxBucket.setRate(theYear.getAddTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myInterest));
        }

        /* Remaining tax credits are not reclaimable */
        /* so add any remaining allowance/LoTaxBand into BasicTaxBand */
        pBands.theBasicBand.addAmount(pBands.theAllowance);
        pBands.theBasicBand.addAmount(pBands.theLoBand);
        pBands.theAllowance.setZero();
        pBands.theLoBand.setZero();

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * calculate the tax due on dividends.
     * @param pBands the remaining allowances and tax bands
     * @return the dividends tax bucket
     */
    private TaxCalcBucket calculateDividendsTax(final TaxBands pBands) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Access Dividends */
        TaxCalcBucket mySrcBucket = myList.getBucket(TaxCategoryClass.GrossDividend);
        JMoney myDividends = new JMoney(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Access Unit Trust Dividends */
        mySrcBucket = myList.getBucket(TaxCategoryClass.GrossUTDividend);
        myDividends.addAmount(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));

        /* Store the total into the TaxDueDividends Bucket */
        TaxCalcBucket myTopBucket = myList.getBucket(TaxCategoryClass.TaxDueDividend);
        myTopBucket.setAmount(myDividends);

        /* Access the BasicDividendBucket */
        TaxCalcBucket myTaxBucket = myList.getBucket(TaxCategoryClass.DividendBasicRate);
        myTaxBucket.setRate(theYear.getDivTaxRate());
        myTaxBucket.setParent(myTopBucket);

        /* If the dividends are greater than the Basic Tax Band */
        if (myDividends.compareTo(pBands.theBasicBand) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

            /* Adjust the dividends to remove BasicBand */
            myDividends.subtractAmount(pBands.theBasicBand);
            pBands.theBasicBand.setZero();

            /* else we still have band left after dividends */
        } else {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myDividends));

            /* Adjust the basicBand to remove dividends and note that we have finished */
            pBands.theBasicBand.subtractAmount(myDividends);
            isFinished = true;
        }

        /* If we have dividends left */
        if (!isFinished) {
            /* Access the HiDividendsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.DividendHiRate);
            myTaxBucket.setRate(theYear.getHiDivTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the dividends are greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand())
                && (myDividends.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the dividends to remove HiBand */
                myDividends.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after dividends */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myDividends));

                /* Adjust the hiBand to remove dividends and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myDividends);
                }
                isFinished = true;
            }
        }

        /* If we have dividends left */
        if (!isFinished) {
            /* Access the AdditionalDividendsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.DividendAdditionalRate);
            myTaxBucket.setRate(theYear.getAddDivTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myDividends));
        }

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * calculate the tax due on taxable gains.
     * @param pBands the remaining allowances and tax bands
     * @return the taxable gains bucket
     */
    private TaxCalcBucket calculateTaxableGainsTax(final TaxBands pBands) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Access Gains */
        JMoney myGains = theCharges.getGainsTotal();
        JMoney myTax = new JMoney();
        boolean isFinished = false;
        TaxCalcBucket myTaxBucket;

        /* Store the total into the TaxDueTaxGains Bucket */
        TaxCalcBucket myTopBucket = myList.getBucket(TaxCategoryClass.TaxDueTaxableGains);
        myTopBucket.setAmount(myGains);

        /* If the gains are less than the available basic tax band */
        if (myGains.compareTo(pBands.theBasicBand) <= 0) {
            /* Access the BasicGainsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.GainsBasicRate);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myGains));

            /* Adjust the basic band to remove taxable gains */
            pBands.theBasicBand.subtractAmount(myGains);
            isFinished = true;
        }

        /*
         * If we are not finished but either have no basic band left or are prevented from top-slicing due to using age allowances
         */
        if ((!isFinished)
            && ((!pBands.theBasicBand.isNonZero()) || (hasAgeAllowance))) {
            /* Access the BasicGainsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.GainsBasicRate);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the gains is greater than the Basic Tax Band */
            if (myGains.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the gains to remove BasicBand */
                myGains.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();
            }

            /* else case already handled */

            /* Access the HiGainsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.GainsHiRate);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the gains are greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand())
                && (myGains.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the gains to remove HiBand */
                myGains.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after gains */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myGains));

                /* Adjust the hiBand to remove dividends and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myGains);
                }
                isFinished = true;
            }

            /* If we have gains left */
            if (!isFinished) {
                /* Access the AdditionalGainsBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.GainsAdditionalRate);
                myTaxBucket.setRate(theYear.getAddDivTaxRate());
                myTaxBucket.setParent(myTopBucket);

                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myGains));
                isFinished = true;
            }
        }

        /* If we are not finished then we need top-slicing relief */
        if (!isFinished) {
            /* Access the taxable slice */
            JMoney mySlice = theCharges.getSliceTotal();
            hasGainsSlices = true;

            /* Access the TaxDueSlice Bucket */
            TaxCalcBucket mySliceBucket = myList.getBucket(TaxCategoryClass.TaxDueSlice);
            mySliceBucket.setAmount(mySlice);

            /* Access the BasicSliceBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.SliceBasicRate);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(mySliceBucket);

            /* If the slice is less than the available basic tax band */
            if (mySlice.compareTo(pBands.theBasicBand) < 0) {
                /* Set the slice details */
                myTax.addAmount(myTaxBucket.setAmount(mySlice));

                /* Distribute the Tax back to the chargeable events */
                theCharges.applyTax(myTax, theCharges.getSliceTotal());

                /* Access the BasicGainsBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.GainsBasicRate);
                myTaxBucket.setRate(theYear.getBasicTaxRate());

                /* Only basic rate tax is payable */
                myTaxBucket.setAmount(myGains);
                mySliceBucket.setTaxation(myTax);

                /* else we are using up the basic rate tax band */
            } else {
                /* Set the slice details */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Subtract the basic band from the slice */
                mySlice.subtractAmount(pBands.theBasicBand);

                /* Access the BasicGainsBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.GainsBasicRate);
                myTaxBucket.setRate(theYear.getBasicTaxRate());

                /* Basic Rate tax is payable on the remainder of the basic band */
                myTaxBucket.setAmount(pBands.theBasicBand);

                /* Remember this taxation amount to remove from HiTax bucket */
                JMoney myHiTax = new JMoney(myTaxBucket.getMoneyAttribute(TaxAttribute.Amount));
                myHiTax.negate();

                /* Access the HiSliceBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.SliceHiRate);
                myTaxBucket.setRate(theYear.getHiTaxRate());
                myTaxBucket.setParent(mySliceBucket);

                /* If the slice is greater than the High Tax Band */
                if ((theYear.hasAdditionalTaxBand())
                    && (mySlice.compareTo(pBands.theHiBand) > 0)) {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                    /* Adjust the slice to remove HiBand */
                    mySlice.subtractAmount(pBands.theHiBand);

                    /* Access the AdditionalSliceBucket */
                    myTaxBucket = myList.getBucket(TaxCategoryClass.SliceAdditionalRate);
                    myTaxBucket.setRate(theYear.getAddTaxRate());
                    myTaxBucket.setParent(mySliceBucket);

                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(mySlice));

                    /* else we still have band left after slice */
                } else {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(mySlice));
                }

                /* Set the total tax into the slice bucket */
                mySliceBucket.setTaxation(myTax);

                /* Distribute the Slice back to the chargeable events */
                theCharges.applyTax(myTax, theCharges.getSliceTotal());

                /* Calculate the total tax payable */
                myTax = theCharges.getTaxTotal();

                /* HiRate tax is the calculated tax minus the tax payable in the basic band */
                myHiTax.addAmount(myTax);

                /* Access the HiGainsBucket */
                myTaxBucket = myList.getBucket(TaxCategoryClass.GainsHiRate);
                myTaxBucket.setParent(myTopBucket);

                /* Subtract the basic band from the gains */
                myGains.subtractAmount(pBands.theBasicBand);

                /* Set the amount and tax explicitly */
                myTaxBucket.setAmount(myGains);
                myTaxBucket.setTaxation(myHiTax);
            }

            /* Re-access the gains */
            TaxCalcBucket mySrcBucket = myList.getBucket(TaxCategoryClass.GrossTaxableGains);
            myGains = new JMoney(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));

            /* Subtract the gains from the tax bands */
            myGains.subtractAmount(pBands.theBasicBand);
            pBands.theBasicBand.setZero();
            if (theYear.hasAdditionalTaxBand()) {
                pBands.theHiBand.subtractAmount(myGains);
            }
        }

        /* Access the TaxDueTaxableGains Bucket */
        myTaxBucket = myList.getBucket(TaxCategoryClass.TaxDueTaxableGains);
        myTaxBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTaxBucket;
    }

    /**
     * calculate the tax due on capital gains.
     * @param pBands the remaining allowances and tax bands
     * @return the capital gains tax bucket
     */
    private TaxCalcBucket calculateCapitalGainsTax(final TaxBands pBands) {
        /* Access Tax Calculations */
        TaxCalcBucketList myList = theAnalysis.getTaxCalculations();

        /* Access base bucket */
        TaxCalcBucket mySrcBucket = myList.getBucket(TaxCategoryClass.GrossCapitalGains);
        JMoney myCapital = new JMoney(mySrcBucket.getMoneyAttribute(TaxAttribute.Amount));

        /* Store the total into the TaxDueCapital Bucket */
        TaxCalcBucket myTopBucket = myList.getBucket(TaxCategoryClass.TaxDueCapitalGains);
        myTopBucket.setAmount(myCapital);

        /* Access the FreeGainsBucket */
        TaxCalcBucket myTaxBucket = myList.getBucket(TaxCategoryClass.CapitalNilRate);
        myTaxBucket.setParent(myTopBucket);

        /* Pick up the capital allowance */
        JMoney myAllowance = theYear.getCapitalAllow();
        JMoney myTax = new JMoney();
        TaxRegime myRegime = theYear.getTaxRegime();
        boolean isFinished = false;

        /* If the gains is greater than the capital allowance */
        if (myCapital.compareTo(myAllowance) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myAllowance));

            /* Adjust the gains to remove allowance */
            myCapital.subtractAmount(myAllowance);

            /* else allowance is sufficient */
        } else {
            /* Set the correct value for the tax bucket and note that we have finished */
            myTax.addAmount(myTaxBucket.setAmount(myCapital));
            isFinished = true;
        }

        /* If we have gains left */
        if (!isFinished) {
            /* Access the BasicGainsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.CapitalBasicRate);
            myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome()
                    ? theYear.getBasicTaxRate()
                    : theYear.getCapTaxRate()));
            myTaxBucket.setParent(myTopBucket);

            /* Determine whether we need to use basic tax band */
            boolean bUseBasicBand = ((myRegime.hasCapitalGainsAsIncome()) || (theYear.getHiCapTaxRate() != null));

            /* If the gains is greater than the Basic Tax Band and we have no higher rate */
            if ((myCapital.compareTo(pBands.theBasicBand) > 0)
                || (!bUseBasicBand)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the gains to remove BasicBand */
                myCapital.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after gains */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myCapital));

                /* Adjust the basicBand to remove capital and note that we have finished */
                if (bUseBasicBand) {
                    pBands.theBasicBand.subtractAmount(myCapital);
                }
                isFinished = true;
            }
        }

        /* If we have gains left */
        if (!isFinished) {
            /* Access the HiGainsBucket */
            myTaxBucket = myList.getBucket(TaxCategoryClass.CapitalHiRate);
            myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome()
                    ? theYear.getHiTaxRate()
                    : theYear.getHiCapTaxRate()));
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myCapital));
        }

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * Class to hold active allowances and tax bands.
     */
    private static class TaxBands {
        /**
         * The allowance.
         */
        private JMoney theAllowance = null;

        /**
         * The Lo Tax Band.
         */
        private JMoney theLoBand = null;

        /**
         * The Basic Tax Band.
         */
        private JMoney theBasicBand = null;

        /**
         * The High Tax Band.
         */
        private JMoney theHiBand = null;
    }
}
