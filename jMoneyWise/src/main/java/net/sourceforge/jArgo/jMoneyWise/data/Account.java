/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.data;

import java.util.Date;
import java.util.Iterator;

import net.sourceforge.JDataManager.DataState;
import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataList.ListStyle;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.data.EncryptedItem;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JFinanceApp.data.Event.EventList;
import net.sourceforge.JFinanceApp.data.FinanceData.LoadState;
import net.sourceforge.JFinanceApp.data.statics.AccountType;
import net.sourceforge.JFinanceApp.data.statics.AccountType.AccountTypeList;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedCharArray;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedValueSet;

/**
 * Account data type.
 * @author Tony Washer
 */
public class Account extends EncryptedItem implements Comparable<Account> {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = Account.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * Name Field Id.
     */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField("Name");

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");

    /**
     * AccountType Field Id.
     */
    public static final JDataField FIELD_TYPE = FIELD_DEFS.declareEqualityValueField("AccountType");

    /**
     * Maturity Field Id.
     */
    public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareDerivedValueField("Maturity");

    /**
     * Close Field Id.
     */
    public static final JDataField FIELD_CLOSE = FIELD_DEFS.declareEqualityValueField("CloseDate");

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField("Parent");

    /**
     * Alias Field Id.
     */
    public static final JDataField FIELD_ALIAS = FIELD_DEFS.declareEqualityValueField("Alias");

    /**
     * WebSite Field Id.
     */
    public static final JDataField FIELD_WEBSITE = FIELD_DEFS.declareEqualityValueField("WebSite");

    /**
     * CustNo Field Id.
     */
    public static final JDataField FIELD_CUSTNO = FIELD_DEFS.declareEqualityValueField("CustomerNo");

    /**
     * UserId Field Id.
     */
    public static final JDataField FIELD_USERID = FIELD_DEFS.declareEqualityValueField("UserId");

    /**
     * Password Field Id.
     */
    public static final JDataField FIELD_PASSWORD = FIELD_DEFS.declareEqualityValueField("Password");

    /**
     * Account Details Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField("Account");

    /**
     * Notes Field Id.
     */
    public static final JDataField FIELD_NOTES = FIELD_DEFS.declareEqualityValueField("Notes");

    /**
     * firstEvent Field Id.
     */
    public static final JDataField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField("FirstEvent");

    /**
     * lastEvent Field Id.
     */
    public static final JDataField FIELD_EVTLAST = FIELD_DEFS.declareLocalField("LastEvent");

    /**
     * initialPrice Field Id.
     */
    public static final JDataField FIELD_INITPRC = FIELD_DEFS.declareLocalField("InitialPrice");

    /**
     * hasDebts Field Id.
     */
    public static final JDataField FIELD_HASDEBTS = FIELD_DEFS.declareLocalField("hasDebts");

    /**
     * hasRates Field Id.
     */
    public static final JDataField FIELD_HASRATES = FIELD_DEFS.declareLocalField("hasRates");

    /**
     * hasPrice Field Id.
     */
    public static final JDataField FIELD_HASPRICE = FIELD_DEFS.declareLocalField("hasPrices");

    /**
     * hasPatterns Field Id.
     */
    public static final JDataField FIELD_HASPATT = FIELD_DEFS.declareLocalField("hasPatterns");

    /**
     * isPatterned Field Id.
     */
    public static final JDataField FIELD_ISPATT = FIELD_DEFS.declareLocalField("isPatterned");

    /**
     * isParent Field Id.
     */
    public static final JDataField FIELD_ISPARENT = FIELD_DEFS.declareLocalField("isParent");

    /**
     * isAliased Field Id.
     */
    public static final JDataField FIELD_ISALIASD = FIELD_DEFS.declareLocalField("isAliasedTo");

