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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.data.AccountInfo.AccountInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet.InfoSetItem;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList.ListStyle;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * Account DataItem utilising AccountInfo.
 * @author Tony Washer
 */
public class Account
        extends AccountBase
        implements InfoSetItem {
    /**
     * Object name.
     */
    public static final String OBJECT_NAME = Account.class.getSimpleName();

    /**
     * List name.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Account.class.getName());

    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), AccountBase.FIELD_DEFS);

    /**
     * AccountInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataInfoSet"));

    /**
     * AccountStatus field Id.
     */
    private static final JDataField FIELD_STATUS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataStatus"));

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = NLS_BUNDLE.getString("ErrorBadInfoSet");

    /**
     * Bad Rates Error Text.
     */
    private static final String ERROR_BADRATES = NLS_BUNDLE.getString("ErrorBadRates");

    /**
     * Bad Close Error Text.
     */
    private static final String ERROR_BADCLOSE = NLS_BUNDLE.getString("ErrorBadClose");

    /**
     * Bad Prices Error Text.
     */
    private static final String ERROR_BADPRICES = NLS_BUNDLE.getString("ErrorBadPrices");

    /**
     * No Prices Error Text.
     */
    private static final String ERROR_NOPRICES = NLS_BUNDLE.getString("ErrorNoPrices");

    /**
     * TaxFree Error Text.
     */
    private static final String ERROR_TAXFREE = NLS_BUNDLE.getString("ErrorTaxFree");

    /**
     * GrossInterest Error Text.
     */
    private static final String ERROR_GROSS = NLS_BUNDLE.getString("ErrorGross");

    /**
     * taxFree And GrossInterest Error Text.
     */
    private static final String ERROR_TAXFREEGROSS = NLS_BUNDLE.getString("ErrorTaxFreeGross");

    /**
     * autoExpense and OpeningBalance Error Text.
     */
    private static final String ERROR_AUTOBALANCE = NLS_BUNDLE.getString("ErrorAutoBalance");

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                             ? theInfoSet
                             : JDataFieldValue.SKIP;
        }

        /* Handle status */
        if (FIELD_STATUS.equals(pField)) {
            return theStatus;
        }

        /* Handle infoSet fields */
        AccountInfoClass myClass = AccountInfoSet.getClassForField(pField);
        if ((theInfoSet != null) && (myClass != null)) {
            return theInfoSet.getFieldValue(pField);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
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
     * AccountStatus.
     */
    private AccountStatus theStatus = new AccountStatus();

    @Override
    public AccountInfoSet getInfoSet() {
        return theInfoSet;
    }

    @Override
    public boolean isEditable() {
        return super.isEditable() && !isClosed();
    }

    /**
     * Obtain Maturity.
     * @return the maturity date
     */
    public JDateDay getMaturity() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.MATURITY, JDateDay.class)
                         : null;
    }

    /**
     * Obtain Parent.
     * @return the parent
     */
    public Account getParent() {
        return hasInfoSet
                         ? theInfoSet.getAccount(AccountInfoClass.PARENT)
                         : null;
    }

    /**
     * Obtain Alias.
     * @return the alias
     */
    public Account getAlias() {
        return hasInfoSet
                         ? theInfoSet.getAccount(AccountInfoClass.ALIAS)
                         : null;
    }

    /**
     * Obtain Portfolio.
     * @return the portfolio
     */
    public Account getPortfolio() {
        return hasInfoSet
                         ? theInfoSet.getAccount(AccountInfoClass.PORTFOLIO)
                         : null;
    }

    /**
     * Obtain Holding.
     * @return the holding
     */
    public Account getHolding() {
        return hasInfoSet
                         ? theInfoSet.getAccount(AccountInfoClass.HOLDING)
                         : null;
    }

    /**
     * Obtain AutoExpense.
     * @return the autoExpense category
     */
    public EventCategory getAutoExpense() {
        return hasInfoSet
                         ? theInfoSet.getEventCategory(AccountInfoClass.AUTOEXPENSE)
                         : null;
    }

    /**
     * Obtain Symbol.
     * @return the symbol
     */
    public String getSymbol() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.SYMBOL, String.class)
                         : null;
    }

    /**
     * Obtain Comments.
     * @return the comments
     */
    public String getComments() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.COMMENTS, String.class)
                         : null;
    }

    /**
     * Obtain Opening Balance.
     * @return the Opening balance
     */
    public JMoney getOpeningBalance() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.OPENINGBALANCE, JMoney.class)
                         : null;
    }

    /**
     * Obtain WebSite.
     * @return the webSite
     */
    public char[] getWebSite() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.WEBSITE, char[].class)
                         : null;
    }

    /**
     * Obtain CustNo.
     * @return the customer #
     */
    public char[] getCustNo() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.CUSTOMERNO, char[].class)
                         : null;
    }

    /**
     * Obtain UserId.
     * @return the userId
     */
    public char[] getUserId() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.USERID, char[].class)
                         : null;
    }

    /**
     * Obtain Password.
     * @return the password
     */
    public char[] getPassword() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.PASSWORD, char[].class)
                         : null;
    }

    /**
     * Obtain SortCode.
     * @return the sort code
     */
    public char[] getSortCode() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.SORTCODE, char[].class)
                         : null;
    }

    /**
     * Obtain Reference.
     * @return the reference
     */
    public char[] getReference() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.REFERENCE, char[].class)
                         : null;
    }

    /**
     * Obtain Account.
     * @return the account
     */
    public char[] getAccount() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.ACCOUNT, char[].class)
                         : null;
    }

    /**
     * Obtain Notes.
     * @return the notes
     */
    public char[] getNotes() {
        return hasInfoSet
                         ? theInfoSet.getValue(AccountInfoClass.NOTES, char[].class)
                         : null;
    }

    /**
     * Obtain Status.
     * @return the status
     */
    protected AccountStatus getStatus() {
        return theStatus;
    }

    /**
     * Obtain Earliest event.
     * @return the event
     */
    public Event getEarliest() {
        return theStatus.getEarliest();
    }

    /**
     * Obtain Latest Event.
     * @return the event
     */
    public Event getLatest() {
        return theStatus.getLatest();
    }

    /**
     * Obtain Initial Price.
     * @return the price
     */
    public SecurityPrice getInitPrice() {
        return theStatus.getInitPrice();
    }

    /**
     * Is the account closeable?
     * @return true/false
     */
    public boolean isCloseable() {
        return theStatus.isCloseable();
    }

    /**
     * Does the account have loans?
     * @return true/false
     */
    public boolean hasLoans() {
        return theStatus.hasLoans();
    }

    /**
     * Is the account a parent?
     * @return true/false
     */
    public boolean isParent() {
        return theStatus.isParent();
    }

    /**
     * Get the close Date of the account.
     * @return the closeDate
     */
    public JDateDay getCloseDate() {
        return theStatus.getCloseDate();
    }

    /**
     * Is the account an alias?
     * @return true/false
     */
    public boolean isAlias() {
        return getAlias() != null;
    }

    /**
     * Is the account aliased to?
     * @return true/false
     */
    public boolean isAliasedTo() {
        return theStatus.isAliasedTo();
    }

    /**
     * Is the account deletable?
     * @return true/false
     */
    public boolean isDeletable() {
        return theStatus.isDeletable(getState()) && (!isDeleted()) && (!getAccountCategoryClass().isSingular());
    }

    @Override
    public DataState getState() {
        /* Pop history for self */
        DataState myState = super.getState();

        /* If we should use the InfoSet */
        if ((myState == DataState.CLEAN) && (useInfoSet)) {
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
        if ((myState == EditState.CLEAN) && (useInfoSet)) {
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
        if ((!hasHistory) && (useInfoSet)) {
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
        AccountInfoClass myClass = AccountInfoSet.getClassForField(pField);
        if (myClass != null) {
            return (useInfoSet)
                               ? theInfoSet.fieldChanged(myClass)
                               : Difference.IDENTICAL;
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
    public AccountList getList() {
        return (AccountList) super.getList();
    }

    @Override
    public boolean isLocked() {
        return isClosed();
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
        if ((getStyle() == ListStyle.EDIT) && (pAccount.getStyle() == ListStyle.CORE)) {
            /* Copy the flags */
            theStatus = new AccountStatus(pAccount.theStatus);
        }
    }

    /**
     * Secure constructor.
     * @param pList the List to add to
     * @param pId the Account id
     * @param pControlId the control id
     * @param pName the Encrypted Name of the account
     * @param pActCatId the Account category id
     * @param isClosed is the account closed?
     * @param isTaxFree is the account taxFree?
     * @param isGross is the account grossInterest?
     * @param uCurrencyId the Account currency id
     * @throws JOceanusException on error
     */
    private Account(final AccountList pList,
                    final Integer pId,
                    final Integer pControlId,
                    final byte[] pName,
                    final Integer pActCatId,
                    final Boolean isClosed,
                    final Boolean isTaxFree,
                    final Boolean isGross,
                    final Integer uCurrencyId) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId, pControlId, pName, pActCatId, isClosed, isTaxFree, isGross, uCurrencyId);

        /* Create the InfoSet */
        theInfoSet = new AccountInfoSet(this, pList.getActInfoTypes(), pList.getAccountInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Open constructor.
     * @param pList the List to add to
     * @param pId the id
     * @param pName the Name of the account
     * @param pCategory the Account category
     * @param isClosed is the account closed?
     * @param isTaxFree is the account taxFree?
     * @param isGross is the account grossInterest?
     * @param pCurrency the Account currency
     * @throws JOceanusException on error
     */
    private Account(final AccountList pList,
                    final Integer pId,
                    final String pName,
                    final String pCategory,
                    final Boolean isClosed,
                    final Boolean isTaxFree,
                    final Boolean isGross,
                    final String pCurrency) throws JOceanusException {
        /* Initialise the item */
        super(pList, pId, pName, pCategory, isClosed, isTaxFree, isGross, pCurrency);

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
     * Adjust closed/maturity dates.
     * @throws JOceanusException on error
     */
    public void adjustDates() throws JOceanusException {
        /* Adjust closed date */
        theStatus.adjustClosed();

        /* If the maturity is null for a bond set it to close date */
        if (isCategoryClass(AccountCategoryClass.BOND) && getMaturity() == null) {
            /* Record a date for maturity */
            setMaturity(getCloseDate());
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

        /* Reset status */
        theStatus.resetStatus();
    }

    /**
     * Touch an account.
     * @param pObject the object touch the account
     */
    @Override
    public void touchItem(final DataItem<MoneyWiseList> pObject) {
        /* Note that the account is Active */
        super.touchItem(pObject);

        /* Adjust status */
        theStatus.touchItem(this, pObject);
    }

    /**
     * Set a new maturity date.
     * @param pDate the new date
     * @throws JOceanusException on error
     */
    public void setMaturity(final JDateDay pDate) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.MATURITY, pDate);
    }

    /**
     * Set a new parent.
     * @param pParent the new parent
     * @throws JOceanusException on error
     */
    public void setParent(final Account pParent) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.PARENT, pParent);
    }

    /**
     * Set a new alias.
     * @param pAlias the new alias
     * @throws JOceanusException on error
     */
    public void setAlias(final Account pAlias) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.ALIAS, pAlias);
    }

    /**
     * Set a new portfolio.
     * @param pPortfolio the new portfolio
     * @throws JOceanusException on error
     */
    public void setPortfolio(final Account pPortfolio) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.PORTFOLIO, pPortfolio);
    }

    /**
     * Set a new holding.
     * @param pHolding the new holding
     * @throws JOceanusException on error
     */
    public void setHolding(final Account pHolding) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.HOLDING, pHolding);
    }

    /**
     * Set a new symbol.
     * @param pSymbol the new symbol
     * @throws JOceanusException on error
     */
    public void setSymbol(final String pSymbol) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.SYMBOL, pSymbol);
    }

    /**
     * Set new comments.
     * @param pComments the new comments
     * @throws JOceanusException on error
     */
    public void setComments(final String pComments) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.COMMENTS, pComments);
    }

    /**
     * Set a new opening balance.
     * @param pBalance the new opening balance
     * @throws JOceanusException on error
     */
    public void setOpeningBalance(final JMoney pBalance) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.OPENINGBALANCE, pBalance);
    }

    /**
     * Set a new autoExpense.
     * @param pCategory the new autoExpense
     * @throws JOceanusException on error
     */
    public void setAutoExpense(final EventCategory pCategory) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.AUTOEXPENSE, pCategory);
    }

    /**
     * Set a new WebSite.
     * @param pWebSite the new webSite
     * @throws JOceanusException on error
     */
    public void setWebSite(final char[] pWebSite) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.WEBSITE, pWebSite);
    }

    /**
     * Set a new CustNo.
     * @param pCustNo the new custNo
     * @throws JOceanusException on error
     */
    public void setCustNo(final char[] pCustNo) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.CUSTOMERNO, pCustNo);
    }

    /**
     * Set a new UserId.
     * @param pUserId the new userId
     * @throws JOceanusException on error
     */
    public void setUserId(final char[] pUserId) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.USERID, pUserId);
    }

    /**
     * Set a new Password.
     * @param pPassword the new password
     * @throws JOceanusException on error
     */
    public void setPassword(final char[] pPassword) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.PASSWORD, pPassword);
    }

    /**
     * Set a new SortCode.
     * @param pSortCode the new sort code
     * @throws JOceanusException on error
     */
    public void setSortCode(final char[] pSortCode) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.SORTCODE, pSortCode);
    }

    /**
     * Set a new Account.
     * @param pAccount the new account
     * @throws JOceanusException on error
     */
    public void setAccount(final char[] pAccount) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.ACCOUNT, pAccount);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws JOceanusException on error
     */
    public void setReference(final char[] pReference) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.REFERENCE, pReference);
    }

    /**
     * Set a new Notes.
     * @param pNotes the new notes
     * @throws JOceanusException on error
     */
    public void setNotes(final char[] pNotes) throws JOceanusException {
        setInfoSetValue(AccountInfoClass.NOTES, pNotes);
    }

    /**
     * Set non-closeable.
     */
    public void setNonCloseable() {
        /* Record the status */
        theStatus.setNonCloseable();
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    private void setInfoSetValue(final AccountInfoClass pInfoClass,
                                 final Object pValue) throws JOceanusException {
        /* Reject if there is no infoSet */
        if (!hasInfoSet) {
            throw new JMoneyWiseLogicException(ERROR_BADINFOSET);
        }

        /* Set the value */
        theInfoSet.setValue(pInfoClass, pValue);
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch underlying items */
        super.touchUnderlyingItems();

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    /**
     * Validate the account.
     */
    @Override
    public void validate() {
        AccountCategoryClass myClass = getAccountCategoryClass();
        Account myAlias = getAlias();

        /* Validate base components */
        super.validate();

        /* If we have a class */
        if (myClass != null) {
            /* If the account has rates then it must be money-based */
            if ((theStatus.hasRates()) && (!myClass.hasValue())) {
                addError(ERROR_BADRATES, FIELD_CATEGORY);
            }

            /* If the account is closed it must be closeable */
            if ((isClosed()) && (!isCloseable())) {
                addError(ERROR_BADCLOSE, FIELD_CLOSED);
            }

            /* If the account has units */
            if (myClass.hasUnits()) {
                /* Account must have prices unless it is idle or alias */
                if ((!theStatus.hasPrices()) && (theStatus.hasEvents()) && (myAlias == null)) {
                    addError(ERROR_NOPRICES, FIELD_CATEGORY);
                }

                /* else the account is not priced */
            } else if (theStatus.hasPrices()) {
                addError(ERROR_BADPRICES, FIELD_CATEGORY);
            }

            /* If the account is tax free, check that it is allowed */
            if ((isTaxFree()) && (!myClass.canTaxFree())) {
                addError(ERROR_TAXFREE, FIELD_TAXFREE);
            }

            /* If the account is gross interest, check that it is allowed */
            if ((isGrossInterest()) && (!myClass.canTaxFree())) {
                addError(ERROR_GROSS, FIELD_GROSS);
            }

            /* Cannot be both gross interest and taxFree */
            if ((isGrossInterest()) && (isTaxFree())) {
                addError(ERROR_TAXFREEGROSS, FIELD_TAXFREE);
                addError(ERROR_TAXFREEGROSS, FIELD_GROSS);
            }

            /* If we have a category and an infoSet */
            if (theInfoSet != null) {
                /* Validate the InfoSet */
                theInfoSet.validate();

                /* If the account is autoExpense, check that there is no opening balance */
                if ((getAutoExpense() != null) && (getOpeningBalance() != null)) {
                    addError(ERROR_AUTOBALANCE, AccountInfoSet.getFieldForClass(AccountInfoClass.AUTOEXPENSE));
                }
            }
        }

        /* Set validation flag */
        boolean isValid = !hasErrors();
        if (isValid) {
            setValidEdit();
        }
    }

    /**
     * Update base account from an edited account.
     * @param pAccount the edited account
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pAccount) {
        /* Can only update from an account */
        if (!(pAccount instanceof Account)) {
            return false;
        }

        Account myAccount = (Account) pAccount;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myAccount);

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Account List class.
     */
    public static class AccountList
            extends AccountBaseList<Account> {
        /**
         * Local Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), AccountBaseList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        /**
         * Account field id.
         */
        private static final JDataField FIELD_ACCOUNT = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataName"));

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_ACCOUNT.equals(pField)) {
                return (theAccount == null)
                                           ? JDataFieldValue.SKIP
                                           : theAccount;
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
        public AccountList(final MoneyWiseData pData) {
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
        public AccountList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (AccountList) super.cloneList(pDataSet);
        }

        /**
         * Construct an edit extract for an Account.
         * @param pAccount the relevant account
         * @return the edit Extract
         * @throws JOceanusException on error
         */
        public AccountList deriveEditList(final Account pAccount) throws JOceanusException {
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
         * @param pCategory the account category
         * @return the edit Extract
         * @throws JOceanusException on error
         */
        public AccountList deriveEditList(final AccountCategory pCategory) throws JOceanusException {
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
            Account myNew = new Account(myList);
            myNew.setAccountCategory(pCategory);
            myNew.setNewVersion();

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
        public Account addCopyItem(final DataItem<?> pAccount) {
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
         * Add an Account.
         * @param pId the is
         * @param pName the Name of the account
         * @param pCategory the Name of the account category
         * @param isClosed is the account closed?
         * @param isTaxFree is the account taxFree?
         * @param isGross is the account gross Interest?
         * @param pCurrency the Account currency
         * @return the new account
         * @throws JOceanusException on error
         */
        public Account addOpenItem(final Integer pId,
                                   final String pName,
                                   final String pCategory,
                                   final Boolean isClosed,
                                   final Boolean isTaxFree,
                                   final Boolean isGross,
                                   final String pCurrency) throws JOceanusException {
            /* Create the new account */
            Account myAccount = new Account(this, pId, pName, pCategory, isClosed, isTaxFree, isGross, pCurrency);

            /* Check that this AccountId has not been previously added */
            if (!isIdUnique(pId)) {
                myAccount.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myAccount, ERROR_VALIDATION);
            }

            /* Check that this Account has not been previously added */
            if (findItemByName(myAccount.getName()) != null) {
                myAccount.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myAccount, "Duplicate Account");
            }

            /* Add the Account to the list */
            append(myAccount);
            return myAccount;
        }

        /**
         * Add an Account.
         * @param pId the Id of the account
         * @param pControlId the control id
         * @param pName the Encrypted Name of the account
         * @param pActCatId the Id of the account category
         * @param isClosed is the account closed?
         * @param isTaxFree is the account taxFree?
         * @param isGross is the account gross Interest?
         * @param pCurrencyId the Account currency id
         * @throws JOceanusException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final byte[] pName,
                                  final Integer pActCatId,
                                  final Boolean isClosed,
                                  final Boolean isTaxFree,
                                  final Boolean isGross,
                                  final Integer pCurrencyId) throws JOceanusException {
            /* Create the new account */
            Account myAccount = new Account(this, pId, pControlId, pName, pActCatId, isClosed, isTaxFree, isGross, pCurrencyId);

            /* Check that this AccountId has not been previously added */
            if (!isIdUnique(pId)) {
                myAccount.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myAccount, ERROR_VALIDATION);
            }

            /* Check that this Account has not been previously added */
            if (findItemByName(myAccount.getName()) != null) {
                myAccount.addError(ERROR_DUPLICATE, FIELD_NAME);
                throw new JMoneyWiseDataException(myAccount, ERROR_VALIDATION);
            }

            /* Add the Account to the list */
            append(myAccount);
        }
    }
}
