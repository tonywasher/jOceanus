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
package net.sourceforge.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataResource;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataValues;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataItem;

/**
 * LoanCategoryType data type.
 *
 * @author Tony Washer
 */
public class MoneyWiseLoanCategoryType
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.LOANTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.LOANTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseLoanCategoryType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanCategoryType.class);

    /**
     * Copy Constructor.
     *
     * @param pList    The list to associate the Loan Category Type with
     * @param pCatType The Loan Category Type to copy
     */
    protected MoneyWiseLoanCategoryType(final MoneyWiseLoanCategoryTypeList pList,
                                        final MoneyWiseLoanCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic constructor.
     *
     * @param pList The list to associate the Loan Category Type with
     * @param pName Name of Loan Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseLoanCategoryType(final MoneyWiseLoanCategoryTypeList pList,
                                      final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     *
     * @param pList  The list to associate the Loan Category Type with
     * @param pClass Class of Loan Category Type
     * @throws OceanusException on error
     */
    private MoneyWiseLoanCategoryType(final MoneyWiseLoanCategoryTypeList pList,
                                      final MoneyWiseLoanCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     *
     * @param pList   The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseLoanCategoryType(final MoneyWiseLoanCategoryTypeList pList,
                                      final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Loan class of the LoanCategoryType.
     *
     * @return the class
     */
    public MoneyWiseLoanCategoryClass getLoanClass() {
        return (MoneyWiseLoanCategoryClass) super.getStaticClass();
    }

    /**
     * Determine whether the LoanCategoryType is the required class.
     *
     * @param pClass the desired class
     * @return <code>true</code> if the loan category type is the required class, <code>false</code>
     * otherwise.
     */
    public boolean isLoanCategory(final MoneyWiseLoanCategoryClass pClass) {
        return getLoanClass().equals(pClass);
    }

    @Override
    public MoneyWiseLoanCategoryType getBase() {
        return (MoneyWiseLoanCategoryType) super.getBase();
    }

    @Override
    public MoneyWiseLoanCategoryTypeList getList() {
        return (MoneyWiseLoanCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseLoanCategoryType} objects.
     */
    public static class MoneyWiseLoanCategoryTypeList
            extends PrometheusStaticList<MoneyWiseLoanCategoryType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseLoanCategoryTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseLoanCategoryTypeList.class);

        /**
         * Construct an empty CORE account category list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseLoanCategoryTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseLoanCategoryType.class, pData, MoneyWiseStaticDataType.LOANTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseLoanCategoryTypeList(final MoneyWiseLoanCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseLoanCategoryTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseLoanCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseLoanCategoryClass> getEnumClass() {
            return MoneyWiseLoanCategoryClass.class;
        }

        @Override
        protected MoneyWiseLoanCategoryTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseLoanCategoryTypeList myList = new MoneyWiseLoanCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseLoanCategoryType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a LoanCategoryType */
            if (!(pItem instanceof MoneyWiseLoanCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseLoanCategoryType myType = new MoneyWiseLoanCategoryType(this, (MoneyWiseLoanCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseLoanCategoryType addNewItem() {
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
         * Add a LoanCategoryType to the list.
         *
         * @param pLoanCatType the Name of the loan category type
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseLoanCategoryType addBasicItem(final String pLoanCatType) throws OceanusException {
            /* Create a new Loan Category Type */
            final MoneyWiseLoanCategoryType myLoanType = new MoneyWiseLoanCategoryType(this, pLoanCatType);

            /* Check that this LoanCategoryTypeId has not been previously added */
            if (!isIdUnique(myLoanType.getIndexedId())) {
                myLoanType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myLoanType, ERROR_VALIDATION);
            }

            /* Add the Loan Category to the list */
            add(myLoanType);
            return myLoanType;
        }

        @Override
        public MoneyWiseLoanCategoryType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseLoanCategoryType myType = new MoneyWiseLoanCategoryType(this, pValues);

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
        protected MoneyWiseLoanCategoryType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseLoanCategoryType myType = new MoneyWiseLoanCategoryType(this, (MoneyWiseLoanCategoryClass) pClass);

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
