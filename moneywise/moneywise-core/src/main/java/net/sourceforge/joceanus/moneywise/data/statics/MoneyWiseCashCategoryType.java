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
 * AccountCategoryType data type.
 * @author Tony Washer
 */
public class MoneyWiseCashCategoryType
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.CASHTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.CASHTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseCashCategoryType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashCategoryType.class);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pCatType The Cash Category Type to copy
     */
    protected MoneyWiseCashCategoryType(final MoneyWiseCashCategoryTypeList pList,
                                        final MoneyWiseCashCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pName Name of Cash Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseCashCategoryType(final MoneyWiseCashCategoryTypeList pList,
                                      final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Cash Category Type with
     * @param pClass Class of Cash Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseCashCategoryType(final MoneyWiseCashCategoryTypeList pList,
                                      final MoneyWiseCashCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseCashCategoryType(final MoneyWiseCashCategoryTypeList pList,
                                      final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Cash class of the CashCategoryType.
     * @return the class
     */
    public MoneyWiseCashCategoryClass getCashClass() {
        return (MoneyWiseCashCategoryClass) super.getStaticClass();
    }

    /**
     * Determine whether the CashCategoryType is the required class.
     * @param pClass the desired class
     * @return <code>true</code> if the cash category type is the required class, <code>false</code>
     * otherwise.
     */
    public boolean isCashCategory(final MoneyWiseCashCategoryClass pClass) {
        return getCashClass().equals(pClass);
    }

    @Override
    public MoneyWiseCashCategoryType getBase() {
        return (MoneyWiseCashCategoryType) super.getBase();
    }

    @Override
    public MoneyWiseCashCategoryTypeList getList() {
        return (MoneyWiseCashCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseCashCategoryType} objects.
     */
    public static class MoneyWiseCashCategoryTypeList
            extends PrometheusStaticList<MoneyWiseCashCategoryType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseCashCategoryTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashCategoryTypeList.class);

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseCashCategoryTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseCashCategoryType.class, pData, MoneyWiseStaticDataType.CASHTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseCashCategoryTypeList(final MoneyWiseCashCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseCashCategoryTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseCashCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseCashCategoryClass> getEnumClass() {
            return MoneyWiseCashCategoryClass.class;
        }

        @Override
        protected MoneyWiseCashCategoryTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseCashCategoryTypeList myList = new MoneyWiseCashCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseCashCategoryType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a CashCategoryType */
            if (!(pItem instanceof MoneyWiseCashCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseCashCategoryType myType = new MoneyWiseCashCategoryType(this, (MoneyWiseCashCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseCashCategoryType addNewItem() {
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
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseCashCategoryType addBasicItem(final String pCatType) throws OceanusException {
            /* Create a new Cash Category Type */
            final MoneyWiseCashCategoryType myCashType = new MoneyWiseCashCategoryType(this, pCatType);

            /* Check that this CashCategoryTypeId has not been previously added */
            if (!isIdUnique(myCashType.getIndexedId())) {
                myCashType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myCashType, ERROR_VALIDATION);
            }

            /* Add the Cash Category to the list */
            add(myCashType);
            return myCashType;
        }

        @Override
        public MoneyWiseCashCategoryType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseCashCategoryType myType = new MoneyWiseCashCategoryType(this, pValues);

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
        protected MoneyWiseCashCategoryType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseCashCategoryType myType = new MoneyWiseCashCategoryType(this, (MoneyWiseCashCategoryClass) pClass);

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
