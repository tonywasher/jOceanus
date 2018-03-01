/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * PayeeType data type.
 */
public class PayeeType
        extends StaticData<PayeeType, PayeeTypeClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.PAYEETYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.PAYEETYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the PayeeType with
     * @param pPayeeType The Payee Type to copy
     */
    protected PayeeType(final PayeeTypeList pList,
                        final PayeeType pPayeeType) {
        super(pList, pPayeeType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PayeeType with
     * @param pName Name of PayeeType
     * @throws OceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the PayeeType with
     * @param pClass Class of PayeeType
     * @throws OceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final PayeeTypeClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private PayeeType(final PayeeTypeList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the PayeeTypeClass of the PayeeType.
     * @return the class
     */
    public PayeeTypeClass getPayeeClass() {
        return super.getStaticClass();
    }

    @Override
    public PayeeType getBase() {
        return (PayeeType) super.getBase();
    }

    @Override
    public PayeeTypeList getList() {
        return (PayeeTypeList) super.getList();
    }

    /**
     * Represents a list of {@link PayeeType} objects.
     */
    public static class PayeeTypeList
            extends StaticList<PayeeType, PayeeTypeClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<PayeeTypeList> FIELD_DEFS = MetisFieldSet.newFieldSet(PayeeTypeList.class);

        /**
         * Construct an empty CORE payeeType list.
         * @param pData the DataSet for the list
         */
        public PayeeTypeList(final DataSet<?, ?> pData) {
            super(PayeeType.class, pData, MoneyWiseDataType.PAYEETYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private PayeeTypeList(final PayeeTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<PayeeTypeList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return PayeeType.FIELD_DEFS;
        }

        @Override
        protected Class<PayeeTypeClass> getEnumClass() {
            return PayeeTypeClass.class;
        }

        @Override
        protected PayeeTypeList getEmptyList(final ListStyle pStyle) {
            final PayeeTypeList myList = new PayeeTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public PayeeType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a PayeeType */
            if (!(pItem instanceof PayeeType)) {
                throw new UnsupportedOperationException();
            }

            final PayeeType myType = new PayeeType(this, (PayeeType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public PayeeType addNewItem() {
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
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pPayeeType) throws OceanusException {
            /* Create a new Payee Type */
            final PayeeType myPayeeType = new PayeeType(this, pPayeeType);

            /* Check that this PayeeTypeId has not been previously added */
            if (!isIdUnique(myPayeeType.getId())) {
                myPayeeType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myPayeeType, ERROR_VALIDATION);
            }

            /* Add the PayeeType to the list */
            add(myPayeeType);
        }

        @Override
        public PayeeType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the type */
            final PayeeType myType = new PayeeType(this, pValues);

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
        protected PayeeType newItem(final PayeeTypeClass pClass) throws OceanusException {
            /* Create the type */
            final PayeeType myType = new PayeeType(this, pClass);

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

        /**
         * Obtain payee type for new payee account.
         * @return the payee type
         */
        public PayeeType getDefaultPayeeType() {
            /* loop through the payee types */
            final Iterator<PayeeType> myIterator = iterator();
            while (myIterator.hasNext()) {
                final PayeeType myType = myIterator.next();

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
