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
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedRate;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;

public class AccountRate extends EncryptedItem<AccountRate> {
    /**
     * Object name
     */
    public static final String OBJECT_NAME = AccountRate.class.getSimpleName();

    /**
     * List name
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    /* Called from constructor */
    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /* Field IDs */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField("Account");
    public static final JDataField FIELD_RATE = FIELD_DEFS.declareEqualityValueField("Rate");
    public static final JDataField FIELD_BONUS = FIELD_DEFS.declareEqualityValueField("Bonus");
    public static final JDataField FIELD_ENDDATE = FIELD_DEFS.declareEqualityValueField("EndDate");

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
    public Rate getRate() {
        return getRate(theValueSet);
    }

    public byte[] getRateBytes() {
        return getRateBytes(theValueSet);
    }

    private EncryptedRate getRateField() {
        return getRateField(theValueSet);
    }

    public Rate getBonus() {
        return getBonus(theValueSet);
    }

    public byte[] getBonusBytes() {
        return getBonusBytes(theValueSet);
    }

    private EncryptedRate getBonusField() {
        return getBonusField(theValueSet);
    }

    public DateDay getDate() {
        return getEndDate();
    }

    public DateDay getEndDate() {
        return getEndDate(theValueSet);
    }

    public Account getAccount() {
        return getAccount(theValueSet);
    }

