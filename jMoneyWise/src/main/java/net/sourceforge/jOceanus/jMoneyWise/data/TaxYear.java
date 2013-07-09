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
package net.sourceforge.jOceanus.jMoneyWise.data;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet.InfoSetItem;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.jOceanus.jSortedList.OrderedListIterator;

/**
 * TaxYear DataItem utilising TaxYearInfo.
 * @author Tony Washer
 */
public class TaxYear
        extends TaxYearBase
        implements InfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYear.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(TaxYear.class.getSimpleName(), TaxYearBase.FIELD_DEFS);

    /**
     * TaxInfoSet field Id.
     */
    public static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField("InfoSet");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                    ? theInfoSet
                    : JDataFieldValue.SkipField;
        }

        /* Handle infoSet fields */
        TaxYearInfoClass myClass = TaxInfoSet.getClassForField(pField);
        if ((theInfoSet != null)
            && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * TaxInfoSet.
     */
    private final TaxInfoSet theInfoSet;

    @Override
    public TaxInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Allowance.
     * @return the allowance
     */
    public JMoney getAllowance() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.Allowance, JMoney.class)
                : null;
    }

    /**
     * Obtain Rental Allowance.
     * @return the rental allowance
     */
    public JMoney getRentalAllowance() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.RentalAllowance, JMoney.class)
                : null;
    }

    /**
     * Obtain LoTaxBand.
     * @return the tax band
     */
    public JMoney getLoBand() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.LoTaxBand, JMoney.class)
                : null;
    }

    /**
     * Obtain Basic Tax band.
     * @return the tax band
     */
    public JMoney getBasicBand() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.BasicTaxBand, JMoney.class)
                : null;
    }

    /**
     * Obtain Capital Allowance.
     * @return the allowance
     */
    public JMoney getCapitalAllow() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.CapitalAllowance, JMoney.class)
                : null;
    }

    /**
     * Obtain LoAge Allowance.
     * @return the allowance
     */
    public JMoney getLoAgeAllow() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.LoAgeAllowance, JMoney.class)
                : null;
    }

    /**
     * Obtain HiAge Allowance.
     * @return the allowance
     */
    public JMoney getHiAgeAllow() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.HiAgeAllowance, JMoney.class)
                : null;
    }

    /**
     * Obtain Age Allowance Limit.
     * @return the limit
     */
    public JMoney getAgeAllowLimit() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.AgeAllowanceLimit, JMoney.class)
                : null;
    }

    /**
     * Obtain Additional Allowance Limit.
     * @return the limit
     */
    public JMoney getAddAllowLimit() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.AdditionalAllowanceLimit, JMoney.class)
                : null;
    }

    /**
     * Obtain Additional Income Boundary.
     * @return the boundary
     */
    public JMoney getAddIncBound() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.AdditionalIncomeThreshold, JMoney.class)
                : null;
    }

    /**
     * Obtain LoTaxRate.
     * @return the rate
     */
    public JRate getLoTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.LoTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain BasicTaxRate.
     * @return the rate
     */
    public JRate getBasicTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.BasicTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain HiTaxRate.
     * @return the rate
     */
    public JRate getHiTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.HiTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain InterestTaxRate.
     * @return the rate
     */
    public JRate getIntTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.InterestTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain DividendTaxRate.
     * @return the rate
     */
    public JRate getDivTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.DividendTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain HiDividendTaxRate.
     * @return the rate
     */
    public JRate getHiDivTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.HiDividendTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain AdditionalTaxRate.
     * @return the rate
     */
    public JRate getAddTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.AdditionalTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain AdditionalDividendTaxRate.
     * @return the rate
     */
    public JRate getAddDivTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.AdditionalDividendTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain CapitalTaxRate.
     * @return the rate
     */
    public JRate getCapTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.CapitalTaxRate, JRate.class)
                : null;
    }

    /**
     * Obtain HiCapitalTaxRate.
     * @return the rate
     */
    public JRate getHiCapTaxRate() {
        return hasInfoSet
                ? theInfoSet.getValue(TaxYearInfoClass.HiCapitalTaxRate, JRate.class)
                : null;
    }

    /**
     * adjust values after taxRegime change.
     * @throws JDataException on error
     */
    public void adjustForTaxRegime() throws JDataException {
        /* Access tax regime */
        TaxRegime myRegime = getTaxRegime();

        /* If we are setting a non-null regime */
        if (myRegime != null) {
            /* Clear Capital tax rates if required */
            if (myRegime.hasCapitalGainsAsIncome()) {
                setCapTaxRate(null);
                setHiCapTaxRate(null);
            }

            /* Clear Additional values if required */
            if (!myRegime.hasAdditionalTaxBand()) {
                setAddAllowLimit(null);
                setAddIncBound(null);
                setAddTaxRate(null);
                setAddDivTaxRate(null);
            }
        }
    }

    /**
     * Set a new allowance.
     * @param pAllowance the allowance
     * @throws JDataException on error
     */
    public void setAllowance(final JMoney pAllowance) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.Allowance, pAllowance);
    }

    /**
     * Set a new rental allowance.
     * @param pAllowance the allowance
     * @throws JDataException on error
     */
    public void setRentalAllowance(final JMoney pAllowance) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.RentalAllowance, pAllowance);
    }

    /**
     * Set a new capital allowance.
     * @param pAllowance the allowance
     * @throws JDataException on error
     */
    public void setCapitalAllow(final JMoney pAllowance) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.CapitalAllowance, pAllowance);
    }

    /**
     * Set a new Low Tax Band.
     * @param pLoBand the Low Tax Band
     * @throws JDataException on error
     */
    public void setLoBand(final JMoney pLoBand) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.LoTaxBand, pLoBand);
    }

    /**
     * Set a new Basic Tax Band.
     * @param pBasicBand the Basic Tax Band
     * @throws JDataException on error
     */
    public void setBasicBand(final JMoney pBasicBand) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.BasicTaxBand, pBasicBand);
    }

    /**
     * Set a new Low Age Allowance.
     * @param pLoAgeAllow the Low Age Allowance
     * @throws JDataException on error
     */
    public void setLoAgeAllow(final JMoney pLoAgeAllow) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.LoAgeAllowance, pLoAgeAllow);
    }

    /**
     * Set a new High Age Allowance.
     * @param pHiAgeAllow the High Age Allowance
     * @throws JDataException on error
     */
    public void setHiAgeAllow(final JMoney pHiAgeAllow) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiAgeAllowance, pHiAgeAllow);
    }

    /**
     * Set a new Age Allowance Limit.
     * @param pAgeAllowLimit the Age Allowance Limit
     * @throws JDataException on error
     */
    public void setAgeAllowLimit(final JMoney pAgeAllowLimit) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AgeAllowanceLimit, pAgeAllowLimit);
    }

    /**
     * Set a new Additional Allowance Limit.
     * @param pAddAllowLimit the Additional Allowance Limit
     * @throws JDataException on error
     */
    public void setAddAllowLimit(final JMoney pAddAllowLimit) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AdditionalAllowanceLimit, pAddAllowLimit);
    }

    /**
     * Set a new Additional Income Boundary.
     * @param pAddIncBound the Additional Income Boundary
     * @throws JDataException on error
     */
    public void setAddIncBound(final JMoney pAddIncBound) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AdditionalIncomeThreshold, pAddIncBound);
    }

    /**
     * Set a new Low Tax Rate.
     * @param pRate the Low Tax Rate
     * @throws JDataException on error
     */
    public void setLoTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.LoTaxRate, pRate);
    }

    /**
     * Set a new Basic tax rate.
     * @param pRate the Basic tax rate
     * @throws JDataException on error
     */
    public void setBasicTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.BasicTaxRate, pRate);
    }

    /**
     * Set a new high tax rate.
     * @param pRate the high tax rate
     * @throws JDataException on error
     */
    public void setHiTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiTaxRate, pRate);
    }

    /**
     * Set a new Interest Tax Rate.
     * @param pRate the Interest Tax Rate
     * @throws JDataException on error
     */
    public void setIntTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.InterestTaxRate, pRate);
    }

    /**
     * Set a new Dividend tax rate.
     * @param pRate the Dividend tax rate
     * @throws JDataException on error
     */
    public void setDivTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.DividendTaxRate, pRate);
    }

    /**
     * Set a new high dividend tax rate.
     * @param pRate the high dividend tax rate
     * @throws JDataException on error
     */
    public void setHiDivTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiDividendTaxRate, pRate);
    }

    /**
     * Set a new additional tax rate.
     * @param pRate the additional tax rate
     * @throws JDataException on error
     */
    public void setAddTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AdditionalTaxRate, pRate);
    }

    /**
     * Set a new additional dividend tax rate.
     * @param pRate the additional dividend tax rate
     * @throws JDataException on error
     */
    public void setAddDivTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AdditionalDividendTaxRate, pRate);
    }

    /**
     * Set a new capital tax rate.
     * @param pRate the capital tax rate
     * @throws JDataException on error
     */
    public void setCapTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.CapitalTaxRate, pRate);
    }

    /**
     * Set a high capital tax rate.
     * @param pRate the high capital tax rate
     * @throws JDataException on error
     */
    public void setHiCapTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiCapitalTaxRate, pRate);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JDataException on error
     */
    private void setInfoSetValue(final TaxYearInfoClass pInfoClass,
                                 final Object pValue) throws JDataException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid call to set InfoSet value");
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN)
            && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public EditState getEditState() {
        /* Pop history for self */
        EditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == EditState.CLEAN)
            && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getEditState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public boolean hasHistory() {
        /* Check for history for self */
        boolean hasHistory = super.hasHistory();

        /* If we should use the InfoSet */
        if ((!hasHistory)
            && (useInfoSet)) {
            /* Check history for infoSet */
            hasHistory = theInfoSet.hasHistory();
        }

        /* Return details */
        return hasHistory;
    }

    @Override
    public void pushHistory() {
        /* Push history for self */
        super.pushHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Push history for infoSet */
            theInfoSet.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Pop history for self */
        super.popHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Pop history for infoSet */
            theInfoSet.popHistory();
        }
    }

    @Override
    public boolean checkForHistory() {
        /* Check for history for self */
        boolean bChanges = super.checkForHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Check for history for infoSet */
            bChanges |= theInfoSet.checkForHistory();
        }

        /* return result */
        return bChanges;
    }

    @Override
    public Difference fieldChanged(final JDataField pField) {
        /* Handle InfoSet fields */
        TaxYearInfoClass myClass = TaxInfoSet.getClassForField(pField);
        if (myClass != null) {
            return (useInfoSet)
                    ? theInfoSet.fieldChanged(myClass)
                    : Difference.Identical;
        }

        /* Check super fields */
        return super.fieldChanged(pField);
    }

    @Override
    public void setDeleted(final boolean bDeleted) {
        /* Pass call to infoSet if required */
        if (useInfoSet) {
            theInfoSet.setDeleted(bDeleted);
        }

        /* Pass call onwards */
        super.setDeleted(bDeleted);
    }

    @Override
    public TaxYear getBase() {
        return (TaxYear) super.getBase();
    }

    /**
     * Construct a copy of a TaxYear.
     * @param pList The List to build into
     * @param pTaxYear The TaxYear to copy
     */
    public TaxYear(final TaxYearList pList,
                   final TaxYear pTaxYear) {
        /* Initialise item */
        super(pList, pTaxYear);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new TaxInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
                theInfoSet.cloneDataInfoSet(pTaxYear.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new TaxInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
                hasInfoSet = true;
                useInfoSet = false;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                useInfoSet = false;
                break;
        }
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param pId the id
     * @param pRegimeId the regime id
     * @param pDate the date
     * @throws JDataException on error
     */
    public TaxYear(final TaxYearList pList,
                   final Integer pId,
                   final Integer pRegimeId,
                   final JDateDay pDate) throws JDataException {
        /* Initialise item */
        super(pList, pId, pRegimeId, pDate);

        /* Create the InfoSet */
        theInfoSet = new TaxInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pRegime the tax regime
     * @param pDate the date
     * @throws JDataException on error
     */
    public TaxYear(final TaxYearList pList,
                   final Integer pId,
                   final String pRegime,
                   final JDateDay pDate) throws JDataException {
        /* Initialise item */
        super(pList, pId, pRegime, pDate);

        /* Create the InfoSet */
        theInfoSet = new TaxInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        /* Validate underlying details */
        super.validate();

        /* Access TaxYear details */
        JDateDay myDate = getTaxYear();
        TaxRegime myTaxRegime = getTaxRegime();
        TaxYearList myList = (TaxYearList) getList();

        /* Check underlying fields */
        if (myDate != null) {
            /* The year must be one greater than the preceding element */
            TaxYear myPrev = myList.peekPrevious(this);
            if ((myPrev != null)
                && (myDate.getYear() != myPrev.getTaxYear().getYear() + 1)) {
                addError("There can be no gaps in the list", FIELD_TAXYEAR);
            }
        }

        /* If we have a tax regime and an infoSet */
        if ((myTaxRegime != null)
            && (theInfoSet != null)) {
            /* Validate the InfoSet */
            theInfoSet.validate();
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    @Override
    public void touchUnderlyingItems() {
        /* mark underlying items */
        super.touchUnderlyingItems();

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    /**
     * The Tax Year List class.
     */
    public static class TaxYearList
            extends TaxYearBaseList<TaxYear> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxYearList.class.getSimpleName(), TaxYearBaseList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The TaxInfo List.
         */
        private TaxInfoList theInfoList = null;

        /**
         * The TaxInfoType list.
         */
        private TaxYearInfoTypeList theInfoTypeList = null;

        /**
         * The NewYear.
         */
        private TaxYear theNewYear = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Obtain the taxInfoList.
         * @return the tax info list
         */
        public TaxInfoList getTaxInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getTaxInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the taxInfoTypeList.
         * @return the tax info type list
         */
        public TaxYearInfoTypeList getTaxInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getTaxInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Obtain the new year.
         * @return the new year
         */
        public TaxYear getNewYear() {
            return theNewYear;
        }

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         */
        public TaxYearList(final FinanceData pData) {
            super(pData, TaxYear.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxYearList(final TaxYearList pSource) {
            super(pSource);
        }

        @Override
        protected TaxYearList getEmptyList(final ListStyle pStyle) {
            TaxYearList myList = new TaxYearList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public TaxYearList cloneList(final DataSet<?> pDataSet) throws JDataException {
            /* Clone the list */
            return (TaxYearList) super.cloneList(pDataSet);
        }

        @Override
        public TaxYearList deriveList(final ListStyle pStyle) throws JDataException {
            return (TaxYearList) super.deriveList(pStyle);
        }

        @Override
        public TaxYearList deriveDifferences(final DataList<TaxYear> pOld) {
            return (TaxYearList) super.deriveDifferences(pOld);
        }

        /**
         * Construct an edit extract for a TaxYear.
         * @param pTaxYear the tax year
         * @return the edit Extract
         */
        public TaxYearList deriveEditList(final TaxYear pTaxYear) {
            /* Build an empty List */
            TaxYearList myList = getEmptyList(ListStyle.EDIT);

            /* Store InfoType list */
            myList.theInfoTypeList = getTaxInfoTypes();

            /* Create info List */
            TaxInfoList myTaxInfo = getTaxInfo();
            myList.theInfoList = myTaxInfo.getEmptyList(ListStyle.EDIT);

            /* Create a new tax year based on the passed tax year */
            TaxYear myYear = new TaxYear(myList, pTaxYear);
            myList.add(myYear);

            /* Return the List */
            return myList;
        }

        /**
         * Create a new year based on the last year.
         * @return the new list
         */
        public TaxYearList deriveNewEditList() {
            /* Build an empty List */
            TaxYearList myList = getEmptyList(ListStyle.EDIT);

            /* Store InfoType list */
            myList.theInfoTypeList = theInfoTypeList;

            /* Create info List */
            TaxInfoList myTaxInfo = getTaxInfo();
            myList.theInfoList = myTaxInfo.getEmptyList(ListStyle.EDIT);

            /* Access the existing tax years */
            FinanceData myData = getDataSet();
            TaxYearList myTaxYears = myData.getTaxYears();
            OrderedListIterator<TaxYear> myIterator = myTaxYears.listIterator();

            /* Create a new tax year for the list */
            TaxYear myBase = myIterator.peekLast();
            TaxYear myYear = new TaxYear(myList, myBase);
            myYear.setBase(null);
            myYear.setId(0);

            /* Make sure that it is new */
            myYear.setNewVersion();

            /* Adjust the year and add to list */
            myYear.setTaxYear(new JDateDay(myBase.getTaxYear()));
            myYear.getTaxYear().adjustYear(1);
            myList.add(myYear);

            /* Record the new year */
            myList.theNewYear = myYear;

            /* Return the List */
            return myList;
        }

        @Override
        public TaxYear addCopyItem(final DataItem pTaxYear) {
            /* Can only clone a TaxYear */
            if (!(pTaxYear instanceof TaxYear)) {
                return null;
            }

            TaxYear myYear = new TaxYear(this, (TaxYear) pTaxYear);
            add(myYear);
            return myYear;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TaxYear addNewItem() {
            return null;
        }

        /**
         * Allow a tax parameter to be added.
         * @param pId the id
         * @param pRegimeId the regime id
         * @param pDate the date
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pRegimeId,
                                  final JDateDay pDate) throws JDataException {
            /* Create the tax year */
            TaxYear myTaxYear = new TaxYear(this, pId, pRegimeId, pDate);

            /* Check that this TaxYearId has not been previously added */
            if (!isIdUnique(pId)) {
                myTaxYear.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myTaxYear, ERROR_VALIDATION);
            }

            /* Check that this TaxYear has not been previously added */
            if (findTaxYearForDate(new JDateDay(pDate)) != null) {
                myTaxYear.addError(ERROR_DUPLICATE, FIELD_TAXYEAR);
                throw new JDataException(ExceptionClass.DATA, myTaxYear, ERROR_VALIDATION);
            }

            /* Add the TaxYear to the end of the list */
            append(myTaxYear);
        }

        /**
         * Allow a tax parameter to be added.
         * @param pId the id
         * @param pRegime the regime
         * @param pDate the date
         * @return the taxYear
         * @throws JDataException on error
         */
        public TaxYear addOpenItem(final Integer pId,
                                   final String pRegime,
                                   final JDateDay pDate) throws JDataException {
            /* Create the tax year */
            TaxYear myTaxYear = new TaxYear(this, pId, pRegime, pDate);

            /* Check that this TaxYear has not been previously added */
            if (findTaxYearForDate(new JDateDay(pDate)) != null) {
                myTaxYear.addError(ERROR_DUPLICATE, FIELD_TAXYEAR);
                throw new JDataException(ExceptionClass.DATA, myTaxYear, ERROR_VALIDATION);
            }

            /* Add the TaxYear to the end of the list */
            append(myTaxYear);
            return myTaxYear;
        }
    }
}
