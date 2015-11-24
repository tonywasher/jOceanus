/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.data.statics;

import net.sourceforge.joceanus.jmetis.data.DataType;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TaxYearInfoType data type.
 * @author Tony Washer
 */
public class TaxYearInfoType
        extends StaticData<TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TAXINFOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TAXINFOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the TaxYear Info Type with
     * @param pInfoType The TaxYear Info Type to copy
     */
    protected TaxYearInfoType(final TaxYearInfoTypeList pList,
                              final TaxYearInfoType pInfoType) {
        super(pList, pInfoType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the TaxYear Info Type with
     * @param pName Name of TaxYear Info Type
     * @throws OceanusException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the TaxYearInfo Type with
     * @param pClass Class of TaxYearInfo Type
     * @throws OceanusException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final TaxYearInfoClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private TaxYearInfoType(final TaxYearInfoTypeList pList,
                            final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the TaxYear Info class of the TaxYearInfoType.
     * @return the class
     */
    public TaxYearInfoClass getInfoClass() {
        return super.getStaticClass();
    }

    /**
     * Return the Data Type of the TaxYearInfoType.
     * @return the data type
     */
    public DataType getDataType() {
        return getInfoClass().getDataType();
    }

    @Override
    public TaxYearInfoType getBase() {
        return (TaxYearInfoType) super.getBase();
    }

    @Override
    public TaxYearInfoTypeList getList() {
        return (TaxYearInfoTypeList) super.getList();
    }

    /**
     * Represents a list of {@link TaxYearInfoType} objects.
     */
    public static class TaxYearInfoTypeList
            extends StaticList<TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE account type list.
         * @param pData the DataSet for the list
         */
        public TaxYearInfoTypeList(final DataSet<?, ?> pData) {
            super(TaxYearInfoType.class, pData, MoneyWiseDataType.TAXINFOTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxYearInfoTypeList(final TaxYearInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return TaxYearInfoType.FIELD_DEFS;
        }

        @Override
        protected Class<TaxYearInfoClass> getEnumClass() {
            return TaxYearInfoClass.class;
        }

        @Override
        protected TaxYearInfoTypeList getEmptyList(final ListStyle pStyle) {
            TaxYearInfoTypeList myList = new TaxYearInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxYearInfoType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone an TaxYearInfoType */
            if (!(pItem instanceof TaxYearInfoType)) {
                throw new UnsupportedOperationException();
            }

            TaxYearInfoType myType = new TaxYearInfoType(this, (TaxYearInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public TaxYearInfoType addNewItem() {
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
         * Add a TaxYearInfoType to the list.
         * @param pInfoType the Name of the TaxYear info type
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pInfoType) throws OceanusException {
            /* Create a new TaxYear Info Type */
            TaxYearInfoType myInfoType = new TaxYearInfoType(this, pInfoType);

            /* Check that this TaxYearTypeId has not been previously added */
            if (!isIdUnique(myInfoType.getId())) {
                myInfoType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myInfoType, ERROR_VALIDATION);
            }

            /* Add the TaxYear Info Type to the list */
            append(myInfoType);
        }

        @Override
        public TaxYearInfoType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the regime */
            TaxYearInfoType myType = new TaxYearInfoType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }

        @Override
        protected TaxYearInfoType newItem(final TaxYearInfoClass pClass) throws OceanusException {
            /* Create the type */
            TaxYearInfoType myType = new TaxYearInfoType(this, pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }
    }
}
