/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * AccountCategoryType data type.
 * @author Tony Washer
 */
public class CashCategoryType
        extends StaticDataItem<CashCategoryType> {
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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticDataItem.FIELD_DEFS);

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
     * @throws OceanusException on error
     */
    private CashCategoryType(final CashCategoryTypeList pList,
                             final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pClass Class of Cash Category Type
     * @throws OceanusException on error
     */
    private CashCategoryType(final CashCategoryTypeList pList,
                             final CashCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private CashCategoryType(final CashCategoryTypeList pList,
                             final DataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Cash class of the CashCategoryType.
     * @return the class
     */
    public CashCategoryClass getCashClass() {
        return (CashCategoryClass) super.getStaticClass();
    }

    /**
     * Determine whether the CashCategoryType is the required class.
     * @param pClass the desired class
     * @return <code>true</code> if the cash category type is the required class, <code>false</code>
     * otherwise.
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
            extends StaticList<CashCategoryType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<CashCategoryTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(CashCategoryTypeList.class);

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public CashCategoryTypeList(final DataSet<?> pData) {
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
        public MetisFieldSet<CashCategoryTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return CashCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<CashCategoryClass> getEnumClass() {
            return CashCategoryClass.class;
        }

        @Override
        protected CashCategoryTypeList getEmptyList(final ListStyle pStyle) {
            final CashCategoryTypeList myList = new CashCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public CashCategoryType addCopyItem(final DataItem pItem) {
            /* Can only clone a CashCategoryType */
            if (!(pItem instanceof CashCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final CashCategoryType myType = new CashCategoryType(this, (CashCategoryType) pItem);
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
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pCatType) throws OceanusException {
            /* Create a new Cash Category Type */
            final CashCategoryType myCashType = new CashCategoryType(this, pCatType);

            /* Check that this CashCategoryTypeId has not been previously added */
            if (!isIdUnique(myCashType.getId())) {
                myCashType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myCashType, ERROR_VALIDATION);
            }

            /* Add the Cash Category to the list */
            add(myCashType);
        }

        @Override
        public CashCategoryType addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the type */
            final CashCategoryType myType = new CashCategoryType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myType);

            /* Return it */
            return myType;
        }

        @Override
        protected CashCategoryType newItem(final StaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final CashCategoryType myType = new CashCategoryType(this, (CashCategoryClass) pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myType);

            /* Return it */
            return myType;
        }
    }
}
