/*******************************************************************************
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

public class CapitalEvent extends ReportItem<CapitalEvent> {
    /**
     * Report fields
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEvent.class.getSimpleName(),
            ReportItem.theLocalFields);

    /**
     * Report fields
     */
    private JDataFields theLocalFields;

    /* Called from constructor */
    @Override
    public JDataFields declareFields() {
        return theLocalFields = new JDataFields(FIELD_DEFS.getName(), FIELD_DEFS);
    }

    /**
     * The attributes
     */
    public static final String capitalInitialCost = "CostInitial";
    public static final String capitalDeltaCost = "CostDelta";
    public static final String capitalFinalCost = "CostFinal";
    public static final String capitalInitialUnits = "UnitsInitial";
    public static final String capitalDeltaUnits = "UnitsDelta";
    public static final String capitalFinalUnits = "UnitsFinal";
    public static final String capitalInitialGains = "GainsInitial";
    public static final String capitalDeltaGains = "GainsDelta";
    public static final String capitalFinalGains = "GainsFinal";
    public static final String capitalInitialGained = "GainedInitial";
    public static final String capitalDeltaGained = "GainedDelta";
    public static final String capitalFinalGained = "GainedFinal";
    public static final String capitalInitialDiv = "DividendInitial";
    public static final String capitalDeltaDiv = "DividendDelta";
    public static final String capitalFinalDiv = "DividendFinal";
    public static final String capitalInitialInvest = "InvestedInitial";
    public static final String capitalDeltaInvest = "InvestedDelta";
    public static final String capitalFinalInvest = "InvestedFinal";
    public static final String capitalInitialValue = "ValueInitial";
    public static final String capitalFinalValue = "ValueFinal";
    public static final String capitalInitialPrice = "PriceInitial";
    public static final String capitalFinalPrice = "PriceFinal";
    public static final String capitalMarket = "MarketMovement";
    public static final String capitalTakeoverCost = "TakeoverCost";
    public static final String capitalTakeoverCash = "TakeoverCash";
    public static final String capitalTakeoverStock = "TakeoverStock";
    public static final String capitalTakeoverTotal = "TakeoverTotal";
    public static final String capitalTakeoverPrice = "TakeoverPrice";
    public static final String capitalTakeoverValue = "TakeoverValue";

    /* Members */
    private final AttributeList theAttributes;
    private final Event theEvent;
    private final DateDay theDate;

    /* Field IDs */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField("Date");
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    /* Access methods */
    public DateDay getDate() {
        return theDate;
    }

    public Event getEvent() {
        return theEvent;
    }

    @Override
    public Object getFieldValue(JDataField pField) {
        /* Handle standard fields */
        if (pField == FIELD_DATE)
            return theDate;
        if (pField == FIELD_EVENT)
            return theEvent;

        /* If the field is an attribute handle specially */
        if (pField.getAnchor() == theLocalFields) {
            /* Obtain the attribute */
            return findAttribute(pField.getName());
        }

        return super.getFieldValue(pField);
    }

    /**
     * Constructor
     * @param pList the list to belong to
     * @param pEvent the underlying event
     */
    private CapitalEvent(CapitalEventList pList,
                         Event pEvent) {
        /* Call super-constructor */
        super(pList);

        /* Create the attributes list */
        theAttributes = new AttributeList();

        /* Store the values */
        theDate = pEvent.getDate();
        theEvent = pEvent;
    }

    /**
     * Constructor
     * @param pList the list to belong to
     * @param pDate the date of the event
     */
    private CapitalEvent(CapitalEventList pList,
                         DateDay pDate) {
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
    public int compareTo(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is a CapitalEvent */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a CapitalEvent */
        CapitalEvent myThat = (CapitalEvent) pThat;

        /* Compare the dates */
        int iResult = getDate().compareTo(myThat.getDate());
        if (iResult != 0)
            return iResult;

        /* If we have a null event then we are after non-null and equal to null */
        if (getEvent() == null)
            return (myThat.getEvent() == null) ? 0 : 1;

        /* If we don't have null event then before any null event */
        if (myThat.getEvent() == null)
            return -1;

        /* Compare the underlying events */
        return getEvent().compareTo(myThat.getEvent());
    }

    /**
     * Add Money Attribute
     * @param pName the name of the attribute
     * @param pValue the value of the attribute
     */
    protected void addAttribute(String pName,
                                Money pValue) {
        /* Create the attribute and add to the list */
        MoneyAttribute myAttr = new MoneyAttribute(pName, new Money(pValue));
        theAttributes.add(myAttr);
        theLocalFields.declareLocalField(pName);
    }

    /**
     * Add Units Attribute
     * @param pName the name of the attribute
     * @param pValue the value of the attribute
     */
    protected void addAttribute(String pName,
                                Units pValue) {
        /* Create the attribute and add to the list */
        UnitsAttribute myAttr = new UnitsAttribute(pName, new Units(pValue));
        theAttributes.add(myAttr);
        theLocalFields.declareLocalField(pName);
    }

    /**
     * Add Price Attribute
     * @param pName the name of the attribute
     * @param pValue the value of the attribute
     */
    protected void addAttribute(String pName,
                                Price pValue) {
        /* Create the attribute and add to the list */
        PriceAttribute myAttr = new PriceAttribute(pName, new Price(pValue));
        theAttributes.add(myAttr);
        theLocalFields.declareLocalField(pName);
    }

    /**
     * Find an attribute
     * @param pName the name of the attribute
     * @return the value of the attribute or null
     */
    public Object findAttribute(String pName) {
        /* Search for the attribute */
        return theAttributes.findAttribute(pName);
    }

    /* The List of capital events */
    public static class CapitalEventList extends ReportList<CapitalEvent> {
        /**
         * Report fields
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CapitalEventList.class.getSimpleName(),
                ReportList.theLocalFields);

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /* Field IDs */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        /* Members */
        private final FinanceData theData;
        private final Account theAccount;

        /* Access methods */
        public FinanceData getData() {
            return theData;
        }

        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty Capital event list
         * @param pData the DataSet
         * @param pAccount the Account for the list
         */
        protected CapitalEventList(FinanceData pData,
                                   Account pAccount) {
            super(CapitalEvent.class);

            /* Store the data */
            theData = pData;
            theAccount = pAccount;
        }

        /**
         * Add an event to the list
         * @param pEvent the Event to add
         * @return the Capital Event
         */
        protected CapitalEvent addEvent(Event pEvent) {
            CapitalEvent myEvent;

            /* Create the Capital Event and add to list */
            myEvent = new CapitalEvent(this, pEvent);
            add(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Add a date event to the list
         * @param pDate the Date for the event
         * @return the Capital Event
         */
        protected CapitalEvent addEvent(DateDay pDate) {
            CapitalEvent myEvent;

            /* Create the Capital Event and add to list */
            myEvent = new CapitalEvent(this, pDate);
            add(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Find the cash takeover event (if present)
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
                    && (myEvent.getEvent().getTransType().getTranClass() == TransClass.CASHTAKEOVER))
                return myEvent;

            /* Return no such event */
            return null;
        }

        /**
         * Purge events after date
         * @param pDate date from which to purge events
         */
        protected void purgeAfterDate(DateDay pDate) {
            SortedListIterator<CapitalEvent> myIterator;
            CapitalEvent myEvent;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the events */
            while ((myEvent = myIterator.next()) != null) {
                /* If this is past (or on) the date remove it */
                if (pDate.compareTo(myEvent.getDate()) <= 0)
                    myIterator.remove();
            }

            /* Return */
            return;
        }
    }

    /* Attribute class */
    private abstract class Attribute {
        /* Members */
        private String theName = null;
        private Object theValue = null;

        /* Access methods */
        public String getName() {
            return theName;
        }

        public Object getValue() {
            return theValue;
        }

        /**
         * Constructor
         * @param pName the name
         * @param pValue the value
         */
        private Attribute(String pName,
                          Object pValue) {
            /* Store the values */
            theName = pName;
            theValue = pValue;
        }

        /**
         * Compare this Attribute to another to establish sort order.
         * 
         * @param pThat The Attribute to compare to
         * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in
         *         the sort order
         */
        public int compareTo(Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an Attributer */
            if (pThat.getClass() != this.getClass())
                return -1;

            /* Access the object as an Attribute */
            Attribute myThat = (Attribute) pThat;

            /* Compare the year */
            return theName.compareTo(myThat.theName);
        }

        /**
         * Format the element
         * @return the formatted element
         */
        public abstract String format();
    }

    /* MoneyAttribute class */
    public class MoneyAttribute extends Attribute {
        /* Access methods */
        @Override
        public Money getValue() {
            return (Money) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name
         * @param pValue the value
         */
        private MoneyAttribute(String pName,
                               Money pValue) {
            /* Store the values */
            super(pName, pValue);
        }

        /**
         * Format the element
         * @return the formatted element
         */
        @Override
        public String format() {
            return getValue().format(true);
        }
    }

    /* UnitsAttribute class */
    public class UnitsAttribute extends Attribute {
        /* Access methods */
        @Override
        public Units getValue() {
            return (Units) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name
         * @param pValue the value
         */
        private UnitsAttribute(String pName,
                               Units pValue) {
            /* Store the values */
            super(pName, pValue);
        }

        /**
         * Format the element
         * @return the formatted element
         */
        @Override
        public String format() {
            return getValue().format(true);
        }
    }

    /* PricesAttribute class */
    public class PriceAttribute extends Attribute {
        /* Access methods */
        @Override
        public Price getValue() {
            return (Price) super.getValue();
        }

        /**
         * Constructor
         * @param pName the name
         * @param pValue the value
         */
        private PriceAttribute(String pName,
                               Price pValue) {
            /* Store the values */
            super(pName, pValue);
        }

        /**
         * Format the element
         * @return the formatted element
         */
        @Override
        public String format() {
            return getValue().format(true);
        }
    }

    /**
     * List of Attributes
     */
    public class AttributeList {
        /**
         * List of attributes
         */
        private List<Attribute> theAttributes;

        /**
         * Construct a list.
         */
        private AttributeList() {
            theAttributes = new ArrayList<Attribute>();
        }

        /**
         * Find an attribute
         * @param pName the name of the attribute
         * @return the value of the attribute or null
         */
        protected Object findAttribute(String pName) {
            Iterator<Attribute> myIterator;
            Attribute myCurr;

            /* Access the iterator */
            myIterator = theAttributes.iterator();

            /* Loop through the attributes */
            while (myIterator.hasNext()) {
                /* If we found the name return its value */
                myCurr = myIterator.next();
                if (myCurr.getName().equals(pName))
                    return myCurr.getValue();
            }

            /* return attribute not found */
            return null;
        }

        /**
         * Add an attribute to the list
         * @param pAttr the attribute to add
         */
        private void add(Attribute pAttr) {
            /* Add the attribute */
            theAttributes.add(pAttr);
        }
    }
}
