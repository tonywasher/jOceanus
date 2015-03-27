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

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;

/**
 * SecurityInfoSet class.
 * @author Tony Washer
 */
public class SecurityInfoSet
        extends DataInfoSet<SecurityInfo, Security, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseDataResource.SECURITY_INFOSET.getValue(), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, AccountInfoClass> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<AccountInfoClass, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, AccountInfoClass.class);

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
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
        /* Return the value */
        Object myValue = getField(pInfoClass);
        return (myValue != null)
                                ? myValue
                                : JDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static AccountInfoClass getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static JDataField getFieldForClass(final AccountInfoClass pClass) {
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
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public JDataFieldRequired isFieldRequired(final JDataField pField) {
        AccountInfoClass myClass = getClassForField(pField);
        return myClass == null
                              ? JDataFieldRequired.NOTALLOWED
                              : isClassRequired(myClass);
    }

    @Override
    public JDataFieldRequired isClassRequired(final AccountInfoClass pClass) {
        /* Switch on class */
        switch (pClass) {
        /* Allowed set */
            case NOTES:
                return JDataFieldRequired.CANEXIST;

                /* Old style */
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
            default:
                return JDataFieldRequired.NOTALLOWED;
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
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MUSTEXIST) {
                    mySecurity.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NOTALLOWED) {
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
                default:
                    break;
            }
        }
    }
}
