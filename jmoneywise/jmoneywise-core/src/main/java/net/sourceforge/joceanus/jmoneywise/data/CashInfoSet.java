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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory.TransactionCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList.DataListSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * CashInfoSet class.
 * @author Tony Washer
 */
public class CashInfoSet
        extends DataInfoSet<CashInfo, Cash, AccountInfoType, AccountInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.CASH_INFOSET.getValue(), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, AccountInfoClass> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, AccountInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<AccountInfoClass, MetisField> REVERSE_FIELDMAP = MetisFields.reverseFieldMap(FIELDSET_MAP, AccountInfoClass.class);

    /**
     * AutoExpense Not Expense Error Text.
     */
    private static final String ERROR_AUTOEXP = MoneyWiseDataResource.CASH_ERROR_AUTOEXPENSE.getValue();

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected CashInfoSet(final Cash pOwner,
                          final AccountInfoTypeList pTypeList,
                          final CashInfoList pInfoList) {
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
            case AUTOPAYEE:
                /* Access payee of object */
                myValue = getPayee(pInfoClass);
                break;
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
     * Obtain the payee for the infoClass.
     * @param pInfoClass the Info Class
     * @return the payee
     */
    public Payee getPayee(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        CashInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the payee */
        return myValue.getPayee();
    }

    /**
     * Obtain the event category for the infoClass.
     * @param pInfoClass the Info Class
     * @return the event category
     */
    public TransactionCategory getEventCategory(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        CashInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the event category */
        return myValue.getEventCategory();
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final CashInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
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
        /* Access details about the Cash */
        Cash myCash = getOwner();
        CashCategory myCategory = myCash.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch (pClass) {
        /* Allowed set */
            case NOTES:
                return MetisFieldRequired.CANEXIST;

            case OPENINGBALANCE:
                return myCash.isAutoExpense()
                                             ? MetisFieldRequired.NOTALLOWED
                                             : MetisFieldRequired.CANEXIST;
            case AUTOPAYEE:
            case AUTOEXPENSE:
                return myCash.isAutoExpense()
                                             ? MetisFieldRequired.MUSTEXIST
                                             : MetisFieldRequired.NOTALLOWED;

                /* Disallowed Set */
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Cash */
        Cash myCash = getOwner();

        /* Loop through the classes */
        for (AccountInfoClass myClass : AccountInfoClass.values()) {
            /* Access info for class */
            CashInfo myInfo = getInfo(myClass);
            boolean isExisting = (myInfo != null) && !myInfo.isDeleted();

            /* Determine requirements for class */
            MetisFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == MetisFieldRequired.MUSTEXIST) {
                    myCash.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == MetisFieldRequired.NOTALLOWED) {
                myCash.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case OPENINGBALANCE:
                    /* Access data */
                    TethysMoney myBalance = myInfo.getValue(TethysMoney.class);
                    if (!myBalance.getCurrency().equals(myCash.getCurrency())) {
                        myCash.addError(DepositInfoSet.ERROR_CURRENCY, getFieldForClass(myClass));
                    }
                    break;
                case AUTOEXPENSE:
                    /* Access data */
                    TransactionCategory myExpense = myInfo.getEventCategory();
                    TransactionCategoryClass myCatClass = myExpense.getCategoryTypeClass();
                    if (!myCatClass.isExpense() || myCatClass.canParentCategory()) {
                        myCash.addError(ERROR_AUTOEXP, getFieldForClass(myClass));
                    }
                    break;
                case NOTES:
                    /* Access data */
                    char[] myArray = myInfo.getValue(char[].class);
                    if (myArray.length > myClass.getMaximumLength()) {
                        myCash.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void setDefaultValue(final DataListSet<MoneyWiseDataType> pUpdateSet,
                                   final AccountInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch (pClass) {
            case AUTOEXPENSE:
                setValue(pClass, getDefaultAutoExpense(pUpdateSet));
                break;
            case AUTOPAYEE:
                setValue(pClass, getDefaultAutoPayee(pUpdateSet));
                break;
            default:
                break;
        }
    }

    /**
     * Obtain default expense for autoExpense cash.
     * @param pUpdateSet the updateSet
     * @return the default expense
     */
    private TransactionCategory getDefaultAutoExpense(final DataListSet<MoneyWiseDataType> pUpdateSet) {
        /* Access the category list */
        TransactionCategoryList myCategories = pUpdateSet.getDataList(MoneyWiseDataType.TRANSCATEGORY, TransactionCategoryList.class);

        /* loop through the categories */
        Iterator<TransactionCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            TransactionCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* Ignore categories that are the wrong class */
            TransactionCategoryClass myCatClass = myCategory.getCategoryTypeClass();
            if (myCatClass.isExpense() && !myCatClass.canParentCategory()) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default payee for autoExpense cash.
     * @param pUpdateSet the updateSet
     * @return the default payee
     */
    private Payee getDefaultAutoPayee(final DataListSet<MoneyWiseDataType> pUpdateSet) {
        /* Access the payee list */
        PayeeList myPayees = pUpdateSet.getDataList(MoneyWiseDataType.PAYEE, PayeeList.class);

        /* loop through the payees */
        Iterator<Payee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            Payee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (!myPayee.isDeleted() && !myPayee.isClosed()) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }
}
