/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.metis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.moneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.moneywise.tax.uk.MoneyWiseUKChargeableGainsScheme.MoneyWiseUKSlicedTaxDueBucket;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;

import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * UK Tax Analysis.
 */
public class MoneyWiseUKTaxAnalysis
        implements MetisFieldItem, MoneyWiseTaxAnalysis {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseUKTaxAnalysis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseUKTaxAnalysis.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXYEAR_NAME, MoneyWiseUKTaxAnalysis::getTaxYear);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXCONFIG_NAME, MoneyWiseUKTaxAnalysis::getTaxConfig);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXANALYSIS_TAXBUCKETS, MoneyWiseUKTaxAnalysis::getTaxBuckets);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_INCOME, MoneyWiseUKTaxAnalysis::getTaxableIncome);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXBANDS_TAXDUE, MoneyWiseUKTaxAnalysis::getTaxDue);
        FIELD_DEFS.declareLocalField(MoneyWiseStaticResource.TAXBASIS_TAXPAID, MoneyWiseUKTaxAnalysis::getTaxPaid);
        FIELD_DEFS.declareLocalField(MoneyWiseTaxResource.TAXANALYSIS_TAXPROFIT, MoneyWiseUKTaxAnalysis::getTaxProfit);
    }

    /**
     * The TaxYear.
     */
    private final MoneyWiseUKTaxYear theTaxYear;

    /**
     * The TaxSource.
     */
    private final MoneyWiseTaxSource theTaxSource;

    /**
     * The TaxConfig.
     */
    private final MoneyWiseUKTaxConfig theTaxConfig;

    /**
     * The TaxDueBuckets.
     */
    private final List<MoneyWiseTaxDueBucket> theTaxBuckets;

    /**
     * The Total TaxableIncome.
     */
    private final OceanusMoney theTaxableIncome;

    /**
     * The Total TaxDue.
     */
    private final OceanusMoney theTaxDue;

    /**
     * The Total Tax Paid.
     */
    private final OceanusMoney theTaxPaid;

    /**
     * The TaxProfit/Loss.
     */
    private final OceanusMoney theTaxProfit;

    /**
     * TaxPreferenceKeys.
     */
    public enum MoneyWiseUKTaxPreferenceKey implements MetisPreferenceKey {
        /**
         * Birth Date.
         */
        BIRTHDATE("BirthDate", MoneyWiseTaxResource.TAXPREF_BIRTH);

        /**
         * The name of the Preference.
         */
        private final String theName;

        /**
         * The display string.
         */
        private final String theDisplay;

        /**
         * Constructor.
         * @param pName the name
         * @param pDisplay the display string;
         */
        MoneyWiseUKTaxPreferenceKey(final String pName,
                                    final MoneyWiseTaxResource pDisplay) {
            theName = pName;
            theDisplay = pDisplay.getValue();
        }

        @Override
        public String getName() {
            return theName;
        }

        @Override
        public String getDisplay() {
            return theDisplay;
        }
    }

    /**
     * Taxation Preferences.
     */
    public static class MoneyWiseUKTaxPreferences
            extends MetisPreferenceSet {
        /**
         * Default year.
         */
        private static final int YEAR = 1970;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MoneyWiseUKTaxPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MoneyWiseTaxResource.TAXPREF_NAME);
        }

        @Override
        protected void definePreferences() {
            defineDatePreference(MoneyWiseUKTaxPreferenceKey.BIRTHDATE);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the birthDate is specified */
            final MetisDatePreference myPref = getDatePreference(MoneyWiseUKTaxPreferenceKey.BIRTHDATE);
            if (!myPref.isAvailable()) {
                myPref.setValue(new OceanusDate(YEAR, Month.JANUARY, 1));
            }
        }
    }

    /**
     * Constructor.
     * @param pTaxSource the taxSource
     * @param pPrefMgr the preference manager
     * @param pTaxYear the tax year
     */
    protected MoneyWiseUKTaxAnalysis(final MoneyWiseTaxSource pTaxSource,
                                     final MetisPreferenceManager pPrefMgr,
                                     final MoneyWiseUKTaxYear pTaxYear) {
        /* Store the parameters */
        theTaxYear = pTaxYear;
        theTaxSource = pTaxSource;

        /* Create the TaxDue Buckets */
        theTaxBuckets = new ArrayList<>();

        /* Determine the client birthday */
        final MoneyWiseUKTaxPreferences myPreferences = pPrefMgr.getPreferenceSet(MoneyWiseUKTaxPreferences.class);
        final OceanusDate myBirthday = myPreferences.getDateValue(MoneyWiseUKTaxPreferenceKey.BIRTHDATE);
        theTaxConfig = new MoneyWiseUKTaxConfig(theTaxYear, theTaxSource, myBirthday);

        /* Create the totals */
        theTaxPaid = pTaxSource.getAmountForTaxBasis(MoneyWiseTaxClass.TAXPAID);
        theTaxableIncome = new OceanusMoney(theTaxPaid);
        theTaxableIncome.setZero();
        theTaxDue = new OceanusMoney(theTaxPaid);
        theTaxDue.setZero();
        theTaxProfit = new OceanusMoney(theTaxPaid);
        theTaxProfit.setZero();
    }

    @Override
    public MoneyWiseUKTaxYear getTaxYear() {
        return theTaxYear;
    }

    /**
     * Obtain the taxConfig.
     * @return the taxConfig
     */
    public MoneyWiseUKTaxConfig getTaxConfig() {
        return theTaxConfig;
    }

    @Override
    public Iterator<MoneyWiseTaxDueBucket> taxDueIterator() {
        return theTaxBuckets.iterator();
    }

    /**
     * Obtain the taxBuckets.
     * @return the taxBuckets
     */
    private List<MoneyWiseTaxDueBucket> getTaxBuckets() {
        return theTaxBuckets;
    }

    @Override
    public OceanusMoney getTaxableIncome() {
        return theTaxableIncome;
    }

    @Override
    public OceanusMoney getTaxDue() {
        return theTaxDue;
    }

    @Override
    public OceanusMoney getTaxPaid() {
        return theTaxPaid;
    }

    @Override
    public OceanusMoney getTaxProfit() {
        return theTaxProfit;
    }

    /**
     * Calculate the taxDue.
     */
    protected void calculateTaxDue() {
        /* Reset the tax Due */
        theTaxableIncome.setZero();
        theTaxDue.setZero();

        /* Loop through the tax bands */
        final Iterator<MoneyWiseTaxDueBucket> myIterator = theTaxBuckets.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTaxDueBucket myBucket = myIterator.next();

            /* Add the values */
            theTaxableIncome.addAmount(myBucket.getTaxableIncome());
            theTaxDue.addAmount(myBucket.getTaxDue());

            /* If this is a sliced tax bucket */
            if (myBucket instanceof MoneyWiseUKSlicedTaxDueBucket mySliced) {
                /* Apply Tax Relief */
                theTaxDue.subtractAmount(mySliced.getTaxRelief());
            }
        }
    }

    /**
     * Calculate the taxProfit.
     */
    protected void calculateTaxProfit() {
        /* Calculate the profit */
        theTaxProfit.setZero();
        theTaxProfit.addAmount(theTaxDue);
        theTaxProfit.addAmount(theTaxPaid);
    }

    /**
     * Process the item.
     * @param pBasis the tax basis
     * @param pScheme the income scheme
     */
    protected void processItem(final MoneyWiseTaxClass pBasis,
                               final MoneyWiseUKIncomeScheme pScheme) {
        /* Obtain the amount */
        final OceanusMoney myAmount = theTaxSource.getAmountForTaxBasis(pBasis);

        /* Ignore zero or negative amounts */
        if (myAmount.isZero()
                || !myAmount.isPositive()) {
            return;
        }

        /* Take a clone of the taxConfig */
        final MoneyWiseUKTaxConfig myConfig = theTaxConfig.cloneIt();

        /* Allocate the amount to the various taxBands */
        final MoneyWiseTaxBandSet myBands = pScheme.allocateToTaxBands(theTaxConfig, pBasis, myAmount);

        /* Create the TaxDueBucket */
        MoneyWiseTaxDueBucket myBucket = new MoneyWiseTaxDueBucket(pBasis, myBands, myConfig);

        /* If this is ChargeableGains, and we have multiple Bands */
        if (MoneyWiseTaxClass.CHARGEABLEGAINS.equals(pBasis)
                && myBands.multipleBands()) {
            /* Analyse the Slices */
            myBucket = new MoneyWiseUKSlicedTaxDueBucket(myBucket, theTaxSource);
        }

        /* Add the bucket to the list */
        theTaxBuckets.add(myBucket);
    }

    @Override
    public MetisFieldSet<MoneyWiseUKTaxAnalysis> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
