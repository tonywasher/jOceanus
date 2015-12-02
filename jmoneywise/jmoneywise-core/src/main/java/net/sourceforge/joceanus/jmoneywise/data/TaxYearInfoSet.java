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

import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList.DataListSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * TaxInfoSet class.
 * @author Tony Washer
 */
public class TaxYearInfoSet
        extends DataInfoSet<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.TAXYEAR_INFOSET.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, TaxYearInfoClass> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, TaxYearInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<TaxYearInfoClass, MetisField> REVERSE_FIELDMAP = MetisFields.reverseFieldMap(FIELDSET_MAP, TaxYearInfoClass.class);

    /**
     * Default Additional Income Threshold.
     */
    private static final TethysMoney DEFAULT_ADDTHRESHOLD = TethysMoney.getWholeUnits(150000);

    /**
     * Default Additional Income Limit.
     */
    private static final TethysMoney DEFAULT_ADDLIMIT = TethysMoney.getWholeUnits(100000);

    /**
     * Allowance Limit Error Text.
     */
    private static final String ERROR_ALLOW = MoneyWiseDataResource.TAXYEAR_ERROR_ALLOWANCE.getValue();

    /**
     * LoAgeAllowance Limit Error Text.
     */
    private static final String ERROR_LOALLOW = MoneyWiseDataResource.TAXYEAR_ERROR_LOALLOWANCE.getValue();

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList source InfoSet
     */
    protected TaxYearInfoSet(final TaxYear pOwner,
                             final TaxYearInfoTypeList pTypeList,
                             final TaxInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
                                : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static TaxYearInfoClass getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static MetisField getFieldForClass(final TaxYearInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final TaxYearInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public MetisFieldRequired isFieldRequired(final MetisField pField) {
        TaxYearInfoClass myClass = getClassForField(pField);
        return myClass == null
                              ? MetisFieldRequired.NOTALLOWED
                              : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final TaxYearInfoClass pClass) {
        /* Access details about the Tax Year */
        TaxYear myTaxYear = getOwner();
        TaxRegime myRegime = myTaxYear.getTaxRegime();

        /* If we have no TaxRegime, no class is allowed */
        if (myRegime == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch (pClass) {

        /* Handle Additional Tax Details */
            case ADDITIONALALLOWANCELIMIT:
            case ADDITIONALINCOMETHRESHOLD:
            case ADDITIONALTAXRATE:
            case ADDITIONALDIVIDENDTAXRATE:
                return myRegime.hasAdditionalTaxBand()
                                                      ? MetisFieldRequired.MUSTEXIST
                                                      : MetisFieldRequired.NOTALLOWED;

                /* Handle CapitalIncome Tax Details */
            case CAPITALTAXRATE:
                return myRegime.hasCapitalGainsAsIncome()
                                                         ? MetisFieldRequired.NOTALLOWED
                                                         : MetisFieldRequired.MUSTEXIST;

                /* Handle CapitalIncome Tax Details */
            case HICAPITALTAXRATE:
                return myRegime.hasCapitalGainsAsIncome()
                                                         ? MetisFieldRequired.NOTALLOWED
                                                         : MetisFieldRequired.CANEXIST;

                /* Handle all other fields */
            default:
                return MetisFieldRequired.MUSTEXIST;
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
            boolean isExisting = (myInfo != null) && !myInfo.isDeleted();

            /* Determine requirements for class */
            MetisFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == MetisFieldRequired.MUSTEXIST) {
                    myTaxYear.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == MetisFieldRequired.NOTALLOWED) {
                myTaxYear.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* All values are decimal so just obtain as decimal */
            TethysDecimal myValue = myInfo.getValue(TethysDecimal.class);

            /* Values must be positive */
            if (!myValue.isPositive()) {
                myTaxYear.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
            }

            /* If this is LoAgeAllowance */
            if (myClass == TaxYearInfoClass.LOAGEALLOWANCE) {
                /* Obtain Allowance value */
                TaxYearInfo myAllowInfo = getInfo(TaxYearInfoClass.ALLOWANCE);
                TethysDecimal myAllowance = (myAllowInfo != null)
                                                            ? myInfo.getValue(TethysDecimal.class)
                                                            : null;
                if ((myAllowance != null) && (myValue.compareTo(myAllowance) < 0)) {
                    myTaxYear.addError(ERROR_ALLOW, getFieldForClass(myClass));
                }
            }

            /* If this is HiAgeAllowance */
            if (myClass == TaxYearInfoClass.HIAGEALLOWANCE) {
                /* Obtain LoAgeAllowance value */
                TaxYearInfo myAllowInfo = getInfo(TaxYearInfoClass.LOAGEALLOWANCE);
                TethysDecimal myAllowance = (myAllowInfo != null)
                                                            ? myInfo.getValue(TethysDecimal.class)
                                                            : null;
                if ((myAllowance != null) && (myValue.compareTo(myAllowance) < 0)) {
                    myTaxYear.addError(ERROR_LOALLOW, getFieldForClass(myClass));
                }
            }
        }
    }

    @Override
    protected void setDefaultValue(final DataListSet<MoneyWiseDataType> pUpdateSet,
                                   final TaxYearInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch (pClass) {
            case CAPITALTAXRATE:
                TethysRate myRate = getValue(TaxYearInfoClass.BASICTAXRATE, TethysRate.class);
                setValue(pClass, myRate);
                break;
            case ADDITIONALTAXRATE:
                myRate = getValue(TaxYearInfoClass.HITAXRATE, TethysRate.class);
                setValue(pClass, myRate);
                break;
            case ADDITIONALDIVIDENDTAXRATE:
                myRate = getValue(TaxYearInfoClass.HIDIVIDENDTAXRATE, TethysRate.class);
                setValue(pClass, myRate);
                break;
            case ADDITIONALALLOWANCELIMIT:
                setValue(pClass, DEFAULT_ADDLIMIT);
                break;
            case ADDITIONALINCOMETHRESHOLD:
                setValue(pClass, DEFAULT_ADDTHRESHOLD);
                break;
            default:
                break;
        }
    }
}
