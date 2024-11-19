/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.tethys.OceanusException;

/**
 * TransactionInfoType data type.
 * @author Tony Washer
 */
public class MoneyWiseTransInfoType
        extends PrometheusStaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.TRANSINFOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.TRANSINFOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTransInfoType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransInfoType.class);

    /**
     * Data length.
     */
    protected static final int DATA_LEN = 20;

    /**
     * Comment length.
     */
    protected static final int COMMENT_LEN = 50;

    /**
     * Copy Constructor.
     * @param pList The list to associate the InfoType with
     * @param pType The InfoType to copy
     */
    protected MoneyWiseTransInfoType(final MoneyWiseTransInfoTypeList pList,
                                     final MoneyWiseTransInfoType pType) {
        super(pList, pType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the EventInfoType with
     * @param pName Name of InfoType
     * @throws OceanusException on error
     */
    private MoneyWiseTransInfoType(final MoneyWiseTransInfoTypeList pList,
                                   final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Info Type with
     * @param pClass Class of Info Type
     * @throws OceanusException on error
     */
    private MoneyWiseTransInfoType(final MoneyWiseTransInfoTypeList pList,
                                   final MoneyWiseTransInfoClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseTransInfoType(final MoneyWiseTransInfoTypeList pList,
                                   final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the Info class of the InfoType.
     * @return the class
     */
    public MoneyWiseTransInfoClass getInfoClass() {
        return (MoneyWiseTransInfoClass) super.getStaticClass();
    }

    /**
     * Return the Data Type of the EventInfoType.
     * @return the data type
     */
    public MetisDataType getDataType() {
        return getInfoClass().getDataType();
    }

    /**
     * is this a Link?
     * @return true/false
     */
    public boolean isLink() {
        return getInfoClass().isLink();
    }

    @Override
    public MoneyWiseTransInfoType getBase() {
        return (MoneyWiseTransInfoType) super.getBase();
    }

    @Override
    public MoneyWiseTransInfoTypeList getList() {
        return (MoneyWiseTransInfoTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseTransInfoType} objects.
     */
    public static class MoneyWiseTransInfoTypeList
            extends PrometheusStaticList<MoneyWiseTransInfoType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseTransInfoTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransInfoTypeList.class);

        /**
         * Construct an empty CORE Info list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseTransInfoTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseTransInfoType.class, pData, MoneyWiseStaticDataType.TRANSINFOTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseTransInfoTypeList(final MoneyWiseTransInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseTransInfoTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseTransInfoType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseTransInfoClass> getEnumClass() {
            return MoneyWiseTransInfoClass.class;
        }

        @Override
        protected MoneyWiseTransInfoTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseTransInfoTypeList myList = new MoneyWiseTransInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseTransInfoType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a TransactionInfoType */
            if (!(pItem instanceof MoneyWiseTransInfoType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseTransInfoType myType = new MoneyWiseTransInfoType(this, (MoneyWiseTransInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseTransInfoType addNewItem() {
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
         * Add an InfoType.
         * @param pType the Name of the InfoType
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseTransInfoType addBasicItem(final String pType) throws OceanusException {
            /* Create a new InfoType */
            final MoneyWiseTransInfoType myType = new MoneyWiseTransInfoType(this, pType);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myType.getIndexedId())) {
                myType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add the Type to the list */
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseTransInfoType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseTransInfoType myType = new MoneyWiseTransInfoType(this, pValues);

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
        protected MoneyWiseTransInfoType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseTransInfoType myType = new MoneyWiseTransInfoType(this, (MoneyWiseTransInfoClass) pClass);

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
