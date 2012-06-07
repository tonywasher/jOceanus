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

import java.util.Date;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ReportItem;
import net.sourceforge.JDataManager.ReportList;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.DilutedPrice;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JSortedList.SortedListIterator;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;

public class DilutionEvent extends ReportItem<DilutionEvent> {
    /**
     * Report fields
     */
    private static final JDataFields theLocalFields = new JDataFields(DilutionEvent.class.getSimpleName(),
            ReportItem.theLocalFields);

    /* Called from constructor */
    @Override
    public JDataFields declareFields() {
        return theLocalFields;
    }

    /* Field IDs */
    public static final JDataField FIELD_ACCOUNT = theLocalFields.declareEqualityField("Account");
    public static final JDataField FIELD_DATE = theLocalFields.declareEqualityField("Date");
    public static final JDataField FIELD_DILUTION = theLocalFields.declareEqualityField("Dilution");
    public static final JDataField FIELD_EVENT = theLocalFields.declareEqualityField("Event");

    @Override
    public Object getFieldValue(JDataField pField) {
        if (pField == FIELD_ACCOUNT)
            return theAccount;
        if (pField == FIELD_DATE)
            return theDate;
        if (pField == FIELD_DILUTION)
            return theDilution;
        if (pField == FIELD_EVENT)
            return theEvent;
        return super.getFieldValue(pField);
    }

    /* Fields */
    private Account theAccount = null;
    private DateDay theDate = null;
    private Dilution theDilution = null;
    private Event theEvent = null;

    /* Access methods */
    public Account getAccount() {
        return theAccount;
    }

    public DateDay getDate() {
        return theDate;
    }

    public Dilution getDilution() {
        return theDilution;
    }

    public Event getEvent() {
        return theEvent;
    }

    /**
     * Compare this DilutionEvent to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is a Dilution Event */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a Dilution Event */
        DilutionEvent myThat = (DilutionEvent) pThat;

        /* If the dates differ */
        if (this.getDate() != myThat.getDate()) {
            /* Handle null dates */
            if (this.getDate() == null)
                return 1;
            if (myThat.getDate() == null)
                return -1;

            /* Compare the dates */
            iDiff = getDate().compareTo(myThat.getDate());
            if (iDiff != 0)
                return iDiff;
        }

