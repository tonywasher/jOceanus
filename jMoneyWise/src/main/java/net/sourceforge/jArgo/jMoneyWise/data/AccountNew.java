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
package net.sourceforge.jArgo.jMoneyWise.data;

import java.util.Date;
import java.util.Iterator;

import net.sourceforge.jArgo.jDataManager.DataState;
import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jArgo.jDataManager.JDataFields;
import net.sourceforge.jArgo.jDataManager.JDataFields.JDataField;
import net.sourceforge.jArgo.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jArgo.jDataModels.data.DataItem;
import net.sourceforge.jArgo.jDataModels.data.DataList;
import net.sourceforge.jArgo.jDataModels.data.DataList.ListStyle;
import net.sourceforge.jArgo.jDataModels.data.DataSet;
import net.sourceforge.jArgo.jDateDay.JDateDay;
import net.sourceforge.jArgo.jMoneyWise.data.FinanceData.LoadState;
import net.sourceforge.jArgo.jMoneyWise.data.statics.AccountInfoClass;
import net.sourceforge.jArgo.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jArgo.jMoneyWise.data.statics.AccountType.AccountTypeList;

/**
 * New version of Account DataItem utilising AccountInfo.
 * @author Tony Washer
 */
public class AccountNew extends AccountBase {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = AccountNew.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AccountBase.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * AccountInfoSet field Id.
     */
    public static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField("InfoSet");

    /**
     * Maturity Field Id.
     */
    public static final JDataField FIELD_MATURITY = FIELD_DEFS.declareEqualityField("Maturity");

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityField("Parent");

    /**
     * Alias Field Id.
     */
    public static final JDataField FIELD_ALIAS = FIELD_DEFS.declareEqualityField("Alias");

    /**
     * WebSite Field Id.
     */
    public static final JDataField FIELD_WEBSITE = FIELD_DEFS.declareEqualityField("WebSite");

    /**
     * CustNo Field Id.
     */
    public static final JDataField FIELD_CUSTNO = FIELD_DEFS.declareEqualityField("CustomerNo");

    /**
     * UserId Field Id.
     */
    public static final JDataField FIELD_USERID = FIELD_DEFS.declareEqualityField("UserId");

    /**
     * Password Field Id.
     */
    public static final JDataField FIELD_PASSWORD = FIELD_DEFS.declareEqualityField("Password");

    /**
     * Account Details Field Id.
     */
    public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareEqualityField("Account");

