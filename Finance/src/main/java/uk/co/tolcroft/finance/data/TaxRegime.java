/*******************************************************************************
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
package uk.co.tolcroft.finance.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import uk.co.tolcroft.finance.data.StaticClass.TaxRegClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.StaticData;

public class TaxRegime extends StaticData<TaxRegime, TaxRegClass> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxRegime.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    private static final JDataFields theLocalFields = new JDataFields(OBJECT_NAME, StaticData.FIELD_DEFS);

    /* Called from constructor */
    @Override
    public JDataFields declareFields() {
        return theLocalFields;
    }

    /**
     * Return the TaxRegime class of the TaxRegime
     * @return the class
     */
    public TaxRegClass getRegime() {
        return super.getStaticClass();
    }

    /* Linking methods */
    @Override
    public TaxRegime getBase() {
        return (TaxRegime) super.getBase();
    }

    /**
     * Construct a copy of a TaxRegime.
     * @param pList The list to associate the TaxRegime with
     * @param pTaxRegime The TaxRegime to copy
     */
    protected TaxRegime(TaxRegimeList pList,
                        TaxRegime pTaxRegime) {
        super(pList, pTaxRegime);
    }

    /**
     * Construct a standard TaxRegime on load
     * 
     * @param pList The list to associate the TaxRegime with
     * @param sName Name of TaxRegime
     * @throws JDataException
     */
    private TaxRegime(TaxRegimeList pList,
                      String sName) throws JDataException {
        super(pList, sName);
    }

    /**
     * Construct a tax regime on load
     * @param pList The list to associate the TaxRegime with
     * @param uId the id of the new item
     * @param isEnabled is the regime enabled
     * @param uOrder the sort order
     * @param pName Name of Tax Regime
     * @param pDesc Description of Tax Regime
     * @throws JDataException
     */
    private TaxRegime(TaxRegimeList pList,
                      int uId,
                      boolean isEnabled,
                      int uOrder,
                      String pName,
                      String pDesc) throws JDataException {
        super(pList, uId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Construct a standard TaxRegime on load
     * @param pList The list to associate the TaxRegime with
     * @param uId ID of TaxRegime
     * @param uControlId the control id of the new item
     * @param isEnabled is the regime enabled
     * @param uOrder the sort order
     * @param pName Encrypted Name of TaxRegime
     * @param pDesc Encrypted Description of TaxRegime
     * @throws JDataException
     */
    private TaxRegime(TaxRegimeList pList,
                      int uId,
                      int uControlId,
                      boolean isEnabled,
                      int uOrder,
                      byte[] pName,
                      byte[] pDesc) throws JDataException {
        super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
    }

    /**
     * Determine whether this tax regime supports a Low Salary Band
     * @return <code>true/false</code>
     */
    public boolean hasLoSalaryBand() {
        switch (getStaticClass()) {
            case ARCHIVE:
                return true;
            case STANDARD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this tax regime treats capital gains as standard income
     * @return <code>true/false</code>
     */
    public boolean hasCapitalGainsAsIncome() {
        switch (getStaticClass()) {
            case STANDARD:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether this tax regime supports an additional taxation band
     * @return <code>true/false</code>
     */
    public boolean hasAdditionalTaxBand() {
        switch (getStaticClass()) {
            case ADDITIONALBAND:
                return true;
            default:
                return false;
        }
    }

    /**
     * Represents a list of {@link TaxRegime} objects.
     */
    public static class TaxRegimeList extends StaticList<TaxRegimeList, TaxRegime, TaxRegClass> {
        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        protected Class<TaxRegClass> getEnumClass() {
            return TaxRegClass.class;
        }

        /**
         * Construct an empty CORE tax regime list
         * @param pData the DataSet for the list
         */
        protected TaxRegimeList(FinanceData pData) {
            super(TaxRegimeList.class, TaxRegime.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private TaxRegimeList(TaxRegimeList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private TaxRegimeList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            TaxRegimeList myList = new TaxRegimeList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public TaxRegimeList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public TaxRegimeList getEditList() {
            return getExtractList(ListStyle.EDIT);
        }

        @Override
        public TaxRegimeList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public TaxRegimeList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            TaxRegimeList myList = new TaxRegimeList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        @Override
        protected TaxRegimeList getDifferences(TaxRegimeList pOld) {
            /* Build an empty Difference List */
            TaxRegimeList myList = new TaxRegimeList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Add a new item to the list
         * @param pItem item to be added
         * @return the newly added item
         */
        @Override
        public TaxRegime addNewItem(DataItem<?> pItem) {
            TaxRegime myRegime = new TaxRegime(this, (TaxRegime) pItem);
            add(myRegime);
            return myRegime;
        }

        /**
         * Create a new empty element in the edit list (null-operation)
         * @return the newly added item
         */
        @Override
        public TaxRegime addNewItem() {
            return null;
        }

        /**
         * Obtain the type of the item
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Add a TaxRegime
         * @param pTaxRegime the Name of the tax regime
         * @throws JDataException
         */
        public void addItem(String pTaxRegime) throws JDataException {
            TaxRegime myTaxRegime;

            /* Create a new tax regime */
            myTaxRegime = new TaxRegime(this, pTaxRegime);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(myTaxRegime.getId()))
                throw new JDataException(ExceptionClass.DATA, myTaxRegime, "Duplicate TaxRegimeId");

            /* Check that this TaxRegime has not been previously added */
            if (searchFor(pTaxRegime) != null)
                throw new JDataException(ExceptionClass.DATA, myTaxRegime, "Duplicate TaxRegime");

            /* Add the TaxRegime to the list */
            add(myTaxRegime);
        }

        /**
         * Add a TaxRegime to the list
         * @param uId the id of the new item
         * @param isEnabled is the regime enabled
         * @param uOrder the sort order
         * @param pTaxRegime the Name of the tax regime
         * @param pDesc the Description of the tax regime
         * @throws JDataException
         */
        public void addItem(int uId,
                            boolean isEnabled,
                            int uOrder,
                            String pTaxRegime,
                            String pDesc) throws JDataException {
            TaxRegime myTaxReg;

            /* Create a new Tax Regime */
            myTaxReg = new TaxRegime(this, uId, isEnabled, uOrder, pTaxRegime, pDesc);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(myTaxReg.getId()))
                throw new JDataException(ExceptionClass.DATA, myTaxReg, "Duplicate TaxRegimeId");

            /* Add the Tax Regime to the list */
            add(myTaxReg);

            /* Validate the TaxRegime */
            myTaxReg.validate();

            /* Handle validation failure */
            if (myTaxReg.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myTaxReg, "Failed validation");
        }

        /**
         * Add a TaxRegime
         * @param uId the Id of the tax regime
         * @param uControlId the control id of the new item
         * @param isEnabled is the regime enabled
         * @param uOrder the sort order
         * @param pTaxRegime the Encrypted Name of the tax regime
         * @param pDesc the Encrypted Description of the tax regime
         * @throws JDataException
         */
        public void addItem(int uId,
                            int uControlId,
                            boolean isEnabled,
                            int uOrder,
                            byte[] pTaxRegime,
                            byte[] pDesc) throws JDataException {
            TaxRegime myTaxReg;

            /* Create a new tax regime */
            myTaxReg = new TaxRegime(this, uId, uControlId, isEnabled, uOrder, pTaxRegime, pDesc);

            /* Check that this TaxRegimeId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, myTaxReg, "Duplicate TaxRegimeId");

            /* Add the TaxRegime to the list */
            add(myTaxReg);

            /* Validate the TaxRegime */
            myTaxReg.validate();

            /* Handle validation failure */
            if (myTaxReg.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myTaxReg, "Failed validation");
        }
    }
}
