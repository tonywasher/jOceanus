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
import java.util.Iterator;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.DilutedPrice;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JSortedList.OrderedIdItem;
import net.sourceforge.JSortedList.OrderedIdList;
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
public final class DilutionEvent implements OrderedIdItem<Integer>, JDataContents, Comparable<DilutionEvent> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(DilutionEvent.class.getSimpleName());

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
        return JDataFieldValue.UnknownField;
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

    @Override
    public Integer getOrderedId() {
        return getEvent().getId();
    }

    @Override
    public int compareTo(final DilutionEvent pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the dates differ */
        iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the account */
        return getAccount().compareTo(pThat.getAccount());
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

        /* Access as Dilution Event */
        DilutionEvent myThat = (DilutionEvent) pThat;

        /* Check equality */
        return Difference.isEqual(getDate(), myThat.getDate())
                && Difference.isEqual(getAccount(), myThat.getAccount())
                && Difference.isEqual(getEvent(), myThat.getEvent());
    }

    @Override
    public int hashCode() {
        int hash = getDate().hashCode();
        hash ^= getAccount().hashCode();
        if (getEvent() != null) {
            hash ^= getEvent().hashCode();
        }
        return hash;
    }

    /**
     * Create a dilution event from an event.
     * @param pEvent the underlying event
     */
    private DilutionEvent(final Event pEvent) {
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
     * @param pAccount the account
     * @param pDate the Date
     * @param pDilution the dilution
     */
    private DilutionEvent(final Account pAccount,
                          final DateDay pDate,
                          final Dilution pDilution) {
        /* Store the values */
        theAccount = pAccount;
        theDate = pDate;
        theDilution = pDilution;
    }

    /**
     * List of DilutionEvents.
     */
    public static class DilutionEventList extends OrderedIdList<Integer, DilutionEvent> implements
            JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(DilutionEventList.class.getSimpleName());

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
            myDilution = new DilutionEvent(pEvent);

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
            /* Access account list */
            AccountList myAccounts = theData.getAccounts();

            /* Search for the account */
            Account myAccount = myAccounts.findItemByName(pAccount);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution account [" + pAccount + "]");
            }

            /* Create the date */
            DateDay myDate = new DateDay(pDate);

            /* Record the dilution */
            Dilution myDilution = Dilution.parseString(pDilution);
            if (myDilution == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution: " + pDilution);
            }

            /* Create the dilution event */
            DilutionEvent myEvent = new DilutionEvent(myAccount, myDate, myDilution);

            /* Add it to the list */
            add(myEvent);
        }

        /**
         * Does this account have diluted prices?
         * @param pAccount the account to test
         * @return <code>true</code> if the account has diluted prices, <code>false</code> otherwise
         */
        public boolean hasDilution(final Account pAccount) {
            /* Create the iterator */
            Iterator<DilutionEvent> myIterator = listIterator();

            /* Loop through the items */
            while (myIterator.hasNext()) {
                DilutionEvent myEvent = myIterator.next();

                /* If the event is for this account */
                if (!Difference.isEqual(pAccount, myEvent.getAccount())) {
                    /* Set result and break loop */
                    return true;
                }
            }

            /* Return no dilution */
            return false;
        }

        /**
         * Obtain the dilution factor for the account and date.
         * @param pAccount the account to dilute
         * @param pDate the date of the price
         * @return the dilution factor
         */
        public Dilution getDilutionFactor(final Account pAccount,
                                          final DateDay pDate) {
            /* No factor if the account has no dilutions */
            if (!hasDilution(pAccount)) {
                return null;
            }

            /* Create the iterator */
            Iterator<DilutionEvent> myIterator = listIterator();
            Dilution myDilution = new Dilution(Dilution.MAX_VALUE);

            /* Loop through the items */
            while (myIterator.hasNext()) {
                DilutionEvent myEvent = myIterator.next();
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
            /* Obtain the prices and accounts */
            AccountList myAccounts = theData.getAccounts();
            AccountPriceList myPrices = theData.getPrices();

            /* Search for the account */
            Account myAccount = myAccounts.findItemByName(pAccount);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Price account [" + pAccount + "]");
            }

            /* Create the date */
            DateDay myDate = new DateDay(pDate);
            Price myPrice;

            /* If the account has diluted prices for this date */
            Dilution myDilution = getDilutionFactor(myAccount, myDate);
            if (myDilution != null) {
                /* Obtain the diluted price */
                DilutedPrice myDilutedPrice = DilutedPrice.parseString(pPrice);
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
