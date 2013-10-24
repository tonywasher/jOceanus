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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jdatamanager.DataType;
import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;

/**
 * AccountInfoType data type.
 * @author Tony Washer
 */
public class AccountInfoType
        extends StaticData<AccountInfoType, AccountInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountInfoType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

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
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final String pName) throws JDataException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Account Info Type with
     * @param pClass Class of Account Info Type
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final AccountInfoClass pClass) throws JDataException {
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
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final Integer pId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final String pName,
                            final String pDesc) throws JDataException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Account Info Type with
     * @param pId ID of Account Info Type
     * @param pControlId the control id of the new item
     * @param isEnabled is the account info type enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of Account Info Type
     * @param pDesc Encrypted Description of Account Info Type
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final Integer pId,
                            final Integer pControlId,
                            final Boolean isEnabled,
                            final Integer pOrder,
                            final byte[] pName,
                            final byte[] pDesc) throws JDataException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link AccountInfoType} objects.
     */
    public static class AccountInfoTypeList
            extends StaticList<AccountInfoType, AccountInfoClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountInfoTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<AccountInfoClass> getEnumClass() {
            return AccountInfoClass.class;
        }

        /**
         * Construct an empty CORE account type list.
         * @param pData the DataSet for the list
         */
        public AccountInfoTypeList(final DataSet<?> pData) {
            super(AccountInfoType.class, pData, ListStyle.CORE);
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
        public AccountInfoTypeList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (AccountInfoTypeList) super.cloneList(pDataSet);
        }

        @Override
        public AccountInfoTypeList deriveList(final ListStyle pStyle) throws JDataException {
            return (AccountInfoTypeList) super.deriveList(pStyle);
        }

        @Override
        public AccountInfoTypeList deriveDifferences(final DataList<AccountInfoType> pOld) {
            return (AccountInfoTypeList) super.deriveDifferences(pOld);
        }

        @Override
        public AccountInfoType addCopyItem(final DataItem pItem) {
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
         * Add a Basic Open AccountInfoType to the list.
         * @param pInfoType the Name of the account info type
         * @throws JDataException on error
         */
        public void addBasicItem(final String pInfoType) throws JDataException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, pInfoType);

            /* Check that this InfoType has not been previously added */
            if (findItemByName(pInfoType) != null) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);

            /* Validate the ActType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Add an Open AccountInfoType to the list.
         * @param pId the Id of the account info type
         * @param isEnabled is the account info type enabled
         * @param pOrder the sort order
         * @param pInfoType the Name of the account info type
         * @param pDesc the Description of the account info type
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pInfoType,
                                final String pDesc) throws JDataException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, pId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);

            /* Validate the AccountInfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Add a Secure AccountInfoType to the list.
         * @param pId the Id of the account info type
         * @param pControlId the control id of the new item
         * @param isEnabled is the account info type enabled
         * @param pOrder the sort order
         * @param pInfoType the encrypted Name of the account info type
         * @param pDesc the Encrypted Description of the account info type
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pInfoType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, pId, pControlId, isEnabled, pOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(pId)) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myInfoType, ERROR_VALIDATION);
            }

            /* Add the Info Type to the list */
            append(myInfoType);

            /* Validate the InfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JDataException on error
         */
        public void populateDefaults() throws JDataException {
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
                    throw new JDataException(ExceptionClass.VALIDATE, myType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
