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

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataContents;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem.EncryptedList;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice.AccountPriceList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;

/**
 * Extension of AccountPrice to cater for spot prices.
 * @author Tony Washer
 */
public class SpotPrices
        implements JDataContents {
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
    private final JDateDay theDate;

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
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Obtain next date.
     * @return the date
     */
    public JDateDay getNext() {
        return thePrices.getNext();
    }

    /**
     * Obtain previous date.
     * @return the date
     */
    public JDateDay getPrev() {
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
                      final JDateDay pDate) {
        /* Create a copy of the date and initiate the list */
        theView = pView;
        theDate = pDate;
        theType = pType;
        thePrices = new SpotList(this);
    }

    /**
     * The Spot Prices List class.
     */
    public static class SpotList
            extends EncryptedList<SpotPrice> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(SpotList.class.getSimpleName(), DataList.FIELD_DEFS);

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

        @Override
        public String listName() {
            return SpotList.class.getSimpleName();
        }

        @Override
        protected SpotList getEmptyList(final ListStyle pStyle) {
            throw new UnsupportedOperationException();
        }

        /**
         * The date.
         */
        private final JDateDay theDate;

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
        private JDateDay theNext = null;

        /**
         * The previous date.
         */
        private JDateDay thePrev = null;

        /**
         * Obtain the next date.
         * @return the date
         */
        public JDateDay getNext() {
            return theNext;
        }

        /**
         * Obtain the previous date.
         * @return the date
         */
        public JDateDay getPrev() {
            return thePrev;
        }

        /**
         * Constructor.
         * @param pPrices the spot price control
         */
        public SpotList(final SpotPrices pPrices) {
            /* Build initial list */
            super(SpotPrice.class, pPrices.getData());
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
                if ((!Difference.isEqual(myAccount.getActType(), theType))
                    || (!myAccount.isPriced())
                    || (myAccount.isAlias())) {
                    continue;
                }

                /* Create a SpotPrice entry */
                SpotPrice mySpot = new SpotPrice(this, myAccount);
                mySpot.setId(myAccount.getId());
                mySpot.setDate(new JDateDay(theDate));
                add(mySpot);

                /* If the account is closed then hide the entry */
                if (myAccount.isClosed()) {
                    mySpot.setDeleted(true);
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
                SpotPrice mySpot = findItemById(myAccount.getId());

                /* If we are exactly the date */
                if (iDiff == 0) {
                    /* Set price */
                    mySpot.setValuePrice(myPrice.getPriceField());

                    /* Link to base and re-establish state */
                    mySpot.setBase(myPrice);

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

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return false;
        }

        /* Disable Add a new item */
        @Override
        public SpotPrice addCopyItem(final DataItem pElement) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SpotPrice addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Calculate the Edit State for the list.
         */
        @Override
        public void findEditState() {
            /* Access the iterator */
            Iterator<SpotPrice> myIterator = listIterator();
            EditState myEdit = EditState.CLEAN;

            /* Loop through the list */
            while (myIterator.hasNext()) {
                SpotPrice myCurr = myIterator.next();
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
            Iterator<SpotPrice> myIterator = listIterator();

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
    }

    /**
     * Spot Price class.
     * @author Tony Washer
     */
    public static final class SpotPrice
            extends AccountPrice {
        /**
         * Object name.
         */
        public static final String OBJECT_NAME = SpotPrice.class.getSimpleName();

        /**
         * List name.
         */
        public static final String LIST_NAME = OBJECT_NAME
                                               + "s";

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

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_PREVDATE.equals(pField)) {
                return thePrevDate;
            }
            if (FIELD_PREVPRICE.equals(pField)) {
                return thePrevPrice;
            }
            return super.getFieldValue(pField);
        }

        /**
         * the previous date.
         */
        private JDateDay thePrevDate;

        /**
         * the previous price.
         */
        private JPrice thePrevPrice;

        /**
         * Obtain previous price.
         * @return the price.
         */
        public JPrice getPrevPrice() {
            return thePrevPrice;
        }

        /**
         * Obtain previous date.
         * @return the date.
         */
        public JDateDay getPrevDate() {
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
        public JPrice getPrice() {
            /* Switch on state */
            switch (getState()) {
                case NEW:
                case CHANGED:
                case RECOVERED:
                case CLEAN:
                    return super.getPrice();
                    /*
                     * case CLEAN: return (getBase().isDeleted()) ? null : getPrice();
                     */
                default:
                    return null;
            }
        }

        @Override
        public DataState getState() {
            EncryptedValueSet myCurr = getValueSet();
            EncryptedValueSet myBase = getOriginalValues();

            /* If we have no changes we are CLEAN */
            if (myCurr.getVersion() == 0) {
                return DataState.CLEAN;
            }

            /* If the original price is Null */
            if (getPrice(myBase) == null) {
                /* Return status */
                return myCurr.isDeletion() ? DataState.DELNEW : DataState.NEW;
            }

            /* If we are deleted return so */
            if (myCurr.isDeletion()) {
                return myBase.isDeletion() ? DataState.CLEAN : DataState.DELETED;
            }

            /* Return RECOVERED or CHANGED depending on whether we started as deleted */
            return (myBase.isDeletion()) ? DataState.RECOVERED : DataState.CHANGED;
        }
    }
}
