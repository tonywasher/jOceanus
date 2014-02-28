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

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * DepositCategoryType data type.
 * @author Tony Washer
 */
public class DepositCategoryType
        extends StaticData<DepositCategoryType, DepositCategoryClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.DEPOSITTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.DEPOSITTYPE.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Deposit class of the DepositCategoryType.
     * @return the class
     */
    public DepositCategoryClass getDepositClass() {
        return super.getStaticClass();
    }

    @Override
    public DepositCategoryType getBase() {
        return (DepositCategoryType) super.getBase();
    }

    @Override
    public DepositCategoryTypeList getList() {
        return (DepositCategoryTypeList) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Deposit Category Type with
     * @param pDepCatType The Deposit Category Type to copy
     */
    protected DepositCategoryType(final DepositCategoryTypeList pList,
                                  final DepositCategoryType pDepCatType) {
        super(pList, pDepCatType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Deposit Category Type with
     * @param pName Name of Deposit Category Type
     * @throws JOceanusException on error
     */
    private DepositCategoryType(final DepositCategoryTypeList pList,
                                final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Deposit Category Type with
     * @param pClass Class of Deposit Category Type
     * @throws JOceanusException on error
     */
    private DepositCategoryType(final DepositCategoryTypeList pList,
                                final DepositCategoryClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private DepositCategoryType(final DepositCategoryTypeList pList,
                                final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
    }

    /**
     * Represents a list of {@link DepositCategoryType} objects.
     */
    public static class DepositCategoryTypeList
            extends StaticList<DepositCategoryType, DepositCategoryClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

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
            return DepositCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<DepositCategoryClass> getEnumClass() {
            return DepositCategoryClass.class;
        }

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public DepositCategoryTypeList(final DataSet<?, ?> pData) {
            super(DepositCategoryType.class, pData, MoneyWiseDataType.DEPOSITTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private DepositCategoryTypeList(final DepositCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        protected DepositCategoryTypeList getEmptyList(final ListStyle pStyle) {
            DepositCategoryTypeList myList = new DepositCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public DepositCategoryType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a DepositCategoryType */
            if (!(pItem instanceof DepositCategoryType)) {
                throw new UnsupportedOperationException();
            }

            DepositCategoryType myType = new DepositCategoryType(this, (DepositCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public DepositCategoryType addNewItem() {
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
         * Add a DepositCategoryType to the list.
         * @param pDepCatType the Name of the account category type
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pDepCatType) throws JOceanusException {
            /* Create a new Deposit Category Type */
            DepositCategoryType myDepType = new DepositCategoryType(this, pDepCatType);

            /* Check that this DepositCategoryType has not been previously added */
            if (findItemByName(pDepCatType) != null) {
                myDepType.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myDepType, ERROR_VALIDATION);
            }

            /* Check that this DepositCategoryTypeId has not been previously added */
            if (!isIdUnique(myDepType.getId())) {
                myDepType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myDepType, ERROR_VALIDATION);
            }

            /* Add the Deposit Category to the list */
            append(myDepType);

            /* Validate the DepType */
            myDepType.validate();

            /* Handle validation failure */
            if (myDepType.hasErrors()) {
                throw new JMoneyWiseDataException(myDepType, ERROR_VALIDATION);
            }
        }

        @Override
        public DepositCategoryType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the type */
            DepositCategoryType myType = new DepositCategoryType(this, pValues);

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

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (DepositCategoryClass myClass : DepositCategoryClass.values()) {
                /* Create new element */
                DepositCategoryType myActType = new DepositCategoryType(this, myClass);

                /* Add the DepositCategoryType to the list */
                append(myActType);

                /* Validate the DepositCategoryType */
                myActType.validate();

                /* Handle validation failure */
                if (myActType.hasErrors()) {
                    throw new JMoneyWiseDataException(myActType, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
