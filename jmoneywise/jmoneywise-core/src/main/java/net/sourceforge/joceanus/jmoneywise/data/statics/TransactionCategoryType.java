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

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TransactionCategoryType data type.
 * @author Tony Washer
 */
public class TransactionCategoryType
        extends StaticData<TransactionCategoryType, TransactionCategoryClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Category Type with
     * @param pCatType The Category Type to copy
     */
    protected TransactionCategoryType(final TransactionCategoryTypeList pList,
                                      final TransactionCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Category Type with
     * @param pName Name of Category Type
     * @throws JOceanusException on error
     */
    private TransactionCategoryType(final TransactionCategoryTypeList pList,
                                    final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Category Type with
     * @param pClass Class of Category Type
     * @throws JOceanusException on error
     */
    private TransactionCategoryType(final TransactionCategoryTypeList pList,
                                    final TransactionCategoryClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private TransactionCategoryType(final TransactionCategoryTypeList pList,
                                    final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Category class of the Category Type.
     * @return the class
     */
    public TransactionCategoryClass getCategoryClass() {
        return super.getStaticClass();
    }

    @Override
    public boolean isActive() {
        return super.isActive() || getCategoryClass().isHiddenType();
    }

    @Override
    public TransactionCategoryType getBase() {
        return (TransactionCategoryType) super.getBase();
    }

    @Override
    public TransactionCategoryTypeList getList() {
        return (TransactionCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link TransactionCategoryType} objects.
     */
    public static class TransactionCategoryTypeList
            extends StaticList<TransactionCategoryType, TransactionCategoryClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE category type list.
         * @param pData the DataSet for the list
         */
        public TransactionCategoryTypeList(final DataSet<?, ?> pData) {
            super(TransactionCategoryType.class, pData, MoneyWiseDataType.TRANSTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TransactionCategoryTypeList(final TransactionCategoryTypeList pSource) {
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
            return TransactionCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<TransactionCategoryClass> getEnumClass() {
            return TransactionCategoryClass.class;
        }

        @Override
        protected TransactionCategoryTypeList getEmptyList(final ListStyle pStyle) {
            TransactionCategoryTypeList myList = new TransactionCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TransactionCategoryType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a TransactionCategoryType */
            if (!(pItem instanceof TransactionCategoryType)) {
                throw new UnsupportedOperationException();
            }

            TransactionCategoryType myType = new TransactionCategoryType(this, (TransactionCategoryType) pItem);
            add(myType);
            return myType;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TransactionCategoryType addNewItem() {
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
         * Add a TransactionCategoryType.
         * @param pCategoryType the Name of the category type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pCategoryType) throws JOceanusException {
            /* Create a new Category Type */
            TransactionCategoryType myCatType = new TransactionCategoryType(this, pCategoryType);

            /* Check that this CategoryTypeId has not been previously added */
            if (!isIdUnique(myCatType.getId())) {
                myCatType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCatType, ERROR_VALIDATION);
            }

            /* Add the Category Type to the list */
            append(myCatType);
        }

        @Override
        public TransactionCategoryType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the type */
            TransactionCategoryType myType = new TransactionCategoryType(this, pValues);

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
        protected TransactionCategoryType newItem(final TransactionCategoryClass pClass) throws JOceanusException {
            /* Create the type */
            TransactionCategoryType myType = new TransactionCategoryType(this, pClass);

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