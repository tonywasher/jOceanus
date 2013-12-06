/**
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.AccountBucket.AccountValues;
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
public abstract class AnalysisFilter<T extends Enum<T> & BucketAttribute> {
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
    protected boolean filterSingleEvent(final Event pEvent) {
        /* Check whether this event is registered */
        return getValuesForEvent(pEvent) == null;
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
        BucketValues<?, T> myValues = getValuesForEvent(pEvent);
        return myValues.getDecimalValue(getCurrentAttribute());
    }

    /**
     * Obtain delta debit value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JDecimal getDebitForEvent(final Event pEvent) {
        JDecimal myValue = getDeltaForEvent(pEvent);
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
        JDecimal myValue = getDeltaForEvent(pEvent);
        return (myValue != null)
               && myValue.isPositive()
                ? myValue
                : null;
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
        public String getName() {
            return theAccount.getName();
        }

        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        public AccountFilter(final AccountBucket pAccount) {
            /* Store parameter */
            super(AccountAttribute.class);
            theAccount = pAccount;
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
        public String getName() {
            return theSecurity.getDecoratedName();
        }

        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public SecurityFilter(final SecurityBucket pSecurity) {
            /* Store parameter */
            super(SecurityAttribute.class);
            theSecurity = pSecurity;
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
        public String getName() {
            return thePayee.getName();
        }

        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public PayeeFilter(final PayeeBucket pPayee) {
            /* Store parameter */
            super(PayeeAttribute.class);
            thePayee = pPayee;
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
        public String getName() {
            return theCategory.getName();
        }

        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public EventCategoryFilter(final EventCategoryBucket pCategory) {
            /* Store parameter */
            super(EventAttribute.class);
            theCategory = pCategory;
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
        public String getName() {
            return theTaxBasis.getName();
        }

        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public TaxBasisFilter(final TaxBasisBucket pTaxBasis) {
            /* Store parameter */
            super(TaxBasisAttribute.class);
            theTaxBasis = pTaxBasis;
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
