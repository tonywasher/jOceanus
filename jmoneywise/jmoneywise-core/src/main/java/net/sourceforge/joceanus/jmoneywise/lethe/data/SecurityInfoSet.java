/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * SecurityInfoSet class.
 * @author Tony Washer
 */
public class SecurityInfoSet
        extends DataInfoSet<SecurityInfo> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.SECURITY_INFOSET.getValue(), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<MetisLetheField, AccountInfoClass> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, AccountInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<AccountInfoClass, MetisLetheField> REVERSE_FIELDMAP = MetisFields.reverseFieldMap(FIELDSET_MAP, AccountInfoClass.class);

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected SecurityInfoSet(final Security pOwner,
                              final AccountInfoTypeList pTypeList,
                              final SecurityInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Security getOwner() {
        return (Security) super.getOwner();
    }

    @Override
    public Object getFieldValue(final MetisLetheField pField) {
        /* Handle InfoSet fields */
        final AccountInfoClass myClass = getClassForField(pField);
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
    private Object getInfoSetValue(final AccountInfoClass pInfoClass) {
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
    public static AccountInfoClass getClassForField(final MetisLetheField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static MetisLetheField getFieldForClass(final AccountInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final SecurityInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Obtain the region for the infoClass.
     * @param pInfoClass the Info Class
     * @return the deposit
     */
    public Region getRegion(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        final SecurityInfo myValue = getInfo(pInfoClass);

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
    public Security getSecurity(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        final SecurityInfo myValue = getInfo(pInfoClass);

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
    public MetisFieldRequired isFieldRequired(final MetisLetheField pField) {
        final AccountInfoClass myClass = getClassForField(pField);
        return myClass == null
                               ? MetisFieldRequired.NOTALLOWED
                               : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final DataInfoClass pClass) {
        /* Access details about the Security */
        final Security mySec = getOwner();
        final SecurityTypeClass myType = mySec.getCategoryClass();

        /* If we have no Type, no class is allowed */
        if (myType == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        /* Switch on class */
        switch ((AccountInfoClass) pClass) {
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
        for (final AccountInfoClass myClass : AccountInfoClass.values()) {
            /* validate the class */
            validateClass(myClass);
        }
    }

    /**
     * Validate the class.
     * @param pClass the infoClass
     */
    private void validateClass(final AccountInfoClass pClass) {
        /* Access details about the Security */
        final Security mySecurity = getOwner();

        /* Access info for class */
        final SecurityInfo myInfo = getInfo(pClass);
        final boolean isExisting = myInfo != null
                                   && !myInfo.isDeleted();

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                mySecurity.addError(DataItem.ERROR_MISSING, getFieldForClass(pClass));
            }
            return;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            mySecurity.addError(DataItem.ERROR_EXIST, getFieldForClass(pClass));
            return;
        }

        /* Switch on class */
        switch (pClass) {
            case NOTES:
                /* Access data */
                final char[] myArray = myInfo.getValue(char[].class);
                if (myArray.length > pClass.getMaximumLength()) {
                    mySecurity.addError(DataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            case SYMBOL:
                /* Access data */
                final String mySymbol = myInfo.getValue(String.class);
                if (mySymbol.length() > pClass.getMaximumLength()) {
                    mySecurity.addError(DataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            case UNDERLYINGSTOCK:
                /* Access data */
                final Security myStock = myInfo.getValue(Security.class);
                if (!myStock.getCategoryClass().isShares()) {
                    mySecurity.addError("Invalid underlying stock", getFieldForClass(pClass));
                }
                break;
            case OPTIONPRICE:
                /* Access data */
                final TethysPrice myPrice = myInfo.getValue(TethysPrice.class);
                if (myPrice.isZero()) {
                    mySecurity.addError(DataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (!myPrice.isPositive()) {
                    mySecurity.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                }
                break;
            default:
                break;
        }
    }
}