    /**
     * isCloseable Field Id.
     */
    public static final JDataField FIELD_ISCLSABL = FIELD_DEFS.declareLocalField("isCloseable");

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_EVTFIRST.equals(pField)) {
            return theEarliest;
        }
        if (FIELD_EVTLAST.equals(pField)) {
            return theLatest;
        }
        if (FIELD_INITPRC.equals(pField)) {
            return theInitPrice;
        }
        if (FIELD_HASDEBTS.equals(pField)) {
            return hasDebts;
        }
        if (FIELD_HASRATES.equals(pField)) {
            return hasRates;
        }
        if (FIELD_HASPRICE.equals(pField)) {
            return hasPrices;
        }
        if (FIELD_HASPATT.equals(pField)) {
            return hasPatterns;
        }
        if (FIELD_ISPATT.equals(pField)) {
            return isPatterned;
        }
        if (FIELD_ISPARENT.equals(pField)) {
            return isParent;
        }
        if (FIELD_ISALIASD.equals(pField)) {
            return isAliasedTo;
        }
        if (FIELD_ISCLSABL.equals(pField)) {
            return isCloseable;
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Account Name length.
     */
    public static final int NAMELEN = 30;

    /**
     * Account Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Account WebSite length.
     */
    public static final int WSITELEN = 50;

    /**
     * Account CustNo length.
     */
    public static final int CUSTLEN = 20;

    /**
     * Account UserId length.
     */
    public static final int UIDLEN = 20;

    /**
     * Account PassWord length.
     */
    public static final int PWDLEN = 20;

    /**
     * Account details length.
     */
    public static final int ACTLEN = 20;

    /**
     * Account Notes length.
     */
    public static final int NOTELEN = 500;

    /**
     * Earliest Event.
     */
    private Event theEarliest = null;

    /**
     * Latest Event.
     */
    private Event theLatest = null;

    /**
     * Initial Price.
     */
    private AccountPrice theInitPrice = null;

    /**
     * Is this closeable?
     */
    private boolean isCloseable = true;

    /**
     * DOes this have debts?
     */
    private boolean hasDebts = false;

    /**
     * Does this have rates?
     */
    private boolean hasRates = false;

    /**
     * Does this have prices?
     */
    private boolean hasPrices = false;

    /**
     * Does this have patterns?
     */
    private boolean hasPatterns = false;

    /**
     * is this pattered?
     */
    private boolean isPatterned = false;

    /**
     * is this a Parent?.
     */
    private boolean isParent = false;

    /**
     * is this Aliased to?
     */
    private boolean isAliasedTo = false;

    /**
     * Obtain Name.
     * @return the name
     */
    public String getName() {
        return getName(getValueSet());
    }

    /**
     * Obtain Encrypted name.
     * @return the bytes
     */
    public byte[] getNameBytes() {
        return getNameBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Name Field.
     * @return the Field
     */
    private EncryptedString getNameField() {
        return getNameField(getValueSet());
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Obtain Encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    private EncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Obtain Parent.
     * @return the parent
     */
    public Account getParent() {
        return getParent(getValueSet());
    }

    /**
     * Obtain Parent Id.
     * @return the parent id
     */
    public Integer getParentId() {
        return getParentId(getValueSet());
    }

    /**
     * Obtain Alias.
     * @return the alias
     */
    public Account getAlias() {
        return getAlias(getValueSet());
    }

    /**
     * Obtain Alias Id.
     * @return the alias id
     */
    public Integer getAliasId() {
        return getAliasId(getValueSet());
    }

    /**
     * Obtain Earliest event.
     * @return the event
     */
    public Event getEarliest() {
        return theEarliest;
    }

    /**
     * Obtain Latest Event.
     * @return the event
     */
    public Event getLatest() {
        return theLatest;
    }

    /**
     * Obtain Initial Price.
     * @return the price
     */
    public AccountPrice getInitPrice() {
        return theInitPrice;
    }

    /**
     * Obtain Account Type.
     * @return the type
     */
    public AccountType getActType() {
        return getAccountType(getValueSet());
    }

    /**
     * Obtain Order.
     * @return the order
     */
    public int getOrder() {
        return getOrder(getValueSet());
    }

    /**
     * Obtain Maturity.
     * @return the date
     */
    public JDateDay getMaturity() {
        return getMaturity(getValueSet());
    }

    /**
     * Obtain Close.
     * @return the date
     */
    public JDateDay getClose() {
        return getClose(getValueSet());
    }

    /**
     * Obtain WebSite.
     * @return the webSite
     */
    public char[] getWebSite() {
        return getWebSite(getValueSet());
    }

    /**
     * Obtain Encrypted webSite.
     * @return the bytes
     */
    public byte[] getWebSiteBytes() {
        return getWebSiteBytes(getValueSet());
    }

    /**
     * Obtain Encrypted webSite Field.
     * @return the Field
     */
    private EncryptedCharArray getWebSiteField() {
        return getWebSiteField(getValueSet());
    }

    /**
     * Obtain CustNo.
     * @return the custNo
     */
    public char[] getCustNo() {
        return getCustNo(getValueSet());
    }

    /**
     * Obtain encrypted custNo.
     * @return the bytes
     */
    public byte[] getCustNoBytes() {
        return getCustNoBytes(getValueSet());
    }

    /**
     * Obtain Encrypted CustomerNo Field.
     * @return the Field
     */
    private EncryptedCharArray getCustNoField() {
        return getCustNoField(getValueSet());
    }

    /**
     * Obtain userId.
     * @return the userId
     */
    public char[] getUserId() {
        return getUserId(getValueSet());
    }

    /**
     * Obtain encrypted userId.
     * @return the bytes
     */
    public byte[] getUserIdBytes() {
        return getUserIdBytes(getValueSet());
    }

    /**
     * Obtain Encrypted UserId Field.
     * @return the Field
     */
    private EncryptedCharArray getUserIdField() {
        return getUserIdField(getValueSet());
    }

    /**
     * Obtain Password.
     * @return the password
     */
    public char[] getPassword() {
        return getPassword(getValueSet());
    }

    /**
     * Obtain Encrypted password.
     * @return the bytes
     */
    public byte[] getPasswordBytes() {
        return getPasswordBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Password Field.
     * @return the Field
     */
    private EncryptedCharArray getPasswordField() {
        return getPasswordField(getValueSet());
    }

    /**
     * Obtain Account details.
     * @return the account details
     */
    public char[] getAccount() {
        return getAccount(getValueSet());
    }

    /**
     * Obtain encrypted account details.
     * @return the bytes
     */
    public byte[] getAccountBytes() {
        return getAccountBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Account details Field.
     * @return the Field
     */
    private EncryptedCharArray getAccountField() {
        return getAccountField(getValueSet());
    }

    /**
     * Obtain Notes.
     * @return the notes
     */
    public char[] getNotes() {
        return getNotes(getValueSet());
    }

    /**
     * Obtain Encrypted notes.
     * @return the bytes
     */
    public byte[] getNotesBytes() {
        return getNotesBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Notes Field.
     * @return the Field
     */
    private EncryptedCharArray getNotesField() {
        return getNotesField(getValueSet());
    }

    /**
     * Is the account closeable?
     * @return true/false
     */
    public boolean isCloseable() {
        return isCloseable;
    }

    /**
     * Does the account have debts?
     * @return true/false
     */
    public boolean hasDebts() {
        return hasDebts;
    }

    /**
     * Is the account a parent?
     * @return true/false
     */
    public boolean isParent() {
        return isParent;
    }

    /**
     * Is the account closed?
     * @return true/false
     */
    public boolean isClosed() {
        return (getClose() != null);
    }

    /**
     * Is the account an alias?
     * @return true/false
     */
    public boolean isAlias() {
        return (getAliasId() != null);
    }

    /**
     * Is the account aliased to?
     * @return true/false
     */
    public boolean isAliasedTo() {
        return isAliasedTo;
    }

    /**
     * Is the account deletable?
     * @return true/false
     */
    public boolean isDeletable() {
        return ((theLatest == null) && (!isDeleted()) && (!isParent) && (!hasRates)
                && ((!hasPrices) || (getState() == DataState.NEW)) && (!hasPatterns) && (!isAliasedTo)
                && (!isPatterned) && (!getActType().isReserved()));
    }

    /**
     * Obtain Name.
     * @param pValueSet the valueSet
     * @return the Name
     */
    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    /**
     * Obtain Encrypted Name.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    /**
     * Obtain Encrypted name field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the description
     */
    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    /**
     * Obtain Encrypted description.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    /**
     * Obtain Encrypted description field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Obtain AccountType.
     * @param pValueSet the valueSet
     * @return the AccountType
     */
    public static AccountType getAccountType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TYPE, AccountType.class);
    }

    /**
     * Obtain Order.
     * @param pValueSet the valueSet
     * @return the Order
     */
    public static Integer getOrder(final ValueSet pValueSet) {
        Object myType = pValueSet.getValue(FIELD_TYPE);
        if (myType instanceof AccountType) {
            return ((AccountType) myType).getOrder();
        }
        return null;
    }

    /**
     * Obtain Maturity.
     * @param pValueSet the valueSet
     * @return the Maturity
     */
    public static JDateDay getMaturity(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_MATURITY, JDateDay.class);
    }

    /**
     * Obtain Close date.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static JDateDay getClose(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSE, JDateDay.class);
    }

    /**
     * Obtain Parent.
     * @param pValueSet the valueSet
     * @return the Parent
     */
    public static Account getParent(final ValueSet pValueSet) {
        Object myAccount = pValueSet.getValue(FIELD_PARENT);
        if (myAccount instanceof Account) {
            return (Account) myAccount;
        }
        return null;
    }

    /**
     * Obtain Parent Id.
     * @param pValueSet the valueSet
     * @return the Parent Id
     */
    public static Integer getParentId(final ValueSet pValueSet) {
        Object myAccount = pValueSet.getValue(FIELD_PARENT);
        if (myAccount instanceof Account) {
            return ((Account) myAccount).getId();
        }
        if (myAccount instanceof Integer) {
            return (Integer) myAccount;
        }
        return null;
    }

    /**
     * Obtain Alias.
     * @param pValueSet the valueSet
     * @return the Alias
     */
    public static Account getAlias(final ValueSet pValueSet) {
        Object myAccount = pValueSet.getValue(FIELD_ALIAS);
        if (myAccount instanceof Account) {
            return (Account) myAccount;
        }
        return null;
    }

    /**
     * Obtain Alias Id.
     * @param pValueSet the valueSet
     * @return the Alias Id
     */
    public static Integer getAliasId(final ValueSet pValueSet) {
        Object myAccount = pValueSet.getValue(FIELD_ALIAS);
        if (myAccount instanceof Account) {
            return ((Account) myAccount).getId();
        }
        if (myAccount instanceof Integer) {
            return (Integer) myAccount;
        }
        return null;
    }

    /**
     * Obtain webSite.
     * @param pValueSet the valueSet
     * @return the webSite
     */
    public static char[] getWebSite(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_WEBSITE, char[].class);
    }

    /**
     * Obtain Encrypted webSite.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getWebSiteBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_WEBSITE);
    }

    /**
     * Obtain webSite field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedCharArray getWebSiteField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_WEBSITE, EncryptedCharArray.class);
    }

    /**
     * Obtain custNo.
     * @param pValueSet the valueSet
     * @return the custNo
     */
    public static char[] getCustNo(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_CUSTNO, char[].class);
    }

    /**
     * Obtain Encrypted custNo.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getCustNoBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_CUSTNO);
    }

    /**
     * Obtain Encrypted custNo field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedCharArray getCustNoField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CUSTNO, EncryptedCharArray.class);
    }

    /**
     * Obtain userId.
     * @param pValueSet the valueSet
     * @return the userId
     */
    public static char[] getUserId(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_USERID, char[].class);
    }

    /**
     * Obtain Encrypted userId.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getUserIdBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_USERID);
    }

    /**
     * Obtain Encrypted userId field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedCharArray getUserIdField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_USERID, EncryptedCharArray.class);
    }

    /**
     * Obtain password.
     * @param pValueSet the valueSet
     * @return the Password
     */
    public static char[] getPassword(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PASSWORD, char[].class);
    }

    /**
     * Obtain Encrypted password.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getPasswordBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PASSWORD);
    }

    /**
     * Obtain Encrypted Password field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedCharArray getPasswordField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PASSWORD, EncryptedCharArray.class);
    }

    /**
     * Obtain Account Details.
     * @param pValueSet the valueSet
     * @return the Account details
     */
    public static char[] getAccount(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_ACCOUNT, char[].class);
    }

    /**
     * Obtain Encrypted Account.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getAccountBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_ACCOUNT);
    }

    /**
     * Obtain Encrypted Account field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedCharArray getAccountField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, EncryptedCharArray.class);
    }

    /**
     * Obtain Notes.
     * @param pValueSet the valueSet
     * @return the Notes
     */
    public static char[] getNotes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NOTES, char[].class);
    }

    /**
     * Obtain Encrypted Notes.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getNotesBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NOTES);
    }

    /**
     * Obtain Encrypted Notes field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedCharArray getNotesField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NOTES, EncryptedCharArray.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueName(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_NAME, pValue);
    }

    /**
     * Set name value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueName(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_NAME, pBytes, String.class);
    }

    /**
     * Set name value.
     * @param pValue the value
     */
    private void setValueName(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_NAME, pValue);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueDesc(final String pValue) throws JDataException {
        setEncryptedValue(FIELD_DESC, pValue);
    }

    /**
     * Set description value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueDesc(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_DESC, pBytes, String.class);
    }

    /**
     * Set description value.
     * @param pValue the value
     */
    private void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set account type value.
     * @param pValue the value
     */
    private void setValueType(final AccountType pValue) {
        getValueSet().setValue(FIELD_TYPE, pValue);
    }

    /**
     * Set account type id.
     * @param pValue the value
     */
    private void setValueType(final Integer pValue) {
        getValueSet().setValue(FIELD_TYPE, pValue);
    }

    /**
     * Set maturity value.
     * @param pValue the value
     */
    private void setValueMaturity(final JDateDay pValue) {
        getValueSet().setValue(FIELD_MATURITY, pValue);
    }

    /**
     * Set close value.
     * @param pValue the value
     */
    private void setValueClose(final JDateDay pValue) {
        getValueSet().setValue(FIELD_CLOSE, pValue);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    private void setValueParent(final Account pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pValue the value
     */
    private void setValueParent(final Integer pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set alias value.
     * @param pValue the value
     */
    private void setValueAlias(final Account pValue) {
        getValueSet().setValue(FIELD_ALIAS, pValue);
    }

    /**
     * Set alias id.
     * @param pValue the value
     */
    private void setValueAlias(final Integer pValue) {
        getValueSet().setValue(FIELD_ALIAS, pValue);
    }

    /**
     * Set webSite value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueWebSite(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_WEBSITE, pValue);
    }

    /**
     * Set webSite value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueWebSite(final byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_WEBSITE, pValue, String.class);
    }

    /**
     * Set webSite value.
     * @param pValue the value
     */
    private void setValueWebSite(final EncryptedCharArray pValue) {
        getValueSet().setValue(FIELD_WEBSITE, pValue);
    }

    /**
     * Set custNo value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueCustNo(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_CUSTNO, pValue);
    }

    /**
     * Set custNo value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueCustNo(final byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_CUSTNO, pValue, String.class);
    }

    /**
     * Set custNo value.
     * @param pValue the value
     */
    private void setValueCustNo(final EncryptedCharArray pValue) {
        getValueSet().setValue(FIELD_CUSTNO, pValue);
    }

    /**
     * Set userId value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueUserId(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_USERID, pValue);
    }

    /**
     * Set userId value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueUserId(final byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_USERID, pValue, String.class);
    }

    /**
     * Set userId value.
     * @param pValue the value
     */
    private void setValueUserId(final EncryptedCharArray pValue) {
        getValueSet().setValue(FIELD_USERID, pValue);
    }

    /**
     * Set passWord value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValuePassword(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_PASSWORD, pValue);
    }

    /**
     * Set passWord value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValuePassword(final byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_PASSWORD, pValue, String.class);
    }

    /**
     * Set passWord value.
     * @param pValue the value
     */
    private void setValuePassword(final EncryptedCharArray pValue) {
        getValueSet().setValue(FIELD_PASSWORD, pValue);
    }

    /**
     * Set account value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueAccount(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_ACCOUNT, pValue);
    }

    /**
     * Set account value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueAccount(final byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_ACCOUNT, pValue, String.class);
    }

    /**
     * Set account value.
     * @param pValue the value
     */
    private void setValueAccount(final EncryptedCharArray pValue) {
        getValueSet().setValue(FIELD_ACCOUNT, pValue);
    }

    /**
     * Set notes value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueNotes(final char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_NOTES, pValue);
    }

    /**
     * Set notes value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueNotes(final byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_NOTES, pValue, String.class);
    }

    /**
     * Set notes value.
     * @param pValue the value
     */
    private void setValueNotes(final EncryptedCharArray pValue) {
        getValueSet().setValue(FIELD_NOTES, pValue);
    }

    @Override
    public FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public Account getBase() {
        return (Account) super.getBase();
    }

    @Override
    public boolean isLocked() {
        return isClosed();
    }

    /**
     * Copy flags.
     * @param pItem the original item
     */
    private void copyFlags(final Account pItem) {
        theEarliest = pItem.theEarliest;
        theLatest = pItem.theLatest;
        theInitPrice = pItem.theInitPrice;
        isCloseable = pItem.isCloseable();
        isAliasedTo = pItem.isAliasedTo();
        isParent = pItem.isParent();
        isPatterned = pItem.isPatterned;
        hasPatterns = pItem.hasPatterns;
        hasRates = pItem.hasRates;
        hasPrices = pItem.hasPrices;
        hasDebts = pItem.hasDebts;
    }

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pAccount The Account to copy
     */
    public Account(final AccountList pList,
                   final Account pAccount) {
        /* Set standard values */
        super(pList, pAccount);

        /* If this is a build of edit from Core */
        if ((getStyle() == ListStyle.EDIT) && (pAccount.getStyle() == ListStyle.CORE)) {
            /* Copy the flags */
            copyFlags(pAccount);
        }
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param uId the Account id
     * @param uControlId the control id
     * @param pName the Encrypted Name of the account
     * @param uAcTypeId the Account type id
     * @param pDesc the Encrypted Description of the account
     * @param pMaturity the Maturity date for the account
     * @param pClose the Close date for the account
     * @param pParentId the Parent id (or -1 if no parent)
     * @param pAliasId the Alias id (or -1 if no parent)
     * @param pWebSite the Encrypted WebSite of the account
     * @param pCustNo the Encrypted CustomerId of the account
     * @param pUserId the Encrypted UserId of the account
     * @param pPassword the Encrypted Password of the account
     * @param pAccount the Encrypted Account details of the account
     * @param pNotes the Encrypted Notes for the account
     * @throws JDataException on error
     */
    private Account(final AccountList pList,
                    final Integer uId,
                    final Integer uControlId,
                    final byte[] pName,
                    final Integer uAcTypeId,
                    final byte[] pDesc,
                    final Date pMaturity,
                    final Date pClose,
                    final Integer pParentId,
                    final Integer pAliasId,
                    final byte[] pWebSite,
                    final byte[] pCustNo,
                    final byte[] pUserId,
                    final byte[] pPassword,
                    final byte[] pAccount,
                    final byte[] pNotes) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(uAcTypeId);
            setValueParent(pParentId);
            setValueAlias(pAliasId);

            /* Set ControlId */
            setControlKey(uControlId);

            /* Look up the Account Type */
            FinanceData myData = getDataSet();
            AccountTypeList myTypes = myData.getAccountTypes();
            AccountType myActType = myTypes.findItemById(uAcTypeId);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Type Id");
            }
            setValueType(myActType);

            /* Parse the maturity date if it exists */
            if (pMaturity != null) {
                setValueMaturity(new JDateDay(pMaturity));
            }

            /* Parse the closed date if it exists */
            if (pClose != null) {
                setValueClose(new JDateDay(pClose));
            }

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);
            setValueWebSite(pWebSite);
            setValueCustNo(pCustNo);
            setValueUserId(pUserId);
            setValuePassword(pPassword);
            setValueAccount(pAccount);
            setValueNotes(pNotes);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param uId the id
     * @param sName the Name of the account
     * @param uAcTypeId the Account type id
     * @param pDesc the description
     * @param pMaturity the Maturity date for the account
     * @param pClose the Close date for the account
     * @param pParentId the Parent id (or -1 if no parent)
     * @param pAliasId the Alias id (or -1 if no parent)
     * @param pWebSite the WebSite of the account
     * @param pCustNo the CustomerId of the account
     * @param pUserId the UserId of the account
     * @param pPassword the Password of the account
     * @param pAccount the Account details of the account
     * @param pNotes the Notes for the account
     * @throws JDataException on error
     */
    private Account(final AccountList pList,
                    final Integer uId,
                    final String sName,
                    final Integer uAcTypeId,
                    final String pDesc,
                    final Date pMaturity,
                    final Date pClose,
                    final Integer pParentId,
                    final Integer pAliasId,
                    final char[] pWebSite,
                    final char[] pCustNo,
                    final char[] pUserId,
                    final char[] pPassword,
                    final char[] pAccount,
                    final char[] pNotes) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Store the IDs */
            setValueType(uAcTypeId);
            setValueParent(pParentId);
            setValueAlias(pAliasId);

            /* Record the encrypted values */
            setValueName(sName);
            setValueDesc(pDesc);
            setValueWebSite(pWebSite);
            setValueCustNo(pCustNo);
            setValueUserId(pUserId);
            setValuePassword(pPassword);
            setValueAccount(pAccount);
            setValueNotes(pNotes);

            /* Look up the Account Type */
            FinanceData myData = getDataSet();
            AccountTypeList myTypes = myData.getAccountTypes();
            AccountType myActType = myTypes.findItemById(uAcTypeId);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Type Id");
            }
            setValueType(myActType);

            /* Parse the maturity date if it exists */
            if (pMaturity != null) {
                setValueMaturity(new JDateDay(pMaturity));
            }

            /* Parse the closed date if it exists */
            if (pClose != null) {
                setValueClose(new JDateDay(pClose));
            }

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Account(final AccountList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    @Override
    public int compareTo(final Account pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If we are comparing owner with non-owner */
        if (isOwner() != pThat.isOwner()) {
            /* List owners first */
            return (isOwner()) ? -1 : 1;
        }

        /* If we are comparing alias with non-alias */
        if (isAlias() != pThat.isAlias()) {
            /* List alias after non-alias */
            return (isAlias()) ? 1 : -1;
        }

        /* Check the order */
        int iDiff = (getOrder() - pThat.getOrder());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the names */
        iDiff = Difference.compareObject(getName(), pThat.getName());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    protected void relinkToDataSet() {
        /* Update the Encryption details */
        super.relinkToDataSet();

        /* Access Account types */
        FinanceData myData = getDataSet();
        AccountTypeList myTypes = myData.getAccountTypes();
        AccountList myList = myData.getAccounts();

        /* Update to use the local copy of the AccountTypes */
        AccountType myType = getActType();
        AccountType myNewType = myTypes.findItemById(myType.getId());
        setValueType(myNewType);

        /* If we have a parent */
        Account myAct = getParent();
        if (myAct != null) {
            /* Update it */
            Account myNewAct = myList.findItemById(myAct.getId());
            setValueParent(myNewAct);
        }

        /* If we have an alias */
        myAct = getAlias();
        if (myAct != null) {
            /* Update it */
            Account myNewAct = myList.findItemById(myAct.getId());
            setValueAlias(myNewAct);
        }
    }

    /**
     * Is the account priced?
     * @return true/false
     */
    public boolean isPriced() {
        return getActType().isPriced();
    }

    /**
     * Is the account the market?
     * @return true/false
     */
    protected boolean isMarket() {
        return getActType().isMarket();
    }

    /**
     * Is the account external?
     * @return true/false
     */
    public boolean isExternal() {
        return getActType().isExternal();
    }

    /**
     * Is the account special?
     * @return true/false
     */
    protected boolean isSpecial() {
        return getActType().isSpecial();
    }

    /**
     * Is the account internal?
     * @return true/false
     */
    protected boolean isInternal() {
        return getActType().isInternal();
    }

    /**
     * Is the account inheritance?
     * @return true/false
     */
    protected boolean isInheritance() {
        return getActType().isInheritance();
    }

    /**
     * Is the account the taxman?
     * @return true/false
     */
    protected boolean isTaxMan() {
        return getActType().isTaxMan();
    }

    /**
     * Is the account money?
     * @return true/false
     */
    public boolean isMoney() {
        return getActType().isMoney();
    }

    /**
     * Is the account cash?
     * @return true/false
     */
    protected boolean isCash() {
        return getActType().isCash();
    }

    /**
     * Is the account writeOff?
     * @return true/false
     */
    protected boolean isWriteOff() {
        return getActType().isWriteOff();
    }

    /**
     * Is the account and endowment?
     * @return true/false
     */
    protected boolean isEndowment() {
        return getActType().isEndowment();
    }

    /**
     * Is the account an owner?
     * @return true/false
     */
    public boolean isOwner() {
        return getActType().isOwner();
    }

    /**
     * Is the account taxFree?
     * @return true/false
     */
    public boolean isTaxFree() {
        return getActType().isTaxFree();
    }

    /**
     * Is the account a UnitTrust?
     * @return true/false
     */
    public boolean isUnitTrust() {
        return getActType().isUnitTrust();
    }

    /**
     * Is the account a debt?
     * @return true/false
     */
    public boolean isDebt() {
        return getActType().isDebt();
    }

    /**
     * Is the account a child?
     * @return true/false
     */
    public boolean isChild() {
        return getActType().isChild();
    }

    /**
     * Is the account a bond?
     * @return true/false
     */
    public boolean isBond() {
        return getActType().isBond();
    }

    /**
     * Is the account benefit?
     * @return true/false
     */
    public boolean isBenefit() {
        return getActType().isBenefit();
    }

    /**
     * Is the account a LifeBond?
     * @return true/false
     */
    public boolean isLifeBond() {
        return getActType().isLifeBond();
    }

    /**
     * Is the account capital?
     * @return true/false
     */
    public boolean isCapital() {
        return getActType().isCapital();
    }

    /**
     * Is the account capitalGains?
     * @return true/false
     */
    public boolean isCapitalGains() {
        return getActType().isCapitalGains();
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        AccountType myType = getActType();
        String myName = getName();
        String myDesc = getDesc();
        Account myParent = getParent();
        Account myAlias = getAlias();
        AccountList myList = (AccountList) getList();
        FinanceData mySet = getDataSet();

        /* AccountType must be non-null */
        if (myType == null) {
            addError("AccountType must be non-null", FIELD_TYPE);
        } else if (!myType.getEnabled()) {
            addError("AccountType must be enabled", FIELD_TYPE);
        }

        /* Name must be non-null */
        if (myName == null) {
            addError("Name must be non-null", FIELD_NAME);

            /* Check that the name is unique */
        } else {
            /* The name must not be too long */
            if (myName.length() > NAMELEN) {
                addError("Name is too long", FIELD_NAME);
            }

            if (myList.countInstances(myName) > 1) {
                addError("Name must be unique", FIELD_NAME);
            }
        }

        /* The description must not be too long */
        if ((myDesc != null) && (myDesc.length() > DESCLEN)) {
            addError("Description is too long", FIELD_DESC);
        }

        /* If the account is priced */
        if (myType.isPriced()) {
            /* If this account has an alias */
            if (myAlias != null) {
                /* Must not have prices */
                if (hasPrices) {
                    addError("Aliased account has prices", FIELD_TYPE);
                }

                /* Alias account must have prices */
                if ((!myAlias.hasPrices) && (myAlias.theEarliest != null)) {
                    addError("Alias account has no prices", FIELD_TYPE);
                }

                /* else this is a standard account */
            } else {
                /* Must have prices */
                if ((!hasPrices) && (theEarliest != null)) {
                    addError("Priced account has no prices", FIELD_TYPE);
                }
            }

            /* else the account is not priced */
        } else {
            /* Prices cannot exist */
            if (hasPrices) {
                addError("non-Priced account has prices", FIELD_TYPE);
            }
        }

        /* If the account is not a child then parent cannot exist */
        if (!myType.isChild()) {
            if (myParent != null) {
                addError("Non-child account has parent", FIELD_PARENT);
            }

            /* else we should have a parent */
        } else {
            /* If data has been fully loaded we have no parent */
            if ((mySet.getLoadState() != LoadState.INITIAL) && (myParent == null)) {
                addError("Child Account must have parent", FIELD_PARENT);
            }

            /* if we have a parent */
            if (myParent != null) {
                /* check that any parent is owner */
                if (!myParent.isOwner()) {
                    addError("Parent account must be owner", FIELD_PARENT);
                }

                /* If we are open then parent must be open */
                if (!isClosed() && myParent.isClosed()) {
                    addError("Parent account must not be closed", FIELD_PARENT);
                }
            }
        }

        /* If we have an alias */
        if (myAlias != null) {
            /* Access the alias type */
            AccountType myAliasType = myAlias.getActType();

            /* Cannot alias to self */
            if (Difference.isEqual(this, myAlias)) {
                addError("Cannot alias to self", FIELD_ALIAS);

                /* Cannot alias to same type */
            } else if (Difference.isEqual(myType, myAliasType)) {
                addError("Cannot alias to same account type", FIELD_ALIAS);
            }

            /* Must be alias type */
            if (!myType.canAlias()) {
                addError("This account type cannot alias", FIELD_ALIAS);
            }

            /* Must not be aliased to */
            if (isAliasedTo) {
                addError("This account is already aliased to", FIELD_ALIAS);
            }

            /* Alias must be alias type */
            if (!myAliasType.canAlias()) {
                addError("The alias account type is invalid", FIELD_ALIAS);
            }

            /* Alias cannot be aliased */
            if (myAlias.isAlias()) {
                addError("The alias account is already aliased", FIELD_ALIAS);
            }
        }

        /* If the account has rates then it must be money-based */
        if ((hasRates) && (!myType.isMoney())) {
            addError("non-Money account has rates", FIELD_TYPE);
        }

        /* If the account has a maturity rate then it must be a bond */
        if ((getMaturity() != null) && (!myType.isBond())) {
            addError("non-Bond has maturity date", FIELD_MATURITY);
        }

        /* Open Bond accounts must have maturity */
        if ((myType.isBond()) && !isClosed() && (getMaturity() == null)) {
            addError("Bond must have maturity date", FIELD_MATURITY);
        }

        /* If data has been fully loaded and the account is closed it must be closeable */
        if ((mySet.getLoadState() != LoadState.INITIAL) && (isClosed()) && (!isCloseable())) {
            addError("Non-closeable account is closed", FIELD_CLOSE);
        }

        /* The WebSite must not be too long */
        if ((getWebSite() != null) && (getWebSite().length > WSITELEN)) {
            addError("WebSite is too long", FIELD_WEBSITE);
        }

        /* The CustNo must not be too long */
        if ((getCustNo() != null) && (getCustNo().length > CUSTLEN)) {
            addError("Customer No. is too long", FIELD_CUSTNO);
        }

        /* The UserId must not be too long */
        if ((getUserId() != null) && (getUserId().length > UIDLEN)) {
            addError("UserId is too long", FIELD_USERID);
        }

        /* The Password must not be too long */
        if ((getPassword() != null) && (getPassword().length > PWDLEN)) {
            addError("Password is too long", FIELD_PASSWORD);
        }

        /* The Account must not be too long */
        if ((getAccount() != null) && (getAccount().length > ACTLEN)) {
            addError("Account is too long", FIELD_ACCOUNT);
        }

        /* The Notes must not be too long */
        if ((getNotes() != null) && (getNotes().length > NOTELEN)) {
            addError("WebSite is too long", FIELD_NOTES);
        }

        /* Set validation flag */
        boolean isValid = !hasErrors();
        if (isValid) {
            setValidEdit();
        }
    }

    /**
     * Get the value of an account on a specific date.
     * @param pDate The date of the valuation
     * @return Valuation of account
     */
    public JMoney getValue(final JDateDay pDate) {
        /* Initialise money */
        JMoney myValue = new JMoney();

        /* Access the Events */
        FinanceData mySet = getDataSet();
        EventList myEvents = mySet.getEvents();

        /* Loop through the Events extracting relevant elements */
        Iterator<Event> myIterator = myEvents.iterator();
        while (myIterator.hasNext()) {
            /* Check the range */
            Event myCurr = myIterator.next();
            int myResult = pDate.compareTo(myCurr.getDate());

            /* Handle out of range */
            if (myResult == -1) {
                break;
            }

            /* If this Event relates to this account */
            if (myCurr.relatesTo(this)) {
                /* Access the amount */
                JMoney myAmount = myCurr.getAmount();

                /* If this is a credit add the value */
                if (this.compareTo(myCurr.getCredit()) == 0) {
                    myValue.addAmount(myAmount);

                    /* else subtract from value */
                } else {
                    myValue.subtractAmount(myAmount);
                }
            }
        }

        /* Return the value */
        return myValue;
    }

    /**
     * Clear the active account flags.
     */
    @Override
    public void clearActive() {
        super.clearActive();

        /* Reset flags */
        isCloseable = true;
        theEarliest = null;
        theLatest = null;
        theInitPrice = null;
        hasDebts = false;
        hasRates = false;
        hasPrices = false;
        hasPatterns = false;
        isPatterned = false;
        isParent = false;
        isAliasedTo = false;
    }

    /**
     * Touch an account.
     * @param pObject the object touch the account
     */
    @Override
    public void touchItem(final DataItem pObject) {
        /* Note that the account is Active */
        super.touchItem(pObject);

        /* If we are being touched by a rate */
        if (pObject instanceof AccountRate) {
            /* Note flags */
            hasRates = true;

            /* If we are being touched by a price */
        } else if (pObject instanceof AccountPrice) {
            /* Note flags */
            hasPrices = true;
            if (theInitPrice == null) {
                theInitPrice = (AccountPrice) pObject;
            }

            /* If we are being touched by a pattern */
        } else if (pObject instanceof Pattern) {
            /* Access as pattern */
            Pattern myPattern = (Pattern) pObject;

            /* Note flags */
            if (Difference.isEqual(myPattern.getAccount(), this)) {
                hasPatterns = true;
            }
            if (Difference.isEqual(myPattern.getPartner(), this)) {
                isPatterned = true;
            }

            /* If we are being touched by an event */
        } else if (pObject instanceof Event) {
            /* Access as event */
            Event myEvent = (Event) pObject;

            /* Note flags */
            /* Record the event */
            if (theEarliest == null) {
                theEarliest = myEvent;
            }
            theLatest = myEvent;

            /* If we have a parent, touch it */
            if (getParent() != null) {
                getParent().touchItem(pObject);
            }

            /* If we are being touched by another account */
        } else if (pObject instanceof Account) {
            /* Access as account */
            Account myAccount = (Account) pObject;

            /* Note flags */
            if (Difference.isEqual(myAccount.getAlias(), this)) {
                isAliasedTo = true;
            }
            if (Difference.isEqual(myAccount.getParent(), this)) {
                isParent = true;
                if (myAccount.isDebt()) {
                    hasDebts = true;
                }
            }
        }
    }

    /**
     * Set non-closeable.
     */
    public void setNonCloseable() {
        /* Record the status */
        isCloseable = false;
    }

    /**
     * Adjust closed date.
     */
    public void adjustClosed() {
        /* If we have a latest event that is later than the close */
        if (getClose().compareTo(theLatest.getDate()) < 0) {
            /* Record the more accurate date */
            setClose(theLatest.getDate());
        }

        /* If the maturity is null for a bond set it to close date */
        if (isBond() && getMaturity() == null) {
            /* Record a date for maturity */
            setMaturity(theLatest.getDate());
        }
    }

    /**
     * Close the account.
     */
    public void closeAccount() {
        /* Close the account */
        setClose(theLatest.getDate());
    }

    /**
     * Re-open the account.
     */
    public void reOpenAccount() {
        /* Reopen the account */
        setClose(null);
    }

    /**
     * Set a new description.
     * @param pDesc the description
     * @throws JDataException on error
     */
    public void setDescription(final String pDesc) throws JDataException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new maturity date.
     * @param pDate the new date
     */
    public void setMaturity(final JDateDay pDate) {
        setValueMaturity(pDate);
    }

    /**
     * Set a new close date.
     * @param pDate the new date
     */
    public void setClose(final JDateDay pDate) {
        setValueClose(pDate);
    }

    /**
     * Set a new parent.
     * @param pParent the new parent
     */
    public void setParent(final Account pParent) {
        setValueParent(pParent);
    }

    /**
     * Set a new alias.
     * @param pAlias the new alias
     */
    public void setAlias(final Account pAlias) {
        setValueAlias(pAlias);
    }

    /**
     * Set a new account name.
     * @param pName the new name
     * @throws JDataException on error
     */
    public void setAccountName(final String pName) throws JDataException {
        setValueName(pName);
    }

    /**
     * Set a new account type.
     * @param pType the new type
     */
    public void setActType(final AccountType pType) {
        setValueType(pType);
    }

    /**
     * Set a new web site.
     * @param pWebSite the new site
     * @throws JDataException on error
     */
    public void setWebSite(final char[] pWebSite) throws JDataException {
        setValueWebSite(pWebSite);
    }

    /**
     * Set a new customer number.
     * @param pCustNo the new number
     * @throws JDataException on error
     */
    public void setCustNo(final char[] pCustNo) throws JDataException {
        setValueCustNo(pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new id
     * @throws JDataException on error
     */
    public void setUserId(final char[] pUserId) throws JDataException {
        setValueUserId(pUserId);
    }

    /**
     * Set a new password.
     * @param pPassword the new password
     * @throws JDataException on error
     */
    public void setPassword(final char[] pPassword) throws JDataException {
        setValuePassword(pPassword);
    }

    /**
     * Set a new account.
     * @param pAccount the new account
     * @throws JDataException on error
     */
    public void setAccount(final char[] pAccount) throws JDataException {
        setValueAccount(pAccount);
    }

    /**
     * Set a new notes.
     * @param pNotes the new notes
     * @throws JDataException on error
     */
    public void setNotes(final char[] pNotes) throws JDataException {
        setValueNotes(pNotes);
    }

    /**
     * Update base account from an edited account.
     * @param pAccount the edited account
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pAccount) {
        /* Can only update from an account */
        if (!(pAccount instanceof Account)) {
            return false;
        }

        Account myAccount = (Account) pAccount;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myAccount.getName())) {
            setValueName(myAccount.getNameField());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myAccount.getDesc())) {
            setValueDesc(myAccount.getDescField());
        }

        /* Update the account type if required */
        if (!Difference.isEqual(getActType(), myAccount.getActType())) {
            setValueType(myAccount.getActType());
        }

        /* Update the maturity if required */
        if (!Difference.isEqual(getMaturity(), myAccount.getMaturity())) {
            setValueMaturity(myAccount.getMaturity());
        }

        /* Update the close if required */
        if (!Difference.isEqual(getClose(), myAccount.getClose())) {
            setValueClose(myAccount.getClose());
        }

        /* Update the parent if required */
        if (!Difference.isEqual(getParent(), myAccount.getParent())) {
            setValueParent(myAccount.getParent());
        }

        /* Update the alias if required */
        if (!Difference.isEqual(getAlias(), myAccount.getAlias())) {
            setValueAlias(myAccount.getAlias());
        }

        /* Update the WebSite if required */
        if (!Difference.isEqual(getWebSite(), myAccount.getWebSite())) {
            setValueWebSite(myAccount.getWebSiteField());
        }

        /* Update the customer number if required */
        if (!Difference.isEqual(getCustNo(), myAccount.getCustNo())) {
            setValueCustNo(myAccount.getCustNoField());
        }

        /* Update the UserId if required */
        if (!Difference.isEqual(getUserId(), myAccount.getUserId())) {
            setValueUserId(myAccount.getUserIdField());
        }

        /* Update the Password if required */
        if (!Difference.isEqual(getPassword(), myAccount.getPassword())) {
            setValuePassword(myAccount.getPasswordField());
        }

        /* Update the account if required */
        if (!Difference.isEqual(getAccount(), myAccount.getAccount())) {
            setValueAccount(myAccount.getAccountField());
        }

        /* Update the notes if required */
        if (!Difference.isEqual(getNotes(), myAccount.getNotes())) {
            setValueNotes(myAccount.getNotesField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * AccountList class.
     */
    public static class AccountList extends EncryptedList<Account> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        /**
         * Account field id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return (theAccount == null) ? JDataFieldValue.SkipField : theAccount;
            }
            return super.getFieldValue(pField);
        }

        /**
         * The account.
         */
        private Account theAccount = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE account list.
         * @param pData the DataSet for the list
         */
        protected AccountList(final FinanceData pData) {
            super(Account.class, pData);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountList(final AccountList pSource) {
            super(pSource);
        }

        @Override
        protected AccountList getEmptyList() {
            return new AccountList(this);
        }

        @Override
        public AccountList cloneList(final DataSet<?> pDataSet) {
            return (AccountList) super.cloneList(pDataSet);
        }

        @Override
        public AccountList deriveList(final ListStyle pStyle) {
            return (AccountList) super.deriveList(pStyle);
        }

        @Override
        public AccountList deriveDifferences(final DataList<Account> pOld) {
            return (AccountList) super.deriveDifferences(pOld);
        }

        /**
         * Construct an edit extract for an Account.
         * @param pAccount the relevant account
         * @return the edit Extract
         */
        public AccountList deriveEditList(final Account pAccount) {
            /* Build an empty Extract List */
            AccountList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Create a new account based on the passed account */
            myList.theAccount = new Account(myList, pAccount);
            myList.add(myList.theAccount);

            /* Return the List */
            return myList;
        }

        /**
         * Construct an edit extract for an Account.
         * @param pType the account type
         * @return the edit Extract
         */
        public AccountList deriveEditList(final AccountType pType) {
            /* Build an empty Extract List */
            AccountList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Create a new account */
            myList.theAccount = new Account(myList);
            myList.theAccount.setActType(pType);
            myList.add(myList.theAccount);

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the list.
         * @param pAccount item
         * @return the newly added item
         */
        @Override
        public Account addCopyItem(final DataItem pAccount) {
            /* Can only clone an Account */
            if (!(pAccount instanceof Account)) {
                return null;
            }

            Account myAccount = new Account(this, (Account) pAccount);
            add(myAccount);
            return myAccount;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public Account addNewItem() {
            return null;
        }

        /**
         * Update account details after data update.
         * @throws JDataException on error
         */
        public void markActiveItems() throws JDataException {
            /* Access the iterator */
            Iterator<Account> myIterator = iterator();
            Account myCurr;

            /* Loop through the accounts */
            while (myIterator.hasNext()) {
                myCurr = myIterator.next();
                /* If we have a parent, mark the parent */
                if (myCurr.getParent() != null) {
                    myCurr.getParent().touchItem(myCurr);
                    if (!myCurr.isClosed()) {
                        myCurr.getParent().setNonCloseable();
                    }
                }

                /* If we have an alias, mark the alias */
                if (myCurr.getAlias() != null) {
                    myCurr.getAlias().touchItem(myCurr);
                    if (!myCurr.isClosed()) {
                        myCurr.getAlias().setNonCloseable();
                    }
                }

                /* Mark the AccountType */
                AccountType myType = myCurr.getActType();
                myType.touchItem(myCurr);

                /* If we are a child and have no latest event, then we are not close-able */
                /*
                 * if ((myCurr.isChild()) && (myCurr.getLatest() == null)) { myCurr.setNonCloseable(); }
                 */

                /* If we have patterns or are touched by patterns, then we are not close-able */
                if (myCurr.hasPatterns || myCurr.isPatterned) {
                    myCurr.setNonCloseable();
                }

                /* If we have a close date and a latest event */
                if ((myCurr.getClose() != null) && (myCurr.getLatest() != null)) {
                    /* Check whether we need to adjust the date */
                    myCurr.adjustClosed();
                }
            }

            /* If we are in final loading stage */
            if (getDataSet().getLoadState() == LoadState.FINAL) {
                /* Access a new iterator */
                myIterator = listIterator();

                /* Loop through the accounts */
                while (myIterator.hasNext()) {
                    myCurr = myIterator.next();

                    /* Validate the account */
                    myCurr.validate();
                    if (myCurr.hasErrors()) {
                        throw new JDataException(ExceptionClass.VALIDATE, myCurr, "Failed validation");
                    }
                }
            }
        }

        /**
         * Count the instances of a string.
         * @param pName the string to check for
         * @return The Item if present (or null)
         */
        protected int countInstances(final String pName) {
            /* Access the iterator */
            Iterator<Account> myIterator = iterator();
            int iCount = 0;

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Account myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    iCount++;
                }
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Search for a particular item by Name.
         * @param pName Name of item
         * @return The Item if present (or null)
         */
        public Account findItemByName(final String pName) {
            /* Access the iterator */
            Iterator<Account> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Account myCurr = myIterator.next();
                if (pName.equals(myCurr.getName())) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Get the market account from the list.
         * @return the Market account
         */
        public Account getMarket() {
            /* Access the iterator */
            Iterator<Account> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Account myCurr = myIterator.next();
                if (myCurr.isMarket()) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Get the TaxMan account from the list.
         * @return the TaxMan account
         */
        public Account getTaxMan() {
            /* Access the iterator */
            Iterator<Account> myIterator = iterator();

            /* Loop through the items to find the entry */
            while (myIterator.hasNext()) {
                Account myCurr = myIterator.next();
                if (myCurr.isTaxMan()) {
                    return myCurr;
                }
            }

            /* Return not found */
            return null;
        }

        /**
         * Add an Account.
         * @param uId the is
         * @param pName the Name of the account
         * @param pAcType the Name of the account type
         * @param pDesc the description of the account
         * @param pMaturity the Maturity date for a bond (or null)
         * @param pClosed the Close Date for the account (or null)
         * @param pParent the Name of the parent account (or null)
         * @param pAlias the Name of the alias account (or null)
         * @param pWebSite the webSite
         * @param pCustNo the customer no
         * @param pUserId the user id
         * @param pPassword the password
         * @param pAccount the account details
         * @param pNotes notes
         * @throws JDataException on error
         */
        public void addOpenItem(final Integer uId,
                                final String pName,
                                final String pAcType,
                                final String pDesc,
                                final Date pMaturity,
                                final Date pClosed,
                                final String pParent,
                                final String pAlias,
                                final char[] pWebSite,
                                final char[] pCustNo,
                                final char[] pUserId,
                                final char[] pPassword,
                                final char[] pAccount,
                                final char[] pNotes) throws JDataException {
            /* Access the account types and accounts */
            FinanceData myData = getDataSet();
            AccountTypeList myActTypes = myData.getAccountTypes();

            /* Look up the Account Type */
            AccountType myActType = myActTypes.findItemByName(pAcType);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                        + "] has invalid Account Type [" + pAcType + "]");
            }

            /* If we have a parent */
            Integer myParentId = null;
            if (pParent != null) {
                /* Look up the Parent */
                Account myParent = findItemByName(pParent);
                if (myParent == null) {
                    throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                            + "] has invalid Parent [" + pParent + "]");
                }
                myParentId = myParent.getId();
            }

            /* If we have a parent */
            Integer myAliasId = null;
            if (pAlias != null) {
                /* Look up the Parent */
                Account myAlias = findItemByName(pAlias);
                if (myAlias == null) {
                    throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                            + "] has invalid Alias [" + pAlias + "]");
                }
                myAliasId = myAlias.getId();
            }

            /* Create the new account */
            Account myAccount = new Account(this, uId, pName, myActType.getId(), pDesc, pMaturity, pClosed,
                    myParentId, myAliasId, pWebSite, pCustNo, pUserId, pPassword, pAccount, pNotes);

            /* Check that this Account has not been previously added */
            if (findItemByName(myAccount.getName()) != null) {
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate Account");
            }

            /* Add the Account to the list */
            append(myAccount);
        }

        /**
         * Add an Account.
         * @param uId the Id of the account
         * @param uControlId the control id
         * @param pName the Encrypted Name of the account
         * @param uAcTypeId the Id of the account type
         * @param pDesc the Encrypted Description of the account (or null)
         * @param pMaturity the Maturity date for a bond (or null)
         * @param pClosed the Close Date for the account (or null)
         * @param pParentId the Id of the parent account (or -1)
         * @param pAliasId the Id of the alias account (or -1)
         * @param pWebSite the Encrypted WebSite of the account
         * @param pCustNo the Encrypted CustomerId of the account
         * @param pUserId the Encrypted UserId of the account
         * @param pPassword the Encrypted Password of the account
         * @param pAccount the Encrypted Account details of the account
         * @param pNotes the Encrypted Notes for the account
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final byte[] pName,
                                  final Integer uAcTypeId,
                                  final byte[] pDesc,
                                  final Date pMaturity,
                                  final Date pClosed,
                                  final Integer pParentId,
                                  final Integer pAliasId,
                                  final byte[] pWebSite,
                                  final byte[] pCustNo,
                                  final byte[] pUserId,
                                  final byte[] pPassword,
                                  final byte[] pAccount,
                                  final byte[] pNotes) throws JDataException {
            /* Create the new account */
            Account myAccount = new Account(this, uId, uControlId, pName, uAcTypeId, pDesc, pMaturity,
                    pClosed, pParentId, pAliasId, pWebSite, pCustNo, pUserId, pPassword, pAccount, pNotes);

            /* Check that this AccountId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate AccountId");
            }

            /* Check that this Account has not been previously added */
            if (findItemByName(myAccount.getName()) != null) {
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate Account");
            }

            /* Add the Account to the list */
            append(myAccount);
        }

        /**
         * Validate newly loaded accounts. This is deliberately deferred until after loading of the
         * Rates/Patterns/Prices so as to validate the interrelationships
         * @throws JDataException on error
         */
        public void validateLoadedAccounts() throws JDataException {
            FinanceData myData = getDataSet();
            Account myCurr;

            /* Mark active items referenced by rates */
            myData.getRates().markActiveItems();

            /* Mark active items referenced by prices */
            myData.getPrices().markActiveItems();

            /* Mark active items referenced by patterns */
            myData.getPatterns().markActiveItems();

            /* Access the iterator */
            Iterator<Account> myIterator = iterator();

            /* Loop through the items */
            while (myIterator.hasNext()) {
                myCurr = myIterator.next();

                /* If the account has a parent Id */
                if (myCurr.getParentId() != null) {
                    /* Set the parent */
                    myCurr.setParent(findItemById(myCurr.getParentId()));
                    myCurr.getParent().touchItem(myCurr);
                }

                /* If the account has an alias Id */
                if (myCurr.getAliasId() != null) {
                    /* Set the alias */
                    myCurr.setAlias(findItemById(myCurr.getAliasId()));
                    myCurr.getAlias().touchItem(myCurr);
                }

                /* Mark the AccountType */
                AccountType myType = myCurr.getActType();
                myType.touchItem(myCurr);
            }

            /* Create another iterator */
            myIterator = iterator();

            /* Loop through the items */
            while (myIterator.hasNext()) {
                myCurr = myIterator.next();

                /* Validate the account */
                myCurr.validate();

                /* Handle validation failure */
                if (myCurr.hasErrors()) {
                    throw new JDataException(ExceptionClass.VALIDATE, myCurr, "Failed validation");
                }
            }
        }
    }
}
