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

import java.util.Map;

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

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, TaxYearInfoClass> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, TaxYearInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<TaxYearInfoClass, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, TaxYearInfoClass.class);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle InfoSet fields */
        TaxYearInfoClass myClass = getClassForField(pField);
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
    public static TaxYearInfoClass getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static JDataField getFieldForClass(final TaxYearInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
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
        TaxYearInfoClass myClass = getClassForField(pField);
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
                    myTaxYear.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NotAllowed) {
                myTaxYear.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* All values are decimal so just obtain as decimal */
            JDecimal myValue = myInfo.getValue(JDecimal.class);

            /* Values must be positive */
            if (!myValue.isPositive()) {
                myTaxYear.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
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
                    myTaxYear.addError("Value must be greater than allowance", getFieldForClass(TaxYearInfoClass.LoAgeAllowance));
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
                    myTaxYear.addError("Value must be greater than LoAgeAllowance", getFieldForClass(TaxYearInfoClass.HiAgeAllowance));
                }
            }
        }
    }
}
