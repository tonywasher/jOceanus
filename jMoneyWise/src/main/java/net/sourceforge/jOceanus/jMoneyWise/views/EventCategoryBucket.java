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
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;

/**
 * The CategoryType Bucket class.
 */
public abstract class EventCategoryBucket
        extends AnalysisBucket {
    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryBucket.class.getSimpleName(), AnalysisBucket.FIELD_DEFS);

    /**
     * CategoryType Field Id.
     */
    public static final JDataField FIELD_CATTYPE = FIELD_DEFS.declareEqualityField("CategoryType");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_CATTYPE.equals(pField)) {
            return theCategoryType;
        }
        return super.getFieldValue(pField);
    }

    /**
     * The Category Type.
     */
    private final EventCategoryType theCategoryType;

    /**
     * Obtain name.
     * @return the name
     */
    public String getName() {
        return theCategoryType.getName();
    }

    /**
     * Obtain category type.
     * @return the type
     */
    public EventCategoryType getCategoryType() {
        return theCategoryType;
    }

    /**
     * Constructor.
     * @param pCategoryType the type
     */
    private EventCategoryBucket(final EventCategoryType pCategoryType) {
        /* Call super-constructor */
        super(BucketType.CATDETAIL, pCategoryType.getId());

        /* Store the category type */
        theCategoryType = pCategoryType;
    }

    /**
     * Constructor.
     * @param pBase the underlying bucket
     */
    private EventCategoryBucket(final EventCategoryBucket pBase) {
        /* Call super-constructor */
        super(pBase);

        /* Store the transaction type */
        theCategoryType = pBase.theCategoryType;
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

        /* Access the object as an CategoryType Bucket */
        EventCategoryBucket myThat = (EventCategoryBucket) pThat;

        /* Compare the CategoryTypes */
        return getCategoryType().compareTo(myThat.getCategoryType());
    }

    /**
     * The EventCategory Detail Bucket class.
     */
    public static final class EventCategoryDetail
            extends EventCategoryBucket {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(EventCategoryDetail.class.getSimpleName(), EventCategoryBucket.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Amount Field Id.
         */
        public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareLocalField("Amount");

        /**
         * TaxCredit Field Id.
         */
        public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareLocalField("TaxCredit");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_AMOUNT.equals(pField)) {
                return theAmount;
            }
            if (FIELD_TAXCREDIT.equals(pField)) {
                return theTaxCredit;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The amount.
         */
        private JMoney theAmount = null;

        /**
         * The tax credit.
         */
        private JMoney theTaxCredit = null;

        @Override
        public EventCategoryDetail getBase() {
            return (EventCategoryDetail) super.getBase();
        }

        /**
         * Obtain the amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return theAmount;
        }

        /**
         * Obtain the tax credit.
         * @return the tax credit
         */
        public JMoney getTaxCredit() {
            return theTaxCredit;
        }

        /**
         * Obtain the previous amount.
         * @return the amount
         */
        public JMoney getPrevAmount() {
            return (getBase() != null)
                    ? getBase().getAmount()
                    : null;
        }

        /**
         * Obtain the previous tax credit.
         * @return the tax credit
         */
        public JMoney getPrevTax() {
            return (getBase() != null)
                    ? getBase().getTaxCredit()
                    : null;
        }

        /**
         * Constructor.
         * @param pCategoryType the category type
         */
        protected EventCategoryDetail(final EventCategoryType pCategoryType) {
            /* Call super-constructor */
            super(pCategoryType);

            /* Initialise the Money values */
            theAmount = new JMoney();
            theTaxCredit = new JMoney();
        }

        /**
         * Constructor.
         * @param pBase the underlying bucket
         */
        protected EventCategoryDetail(final EventCategoryDetail pBase) {
            /* Call super-constructor */
            super(pBase.cloneIt());

            /* Initialise the Money values */
            theAmount = new JMoney();
            theTaxCredit = new JMoney();
        }

        /**
         * Create a clone of the Category Detail.
         * @return the cloned CategoryDetail.
         */
        private EventCategoryDetail cloneIt() {
            /* Create clone */
            EventCategoryDetail myClone = new EventCategoryDetail(getCategoryType());

            /* Copy the External values */
            myClone.theAmount = new JMoney(theAmount);
            myClone.theTaxCredit = new JMoney(theTaxCredit);

            /* Return the clone */
            return myClone;
        }

        @Override
        public boolean isActive() {
            /* Copy if the amount is non-zero */
            return theAmount.isNonZero();
        }

        @Override
        protected boolean isRelevant() {
            /* Relevant if this value or the previous value is non-zero */
            return (theAmount.isNonZero() || ((getPrevAmount() != null) && (getPrevAmount().isNonZero())));
        }

        /**
         * Adjust account for transaction.
         * @param pEvent the source event
         */
        protected void adjustAmount(final Event pEvent) {
            /* Adjust for transaction */
            theAmount.addAmount(pEvent.getAmount());

            /* Adjust for tax credit */
            if (pEvent.getTaxCredit() != null) {
                theTaxCredit.addAmount(pEvent.getTaxCredit());
            }
        }

        /**
         * Adjust account for tax credit.
         * @param pEvent the source event
         */
        protected void adjustForTaxCredit(final Event pEvent) {
            /* Adjust for tax credit */
            theAmount.addAmount(pEvent.getTaxCredit());
        }
    }
}
