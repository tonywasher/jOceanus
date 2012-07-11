/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.StaticClass.TransClass;
import net.sourceforge.JSortedList.OrderedIdItem;
import net.sourceforge.JSortedList.OrderedIdList;
import net.sourceforge.JSortedList.OrderedListIterator;

/**
 * Capital Events relating to asset movements.
 * @author Tony Washer
 */
public final class CapitalEvent implements OrderedIdItem<Integer>, JDataContents, Comparable<CapitalEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEvent.class.getSimpleName());

    /**
     * Report fields.
     */
    private final JDataFields theLocalFields;

    @Override
    public JDataFields getDataFields() {
        return theLocalFields;
    }

    @Override
    public String formatObject() {
        return getDataFields().getName();
    }

    /**
     * Declare local data fields.
     * @return the local fields
     */
    public static JDataFields declareFields() {
        return new JDataFields(FIELD_DEFS.getName(), FIELD_DEFS);
    }

    /**
     * The Initial Cost Attribute.
     */
    public static final String CAPITAL_INITIALCOST = "CostInitial";

    /**
     * The Delta Cost Attribute.
     */
    public static final String CAPITAL_DELTACOST = "CostDelta";

    /**
     * The Final Cost Attribute.
     */
    public static final String CAPITAL_FINALCOST = "CostFinal";

    /**
     * The Initial Units Attribute.
     */
    public static final String CAPITAL_INITIALUNITS = "UnitsInitial";

    /**
     * The Delta Units Attribute.
     */
    public static final String CAPITAL_DELTAUNITS = "UnitsDelta";

    /**
     * The Final Units Attribute.
     */
    public static final String CAPITAL_FINALUNITS = "UnitsFinal";

    /**
     * The Initial Gains Attribute.
     */
    public static final String CAPITAL_INITIALGAINS = "GainsInitial";

    /**
     * The Delta Gains Attribute.
     */
    public static final String CAPITAL_DELTAGAINS = "GainsDelta";

    /**
     * The Final Gains Attribute.
     */
    public static final String CAPITAL_FINALGAINS = "GainsFinal";

    /**
     * The Initial Gained Attribute.
     */
    public static final String CAPITAL_INITIALGAINED = "GainedInitial";

    /**
     * The Delta Gained Attribute.
     */
    public static final String CAPITAL_DELTAGAINED = "GainedDelta";

    /**
     * The Final Gained Attribute.
     */
    public static final String CAPITAL_FINALGAINED = "GainedFinal";

    /**
     * The Initial Dividend Attribute.
     */
    public static final String CAPITAL_INITIALDIVIDEND = "DividendInitial";

    /**
     * The Delta Dividend Attribute.
     */
    public static final String CAPITAL_DELTADIVIDEND = "DividendDelta";

    /**
     * The Final Dividend Attribute.
     */
    public static final String CAPITAL_FINALDIVIDEND = "DividendFinal";

    /**
     * The Initial Invest Attribute.
     */
    public static final String CAPITAL_INITIALINVEST = "InvestedInitial";

    /**
     * The Delta Invest Attribute.
     */
    public static final String CAPITAL_DELTAINVEST = "InvestedDelta";

    /**
     * The Final Invest Attribute.
     */
    public static final String CAPITAL_FINALINVEST = "InvestedFinal";

    /**
     * The Initial Value Attribute.
     */
    public static final String CAPITAL_INITIALVALUE = "ValueInitial";

    /**
     * The Final Value Attribute.
     */
    public static final String CAPITAL_FINALVALUE = "ValueFinal";

    /**
     * The Initial Price Attribute.
     */
    public static final String CAPITAL_INITIALPRICE = "PriceInitial";

    /**
     * The Final Price Attribute.
     */
    public static final String CAPITAL_FINALPRICE = "PriceFinal";

    /**
     * The Market Attribute.
     */
    public static final String CAPITAL_MARKET = "MarketMovement";

    /**
     * The Takeover Cost.
     */
    public static final String CAPITAL_TAKEOVERCOST = "TakeoverCost";

    /**
     * The Takeover Cash.
     */
    public static final String CAPITAL_TAKEOVERCASH = "TakeoverCash";

    /**
     * The Takeover Stock.
     */
    public static final String CAPITAL_TAKEOVERSTOCK = "TakeoverStock";

    /**
     * The Takeover Total.
     */
    public static final String CAPITAL_TAKEOVERTOTAL = "TakeoverTotal";

    /**
     * The Takeover Price.
     */
    public static final String CAPITAL_TAKEOVERPRICE = "TakeoverPrice";

    /**
     * The Takeover Value.
     */
    public static final String CAPITAL_TAKEOVERVALUE = "TakeoverValue";

    /**
     * Attribute List.
     */
    private final AttributeList theAttributes;

    /**
     * The event.
     */
    private final Event theEvent;

    /**
     * The Date of the event.
     */
    private final DateDay theDate;

    /**
     * Date field id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField("Date");

    /**
     * Event Field id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    /**
     * Obtain the date.
     * @return the date.
     */
    public DateDay getDate() {
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
        return (theEvent != null) ? theEvent.getId() : -theDate.getId();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_EVENT.equals(pField)) {
            return (theEvent == null) ? JDataFieldValue.SkipField : theEvent;
        }

        /* If the field is an attribute handle specially */
        if (pField.getAnchor() == theLocalFields) {
            /* Obtain the attribute */
            return findAttribute(pField.getName());
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Constructor.
     * @param pEvent the underlying event
     */
    private CapitalEvent(final Event pEvent) {
        /* declare local fields */
        theLocalFields = declareFields();

        /* Create the attributes list */
        theAttributes = new AttributeList();

        /* Store the values */
        theDate = pEvent.getDate();
        theEvent = pEvent;
    }

    /**
     * Constructor.
     * @param pDate the date of the event
     */
    private CapitalEvent(final DateDay pDate) {
        /* declare local fields */
        theLocalFields = declareFields();

        /* Create the attributes list */
        theAttributes = new AttributeList();

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
     * Add Money Attribute.
     * @param pName the name of the attribute
     * @param pValue the value of the attribute
     */
    protected void addAttribute(final String pName,
                                final Money pValue) {
        /* Create the attribute and add to the list */
        MoneyAttribute myAttr = new MoneyAttribute(pName, new Money(pValue));
        theAttributes.add(myAttr);
        theLocalFields.declareLocalField(pName);
    }

    /**
     * Add Units Attribute.
     * @param pName the name of the attribute
     * @param pValue the value of the attribute
     */
    protected void addAttribute(final String pName,
                                final Units pValue) {
        /* Create the attribute and add to the list */
        UnitsAttribute myAttr = new UnitsAttribute(pName, new Units(pValue));
        theAttributes.add(myAttr);
        theLocalFields.declareLocalField(pName);
    }

    /**
     * Add Price Attribute.
     * @param pName the name of the attribute
     * @param pValue the value of the attribute
     */
    protected void addAttribute(final String pName,
                                final Price pValue) {
        /* Create the attribute and add to the list */
        PriceAttribute myAttr = new PriceAttribute(pName, new Price(pValue));
        theAttributes.add(myAttr);
        theLocalFields.declareLocalField(pName);
    }

    /**
     * Find an attribute.
     * @param pName the name of the attribute
     * @return the value of the attribute or null
     */
    public Object findAttribute(final String pName) {
        /* Search for the attribute */
        return theAttributes.findAttribute(pName);
    }

    /**
     * The List of capital events.
     */
    public static class CapitalEventList extends OrderedIdList<Integer, CapitalEvent> implements
            JDataContents {
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
            return getDataFields().getName() + "(" + size() + ")";
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
            addAtEnd(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Add a date event to the list.
         * @param pDate the Date for the event
         * @return the Capital Event
         */
        protected CapitalEvent addEvent(final DateDay pDate) {
            CapitalEvent myEvent;

            /* Create the Capital Event and add to list */
            myEvent = new CapitalEvent(pDate);
            addAtEnd(myEvent);

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
            if ((myEvent != null) && (myEvent.getEvent() != null)
                    && (myEvent.getEvent().getTransType().getTranClass() == TransClass.CASHTAKEOVER)) {
                return myEvent;
            }

            /* Return no such event */
            return null;
        }

        /**
         * Purge events after date.
         * @param pDate date from which to purge events
         */
        protected void purgeAfterDate(final DateDay pDate) {
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
     * Attribute class.
     */
    private abstract class Attribute {
        /**
         * The Name.
         */
        private final String theName;

        /**
         * The value.
         */
        private final Object theValue;

        /**
         * Obtain the name.
         * @return the name
         */
        public String getName() {
            return theName;
        }

        /**
         * Obtain the value.
         * @return the value
         */
        public Object getValue() {
            return theValue;
        }

        /**
         * Constructor.
         * @param pName the name
         * @param pValue the value
         */
        private Attribute(final String pName,
                          final Object pValue) {
            /* Store the values */
            theName = pName;
            theValue = pValue;
        }

        /**
         * Compare this Attribute to another to establish sort order.
         * @param pThat The Attribute to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        public int compareTo(final Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Make sure that the object is an Attributer */
            if (pThat.getClass() != this.getClass()) {
                return -1;
            }

            /* Access the object as an Attribute */
            Attribute myThat = (Attribute) pThat;

            /* Compare the year */
            return theName.compareTo(myThat.theName);
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

            /* Make sure that the object is an Attributer */
            if (pThat.getClass() != this.getClass()) {
                return false;
            }

            /* Access the object as an Attribute */
            Attribute myThat = (Attribute) pThat;

            /* Compare the year */
            return theName.equals(myThat.theName);
        }

        @Override
        public int hashCode() {
            return theName.hashCode();
        }

        /**
         * Format the element.
         * @return the formatted element
         */
        public abstract String format();
    }

    /**
     * MoneyAttribute class.
     */
    public final class MoneyAttribute extends Attribute {
        @Override
        public Money getValue() {
            return (Money) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name
         * @param pValue the value
         */
        private MoneyAttribute(final String pName,
                               final Money pValue) {
            /* Store the values */
            super(pName, pValue);
        }

        /**
         * Format the element.
         * @return the formatted element
         */
        @Override
        public String format() {
            return getValue().format(true);
        }
    }

    /**
     * UnitsAttribute class.
     */
    public final class UnitsAttribute extends Attribute {
        @Override
        public Units getValue() {
            return (Units) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name
         * @param pValue the value
         */
        private UnitsAttribute(final String pName,
                               final Units pValue) {
            /* Store the values */
            super(pName, pValue);
        }

        /**
         * Format the element.
         * @return the formatted element
         */
        @Override
        public String format() {
            return getValue().format(true);
        }
    }

    /**
     * PriceAttribute class.
     */
    public final class PriceAttribute extends Attribute {
        @Override
        public Price getValue() {
            return (Price) super.getValue();
        }

        /**
         * Constructor.
         * @param pName the name
         * @param pValue the value
         */
        private PriceAttribute(final String pName,
                               final Price pValue) {
            /* Store the values */
            super(pName, pValue);
        }

        /**
         * Format the element.
         * @return the formatted element
         */
        @Override
        public String format() {
            return getValue().format(true);
        }
    }

    /**
     * List of Attributes.
     */
    public static final class AttributeList {
        /**
         * List of attributes.
         */
        private List<Attribute> theAttributes;

        /**
         * Construct a list.
         */
        private AttributeList() {
            theAttributes = new ArrayList<Attribute>();
        }

        /**
         * Find an attribute.
         * @param pName the name of the attribute
         * @return the value of the attribute or null
         */
        protected Object findAttribute(final String pName) {
            Iterator<Attribute> myIterator;
            Attribute myCurr;

            /* Access the iterator */
            myIterator = theAttributes.iterator();

            /* Loop through the attributes */
            while (myIterator.hasNext()) {
                /* If we found the name return its value */
                myCurr = myIterator.next();
                if (myCurr.getName().equals(pName)) {
                    return myCurr.getValue();
                }
            }

            /* return attribute not found */
            return null;
        }

        /**
         * Add an attribute to the list.
         * @param pAttr the attribute to add
         */
        private void add(final Attribute pAttr) {
            /* Add the attribute */
            theAttributes.add(pAttr);
        }
    }
}
