/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;

/**
 * DepositCategoryType data type.
 *
 * @author Tony Washer
 */
public class MoneyWiseDepositCategoryType
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.DEPOSITTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.DEPOSITTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseDepositCategoryType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositCategoryType.class);

    /**
     * Copy Constructor.
     *
     * @param pList       The list to associate the Deposit Category Type with
     * @param pDepCatType The Deposit Category Type to copy
     */
    protected MoneyWiseDepositCategoryType(final MoneyWiseDepositCategoryTypeList pList,
                                           final MoneyWiseDepositCategoryType pDepCatType) {
        super(pList, pDepCatType);
    }

    /**
     * Basic constructor.
     *
     * @param pList The list to associate the Deposit Category Type with
     * @param pName Name of Deposit Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseDepositCategoryType(final MoneyWiseDepositCategoryTypeList pList,
                                         final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     *
     * @param pList  The list to associate the Deposit Category Type with
     * @param pClass Class of Deposit Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseDepositCategoryType(final MoneyWiseDepositCategoryTypeList pList,
                                         final MoneyWiseDepositCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     *
     * @param pList   The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseDepositCategoryType(final MoneyWiseDepositCategoryTypeList pList,
                                         final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Deposit class of the DepositCategoryType.
     *
     * @return the class
     */
    public MoneyWiseDepositCategoryClass getDepositClass() {
        return (MoneyWiseDepositCategoryClass) super.getStaticClass();
    }

    /**
     * Determine whether the DepositCategoryType is the required class.
     *
     * @param pClass the desired class
     * @return <code>true</code> if the deposit category type is the required class,
     * <code>false</code> otherwise.
     */
    public boolean isDepositCategory(final MoneyWiseDepositCategoryClass pClass) {
        return getDepositClass().equals(pClass);
    }

    @Override
    public MoneyWiseDepositCategoryType getBase() {
        return (MoneyWiseDepositCategoryType) super.getBase();
    }

    @Override
    public MoneyWiseDepositCategoryTypeList getList() {
        return (MoneyWiseDepositCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseDepositCategoryType} objects.
     */
    public static class MoneyWiseDepositCategoryTypeList
            extends PrometheusStaticList<MoneyWiseDepositCategoryType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseDepositCategoryTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseDepositCategoryTypeList.class);

        /**
         * Construct an empty CORE account category list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseDepositCategoryTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseDepositCategoryType.class, pData, MoneyWiseStaticDataType.DEPOSITTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseDepositCategoryTypeList(final MoneyWiseDepositCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseDepositCategoryTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseDepositCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseDepositCategoryClass> getEnumClass() {
            return MoneyWiseDepositCategoryClass.class;
        }

        @Override
        protected MoneyWiseDepositCategoryTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseDepositCategoryTypeList myList = new MoneyWiseDepositCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseDepositCategoryType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a DepositCategoryType */
            if (!(pItem instanceof MoneyWiseDepositCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseDepositCategoryType myType = new MoneyWiseDepositCategoryType(this, (MoneyWiseDepositCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseDepositCategoryType addNewItem() {
            throw new UnsupportedOperationException();
        }

        /**
         * Obtain the type of the item.
         *
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a DepositCategoryType to the list.
         *
         * @param pDepCatType the Name of the account category type
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseDepositCategoryType addBasicItem(final String pDepCatType) throws OceanusException {
            /* Create a new Deposit Category Type */
            final MoneyWiseDepositCategoryType myDepType = new MoneyWiseDepositCategoryType(this, pDepCatType);

            /* Check that this DepositCategoryTypeId has not been previously added */
            if (!isIdUnique(myDepType.getIndexedId())) {
                myDepType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myDepType, ERROR_VALIDATION);
            }

            /* Add the Deposit Category to the list */
            add(myDepType);
            return myDepType;
        }

        @Override
        public MoneyWiseDepositCategoryType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseDepositCategoryType myType = new MoneyWiseDepositCategoryType(this, pValues);

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
        protected MoneyWiseDepositCategoryType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseDepositCategoryType myType = new MoneyWiseDepositCategoryType(this, (MoneyWiseDepositCategoryClass) pClass);

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
