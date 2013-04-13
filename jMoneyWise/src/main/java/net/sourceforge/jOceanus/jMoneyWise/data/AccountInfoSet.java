/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCurrency;
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

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Maturity Field Id.
     */
    public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareLocalField(AccountInfoClass.Maturity.toString());

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareLocalField(AccountInfoClass.Parent.toString());

    /**
     * Alias Field Id.
     */
    public static final JDataField FIELD_ALIAS = FIELD_DEFS.declareLocalField(AccountInfoClass.Alias.toString());

    /**
     * Currency Field Id.
     */
    public static final JDataField FIELD_CURRENCY = FIELD_DEFS.declareLocalField(AccountInfoClass.Currency.toString());

    /**
     * AutoExpense Field Id.
     */
    public static final JDataField FIELD_AUTOEXP = FIELD_DEFS.declareLocalField(AccountInfoClass.AutoExpense.toString());

    /**
     * Symbol Field Id.
     */
    public static final JDataField FIELD_SYMBOL = FIELD_DEFS.declareLocalField(AccountInfoClass.Symbol.toString());

    /**
     * OpeningBalance Field Id.
     */
    public static final JDataField FIELD_OPENBAL = FIELD_DEFS.declareLocalField(AccountInfoClass.OpeningBalance.toString());

    /**
     * WebSite Field Id.
     */
    public static final JDataField FIELD_WEBSITE = FIELD_DEFS.declareLocalField(AccountInfoClass.WebSite.toString());

    /**
     * CustNo Field Id.
     */
    public static final JDataField FIELD_CUSTNO = FIELD_DEFS.declareLocalField(AccountInfoClass.CustomerNo.toString());

    /**
     * UserId Field Id.
     */
    public static final JDataField FIELD_USERID = FIELD_DEFS.declareLocalField(AccountInfoClass.UserId.toString());

    /**
     * Password Field Id.
     */
    public static final JDataField FIELD_PASSWORD = FIELD_DEFS.declareLocalField(AccountInfoClass.Password.toString());

    /**
     * Account Details Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField(AccountInfoClass.Account.toString());

    /**
     * Notes Field Id.
     */
    public static final JDataField FIELD_NOTES = FIELD_DEFS.declareLocalField(AccountInfoClass.Notes.toString());

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle InfoSet fields */
        AccountInfoClass myClass = getFieldClass(pField);
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
                /* Access account of object */
                myValue = getAccount(pInfoClass);
                break;
            case AutoExpense:
                /* Access event category of object */
                myValue = getEventCategory(pInfoClass);
                break;
            case Currency:
                /* Access currency of object */
                myValue = getAccountCurrency(pInfoClass);
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
    protected static AccountInfoClass getFieldClass(final JDataField pField) {
        if (FIELD_MATURITY.equals(pField)) {
            return AccountInfoClass.Maturity;
        }
        if (FIELD_PARENT.equals(pField)) {
            return AccountInfoClass.Parent;
        }
        if (FIELD_ALIAS.equals(pField)) {
            return AccountInfoClass.Alias;
        }
        if (FIELD_CURRENCY.equals(pField)) {
            return AccountInfoClass.Currency;
        }
        if (FIELD_AUTOEXP.equals(pField)) {
            return AccountInfoClass.AutoExpense;
        }
        if (FIELD_SYMBOL.equals(pField)) {
            return AccountInfoClass.Symbol;
        }
        if (FIELD_OPENBAL.equals(pField)) {
            return AccountInfoClass.OpeningBalance;
        }
        if (FIELD_WEBSITE.equals(pField)) {
            return AccountInfoClass.WebSite;
        }
        if (FIELD_CUSTNO.equals(pField)) {
            return AccountInfoClass.CustomerNo;
        }
        if (FIELD_USERID.equals(pField)) {
            return AccountInfoClass.UserId;
        }
        if (FIELD_PASSWORD.equals(pField)) {
            return AccountInfoClass.Password;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return AccountInfoClass.Account;
        }
        if (FIELD_NOTES.equals(pField)) {
            return AccountInfoClass.Notes;
        }
        return null;
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
     * Obtain the currency for the infoClass.
     * @param pInfoClass the Info Class
     * @return the account currency
     */
    public AccountCurrency getAccountCurrency(final AccountInfoClass pInfoClass) {
        /* Access existing entry */
        AccountInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the account currency */
        return myValue.getAccountCurrency();
    }
}
