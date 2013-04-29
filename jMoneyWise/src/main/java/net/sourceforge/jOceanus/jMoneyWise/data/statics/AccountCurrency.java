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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import java.util.Currency;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * AccountCurrency data type.
 * @author Tony Washer
 */
public class AccountCurrency
        extends StaticData<AccountCurrency, AccountCurrencyClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountCurrency.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "AccountCurrencies";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Currency class of the AccountCurrency.
     * @return the class
     */
    public AccountCurrencyClass getAccountClass() {
        return super.getStaticClass();
    }

    @Override
    public AccountCurrency getBase() {
        return (AccountCurrency) super.getBase();
    }

    /**
     * Return the Currency of the AccountCurrency.
     * @return the currency
     */
    public Currency getCurrency() {
        return getAccountClass().getCurrency();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pCurrency The Account Currency to copy
     */
    protected AccountCurrency(final AccountCurrencyList pList,
                              final AccountCurrency pCurrency) {
        super(pList, pCurrency);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pName Name of Account Currency
     * @throws JDataException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final String pName) throws JDataException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Currency with
     * @param pClass Class of Account Currency
     * @throws JDataException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final AccountCurrencyClass pClass) throws JDataException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pId the id
     * @param isEnabled is the account currency enabled
     * @param pOrder the sort order
     * @param pName Name of Account Currency
     * @param pDesc Description of Account Currency
     * @throws JDataException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final Integer pId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final String pName,
                            final String pDesc) throws JDataException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Account Currency with
     * @param pId ID of Account Currency
     * @param pControlId the control id of the new item
     * @param isEnabled is the account currency enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of Account Currency
     * @param pDesc Encrypted Description of Account Currency
     * @throws JDataException on error
     */
    private AccountCurrency(final AccountCurrencyList pList,
                            final Integer pId,
                            final Integer pControlId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final byte[] pName,
                            final byte[] pDesc) throws JDataException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link AccountCurrency} objects.
     */
    public static class AccountCurrencyList
            extends StaticList<AccountCurrency, AccountCurrencyClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountCurrency.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<AccountCurrencyClass> getEnumClass() {
            return AccountCurrencyClass.class;
        }

        /**
         * Construct an empty CORE account currency list.
         * @param pData the DataSet for the list
         */
        public AccountCurrencyList(final DataSet<?> pData) {
            super(AccountCurrency.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountCurrencyList(final AccountCurrencyList pSource) {
            super(pSource);
        }

        @Override
        protected AccountCurrencyList getEmptyList(final ListStyle pStyle) {
            AccountCurrencyList myList = new AccountCurrencyList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountCurrencyList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (AccountCurrencyList) super.cloneList(pDataSet);
        }

        @Override
        public AccountCurrencyList deriveList(final ListStyle pStyle) throws JDataException {
            return (AccountCurrencyList) super.deriveList(pStyle);
        }

        @Override
        public AccountCurrencyList deriveDifferences(final DataList<AccountCurrency> pOld) {
            return (AccountCurrencyList) super.deriveDifferences(pOld);
        }

        @Override
        public AccountCurrency addCopyItem(final DataItem pItem) {
            /* Can only clone an AccountCurrency */
            if (!(pItem instanceof AccountCurrency)) {
                return null;
            }

            AccountCurrency myCurr = new AccountCurrency(this, (AccountCurrency) pItem);
            add(myCurr);
            return myCurr;
        }

        @Override
        public AccountCurrency addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pCurrency the Name of the account currency
         * @throws JDataException on error
         */
        public void addBasicItem(final String pCurrency) throws JDataException {
            /* Create a new Account Currency */
            AccountCurrency myCurr = new AccountCurrency(this, pCurrency);

            /* Check that this AccountCurrency has not been previously added */
            if (findItemByName(pCurrency) != null) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JDataException(ExceptionClass.DATA, myCurr, ERROR_VALIDATION);
            }

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(myCurr.getId())) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            append(myCurr);

            /* Validate the Currency */
            myCurr.validate();

            /* Handle validation failure */
            if (myCurr.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCurr, ERROR_VALIDATION);
            }
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pId the Id of the account currency
         * @param isEnabled is the account currency enabled
         * @param pOrder the sort order
         * @param pCurrency the Name of the account currency
         * @param pDesc the Description of the account currency
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pCurrency,
                                final String pDesc) throws JDataException {
            /* Create a new Account Currency */
            AccountCurrency myCurr = new AccountCurrency(this, pId, isEnabled, pOrder, pCurrency, pDesc);

            /* Check that this AccountCurrencyTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JDataException(ExceptionClass.DATA, myCurr, ERROR_VALIDATION);
            }

            /* Add the Account Currency to the list */
            append(myCurr);

            /* Validate the AccountCurrency */
            myCurr.validate();

            /* Handle validation failure */
            if (myCurr.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCurr, ERROR_VALIDATION);
            }
        }

        /**
         * Add an AccountCurrency to the list.
         * @param pId the Id of the account currency
         * @param pControlId the control id of the new item
         * @param isEnabled is the account currency enabled
         * @param pOrder the sort order
         * @param pCurrency the encrypted Name of the account currency
         * @param pDesc the Encrypted Description of the account currency
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pCurrency,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Account Currency */
            AccountCurrency myCurr = new AccountCurrency(this, pId, pControlId, isEnabled, pOrder, pCurrency, pDesc);

            /* Check that this AccountCurrencyId has not been previously added */
            if (!isIdUnique(pId)) {
                myCurr.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCurr, ERROR_VALIDATION);
            }

            /* Add the AccountCurrency to the list */
            append(myCurr);

            /* Validate the AccountCurrency */
            myCurr.validate();

            /* Handle validation failure */
            if (myCurr.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCurr, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JDataException on error
         */
        public void populateDefaults() throws JDataException {
            /* Loop through all elements */
            for (AccountCurrencyClass myClass : AccountCurrencyClass.values()) {
                /* Create new element */
                AccountCurrency myCurr = new AccountCurrency(this, myClass);

                /* Add the AccountCurrency to the list */
                append(myCurr);

                /* Validate the AccountCurrency */
                myCurr.validate();

                /* Handle validation failure */
                if (myCurr.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myCurr, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
