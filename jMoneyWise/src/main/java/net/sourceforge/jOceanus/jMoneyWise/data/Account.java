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

import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.DataState;
import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataList.ListStyle;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData.LoadState;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType.AccountTypeList;

/**
 * Account DataItem utilising AccountInfo.
 * @author Tony Washer
 */
public class Account
        extends AccountBase {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = Account.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

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
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, AccountBase.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * CloseDate Field Id.
     */
    public static final JDataField FIELD_CLOSEDATE = FIELD_DEFS.declareLocalField("CloseDate");

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
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet ? theInfoSet : JDataFieldValue.SkipField;
        }

        /* Handle InfoSet fields */
        AccountInfoClass myClass = getFieldClass(pField);
        if (myClass != null) {
            return getInfoSetValue(myClass);
        }

        /* Handle flags */
        if (FIELD_EVTFIRST.equals(pField)) {
            return (theEarliest != null) ? theEarliest : JDataFieldValue.SkipField;
        }
        if (FIELD_EVTLAST.equals(pField)) {
            return (theLatest != null) ? theLatest : JDataFieldValue.SkipField;
        }
        if (FIELD_INITPRC.equals(pField)) {
            return (theInitPrice != null) ? theInitPrice : JDataFieldValue.SkipField;
        }
        if (FIELD_CLOSEDATE.equals(pField)) {
            return (theCloseDate != null) ? theCloseDate : JDataFieldValue.SkipField;
        }
        if (FIELD_HASDEBTS.equals(pField)) {
            return hasDebts ? hasDebts : JDataFieldValue.SkipField;
        }
        if (FIELD_HASRATES.equals(pField)) {
            return hasRates ? hasRates : JDataFieldValue.SkipField;
        }
        if (FIELD_HASPRICE.equals(pField)) {
            return hasPrices ? hasPrices : JDataFieldValue.SkipField;
        }
        if (FIELD_HASPATT.equals(pField)) {
            return hasPatterns ? hasPatterns : JDataFieldValue.SkipField;
        }
        if (FIELD_ISPATT.equals(pField)) {
            return isPatterned ? isPatterned : JDataFieldValue.SkipField;
        }
        if (FIELD_ISPARENT.equals(pField)) {
            return isParent ? isParent : JDataFieldValue.SkipField;
        }
        if (FIELD_ISALIASD.equals(pField)) {
            return isAliasedTo ? isAliasedTo : JDataFieldValue.SkipField;
        }
        if (FIELD_ISCLSABL.equals(pField)) {
            return isCloseable;
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    private static AccountInfoClass getFieldClass(final JDataField pField) {
        if (FIELD_MATURITY.equals(pField)) {
            return AccountInfoClass.Maturity;
        }
        if (FIELD_PARENT.equals(pField)) {
            return AccountInfoClass.Parent;
        }
        if (FIELD_ALIAS.equals(pField)) {
            return AccountInfoClass.Alias;
        }
        if (FIELD_WEBSITE.equals(pField)) {
            return AccountInfoClass.WebSite;
        }
        if (FIELD_CUSTNO.equals(pField)) {
            return AccountInfoClass.CustNo;
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
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * Should we use infoSet for DataState etc.
     */
    private final boolean useInfoSet;

    /**
     * AccountInfoSet.
     */
    private final AccountInfoSet theInfoSet;

    /**
     * Obtain InfoSet.
     * @return the infoSet
     */
    protected AccountInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Close Date.
     */
    private JDateDay theCloseDate = null;

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
    public Account getParent() {
        return hasInfoSet ? theInfoSet.getAccount(AccountInfoClass.Parent) : null;
    }

    /**
     * Obtain Alias.
     * @return the alias
     */
    public Account getAlias() {
        return hasInfoSet ? theInfoSet.getAccount(AccountInfoClass.Alias) : null;
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
     * Get the close Date of the account.
     * @return the closeDate
     */
    public JDateDay getCloseDate() {
        return theCloseDate;
    }

    /**
     * Is the account an alias?
     * @return true/false
     */
    public boolean isAlias() {
        return (getAlias() != null);
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
        return ((theLatest == null)
                && (!isDeleted())
                && (!isParent)
                && (!hasRates)
                && ((!hasPrices) || (getState() == DataState.NEW))
                && (!hasPatterns)
                && (!isAliasedTo)
                && (!isPatterned) && (!getActType().isReserved()));
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN)
            && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public EditState getEditState() {
        /* Pop history for self */
        EditState myState = super.getEditState();

        /* If we should use the InfoSet */
        if ((myState == EditState.CLEAN)
            && (useInfoSet)) {
            /* Get state for infoSet */
            myState = theInfoSet.getEditState();
        }

        /* Return the state */
        return myState;
    }

    @Override
    public boolean hasHistory() {
        /* Check for history for self */
        boolean hasHistory = super.hasHistory();

        /* If we should use the InfoSet */
        if ((!hasHistory)
            && (useInfoSet)) {
            /* Check history for infoSet */
            hasHistory = theInfoSet.hasHistory();
        }

        /* Return details */
        return hasHistory;
    }

    @Override
    public void pushHistory() {
        /* Push history for self */
        super.pushHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Push history for infoSet */
            theInfoSet.pushHistory();
        }
    }

    @Override
    public void popHistory() {
        /* Pop history for self */
        super.popHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Pop history for infoSet */
            theInfoSet.popHistory();
        }
    }

    @Override
    public boolean checkForHistory() {
        /* Check for history for self */
        boolean bChanges = super.checkForHistory();

        /* If we should use the InfoSet */
        if (useInfoSet) {
            /* Check for history for infoSet */
            bChanges |= theInfoSet.checkForHistory();
        }

        /* return result */
        return bChanges;
    }

    @Override
    public Difference fieldChanged(final JDataField pField) {
        /* Handle InfoSet fields */
        AccountInfoClass myClass = getFieldClass(pField);
        if (myClass != null) {
            return (useInfoSet) ? theInfoSet.fieldChanged(myClass) : Difference.Identical;
        }

        /* Check super fields */
        return super.fieldChanged(pField);
    }

    @Override
    public void setDeleted(final boolean bDeleted) {
        /* Pass call to infoSet if required */
        if (useInfoSet) {
            theInfoSet.setDeleted(bDeleted);
        }

        /* Pass call onwards */
        super.setDeleted(bDeleted);
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
        theCloseDate = pItem.theCloseDate;
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

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new AccountInfoSet(this, pList.getActInfoTypes(), pList.getAccountInfo());
                theInfoSet.cloneDataInfoSet(pAccount.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new AccountInfoSet(this, pList.getActInfoTypes(), pList.getAccountInfo());
                hasInfoSet = true;
                useInfoSet = false;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                useInfoSet = false;
                break;
        }

        /* If this is a build of edit from Core */
        if ((getStyle() == ListStyle.EDIT)
            && (pAccount.getStyle() == ListStyle.CORE)) {
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
     * @param isClosed is the account closed?
     * @throws JDataException on error
     */
    private Account(final AccountList pList,
                    final Integer uId,
                    final Integer uControlId,
                    final byte[] pName,
                    final Integer uAcTypeId,
                    final byte[] pDesc,
                    final Boolean isClosed) throws JDataException {
        /* Initialise the item */
        super(pList, uId, uControlId, pName, uAcTypeId, pDesc, isClosed);

        /* Create the InfoSet */
        theInfoSet = new AccountInfoSet(this, pList.getActInfoTypes(), pList.getAccountInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param uId the id
     * @param sName the Name of the account
     * @param uAcTypeId the Account type id
     * @param pDesc the description
     * @param isClosed is the account closed?
     * @throws JDataException on error
     */
    private Account(final AccountList pList,
                    final Integer uId,
                    final String sName,
                    final Integer uAcTypeId,
                    final String pDesc,
                    final Boolean isClosed) throws JDataException {
        /* Initialise the item */
        super(pList, uId, sName, uAcTypeId, pDesc, isClosed);

        /* Create the InfoSet */
        theInfoSet = new AccountInfoSet(this, pList.getActInfoTypes(), pList.getAccountInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Edit Constructor.
     * @param pList the list
     */
    public Account(final AccountList pList) {
        super(pList);

        /* Build InfoSet */
        theInfoSet = new AccountInfoSet(this, pList.getActInfoTypes(), pList.getAccountInfo());
        hasInfoSet = true;
        useInfoSet = true;
        setClosed(Boolean.FALSE);
    }

    /**
     * Set non-closeable.
     */
    public void setNonCloseable() {
        /* Record the status */
        isCloseable = false;
    }

    /**
     * Adjust closed/maturity dates.
     * @throws JDataException on error
     */
    public void adjustDates() throws JDataException {
        /* Access latest activity date */
        JDateDay myCloseDate = (theLatest == null) ? null : theLatest.getDate();

        /* Store the close date */
        theCloseDate = myCloseDate;

        /* If the maturity is null for a bond set it to close date */
        if (isBond()
            && getMaturity() == null) {
            /* Record a date for maturity */
            setMaturity(theCloseDate);
        }
    }

    /**
     * Close the account.
     */
    public void closeAccount() {
        /* Close the account */
        setClosed(Boolean.TRUE);
    }

    /**
     * Re-open the account.
     */
    public void reOpenAccount() {
        /* Reopen the account */
        setClosed(Boolean.FALSE);
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
    public void setParent(final Account pParent) throws JDataException {
        setInfoSetValue(AccountInfoClass.Parent, pParent);
    }

    /**
     * Set a new alias.
     * @param pAlias the new alias
     * @throws JDataException on error
     */
    public void setAlias(final Account pAlias) throws JDataException {
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
                myValue = hasInfoSet ? theInfoSet.getAccount(pInfoClass) : null;
                break;
            default:
                /* Access value of object */
                myValue = hasInfoSet ? theInfoSet.getField(pInfoClass) : null;
                break;

        }
        /* Return the value */
        return (myValue != null) ? myValue : JDataFieldValue.SkipField;
    }

    @Override
    protected void markActiveItems() {
        /* mark underlying items */
        super.markActiveItems();

        /* Access values */
        Account myParent = getParent();
        Account myAlias = getAlias();
        Boolean isClosed = isClosed();

        /* If we have a parent, mark the parent */
        if (myParent != null) {
            myParent.touchItem(this);
            if (!isClosed) {
                myParent.setNonCloseable();
            }
        }

        /* If we have an alias, mark the alias */
        if (myAlias != null) {
            myAlias.touchItem(this);
            if (!isClosed) {
                myAlias.setNonCloseable();
            }
        }

        /* If we have patterns or are touched by patterns, then we are not close-able */
        if (hasPatterns
            || isPatterned) {
            setNonCloseable();
        }

        /* Mark infoSet items */
        theInfoSet.markActiveItems();
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        AccountType myType = getActType();
        Account myParent = getParent();
        Account myAlias = getAlias();
        LoadState myState = getDataSet().getLoadState();

        /* Validate base components */
        super.validate();

        /* If the account is priced */
        if (myType.isPriced()) {
            /* If this account has an alias */
            if (myAlias != null) {
                /* Must not have prices */
                if (hasPrices) {
                    addError("Aliased account has prices", FIELD_TYPE);
                }

                /* Alias account must have prices */
                if ((!myAlias.hasPrices)
                    && (myAlias.theEarliest != null)) {
                    addError("Alias account has no prices", FIELD_TYPE);
                }

                /* else this is a standard account */
            } else {
                /* Must have prices */
                if ((!hasPrices)
                    && (theEarliest != null)) {
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
            if ((myState != LoadState.INITIAL)
                && (myParent == null)) {
                addError("Child Account must have parent", FIELD_PARENT);
            }

            /* if we have a parent */
            if (myParent != null) {
                /* check that any parent is owner */
                if (!myParent.isOwner()) {
                    addError("Parent account must be owner", FIELD_PARENT);
                }

                /* If we are open then parent must be open */
                if (!isClosed()
                    && myParent.isClosed()) {
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
        if ((hasRates)
            && (!myType.isMoney())) {
            addError("non-Money account has rates", FIELD_TYPE);
        }

        /* If the account has a maturity rate then it must be a bond */
        if ((getMaturity() != null)
            && (!myType.isBond())) {
            addError("non-Bond has maturity date", FIELD_MATURITY);
        }

        /* Open Bond accounts must have maturity */
        if ((myType.isBond())
            && !isClosed()
            && (getMaturity() == null)) {
            addError("Bond must have maturity date", FIELD_MATURITY);
        }

        /* If data has been fully loaded and the account is closed it must be closeable */
        if ((myState != LoadState.INITIAL)
            && (isClosed())
            && (!isCloseable())) {
            addError("Non-closeable account is closed", FIELD_CLOSED);
        }

        /* The WebSite must not be too long */
        if ((getWebSite() != null)
            && (getWebSite().length > WSITELEN)) {
            addError("WebSite is too long", FIELD_WEBSITE);
        }

        /* The CustNo must not be too long */
        if ((getCustNo() != null)
            && (getCustNo().length > CUSTLEN)) {
            addError("Customer No. is too long", FIELD_CUSTNO);
        }

        /* The UserId must not be too long */
        if ((getUserId() != null)
            && (getUserId().length > UIDLEN)) {
            addError("UserId is too long", FIELD_USERID);
        }

        /* The Password must not be too long */
        if ((getPassword() != null)
            && (getPassword().length > PWDLEN)) {
            addError("Password is too long", FIELD_PASSWORD);
        }

        /* The Account must not be too long */
        if ((getAccount() != null)
            && (getAccount().length > ACTLEN)) {
            addError("Account is too long", FIELD_ACCOUNT);
        }

        /* The Notes must not be too long */
        if ((getNotes() != null)
            && (getNotes().length > NOTELEN)) {
            addError("WebSite is too long", FIELD_NOTES);
        }

        /* Set validation flag */
        boolean isValid = !hasErrors();
        if (isValid) {
            setValidEdit();
        }
    }

    /**
     * The Account List class.
     */
    public static class AccountList
            extends AccountBaseList<Account> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(AccountList.class.getSimpleName(), AccountBaseList.FIELD_DEFS);

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
         * The AccountInfo List.
         */
        private AccountInfoList theInfoList = null;

        /**
         * The AccountInfoType list.
         */
        private AccountInfoTypeList theInfoTypeList = null;

        /**
         * The account.
         */
        private Account theAccount = null;

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Obtain the account.
         * @return the account
         */
        public Account getAccount() {
            return theAccount;
        }

        /**
         * Obtain the accountInfoList.
         * @return the account info list
         */
        public AccountInfoList getAccountInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getAccountInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the accountInfoTypeList.
         * @return the account info type list
         */
        public AccountInfoTypeList getActInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getActInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Construct an empty CORE Account list.
         * @param pData the DataSet for the list
         */
        public AccountList(final FinanceData pData) {
            super(pData, Account.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private AccountList(final AccountList pSource) {
            super(pSource);
        }

        @Override
        protected AccountList getEmptyList(final ListStyle pStyle) {
            AccountList myList = new AccountList(this);
            myList.setStyle(pStyle);
            return myList;
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
            AccountList myList = getEmptyList(ListStyle.EDIT);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            AccountInfoList myActInfo = getAccountInfo();
            myList.theInfoList = myActInfo.getEmptyList(ListStyle.EDIT);
            populateList(myList);

            /* Find the interesting account */
            myList.theAccount = myList.findItemById(pAccount.getId());

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
            AccountList myList = getEmptyList(ListStyle.EDIT);

            /* Store InfoType list */
            myList.theInfoTypeList = getActInfoTypes();

            /* Create info List */
            AccountInfoList myActInfo = getAccountInfo();
            myActInfo = myActInfo.getEmptyList(ListStyle.EDIT);
            myList.theInfoList = myActInfo;
            populateList(myList);

            /* Create a new account */
            Account myNew = myList.theAccount = new Account(myList);
            myNew.setActType(pType);
            myNew.setNewVersion();

            /* Set lists to show new version */
            // Integer myVersion = myNew.getValueSetVersion();
            // myList.setVersion(myVersion);
            // myActInfo.setVersion(myVersion);

            /* Add to the list and store as master account */
            myList.add(myNew);
            myList.theAccount = myNew;
            myNew.validate();

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
            /* Access dataSet */
            FinanceData myData = getDataSet();

            /* Mark active items referenced by rates */
            myData.getRates().markActiveItems();

            /* Mark active items referenced by prices */
            myData.getPrices().markActiveItems();

            /* Mark active items referenced by patterns */
            myData.getPatterns().markActiveItems();

            /* Access the iterator */
            Iterator<Account> myIterator = iterator();

            /* Loop through the accounts */
            while (myIterator.hasNext()) {
                Account myCurr = myIterator.next();

                /* mark active items */
                myCurr.markActiveItems();

                /* If we are closed adjust dates */
                if (myCurr.isClosed()) {
                    /* Ensure that we have correct closed/maturity dates */
                    myCurr.adjustDates();
                }
            }

            /* If we are in final loading stage */
            if (myData.getLoadState() == LoadState.FINAL) {
                /* Access a new iterator */
                myIterator = listIterator();

                /* Loop through the accounts */
                while (myIterator.hasNext()) {
                    Account myCurr = myIterator.next();

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
         * @param isClosed is the account closed?
         * @return the new account
         * @throws JDataException on error
         */
        public Account addOpenItem(final Integer uId,
                                   final String pName,
                                   final String pAcType,
                                   final String pDesc,
                                   final Boolean isClosed) throws JDataException {
            /* Access the account types and accounts */
            FinanceData myData = getDataSet();
            AccountTypeList myActTypes = myData.getAccountTypes();

            /* Look up the Account Type */
            AccountType myActType = myActTypes.findItemByName(pAcType);
            if (myActType == null) {
                throw new JDataException(ExceptionClass.DATA, "Account ["
                                                              + pName
                                                              + "] has invalid Account Type ["
                                                              + pAcType
                                                              + "]");
            }
            /* Create the new account */
            Account myAccount = new Account(this, uId, pName, myActType.getId(), pDesc, isClosed);

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
         * @param isClosed is the account closed?
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final byte[] pName,
                                  final Integer uAcTypeId,
                                  final byte[] pDesc,
                                  final Boolean isClosed) throws JDataException {
            /* Create the new account */
            Account myAccount = new Account(this, uId, uControlId, pName, uAcTypeId, pDesc, isClosed);

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
    }
}
