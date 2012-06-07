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
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDecimal.Money;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.StaticClass.TransClass;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;

public class IncomeBreakdown implements JDataContents {
    /**
     * Report fields
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(IncomeBreakdown.class.getSimpleName());

    /* Field IDs */
    public static final JDataField FIELD_SALARY = FIELD_DEFS.declareLocalField("Salary");
    public static final JDataField FIELD_RENTAL = FIELD_DEFS.declareLocalField("Rental");
    public static final JDataField FIELD_INTEREST = FIELD_DEFS.declareLocalField("TaxedInterest");
    public static final JDataField FIELD_TFINTEREST = FIELD_DEFS.declareLocalField("TaxFreeInterest");
    public static final JDataField FIELD_DIVIDEND = FIELD_DEFS.declareLocalField("TaxedDividends");
    public static final JDataField FIELD_TFDIVIDEND = FIELD_DEFS.declareLocalField("TaxFreeDividends");
    public static final JDataField FIELD_UTDIVIDEND = FIELD_DEFS.declareLocalField("UnitTrustDividend");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(JDataField pField) {
        if (pField == FIELD_SALARY)
            return theSalary;
        if (pField == FIELD_RENTAL)
            return theRental;
        if (pField == FIELD_INTEREST)
            return theTaxedInterest;
        if (pField == FIELD_TFINTEREST)
            return theTaxFreeInterest;
        if (pField == FIELD_DIVIDEND)
            return theTaxedDividend;
        if (pField == FIELD_TFDIVIDEND)
            return theTaxFreeDividend;
        if (pField == FIELD_UTDIVIDEND)
            return theUnitTrustDividend;
        return null;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Salary analysis
     */
    private RecordList theSalary = null;

    /**
     * The Rental analysis
     */
    private RecordList theRental = null;

    /**
     * The Taxable Interest analysis
     */
    private RecordList theTaxedInterest = null;

    /**
     * The TaxFree Interest analysis
     */
    private RecordList theTaxFreeInterest = null;

    /**
     * The Taxable Dividend analysis
     */
    private RecordList theTaxedDividend = null;

    /**
     * The TaxFree Dividend analysis
     */
    private RecordList theTaxFreeDividend = null;

    /**
     * The Taxable UnitTrust Dividend analysis
     */
    private RecordList theUnitTrustDividend = null;

    /* Access functions */
    public RecordList getSalary() {
        return theSalary;
    }

    public RecordList getRental() {
        return theRental;
    }

    public RecordList getTaxableInterest() {
        return theTaxedInterest;
    }

    public RecordList getTaxFreeInterest() {
        return theTaxFreeInterest;
    }

    public RecordList getTaxableDividend() {
        return theTaxedDividend;
    }

    public RecordList getTaxFreeDividend() {
        return theTaxFreeDividend;
    }

    public RecordList getUnitTrustDividend() {
        return theUnitTrustDividend;
    }

    /**
     * Constructor
     * @param pData the DataSet
     */
    protected IncomeBreakdown(FinanceData pData) {
        /* Allocate lists */
        theSalary = new RecordList(pData, "Salary");
        theRental = new RecordList(pData, "Rental");
        theTaxedInterest = new RecordList(pData, "TaxedInterest");
        theTaxFreeInterest = new RecordList(pData, "TaxFreeInterest");
        theTaxedDividend = new RecordList(pData, "TaxedDividend");
        theTaxFreeDividend = new RecordList(pData, "TaxFreeDividend");
        theUnitTrustDividend = new RecordList(pData, "UnitTrustDividend");
    }

    /**
     * Process event
     * @param pEvent the event to process
     */
    protected void processEvent(Event pEvent) {
        AccountRecord myRecord;
        Account myDebit = pEvent.getDebit();
        TransactionType myTrans = pEvent.getTransType();

        /* Switch on Transaction Type */
        switch (myTrans.getTranClass()) {
            case INTEREST:
                if (myDebit.isTaxFree())
                    myRecord = theTaxFreeInterest.findAccountRecord(myDebit.getParent());
                else
                    myRecord = theTaxedInterest.findAccountRecord(myDebit.getParent());
                myRecord.processEvent(pEvent);
                break;
            case DIVIDEND:
                if (myDebit.isTaxFree())
                    myRecord = theTaxFreeDividend.findAccountRecord(myDebit.isChild()
                                                                                     ? myDebit.getParent()
                                                                                     : myDebit);
                else if (myDebit.isUnitTrust())
                    myRecord = theUnitTrustDividend.findAccountRecord(myDebit.isChild()
                                                                                       ? myDebit.getParent()
                                                                                       : myDebit);
                else
                    myRecord = theTaxedDividend.findAccountRecord(myDebit.isChild()
                                                                                   ? myDebit.getParent()
                                                                                   : myDebit);
                myRecord.processEvent(pEvent);
                break;
            case TAXEDINCOME:
            case BENEFIT:
            case NATINSURANCE:
                myRecord = theSalary.findAccountRecord(myDebit);
                myRecord.processEvent(pEvent);
                break;
            case RENTALINCOME:
                myRecord = theRental.findAccountRecord(myDebit);
                myRecord.processEvent(pEvent);
                break;
        }
    }

    /**
     * Totals
     */
    protected static class IncomeTotals implements JDataContents {
        /**
         * Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(IncomeBreakdown.class.getSimpleName());

        /* Field IDs */
        public static final JDataField FIELD_GROSS = FIELD_DEFS.declareLocalField("GrossIncome");
        public static final JDataField FIELD_NET = FIELD_DEFS.declareLocalField("NetIncome");
        public static final JDataField FIELD_TAX = FIELD_DEFS.declareLocalField("TaxCredit");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_GROSS)
                return theGrossIncome;
            if (pField == FIELD_NET)
                return theNetIncome;
            if (pField == FIELD_TAX)
                return theTaxCredit;
            return null;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        /**
         * The Gross income
         */
        private Money theGrossIncome = new Money(0);

        /**
         * The Net income
         */
        private Money theNetIncome = new Money(0);

        /**
         * The Tax Credit
         */
        private Money theTaxCredit = new Money(0);

        /**
         * Obtain the Gross Income
         * @return the gross income
         */
        protected Money getGrossIncome() {
            return theGrossIncome;
        }

        /**
         * Obtain the Net Income
         * @return the net income
         */
        protected Money getNetIncome() {
            return theNetIncome;
        }

        /**
         * Obtain the Tax Credit
         * @return the tax credit
         */
        protected Money getTaxCredit() {
            return theTaxCredit;
        }
    }

