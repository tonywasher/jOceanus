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
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventCategoryDetail;

/**
 * The Tax Bucket class.
 */
public abstract class TaxBucket
        extends AnalysisBucket {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(TaxBucket.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

    /**
     * Tax Type Field Id.
     */
    public static final JDataField FIELD_TAXCAT = FIELD_DEFS.declareEqualityField("TaxCategory");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_TAXCAT.equals(pField)) {
            return theTaxCategory;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Tax Category.
     */
    private final TaxCategory theTaxCategory;

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theTaxCategory.getName();
    }

    /**
     * Obtain tax category.
     * @return the category
     */
    public TaxCategory getTaxCategory() {
        return theTaxCategory;
    }

    /**
     * Constructor.
     * @param pTaxCategory the category
     */
    private TaxBucket(final TaxCategory pTaxCategory) {
        /* Call super-constructor */
        super(BucketType.getTaxBucketType(pTaxCategory), pTaxCategory.getId());

        /* Store the tax category */
        theTaxCategory = pTaxCategory;
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

        /* Compare the super-class */
        int result = super.compareTo(pThat);
        if (result != 0) {
            return result;
        }

        /* Access the object as an Tax Bucket */
        TaxBucket myThat = (TaxBucket) pThat;

        /* Compare the TaxCategories */
        return getTaxCategory().compareTo(myThat.getTaxCategory());
    }

    /**
     * The Transaction Summary Bucket class.
     */
    public static final class CategorySummary
            extends TaxBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CategorySummary.class.getSimpleName(), TaxBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * Previous Amount Field Id.
         */
        public static final JDataField FIELD_PREVAMOUNT = FIELD_DEFS.declareLocalField("PreviousAmount");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_PREVAMOUNT.equals(pField)) {
                return thePrevAmount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private JMoney theAmount = null;

        /**
         * The previous amount.
         */
        private JMoney thePrevAmount = null;

        @Override
        public CategorySummary getBase() {
            return (CategorySummary) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount.
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the previous amount.
         * @return the amount.
         */
        public JMoney getPrevAmount() {
            return thePrevAmount;
        }

        /**
         * Constructor.
         * @param pTaxCategory the tax category
         */
        protected CategorySummary(final TaxCategory pTaxCategory) {
            /* Call super-constructor */
            super(pTaxCategory);

            /* Initialise the Money values */
            theAmount = new JMoney();
            thePrevAmount = new JMoney();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value.
         * @param pBucket the bucket
         */
        protected void addValues(final EventCategoryDetail pBucket) {
            EventCategoryDetail myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.addAmount(pBucket.getAmount());
            theAmount.addAmount(pBucket.getTaxCredit());

            /* If there are previous totals and we have previous totals */
            if (myPrevious != null) {
                /* Add previous values */
                thePrevAmount.addAmount(myPrevious.getAmount());
                thePrevAmount.addAmount(myPrevious.getTaxCredit());
            }
        }

        /**
         * Subtract values from the total value.
         * @param pBucket the bucket
         */
        protected void subtractValues(final EventCategoryDetail pBucket) {
            EventCategoryDetail myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.subtractAmount(pBucket.getAmount());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null)
                && (getBase() != null)) {
                /* Add previous values */
                getBase().subtractValues(myPrevious);
            }
        }
    }

    /**
     * The Category Total Bucket class.
     */
    public static final class CategoryTotal
            extends TaxBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CategoryTotal.class.getSimpleName(), TaxBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * Previous Amount Field Id.
         */
        public static final JDataField FIELD_PREVAMOUNT = FIELD_DEFS.declareLocalField("PreviousAmount");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_PREVAMOUNT.equals(pField)) {
                return thePrevAmount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private JMoney theAmount = null;

        /**
         * The previous amount.
         */
        private JMoney thePrevAmount = null;

        @Override
        public CategoryTotal getBase() {
            return (CategoryTotal) super.getBase();
        }

        /**
         * Obtain amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain previous amount.
         * @return the amount
         */
        public JMoney getPrevAmount() {
            return thePrevAmount;
        }

        /**
         * Constructor.
         * @param pTaxCategory the tax category
         */
        protected CategoryTotal(final TaxCategory pTaxCategory) {
            /* Call super-constructor */
            super(pTaxCategory);

            /* Initialise the Money values */
            theAmount = new JMoney();
            thePrevAmount = new JMoney();
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            return true;
        }

        /**
         * Add values to the total value.
         * @param pBucket the bucket
         */
        protected void addValues(final CategorySummary pBucket) {
            CategorySummary myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.addAmount(pBucket.getAmount());

            /* If there are previous totals and we have previous totals */
            if (myPrevious != null) {
                /* Add previous values */
                thePrevAmount.addAmount(myPrevious.getAmount());
            }
        }

        /**
         * Subtract values from the total value.
         * @param pBucket the bucket
         */
        protected void subtractValues(final CategorySummary pBucket) {
            CategorySummary myPrevious = pBucket.getBase();

            /* Add the values */
            theAmount.subtractAmount(pBucket.getAmount());

            /* If there are previous totals and we have previous totals */
            if ((myPrevious != null)
                && (getBase() != null)) {
                /* Add previous values */
                getBase().subtractValues(myPrevious);
            }
        }
    }

    /**
     * The Taxation Detail Bucket class.
     */
    public static final class TaxDetail
            extends TaxBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(TaxDetail.class.getSimpleName(), TaxBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * Taxation Field Id.
         */
        public static final JDataField FIELD_TAXATION = FIELD_DEFS.declareLocalField("Taxation");

        /**
         * Rate Field Id.
         */
        public static final JDataField FIELD_RATE = FIELD_DEFS.declareLocalField("Rate");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_TAXATION.equals(pField)) {
                return theTaxation;
            }
            if (FIELD_RATE.equals(pField)) {
                return theRate;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private JMoney theAmount = null;

        /**
         * The taxation.
         */
        private JMoney theTaxation = null;

        /**
         * The rate.
         */
        private JRate theRate = null;

        /**
         * The parent.
         */
        private TaxDetail theParent = null;

        @Override
        public TaxDetail getBase() {
            return (TaxDetail) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the taxation.
         * @return the taxation
         */
        public JMoney getTaxation() {
            return theTaxation;
        }

        /**
         * Obtain the rate.
         * @return the rate
         */
        public JRate getRate() {
            return theRate;
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        public TaxDetail getParent() {
            return theParent;
        }

        /**
         * Constructor.
         * @param pTaxCategory the tax category
         */
        protected TaxDetail(final TaxCategory pTaxCategory) {
            /* Call super-constructor */
            super(pTaxCategory);
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || theTaxation.isNonZero());
        }

        /**
         * Set a taxation amount and calculate the tax on it.
         * @param pAmount Amount to set
         * @return the taxation on this bucket
         */
        protected JMoney setAmount(final JMoney pAmount) {
            /* Set the value */
            theAmount = new JMoney(pAmount);

            /* Calculate the tax if we have a rate */
            theTaxation = (theRate != null)
                    ? theAmount.valueAtRate(theRate)
                    : new JMoney();

            /* Return the taxation amount */
            return theTaxation;
        }

        /**
         * Set explicit taxation value.
         * @param pAmount Amount to set
         */
        protected void setTaxation(final JMoney pAmount) {
            /* Set the value */
            theTaxation = new JMoney(pAmount);
        }

        /**
         * Set parent bucket for reporting purposes.
         * @param pParent the parent bucket
         */
        protected void setParent(final TaxDetail pParent) {
            /* Set the value */
            theParent = pParent;
        }

        /**
         * Set a tax rate.
         * @param pRate Amount to set
         */
        protected void setRate(final JRate pRate) {
            /* Set the value */
            theRate = pRate;
        }
    }
}
