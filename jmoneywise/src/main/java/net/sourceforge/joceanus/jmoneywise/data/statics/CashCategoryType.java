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
 * AccountCategoryType data type.
 * @author Tony Washer
 */
public class CashCategoryType
        extends StaticData<CashCategoryType, CashCategoryClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.CASHTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.CASHTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pCatType The Cash Category Type to copy
     */
    protected CashCategoryType(final CashCategoryTypeList pList,
                               final CashCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pName Name of Cash Category Type
     * @throws JOceanusException on error
     */
    private CashCategoryType(final CashCategoryTypeList pList,
                             final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pClass Class of Cash Category Type
     * @throws JOceanusException on error
     */
    private CashCategoryType(final CashCategoryTypeList pList,
                             final CashCategoryClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private CashCategoryType(final CashCategoryTypeList pList,
                             final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Cash class of the CashCategoryType.
     * @return the class
     */
    public CashCategoryClass getCashClass() {
        return super.getStaticClass();
    }

    /**
     * Determine whether the CashCategoryType is the required class.
     * @param pClass the desired class
     * @return <code>true</code> if the cash category type is the required class, <code>false</code> otherwise.
     */
    public boolean isCashCategory(final CashCategoryClass pClass) {
        return getCashClass().equals(pClass);
    }

    @Override
    public CashCategoryType getBase() {
        return (CashCategoryType) super.getBase();
    }

    @Override
    public CashCategoryTypeList getList() {
        return (CashCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link CashCategoryType} objects.
     */
    public static class CashCategoryTypeList
            extends StaticList<CashCategoryType, CashCategoryClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public CashCategoryTypeList(final DataSet<?, ?> pData) {
            super(CashCategoryType.class, pData, MoneyWiseDataType.CASHTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private CashCategoryTypeList(final CashCategoryTypeList pSource) {
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
            return CashCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<CashCategoryClass> getEnumClass() {
            return CashCategoryClass.class;
        }

        @Override
        protected CashCategoryTypeList getEmptyList(final ListStyle pStyle) {
            CashCategoryTypeList myList = new CashCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public CashCategoryType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a CashCategoryType */
            if (!(pItem instanceof CashCategoryType)) {
                throw new UnsupportedOperationException();
            }

            CashCategoryType myType = new CashCategoryType(this, (CashCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public CashCategoryType addNewItem() {
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
         * Add a CashCategoryType to the list.
         * @param pCatType the Name of the cash category type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pCatType) throws JOceanusException {
            /* Create a new Cash Category Type */
            CashCategoryType myCashType = new CashCategoryType(this, pCatType);

            /* Check that this CashCategoryTypeId has not been previously added */
            if (!isIdUnique(myCashType.getId())) {
                myCashType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myCashType, ERROR_VALIDATION);
            }

            /* Add the Cash Category to the list */
            append(myCashType);
        }

        @Override
        public CashCategoryType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the type */
            CashCategoryType myType = new CashCategoryType(this, pValues);

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
        protected CashCategoryType newItem(final CashCategoryClass pClass) throws JOceanusException {
            /* Create the type */
            CashCategoryType myType = new CashCategoryType(this, pClass);

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
