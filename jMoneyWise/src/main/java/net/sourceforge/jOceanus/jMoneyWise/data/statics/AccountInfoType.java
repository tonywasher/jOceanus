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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * AccountInfoType data type.
 * @author Tony Washer
 */
public class AccountInfoType extends StaticData<AccountInfoType, AccountInfoClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountInfoType.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

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
     * @param sName Name of Account Info Type
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Account Info Type with
     * @param uId the id
     * @param isEnabled is the account info type enabled
     * @param uOrder the sort order
     * @param pName Name of Account Info Type
     * @param pDesc Description of Account Info Type
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final Integer uId,
                            final Boolean isEnabled,
                            final Integer uOrder,
                            final String pName,
                            final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the Account Info Type with
     * @param uId ID of Account Info Type
     * @param uControlId the control id of the new item
     * @param isEnabled is the account info type enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of Account Info Type
     * @param pDesc Encrypted Description of Account Info Type
     * @throws JDataException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final Integer uId,
                            final Integer uControlId,
                            final Boolean isEnabled,
                            final Integer uOrder,
                            final byte[] pName,
                            final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Represents a list of {@link AccountInfoType} objects.
     */
    public static class AccountInfoTypeList extends StaticList<AccountInfoType, AccountInfoClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(
                AccountInfoTypeList.class.getSimpleName(), DataList.FIELD_DEFS);

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
        protected AccountInfoTypeList getEmptyList() {
            return new AccountInfoTypeList(this);
        }

        @Override
        public AccountInfoTypeList cloneList(final DataSet<?> pDataSet) {
            return (AccountInfoTypeList) super.cloneList(pDataSet);
        }

        @Override
        public AccountInfoTypeList deriveList(final ListStyle pStyle) {
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
                throw new JDataException(ExceptionClass.DATA, myInfoType, "Duplicate Account Info Type");
            }

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myInfoType, "Duplicate AccountInfoTypeId");
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);

            /* Validate the ActType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, "Failed validation");
            }
        }

        /**
         * Add an Open AccountInfoType to the list.
         * @param uId the Id of the account info type
         * @param isEnabled is the account info type enabled
         * @param uOrder the sort order
         * @param pInfoType the Name of the account info type
         * @param pDesc the Description of the account info type
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final String pInfoType,
                                final String pDesc) throws JDataException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, uId, isEnabled, uOrder, pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                throw new JDataException(ExceptionClass.DATA, myInfoType, "Duplicate AccountTypeId");
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);

            /* Validate the AccountInfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, "Failed validation");
            }
        }

        /**
         * Add a Secure AccountInfoType to the list.
         * @param uId the Id of the account info type
         * @param uControlId the control id of the new item
         * @param isEnabled is the account info type enabled
         * @param uOrder the sort order
         * @param pInfoType the encrypted Name of the account info type
         * @param pDesc the Encrypted Description of the account info type
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Boolean isEnabled,
                                  final Integer uOrder,
                                  final byte[] pInfoType,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Account Info Type */
            AccountInfoType myInfoType = new AccountInfoType(this, uId, uControlId, isEnabled, uOrder,
                    pInfoType, pDesc);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myInfoType, "Duplicate AccountInfoTypeId");
            }

            /* Add the Info Type to the list */
            append(myInfoType);

            /* Validate the InfoType */
            myInfoType.validate();

            /* Handle validation failure */
            if (myInfoType.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myInfoType, "Failed validation");
            }
        }
    }
}