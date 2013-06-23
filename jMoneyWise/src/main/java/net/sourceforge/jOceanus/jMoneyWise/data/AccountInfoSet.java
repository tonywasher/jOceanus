/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import java.util.Map;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataFieldRequired;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;

/**
 * AccountInfoSet class.
 * @author Tony Washer
 */
public class AccountInfoSet
        extends DataInfoSet<AccountInfo, Account, AccountInfoType, AccountInfoClass> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AccountInfoSet.class.getSimpleName(), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, AccountInfoClass> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, AccountInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<AccountInfoClass, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, AccountInfoClass.class);

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
            case Parent:
            case Alias:
            case Holding:
                /* Access account of object */
                myValue = getAccount(pInfoClass);
                break;
            case AutoExpense:
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
                : JDataFieldValue.SkipField;
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
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected AccountInfoSet(final Account pOwner,
                             final AccountInfoTypeList pTypeList,
                             final AccountInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final AccountInfoSet pSource) {
        /* Clone the dataInfoSet */
        super.cloneDataInfoSet(pSource);
    }

    /**
     * Obtain the account for the infoClass.
     * @param pInfoClass the Info Class
     * @return the account
     */
    public Account getAccount(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        AccountInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the account */
        return myValue.getAccount();
    }

    /**
     * Obtain the event category for the infoClass.
     * @param pInfoClass the Info Class
     * @return the event category
     */
    public EventCategory getEventCategory(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        AccountInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the event category */
        return myValue.getEventCategory();
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public JDataFieldRequired isFieldRequired(final JDataField pField) {
        AccountInfoClass myClass = getClassForField(pField);
        return myClass == null
                ? JDataFieldRequired.NotAllowed
                : isClassRequired(myClass);
    }

    /**
     * Determine if an infoSet class is required.
     * @param pClass the infoSet class
     * @return the status
     */
    protected JDataFieldRequired isClassRequired(final AccountInfoClass pClass) {
        /* Access details about the Account */
        Account myAccount = getOwner();
        AccountCategory myCategory = myAccount.getAccountCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return JDataFieldRequired.NotAllowed;
        }
        AccountCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
        /* Notes/Account are always available */
            case Notes:
            case SortCode:
            case Account:
            case Reference:
            case Comments:
                return JDataFieldRequired.CanExist;

                /* Handle Institution Details */
            case WebSite:
            case CustomerNo:
            case UserId:
            case Password:
                return myClass.isNonAsset()
                        ? JDataFieldRequired.CanExist
                        : JDataFieldRequired.NotAllowed;

                /* Parent */
            case Parent:
                return myClass.isChild()
                        ? JDataFieldRequired.MustExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle Alias */
            case Alias:
                return myClass.canAlias()
                        ? JDataFieldRequired.CanExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle Holding */
            case Holding:
                return (myClass == AccountCategoryClass.Portfolio)
                        ? JDataFieldRequired.MustExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle Maturity */
            case Maturity:
                return (myClass == AccountCategoryClass.Bond)
                        ? JDataFieldRequired.MustExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle Symbol */
            case Symbol:
                return (myClass.hasUnits() && (myAccount.getAlias() == null))
                        ? JDataFieldRequired.MustExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle OpeningBalance */
            case OpeningBalance:
                return myClass.isSavings()
                        ? JDataFieldRequired.CanExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle AutoExpense */
            case AutoExpense:
                return myClass.isCash()
                        ? JDataFieldRequired.CanExist
                        : JDataFieldRequired.NotAllowed;

                /* Handle all other fields */
            default:
                return JDataFieldRequired.MustExist;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Account */
        Account myAccount = getOwner();

        /* Loop through the classes */
        for (AccountInfoClass myClass : AccountInfoClass.values()) {
            /* Access info for class */
            AccountInfo myInfo = getInfo(myClass);
            boolean isExisting = (myInfo != null)
                                 && !myInfo.isDeleted();

            /* Determine requirements for class */
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MustExist) {
                    myAccount.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NotAllowed) {
                myAccount.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case WebSite:
                case CustomerNo:
                case UserId:
                case Password:
                case SortCode:
                case Account:
                case Notes:
                    /* Access data */
                    char[] myArray = myInfo.getValue(char[].class);
                    if (myArray.length > myClass.getMaximumLength()) {
                        myAccount.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case Reference:
                case Comments:
                    /* Access data */
                    String myString = myInfo.getValue(String.class);
                    if (myString.length() > myClass.getMaximumLength()) {
                        myAccount.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case Parent:
                    /* Access parent */
                    Account myParent = myInfo.getAccount();

                    /* check that any parent is owner */
                    if (!myParent.getAccountCategoryClass().canParentAccount()) {
                        myAccount.addError("Parent account cannot have children", getFieldForClass(myClass));
                    }

                    /* If we are open then parent must be open */
                    if (!myAccount.isClosed()
                        && myParent.isClosed()) {
                        myAccount.addError("Parent account must not be closed", getFieldForClass(myClass));
                    }

                    /* If we have Units then parent must be portfolio */
                    if (myAccount.hasUnits()
                        && (myParent.getAccountCategoryClass() != AccountCategoryClass.Portfolio)) {
                        myAccount.addError("Parent account must be portfolio", getFieldForClass(myClass));
                    }
                    break;
                case Alias:
                    /* Access Alias account */
                    Account myAlias = myInfo.getAccount();
                    AccountCategoryClass myAliasClass = myAlias.getAccountCategoryClass();

                    /* Cannot alias to self */
                    if (Difference.isEqual(myAccount, myAlias)) {
                        myAccount.addError("Cannot alias to self", getFieldForClass(myClass));

                        /* Must alias to same type */
                    } else if (!Difference.isEqual(myAccount.getAccountCategoryClass(), myAliasClass)) {
                        myAccount.addError("Must alias to same account category", getFieldForClass(myClass));

                        /* Must alias to different TaxFree type */
                    } else if (myAccount.isTaxFree().equals(myAlias.isTaxFree())) {
                        myAccount.addError("Must alias to different TaxFree account type", getFieldForClass(myClass));
                    }

                    /* Must not be aliased to */
                    if (myAccount.isAliasedTo()) {
                        myAccount.addError("This account is already aliased to", getFieldForClass(myClass));
                    }

                    /* Alias cannot be aliased */
                    if (myAlias.isAlias()) {
                        myAccount.addError("The alias account is already aliased", getFieldForClass(myClass));
                    }

                    /* Must not have prices */
                    AccountStatus myStatus = myAccount.getStatus();
                    if (myStatus.hasPrices()) {
                        myAccount.addError("Aliased account has prices", getFieldForClass(myClass));
                    }

                    /* Alias account must have prices */
                    AccountStatus myAliasStatus = myAlias.getStatus();
                    if ((!myAliasStatus.hasPrices())
                        && (myAliasStatus.hasEvents())) {
                        myAccount.addError("Alias account has no prices", getFieldForClass(myClass));
                    }
                    break;
                case Holding:
                    /* Access parent */
                    Account myHolding = myInfo.getAccount();

                    /* check that holding account is savings */
                    if (!myHolding.isSavings()) {
                        myAccount.addError("Holding account must be a savings account", getFieldForClass(myClass));
                    }

                    /* If we are open then parent must be open */
                    if (!myAccount.isClosed()
                        && myHolding.isClosed()) {
                        myAccount.addError("Holding account must not be closed", getFieldForClass(myClass));
                    }

                    /* We must be parent of holding account */
                    if (!myAccount.equals(myHolding.getParent())) {
                        myAccount.addError("Holding account must be child of this account", getFieldForClass(myClass));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
