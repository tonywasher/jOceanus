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

import net.sourceforge.joceanus.jmetis.viewer.DataType;
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
 * AccountInfoType data type.
 * @author Tony Washer
 */
public class AccountInfoType
                            extends StaticData<AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.ACCOUNTINFOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.ACCOUNTINFOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * WebSite length.
     */
    protected static final int WEBSITE_LEN = 50;

    /**
     * Data length.
     */
    protected static final int DATA_LEN = 20;

    /**
     * Comment length.
     */
    protected static final int COMMENT_LEN = 50;

    /**
     * Notes length.
     */
    protected static final int NOTES_LEN = 500;

    /**
     * Return the Account Info class of the AccountInfoType.
     * @return the class
     */
    public AccountInfoClass getInfoClass() {
        return super.getStaticClass();
    }

    /**
     * Return the Data Type of the AccountInfoType.
     * @return the data type
     */
    public DataType getDataType() {
        return getInfoClass().getDataType();
    }

    /**
     * is this a Link?
     * @return true/false
     */
    public boolean isLink() {
        return getInfoClass().isLink();
    }

    @Override
    public AccountInfoType getBase() {
        return (AccountInfoType) super.getBase();
    }

    @Override
    public AccountInfoTypeList getList() {
        return (AccountInfoTypeList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Account Info Type with
     * @param pInfoType The Account Info Type to copy
     */
    protected AccountInfoType(final AccountInfoTypeList pList,
                              final AccountInfoType pInfoType) {
        super(pList, pInfoType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Account Info Type with
     * @param pName Name of Account Info Type
     * @throws JOceanusException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Info Type with
     * @param pClass Class of Account Info Type
     * @throws JOceanusException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final AccountInfoClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Account Info Type with
     * @param pId the id
     * @param isEnabled is the account info type enabled
     * @param pOrder the sort order
     * @param pName Name of Account Info Type
     * @param pDesc Description of Account Info Type
     * @throws JOceanusException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final Integer pId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final String pName,
                            final String pDesc) throws JOceanusException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    /**
     * Represents a list of {@link AccountInfoType} objects.
     */
    public static class AccountInfoTypeList
                                           extends StaticList<AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
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
            return AccountInfoType.FIELD_DEFS;
        }

        @Override
        protected Class<AccountInfoClass> getEnumClass() {
            return AccountInfoClass.class;
        }

        /**
         * Construct an empty CORE account type list.
         * @param pData the DataSet for the list
         */
        public AccountInfoTypeList(final DataSet<?, ?> pData) {
            super(AccountInfoType.class, pData, MoneyWiseDataType.ACCOUNTINFOTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountInfoTypeList(final AccountInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        protected AccountInfoTypeList getEmptyList(final ListStyle pStyle) {
            AccountInfoTypeList myList = new AccountInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountInfoTypeList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountInfoTypeList) super.cloneList(pDataSet);
        }

        @Override
        public AccountInfoType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an AccountInfoType */
            if (!(pItem instanceof AccountInfoType)) {
                return null;
            }

            AccountInfoType myType = new AccountInfoType(this, (AccountInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public AccountInfoType addNewItem() {
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
         * Add a Basic Open AccountInfoType to the list.
         * @param pInfoType the Name of the account info type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pInfoType) throws JOceanusException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, pInfoType);

            /* Check that this InfoType has not been previously added */
            if (findItemByName(pInfoType) != null) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);

            /* Validate the ActType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Add an Open AccountInfoType to the list.
         * @param pId the Id of the account info type
         * @param isEnabled is the account info type enabled
         * @param pOrder the sort order
         * @param pInfoType the Name of the account info type
         * @param pDesc the Description of the account info type
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pInfoType,
                                final String pDesc) throws JOceanusException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, pId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);

            /* Validate the AccountInfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }
        }

        @Override
        public AccountInfoType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the type */
            AccountInfoType myType = new AccountInfoType(this, pValues);

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
            for (AccountInfoClass myClass : AccountInfoClass.values()) {
                /* Create new element */
                AccountInfoType myType = new AccountInfoType(this, myClass);

                /* Add the AccountInfoType to the list */
                append(myType);

                /* Validate the AccountInfoType */
                myType.validate();

                /* Handle validation failure */
                if (myType.hasErrors()) {
                    throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
