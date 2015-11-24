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

import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TaxRegime data type.
 * @author Tony Washer
 */
public class TaxRegime
        extends StaticData<TaxRegime, TaxRegimeClass, MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TAXREGIME.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TAXREGIME.getListName();

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /**
     * Copy Constructor.
     * @param pList The list to associate the TaxRegime with
     * @param pTaxRegime The TaxRegime to copy
     */
    protected TaxRegime(final TaxRegimeList pList,
                        final TaxRegime pTaxRegime) {
        super(pList, pTaxRegime);
    }

    /**
     * Basic Constructor.
     * @param pList The list to associate the TaxRegime with
     * @param pName Name of TaxRegime
     * @throws OceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final String pName) throws OceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Tax Regime with
     * @param pClass Class of Tax Regime
     * @throws OceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final TaxRegimeClass pClass) throws OceanusException {
        super(pList, pClass);
    }

    /**
     * Values constructor.
     * @param pList The list to associate the item with
     * @param pValues the values
     * @throws OceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        super(pList, pValues);
    }

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Return the TaxRegime class of the TaxRegime.
     * @return the class
     */
    public TaxRegimeClass getRegime() {
        return super.getStaticClass();
    }

    @Override
    public TaxRegime getBase() {
        return (TaxRegime) super.getBase();
    }

    @Override
    public TaxRegimeList getList() {
        return (TaxRegimeList) super.getList();
    }

    /**
     * Determine whether this tax regime supports a Low Salary Band.
     * @return <code>true/false</code>
     */
    public boolean hasLoSalaryBand() {
        return getStaticClass().hasLoSalaryBand();
    }

    /**
     * Determine whether this tax regime treats capital gains as standard income.
     * @return <code>true/false</code>
     */
    public boolean hasCapitalGainsAsIncome() {
        return getStaticClass().hasCapitalGainsAsIncome();
    }

    /**
     * Determine whether this tax regime supports an additional taxation band.
     * @return <code>true/false</code>
     */
    public boolean hasAdditionalTaxBand() {
        return getStaticClass().hasAdditionalTaxBand();
    }

    /**
     * Represents a list of {@link TaxRegime} objects.
     */
    public static class TaxRegimeList
            extends StaticList<TaxRegime, TaxRegimeClass, MoneyWiseDataType> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, StaticList.FIELD_DEFS);

        /**
         * Construct an empty CORE tax regime list.
         * @param pData the DataSet for the list
         */
        public TaxRegimeList(final DataSet<?, ?> pData) {
            super(TaxRegime.class, pData, MoneyWiseDataType.TAXREGIME, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxRegimeList(final TaxRegimeList pSource) {
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
            return TaxRegime.FIELD_DEFS;
        }

        @Override
        protected Class<TaxRegimeClass> getEnumClass() {
            return TaxRegimeClass.class;
        }

        @Override
        protected TaxRegimeList getEmptyList(final ListStyle pStyle) {
            TaxRegimeList myList = new TaxRegimeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxRegime addCopyItem(final DataItem<?> pItem) {
            /* Can only clone a TaxRegime */
            if (!(pItem instanceof TaxRegime)) {
                throw new UnsupportedOperationException();
            }

            TaxRegime myRegime = new TaxRegime(this, (TaxRegime) pItem);
            add(myRegime);
            return myRegime;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TaxRegime addNewItem() {
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
         * Add a TaxRegime.
         * @param pTaxRegime the Name of the tax regime
         * @throws OceanusException on error
         */
        public void addBasicItem(final String pTaxRegime) throws OceanusException {
            /* Create a new tax regime */
            TaxRegime myTaxRegime = new TaxRegime(this, pTaxRegime);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(myTaxRegime.getId())) {
                myTaxRegime.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myTaxRegime, ERROR_VALIDATION);
            }

            /* Add the TaxRegime to the list */
            append(myTaxRegime);
        }

        @Override
        public TaxRegime addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the regime */
            TaxRegime myRegime = new TaxRegime(this, pValues);

            /* Check that this RegimeId has not been previously added */
            if (!isIdUnique(myRegime.getId())) {
                myRegime.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myRegime, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myRegime);

            /* Return it */
            return myRegime;
        }

        @Override
        protected TaxRegime newItem(final TaxRegimeClass pClass) throws OceanusException {
            /* Create the type */
            TaxRegime myRegime = new TaxRegime(this, pClass);

            /* Check that this TypeId has not been previously added */
            if (!isIdUnique(myRegime.getId())) {
                myRegime.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myRegime, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myRegime);

            /* Return it */
            return myRegime;
        }
    }
}
