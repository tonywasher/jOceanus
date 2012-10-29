/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.views;

import java.util.Date;
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JDilutedPrice;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;
import net.sourceforge.jOceanus.jSortedList.OrderedIdItem;
import net.sourceforge.jOceanus.jSortedList.OrderedIdList;

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
     * Id Field Id.
     */
    public static final JDataField FIELD_ID = FIELD_DEFS.declareEqualityField("ID");

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
        if (FIELD_ID.equals(pField)) {
            return theId;
        }
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
     * The Id.
     */
    private final int theId;

    /**
     * The Account.
     */
    private final Account theAccount;

    /**
     * The Date.
     */
    private final JDateDay theDate;

    /**
     * The Dilution.
     */
    private final JDilution theDilution;

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
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain the Dilution.
     * @return the dilution
     */
    public JDilution getDilution() {
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
        return theId;
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
     * @param pId the id for the dilution
     * @param pEvent the underlying event
     */
    private DilutionEvent(final int pId,
                          final Event pEvent) {
        /* Local variables */
        Account myAccount;

        /* Access the transaction type */
        TransactionType myType = pEvent.getTransType();

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
        theId = pId;
        theAccount = myAccount;
        theDate = pEvent.getDate();
        theDilution = pEvent.getDilution();
        theEvent = pEvent;
    }

    /**
     * Create a dilution event from details.
     * @param pId the id for the dilution
     * @param pAccount the account
     * @param pDate the Date
     * @param pDilution the dilution
     */
    private DilutionEvent(final int pId,
                          final Account pAccount,
                          final JDateDay pDate,
                          final JDilution pDilution) {
        /* Store the values */
        theId = pId;
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
        private final FinanceData theData;

        /**
         * The Decimal parser.
         */
        private final JDecimalParser theParser;

        /**
         * The next Id.
         */
        private int theNextId = 1;

        /**
         * Constructor.
         * @param pData the DataSet
         */
        public DilutionEventList(final FinanceData pData) {
            super(DilutionEvent.class);
            theData = pData;
            theParser = theData.getDataFormatter().getDecimalParser();
        }

        /**
         * Add Dilution Event to List.
         * @param pEvent the base event
         */
        public void addDilution(final Event pEvent) {
            DilutionEvent myDilution;

            /* Create the dilution event */
            myDilution = new DilutionEvent(theNextId++, pEvent);

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
            JDateDay myDate = new JDateDay(pDate);

            /* Record the dilution */
            JDilution myDilution = theParser.parseDilutionValue(pDilution);
            if (myDilution == null) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Dilution: " + pDilution);
            }

            /* Create the dilution event */
            DilutionEvent myEvent = new DilutionEvent(theNextId++, myAccount, myDate, myDilution);

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
        public JDilution getDilutionFactor(final Account pAccount,
                                           final JDateDay pDate) {
            /* No factor if the account has no dilutions */
            if (!hasDilution(pAccount)) {
                return null;
            }

            /* Create the iterator */
            Iterator<DilutionEvent> myIterator = listIterator();
            JDilution myDilution = new JDilution(JDilution.MAX_DILUTION);

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
            if (myDilution.compareTo(JDilution.MAX_DILUTION) == 0) {
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
            JDateDay myDate = new JDateDay(pDate);
            JPrice myPrice;

            /* Protect against exceptions */
            try {
                /* If the account has diluted prices for this date */
                JDilution myDilution = getDilutionFactor(myAccount, myDate);
                if (myDilution != null) {
                    /* Obtain the diluted price */
                    JDilutedPrice myDilutedPrice = theParser.parseDilutedPriceValue(pPrice);

                    /* Obtain the undiluted price */
                    myPrice = myDilutedPrice.getPrice(myDilution);

                    /* Else this is just a price */
                } else {
                    /* Obtain the the price */
                    myPrice = theParser.parsePriceValue(pPrice);
                }
                /* Catch exceptions */
            } catch (IllegalArgumentException e) {
                throw new JDataException(ExceptionClass.DATA, "Invalid Price: " + pPrice, e);
            }

            /* Add the item to the list */
            myPrices.addOpenItem(myAccount, myDate, myPrice);
        }
    }
}
