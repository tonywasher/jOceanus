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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * AccountCategoryType data type.
 * @author Tony Washer
 */
public class AccountCategoryType
        extends StaticData<AccountCategoryType, AccountCategoryClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.ACCOUNTTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.ACCOUNTTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Account class of the AccountCategoryType.
     * @return the class
     */
    public AccountCategoryClass getAccountClass() {
        return super.getStaticClass();
    }

    @Override
    public AccountCategoryType getBase() {
        return (AccountCategoryType) super.getBase();
    }

    @Override
    public AccountCategoryTypeList getList() {
        return (AccountCategoryTypeList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Category Type with
     * @param pAcCatType The Account Category Type to copy
     */
    protected AccountCategoryType(final AccountCategoryTypeList pList,
                                  final AccountCategoryType pAcCatType) {
        super(pList, pAcCatType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Category Type with
     * @param pName Name of Account Category Type
     * @throws JOceanusException on error
     */
    private AccountCategoryType(final AccountCategoryTypeList pList,
                                final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Category Type with
     * @param pClass Class of Account Category Type
     * @throws JOceanusException on error
     */
    private AccountCategoryType(final AccountCategoryTypeList pList,
                                final AccountCategoryClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private AccountCategoryType(final AccountCategoryTypeList pList,
                                final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    /**
     * Represents a list of {@link AccountCategoryType} objects.
     */
    public static class AccountCategoryTypeList
            extends StaticList<AccountCategoryType, AccountCategoryClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return AccountCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<AccountCategoryClass> getEnumClass() {
            return AccountCategoryClass.class;
        }

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public AccountCategoryTypeList(final DataSet<?, ?> pData) {
            super(AccountCategoryType.class, pData, MoneyWiseDataType.ACCOUNTTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountCategoryTypeList(final AccountCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        protected AccountCategoryTypeList getEmptyList(final ListStyle pStyle) {
            AccountCategoryTypeList myList = new AccountCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountCategoryType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an AccountCategoryType */
            if (!(pItem instanceof AccountCategoryType)) {
                throw new UnsupportedOperationException();
            }

            AccountCategoryType myType = new AccountCategoryType(this, (AccountCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public AccountCategoryType addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add an AccountCategoryType to the list.
         * @param pActCatType the Name of the account category type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pActCatType) throws JOceanusException {
            /* Create a new Account Category Type */
            AccountCategoryType myActType = new AccountCategoryType(this, pActCatType);

            /* Check that this AccountCategoryType has not been previously added */
            if (findItemByName(pActCatType) != null) {
                myActType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myActType, ERROR_VALIDATION);
            }

            /* Check that this AccountCategoryTypeId has not been previously added */
            if (!isIdUnique(myActType.getId())) {
                myActType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myActType, ERROR_VALIDATION);
            }

            /* Add the Account Category to the list */
            append(myActType);

            /* Validate the ActType */
            myActType.validate();

            /* Handle validation failure */
            if (myActType.hasErrors()) {
                throw new JMoneyWiseDataException(myActType, ERROR_VALIDATION);
            }
        }

        @Override
        public AccountCategoryType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the type */
            AccountCategoryType myType = new AccountCategoryType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (AccountCategoryClass myClass : AccountCategoryClass.values()) {
                /* Create new element */
                AccountCategoryType myActType = new AccountCategoryType(this, myClass);

                /* Add the AccountCategoryType to the list */
                append(myActType);

                /* Validate the AccountCategoryType */
                myActType.validate();

                /* Handle validation failure */
                if (myActType.hasErrors()) {
                    throw new JMoneyWiseDataException(myActType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
