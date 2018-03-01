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
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, StaticData.FIELD_DEFS);

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
     * @throws OceanusException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Tax Basis with
     * @param pClass Class of Tax Basis
     * @throws OceanusException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final TaxBasisClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private TaxBasis(final TaxBasisList pList,
                     final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public MetisFields declareFields() {
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
     * Determine whether we this is the tax paid category.
     * @return <code>true</code> if we should add tax credits to the total, <code>false</code>
     * otherwise.
     */
    public boolean isTaxPaid() {
        return TaxBasisClass.TAXPAID.equals(getTaxClass());
    }

    /**
     * Represents a list of {@link TaxBasis} objects.
     */
    public static class TaxBasisList
            extends StaticList<TaxBasis, TaxBasisClass, MoneyWiseDataType> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<TaxBasisList> FIELD_DEFS = MetisFieldSet.newFieldSet(TaxBasisList.class);

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
        public MetisFieldSet<TaxBasisList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return TaxBasis.FIELD_DEFS;
        }

        @Override
        protected Class<TaxBasisClass> getEnumClass() {
            return TaxBasisClass.class;
        }

        @Override
        protected TaxBasisList getEmptyList(final ListStyle pStyle) {
            final TaxBasisList myList = new TaxBasisList(this);
            myList.setStyle(pStyle);
            return myList;
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

            final TaxBasis myBasis = new TaxBasis(this, (TaxBasis) pItem);
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
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pTaxBasis) throws OceanusException {
            /* Create a new Tax Basis */
            final TaxBasis myBasis = new TaxBasis(this, pTaxBasis);

            /* Check that this TaxBasisId has not been previously added */
            if (!isIdUnique(myBasis.getId())) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add the Tax Basis to the list */
            add(myBasis);
        }

        @Override
        public TaxBasis addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the basis */
            final TaxBasis myBasis = new TaxBasis(this, pValues);

            /* Check that this BasisId has not been previously added */
            if (!isIdUnique(myBasis.getId())) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myBasis);

            /* Return it */
            return myBasis;
        }

        @Override
        protected TaxBasis newItem(final TaxBasisClass pClass) throws OceanusException {
            /* Create the basis */
            final TaxBasis myBasis = new TaxBasis(this, pClass);

            /* Check that this BasisId has not been previously added */
            if (!isIdUnique(myBasis.getId())) {
                myBasis.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(myBasis, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(myBasis);

            /* Return it */
            return myBasis;
        }
    }
}
