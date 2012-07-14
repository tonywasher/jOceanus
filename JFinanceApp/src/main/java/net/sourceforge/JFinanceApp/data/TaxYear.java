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

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JFinanceApp.data.TaxRegime.TaxRegimeList;
import net.sourceforge.JSortedList.OrderedListIterator;

/**
 * Tax Year Class representing taxation parameters for a tax year.
 * @author Tony Washer
 */
public class TaxYear extends DataItem implements Comparable<TaxYear> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = TaxYear.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Value must be positive error text.
     */
    private static final String ERROR_POSITIVE = "Value must be positive";

    /**
     * TaxYear end of month day.
     */
    public static final int END_OF_MONTH_DAY = 5;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * TaxYear field Id.
     */
    public static final JDataField FIELD_TAXYEAR = FIELD_DEFS.declareEqualityValueField("TaxYear");

    /**
     * TaxRegime field Id.
     */
    public static final JDataField FIELD_REGIME = FIELD_DEFS.declareEqualityValueField("Regime");

    /**
     * Rental field Id.
     */
    public static final JDataField FIELD_RENTAL = FIELD_DEFS.declareEqualityValueField("RentalAllowance");

    /**
     * Allowance field Id.
     */
    public static final JDataField FIELD_ALLOW = FIELD_DEFS.declareEqualityValueField("Allowance");

    /**
     * LoAgeAllowance field Id.
     */
    public static final JDataField FIELD_LOAGAL = FIELD_DEFS.declareEqualityValueField("LoAgeAllowance");

    /**
     * HiAgeAllowance field Id.
     */
    public static final JDataField FIELD_HIAGAL = FIELD_DEFS.declareEqualityValueField("HiAgeAllowance");

    /**
     * LoTaxBand field Id.
     */
    public static final JDataField FIELD_LOBAND = FIELD_DEFS.declareEqualityValueField("LoTaxBand");

    /**
     * BaseTaxBand field Id.
     */
    public static final JDataField FIELD_BSBAND = FIELD_DEFS.declareEqualityValueField("BasicTaxBand");

    /**
     * CapitalAllowance field Id.
     */
    public static final JDataField FIELD_CAPALW = FIELD_DEFS.declareEqualityValueField("CapitalAllowance");

    /**
     * AgeAllowanceLimit field Id.
     */
    public static final JDataField FIELD_AGELMT = FIELD_DEFS.declareEqualityValueField("AgeAllowanceLimit");

    /**
     * Additional Allowance Limit field Id.
     */
    public static final JDataField FIELD_ADDLMT = FIELD_DEFS
            .declareEqualityValueField("AdditionalAllowanceLimit");

    /**
     * Additional Income Boundary field Id.
     */
    public static final JDataField FIELD_ADDBDY = FIELD_DEFS
            .declareEqualityValueField("AdditionalIncomeBoundary");

    /**
     * LoTaxRate field Id.
     */
    public static final JDataField FIELD_LOTAX = FIELD_DEFS.declareEqualityValueField("LoTaxRate");

    /**
     * BasicTaxRate field Id.
     */
    public static final JDataField FIELD_BASTAX = FIELD_DEFS.declareEqualityValueField("BasicTaxRate");

    /**
     * HiTaxRate field Id.
     */
    public static final JDataField FIELD_HITAX = FIELD_DEFS.declareEqualityValueField("HiTaxRate");

    /**
     * InterestTaxRate field Id.
     */
    public static final JDataField FIELD_INTTAX = FIELD_DEFS.declareEqualityValueField("InterestTaxRate");

    /**
     * DividendTaxRate field Id.
     */
    public static final JDataField FIELD_DIVTAX = FIELD_DEFS.declareEqualityValueField("DividendTaxRate");

    /**
     * HiDividendTaxRate field Id.
     */
    public static final JDataField FIELD_HDVTAX = FIELD_DEFS.declareEqualityValueField("HiDividendTaxRate");

    /**
     * AdditionalTaxRate field Id.
     */
    public static final JDataField FIELD_ADDTAX = FIELD_DEFS.declareEqualityValueField("AdditionalTaxRate");

    /**
     * AddDividendTaxRate field Id.
     */
    public static final JDataField FIELD_ADVTAX = FIELD_DEFS
            .declareEqualityValueField("AdditionalDivTaxRate");

    /**
     * CapitalTaxRate field Id.
     */
    public static final JDataField FIELD_CAPTAX = FIELD_DEFS.declareEqualityValueField("CapitalTaxRate");

    /**
     * HiCapitalTaxRate field Id.
     */
    public static final JDataField FIELD_HCPTAX = FIELD_DEFS.declareEqualityValueField("HiCapitalTaxRate");

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public String toString() {
        return Integer.toString(getTaxYear().getYear());
    }

    /**
     * Obtain TaxYear.
     * @return the taxYear date
     */
    public DateDay getTaxYear() {
        return getTaxYear(getValueSet());
    }

    /**
     * Obtain TaxRegime.
     * @return the taxRegime
     */
    public TaxRegime getTaxRegime() {
        return getTaxRegime(getValueSet());
    }

    /**
     * Obtain Allowance.
     * @return the allowance
     */
    public Money getAllowance() {
        return getAllowance(getValueSet());
    }

    /**
     * Obtain Rental Allowance.
     * @return the rental allowance
     */
    public Money getRentalAllowance() {
        return getRentalAllowance(getValueSet());
    }

    /**
     * Obtain LoTaxBand.
     * @return the tax band
     */
    public Money getLoBand() {
        return getLoBand(getValueSet());
    }

    /**
     * Obtain Basic Tax band.
     * @return the tax band
     */
    public Money getBasicBand() {
        return getBasicBand(getValueSet());
    }

    /**
     * Obtain Capital Allowance.
     * @return the allowance
     */
    public Money getCapitalAllow() {
        return getCapitalAllow(getValueSet());
    }

    /**
     * Obtain LoAge Allowance.
     * @return the allowance
     */
    public Money getLoAgeAllow() {
        return getLoAgeAllow(getValueSet());
    }

    /**
     * Obtain HiAge Allowance.
     * @return the allowance
     */
    public Money getHiAgeAllow() {
        return getHiAgeAllow(getValueSet());
    }

    /**
     * Obtain Age Allowance Limit.
     * @return the limit
     */
    public Money getAgeAllowLimit() {
        return getAgeAllowLimit(getValueSet());
    }

    /**
     * Obtain Additional Allowance Limit.
     * @return the limit
     */
    public Money getAddAllowLimit() {
        return getAddAllowLimit(getValueSet());
    }

    /**
     * Obtain Additional Income Boundary.
     * @return the boundary
     */
    public Money getAddIncBound() {
        return getAddIncBound(getValueSet());
    }

    /**
     * Obtain LoTaxRate.
     * @return the rate
     */
    public Rate getLoTaxRate() {
        return getLoTaxRate(getValueSet());
    }

    /**
     * Obtain BasicTaxRate.
     * @return the rate
     */
    public Rate getBasicTaxRate() {
        return getBasicTaxRate(getValueSet());
    }

    /**
     * Obtain HiTaxRate.
     * @return the rate
     */
    public Rate getHiTaxRate() {
        return getHiTaxRate(getValueSet());
    }

    /**
     * Obtain InterestTaxRate.
     * @return the rate
     */
    public Rate getIntTaxRate() {
        return getIntTaxRate(getValueSet());
    }

    /**
     * Obtain DividendTaxRate.
     * @return the rate
     */
    public Rate getDivTaxRate() {
        return getDivTaxRate(getValueSet());
    }

    /**
     * Obtain HiDividendTaxRate.
     * @return the rate
     */
    public Rate getHiDivTaxRate() {
        return getHiDivTaxRate(getValueSet());
    }

    /**
     * Obtain AdditionalTaxRate.
     * @return the rate
     */
    public Rate getAddTaxRate() {
        return getAddTaxRate(getValueSet());
    }

    /**
     * Obtain AdditionalDividendTaxRate.
     * @return the rate
     */
    public Rate getAddDivTaxRate() {
        return getAddDivTaxRate(getValueSet());
    }

    /**
     * Obtain CapitalTaxRate.
     * @return the rate
     */
    public Rate getCapTaxRate() {
        return getCapTaxRate(getValueSet());
    }

    /**
     * Obtain HiCapitalTaxRate.
     * @return the rate
     */
    public Rate getHiCapTaxRate() {
        return getHiCapTaxRate(getValueSet());
    }

    /**
     * Obtain TaxYear date.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static DateDay getTaxYear(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXYEAR, DateDay.class);
    }

    /**
     * Obtain TaxRegime.
     * @param pValueSet the valueSet
     * @return the regime
     */
    public static TaxRegime getTaxRegime(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_REGIME, TaxRegime.class);
    }

    /**
     * Obtain Allowance.
     * @param pValueSet the valueSet
     * @return the allowance
     */
    public static Money getAllowance(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ALLOW, Money.class);
    }

    /**
     * Obtain RentalAllowance.
     * @param pValueSet the valueSet
     * @return the allowance
     */
    public static Money getRentalAllowance(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RENTAL, Money.class);
    }

    /**
     * Obtain LoTaxBand.
     * @param pValueSet the valueSet
     * @return the band
     */
    public static Money getLoBand(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_LOBAND, Money.class);
    }

    /**
     * Obtain BasicTaxBand.
     * @param pValueSet the valueSet
     * @return the allowance
     */
    public static Money getBasicBand(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BSBAND, Money.class);
    }

    /**
     * Obtain Capital Allowance.
     * @param pValueSet the valueSet
     * @return the allowance
     */
    public static Money getCapitalAllow(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CAPALW, Money.class);
    }

    /**
     * Obtain LoAge Allowance.
     * @param pValueSet the valueSet
     * @return the allowance
     */
    public static Money getLoAgeAllow(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_LOAGAL, Money.class);
    }

    /**
     * Obtain HiAge Allowance.
     * @param pValueSet the valueSet
     * @return the allowance
     */
    public static Money getHiAgeAllow(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HIAGAL, Money.class);
    }

    /**
     * Obtain Age Allowance Limit.
     * @param pValueSet the valueSet
     * @return the limit
     */
    public static Money getAgeAllowLimit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_AGELMT, Money.class);
    }

    /**
     * Obtain Additional Allowance Limit.
     * @param pValueSet the valueSet
     * @return the limit
     */
    public static Money getAddAllowLimit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ADDLMT, Money.class);
    }

    /**
     * Obtain Additional Income Boundary.
     * @param pValueSet the valueSet
     * @return the boundary
     */
    public static Money getAddIncBound(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ADDBDY, Money.class);
    }

    /**
     * Obtain LoTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getLoTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_LOTAX, Rate.class);
    }

    /**
     * Obtain BasicTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getBasicTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_BASTAX, Rate.class);
    }

    /**
     * Obtain HiTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getHiTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HITAX, Rate.class);
    }

    /**
     * Obtain InterestTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getIntTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_INTTAX, Rate.class);
    }

    /**
     * Obtain DividendTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getDivTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DIVTAX, Rate.class);
    }

    /**
     * Obtain HiDividendTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getHiDivTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HDVTAX, Rate.class);
    }

    /**
     * Obtain AdditionalTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getAddTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ADDTAX, Rate.class);
    }

    /**
     * Obtain AdditionalDividendTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getAddDivTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ADVTAX, Rate.class);
    }

    /**
     * Obtain CapitalTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getCapTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CAPTAX, Rate.class);
    }

    /**
     * Obtain HiCapitalTaxRate.
     * @param pValueSet the valueSet
     * @return the rate
     */
    public static Rate getHiCapTaxRate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_HCPTAX, Rate.class);
    }

    /**
     * Do we have a low Salary Band?
     * @return true/false
     */
    public boolean hasLoSalaryBand() {
        return getTaxRegime().hasLoSalaryBand();
    }

    /**
     * Do we have a additional Tax Band?
     * @return true/false
     */
    public boolean hasAdditionalTaxBand() {
        return getTaxRegime().hasAdditionalTaxBand();
    }

    /**
     * Do we treat Capital Gains as Income?
     * @return true/false
     */
    public boolean hasCapitalGainsAsIncome() {
        return getTaxRegime().hasCapitalGainsAsIncome();
    }

    /**
     * Set Tax Year value.
     * @param pValue the value
     */
    private void setValueTaxYear(final DateDay pValue) {
        getValueSet().setValue(FIELD_TAXYEAR, pValue);
    }

    /**
     * Set Tax Regime value.
     * @param pValue the value
     */
    private void setValueTaxRegime(final TaxRegime pValue) {
        getValueSet().setValue(FIELD_REGIME, pValue);
    }

    /**
     * Set Tax Regime id.
     * @param pId the id
     */
    private void setValueTaxRegime(final Integer pId) {
        getValueSet().setValue(FIELD_REGIME, pId);
    }

    /**
     * Set Allowance.
     * @param pValue the value
     */
    private void setValueAllowance(final Money pValue) {
        getValueSet().setValue(FIELD_ALLOW, pValue);
    }

    /**
     * Set Rental Allowance.
     * @param pValue the value
     */
    private void setValueRental(final Money pValue) {
        getValueSet().setValue(FIELD_RENTAL, pValue);
    }

    /**
     * Set LoTaxBand.
     * @param pValue the value
     */
    private void setValueLoBand(final Money pValue) {
        getValueSet().setValue(FIELD_LOBAND, pValue);
    }

    /**
     * Set BasicTaxBand.
     * @param pValue the value
     */
    private void setValueBasicBand(final Money pValue) {
        getValueSet().setValue(FIELD_BSBAND, pValue);
    }

    /**
     * Set Capital Allowance.
     * @param pValue the value
     */
    private void setValueCapitalAllowance(final Money pValue) {
        getValueSet().setValue(FIELD_CAPALW, pValue);
    }

    /**
     * Set LoAge Allowance.
     * @param pValue the value
     */
    private void setValueLoAgeAllowance(final Money pValue) {
        getValueSet().setValue(FIELD_LOAGAL, pValue);
    }

    /**
     * Set HiAge Allowance.
     * @param pValue the value
     */
    private void setValueHiAgeAllowance(final Money pValue) {
        getValueSet().setValue(FIELD_HIAGAL, pValue);
    }

    /**
     * Set Age Allowance Limit.
     * @param pValue the value
     */
    private void setValueAgeAllowLimit(final Money pValue) {
        getValueSet().setValue(FIELD_AGELMT, pValue);
    }

    /**
     * Set Additional Allowance Limit.
     * @param pValue the value
     */
    private void setValueAddAllowLimit(final Money pValue) {
        getValueSet().setValue(FIELD_ADDLMT, pValue);
    }

    /**
     * Set Additional Income Boundary.
     * @param pValue the value
     */
    private void setValueAddIncBound(final Money pValue) {
        getValueSet().setValue(FIELD_ADDBDY, pValue);
    }

    /**
     * Set LoTaxRate.
     * @param pValue the value
     */
    private void setValueLoTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_LOTAX, pValue);
    }

    /**
     * Set BasicTaxRate.
     * @param pValue the value
     */
    private void setValueBasicTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_BASTAX, pValue);
    }

    /**
     * Set HiTaxRate.
     * @param pValue the value
     */
    private void setValueHiTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_HITAX, pValue);
    }

    /**
     * Set InterestTaxRate.
     * @param pValue the value
     */
    private void setValueIntTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_INTTAX, pValue);
    }

    /**
     * Set DividendTaxRate.
     * @param pValue the value
     */
    private void setValueDivTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_DIVTAX, pValue);
    }

    /**
     * Set HiDividendTaxRate.
     * @param pValue the value
     */
    private void setValueHiDivTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_HDVTAX, pValue);
    }

    /**
     * Set AdditionalLoTaxRate.
     * @param pValue the value
     */
    private void setValueAddTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_ADDTAX, pValue);
    }

    /**
     * Set AdditionalDividendTaxRate.
     * @param pValue the value
     */
    private void setValueAddDivTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_ADVTAX, pValue);
    }

    /**
     * Set CapitalTaxRate.
     * @param pValue the value
     */
    private void setValueCapTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_CAPTAX, pValue);
    }

    /**
     * Set HiCapitalTaxRate.
     * @param pValue the value
     */
    private void setValueHiCapTaxRate(final Rate pValue) {
        getValueSet().setValue(FIELD_HCPTAX, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
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
        super(pList, pTaxYear);
    }

    /**
     * Standard constructor.
     * @param pList the list
     * @param uId the id
     * @param uRegimeId the regime id
     * @param pDate the date
     * @param pAllowance the allowance
     * @param pRentalAllow the rental allowance
     * @param pLoAgeAllow the low Age allowance
     * @param pHiAgeAllow the high age allowance
     * @param pCapAllow the capital allowance
     * @param pAgeAllowLimit the age allowance limit
     * @param pAddAllowLimit the additional allowance limit
     * @param pLoTaxBand the low tax band
     * @param pBasicTaxBand the basic tax band
     * @param pAddIncBound the additional income boundary
     * @param pLoTaxRate the low tax rate
     * @param pBasicTaxRate the basic tax rate
     * @param pHiTaxRate the high tax rate
     * @param pIntTaxRate the additional tax rate
     * @param pDivTaxRate the dividend tax rate
     * @param pHiDivTaxRate the high dividend tax rate
     * @param pAddTaxRate the additional tax rate
     * @param pAddDivTaxRate the addition dividend tax rate
     * @param pCapTaxRate the capital tax rate
     * @param pHiCapTaxRate the high capital tax rate
     * @throws JDataException on error
     */
    private TaxYear(final TaxYearList pList,
                    final int uId,
                    final int uRegimeId,
                    final Date pDate,
                    final String pAllowance,
                    final String pRentalAllow,
                    final String pLoAgeAllow,
                    final String pHiAgeAllow,
                    final String pCapAllow,
                    final String pAgeAllowLimit,
                    final String pAddAllowLimit,
                    final String pLoTaxBand,
                    final String pBasicTaxBand,
                    final String pAddIncBound,
                    final String pLoTaxRate,
                    final String pBasicTaxRate,
                    final String pHiTaxRate,
                    final String pIntTaxRate,
                    final String pDivTaxRate,
                    final String pHiDivTaxRate,
                    final String pAddTaxRate,
                    final String pAddDivTaxRate,
                    final String pCapTaxRate,
                    final String pHiCapTaxRate) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Record the Id */
            setValueTaxRegime(uRegimeId);
            setValueTaxYear(new DateDay(pDate));

            /* Look up the Regime */
            FinanceData myData = getDataSet();
            TaxRegimeList myRegimes = myData.getTaxRegimes();
            TaxRegime myRegime = myRegimes.findItemById(uRegimeId);
            if (myRegime == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Tax Regime Id");
            }
            setValueTaxRegime(myRegime);

            /* Record the allowances */
            Money myMoney = Money.parseString(pAllowance);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Allowance: " + pAllowance);
            }
            setValueAllowance(myMoney);
            myMoney = Money.parseString(pLoTaxBand);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Low Tax Band: " + pLoTaxBand);
            }
            setValueLoBand(myMoney);
            myMoney = Money.parseString(pBasicTaxBand);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Basic Tax Band: "
                        + pBasicTaxBand);
            }
            setValueBasicBand(myMoney);
            myMoney = Money.parseString(pRentalAllow);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Rental Allowance: "
                        + pRentalAllow);
            }
            setValueRental(myMoney);
            myMoney = Money.parseString(pLoAgeAllow);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Low Age Allowance: "
                        + pLoAgeAllow);
            }
            setValueLoAgeAllowance(myMoney);
            myMoney = Money.parseString(pHiAgeAllow);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid High Age Allowance: "
                        + pHiAgeAllow);
            }
            setValueHiAgeAllowance(myMoney);
            myMoney = Money.parseString(pCapAllow);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Capital Allowance: "
                        + pHiAgeAllow);
            }
            setValueCapitalAllowance(myMoney);
            myMoney = Money.parseString(pAgeAllowLimit);
            if (myMoney == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Age Allowance Limit: "
                        + pAgeAllowLimit);
            }
            setValueAgeAllowLimit(myMoney);
            if (pAddAllowLimit != null) {
                myMoney = Money.parseString(pAddAllowLimit);
                if (myMoney == null) {
                    throw new JDataException(ExceptionClass.DATA, this,
                            "Invalid Additional Allowance Limit: " + pAddAllowLimit);
                }
                setValueAddAllowLimit(myMoney);
            }
            if (pAddIncBound != null) {
                myMoney = Money.parseString(pAddIncBound);
                if (myMoney == null) {
                    throw new JDataException(ExceptionClass.DATA, this,
                            "Invalid Additional Income Boundary: " + pAddIncBound);
                }
                setValueAddIncBound(myMoney);
            }

            /* Record the rates */
            Rate myRate = Rate.parseString(pLoTaxRate);
            if (myRate == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Low Tax Rate: " + pLoTaxRate);
            }
            setValueLoTaxRate(myRate);
            myRate = Rate.parseString(pBasicTaxRate);
            if (myRate == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Basic Tax Rate: "
                        + pBasicTaxRate);
            }
            setValueBasicTaxRate(myRate);
            myRate = Rate.parseString(pHiTaxRate);
            if (myRate == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid High Tax Rate: " + pHiTaxRate);
            }
            setValueHiTaxRate(myRate);
            myRate = Rate.parseString(pIntTaxRate);
            if (myRate == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Int Tax Rate: " + pIntTaxRate);
            }
            setValueIntTaxRate(myRate);
            myRate = Rate.parseString(pDivTaxRate);
            if (myRate == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Div Tax Rate: " + pDivTaxRate);
            }
            setValueDivTaxRate(myRate);
            myRate = Rate.parseString(pHiDivTaxRate);
            if (myRate == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid High Div Tax Rate: "
                        + pHiDivTaxRate);
            }
            setValueHiDivTaxRate(myRate);
            if (pAddTaxRate != null) {
                myRate = Rate.parseString(pAddTaxRate);
                if (myRate == null) {
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Additional Tax Rate: "
                            + pAddTaxRate);
                }
                setValueAddTaxRate(myRate);
            }
            if (pAddDivTaxRate != null) {
                myRate = Rate.parseString(pAddDivTaxRate);
                if (myRate == null) {
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Additional Div Tax Rate: "
                            + pAddDivTaxRate);
                }
                setValueAddDivTaxRate(myRate);
            }
            if (pCapTaxRate != null) {
                myRate = Rate.parseString(pCapTaxRate);
                if (myRate == null) {
                    throw new JDataException(ExceptionClass.DATA, this, "Invalid Capital Gains Tax Rate: "
                            + pCapTaxRate);
                }
                setValueCapTaxRate(myRate);
            }
            if (pHiCapTaxRate != null) {
                myRate = Rate.parseString(pHiCapTaxRate);
                if (myRate == null) {
                    throw new JDataException(ExceptionClass.DATA, this,
                            "Invalid High Capital Gains Tax Rate: " + pHiCapTaxRate);
                }
                setValueHiCapTaxRate(myRate);
            }
            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Standard constructor for a newly inserted tax year.
     * @param pList the list
     */
    public TaxYear(final TaxYearList pList) {
        super(pList, 0);
    }

    @Override
    public int compareTo(final TaxYear pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If the dates differ */
        int iDiff = Difference.compareObject(getTaxYear(), pThat.getTaxYear());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    protected void relinkToDataSet() {
        FinanceData myData = getDataSet();
        TaxRegimeList myRegimes = myData.getTaxRegimes();

        /* Update to use the local copy of the TaxRegimes */
        TaxRegime myRegime = getTaxRegime();
        TaxRegime myNewReg = myRegimes.findItemById(myRegime.getId());
        setValueTaxRegime(myNewReg);
    }

    /**
     * Validate the taxYear.
     */
    @Override
    public void validate() {
        DateDay myDate = getTaxYear();
        TaxYearList myList = (TaxYearList) getList();
        TaxYear myPrev;

        /* The date must not be null */
        if (myDate == null) {
            addError("Null date is not allowed", FIELD_TAXYEAR);

            /* else we have a date */
        } else {
            /* The date must be unique */
            if (myList.countInstances(myDate) > 1) {
                addError("Date must be unique", FIELD_TAXYEAR);
            }

            /* The day and month must be 5th April */
            if ((myDate.getDay() != END_OF_MONTH_DAY) || (myDate.getMonth() != Calendar.APRIL)) {
                addError("Date must be 5th April", FIELD_TAXYEAR);
            }

            /* The year must be one greater than the preceding element */
            myPrev = myList.peekPrevious(this);
            if ((myPrev != null) && (myDate.getYear() != myPrev.getTaxYear().getYear() + 1)) {
                addError("There can be no gaps in the list", FIELD_TAXYEAR);
            }
        }

        /* TaxRegime must be non-null */
        if (getTaxRegime() == null) {
            addError("TaxRegime must be non-null", FIELD_REGIME);
        } else if (!getTaxRegime().getEnabled()) {
            addError("TaxRegime must be enabled", FIELD_REGIME);
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
                && (getLoAgeAllow().getValue() < getAllowance().getValue())) {
            addError("Value must be greater than allowance", FIELD_LOAGAL);
        }

        /* The hiAgeAllow must be non-null */
        if ((getHiAgeAllow() == null) || (!getHiAgeAllow().isPositive())) {
            addError(ERROR_POSITIVE, FIELD_HIAGAL);
        }

        /* The hiAgeAllow must be greater than loAgeAllowance */
        if ((getHiAgeAllow() != null) && (getLoAgeAllow() != null)
                && (getHiAgeAllow().getValue() < getLoAgeAllow().getValue())) {
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
     * Extract the date range represented by the tax years.
     * @return the range of tax years
     */
    public DateDayRange getRange() {
        DateDay myStart;
        DateDay myEnd;
        DateDayRange myRange;

        /* Access start date */
        myStart = new DateDay(getTaxYear());

        /* Move back to start of year */
        myStart.adjustYear(-1);
        myStart.adjustDay(1);

        /* Access last date */
        myEnd = getTaxYear();

        /* Create the range */
        myRange = new DateDayRange(myStart, myEnd);

        /* Return the range */
        return myRange;
    }

    /**
     * Set a new tax regime.
     * @param pTaxYear the TaxYear
     */
    protected void setTaxYear(final DateDay pTaxYear) {
        setValueTaxYear(pTaxYear);
    }

    /**
     * Set a new tax regime.
     * @param pTaxRegime the TaxRegime
     */
    public void setTaxRegime(final TaxRegime pTaxRegime) {
        setValueTaxRegime(pTaxRegime);
    }

    /**
     * Set a new allowance.
     * @param pAllowance the allowance
     */
    public void setAllowance(final Money pAllowance) {
        setValueAllowance(pAllowance);
    }

    /**
     * Set a new rental allowance.
     * @param pAllowance the allowance
     */
    public void setRentalAllowance(final Money pAllowance) {
        setValueRental(pAllowance);
    }

    /**
     * Set a new capital allowance.
     * @param pAllowance the allowance
     */
    public void setCapitalAllow(final Money pAllowance) {
        setValueCapitalAllowance(pAllowance);
    }

    /**
     * Set a new Low Tax Band.
     * @param pLoBand the Low Tax Band
     */
    public void setLoBand(final Money pLoBand) {
        setValueLoBand(pLoBand);
    }

    /**
     * Set a new Basic Tax Band.
     * @param pBasicBand the Basic Tax Band
     */
    public void setBasicBand(final Money pBasicBand) {
        setValueBasicBand(pBasicBand);
    }

    /**
     * Set a new Low Age Allowance.
     * @param pLoAgeAllow the Low Age Allowance
     */
    public void setLoAgeAllow(final Money pLoAgeAllow) {
        setValueLoAgeAllowance(pLoAgeAllow);
    }

    /**
     * Set a new High Age Allowance.
     * @param pHiAgeAllow the High Age Allowance
     */
    public void setHiAgeAllow(final Money pHiAgeAllow) {
        setValueHiAgeAllowance(pHiAgeAllow);
    }

    /**
     * Set a new Age Allowance Limit.
     * @param pAgeAllowLimit the Age Allowance Limit
     */
    public void setAgeAllowLimit(final Money pAgeAllowLimit) {
        setValueAgeAllowLimit(pAgeAllowLimit);
    }

    /**
     * Set a new Additional Allowance Limit.
     * @param pAddAllowLimit the Additional Allowance Limit
     */
    public void setAddAllowLimit(final Money pAddAllowLimit) {
        setValueAddAllowLimit(pAddAllowLimit);
    }

    /**
     * Set a new Additional Income Boundary.
     * @param pAddIncBound the Additional Income Boundary
     */
    public void setAddIncBound(final Money pAddIncBound) {
        setValueAddIncBound(pAddIncBound);
    }

    /**
     * Set a new Low Tax Rate.
     * @param pRate the Low Tax Rate
     */
    public void setLoTaxRate(final Rate pRate) {
        setValueLoTaxRate(pRate);
    }

    /**
     * Set a new Basic tax rate.
     * @param pRate the Basic tax rate
     */
    public void setBasicTaxRate(final Rate pRate) {
        setValueBasicTaxRate(pRate);
    }

    /**
     * Set a new high tax rate.
     * @param pRate the high tax rate
     */
    public void setHiTaxRate(final Rate pRate) {
        setValueHiTaxRate(pRate);
    }

    /**
     * Set a new Interest Tax Rate.
     * @param pRate the Interest Tax Rate
     */
    public void setIntTaxRate(final Rate pRate) {
        setValueIntTaxRate(pRate);
    }

    /**
     * Set a new Dividend tax rate.
     * @param pRate the Dividend tax rate
     */
    public void setDivTaxRate(final Rate pRate) {
        setValueDivTaxRate(pRate);
    }

    /**
     * Set a new high dividend tax rate.
     * @param pRate the high dividend tax rate
     */
    public void setHiDivTaxRate(final Rate pRate) {
        setValueHiDivTaxRate(pRate);
    }

    /**
     * Set a new additional tax rate.
     * @param pRate the additional tax rate
     */
    public void setAddTaxRate(final Rate pRate) {
        setValueAddTaxRate(pRate);
    }

    /**
     * Set a new additional dividend tax rate.
     * @param pRate the additional dividend tax rate
     */
    public void setAddDivTaxRate(final Rate pRate) {
        setValueAddDivTaxRate(pRate);
    }

    /**
     * Set a new capital tax rate.
     * @param pRate the capital tax rate
     */
    public void setCapTaxRate(final Rate pRate) {
        setValueCapTaxRate(pRate);
    }

    /**
     * Set a high capital tax rate.
     * @param pRate the additional dividend tax rate
     */
    public void setHiCapTaxRate(final Rate pRate) {
        setValueHiCapTaxRate(pRate);
    }

    /**
     * Update taxYear from a taxYear extract.
     * @param pTaxYear the changed taxYear
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pTaxYear) {
        /* Can only update from TaxYear */
        if (!(pTaxYear instanceof TaxYear)) {
            return false;
        }

        TaxYear myTaxYear = (TaxYear) pTaxYear;

        /* Store the current detail into history */
        pushHistory();

        /* Update the tax regime if required */
        if (!Difference.isEqual(getTaxRegime(), myTaxYear.getTaxRegime())) {
            setTaxRegime(myTaxYear.getTaxRegime());
        }

        /* Update the allowance if required */
        if (!Difference.isEqual(getAllowance(), myTaxYear.getAllowance())) {
            setAllowance(myTaxYear.getAllowance());
        }

        /* Update the rental allowance if required */
        if (!Difference.isEqual(getRentalAllowance(), myTaxYear.getRentalAllowance())) {
            setRentalAllowance(myTaxYear.getRentalAllowance());
        }

        /* Update the Low band if required */
        if (!Difference.isEqual(getLoBand(), myTaxYear.getLoBand())) {
            setLoBand(myTaxYear.getLoBand());
        }

        /* Update the basic band if required */
        if (!Difference.isEqual(getBasicBand(), myTaxYear.getBasicBand())) {
            setBasicBand(myTaxYear.getBasicBand());
        }

        /* Update the low age allowance if required */
        if (!Difference.isEqual(getLoAgeAllow(), myTaxYear.getLoAgeAllow())) {
            setLoAgeAllow(myTaxYear.getLoAgeAllow());
        }

        /* Update the high age allowance if required */
        if (!Difference.isEqual(getHiAgeAllow(), myTaxYear.getHiAgeAllow())) {
            setHiAgeAllow(myTaxYear.getHiAgeAllow());
        }

        /* Update the age allowance limit if required */
        if (!Difference.isEqual(getAgeAllowLimit(), myTaxYear.getAgeAllowLimit())) {
            setAgeAllowLimit(myTaxYear.getAgeAllowLimit());
        }

        /* Update the additional allowance limit if required */
        if (!Difference.isEqual(getAddAllowLimit(), myTaxYear.getAddAllowLimit())) {
            setAddAllowLimit(myTaxYear.getAddAllowLimit());
        }

        /* Update the additional income boundary if required */
        if (!Difference.isEqual(getAddIncBound(), myTaxYear.getAddIncBound())) {
            setAddIncBound(myTaxYear.getAddIncBound());
        }

        /* Update the Low tax rate if required */
        if (!Difference.isEqual(getLoTaxRate(), myTaxYear.getLoTaxRate())) {
            setLoTaxRate(myTaxYear.getLoTaxRate());
        }

        /* Update the standard tax rate if required */
        if (!Difference.isEqual(getBasicTaxRate(), myTaxYear.getBasicTaxRate())) {
            setBasicTaxRate(myTaxYear.getBasicTaxRate());
        }

        /* Update the high tax rate if required */
        if (!Difference.isEqual(getHiTaxRate(), myTaxYear.getHiTaxRate())) {
            setHiTaxRate(myTaxYear.getHiTaxRate());
        }

        /* Update the interest tax rate if required */
        if (!Difference.isEqual(getIntTaxRate(), myTaxYear.getIntTaxRate())) {
            setIntTaxRate(myTaxYear.getIntTaxRate());
        }

        /* Update the dividend tax rate if required */
        if (!Difference.isEqual(getDivTaxRate(), myTaxYear.getDivTaxRate())) {
            setDivTaxRate(myTaxYear.getDivTaxRate());
        }

        /* Update the high dividend rate if required */
        if (!Difference.isEqual(getHiDivTaxRate(), myTaxYear.getHiDivTaxRate())) {
            setHiDivTaxRate(myTaxYear.getHiDivTaxRate());
        }

        /* Update the additional rate if required */
        if (!Difference.isEqual(getAddTaxRate(), myTaxYear.getAddTaxRate())) {
            setAddTaxRate(myTaxYear.getAddTaxRate());
        }

        /* Update the additional dividend rate if required */
        if (!Difference.isEqual(getAddDivTaxRate(), myTaxYear.getAddDivTaxRate())) {
            setAddDivTaxRate(myTaxYear.getAddDivTaxRate());
        }

        /* Update the capital rate if required */
        if (!Difference.isEqual(getCapTaxRate(), myTaxYear.getCapTaxRate())) {
            setCapTaxRate(myTaxYear.getCapTaxRate());
        }

        /* Update the high capital rate if required */
        if (!Difference.isEqual(getHiCapTaxRate(), myTaxYear.getHiCapTaxRate())) {
            setHiCapTaxRate(myTaxYear.getHiCapTaxRate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Tax Year List class.
     */
    public static class TaxYearList extends DataList<TaxYearList, TaxYear> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(TaxYearList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * The NewYear.
         */
        private TaxYear theNewYear = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
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
        protected TaxYearList(final FinanceData pData) {
            super(TaxYearList.class, TaxYear.class, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private TaxYearList(final TaxYearList pSource) {
            super(pSource);
        }

        @Override
        protected TaxYearList getEmptyList() {
            return new TaxYearList(this);
        }

        /**
         * Construct an edit extract for a TaxYear.
         * @param pTaxYear the tax year
         * @return the edit Extract
         */
        public TaxYearList deriveEditList(final TaxYear pTaxYear) {
            /* Build an empty List */
            TaxYearList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

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
            TaxYearList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Access the existing tax years */
            FinanceData myData = getDataSet();
            TaxYearList myTaxYears = myData.getTaxYears();
            OrderedListIterator<TaxYear> myIterator = myTaxYears.listIterator();

            /* Create a new tax year for the list */
            TaxYear myBase = myIterator.peekLast();
            TaxYear myYear = new TaxYear(myList, myBase);
            myYear.setBase(null);
            myYear.setId(0);

            /* Adjust the year and add to list */
            myYear.setTaxYear(new DateDay(myBase.getTaxYear()));
            myYear.getTaxYear().adjustYear(1);
            myList.add(myYear);

            /* Record the new year */
            myList.theNewYear = myYear;

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pTaxYear item
         * @return the newly added item
         */
        @Override
        public TaxYear addNewItem(final DataItem pTaxYear) {
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
         * Search for the tax year that encompasses this date.
         * @param pDate Date of item
         * @return The TaxYear if present (or null)
         */
        public TaxYear findTaxYearForDate(final DateDay pDate) {
            /* Access the iterator */
            Iterator<TaxYear> myIterator = iterator();
            TaxYear myCurr = null;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                myCurr = myIterator.next();

                /* Access the range for this tax year */
                DateDayRange myRange = myCurr.getRange();

                /* Determine whether the date is owned by the tax year */
                int iDiff = myRange.compareTo(pDate);
                if (iDiff == 0) {
                    return myCurr;
                }
            }

            /* Return to caller */
            return null;
        }

        /**
         * Count the instances of a date.
         * @param pDate the date
         * @return The Item if present (or null)
         */
        protected int countInstances(final DateDay pDate) {
            /* Access the iterator */
            Iterator<TaxYear> myIterator = listIterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                TaxYear myCurr = myIterator.next();
                int iDiff = pDate.compareTo(myCurr.getTaxYear());
                if (iDiff == 0) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Extract the date range represented by the tax years.
         * @return the range of tax years
         */
        public DateDayRange getRange() {
            /* Access the iterator */
            OrderedListIterator<TaxYear> myIterator = listIterator();
            DateDay myStart = null;
            DateDay myEnd = null;

            /* Extract the first item */
            TaxYear myCurr = myIterator.peekFirst();
            if (myCurr != null) {
                /* Access start date */
                myStart = new DateDay(myCurr.getTaxYear());

                /* Move back to start of year */
                myStart.adjustYear(-1);
                myStart.adjustDay(1);

                /* Extract the last item */
                myCurr = myIterator.peekLast();
                myEnd = myCurr.getTaxYear();
            }

            /* Create the range */
            return new DateDayRange(myStart, myEnd);
        }

        /**
         * Mark active items.
         */
        protected void markActiveItems() {
            /* Access the list iterator */
            Iterator<TaxYear> myIterator = listIterator();

            /* Loop through the Prices */
            while (myIterator.hasNext()) {
                TaxYear myCurr = myIterator.next();

                /* mark the tax regime referred to */
                myCurr.getTaxRegime().touchItem(myCurr);
            }
        }

        /**
         * Allow a tax parameter to be added.
         * @param uId the id
         * @param pRegime the regime
         * @param pDate the date
         * @param pAllowance the allowance
         * @param pRentalAllow the rental allowance
         * @param pLoAgeAllow the low Age allowance
         * @param pHiAgeAllow the high age allowance
         * @param pCapAllow the capital allowance
         * @param pAgeAllowLimit the age allowance limit
         * @param pAddAllowLimit the additional allowance limit
         * @param pLoTaxBand the low tax band
         * @param pBasicTaxBand the basic tax band
         * @param pAddIncBound the additional income boundary
         * @param pLoTaxRate the low tax rate
         * @param pBasicTaxRate the basic tax rate
         * @param pHiTaxRate the high tax rate
         * @param pIntTaxRate the additional tax rate
         * @param pDivTaxRate the dividend tax rate
         * @param pHiDivTaxRate the high dividend tax rate
         * @param pAddTaxRate the additional tax rate
         * @param pAddDivTaxRate the addition dividend tax rate
         * @param pCapTaxRate the capital tax rate
         * @param pHiCapTaxRate the high capital tax rate
         * @throws JDataException on error
         * @throws JDataException
         */
        public void addItem(final int uId,
                            final String pRegime,
                            final Date pDate,
                            final String pAllowance,
                            final String pRentalAllow,
                            final String pLoAgeAllow,
                            final String pHiAgeAllow,
                            final String pCapAllow,
                            final String pAgeAllowLimit,
                            final String pAddAllowLimit,
                            final String pLoTaxBand,
                            final String pBasicTaxBand,
                            final String pAddIncBound,
                            final String pLoTaxRate,
                            final String pBasicTaxRate,
                            final String pHiTaxRate,
                            final String pIntTaxRate,
                            final String pDivTaxRate,
                            final String pHiDivTaxRate,
                            final String pAddTaxRate,
                            final String pAddDivTaxRate,
                            final String pCapTaxRate,
                            final String pHiCapTaxRate) throws JDataException {
            /* Look up the Tax Regime */
            FinanceData myData = getDataSet();
            TaxRegimeList myList = myData.getTaxRegimes();
            TaxRegime myTaxRegime = myList.findItemByName(pRegime);
            if (myTaxRegime == null) {
                throw new JDataException(ExceptionClass.DATA, "TaxYear on <"
                        + JDataObject.formatField(new DateDay(pDate)) + "> has invalid TaxRegime <" + pRegime
                        + ">");
            }

            /* Create the tax year */
            addItem(uId, myTaxRegime.getId(), pDate, pAllowance, pRentalAllow, pLoAgeAllow, pHiAgeAllow,
                    pCapAllow, pAgeAllowLimit, pAddAllowLimit, pLoTaxBand, pBasicTaxBand, pAddIncBound,
                    pLoTaxRate, pBasicTaxRate, pHiTaxRate, pIntTaxRate, pDivTaxRate, pHiDivTaxRate,
                    pAddTaxRate, pAddDivTaxRate, pCapTaxRate, pHiCapTaxRate);
        }

        /**
         * Allow a tax parameter to be added.
         * @param uId the id
         * @param uRegimeId the regime id
         * @param pDate the date
         * @param pAllowance the allowance
         * @param pRentalAllow the rental allowance
         * @param pLoAgeAllow the low Age allowance
         * @param pHiAgeAllow the high age allowance
         * @param pCapAllow the capital allowance
         * @param pAgeAllowLimit the age allowance limit
         * @param pAddAllowLimit the additional allowance limit
         * @param pLoTaxBand the low tax band
         * @param pBasicTaxBand the basic tax band
         * @param pAddIncBound the additional income boundary
         * @param pLoTaxRate the low tax rate
         * @param pBasicTaxRate the basic tax rate
         * @param pHiTaxRate the high tax rate
         * @param pIntTaxRate the additional tax rate
         * @param pDivTaxRate the dividend tax rate
         * @param pHiDivTaxRate the high dividend tax rate
         * @param pAddTaxRate the additional tax rate
         * @param pAddDivTaxRate the addition dividend tax rate
         * @param pCapTaxRate the capital tax rate
         * @param pHiCapTaxRate the high capital tax rate
         * @throws JDataException on error
         * @throws JDataException
         */
        public void addItem(final int uId,
                            final int uRegimeId,
                            final Date pDate,
                            final String pAllowance,
                            final String pRentalAllow,
                            final String pLoAgeAllow,
                            final String pHiAgeAllow,
                            final String pCapAllow,
                            final String pAgeAllowLimit,
                            final String pAddAllowLimit,
                            final String pLoTaxBand,
                            final String pBasicTaxBand,
                            final String pAddIncBound,
                            final String pLoTaxRate,
                            final String pBasicTaxRate,
                            final String pHiTaxRate,
                            final String pIntTaxRate,
                            final String pDivTaxRate,
                            final String pHiDivTaxRate,
                            final String pAddTaxRate,
                            final String pAddDivTaxRate,
                            final String pCapTaxRate,
                            final String pHiCapTaxRate) throws JDataException {
            /* Create the tax year */
            TaxYear myTaxYear = new TaxYear(this, uId, uRegimeId, pDate, pAllowance, pRentalAllow,
                    pLoAgeAllow, pHiAgeAllow, pCapAllow, pAgeAllowLimit, pAddAllowLimit, pLoTaxBand,
                    pBasicTaxBand, pAddIncBound, pLoTaxRate, pBasicTaxRate, pHiTaxRate, pIntTaxRate,
                    pDivTaxRate, pHiDivTaxRate, pAddTaxRate, pAddDivTaxRate, pCapTaxRate, pHiCapTaxRate);

            /* Check that this TaxYearId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myTaxYear, "Duplicate TaxYearId");
            }

            /* Check that this TaxYear has not been previously added */
            if (findTaxYearForDate(new DateDay(pDate)) != null) {
                throw new JDataException(ExceptionClass.DATA, myTaxYear, "Duplicate TaxYear");
            }

            /* Validate the tax year */
            myTaxYear.validate();

            /* Handle validation failure */
            if (myTaxYear.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myTaxYear, "Failed validation");
            }

            /* Add the TaxYear to the end of the list */
            addAtEnd(myTaxYear);
        }
    }
}