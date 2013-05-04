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

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;
import net.sourceforge.jOceanus.jSortedList.OrderedListIterator;

/**
 * Capital Events relating to asset movements.
 * @author Tony Washer
 */
public final class CapitalEvent
        implements OrderedIdItem<Integer>, JDataContents, Comparable<CapitalEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEvent.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * Attribute Map.
     */
    private final Map<EventAttribute, JDecimal> theAttributes;

    /**
     * The event.
     */
    private final Event theEvent;

    /**
     * The Date of the event.
     */
    private final JDateDay theDate;

    /**
     * Date field id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField("Date");

    /**
     * Event Field id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, EventAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, EventAttribute.class);

    /**
     * Reverse FieldSet map.
     */
    // private static final Map<EventAttribute, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, EventAttribute.class);

    /**
     * Obtain the date.
     * @return the date.
     */
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain the event.
     * @return the event.
     */
    public Event getEvent() {
        return theEvent;
    }

    @Override
    public Integer getOrderedId() {
        /* This is the id of the event, or in the case where there is no event, the negative Date id */
        return (theEvent != null)
                ? theEvent.getId()
                : -theDate.getId();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_EVENT.equals(pField)) {
            return (theEvent == null)
                    ? JDataFieldValue.SkipField
                    : theEvent;
        }

        /* Handle Attribute fields */
        EventAttribute myClass = getClassForField(pField);
        if (myClass != null) {
            return getAttributeValue(myClass);
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Get an attribute value.
     * @param pAttr the attribute
     * @return the value to set
     */
    private Object getAttributeValue(final EventAttribute pAttr) {
        /* Access value of object */
        Object myValue = getAttribute(pAttr);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    private static EventAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Constructor.
     * @param pEvent the underlying event
     */
    private CapitalEvent(final Event pEvent) {
        /* Create the attributes map */
        theAttributes = new EnumMap<EventAttribute, JDecimal>(EventAttribute.class);

        /* Store the values */
        theDate = pEvent.getDate();
        theEvent = pEvent;
    }

    /**
     * Constructor.
     * @param pDate the date of the event
     */
    private CapitalEvent(final JDateDay pDate) {
        /* Create the attributes map */
        theAttributes = new EnumMap<EventAttribute, JDecimal>(EventAttribute.class);

        /* Store the values */
        theDate = pDate;
        theEvent = null;
    }

    @Override
    public int compareTo(final CapitalEvent pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the dates */
        int iResult = getDate().compareTo(pThat.getDate());
        if (iResult != 0) {
            return iResult;
        }

        /* Compare the underlying events */
        return Difference.compareObject(getEvent(), pThat.getEvent());
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

        /* Access as Capital Event */
        CapitalEvent myThat = (CapitalEvent) pThat;

        /* Check equality */
        return Difference.isEqual(getDate(), myThat.getDate())
               && Difference.isEqual(getEvent(), myThat.getEvent());
    }

    @Override
    public int hashCode() {
        int hash = getDate().hashCode();
        if (getEvent() != null) {
            hash ^= getEvent().hashCode();
        }
        return hash;
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final EventAttribute pAttr,
                                final JDecimal pValue) {
        /* Set the value into the list */
        theAttributes.put(pAttr, pValue);
    }

    /**
     * Obtain an attribute value.
     * @param <X> the data type
     * @param pAttr the attribute
     * @param pClass the class of the attribute
     * @return the value of the attribute or null
     */
    public <X extends JDecimal> X getAttribute(final EventAttribute pAttr,
                                               final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final EventAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * The List of capital events.
     */
    public static class CapitalEventList
            extends OrderedIdList<Integer, CapitalEvent>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEventList.class.getSimpleName());

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
         * The Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            if (FIELD_ACCOUNT.equals(pField)) {
                return theAccount;
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        /**
         * The DataSet.
         */
        private final FinanceData theData;

        /**
         * The Account.
         */
        private final Account theAccount;

        /**
         * Obtain the dataSet.
         * @return the data
         */
        public FinanceData getData() {
            return theData;
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty Capital event list.
         * @param pData the DataSet
         * @param pAccount the Account for the list
         */
        protected CapitalEventList(final FinanceData pData,
                                   final Account pAccount) {
            super(CapitalEvent.class);

            /* Store the data */
            theData = pData;
            theAccount = pAccount;
        }

        /**
         * Add an event to the list.
         * @param pEvent the Event to add
         * @return the Capital Event
         */
        protected CapitalEvent addEvent(final Event pEvent) {
            CapitalEvent myEvent;

            /* Create the Capital Event and add to list */
            myEvent = new CapitalEvent(pEvent);
            append(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Add a date event to the list.
         * @param pDate the Date for the event
         * @return the Capital Event
         */
        protected CapitalEvent addEvent(final JDateDay pDate) {
            CapitalEvent myEvent;

            /* Create the Capital Event and add to list */
            myEvent = new CapitalEvent(pDate);
            append(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Find the cash takeover event (if present).
         * @return the Capital Event
         */
        protected CapitalEvent getCashTakeOver() {
            /* Create the iterator */
            OrderedListIterator<CapitalEvent> myIterator = listIterator();

            /* Access the last element */
            CapitalEvent myEvent = myIterator.peekLast();

            /* If the element is a cash takeover */
            if ((myEvent != null)
                && (myEvent.getEvent() != null)) {
                // && (myEvent.getEvent().getCategoryClass() == EventCategoryClass.CashTakeOver)) TODO {
                return myEvent;
            }

            /* Return no such event */
            return null;
        }

        /**
         * Purge events after date.
         * @param pDate date from which to purge events
         */
        protected void purgeAfterDate(final JDateDay pDate) {
            /* Access the iterator */
            Iterator<CapitalEvent> myIterator = listIterator();

            /* Loop through the events */
            while (myIterator.hasNext()) {
                CapitalEvent myEvent = myIterator.next();
                /* If this is past (or on) the date remove it */
                if (pDate.compareTo(myEvent.getDate()) <= 0) {
                    myIterator.remove();
                }
            }
        }
    }

    /**
     * Capital Event Attributes.
     */
    public enum EventAttribute {
        /**
         * The Initial Cost Attribute.
         */
        InitialCost,

        /**
         * The Delta Cost Attribute.
         */
        DeltaCost,

        /**
         * The Final Cost Attribute.
         */
        FinalCost,

        /**
         * The Initial Units Attribute.
         */
        InitialUnits,

        /**
         * The Delta Units Attribute.
         */
        DeltaUnits,

        /**
         * The Final Units Attribute.
         */
        FinalUnits,

        /**
         * The Initial Gains Attribute.
         */
        InitialGains,

        /**
         * The Delta Gains Attribute.
         */
        DeltaGains,

        /**
         * The Final Gains Attribute.
         */
        FinalGains,

        /**
         * The Initial Gained Attribute.
         */
        InitialGained,

        /**
         * The Delta Gained Attribute.
         */
        DeltaGained,

        /**
         * The Final Gained Attribute.
         */
        FinalGained,

        /**
         * The Initial Dividend Attribute.
         */
        InitialDividend,

        /**
         * The Delta Dividend Attribute.
         */
        DeltaDividend,

        /**
         * The Final Dividend Attribute.
         */
        FinalDividend,

        /**
         * The Initial Invested Attribute.
         */
        InitialInvested,

        /**
         * The Delta Invested Attribute.
         */
        DeltaInvested,

        /**
         * The Final Invested Attribute.
         */
        FinalInvested,

        /**
         * The Initial Value Attribute.
         */
        InitialValue,

        /**
         * The Final Value Attribute.
         */
        FinalValue,

        /**
         * The Initial Price Attribute.
         */
        InitialPrice,

        /**
         * The Final Price Attribute.
         */
        FinalPrice,

        /**
         * The Market Movement Attribute.
         */
        MarketMovement,

        /**
         * The Takeover Cost Attribute.
         */
        TakeOverCost,

        /**
         * The Takeover Cash Attribute.
         */
        TakeOverCash,

        /**
         * The Takeover Stock Attribute.
         */
        TakeOverStock,

        /**
         * The Takeover Total Attribute.
         */
        TakeOverTotal,

        /**
         * The Takeover Price Attribute.
         */
        TakeOverPrice,

        /**
         * The Takeover Value Attrribute.
         */
        TakeoverValue;
    }
}
