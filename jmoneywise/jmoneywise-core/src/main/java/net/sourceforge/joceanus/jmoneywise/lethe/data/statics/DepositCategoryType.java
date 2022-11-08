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
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * DepositCategoryType data type.
 * @author Tony Washer
 */
public class DepositCategoryType
        extends StaticDataItem<DepositCategoryType, DepositCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSITTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSITTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticDataItem.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Deposit Category Type with
     * @param pDepCatType The Deposit Category Type to copy
     */
    protected DepositCategoryType(final DepositCategoryTypeList pList,
                                  final DepositCategoryType pDepCatType) {
        super(pList, pDepCatType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Deposit Category Type with
     * @param pName Name of Deposit Category Type
     * @throws OceanusException on error
     */
    private DepositCategoryType(final DepositCategoryTypeList pList,
                                final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Deposit Category Type with
     * @param pClass Class of Deposit Category Type
     * @throws OceanusException on error
     */
    private DepositCategoryType(final DepositCategoryTypeList pList,
                                final DepositCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private DepositCategoryType(final DepositCategoryTypeList pList,
                                final DataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Deposit class of the DepositCategoryType.
     * @return the class
     */
    public DepositCategoryClass getDepositClass() {
        return super.getStaticClass();
    }

    /**
     * Determine whether the DepositCategoryType is the required class.
     * @param pClass the desired class
     * @return <code>true</code> if the deposit category type is the required class,
     * <code>false</code> otherwise.
     */
    public boolean isDepositCategory(final DepositCategoryClass pClass) {
        return getDepositClass().equals(pClass);
    }

    @Override
    public DepositCategoryType getBase() {
        return (DepositCategoryType) super.getBase();
    }

    @Override
    public DepositCategoryTypeList getList() {
        return (DepositCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link DepositCategoryType} objects.
     */
    public static class DepositCategoryTypeList
            extends StaticList<DepositCategoryType, DepositCategoryClass> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<DepositCategoryTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(DepositCategoryTypeList.class);

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public DepositCategoryTypeList(final DataSet<?> pData) {
            super(DepositCategoryType.class, pData, MoneyWiseDataType.DEPOSITTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DepositCategoryTypeList(final DepositCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<DepositCategoryTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return DepositCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<DepositCategoryClass> getEnumClass() {
            return DepositCategoryClass.class;
        }

        @Override
        protected DepositCategoryTypeList getEmptyList(final ListStyle pStyle) {
            final DepositCategoryTypeList myList = new DepositCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DepositCategoryType addCopyItem(final DataItem pItem) {
            /* Can only clone a DepositCategoryType */
            if (!(pItem instanceof DepositCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final DepositCategoryType myType = new DepositCategoryType(this, (DepositCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public DepositCategoryType addNewItem() {
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
         * Add a DepositCategoryType to the list.
         * @param pDepCatType the Name of the account category type
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pDepCatType) throws OceanusException {
            /* Create a new Deposit Category Type */
            final DepositCategoryType myDepType = new DepositCategoryType(this, pDepCatType);

            /* Check that this DepositCategoryTypeId has not been previously added */
            if (!isIdUnique(myDepType.getId())) {
                myDepType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myDepType, ERROR_VALIDATION);
            }

            /* Add the Deposit Category to the list */
            add(myDepType);
        }

        @Override
        public DepositCategoryType addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the type */
            final DepositCategoryType myType = new DepositCategoryType(this, pValues);

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
        protected DepositCategoryType newItem(final DepositCategoryClass pClass) throws OceanusException {
            /* Create the type */
            final DepositCategoryType myType = new DepositCategoryType(this, pClass);

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
