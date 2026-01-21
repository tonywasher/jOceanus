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
import io.github.tonywasher.joceanus.metis.data.MetisDataType;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;

/**
 * AccountInfoType data type.
 *
 * @author Tony Washer
 */
public class MoneyWiseAccountInfoType
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.ACCOUNTINFOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.ACCOUNTINFOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAccountInfoType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAccountInfoType.class);

    /**
     * WebSite length.
     */
    public static final int WEBSITE_LEN = 50;

    /**
     * Data length.
     */
    public static final int DATA_LEN = 20;

    /**
     * Notes length.
     */
    public static final int NOTES_LEN = 500;

    /**
     * Copy Constructor.
     *
     * @param pList     The list to associate the Account Info Type with
     * @param pInfoType The Account Info Type to copy
     */
    protected MoneyWiseAccountInfoType(final MoneyWiseAccountInfoTypeList pList,
                                       final MoneyWiseAccountInfoType pInfoType) {
        super(pList, pInfoType);
    }

    /**
     * Basic Constructor.
     *
     * @param pList The list to associate the Account Info Type with
     * @param pName Name of Account Info Type
     * @throws OceanusException on error
     */
    private MoneyWiseAccountInfoType(final MoneyWiseAccountInfoTypeList pList,
                                     final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     *
     * @param pList  The list to associate the Account Info Type with
     * @param pClass Class of Account Info Type
     * @throws OceanusException on error
     */
    private MoneyWiseAccountInfoType(final MoneyWiseAccountInfoTypeList pList,
                                     final MoneyWiseAccountInfoClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     *
     * @param pList   The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseAccountInfoType(final MoneyWiseAccountInfoTypeList pList,
                                     final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Account Info class of the AccountInfoType.
     *
     * @return the class
     */
    public MoneyWiseAccountInfoClass getInfoClass() {
        return (MoneyWiseAccountInfoClass) super.getStaticClass();
    }

    /**
     * Return the Data Type of the AccountInfoType.
     *
     * @return the data type
     */
    public MetisDataType getDataType() {
        return getInfoClass().getDataType();
    }

    /**
     * is this a Link?
     *
     * @return true/false
     */
    public boolean isLink() {
        return getInfoClass().isLink();
    }

    @Override
    public MoneyWiseAccountInfoType getBase() {
        return (MoneyWiseAccountInfoType) super.getBase();
    }

    @Override
    public MoneyWiseAccountInfoTypeList getList() {
        return (MoneyWiseAccountInfoTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseAccountInfoType} objects.
     */
    public static class MoneyWiseAccountInfoTypeList
            extends PrometheusStaticList<MoneyWiseAccountInfoType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseAccountInfoTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAccountInfoTypeList.class);

        /**
         * Construct an empty CORE account type list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseAccountInfoTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseAccountInfoType.class, pData, MoneyWiseStaticDataType.ACCOUNTINFOTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseAccountInfoTypeList(final MoneyWiseAccountInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseAccountInfoTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseAccountInfoType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseAccountInfoClass> getEnumClass() {
            return MoneyWiseAccountInfoClass.class;
        }

        @Override
        protected MoneyWiseAccountInfoTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseAccountInfoTypeList myList = new MoneyWiseAccountInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseAccountInfoType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone an AccountInfoType */
            if (!(pItem instanceof MoneyWiseAccountInfoType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseAccountInfoType myType = new MoneyWiseAccountInfoType(this, (MoneyWiseAccountInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseAccountInfoType addNewItem() {
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
         * Add a Basic Open AccountInfoType to the list.
         *
         * @param pInfoType the Name of the account info type
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseAccountInfoType addBasicItem(final String pInfoType) throws OceanusException {
            /* Create a new Account Info Type */
            final MoneyWiseAccountInfoType myInfoType = new MoneyWiseAccountInfoType(this, pInfoType);

            /* Check that this InfoTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getIndexedId())) {
                myInfoType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }

            /* Add the Account Info Type to the list */
            add(myInfoType);
            return myInfoType;
        }

        @Override
        public MoneyWiseAccountInfoType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseAccountInfoType myType = new MoneyWiseAccountInfoType(this, pValues);

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
        protected MoneyWiseAccountInfoType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseAccountInfoType myType = new MoneyWiseAccountInfoType(this, (MoneyWiseAccountInfoClass) pClass);

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
