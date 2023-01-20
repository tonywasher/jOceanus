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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
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
 * TransactionInfoType data type.
 * @author Tony Washer
 */
public class TransactionInfoType
        extends StaticDataItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TRANSINFOTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TRANSINFOTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticDataItem.FIELD_DEFS);

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
    protected TransactionInfoType(final TransactionInfoTypeList pList,
                                  final TransactionInfoType pType) {
        super(pList, pType);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the EventInfoType with
     * @param pName Name of InfoType
     * @throws OceanusException on error
     */
    private TransactionInfoType(final TransactionInfoTypeList pList,
                                final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Info Type with
     * @param pClass Class of Info Type
     * @throws OceanusException on error
     */
    private TransactionInfoType(final TransactionInfoTypeList pList,
                                final TransactionInfoClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private TransactionInfoType(final TransactionInfoTypeList pList,
                                final DataValues pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Info class of the InfoType.
     * @return the class
     */
    public TransactionInfoClass getInfoClass() {
        return (TransactionInfoClass) super.getStaticClass();
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
    public TransactionInfoType getBase() {
        return (TransactionInfoType) super.getBase();
    }

    @Override
    public TransactionInfoTypeList getList() {
        return (TransactionInfoTypeList) super.getList();
    }

    /**
     * Represents a list of {@link TransactionInfoType} objects.
     */
    public static class TransactionInfoTypeList
            extends StaticList<TransactionInfoType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<TransactionInfoTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(TransactionInfoTypeList.class);

        /**
         * Construct an empty CORE Info list.
         * @param pData the DataSet for the list
         */
        public TransactionInfoTypeList(final DataSet<?> pData) {
            super(TransactionInfoType.class, pData, MoneyWiseDataType.TRANSINFOTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TransactionInfoTypeList(final TransactionInfoTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<TransactionInfoTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return TransactionInfoType.FIELD_DEFS;
        }

        @Override
        protected Class<TransactionInfoClass> getEnumClass() {
            return TransactionInfoClass.class;
        }

        @Override
        protected TransactionInfoTypeList getEmptyList(final ListStyle pStyle) {
            final TransactionInfoTypeList myList = new TransactionInfoTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TransactionInfoType addCopyItem(final DataItem pItem) {
            /* Can only clone a TransactioonInfoType */
            if (!(pItem instanceof TransactionInfoType)) {
                throw new UnsupportedOperationException();
            }

            final TransactionInfoType myType = new TransactionInfoType(this, (TransactionInfoType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public TransactionInfoType addNewItem() {
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
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pType) throws OceanusException {
            /* Create a new InfoType */
            final TransactionInfoType myType = new TransactionInfoType(this, pType);

            /* Check that this InfoId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add the Type to the list */
            add(myType);
        }

        @Override
        public TransactionInfoType addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the type */
            final TransactionInfoType myType = new TransactionInfoType(this, pValues);

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
        protected TransactionInfoType newItem(final StaticDataClass pClass) throws OceanusException {
            /* Create the type */
            final TransactionInfoType myType = new TransactionInfoType(this, (TransactionInfoClass) pClass);

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
