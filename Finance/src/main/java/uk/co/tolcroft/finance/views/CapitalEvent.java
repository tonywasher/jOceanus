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
package uk.co.tolcroft.finance.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ReportItem;
import net.sourceforge.JDataManager.ReportList;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JSortedList.SortedListIterator;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.StaticClass.TransClass;

/**
 * Capital Events relating to asset movements.
 * @author Tony Washer
 */
public final class CapitalEvent extends ReportItem<CapitalEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEvent.class.getSimpleName(),
            ReportItem.theLocalFields);

    /**
     * Report fields.
     */
    private JDataFields theLocalFields;

    @Override
    public JDataFields declareFields() {
        theLocalFields = new JDataFields(FIELD_DEFS.getName(), FIELD_DEFS);
        return theLocalFields;
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
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_EVENT.equals(pField)) {
            return theEvent;
        }

        /* If the field is an attribute handle specially */
        if (pField.getAnchor() == theLocalFields) {
            /* Obtain the attribute */
            return findAttribute(pField.getName());
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Constructor.
     * @param pList the list to belong to
     * @param pEvent the underlying event
     */
    private CapitalEvent(final CapitalEventList pList,
                         final Event pEvent) {
        /* Call super-constructor */
        super(pList);

        /* Create the attributes list */
        theAttributes = new AttributeList();

        /* Store the values */
        theDate = pEvent.getDate();
        theEvent = pEvent;
    }

    /**
     * Constructor.
     * @param pList the list to belong to
     * @param pDate the date of the event
     */
    private CapitalEvent(final CapitalEventList pList,
                         final DateDay pDate) {
        /* Call super-constructor */
        super(pList);

        /* Create the attributes list */
        theAttributes = new AttributeList();

        /* Store the values */
        theDate = pDate;
        theEvent = null;
    }

    /**
     * Compare this capital event to another to establish sort order.
     * @param pThat The Capital Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is a CapitalEvent */
        if (pThat.getClass() != this.getClass()) {
            return -1;
        }

        /* Access the object as a CapitalEvent */
        CapitalEvent myThat = (CapitalEvent) pThat;

        /* Compare the dates */
        int iResult = getDate().compareTo(myThat.getDate());
        if (iResult != 0) {
            return iResult;
        }

        /* If we have a null event then we are after non-null and equal to null */
        if (getEvent() == null) {
            return (myThat.getEvent() == null) ? 0 : 1;
        }

        /* If we don't have null event then before any null event */
        if (myThat.getEvent() == null) {
            return -1;
        }

        /* Compare the underlying events */
        return getEvent().compareTo(myThat.getEvent());
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
    public static class CapitalEventList extends ReportList<CapitalEvent> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEventList.class.getSimpleName(),
                ReportList.theLocalFields);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_ACCOUNT.equals(pField)) {
                return theAccount;
            }

            /* Pass onwards */
            return super.getFieldValue(pField);
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
            myEvent = new CapitalEvent(this, pEvent);
            add(myEvent);

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
            myEvent = new CapitalEvent(this, pDate);
            add(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Find the cash takeover event (if present).
         * @return the Capital Event
         */
        protected CapitalEvent getCashTakeOver() {
            SortedListIterator<CapitalEvent> myIterator;
            CapitalEvent myEvent;

            /* Create the iterator */
            myIterator = listIterator();

            /* Access the last element */
            myEvent = myIterator.peekLast();

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
            SortedListIterator<CapitalEvent> myIterator;
            CapitalEvent myEvent;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the events */
            while ((myEvent = myIterator.next()) != null) {
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
    public final class AttributeList {
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