        /* Compare the account */
        return getAccount().compareTo(myThat.getAccount());
    }

    /**
     * Create a dilution event from an event
     * @param pList the list
     * @param pEvent the underlying event
     */
    private DilutionEvent(DilutionEventList pList,
                          Event pEvent) {
        /* Call super constructor */
        super(pList);

        /* Local variables */
        Account myAccount;
        TransactionType myType;

        /* Access the transaction type */
        myType = pEvent.getTransType();

        /* Switch on the transaction type */
        switch (myType.getTranClass()) {
            case STOCKSPLIT:
            case STOCKRIGHTWAIVED:
            case STOCKDEMERGER:
            default:
                myAccount = pEvent.getDebit();
                break;
            case STOCKRIGHTTAKEN:
                myAccount = pEvent.getCredit();
                break;
        }

        /* Store the values */
        theAccount = myAccount;
        theDate = pEvent.getDate();
        theDilution = pEvent.getDilution();
        theEvent = pEvent;
    }

    /**
     * Create a dilution event from details
     * @param pList the list
     * @param pAccount the account
     * @param pDate the Date
     * @param pDilution the dilution
     */
    private DilutionEvent(DilutionEventList pList,
                          Account pAccount,
                          DateDay pDate,
                          Dilution pDilution) {
        /* Call super constructor */
        super(pList);

        /* Store the values */
        theAccount = pAccount;
        theDate = pDate;
        theDilution = pDilution;
    }

    /**
     * List of DilutionEvents
     */
    public static class DilutionEventList extends ReportList<DilutionEvent> {
        /**
         * Report fields
         */
        private static final JDataFields theLocalFields = new JDataFields(
                DilutionEventList.class.getSimpleName(), ReportList.theLocalFields);

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return theLocalFields;
        }

        /* Members */
        FinanceData theData = null;

        /**
         * Constructor
         * @param pData the DataSet
         */
        public DilutionEventList(FinanceData pData) {
            super(DilutionEvent.class);
            theData = pData;
        }

        /**
         * Add Dilution Event to List
         * @param pEvent the base event
         */
        public void addDilution(Event pEvent) {
            DilutionEvent myDilution;

            /* Create the dilution event */
            myDilution = new DilutionEvent(this, pEvent);

            /* Add it to the list */
            add(myDilution);
        }

        /**
         * Add Dilution Event to List
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @param pDilution the (possibly) diluted price
         * @throws JDataException
         */
        public void addDilution(String pAccount,
                                Date pDate,
                                String pDilution) throws JDataException {
            DilutionEvent myEvent;
            Account myAccount;
            DateDay myDate;
            Dilution myDilution;
            Account.AccountList myAccounts;

            /* Access account list */
            myAccounts = theData.getAccounts();

            /* Search for the account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution account [" + pAccount + "]");

            /* Create the date */
            myDate = new DateDay(pDate);

            /* Record the dilution */
            myDilution = Dilution.parseString(pDilution);
            if (myDilution == null)
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution: " + pDilution);

            /* Create the dilution event */
            myEvent = new DilutionEvent(this, myAccount, myDate, myDilution);

            /* Add it to the list */
            add(myEvent);
        }

        /**
         * Does this account have diluted prices
         * @param pAccount the account to test
         * @return <code>true</code> if the account has diluted prices, <code>false</code> otherwise
         */
        public boolean hasDilution(Account pAccount) {
            SortedListIterator<DilutionEvent> myIterator;
            DilutionEvent myEvent;
            boolean myResult = false;

            /* Create the iterator */
            myIterator = listIterator();

            /* Loop through the items */
            while ((myEvent = myIterator.next()) != null) {
                /* If the event is for this account */
                if (!Difference.isEqual(pAccount, myEvent.getAccount())) {
                    /* Set result and break loop */
                    myResult = true;
                    break;
                }
            }

            /* Return to caller */
            return myResult;
        }

        /**
         * Obtain the dilution factor for the account and date
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @return the dilution factor
         */
        public Dilution getDilutionFactor(Account pAccount,
                                          DateDay pDate) {
            SortedListIterator<DilutionEvent> myIterator;
            DilutionEvent myEvent;
            Dilution myDilution = new Dilution(Dilution.MAX_VALUE);

            /* Create the iterator */
            myIterator = listIterator();

            /* Loop through the items */
            while ((myEvent = myIterator.next()) != null) {
                /* If the event is for this account */
                if (Difference.isEqual(pAccount, myEvent.getAccount())) {
                    /* If the dilution date is later */
                    if (pDate.compareTo(myEvent.getDate()) < 0) {
                        /* add in the dilution factor */
                        myDilution = myDilution.getFurtherDilution(myEvent.getDilution());
                    }
                }
            }

            /* If there is no dilution at all */
            if (myDilution.getValue() == Dilution.MAX_VALUE)
                myDilution = null;

            /* Return to caller */
            return myDilution;
        }

        /**
         * Add undiluted price for the account and date
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @param pPrice the (possibly) diluted price
         * @throws JDataException
         */
        public void addPrice(String pAccount,
                             Date pDate,
                             String pPrice) throws JDataException {
            Account myAccount;
            Account.AccountList myAccounts;
            AccountPrice.AccountPriceList myPrices;
            DateDay myDate;
            Price myPrice;
            DilutedPrice myDilutedPrice;
            Dilution myDilution;

            /* Obtain the prices and accounts */
            myAccounts = theData.getAccounts();
            myPrices = theData.getPrices();

            /* Search for the account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, "Invalid Price account [" + pAccount + "]");

            /* Create the date */
            myDate = new DateDay(pDate);

            /* If the account has diluted prices for this date */
            if ((hasDilution(myAccount)) && ((myDilution = getDilutionFactor(myAccount, myDate)) != null)) {
                /* Obtain the diluted price */
                myDilutedPrice = DilutedPrice.parseString(pPrice);
                if (myDilutedPrice == null)
                    throw new JDataException(ExceptionClass.DATA, "Invalid DilutedPrice: " + pPrice);

                /* Obtain the undiluted price */
                myPrice = myDilutedPrice.getPrice(myDilution);
            }

            /* Else this is just a price */
            else {
                /* Obtain the the price */
                myPrice = Price.parseString(pPrice);
                if (myPrice == null)
                    throw new JDataException(ExceptionClass.DATA, "Invalid Price: " + pPrice);
            }

            /* Add the item to the list */
            myPrices.addItem(myAccount, myDate, myPrice);

            /* Return to caller */
            return;
        }
    }
}
