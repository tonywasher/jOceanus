/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * LoanInfoSet class.
 * @author Tony Washer
 */
public class LoanInfoSet
        extends DataInfoSet<LoanInfo, Loan, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.LOAN_INFOSET.getValue(), DataInfoSet.FIELD_DEFS);

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
    protected LoanInfoSet(final Loan pOwner,
                          final AccountInfoTypeList pTypeList,
                          final LoanInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
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
        /* Return the value */
        final Object myValue = getField(pInfoClass);
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
    protected void cloneDataInfoSet(final LoanInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
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
    public MetisFieldRequired isClassRequired(final AccountInfoClass pClass) {
        /* Access details about the Loan */
        final Loan myLoan = getOwner();
        final LoanCategory myCategory = myLoan.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch (pClass) {
            /* Allowed set */
            case NOTES:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case OPENINGBALANCE:
                return MetisFieldRequired.CANEXIST;

            /* Not allowd */
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            case AUTOEXPENSE:
            case AUTOPAYEE:
            case SYMBOL:
            case REGION:
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
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
        /* Access details about the Loan */
        final Loan myLoan = getOwner();

        /* Access info for class */
        final LoanInfo myInfo = getInfo(pClass);
        final boolean isExisting = myInfo != null
                                   && !myInfo.isDeleted();

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                myLoan.addError(DataItem.ERROR_MISSING, getFieldForClass(pClass));
            }
            return;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            myLoan.addError(DataItem.ERROR_EXIST, getFieldForClass(pClass));
            return;
        }

        /* Switch on class */
        switch (pClass) {
            case OPENINGBALANCE:
                /* Access data */
                final TethysMoney myBalance = myInfo.getValue(TethysMoney.class);
                if (!myBalance.getCurrency().equals(myLoan.getCurrency())) {
                    myLoan.addError(DepositInfoSet.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case SORTCODE:
            case ACCOUNT:
            case NOTES:
            case REFERENCE:
                /* Access data */
                final char[] myArray = myInfo.getValue(char[].class);
                if (myArray.length > pClass.getMaximumLength()) {
                    myLoan.addError(DataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            default:
                break;
        }
    }
}