    public static Account getAccount(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, Account.class);
    }

    public static Rate getRate(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_RATE, Rate.class);
    }

    public static byte[] getRateBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_RATE);
    }

    private static EncryptedRate getRateField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, EncryptedRate.class);
    }

    public static Rate getBonus(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_BONUS, Rate.class);
    }

    public static byte[] getBonusBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_BONUS);
    }

    private static EncryptedRate getBonusField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BONUS, EncryptedRate.class);
    }

    public static DateDay getEndDate(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ENDDATE, DateDay.class);
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

    private void setValueRate(String pRate) throws JDataException {
        setValueRate(new Rate(pRate));
    }

    private void setValueRate(Rate pRate) throws JDataException {
        setEncryptedValue(FIELD_RATE, pRate);
    }

    private void setValueRate(byte[] pRate) throws JDataException {
        setEncryptedValue(FIELD_RATE, pRate, Rate.class);
    }

    private void setValueRate(EncryptedRate pRate) {
        theValueSet.setValue(FIELD_RATE, pRate);
    }

    private void setValueBonus(String pRate) throws JDataException {
        setValueBonus(new Rate(pRate));
    }

    private void setValueBonus(Rate pRate) throws JDataException {
        setEncryptedValue(FIELD_BONUS, pRate);
    }

    private void setValueBonus(byte[] pRate) throws JDataException {
        setEncryptedValue(FIELD_BONUS, pRate, Rate.class);
    }

    private void setValueBonus(EncryptedRate pRate) {
        theValueSet.setValue(FIELD_BONUS, pRate);
    }

    private void setValueEndDate(DateDay pDate) {
        theValueSet.setValue(FIELD_ENDDATE, pDate);
    }

    /* Linking methods */
    @Override
    public AccountRate getBase() {
        return (AccountRate) super.getBase();
    }

    /**
     * Construct a copy of a Rate Period
     * @param pList
     * @param pPeriod The Period to copy
     */
    protected AccountRate(AccountRateList pList,
                          AccountRate pPeriod) {
        /* Set standard values */
        super(pList, pPeriod);
        ListStyle myOldStyle = pPeriod.getStyle();

        /* Switch on the ListStyle */
        switch (getStyle()) {
            case EDIT:
                /* If this is a view creation */
                if (myOldStyle == ListStyle.CORE) {
                    /* Rate is based on the original element */
                    setBase(pPeriod);
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
                /* Reset Id if this is an insert from a view */
                if (myOldStyle == ListStyle.EDIT)
                    setId(0);
                pList.setNewId(this);
                break;
            case UPDATE:
                setBase(pPeriod);
                setState(pPeriod.getState());
                break;
        }
    }

    /* Insert constructor */
    public AccountRate(AccountRateList pList) {
        super(pList, 0);
        setValueAccount(pList.theAccount);
        pList.setNewId(this);
    }

    /* Extract constructor */
    private AccountRate(AccountRateList pList,
                        int uId,
                        int uAccountId,
                        Date pEndDate,
                        String pRate,
                        String pBonus) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueAccount(uAccountId);

            /* Look up the Account */
            FinanceData myData = pList.getData();
            Account myAccount = myData.getAccounts().searchFor(uAccountId);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
            setValueAccount(myAccount);

            /* Record the date */
            if (pEndDate != null)
                setValueEndDate(new DateDay(pEndDate));

            /* Set the encrypted objects */
            setValueRate(pRate);
            setValueBonus(pBonus);

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
    private AccountRate(AccountRateList pList,
                        int uId,
                        int uControlId,
                        int uAccountId,
                        Date pEndDate,
                        byte[] pRate,
                        byte[] pBonus) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueAccount(uAccountId);

            /* Store the controlId */
            setControlKey(uControlId);

            /* Look up the Account */
            FinanceData myData = pList.getData();
            Account myAccount = myData.getAccounts().searchFor(uAccountId);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Id");
            setValueAccount(myAccount);

            /* Record the date */
            if (pEndDate != null)
                setValueEndDate(new DateDay(pEndDate));

            /* Set the encrypted objects */
            setValueRate(pRate);
            setValueBonus(pBonus);

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Compare this rate to another to establish sort order.
     * @param pThat The Rate to compare to
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

        /* Make sure that the object is a Rate */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as a Rate */
        AccountRate myThat = (AccountRate) pThat;

        /* If the date differs */
        if (this.getEndDate() != myThat.getEndDate()) {
            /* Handle null dates */
            if (this.getEndDate() == null)
                return 1;
            if (myThat.getEndDate() == null)
                return -1;

            /* Compare the dates */
            iDiff = getEndDate().compareTo(myThat.getEndDate());
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

        /* Update to use the local copy of the Accounts */
        Account myAct = getAccount();
        Account myNewAct = myAccounts.searchFor(myAct.getId());
        setValueAccount(myNewAct);
    }

    /**
     * Validate the rate
     */
    @Override
    public void validate() {
        AccountRate myCurr;
        DateDay myDate = getEndDate();
        AccountRateList myList = (AccountRateList) getList();
        FinanceData mySet = myList.getData();

        /* If the date is null then we must be the last element for the account */
        if (myDate == null) {
            /* Access the next element (if any) */
            myCurr = myList.peekNext(this);

            /* Can only have null date on last entry for account */
            if ((myCurr != null) && (Difference.isEqual(myCurr.getAccount(), getAccount()))) {
                addError("Null date is only allowed on last date", FIELD_ENDDATE);
            }
        }

        /* If we have a date */
        else if (myDate != null) {
            /* The date must be unique for this account */
            if (myList.countInstances(myDate, getAccount()) > 1) {
                addError("Rate Date must be unique", FIELD_ENDDATE);
            }

            /* The date must be in-range (unless it is the last one) */
            if ((myList.peekNext(this) != null) && (mySet.getDateRange().compareTo(myDate) != 0)) {
                addError("Date must be within range", FIELD_ENDDATE);
            }
        }

        /* The rate must be non-zero */
        if ((getRate() == null) || (!getRate().isPositive())) {
            addError("Rate must be positive", FIELD_RATE);
        }

        /* The bonus rate must be non-zero if it exists */
        if ((getBonus() != null) && ((!getBonus().isNonZero()) || (!getBonus().isPositive()))) {
            addError("Bonus Rate must be non-Zero and positive", FIELD_BONUS);
        }

        /* Set validation flag */
        if (!hasErrors())
            setValidEdit();
    }

    /**
     * Set a new rate
     * @param pRate the rate
     * @throws JDataException
     */
    public void setRate(Rate pRate) throws JDataException {
        setValueRate(pRate);
    }

    /**
     * Set a new bonus
     * @param pBonus the rate
     * @throws JDataException
     */
    public void setBonus(Rate pBonus) throws JDataException {
        setValueBonus(pBonus);
    }

    /**
     * Set a new date
     * 
     * @param pDate the new date
     */
    public void setEndDate(DateDay pDate) {
        setValueEndDate(new DateDay(pDate));
    }

    /**
     * Update Rate from a Rate extract
     * @param pRate the updated item
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(DataItem<?> pRate) {
        AccountRate myRate = (AccountRate) pRate;
        boolean bChanged = false;

        /* Store the current detail into history */
        pushHistory();

        /* Update the rate if required */
        if (!Difference.isEqual(getRate(), myRate.getRate()))
            setValueRate(myRate.getRateField());

        /* Update the bonus if required */
        if (!Difference.isEqual(getBonus(), myRate.getBonus()))
            setValueBonus(myRate.getBonusField());

        /* Update the date if required */
        if (!Difference.isEqual(getEndDate(), myRate.getEndDate()))
            setValueEndDate(myRate.getEndDate());

        /* Check for changes */
        if (checkForHistory()) {
            /* Mark as changed */
            setState(DataState.CHANGED);
            bChanged = true;
        }

        /* Return to caller */
        return bChanged;
    }

    public static class AccountRateList extends EncryptedList<AccountRateList, AccountRate> {
        /**
         * Local Report fields
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                AccountRateList.class.getSimpleName(), DataList.FIELD_DEFS);

        /* Field IDs */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        /* Called from constructor */
        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(JDataField pField) {
            if (pField == FIELD_ACCOUNT)
                return (theAccount == null) ? JDataObject.FIELD_SKIP : theAccount;
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
         * Construct an empty CORE rate list
         * @param pData the DataSet for the list
         */
        protected AccountRateList(FinanceData pData) {
            super(AccountRateList.class, AccountRate.class, pData);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private AccountRateList(AccountRateList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private AccountRateList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            AccountRateList myList = new AccountRateList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public AccountRateList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public AccountRateList getEditList() {
            return null;
        }

        @Override
        public AccountRateList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public AccountRateList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            AccountRateList myList = new AccountRateList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        /**
         * Construct a difference Rate list
         * @param pOld the old Rate list
         * @return the difference list
         */
        @Override
        protected AccountRateList getDifferences(AccountRateList pOld) {
            /* Build an empty Difference List */
            AccountRateList myList = new AccountRateList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Construct an edit extract of a Rate list
         * @param pAccount The account to extract rates for
         * @return the edit list
         */
        public AccountRateList getEditList(Account pAccount) {
            /* Build an empty List */
            AccountRateList myList = new AccountRateList(this);

            /* Make this list the correct style */
            myList.setStyle(ListStyle.EDIT);

            /* Local variables */
            DataListIterator<AccountRate> myIterator;
            AccountRate myCurr;
            AccountRate myItem;

            /* Store the account */
            myList.theAccount = pAccount;

            /* Access the list iterator */
            myIterator = listIterator(true);

            /* Loop through the list */
            while ((myCurr = myIterator.next()) != null) {
                /* Check the account */
                int myResult = pAccount.compareTo(myCurr.getAccount());

                /* Skip different accounts */
                if (myResult != 0)
                    continue;

                /* Copy the item */
                myItem = new AccountRate(myList, myCurr);
                myList.add(myItem);
            }

            /* Return the List */
            return myList;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return ((theAccount != null) && (theAccount.isLocked()));
        }

        /**
         * Add a new item to the core list
         * @param pRate item
         * @return the newly added item
         */
        @Override
        public AccountRate addNewItem(DataItem<?> pRate) {
            AccountRate myRate = new AccountRate(this, (AccountRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list
         * @return the new item
         */
        @Override
        public AccountRate addNewItem() {
            AccountRate myRate = new AccountRate(this);
            myRate.setAccount(theAccount);
            add(myRate);
            return myRate;
        }

        /**
         * Count the instances of a date
         * @param pDate the date
         * @param pAccount
         * @return The Item if present (or null)
         */
        protected int countInstances(DateDay pDate,
                                     Account pAccount) {
            DataListIterator<AccountRate> myIterator;
            AccountRate myCurr;
            int iDiff;
            int iCount = 0;

            /* Access the list iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = pDate.compareTo(myCurr.getEndDate());
                if (iDiff == 0)
                    iDiff = pAccount.compareTo(myCurr.getAccount());
                if (iDiff == 0)
                    iCount++;
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Mark active rates
         */
        protected void markActiveItems() {
            DataListIterator<AccountRate> myIterator;
            AccountRate myCurr;

            /* Access the list iterator */
            myIterator = listIterator();

            /* Loop through the Rates */
            while ((myCurr = myIterator.next()) != null) {
                /* mark the account referred to */
                myCurr.getAccount().touchItem(myCurr);
            }
        }

        /**
         * Obtain the most relevant rate for an Account and a Date
         * @param pAccount the Account for which to get the rate
         * @param pDate the date from which a rate is required
         * @return The relevant Rate record
         */
        public AccountRate getLatestRate(Account pAccount,
                                         DateDay pDate) {
            DataListIterator<AccountRate> myIterator;
            AccountRate myRate = null;
            AccountRate myCurr;
            DateDay myDate;

            /* Access the list iterator */
            myIterator = listIterator();

            /* Loop through the Rates */
            while ((myCurr = myIterator.next()) != null) {
                /* Skip records that do not belong to this account */
                if (!Difference.isEqual(myCurr.getAccount(), pAccount))
                    continue;

                /* Access the date */
                myDate = myCurr.getDate();

                /* break loop if we have the correct record */
                if ((myDate == null) || (myDate.compareTo(pDate) >= 0)) {
                    myRate = myCurr;
                    break;
                }
            }

            /* Return the rate */
            return myRate;
        }

        /**
         * Allow a rate to be added
         * @param uId the id
         * @param pAccount the account
         * @param pRate the Rate
         * @param pDate the end date
         * @param pBonus the Bonus
         * @throws JDataException
         */
        public void addItem(int uId,
                            String pAccount,
                            String pRate,
                            Date pDate,
                            String pBonus) throws JDataException {
            Account myAccount;
            Account.AccountList myAccounts;

            /* Access the Accounts */
            myAccounts = getData().getAccounts();

            /* Look up the Account */
            myAccount = myAccounts.searchFor(pAccount);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, "Rate on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Account [" + pAccount
                        + "]");

            /* Add the rate */
            addItem(uId, myAccount.getId(), pRate, pDate, pBonus);
        }

        /**
         * Load an Extract Rate
         * @param uId the id
         * @param uAccountId the account id
         * @param pRate the Rate
         * @param pDate the end date
         * @param pBonus the Bonus
         * @throws JDataException
         */
        private void addItem(int uId,
                             int uAccountId,
                             String pRate,
                             Date pDate,
                             String pBonus) throws JDataException {
            AccountRate myRate;

            /* Create the period */
            myRate = new AccountRate(this, uId, uAccountId, pDate, pRate, pBonus);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(myRate.getId()))
                throw new JDataException(ExceptionClass.DATA, myRate, "Duplicate RateId");

            /* Validate the rate */
            myRate.validate();

            /* Handle validation failure */
            if (myRate.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myRate, "Failed validation");

            /* Add to the list */
            add(myRate);
        }

        /**
         * Load an Encrypted Rate
         * @param uId the id
         * @param uControlId the control id
         * @param uAccountId the account id
         * @param pRate the Rate
         * @param pDate the end date
         * @param pBonus the Bonus
         * @throws JDataException
         */
        public void addItem(int uId,
                            int uControlId,
                            int uAccountId,
                            byte[] pRate,
                            Date pDate,
                            byte[] pBonus) throws JDataException {
            AccountRate myRate;

            /* Create the period */
            myRate = new AccountRate(this, uId, uControlId, uAccountId, pDate, pRate, pBonus);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, myRate, "Duplicate RateId");

            /* Validate the rate */
            myRate.validate();

            /* Handle validation failure */
            if (myRate.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myRate, "Failed validation");

            /* Add to the list */
            add(myRate);
        }
    }
}
