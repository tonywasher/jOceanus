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

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JSortedList.OrderedIdItem;
import net.sourceforge.JSortedList.OrderedIdList;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.StaticClass.TransClass;
import uk.co.tolcroft.finance.data.TransactionType;

/**
 * Income Breakdown analysis.
 * @author Tony Washer
 */
public class IncomeBreakdown implements JDataContents {
    /**
     * Buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(IncomeBreakdown.class.getSimpleName());

    /**
     * Salary Field Id.
     */
    public static final JDataField FIELD_SALARY = FIELD_DEFS.declareLocalField("Salary");

    /**
     * Rental Field Id.
     */
    public static final JDataField FIELD_RENTAL = FIELD_DEFS.declareLocalField("Rental");

    /**
     * Interest Field Id.
     */
    public static final JDataField FIELD_INTEREST = FIELD_DEFS.declareLocalField("TaxedInterest");

    /**
     * Tax Free Interest Field Id.
     */
    public static final JDataField FIELD_TFINTEREST = FIELD_DEFS.declareLocalField("TaxFreeInterest");

    /**
     * Dividend Field Id.
     */
    public static final JDataField FIELD_DIVIDEND = FIELD_DEFS.declareLocalField("TaxedDividends");

    /**
     * Tax Free Dividend Field Id.
     */
    public static final JDataField FIELD_TFDIVIDEND = FIELD_DEFS.declareLocalField("TaxFreeDividends");

