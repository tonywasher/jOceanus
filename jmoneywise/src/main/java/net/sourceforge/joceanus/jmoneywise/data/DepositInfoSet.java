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
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * PayeeInfoSet class.
 * @author Tony Washer
 */
public class DepositInfoSet
        extends DataInfoSet<DepositInfo, Deposit, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(DepositInfoSet.class.getName());

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, AccountInfoClass> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<AccountInfoClass, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, AccountInfoClass.class);

    /**
     * Opening Balance Currency Error Text.
     */
    private static final String ERROR_BALANCE = NLS_BUNDLE.getString("ErrorBalance");

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
        Object myValue;

        switch (pInfoClass) {
            case AUTOEXPENSE:
                /* Access event category of object */
                myValue = getEventCategory(pInfoClass);
                break;
            default:
                /* Access value of object */
                myValue = getField(pInfoClass);
                break;
        }

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
     * Obtain the event category for the infoClass.
     * @param pInfoClass the Info Class
     * @return the event category
     */
    public EventCategory getEventCategory(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        DepositInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the event category */
        return myValue.getEventCategory();
    }

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected DepositInfoSet(final Deposit pOwner,
                             final AccountInfoTypeList pTypeList,
                             final DepositInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final DepositInfoSet pSource) {
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

    /**
     * Determine if an infoSet class is required.
     * @param pClass the infoSet class
     * @return the status
     */
    protected JDataFieldRequired isClassRequired(final AccountInfoClass pClass) {
        /* Access details about the Deposit */
        Deposit myDeposit = getOwner();
        AccountCategory myCategory = myDeposit.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return JDataFieldRequired.NOTALLOWED;
        }
        AccountCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
        /* Allowed set */
            case NOTES:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case COMMENTS:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
                return JDataFieldRequired.CANEXIST;

                /* Handle Maturity */
            case MATURITY:
                return (myClass == AccountCategoryClass.BOND)
                                                             ? JDataFieldRequired.MUSTEXIST
                                                             : JDataFieldRequired.NOTALLOWED;

                /* Handle OpeningBalance */
            case OPENINGBALANCE:
                return myClass.isDeposit()
                                          ? JDataFieldRequired.CANEXIST
                                          : JDataFieldRequired.NOTALLOWED;

                /* Handle AutoExpense */
            case AUTOEXPENSE:
                return myClass.isCash()
                                       ? JDataFieldRequired.CANEXIST
                                       : JDataFieldRequired.NOTALLOWED;

                /* Old style */
            case PARENT:
            case PORTFOLIO:
            case ALIAS:
            case HOLDING:
            case SYMBOL:
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Deposit */
        Deposit myDeposit = getOwner();

        /* Loop through the classes */
        for (AccountInfoClass myClass : AccountInfoClass.values()) {
            /* Access info for class */
            DepositInfo myInfo = getInfo(myClass);
            boolean isExisting = (myInfo != null) && !myInfo.isDeleted();

            /* Determine requirements for class */
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MUSTEXIST) {
                    myDeposit.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NOTALLOWED) {
                myDeposit.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case OPENINGBALANCE:
                    /* Access data */
                    JMoney myBalance = myInfo.getValue(JMoney.class);
                    if (!myBalance.getCurrency().equals(myDeposit.getDepositCurrency().getCurrency())) {
                        myDeposit.addError(ERROR_BALANCE, getFieldForClass(myClass));
                    }
                    break;
                case WEBSITE:
                case CUSTOMERNO:
                case USERID:
                case PASSWORD:
                case SORTCODE:
                case ACCOUNT:
                case NOTES:
                    /* Access data */
                    char[] myArray = myInfo.getValue(char[].class);
                    if (myArray.length > myClass.getMaximumLength()) {
                        myDeposit.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case REFERENCE:
                case COMMENTS:
                    /* Access data */
                    String myString = myInfo.getValue(String.class);
                    if (myString.length() > myClass.getMaximumLength()) {
                        myDeposit.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
