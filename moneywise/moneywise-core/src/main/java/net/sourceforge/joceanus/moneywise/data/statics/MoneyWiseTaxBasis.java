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
import net.sourceforge.joceanus.metis.data.MetisDataResource;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;

/**
 * TaxBasis data type.
 *
 * @author Tony Washer
 */
public class MoneyWiseTaxBasis
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.TAXBASIS.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.TAXBASIS.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTaxBasis> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxBasis.class);

    /**
     * Copy Constructor.
     *
     * @param pList     The list to associate the Tax Basis with
     * @param pTaxBasis The Tax Basis to copy
     */
    protected MoneyWiseTaxBasis(final MoneyWiseTaxBasisList pList,
                                final MoneyWiseTaxBasis pTaxBasis) {
        super(pList, pTaxBasis);
    }

    /**
     * Basic Constructor.
     *
     * @param pList The list to associate the Tax Basis with
     * @param pName Name of Tax Basis
     * @throws OceanusException on error
     */
    private MoneyWiseTaxBasis(final MoneyWiseTaxBasisList pList,
                              final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     *
     * @param pList  The list to associate the Tax Basis with
     * @param pClass Class of Tax Basis
     * @throws OceanusException on error
     */
    private MoneyWiseTaxBasis(final MoneyWiseTaxBasisList pList,
                              final MoneyWiseTaxClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     *
     * @param pList   The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseTaxBasis(final MoneyWiseTaxBasisList pList,
                              final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Tax class of the Tax Basis.
     *
     * @return the class
     */
    public MoneyWiseTaxClass getTaxClass() {
        return (MoneyWiseTaxClass) super.getStaticClass();
    }

    @Override
    public MoneyWiseTaxBasis getBase() {
        return (MoneyWiseTaxBasis) super.getBase();
    }

    @Override
    public MoneyWiseTaxBasisList getList() {
        return (MoneyWiseTaxBasisList) super.getList();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Determine whether we this is the tax paid category.
     *
     * @return <code>true</code> if we should add tax credits to the total, <code>false</code>
     * otherwise.
     */
    public boolean isTaxPaid() {
        return MoneyWiseTaxClass.TAXPAID.equals(getTaxClass());
    }

    /**
     * Represents a list of {@link MoneyWiseTaxBasis} objects.
     */
    public static class MoneyWiseTaxBasisList
            extends PrometheusStaticList<MoneyWiseTaxBasis> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTaxBasisList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTaxBasisList.class);

        /**
         * Construct an empty CORE tax bucket list.
         *
         * @param pData the DataSet for the list
         */
        public MoneyWiseTaxBasisList(final PrometheusDataSet pData) {
            super(MoneyWiseTaxBasis.class, pData, MoneyWiseStaticDataType.TAXBASIS, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         *
         * @param pSource the source List
         */
        private MoneyWiseTaxBasisList(final MoneyWiseTaxBasisList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTaxBasisList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTaxBasis.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseTaxClass> getEnumClass() {
            return MoneyWiseTaxClass.class;
        }

        @Override
        protected MoneyWiseTaxBasisList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTaxBasisList myList = new MoneyWiseTaxBasisList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the list.
         *
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public MoneyWiseTaxBasis addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a TaxBasis */
            if (!(pItem instanceof MoneyWiseTaxBasis)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseTaxBasis myBasis = new MoneyWiseTaxBasis(this, (MoneyWiseTaxBasis) pItem);
            add(myBasis);
            return myBasis;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         *
         * @return the newly added item
         */
        @Override
        public MoneyWiseTaxBasis addNewItem() {
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
         * Add a TaxBasis.
         *
         * @param pTaxBasis the Name of the tax basis
         * @return the new basis
         * @throws OceanusException on error
         */
        public MoneyWiseTaxBasis addBasicItem(final String pTaxBasis) throws OceanusException {
            /* Create a new Tax Basis */
            final MoneyWiseTaxBasis myBasis = new MoneyWiseTaxBasis(this, pTaxBasis);

            /* Check that this TaxBasisId has not been previously added */
            if (!isIdUnique(myBasis.getIndexedId())) {
                myBasis.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add the Tax Basis to the list */
            add(myBasis);
            return myBasis;
        }

        @Override
        public MoneyWiseTaxBasis addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the basis */
            final MoneyWiseTaxBasis myBasis = new MoneyWiseTaxBasis(this, pValues);

            /* Check that this BasisId has not been previously added */
            if (!isIdUnique(myBasis.getIndexedId())) {
                myBasis.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myBasis);

            /* Return it */
            return myBasis;
        }

        @Override
        protected MoneyWiseTaxBasis newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the basis */
            final MoneyWiseTaxBasis myBasis = new MoneyWiseTaxBasis(this, (MoneyWiseTaxClass) pClass);

            /* Check that this BasisId has not been previously added */
            if (!isIdUnique(myBasis.getIndexedId())) {
                myBasis.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myBasis);

            /* Return it */
            return myBasis;
        }
    }
}
