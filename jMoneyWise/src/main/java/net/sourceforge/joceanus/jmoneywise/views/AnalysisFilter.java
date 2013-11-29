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

import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
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
     * Set current attribute.
     * @param pAttr the current attribute
     */
    public void setCurrentAttribute(final T pAttr) {
        theAttr = pAttr;
    }

    /**
     * Obtain current attribute.
     * @return the current attribute
     */
    public T getCurrentAttribute() {
        return theAttr;
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
        return getValuesForEvent(pEvent) != null;
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
     * Obtain delta values for event.
     * @param pEvent the event
     * @return the delta values
     */
    public abstract BucketValues<?, T> getDeltaForEvent(final Event pEvent);

    /**
     * Obtain starting money value for attribute.
     * @return the value
     */
    public JMoney getStartingMoney() {
        BucketValues<?, T> myValues = getBaseValues();
        return myValues.getMoneyValue(getCurrentAttribute());
    }

    /**
     * Obtain total money value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JMoney getTotalMoney(final Event pEvent) {
        BucketValues<?, T> myValues = getValuesForEvent(pEvent);
        return myValues.getMoneyValue(getCurrentAttribute());
    }

    /**
     * Obtain delta money value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JMoney getDeltaMoney(final Event pEvent) {
        BucketValues<?, T> myValues = getDeltaForEvent(pEvent);
        return myValues.getMoneyValue(getCurrentAttribute());
    }

    /**
     * Obtain starting units value for attribute.
     * @return the value
     */
    public JUnits getStartingUnits() {
        BucketValues<?, T> myValues = getBaseValues();
        return myValues.getUnitsValue(getCurrentAttribute());
    }

    /**
     * Obtain total units value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JUnits getTotalUnits(final Event pEvent) {
        BucketValues<?, T> myValues = getValuesForEvent(pEvent);
        return myValues.getUnitsValue(getCurrentAttribute());
    }

    /**
     * Obtain delta units value for attribute.
     * @param pEvent the event to check
     * @return the value
     */
    public JUnits getDeltaUnits(final Event pEvent) {
        BucketValues<?, T> myValues = getDeltaForEvent(pEvent);
        return myValues.getUnitsValue(getCurrentAttribute());
    }

    /**
     * Account Bucket filter class.
     */
    public static class AccountFilter
            extends AnalysisFilter<AccountAttribute> {
        /**
         * The account bucket.
         */
        private final AccountBucket theAccount;

        /**
         * Constructor.
         * @param pAccount the account bucket
         */
        public AccountFilter(final AccountBucket pAccount) {
            /* Store parameter */
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
        public AccountValues getDeltaForEvent(final Event pEvent) {
            return theAccount.getDeltaForEvent(pEvent);
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

        /**
         * Constructor.
         * @param pSecurity the security bucket
         */
        public SecurityFilter(final SecurityBucket pSecurity) {
            /* Store parameter */
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
        public SecurityValues getDeltaForEvent(final Event pEvent) {
            return theSecurity.getDeltaForEvent(pEvent);
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

        /**
         * Constructor.
         * @param pPayee the payee bucket
         */
        public PayeeFilter(final PayeeBucket pPayee) {
            /* Store parameter */
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
        public PayeeValues getDeltaForEvent(final Event pEvent) {
            return thePayee.getDeltaForEvent(pEvent);
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

        /**
         * Constructor.
         * @param pCategory the category bucket
         */
        public EventCategoryFilter(final EventCategoryBucket pCategory) {
            /* Store parameter */
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
        public CategoryValues getDeltaForEvent(final Event pEvent) {
            return theCategory.getDeltaForEvent(pEvent);
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

        /**
         * Constructor.
         * @param pTaxBasis the taxBasis bucket
         */
        public TaxBasisFilter(final TaxBasisBucket pTaxBasis) {
            /* Store parameter */
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
        public TaxBasisValues getDeltaForEvent(final Event pEvent) {
            return theTaxBasis.getDeltaForEvent(pEvent);
        }
    }
}
