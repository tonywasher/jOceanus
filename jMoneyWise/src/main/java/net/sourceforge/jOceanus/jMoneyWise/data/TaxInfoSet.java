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
package net.sourceforge.jOceanus.jMoneyWise.data;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;

/**
 * TaxInfoSet class.
 * @author Tony Washer
 */
public class TaxInfoSet
        extends DataInfoSet<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(TaxInfoSet.class.getSimpleName(), DataInfoSet.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Allowance field Id.
     */
    public static final JDataField FIELD_ALLOW = FIELD_DEFS.declareLocalField(TaxYearInfoClass.Allowance.toString());

    /**
     * Rental field Id.
     */
    public static final JDataField FIELD_RENTAL = FIELD_DEFS.declareLocalField(TaxYearInfoClass.RentalAllowance.toString());

    /**
     * LoAgeAllowance field Id.
     */
    public static final JDataField FIELD_LOAGAL = FIELD_DEFS.declareLocalField(TaxYearInfoClass.LoAgeAllowance.toString());

    /**
     * HiAgeAllowance field Id.
     */
    public static final JDataField FIELD_HIAGAL = FIELD_DEFS.declareLocalField(TaxYearInfoClass.HiAgeAllowance.toString());

    /**
     * LoTaxBand field Id.
     */
    public static final JDataField FIELD_LOBAND = FIELD_DEFS.declareLocalField(TaxYearInfoClass.LoTaxBand.toString());

    /**
     * BaseTaxBand field Id.
     */
    public static final JDataField FIELD_BSBAND = FIELD_DEFS.declareLocalField(TaxYearInfoClass.BasicTaxBand.toString());

    /**
     * CapitalAllowance field Id.
     */
    public static final JDataField FIELD_CAPALW = FIELD_DEFS.declareLocalField(TaxYearInfoClass.CapitalAllowance.toString());

    /**
     * AgeAllowanceLimit field Id.
     */
    public static final JDataField FIELD_AGELMT = FIELD_DEFS.declareLocalField(TaxYearInfoClass.AgeAllowanceLimit.toString());

    /**
     * Additional Allowance Limit field Id.
     */
    public static final JDataField FIELD_ADDLMT = FIELD_DEFS.declareLocalField(TaxYearInfoClass.AdditionalAllowanceLimit.toString());

    /**
     * Additional Income Boundary field Id.
     */
    public static final JDataField FIELD_ADDBDY = FIELD_DEFS.declareLocalField(TaxYearInfoClass.AdditionalIncomeThreshold.toString());

    /**
     * LoTaxRate field Id.
     */
    public static final JDataField FIELD_LOTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.LoTaxRate.toString());

    /**
     * BasicTaxRate field Id.
     */
    public static final JDataField FIELD_BASTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.BasicTaxRate.toString());

    /**
     * HiTaxRate field Id.
     */
    public static final JDataField FIELD_HITAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.HiTaxRate.toString());

    /**
     * InterestTaxRate field Id.
     */
    public static final JDataField FIELD_INTTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.InterestTaxRate.toString());

    /**
     * DividendTaxRate field Id.
     */
    public static final JDataField FIELD_DIVTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.DividendTaxRate.toString());

    /**
     * HiDividendTaxRate field Id.
     */
    public static final JDataField FIELD_HDVTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.HiDividendTaxRate.toString());

    /**
     * AdditionalTaxRate field Id.
     */
    public static final JDataField FIELD_ADDTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.AdditionalTaxRate.toString());

    /**
     * AddDividendTaxRate field Id.
     */
    public static final JDataField FIELD_ADVTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.AdditionalDividendTaxRate.toString());

    /**
     * CapitalTaxRate field Id.
     */
    public static final JDataField FIELD_CAPTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.CapitalTaxRate.toString());

    /**
     * HiCapitalTaxRate field Id.
     */
    public static final JDataField FIELD_HCPTAX = FIELD_DEFS.declareLocalField(TaxYearInfoClass.HiCapitalTaxRate.toString());

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle InfoSet fields */
        TaxYearInfoClass myClass = TaxInfoSet.getFieldClass(pField);
        if (myClass != null) {
            return getInfoSetValue(myClass);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Get an infoSet value.
     * @param pInfoClass the class of info to get
     * @return the value to set
     */
    private Object getInfoSetValue(final TaxYearInfoClass pInfoClass) {
        /* Access value of object */
        Object myValue = getField(pInfoClass);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    protected static TaxYearInfoClass getFieldClass(final JDataField pField) {
        if (FIELD_ALLOW.equals(pField)) {
            return TaxYearInfoClass.Allowance;
        }
        if (FIELD_RENTAL.equals(pField)) {
            return TaxYearInfoClass.RentalAllowance;
        }
        if (FIELD_LOAGAL.equals(pField)) {
            return TaxYearInfoClass.LoAgeAllowance;
        }
        if (FIELD_HIAGAL.equals(pField)) {
            return TaxYearInfoClass.HiAgeAllowance;
        }
        if (FIELD_LOBAND.equals(pField)) {
            return TaxYearInfoClass.LoTaxBand;
        }
        if (FIELD_BSBAND.equals(pField)) {
            return TaxYearInfoClass.BasicTaxBand;
        }
        if (FIELD_CAPALW.equals(pField)) {
            return TaxYearInfoClass.CapitalAllowance;
        }
        if (FIELD_AGELMT.equals(pField)) {
            return TaxYearInfoClass.AgeAllowanceLimit;
        }
        if (FIELD_ADDLMT.equals(pField)) {
            return TaxYearInfoClass.AdditionalAllowanceLimit;
        }
        if (FIELD_ADDBDY.equals(pField)) {
            return TaxYearInfoClass.AdditionalIncomeThreshold;
        }
        if (FIELD_LOTAX.equals(pField)) {
            return TaxYearInfoClass.LoTaxRate;
        }
        if (FIELD_BASTAX.equals(pField)) {
            return TaxYearInfoClass.BasicTaxRate;
        }
        if (FIELD_HITAX.equals(pField)) {
            return TaxYearInfoClass.HiTaxRate;
        }
        if (FIELD_INTTAX.equals(pField)) {
            return TaxYearInfoClass.InterestTaxRate;
        }
        if (FIELD_DIVTAX.equals(pField)) {
            return TaxYearInfoClass.DividendTaxRate;
        }
        if (FIELD_HDVTAX.equals(pField)) {
            return TaxYearInfoClass.HiDividendTaxRate;
        }
        if (FIELD_ADDTAX.equals(pField)) {
            return TaxYearInfoClass.AdditionalTaxRate;
        }
        if (FIELD_ADVTAX.equals(pField)) {
            return TaxYearInfoClass.AdditionalDividendTaxRate;
        }
        if (FIELD_CAPTAX.equals(pField)) {
            return TaxYearInfoClass.CapitalTaxRate;
        }
        if (FIELD_HCPTAX.equals(pField)) {
            return TaxYearInfoClass.HiCapitalTaxRate;
        }
        return null;
    }

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList source InfoSet
     */
    protected TaxInfoSet(final TaxYear pOwner,
                         final TaxYearInfoTypeList pTypeList,
                         final TaxInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final TaxInfoSet pSource) {
        /* Clone the dataInfoSet */
        super.cloneDataInfoSet(pSource);
    }
}
