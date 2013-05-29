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

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.views.EventCategoryBucket.EventAttribute;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * The Tax Bucket class.
 */
public final class TaxCategoryBucket
        extends AnalysisBucket {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(TaxCategoryBucket.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

    /**
     * Tax Type Field Id.
     */
    public static final JDataField FIELD_TAXCAT = FIELD_DEFS.declareEqualityField("TaxCategory");

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

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareLocalField("Parent");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_TAXCAT.equals(pField)) {
            return theTaxCategory;
        }
        if (FIELD_AMOUNT.equals(pField)) {
            return theAmount;
        }
        if (FIELD_TAXATION.equals(pField)) {
            return theTaxation;
        }
        if (FIELD_RATE.equals(pField)) {
            return theRate;
        }
        if (FIELD_PARENT.equals(pField)) {
            return (theParent != null)
                    ? theParent
                    : JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

    /**
     * Tax Category.
     */
    private final TaxCategory theTaxCategory;

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
    private TaxCategoryBucket theParent = null;

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
    public TaxCategoryBucket getParent() {
        return theParent;
    }

    /**
     * Constructor.
     * @param pTaxCategory the category
     */
    private TaxCategoryBucket(final TaxCategory pTaxCategory) {
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
        TaxCategoryBucket myThat = (TaxCategoryBucket) pThat;

        /* Compare the TaxCategories */
        return getTaxCategory().compareTo(myThat.getTaxCategory());
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
    protected void setParent(final TaxCategoryBucket pParent) {
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

    /**
     * Add values.
     * @param pBucket event category bucket
     */
    protected void addValues(final EventCategoryBucket pBucket) {
        /* Set the value */
        theAmount.addAmount(pBucket.getAttribute(EventAttribute.Income, JMoney.class));
    }

    /**
     * Subtract values.
     * @param pBucket event category bucket
     */
    protected void subtractValues(final EventCategoryBucket pBucket) {
        /* Set the value */
        theAmount.subtractAmount(pBucket.getAttribute(EventAttribute.Income, JMoney.class));
    }

    /**
     * TaxCategoryBucketList class.
     */
    public static class TaxCategoryBucketList
            extends OrderedIdList<Integer, TaxCategoryBucket>
            implements JDataContents {

        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxCategoryBucketList.class.getSimpleName());

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName()
                   + "("
                   + size()
                   + ")";
        }

        /**
         * Size Field Id.
         */
        public static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Analysis field Id.
         */
        public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ANALYSIS.equals(pField)) {
                return theAnalysis;
            }
            return JDataFieldValue.UnknownField;
        }

        /**
         * The analysis.
         */
        private final Analysis theAnalysis;

        /**
         * The data.
         */
        private final FinanceData theData;

        /**
         * Construct a top-level List.
         * @param pAnalysis the analysis
         */
        public TaxCategoryBucketList(final Analysis pAnalysis) {
            super(TaxCategoryBucket.class);
            theAnalysis = pAnalysis;
            theData = theAnalysis.getData();
        }

        /**
         * Obtain the EventCategoryBucket for a given event category class.
         * @param pClass the event category class
         * @return the bucket
         */
        protected TaxCategoryBucket getBucket(final TaxCategoryClass pClass) {
            /* Locate the bucket in the list */
            TaxCategory myCategory = theData.getTaxCategories().findItemByClass(pClass);
            TaxCategoryBucket myItem = findItemById(myCategory.getId());

            /* If the item does not yet exist */
            if (myItem == null) {
                /* Create the new bucket */
                myItem = new TaxCategoryBucket(myCategory);

                /* Add to the list */
                add(myItem);
            }

            /* Return the bucket */
            return myItem;
        }

        /**
         * Prune the list to remove irrelevant items.
         */
        protected void prune() {
            /* Access the iterator */
            Iterator<TaxCategoryBucket> myIterator = listIterator();

            /* Loop through the buckets */
            while (myIterator.hasNext()) {
                TaxCategoryBucket myCurr = myIterator.next();

                /* Remove the bucket if it is irrelevant */
                if (!myCurr.isRelevant()) {
                    myIterator.remove();
                }
            }
        }
    }
}
