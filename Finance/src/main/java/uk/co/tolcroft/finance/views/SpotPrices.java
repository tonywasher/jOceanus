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
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Price;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.AccountPrice;
import uk.co.tolcroft.finance.data.AccountPrice.AccountPriceList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EditState;

public class SpotPrices implements JDataContents {
    /**
     * Report fields
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SpotPrices.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /* Field IDs */
    public static final JDataField FIELD_VIEW = FIELD_DEFS.declareLocalField("View");
    public static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField("AccountType");
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");
    public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField("Next");
    public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField("Previous");
    public static final JDataField FIELD_PRICES = FIELD_DEFS.declareLocalField("Prices");

    @Override
    public Object getFieldValue(JDataField pField) {
        if (pField == FIELD_VIEW)
            return theView;
        if (pField == FIELD_TYPE)
            return theType;
        if (pField == FIELD_DATE)
            return theDate;
        if (pField == FIELD_NEXT)
            return getNext();
        if (pField == FIELD_PREV)
            return getPrev();
        if (pField == FIELD_PRICES)
            return thePrices;
        return null;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /* Members */
    private View theView = null;
    private AccountType theType = null;
    private DateDay theDate = null;
    private SpotList thePrices = null;

    /* Access methods */
    public AccountType getAccountType() {
        return theType;
    }

    protected View getView() {
        return theView;
    }

    protected FinanceData getData() {
        return theView.getData();
    }

    public DateDay getDate() {
        return theDate;
    }

    public DateDay getNext() {
        return thePrices.getNext();
    }

    public DateDay getPrev() {
        return thePrices.getPrev();
    }

    public SpotList getPrices() {
        return thePrices;
    }

    public SpotPrice get(long uIndex) {
        return (SpotPrice) thePrices.get((int) uIndex);
    }

    /* Constructor */
    public SpotPrices(View pView,
                      AccountType pType,
                      DateDay pDate) {
        /* Create a copy of the date and initiate the list */
        theView = pView;
        theDate = pDate;
        theType = pType;
        thePrices = new SpotList(this);
    }

    /* The List class */
    public static class SpotList extends AccountPriceList {
        /**
         * Local Report fields
         */
        protected static final JDataFields theLocalFields = new JDataFields(SpotList.class.getSimpleName(),
                AccountPriceList.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField("AccountType");
        public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");
        public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField("Next");
        public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField("Previous");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return theLocalFields;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_TYPE)
                return theType;
            if (pField == FIELD_DATE)
                return theDate;
            if (pField == FIELD_NEXT)
                return getNext();
            if (pField == FIELD_PREV)
                return getPrev();
            return super.getFieldValue(pField);
        }

        /* Members */
        private final DateDay theDate;
        private final View theView;
        private final AccountType theType;
        private DateDay theNext = null;
        private DateDay thePrev = null;

        public DateDay getNext() {
            return theNext;
        }

        public DateDay getPrev() {
            return thePrev;
        }

        /* Constructors */
        public SpotList(SpotPrices pPrices) {
            /* Build initial list */
            super(pPrices.getData());
            setStyle(ListStyle.EDIT);
            theDate = pPrices.getDate();
            theView = pPrices.getView();
            theType = pPrices.getAccountType();

            /* Declare variables */
            FinanceData myData;
            DataListIterator<Account> myActIterator;
            Account myAccount;
            SpotPrice mySpot;
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myPrice;
            int iDiff;

            /* Loop through the Accounts */
            myData = theView.getData();
            myActIterator = myData.getAccounts().listIterator();
            while ((myAccount = myActIterator.next()) != null) {
                /* Ignore accounts that are wrong type */
                if (!Difference.isEqual(myAccount.getActType(), theType))
                    continue;

                /* Ignore accounts that do not have prices */
                if (!myAccount.isPriced())
                    continue;

                /* Ignore aliases */
                if (myAccount.isAlias())
                    continue;

                /* Create a SpotPrice entry */
                mySpot = new SpotPrice(this, myAccount);
                add(mySpot);

                /* If the account is closed then hide the entry */
                if (myAccount.isClosed())
                    mySpot.setHidden();
            }

            /* Set the base for this list */
            AccountPriceList myPrices = myData.getPrices();
            setBase(myPrices);

            /* Loop through the prices */
            myIterator = myPrices.listIterator(true);
            while ((myPrice = myIterator.next()) != null) {
                /* Ignore accounts that are wrong type */
                if (!Difference.isEqual(myPrice.getAccount().getActType(), theType))
                    continue;

                /* Test the Date */
                iDiff = theDate.compareTo(myPrice.getDate());

                /* If we are past the date */
                if (iDiff < 0) {
                    /* Record the next date and break the loop */
                    theNext = myPrice.getDate();
                    break;
                }

                /* Access the Spot Price */
                myAccount = myPrice.getAccount();
                mySpot = (SpotPrice) searchFor(myAccount.getId());

                /* If we are exactly the date */
                if (iDiff == 0) {
                    /* Set price */
                    mySpot.setValuePrice(myPrice.getPriceField());

                    /* Link to base and re-establish state */
                    mySpot.setBase(myPrice);
                    mySpot.setState(DataState.CLEAN);
                }

                /* else we are a previous date */
                else {
                    /* Set previous date and value */
                    mySpot.thePrevDate = myPrice.getDate();
                    mySpot.thePrevPrice = myPrice.getPrice();

                    /* Record the latest previous date */
                    thePrev = myPrice.getDate();
                }
            }

        }

        /* Disable extract lists. */
        @Override
        public SpotList getUpdateList() {
            return null;
        }

        @Override
        public SpotList getEditList() {
            return null;
        }

        @Override
        public SpotList getShallowCopy() {
            return null;
        }

        @Override
        public SpotList getDeepCopy(DataSet<?> pData) {
            return null;
        }

        public SpotList getDifferences(SpotList pOld) {
            return null;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return false;
        }

        /* Disable Add a new item */
        @Override
        public SpotPrice addNewItem(DataItem<?> pElement) {
            return null;
        }

        @Override
        public SpotPrice addNewItem() {
            return null;
        }

        /**
         * Calculate the Edit State for the list
         */
        @Override
        public void findEditState() {
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myCurr;
            EditState myEdit;

            /* Access the iterator */
            myIterator = listIterator();
            myEdit = EditState.CLEAN;

            /* Loop through the list */
            while ((myCurr = myIterator.next()) != null) {
                /* Switch on new state */
                switch (myCurr.getState()) {
                    case CLEAN:
                    case DELNEW:
                        break;
                    case NEW:
                    case DELETED:
                    case DELCHG:
                    case CHANGED:
                    case RECOVERED:
                        myEdit = EditState.VALID;
                        break;
                }
            }

            /* Set the Edit State */
            setEditState(myEdit);
        }

        /**
         * Does the list have updates
         */
        @Override
        public boolean hasUpdates() {
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myCurr;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the list */
            while ((myCurr = myIterator.next()) != null) {
                /* Switch on state */
                switch (myCurr.getState()) {
                    case CLEAN:
                    case DELNEW:
                        break;
                    case DELETED:
                    case DELCHG:
                    case CHANGED:
                    case RECOVERED:
                        return true;
                }
            }

            /* Return no updates */
            return false;
        }

        /**
         * Reset changes in an edit view
         */
        @Override
        public void resetChanges() {
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myCurr;

            /* Create an iterator for the list */
            myIterator = listIterator(true);

            /* Loop through the elements */
            while ((myCurr = myIterator.next()) != null) {
                /* Switch on the state */
                switch (myCurr.getState()) {
                /* If this is a clean item, just ignore */
                    case CLEAN:
                    case DELNEW:
                        break;

                    /* If this is a changed or DELCHG item */
                    case NEW:
                    case CHANGED:
                    case DELCHG:
                        /* Clear changes and fall through */
                        myCurr.resetHistory();

                        /* If this is a deleted or recovered item */
                    case DELETED:
                    case RECOVERED:
                        /* Clear errors and mark the item as clean */
                        myCurr.clearErrors();
                        myCurr.setState(DataState.CLEAN);
                        break;
                }
            }
        }
    }

