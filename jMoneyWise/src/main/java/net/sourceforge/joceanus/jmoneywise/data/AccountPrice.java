/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedPrice;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.views.SpotPrices;
import net.sourceforge.jOceanus.jMoneyWise.views.SpotPrices.SpotList;
import net.sourceforge.jOceanus.jMoneyWise.views.SpotPrices.SpotPrice;

/**
 * AccountPrice data type.
 * @author Tony Washer
 */
public class AccountPrice
        extends EncryptedItem
        implements Comparable<AccountPrice> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountPrice.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountPrice.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataSecurity"));

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDate"));

    /**
     * Price Field Id.
     */
    public static final JDataField FIELD_PRICE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataPrice"));

    /**
     * Obtain Price.
     * @return the price
     */
    public JPrice getPrice() {
        return getPrice(getValueSet());
    }

    /**
     * Obtain Encrypted Price.
     * @return the Bytes
     */
    public byte[] getPriceBytes() {
        return getPriceBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Price Field.
     * @return the field
     */
    public EncryptedPrice getPriceField() {
        return getPriceField(getValueSet());
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public Account getAccount() {
        return getAccount(getValueSet());
    }

    /**
     * Obtain AccountId.
     * @return the accountId
     */
    public Integer getAccountId() {
        Account myAccount = getAccount();
        return (myAccount == null)
                ? null
                : myAccount.getId();
    }

    /**
     * Obtain AccountName.
     * @return the accountName
     */
    public String getAccountName() {
        Account myAccount = getAccount();
        return (myAccount == null)
                ? null
                : myAccount.getName();
    }

    /**
     * Obtain Account.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static Account getAccount(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, Account.class);
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain Price.
     * @param pValueSet the valueSet
     * @return the Price
     */
    public static JPrice getPrice(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PRICE, JPrice.class);
    }

    /**
     * Obtain Encrypted Price.
     * @param pValueSet the valueSet
     * @return the Price
     */
    public static byte[] getPriceBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PRICE);
    }

    /**
     * Obtain Price Field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedPrice getPriceField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRICE, EncryptedPrice.class);
    }

    /**
     * Set the account.
     * @param pValue the account
     */
    protected void setAccount(final Account pValue) {
        setValueAccount(pValue);
    }

    /**
     * Set the account.
     * @param pValue the account
     */
    private void setValueAccount(final Account pValue) {
        getValueSet().setValue(FIELD_ACCOUNT, pValue);
    }

    /**
     * Set the account id.
     * @param pId the account id
     */
    private void setValueAccount(final Integer pId) {
        getValueSet().setValue(FIELD_ACCOUNT, pId);
    }

    /**
     * Set the account name.
     * @param pName the account name
     */
    private void setValueAccount(final String pName) {
        getValueSet().setValue(FIELD_ACCOUNT, pName);
    }

    /**
     * Set the price.
     * @param pValue the price
     * @throws JDataException on error
     */
    private void setValuePrice(final JPrice pValue) throws JDataException {
        setEncryptedValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the encrypted price.
     * @param pBytes the encrypted price
     * @throws JDataException on error
     */
    private void setValuePrice(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_PRICE, pBytes, JPrice.class);
    }

    /**
     * Set the price.
     * @param pValue the price
     */
    public void setValuePrice(final EncryptedPrice pValue) {
        getValueSet().setValue(FIELD_PRICE, pValue);
    }

    /**
     * Set the date.
     * @param pValue the date
     */
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public AccountPrice getBase() {
        return (AccountPrice) super.getBase();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPrice The Price
     */
    protected AccountPrice(final EncryptedList<? extends AccountPrice> pList,
                           final AccountPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     * @param pAccount the account
     */
    protected AccountPrice(final EncryptedList<? extends AccountPrice> pList,
                           final Account pAccount) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
        setValueAccount(pAccount);
    }

    /**
     * Open Constructor.
     * @param pList the list
     * @param pId the id
     * @param pAccount the account name
     * @param pDate the date
     * @param pPrice the price
     * @throws JDataException on error
     */
    private AccountPrice(final EncryptedList<? extends AccountPrice> pList,
                         final Integer pId,
                         final String pAccount,
                         final JDateDay pDate,
                         final String pPrice) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Access the DataSet and parser */
            FinanceData myDataSet = getDataSet();
            JDataFormatter myFormatter = myDataSet.getDataFormatter();
            JDecimalParser myParser = myFormatter.getDecimalParser();

            /* Record account, date and price */
            setValueAccount(pAccount);
            setValueDate(pDate);
            setValuePrice(myParser.parsePriceValue(pPrice));

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Secure Constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the control id
     * @param pAccountId the account id
     * @param pDate the date
     * @param pPrice the price
     * @throws JDataException on error
     */
    private AccountPrice(final EncryptedList<? extends AccountPrice> pList,
                         final Integer pId,
                         final Integer pControlId,
                         final Integer pAccountId,
                         final JDateDay pDate,
                         final byte[] pPrice) throws JDataException {
        /* Initialise the item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueAccount(pAccountId);

            /* Store the controlId */
            setControlKey(pControlId);

            /* Record the date and price */
            setValueDate(pDate);
            setValuePrice(pPrice);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public int compareTo(final AccountPrice pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the dates */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the accounts */
        iDiff = getAccount().compareTo(pThat.getAccount());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JDataException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        FinanceData myData = getDataSet();
        AccountList myAccounts = myData.getAccounts();
        ValueSet myValues = getValueSet();

        /* Adjust Account */
        Object myAccount = myValues.getValue(FIELD_ACCOUNT);
        if (myAccount instanceof Account) {
            myAccount = ((Account) myAccount).getId();
        }
        if (myAccount instanceof Integer) {
            Account myAct = myAccounts.findItemById((Integer) myAccount);
            if (myAct == null) {
                addError(ERROR_UNKNOWN, FIELD_ACCOUNT);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }
            setValueAccount(myAct);
        } else if (myAccount instanceof String) {
            Account myAct = myAccounts.findItemByName((String) myAccount);
            if (myAct == null) {
                addError(ERROR_UNKNOWN, FIELD_ACCOUNT);
                throw new JDataException(ExceptionClass.DATA, this, ERROR_RESOLUTION);
            }
            setValueAccount(myAct);
        }
    }

    /**
     * Validate the price.
     */
    @Override
    public void validate() {
        JDateDay myDate = getDate();
        JPrice myPrice = getPrice();
        AccountPriceList myList = (AccountPriceList) getList();
        FinanceData mySet = getDataSet();

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);

            /* else date is non-null */
        } else {
            /* Date must be unique for this account */
            if (myList.countInstances(myDate, getAccount()) > 1) {
                addError(ERROR_DUPLICATE, FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareTo(myDate) != 0) {
                addError(ERROR_RANGE, FIELD_DATE);
            }
        }

        /* The Price must be non-zero and greater than zero */
        if (myPrice == null) {
            addError(ERROR_MISSING, FIELD_PRICE);
        } else if (myPrice.isZero()) {
            addError(ERROR_ZERO, FIELD_PRICE);
        } else if (!myPrice.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_PRICE);
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Set a new price.
     * @param pPrice the price
     * @throws JDataException on error
     */
    public void setPrice(final JPrice pPrice) throws JDataException {
        setValuePrice(pPrice);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
        setValueDate(pDate);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying account */
        getAccount().touchItem(this);
    }

    /**
     * Update Price from an item Element.
     * @param pItem the price extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pItem) {
        if (pItem instanceof SpotPrice) {
            SpotPrice mySpot = (SpotPrice) pItem;
            return applyChanges(mySpot);
        } else if (pItem instanceof AccountPrice) {
            AccountPrice myPrice = (AccountPrice) pItem;
            return applyChanges(myPrice);
        }
        return false;
    }

    /**
     * Update Price from a Price extract.
     * @param pPrice the price extract
     * @return whether changes have been made
     */
    private boolean applyChanges(final AccountPrice pPrice) {
        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!Difference.isEqual(getPrice(), pPrice.getPrice())) {
            setValuePrice(pPrice.getPriceField());
        }

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), pPrice.getDate())) {
            setValueDate(pPrice.getDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Update Price from a Price extract.
     * @param pPrice the price extract
     * @return whether changes have been made
     */
    private boolean applyChanges(final SpotPrice pPrice) {
        /* If we are setting a null price */
        if (pPrice.getPrice() == null) {
            /* We are actually deleting the price */
            setDeleted(true);
            return true;

            /* else we have a price to set */
        }

        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!Difference.isEqual(getPrice(), pPrice.getPrice())) {
            setValuePrice(pPrice.getPriceField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * Price List.
     */
    public static class AccountPriceList
            extends EncryptedList<AccountPrice> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Construct an empty CORE price list.
         * @param pData the DataSet for the list
         */
        protected AccountPriceList(final FinanceData pData) {
            super(AccountPrice.class, pData);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountPriceList(final AccountPriceList pSource) {
            super(pSource);
        }

        @Override
        protected AccountPriceList getEmptyList(final ListStyle pStyle) {
            AccountPriceList myList = new AccountPriceList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountPriceList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (AccountPriceList) super.cloneList(pDataSet);
        }

        @Override
        public AccountPriceList deriveList(final ListStyle pStyle) throws JDataException {
            return (AccountPriceList) super.deriveList(pStyle);
        }

        @Override
        public AccountPriceList deriveDifferences(final DataList<AccountPrice> pOld) {
            return (AccountPriceList) super.deriveDifferences(pOld);
        }

        /**
         * Add a new item to the core list.
         * @param pPrice item
         * @return the newly added item
         */
        @Override
        public AccountPrice addCopyItem(final DataItem pPrice) {
            if (pPrice instanceof AccountPrice) {
                AccountPrice myPrice = new AccountPrice(this, (AccountPrice) pPrice);
                add(myPrice);
                return myPrice;
            } else {
                return null;
            }
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public AccountPrice addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Count the instances of a date.
         * @param pDate the date
         * @param pAccount the account
         * @return The Item if present (or null)
         */
        public int countInstances(final JDateDay pDate,
                                  final Account pAccount) {
            /* Access the list iterator */
            Iterator<AccountPrice> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();
                if (!pDate.equals(myCurr.getDate())) {
                    continue;
                }
                if (pAccount.equals(myCurr.getAccount())) {
                    iCount++;
                }
            }

            /* return to caller */
            return iCount;
        }

        /**
         * Obtain the most relevant price for a Date.
         * @param pAccount the account
         * @param pDate the date from which a price is required
         * @return The relevant Price record
         */
        public AccountPrice getLatestPrice(final Account pAccount,
                                           final JDateDay pDate) {
            /* Skip to alias if required */
            Account myAccount = pAccount;
            if (myAccount.getAlias() != null) {
                myAccount = pAccount.getAlias();
            }

            /* Access the list iterator */
            Iterator<AccountPrice> myIterator = iterator();
            AccountPrice myPrice = null;

            /* Loop through the Prices */
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();

                /* Skip records that do not belong to this account */
                if (!Difference.isEqual(myCurr.getAccount(), myAccount)) {
                    continue;
                }

                /* break loop if we have passed the date */
                if (myCurr.getDate().compareTo(pDate) > 0) {
                    break;
                }

                /* Record the best case so far */
                myPrice = myCurr;
            }

            /* Return the price */
            return myPrice;
        }

        /**
         * Apply changes from a Spot Price list.
         * @param pPrices the spot prices
         */
        public void applyChanges(final SpotPrices pPrices) {
            /* Access details */
            JDateDay myDate = pPrices.getDate();
            SpotList myList = pPrices.getPrices();

            /* Access the iterator */
            Iterator<SpotPrice> myIterator = myList.listIterator();

            /* Loop through the spot prices */
            while (myIterator.hasNext()) {
                SpotPrice mySpot = myIterator.next();

                /* Access the price for this date if it exists */
                AccountPrice myPrice = mySpot.getBase();
                EncryptedPrice myPoint = mySpot.getPriceField();

                /* If the state is not clean */
                if (mySpot.getState() != DataState.CLEAN) {
                    /* If we have an underlying price */
                    if (myPrice != null) {
                        /* Apply changes to the underlying entry */
                        myPrice.applyChanges(mySpot);

                        /* else if we have a new price with no underlying */
                    } else if (myPoint != null) {
                        /* Create the new Price */
                        myPrice = new AccountPrice(this, mySpot.getAccount());

                        /* Set the date and price */
                        myPrice.setDate(new JDateDay(myDate));
                        myPrice.setValuePrice(myPoint);

                        /* Add to the list and link backwards */
                        mySpot.setBase(myPrice);
                        add(myPrice);
                    }

                    /* Clear history and set as a clean item */
                    mySpot.clearHistory();
                }
            }
        }

        /**
         * Add a Price.
         * @param pId the id
         * @param pDate the date
         * @param pAccount the account
         * @param pPrice the price
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final JDateDay pDate,
                                final String pAccount,
                                final String pPrice) throws JDataException {
            /* Create the PricePoint */
            AccountPrice myPrice = new AccountPrice(this, pId, pAccount, pDate, pPrice);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(myPrice.getId())) {
                myPrice.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myPrice, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPrice);
        }

        /**
         * Load an Encrypted price.
         * @param pId the id
         * @param pControlId the control id
         * @param pDate the date
         * @param pAccountId the account id
         * @param pPrice the price
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final JDateDay pDate,
                                  final Integer pAccountId,
                                  final byte[] pPrice) throws JDataException {
            /* Create the price and PricePoint */
            AccountPrice myPrice = new AccountPrice(this, pId, pControlId, pAccountId, pDate, pPrice);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(pId)) {
                myPrice.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myPrice, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myPrice);
        }
    }
}