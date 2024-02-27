/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.data.MetisDataResource;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * PortfolioType data type.
 */
public class MoneyWisePortfolioType
        extends PrometheusStaticDataItem
        implements MoneyWiseAssetCategory {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.PORTFOLIOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.PORTFOLIOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWisePortfolioType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioType.class);

    /**
     * Copy Constructor.
     * @param pList The list to associate the PortfolioType with
     * @param pPortType The PortfolioType to copy
     */
    protected MoneyWisePortfolioType(final MoneyWisePortfolioTypeList pList,
                                     final MoneyWisePortfolioType pPortType) {
        super(pList, pPortType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PortfolioType with
     * @param pName Name of PortfolioType
     * @throws OceanusException on error
     */
    private MoneyWisePortfolioType(final MoneyWisePortfolioTypeList pList,
                                   final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PortfolioType with
     * @param pClass Class of PortfolioType
     * @throws OceanusException on error
     */
    private MoneyWisePortfolioType(final MoneyWisePortfolioTypeList pList,
                                   final MoneyWisePortfolioClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWisePortfolioType(final MoneyWisePortfolioTypeList pList,
                                   final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the SecurityTypeClass of the SecurityType.
     * @return the class
     */
    public MoneyWisePortfolioClass getPortfolioClass() {
        return (MoneyWisePortfolioClass) super.getStaticClass();
    }

    @Override
    public MoneyWisePortfolioType getBase() {
        return (MoneyWisePortfolioType) super.getBase();
    }

    @Override
    public MoneyWisePortfolioTypeList getList() {
        return (MoneyWisePortfolioTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWisePortfolioType} objects.
     */
    public static class MoneyWisePortfolioTypeList
            extends PrometheusStaticList<MoneyWisePortfolioType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePortfolioTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioTypeList.class);

        /**
         * Construct an empty CORE portfolioType list.
         * @param pData the DataSet for the list
         */
        public MoneyWisePortfolioTypeList(final PrometheusDataSet pData) {
            super(MoneyWisePortfolioType.class, pData, MoneyWiseStaticDataType.PORTFOLIOTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWisePortfolioTypeList(final MoneyWisePortfolioTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWisePortfolioTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWisePortfolioType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWisePortfolioClass> getEnumClass() {
            return MoneyWisePortfolioClass.class;
        }

        @Override
        protected MoneyWisePortfolioTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWisePortfolioTypeList myList = new MoneyWisePortfolioTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWisePortfolioType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a PortfolioType */
            if (!(pItem instanceof MoneyWisePortfolioType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWisePortfolioType myType = new MoneyWisePortfolioType(this, (MoneyWisePortfolioType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWisePortfolioType addNewItem() {
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
         * Add a PortfolioType to the list.
         * @param pPortType the Name of the portfolio type
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWisePortfolioType addBasicItem(final String pPortType) throws OceanusException {
            /* Create a new Portfolio Type */
            final MoneyWisePortfolioType myPortType = new MoneyWisePortfolioType(this, pPortType);

            /* Check that this PortTypeId has not been previously added */
            if (!isIdUnique(myPortType.getIndexedId())) {
                myPortType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myPortType, ERROR_VALIDATION);
            }

            /* Add the PortfolioType to the list */
            add(myPortType);
            return myPortType;
        }

        @Override
        public MoneyWisePortfolioType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWisePortfolioType myType = new MoneyWisePortfolioType(this, pValues);

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
        protected MoneyWisePortfolioType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWisePortfolioType myType = new MoneyWisePortfolioType(this, (MoneyWisePortfolioClass) pClass);

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
