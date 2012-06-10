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
package uk.co.tolcroft.finance.data;

import java.util.Date;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Price;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedPrice;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.views.SpotPrices;
import uk.co.tolcroft.finance.views.SpotPrices.SpotPrice;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;

/**
 * AccountPrice data type.
 * @author Tony Washer
 */
public class AccountPrice extends EncryptedItem<AccountPrice> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountPrice.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /* Field IDs */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField("Account");
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField("Date");
    public static final JDataField FIELD_PRICE = FIELD_DEFS.declareEqualityValueField("Price");

    /**
     * The active set of values
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public Price getPrice() {
        return getPrice(theValueSet);
    }

    public byte[] getPriceBytes() {
        return getPriceBytes(theValueSet);
    }

    public EncryptedPrice getPriceField() {
        return getPriceField(theValueSet);
    }

    public DateDay getDate() {
        return getDate(theValueSet);
    }

    public Account getAccount() {
        return getAccount(theValueSet);
    }

    public static Account getAccount(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, Account.class);
    }

    public static DateDay getDate(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, DateDay.class);
    }

    public static Price getPrice(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PRICE, Price.class);
    }

    public static byte[] getPriceBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PRICE);
    }

    private static EncryptedPrice getPriceField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PRICE, EncryptedPrice.class);
    }

    protected void setAccount(Account pAccount) {
        setValueAccount(pAccount);
    }

    private void setValueAccount(Account pAccount) {
        theValueSet.setValue(FIELD_ACCOUNT, pAccount);
    }

    private void setValueAccount(Integer pAccount) {
        theValueSet.setValue(FIELD_ACCOUNT, pAccount);
    }

    private void setValuePrice(String pPrice) throws JDataException {
        setValuePrice(new Price(pPrice));
    }

    private void setValuePrice(Price pPrice) throws JDataException {
        setEncryptedValue(FIELD_PRICE, pPrice);
    }

    private void setValuePrice(byte[] pPrice) throws JDataException {
        setEncryptedValue(FIELD_PRICE, pPrice, Price.class);
    }

    public void setValuePrice(EncryptedPrice pPrice) {
        theValueSet.setValue(FIELD_PRICE, pPrice);
    }

    private void setValueDate(DateDay pDate) {
        theValueSet.setValue(FIELD_DATE, pDate);
    }

    /* Linking methods */
    @Override
    public AccountPrice getBase() {
        return (AccountPrice) super.getBase();
    }

    /**
     * Construct a copy of a Price
     * @param pList the list
     * @param pPrice The Price
     */
    protected AccountPrice(AccountPriceList pList,
                           AccountPrice pPrice) {
        /* Set standard values */
        super(pList, pPrice.getId());
        ListStyle myOldStyle = pPrice.getStyle();

        /* Switch on the ListStyle */
        switch (getStyle()) {
            case EDIT:
                /* If this is a view creation */
                if (myOldStyle == ListStyle.CORE) {
                    /* Price is based on the original element */
                    setBase(pPrice);
                    pList.setNewId(this);
                    break;
                }

                /* Else this is a duplication so treat as new item */
                setId(0);
                pList.setNewId(this);
                break;
            case CLONE:
                reBuildLinks(pList.getData());
            case COPY:
            case CORE:
                /* If this is an insert from a view */
                if (myOldStyle == ListStyle.EDIT) {
                    /* Generate new id */
                    setId(0);

                    /* Rebuild links */
                    reBuildLinks(pList.getData());
                }
                pList.setNewId(this);
                break;
            case UPDATE:
                setBase(pPrice);
                setState(pPrice.getState());
                break;
        }
    }

    /**
     * Construct a new price from a SpotPrice
     * @param pList the list
     * @param pPrice the price to copy
     */
    private AccountPrice(AccountPriceList pList,
                         SpotPrice pPrice) {

        /* Set standard values */
        super(pList, pPrice);
    }

    /* Standard constructor for a newly inserted price */
    protected AccountPrice(AccountPriceList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
        setValueAccount(pList.getAccount());
        pList.setNewId(this);
    }

    /* Standard constructor for a newly inserted price */
    protected AccountPrice(AccountPriceList pList,
                           Account pAccount) {
        super(pList, pAccount.getId());
        setControlKey(pList.getControlKey());
        setValueAccount(pAccount);
        pList.setNewId(this);
    }

    /* Extract constructor */
    private AccountPrice(AccountPriceList pList,
                         int uId,
                         int uAccountId,
                         Date pDate,
                         String pPrice) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access the DataSet */
            FinanceData myData = pList.getData();

            /* Record the Id */
            setValueAccount(uAccountId);

            /* Look up the Account */
            Account myAccount = myData.getAccounts().searchFor(uAccountId);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
            }
            setValueAccount(myAccount);

            /* Record the date and price */
            setValueDate(new DateDay(pDate));
            setValuePrice(pPrice);

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /* Encryption constructor */
    private AccountPrice(AccountPriceList pList,
                         int uId,
                         int uControlId,
                         int uAccountId,
                         Date pDate,
                         byte[] pPrice) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueAccount(uAccountId);

            /* Store the controlId */
            setControlKey(uControlId);

            /* Access the DataSet */
            FinanceData myData = pList.getData();

            /* Look up the Account */
            Account myAccount = myData.getAccounts().searchFor(uAccountId);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
            }
            setValueAccount(myAccount);

            /* Record the date and price */
            setValueDate(new DateDay(pDate));
            setValuePrice(pPrice);

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /* Special price constructor for diluted prices */
    private AccountPrice(AccountPriceList pList,
                         Account pAccount,
                         DateDay pDate,
                         Price pPrice) throws JDataException {
        /* Initialise the item */
        super(pList, 0);

        /* Set the passed details */
        setValueAccount(pAccount);
        setValueDate(pDate);

        /* Create the pair for the values */
        setValuePrice(pPrice);

        /* Allocate the id */
        pList.setNewId(this);
    }

    /**
     * Compare this price to another to establish sort order.
     * @param pThat The Price to compare to
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

        /* Make sure that the object is an Price */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a Price */
        AccountPrice myThat = (AccountPrice) pThat;

        /* If the date differs */
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

        /* Compare the accounts */
        iDiff = getAccount().compareTo(myThat.getAccount());
        if (iDiff != 0)
            return iDiff;

        /* Compare the IDs */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;
        return 0;
    }

    /**
     * Rebuild Links to partner data
     * @param pData the DataSet
     */
    protected void reBuildLinks(FinanceData pData) {
        /* Update the Encryption details */
        super.reBuildLinks(pData);

        /* Access Accounts */
        AccountList myAccounts = pData.getAccounts();

        /* Update to use the local copy of the Accounts (use name rather than id) */
        Account myAct = getAccount();
        Account myNewAct = myAccounts.searchFor(myAct.getName());
        setValueAccount(myNewAct);
    }

    /**
     * Validate the price
     */
    @Override
    public void validate() {
        DateDay myDate = getDate();
        AccountPriceList myList = (AccountPriceList) getList();
        FinanceData mySet = myList.getData();

        /* The date must be non-null */
        if (myDate == null) {
            addError("Null Date is not allowed", FIELD_DATE);
        }

        /* else date is non-null */
        else {
            /* Date must be unique for this account */
            if (myList.countInstances(myDate, getAccount()) > 1) {
                addError("Date must be unique", FIELD_DATE);
            }

            /* The date must be in-range */
            if (mySet.getDateRange().compareTo(myDate) != 0) {
                addError("Date must be within range", FIELD_DATE);
            }
        }

        /* The Price must be non-zero */
        if ((getPrice() == null) || (!getPrice().isNonZero()) || (!getPrice().isPositive())) {
            addError("Price must be non-Zero and positive", FIELD_PRICE);
        }

        /* Set validation flag */
        if (!hasErrors())
            setValidEdit();
    }

    /**
     * Set a new price
     * @param pPrice the price
     * @throws JDataException
     */
    public void setPrice(Price pPrice) throws JDataException {
        setValuePrice(pPrice);
    }

    /**
     * Set a new date
     * @param pDate the new date
     */
    public void setDate(DateDay pDate) {
        setValueDate(pDate);
    }

    /**
     * Update Price from an item Element
     * @param pItem the price extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(DataItem<?> pItem) {
        boolean bChanged = false;
        if (pItem instanceof SpotPrice) {
            SpotPrice mySpot = (SpotPrice) pItem;
            bChanged = applyChanges(mySpot);
        } else if (pItem instanceof AccountPrice) {
            AccountPrice myPrice = (AccountPrice) pItem;
            bChanged = applyChanges(myPrice);
        }
        return bChanged;
    }

    /**
     * Update Price from a Price extract
     * @param pPrice the price extract
     * @return whether changes have been made
     */
    private boolean applyChanges(AccountPrice pPrice) {
        boolean bChanged = false;

        /* Store the current detail into history */
        pushHistory();

        /* Update the price if required */
        if (!Difference.isEqual(getPrice(), pPrice.getPrice()))
            setValuePrice(pPrice.getPriceField());

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), pPrice.getDate()))
            setValueDate(pPrice.getDate());

        /* Check for changes */
        if (checkForHistory()) {
            /* Mark as changed */
            setState(DataState.CHANGED);
            bChanged = true;
        }

        /* Return to caller */
        return bChanged;
    }

    /**
     * Update Price from a Price extract
     * @param pPrice the price extract
     * @return whether changes have been made
     */
    private boolean applyChanges(SpotPrice pPrice) {
        boolean bChanged = false;

        /* If we are setting a null price */
        if (pPrice.getPrice() == null) {
            /* We are actually deleting the price */
            setState(DataState.DELETED);
        }

        /* else we have a price to set */
        else {
            /* Store the current detail into history */
            pushHistory();

            /* Update the price if required */
            if (!Difference.isEqual(getPrice(), pPrice.getPrice()))
                setValuePrice(pPrice.getPriceField());

            /* Check for changes */
            if (checkForHistory()) {
                /* Mark as changed */
                setState(DataState.CHANGED);
                bChanged = false;
            }
        }

        /* Return to caller */
        return bChanged;
    }

    /**
     * Price List
     */
    public static class AccountPriceList extends EncryptedList<AccountPriceList, AccountPrice> {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                AccountPriceList.class.getSimpleName(), DataList.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_ACCOUNT) {
                return (theAccount == null) ? JDataObject.FIELD_SKIP : theAccount;
            }
            return super.getFieldValue(pField);
        }

        /* Members */
        private Account theAccount = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /* Access Extra Variables correctly */
        @Override
        public FinanceData getData() {
            return (FinanceData) super.getData();
        }

        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE price list
         * @param pData the DataSet for the list
         */
        protected AccountPriceList(FinanceData pData) {
            super(AccountPriceList.class, AccountPrice.class, pData);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private AccountPriceList(AccountPriceList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private AccountPriceList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            AccountPriceList myList = new AccountPriceList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public AccountPriceList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public AccountPriceList getEditList() {
            return null;
        }

        @Override
        public AccountPriceList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public AccountPriceList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            AccountPriceList myList = new AccountPriceList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        /**
         * Construct a difference Price list
         * @param pOld the old Price list
         * @return the difference list
         */
        @Override
        protected AccountPriceList getDifferences(AccountPriceList pOld) {
            /* Build an empty Difference List */
            AccountPriceList myList = new AccountPriceList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Construct an edit extract of a Pattern list
         * @param pAccount The account to extract patterns for
         * @return the edit list
         */
        public AccountPriceList getEditList(Account pAccount) {
            /* Build an empty Update */
            AccountPriceList myList = new AccountPriceList(this);

            /* Make this list the correct style */
            myList.setStyle(ListStyle.EDIT);

            /* Local variables */
            AccountPrice myCurr;
            AccountPrice myItem;
            DataListIterator<AccountPrice> myIterator;

            /* Store the account */
            myList.theAccount = pAccount;

            /* Access the list iterator */
            myIterator = listIterator(true);

            /* Loop through the Prices */
            while ((myCurr = myIterator.next()) != null) {
                /* Check the account */
                int myResult = pAccount.compareTo(myCurr.getAccount());

                /* Skip different accounts */
                if (myResult != 0)
                    continue;

                /* Copy the item */
                myItem = new AccountPrice(myList, myCurr);
                myList.add(myItem);
            }

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the core list
         * 
         * @param pPrice item
         * @return the newly added item
         */
        @Override
        public AccountPrice addNewItem(DataItem<?> pPrice) {
            if (pPrice instanceof SpotPrice) {
                AccountPrice myPrice = new AccountPrice(this, (SpotPrice) pPrice);
                add(myPrice);
                return myPrice;
            } else if (pPrice instanceof AccountPrice) {
                AccountPrice myPrice = new AccountPrice(this, (AccountPrice) pPrice);
                add(myPrice);
                return myPrice;
            } else
                return null;
        }

        /**
         * Add a new item to the edit list
         * @return the newly added item
         */
        @Override
        public AccountPrice addNewItem() {
            AccountPrice myPrice = new AccountPrice(this);
            myPrice.setAccount(theAccount);
            add(myPrice);
            return myPrice;
        }

        /**
         * Obtain the type of the item
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Count the instances of a date
         * @param pDate the date
         * @param pAccount the account
         * @return The Item if present (or null)
         */
        public int countInstances(DateDay pDate,
                                  Account pAccount) {
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myCurr;
            int iDiff;
            int iCount = 0;

            /* Access the list iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = pDate.compareTo(myCurr.getDate());
                if (iDiff != 0)
                    continue;
                iDiff = pAccount.compareTo(myCurr.getAccount());
                if (iDiff == 0)
                    iCount++;
            }

            /* return to caller */
            return iCount;
        }

        /**
         * Obtain the most relevant price for a Date
         * @param pAccount the account
         * @param pDate the date from which a price is required
         * @return The relevant Price record
         */
        public AccountPrice getLatestPrice(Account pAccount,
                                           DateDay pDate) {
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myPrice = null;
            AccountPrice myCurr;
            Account myAccount = pAccount;

            /* Skip to alias if required */
            if (myAccount.getAlias() != null)
                myAccount = pAccount.getAlias();

            /* Access the list iterator */
            myIterator = listIterator();

            /* Loop through the Prices */
            while ((myCurr = myIterator.next()) != null) {
                /* Skip records that do not belong to this account */
                if (!Difference.isEqual(myCurr.getAccount(), myAccount))
                    continue;

                /* break loop if we have passed the date */
                if (myCurr.getDate().compareTo(pDate) > 0)
                    break;

                /* Record the best case so far */
                myPrice = myCurr;
            }

            /* Return the price */
            return myPrice;
        }

        /**
         * Mark active prices
         */
        protected void markActiveItems() {
            DataListIterator<AccountPrice> myIterator;
            AccountPrice myCurr;

            /* Access the list iterator */
            myIterator = listIterator();

            /* Loop through the Prices */
            while ((myCurr = myIterator.next()) != null) {
                /* mark the account referred to */
                myCurr.getAccount().touchItem(myCurr);
            }
        }

        /**
         * Apply changes from a Spot Price list
         * @param pPrices the spot prices
         */
        public void applyChanges(SpotPrices pPrices) {
            DataListIterator<AccountPrice> myIterator;
            AccountPriceList myList;
            AccountPrice mySpot;
            DateDay myDate;
            EncryptedPrice myPoint;
            AccountPrice myPrice;

            /* Access details */
            myDate = pPrices.getDate();
            myList = pPrices.getPrices();

            /* Access the iterator */
            myIterator = myList.listIterator();

            /* Loop through the spot prices */
            while ((mySpot = myIterator.next()) != null) {
                /* Access the price for this date if it exists */
                myPrice = mySpot.getBase();
                myPoint = mySpot.getPriceField();

                /* If the state is not clean */
                if (mySpot.getState() != DataState.CLEAN) {
                    /* If we have an underlying price */
                    if (myPrice != null) {
                        /* Apply changes to the underlying entry */
                        myPrice.applyChanges(mySpot);
                    }

                    /* else if we have a new price with no underlying */
                    else if (myPoint != null) {
                        /* Create the new Price */
                        myPrice = new AccountPrice(this);

                        /* Set the date and price */
                        myPrice.setDate(new DateDay(myDate));
                        myPrice.setValuePrice(myPoint);
                        myPrice.setAccount(mySpot.getAccount());

                        /* Add to the list and link backwards */
                        mySpot.setBase(myPrice);
                        add(myPrice);
                    }

                    /* Clear history and set as a clean item */
                    mySpot.clearHistory();
                    mySpot.setState(DataState.CLEAN);
                }
            }
        }

        /**
         * Add a Price
         * @param uId the id
         * @param pDate the date
         * @param pAccount the account
         * @param pPrice the price
         * @throws JDataException
         */
        public void addItem(int uId,
                            Date pDate,
                            String pAccount,
                            String pPrice) throws JDataException {
            Account myAccount;
            Account.AccountList myAccounts;

            /* Access the Accounts */
            myAccounts = getData().getAccounts();

            /* Look up the Account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, "Price on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Account [" + pAccount
                        + "]");

            /* Add the price */
            addItem(uId, pDate, myAccount.getId(), pPrice);
        }

        /**
         * Allow a price to be added
         * @param uId the id
         * @param pDate the date
         * @param uAccountId the account
         * @param pPrice the price
         * @throws JDataException
         */
        public void addItem(int uId,
                            Date pDate,
                            int uAccountId,
                            String pPrice) throws JDataException {
            AccountPrice myPrice;

            /* Create the price and PricePoint */
            myPrice = new AccountPrice(this, uId, uAccountId, pDate, pPrice);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(myPrice.getId()))
                throw new JDataException(ExceptionClass.DATA, myPrice, "Duplicate PriceId <"
                        + myPrice.getId() + ">");

            /* Validate the price */
            myPrice.validate();

            /* Handle validation failure */
            if (myPrice.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myPrice, "Failed validation");

            /* Add to the list */
            add(myPrice);
        }

        /**
         * Load an Encrypted price
         * @param uId the id
         * @param uControlId the control id
         * @param pDate the date
         * @param uAccountId the account id
         * @param pPrice the price
         * @throws JDataException
         */
        public void addItem(int uId,
                            int uControlId,
                            Date pDate,
                            int uAccountId,
                            byte[] pPrice) throws JDataException {
            AccountPrice myPrice;

            /* Create the price and PricePoint */
            myPrice = new AccountPrice(this, uId, uControlId, uAccountId, pDate, pPrice);

            /* Check that this PriceId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, myPrice, "Duplicate PriceId <" + uId + ">");

            /* Validate the price */
            myPrice.validate();

            /* Handle validation failure */
            if (myPrice.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myPrice, "Failed validation");

            /* Add to the list */
            add(myPrice);
        }

        /**
         * Allow a price to be added
         * @param pAccount the account
         * @param pDate the date
         * @param pPrice the price
         * @return the new item
         * @throws JDataException
         */
        public AccountPrice addItem(Account pAccount,
                                    DateDay pDate,
                                    Price pPrice) throws JDataException {
            AccountPrice myPrice;

            /* Create the price and PricePoint */
            myPrice = new AccountPrice(this, pAccount, pDate, pPrice);

            /* Validate the price */
            myPrice.validate();

            /* Handle validation failure */
            if (myPrice.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myPrice, "Failed validation");

            /* Add to the list */
            add(myPrice);

            /* Return the caller */
            return myPrice;
        }
    }
}
