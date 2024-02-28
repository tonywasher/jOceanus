/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.data.basic;

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

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
        final Iterator<MoneyWiseSecurityInfo> myIterator = iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseSecurityInfo myInfo = myIterator.next();
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
            /* validate the class */
            validateClass(myClass);
        }
    }

    /**
     * Validate the class.
     * @param pClass the infoClass
     */
    private void validateClass(final MoneyWiseAccountInfoClass pClass) {
        /* Access details about the Security */
        final MoneyWiseSecurity mySecurity = getOwner();

        /* Access info for class */
        final MoneyWiseSecurityInfo myInfo = getInfo(pClass);
        final boolean isExisting = myInfo != null
                && !myInfo.isDeleted();

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                mySecurity.addError(PrometheusDataItem.ERROR_MISSING, getFieldForClass(pClass));
            }
            return;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            mySecurity.addError(PrometheusDataItem.ERROR_EXIST, getFieldForClass(pClass));
            return;
        }

        /* Switch on class */
        switch (pClass) {
            case NOTES:
                /* Access data */
                final char[] myArray = myInfo.getValue(char[].class);
                if (myArray.length > pClass.getMaximumLength()) {
                    mySecurity.addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            case SYMBOL:
                /* Access data */
                final String mySymbol = myInfo.getValue(String.class);
                if (mySymbol.length() > pClass.getMaximumLength()) {
                    mySecurity.addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            case UNDERLYINGSTOCK:
                /* Access data */
                final MoneyWiseSecurity myStock = myInfo.getValue(MoneyWiseSecurity.class);
                if (!myStock.getCategoryClass().isShares()) {
                    mySecurity.addError("Invalid underlying stock", getFieldForClass(pClass));
                }
                break;
            case OPTIONPRICE:
                /* Access data */
                final TethysPrice myPrice = myInfo.getValue(TethysPrice.class);
                if (myPrice.isZero()) {
                    mySecurity.addError(PrometheusDataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (!myPrice.isPositive()) {
                    mySecurity.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                }
                break;
            default:
                break;
        }
    }
}
