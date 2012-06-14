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
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.Event;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TransactionType;

/**
 * Dilution Events relating to stock dilution.
 * @author Tony Washer
 */
public final class DilutionEvent extends ReportItem<DilutionEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(DilutionEvent.class.getSimpleName(),
            ReportItem.theLocalFields);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityField("Date");

    /**
     * Dilution Field Id.
     */
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityField("Dilution");

    /**
     * Event Field Id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_ACCOUNT.equals(pField)) {
            return theAccount;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_DILUTION.equals(pField)) {
            return theDilution;
        }
        if (FIELD_EVENT.equals(pField)) {
            return theEvent;
        }
        return super.getFieldValue(pField);
    }

    /**
     * The Account.
     */
    private Account theAccount = null;

    /**
     * The Date.
     */
    private DateDay theDate = null;

    /**
     * The Dilution.
     */
    private Dilution theDilution = null;

    /**
     * The Event.
     */
    private Event theEvent = null;

    /**
     * Obtain the Account.
     * @return the account
     */
    public Account getAccount() {
        return theAccount;
    }

    /**
     * Obtain the Date.
     * @return the date
     */
    public DateDay getDate() {
        return theDate;
    }

    /**
     * Obtain the Dilution.
     * @return the dilution
     */
    public Dilution getDilution() {
        return theDilution;
    }

    /**
     * Obtain the Event.
     * @return the event
     */
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
    public int compareTo(final Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Make sure that the object is a Dilution Event */
        if (pThat.getClass() != this.getClass()) {
            return -1;
        }

        /* Access the object as a Dilution Event */
        DilutionEvent myThat = (DilutionEvent) pThat;

        /* If the dates differ */
        if (this.getDate() != myThat.getDate()) {
            /* Handle null dates */
            if (this.getDate() == null) {
                return 1;
            }
            if (myThat.getDate() == null) {
                return -1;
            }

            /* Compare the dates */
            iDiff = getDate().compareTo(myThat.getDate());
            if (iDiff != 0) {
                return iDiff;
            }
        }

        /* Compare the account */
        return getAccount().compareTo(myThat.getAccount());
    }

    /**
     * Create a dilution event from an event.
     * @param pList the list
     * @param pEvent the underlying event
     */
    private DilutionEvent(final DilutionEventList pList,
                          final Event pEvent) {
        /* Call super constructor */
        super(pList);

        /* Local variables */
        Account myAccount;
        TransactionType myType;

        /* Access the transaction type */
        myType = pEvent.getTransType();

        /* Switch on the transaction type */
        switch (myType.getTranClass()) {
            case STOCKRIGHTTAKEN:
                myAccount = pEvent.getCredit();
                break;
            case STOCKSPLIT:
            case STOCKRIGHTWAIVED:
            case STOCKDEMERGER:
            default:
                myAccount = pEvent.getDebit();
                break;
        }

        /* Store the values */
        theAccount = myAccount;
        theDate = pEvent.getDate();
        theDilution = pEvent.getDilution();
        theEvent = pEvent;
    }

    /**
     * Create a dilution event from details.
     * @param pList the list
     * @param pAccount the account
     * @param pDate the Date
     * @param pDilution the dilution
     */
    private DilutionEvent(final DilutionEventList pList,
                          final Account pAccount,
                          final DateDay pDate,
                          final Dilution pDilution) {
        /* Call super constructor */
        super(pList);

        /* Store the values */
        theAccount = pAccount;
        theDate = pDate;
        theDilution = pDilution;
    }

    /**
     * List of DilutionEvents.
     */
    public static class DilutionEventList extends ReportList<DilutionEvent> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(
                DilutionEventList.class.getSimpleName(), ReportList.theLocalFields);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The DataSet.
         */
        private FinanceData theData = null;

        /**
         * Constructor.
         * @param pData the DataSet
         */
        public DilutionEventList(final FinanceData pData) {
            super(DilutionEvent.class);
            theData = pData;
        }

        /**
         * Add Dilution Event to List.
         * @param pEvent the base event
         */
        public void addDilution(final Event pEvent) {
            DilutionEvent myDilution;

            /* Create the dilution event */
            myDilution = new DilutionEvent(this, pEvent);

            /* Add it to the list */
            add(myDilution);
        }

        /**
         * Add Dilution Event to List.
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @param pDilution the (possibly) diluted price
         * @throws JDataException on error
         */
        public void addDilution(final String pAccount,
                                final Date pDate,
                                final String pDilution) throws JDataException {
            DilutionEvent myEvent;
            Account myAccount;
            DateDay myDate;
            Dilution myDilution;
            Account.AccountList myAccounts;

            /* Access account list */
            myAccounts = theData.getAccounts();

            /* Search for the account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution account [" + pAccount + "]");
            }

            /* Create the date */
            myDate = new DateDay(pDate);

            /* Record the dilution */
            myDilution = Dilution.parseString(pDilution);
            if (myDilution == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution: " + pDilution);
            }

            /* Create the dilution event */
            myEvent = new DilutionEvent(this, myAccount, myDate, myDilution);

            /* Add it to the list */
            add(myEvent);
        }

        /**
         * Does this account have diluted prices?
         * @param pAccount the account to test
         * @return <code>true</code> if the account has diluted prices, <code>false</code> otherwise
         */
        public boolean hasDilution(final Account pAccount) {
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
         * Obtain the dilution factor for the account and date.
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @return the dilution factor
         */
        public Dilution getDilutionFactor(final Account pAccount,
                                          final DateDay pDate) {
            SortedListIterator<DilutionEvent> myIterator;
            DilutionEvent myEvent;
            Dilution myDilution = new Dilution(Dilution.MAX_VALUE);

            /* No factor if the account has no dilutions */
            if (!hasDilution(pAccount)) {
                return null;
            }

            /* Create the iterator */
            myIterator = listIterator();

            /* Loop through the items */
            while ((myEvent = myIterator.next()) != null) {
                /* If the event is for this account, and if the dilution date is later */
                if ((Difference.isEqual(pAccount, myEvent.getAccount()))
                        && (pDate.compareTo(myEvent.getDate()) < 0)) {
                    /* add in the dilution factor */
                    myDilution = myDilution.getFurtherDilution(myEvent.getDilution());
                }
            }

            /* If there is no dilution at all */
            if (myDilution.getValue() == Dilution.MAX_VALUE) {
                myDilution = null;
            }

            /* Return to caller */
            return myDilution;
        }

        /**
         * Add undiluted price for the account and date.
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @param pPrice the (possibly) diluted price
         * @throws JDataException on error
         */
        public void addPrice(final String pAccount,
                             final Date pDate,
                             final String pPrice) throws JDataException {
            Account myAccount;
            AccountList myAccounts;
            AccountPriceList myPrices;
            DateDay myDate;
            Price myPrice;
            DilutedPrice myDilutedPrice;
            Dilution myDilution;

            /* Obtain the prices and accounts */
            myAccounts = theData.getAccounts();
            myPrices = theData.getPrices();

            /* Search for the account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Price account [" + pAccount + "]");
            }

            /* Create the date */
            myDate = new DateDay(pDate);

            /* If the account has diluted prices for this date */
            myDilution = getDilutionFactor(myAccount, myDate);
            if (myDilution != null) {
                /* Obtain the diluted price */
                myDilutedPrice = DilutedPrice.parseString(pPrice);
                if (myDilutedPrice == null) {
                    throw new JDataException(ExceptionClass.DATA, "Invalid DilutedPrice: " + pPrice);
                }

                /* Obtain the undiluted price */
                myPrice = myDilutedPrice.getPrice(myDilution);

                /* Else this is just a price */
            } else {
                /* Obtain the the price */
                myPrice = Price.parseString(pPrice);
                if (myPrice == null) {
                    throw new JDataException(ExceptionClass.DATA, "Invalid Price: " + pPrice);
                }
            }

            /* Add the item to the list */
            myPrices.addItem(myAccount, myDate, myPrice);
        }
    }
}
