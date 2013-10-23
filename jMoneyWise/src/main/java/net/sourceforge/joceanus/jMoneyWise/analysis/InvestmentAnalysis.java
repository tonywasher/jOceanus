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
package net.sourceforge.jOceanus.jMoneyWise.analysis;

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
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.EventCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

/**
 * Investment Analysis relating to asset movements.
 * @author Tony Washer
 */
public final class InvestmentAnalysis
        implements OrderedIdItem<Integer>, JDataContents, Comparable<InvestmentAnalysis> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(InvestmentAnalysis.class.getSimpleName());

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
    private final Map<InvestmentAttribute, JDecimal> theAttributes;

    /**
     * The account.
     */
    private final Account theAccount;

    /**
     * The event.
     */
    private final Event theEvent;

    /**
     * The Date of the event.
     */
    private final JDateDay theDate;

    /**
     * Account field id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

    /**
     * Date field id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField("Date");

    /**
     * Event Field id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    /**
     * Category Field id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityField("Category");

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, InvestmentAttribute> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, InvestmentAttribute.class);

    /**
     * Obtain the account.
     * @return the account.
     */
    public Account getAccount() {
        return theAccount;
    }

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

    /**
     * Obtain the category.
     * @return the category.
     */
    public EventCategory getCategory() {
        return (theEvent != null)
                ? theEvent.getCategory()
                : null;
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
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_EVENT.equals(pField)) {
            return (theEvent == null)
                    ? JDataFieldValue.SkipField
                    : theEvent;
        }
        if (FIELD_CATEGORY.equals(pField)) {
            return (theEvent == null)
                    ? JDataFieldValue.SkipField
                    : getCategory();
        }

        /* Handle Attribute fields */
        InvestmentAttribute myClass = getClassForField(pField);
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
    private Object getAttributeValue(final InvestmentAttribute pAttr) {
        /* Access value of object */
        Object myValue = getAttribute(pAttr);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an attribute field.
     * @param pField the field
     * @return the class
     */
    private static InvestmentAttribute getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Set Attribute.
     * @param pAttr the attribute
     * @param pValue the value of the attribute
     */
    protected void setAttribute(final InvestmentAttribute pAttr,
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
    private <X extends JDecimal> X getAttribute(final InvestmentAttribute pAttr,
                                                final Class<X> pClass) {
        /* Obtain the attribute */
        return pClass.cast(getAttribute(pAttr));
    }

    /**
     * Obtain a money attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JMoney getMoneyAttribute(final InvestmentAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JMoney.class);
    }

    /**
     * Obtain a units attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    public JUnits getUnitsAttribute(final InvestmentAttribute pAttr) {
        /* Obtain the attribute */
        return getAttribute(pAttr, JUnits.class);
    }

    /**
     * Obtain an attribute value.
     * @param pAttr the attribute
     * @return the value of the attribute or null
     */
    private Object getAttribute(final InvestmentAttribute pAttr) {
        /* Obtain the attribute */
        return theAttributes.get(pAttr);
    }

    /**
     * Constructor.
     * @param pAccount the owning account
     * @param pEvent the underlying event
     */
    private InvestmentAnalysis(final Account pAccount,
                               final Event pEvent) {
        /* Create the attributes map */
        theAttributes = new EnumMap<InvestmentAttribute, JDecimal>(InvestmentAttribute.class);

        /* Store the values */
        theDate = pEvent.getDate();
        theAccount = pAccount;
        theEvent = pEvent;
    }

    /**
     * Constructor.
     * @param pAccount the owning account
     * @param pDate the date of the event
     */
    private InvestmentAnalysis(final Account pAccount,
                               final JDateDay pDate) {
        /* Create the attributes map */
        theAttributes = new EnumMap<InvestmentAttribute, JDecimal>(InvestmentAttribute.class);

        /* Store the values */
        theDate = pDate;
        theAccount = pAccount;
        theEvent = null;
    }

    @Override
    public int compareTo(final InvestmentAnalysis pThat) {
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
        InvestmentAnalysis myThat = (InvestmentAnalysis) pThat;

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
     * Adjust cost.
     * @param pTotal the total cost
     * @param pDelta the delta cost
     */
    protected void adjustCost(final JMoney pTotal,
                              final JMoney pDelta) {
        /* Record current and delta cost */
        setAttribute(InvestmentAttribute.InitialCost, new JMoney(pTotal));
        setAttribute(InvestmentAttribute.DeltaCost, pDelta);

        /* Adjust the total cost */
        pTotal.addAmount(pDelta);
        setAttribute(InvestmentAttribute.FinalCost, new JMoney(pTotal));
    }

    /**
     * Adjust invested.
     * @param pTotal the total invested
     * @param pDelta the delta invested
     */
    protected void adjustInvested(final JMoney pTotal,
                                  final JMoney pDelta) {
        /* Record current and delta invested */
        setAttribute(InvestmentAttribute.InitialInvested, new JMoney(pTotal));
        setAttribute(InvestmentAttribute.DeltaInvested, pDelta);

        /* Adjust the total invested */
        pTotal.addAmount(pDelta);
        setAttribute(InvestmentAttribute.FinalInvested, new JMoney(pTotal));
    }

    /**
     * Adjust dividend.
     * @param pTotal the total dividend
     * @param pDelta the delta dividend
     */
    protected void adjustDividend(final JMoney pTotal,
                                  final JMoney pDelta) {
        /* Record current and delta dividend */
        setAttribute(InvestmentAttribute.InitialDividend, new JMoney(pTotal));
        setAttribute(InvestmentAttribute.DeltaDividend, pDelta);

        /* Adjust the total dividend */
        pTotal.addAmount(pDelta);
        setAttribute(InvestmentAttribute.FinalDividend, new JMoney(pTotal));
    }

    /**
     * Adjust gains.
     * @param pTotal the total gains
     * @param pDelta the delta gains
     */
    protected void adjustGains(final JMoney pTotal,
                               final JMoney pDelta) {
        /* Record current and delta gains */
        setAttribute(InvestmentAttribute.InitialGains, new JMoney(pTotal));
        setAttribute(InvestmentAttribute.DeltaGains, pDelta);

        /* Adjust the total gains */
        pTotal.addAmount(pDelta);
        setAttribute(InvestmentAttribute.FinalGains, new JMoney(pTotal));
    }

    /**
     * Adjust units.
     * @param pTotal the total units
     * @param pDelta the delta units
     */
    protected void adjustUnits(final JUnits pTotal,
                               final JUnits pDelta) {
        /* Record current and delta units */
        setAttribute(InvestmentAttribute.InitialUnits, new JUnits(pTotal));
        setAttribute(InvestmentAttribute.DeltaUnits, pDelta);

        /* Adjust the total units */
        pTotal.addUnits(pDelta);
        setAttribute(InvestmentAttribute.FinalUnits, new JUnits(pTotal));
    }

    /**
     * The List of capital events.
     */
    public static class InvestmentAnalysisList
            extends OrderedIdList<Integer, InvestmentAnalysis>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(InvestmentAnalysisList.class.getSimpleName());

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
         * Construct an empty Investment Analysis list.
         * @param pData the DataSet
         * @param pAccount the Account for the list
         */
        protected InvestmentAnalysisList(final FinanceData pData,
                                         final Account pAccount) {
            super(InvestmentAnalysis.class);

            /* Store the data */
            theData = pData;
            theAccount = pAccount;
        }

        /**
         * Clone an Investment Analysis list.
         * @param pSource the Source
         */
        protected InvestmentAnalysisList(final InvestmentAnalysisList pSource) {
            super(InvestmentAnalysis.class, pSource);

            /* Store the data */
            theData = pSource.getData();
            theAccount = pSource.getAccount();
        }

        /**
         * Add an event to the list.
         * @param pEvent the Event to add
         * @return the Investment Analysis
         */
        protected InvestmentAnalysis addAnalysis(final Event pEvent) {
            /* Create the Capital Event and add to list */
            InvestmentAnalysis myEvent = new InvestmentAnalysis(theAccount, pEvent);
            append(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Add a date analysis to the list.
         * @param pDate the Date for the event
         * @return the Investment Analysis
         */
        protected InvestmentAnalysis addAnalysis(final JDateDay pDate) {
            /* Create the Analysis and add to list */
            InvestmentAnalysis myEvent = new InvestmentAnalysis(theAccount, pDate);
            append(myEvent);

            /* return the new event */
            return myEvent;
        }

        /**
         * Purge analyses after date.
         * @param pDate date from which to purge analyses
         */
        protected void purgeAfterDate(final JDateDay pDate) {
            /* Access the iterator */
            Iterator<InvestmentAnalysis> myIterator = listIterator();

            /* Loop through the analyses */
            while (myIterator.hasNext()) {
                InvestmentAnalysis myEvent = myIterator.next();
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
    public enum InvestmentAttribute {
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
         * The Takeover Cash Cost Portion Attribute.
         */
        TakeOverCashCost,

        /**
         * The Takeover Stock Cost Portion Attribute.
         */
        TakeOverStockCost,

        /**
         * The Takeover Cash Value Attribute.
         */
        TakeOverCashValue,

        /**
         * The Takeover Stock Price Attribute.
         */
        TakeOverStockPrice,

        /**
         * The Takeover Stock Value Attribute.
         */
        TakeOverStockValue;
    }
}
