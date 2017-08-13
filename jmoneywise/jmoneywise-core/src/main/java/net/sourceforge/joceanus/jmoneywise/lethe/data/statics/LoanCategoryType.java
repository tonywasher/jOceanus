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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LoanCategoryType data type.
 * @author Tony Washer
 */
public class LoanCategoryType
        extends StaticData<LoanCategoryType, LoanCategoryClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.LOANTYPE.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.LOANTYPE.getListName();

    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the Loan Category Type with
     * @param pCatType The Loan Category Type to copy
     */
    protected LoanCategoryType(final LoanCategoryTypeList pList,
                               final LoanCategoryType pCatType) {
        super(pList, pCatType);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Loan Category Type with
     * @param pName Name of Loan Category Type
     * @throws OceanusException on error
     */
    private LoanCategoryType(final LoanCategoryTypeList pList,
                             final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Loan Category Type with
     * @param pClass Class of Loan Category Type
     * @throws OceanusException on error
     */
    private LoanCategoryType(final LoanCategoryTypeList pList,
                             final LoanCategoryClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private LoanCategoryType(final LoanCategoryTypeList pList,
                             final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Loan class of the LoanCategoryType.
     * @return the class
     */
    public LoanCategoryClass getLoanClass() {
        return super.getStaticClass();
    }

    /**
     * Determine whether the LoanCategoryType is the required class.
     * @param pClass the desired class
     * @return <code>true</code> if the loan category type is the required class, <code>false</code>
     * otherwise.
     */
    public boolean isLoanCategory(final LoanCategoryClass pClass) {
        return getLoanClass().equals(pClass);
    }

    @Override
    public LoanCategoryType getBase() {
        return (LoanCategoryType) super.getBase();
    }

    @Override
    public LoanCategoryTypeList getList() {
        return (LoanCategoryTypeList) super.getList();
    }

    /**
     * Represents a list of {@link LoanCategoryType} objects.
     */
    public static class LoanCategoryTypeList
            extends StaticList<LoanCategoryType, LoanCategoryClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE account category list.
         * @param pData the DataSet for the list
         */
        public LoanCategoryTypeList(final DataSet<?, ?> pData) {
            super(LoanCategoryType.class, pData, MoneyWiseDataType.LOANTYPE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private LoanCategoryTypeList(final LoanCategoryTypeList pSource) {
            super(pSource);
        }

        @Override
        public MetisFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return LoanCategoryType.FIELD_DEFS;
        }

        @Override
        protected Class<LoanCategoryClass> getEnumClass() {
            return LoanCategoryClass.class;
        }

        @Override
        protected LoanCategoryTypeList getEmptyList(final ListStyle pStyle) {
            final LoanCategoryTypeList myList = new LoanCategoryTypeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public LoanCategoryType addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a LoanCategoryType */
            if (!(pItem instanceof LoanCategoryType)) {
                throw new UnsupportedOperationException();
            }

            final LoanCategoryType myType = new LoanCategoryType(this, (LoanCategoryType) pItem);
            add(myType);
            return myType;
        }

        @Override
        public LoanCategoryType addNewItem() {
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
         * Add a LoanCategoryType to the list.
         * @param pLoanCatType the Name of the loan category type
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pLoanCatType) throws OceanusException {
            /* Create a new Loan Category Type */
            final LoanCategoryType myLoanType = new LoanCategoryType(this, pLoanCatType);

            /* Check that this LoanCategoryTypeId has not been previously added */
            if (!isIdUnique(myLoanType.getId())) {
                myLoanType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myLoanType, ERROR_VALIDATION);
            }

            /* Add the Loan Category to the list */
            append(myLoanType);
        }

        @Override
        public LoanCategoryType addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the type */
            final LoanCategoryType myType = new LoanCategoryType(this, pValues);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }

        @Override
        protected LoanCategoryType newItem(final LoanCategoryClass pClass) throws OceanusException {
            /* Create the type */
            final LoanCategoryType myType = new LoanCategoryType(this, pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myType.getId())) {
                myType.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myType, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myType);

            /* Return it */
            return myType;
        }
    }
}
