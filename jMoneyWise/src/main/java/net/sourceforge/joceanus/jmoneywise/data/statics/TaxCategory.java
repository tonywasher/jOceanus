/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;

/**
 * TaxCategory data type.
 * @author Tony Washer
 */
public class TaxCategory
        extends StaticData<TaxCategory, TaxCategoryClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxCategory.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "TaxCategories";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Tax class of the Tax Category.
     * @return the class
     */
    public TaxCategoryClass getTaxClass() {
        return super.getStaticClass();
    }

    @Override
    public TaxCategory getBase() {
        return (TaxCategory) super.getBase();
    }

    @Override
    public TaxCategoryList getList() {
        return (TaxCategoryList) super.getList();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Tax Category with
     * @param pTaxCategory The Tax Category to copy
     */
    protected TaxCategory(final TaxCategoryList pList,
                          final TaxCategory pTaxCategory) {
        super(pList, pTaxCategory);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Tax Category with
     * @param pName Name of Tax Category
     * @throws JDataException on error
     */
    private TaxCategory(final TaxCategoryList pList,
                        final String pName) throws JDataException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Tax Category with
     * @param pClass Class of Tax Category
     * @throws JDataException on error
     */
    private TaxCategory(final TaxCategoryList pList,
                        final TaxCategoryClass pClass) throws JDataException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Tax Category with
     * @param pId ID of TaxCategory
     * @param isEnabled is the TaxCategory enabled
     * @param pOrder the sort order
     * @param pName Name of Tax Category
     * @param pDesc Description of Tax Category
     * @throws JDataException on error
     */
    private TaxCategory(final TaxCategoryList pList,
                        final Integer pId,
                        final Boolean isEnabled,
                        final Integer pOrder,
                        final String pName,
                        final String pDesc) throws JDataException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the TaxCategory with
     * @param pId ID of TaxCategory
     * @param pControlId the control id of the new item
     * @param isEnabled is the TaxCategory enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of TaxCategory
     * @param pDesc Encrypted Description of TaxCategory
     * @throws JDataException on error
     */
    private TaxCategory(final TaxCategoryList pList,
                        final Integer pId,
                        final Integer pControlId,
                        final Boolean isEnabled,
                        final Integer pOrder,
                        final byte[] pName,
                        final byte[] pDesc) throws JDataException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Determine whether we should add tax credits to the total.
     * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
     */
    public boolean hasTaxCredits() {
        switch (getTaxClass()) {
            case GrossSalary:
            case GrossInterest:
            case GrossDividend:
            case GrossUTDividend:
            case GrossTaxableGains:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether we this is the tax paid category.
     * @return <code>true</code> if we should add tax credits to the total, <code>false</code> otherwise.
     */
    public boolean isTaxPaid() {
        switch (getTaxClass()) {
            case TaxPaid:
                return true;
            default:
                return false;
        }
    }

    /**
     * Represents a list of {@link TaxCategory} objects.
     */
    public static class TaxCategoryList
            extends StaticList<TaxCategory, TaxCategoryClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxCategoryList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TaxCategoryClass> getEnumClass() {
            return TaxCategoryClass.class;
        }

        /**
         * Construct an empty CORE tax bucket list.
         * @param pData the DataSet for the list
         */
        public TaxCategoryList(final DataSet<?> pData) {
            super(TaxCategory.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxCategoryList(final TaxCategoryList pSource) {
            super(pSource);
        }

        @Override
        protected TaxCategoryList getEmptyList(final ListStyle pStyle) {
            TaxCategoryList myList = new TaxCategoryList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxCategoryList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (TaxCategoryList) super.cloneList(pDataSet);
        }

        @Override
        public TaxCategoryList deriveList(final ListStyle pStyle) throws JDataException {
            return (TaxCategoryList) super.deriveList(pStyle);
        }

        @Override
        public TaxCategoryList deriveDifferences(final DataList<TaxCategory> pOld) {
            return (TaxCategoryList) super.deriveDifferences(pOld);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxCategory addCopyItem(final DataItem pItem) {
            /* Can only clone a TaxCategory */
            if (!(pItem instanceof TaxCategory)) {
                return null;
            }

            TaxCategory myCategory = new TaxCategory(this, (TaxCategory) pItem);
            add(myCategory);
            return myCategory;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TaxCategory addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item.
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a TaxCategory.
         * @param pTaxCategory the Name of the tax bucket
         * @throws JDataException on error
         */
        public void addBasicItem(final String pTaxCategory) throws JDataException {
            /* Create a new Tax Category */
            TaxCategory myCategory = new TaxCategory(this, pTaxCategory);

            /* Check that this TaxCategory has not been previously added */
            if (findItemByName(pTaxCategory) != null) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JDataException(ExceptionClass.DATA, myCategory, ERROR_VALIDATION);
            }

            /* Check that this TaxCategoryId has not been previously added */
            if (!isIdUnique(myCategory.getId())) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCategory, ERROR_VALIDATION);
            }

            /* Add the Tax Category to the list */
            append(myCategory);

            /* Validate the Category */
            myCategory.validate();

            /* Handle validation failure */
            if (myCategory.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCategory, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxCategory to the list.
         * @param pId ID of TaxCategory
         * @param isEnabled is the TaxCategory enabled
         * @param pOrder the sort order
         * @param pTaxCategory the Name of the tax bucket
         * @param pDesc the Description of the tax bucket
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pTaxCategory,
                                final String pDesc) throws JDataException {
            /* Create a new Tax Category */
            TaxCategory myCategory = new TaxCategory(this, pId, isEnabled, pOrder, pTaxCategory, pDesc);

            /* Check that this TaxCategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCategory, ERROR_VALIDATION);
            }

            /* Add the Tax Category to the list */
            append(myCategory);

            /* Validate the Category */
            myCategory.validate();

            /* Handle validation failure */
            if (myCategory.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCategory, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxCategory.
         * @param pId the Id of the tax bucket
         * @param pControlId the control id of the new item
         * @param isEnabled is the TaxCategory enabled
         * @param pOrder the sort order
         * @param pTaxCategory the Encrypted Name of the tax bucket
         * @param pDesc the Encrypted Description of the tax bucket
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pTaxCategory,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Tax Category */
            TaxCategory myCategory = new TaxCategory(this, pId, pControlId, isEnabled, pOrder, pTaxCategory, pDesc);

            /* Check that this TaxCategoryId has not been previously added */
            if (!isIdUnique(pId)) {
                myCategory.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myCategory, ERROR_VALIDATION);
            }

            /* Add the Tax Category to the list */
            append(myCategory);

            /* Validate the TaxCategory */
            myCategory.validate();

            /* Handle validation failure */
            if (myCategory.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myCategory, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JDataException on error
         */
        public void populateDefaults() throws JDataException {
            /* Loop through all elements */
            for (TaxCategoryClass myClass : TaxCategoryClass.values()) {
                /* Create new element */
                TaxCategory myCategory = new TaxCategory(this, myClass);

                /* Add the TaxCategory to the list */
                append(myCategory);

                /* Validate the TaxCategory */
                myCategory.validate();

                /* Handle validation failure */
                if (myCategory.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myCategory, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