    public static class SpotPrice extends AccountPrice {
        /**
         * Object name
         */
        public static String objName = SpotPrice.class.getSimpleName();

        /**
         * List name
         */
        public static String listName = objName + "s";

        /**
         * Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(objName, AccountPrice.FIELD_DEFS);

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /* Field IDs */
        public static final JDataField FIELD_PREVDATE = FIELD_DEFS.declareEqualityField("PreviousDate");
        public static final JDataField FIELD_PREVPRICE = FIELD_DEFS.declareEqualityField("PreviousPrice");

        /**
         * the previous date
         */
        private DateDay thePrevDate;

        /**
         * the previous price
         */
        private Price thePrevPrice;

        public Price getPrevPrice() {
            return thePrevPrice;
        }

        public DateDay getPrevDate() {
            return thePrevDate;
        }

        /* Linking methods */
        @Override
        public AccountPrice getBase() {
            return (AccountPrice) super.getBase();
        }

        /**
         * Constructor for a new SpotPrice where no price data exists
         * @param pList the Spot Price List
         * @param pAccount the price for the date
         */
        private SpotPrice(SpotList pList,
                          Account pAccount) {
            super(pList, pAccount);

            /* Store base values */
            setControlKey(pList.getControlKey());
            setDate(pList.theDate);
            setAccount(pAccount);

            /* Set the state */
            setState(DataState.CLEAN);
        }

