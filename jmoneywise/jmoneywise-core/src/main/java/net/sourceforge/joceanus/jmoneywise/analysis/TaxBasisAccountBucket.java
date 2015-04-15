/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/jmoneywise-core/src/main/java/net/sourceforge/joceanus/jmoneywise/analysis/TaxBasisBucket.java $
 * $Revision: 595 $
 * $Author: Tony $
 * $Date: 2015-04-14 13:17:38 +0100 (Tue, 14 Apr 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.list.OrderedIdList;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataResource;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxBasis;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;

/**
 * The TaxBasis Account Bucket class.
 */
public final class TaxBasisAccountBucket
        extends TaxBasisBucket {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.TAXBASIS_ACCOUNTNAME.getValue(), TaxBasisBucket.FIELD_DEFS);

    /**
     * Parent Field Id.
     */
    private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField(MoneyWiseDataResource.TRANSACTION_ACCOUNT.getValue());

    /**
     * Account.
     */
    private final TransactionAsset theAccount;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pTaxBasis the basis bucket
     * @param pAccount the account
     */
    private TaxBasisAccountBucket(final Analysis pAnalysis,
                                  final TaxBasis pTaxBasis,
                                  final TransactionAsset pAccount) {
        /* Store the parameters */
        super(pAnalysis, pTaxBasis);
        theAccount = pAccount;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    private TaxBasisAccountBucket(final Analysis pAnalysis,
                                  final TaxBasisAccountBucket pBase,
                                  final JDateDay pDate) {
        /* Copy details from base */
        super(pAnalysis, pBase, pDate);
        theAccount = pBase.getAccount();
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    private TaxBasisAccountBucket(final Analysis pAnalysis,
                                  final TaxBasisAccountBucket pBase,
                                  final JDateDayRange pRange) {
        /* Copy details from base */
        super(pAnalysis, pBase, pRange);
        theAccount = pBase.getAccount();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        return super.getFieldValue(pField);
    }

    @Override
    public Integer getOrderedId() {
        return theAccount.getId();
    }

    @Override
    public String getName() {
        return theAccount.getName();
    }

    /**
     * Obtain account.
     * @return the account
     */
    public TransactionAsset getAccount() {
        return theAccount;
    }

    @Override
    public int compareTo(final TaxBasisBucket pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare types of buckets */
        int iDiff = super.compareTo(pThat);
        if ((iDiff == 0)
            && (pThat instanceof TaxBasisAccountBucket)) {
            /* Compare the Accounts */
            TaxBasisAccountBucket myThat = (TaxBasisAccountBucket) pThat;
            iDiff = getAccount().compareTo(myThat.getAccount());
        }

        /* Return the result */
        return iDiff;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }
        if (!(pThat instanceof TaxBasisAccountBucket)) {
            return false;
        }

        /* Compare the Accounts */
        TaxBasisAccountBucket myThat = (TaxBasisAccountBucket) pThat;
        if (!getAccount().equals(myThat.getAccount())) {
            return false;
        }

        /* Compare the tax bases */
        return super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return getAccount().hashCode();
    }

    /**
     * TaxBasisAccountBucketList class.
     */
    public static class TaxBasisAccountBucketList
            extends OrderedIdList<Integer, TaxBasisAccountBucket>
            implements JDataContents {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.TAXBASIS_ACCOUNTLIST.getValue());

        /**
         * Size Field Id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATALIST_SIZE.getValue());

        /**
         * Analysis field Id.
         */
        private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

        /**
         * Tax Basis Field Id.
         */
        private static final JDataField FIELD_TAXBASIS = FIELD_DEFS.declareEqualityField(MoneyWiseDataType.TAXBASIS.getItemName());

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * Tax Basis.
         */
        private final TaxBasis theTaxBasis;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         * @param pTaxBasis the tax basis
         */
        protected TaxBasisAccountBucketList(final Analysis pAnalysis,
                                            final TaxBasis pTaxBasis) {
            super(TaxBasisAccountBucket.class);
            theAnalysis = pAnalysis;
            theTaxBasis = pTaxBasis;
        }

        /**
         * Construct a dated List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pDate the Date
         */
        protected TaxBasisAccountBucketList(final Analysis pAnalysis,
                                            final TaxBasisAccountBucketList pBase,
                                            final JDateDay pDate) {
            /* Initialise class */
            super(TaxBasisAccountBucket.class);
            theAnalysis = pAnalysis;
            theTaxBasis = pBase.getTaxBasis();

            /* Loop through the buckets */
            Iterator<TaxBasisAccountBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this date */
                TaxBasisAccountBucket myBucket = new TaxBasisAccountBucket(pAnalysis, myCurr, pDate);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Calculate the delta and add to the list */
                    add(myBucket);
                }
            }
        }

        /**
         * Construct a ranged List.
         * @param pAnalysis the analysis
         * @param pBase the base list
         * @param pRange the Date Range
         */
        protected TaxBasisAccountBucketList(final Analysis pAnalysis,
                                            final TaxBasisAccountBucketList pBase,
                                            final JDateDayRange pRange) {
            /* Initialise class */
            super(TaxBasisAccountBucket.class);
            theAnalysis = pAnalysis;
            theTaxBasis = pBase.getTaxBasis();

            /* Loop through the buckets */
            Iterator<TaxBasisAccountBucket> myIterator = pBase.listIterator();
            while (myIterator.hasNext()) {
                TaxBasisAccountBucket myCurr = myIterator.next();

                /* Access the bucket for this range */
                TaxBasisAccountBucket myBucket = new TaxBasisAccountBucket(pAnalysis, myCurr, pRange);

                /* If the bucket is non-idle */
                if (!myBucket.isIdle()) {
                    /* Adjust to the base */
                    myBucket.adjustToBase();
                    add(myBucket);
                }
            }
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName() + "(" + size() + ")";
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            if (FIELD_TAXBASIS.equals(pField)) {
                return theTaxBasis;
            }
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain tax basis.
         * @return the basis
         */
        private TaxBasis getTaxBasis() {
            return theTaxBasis;
        }

        /**
         * Obtain the TaxBasisAccountBucket for a given account.
         * @param pAccount the account
         * @return the bucket
         */
        protected TaxBasisAccountBucket getBucket(final TransactionAsset pAccount) {
            /* Locate the bucket in the list */
            TaxBasisAccountBucket myItem = findItemById(pAccount.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxBasisAccountBucket(theAnalysis, theTaxBasis, pAccount);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }
    }
}
