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

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.EncryptedData.EncryptedMoney;
import net.sourceforge.joceanus.jdatamanager.EncryptedValueSet;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFormatter;
import net.sourceforge.joceanus.jdatamanager.ValueSet;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.EncryptedItem;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.Account.AccountList;
import net.sourceforge.joceanus.jmoneywise.data.EventCategory.EventCategoryList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Event data type.
 * @author Tony Washer
 */
public abstract class EventBase
        extends EncryptedItem
        implements Comparable<EventBase> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventBase.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), EncryptedItem.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDate"));

    /**
     * Debit Field Id.
     */
    public static final JDataField FIELD_DEBIT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataDebit"));

    /**
     * Credit Field Id.
     */
    public static final JDataField FIELD_CREDIT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCredit"));

    /**
     * Amount Field Id.
     */
    public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataAmount"));

    /**
     * Category Field Id.
     */
    public static final JDataField FIELD_CATEGORY = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataCategory"));

    /**
     * Reconciled Field Id.
     */
    public static final JDataField FIELD_RECONCILED = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataReconciled"));

    /**
     * Split Event Field Id.
     */
    public static final JDataField FIELD_SPLIT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataSplit"));

    /**
     * Parent Field Id.
     */
    public static final JDataField FIELD_PARENT = FIELD_DEFS.declareEqualityValueField(NLS_BUNDLE.getString("DataParent"));

    /**
     * Hidden Category Error Text.
     */
    private static final String ERROR_HIDDEN = NLS_BUNDLE.getString("ErrorHidden");

    /**
     * Invalid Debit/Credit/Category Combination Error Text.
     */
    private static final String ERROR_COMBO = NLS_BUNDLE.getString("ErrorCombo");

    /**
     * Invalid Parent Error Text.
     */
    private static final String ERROR_BADPARENT = NLS_BUNDLE.getString("ErrorBadParent");

    /**
     * Parent Date Error Text.
     */
    private static final String ERROR_PARENTDATE = NLS_BUNDLE.getString("ErrorParentDate");

    /**
     * Zero Amount Error Text.
     */
    private static final String ERROR_ZEROAMOUNT = NLS_BUNDLE.getString("ErrorZeroAmount");

    @Override
    public boolean skipField(final JDataField pField) {
        if ((FIELD_SPLIT.equals(pField))
            && !isSplit()) {
            return true;
        }
        if ((FIELD_PARENT.equals(pField))
            && (!isSplit() || (!isChild()))) {
            return true;
        }
        return super.skipField(pField);
    }

    /**
     * Obtain Date.
     * @return the date
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain category.
     * @return the category
     */
    public final EventCategory getCategory() {
        return getCategory(getValueSet());
    }

    /**
     * Obtain CategoryId.
     * @return the categoryId
     */
    public Integer getCategoryId() {
        EventCategory myCategory = getCategory();
        return (myCategory == null)
                ? null
                : myCategory.getId();
    }

    /**
     * Obtain categoryName.
     * @return the categoryName
     */
    public String getCategoryName() {
        EventCategory myCategory = getCategory();
        return (myCategory == null)
                ? null
                : myCategory.getName();
    }

    /**
     * Obtain EventCategoryClass.
     * @return the eventCategoryClass
     */
    public EventCategoryClass getCategoryClass() {
        EventCategory myCategory = getCategory();
        return (myCategory == null)
                ? null
                : myCategory.getCategoryTypeClass();
    }

    /**
     * Obtain Amount.
     * @return the amount
     */
    public JMoney getAmount() {
        return getAmount(getValueSet());
    }

    /**
     * Obtain Encrypted amount.
     * @return the bytes
     */
    public byte[] getAmountBytes() {
        return getAmountBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Amount Field.
     * @return the Field
     */
    protected EncryptedMoney getAmountField() {
        return getAmountField(getValueSet());
    }

    /**
     * Obtain Debit account.
     * @return the debit
     */
    public Account getDebit() {
        return getDebit(getValueSet());
    }

    /**
     * Obtain DebitId.
     * @return the debitId
     */
    public Integer getDebitId() {
        Account myAccount = getDebit();
        return (myAccount == null)
                ? null
                : myAccount.getId();
    }

    /**
     * Obtain DebitName.
     * @return the debitName
     */
    public String getDebitName() {
        Account myAccount = getDebit();
        return (myAccount == null)
                ? null
                : myAccount.getName();
    }

    /**
     * Obtain Credit account.
     * @return the credit
     */
    public Account getCredit() {
        return getCredit(getValueSet());
    }

    /**
     * Obtain CreditId.
     * @return the creditId
     */
    public Integer getCreditId() {
        Account myAccount = getCredit();
        return (myAccount == null)
                ? null
                : myAccount.getId();
    }

    /**
     * Obtain CreditName.
     * @return the creditName
     */
    public String getCreditName() {
        Account myAccount = getCredit();
        return (myAccount == null)
                ? null
                : myAccount.getName();
    }

    /**
     * Obtain Reconciled State.
     * @return the reconciled state
     */
    public Boolean isReconciled() {
        return isReconciled(getValueSet());
    }

    /**
     * Obtain Split State.
     * @return the split state
     */
    public Boolean isSplit() {
        return isSplit(getValueSet());
    }

    /**
     * Obtain Parent.
     * @return the parent
     */
    public EventBase getParent() {
        return getParent(getValueSet());
    }

    /**
     * Is the event a child.
     * @return true/false
     */
    public boolean isChild() {
        return getParent() != null;
    }

    /**
     * Obtain ParentId.
     * @return the parentId
     */
    public Integer getParentId() {
        EventBase myParent = EventBase.getParent(getValueSet());
        return (myParent == null)
                ? null
                : myParent.getId();
    }

    /**
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain Reconciled State.
     * @param pValueSet the valueSet
     * @return the Reconciled State
     */
    public static Boolean isReconciled(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_RECONCILED, Boolean.class);
    }

    /**
     * Obtain Split State.
     * @param pValueSet the valueSet
     * @return the Split State
     */
    public static Boolean isSplit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_SPLIT, Boolean.class);
    }

    /**
     * Obtain Category.
     * @param pValueSet the valueSet
     * @return the category
     */
    public static EventCategory getCategory(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATEGORY, EventCategory.class);
    }

    /**
     * Obtain Amount.
     * @param pValueSet the valueSet
     * @return the Amount
     */
    public static JMoney getAmount(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_AMOUNT, JMoney.class);
    }

    /**
     * Obtain Encrypted Amount.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getAmountBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_AMOUNT);
    }

    /**
     * Obtain Encrypted amount field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedMoney getAmountField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_AMOUNT, EncryptedMoney.class);
    }

    /**
     * Obtain Debit Account.
     * @param pValueSet the valueSet
     * @return the Debit Account
     */
    public static Account getDebit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEBIT, Account.class);
    }

    /**
     * Obtain Credit Account.
     * @param pValueSet the valueSet
     * @return the Credit Account
     */
    public static Account getCredit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CREDIT, Account.class);
    }

    /**
     * Obtain Parent Event.
     * @param pValueSet the valueSet
     * @return the Parent Event
     */
    public static EventBase getParent(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PARENT, EventBase.class);
    }

    /**
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
    }

    /**
     * Set reconciled state.
     * @param pValue the value
     */
    protected void setValueReconciled(final Boolean pValue) {
        getValueSet().setValue(FIELD_RECONCILED, pValue);
    }

    /**
     * Set split state.
     * @param pValue the value
     */
    protected void setValueSplit(final Boolean pValue) {
        getValueSet().setValue(FIELD_SPLIT, pValue);
    }

    /**
     * Set category value.
     * @param pValue the value
     */
    private void setValueCategory(final EventCategory pValue) {
        getValueSet().setValue(FIELD_CATEGORY, pValue);
    }

    /**
     * Set category id.
     * @param pId the id
     */
    private void setValueCategory(final Integer pId) {
        getValueSet().setValue(FIELD_CATEGORY, pId);
    }

    /**
     * Set category name.
     * @param pName the name
     */
    private void setValueCategory(final String pName) {
        getValueSet().setValue(FIELD_CATEGORY, pName);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JOceanusException on error
     */
    private void setValueAmount(final JMoney pValue) throws JOceanusException {
        setEncryptedValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pBytes the value
     * @throws JOceanusException on error
     */
    private void setValueAmount(final byte[] pBytes) throws JOceanusException {
        setEncryptedValue(FIELD_AMOUNT, pBytes, JMoney.class);
    }

    /**
     * Set amount value.
     * @param pValue the value
     */
    protected final void setValueAmount(final EncryptedMoney pValue) {
        getValueSet().setValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set debit value.
     * @param pValue the value
     */
    protected final void setValueDebit(final Account pValue) {
        getValueSet().setValue(FIELD_DEBIT, pValue);
    }

    /**
     * Set debit name.
     * @param pName the name
     */
    private void setValueDebit(final String pName) {
        getValueSet().setValue(FIELD_DEBIT, pName);
    }

    /**
     * Set debit id.
     * @param pId the value
     */
    private void setValueDebit(final Integer pId) {
        getValueSet().setValue(FIELD_DEBIT, pId);
    }

    /**
     * Set credit value.
     * @param pValue the value
     */
    protected final void setValueCredit(final Account pValue) {
        getValueSet().setValue(FIELD_CREDIT, pValue);
    }

    /**
     * Set credit id.
     * @param pId the id
     */
    private void setValueCredit(final Integer pId) {
        getValueSet().setValue(FIELD_CREDIT, pId);
    }

    /**
     * Set credit name.
     * @param pName the name
     */
    private void setValueCredit(final String pName) {
        getValueSet().setValue(FIELD_CREDIT, pName);
    }

    /**
     * Set parent value.
     * @param pValue the value
     */
    protected final void setValueParent(final EventBase pValue) {
        getValueSet().setValue(FIELD_PARENT, pValue);
    }

    /**
     * Set parent id.
     * @param pId the value
     */
    private void setValueParent(final Integer pId) {
        getValueSet().setValue(FIELD_PARENT, pId);
    }

    @Override
    public final MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    @Override
    public EventBaseList<?> getList() {
        return (EventBaseList<?>) super.getList();
    }

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pEvent The Event to copy
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList,
                        final EventBase pEvent) {
        /* Set standard values */
        super(pList, pEvent);
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());
    }

    /**
     * Secure constructor.
     * @param pList the list
     * @param pId the id
     * @param pControlId the controlId
     * @param pDate the date
     * @param pDebit the debit id
     * @param pCredit the credit id
     * @param pAmount the amount
     * @param pCategory the category id
     * @param pReconciled is the event reconciled
     * @param pSplit is the event split
     * @param pParent the parent id
     * @throws JOceanusException on error
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList,
                        final Integer pId,
                        final Integer pControlId,
                        final JDateDay pDate,
                        final Integer pDebit,
                        final Integer pCredit,
                        final byte[] pAmount,
                        final Integer pCategory,
                        final Boolean pReconciled,
                        final Boolean pSplit,
                        final Integer pParent) throws JOceanusException {
        /* Initialise item */
        super(pList, pId);

        /* Protect against exceptions */
        try {
            /* Store the IDs that we will look up */
            setValueDebit(pDebit);
            setValueCredit(pCredit);
            setValueParent(pParent);
            setValueCategory(pCategory);
            setControlKey(pControlId);
            setValueReconciled(pReconciled);
            setValueSplit(pSplit);

            /* Create the date */
            setValueDate(pDate);

            /* Record the encrypted values */
            setValueAmount(pAmount);

            /* Catch Exceptions */
        } catch (JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param uId the id
     * @param pDate the date
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pAmount the amount
     * @param pCategory the category
     * @param pReconciled is the event reconciled
     * @param pSplit is the event split
     * @param pParent the parent
     * @throws JOceanusException on error
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList,
                        final Integer uId,
                        final JDateDay pDate,
                        final String pDebit,
                        final String pCredit,
                        final String pAmount,
                        final String pCategory,
                        final Boolean pReconciled,
                        final Boolean pSplit,
                        final EventBase pParent) throws JOceanusException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access the parser */
            MoneyWiseData myDataSet = getDataSet();
            JDataFormatter myFormatter = myDataSet.getDataFormatter();
            JDecimalParser myParser = myFormatter.getDecimalParser();

            /* Record the standard values */
            setValueDebit(pDebit);
            setValueCredit(pCredit);
            setValueParent(pParent);
            setValueCategory(pCategory);
            setValueReconciled(pReconciled);
            setValueSplit(pSplit);
            setValueDate(pDate);
            setValueAmount(myParser.parseMoneyValue(pAmount));

            /* Catch Exceptions */
        } catch (IllegalArgumentException | JOceanusException e) {
            /* Pass on exception */
            throw new JMoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    /**
     * Compare this event to another to establish sort order.
     * @param pThat The Event to compare to
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the sort order
     */
    @Override
    public int compareTo(final EventBase pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* If header settings differ */
        if (isHeader() != pThat.isHeader()) {
            return isHeader()
                    ? -1
                    : 1;
        }

        /* If the dates differ */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Access parents */
        EventBase myParent = getParent();
        EventBase myAltParent = pThat.getParent();

        /* If we are a child */
        if (myParent != null) {
            /* If we are both children */
            if (myAltParent != null) {
                /* Compare parents */
                iDiff = Difference.compareObject(myParent, myAltParent);
                if (iDiff != 0) {
                    return iDiff;
                }

                /* Same parent so compare directly */

                /* else we are comparing against a parent */
            } else {
                /* Compare parent with target */
                iDiff = Difference.compareObject(myParent, pThat);
                if (iDiff != 0) {
                    return iDiff;
                }

                /* We are comparing against our parent, so always later */
                return 1;
            }

            /* else if we are comparing against a child */
        } else if (myAltParent != null) {
            /* Compare with targets parent */
            iDiff = Difference.compareObject(this, myAltParent);
            if (iDiff != 0) {
                return iDiff;
            }

            /* We are comparing against our parent, so always later */
            return -1;
        }

        /* If the categories differ */
        iDiff = Difference.compareObject(getCategory(), pThat.getCategory());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Encryption details */
        super.resolveDataSetLinks();

        /* Access Relevant lists */
        MoneyWiseData myData = getDataSet();
        AccountList myAccounts = myData.getAccounts();
        EventCategoryList myCategories = myData.getEventCategories();
        ValueSet myValues = getValueSet();

        /* Adjust Debit */
        Object myDebit = myValues.getValue(FIELD_DEBIT);
        if (myDebit instanceof Account) {
            myDebit = ((Account) myDebit).getId();
        }
        if (myDebit instanceof Integer) {
            Account myAccount = myAccounts.findItemById((Integer) myDebit);
            if (myAccount == null) {
                addError(ERROR_UNKNOWN, FIELD_DEBIT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueDebit(myAccount);
        } else if (myDebit instanceof String) {
            Account myAccount = myAccounts.findItemByName((String) myDebit);
            if (myAccount == null) {
                addError(ERROR_UNKNOWN, FIELD_DEBIT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueDebit(myAccount);
        }

        /* Adjust Credit */
        Object myCredit = myValues.getValue(FIELD_CREDIT);
        if (myCredit instanceof Account) {
            myCredit = ((Account) myCredit).getId();
        }
        if (myCredit instanceof Integer) {
            Account myAccount = myAccounts.findItemById((Integer) myCredit);
            if (myAccount == null) {
                addError(ERROR_UNKNOWN, FIELD_CREDIT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCredit(myAccount);
        } else if (myCredit instanceof String) {
            Account myAccount = myAccounts.findItemByName((String) myCredit);
            if (myAccount == null) {
                addError(ERROR_UNKNOWN, FIELD_CREDIT);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCredit(myAccount);
        }

        /* Adjust Category */
        Object myCategory = myValues.getValue(FIELD_CATEGORY);
        if (myCategory instanceof EventCategory) {
            myCategory = ((EventCategory) myCategory).getId();
        }
        if (myCategory instanceof Integer) {
            EventCategory myCat = myCategories.findItemById((Integer) myCategory);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_CATEGORY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCategory(myCat);
        } else if (myCategory instanceof String) {
            EventCategory myCat = myCategories.findItemByName((String) myCategory);
            if (myCat == null) {
                addError(ERROR_UNKNOWN, FIELD_CATEGORY);
                throw new JMoneyWiseDataException(this, ERROR_RESOLUTION);
            }
            setValueCategory(myCat);
        }
    }

    /**
     * Determines whether an event can be valid.
     * @param pEventClass The event category of the event
     * @param pAccountCategory The account category of the event
     * @param pCredit is the account a credit or a debit
     * @return valid true/false
     */
    public static boolean isValidEvent(final EventCategoryClass pEventClass,
                                       final AccountCategory pAccountCategory,
                                       final boolean pCredit) {
        return false; // TODO
    }

    /**
     * Determine validity of an event between the two accounts, for the given category.
     * @param pCategory The category of the event
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @return true/false
     */
    public static boolean isValidEvent(final EventCategory pCategory,
                                       final Account pDebit,
                                       final Account pCredit) {
        /* Analyse the components */
        boolean isRecursive = Difference.isEqual(pDebit, pCredit);
        AccountType myDebitType = AccountType.deriveType(pDebit);
        AccountType myCreditType = AccountType.deriveType(pCredit);
        TransactionType myCatTran = TransactionType.deriveType(pCategory);
        TransactionType myActTran = myDebitType.getTransactionType(myCreditType);

        /* Handle illegal setups */
        if (myCatTran.isIllegal()
            || myActTran.isIllegal()) {
            return false;
        }

        /* Access account category classes */
        AccountCategoryClass myDebitClass = pDebit.getAccountCategoryClass();
        AccountCategoryClass myCreditClass = pCredit.getAccountCategoryClass();
        EventCategoryClass myCatClass = pCategory.getCategoryTypeClass();

        /* If the transaction involves autoExpense */
        if (myActTran.isAutoExpense()) {
            /* Special processing */
            switch (myCatClass) {
                case TRANSFER:
                    /* Transfer must be to/from savings */
                    return (myDebitType.isAutoExpense())
                            ? myCreditClass.isSavings()
                            : myDebitClass.isSavings();
                case EXPENSE:
                    /* Transfer must be to/from nonAsset */
                    return (myDebitType.isAutoExpense())
                            ? myCreditClass.isNonAsset()
                            : myDebitClass.isNonAsset();

                    /* Auto Expense cannot be used for other categories */
                default:
                    return false;
            }
        }

        /* If this is an non-recursive expense (i.e. decreases the value of assets) */
        if (myActTran.isExpense()
            && !isRecursive) {
            /* Switch Debit and Credit classes so that this look like an expense */
            AccountCategoryClass myClass = myDebitClass;
            myDebitClass = myCreditClass;
            myCreditClass = myClass;

            /* Switch debit and credit types */
            AccountType myType = myDebitType;
            myDebitType = myCreditType;
            myCreditType = myType;
        }

        /* Switch on the CategoryClass */
        switch (myCatClass) {
            case TAXEDINCOME:
                /* Cannot refund Taxed Income */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Taxed income must be from employer to savings/loan (as expense) */
                return (myDebitClass == AccountCategoryClass.EMPLOYER)
                       && (myCreditClass.isSavings() || myCreditClass.isLoan());

            case GRANTINCOME:
                /* Cannot refund Grant Income */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Grant income must be from grant-able to savings account */
                return myDebitClass.canGrant()
                       && myCreditClass.isSavings();

            case BENEFITINCOME:
                /* Cannot refund Benefit Income */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Benefit income must be from government to savings account */
                return (myDebitClass == AccountCategoryClass.GOVERNMENT)
                       && myCreditClass.isSavings();

            case OTHERINCOME:
                /* Other income is from nonAsset to savings/loan */
                return myDebitClass.isNonAsset()
                       && (myCreditClass.isSavings() || myCreditClass.isLoan());

            case GIFTEDINCOME:
            case INHERITED:
                /* Cannot refund Gifted Income/Inheritance */
                if (myActTran.isExpense()) {
                    return false;
                }

                /* Inheritance must be from individual to asset */
                return (myDebitClass == AccountCategoryClass.INDIVIDUAL)
                       && myCreditType.isAsset();

            case INTEREST:
                /* Debit must be able to generate interest */
                if (!myDebitClass.isSavings()) {
                    return false;
                }

                /* Interest must be paid to valued account */
                return myCreditType.isValued();

            case DIVIDEND:
                /* Debit must be able to generate dividend */
                if (!myDebitClass.isDividend()) {
                    return false;
                }

                /* Dividend must be paid to valued account or else re-invested into capital */
                return myCreditType.isValued()
                       || (isRecursive && myDebitClass.isCapital());

            case STOCKRIGHTSTAKEN:
                /* Stock rights taken is a transfer from a valued account to shares */
                return myDebitType.isValued()
                       && myCreditClass.isShares();

            case STOCKRIGHTSWAIVED:
                /* Stock rights taken is a transfer to a valued account from shares */
                return myCreditType.isValued()
                       && myDebitClass.isShares();

            case STOCKSPLIT:
                /* Stock adjust is only valid for shares and must be recursive */
                return isRecursive
                       && myDebitClass.isShares();

            case STOCKADJUST:
                /* Stock adjust is only valid for capital and must be recursive */
                return isRecursive
                       && myDebitClass.isCapital();

            case STOCKDEMERGER:
            case STOCKTAKEOVER:
                /* Stock DeMerger/TakeOver must be between different capital shares */
                return !isRecursive
                       && myDebitClass.isShares()
                       && myCreditClass.isShares();

            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* Rental Income must be from property to loan */
                return (myDebitClass == AccountCategoryClass.PROPERTY)
                       && myCreditClass.isLoan();

            case WRITEOFF:
            case LOANINTERESTEARNED:
            case LOANINTERESTCHARGED:
                /* Must be recursive on loan */
                return myDebitClass.isLoan()
                       && isRecursive;

            case LOCALTAXES:
                /* Local taxes must be from government to valued account */
                return (myDebitClass == AccountCategoryClass.GOVERNMENT)
                       && myCreditType.isValued();

            case CHARITYDONATION:
                /* CharityDonation is from nonAsset to Asset */
                return myDebitClass.isNonAsset()
                       && myCreditClass.isAsset();

            case TAXRELIEF:
                /* Tax Relief is from TaxMan to loan */
                return (myDebitClass == AccountCategoryClass.TAXMAN)
                       && myCreditClass.isLoan();

            case TAXSETTLEMENT:
                /* Settlement income is from TaxMan to valued */
                return (myDebitClass == AccountCategoryClass.TAXMAN)
                       && myCreditType.isValued();

            case TRANSFER:
                /* transfer is nonRecursive and from Asset to Asset */
                return !isRecursive
                       && myDebitClass.isAsset()
                       && myCreditClass.isAsset();

            case EXPENSE:
                /* Recovered expense is from nonAsset to Asset */
                return myDebitClass.isNonAsset()
                       && myCreditClass.isAsset();

            default:
                return false;
        }
    }

    /**
     * Determines whether an event relates to an account.
     * @param pAccount The account to check relations with
     * @return related to the account true/false
     */
    public boolean relatesTo(final Account pAccount) {
        boolean myResult = false;

        /* Check credit and debit accounts */
        if (getCredit().equals(pAccount)) {
            myResult = true;
        } else if (getDebit().equals(pAccount)) {
            myResult = true;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether a line is locked to updates.
     * @return true/false
     */
    @Override
    public boolean isLocked() {
        Account myCredit = getCredit();
        Account myDebit = getDebit();

        /* Check credit and debit accounts */
        return ((myCredit != null) && myCredit.isClosed())
               || ((myDebit != null) && myDebit.isClosed());
    }

    /**
     * Is this event category the required class.
     * @param pClass the required category class.
     * @return true/false
     */
    public boolean isCategoryClass(final EventCategoryClass pClass) {
        /* Check for match */
        return getCategoryClass() == pClass;
    }

    /**
     * Determines whether an event is a dividend re-investment.
     * @return dividend re-investment true/false
     */
    public boolean isDividendReInvestment() {
        /* Check for dividend re-investment */
        if (!isDividend()) {
            return false;
        }
        return (getCredit() != null)
               && Difference.isEqual(getDebit(), getCredit());
    }

    /**
     * Determines whether an event is an interest payment.
     * @return interest true/false
     */
    public boolean isInterest() {
        /* Check for interest */
        return (getCategory() != null)
               && getCategory().getCategoryTypeClass().isInterest();
    }

    /**
     * Determines whether an event is a dividend payment.
     * @return dividend true/false
     */
    public boolean isDividend() {
        /* Check for dividend */
        return (getCategory() != null)
               && getCategory().getCategoryTypeClass().isDividend();
    }

    /**
     * Determines whether an event needs a tax credit.
     * @param pCategory the category
     * @param pDebit the debit account
     * @return needs tax credit true/false
     */
    public static boolean needsTaxCredit(final EventCategory pCategory,
                                         final Account pDebit) {
        /* Handle null category */
        if (pCategory == null) {
            return false;
        }

        /* Switch on category class */
        switch (pCategory.getCategoryTypeClass()) {
        /* If this is a Taxable Gain/TaxedIncome we need a tax credit */
        // case TaxableGain:
            case TAXEDINCOME:
                return true;
                /* Check for dividend/interest */
            case DIVIDEND:
            case INTEREST:
                return (pDebit != null)
                       && !pDebit.isTaxFree();
            default:
                return false;
        }
    }

    /**
     * Determines whether an event needs a dilution factor.
     * @param pCategory the category
     * @return needs dilution factor true/false
     */
    public static boolean needsDilution(final EventCategory pCategory) {
        /* Handle null category */
        if (pCategory == null) {
            return false;
        }

        /* Switch on category type */
        switch (pCategory.getCategoryType().getCategoryClass()) {
        /* If this is a Stock Operation we need a dilution factor */
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTSTAKEN:
            case STOCKRIGHTSWAIVED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Set a new debit account.
     * @param pDebit the debit account
     */
    public void setDebit(final Account pDebit) {
        setValueDebit(pDebit);
    }

    /**
     * Set a new credit account.
     * @param pCredit the credit account
     */
    public void setCredit(final Account pCredit) {
        setValueCredit(pCredit);
    }

    /**
     * Set a new parent event.
     * @param pParent the parent event
     */
    public void setParent(final EventBase pParent) {
        setValueParent(pParent);
    }

    /**
     * Set a new category.
     * @param pCategory the category
     */
    public void setCategory(final EventCategory pCategory) {
        setValueCategory(pCategory);
    }

    /**
     * Set a reconciled indication.
     * @param pReconciled the reconciled state
     */
    public void setReconciled(final Boolean pReconciled) {
        setValueReconciled(pReconciled);
    }

    /**
     * Set a split indication.
     * @param pSplit the reconciled state
     */
    public void setSplit(final Boolean pSplit) {
        setValueSplit(pSplit);
    }

    /**
     * Set a new amount.
     * @param pAmount the amount
     * @throws JOceanusException on error
     */
    public void setAmount(final JMoney pAmount) throws JOceanusException {
        setValueAmount(pAmount);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
        setValueDate((pDate == null)
                ? null
                : new JDateDay(pDate));
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch the event category referred to */
        getCategory().touchItem(this);

        /* Touch the credit and debit accounts */
        getDebit().touchItem(this);
        getCredit().touchItem(this);

        /* Touch parent */
        EventBase myParent = getParent();
        if (myParent != null) {
            myParent.touchItem(this);
        }
    }

    /**
     * Validate the event.
     */
    @Override
    public void validate() {
        JDateDay myDate = getDate();
        Account myDebit = getDebit();
        Account myCredit = getCredit();
        JMoney myAmount = getAmount();
        EventBase myParent = getParent();
        EventCategory myCategory = getCategory();
        boolean doCheckCombo = true;

        /* Determine date range to check for */
        EventBaseList<?> myList = getList();
        JDateDayRange myRange = myList.getValidDateRange();

        /* The date must be non-null */
        if (myDate == null) {
            addError(ERROR_MISSING, FIELD_DATE);
            /* The date must be in-range */
        } else if (myRange.compareTo(myDate) != 0) {
            addError(ERROR_RANGE, FIELD_DATE);
        }

        /* Category must be non-null */
        if (myCategory == null) {
            addError(ERROR_MISSING, FIELD_CATEGORY);
            doCheckCombo = false;
            /* Must not be hidden */
        } else if (myCategory.getCategoryTypeClass().isHiddenType()) {
            addError(ERROR_HIDDEN, FIELD_CATEGORY);
        }

        /* Credit account must be non-null */
        if (myCredit == null) {
            addError(ERROR_MISSING, FIELD_CREDIT);
            doCheckCombo = false;
        }

        /* Debit account must be non-null */
        if (myDebit == null) {
            addError(ERROR_MISSING, FIELD_DEBIT);
            doCheckCombo = false;
        }

        /* Check combinations */
        if ((doCheckCombo)
            && (!isValidEvent(myCategory, myDebit, myCredit))) {
            addError(ERROR_COMBO, FIELD_DEBIT);
            addError(ERROR_COMBO, FIELD_CREDIT);
        }

        /* If we have a parent */
        if (myParent != null) {
            /* Parent must not be child */
            if (myParent.isChild()) {
                addError(ERROR_BADPARENT, FIELD_PARENT);
            }

            /* Parent must not be child */
            if (!Difference.isEqual(myDate, myParent.getDate())) {
                addError(ERROR_PARENTDATE, FIELD_PARENT);
            }
        }

        /* Money must not be null/negative */
        if (myAmount == null) {
            addError(ERROR_MISSING, FIELD_AMOUNT);
        } else if (!myAmount.isPositive()) {
            addError(ERROR_NEGATIVE, FIELD_AMOUNT);
        }

        /* Money must be zero for stock split/adjust/deMerger */
        if ((myAmount != null)
            && (myAmount.isNonZero())
            && (myCategory != null)
            && (myCategory.getCategoryTypeClass().needsZeroAmount())) {
            addError(ERROR_ZEROAMOUNT, FIELD_AMOUNT);
        }
    }

    /**
     * Update base event from an edited event.
     * @param pEvent the edited event
     */
    protected void applyBasicChanges(final EventBase pEvent) {
        /* Update the Date if required */
        if (!Difference.isEqual(getDate(), pEvent.getDate())) {
            setValueDate(pEvent.getDate());
        }

        /* Update the category if required */
        if (!Difference.isEqual(getCategory(), pEvent.getCategory())) {
            setValueCategory(pEvent.getCategory());
        }

        /* Update the debit account if required */
        if (!Difference.isEqual(getDebit(), pEvent.getDebit())) {
            setValueDebit(pEvent.getDebit());
        }

        /* Update the credit account if required */
        if (!Difference.isEqual(getCredit(), pEvent.getCredit())) {
            setValueCredit(pEvent.getCredit());
        }

        /* Update the parent event if required */
        if (!Difference.isEqual(getParent(), pEvent.getParent())) {
            setValueParent(pEvent.getParent());
        }

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), pEvent.getAmount())) {
            setValueAmount(pEvent.getAmountField());
        }

        /* Update the reconciled state if required */
        if (!Difference.isEqual(isReconciled(), pEvent.isReconciled())) {
            setValueReconciled(pEvent.isReconciled());
        }

        /* Update the split state if required */
        if (!Difference.isEqual(isSplit(), pEvent.isSplit())) {
            setValueSplit(pEvent.isSplit());
        }
    }

    /**
     * The Event List class.
     * @param <T> the dataTypeoppo
     */
    public abstract static class EventBaseList<T extends EventBase>
            extends EncryptedList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataListName"), DataList.FIELD_DEFS);

        /**
         * Range field id.
         */
        private static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataRange"));

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_RANGE.equals(pField)) {
                return theRange;
            }
            return super.getFieldValue(pField);
        }

        /**
         * DataSet range.
         */
        private JDateDayRange theRange = null;

        @Override
        public MoneyWiseData getDataSet() {
            return (MoneyWiseData) super.getDataSet();
        }

        /**
         * Obtain valid date range.
         * @return the valid range
         */
        public JDateDayRange getValidDateRange() {
            return theRange;
        }

        /**
         * Set the range.
         * @param pRange the range
         */
        protected void setRange(final JDateDayRange pRange) {
            theRange = pRange;
        }

        /**
         * Construct an empty CORE Event list.
         * @param pData the DataSet for the list
         * @param pClass the class of the item
         */
        protected EventBaseList(final MoneyWiseData pData,
                                final Class<T> pClass) {
            super(pClass, pData, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventBaseList(final EventBaseList<T> pSource) {
            super(pSource);
        }
    }
}
