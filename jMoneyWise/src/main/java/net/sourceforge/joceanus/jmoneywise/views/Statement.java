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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountCategory;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.data.Event.BaseEventList;
import net.sourceforge.joceanus.jmoneywise.data.EventGroup;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jmoneywise.views.AccountBucket.AccountAttribute;
import net.sourceforge.joceanus.jmoneywise.views.AccountCategoryBucket.CategoryType;
import net.sourceforge.joceanus.jtablefilter.TableFilter;

/**
 * Extension of Event to cater for statements.
 * @author Tony Washer
 */
public class Statement
        implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(Statement.class.getSimpleName());

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

    /**
     * DateRange Field Id.
     */
    public static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField("DateRange");

    /**
     * Starting Balance Field Id.
     */
    public static final JDataField FIELD_BALSTART = FIELD_DEFS.declareLocalField("StartBalance");

    /**
     * Ending Balance Field Id.
     */
    public static final JDataField FIELD_BALEND = FIELD_DEFS.declareLocalField("EndBalance");

    /**
     * Starting Units Field Id.
     */
    public static final JDataField FIELD_UNITSTART = FIELD_DEFS.declareLocalField("StartUnits");

    /**
     * Ending Units Field Id.
     */
    public static final JDataField FIELD_UNITEND = FIELD_DEFS.declareLocalField("EndUnits");

    /**
     * Lines Field Id.
     */
    public static final JDataField FIELD_LINES = FIELD_DEFS.declareLocalField("StatementLines");

    /**
     * Analysis Field Id.
     */
    public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_RANGE.equals(pField)) {
            return theRange;
        }
        if (FIELD_BALSTART.equals(pField)) {
            return theStartBalance;
        }
        if (FIELD_BALEND.equals(pField)) {
            return theEndBalance;
        }
        if (FIELD_UNITSTART.equals(pField)) {
            return theStartUnits;
        }
        if (FIELD_UNITEND.equals(pField)) {
            return theEndUnits;
        }
        if (FIELD_LINES.equals(pField)) {
            return theLines;
        }
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * The view.
     */
    private final View theView;

    /**
     * The view.
     */
    private final Account theAccount;

    /**
     * The Account bucket.
     */
    private AccountBucket theBucket = null;

    /**
     * The date range.
     */
    private final JDateDayRange theRange;

    /**
     * The starting balance.
     */
    private JMoney theStartBalance = null;

    /**
     * The ending balance.
     */
    private JMoney theEndBalance = null;

    /**
     * The starting units.
     */
    private JUnits theStartUnits = null;

    /**
     * The ending units.
     */
    private JUnits theEndUnits = null;

    /**
     * The table filter.
     */
    private TableFilter<StatementLine> theFilter = null;

    /**
     * The analysis.
     */
    private DataAnalysis theAnalysis = null;

    /**
     * The lines.
     */
    private final StatementLines theLines;

    /**
     * Obtain the account.
     * @return the account
     */
    public Account getAccount() {
        return theAccount;
    }

    /**
     * Obtain the Date range.
     * @return the range
     */
    public JDateDayRange getDateRange() {
        return theRange;
    }

    /**
     * Obtain the starting balance.
     * @return the balance
     */
    public JMoney getStartBalance() {
        return theStartBalance;
    }

    /**
     * Obtain the ending balance.
     * @return the balance
     */
    public JMoney getEndBalance() {
        return theEndBalance;
    }

    /**
     * Obtain the starting units.
     * @return the units
     */
    public JUnits getStartUnits() {
        return theStartUnits;
    }

    /**
     * Obtain the ending units.
     * @return the units
     */
    public JUnits getEndUnits() {
        return theEndUnits;
    }

    /**
     * Obtain the account category.
     * @return the category
     */
    public AccountCategory getAccountCategory() {
        return theAccount.getAccountCategory();
    }

    /**
     * Obtain the statement lines.
     * @return the lines
     */
    public StatementLines getLines() {
        return theLines;
    }

    /**
     * Obtain the iterator.
     * @return the iterator
     */
    public Iterator<StatementLine> getIterator() {
        return (theFilter == null)
                ? theLines.listIterator()
                : theFilter.viewIterator();
    }

    /**
     * Set the filter.
     * @param pFilter the filter
     */
    public void setFilter(final TableFilter<StatementLine> pFilter) {
        theFilter = pFilter;
    }

    /**
     * Constructor.
     * @param pView the view
     * @param pAccount the account
     * @param pRange the date range
     * @throws JDataException on error
     */
    public Statement(final View pView,
                     final Account pAccount,
                     final JDateDayRange pRange) throws JDataException {
        /* Create a copy of the account (plus surrounding list) */
        theView = pView;
        theAccount = pAccount;
        theRange = pRange;
        theLines = new StatementLines(this);

        /* Create an analysis for this statement */
        theAnalysis = new DataAnalysis(theView.getData(), this);
    }

    /**
     * Set the ending balances for the statement.
     * @param pAccount the Account Bucket
     */
    protected void setStartBalances(final AccountBucket pAccount) {
        /* Record the bucket and access bucket type */
        theBucket = pAccount;

        /* If the bucket has a balance */
        if (hasBalance()) {
            /* Set starting balance */
            theStartBalance = new JMoney(theBucket.getMoneyAttribute(AccountAttribute.Valuation));
        }

        /* If the bucket has units */
        if (hasUnits()) {
            /* Set starting units */
            theStartUnits = new JUnits(theBucket.getUnitsAttribute(AccountAttribute.Units));
        }
    }

    /**
     * Set the ending balances for the statement.
     */
    protected void setEndBalances() {
        /* If the bucket has a balance */
        if (hasBalance()) {
            /* Set ending balance */
            theEndBalance = new JMoney(theBucket.getMoneyAttribute(AccountAttribute.Valuation));
        }

        /* If the bucket has units */
        if (hasUnits()) {
            /* Set ending units */
            theEndUnits = new JUnits(theBucket.getUnitsAttribute(AccountAttribute.Units));
        }
    }

    /**
     * Reset the balances.
     * @throws JDataException on error
     */
    public void resetBalances() throws JDataException {
        /* Reset the balances */
        theAnalysis.resetStatementBalance(this);
    }

    /**
     * Does the statement have a money balance?
     * @return TRUE/FALSE
     */
    public boolean hasBalance() {
        return (theBucket.getCategoryType().hasBalances());
    }

    /**
     * Does the statement have units?
     * @return TRUE/FALSE
     */
    public boolean hasUnits() {
        return (theBucket.getCategoryType() == CategoryType.Priced);
    }

    /**
     * The Statement Lines.
     */
    public static class StatementLines
            extends BaseEventList<StatementLine> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(StatementLines.class.getSimpleName(), DataList.FIELD_DEFS);

        /**
         * EventGroupList field Id.
         */
        public static final JDataField FIELD_EVENTGROUPS = FIELD_DEFS.declareLocalField("EventGroups");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_EVENTGROUPS.equals(pField)) {
                return theGroups.size() > 0
                        ? theGroups
                        : JDataFieldValue.SkipField;
            }

            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * EventGroupMap.
         */
        private final Map<Integer, EventGroup<StatementLine>> theGroups = new HashMap<Integer, EventGroup<StatementLine>>();

        /**
         * Statement.
         */
        private final Statement theStatement;

        /**
         * Obtain the statement.
         * @return the statement
         */
        private Statement getStatement() {
            return theStatement;
        }

        /**
         * Obtain the account.
         * @return the account
         */
        private Account getAccount() {
            return theStatement.getAccount();
        }

        @Override
        public String listName() {
            return StatementLines.class.getSimpleName();
        }

        /**
         * Constructor.
         * @param pStatement the statement
         */
        public StatementLines(final Statement pStatement) {
            /* Declare the data and set the style */
            super(pStatement.theView.getData(), StatementLine.class);
            setStyle(ListStyle.EDIT);
            setRange(pStatement.getDateRange());
            theStatement = pStatement;
            FinanceData myData = getDataSet();
            setBase(myData.getEvents());

            /* Store InfoType list */
            setEventInfoTypes(myData.getEventInfoTypes());

            /* Create info List */
            EventInfoList myEventInfo = myData.getEventInfo();
            setEventInfos(myEventInfo.getEmptyList(ListStyle.EDIT));
        }

        @Override
        protected StatementLines getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return theStatement.theAccount.isLocked();
        }

        @Override
        public StatementLine addCopyItem(final DataItem pElement) {
            /* Can only clone a StatementLine */
            if (!(pElement instanceof StatementLine)) {
                return null;
            }

            StatementLine myLine = new StatementLine(this, (StatementLine) pElement);
            add(myLine);
            return myLine;
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public StatementLine addNewItem() {
            /* Create the new line */
            StatementLine myLine = new StatementLine(this);

            /* Set the Date as the start of the range */
            myLine.setDate(theStatement.theRange.getStart());

            /* Add line to list */
            add(myLine);
            return myLine;
        }

        /**
         * Register child into event group.
         * @param pChild the child to register
         */
        public void registerChild(final StatementLine pChild) {
            /* Access parent */
            StatementLine myParent = pChild.getParent();
            Integer myId = myParent.getId();
            myParent = findItemById(myId);

            /* Access EventGroup */
            EventGroup<StatementLine> myGroup = theGroups.get(myId);
            if (myGroup == null) {
                myGroup = new EventGroup<StatementLine>(myParent, StatementLine.class);
                theGroups.put(myId, myGroup);
            }

            /* Register the child */
            myGroup.registerChild(pChild);
        }

        /**
         * Obtain the group for a parent.
         * @param pParent the parent event
         * @return the group
         */
        public EventGroup<StatementLine> getGroup(final StatementLine pParent) {
            return theGroups.get(pParent.getId());
        }
    }

    /**
     * Statement line.
     */
    public static class StatementLine
            extends Event {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, Event.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * IsCredit Field Id.
         */
        public static final JDataField FIELD_ISCREDIT = FIELD_DEFS.declareEqualityValueField("IsCredit");

        /**
         * Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        /**
         * Partner Field Id.
         */
        public static final JDataField FIELD_PARTNER = FIELD_DEFS.declareLocalField("Partner");

        /**
         * Balance Field Id.
         */
        public static final JDataField FIELD_BALANCE = FIELD_DEFS.declareLocalField("Balance");

        /**
         * Units Balance Field Id.
         */
        public static final JDataField FIELD_UNITS = FIELD_DEFS.declareLocalField("UnitsBalance");

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return JDataFieldValue.SkipField;
            }
            if (FIELD_PARTNER.equals(pField)) {
                return JDataFieldValue.SkipField;
            }
            if (FIELD_BALANCE.equals(pField)) {
                return theBalance;
            }
            if (FIELD_UNITS.equals(pField)) {
                return theBalUnits;
            }
            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        @Override
        public StatementLine getParent() {
            return (StatementLine) super.getParent();
        }

        /**
         * Is this a credit to the account?
         * @return true/false
         */
        public boolean isCredit() {
            return isCredit(getValueSet());
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return isCredit()
                    ? getCredit()
                    : getDebit();
        }

        /**
         * Obtain the account category.
         * @return the category
         */
        public AccountCategory getActType() {
            Account myAccount = getAccount();
            return (myAccount == null)
                    ? null
                    : myAccount.getAccountCategory();
        }

        /**
         * Obtain the partner.
         * @return the partner
         */
        public Account getPartner() {
            return isCredit()
                    ? getDebit()
                    : getCredit();
        }

        /**
         * Is this a credit to the account?
         * @param pValueSet the valueSet
         * @return true/false
         */
        public static Boolean isCredit(final ValueSet pValueSet) {
            return pValueSet.getValue(FIELD_ISCREDIT, Boolean.class);
        }

        /**
         * Set whether this a credit to the account.
         * @param isCredit true/false
         */
        private void setValueIsCredit(final Boolean isCredit) {
            getValueSet().setValue(FIELD_ISCREDIT, isCredit);
        }

        /**
         * The running balance.
         */
        private JMoney theBalance = null;

        /**
         * The running units balance.
         */
        private JUnits theBalUnits = null;

        /**
         * The statement.
         */
        private final Statement theStatement;

        /**
         * Obtain the balance.
         * @return the balance
         */
        public JMoney getBalance() {
            return theBalance;
        }

        /**
         * Obtain the units balance.
         * @return the balance
         */
        public JUnits getBalanceUnits() {
            return theBalUnits;
        }

        @Override
        public String getComments() {
            return isHeader()
                    ? "Opening Balance"
                    : super.getComments();
        }

        /**
         * Obtain the bucket.
         * @return the bucket
         */
        private AccountBucket getBucket() {
            return theStatement.theBucket;
        }

        @Override
        public Event getBase() {
            return (Event) super.getBase();
        }

        /**
         * Construct a copy of a Line.
         * @param pList the list
         * @param pLine The Line
         */
        protected StatementLine(final StatementLines pList,
                                final StatementLine pLine) {
            /* Set standard values */
            super(pList, pLine);
            theStatement = pList.getStatement();
        }

        /**
         * Constructor for new line.
         * @param pList the list
         */
        public StatementLine(final StatementLines pList) {
            super(pList);
            theStatement = pList.getStatement();
            setValueIsCredit(false);
            setValueReconciled(false);
            setValueSplit(false);
            setDebit(pList.getAccount());
        }

        /**
         * Constructor from event.
         * @param pList the list
         * @param pEvent the event
         */
        public StatementLine(final StatementLines pList,
                             final Event pEvent) {
            /* Make this an element */
            super(pList, pEvent);
            theStatement = pList.getStatement();
            setValueIsCredit(!Difference.isEqual(getDebit(), pList.getAccount()));
            setBase(pEvent);

            /* If we have a parent */
            Integer myParentId = getParentId();
            if (myParentId != null) {
                /* Convert to Statement parent */
                StatementLine myParent = pList.findItemById(myParentId);
                setValueParent(myParent);
            }
        }

        /**
         * Constructor for header.
         * @param pStatement the statement
         * @throws JDataException on error
         */
        public StatementLine(final Statement pStatement) throws JDataException {
            /* Make this an element */
            super(pStatement.getLines());
            theStatement = pStatement;
            setHeader(true);
            setId(0);
            setDate(theStatement.getDateRange().getStart());
            setValueIsCredit(false);
            setValueReconciled(false);
            setValueSplit(false);
            setDebit(theStatement.getAccount());
            theBalance = theStatement.getStartBalance();
            theBalUnits = theStatement.getStartUnits();
        }

        /**
         * Set Balances.
         */
        protected void setBalances() {
            /* If the bucket has a balance */
            if (theStatement.hasBalance()) {
                /* Set current balance */
                theBalance = new JMoney(getBucket().getMoneyAttribute(AccountAttribute.Valuation));
            }

            /* If the bucket has units */
            if (theStatement.hasUnits()) {
                /* Set current units */
                theBalUnits = new JUnits(getBucket().getUnitsAttribute(AccountAttribute.Units));
            }
        }

        /**
         * Set a new partner.
         * @param pPartner the new partner
         */
        public void setPartner(final Account pPartner) {
            if (isCredit()) {
                setDebit(pPartner);
            } else {
                setCredit(pPartner);
            }
        }

        /**
         * Set a new isCredit indication.
         * @param isCredit true/false
         */
        public void setIsCredit(final boolean isCredit) {
            /* If we are changing values */
            if (isCredit != isCredit()) {
                /* Swap credit/debit values */
                Account myTemp = getCredit();
                setCredit(getDebit());
                setDebit(myTemp);
            }
            setValueIsCredit(isCredit);
        }

        /**
         * Add an error for this item.
         * @param pError the error text
         * @param iField the associated field
         */
        @Override
        public void addError(final String pError,
                             final JDataField iField) {
            JDataField myField = iField;
            /* Re-Map Credit/Debit field errors */
            if (iField == FIELD_CREDIT) {
                myField = isCredit()
                        ? FIELD_ACCOUNT
                        : FIELD_PARTNER;
            } else if (iField == FIELD_DEBIT) {
                myField = isCredit()
                        ? FIELD_PARTNER
                        : FIELD_ACCOUNT;
            }

            /* Call super class */
            super.addError(pError, myField);
        }
    }
}
