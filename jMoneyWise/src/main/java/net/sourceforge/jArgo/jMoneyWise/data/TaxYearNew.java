/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.data;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataFormatter;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JRate;
import net.sourceforge.JFinanceApp.data.statics.TaxRegime;
import net.sourceforge.JFinanceApp.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.JFinanceApp.data.statics.TaxYearInfoClass;
import net.sourceforge.JSortedList.OrderedListIterator;

/**
 * New version of TaxYear DataItem utilising TaxYearInfo.
 * @author Tony Washer
 */
public class TaxYearNew extends TaxYearBase {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYearNew.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Value must be positive error text.
     */
    private static final String ERROR_POSITIVE = "Value must be positive";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(TaxYearNew.class.getSimpleName(),
            TaxYearBase.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * TaxInfoSet field Id.
     */
    public static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField("InfoSet");

    /**
     * Allowance field Id.
     */
    public static final JDataField FIELD_ALLOW = FIELD_DEFS.declareEqualityField("Allowance");

    /**
     * Rental field Id.
     */
    public static final JDataField FIELD_RENTAL = FIELD_DEFS.declareEqualityField("RentalAllowance");

    /**
     * LoAgeAllowance field Id.
     */
    public static final JDataField FIELD_LOAGAL = FIELD_DEFS.declareEqualityField("LoAgeAllowance");

    /**
     * HiAgeAllowance field Id.
     */
    public static final JDataField FIELD_HIAGAL = FIELD_DEFS.declareEqualityField("HiAgeAllowance");

    /**
     * LoTaxBand field Id.
     */
    public static final JDataField FIELD_LOBAND = FIELD_DEFS.declareEqualityField("LoTaxBand");

    /**
     * BaseTaxBand field Id.
     */
    public static final JDataField FIELD_BSBAND = FIELD_DEFS.declareEqualityField("BasicTaxBand");

    /**
     * CapitalAllowance field Id.
     */
    public static final JDataField FIELD_CAPALW = FIELD_DEFS.declareEqualityField("CapitalAllowance");

    /**
     * AgeAllowanceLimit field Id.
     */
    public static final JDataField FIELD_AGELMT = FIELD_DEFS.declareEqualityField("AgeAllowanceLimit");

    /**
     * Additional Allowance Limit field Id.
     */
    public static final JDataField FIELD_ADDLMT = FIELD_DEFS.declareEqualityField("AdditionalAllowanceLimit");

    /**
     * Additional Income Boundary field Id.
     */
    public static final JDataField FIELD_ADDBDY = FIELD_DEFS.declareEqualityField("AdditionalIncomeBoundary");

    /**
     * LoTaxRate field Id.
     */
    public static final JDataField FIELD_LOTAX = FIELD_DEFS.declareEqualityField("LoTaxRate");

    /**
     * BasicTaxRate field Id.
     */
    public static final JDataField FIELD_BASTAX = FIELD_DEFS.declareEqualityField("BasicTaxRate");

    /**
     * HiTaxRate field Id.
     */
    public static final JDataField FIELD_HITAX = FIELD_DEFS.declareEqualityField("HiTaxRate");

    /**
     * InterestTaxRate field Id.
     */
    public static final JDataField FIELD_INTTAX = FIELD_DEFS.declareEqualityField("InterestTaxRate");

    /**
     * DividendTaxRate field Id.
     */
    public static final JDataField FIELD_DIVTAX = FIELD_DEFS.declareEqualityField("DividendTaxRate");

    /**
     * HiDividendTaxRate field Id.
     */
    public static final JDataField FIELD_HDVTAX = FIELD_DEFS.declareEqualityField("HiDividendTaxRate");

    /**
     * AdditionalTaxRate field Id.
     */
    public static final JDataField FIELD_ADDTAX = FIELD_DEFS.declareEqualityField("AdditionalTaxRate");

    /**
     * AddDividendTaxRate field Id.
     */
    public static final JDataField FIELD_ADVTAX = FIELD_DEFS.declareEqualityField("AdditionalDivTaxRate");

    /**
     * CapitalTaxRate field Id.
     */
    public static final JDataField FIELD_CAPTAX = FIELD_DEFS.declareEqualityField("CapitalTaxRate");

    /**
     * HiCapitalTaxRate field Id.
     */
    public static final JDataField FIELD_HCPTAX = FIELD_DEFS.declareEqualityField("HiCapitalTaxRate");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet ? theInfoSet : JDataFieldValue.SkipField;
        }
        if (FIELD_ALLOW.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.Allowance) : JDataFieldValue.SkipField;
        }
        if (FIELD_RENTAL.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.RentalAllow) : JDataFieldValue.SkipField;
        }
        if (FIELD_LOAGAL.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.LoAgeAllow) : JDataFieldValue.SkipField;
        }
        if (FIELD_HIAGAL.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.HiAgeAllow) : JDataFieldValue.SkipField;
        }
        if (FIELD_LOBAND.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.LoTaxBand) : JDataFieldValue.SkipField;
        }
        if (FIELD_BSBAND.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.BasicTaxBand)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_CAPALW.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.CapitalAllow)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_AGELMT.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.AgeAllowLimit)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_ADDLMT.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.AddAllowLimit)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_ADDBDY.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.AddIncomeThold)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_LOTAX.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.LoTaxRate) : JDataFieldValue.SkipField;
        }
        if (FIELD_BASTAX.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.BasicTaxRate)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_HITAX.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.HiTaxRate) : JDataFieldValue.SkipField;
        }
        if (FIELD_INTTAX.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.IntTaxRate) : JDataFieldValue.SkipField;
        }
        if (FIELD_DIVTAX.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.DivTaxRate) : JDataFieldValue.SkipField;
        }
        if (FIELD_HDVTAX.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.HiDivTaxRate)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_ADDTAX.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.AddTaxRate) : JDataFieldValue.SkipField;
        }
        if (FIELD_ADVTAX.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.AddDivTaxRate)
                             : JDataFieldValue.SkipField;
        }
        if (FIELD_CAPTAX.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(TaxYearInfoClass.CapTaxRate) : JDataFieldValue.SkipField;
        }
        if (FIELD_HCPTAX.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet.getField(TaxYearInfoClass.HiCapTaxRate)
                             : JDataFieldValue.SkipField;
        }
        return JDataFieldValue.UnknownField;
    }

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * TaxInfoSet.
     */
    private final TaxInfoSet theInfoSet;

    /**
     * Obtain Allowance.
     * @return the allowance
     */
    public JMoney getAllowance() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.Allowance, JMoney.class) : null;
    }

    /**
     * Obtain Rental Allowance.
     * @return the rental allowance
     */
    public JMoney getRentalAllowance() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.RentalAllow, JMoney.class) : null;
    }

    /**
     * Obtain LoTaxBand.
     * @return the tax band
     */
    public JMoney getLoBand() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.LoTaxBand, JMoney.class) : null;
    }

    /**
     * Obtain Basic Tax band.
     * @return the tax band
     */
    public JMoney getBasicBand() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.BasicTaxBand, JMoney.class) : null;
    }

    /**
     * Obtain Capital Allowance.
     * @return the allowance
     */
    public JMoney getCapitalAllow() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.CapitalAllow, JMoney.class) : null;
    }

    /**
     * Obtain LoAge Allowance.
     * @return the allowance
     */
    public JMoney getLoAgeAllow() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.LoAgeAllow, JMoney.class) : null;
    }

    /**
     * Obtain HiAge Allowance.
     * @return the allowance
     */
    public JMoney getHiAgeAllow() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.HiAgeAllow, JMoney.class) : null;
    }

    /**
     * Obtain Age Allowance Limit.
     * @return the limit
     */
    public JMoney getAgeAllowLimit() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.AgeAllowLimit, JMoney.class) : null;
    }

    /**
     * Obtain Additional Allowance Limit.
     * @return the limit
     */
    public JMoney getAddAllowLimit() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.AddAllowLimit, JMoney.class) : null;
    }

    /**
     * Obtain Additional Income Boundary.
     * @return the boundary
     */
    public JMoney getAddIncBound() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.AddIncomeThold, JMoney.class) : null;
    }

    /**
     * Obtain LoTaxRate.
     * @return the rate
     */
    public JRate getLoTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.LoTaxRate, JRate.class) : null;
    }

    /**
     * Obtain BasicTaxRate.
     * @return the rate
     */
    public JRate getBasicTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.BasicTaxRate, JRate.class) : null;
    }

    /**
     * Obtain HiTaxRate.
     * @return the rate
     */
    public JRate getHiTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.HiTaxRate, JRate.class) : null;
    }

    /**
     * Obtain InterestTaxRate.
     * @return the rate
     */
    public JRate getIntTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.IntTaxRate, JRate.class) : null;
    }

    /**
     * Obtain DividendTaxRate.
     * @return the rate
     */
    public JRate getDivTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.DivTaxRate, JRate.class) : null;
    }

    /**
     * Obtain HiDividendTaxRate.
     * @return the rate
     */
    public JRate getHiDivTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.HiDivTaxRate, JRate.class) : null;
    }

    /**
     * Obtain AdditionalTaxRate.
     * @return the rate
     */
    public JRate getAddTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.AddTaxRate, JRate.class) : null;
    }

    /**
     * Obtain AdditionalDividendTaxRate.
     * @return the rate
     */
    public JRate getAddDivTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.AddDivTaxRate, JRate.class) : null;
    }

    /**
     * Obtain CapitalTaxRate.
     * @return the rate
     */
    public JRate getCapTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.CapTaxRate, JRate.class) : null;
    }

    /**
     * Obtain HiCapitalTaxRate.
     * @return the rate
     */
    public JRate getHiCapTaxRate() {
        return hasInfoSet ? theInfoSet.getValue(TaxYearInfoClass.HiCapTaxRate, JRate.class) : null;
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
        setInfoSetValue(TaxYearInfoClass.RentalAllow, pAllowance);
    }

    /**
     * Set a new capital allowance.
     * @param pAllowance the allowance
     * @throws JDataException on error
     */
    public void setCapitalAllow(final JMoney pAllowance) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.CapitalAllow, pAllowance);
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
        setInfoSetValue(TaxYearInfoClass.LoAgeAllow, pLoAgeAllow);
    }

    /**
     * Set a new High Age Allowance.
     * @param pHiAgeAllow the High Age Allowance
     * @throws JDataException on error
     */
    public void setHiAgeAllow(final JMoney pHiAgeAllow) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiAgeAllow, pHiAgeAllow);
    }

    /**
     * Set a new Age Allowance Limit.
     * @param pAgeAllowLimit the Age Allowance Limit
     * @throws JDataException on error
     */
    public void setAgeAllowLimit(final JMoney pAgeAllowLimit) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AgeAllowLimit, pAgeAllowLimit);
    }

    /**
     * Set a new Additional Allowance Limit.
     * @param pAddAllowLimit the Additional Allowance Limit
     * @throws JDataException on error
     */
    public void setAddAllowLimit(final JMoney pAddAllowLimit) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AddAllowLimit, pAddAllowLimit);
    }

    /**
     * Set a new Additional Income Boundary.
     * @param pAddIncBound the Additional Income Boundary
     * @throws JDataException on error
     */
    public void setAddIncBound(final JMoney pAddIncBound) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AddIncomeThold, pAddIncBound);
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
        setInfoSetValue(TaxYearInfoClass.IntTaxRate, pRate);
    }

    /**
     * Set a new Dividend tax rate.
     * @param pRate the Dividend tax rate
     * @throws JDataException on error
     */
    public void setDivTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.DivTaxRate, pRate);
    }

    /**
     * Set a new high dividend tax rate.
     * @param pRate the high dividend tax rate
     * @throws JDataException on error
     */
    public void setHiDivTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiDivTaxRate, pRate);
    }

    /**
     * Set a new additional tax rate.
     * @param pRate the additional tax rate
     * @throws JDataException on error
     */
    public void setAddTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AddTaxRate, pRate);
    }

    /**
     * Set a new additional dividend tax rate.
     * @param pRate the additional dividend tax rate
     * @throws JDataException on error
     */
    public void setAddDivTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.AddDivTaxRate, pRate);
    }

    /**
     * Set a new capital tax rate.
     * @param pRate the capital tax rate
     * @throws JDataException on error
     */
    public void setCapTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.CapTaxRate, pRate);
    }

    /**
     * Set a high capital tax rate.
     * @param pRate the additional dividend tax rate
     * @throws JDataException on error
     */
    public void setHiCapTaxRate(final JRate pRate) throws JDataException {
        setInfoSetValue(TaxYearInfoClass.HiCapTaxRate, pRate);
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
    public TaxYearNew getBase() {
        return (TaxYearNew) super.getBase();
    }

    /**
     * Construct a copy of a TaxYear.
     * @param pList The List to build into
     * @param pTaxYear The TaxYear to copy
     */
    public TaxYearNew(final TaxYearNewList pList,
                      final TaxYearNew pTaxYear) {
        /* Initialise item */
        super(pList, pTaxYear);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new TaxInfoSet(pTaxYear.theInfoSet);
                hasInfoSet = true;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                break;
        }
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param uId the id
     * @param uRegimeId the regime id
     * @param pDate the date
     * @throws JDataException on error
     */
    public TaxYearNew(final TaxYearNewList pList,
                      final Integer uId,
                      final Integer uRegimeId,
                      final Date pDate) throws JDataException {
        /* Initialise item */
        super(pList, uId, uRegimeId, pDate);

        /* Create the InfoSet */
        FinanceData myData = getDataSet();
        theInfoSet = new TaxInfoSet(this, myData.getTaxInfo(), myData.getTaxInfoTypes());
        hasInfoSet = true;
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param uId the id
     * @param pRegime the tax regime
     * @param pDate the date
     * @throws JDataException on error
     */
    public TaxYearNew(final TaxYearNewList pList,
                      final Integer uId,
                      final TaxRegime pRegime,
                      final Date pDate) throws JDataException {
        /* Initialise item */
        super(pList, uId, pRegime, pDate);

        /* Create the InfoSet */
        FinanceData myData = getDataSet();
        theInfoSet = new TaxInfoSet(this, myData.getTaxInfo(), myData.getTaxInfoTypes());
        hasInfoSet = true;
    }

    @Override
    protected void relinkToDataSet() {
        /* Invoke underlying re-link */
        super.relinkToDataSet();

        /* If we have an InfoSet */
        if (hasInfoSet) {
            /* Update to use the new lists */
            FinanceData myData = getDataSet();
            theInfoSet.relinkToDataSet(myData.getTaxInfo(), myData.getTaxInfoTypes());
        }
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        JDateDay myDate = getTaxYear();
        TaxYearNewList myList = (TaxYearNewList) getList();
        TaxYearNew myPrev;

        /* Check underlying fields */
        if (myDate != null) {
            /* The year must be one greater than the preceding element */
            myPrev = myList.peekPrevious(this);
            if ((myPrev != null) && (myDate.getYear() != myPrev.getTaxYear().getYear() + 1)) {
                addError("There can be no gaps in the list", FIELD_TAXYEAR);
            }
        }

        /* The allowance must be non-null */
        if ((getAllowance() == null) || (!getAllowance().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_ALLOW);
        }

        /* The rental allowance must be non-null */
        if ((getRentalAllowance() == null) || (!getRentalAllowance().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_RENTAL);
        }

        /* The loAgeAllow must be non-null */
        if ((getLoAgeAllow() == null) || (!getLoAgeAllow().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_LOAGAL);
        }

        /* The loAgeAllow must be greater than Allowance */
        if ((getLoAgeAllow() != null) && (getAllowance() != null)
                && (getLoAgeAllow().compareTo(getAllowance()) < 0)) {
            addError("Value must be greater than allowance", FIELD_LOAGAL);
        }

        /* The hiAgeAllow must be non-null */
        if ((getHiAgeAllow() == null) || (!getHiAgeAllow().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_HIAGAL);
        }

        /* The hiAgeAllow must be greater than loAgeAllowance */
        if ((getHiAgeAllow() != null) && (getLoAgeAllow() != null)
                && (getHiAgeAllow().compareTo(getLoAgeAllow()) < 0)) {
            addError("Value must be greater than low age allowance", FIELD_HIAGAL);
        }

        /* The ageAllowLimit must be non-null */
        if ((getAgeAllowLimit() == null) || (!getAgeAllowLimit().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_AGELMT);
        }

        /* The capitalAllow must be non-null */
        if ((getCapitalAllow() == null) || (!getCapitalAllow().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_CAPALW);
        }

        /* The loBand must be non-null */
        if ((getLoBand() == null) || (!getLoBand().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_LOBAND);
        }

        /* The basicBand must be non-null */
        if ((getBasicBand() == null) || (!getBasicBand().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_BSBAND);
        }

        /* The loRate must be non-null */
        if ((getLoTaxRate() == null) || (!getLoTaxRate().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_LOTAX);
        }

        /* The basicRate must be non-null */
        if ((getBasicTaxRate() == null) || (!getBasicTaxRate().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_BASTAX);
        }

        /* The hiRate must be non-null */
        if ((getHiTaxRate() == null) || (!getHiTaxRate().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_HITAX);
        }

        /* The intRate must be non-null */
        if ((getIntTaxRate() == null) || (!getIntTaxRate().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_INTTAX);
        }

        /* The divRate must be non-null */
        if ((getDivTaxRate() == null) || (!getDivTaxRate().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_DIVTAX);
        }

        /* The hiDivRate must be non-null */
        if ((getHiDivTaxRate() == null) || (!getHiDivTaxRate().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_HDVTAX);
        }

        /* If the tax regime is additional */
        if ((getTaxRegime() != null) && (getTaxRegime().hasAdditionalTaxBand())) {
            /* The addAllowLimit must be non-null */
            if ((getAddAllowLimit() == null) || (!getAddAllowLimit().isPositive())) {
                addError(ERROR_POSITIVE, FIELD_ADDLMT);
            }

            /* The addIncBound must be non-null */
            if ((getAddIncBound() == null) || (!getAddIncBound().isPositive())) {
                addError(ERROR_POSITIVE, FIELD_ADDBDY);
            }

            /* The addRate must be non-null */
            if ((getAddTaxRate() == null) || (!getAddTaxRate().isPositive())) {
                addError(ERROR_POSITIVE, FIELD_ADDTAX);
            }

            /* The addDivRate must be non-null */
            if ((getAddDivTaxRate() == null) || (!getAddDivTaxRate().isPositive())) {
                addError(ERROR_POSITIVE, FIELD_ADVTAX);
            }
        }

        /* If the tax regime does not have capital gains as income */
        if ((getTaxRegime() != null) && (!getTaxRegime().hasCapitalGainsAsIncome())) {
            /* The capitalRate must be non-null */
            if ((getCapTaxRate() == null) || (!getCapTaxRate().isPositive())) {
                addError(ERROR_POSITIVE, FIELD_CAPTAX);
            }

            /* The hiCapTaxRate must be positive */
            if ((getHiCapTaxRate() != null) && (!getHiCapTaxRate().isPositive())) {
                addError(ERROR_POSITIVE, FIELD_HCPTAX);
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * The Tax Year List class.
     */
    public static class TaxYearNewList extends TaxYearBaseList<TaxYearNew> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxYearNewList.class.getSimpleName(),
                TaxYearBaseList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The NewYear.
         */
        private TaxYearNew theNewYear = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Obtain the new year.
         * @return the new year
         */
        public TaxYearNew getNewYear() {
            return theNewYear;
        }

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         */
        public TaxYearNewList(final FinanceData pData) {
            super(pData, TaxYearNew.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxYearNewList(final TaxYearNewList pSource) {
            super(pSource);
        }

        @Override
        protected TaxYearNewList getEmptyList() {
            return new TaxYearNewList(this);
        }

        @Override
        public TaxYearNewList cloneList(final DataSet<?> pDataSet) {
            return (TaxYearNewList) super.cloneList(pDataSet);
        }

        @Override
        public TaxYearNewList deriveList(final ListStyle pStyle) {
            return (TaxYearNewList) super.deriveList(pStyle);
        }

        @Override
        public TaxYearNewList deriveDifferences(final DataList<TaxYearNew> pOld) {
            return (TaxYearNewList) super.deriveDifferences(pOld);
        }

        /**
         * Construct an edit extract for a TaxYear.
         * @param pTaxYear the tax year
         * @return the edit Extract
         */
        public TaxYearNewList deriveEditList(final TaxYearNew pTaxYear) {
            /* Build an empty List */
            TaxYearNewList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Create a new tax year based on the passed tax year */
            TaxYearNew myYear = new TaxYearNew(myList, pTaxYear);
            myList.add(myYear);

            /* Return the List */
            return myList;
        }

        /**
         * Create a new year based on the last year.
         * @return the new list
         */
        public TaxYearNewList deriveNewEditList() {
            /* Build an empty List */
            TaxYearNewList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Access the existing tax years */
            FinanceData myData = getDataSet();
            TaxYearNewList myTaxYears = myData.getNewTaxYears();
            OrderedListIterator<TaxYearNew> myIterator = myTaxYears.listIterator();

            /* Create a new tax year for the list */
            TaxYearNew myBase = myIterator.peekLast();
            TaxYearNew myYear = new TaxYearNew(myList, myBase);
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
        public TaxYearNew addCopyItem(final DataItem pTaxYear) {
            /* Can only clone a TaxYear */
            if (!(pTaxYear instanceof TaxYear)) {
                return null;
            }

            TaxYearNew myYear = new TaxYearNew(this, (TaxYearNew) pTaxYear);
            add(myYear);
            return myYear;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public TaxYearNew addNewItem() {
            return null;
        }

        /**
         * Allow a tax parameter to be added.
         * @param uId the id
         * @param uRegimeId the regime id
         * @param pDate the date
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uRegimeId,
                                  final Date pDate) throws JDataException {
            /* Create the tax year */
            TaxYearNew myTaxYear = new TaxYearNew(this, uId, uRegimeId, pDate);

            /* Check that this TaxYearId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myTaxYear, "Duplicate TaxYearId");
            }

            /* Check that this TaxYear has not been previously added */
            if (findTaxYearForDate(new JDateDay(pDate)) != null) {
                throw new JDataException(ExceptionClass.DATA, myTaxYear, "Duplicate TaxYear");
            }

            /* Validate the tax year */
            myTaxYear.validate();

            /* Handle validation failure */
            if (myTaxYear.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxYear, "Failed validation");
            }

            /* Add the TaxYear to the end of the list */
            append(myTaxYear);
        }

        /**
         * Allow a tax parameter to be added.
         * @param uId the id
         * @param pRegime the regime
         * @param pDate the date
         * @return the taxYear
         * @throws JDataException on error
         */
        public TaxYearNew addOpenItem(final Integer uId,
                                      final String pRegime,
                                      final Date pDate) throws JDataException {
            /* Look up the Tax Regime */
            FinanceData myData = getDataSet();
            JDataFormatter myFormatter = myData.getDataFormatter();
            TaxRegimeList myList = myData.getTaxRegimes();
            TaxRegime myTaxRegime = myList.findItemByName(pRegime);
            if (myTaxRegime == null) {
                throw new JDataException(ExceptionClass.DATA, "TaxYear on <"
                        + myFormatter.formatObject(new JDateDay(pDate)) + "> has invalid TaxRegime <"
                        + pRegime + ">");
            }

            /* Create the tax year */
            TaxYearNew myTaxYear = new TaxYearNew(this, uId, myTaxRegime, pDate);

            /* Check that this TaxYear has not been previously added */
            if (findTaxYearForDate(new JDateDay(pDate)) != null) {
                throw new JDataException(ExceptionClass.DATA, myTaxYear, "Duplicate TaxYear");
            }

            /* Validate the tax year */
            myTaxYear.validate();

            /* Handle validation failure */
            if (myTaxYear.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxYear, "Failed validation");
            }

            /* Add the TaxYear to the end of the list */
            append(myTaxYear);
            return myTaxYear;
        }
    }
}
