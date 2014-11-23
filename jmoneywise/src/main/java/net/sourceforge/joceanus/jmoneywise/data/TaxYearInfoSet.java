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

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxRegime;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JRate;

/**
 * TaxInfoSet class.
 * @author Tony Washer
 */
public class TaxYearInfoSet
        extends DataInfoSet<TaxYearInfo, TaxYear, TaxYearInfoType, TaxYearInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseDataResource.TAXYEAR_INFOSET.getValue());

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, TaxYearInfoClass> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, TaxYearInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<TaxYearInfoClass, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, TaxYearInfoClass.class);

    /**
     * Default Additional Income Threshold.
     */
    private static final JMoney DEFAULT_ADDTHRESHOLD = JMoney.getWholeUnits(150000);

    /**
     * Default Additional Income Limit.
     */
    private static final JMoney DEFAULT_ADDLIMIT = JMoney.getWholeUnits(100000);

    /**
     * Allowance Limit Error Text.
     */
    private static final String ERROR_ALLOW = MoneyWiseDataResource.TAXYEAR_ERROR_ALLOWANCE.getValue();

    /**
     * LoAgeAllowance Limit Error Text.
     */
    private static final String ERROR_LOALLOW = MoneyWiseDataResource.TAXYEAR_ERROR_LOALLOWANCE.getValue();

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
                                : JDataFieldValue.SKIP;
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
    protected TaxYearInfoSet(final TaxYear pOwner,
                             final TaxYearInfoTypeList pTypeList,
                             final TaxInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
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
    public JDataFieldRequired isFieldRequired(final JDataField pField) {
        TaxYearInfoClass myClass = getClassForField(pField);
        return myClass == null
                              ? JDataFieldRequired.NOTALLOWED
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
            return JDataFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch (pClass) {

        /* Handle Additional Tax Details */
            case ADDITIONALALLOWANCELIMIT:
            case ADDITIONALINCOMETHRESHOLD:
            case ADDITIONALTAXRATE:
            case ADDITIONALDIVIDENDTAXRATE:
                return myRegime.hasAdditionalTaxBand()
                                                      ? JDataFieldRequired.MUSTEXIST
                                                      : JDataFieldRequired.NOTALLOWED;

                /* Handle CapitalIncome Tax Details */
            case CAPITALTAXRATE:
                return myRegime.hasCapitalGainsAsIncome()
                                                         ? JDataFieldRequired.NOTALLOWED
                                                         : JDataFieldRequired.MUSTEXIST;

                /* Handle CapitalIncome Tax Details */
            case HICAPITALTAXRATE:
                return myRegime.hasCapitalGainsAsIncome()
                                                         ? JDataFieldRequired.NOTALLOWED
                                                         : JDataFieldRequired.CANEXIST;

                /* Handle all other fields */
            default:
                return JDataFieldRequired.MUSTEXIST;
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
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MUSTEXIST) {
                    myTaxYear.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NOTALLOWED) {
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
            if (myClass == TaxYearInfoClass.LOAGEALLOWANCE) {
                /* Obtain Allowance value */
                TaxYearInfo myAllowInfo = getInfo(TaxYearInfoClass.ALLOWANCE);
                JDecimal myAllowance = (myAllowInfo != null)
                                                            ? myInfo.getValue(JDecimal.class)
                                                            : null;
                if ((myAllowance != null) && (myValue.compareTo(myAllowance) < 0)) {
                    myTaxYear.addError(ERROR_ALLOW, getFieldForClass(myClass));
                }
            }

            /* If this is HiAgeAllowance */
            if (myClass == TaxYearInfoClass.HIAGEALLOWANCE) {
                /* Obtain LoAgeAllowance value */
                TaxYearInfo myAllowInfo = getInfo(TaxYearInfoClass.LOAGEALLOWANCE);
                JDecimal myAllowance = (myAllowInfo != null)
                                                            ? myInfo.getValue(JDecimal.class)
                                                            : null;
                if ((myAllowance != null) && (myValue.compareTo(myAllowance) < 0)) {
                    myTaxYear.addError(ERROR_LOALLOW, getFieldForClass(myClass));
                }
            }
        }
    }

    /**
     * adjust values after change.
     * @throws JOceanusException on error
     */
    protected void autoCorrect() throws JOceanusException {
        /* Access tax regime */
        TaxYear myOwner = getOwner();
        TaxRegime myRegime = myOwner.getTaxRegime();

        /* If Capital Gains is taxed as income */
        if (myRegime.hasCapitalGainsAsIncome()) {
            /* Clear the rates */
            setValue(TaxYearInfoClass.CAPITALTAXRATE, null);
            setValue(TaxYearInfoClass.HICAPITALTAXRATE, null);

            /* Else Initialise CapitalTaxRate from Standard Rate if required */
        } else if (getInfo(TaxYearInfoClass.CAPITALTAXRATE) == null) {
            JRate myRate = getValue(TaxYearInfoClass.BASICTAXRATE, JRate.class);
            setValue(TaxYearInfoClass.CAPITALTAXRATE, myRate);
        }

        /* If we do not use additional bands */
        if (!myRegime.hasAdditionalTaxBand()) {
            /* Clear the values */
            setValue(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, null);
            setValue(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, null);
            setValue(TaxYearInfoClass.ADDITIONALTAXRATE, null);
            setValue(TaxYearInfoClass.ADDITIONALTAXRATE, null);

            /* Else we need additional bands */
        } else {
            /* Set boundaries as required */
            if (getInfo(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT) == null) {
                setValue(TaxYearInfoClass.ADDITIONALALLOWANCELIMIT, DEFAULT_ADDLIMIT);
            }
            if (getInfo(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD) == null) {
                setValue(TaxYearInfoClass.ADDITIONALINCOMETHRESHOLD, DEFAULT_ADDTHRESHOLD);
            }

            /* Initialise Additional rates from High rates */
            if (getInfo(TaxYearInfoClass.ADDITIONALTAXRATE) == null) {
                JRate myRate = getValue(TaxYearInfoClass.HITAXRATE, JRate.class);
                setValue(TaxYearInfoClass.ADDITIONALTAXRATE, myRate);
            }
            if (getInfo(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE) == null) {
                JRate myRate = getValue(TaxYearInfoClass.HIDIVIDENDTAXRATE, JRate.class);
                setValue(TaxYearInfoClass.ADDITIONALDIVIDENDTAXRATE, myRate);
            }
        }

        /* Access age allowances */
        JMoney myAllow = getValue(TaxYearInfoClass.ALLOWANCE, JMoney.class);
        JMoney myLoAgeAllow = getValue(TaxYearInfoClass.LOAGEALLOWANCE, JMoney.class);
        JMoney myHiAgeAllow = getValue(TaxYearInfoClass.HIAGEALLOWANCE, JMoney.class);

        /* Check LoAge Allowance */
        if (myLoAgeAllow.compareTo(myAllow) < 0) {
            setValue(TaxYearInfoClass.LOAGEALLOWANCE, myAllow);
            myLoAgeAllow = myAllow;
        }

        /* Check HiAge Allowance */
        if (myHiAgeAllow.compareTo(myLoAgeAllow) < 0) {
            setValue(TaxYearInfoClass.HIAGEALLOWANCE, myLoAgeAllow);
        }
    }
}
