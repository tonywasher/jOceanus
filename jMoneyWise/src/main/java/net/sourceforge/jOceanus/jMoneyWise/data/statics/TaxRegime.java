/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;

/**
 * TaxRegime data type.
 * @author Tony Washer
 */
public class TaxRegime
        extends StaticData<TaxRegime, TaxRegClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxRegime.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

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
    public TaxRegClass getRegime() {
        return super.getStaticClass();
    }

    @Override
    public TaxRegime getBase() {
        return (TaxRegime) super.getBase();
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
     * @param sName Name of TaxRegime
     * @throws JDataException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Open Constructor.
     * @param pList The list to associate the TaxRegime with
     * @param uId the id of the new item
     * @param isEnabled is the regime enabled
     * @param uOrder the sort order
     * @param pName Name of Tax Regime
     * @param pDesc Description of Tax Regime
     * @throws JDataException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final Integer uId,
                      final Boolean isEnabled,
                      final Integer uOrder,
                      final String pName,
                      final String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Secure Constructor.
     * @param pList The list to associate the TaxRegime with
     * @param uId ID of TaxRegime
     * @param uControlId the control id of the new item
     * @param isEnabled is the regime enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of TaxRegime
     * @param pDesc Encrypted Description of TaxRegime
     * @throws JDataException on error
     */
    private TaxRegime(final TaxRegimeList pList,
                      final Integer uId,
                      final Integer uControlId,
                      final Boolean isEnabled,
                      final Integer uOrder,
                      final byte[] pName,
                      final byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
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
            extends StaticList<TaxRegime, TaxRegClass> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxRegimeList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TaxRegClass> getEnumClass() {
            return TaxRegClass.class;
        }

        /**
         * Construct an empty CORE tax regime list.
         * @param pData the DataSet for the list
         */
        public TaxRegimeList(final DataSet<?> pData) {
            super(TaxRegime.class, pData, ListStyle.CORE);
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
        public TaxRegimeList cloneList(final DataSet<?> pDataSet) {
            return (TaxRegimeList) super.cloneList(pDataSet);
        }

        @Override
        public TaxRegimeList deriveList(final ListStyle pStyle) {
            return (TaxRegimeList) super.deriveList(pStyle);
        }

        @Override
        public TaxRegimeList deriveDifferences(final DataList<TaxRegime> pOld) {
            return (TaxRegimeList) super.deriveDifferences(pOld);
        }

        /**
         * Add a new item to the list.
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxRegime addCopyItem(final DataItem pItem) {
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
         * Add a TaxRegime.
         * @param pTaxRegime the Name of the tax regime
         * @throws JDataException on error
         */
        public void addBasicItem(final String pTaxRegime) throws JDataException {
            /* Create a new tax regime */
            TaxRegime myTaxRegime = new TaxRegime(this, pTaxRegime);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(myTaxRegime.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTaxRegime, "Duplicate TaxRegimeId");
            }

            /* Check that this TaxRegime has not been previously added */
            if (findItemByName(pTaxRegime) != null) {
                throw new JDataException(ExceptionClass.DATA, myTaxRegime, "Duplicate TaxRegime");
            }

            /* Add the TaxRegime to the list */
            append(myTaxRegime);

            /* Validate the TaxRegime */
            myTaxRegime.validate();

            /* Handle validation failure */
            if (myTaxRegime.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxRegime, "Failed validation");
            }
        }

        /**
         * Add a TaxRegime to the list.
         * @param uId the id of the new item
         * @param isEnabled is the regime enabled
         * @param uOrder the sort order
         * @param pTaxRegime the Name of the tax regime
         * @param pDesc the Description of the tax regime
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final Boolean isEnabled,
                                final Integer uOrder,
                                final String pTaxRegime,
                                final String pDesc) throws JDataException {
            /* Create a new Tax Regime */
            TaxRegime myTaxReg = new TaxRegime(this, uId, isEnabled, uOrder, pTaxRegime, pDesc);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(myTaxReg.getId())) {
                throw new JDataException(ExceptionClass.DATA, myTaxReg, "Duplicate TaxRegimeId");
            }

            /* Add the Tax Regime to the list */
            append(myTaxReg);

            /* Validate the TaxRegime */
            myTaxReg.validate();

            /* Handle validation failure */
            if (myTaxReg.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxReg, "Failed validation");
            }
        }

        /**
         * Add a TaxRegime.
         * @param uId the Id of the tax regime
         * @param uControlId the control id of the new item
         * @param isEnabled is the regime enabled
         * @param uOrder the sort order
         * @param pTaxRegime the Encrypted Name of the tax regime
         * @param pDesc the Encrypted Description of the tax regime
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Boolean isEnabled,
                                  final Integer uOrder,
                                  final byte[] pTaxRegime,
                                  final byte[] pDesc) throws JDataException {
            /* Create a new tax regime */
            TaxRegime myTaxReg = new TaxRegime(this, uId, uControlId, isEnabled, uOrder, pTaxRegime, pDesc);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myTaxReg, "Duplicate TaxRegimeId");
            }

            /* Add the TaxRegime to the list */
            append(myTaxReg);

            /* Validate the TaxRegime */
            myTaxReg.validate();

            /* Handle validation failure */
            if (myTaxReg.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxReg, "Failed validation");
            }
        }
    }
}
