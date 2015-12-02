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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataState;
import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.data.MetisEditState;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedListIterator;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataInstanceMap;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysFiscalYear;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * TaxYear DataItem utilising TaxYearInfo.
 * @author Tony Washer
 */
public class TaxYear
        extends TaxYearBase<TaxYear>
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.TAXYEAR.getItemName();

    /**
     * List name.
     */
    public static final String LIST_NAME = MoneyWiseDataType.TAXYEAR.getListName();

    /**
     * Local Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, TaxYearBase.FIELD_DEFS);

    /**
     * TaxInfoSet field Id.
     */
    private static final MetisField FIELD_INFOSET = FIELD_DEFS.declareLocalField(PrometheusDataResource.DATAINFOSET_NAME.getValue());

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = PrometheusDataResource.DATAINFOSET_ERROR_BADSET.getValue();

    /**
     * Bad ListGap Error Text.
     */
    private static final String ERROR_LISTGAP = MoneyWiseDataResource.TAXYEAR_ERROR_LISTGAP.getValue();

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
    private final TaxYearInfoSet theInfoSet;

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
                theInfoSet = new TaxYearInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
                theInfoSet.cloneDataInfoSet(pTaxYear.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new TaxYearInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
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
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values constructor
     * @throws OceanusException on error
     */
    private TaxYear(final TaxYearList pList,
                    final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new TaxYearInfoSet(this, pList.getTaxInfoTypes(), pList.getTaxInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                              ? theInfoSet
                              : MetisFieldValue.SKIP;
        }

        /* Handle infoSet fields */
        TaxYearInfoClass myClass = TaxYearInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    @Override
    public TaxYearInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Allowance.
     * @return the allowance
     */
    public TethysMoney getAllowance() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.ALLOWANCE, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Rental Allowance.
     * @return the rental allowance
     */
    public TethysMoney getRentalAllowance() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.RENTALALLOWANCE, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain LoTaxBand.
     * @return the tax band
     */
    public TethysMoney getLoBand() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.LOTAXBAND, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Basic Tax band.
     * @return the tax band
     */
    public TethysMoney getBasicBand() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.BASICTAXBAND, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Capital Allowance.
     * @return the allowance
     */
    public TethysMoney getCapitalAllow() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.CAPITALALLOWANCE, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain LoAge Allowance.
     * @return the allowance
     */
    public TethysMoney getLoAgeAllow() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.LOAGEALLOWANCE, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain HiAge Allowance.
     * @return the allowance
     */
    public TethysMoney getHiAgeAllow() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.HIAGEALLOWANCE, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Age Allowance Limit.
     * @return the limit
     */
    public TethysMoney getAgeAllowLimit() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.AGEALLOWANCELIMIT, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Additional Allowance Limit.
     * @return the limit
     */
    public TethysMoney getAddAllowLimit() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain Additional Income Boundary.
     * @return the boundary
     */
    public TethysMoney getAddIncBound() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, TethysMoney.class)
                          : null;
    }

    /**
     * Obtain LoTaxRate.
     * @return the rate
     */
    public TethysRate getLoTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.LOTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain BasicTaxRate.
     * @return the rate
     */
    public TethysRate getBasicTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.BASICTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain HiTaxRate.
     * @return the rate
     */
    public TethysRate getHiTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.HITAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain InterestTaxRate.
     * @return the rate
     */
    public TethysRate getIntTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.INTERESTTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain DividendTaxRate.
     * @return the rate
     */
    public TethysRate getDivTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.DIVIDENDTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain HiDividendTaxRate.
     * @return the rate
     */
    public TethysRate getHiDivTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.HIDIVIDENDTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain AdditionalTaxRate.
     * @return the rate
     */
    public TethysRate getAddTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.ADDITIONALTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain AdditionalDividendTaxRate.
     * @return the rate
     */
    public TethysRate getAddDivTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain CapitalTaxRate.
     * @return the rate
     */
    public TethysRate getCapTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.CAPITALTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Obtain HiCapitalTaxRate.
     * @return the rate
     */
    public TethysRate getHiCapTaxRate() {
        return hasInfoSet
                          ? theInfoSet.getValue(TaxYearInfoClass.HICAPITALTAXRATE, TethysRate.class)
                          : null;
    }

    /**
     * Set a new allowance.
     * @param pAllowance the allowance
     * @throws OceanusException on error
     */
    public void setAllowance(final TethysMoney pAllowance) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.ALLOWANCE, pAllowance);
    }

    /**
     * Set a new rental allowance.
     * @param pAllowance the allowance
     * @throws OceanusException on error
     */
    public void setRentalAllowance(final TethysMoney pAllowance) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.RENTALALLOWANCE, pAllowance);
    }

    /**
     * Set a new capital allowance.
     * @param pAllowance the allowance
     * @throws OceanusException on error
     */
    public void setCapitalAllow(final TethysMoney pAllowance) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.CAPITALALLOWANCE, pAllowance);
    }

    /**
     * Set a new Low Tax Band.
     * @param pLoBand the Low Tax Band
     * @throws OceanusException on error
     */
    public void setLoBand(final TethysMoney pLoBand) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.LOTAXBAND, pLoBand);
    }

    /**
     * Set a new Basic Tax Band.
     * @param pBasicBand the Basic Tax Band
     * @throws OceanusException on error
     */
    public void setBasicBand(final TethysMoney pBasicBand) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.BASICTAXBAND, pBasicBand);
    }

    /**
     * Set a new Low Age Allowance.
     * @param pLoAgeAllow the Low Age Allowance
     * @throws OceanusException on error
     */
    public void setLoAgeAllow(final TethysMoney pLoAgeAllow) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.LOAGEALLOWANCE, pLoAgeAllow);
    }

    /**
     * Set a new High Age Allowance.
     * @param pHiAgeAllow the High Age Allowance
     * @throws OceanusException on error
     */
    public void setHiAgeAllow(final TethysMoney pHiAgeAllow) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.HIAGEALLOWANCE, pHiAgeAllow);
    }

    /**
     * Set a new Age Allowance Limit.
     * @param pAgeAllowLimit the Age Allowance Limit
     * @throws OceanusException on error
     */
    public void setAgeAllowLimit(final TethysMoney pAgeAllowLimit) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.AGEALLOWANCELIMIT, pAgeAllowLimit);
    }

    /**
     * Set a new Additional Allowance Limit.
     * @param pAddAllowLimit the Additional Allowance Limit
     * @throws OceanusException on error
     */
    public void setAddAllowLimit(final TethysMoney pAddAllowLimit) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, pAddAllowLimit);
    }

    /**
     * Set a new Additional Income Boundary.
     * @param pAddIncBound the Additional Income Boundary
     * @throws OceanusException on error
     */
    public void setAddIncBound(final TethysMoney pAddIncBound) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, pAddIncBound);
    }

    /**
     * Set a new Low Tax Rate.
     * @param pRate the Low Tax Rate
     * @throws OceanusException on error
     */
    public void setLoTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.LOTAXRATE, pRate);
    }

    /**
     * Set a new Basic tax rate.
     * @param pRate the Basic tax rate
     * @throws OceanusException on error
     */
    public void setBasicTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.BASICTAXRATE, pRate);
    }

    /**
     * Set a new high tax rate.
     * @param pRate the high tax rate
     * @throws OceanusException on error
     */
    public void setHiTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.HITAXRATE, pRate);
    }

    /**
     * Set a new Interest Tax Rate.
     * @param pRate the Interest Tax Rate
     * @throws OceanusException on error
     */
    public void setIntTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.INTERESTTAXRATE, pRate);
    }

    /**
     * Set a new Dividend tax rate.
     * @param pRate the Dividend tax rate
     * @throws OceanusException on error
     */
    public void setDivTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.DIVIDENDTAXRATE, pRate);
    }

    /**
     * Set a new high dividend tax rate.
     * @param pRate the high dividend tax rate
     * @throws OceanusException on error
     */
    public void setHiDivTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.HIDIVIDENDTAXRATE, pRate);
    }

    /**
     * Set a new additional tax rate.
     * @param pRate the additional tax rate
     * @throws OceanusException on error
     */
    public void setAddTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.ADDITIONALTAXRATE, pRate);
    }

    /**
     * Set a new additional dividend tax rate.
     * @param pRate the additional dividend tax rate
     * @throws OceanusException on error
     */
    public void setAddDivTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE, pRate);
    }

    /**
     * Set a new capital tax rate.
     * @param pRate the capital tax rate
     * @throws OceanusException on error
     */
    public void setCapTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.CAPITALTAXRATE, pRate);
    }

    /**
     * Set a high capital tax rate.
     * @param pRate the high capital tax rate
     * @throws OceanusException on error
     */
    public void setHiCapTaxRate(final TethysRate pRate) throws OceanusException {
        setInfoSetValue(TaxYearInfoClass.HICAPITALTAXRATE, pRate);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws OceanusException on error
     */
    private void setInfoSetValue(final TaxYearInfoClass pInfoClass,
                                 final Object pValue) throws OceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public MetisDataState getState() {
        /* Pop history for self */
        MetisDataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == MetisDataState.CLEAN) && useInfoSet) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public MetisEditState getEditState() {
        /* Pop history for self */
        MetisEditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == MetisEditState.CLEAN) && useInfoSet) {
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
        if (!hasHistory && useInfoSet) {
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
    public MetisDifference fieldChanged(final MetisField pField) {
        /* Handle InfoSet fields */
        TaxYearInfoClass myClass = TaxYearInfoSet.getClassForField(pField);
        if (myClass != null) {
            return useInfoSet
                              ? theInfoSet.fieldChanged(myClass)
                              : MetisDifference.IDENTICAL;
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

    @Override
    public TaxYearList getList() {
        return (TaxYearList) super.getList();
    }

    /**
     * adjust values after taxRegime change.
     * @param pUpdateSet the update set
     * @throws OceanusException on error
     */
    public void autoCorrect(final UpdateSet<MoneyWiseDataType> pUpdateSet) throws OceanusException {
        /* autoCorrect the infoSet */
        theInfoSet.autoCorrect(pUpdateSet);
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        /* Validate underlying details */
        super.validate();

        /* Access TaxYear details */
        TethysDate myDate = getTaxYear();
        TaxRegime myTaxRegime = getTaxRegime();
        TaxYearList myList = getList();

        /* Check underlying fields */
        if (myDate != null) {
            /* The year must be one greater than the preceding element */
            TaxYear myPrev = myList.peekPrevious(this);
            if ((myPrev != null) && (myDate.getYear() != myPrev.getTaxYear().getYear() + 1)) {
                addError(ERROR_LISTGAP, FIELD_TAXYEAR);
            }
        }

        /* If we have a tax regime and an infoSet */
        if ((myTaxRegime != null) && (theInfoSet != null)) {
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

    @Override
    public void adjustMapForItem() {
        TaxYearList myList = getList();
        TaxYearDataMap myMap = myList.getDataMap();
        myMap.adjustForItem(this);
    }

    /**
     * The Tax Year List class.
     */
    public static class TaxYearList
            extends TaxYearBaseList<TaxYear> {
        /**
         * Local Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(LIST_NAME, TaxYearBaseList.FIELD_DEFS);

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

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         */
        public TaxYearList(final MoneyWiseData pData) {
            super(pData, TaxYear.class, MoneyWiseDataType.TAXYEAR);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxYearList(final TaxYearList pSource) {
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
            return TaxYear.FIELD_DEFS;
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

        @Override
        protected TaxYearDataMap getDataMap() {
            return (TaxYearDataMap) super.getDataMap();
        }

        @Override
        protected TaxYearList getEmptyList(final ListStyle pStyle) {
            TaxYearList myList = new TaxYearList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Derive Edit list.
         * @return the edit list
         */
        public TaxYearList deriveEditList() {
            /* Build an empty List */
            TaxYearList myList = getEmptyList(ListStyle.EDIT);
            myList.ensureMap();

            /* Store InfoType list */
            myList.theInfoTypeList = getTaxInfoTypes();

            /* Create info List */
            TaxInfoList myTaxInfo = getTaxInfo();
            myList.theInfoList = myTaxInfo.getEmptyList(ListStyle.EDIT);

            /* Loop through the taxYears */
            Iterator<TaxYear> myIterator = iterator();
            while (myIterator.hasNext()) {
                TaxYear myCurr = myIterator.next();

                /* Ignore deleted years */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Build the new linked taxYear and add it to the list */
                TaxYear myYear = new TaxYear(myList, myCurr);
                myList.append(myYear);

                /* Adjust the map */
                myYear.adjustMapForItem();
            }

            /* Return the list */
            return myList;
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
            MoneyWiseData myData = getDataSet();
            TaxYearList myTaxYears = myData.getTaxYears();
            MetisOrderedListIterator<TaxYear> myIterator = myTaxYears.listIterator();

            /* Create a new tax year for the list */
            TaxYear myBase = myIterator.peekLast();
            TaxYear myYear = new TaxYear(myList, myBase);
            myYear.setBase(null);
            myYear.setId(0);

            /* Make sure that it is new */
            myYear.setNewVersion();

            /* Adjust the year and add to list */
            myYear.setTaxYear(new TethysDate(myBase.getTaxYear()));
            myYear.getTaxYear().adjustYear(1);
            myList.add(myYear);

            /* Record the new year */
            myList.theNewYear = myYear;

            /* Return the List */
            return myList;
        }

        @Override
        public TaxYear addCopyItem(final DataItem<?> pTaxYear) {
            /* Can only clone a TaxYear */
            if (!(pTaxYear instanceof TaxYear)) {
                throw new UnsupportedOperationException();
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
            throw new UnsupportedOperationException();
        }

        @Override
        public TaxYear addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws OceanusException {
            /* Create the taxYear */
            TaxYear myYear = new TaxYear(this, pValues);

            /* Check that this YearId has not been previously added */
            if (!isIdUnique(myYear.getId())) {
                myYear.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myYear, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myYear);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myYear);
                    theInfoList.addValuesItem(myValues);
                }
            }

            /* Return it */
            return myYear;
        }

        @Override
        protected TaxYearDataMap allocateDataMap() {
            return new TaxYearDataMap();
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Calculate the date range */
            getDataSet().calculateDateRange();

            /* Resolve links and sort the data */
            resolveDataSetLinks();
            reSort();

            /* Map the data */
            mapData();
        }
    }

    /**
     * The dataMap class.
     */
    protected static class TaxYearDataMap
            extends DataInstanceMap<TaxYear, MoneyWiseDataType, TethysDate>
            implements MetisDataContents {
        @Override
        public void adjustForItem(final TaxYear pItem) {
            /* Adjust year count */
            adjustForItem(pItem, pItem.getTaxYear());
        }

        /**
         * find item by date.
         * @param pDate the date to look up
         * @return the matching item
         */
        public TaxYear findItemByDate(final TethysDate pDate) {
            TethysDate myDate = TethysFiscalYear.UK.normaliseDate(pDate);
            return findItemByKey(myDate);
        }

        /**
         * Check validity of date.
         * @param pDate the date to look up
         * @return true/false
         */
        public boolean validDateCount(final TethysDate pDate) {
            return validKeyCount(pDate);
        }

        /**
         * Check availability of date.
         * @param pDate the key to look up
         * @return true/false
         */
        public boolean availableDate(final TethysDate pDate) {
            return availableKey(pDate);
        }
    }
}
