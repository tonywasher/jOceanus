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
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataFieldRequired;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime;
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
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    protected static JDataField getClassField(final TaxYearInfoClass pClass) {
        switch (pClass) {
            case Allowance:
                return FIELD_ALLOW;
            case LoTaxBand:
                return FIELD_LOBAND;
            case BasicTaxBand:
                return FIELD_BSBAND;
            case LoAgeAllowance:
                return FIELD_LOAGAL;
            case HiAgeAllowance:
                return FIELD_HIAGAL;
            case RentalAllowance:
                return FIELD_RENTAL;
            case CapitalAllowance:
                return FIELD_CAPALW;
            case AgeAllowanceLimit:
                return FIELD_AGELMT;
            case AdditionalAllowanceLimit:
                return FIELD_ADDLMT;
            case AdditionalIncomeThreshold:
                return FIELD_ADDBDY;
            case LoTaxRate:
                return FIELD_LOTAX;
            case BasicTaxRate:
                return FIELD_BASTAX;
            case HiTaxRate:
                return FIELD_HITAX;
            case AdditionalTaxRate:
                return FIELD_ADDTAX;
            case InterestTaxRate:
                return FIELD_INTTAX;
            case DividendTaxRate:
                return FIELD_DIVTAX;
            case HiDividendTaxRate:
                return FIELD_HDVTAX;
            case AdditionalDividendTaxRate:
                return FIELD_ADVTAX;
            case CapitalTaxRate:
                return FIELD_CAPTAX;
            case HiCapitalTaxRate:
                return FIELD_HCPTAX;
            default:
                return null;
        }
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

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public JDataFieldRequired isFieldRequired(final JDataField pField) {
        TaxYearInfoClass myClass = getFieldClass(pField);
        return myClass == null
                ? JDataFieldRequired.NotAllowed
                : isClassRequired(myClass);
    }

    /**
     * Determine if an infoSet class is required.
     * @param pClass the infoSet class
     * @return the status
     */
    protected JDataFieldRequired isClassRequired(final TaxYearInfoClass pClass) {
        /* Access details about the Tax Year */
        TaxYear myTaxYear = getOwner();
        TaxRegime myRegime = myTaxYear.getTaxRegime();

        /* If we have no TaxRegime, no class is allowed */
        if (myRegime == null) {
            return JDataFieldRequired.NotAllowed;
        }

        /* Switch on class */
        switch (pClass) {

        /* Handle Additional Tax Details */
            case AdditionalAllowanceLimit:
            case AdditionalIncomeThreshold:
            case AdditionalTaxRate:
            case AdditionalDividendTaxRate:
                return myRegime.hasAdditionalTaxBand()
                        ? JDataFieldRequired.MustExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle CapitalIncome Tax Details */
            case CapitalTaxRate:
                return myRegime.hasCapitalGainsAsIncome()
                        ? JDataFieldRequired.NotAllowed
                        : JDataFieldRequired.MustExist;

                /* Handle CapitalIncome Tax Details */
            case HiCapitalTaxRate:
                return myRegime.hasCapitalGainsAsIncome()
                        ? JDataFieldRequired.NotAllowed
                        : JDataFieldRequired.CanExist;

                /* Handle all other fields */
            default:
                return JDataFieldRequired.MustExist;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Tax Year */
        TaxYear myTaxYear = getOwner();

        /* Loop through the classes */
        for (TaxYearInfoClass myClass : TaxYearInfoClass.values()) {
            /* Access info for class */
            TaxYearInfo myInfo = getInfo(myClass);
            boolean isExisting = (myInfo != null)
                                 && !myInfo.isDeleted();

            /* Determine requirements for class */
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MustExist) {
                    myTaxYear.addError(DataItem.ERROR_MISSING, getClassField(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NotAllowed) {
                myTaxYear.addError(DataItem.ERROR_EXIST, getClassField(myClass));
                continue;
            }

            /* All values are decimal so just obtain as decimal */
            JDecimal myValue = myInfo.getValue(JDecimal.class);

            /* Values must be positive */
            if (!myValue.isPositive()) {
                myTaxYear.addError(DataItem.ERROR_POSITIVE, getClassField(myClass));
            }

            /* If this is LoAgeAllowance */
            if (myClass == TaxYearInfoClass.LoAgeAllowance) {
                /* Obtain Allowance value */
                TaxYearInfo myAllowInfo = getInfo(TaxYearInfoClass.Allowance);
                JDecimal myAllowance = (myAllowInfo != null)
                        ? myInfo.getValue(JDecimal.class)
                        : null;
                if ((myAllowance != null)
                    && (myValue.compareTo(myAllowance) < 0)) {
                    myTaxYear.addError("Value must be greater than allowance", FIELD_LOAGAL);
                }
            }

            /* If this is HiAgeAllowance */
            if (myClass == TaxYearInfoClass.HiAgeAllowance) {
                /* Obtain LoAgeAllowance value */
                TaxYearInfo myAllowInfo = getInfo(TaxYearInfoClass.LoAgeAllowance);
                JDecimal myAllowance = (myAllowInfo != null)
                        ? myInfo.getValue(JDecimal.class)
                        : null;
                if ((myAllowance != null)
                    && (myValue.compareTo(myAllowance) < 0)) {
                    myTaxYear.addError("Value must be greater than LoAgeAllowance", FIELD_HIAGAL);
                }
            }
        }
    }
}
