/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.EncryptedData.EncryptedRate;
import net.sourceforge.joceanus.jdatamanager.EncryptedValueSet;
import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.EncryptedItem;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimalParser;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * AccountRate data type.
 * @author Tony Washer
 */
public class AccountRate
        extends EncryptedItem
        implements Comparable<AccountRate> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountRate.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountRate.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Account Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataAccount"));

    /**
     * Rate Field Id.
     */
    public static final JDataField FIELD_RATE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataRate"));

    /**
     * Bonus Field Id.
     */
    public static final JDataField FIELD_BONUS = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataBonus"));

    /**
     * EndDate Field Id.
     */
    public static final JDataField FIELD_ENDDATE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataEndDate"));

    /**
     * Null Date Error.
     */
    private static final String ERROR_NULLDATE = NLS_BUNDLE.getString("ErrorNullDate");

    /**
     * Obtain Rate.
     * @return the rate
     */
    public JRate getRate() {
        return getRate(getValueSet());
    }

    /**
     * Obtain Encrypted rate.
     * @return the Bytes
     */
    public byte[] getRateBytes() {
        return getRateBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Rate Field.
     * @return the Field
     */
    private EncryptedRate getRateField() {
        return getRateField(getValueSet());
    }

    /**
     * Obtain Bonus.
     * @return the bonus rate
     */
    public JRate getBonus() {
        return getBonus(getValueSet());
    }

    /**
     * Obtain Encrypted bonus.
     * @return the Bytes
     */
    public byte[] getBonusBytes() {
        return getBonusBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Rate Field.
     * @return the Field
     */
    private EncryptedRate getBonusField() {
        return getBonusField(getValueSet());
    }

    /**
     * Obtain date.
     * @return the date
     */
    public JDateDay getDate() {
        return getEndDate();
    }

    /**
     * Obtain End Date.
     * @return the End Date
     */
    public JDateDay getEndDate() {
        return getEndDate(getValueSet());
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
     * Obtain Rate.
     * @param pValueSet the valueSet
     * @return the Rate
     */
    public static JRate getRate(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_RATE, JRate.class);
    }

    /**
     * Obtain Encrypted Rate.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getRateBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_RATE);
    }

    /**
     * Obtain Rate Field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedRate getRateField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RATE, EncryptedRate.class);
    }

    /**
     * Obtain Bonus.
     * @param pValueSet the valueSet
     * @return the Bonus
     */
    public static JRate getBonus(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_BONUS, JRate.class);
    }

    /**
     * Obtain Encrypted Bonus.
     * @param pValueSet the valueSet
     * @return the Bytes
     */
    public static byte[] getBonusBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_BONUS);
    }

    /**
     * Obtain Bonus field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedRate getBonusField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BONUS, EncryptedRate.class);
    }

    /**
     * Obtain End Date.
     * @param pValueSet the valueSet
     * @return the End Date
     */
    public static JDateDay getEndDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ENDDATE, JDateDay.class);
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
     * Set the rate.
     * @param pValue the rate
     * @throws JOceanusException on error
     */
    private void setValueRate(final JRate pValue) throws JOceanusException {
        setEncryptedValue(FIELD_RATE, pValue);
    }

    /**
     * Set the rate.
     * @param pBytes the encrypted rate
     * @throws JOceanusException on error
     */
    private void setValueRate(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_RATE, pBytes, JRate.class);
    }

    /**
     * Set the rate.
     * @param pValue the rate
     */
    private void setValueRate(final EncryptedRate pValue) {
        getValueSet().setValue(FIELD_RATE, pValue);
    }

    /**
     * Set the bonus rate.
     * @param pValue the bonus rate
     * @throws JOceanusException on error
     */
    private void setValueBonus(final JRate pValue) throws JOceanusException {
        setEncryptedValue(FIELD_BONUS, pValue);
    }

    /**
     * Set the encrypted bonus.
     * @param pBytes the encrypted bonus
     * @throws JOceanusException on error
     */
    private void setValueBonus(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_BONUS, pBytes, JRate.class);
    }

    /**
     * Set the bonus.
     * @param pValue the bonus
     */
    private void setValueBonus(final EncryptedRate pValue) {
        getValueSet().setValue(FIELD_BONUS, pValue);
    }

    /**
     * Set the end date rate.
     * @param pValue the date
     */
    private void setValueEndDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_ENDDATE, pValue);
    }

    @Override
    public MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public AccountRate getBase() {
        return (AccountRate) super.getBase();
    }

    @Override
    public AccountRateList getList() {
        return (AccountRateList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pPeriod The Period to copy
     */
    protected AccountRate(final AccountRateList pList,
                          final AccountRate pPeriod) {
        /* Set standard values */
        super(pList, pPeriod);
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public AccountRate(final AccountRateList pList) {
        super(pList, 0);
        setValueAccount(pList.theAccount);
    }

    /**
     * Open Constructor.
     * @param pList the list
     * @param uId the id
     * @param pAccount the account name
     * @param pEndDate the end date
     * @param pRate the rate
     * @param pBonus the bonus
     * @throws JOceanusException on error
     */
    private AccountRate(final AccountRateList pList,
                        final Integer uId,
                        final String pAccount,
                        final JDateDay pEndDate,
                        final String pRate,
                        final String pBonus) throws JOceanusException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access the parser */
            MoneyWiseData myDataSet = getDataSet();
            JDataFormatter myFormatter = myDataSet.getDataFormatter();
            JDecimalParser myParser = myFormatter.getDecimalParser();

            /* Record the account */
            setValueAccount(pAccount);

            /* Record the date */
            setValueEndDate(pEndDate);

            /* Set the encrypted objects */
            setValueRate(myParser.parseRateValue(pRate));
            setValueBonus(myParser.parseRateValue(pBonus));

            /* Catch Exceptions */
        } catch (IllegalArgumentException | JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Secure Constructor.
     * @param pList the list
     * @param uId the id
     * @param uControlId the control id
     * @param uAccountId the account id
     * @param pEndDate the end date
     * @param pRate the rate
     * @param pBonus the bonus
     * @throws JOceanusException on error
     */
    private AccountRate(final AccountRateList pList,
                        final Integer uId,
                        final Integer uControlId,
                        final Integer uAccountId,
                        final JDateDay pEndDate,
                        final byte[] pRate,
                        final byte[] pBonus) throws JOceanusException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueAccount(uAccountId);

            /* Store the controlId */
            setControlKey(uControlId);

            /* Record the date */
            setValueEndDate(pEndDate);

            /* Set the encrypted objects */
            setValueRate(pRate);
            setValueBonus(pBonus);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Compare this rate to another to establish sort order.
     * @param pThat The Rate to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final AccountRate pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the date differs */
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
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
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
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueAccount(myAct);
        } else if (myAccount instanceof String) {
            Account myAct = myAccounts.findItemByName((String) myAccount);
            if (myAct == null) {
                addError(ERROR_UNKNOWN, FIELD_ACCOUNT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueAccount(myAct);
        }
    }

    /**
     * Validate the rate.
     */
    @Override
    public void validate() {
        AccountRateList myList = getList();
        MoneyWiseData mySet = getDataSet();
        JDateDay myDate = getEndDate();
        JRate myRate = getRate();
        JRate myBonus = getBonus();

        /* Access the next element (if any) */
        AccountRate myNext = myList.peekNext(this);

        /* Determine whether this is the last entry for the account */
        boolean isLast = (myNext == null)
                         || !Difference.isEqual(myNext.getAccount(), getAccount());

        /* If the date is null then we must be the last element for the account */
        if (myDate == null) {
            /* Can only have null date on last entry for account */
            if (!isLast) {
                addError(ERROR_NULLDATE, FIELD_ENDDATE);
            }

            /* If we have a date */
        } else {
            /* The date must be unique for this account */
            if (myList.countInstances(myDate, getAccount()) > 1) {
                addError(ERROR_DUPLICATE, FIELD_ENDDATE);
            }

            /* The date must be in-range (unless it is the last one) */
            if ((!isLast)
                && (mySet.getDateRange().compareTo(myDate) != 0)) {
                addError(ERROR_RANGE, FIELD_ENDDATE);
            }
        }

        /* The Rate must be non-zero and greater than zero */
        if (myRate == null) {
            addError(ERROR_MISSING, FIELD_RATE);
        } else if (myRate.isZero()) {
            addError(ERROR_ZERO, FIELD_RATE);
        } else if (!myRate.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_RATE);
        }

        /* The bonus rate must be non-zero if it exists */
        if (myBonus != null) {
            if (myRate.isZero()) {
                addError(ERROR_ZERO, FIELD_BONUS);
            } else if (!myRate.isPositive()) {
                addError(ERROR_NEGATIVE, FIELD_BONUS);
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Set a new rate.
     * @param pRate the rate
     * @throws JOceanusException on error
     */
    public void setRate(final JRate pRate) throws JOceanusException {
        setValueRate(pRate);
    }

    /**
     * Set a new bonus.
     * @param pBonus the rate
     * @throws JOceanusException on error
     */
    public void setBonus(final JRate pBonus) throws JOceanusException {
        setValueBonus(pBonus);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setEndDate(final JDateDay pDate) {
        setValueEndDate(new JDateDay(pDate));
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the underlying account */
        getAccount().touchItem(this);
    }

    /**
     * Update Rate from a Rate extract.
     * @param pRate the updated item
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pRate) {
        /* Can only update from an AccountRate */
        if (!(pRate instanceof AccountRate)) {
            return false;
        }

        AccountRate myRate = (AccountRate) pRate;

        /* Store the current detail into history */
        pushHistory();

        /* Update the rate if required */
        if (!Difference.isEqual(getRate(), myRate.getRate())) {
            setValueRate(myRate.getRateField());
        }

        /* Update the bonus if required */
        if (!Difference.isEqual(getBonus(), myRate.getBonus())) {
            setValueBonus(myRate.getBonusField());
        }

        /* Update the date if required */
        if (!Difference.isEqual(getEndDate(), myRate.getEndDate())) {
            setValueEndDate(myRate.getEndDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * List class.
     */
    public static class AccountRateList
            extends EncryptedList<AccountRate> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountRateList.class.getSimpleName(), DataList.FIELD_DEFS);

        /**
         * Account Field Id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return (theAccount == null)
                        ? JDataFieldValue.SKIP
                        : theAccount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The Account.
         */
        private Account theAccount = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE rate list.
         * @param pData the DataSet for the list
         */
        protected AccountRateList(final MoneyWiseData pData) {
            super(AccountRate.class, pData);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountRateList(final AccountRateList pSource) {
            super(pSource);
        }

        @Override
        protected AccountRateList getEmptyList(final ListStyle pStyle) {
            AccountRateList myList = new AccountRateList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountRateList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountRateList) super.cloneList(pDataSet);
        }

        /**
         * Construct an edit extract of a Rate list.
         * @param pAccount The account to extract rates for
         * @return the edit list
         */
        public AccountRateList deriveEditList(final Account pAccount) {
            /* Build an empty List */
            AccountRateList myList = getEmptyList(ListStyle.EDIT);

            /* Store the account */
            myList.theAccount = pAccount;

            /* Access the list iterator */
            Iterator<AccountRate> myIterator = iterator();

            /* Loop through the list */
            while (myIterator.hasNext()) {
                AccountRate myCurr = myIterator.next();

                /* Check the account */
                int myResult = pAccount.compareTo(myCurr.getAccount());

                /* Skip different accounts */
                if (myResult != 0) {
                    continue;
                }

                /* Copy the item */
                AccountRate myItem = new AccountRate(myList, myCurr);
                myList.append(myItem);
            }

            /* Return the List */
            return myList;
        }

        /* Is this list locked */
        @Override
        public boolean isLocked() {
            return (theAccount != null)
                   && theAccount.isLocked();
        }

        /**
         * Add a new item to the core list.
         * @param pRate item
         * @return the newly added item
         */
        @Override
        public AccountRate addCopyItem(final DataItem pRate) {
            /* Can only clone an AccountRate */
            if (!(pRate instanceof AccountRate)) {
                return null;
            }

            AccountRate myRate = new AccountRate(this, (AccountRate) pRate);
            add(myRate);
            return myRate;
        }

        /**
         * Add a new item to the edit list.
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
         * Count the instances of a date.
         * @param pDate the date
         * @param pAccount the account
         * @return The Item if present (or null)
         */
        protected int countInstances(final JDateDay pDate,
                                     final Account pAccount) {
            /* Access the list iterator */
            Iterator<AccountRate> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                AccountRate myCurr = myIterator.next();
                if ((pDate.equals(myCurr.getEndDate()))
                    && (pAccount.equals(myCurr.getAccount()))) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Obtain the most relevant rate for an Account and a Date.
         * @param pAccount the Account for which to get the rate
         * @param pDate the date from which a rate is required
         * @return The relevant Rate record
         */
        public AccountRate getLatestRate(final Account pAccount,
                                         final JDateDay pDate) {
            /* Access the list iterator */
            Iterator<AccountRate> myIterator = listIterator();

            /* Loop through the Rates */
            while (myIterator.hasNext()) {
                AccountRate myCurr = myIterator.next();
                /* Skip records that do not belong to this account */
                if (!Difference.isEqual(myCurr.getAccount(), pAccount)) {
                    continue;
                }

                /* Access the date */
                JDateDay myDate = myCurr.getDate();

                /* break loop if we have the correct record */
                if ((myDate == null)
                    || (myDate.compareTo(pDate) >= 0)) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Allow a rate to be added.
         * @param uId the id
         * @param pAccount the account
         * @param pRate the Rate
         * @param pDate the end date
         * @param pBonus the Bonus
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer uId,
                                final String pAccount,
                                final String pRate,
                                final JDateDay pDate,
                                final String pBonus) throws JOceanusException {
            /* Create the ratePeriod */
            AccountRate myRate = new AccountRate(this, uId, pAccount, pDate, pRate, pBonus);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(myRate.getId())) {
                throw new JMoneyWiseDataException(myRate, "Duplicate RateId");
            }

            /* Add to the list */
            append(myRate);
        }

        /**
         * Load an Encrypted Rate.
         * @param uId the id
         * @param uControlId the control id
         * @param uAccountId the account id
         * @param pRate the Rate
         * @param pDate the end date
         * @param pBonus the Bonus
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Integer uAccountId,
                                  final byte[] pRate,
                                  final JDateDay pDate,
                                  final byte[] pBonus) throws JOceanusException {
            /* Create the period */
            AccountRate myRate = new AccountRate(this, uId, uControlId, uAccountId, pDate, pRate, pBonus);

            /* Check that this RateId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JMoneyWiseDataException(myRate, "Duplicate RateId");
            }

            /* Add to the list */
            append(myRate);
        }
    }
}
