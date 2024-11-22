/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.moneywise.data.basic;

import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;

/**
 * SecurityInfoSet class.
 * @author Tony Washer
 */
public class MoneyWiseSecurityInfoSet
        extends PrometheusDataInfoSet<MoneyWiseSecurityInfo> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseSecurityInfoSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseSecurityInfoSet.class);

    /**
     * FieldSet map.
     */
    private static final Map<MetisDataFieldId, MoneyWiseAccountInfoClass> FIELDSET_MAP = FIELD_DEFS.buildFieldMap(MoneyWiseAccountInfoClass.class, MoneyWiseSecurityInfoSet::getFieldValue);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<MoneyWiseAccountInfoClass, MetisDataFieldId> REVERSE_FIELDMAP = MetisFieldSet.reverseFieldMap(FIELDSET_MAP, MoneyWiseAccountInfoClass.class);

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected MoneyWiseSecurityInfoSet(final MoneyWiseSecurity pOwner,
                                       final MoneyWiseAccountInfoTypeList pTypeList,
                                       final MoneyWiseSecurityInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MoneyWiseSecurity getOwner() {
        return (MoneyWiseSecurity) super.getOwner();
    }

    /**
     * Obtain fieldValue for infoSet.
     * @param pFieldId the fieldId
     * @return the value
     */
    public Object getFieldValue(final MetisDataFieldId pFieldId) {
        /* Handle InfoSet fields */
        final MoneyWiseAccountInfoClass myClass = getClassForField(pFieldId);
        if (myClass != null) {
            return getInfoSetValue(myClass);
        }

        /* Pass onwards */
        return null;
    }

    /**
     * Get an infoSet value.
     * @param pInfoClass the class of info to get
     * @return the value to set
     */
    private Object getInfoSetValue(final MoneyWiseAccountInfoClass pInfoClass) {
        final Object myValue;

        switch (pInfoClass) {
            case REGION:
                /* Access region of object */
                myValue = getRegion(pInfoClass);
                break;
            case UNDERLYINGSTOCK:
                /* Access underlying Stock of object */
                myValue = getSecurity(pInfoClass);
                break;
            default:
                /* Access value of object */
                myValue = getField(pInfoClass);
                break;
        }

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static MoneyWiseAccountInfoClass getClassForField(final MetisDataFieldId pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static MetisDataFieldId getFieldForClass(final MoneyWiseAccountInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    @Override
    public MetisDataFieldId getFieldForClass(final PrometheusDataInfoClass pClass) {
        return getFieldForClass((MoneyWiseAccountInfoClass) pClass);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final MoneyWiseSecurityInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Resolve editSetLinks.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Loop through the items */
        for (MoneyWiseSecurityInfo myInfo : this) {
            myInfo.resolveEditSetLinks(pEditSet);
        }
    }

    /**
     * Obtain the region for the infoClass.
     * @param pInfoClass the Info Class
     * @return the deposit
     */
    public MoneyWiseRegion getRegion(final MoneyWiseAccountInfoClass pInfoClass) {
        /* Access existing entry */
        final MoneyWiseSecurityInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the region */
        return myValue.getRegion();
    }

    /**
     * Obtain the security for the infoClass.
     * @param pInfoClass the Info Class
     * @return the security
     */
    public MoneyWiseSecurity getSecurity(final MoneyWiseAccountInfoClass pInfoClass) {
        /* Access existing entry */
        final MoneyWiseSecurityInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the security */
        return myValue.getSecurity();
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public MetisFieldRequired isFieldRequired(final MetisDataFieldId pField) {
        final MoneyWiseAccountInfoClass myClass = getClassForField(pField);
        return myClass == null
                ? MetisFieldRequired.NOTALLOWED
                : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Security */
        final MoneyWiseSecurity mySec = getOwner();
        final MoneyWiseSecurityClass myType = mySec.getCategoryClass();

        /* If we have no Type, no class is allowed */
        if (myType == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
                return MetisFieldRequired.CANEXIST;

            /* Symbol */
            case SYMBOL:
                return myType.needsSymbol()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Region */
            case REGION:
                return myType.needsRegion()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Options */
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
                return myType.isOption()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Not Allowed */
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            case OPENINGBALANCE:
            case AUTOEXPENSE:
            case AUTOPAYEE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Loop through the classes */
        for (final MoneyWiseAccountInfoClass myClass : MoneyWiseAccountInfoClass.values()) {
            /* Access info for class */
            final MoneyWiseSecurityInfo myInfo = getInfo(myClass);

            /* If basic checks are passed */
            if (checkClass(myInfo, myClass)) {
                /* validate the class */
                validateClass(myInfo, myClass);
            }
        }
    }

    /**
     * Validate the class.
     * @param pInfo the info
     * @param pClass the infoClass
     */
    private void validateClass(final MoneyWiseSecurityInfo pInfo,
                               final MoneyWiseAccountInfoClass pClass) {
        /* Switch on class */
        switch (pClass) {
            case NOTES:
                validateNotes(pInfo);
                break;
            case SYMBOL:
                validateSymbol(pInfo);
                break;
            case UNDERLYINGSTOCK:
                validateUnderlyingStock(pInfo);
                break;
            case OPTIONPRICE:
                validateOptionPrice(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the Notes info.
     * @param pInfo the info
     */
    private void validateNotes(final MoneyWiseSecurityInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        if (myArray.length > MoneyWiseAccountInfoClass.NOTES.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(MoneyWiseAccountInfoClass.NOTES));
        }
    }

    /**
     * Validate the Symbol info.
     * @param pInfo the info
     */
    private void validateSymbol(final MoneyWiseSecurityInfo pInfo) {
        final String mySymbol = pInfo.getValue(String.class);
        if (mySymbol.length() > MoneyWiseAccountInfoClass.SYMBOL.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(MoneyWiseAccountInfoClass.SYMBOL));
        }
    }

    /**
     * Validate the UnderlyingStock info.
     * @param pInfo the info
     */
    private void validateUnderlyingStock(final MoneyWiseSecurityInfo pInfo) {
        final MoneyWiseSecurity myStock = pInfo.getValue(MoneyWiseSecurity.class);
        if (!myStock.getCategoryClass().isShares()) {
            getOwner().addError("Invalid underlying stock", getFieldForClass(MoneyWiseAccountInfoClass.UNDERLYINGSTOCK));
        }
    }

    /**
     * Validate the OptionPrice info.
     * @param pInfo the info
     */
    private void validateOptionPrice(final MoneyWiseSecurityInfo pInfo) {
        final OceanusPrice myPrice = pInfo.getValue(OceanusPrice.class);
        if (myPrice.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        } else if (!myPrice.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(MoneyWiseAccountInfoClass.OPTIONPRICE));
        }
    }
}
