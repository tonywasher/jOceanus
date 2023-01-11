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

import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.StaticDataResource;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseUKChargeableGainsScheme.MoneyWiseUKSlicedTaxDueBucket;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

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
        FIELD_DEFS.declareLocalField(StaticDataResource.TAXBASIS_TAXPAID, MoneyWiseUKTaxAnalysis::getTaxPaid);
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
    private final TethysMoney theTaxableIncome;

    /**
     * The Total TaxDue.
     */
    private final TethysMoney theTaxDue;

    /**
     * The Total Tax Paid.
     */
    private final TethysMoney theTaxPaid;

    /**
     * The TaxProfit/Loss.
     */
    private final TethysMoney theTaxProfit;

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
            extends MetisPreferenceSet<MoneyWiseUKTaxPreferenceKey> {
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
            super(pManager, MoneyWiseUKTaxPreferenceKey.class, MoneyWiseTaxResource.TAXPREF_NAME);
        }

        @Override
        protected void definePreferences() {
            defineDatePreference(MoneyWiseUKTaxPreferenceKey.BIRTHDATE);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the birthDate is specified */
            final MetisDatePreference<MoneyWiseUKTaxPreferenceKey> myPref = getDatePreference(MoneyWiseUKTaxPreferenceKey.BIRTHDATE);
            if (!myPref.isAvailable()) {
                myPref.setValue(new TethysDate(YEAR, Month.JANUARY, 1));
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
        final TethysDate myBirthday = myPreferences.getDateValue(MoneyWiseUKTaxPreferenceKey.BIRTHDATE);
        theTaxConfig = new MoneyWiseUKTaxConfig(theTaxYear, theTaxSource, myBirthday);

        /* Create the totals */
        theTaxPaid = pTaxSource.getAmountForTaxBasis(TaxBasisClass.TAXPAID);
        theTaxableIncome = new TethysMoney(theTaxPaid);
        theTaxableIncome.setZero();
        theTaxDue = new TethysMoney(theTaxPaid);
        theTaxDue.setZero();
        theTaxProfit = new TethysMoney(theTaxPaid);
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
    public TethysMoney getTaxableIncome() {
        return theTaxableIncome;
    }

    @Override
    public TethysMoney getTaxDue() {
        return theTaxDue;
    }

    @Override
    public TethysMoney getTaxPaid() {
        return theTaxPaid;
    }

    @Override
    public TethysMoney getTaxProfit() {
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
            if (myBucket instanceof MoneyWiseUKSlicedTaxDueBucket) {
                /* Apply Tax Relief */
                final MoneyWiseUKSlicedTaxDueBucket mySliced = (MoneyWiseUKSlicedTaxDueBucket) myBucket;
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
    protected void processItem(final TaxBasisClass pBasis,
                               final MoneyWiseUKIncomeScheme pScheme) {
        /* Obtain the amount */
        final TethysMoney myAmount = theTaxSource.getAmountForTaxBasis(pBasis);

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
        if (TaxBasisClass.CHARGEABLEGAINS.equals(pBasis)
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
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
