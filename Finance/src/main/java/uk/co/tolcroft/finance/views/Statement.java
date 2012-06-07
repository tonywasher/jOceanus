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

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.views.Analysis.ActDetail;
import uk.co.tolcroft.finance.views.Analysis.AssetAccount;
import uk.co.tolcroft.finance.views.Analysis.BucketType;
import uk.co.tolcroft.finance.views.Analysis.ValueAccount;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;

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

    /* Field IDs */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");
    public static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField("DateRange");
    public static final JDataField FIELD_BALSTART = FIELD_DEFS.declareLocalField("StartBalance");
    public static final JDataField FIELD_BALEND = FIELD_DEFS.declareLocalField("EndBalance");
    public static final JDataField FIELD_UNITSTART = FIELD_DEFS.declareLocalField("StartUnits");
    public static final JDataField FIELD_UNITEND = FIELD_DEFS.declareLocalField("EndUnits");
    public static final JDataField FIELD_LINES = FIELD_DEFS.declareLocalField("StatementLines");
    public static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField("Analysis");

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* If the field is not an attribute handle normally */
        if (pField == FIELD_ACCOUNT) {
            return theAccount;
        }
        if (pField == FIELD_RANGE) {
            return theRange;
        }
        if (pField == FIELD_BALSTART) {
            return theStartBalance;
        }
        if (pField == FIELD_BALEND) {
            return theEndBalance;
        }
        if (pField == FIELD_UNITSTART) {
            return theStartUnits;
        }
        if (pField == FIELD_UNITEND) {
            return theEndUnits;
        }
        if (pField == FIELD_LINES) {
            return theLines;
        }
        if (pField == FIELD_ANALYSIS) {
            return theAnalysis;
        }
        /* Pass onwards */
        return null;
    }

    /* Members */
    private View theView = null;
    private Account theAccount = null;
    private ActDetail theBucket = null;
    private DateDayRange theRange = null;
    private Money theStartBalance = null;
    private Money theEndBalance = null;
    private Units theStartUnits = null;
    private Units theEndUnits = null;
    private EventAnalysis theAnalysis = null;
    private StatementLines theLines = null;

    /* Access methods */
    public Account getAccount() {
        return theAccount;
    }

    public DateDayRange getDateRange() {
        return theRange;
    }

    public Money getStartBalance() {
        return theStartBalance;
    }

    public Money getEndBalance() {
        return theEndBalance;
    }

    public Units getStartUnits() {
        return theStartUnits;
    }

    public Units getEndUnits() {
        return theEndUnits;
    }

    public AccountType getActType() {
        return theAccount.getActType();
    }

    public StatementLines getLines() {
        return theLines;
    }

    public StatementLine extractItemAt(long uIndex) {
        return (StatementLine) theLines.get((int) uIndex);
    }

    /* Constructor */
    public Statement(View pView,
                     Account pAccount,
                     DateDayRange pRange) throws JDataException {
        /* Create a copy of the account (plus surrounding list) */
        theView = pView;
        theAccount = pAccount;
        theRange = pRange;
        theLines = new StatementLines(this);

        /* Create an analysis for this statement */
        theAnalysis = new EventAnalysis(theView.getData(), this);
    }

    /**
     * Set the ending balances for the statement
     * @param pAccount the Account Bucket
     */
    protected void setStartBalances(ActDetail pAccount) {
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
     * Set the ending balances for the statement
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
     * Reset the balances
     * @throws JDataException
     */
    public void resetBalances() throws JDataException {
        /* Reset the balances */
        theAnalysis.resetStatementBalance(this);
    }

    /**
     * Does the statement have a money balance
     * @return TRUE/FALSE
     */
    public boolean hasBalance() {
        return (theBucket.getBucketType() != BucketType.EXTERNALDETAIL);
    }

    /**
     * Does the statement have units
     * @return TRUE/FALSE
     */
    public boolean hasUnits() {
        return (theBucket.getBucketType() == BucketType.ASSETDETAIL);
    }

    /**
     * Prepare changes in a statement back into the underlying finance objects
     */
    protected void prepareChanges() {
        /* Prepare the changes from this list */
        theLines.prepareChanges();
    }

    /**
     * Commit/RollBack changes in a statement back into the underlying finance objects
     * @param bCommit <code>true/false</code>
     */
    protected void commitChanges(boolean bCommit) {
        /* Commit/RollBack the changes */
        if (bCommit)
            theLines.commitChanges();
        else
            theLines.rollBackChanges();
    }

    /* The List class */
    public static class StatementLines extends EventList {
        private Statement theStatement = null;

        /* Access functions */
        private Statement getStatement() {
            return theStatement;
        }

        private Account getAccount() {
            return theStatement.getAccount();
        }

        /* Constructors */
        public StatementLines(Statement pStatement) {
            /* Declare the data and set the style */
            super(pStatement.theView.getData());
            setStyle(ListStyle.EDIT);
            theStatement = pStatement;
            setRange(theStatement.getDateRange());
            setBase(theStatement.theView.getData().getEvents());
        }

        /* Obtain extract lists. */
        @Override
        public StatementLines getUpdateList() {
            return null;
        }

        @Override
        public StatementLines getEditList() {
            return null;
        }

        @Override
        public StatementLines getShallowCopy() {
            return null;
        }

        @Override
        public StatementLines getDeepCopy(DataSet<?> pData) {
            return null;
        }

        public StatementLines getDifferences(StatementLines pOld) {
            return null;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return theStatement.theAccount.isLocked();
        }

        /**
         * Add a new item (never used)
         */
        @Override
        public StatementLine addNewItem(DataItem<?> pElement) {
            StatementLine myLine = new StatementLine(this, (StatementLine) pElement);
            add(myLine);
            return myLine;
        }

        /**
         * Add a new item to the edit list
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
         * Obtain the type of the item
         * @return the type of the item
         */
        @Override
        public String itemType() {
            return "StatementLine";
        }
    }

    public static class StatementLine extends Event {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, Event.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /* Field IDs */
        public static final JDataField FIELD_ISCREDIT = FIELD_DEFS.declareEqualityValueField("IsCredit");
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");
        public static final JDataField FIELD_PARTNER = FIELD_DEFS.declareLocalField("Partner");

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* If the field is not an attribute handle normally */
            if (pField == FIELD_ACCOUNT) {
                return JDataObject.FIELD_SKIP;
            }
            if (pField == FIELD_PARTNER) {
                return JDataObject.FIELD_SKIP;
            }
            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * The active set of values
         */
        private EncryptedValueSet theValueSet;

        @Override
        public void declareValues(EncryptedValueSet pValues) {
            super.declareValues(pValues);
            theValueSet = pValues;
        }

        /* Access methods */
        public boolean isCredit() {
            return isCredit(theValueSet);
        }

        public Account getAccount() {
            return isCredit() ? getCredit() : getDebit();
        }

        public AccountType getActType() {
            Account myAccount = getAccount();
            return (myAccount == null) ? null : myAccount.getActType();
        }

        public Account getPartner() {
            return isCredit() ? getCredit() : getDebit();
        }

        public static Boolean isCredit(ValueSet pValueSet) {
            return pValueSet.getValue(FIELD_ISCREDIT, Boolean.class);
        }

        private void setValueIsCredit(Boolean isCredit) {
            theValueSet.setValue(FIELD_ISCREDIT, isCredit);
        }

        private Money theBalance = null;
        private Units theBalUnits = null;
        private Statement theStatement = null;

        /* Access methods */
        public Money getBalance() {
            return theBalance;
        }

        public Units getBalanceUnits() {
            return theBalUnits;
        }

        private ActDetail getBucket() {
            return theStatement.theBucket;
        }

        /* Linking methods */
        @Override
        public Event getBase() {
            return (Event) super.getBase();
        }

        /**
         * Construct a copy of a Line
         * @param pList the list
         * @param pLine The Line
         */
        protected StatementLine(StatementLines pList,
                                StatementLine pLine) {
            /* Set standard values */
            super(pList, pLine);
            theStatement = pList.getStatement();
            pList.setNewId(this);
        }

        /* Standard constructor for a newly inserted line */
        public StatementLine(StatementLines pList) {
            super(pList);
            theStatement = pList.getStatement();
            setDebit(pList.getAccount());
        }

        /* Standard constructor */
        public StatementLine(StatementLines pList,
                             Event pEvent) {
            /* Make this an element */
            super(pList, pEvent);
            theStatement = pList.getStatement();
            if (!Difference.isEqual(getDebit(), pList.getAccount())) {
                setValueIsCredit(true);
            }
            setBase(pEvent);
        }

        /**
         * Set Balances
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
         * Compare the line
         */
        @Override
        public boolean equals(Object that) {
            return (this == that);
        }

        /**
         * Set a new partner
         * @param pPartner the new partner
         */
        public void setPartner(Account pPartner) {
            if (isCredit())
                setDebit(pPartner);
            else
                setCredit(pPartner);
        }

        /**
         * Set a new isCredit indication
         * @param isCredit
         */
        public void setIsCredit(boolean isCredit) {
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
         * Add an error for this item
         * @param pError the error text
         * @param iField the associated field
         */
        @Override
        protected void addError(String pError,
                                JDataField iField) {
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
