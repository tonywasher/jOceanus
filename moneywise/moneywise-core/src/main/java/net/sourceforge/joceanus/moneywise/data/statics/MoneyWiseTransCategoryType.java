/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.statics;

import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * TransactionCategoryType data type.
 * @author Tony Washer
 */
public class MoneyWiseTransCategoryType
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.TRANSTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.TRANSTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTransCategoryType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransCategoryType.class);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Category Type with
     * @param pCatType The Category Type to copy
     */
    protected MoneyWiseTransCategoryType(final MoneyWiseTransCategoryTypeList pList,
                                         final MoneyWiseTransCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Category Type with
     * @param pName Name of Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseTransCategoryType(final MoneyWiseTransCategoryTypeList pList,
                                       final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Category Type with
     * @param pClass Class of Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseTransCategoryType(final MoneyWiseTransCategoryTypeList pList,
                                       final MoneyWiseTransCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseTransCategoryType(final MoneyWiseTransCategoryTypeList pList,
                                       final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Category class of the Category Type.
     * @return the class
     */
    public MoneyWiseTransCategoryClass getCategoryClass() {
        return (MoneyWiseTransCategoryClass) super.getStaticClass();
    }

    @Override
    public boolean isActive() {
        return super.isActive() || getCategoryClass().isHiddenType();
    }

    @Override
    public MoneyWiseTransCategoryType getBase() {
        return (MoneyWiseTransCategoryType) super.getBase();
    }

    @Override
    public MoneyWiseTransCategoryTypeList getList() {
        return (MoneyWiseTransCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseTransCategoryType} objects.
     */
    public static class MoneyWiseTransCategoryTypeList
            extends PrometheusStaticList<MoneyWiseTransCategoryType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransCategoryTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransCategoryTypeList.class);

        /**
         * Construct an empty CORE category type list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseTransCategoryTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseTransCategoryType.class, pData, MoneyWiseStaticDataType.TRANSTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseTransCategoryTypeList(final MoneyWiseTransCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTransCategoryTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTransCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseTransCategoryClass> getEnumClass() {
            return MoneyWiseTransCategoryClass.class;
        }

        @Override
        protected MoneyWiseTransCategoryTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTransCategoryTypeList myList = new MoneyWiseTransCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public MoneyWiseTransCategoryType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a TransactionCategoryType */
            if (!(pItem instanceof MoneyWiseTransCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseTransCategoryType myType = new MoneyWiseTransCategoryType(this, (MoneyWiseTransCategoryType) pItem);
            add(myType);
            return myType;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public MoneyWiseTransCategoryType addNewItem() {
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
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseTransCategoryType addBasicItem(final String pCategoryType) throws OceanusException {
            /* Create a new Category Type */
            final MoneyWiseTransCategoryType myCatType = new MoneyWiseTransCategoryType(this, pCategoryType);

            /* Check that this CategoryTypeId has not been previously added */
            if (!isIdUnique(myCatType.getIndexedId())) {
                myCatType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCatType, ERROR_VALIDATION);
            }

            /* Add the Category Type to the list */
            add(myCatType);
            return myCatType;
        }

        @Override
        public MoneyWiseTransCategoryType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseTransCategoryType myType = new MoneyWiseTransCategoryType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getIndexedId())) {
                myType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myType);

            /* Return it */
            return myType;
        }

        @Override
        protected MoneyWiseTransCategoryType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseTransCategoryType myType = new MoneyWiseTransCategoryType(this, (MoneyWiseTransCategoryClass) pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getIndexedId())) {
                myType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myType);

            /* Return it */
            return myType;
        }
    }
}
