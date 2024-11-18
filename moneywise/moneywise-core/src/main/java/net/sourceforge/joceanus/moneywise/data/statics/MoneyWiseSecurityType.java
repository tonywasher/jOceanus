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
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusStaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SecurityType data type.
 */
public class MoneyWiseSecurityType
        extends PrometheusStaticDataItem
        implements MoneyWiseAssetCategory {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.SECURITYTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.SECURITYTYPE.getListName();

    /**
     * Symbol length.
     */
    public static final int SYMBOL_LEN = 30;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseSecurityType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityType.class);

    /**
     * Copy Constructor.
     * @param pList The list to associate the SecurityType with
     * @param pSecType The SecurityType to copy
     */
    protected MoneyWiseSecurityType(final MoneyWiseSecurityTypeList pList,
                                    final MoneyWiseSecurityType pSecType) {
        super(pList, pSecType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the SecurityType with
     * @param pName Name of SecurityType
     * @throws OceanusException on error
     */
    private MoneyWiseSecurityType(final MoneyWiseSecurityTypeList pList,
                                  final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the SecurityType with
     * @param pClass Class of SecurityType
     * @throws OceanusException on error
     */
    private MoneyWiseSecurityType(final MoneyWiseSecurityTypeList pList,
                                  final MoneyWiseSecurityClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWiseSecurityType(final MoneyWiseSecurityTypeList pList,
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
    public MoneyWiseSecurityClass getSecurityClass() {
        return (MoneyWiseSecurityClass) super.getStaticClass();
    }

    @Override
    public MoneyWiseSecurityType getBase() {
        return (MoneyWiseSecurityType) super.getBase();
    }

    @Override
    public MoneyWiseSecurityTypeList getList() {
        return (MoneyWiseSecurityTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWiseSecurityType} objects.
     */
    public static class MoneyWiseSecurityTypeList
            extends PrometheusStaticList<MoneyWiseSecurityType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWiseSecurityTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityTypeList.class);

        /**
         * Construct an empty CORE securityType list.
         * @param pData the DataSet for the list
         */
        public MoneyWiseSecurityTypeList(final PrometheusDataSet pData) {
            super(MoneyWiseSecurityType.class, pData, MoneyWiseStaticDataType.SECURITYTYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWiseSecurityTypeList(final MoneyWiseSecurityTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWiseSecurityTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWiseSecurityType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWiseSecurityClass> getEnumClass() {
            return MoneyWiseSecurityClass.class;
        }

        @Override
        protected MoneyWiseSecurityTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWiseSecurityTypeList myList = new MoneyWiseSecurityTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWiseSecurityType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a SecurityType */
            if (!(pItem instanceof MoneyWiseSecurityType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWiseSecurityType myType = new MoneyWiseSecurityType(this, (MoneyWiseSecurityType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWiseSecurityType addNewItem() {
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
         * Add a SecurityType to the list.
         * @param pSecType the Name of the security type
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWiseSecurityType addBasicItem(final String pSecType) throws OceanusException {
            /* Create a new Security Type */
            final MoneyWiseSecurityType mySecType = new MoneyWiseSecurityType(this, pSecType);

            /* Check that this SecurityTypeId has not been previously added */
            if (!isIdUnique(mySecType.getIndexedId())) {
                mySecType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(mySecType, ERROR_VALIDATION);
            }

            /* Add the SecurityType to the list */
            add(mySecType);
            return mySecType;
        }

        @Override
        public MoneyWiseSecurityType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWiseSecurityType myType = new MoneyWiseSecurityType(this, pValues);

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
        protected MoneyWiseSecurityType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWiseSecurityType myType = new MoneyWiseSecurityType(this, (MoneyWiseSecurityClass) pClass);

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