    /**
     * The Account record class
     */
    protected static class AccountRecord extends DataItem<AccountRecord> {
        /**
         * Object name
         */
        public static String objName = AccountRecord.class.getSimpleName();

        /**
         * Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(objName, DataItem.FIELD_DEFS);

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /* Field IDs */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");
        public static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField("Totals");
        public static final JDataField FIELD_LISTTOTALS = FIELD_DEFS.declareLocalField("ListTotals");
        public static final JDataField FIELD_EVENTS = FIELD_DEFS.declareLocalField("Events");
        public static final JDataField FIELD_CHILDREN = FIELD_DEFS.declareLocalField("Children");

        @Override
        public Object getFieldValue(JDataField pField) {
            /* If the field is not an attribute handle normally */
            if (pField == FIELD_ACCOUNT)
                return theAccount;
            if (pField == FIELD_TOTALS)
                return theTotals;
            if (pField == FIELD_LISTTOTALS)
                return theListTotals;
            if (pField == FIELD_EVENTS)
                return theEvents;
            if (pField == FIELD_CHILDREN)
                return theChildren;

            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * The account for the record
         */
        private Account theAccount = null;

        /**
         * The Totals
         */
        private IncomeTotals theTotals = null;

        /**
         * The List Totals
         */
        private IncomeTotals theListTotals = null;

        /**
         * The Events relating to this account
         */
        private EventList theEvents = null;

        /**
         * The Children relating to this account
         */
        private RecordList theChildren = null;

        /**
         * Obtain the Account
         * @return the account
         */
        protected Account getAccount() {
            return theAccount;
        }

        /**
         * Obtain the Totals
         * @return the totals
         */
        protected IncomeTotals getTotals() {
            return theTotals;
        }

        /**
         * Obtain the List of events
         * @return the events
         */
        protected EventList getEvents() {
            return theEvents;
        }

        /**
         * Obtain the List of children
         * @return the children
         */
        protected RecordList getChildren() {
            return theChildren;
        }

        /**
         * Constructor
         * @param pList the list to which the record belongs
         * @param pAccount the account
         */
        private AccountRecord(RecordList pList,
                              Account pAccount) {
            /* Call super-constructor */
            super(pList, pAccount.getId());

            /* Access the Data for the account */
            FinanceData myData = pList.getData();

            /* Store parameter */
            theAccount = pAccount;

            /* Create the totals and access those of the list */
            theTotals = new IncomeTotals();
            theListTotals = pList.theTotals;

            /* Build the name of the child list */
            StringBuilder myNameBuilder = new StringBuilder(100);
            myNameBuilder.append(pList.getName());
            myNameBuilder.append("-");
            myNameBuilder.append(pAccount.getName());
            String myName = myNameBuilder.toString();

            /* Create the list of children */
            theChildren = new RecordList(myData, myName);

            /* Create the event list */
            theEvents = myData.getEvents().getViewList();
        }

        /**
         * Process Event
         * @param pEvent the event to process
         */
        private void processEvent(Event pEvent) {
            /* Add the event to the list */
            // theEvents.addNewItem(pEvent);

            /* Access values */
            Money myAmount = pEvent.getAmount();
            Money myTax = pEvent.getTaxCredit();
            Account myDebit = pEvent.getDebit();
            TransactionType myTrans = pEvent.getTransType();

            /* If we are NatInsurance/Benefit */
            if ((myTrans.getTranClass() == TransClass.NATINSURANCE)
                    || (myTrans.getTranClass() == TransClass.BENEFIT)) {
                /* Just add to gross */
                theTotals.theGrossIncome.addAmount(myAmount);
                theListTotals.theGrossIncome.addAmount(myAmount);
            }

            else {
                /* Add to gross and net */
                theTotals.theGrossIncome.addAmount(myAmount);
                theTotals.theNetIncome.addAmount(myAmount);
                theListTotals.theGrossIncome.addAmount(myAmount);
                theListTotals.theNetIncome.addAmount(myAmount);

                /* If we have a tax credit */
                if (myTax != null) {
                    /* Add to gross and tax */
                    theTotals.theGrossIncome.addAmount(myTax);
                    theTotals.theTaxCredit.addAmount(myTax);
                    theListTotals.theGrossIncome.addAmount(myTax);
                    theListTotals.theTaxCredit.addAmount(myTax);
                }
            }

            /* If the debit account is a child */
            if (!Difference.isEqual(theAccount, myDebit)) {
                /* Find the relevant account */
                AccountRecord myChild = theChildren.findAccountRecord(myDebit);

                /* Process the record for the child */
                myChild.processEvent(pEvent);
            }

            /* else we need to record the event */
            else {
                /* Add a copy of the event to the list */
                theEvents.addNewItem(pEvent);
            }
        }

        @Override
        public boolean equals(Object that) {
            return this == that;
        }

        @Override
        public int compareTo(Object pThat) {
            /* Handle the trivial cases */
            if (this == pThat)
                return 0;
            if (pThat == null)
                return -1;

            /* Make sure that the object is an AccountRecord */
            if (!(pThat instanceof AccountRecord))
                return -1;

            /* Access as AccountRecord and compare Accounts */
            AccountRecord myThat = (AccountRecord) pThat;
            return theAccount.compareTo(myThat.theAccount);
        }
    }

