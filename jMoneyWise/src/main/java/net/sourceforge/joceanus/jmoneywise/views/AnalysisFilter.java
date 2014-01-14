/**
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisType;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.BucketValues;
import net.sourceforge.joceanus.jmoneywise.analysis.EventAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.EventCategoryBucket.CategoryValues;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PayeeBucket.PayeeValues;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.SecurityBucket.SecurityValues;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.TaxBasisBucket.TaxBasisValues;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventGroup;

/**
 * Analysis Filter Classes.
 * @param <T> the attribute for the filter
 */
public abstract class AnalysisFilter<T extends Enum<T> & BucketAttribute>
        implements JDataContents {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AnalysisFilter.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"));

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Bucket Field Id.
     */
    private static final JDataField FIELD_BUCKET = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataBucket"));

    /**
     * Attribute Field Id.
     */
    private static final JDataField FIELD_ATTR = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataAttr"));

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ATTR.equals(pField)) {
            return theAttr;
        }
        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    /**
     * The Current Attribute.
     */
    private T theAttr;

    /**
     * The Attribute class.
     */
    private final Class<T> theClass;

    /**
     * Set attribute.
     * @param pAttr the attribute
     */
    public void setCurrentAttribute(final BucketAttribute pAttr) {
        theAttr = theClass.cast(pAttr);
    }

    /**
     * Obtain current attribute.
     * @return the current attribute
     */
    public T getCurrentAttribute() {
        return theAttr;
    }

    /**
     * Get Analysis Type.
     * @return the Analysis Type
     */
    public abstract AnalysisType getAnalysisType();

    /**
     * Constructor.
     * @param pClass the attribute class
     */
    protected AnalysisFilter(final Class<T> pClass) {
        theClass = pClass;
    }

    /**
     * Should we filter this event out?
     * @param pEvent the event to check
     * @return true/false
     */
    public boolean filterEvent(final Event pEvent) {
        /* If this is a split event */
        if (pEvent.isSplit()) {
            /* Filter out children */
            if (pEvent.isChild()) {
                return true;
            }

            /* Access the group */
            EventList myList = (EventList) pEvent.getList();
            EventGroup<Event> myGroup = myList.getGroup(pEvent);

            /* Loop through the elements */
            Iterator<Event> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Event myEvent = myIterator.next();

                /* Check event */
                if (!filterSingleEvent(myEvent)) {
                    return false;
                }
            }

            /* Ignore Event Group */
            return true;
        }

        /* Check as a single event */
        return filterSingleEvent(pEvent);
    }

    /**
     * Should we filter this event out?
     * @param pEvent the event to check
     * @return true/false
     */
    private boolean filterSingleEvent(final Event pEvent) {
        /* Check whether this event is registered */
        return !pEvent.isHeader()
               && getValuesForEvent(pEvent) == null;
    }

    /**
     * Obtain base bucket values.
     * @return the value
     */
    protected abstract BucketValues<?, T> getBaseValues();

    /**
     * Obtain values for event.
     * @param pEvent the event
     * @return the values
     */
    public abstract BucketValues<?, T> getValuesForEvent(final Event pEvent);

    /**
     * Obtain delta value for event.
     * @param pEvent the event
     * @return the delta value
     */
    public abstract JDecimal getDeltaForEvent(final Event pEvent);

    /**
     * Obtain starting value for attribute.
     * @return the value
     */
    public JDecimal getStartingBalance() {
        BucketValues<?, T> myValues = getBaseValues();
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JDecimal getBalanceForEvent(final Event pEvent) {
        /* If this is a split event */
        if (pEvent.isSplit()) {
            /* Access the group */
            EventList myList = (EventList) pEvent.getList();
            EventGroup<Event> myGroup = myList.getGroup(pEvent);

            /* Initialise return value */
            JDecimal myBalance = null;

            /* Loop through the elements */
            Iterator<Event> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Event myEvent = myIterator.next();

                /* Access Balance for event */
                JDecimal myValue = getSingleBalanceForEvent(myEvent);
                if (myValue != null) {
                    /* Record as value */
                    myBalance = myValue;
                }
            }

            /* Return the balance */
            return myBalance;
        }

        /* Obtain single event value */
        return getSingleBalanceForEvent(pEvent);
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JDecimal getDebitForEvent(final Event pEvent) {
        JDecimal myValue = getDeltaValueForEvent(pEvent);
        if (myValue != null) {
            if (myValue.isPositive()
                || myValue.isZero()) {
                myValue = null;
            } else {
                myValue.negate();
            }
        }
        return (myValue != null)
                ? myValue
                : null;
    }

    /**
     * Obtain delta credit value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JDecimal getCreditForEvent(final Event pEvent) {
        JDecimal myValue = getDeltaValueForEvent(pEvent);
        return ((myValue != null)
                && myValue.isPositive() && myValue.isNonZero())
                ? myValue
                : null;
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    private JDecimal getDeltaValueForEvent(final Event pEvent) {
        /* If this is a split event */
        if (pEvent.isSplit()) {
            /* Access the group */
            EventList myList = (EventList) pEvent.getList();
            EventGroup<Event> myGroup = myList.getGroup(pEvent);

            /* Initialise return value */
            JDecimal myTotal = null;

            /* Loop through the elements */
            Iterator<Event> myIterator = myGroup.iterator();
            while (myIterator.hasNext()) {
                Event myEvent = myIterator.next();

                /* Access Delta for event */
                JDecimal myDelta = getDeltaForEvent(myEvent);
                if (myDelta != null) {
                    /* If this is the first value */
                    if (myTotal == null) {
                        /* Record as value */
                        myTotal = myDelta;

                        /* else need to add values */
                    } else {
                        /* add values appropriately */
                        myTotal = addDecimals(myTotal, myDelta);
                    }
                }
            }

            /* Return the total */
            return myTotal;
        }

        /* Obtain single event value */
        return getDeltaForEvent(pEvent);
    }

    /**
     * Add decimal values.
     * @param pFirst the first decimal
     * @param pSecond the second decimal
     * @return the sum
     */
    private JDecimal addDecimals(final JDecimal pFirst,
                                 final JDecimal pSecond) {
        switch (theAttr.getDataType()) {
            case MONEY:
                ((JMoney) pFirst).addAmount((JMoney) pSecond);
                return pFirst;
            case UNITS:
                ((JUnits) pFirst).addUnits((JUnits) pSecond);
                return pFirst;
            default:
                return null;
        }
    }

    /**
     * Obtain total money value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    private JDecimal getSingleBalanceForEvent(final Event pEvent) {
        BucketValues<?, T> myValues = getValuesForEvent(pEvent);
        return (myValues == null)
                ? null
                : myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain analysis name.
     * @return the name
     */
    public abstract String getName();

    /**
     * Account Bucket filter class.
     */
    public static class AccountFilter
            extends AnalysisFilter<AccountAttribute> {
        /**
         * The account bucket.
         */
        private final AccountBucket theAccount;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theAccount;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public AccountBucket getBucket() {
            return theAccount;
        }

        @Override
        public String getName() {
            return theAccount.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.ACCOUNT;
        }

        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        public AccountFilter(final AccountBucket pAccount) {
            /* Store parameter */
            super(AccountAttribute.class);
            theAccount = pAccount;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected AccountValues getBaseValues() {
            return theAccount.getBaseValues();
        }

        @Override
        public AccountValues getValuesForEvent(final Event pEvent) {
            return theAccount.getValuesForEvent(pEvent);
        }

        @Override
        public JDecimal getDeltaForEvent(final Event pEvent) {
            return theAccount.getDeltaForEvent(pEvent, getCurrentAttribute());
        }
    }

    /**
     * Security Bucket filter class.
     */
    public static class SecurityFilter
            extends AnalysisFilter<SecurityAttribute> {
        /**
         * The security bucket.
         */
        private final SecurityBucket theSecurity;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theSecurity;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public SecurityBucket getBucket() {
            return theSecurity;
        }

        @Override
        public String getName() {
            return theSecurity.getDecoratedName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.SECURITY;
        }

        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public SecurityFilter(final SecurityBucket pSecurity) {
            /* Store parameter */
            super(SecurityAttribute.class);
            theSecurity = pSecurity;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected SecurityValues getBaseValues() {
            return theSecurity.getBaseValues();
        }

        @Override
        public SecurityValues getValuesForEvent(final Event pEvent) {
            return theSecurity.getValuesForEvent(pEvent);
        }

        @Override
        public JDecimal getDeltaForEvent(final Event pEvent) {
            return theSecurity.getDeltaForEvent(pEvent, getCurrentAttribute());
        }
    }

    /**
     * Payee Bucket filter class.
     */
    public static class PayeeFilter
            extends AnalysisFilter<PayeeAttribute> {
        /**
         * The payee bucket.
         */
        private final PayeeBucket thePayee;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return thePayee;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public PayeeBucket getBucket() {
            return thePayee;
        }

        @Override
        public String getName() {
            return thePayee.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.PAYEE;
        }

        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public PayeeFilter(final PayeeBucket pPayee) {
            /* Store parameter */
            super(PayeeAttribute.class);
            thePayee = pPayee;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected PayeeValues getBaseValues() {
            return thePayee.getBaseValues();
        }

        @Override
        public PayeeValues getValuesForEvent(final Event pEvent) {
            return thePayee.getValuesForEvent(pEvent);
        }

        @Override
        public JDecimal getDeltaForEvent(final Event pEvent) {
            return thePayee.getDeltaForEvent(pEvent, getCurrentAttribute());
        }
    }

    /**
     * EventCategory Bucket filter class.
     */
    public static class EventCategoryFilter
            extends AnalysisFilter<EventAttribute> {
        /**
         * The event category bucket.
         */
        private final EventCategoryBucket theCategory;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theCategory;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public EventCategoryBucket getBucket() {
            return theCategory;
        }

        @Override
        public String getName() {
            return theCategory.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.CATEGORY;
        }

        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public EventCategoryFilter(final EventCategoryBucket pCategory) {
            /* Store parameter */
            super(EventAttribute.class);
            theCategory = pCategory;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected CategoryValues getBaseValues() {
            return theCategory.getBaseValues();
        }

        @Override
        public CategoryValues getValuesForEvent(final Event pEvent) {
            return theCategory.getValuesForEvent(pEvent);
        }

        @Override
        public JDecimal getDeltaForEvent(final Event pEvent) {
            return theCategory.getDeltaForEvent(pEvent, getCurrentAttribute());
        }
    }

    /**
     * TaxBasis Bucket filter class.
     */
    public static class TaxBasisFilter
            extends AnalysisFilter<TaxBasisAttribute> {
        /**
         * The payee bucket.
         */
        private final TaxBasisBucket theTaxBasis;

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_BUCKET.equals(pField)) {
                return theTaxBasis;
            }
            /* Unknown */
            return super.getFieldValue(pField);
        }

        /**
         * Obtain bucket.
         * @return theBucket
         */
        public TaxBasisBucket getBucket() {
            return theTaxBasis;
        }

        @Override
        public String getName() {
            return theTaxBasis.getName();
        }

        @Override
        public AnalysisType getAnalysisType() {
            return AnalysisType.TAXBASIS;
        }

        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public TaxBasisFilter(final TaxBasisBucket pTaxBasis) {
            /* Store parameter */
            super(TaxBasisAttribute.class);
            theTaxBasis = pTaxBasis;
            setCurrentAttribute(getAnalysisType().getDefaultValue());
        }

        @Override
        protected TaxBasisValues getBaseValues() {
            return theTaxBasis.getBaseValues();
        }

        @Override
        public TaxBasisValues getValuesForEvent(final Event pEvent) {
            return theTaxBasis.getValuesForEvent(pEvent);
        }

        @Override
        public JDecimal getDeltaForEvent(final Event pEvent) {
            return theTaxBasis.getDeltaForEvent(pEvent, getCurrentAttribute());
        }
    }
}
