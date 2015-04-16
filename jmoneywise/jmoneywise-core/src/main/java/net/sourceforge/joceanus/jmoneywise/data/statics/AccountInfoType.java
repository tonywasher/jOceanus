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

import net.sourceforge.joceanus.jmetis.data.DataType;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
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
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private AccountInfoType(final AccountInfoTypeList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

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

    @Override
    public AccountInfoTypeList getList() {
        return (AccountInfoTypeList) super.getList();
    }

    /**
     * Represents a list of {@link AccountInfoType} objects.
     */
    public static class AccountInfoTypeList
            extends StaticList<AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

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

        @Override
        protected AccountInfoTypeList getEmptyList(final ListStyle pStyle) {
            AccountInfoTypeList myList = new AccountInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public AccountInfoType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an AccountInfoType */
            if (!(pItem instanceof AccountInfoType)) {
                throw new UnsupportedOperationException();
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

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }

            /* Add the Account Info Type to the list */
            append(myInfoType);
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

        @Override
        protected AccountInfoType newItem(final AccountInfoClass pClass) throws JOceanusException {
            /* Create the type */
            AccountInfoType myType = new AccountInfoType(this, pClass);

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
    }
}