    /**
     * The RecordList class
     */
    public static class RecordList extends DataList<RecordList, AccountRecord> {
        /**
         * The name of the object
         */
        private static final String listName = "RecordList";

        @Override
        public String listName() {
            return listName;
        }

        /**
         * The DataSet that this list is based on
         */
        private FinanceData theData = null;

        /**
         * The Totals for the record list
         */
        private IncomeTotals theTotals = new IncomeTotals();

        /**
         * The Name for the record list
         */
        private String theName = null;

        /**
         * Access the DataSet for a RecordList
         * @return the DataSet
         */
        private FinanceData getData() {
            return theData;
        }

        /**
         * Access the Totals
         * @return the Totals
         */
        public IncomeTotals getTotals() {
            return theTotals;
        }

        /**
         * Access the Name
         * @return the Name
         */
        public String getName() {
            return theName;
        }

        /**
         * Construct a top-level List
         * @param pData the data
         * @param pName the name of the list
         */
        public RecordList(FinanceData pData,
                          String pName) {
            super(RecordList.class, AccountRecord.class, ListStyle.VIEW, false);
            theData = pData;
            theName = pName;
        }

        /* Obtain extract lists. */
        @Override
        public RecordList getUpdateList() {
            return null;
        }

        @Override
        public RecordList getEditList() {
            return null;
        }

        @Override
        public RecordList getShallowCopy() {
            return null;
        }

        @Override
        public RecordList getDeepCopy(DataSet<?> pData) {
            return null;
        }

        @Override
        public RecordList getDifferences(RecordList pOld) {
            return null;
        }

        @Override
        public AccountRecord addNewItem(DataItem<?> pElement) {
            return null;
        }

        @Override
        public AccountRecord addNewItem() {
            return null;
        }

        /**
         * Obtain the AccountRecord for a given account
         * @param pAccount the account
         * @return the record
         */
        protected AccountRecord findAccountRecord(Account pAccount) {
            /* Locate the record in the list */
            AccountRecord myRecord = (AccountRecord) searchFor(pAccount.getId());

            /* If the record does not yet exist */
            if (myRecord == null) {
                /* Allocate the record */
                myRecord = new AccountRecord(this, pAccount);

                /* Add to the list */
                add(myRecord);
            }

            /* Return the record */
            return myRecord;
        }

        /**
         * Add child entries for the debug object
         * @param pManager the debug manager
         * @param pParent the parent debug entry
         */
        public void addChildEntries(JDataManager pManager,
                                    JDataEntry pParent) {
            DataListIterator<AccountRecord> myIterator = listIterator();
            AccountRecord myRecord;

            /* Loop through the records */
            while ((myRecord = myIterator.previous()) != null) {
                /* Add child */
                pManager.addChildEntry(pParent, myRecord.getAccount().getName(), myRecord);
            }
        }
    }
}
