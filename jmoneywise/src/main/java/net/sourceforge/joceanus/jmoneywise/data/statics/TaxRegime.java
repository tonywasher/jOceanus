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
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
     * @throws JOceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final String pName) throws JOceanusException {
        super(pList, pName);
    }

    /**
     * Basic constructor.
     * @param pList The list to associate the Tax Regime with
     * @param pClass Class of Tax Regime
     * @throws JOceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final TaxRegimeClass pClass) throws JOceanusException {
        super(pList, pClass);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the TaxRegime with
     * @param pId the id of the new item
     * @param isEnabled is the regime enabled
     * @param pOrder the sort order
     * @param pName Name of Tax Regime
     * @param pDesc Description of Tax Regime
     * @throws JOceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final Integer pId,
                      final Boolean isEnabled,
                      final Integer pOrder,
                      final String pName,
                      final String pDesc) throws JOceanusException {
        super(pList, pId, isEnabled, pOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the TaxRegime with
     * @param pId ID of TaxRegime
     * @param pControlId the control id of the new item
     * @param isEnabled is the regime enabled
     * @param pOrder the sort order
     * @param pName Encrypted Name of TaxRegime
     * @param pDesc Encrypted Description of TaxRegime
     * @throws JOceanusException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final Integer pId,
                      final Integer pControlId,
                      final Boolean isEnabled,
                      final Integer pOrder,
                      final byte[] pName,
                      final byte[] pDesc) throws JOceanusException {
        super(pList, pId, pControlId, isEnabled, pOrder, pName, pDesc);
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
        protected Class<TaxRegimeClass> getEnumClass() {
            return TaxRegimeClass.class;
        }

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
        protected TaxRegimeList getEmptyList(final ListStyle pStyle) {
            TaxRegimeList myList = new TaxRegimeList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxRegimeList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (TaxRegimeList) super.cloneList(pDataSet);
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
                return null;
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
         * @throws JOceanusException on error
         */
        public void addBasicItem(final String pTaxRegime) throws JOceanusException {
            /* Create a new tax regime */
            TaxRegime myTaxRegime = new TaxRegime(this, pTaxRegime);

            /* Check that this TaxRegime has not been previously added */
            if (findItemByName(pTaxRegime) != null) {
                myTaxRegime.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myTaxRegime, ERROR_VALIDATION);
            }

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(myTaxRegime.getId())) {
                myTaxRegime.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myTaxRegime, ERROR_VALIDATION);
            }

            /* Add the TaxRegime to the list */
            append(myTaxRegime);

            /* Validate the TaxRegime */
            myTaxRegime.validate();

            /* Handle validation failure */
            if (myTaxRegime.hasErrors()) {
                throw new JMoneyWiseDataException(myTaxRegime, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxRegime to the list.
         * @param pId the id of the new item
         * @param isEnabled is the regime enabled
         * @param pOrder the sort order
         * @param pTaxRegime the Name of the tax regime
         * @param pDesc the Description of the tax regime
         * @throws JOceanusException on error
         */
        public void addOpenItem(final Integer pId,
                                final Boolean isEnabled,
                                final Integer pOrder,
                                final String pTaxRegime,
                                final String pDesc) throws JOceanusException {
            /* Create a new Tax Regime */
            TaxRegime myTaxReg = new TaxRegime(this, pId, isEnabled, pOrder, pTaxRegime, pDesc);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(pId)) {
                myTaxReg.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myTaxReg, ERROR_VALIDATION);
            }

            /* Add the Tax Regime to the list */
            append(myTaxReg);

            /* Validate the TaxRegime */
            myTaxReg.validate();

            /* Handle validation failure */
            if (myTaxReg.hasErrors()) {
                throw new JMoneyWiseDataException(myTaxReg, ERROR_VALIDATION);
            }
        }

        /**
         * Add a TaxRegime.
         * @param pId the Id of the tax regime
         * @param pControlId the control id of the new item
         * @param isEnabled is the regime enabled
         * @param pOrder the sort order
         * @param pTaxRegime the Encrypted Name of the tax regime
         * @param pDesc the Encrypted Description of the tax regime
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final Boolean isEnabled,
                                  final Integer pOrder,
                                  final byte[] pTaxRegime,
                                  final byte[] pDesc) throws JOceanusException {
            /* Create a new tax regime */
            TaxRegime myTaxReg = new TaxRegime(this, pId, pControlId, isEnabled, pOrder, pTaxRegime, pDesc);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(pId)) {
                myTaxReg.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myTaxReg, ERROR_VALIDATION);
            }

            /* Add the TaxRegime to the list */
            append(myTaxReg);

            /* Validate the TaxRegime */
            myTaxReg.validate();

            /* Handle validation failure */
            if (myTaxReg.hasErrors()) {
                throw new JMoneyWiseDataException(myTaxReg, ERROR_VALIDATION);
            }
        }

        /**
         * Populate default values.
         * @throws JOceanusException on error
         */
        public void populateDefaults() throws JOceanusException {
            /* Loop through all elements */
            for (TaxRegimeClass myClass : TaxRegimeClass.values()) {
                /* Create new element */
                TaxRegime myRegime = new TaxRegime(this, myClass);

                /* Add the Regime to the list */
                append(myRegime);

                /* Validate the Regime */
                myRegime.validate();

                /* Handle validation failure */
                if (myRegime.hasErrors()) {
                    throw new JMoneyWiseDataException(myRegime, ERROR_VALIDATION);
                }
            }

            /* Ensure that the list is sorted */
            reSort();
        }
    }
}
