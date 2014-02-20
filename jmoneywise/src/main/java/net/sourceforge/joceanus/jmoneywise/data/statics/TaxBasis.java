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
 * TaxBasis data type.
 * @author Tony Washer
 */
public class TaxBasis
        extends StaticData<TaxBasis, TaxBasisClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TAXBASIS.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TAXBASIS.getListName();

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
     * @throws JOceanusException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Tax Basis with
     * @param pClass Class of Tax Basis
     * @throws JOceanusException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final TaxBasisClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        super(pList, pValues);
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
            extends StaticList<TaxBasis, TaxBasisClass, MoneyWiseDataType> {
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
            return TaxBasis.FIELD_DEFS;
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
            super(TaxBasis.class, pData, MoneyWiseDataType.TAXBASIS, ListStyle.CORE);
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
        public TaxBasisList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (TaxBasisList) super.cloneList(pDataSet);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxBasis addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a TaxBasis */
            if (!(pItem instanceof TaxBasis)) {
                throw new UnsupportedOperationException();
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
         * Add a TaxBasis.
         * @param pTaxBasis the Name of the tax basis
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pTaxBasis) throws JOceanusException {
            /* Create a new Tax Basis */
            TaxBasis myBasis = new TaxBasis(this, pTaxBasis);

            /* Check that this TaxBasis has not been previously added */
            if (findItemByName(pTaxBasis) != null) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Check that this TaxBasisId has not been previously added */
            if (!isIdUnique(myBasis.getId())) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add the Tax Basis to the list */
            append(myBasis);

            /* Validate the Basis */
            myBasis.validate();

            /* Handle validation failure */
            if (myBasis.hasErrors()) {
                throw new JMoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }
        }

        @Override
        public TaxBasis addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* Create the basis */
            TaxBasis myBasis = new TaxBasis(this, pValues);

            /* Check that this BasisId has not been previously added */
            if (!isIdUnique(myBasis.getId())) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myBasis);

            /* Return it */
            return myBasis;
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
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
                    throw new JMoneyWiseDataException(myBasis, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
