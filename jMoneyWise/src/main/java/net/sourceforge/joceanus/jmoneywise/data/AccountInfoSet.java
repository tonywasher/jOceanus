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

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo.AccountInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;

/**
 * AccountInfoSet class.
 * @author Tony Washer
 */
public class AccountInfoSet
        extends DataInfoSet<AccountInfo, Account, AccountInfoType, AccountInfoClass> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountInfoSet.class.getName());

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

    /**
     * Parent Not Market Error Text.
     */
    private static final String ERROR_PARMARKET = NLS_BUNDLE.getString("ErrorParentMarket");

    /**
     * Parent Invalid Error Text.
     */
    private static final String ERROR_PARBAD = NLS_BUNDLE.getString("ErrorBadParent");

    /**
     * Parent Closed Error Text.
     */
    private static final String ERROR_PARCLOSED = NLS_BUNDLE.getString("ErrorParentClosed");

    /**
     * Alias Self Error Text.
     */
    private static final String ERROR_ALSSELF = NLS_BUNDLE.getString("ErrorAliasSelf");

    /**
     * Alias Category Error Text.
     */
    private static final String ERROR_ALSCATEGORY = NLS_BUNDLE.getString("ErrorAliasCategory");

    /**
     * Alias Tax Error Text.
     */
    private static final String ERROR_ALSTAX = NLS_BUNDLE.getString("ErrorAliasTax");

    /**
     * Aliased To Error Text.
     */
    private static final String ERROR_ALSTO = NLS_BUNDLE.getString("ErrorAliasedTo");

    /**
     * IsAliased Error Text.
     */
    private static final String ERROR_ISALIAS = NLS_BUNDLE.getString("ErrorIsAlias");

    /**
     * Aliased Prices Error Text.
     */
    private static final String ERROR_ALSEDPRICES = NLS_BUNDLE.getString("ErrorAliasedPrices");

    /**
     * Alias No Prices Error Text.
     */
    private static final String ERROR_ALSNOPRICES = NLS_BUNDLE.getString("ErrorAliasNoPrices");

    /**
     * Portfolio Invalid Error Text.
     */
    private static final String ERROR_BADPORT = NLS_BUNDLE.getString("ErrorBadPortfolio");

    /**
     * Portfolio Closed Error Text.
     */
    private static final String ERROR_PORTCLOSED = NLS_BUNDLE.getString("ErrorPortClosed");

    /**
     * Holding Child Error Text.
     */
    private static final String ERROR_HOLDCHILD = NLS_BUNDLE.getString("ErrorHoldChild");

    /**
     * Holding Invalid Error Text.
     */
    private static final String ERROR_BADHOLD = NLS_BUNDLE.getString("ErrorBadHolding");

    /**
     * Holding Closed Error Text.
     */
    private static final String ERROR_HOLDCLOSED = NLS_BUNDLE.getString("ErrorHoldClosed");

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
            case PARENT:
            case ALIAS:
            case PORTFOLIO:
            case HOLDING:
                /* Access account of object */
                myValue = getAccount(pInfoClass);
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
        cloneTheDataInfoSet(pSource);
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
                ? JDataFieldRequired.NOTALLOWED
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
            return JDataFieldRequired.NOTALLOWED;
        }
        AccountCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
        /* Notes/Account are always available */
            case NOTES:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case COMMENTS:
                return JDataFieldRequired.CANEXIST;

                /* Handle Institution Details */
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
                return myClass.isNonAsset()
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Parent */
            case PARENT:
                return myClass.isChild()
                        ? JDataFieldRequired.MUSTEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle Alias */
            case ALIAS:
                return myClass.canAlias()
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle Portfolio */
            case PORTFOLIO:
                return (myClass.hasUnits())
                        ? JDataFieldRequired.MUSTEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle Holding */
            case HOLDING:
                return (myClass == AccountCategoryClass.PORTFOLIO)
                        ? JDataFieldRequired.MUSTEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle Maturity */
            case MATURITY:
                return (myClass == AccountCategoryClass.BOND)
                        ? JDataFieldRequired.MUSTEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle Symbol */
            case SYMBOL:
                return (myClass.hasUnits() && (myAccount.getAlias() == null))
                        ? JDataFieldRequired.MUSTEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle OpeningBalance */
            case OPENINGBALANCE:
                return myClass.isSavings()
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle AutoExpense */
            case AUTOEXPENSE:
                return myClass.isCash()
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle all other fields */
            default:
                return JDataFieldRequired.MUSTEXIST;
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
                if (myState == JDataFieldRequired.MUSTEXIST) {
                    myAccount.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NOTALLOWED) {
                myAccount.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case OPENINGBALANCE:
                    /* Access data */
                    JMoney myBalance = myInfo.getValue(JMoney.class);
                    if (!myBalance.getCurrency().equals(myAccount.getAccountCurrency().getCurrency())) {
                        myAccount.addError(ERROR_BALANCE, getFieldForClass(myClass));
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
                        myAccount.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case REFERENCE:
                case COMMENTS:
                    /* Access data */
                    String myString = myInfo.getValue(String.class);
                    if (myString.length() > myClass.getMaximumLength()) {
                        myAccount.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case PARENT:
                    /* Access parent */
                    Account myParent = myInfo.getAccount();

                    /* If the account needs a market parent */
                    if (myAccount.getAccountCategoryClass().needsMarketParent()) {
                        if (!myParent.isCategoryClass(AccountCategoryClass.MARKET)) {
                            myAccount.addError(ERROR_PARMARKET, getFieldForClass(myClass));
                        }

                        /* else check that any parent is owner */
                    } else if (!myParent.getAccountCategoryClass().canParentAccount()) {
                        myAccount.addError(ERROR_PARBAD, getFieldForClass(myClass));
                    }

                    /* If we are open then parent must be open */
                    if (!myAccount.isClosed()
                        && myParent.isClosed()) {
                        myAccount.addError(ERROR_PARCLOSED, getFieldForClass(myClass));
                    }
                    break;
                case ALIAS:
                    /* Access Alias account */
                    Account myAlias = myInfo.getAccount();
                    AccountCategoryClass myAliasClass = myAlias.getAccountCategoryClass();

                    /* Cannot alias to self */
                    if (Difference.isEqual(myAccount, myAlias)) {
                        myAccount.addError(ERROR_ALSSELF, getFieldForClass(myClass));

                        /* Must alias to same type */
                    } else if (!Difference.isEqual(myAccount.getAccountCategoryClass(), myAliasClass)) {
                        myAccount.addError(ERROR_ALSCATEGORY, getFieldForClass(myClass));

                        /* Must alias to different TaxFree type */
                    } else if (myAccount.isTaxFree().equals(myAlias.isTaxFree())) {
                        myAccount.addError(ERROR_ALSTAX, getFieldForClass(myClass));
                    }

                    /* Must not be aliased to */
                    if (myAccount.isAliasedTo()) {
                        myAccount.addError(ERROR_ALSTO, getFieldForClass(myClass));
                    }

                    /* Alias cannot be aliased */
                    if (myAlias.isAlias()) {
                        myAccount.addError(ERROR_ISALIAS, getFieldForClass(myClass));
                    }

                    /* Must not have prices */
                    AccountStatus myStatus = myAccount.getStatus();
                    if (myStatus.hasPrices()) {
                        myAccount.addError(ERROR_ALSEDPRICES, getFieldForClass(myClass));
                    }

                    /* Alias account must have prices */
                    AccountStatus myAliasStatus = myAlias.getStatus();
                    if ((!myAliasStatus.hasPrices())
                        && (myAliasStatus.hasEvents())) {
                        myAccount.addError(ERROR_ALSNOPRICES, getFieldForClass(myClass));
                    }
                    break;
                case PORTFOLIO:
                    /* Access portfolio */
                    Account myPortfolio = myInfo.getAccount();

                    /* check that portfolio account is portfolio */
                    if (!myPortfolio.isCategoryClass(AccountCategoryClass.PORTFOLIO)) {
                        myAccount.addError(ERROR_BADPORT, getFieldForClass(myClass));
                    }

                    /* If we are open then portfolio must be open */
                    if (!myAccount.isClosed()
                        && myPortfolio.isClosed()) {
                        myAccount.addError(ERROR_PORTCLOSED, getFieldForClass(myClass));
                    }
                    break;
                case HOLDING:
                    /* Access holding account */
                    Account myHolding = myInfo.getAccount();

                    /* check that holding account is savings */
                    if (!myHolding.isSavings()) {
                        myAccount.addError(ERROR_BADHOLD, getFieldForClass(myClass));
                    }

                    /* If we are open then holding account must be open */
                    if (!myAccount.isClosed()
                        && myHolding.isClosed()) {
                        myAccount.addError(ERROR_HOLDCLOSED, getFieldForClass(myClass));
                    }

                    /* We must be parent of holding account */
                    if (!myAccount.equals(myHolding.getParent())) {
                        myAccount.addError(ERROR_HOLDCHILD, getFieldForClass(myClass));
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