    /**
     * Unit Trust Dividend Field Id.
     */
    public static final JDataField FIELD_UTDIVIDEND = FIELD_DEFS.declareLocalField("UnitTrustDividend");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_SALARY.equals(pField)) {
            return (theSalary.size() > 0) ? theSalary : JDataFieldValue.SkipField;
        }
        if (FIELD_RENTAL.equals(pField)) {
            return (theRental.size() > 0) ? theRental : JDataFieldValue.SkipField;
        }
        if (FIELD_INTEREST.equals(pField)) {
            return (theTaxedInterest.size() > 0) ? theTaxedInterest : JDataFieldValue.SkipField;
        }
        if (FIELD_TFINTEREST.equals(pField)) {
            return (theTaxFreeInterest.size() > 0) ? theTaxFreeInterest : JDataFieldValue.SkipField;
        }
        if (FIELD_DIVIDEND.equals(pField)) {
            return (theTaxedDividend.size() > 0) ? theTaxedDividend : JDataFieldValue.SkipField;
        }
        if (FIELD_TFDIVIDEND.equals(pField)) {
            return (theTaxFreeDividend.size() > 0) ? theTaxFreeDividend : JDataFieldValue.SkipField;
        }
        if (FIELD_UTDIVIDEND.equals(pField)) {
            return (theUnitTrustDividend.size() > 0) ? theUnitTrustDividend : JDataFieldValue.SkipField;
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Salary analysis.
     */
    private final RecordList theSalary;

    /**
     * The Rental analysis.
     */
    private final RecordList theRental;

    /**
     * The Taxable Interest analysis.
     */
    private final RecordList theTaxedInterest;

    /**
     * The TaxFree Interest analysis.
     */
    private final RecordList theTaxFreeInterest;

    /**
     * The Taxable Dividend analysis.
     */
    private final RecordList theTaxedDividend;

    /**
     * The TaxFree Dividend analysis.
     */
    private final RecordList theTaxFreeDividend;

    /**
     * The Taxable UnitTrust Dividend analysis.
     */
    private final RecordList theUnitTrustDividend;

    /**
     * Obtain Salary totals.
     * @return the totals
     */
    public RecordList getSalary() {
        return theSalary;
    }

    /**
     * Obtain Rental totals.
     * @return the totals
     */
    public RecordList getRental() {
        return theRental;
    }

    /**
     * Obtain Taxable interest totals.
     * @return the totals
     */
    public RecordList getTaxableInterest() {
        return theTaxedInterest;
    }

    /**
     * Obtain Tax Free interest totals.
     * @return the totals
     */
    public RecordList getTaxFreeInterest() {
        return theTaxFreeInterest;
    }

    /**
     * Obtain Taxable Dividend totals.
     * @return the totals
     */
    public RecordList getTaxableDividend() {
        return theTaxedDividend;
    }

    /**
     * Obtain Tax Free Dividend totals.
     * @return the totals
     */
    public RecordList getTaxFreeDividend() {
        return theTaxFreeDividend;
    }

    /**
     * Obtain Unit Trust Dividend totals.
     * @return the totals
     */
    public RecordList getUnitTrustDividend() {
        return theUnitTrustDividend;
    }

    /**
     * Constructor.
     * @param pData the DataSet
     */
    protected IncomeBreakdown(final FinanceData pData) {
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
     * Process event.
     * @param pEvent the event to process
     */
    protected void processEvent(final Event pEvent) {
        AccountRecord myRecord;
        Account myDebit = pEvent.getDebit();
        TransactionType myTrans = pEvent.getTransType();

        /* Switch on Transaction Type */
        switch (myTrans.getTranClass()) {
            case INTEREST:
                if (myDebit.isTaxFree()) {
                    myRecord = theTaxFreeInterest.findAccountRecord(myDebit.getParent());
                } else {
                    myRecord = theTaxedInterest.findAccountRecord(myDebit.getParent());
                }
                myRecord.processEvent(pEvent);
                break;
            case DIVIDEND:
                if (myDebit.isTaxFree()) {
                    myRecord = theTaxFreeDividend.findAccountRecord(myDebit.isChild()
                                                                                     ? myDebit.getParent()
                                                                                     : myDebit);
                } else if (myDebit.isUnitTrust()) {
                    myRecord = theUnitTrustDividend.findAccountRecord(myDebit.isChild()
                                                                                       ? myDebit.getParent()
                                                                                       : myDebit);
                } else {
                    myRecord = theTaxedDividend.findAccountRecord(myDebit.isChild()
                                                                                   ? myDebit.getParent()
                                                                                   : myDebit);
                }
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
            default:
                break;
        }
    }

    /**
     * Totals class.
     */
    protected static class IncomeTotals implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(IncomeBreakdown.class.getSimpleName());

        /**
         * Gross field id.
         */
        public static final JDataField FIELD_GROSS = FIELD_DEFS.declareLocalField("GrossIncome");

        /**
         * Net field id.
         */
        public static final JDataField FIELD_NET = FIELD_DEFS.declareLocalField("NetIncome");

        /**
         * Tax Credit field id.
         */
        public static final JDataField FIELD_TAX = FIELD_DEFS.declareLocalField("TaxCredit");

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_GROSS.equals(pField)) {
                return theGrossIncome;
            }
            if (FIELD_NET.equals(pField)) {
                return theNetIncome;
            }
            if (FIELD_TAX.equals(pField)) {
                return theTaxCredit;
            }
            return JDataFieldValue.UnknownField;
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        /**
         * The Gross income.
         */
        private Money theGrossIncome = new Money(0);

        /**
         * The Net income.
         */
        private Money theNetIncome = new Money(0);

        /**
         * The Tax Credit.
         */
        private Money theTaxCredit = new Money(0);

        /**
         * Obtain the Gross Income.
         * @return the gross income
         */
        protected Money getGrossIncome() {
            return theGrossIncome;
        }

        /**
         * Obtain the Net Income.
         * @return the net income
         */
        protected Money getNetIncome() {
            return theNetIncome;
        }

        /**
         * Obtain the Tax Credit.
         * @return the tax credit
         */
        protected Money getTaxCredit() {
            return theTaxCredit;
        }
    }

    /**
     * The Account record class.
     */
    protected static final class AccountRecord implements OrderedIdItem<Integer>, JDataContents,
            Comparable<AccountRecord> {
        /**
         * Object name.
         */
        public static final String OBJECT_NAME = AccountRecord.class.getSimpleName();

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public String formatObject() {
            return getDataFields().getName();
        }

        /**
         * Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        /**
         * Totals Field Id.
         */
        public static final JDataField FIELD_TOTALS = FIELD_DEFS.declareLocalField("Totals");

        /**
         * List Totals Field Id.
         */
        public static final JDataField FIELD_LISTTOTALS = FIELD_DEFS.declareLocalField("ListTotals");

        /**
         * Events Field Id.
         */
        public static final JDataField FIELD_EVENTS = FIELD_DEFS.declareLocalField("Events");

        /**
         * Children Field Id.
         */
        public static final JDataField FIELD_CHILDREN = FIELD_DEFS.declareLocalField("Children");

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* If the field is not an attribute handle normally */
            if (FIELD_ACCOUNT.equals(pField)) {
                return theAccount;
            }
            if (FIELD_TOTALS.equals(pField)) {
                return theTotals;
            }
            if (FIELD_LISTTOTALS.equals(pField)) {
                return theListTotals;
            }
            if (FIELD_EVENTS.equals(pField)) {
                return (theEvents.size() > 0) ? theEvents : JDataFieldValue.SkipField;
            }
            if (FIELD_CHILDREN.equals(pField)) {
                return (theChildren.size() > 0) ? theChildren : JDataFieldValue.SkipField;
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        /**
         * The account for the record.
         */
        private final Account theAccount;

        /**
         * The Totals.
         */
        private final IncomeTotals theTotals;

        /**
         * The List Totals.
         */
        private final IncomeTotals theListTotals;

        /**
         * The Events relating to this account.
         */
        private final EventList theEvents;

        /**
         * The Children relating to this account.
         */
        private final RecordList theChildren;

        /**
         * Obtain the Account.
         * @return the account
         */
        protected Account getAccount() {
            return theAccount;
        }

        /**
         * Obtain the Totals.
         * @return the totals
         */
        protected IncomeTotals getTotals() {
            return theTotals;
        }

        /**
         * Obtain the List of events.
         * @return the events
         */
        protected EventList getEvents() {
            return theEvents;
        }

        /**
         * Obtain the List of children.
         * @return the children
         */
        protected RecordList getChildren() {
            return theChildren;
        }

        @Override
        public Integer getOrderedId() {
            return theAccount.getId();
        }

        /**
         * Constructor.
         * @param pList the list to which the record belongs
         * @param pAccount the account
         */
        private AccountRecord(final RecordList pList,
                              final Account pAccount) {
            /* Access the Data for the account */
            FinanceData myData = pList.getData();

            /* Store parameter */
            theAccount = pAccount;

            /* Create the totals and access those of the list */
            theTotals = new IncomeTotals();
            theListTotals = pList.theTotals;

            /* Build the name of the child list */
            StringBuilder myNameBuilder = new StringBuilder(BUFFER_LEN);
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
         * Process Event.
         * @param pEvent the event to process
         */
        private void processEvent(final Event pEvent) {
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
            } else {
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

                /* else we need to record the event */
            } else {
                /* Add a copy of the event to the list */
                theEvents.addNewItem(pEvent);
            }
        }

        @Override
        public int compareTo(final AccountRecord pThat) {
            /* Handle the trivial cases */
            if (this == pThat) {
                return 0;
            }
            if (pThat == null) {
                return -1;
            }

            /* Compare Accounts */
            return theAccount.compareTo(pThat.theAccount);
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

            /* Access as AccountRecord */
            AccountRecord myThat = (AccountRecord) pThat;

            /* Check equality */
            return Difference.isEqual(getAccount(), myThat.getAccount());
        }

        @Override
        public int hashCode() {
            return getAccount().hashCode();
        }
    }

    /**
     * The RecordList class.
     */
    public static class RecordList extends OrderedIdList<Integer, AccountRecord> implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(RecordList.class.getSimpleName());

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

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UnknownField;
        }

        /**
         * The DataSet that this list is based on.
         */
        private final FinanceData theData;

        /**
         * The Totals for the record list.
         */
        private IncomeTotals theTotals = new IncomeTotals();

        /**
         * The Name for the record list.
         */
        private final String theName;

        /**
         * Access the DataSet for a RecordList.
         * @return the DataSet.
         */
        private FinanceData getData() {
            return theData;
        }

        /**
         * Access the Totals.
         * @return the Totals
         */
        public IncomeTotals getTotals() {
            return theTotals;
        }

        /**
         * Access the Name.
         * @return the Name
         */
        public String getName() {
            return theName;
        }

        /**
         * Construct a top-level List.
         * @param pData the data
         * @param pName the name of the list
         */
        public RecordList(final FinanceData pData,
                          final String pName) {
            super(AccountRecord.class);
            theData = pData;
            theName = pName;
        }

        /**
         * Obtain the AccountRecord for a given account.
         * @param pAccount the account
         * @return the record
         */
        protected AccountRecord findAccountRecord(final Account pAccount) {
            /* Locate the record in the list */
            AccountRecord myRecord = findItemById(pAccount.getId());

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
    }
}
