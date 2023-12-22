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
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxBandSet;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxResource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.MoneyWiseXTaxSource;
import net.sourceforge.joceanus.jmoneywise.lethe.tax.uk.MoneyWiseXUKChargeableGainsScheme.MoneyWiseXUKSlicedTaxDueBucket;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * UK Tax Analysis.
 */
public class MoneyWiseXUKTaxAnalysis
        implements MetisFieldItem, MoneyWiseXTaxAnalysis {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXUKTaxAnalysis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXUKTaxAnalysis.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXYEAR_NAME, MoneyWiseXUKTaxAnalysis::getTaxYear);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXCONFIG_NAME, MoneyWiseXUKTaxAnalysis::getTaxConfig);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXANALYSIS_TAXBUCKETS, MoneyWiseXUKTaxAnalysis::getTaxBuckets);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_INCOME, MoneyWiseXUKTaxAnalysis::getTaxableIncome);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXBANDS_TAXDUE, MoneyWiseXUKTaxAnalysis::getTaxDue);
        FIELD_DEFS.declareLocalField(StaticDataResource.TAXBASIS_TAXPAID, MoneyWiseXUKTaxAnalysis::getTaxPaid);
        FIELD_DEFS.declareLocalField(MoneyWiseXTaxResource.TAXANALYSIS_TAXPROFIT, MoneyWiseXUKTaxAnalysis::getTaxProfit);
    }

    /**
     * The TaxYear.
     */
    private final MoneyWiseXUKTaxYear theTaxYear;

    /**
     * The TaxSource.
     */
    private final MoneyWiseXTaxSource theTaxSource;

    /**
     * The TaxConfig.
     */
    private final MoneyWiseXUKTaxConfig theTaxConfig;

    /**
     * The TaxDueBuckets.
     */
    private final List<MoneyWiseXTaxDueBucket> theTaxBuckets;

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
    public enum MoneyWiseXUKTaxPreferenceKey implements MetisPreferenceKey {
        /**
         * Birth Date.
         */
        BIRTHDATE("BirthDate", MoneyWiseXTaxResource.TAXPREF_BIRTH);

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
        MoneyWiseXUKTaxPreferenceKey(final String pName,
                                     final MoneyWiseXTaxResource pDisplay) {
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
    public static class MoneyWiseXUKTaxPreferences
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
        public MoneyWiseXUKTaxPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MoneyWiseXTaxResource.TAXPREF_NAME);
        }

        @Override
        protected void definePreferences() {
            defineDatePreference(MoneyWiseXUKTaxPreferenceKey.BIRTHDATE);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the birthDate is specified */
            final MetisDatePreference myPref = getDatePreference(MoneyWiseXUKTaxPreferenceKey.BIRTHDATE);
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
    protected MoneyWiseXUKTaxAnalysis(final MoneyWiseXTaxSource pTaxSource,
                                      final MetisPreferenceManager pPrefMgr,
                                      final MoneyWiseXUKTaxYear pTaxYear) {
        /* Store the parameters */
        theTaxYear = pTaxYear;
        theTaxSource = pTaxSource;

        /* Create the TaxDue Buckets */
        theTaxBuckets = new ArrayList<>();

        /* Determine the client birthday */
        final MoneyWiseXUKTaxPreferences myPreferences = pPrefMgr.getPreferenceSet(MoneyWiseXUKTaxPreferences.class);
        final TethysDate myBirthday = myPreferences.getDateValue(MoneyWiseXUKTaxPreferenceKey.BIRTHDATE);
        theTaxConfig = new MoneyWiseXUKTaxConfig(theTaxYear, theTaxSource, myBirthday);

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
    public MoneyWiseXUKTaxYear getTaxYear() {
        return theTaxYear;
    }

    /**
     * Obtain the taxConfig.
     * @return the taxConfig
     */
    public MoneyWiseXUKTaxConfig getTaxConfig() {
        return theTaxConfig;
    }

    @Override
    public Iterator<MoneyWiseXTaxDueBucket> taxDueIterator() {
        return theTaxBuckets.iterator();
    }

    /**
     * Obtain the taxBuckets.
     * @return the taxBuckets
     */
    private List<MoneyWiseXTaxDueBucket> getTaxBuckets() {
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
        final Iterator<MoneyWiseXTaxDueBucket> myIterator = theTaxBuckets.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseXTaxDueBucket myBucket = myIterator.next();

            /* Add the values */
            theTaxableIncome.addAmount(myBucket.getTaxableIncome());
            theTaxDue.addAmount(myBucket.getTaxDue());

            /* If this is a sliced tax bucket */
            if (myBucket instanceof MoneyWiseXUKSlicedTaxDueBucket) {
                /* Apply Tax Relief */
                final MoneyWiseXUKSlicedTaxDueBucket mySliced = (MoneyWiseXUKSlicedTaxDueBucket) myBucket;
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
                               final MoneyWiseXUKIncomeScheme pScheme) {
        /* Obtain the amount */
        final TethysMoney myAmount = theTaxSource.getAmountForTaxBasis(pBasis);

        /* Ignore zero or negative amounts */
        if (myAmount.isZero()
            || !myAmount.isPositive()) {
            return;
        }

        /* Take a clone of the taxConfig */
        final MoneyWiseXUKTaxConfig myConfig = theTaxConfig.cloneIt();

        /* Allocate the amount to the various taxBands */
        final MoneyWiseXTaxBandSet myBands = pScheme.allocateToTaxBands(theTaxConfig, pBasis, myAmount);

        /* Create the TaxDueBucket */
        MoneyWiseXTaxDueBucket myBucket = new MoneyWiseXTaxDueBucket(pBasis, myBands, myConfig);

        /* If this is ChargeableGains, and we have multiple Bands */
        if (TaxBasisClass.CHARGEABLEGAINS.equals(pBasis)
            && myBands.multipleBands()) {
            /* Analyse the Slices */
            myBucket = new MoneyWiseXUKSlicedTaxDueBucket(myBucket, theTaxSource);
        }

        /* Add the bucket to the list */
        theTaxBuckets.add(myBucket);
    }

    @Override
    public MetisFieldSet<MoneyWiseXUKTaxAnalysis> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }
}
