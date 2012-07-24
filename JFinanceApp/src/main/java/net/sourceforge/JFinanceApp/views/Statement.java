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

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.EncryptedItem.EncryptedList;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.AccountType;
import net.sourceforge.JFinanceApp.data.Event;
import net.sourceforge.JFinanceApp.data.Pattern.PatternList;
import net.sourceforge.JFinanceApp.views.Analysis.ActDetail;
import net.sourceforge.JFinanceApp.views.Analysis.AssetAccount;
import net.sourceforge.JFinanceApp.views.Analysis.BucketType;
import net.sourceforge.JFinanceApp.views.Analysis.ValueAccount;

/**
 * Extension of Event to cater for statements.
 * @author Tony Washer
 */
public class Statement implements JDataContents {
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
        /* Pass onwards */
        return null;
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
    private ActDetail theBucket = null;

    /**
     * The date range.
     */
    private final DateDayRange theRange;

    /**
     * The starting balance.
     */
    private Money theStartBalance = null;

    /**
     * The ending balance.
     */
    private Money theEndBalance = null;

    /**
     * The starting units.
     */
    private Units theStartUnits = null;

    /**
     * The ending units.
     */
    private Units theEndUnits = null;

    /**
     * The analysis.
     */
    private EventAnalysis theAnalysis = null;

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
    public DateDayRange getDateRange() {
        return theRange;
    }

    /**
     * Obtain the starting balance.
     * @return the balance
     */
    public Money getStartBalance() {
        return theStartBalance;
    }

    /**
     * Obtain the ending balance.
     * @return the balance
     */
    public Money getEndBalance() {
        return theEndBalance;
    }

    /**
     * Obtain the starting units.
     * @return the units
     */
    public Units getStartUnits() {
        return theStartUnits;
    }

    /**
     * Obtain the ending units.
     * @return the units
     */
    public Units getEndUnits() {
        return theEndUnits;
    }

    /**
     * Obtain the account type.
     * @return the type
     */
    public AccountType getActType() {
        return theAccount.getActType();
    }

    /**
     * Obtain the statement lines.
     * @return the lines
     */
    public StatementLines getLines() {
        return theLines;
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
                     final DateDayRange pRange) throws JDataException {
        /* Create a copy of the account (plus surrounding list) */
        theView = pView;
        theAccount = pAccount;
        theRange = pRange;
        theLines = new StatementLines(this);

        /* Create an analysis for this statement */
        theAnalysis = new EventAnalysis(theView.getData(), this);
    }

    /**
     * Set the ending balances for the statement.
     * @param pAccount the Account Bucket
     */
    protected void setStartBalances(final ActDetail pAccount) {
        /* Record the bucket and access bucket type */
        theBucket = pAccount;

        /* If the bucket has a balance */
        if (hasBalance()) {
            /* Set starting balance */
            theStartBalance = new Money(((ValueAccount) theBucket).getValue());
        }

        /* If the bucket has units */
        if (hasUnits()) {
            /* Set starting units */
            theStartUnits = new Units(((AssetAccount) theBucket).getUnits());
        }
    }

    /**
     * Set the ending balances for the statement.
     */
    protected void setEndBalances() {
        /* If the bucket has a balance */
        if (hasBalance()) {
            /* Set ending balance */
            theEndBalance = new Money(((ValueAccount) theBucket).getValue());
        }

        /* If the bucket has units */
        if (hasUnits()) {
            /* Set ending units */
            theEndUnits = new Units(((AssetAccount) theBucket).getUnits());
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
        return (theBucket.getBucketType() != BucketType.EXTERNALDETAIL);
    }

    /**
     * Does the statement have units?
     * @return TRUE/FALSE
     */
    public boolean hasUnits() {
        return (theBucket.getBucketType() == BucketType.ASSETDETAIL);
    }

    /**
     * Prepare changes in a statement back into the underlying finance objects.
     */
    protected void prepareChanges() {
        /* Prepare the changes from this list */
        theLines.prepareChanges();
    }

    /**
     * Commit/RollBack changes in a statement back into the underlying finance objects.
     * @param bCommit <code>true/false</code>
     */
    protected void commitChanges(final boolean bCommit) {
        /* Commit/RollBack the changes */
        if (bCommit) {
            theLines.commitChanges();
        } else {
            theLines.rollBackChanges();
        }
    }

    /**
     * The Statement Lines.
     */
    public static class StatementLines extends EncryptedList<StatementLine> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(PatternList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

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
            super(StatementLine.class, pStatement.theView.getData());
            setStyle(ListStyle.EDIT);
            theStatement = pStatement;
            setBase(theStatement.theView.getData().getEvents());
        }

        @Override
        protected StatementLines getEmptyList() {
            throw new UnsupportedOperationException();
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return theStatement.theAccount.isLocked();
        }

        @Override
        public StatementLine addNewItem(final DataItem pElement) {
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
    }

    /**
     * Statement line.
     */
    public static class StatementLine extends Event {
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
            return isCredit() ? getCredit() : getDebit();
        }

        /**
         * Obtain the account type.
         * @return the type
         */
        public AccountType getActType() {
            Account myAccount = getAccount();
            return (myAccount == null) ? null : myAccount.getActType();
        }

        /**
         * Obtain the partner.
         * @return the partner
         */
        public Account getPartner() {
            return isCredit() ? getDebit() : getCredit();
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
        private Money theBalance = null;

        /**
         * The running units balance.
         */
        private Units theBalUnits = null;

        /**
         * The statement.
         */
        private final Statement theStatement;

        /**
         * Obtain the balance.
         * @return the balance
         */
        public Money getBalance() {
            return theBalance;
        }

        /**
         * Obtain the units balance.
         * @return the balance
         */
        public Units getBalanceUnits() {
            return theBalUnits;
        }

        /**
         * Obtain the bucket.
         * @return the bucket
         */
        private ActDetail getBucket() {
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
            setDate(theStatement.getDateRange().getStart());
            setDescription("Opening Balance");
            setValueIsCredit(false);
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
                theBalance = new Money(((ValueAccount) getBucket()).getValue());
            }

            /* If the bucket has units */
            if (theStatement.hasUnits()) {
                /* Set current units */
                theBalUnits = new Units(((AssetAccount) getBucket()).getUnits());
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
        protected void addError(final String pError,
                                final JDataField iField) {
            JDataField myField = iField;
            /* Re-Map Credit/Debit field errors */
            if (iField == FIELD_CREDIT) {
                myField = isCredit() ? FIELD_ACCOUNT : FIELD_PARTNER;
            } else if (iField == FIELD_DEBIT) {
                myField = isCredit() ? FIELD_PARTNER : FIELD_ACCOUNT;
            }

            /* Call super class */
            super.addError(pError, myField);
        }
    }
}
