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

import java.time.Month;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasisClass;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxAnalysis;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxDueBucket;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxResource;
import net.sourceforge.joceanus.jmoneywise.tax.MoneyWiseTaxSource;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * UK Tax Analysis.
 */
public class MoneyWiseUKTaxAnalysis
        implements MetisDataContents, MoneyWiseTaxAnalysis {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseTaxDueBucket.class.getSimpleName());

    /**
     * TaxYear Field Id.
     */
    private static final MetisField FIELD_TAXYEAR = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXYEAR_NAME.getValue());

    /**
     * TaxConfig Field Id.
     */
    private static final MetisField FIELD_TAXCONFIG = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXCONFIG_NAME.getValue());

    /**
     * TaxBuckets Field Id.
     */
    private static final MetisField FIELD_TAXBUCKETS = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXANALYSIS_TAXBUCKETS.getValue());

    /**
     * TaxDue Field Id.
     */
    private static final MetisField FIELD_TAXDUE = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXBANDS_TAXDUE.getValue());

    /**
     * TaxPaid Field Id.
     */
    private static final MetisField FIELD_TAXPAID = FIELD_DEFS.declareEqualityField(TaxBasisClass.TAXPAID.toString());

    /**
     * TaxProfit Field Id.
     */
    private static final MetisField FIELD_TAXPROFIT = FIELD_DEFS.declareEqualityField(MoneyWiseTaxResource.TAXANALYSIS_TAXPROFIT.getValue());

    /**
     * The TaxYear.
     */
    private final MoneyWiseUKTaxYear theTaxYear;

    /**
     * The TaxConfig.
     */
    private final MoneyWiseUKTaxConfig theTaxConfig;

    /**
     * The TaxDueBuckets.
     */
    private final List<MoneyWiseTaxDueBucket> theTaxBuckets;

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
    public enum MoneyWiseTaxPreferenceKey implements MetisPreferenceKey {
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
        MoneyWiseTaxPreferenceKey(final String pName,
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
    public static class MoneyWiseTaxPreferences
            extends MetisPreferenceSet<MoneyWiseTaxPreferenceKey> {
        /**
         * Default year.
         */
        private static final int YEAR = 1970;

        /**
         * Constructor.
         * @param pManager the preference manager
         * @throws OceanusException on error
         */
        public MoneyWiseTaxPreferences(final MetisPreferenceManager pManager) throws OceanusException {
            super(pManager, MoneyWiseTaxPreferenceKey.class, MoneyWiseTaxResource.TAXPREF_NAME);
        }

        @Override
        protected void definePreferences() {
            defineDatePreference(MoneyWiseTaxPreferenceKey.BIRTHDATE);
        }

        @Override
        public void autoCorrectPreferences() {
            /* Make sure that the birthDate is specified */
            MetisDatePreference<MoneyWiseTaxPreferenceKey> myPref = getDatePreference(MoneyWiseTaxPreferenceKey.BIRTHDATE);
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

        /* Create the TaxDue Buckets */
        theTaxBuckets = new ArrayList<>();

        /* Determine the client birthday */
        MoneyWiseTaxPreferences myPreferences = pPrefMgr.getPreferenceSet(MoneyWiseTaxPreferences.class);
        TethysDate myBirthday = myPreferences.getDateValue(MoneyWiseTaxPreferenceKey.BIRTHDATE);
        theTaxConfig = new MoneyWiseUKTaxConfig(theTaxYear, pTaxSource, myBirthday);

        /* Create the totals */
        theTaxPaid = pTaxSource.getAmountForTaxBasis(TaxBasisClass.TAXPAID);
        theTaxDue = new TethysMoney(theTaxPaid);
        theTaxDue.setZero();
        theTaxProfit = new TethysMoney(theTaxPaid);
        theTaxProfit.setZero();
    }

    /**
     * Obtain the taxYear.
     * @return the taxYear
     */
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

    /**
     * Obtain the taxBands iterator.
     * @return the iterator
     */
    public Iterator<MoneyWiseTaxDueBucket> taxDueIterator() {
        return theTaxBuckets.iterator();
    }

    /**
     * Obtain the taxDue.
     * @return the taxDue
     */
    public TethysMoney getTaxDue() {
        return theTaxDue;
    }

    /**
     * Obtain the taxPaid.
     * @return the taxPaid
     */
    public TethysMoney getTaxPaid() {
        return theTaxPaid;
    }

    /**
     * Obtain the taxProfit.
     * @return the taxProfit
     */
    public TethysMoney getTaxProfit() {
        return theTaxProfit;
    }

    /**
     * Calculate the taxDue.
     */
    protected void calculateTaxDue() {
        /* Reset the tax Due */
        theTaxDue.setZero();

        /* Loop through the tax bands */
        Iterator<MoneyWiseTaxDueBucket> myIterator = theTaxBuckets.iterator();
        while (myIterator.hasNext()) {
            MoneyWiseTaxDueBucket myBucket = myIterator.next();

            /* Add the tax */
            theTaxDue.addAmount(myBucket.getTaxDue());
        }
    }

    /**
     * Calculate the taxProfit.
     */
    protected void calculateTaxProfit() {
        /* Calculate the profit */
        theTaxProfit.setZero();
        theTaxProfit.addAmount(theTaxPaid);
        theTaxProfit.subtractAmount(theTaxPaid);
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
        if (FIELD_TAXCONFIG.equals(pField)) {
            return theTaxConfig;
        }
        if (FIELD_TAXBUCKETS.equals(pField)) {
            return theTaxBuckets;
        }
        if (FIELD_TAXDUE.equals(pField)) {
            return theTaxDue;
        }
        if (FIELD_TAXPAID.equals(pField)) {
            return theTaxPaid;
        }
        if (FIELD_TAXPROFIT.equals(pField)) {
            return theTaxProfit;
        }

        /* Not recognised */
        return MetisFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }
}
