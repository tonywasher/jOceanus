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

import java.util.Iterator;

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

/**
 * Extension of AccountPrice to cater for spot prices.
 * @author Tony Washer
 */
public class SpotPrices implements JDataContents {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SpotPrices.class.getSimpleName());

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * View Field Id.
     */
    public static final JDataField FIELD_VIEW = FIELD_DEFS.declareLocalField("View");

    /**
     * AccountType Field Id.
     */
    public static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField("AccountType");

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

    /**
     * Next Field Id.
     */
    public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField("Next");

    /**
     * Previous Field Id.
     */
    public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField("Previous");

    /**
     * Prices Field Id.
     */
    public static final JDataField FIELD_PRICES = FIELD_DEFS.declareLocalField("Prices");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_VIEW.equals(pField)) {
            return theView;
        }
        if (FIELD_TYPE.equals(pField)) {
            return theType;
        }
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_NEXT.equals(pField)) {
            return getNext();
        }
        if (FIELD_PREV.equals(pField)) {
            return getPrev();
        }
        if (FIELD_PRICES.equals(pField)) {
            return thePrices;
        }
        return null;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The view.
     */
    private final View theView;

    /**
     * The account type.
     */
    private final AccountType theType;

    /**
     * The date.
     */
    private final DateDay theDate;

    /**
     * The prices.
     */
    private final SpotList thePrices;

    /**
     * Obtain account type.
     * @return the account type
     */
    public AccountType getAccountType() {
        return theType;
    }

    /**
     * Obtain view.
     * @return the view
     */
    protected View getView() {
        return theView;
    }

    /**
     * Obtain dataSet.
     * @return the dataSet
     */
    protected FinanceData getData() {
        return theView.getData();
    }

    /**
     * Obtain date.
     * @return the date
     */
    public DateDay getDate() {
        return theDate;
    }

    /**
     * Obtain next date.
     * @return the date
     */
    public DateDay getNext() {
        return thePrices.getNext();
    }

    /**
     * Obtain previous date.
     * @return the date
     */
    public DateDay getPrev() {
        return thePrices.getPrev();
    }

    /**
     * Obtain prices.
     * @return the prices
     */
    public SpotList getPrices() {
        return thePrices;
    }

    /**
     * Obtain spotPrice at index.
     * @param uIndex the index
     * @return the spotPrice
     */
    public SpotPrice get(final long uIndex) {
        return (SpotPrice) thePrices.get((int) uIndex);
    }

    /**
     * Constructor.
     * @param pView the view
     * @param pType the account type
     * @param pDate the date
     */
    public SpotPrices(final View pView,
                      final AccountType pType,
                      final DateDay pDate) {
        /* Create a copy of the date and initiate the list */
        theView = pView;
        theDate = pDate;
        theType = pType;
        thePrices = new SpotList(this);
    }

    /**
     * The Spot Prices List class.
     */
    public static class SpotList extends AccountPriceList {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(SpotList.class.getSimpleName(),
                AccountPriceList.FIELD_DEFS);

        /**
         * The account type field Id.
         */
        public static final JDataField FIELD_TYPE = FIELD_DEFS.declareLocalField("AccountType");

        /**
         * The date field Id.
         */
        public static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

        /**
         * The next date field Id.
         */
        public static final JDataField FIELD_NEXT = FIELD_DEFS.declareLocalField("Next");

        /**
         * The previous date field Id.
         */
        public static final JDataField FIELD_PREV = FIELD_DEFS.declareLocalField("Previous");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_TYPE.equals(pField)) {
                return theType;
            }
            if (FIELD_DATE.equals(pField)) {
                return theDate;
            }
            if (FIELD_NEXT.equals(pField)) {
                return getNext();
            }
            if (FIELD_PREV.equals(pField)) {
                return getPrev();
            }
            return super.getFieldValue(pField);
        }

        /**
         * The date.
         */
        private final DateDay theDate;

        /**
         * The view.
         */
        private final View theView;

        /**
         * The account type.
         */
        private final AccountType theType;

        /**
         * The next date.
         */
        private DateDay theNext = null;

        /**
         * The previous date.
         */
        private DateDay thePrev = null;

        /**
         * Obtain the next date.
         * @return the date
         */
        public DateDay getNext() {
            return theNext;
        }

        /**
         * Obtain the previous date.
         * @return the date
         */
        public DateDay getPrev() {
            return thePrev;
        }

        /**
         * Constructor.
         * @param pPrices the spot price control
         */
        public SpotList(final SpotPrices pPrices) {
            /* Build initial list */
            super(pPrices.getData());
            setStyle(ListStyle.EDIT);
            theDate = pPrices.getDate();
            theView = pPrices.getView();
            theType = pPrices.getAccountType();

            /* Loop through the Accounts */
            FinanceData myData = theView.getData();
            Iterator<Account> myActIterator = myData.getAccounts().listIterator();
            while (myActIterator.hasNext()) {
                Account myAccount = myActIterator.next();
                /* Ignore accounts that are wrong type, have no prices or are aliases */
                if ((!Difference.isEqual(myAccount.getActType(), theType)) || (!myAccount.isPriced())
                        || (myAccount.isAlias())) {
                    continue;
                }

                /* Create a SpotPrice entry */
                SpotPrice mySpot = new SpotPrice(this, myAccount);
                add(mySpot);

                /* If the account is closed then hide the entry */
                if (myAccount.isClosed()) {
                    mySpot.setHidden();
                }
            }

            /* Set the base for this list */
            AccountPriceList myPrices = myData.getPrices();
            setBase(myPrices);

            /* Loop through the prices */
            Iterator<AccountPrice> myIterator = myPrices.listIterator();
            while (myIterator.hasNext()) {
                AccountPrice myPrice = myIterator.next();
                /* Ignore accounts that are wrong type */
                if (!Difference.isEqual(myPrice.getAccount().getActType(), theType)) {
                    continue;
                }

                /* Test the Date */
                int iDiff = theDate.compareTo(myPrice.getDate());

                /* If we are past the date */
                if (iDiff < 0) {
                    /* Record the next date and break the loop */
                    theNext = myPrice.getDate();
                    break;
                }

                /* Access the Spot Price */
                Account myAccount = myPrice.getAccount();
                SpotPrice mySpot = (SpotPrice) findItemById(myAccount.getId());

                /* If we are exactly the date */
                if (iDiff == 0) {
                    /* Set price */
                    mySpot.setValuePrice(myPrice.getPriceField());

                    /* Link to base and re-establish state */
                    mySpot.setBase(myPrice);
                    mySpot.setState(DataState.CLEAN);

                    /* else we are a previous date */
                } else {
                    /* Set previous date and value */
                    mySpot.thePrevDate = myPrice.getDate();
                    mySpot.thePrevPrice = myPrice.getPrice();

                    /* Record the latest previous date */
                    thePrev = myPrice.getDate();
                }
            }

        }

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
        public SpotList getDeepCopy(final DataSet<?> pData) {
            return null;
        }

        // public SpotList getDifferences(final SpotList pOld) {
        // return null;
        // }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return false;
        }

        /* Disable Add a new item */
        @Override
        public SpotPrice addNewItem(final DataItem pElement) {
            return null;
        }

        @Override
        public SpotPrice addNewItem() {
            return null;
        }

        /**
         * Calculate the Edit State for the list.
         */
        @Override
        public void findEditState() {
            /* Access the iterator */
            Iterator<AccountPrice> myIterator = listIterator();
            EditState myEdit = EditState.CLEAN;

            /* Loop through the list */
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();
                /* Switch on new state */
                switch (myCurr.getState()) {
                    case NEW:
                    case DELETED:
                    case DELCHG:
                    case CHANGED:
                    case RECOVERED:
                        myEdit = EditState.VALID;
                        break;
                    case CLEAN:
                    case DELNEW:
                    default:
                        break;
                }
            }

            /* Set the Edit State */
            setEditState(myEdit);
        }

        @Override
        public boolean hasUpdates() {
            /* Access the iterator */
            Iterator<AccountPrice> myIterator = listIterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();
                /* Switch on state */
                switch (myCurr.getState()) {
                    case DELETED:
                    case DELCHG:
                    case CHANGED:
                    case RECOVERED:
                        return true;
                    case CLEAN:
                    case DELNEW:
                    default:
                        break;
                }
            }

            /* Return no updates */
            return false;
        }

        /**
         * Reset changes in an edit view.
         */
        @Override
        public void resetChanges() {
            /* Create an iterator for the list */
            Iterator<AccountPrice> myIterator = iterator();

            /* Loop through the elements */
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();
                /* Switch on the state */
                switch (myCurr.getState()) {
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

                    /* If this is a clean item, just ignore */
                    case CLEAN:
                    case DELNEW:
                    default:
                        break;
                }
            }
        }
    }

    /**
     * Spot Price class.
     * @author Tony Washer
     */
    public static final class SpotPrice extends AccountPrice {
        /**
         * Object name.
         */
        public static final String OBJECT_NAME = SpotPrice.class.getSimpleName();

        /**
         * List name.
         */
        public static final String LIST_NAME = OBJECT_NAME + "s";

        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AccountPrice.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * Previous Date field Id.
         */
        public static final JDataField FIELD_PREVDATE = FIELD_DEFS.declareEqualityField("PreviousDate");

        /**
         * Previous Price field Id.
         */
        public static final JDataField FIELD_PREVPRICE = FIELD_DEFS.declareEqualityField("PreviousPrice");

        /**
         * the previous date.
         */
        private DateDay thePrevDate;

        /**
         * the previous price.
         */
        private Price thePrevPrice;

        /**
         * Obtain previous price.
         * @return the price.
         */
        public Price getPrevPrice() {
            return thePrevPrice;
        }

        /**
         * Obtain previous date.
         * @return the date.
         */
        public DateDay getPrevDate() {
            return thePrevDate;
        }

        @Override
        public AccountPrice getBase() {
            return (AccountPrice) super.getBase();
        }

        /**
         * Constructor for a new SpotPrice where no price data exists.
         * @param pList the Spot Price List
         * @param pAccount the price for the date
         */
        private SpotPrice(final SpotList pList,
                          final Account pAccount) {
            super(pList, pAccount);

            /* Store base values */
            setControlKey(pList.getControlKey());
            setDate(pList.theDate);
            setAccount(pAccount);

            /* Set the state */
            setState(DataState.CLEAN);
        }

        /**
         * Validate the line.
         */
        @Override
        public void validate() {
            setValidEdit();
        }

        /* Is this row locked */
        @Override
        public boolean isLocked() {
            return isDeleted();
        }

        /**
         * Note that this item has been validated.
         */
        @Override
        public void setValidEdit() {
            setEditState((hasHistory()) ? EditState.VALID : EditState.CLEAN);
        }

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
        public void setDate(final DateDay pDate) {
        }

        /**
         * Set the state of the item A Spot list has some minor changes to the algorithm in that there are no
         * NEW or DELETED states, leaving just CLEAN and CHANGED. The isDeleted flags is changed in usage to
         * an isVisible flag
         * @param newState the new state to set
         */
        @Override
        public void setState(final DataState newState) {
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
                default:
                    break;
            }
        }
    }
}
