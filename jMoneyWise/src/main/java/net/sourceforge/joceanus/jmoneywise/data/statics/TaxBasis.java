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

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdatamodels.data.DataList;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.StaticData;

/**
 * TaxBasis data type.
 * @author Tony Washer
 */
public class TaxBasis
        extends StaticData<TaxBasis, TaxBasisClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxBasis.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = "TaxBases";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the Tax class of the Tax Basis.
     * @return the class
     */
    public TaxBasisClass getTaxClass() {
        return super.getStaticClass();
    }

    @Override
    public TaxBasis getBase() {
        return (TaxBasis) super.getBase();
    }

    @Override
    public TaxBasisList getList() {
        return (TaxBasisList) super.getList();
    }

    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Copy Constructor.
     * @param pList The list to associate the Tax Basis with
     * @param pTaxBasis The Tax Basis to copy
     */
    protected TaxBasis(final TaxBasisList pList,
                       final TaxBasis pTaxBasis) {
        super(pList, pTaxBasis);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the Tax Basis with
     * @param pName Name of Tax Basis
     * @throws JDataException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final String pName) throws JDataException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Tax Basis with
     * @param pClass Class of Tax Basis
     * @throws JDataException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final TaxBasisClass pClass) throws JDataException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the Tax Basis with
     * @param pId ID of TaxBasis
     * @param isEnabled is the TaxBasis enabled
     * @param pOrder the sort order
     * @param pName Name of Tax Basis
     * @param pDesc Description of Tax Basis
     * @throws JDataException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final Integer pId,
                     final Boolean isEnabled,
                     final Integer pOrder,
                     final String pName,
                     final String pDesc) throws JDataException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the TaxBasis with
     * @param pId ID of TaxBasis
     * @param pControlId the control id of the new item
     * @param isEnabled is the TaxBasis enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of TaxBasis
     * @param pDesc Encrypted Description of TaxBasis
     * @throws JDataException on error
     */
    private TaxBasis(final TaxBasisList pList,
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
            case GROSSSALARY:
            case GROSSINTEREST:
            case GROSSDIVIDEND:
            case GROSSUTDIVIDEND:
            case GROSSTAXABLEGAINS:
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
            case TAXPAID:
                return true;
            default:
                return false;
        }
    }

    /**
     * Represents a list of {@link TaxBasis} objects.
     */
    public static class TaxBasisList
            extends StaticList<TaxBasis, TaxBasisClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxBasisList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TaxBasisClass> getEnumClass() {
            return TaxBasisClass.class;
        }

        /**
         * Construct an empty CORE tax bucket list.
         * @param pData the DataSet for the list
         */
        public TaxBasisList(final DataSet<?, ?> pData) {
            super(TaxBasis.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxBasisList(final TaxBasisList pSource) {
            super(pSource);
        }

        @Override
        protected TaxBasisList getEmptyList(final ListStyle pStyle) {
            TaxBasisList myList = new TaxBasisList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxBasisList cloneList(final DataSet<?, ?> pDataSet) throws JDataException {
            return (TaxBasisList) super.cloneList(pDataSet);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxBasis addCopyItem(final DataItem pItem) {
            /* Can only clone a TaxBasis */
            if (!(pItem instanceof TaxBasis)) {
                return null;
            }

            TaxBasis myBasis = new TaxBasis(this, (TaxBasis) pItem);
            add(myBasis);
            return myBasis;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TaxBasis addNewItem() {
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
         * Add a TaxBasis.
         * @param pTaxBasis the Name of the tax basis
         * @throws JDataException on error
         */
        public void addBasicItem(final String pTaxBasis) throws JDataException {
            /* Create a new Tax Basis */
            TaxBasis myBasis = new TaxBasis(this, pTaxBasis);

            /* Check that this TaxBasis has not been previously added */
            if (findItemByName(pTaxBasis) != null) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JDataException(ExceptionClass.DATA, myBasis, ERROR_VALIDATION);
            }

            /* Check that this TaxBasisId has not been previously added */
            if (!isIdUnique(myBasis.getId())) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myBasis, ERROR_VALIDATION);
            }

            /* Add the Tax Basis to the list */
            append(myBasis);

            /* Validate the Basis */
            myBasis.validate();

            /* Handle validation failure */
            if (myBasis.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myBasis, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxBasis to the list.
         * @param pId ID of TaxBasis
         * @param isEnabled is the TaxBasis enabled
         * @param pOrder the sort order
         * @param pTaxBasis the Name of the tax basis
         * @param pDesc the Description of the tax basis
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pTaxBasis,
                                final String pDesc) throws JDataException {
            /* Create a new Tax Basis */
            TaxBasis myBasis = new TaxBasis(this, pId, isEnabled, pOrder, pTaxBasis, pDesc);

            /* Check that this TaxBasisId has not been previously added */
            if (!isIdUnique(pId)) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myBasis, ERROR_VALIDATION);
            }

            /* Add the Tax Basis to the list */
            append(myBasis);

            /* Validate the Basis */
            myBasis.validate();

            /* Handle validation failure */
            if (myBasis.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myBasis, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxBasis.
         * @param pId the Id of the tax basis
         * @param pControlId the control id of the new item
         * @param isEnabled is the TaxBasis enabled
         * @param pOrder the sort order
         * @param pTaxBasis the Encrypted Name of the tax basis
         * @param pDesc the Encrypted Description of the tax basis
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pTaxBasis,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new Tax Basis */
            TaxBasis myBasis = new TaxBasis(this, pId, pControlId, isEnabled, pOrder, pTaxBasis, pDesc);

            /* Check that this TaxBasisId has not been previously added */
            if (!isIdUnique(pId)) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myBasis, ERROR_VALIDATION);
            }

            /* Add the Tax Basis to the list */
            append(myBasis);

            /* Validate the TaxBasis */
            myBasis.validate();

            /* Handle validation failure */
            if (myBasis.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myBasis, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JDataException on error
         */
        public void populateDefaults() throws JDataException {
            /* Loop through all elements */
            for (TaxBasisClass myClass : TaxBasisClass.values()) {
                /* Create new element */
                TaxBasis myBasis = new TaxBasis(this, myClass);

                /* Add the TaxBasis to the list */
                append(myBasis);

                /* Validate the TaxBasis */
                myBasis.validate();

                /* Handle validation failure */
                if (myBasis.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myBasis, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