    /**
     * Notes Field Id.
     */
    public static final JDataField FIELD_NOTES = FIELD_DEFS.declareEqualityField("Notes");

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
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet ? theInfoSet : JDataFieldValue.SkipField;
        }
        if (FIELD_MATURITY.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.Maturity) : JDataFieldValue.SkipField;
        }
        if (FIELD_PARENT.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.Parent) : JDataFieldValue.SkipField;
        }
        if (FIELD_ALIAS.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.Alias) : JDataFieldValue.SkipField;
        }
        if (FIELD_WEBSITE.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.WebSite) : JDataFieldValue.SkipField;
        }
        if (FIELD_CUSTNO.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.CustNo) : JDataFieldValue.SkipField;
        }
        if (FIELD_USERID.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.UserId) : JDataFieldValue.SkipField;
        }
        if (FIELD_PASSWORD.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.Password) : JDataFieldValue.SkipField;
        }
        if (FIELD_ACCOUNT.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.Account) : JDataFieldValue.SkipField;
        }
        if (FIELD_NOTES.equals(pField)) {
            return hasInfoSet ? theInfoSet.getField(AccountInfoClass.Notes) : JDataFieldValue.SkipField;
        }
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
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * AccountInfoSet.
     */
    private final AccountInfoSet theInfoSet;

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
     * Obtain Maturity.
     * @return the maturity date
     */
    public JDateDay getMaturity() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.Maturity, JDateDay.class) : null;
    }

    /**
     * Obtain Parent.
     * @return the parent
     */
    public AccountNew getParent() {
        return hasInfoSet ? theInfoSet.getAccount(AccountInfoClass.Parent) : null;
    }

    /**
     * Obtain Parent Id.
     * @return the parent id
     */
    public Integer getParentId() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.Parent, Integer.class) : null;
    }

    /**
     * Obtain Alias.
     * @return the alias
     */
    public AccountNew getAlias() {
        return hasInfoSet ? theInfoSet.getAccount(AccountInfoClass.Alias) : null;
    }

    /**
     * Obtain Alias Id.
     * @return the alias id
     */
    public Integer getAliasId() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.Alias, Integer.class) : null;
    }

    /**
     * Obtain WebSite.
     * @return the webSite
     */
    public char[] getWebSite() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.WebSite, char[].class) : null;
    }

    /**
     * Obtain CustNo.
     * @return the customer #
     */
    public char[] getCustNo() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.CustNo, char[].class) : null;
    }

    /**
     * Obtain UserId.
     * @return the userId
     */
    public char[] getUserId() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.UserId, char[].class) : null;
    }

    /**
     * Obtain Password.
     * @return the password
     */
    public char[] getPassword() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.Password, char[].class) : null;
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public char[] getAccount() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.Account, char[].class) : null;
    }

    /**
     * Obtain Notes.
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet ? theInfoSet.getValue(AccountInfoClass.Notes, char[].class) : null;
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

    @Override
    public AccountNew getBase() {
        return (AccountNew) super.getBase();
    }

    @Override
    public boolean isLocked() {
        return isClosed();
    }

    /**
     * Copy flags.
     * @param pItem the original item
     */
    private void copyFlags(final AccountNew pItem) {
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
    public AccountNew(final AccountNewList pList,
                      final AccountNew pAccount) {
        /* Set standard values */
        super(pList, pAccount);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new AccountInfoSet(pAccount.theInfoSet);
                hasInfoSet = true;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                break;
        }
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
     * @param pClose the Close date for the account
     * @throws JDataException on error
     */
    private AccountNew(final AccountNewList pList,
                       final Integer uId,
                       final Integer uControlId,
                       final byte[] pName,
                       final Integer uAcTypeId,
                       final byte[] pDesc,
                       final Date pClose) throws JDataException {
        /* Initialise the item */
        super(pList, uId, uControlId, pName, uAcTypeId, pDesc, pClose);

        /* Create the InfoSet */
        FinanceData myData = getDataSet();
        theInfoSet = new AccountInfoSet(this, myData.getAccountInfo(), myData.getActInfoTypes());
        hasInfoSet = true;
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param uId the id
     * @param sName the Name of the account
     * @param uAcTypeId the Account type id
     * @param pDesc the description
     * @param pClose the Close date for the account
     * @throws JDataException on error
     */
    private AccountNew(final AccountNewList pList,
                       final Integer uId,
                       final String sName,
                       final Integer uAcTypeId,
                       final String pDesc,
                       final Date pClose) throws JDataException {
        /* Initialise the item */
        super(pList, uId, sName, uAcTypeId, pDesc, pClose);

        /* Create the InfoSet */
        FinanceData myData = getDataSet();
        theInfoSet = new AccountInfoSet(this, myData.getAccountInfo(), myData.getActInfoTypes());
        hasInfoSet = true;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public AccountNew(final AccountNewList pList) {
        super(pList);

        /* Build InfoSet */
        FinanceData myData = getDataSet();
        theInfoSet = new AccountInfoSet(this, myData.getAccountInfo().getEmptyList(),
                myData.getActInfoTypes());
        hasInfoSet = true;
    }

    @Override
    protected void relinkToDataSet() {
        /* Invoke underlying re-link */
        super.relinkToDataSet();

        /* If we have an InfoSet */
        if (hasInfoSet) {
            /* Update to use the new lists */
            FinanceData myData = getDataSet();
            theInfoSet.relinkToDataSet(myData.getAccountInfo(), myData.getActInfoTypes());
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
     * @throws JDataException on error
     */
    public void adjustClosed() throws JDataException {
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
     * Set a new maturity date.
     * @param pDate the new date
     * @throws JDataException on error
     */
    public void setMaturity(final JDateDay pDate) throws JDataException {
        setInfoSetValue(AccountInfoClass.Maturity, pDate);
    }

    /**
     * Set a new parent.
     * @param pParent the new parent
     * @throws JDataException on error
     */
    public void setParent(final AccountNew pParent) throws JDataException {
        setInfoSetValue(AccountInfoClass.Parent, pParent);
    }

    /**
     * Set a new alias.
     * @param pAlias the new alias
     * @throws JDataException on error
     */
    public void setAlias(final AccountNew pAlias) throws JDataException {
        setInfoSetValue(AccountInfoClass.Alias, pAlias);
    }

    /**
     * Set a new WebSite.
     * @param pWebSite the new webSite
     * @throws JDataException on error
     */
    public void setWebSite(final char[] pWebSite) throws JDataException {
        setInfoSetValue(AccountInfoClass.WebSite, pWebSite);
    }

    /**
     * Set a new CustNo.
     * @param pCustNo the new custNo
     * @throws JDataException on error
     */
    public void setCustNo(final char[] pCustNo) throws JDataException {
        setInfoSetValue(AccountInfoClass.CustNo, pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new userId
     * @throws JDataException on error
     */
    public void setUserId(final char[] pUserId) throws JDataException {
        setInfoSetValue(AccountInfoClass.UserId, pUserId);
    }

    /**
     * Set a new Password.
     * @param pPassword the new password
     * @throws JDataException on error
     */
    public void setPassword(final char[] pPassword) throws JDataException {
        setInfoSetValue(AccountInfoClass.Password, pPassword);
    }

    /**
     * Set a new Account.
     * @param pAccount the new account
     * @throws JDataException on error
     */
    public void setAccount(final char[] pAccount) throws JDataException {
        setInfoSetValue(AccountInfoClass.Account, pAccount);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws JDataException on error
     */
    public void setNotes(final char[] pNotes) throws JDataException {
        setInfoSetValue(AccountInfoClass.Notes, pNotes);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JDataException on error
     */
    private void setInfoSetValue(final AccountInfoClass pInfoClass,
                                 final Object pValue) throws JDataException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JDataException(ExceptionClass.LOGIC, "Invalid call to set InfoSet value");
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    /**
     * The Account List class.
     */
    public static class AccountNewList extends AccountBaseList<AccountNew> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountNewList.class.getSimpleName(),
                AccountBaseList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * Account field id.
         */
        public static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField("Account");

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
        private AccountNew theAccount = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public AccountNew getAccount() {
            return theAccount;
        }

        /**
         * Construct an empty CORE TaxYear list.
         * @param pData the DataSet for the list
         */
        public AccountNewList(final FinanceData pData) {
            super(pData, AccountNew.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountNewList(final AccountNewList pSource) {
            super(pSource);
        }

        @Override
        protected AccountNewList getEmptyList() {
            return new AccountNewList(this);
        }

        @Override
        public AccountNewList cloneList(final DataSet<?> pDataSet) {
            return (AccountNewList) super.cloneList(pDataSet);
        }

        @Override
        public AccountNewList deriveList(final ListStyle pStyle) {
            return (AccountNewList) super.deriveList(pStyle);
        }

        @Override
        public AccountNewList deriveDifferences(final DataList<AccountNew> pOld) {
            return (AccountNewList) super.deriveDifferences(pOld);
        }

        /**
         * Construct an edit extract for an Account.
         * @param pAccount the relevant account
         * @return the edit Extract
         */
        public AccountNewList deriveEditList(final AccountNew pAccount) {
            /* Build an empty Extract List */
            AccountNewList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Create a new account based on the passed account */
            myList.theAccount = new AccountNew(myList, pAccount);
            myList.add(myList.theAccount);

            /* Return the List */
            return myList;
        }

        /**
         * Construct an edit extract for an Account.
         * @param pType the account type
         * @return the edit Extract
         */
        public AccountNewList deriveEditList(final AccountType pType) {
            /* Build an empty Extract List */
            AccountNewList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);

            /* Create a new account */
            myList.theAccount = new AccountNew(myList);
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
        public AccountNew addCopyItem(final DataItem pAccount) {
            /* Can only clone an Account */
            if (!(pAccount instanceof AccountNew)) {
                return null;
            }

            AccountNew myAccount = new AccountNew(this, (AccountNew) pAccount);
            add(myAccount);
            return myAccount;
        }

        /**
         * Create a new empty element in the edit list (null-operation).
         * @return the newly added item
         */
        @Override
        public AccountNew addNewItem() {
            return null;
        }

        /**
         * Update account details after data update.
         * @throws JDataException on error
         */
        public void markActiveItems() throws JDataException {
            /* Access the iterator */
            Iterator<AccountNew> myIterator = iterator();
            AccountNew myCurr;

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
         * Add an Account.
         * @param uId the is
         * @param pName the Name of the account
         * @param pAcType the Name of the account type
         * @param pDesc the description of the account
         * @param pClosed the Close Date for the account (or null)
         * @return the new account
         * @throws JDataException on error
         */
        public AccountNew addOpenItem(final Integer uId,
                                      final String pName,
                                      final String pAcType,
                                      final String pDesc,
                                      final Date pClosed) throws JDataException {
            /* Access the account types and accounts */
            FinanceData myData = getDataSet();
            AccountTypeList myActTypes = myData.getAccountTypes();

            /* Look up the Account Type */
            AccountType myActType = myActTypes.findItemByName(pAcType);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, "Account [" + pName
                        + "] has invalid Account Type [" + pAcType + "]");
            }
            /* Create the new account */
            AccountNew myAccount = new AccountNew(this, uId, pName, myActType.getId(), pDesc, pClosed);

            /* Check that this Account has not been previously added */
            if (findItemByName(myAccount.getName()) != null) {
                throw new JDataException(ExceptionClass.DATA, myAccount, "Duplicate Account");
            }

            /* Add the Account to the list */
            append(myAccount);
            return myAccount;
        }

        /**
         * Add an Account.
         * @param uId the Id of the account
         * @param uControlId the control id
         * @param pName the Encrypted Name of the account
         * @param uAcTypeId the Id of the account type
         * @param pDesc the Encrypted Description of the account (or null)
         * @param pClosed the Close Date for the account (or null)
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final byte[] pName,
                                  final Integer uAcTypeId,
                                  final byte[] pDesc,
                                  final Date pClosed) throws JDataException {
            /* Create the new account */
            AccountNew myAccount = new AccountNew(this, uId, uControlId, pName, uAcTypeId, pDesc, pClosed);

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
            AccountNew myCurr;

            /* Mark active items referenced by rates */
            myData.getRates().markActiveItems();

            /* Mark active items referenced by prices */
            myData.getPrices().markActiveItems();

            /* Mark active items referenced by patterns */
            myData.getPatterns().markActiveItems();

            /* Access the iterator */
            Iterator<AccountNew> myIterator = iterator();

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
