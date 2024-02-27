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

import java.util.Iterator;

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
 * PayeeType data type.
 */
public class MoneyWisePayeeType
        extends PrometheusStaticDataItem
        implements MoneyWiseAssetCategory {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseStaticDataType.PAYEETYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseStaticDataType.PAYEETYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWisePayeeType> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeType.class);

    /**
     * Copy Constructor.
     * @param pList The list to associate the PayeeType with
     * @param pPayeeType The Payee Type to copy
     */
    protected MoneyWisePayeeType(final MoneyWisePayeeTypeList pList,
                                 final MoneyWisePayeeType pPayeeType) {
        super(pList, pPayeeType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PayeeType with
     * @param pName Name of PayeeType
     * @throws OceanusException on error
     */
    private MoneyWisePayeeType(final MoneyWisePayeeTypeList pList,
                               final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PayeeType with
     * @param pClass Class of PayeeType
     * @throws OceanusException on error
     */
    private MoneyWisePayeeType(final MoneyWisePayeeTypeList pList,
                               final MoneyWisePayeeClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private MoneyWisePayeeType(final MoneyWisePayeeTypeList pList,
                               final PrometheusDataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Return the PayeeTypeClass of the PayeeType.
     * @return the class
     */
    public MoneyWisePayeeClass getPayeeClass() {
        return (MoneyWisePayeeClass) super.getStaticClass();
    }

    @Override
    public MoneyWisePayeeType getBase() {
        return (MoneyWisePayeeType) super.getBase();
    }

    @Override
    public MoneyWisePayeeTypeList getList() {
        return (MoneyWisePayeeTypeList) super.getList();
    }

    /**
     * Represents a list of {@link MoneyWisePayeeType} objects.
     */
    public static class MoneyWisePayeeTypeList
            extends PrometheusStaticList<MoneyWisePayeeType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<MoneyWisePayeeTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePayeeTypeList.class);

        /**
         * Construct an empty CORE payeeType list.
         * @param pData the DataSet for the list
         */
        public MoneyWisePayeeTypeList(final PrometheusDataSet pData) {
            super(MoneyWisePayeeType.class, pData, MoneyWiseStaticDataType.PAYEETYPE, PrometheusListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private MoneyWisePayeeTypeList(final MoneyWisePayeeTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<MoneyWisePayeeTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFieldSetDef getItemFields() {
            return MoneyWisePayeeType.FIELD_DEFS;
        }

        @Override
        protected Class<MoneyWisePayeeClass> getEnumClass() {
            return MoneyWisePayeeClass.class;
        }

        @Override
        protected MoneyWisePayeeTypeList getEmptyList(final PrometheusListStyle pStyle) {
            final MoneyWisePayeeTypeList myList = new MoneyWisePayeeTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public MoneyWisePayeeType addCopyItem(final PrometheusDataItem pItem) {
            /* Can only clone a PayeeType */
            if (!(pItem instanceof MoneyWisePayeeType)) {
                throw new UnsupportedOperationException();
            }

            final MoneyWisePayeeType myType = new MoneyWisePayeeType(this, (MoneyWisePayeeType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public MoneyWisePayeeType addNewItem() {
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
         * Add a PayeeType to the list.
         * @param pPayeeType the Name of the account category type
         * @return the new type
         * @throws OceanusException on error
         */
        public MoneyWisePayeeType addBasicItem(final String pPayeeType) throws OceanusException {
            /* Create a new Payee Type */
            final MoneyWisePayeeType myPayeeType = new MoneyWisePayeeType(this, pPayeeType);

            /* Check that this PayeeTypeId has not been previously added */
            if (!isIdUnique(myPayeeType.getIndexedId())) {
                myPayeeType.addError(ERROR_DUPLICATE, MetisDataResource.DATA_ID);
                throw new MoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }

            /* Add the PayeeType to the list */
            add(myPayeeType);
            return myPayeeType;
        }

        @Override
        public MoneyWisePayeeType addValuesItem(final PrometheusDataValues pValues) throws OceanusException {
            /* Create the type */
            final MoneyWisePayeeType myType = new MoneyWisePayeeType(this, pValues);

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
        protected MoneyWisePayeeType newItem(final PrometheusStaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final MoneyWisePayeeType myType = new MoneyWisePayeeType(this, (MoneyWisePayeeClass) pClass);

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

        /**
         * Obtain payee type for new payee account.
         * @return the payee type
         */
        public MoneyWisePayeeType getDefaultPayeeType() {
            /* loop through the payee types */
            final Iterator<MoneyWisePayeeType> myIterator = iterator();
            while (myIterator.hasNext()) {
                final MoneyWisePayeeType myType = myIterator.next();

                /* Ignore deleted and singular types */
                if (!myType.isDeleted() && !myType.getPayeeClass().isSingular()) {
                    return myType;
                }
            }

            /* Return no category */
            return null;
        }
    }
}