        /**
         * Validate the line
         */
        @Override
        public void validate() {
            setValidEdit();
        }

        /* Is this row locked */
        @Override
        public boolean isLocked() {
            return isHidden();
        }

        /**
         * Note that this item has been validated
         */
        @Override
        public void setValidEdit() {
            setEditState((hasHistory()) ? EditState.VALID : EditState.CLEAN);
        }

        /**
         * Obtain the price of the item
         */
        @Override
        public Price getPrice() {
            /* Switch on state */
            switch (getState()) {
                case NEW:
                case CHANGED:
                case RECOVERED:
                    return getPrice();
                case CLEAN:
                    return (getBase().isDeleted()) ? null : getPrice();
                default:
                    return null;
            }
        }

        /* Disable setDate */
        @Override
        public void setDate(DateDay pDate) {
        }

        /**
         * Set the state of the item A Spot list has some minor changes to the algorithm in that there are no
         * NEW or DELETED states, leaving just CLEAN and CHANGED. The isDeleted flags is changed in usage to
         * an isVisible flag
         * @param newState the new state to set
         */
        @Override
        public void setState(DataState newState) {
            /* Switch on new state */
            switch (newState) {
                case CLEAN:
                    setDataState((getBase() == null) ? DataState.DELNEW : newState);
                    setEditState(EditState.CLEAN);
                    break;
                case CHANGED:
                    setDataState((getBase() == null) ? DataState.NEW : newState);
                    setEditState(EditState.DIRTY);
                    break;
                case DELETED:
                    switch (getState()) {
                        case NEW:
                            setDataState(DataState.DELNEW);
                            break;
                        case CHANGED:
                            setDataState(DataState.DELCHG);
                            break;
                        default:
                            setDataState(DataState.DELETED);
                            break;
                    }
                    setEditState(EditState.DIRTY);
                    break;
                case RECOVERED:
                    switch (getState()) {
                        case DELNEW:
                            setDataState(DataState.NEW);
                            break;
                        case DELCHG:
                            setDataState(DataState.CHANGED);
                            break;
                        case DELETED:
                            setDataState(DataState.CLEAN);
                            break;
                        default:
                            setDataState(DataState.RECOVERED);
                            break;
                    }
                    setEditState(EditState.DIRTY);
                    break;
            }
        }

        /**
         * Compare the price
         */
        @Override
        public boolean equals(Object that) {
            return (this == that);
        }
    }
}
