/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Map;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * SecurityInfoSet class.
 * @author Tony Washer
 */
public class SecurityInfoSet
        extends DataInfoSet<SecurityInfo, Security, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.SECURITY_INFOSET.getValue(), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, AccountInfoClass> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, AccountInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<AccountInfoClass, MetisField> REVERSE_FIELDMAP = MetisFields.reverseFieldMap(FIELDSET_MAP, AccountInfoClass.class);

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
    public Object getFieldValue(final MetisField pField) {
        /* Handle InfoSet fields */
        AccountInfoClass myClass = getClassForField(pField);
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
        Object myValue;

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
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static AccountInfoClass getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static MetisField getFieldForClass(final AccountInfoClass pClass) {
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
        SecurityInfo myValue = getInfo(pInfoClass);

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
        SecurityInfo myValue = getInfo(pInfoClass);

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
    public MetisFieldRequired isFieldRequired(final MetisField pField) {
        AccountInfoClass myClass = getClassForField(pField);
        return myClass == null
                               ? MetisFieldRequired.NOTALLOWED
                               : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final AccountInfoClass pClass) {
        /* Access details about the Security */
        Security mySec = getOwner();
        SecurityTypeClass myType = mySec.getSecurityTypeClass();

        /* If we have no Type, no class is allowed */
        if (myType == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        /* Switch on class */
        switch (pClass) {
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
        /* Access details about the Security */
        Security mySecurity = getOwner();

        /* Loop through the classes */
        for (AccountInfoClass myClass : AccountInfoClass.values()) {
            /* Access info for class */
            SecurityInfo myInfo = getInfo(myClass);
            boolean isExisting = (myInfo != null) && !myInfo.isDeleted();

            /* Determine requirements for class */
            MetisFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == MetisFieldRequired.MUSTEXIST) {
                    mySecurity.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == MetisFieldRequired.NOTALLOWED) {
                mySecurity.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case NOTES:
                    /* Access data */
                    char[] myArray = myInfo.getValue(char[].class);
                    if (myArray.length > myClass.getMaximumLength()) {
                        mySecurity.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case SYMBOL:
                    /* Access data */
                    String mySymbol = myInfo.getValue(String.class);
                    if (mySymbol.length() > myClass.getMaximumLength()) {
                        mySecurity.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case UNDERLYINGSTOCK:
                    /* Access data */
                    Security myStock = myInfo.getValue(Security.class);
                    if (!myStock.getSecurityTypeClass().isShares()) {
                        mySecurity.addError("Invalid underlying stock", getFieldForClass(myClass));
                    }
                    break;
                case OPTIONPRICE:
                    /* Access data */
                    TethysPrice myPrice = myInfo.getValue(TethysPrice.class);
                    if (myPrice.isZero()) {
                        mySecurity.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myPrice.isPositive()) {
                        mySecurity.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
