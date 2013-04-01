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
package net.sourceforge.jOceanus.jMoneyWise.views;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategorySection;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;

/**
 * AnalysisBucket Class.
 */
public abstract class AnalysisBucket
        implements OrderedIdItem<Integer>, JDataContents, Comparable<AnalysisBucket> {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(AnalysisBucket.class.getSimpleName());

    /**
     * Bucket type field id.
     */
    public static final JDataField FIELD_BUCKETTYPE = FIELD_DEFS.declareEqualityField("BucketType");

    /**
     * Id field id.
     */
    public static final JDataField FIELD_ID = FIELD_DEFS.declareLocalField("Id");

    /**
     * Base Field Id.
     */
    public static final JDataField FIELD_BASE = FIELD_DEFS.declareLocalField("Base");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_BUCKETTYPE.equals(pField)) {
            return theBucketType;
        }
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
        if (FIELD_BASE.equals(pField)) {
            return (theBase != null)
                    ? theBase
                    : JDataFieldValue.SkipField;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return theBucketType.toString();
    }

    /**
     * The bucket type.
     */
    private final BucketType theBucketType;

    /**
     * The base bucket.
     */
    private AnalysisBucket theBase;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Obtain the bucket type.
     * @return the type
     */
    public BucketType getBucketType() {
        return theBucketType;
    }

    /**
     * Obtain the base.
     * @return the base
     */
    protected AnalysisBucket getBase() {
        return theBase;
    }

    @Override
    public Integer getOrderedId() {
        /* This is the id of the event, or in the case where there is no event, the negative Date id */
        return theId;
    }

    /**
     * Constructor.
     * @param pType the bucket type
     * @param uId the id
     */
    public AnalysisBucket(final BucketType pType,
                          final int uId) {
        /* Store info */
        theId = uId
                + pType.getIdShift();
        theBase = null;
        theBucketType = pType;
    }

    /**
     * Constructor.
     * @param pBase the underlying bucket
     */
    public AnalysisBucket(final AnalysisBucket pBase) {
        /* Store info */
        theId = pBase.theId;
        theBase = pBase;
        theBucketType = pBase.theBucketType;
    }

    @Override
    public int compareTo(final AnalysisBucket pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the bucket order */
        return getBucketType().compareTo(pThat.getBucketType());
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

        /* Check class */
        if (getClass() != pThat.getClass()) {
            return false;
        }

        /* Access as AnalysisBucket */
        AnalysisBucket myThat = (AnalysisBucket) pThat;

        /* Check equality */
        return (getBucketType() == myThat.getBucketType())
               && (theId.equals(myThat.theId));
    }

    @Override
    public int hashCode() {
        return getBucketType().hashCode()
               ^ theId;
    }

    /**
     * is the bucket active?
     * @return TRUE/FALSE
     */
    public abstract boolean isActive();

    /**
     * is the bucket relevant (i.e. should it be reported)?
     * @return TRUE/FALSE
     */
    protected abstract boolean isRelevant();

    /**
     * Bucket Types.
     */
    public static enum BucketType {
        /**
         * Bank Detail.
         */
        BANKDETAIL(1000),

        /**
         * Asset Detail.
         */
        ASSETDETAIL(1000),

        /**
         * Loan Detail.
         */
        LOANDETAIL(1000),

        /**
         * Payee Detail.
         */
        PAYEEDETAIL(1000),

        /**
         * Asset Summary.
         */
        ASSETSUMMARY(100),

        /**
         * Asset Total.
         */
        ASSETTOTAL(1),

        /**
         * Market Total.
         */
        MARKETTOTAL(2),

        /**
         * Payee total.
         */
        PAYEETOTAL(3),

        /**
         * Category detail.
         */
        CATDETAIL(200),

        /**
         * Category Summary.
         */
        CATSUMMARY(300),

        /**
         * Category Total.
         */
        CATTOTAL(300),

        /**
         * Tax Detail.
         */
        TAXDETAIL(300),

        /**
         * Tax Summary.
         */
        TAXSUMMARY(300),

        /**
         * Tax Total.
         */
        TAXTOTAL(300);

        /**
         * The id shift.
         */
        private final int theShift;

        /**
         * Get the Id shift for this BucketType.
         * @return the id shift
         */
        protected int getIdShift() {
            return theShift;
        }

        /**
         * Constructor.
         * @param pShift he id shift
         */
        private BucketType(final int pShift) {
            theShift = pShift;
        }

        /**
         * Get the BucketType for this Account.
         * @param pAccount the account
         * @return the Bucket type
         */
        protected static BucketType getAccountBucketType(final Account pAccount) {
            /* If this is a external/benefit */
            if (pAccount.hasUnits()) {
                return ASSETDETAIL;
            } else if (pAccount.isLoan()) {
                return LOANDETAIL;
            } else if (pAccount.hasValue()) {
                return BANKDETAIL;
            } else {
                return PAYEEDETAIL;
            }
        }

        /**
         * Get the BucketType for this TaxCategory.
         * @param pTaxCategory the tax category
         * @return the id shift
         */
        protected static BucketType getTaxBucketType(final TaxCategory pTaxCategory) {
            TaxCategorySection mySection = pTaxCategory.getTaxClass().getClassSection();
            switch (mySection) {
                case CATTOTAL:
                    return CATTOTAL;
                case TAXDETAIL:
                    return TAXDETAIL;
                case TAXSUMM:
                    return TAXSUMMARY;
                case TAXTOTAL:
                    return TAXTOTAL;
                default:
                    return CATSUMMARY;
            }
        }
    }
}
