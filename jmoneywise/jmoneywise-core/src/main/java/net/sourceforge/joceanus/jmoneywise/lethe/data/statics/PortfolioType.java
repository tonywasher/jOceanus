/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * PortfolioType data type.
 */
public class PortfolioType
        extends StaticDataItem<PortfolioType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PORTFOLIOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PORTFOLIOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticDataItem.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the PortfolioType with
     * @param pPortType The PortfolioType to copy
     */
    protected PortfolioType(final PortfolioTypeList pList,
                            final PortfolioType pPortType) {
        super(pList, pPortType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PortfolioType with
     * @param pName Name of PortfolioType
     * @throws OceanusException on error
     */
    private PortfolioType(final PortfolioTypeList pList,
                          final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PortfolioType with
     * @param pClass Class of PortfolioType
     * @throws OceanusException on error
     */
    private PortfolioType(final PortfolioTypeList pList,
                          final PortfolioTypeClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private PortfolioType(final PortfolioTypeList pList,
                          final DataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the SecurityTypeClass of the SecurityType.
     * @return the class
     */
    public PortfolioTypeClass getPortfolioClass() {
        return (PortfolioTypeClass) super.getStaticClass();
    }

    @Override
    public PortfolioType getBase() {
        return (PortfolioType) super.getBase();
    }

    @Override
    public PortfolioTypeList getList() {
        return (PortfolioTypeList) super.getList();
    }

    /**
     * Represents a list of {@link PortfolioType} objects.
     */
    public static class PortfolioTypeList
            extends StaticList<PortfolioType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PortfolioTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(PortfolioTypeList.class);

        /**
         * Construct an empty CORE portfolioType list.
         * @param pData the DataSet for the list
         */
        public PortfolioTypeList(final DataSet<?> pData) {
            super(PortfolioType.class, pData, MoneyWiseDataType.PORTFOLIOTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PortfolioTypeList(final PortfolioTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PortfolioTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return PortfolioType.FIELD_DEFS;
        }

        @Override
        protected Class<PortfolioTypeClass> getEnumClass() {
            return PortfolioTypeClass.class;
        }

        @Override
        protected PortfolioTypeList getEmptyList(final ListStyle pStyle) {
            final PortfolioTypeList myList = new PortfolioTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PortfolioType addCopyItem(final DataItem pItem) {
            /* Can only clone a PortfolioType */
            if (!(pItem instanceof PortfolioType)) {
                throw new UnsupportedOperationException();
            }

            final PortfolioType myType = new PortfolioType(this, (PortfolioType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public PortfolioType addNewItem() {
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
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pPortType) throws OceanusException {
            /* Create a new Portfolio Type */
            final PortfolioType myPortType = new PortfolioType(this, pPortType);

            /* Check that this PortTypeId has not been previously added */
            if (!isIdUnique(myPortType.getId())) {
                myPortType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myPortType, ERROR_VALIDATION);
            }

            /* Add the PortfolioType to the list */
            add(myPortType);
        }

        @Override
        public PortfolioType addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the type */
            final PortfolioType myType = new PortfolioType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myType);

            /* Return it */
            return myType;
        }

        @Override
        protected PortfolioType newItem(final StaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final PortfolioType myType = new PortfolioType(this, (PortfolioTypeClass) pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myType);

            /* Return it */
            return myType;
        }
    }
}
