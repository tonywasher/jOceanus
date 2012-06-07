/*******************************************************************************
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
package uk.co.tolcroft.finance.data;

import java.util.Date;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedCharArray;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.AccountType.AccountTypeList;
import uk.co.tolcroft.finance.data.Event.EventList;
import uk.co.tolcroft.finance.data.FinanceData.LoadState;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;

public class Account extends EncryptedItem<Account> {
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

    /* Field IDs */
    public static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityValueField("Name");
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");
    public static final JDataField FIELD_TYPE = FIELD_DEFS.declareEqualityValueField("AccountType");
    public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareDerivedValueField("Maturity");
    public static final JDataField FIELD_CLOSE = FIELD_DEFS.declareEqualityValueField("CloseDate");
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField("Parent");
    public static final JDataField FIELD_ALIAS = FIELD_DEFS.declareEqualityValueField("Alias");
    public static final JDataField FIELD_WEBSITE = FIELD_DEFS.declareEqualityValueField("WebSite");
    public static final JDataField FIELD_CUSTNO = FIELD_DEFS.declareEqualityValueField("CustomerNo");
    public static final JDataField FIELD_USERID = FIELD_DEFS.declareEqualityValueField("UserId");
    public static final JDataField FIELD_PASSWORD = FIELD_DEFS.declareEqualityValueField("Password");
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityValueField("Account");
    public static final JDataField FIELD_NOTES = FIELD_DEFS.declareEqualityValueField("Notes");
    public static final JDataField FIELD_EVTFIRST = FIELD_DEFS.declareLocalField("FirstEvent");
    public static final JDataField FIELD_EVTLAST = FIELD_DEFS.declareLocalField("LastEvent");
    public static final JDataField FIELD_INITPRC = FIELD_DEFS.declareLocalField("InitialPrice");
    public static final JDataField FIELD_HASDEBTS = FIELD_DEFS.declareLocalField("hasDebts");
    public static final JDataField FIELD_HASRATES = FIELD_DEFS.declareLocalField("hasRates");
    public static final JDataField FIELD_HASPRICE = FIELD_DEFS.declareLocalField("hasPrices");
    public static final JDataField FIELD_HASPATT = FIELD_DEFS.declareLocalField("hasPatterns");
    public static final JDataField FIELD_ISPATT = FIELD_DEFS.declareLocalField("isPatterned");
    public static final JDataField FIELD_ISPARENT = FIELD_DEFS.declareLocalField("isParent");
    public static final JDataField FIELD_ISALIASD = FIELD_DEFS.declareLocalField("isAliasedTo");
    public static final JDataField FIELD_ISCLSABL = FIELD_DEFS.declareLocalField("isCloseable");

    /**
     * The active set of values.
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(final EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    @Override
    public String formatObject() {
        return getName();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (pField == FIELD_EVTFIRST) {
            return theEarliest;
        }
        if (pField == FIELD_EVTLAST) {
            return theLatest;
        }
        if (pField == FIELD_INITPRC) {
            return theInitPrice;
        }
        if (pField == FIELD_HASDEBTS) {
            return hasDebts;
        }
        if (pField == FIELD_HASRATES) {
            return hasRates;
        }
        if (pField == FIELD_HASPRICE) {
            return hasPrices;
        }
        if (pField == FIELD_HASPATT) {
            return hasPatterns;
        }
        if (pField == FIELD_ISPATT) {
            return isPatterned;
        }
        if (pField == FIELD_ISPARENT) {
            return isParent;
        }
        if (pField == FIELD_ISALIASD) {
            return isAliasedTo;
        }
        if (pField == FIELD_ISCLSABL) {
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

    /* Members */
    private Event theEarliest = null;
    private Event theLatest = null;
    private AccountPrice theInitPrice = null;
    private boolean isCloseable = true;
    private boolean hasDebts = false;
    private boolean hasRates = false;
    private boolean hasPrices = false;
    private boolean hasPatterns = false;
    private boolean isPatterned = false;
    private boolean isParent = false;
    private boolean isAliasedTo = false;

    /* Access methods */
    public String getName() {
        return getName(theValueSet);
    }

    public byte[] getNameBytes() {
        return getNameBytes(theValueSet);
    }

    private EncryptedString getNameField() {
        return getNameField(theValueSet);
    }

    public String getDesc() {
        return getDesc(theValueSet);
    }

    public byte[] getDescBytes() {
        return getDescBytes(theValueSet);
    }

    private EncryptedString getDescField() {
        return getDescField(theValueSet);
    }

    public Account getParent() {
        return getParent(theValueSet);
    }

    public Integer getParentId() {
        return getParentId(theValueSet);
    }

    public Account getAlias() {
        return getAlias(theValueSet);
    }

    public Integer getAliasId() {
        return getAliasId(theValueSet);
    }

    public Event getEarliest() {
        return theEarliest;
    }

    public Event getLatest() {
        return theLatest;
    }

    public AccountPrice getInitPrice() {
        return theInitPrice;
    }

    public AccountType getActType() {
        return getAccountType(theValueSet);
    }

    public int getOrder() {
        return getOrder(theValueSet);
    }

    public DateDay getMaturity() {
        return getMaturity(theValueSet);
    }

    public DateDay getClose() {
        return getClose(theValueSet);
    }

    public char[] getWebSite() {
        return getWebSite(theValueSet);
    }

    public byte[] getWebSiteBytes() {
        return getWebSiteBytes(theValueSet);
    }

    private EncryptedCharArray getWebSiteField() {
        return getWebSiteField(theValueSet);
    }

    public char[] getCustNo() {
        return getCustNo(theValueSet);
    }

    public byte[] getCustNoBytes() {
        return getCustNoBytes(theValueSet);
    }

    private EncryptedCharArray getCustNoField() {
        return getCustNoField(theValueSet);
    }

    public char[] getUserId() {
        return getUserId(theValueSet);
    }

    public byte[] getUserIdBytes() {
        return getUserIdBytes(theValueSet);
    }

    private EncryptedCharArray getUserIdField() {
        return getUserIdField(theValueSet);
    }

    public char[] getPassword() {
        return getPassword(theValueSet);
    }

    public byte[] getPasswordBytes() {
        return getPasswordBytes(theValueSet);
    }

    private EncryptedCharArray getPasswordField() {
        return getPasswordField(theValueSet);
    }

    public char[] getAccount() {
        return getAccount(theValueSet);
    }

    public byte[] getAccountBytes() {
        return getAccountBytes(theValueSet);
    }

    private EncryptedCharArray getAccountField() {
        return getAccountField(theValueSet);
    }

    public char[] getNotes() {
        return getNotes(theValueSet);
    }

    public byte[] getNotesBytes() {
        return getNotesBytes(theValueSet);
    }

    private EncryptedCharArray getNotesField() {
        return getNotesField(theValueSet);
    }

    public boolean isCloseable() {
        return isCloseable;
    }

    public boolean hasDebts() {
        return hasDebts;
    }

    public boolean isParent() {
        return isParent;
    }

    public boolean isClosed() {
        return (getClose() != null);
    }

    public boolean isAlias() {
        return (getAliasId() != null);
    }

    public boolean isAliasedTo() {
        return isAliasedTo;
    }

    public boolean isDeletable() {
        return ((theLatest == null) && (!isDeleted()) && (!isParent) && (!hasRates)
                && ((!hasPrices) || (getState() == DataState.NEW)) && (!hasPatterns) && (!isAliasedTo)
                && (!isPatterned) && (!getActType().isReserved()));
    }

    public static String getName(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NAME, String.class);
    }

    public static byte[] getNameBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NAME);
    }

    private static EncryptedString getNameField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NAME, EncryptedString.class);
    }

    public static String getDesc(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    public static byte[] getDescBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    public static AccountType getAccountType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TYPE, AccountType.class);
    }

    public static Integer getOrder(final ValueSet pValueSet) {
        Object myType = pValueSet.getValue(FIELD_TYPE);
        if (myType instanceof AccountType) {
            return ((AccountType) myType).getOrder();
        }
        return null;
    }

    public static DateDay getMaturity(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_MATURITY, DateDay.class);
    }

    public static DateDay getClose(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CLOSE, DateDay.class);
    }

    public static Account getParent(final ValueSet pValueSet) {
        Object myAccount = pValueSet.getValue(FIELD_PARENT);
        if (myAccount instanceof Account) {
            return (Account) myAccount;
        }
        return null;
    }

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

    public static Account getAlias(final ValueSet pValueSet) {
        Object myAccount = pValueSet.getValue(FIELD_ALIAS);
        if (myAccount instanceof Account) {
            return (Account) myAccount;
        }
        return null;
    }

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

    public static char[] getWebSite(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_WEBSITE, char[].class);
    }

    public static byte[] getWebSiteBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_WEBSITE);
    }

    private static EncryptedCharArray getWebSiteField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_WEBSITE, EncryptedCharArray.class);
    }

    public static char[] getCustNo(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_CUSTNO, char[].class);
    }

    public static byte[] getCustNoBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_CUSTNO);
    }

    private static EncryptedCharArray getCustNoField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CUSTNO, EncryptedCharArray.class);
    }

    public static char[] getUserId(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_USERID, char[].class);
    }

    public static byte[] getUserIdBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_USERID);
    }

    private static EncryptedCharArray getUserIdField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_USERID, EncryptedCharArray.class);
    }

    public static char[] getPassword(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_PASSWORD, char[].class);
    }

    public static byte[] getPasswordBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_PASSWORD);
    }

    private static EncryptedCharArray getPasswordField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PASSWORD, EncryptedCharArray.class);
    }

    public static char[] getAccount(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_ACCOUNT, char[].class);
    }

    public static byte[] getAccountBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_ACCOUNT);
    }

    private static EncryptedCharArray getAccountField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ACCOUNT, EncryptedCharArray.class);
    }

    public static char[] getNotes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_NOTES, char[].class);
    }

    public static byte[] getNotesBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_NOTES);
    }

    private static EncryptedCharArray getNotesField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NOTES, EncryptedCharArray.class);
    }

    private void setValueName(String pName) throws JDataException {
        setEncryptedValue(FIELD_NAME, pName);
    }

    private void setValueName(byte[] pName) throws JDataException {
        setEncryptedValue(FIELD_NAME, pName, String.class);
    }

    private void setValueName(EncryptedString pName) {
        theValueSet.setValue(FIELD_NAME, pName);
    }

    private void setValueDesc(String pDesc) throws JDataException {
        setEncryptedValue(FIELD_DESC, pDesc);
    }

    private void setValueDesc(byte[] pDesc) throws JDataException {
        setEncryptedValue(FIELD_DESC, pDesc, String.class);
    }

    private void setValueDesc(EncryptedString pDesc) {
        theValueSet.setValue(FIELD_DESC, pDesc);
    }

    private void setValueType(AccountType pValue) {
        theValueSet.setValue(FIELD_TYPE, pValue);
    }

    private void setValueType(Integer pValue) {
        theValueSet.setValue(FIELD_TYPE, pValue);
    }

    private void setValueMaturity(DateDay pValue) {
        theValueSet.setValue(FIELD_MATURITY, pValue);
    }

    private void setValueClose(DateDay pValue) {
        theValueSet.setValue(FIELD_CLOSE, pValue);
    }

    private void setValueParent(Account pValue) {
        theValueSet.setValue(FIELD_PARENT, pValue);
    }

    private void setValueParent(Integer pValue) {
        theValueSet.setValue(FIELD_PARENT, pValue);
    }

    private void setValueAlias(Account pValue) {
        theValueSet.setValue(FIELD_ALIAS, pValue);
    }

    private void setValueAlias(Integer pValue) {
        theValueSet.setValue(FIELD_ALIAS, pValue);
    }

    private void setValueWebSite(char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_WEBSITE, pValue);
    }

    private void setValueWebSite(byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_WEBSITE, pValue, String.class);
    }

    private void setValueWebSite(EncryptedCharArray pValue) {
        theValueSet.setValue(FIELD_WEBSITE, pValue);
    }

    private void setValueCustNo(char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_CUSTNO, pValue);
    }

    private void setValueCustNo(byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_CUSTNO, pValue, String.class);
    }

    private void setValueCustNo(EncryptedCharArray pValue) {
        theValueSet.setValue(FIELD_CUSTNO, pValue);
    }

    private void setValueUserId(char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_USERID, pValue);
    }

    private void setValueUserId(byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_USERID, pValue, String.class);
    }

    private void setValueUserId(EncryptedCharArray pValue) {
        theValueSet.setValue(FIELD_USERID, pValue);
    }

    private void setValuePassword(char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_PASSWORD, pValue);
    }

    private void setValuePassword(byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_PASSWORD, pValue, String.class);
    }

    private void setValuePassword(EncryptedCharArray pValue) {
        theValueSet.setValue(FIELD_PASSWORD, pValue);
    }

    private void setValueAccount(char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_ACCOUNT, pValue);
    }

    private void setValueAccount(byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_ACCOUNT, pValue, String.class);
    }

    private void setValueAccount(EncryptedCharArray pValue) {
        theValueSet.setValue(FIELD_ACCOUNT, pValue);
    }

    private void setValueNotes(char[] pValue) throws JDataException {
        setEncryptedValue(FIELD_NOTES, pValue);
    }

    private void setValueNotes(byte[] pValue) throws JDataException {
        setEncryptedValue(FIELD_NOTES, pValue, String.class);
    }

    private void setValueNotes(EncryptedCharArray pValue) {
        theValueSet.setValue(FIELD_NOTES, pValue);
    }

    /* Linking methods */
    @Override
    public Account getBase() {
        return (Account) super.getBase();
    }

    @Override
    public boolean isLocked() {
        return isClosed();
    }

    /**
     * Copy flags
     * @param pItem the original item
     */
    @Override
    protected void copyFlags(Account pItem) {
        /* Copy Main flags */
        super.copyFlags(pItem);

        /* Copy Remaining flags */
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
     * Construct a copy of an Account
     * @param pList the list
     * @param pAccount The Account to copy
     */
    public Account(AccountList pList,
                   Account pAccount) {
        /* Set standard values */
        super(pList, pAccount);
        ListStyle myOldStyle = pAccount.getStyle();

        /* Switch on the ListStyle */
        switch (getStyle()) {
            case EDIT:
                /* If this is a view creation */
                if (myOldStyle == ListStyle.CORE) {
                    /* Account is based on the original element */
                    setBase(pAccount);
                    copyFlags(pAccount);
                    pList.setNewId(this);
                    break;
                }

                /* Else this is a duplication so treat as new item */
                setId(0);
                pList.setNewId(this);
                break;
            case CLONE:
                reBuildLinks(pList, pList.getData());
            case COPY:
            case CORE:
                /* Reset Id if this is an insert from a view */
                if (myOldStyle == ListStyle.EDIT)
                    setId(0);
                pList.setNewId(this);
                break;
            case UPDATE:
                setBase(pAccount);
                setState(pAccount.getState());
                break;
        }
    }

    /**
     * Standard constructor for account added from Database/Backup
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
     * @throws JDataException
     */
    private Account(AccountList pList,
                    int uId,
                    int uControlId,
                    byte[] pName,
                    int uAcTypeId,
                    byte[] pDesc,
                    Date pMaturity,
                    Date pClose,
                    Integer pParentId,
                    Integer pAliasId,
                    byte[] pWebSite,
                    byte[] pCustNo,
                    byte[] pUserId,
                    byte[] pPassword,
                    byte[] pAccount,
                    byte[] pNotes) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Local Variable */
            AccountType myActType;

            /* Store the IDs */
            setValueType(uAcTypeId);
            setValueParent(pParentId);
            setValueAlias(pAliasId);

            /* Set ControlId */
            setControlKey(uControlId);

            /* Look up the Account Type */
            FinanceData myData = pList.getData();
            myActType = myData.getAccountTypes().searchFor(uAcTypeId);
            if (myActType == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Type Id");
            setValueType(myActType);

            /* Parse the maturity date if it exists */
            if (pMaturity != null)
                setValueMaturity(new DateDay(pMaturity));

            /* Parse the closed date if it exists */
            if (pClose != null)
                setValueClose(new DateDay(pClose));

            /* Record the encrypted values */
            setValueName(pName);
            setValueDesc(pDesc);
            setValueWebSite(pWebSite);
            setValueCustNo(pCustNo);
            setValueUserId(pUserId);
            setValuePassword(pPassword);
            setValueAccount(pAccount);
            setValueNotes(pNotes);

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Standard constructor for account added from SpreadSheet
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
     * @throws JDataException
     */
    private Account(AccountList pList,
                    int uId,
                    String sName,
                    int uAcTypeId,
                    String pDesc,
                    Date pMaturity,
                    Date pClose,
                    Integer pParentId,
                    Integer pAliasId,
                    char[] pWebSite,
                    char[] pCustNo,
                    char[] pUserId,
                    char[] pPassword,
                    char[] pAccount,
                    char[] pNotes) throws JDataException {
        /* Initialise the item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Local Variable */
            AccountType myActType;

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
            FinanceData myData = pList.getData();
            myActType = myData.getAccountTypes().searchFor(uAcTypeId);
            if (myActType == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Account Type Id");
            setValueType(myActType);

            /* Parse the maturity date if it exists */
            if (pMaturity != null)
                setValueMaturity(new DateDay(pMaturity));

            /* Parse the closed date if it exists */
            if (pClose != null)
                setValueClose(new DateDay(pClose));

            /* Allocate the id */
            pList.setNewId(this);
        }

        /* Catch Exceptions */
        catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /* Standard constructor for a newly inserted account */
    public Account(AccountList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
        pList.setNewId(this);
    }

    /**
     * Compare this account to another to establish sort order.
     * @param pThat The Account to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
     */
    @Override
    public int compareTo(Object pThat) {
        long result;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is an Account */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as an Account */
        Account myThat = (Account) pThat;

        /* If we are comparing owner with non-owner */
        if (isOwner() != myThat.isOwner()) {
            /* List owners first */
            if (isOwner())
                return -1;
            else
                return 1;
        }

        /* If we are comparing alias with non-alias */
        if (isAlias() != myThat.isAlias()) {
            /* List alias after non-alias */
            if (isAlias())
                return 1;
            else
                return -1;
        }

        /* If the order differs */
        if (getOrder() < myThat.getOrder())
            return -1;
        if (getOrder() > myThat.getOrder())
            return 1;

        /* If the names differ */
        if (getName() != myThat.getName()) {
            /* Handle nulls */
            if (this.getName() == null)
                return 1;
            if (myThat.getName() == null)
                return -1;

            /* Compare the names */
            result = getName().compareTo(myThat.getName());
            if (result < 0)
                return -1;
            if (result > 0)
                return 1;
        }

        /* Compare the IDs */
        result = (int) (getId() - myThat.getId());
        if (result == 0)
            return 0;
        else if (result < 0)
            return -1;
        else
            return 1;
    }

    /**
     * Rebuild Links to partner data
     * @param pList the list
     * @param pData the DataSet
     */
    protected void reBuildLinks(AccountList pList,
                                FinanceData pData) {
        /* Update the Encryption details */
        super.reBuildLinks(pData);

        /* Access Account types */
        AccountTypeList myTypes = pData.getAccountTypes();

        /* Update to use the local copy of the AccountTypes */
        AccountType myType = getActType();
        AccountType myNewType = myTypes.searchFor(myType.getId());
        setValueType(myNewType);

        /* If we have a parent */
        Account myAct = getParent();
        if (myAct != null) {
            /* Update it */
            Account myNewAct = pList.searchFor(myAct.getId());
            setValueParent(myNewAct);
        }

        /* If we have an alias */
        myAct = getAlias();
        if (myAct != null) {
            /* Update it */
            Account myNewAct = pList.searchFor(myAct.getId());
            setValueAlias(myNewAct);
        }
    }

    /* Account flags */
    public boolean isPriced() {
        return getActType().isPriced();
    }

    protected boolean isMarket() {
        return getActType().isMarket();
    }

    public boolean isExternal() {
        return getActType().isExternal();
    }

    protected boolean isSpecial() {
        return getActType().isSpecial();
    }

    protected boolean isInternal() {
        return getActType().isInternal();
    }

    protected boolean isInheritance() {
        return getActType().isInheritance();
    }

    protected boolean isTaxMan() {
        return getActType().isTaxMan();
    }

    public boolean isMoney() {
        return getActType().isMoney();
    }

    protected boolean isCash() {
        return getActType().isCash();
    }

    protected boolean isWriteOff() {
        return getActType().isWriteOff();
    }

    protected boolean isEndowment() {
        return getActType().isEndowment();
    }

    public boolean isOwner() {
        return getActType().isOwner();
    }

    public boolean isTaxFree() {
        return getActType().isTaxFree();
    }

    public boolean isUnitTrust() {
        return getActType().isUnitTrust();
    }

    public boolean isDebt() {
        return getActType().isDebt();
    }

    public boolean isChild() {
        return getActType().isChild();
    }

    public boolean isBond() {
        return getActType().isBond();
    }

    public boolean isBenefit() {
        return getActType().isBenefit();
    }

    public boolean isLifeBond() {
        return getActType().isLifeBond();
    }

    public boolean isCapital() {
        return getActType().isCapital();
    }

    public boolean isCapitalGains() {
        return getActType().isCapitalGains();
    }

    /**
     * Validate the account
     */
    @Override
    public void validate() {
        boolean isValid;
        AccountType myType = getActType();
        String myName = getName();
        String myDesc = getDesc();
        Account myParent = getParent();
        Account myAlias = getAlias();
        AccountList myList = (AccountList) getList();
        FinanceData mySet = myList.getData();

        /* AccountType must be non-null */
        if (myType == null)
            addError("AccountType must be non-null", FIELD_TYPE);
        else if (!myType.getEnabled())
            addError("AccountType must be enabled", FIELD_TYPE);

        /* Name must be non-null */
        if (myName == null) {
            addError("Name must be non-null", FIELD_NAME);
        }

        /* Check that the name is unique */
        else {
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
                if (hasPrices)
                    addError("Aliased account has prices", FIELD_TYPE);

                /* Alias account must have prices */
                if ((!myAlias.hasPrices) && (myAlias.theEarliest != null))
                    addError("Alias account has no prices", FIELD_TYPE);
            }

            /* else this is a standard account */
            else {
                /* Must have prices */
                if ((!hasPrices) && (theEarliest != null))
                    addError("Priced account has no prices", FIELD_TYPE);
            }
        }

        /* else the account is not priced */
        else {
            /* Prices cannot exist */
            if (hasPrices)
                addError("non-Priced account has prices", FIELD_TYPE);
        }

        /* If the account is not a child then parent cannot exist */
        if (!myType.isChild()) {
            if (myParent != null)
                addError("Non-child account has parent", FIELD_PARENT);
        }

        /* else we should have a parent */
        else {
            /* If data has been fully loaded we have no parent */
            if ((mySet.getLoadState() != LoadState.INITIAL) && (myParent == null))
                addError("Child Account must have parent", FIELD_PARENT);

            /* if we have a parent */
            if (myParent != null) {
                /* check that any parent is owner */
                if (!myParent.isOwner())
                    addError("Parent account must be owner", FIELD_PARENT);

                /* If we are open then parent must be open */
                if (!isClosed() && myParent.isClosed())
                    addError("Parent account must not be closed", FIELD_PARENT);
            }
        }

        /* If we have an alias */
        if (myAlias != null) {
            /* Access the alias type */
            AccountType myAliasType = myAlias.getActType();

            /* Cannot alias to self */
            if (!Difference.isEqual(this, myAlias))
                addError("Cannot alias to self", FIELD_ALIAS);

            /* Cannot alias to same type */
            else if (!Difference.isEqual(myType, myAliasType))
                addError("Cannot alias to same account type", FIELD_ALIAS);

            /* Must be alias type */
            if (!myType.canAlias())
                addError("This account type cannot alias", FIELD_ALIAS);

            /* Must not be aliased to */
            if (isAliasedTo)
                addError("This account is already aliased to", FIELD_ALIAS);

            /* Alias must be alias type */
            if (!myAliasType.canAlias())
                addError("The alias account type is invalid", FIELD_ALIAS);

            /* Alias cannot be aliased */
            if (myAlias.isAlias())
                addError("The alias account is already aliased", FIELD_ALIAS);
        }

        /* If the account has rates then it must be money-based */
        if (hasRates) {
            if (!myType.isMoney())
                addError("non-Money account has rates", FIELD_TYPE);
        }

        /* If the account has a maturity rate then it must be a bond */
        if (getMaturity() != null) {
            if (!myType.isBond())
                addError("non-Bond has maturity date", FIELD_MATURITY);
        }

        /* Open Bond accounts must have maturity */
        if (myType.isBond()) {
            if (!isClosed() && (getMaturity() == null))
                addError("Bond must have maturity date", FIELD_MATURITY);
        }

        /* If data has been fully loaded and the account is closed */
        if ((mySet.getLoadState() != LoadState.INITIAL) && (isClosed())) {
            /* Account must be close-able */
            if (!isCloseable())
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
        isValid = !hasErrors();
        if (isValid)
            setValidEdit();
    }

    /**
     * Get the value of an account on a specific date
     * @param pDate The date of the valuation
     * @return Valuation of account
     */
    public Money getValue(final DateDay pDate) {
        Event myCurr;
        EventList myEvents;
        DataListIterator<Event> myIterator;
        int myResult;
        Money myAmount;
        Money myValue;
        AccountList myList = (AccountList) getList();
        FinanceData mySet = myList.getData();

        /* Initialise money */
        myValue = new Money(0);

        /* Access the Events and create an iterator on the events */
        myEvents = mySet.getEvents();
        myIterator = myEvents.listIterator();

        /* Loop through the Events extracting relevant elements */
        while ((myCurr = myIterator.next()) != null) {
            /* Check the range */
            myResult = pDate.compareTo(myCurr.getDate());

            /* Handle out of range */
            if (myResult == -1)
                break;

            /* If this Event relates to this account */
            if (myCurr.relatesTo(this)) {
                /* Access the amount */
                myAmount = myCurr.getAmount();

                /* If this is a credit add the value */
                if (this.compareTo(myCurr.getCredit()) == 0)
                    myValue.addAmount(myAmount);

                /* else subtract from value */
                else
                    myValue.subtractAmount(myAmount);
            }
        }

        /* Return the value */
        return myValue;
    }

    /**
     * Clear the active account flags
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
     * Touch an account
     * @param pObject the object touch the account
     */
    @Override
    public void touchItem(DataItem<?> pObject) {
        /* Note that the account is Active */
        super.touchItem(pObject);

        /* If we are being touched by a rate */
        if (pObject instanceof AccountRate) {
            /* Note flags */
            hasRates = true;
        }

        /* If we are being touched by a price */
        else if (pObject instanceof AccountPrice) {
            /* Note flags */
            hasPrices = true;
            if (theInitPrice == null)
                theInitPrice = (AccountPrice) pObject;
        }

        /* If we are being touched by a pattern */
        else if (pObject instanceof Pattern) {
            /* Access as pattern */
            Pattern myPattern = (Pattern) pObject;

            /* Note flags */
            if (Difference.isEqual(myPattern.getAccount(), this))
                hasPatterns = true;
            if (Difference.isEqual(myPattern.getPartner(), this))
                isPatterned = true;
        }

        /* If we are being touched by an event */
        else if (pObject instanceof Event) {
            /* Access as event */
            Event myEvent = (Event) pObject;

            /* Note flags */
            /* Record the event */
            if (theEarliest == null)
                theEarliest = myEvent;
            theLatest = myEvent;

            /* If we have a parent, touch it */
            if (getParent() != null)
                getParent().touchItem(pObject);
        }

        /* If we are being touched by another account */
        else if (pObject instanceof Account) {
            /* Access as account */
            Account myAccount = (Account) pObject;

            /* Note flags */
            if (Difference.isEqual(myAccount.getAlias(), this))
                isAliasedTo = true;
            if (Difference.isEqual(myAccount.getParent(), this)) {
                isParent = true;
                if (myAccount.isDebt())
                    hasDebts = true;
            }
        }
    }

    /**
     * Set non-closeable
     */
    public void setNonCloseable() {
        /* Record the status */
        isCloseable = false;
    }

    /**
     * Adjust closed date
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
     * Close the account
     */
    public void closeAccount() {
        /* Close the account */
        setClose(theLatest.getDate());
    }

    /**
     * Re-open the account
     */
    public void reOpenAccount() {
        /* Reopen the account */
        setClose(null);
    }

    /**
     * Set a new description
     * @param pDesc the description
     * @throws JDataException
     */
    public void setDescription(String pDesc) throws JDataException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new maturity date
     * @param pDate the new date
     */
    public void setMaturity(DateDay pDate) {
        setValueMaturity(pDate);
    }

    /**
     * Set a new close date
     * @param pDate the new date
     */
    public void setClose(DateDay pDate) {
        setValueClose(pDate);
    }

    /**
     * Set a new parent
     * @param pParent the new parent
     */
    public void setParent(Account pParent) {
        setValueParent(pParent);
    }

    /**
     * Set a new alias
     * @param pAlias the new alias
     */
    public void setAlias(Account pAlias) {
        setValueAlias(pAlias);
    }

    /**
     * Set a new account name
     * @param pName the new name
     * @throws JDataException
     */
    public void setAccountName(String pName) throws JDataException {
        setValueName(pName);
    }

    /**
     * Set a new account type
     * @param pType the new type
     */
    public void setActType(AccountType pType) {
        setValueType(pType);
    }

    /**
     * Set a new web site
     * @param pWebSite the new site
     * @throws JDataException
     */
    public void setWebSite(char[] pWebSite) throws JDataException {
        setValueWebSite(pWebSite);
    }

    /**
     * Set a new customer number
     * @param pCustNo the new number
     * @throws JDataException
     */
    public void setCustNo(char[] pCustNo) throws JDataException {
        setValueCustNo(pCustNo);
    }

    /**
     * Set a new UserId
     * @param pUserId the new id
     * @throws JDataException
     */
    public void setUserId(char[] pUserId) throws JDataException {
        setValueUserId(pUserId);
    }

    /**
     * Set a new password
     * @param pPassword the new password
     * @throws JDataException
     */
    public void setPassword(char[] pPassword) throws JDataException {
        setValuePassword(pPassword);
    }

    /**
     * Set a new account
     * @param pAccount the new account
     * @throws JDataException
     */
    public void setAccount(char[] pAccount) throws JDataException {
        setValueAccount(pAccount);
    }

    /**
     * Set a new notes
     * @param pNotes the new notes
     * @throws JDataException
     */
    public void setNotes(char[] pNotes) throws JDataException {
        setValueNotes(pNotes);
    }

    /**
     * Update base account from an edited account
     * @param pAccount the edited account
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(DataItem<?> pAccount) {
        Account myAccount = (Account) pAccount;
        boolean bChanged = false;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Name if required */
        if (!Difference.isEqual(getName(), myAccount.getName()))
            setValueName(myAccount.getNameField());

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myAccount.getDesc()))
            setValueDesc(myAccount.getDescField());

        /* Update the account type if required */
        if (!Difference.isEqual(getActType(), myAccount.getActType()))
            setValueType(myAccount.getActType());

        /* Update the maturity if required */
        if (!Difference.isEqual(getMaturity(), myAccount.getMaturity()))
            setValueMaturity(myAccount.getMaturity());

        /* Update the close if required */
        if (!Difference.isEqual(getClose(), myAccount.getClose()))
            setValueClose(myAccount.getClose());

        /* Update the parent if required */
        if (!Difference.isEqual(getParent(), myAccount.getParent()))
            setValueParent(myAccount.getParent());

        /* Update the alias if required */
        if (!Difference.isEqual(getAlias(), myAccount.getAlias()))
            setValueAlias(myAccount.getAlias());

        /* Update the WebSite if required */
        if (!Difference.isEqual(getWebSite(), myAccount.getWebSite()))
            setValueWebSite(myAccount.getWebSiteField());

        /* Update the customer number if required */
        if (!Difference.isEqual(getCustNo(), myAccount.getCustNo()))
            setValueCustNo(myAccount.getCustNoField());

        /* Update the UserId if required */
        if (!Difference.isEqual(getUserId(), myAccount.getUserId()))
            setValueUserId(myAccount.getUserIdField());

        /* Update the Password if required */
        if (!Difference.isEqual(getPassword(), myAccount.getPassword()))
            setValuePassword(myAccount.getPasswordField());

        /* Update the account if required */
        if (!Difference.isEqual(getAccount(), myAccount.getAccount()))
            setValueAccount(myAccount.getAccountField());

        /* Update the notes if required */
        if (!Difference.isEqual(getNotes(), myAccount.getNotes()))
            setValueNotes(myAccount.getNotesField());

        /* Check for changes */
        if (checkForHistory()) {
            /* Set changed status */
            setState(DataState.CHANGED);
            bChanged = true;
        }

        /* Return to caller */
        return bChanged;
    }

    /**
     * AccountList class
     */
    public static class AccountList extends EncryptedList<AccountList, Account> {
        /* Properties */
        private Account theAccount = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /* Access DataSet correctly */
        @Override
        public FinanceData getData() {
            return (FinanceData) super.getData();
        }

        public Account getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE account list
         * @param pData the DataSet for the list
         */
        protected AccountList(FinanceData pData) {
            super(AccountList.class, Account.class, pData);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        private AccountList(AccountList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the list style
         * @return the update Extract
         */
        private AccountList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            AccountList myList = new AccountList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public AccountList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public AccountList getEditList() {
            return getExtractList(ListStyle.EDIT);
        }

        @Override
        public AccountList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public AccountList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            AccountList myList = new AccountList(this);
            myList.setData(pDataSet);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        /**
         * Construct a difference Account list
         * @param pOld the old Account list
         * @return the difference list
         */
        @Override
        protected AccountList getDifferences(AccountList pOld) {
            /* Build an empty Difference List */
            AccountList myList = new AccountList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Construct an edit extract for an Account.
         * @param pAccount the relevant account
         * @return the edit Extract
         */
        public AccountList getEditList(Account pAccount) {
            /* Build an empty Extract List */
            AccountList myList = new AccountList(this);

            /* Set the correct style */
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
        public AccountList getEditList(AccountType pType) {
            /* Build an empty Extract List */
            AccountList myList = new AccountList(this);

            /* Set the correct style */
            myList.setStyle(ListStyle.EDIT);

            /* Create a new account */
            myList.theAccount = new Account(myList);
            myList.theAccount.setActType(pType);
            myList.add(myList.theAccount);

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the list
         * @param pAccount item
         * @return the newly added item
         */
        @Override
        public Account addNewItem(DataItem<?> pAccount) {
            Account myAccount = new Account(this, (Account) pAccount);
            add(myAccount);
            return myAccount;
        }

        /**
         * Create a new empty element in the edit list (null-operation)
         * @return the newly added item
         */
        @Override
        public Account addNewItem() {
            return null;
        }

        /**
         * Update account details after data update
         * @throws JDataException
         */
        public void markActiveItems() throws JDataException {
            DataListIterator<Account> myIterator;
            Account myCurr;
            AccountType myType;

            /* Access the iterator */
            myIterator = listIterator();

            /* Loop through the accounts */
            while ((myCurr = myIterator.next()) != null) {
                /* If we have a parent, mark the parent */
                if (myCurr.getParent() != null) {
                    myCurr.getParent().touchItem(myCurr);
                    if (!myCurr.isClosed())
                        myCurr.getParent().setNonCloseable();
                }

                /* If we have an alias, mark the alias */
                if (myCurr.getAlias() != null) {
                    myCurr.getAlias().touchItem(myCurr);
                    if (!myCurr.isClosed())
                        myCurr.getAlias().setNonCloseable();
                }

                /* Mark the AccountType */
                myType = myCurr.getActType();
                myType.touchItem(myCurr);

                /* If we are a child and have no latest event, then we are not close-able */
                if ((myCurr.isChild()) && (myCurr.getLatest() == null)) {
                    myCurr.setNonCloseable();
                }

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
            if (getData().getLoadState() == LoadState.FINAL) {
                /* Access a new iterator */
                myIterator = listIterator();

                /* Loop through the accounts */
                while ((myCurr = myIterator.next()) != null) {
                    /* Validate the account */
                    myCurr.validate();
                    if (myCurr.hasErrors())
                        throw new JDataException(ExceptionClass.VALIDATE, myCurr, "Failed validation");
                }
            }
        }

        /**
         * Count the instances of a string
         * @param pName the string to check for
         * @return The Item if present (or null)
         */
        protected int countInstances(String pName) {
            DataListIterator<Account> myIterator;
            Account myCurr;
            int iDiff;
            int iCount = 0;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = pName.compareTo(myCurr.getName());
                if (iDiff == 0)
                    iCount++;
            }

            /* Return to caller */
            return iCount;
        }

        /**
         * Search for a particular item by Name
         * @param sName Name of item
         * @return The Item if present (or null)
         */
        public Account searchFor(String sName) {
            DataListIterator<Account> myIterator;
            Account myCurr;
            int iDiff;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                iDiff = sName.compareTo(myCurr.getName());
                if (iDiff == 0)
                    break;
            }

            /* Return to caller */
            return myCurr;
        }

        /**
         * Get the market account from the list
         * @return the Market account
         */
        public Account getMarket() {
            DataListIterator<Account> myIterator;
            Account myCurr;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                if (myCurr.isMarket())
                    break;
            }

            /* Return */
            return myCurr;
        }

        /**
         * Get the TaxMan account from the list
         * @return the TaxMan account
         */
        public Account getTaxMan() {
            DataListIterator<Account> myIterator;
            Account myCurr;

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                if (myCurr.isTaxMan())
                    break;
            }

            /* Return */
            return myCurr;
        }

        /**
         * Add an Account
         * @param uId the is
         * @param pName the Name of the account
         * @param pAcType the Name of the account type
         * @param pDesc the description of the account
         * @param pMaturity the Maturity date for a bond (or null)
         * @param pClosed the Close Date for the account (or null)
         * @param pParent the Name of the parent account (or null)
         * @param pAlias the Name of the alias account (or null)
         * @param pWebSite the website
         * @param pCustNo the customer no
         * @param pUserId the user id
         * @param pPassword the password
         * @param pAccount the account details
         * @param pNotes notes
         * @throws JDataException on error
         */
        public void addItem(int uId,
                            String pName,
                            String pAcType,
                            String pDesc,
                            Date pMaturity,
                            Date pClosed,
                            String pParent,
                            String pAlias,
                            char[] pWebSite,
                            char[] pCustNo,
                            char[] pUserId,
                            char[] pPassword,
                            char[] pAccount,
                            char[] pNotes) throws JDataException {
            AccountType.AccountTypeList myActTypes;
            AccountType myActType;
            Account myAccount;
            Account myParent;
            Account myAlias;
            Integer myParentId = null;
            Integer myAliasId = null;

            /* Access the account types and accounts */
            myActTypes = getData().getAccountTypes();

            /* Look up the Account Type */
            myActType = myActTypes.searchFor(pAcType);
            if (myActType == null)
                throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                        + "] has invalid Account Type [" + pAcType + "]");

            /* If we have a parent */
            if (pParent != null) {
                /* Look up the Parent */
                myParent = searchFor(pParent);
                if (myParent == null)
                    throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                            + "] has invalid Parent [" + pParent + "]");
                myParentId = myParent.getId();
            }

            /* If we have a parent */
            if (pAlias != null) {
                /* Look up the Parent */
                myAlias = searchFor(pAlias);
                if (myAlias == null)
                    throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                            + "] has invalid Alias [" + pAlias + "]");
                myAliasId = myAlias.getId();
            }

            /* Create the new account */
            myAccount = new Account(this, uId, pName, myActType.getId(), pDesc, pMaturity, pClosed,
                    myParentId, myAliasId, pWebSite, pCustNo, pUserId, pPassword, pAccount, pNotes);

            /* Check that this Account has not been previously added */
            if (searchFor(myAccount.getName()) != null)
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate Account");

            /* Add the Account to the list */
            add(myAccount);
        }

        /**
         * Add an Account
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
        public void addItem(int uId,
                            int uControlId,
                            byte[] pName,
                            int uAcTypeId,
                            byte[] pDesc,
                            Date pMaturity,
                            Date pClosed,
                            Integer pParentId,
                            Integer pAliasId,
                            byte[] pWebSite,
                            byte[] pCustNo,
                            byte[] pUserId,
                            byte[] pPassword,
                            byte[] pAccount,
                            byte[] pNotes) throws JDataException {
            Account myAccount;

            /* Create the new account */
            myAccount = new Account(this, uId, uControlId, pName, uAcTypeId, pDesc, pMaturity, pClosed,
                    pParentId, pAliasId, pWebSite, pCustNo, pUserId, pPassword, pAccount, pNotes);

            /* Check that this AccountId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate AccountId");

            /* Check that this Account has not been previously added */
            if (searchFor(myAccount.getName()) != null)
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate Account");

            /* Add the Account to the list */
            add(myAccount);
        }

        /**
         * Validate newly loaded accounts. This is deliberately deferred until after loading of the
         * Rates/Patterns/Prices so as to validate the interrelationships
         * @throws JDataException
         */
        public void validateLoadedAccounts() throws JDataException {
            DataListIterator<Account> myIterator;
            Account myCurr;
            AccountType myType;
            FinanceData myData = getData();

            /* Mark active items referenced by rates */
            myData.getRates().markActiveItems();

            /* Mark active items referenced by prices */
            myData.getPrices().markActiveItems();

            /* Mark active items referenced by patterns */
            myData.getPatterns().markActiveItems();

            /* Access the iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                /* If the account has a parent Id */
                if (myCurr.getParentId() != null) {
                    /* Set the parent */
                    myCurr.setParent(searchFor(myCurr.getParentId()));
                    myCurr.getParent().touchItem(myCurr);
                }

                /* If the account has an alias Id */
                if (myCurr.getAliasId() != null) {
                    /* Set the alias */
                    myCurr.setAlias(searchFor(myCurr.getAliasId()));
                    myCurr.getAlias().touchItem(myCurr);
                }

                /* Mark the AccountType */
                myType = myCurr.getActType();
                myType.touchItem(myCurr);
            }

            /* Create another iterator */
            myIterator = listIterator(true);

            /* Loop through the items to find the entry */
            while ((myCurr = myIterator.next()) != null) {
                /* Validate the account */
                myCurr.validate();

                /* Handle validation failure */
                if (myCurr.hasErrors())
                    throw new JDataException(ExceptionClass.VALIDATE, myCurr, "Failed validation");
            }
        }
    }
}
