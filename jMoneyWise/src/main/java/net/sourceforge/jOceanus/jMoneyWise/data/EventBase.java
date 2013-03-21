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

import java.util.Date;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.ValueSet;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryType.EventCategoryTypeList;

/**
 * Event data type.
 * @author Tony Washer
 */
public abstract class EventBase
        extends EncryptedItem
        implements Comparable<EventBase> {
    /**
     * Event Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventBase.class.getSimpleName(), EncryptedItem.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField("Date");

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");

    /**
     * CategoryType Field Id.
     */
    public static final JDataField FIELD_CATTYP = FIELD_DEFS.declareEqualityValueField("CategoryType");

    /**
     * Amount Field Id.
     */
    public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareEqualityValueField("Amount");

    /**
     * Debit Field Id.
     */
    public static final JDataField FIELD_DEBIT = FIELD_DEFS.declareEqualityValueField("Debit");

    /**
     * Credit Field Id.
     */
    public static final JDataField FIELD_CREDIT = FIELD_DEFS.declareEqualityValueField("Credit");

    /**
     * Obtain Date.
     * @return the date
     */
    public JDateDay getDate() {
        return getDate(getValueSet());
    }

    /**
     * Obtain Description.
     * @return the description
     */
    public String getDesc() {
        return getDesc(getValueSet());
    }

    /**
     * Obtain encrypted description.
     * @return the bytes
     */
    public byte[] getDescBytes() {
        return getDescBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Description Field.
     * @return the Field
     */
    protected EncryptedString getDescField() {
        return getDescField(getValueSet());
    }

    /**
     * Obtain category Type.
     * @return the categoryType
     */
    public final EventCategoryType getCategoryType() {
        return getCategoryType(getValueSet());
    }

    /**
     * Obtain CategoryTypeId.
     * @return the categoryTypeId
     */
    public Integer getCategoryTypeId() {
        EventCategoryType myType = getCategoryType();
        return (myType == null)
                ? null
                : myType.getId();
    }

    /**
     * Obtain categoryTypeName.
     * @return the categoryTypeName
     */
    public String getCategoryTypeName() {
        EventCategoryType myType = getCategoryType();
        return (myType == null)
                ? null
                : myType.getName();
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
     * Obtain Date.
     * @param pValueSet the valueSet
     * @return the Date
     */
    public static JDateDay getDate(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, JDateDay.class);
    }

    /**
     * Obtain Description.
     * @param pValueSet the valueSet
     * @return the Description
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
     * Obtain Encrypted Description field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedString getDescField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    /**
     * Obtain Category type.
     * @param pValueSet the valueSet
     * @return the categoryType
     */
    public static EventCategoryType getCategoryType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CATTYP, EventCategoryType.class);
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
     * Set date value.
     * @param pValue the value
     */
    private void setValueDate(final JDateDay pValue) {
        getValueSet().setValue(FIELD_DATE, pValue);
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
    protected final void setValueDesc(final EncryptedString pValue) {
        getValueSet().setValue(FIELD_DESC, pValue);
    }

    /**
     * Set categoryType value.
     * @param pValue the value
     */
    private void setValueCategoryType(final EventCategoryType pValue) {
        getValueSet().setValue(FIELD_CATTYP, pValue);
    }

    /**
     * Set categoryType id.
     * @param pId the id
     */
    private void setValueCategoryType(final Integer pId) {
        getValueSet().setValue(FIELD_CATTYP, pId);
    }

    /**
     * Set description value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueAmount(final JMoney pValue) throws JDataException {
        setEncryptedValue(FIELD_AMOUNT, pValue);
    }

    /**
     * Set amount value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueAmount(final byte[] pBytes) throws JDataException {
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
    protected void setValueDebit(final Account pValue) {
        getValueSet().setValue(FIELD_DEBIT, pValue);
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
    protected void setValueCredit(final Account pValue) {
        getValueSet().setValue(FIELD_CREDIT, pValue);
    }

    /**
     * Set credit id.
     * @param pId the id
     */
    private void setValueCredit(final Integer pId) {
        getValueSet().setValue(FIELD_CREDIT, pId);
    }

    @Override
    public final FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
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
     * @param uId the id
     * @param uControlId the controlId
     * @param pDate the date
     * @param pDesc the description
     * @param uDebit the debit id
     * @param uCredit the credit id
     * @param uCatType the categoryType id
     * @param pAmount the amount
     * @throws JDataException on error
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList,
                        final Integer uId,
                        final Integer uControlId,
                        final Date pDate,
                        final byte[] pDesc,
                        final Integer uDebit,
                        final Integer uCredit,
                        final Integer uCatType,
                        final byte[] pAmount) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access account list */
            FinanceData myData = getDataSet();
            AccountList myAccounts = myData.getAccounts();

            /* Store the IDs that we will look up */
            setValueDebit(uDebit);
            setValueCredit(uCredit);
            setValueCategoryType(uCatType);
            setControlKey(uControlId);

            /* Create the date */
            setValueDate(new JDateDay(pDate));

            /* Look up the Debit Account */
            Account myAccount = myAccounts.findItemById(uDebit);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Debit Account Id");
            }
            setValueDebit(myAccount);

            /* Look up the Debit Account */
            myAccount = myAccounts.findItemById(uCredit);
            if (myAccount == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Credit Account Id");
            }
            setValueCredit(myAccount);

            /* Look up the Category Type */
            EventCategoryType myCatType = myData.getEventCategoryTypes().findItemById(uCatType);
            if (myCatType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Transaction Type Id");
            }
            setValueCategoryType(myCatType);

            /* Record the encrypted values */
            setValueDesc(pDesc);
            setValueAmount(pAmount);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param uId the id
     * @param pDate the date
     * @param pDesc the description
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pCatType the category type
     * @param pAmount the amount
     * @throws JDataException on error
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList,
                        final Integer uId,
                        final Date pDate,
                        final String pDesc,
                        final Account pDebit,
                        final Account pCredit,
                        final EventCategoryType pCatType,
                        final String pAmount) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access the parser */
            FinanceData myDataSet = getDataSet();
            JDataFormatter myFormatter = myDataSet.getDataFormatter();
            JDecimalParser myParser = myFormatter.getDecimalParser();

            /* Record the standard values */
            setValueDesc(pDesc);
            setValueDebit(pDebit);
            setValueCredit(pCredit);
            setValueCategoryType(pCatType);
            setValueDate(new JDateDay(pDate));
            setValueAmount(myParser.parseMoneyValue(pAmount));

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
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

        /* If the category types differ */
        iDiff = Difference.compareObject(getCategoryType(), pThat.getCategoryType());
        if (iDiff != 0) {
            return iDiff;
        }

        /* If the descriptions differ */
        iDiff = Difference.compareObject(getDesc(), pThat.getDesc());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare the underlying id */
        return super.compareId(pThat);
    }

    @Override
    public void relinkToDataSet() {
        /* Update the Encryption details */
        super.relinkToDataSet();

        /* Access Lists */
        FinanceData myData = getDataSet();
        AccountList myAccounts = myData.getAccounts();
        EventCategoryTypeList myCatTypes = myData.getEventCategoryTypes();

        /* Update credit to use the local copy of the Accounts */
        Account myAct = getCredit();
        Account myNewAct = myAccounts.findItemById(myAct.getId());
        setValueCredit(myNewAct);

        /* Update debit to use the local copy of the Accounts */
        myAct = getDebit();
        myNewAct = myAccounts.findItemById(myAct.getId());
        setValueDebit(myNewAct);

        /* Update categoryType to use the local copy */
        EventCategoryType myCat = getCategoryType();
        EventCategoryType myNewCat = myCatTypes.findItemById(myCat.getId());
        setValueCategoryType(myNewCat);
    }

    /**
     * Determines whether an event can be valid.
     * @param pCategory The category type of the event
     * @param pType The account type of the event
     * @param pCredit is the account a credit or a debit
     * @return valid true/false
     */
    public static boolean isValidEvent(final EventCategoryType pCategory,
                                       final AccountCategoryType pType,
                                       final boolean pCredit) {
        boolean myResult = false;
        boolean isCredit = pCredit;

        /* Market is always false */
        if (pType.isMarket()) {
            return false;
        }

        /* Switch on the CategoryType */
        switch (pCategory.getCategoryClass()) {
            case TaxFreeIncome:
                if (!isCredit) {
                    myResult = (pType.isExternal() && !pType.isCash());
                } else {
                    myResult = !pType.isExternal();
                }
                break;
            case TaxableGain:
                if (!isCredit) {
                    myResult = pType.isLifeBond();
                } else {
                    myResult = pType.isMoney();
                }
                break;
            case AdminCharge:
                myResult = pType.isLifeBond();
                break;
            case Dividend:
                if (!isCredit) {
                    myResult = pType.isDividend();
                } else {
                    myResult = (pType.isMoney()
                                || pType.isCapital() || pType.isDeferred());
                }
                break;
            case StockDeMerger:
            case StockSplit:
            case StockTakeOver:
                myResult = pType.isShares();
                break;
            case StockRightsWaived:
            case CashTakeOver:
                isCredit = !isCredit;
            case StockRightsTaken:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDeferred());
                } else {
                    myResult = pType.isShares();
                }
                break;
            case Interest:
                if (!isCredit) {
                    myResult = pType.isMoney();
                } else {
                    myResult = pType.isMoney();
                }
                break;
            case TaxedIncome:
                if (!isCredit) {
                    myResult = pType.isEmployer();
                } else {
                    myResult = ((pType.isMoney()) || (pType.isDeferred()));
                }
                break;
            case NatInsurance:
                if (!isCredit) {
                    myResult = pType.isEmployer();
                } else {
                    myResult = pType.isTaxMan();
                }
                break;
            case Transfer:
                myResult = !pType.isExternal();
                if (isCredit) {
                    myResult &= !pType.isEndowment();
                }
                break;
            case Endowment:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDebt());
                } else {
                    myResult = pType.isEndowment();
                }
                break;
            case CashPayment:
                isCredit = !isCredit;
            case CashRecovery:
                if (!isCredit) {
                    myResult = ((pType.isExternal()) && (!pType.isCash()));
                } else {
                    myResult = pType.isCash();
                }
                break;
            case Inherited:
                if (!isCredit) {
                    myResult = pType.isInheritance();
                } else {
                    myResult = !pType.isExternal();
                }
                break;
            case Benefit:
                if (!isCredit) {
                    myResult = pType.isEmployer();
                } else {
                    myResult = pType.isBenefit();
                }
                break;
            case Recovered:
                isCredit = !isCredit;
            case Expense:
                if (!isCredit) {
                    myResult = !pType.isExternal();
                } else {
                    myResult = pType.isExternal();
                }
                break;
            case ExtraTax:
            case Insurance:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDebt());
                } else {
                    myResult = (pType.isExternal() && !pType.isCash());
                }
                break;
            case Mortgage:
                if (!isCredit) {
                    myResult = pType.isDebt();
                } else {
                    myResult = (pType.isExternal() && !pType.isCash());
                }
                break;
            case TaxRefund:
                isCredit = !isCredit;
            case TaxOwed:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDeferred());
                } else {
                    myResult = pType.isTaxMan();
                }
                break;
            case TaxRelief:
                if (!isCredit) {
                    myResult = pType.isTaxMan();
                } else {
                    myResult = pType.isDebt();
                }
                break;
            case DebtInterest:
            case RentalIncome:
                if (!isCredit) {
                    myResult = (pType.isExternal() && !pType.isCash());
                } else {
                    myResult = pType.isDebt();
                }
                break;
            case WriteOff:
                if (!isCredit) {
                    myResult = pType.isDebt();
                } else {
                    myResult = pType.isWriteOff();
                }
                break;
            default:
                break;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Is an event allowed between these two accounts, used for more detailed analysis once the event is deemed valid based on the account types.
     * @param pCategory The category type of the event
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @return true/false
     */
    public static boolean isValidEvent(final EventCategoryType pCategory,
                                       final Account pDebit,
                                       final Account pCredit) {
        /* Generally we must not be recursive */
        boolean myResult = !Difference.isEqual(pDebit, pCredit);

        /* Switch on the CategoryType */
        switch (pCategory.getCategoryClass()) {
        /* Dividend */
            case Dividend:
                /* If the credit account is capital */
                if (pCredit.isCapital()) {
                    /* Debit and credit accounts must be identical */
                    myResult = !myResult;
                }
                break;
            /* AdminCharge/StockSplit */
            case AdminCharge:
            case StockSplit:
                /* Debit and credit accounts must be identical */
                myResult = !myResult;
                break;
            /* Interest can be recursive */
            case Interest:
                myResult = true;
                break;
            /* Debt Interest and Rental Income must come from the owner of the debt */
            case RentalIncome:
            case DebtInterest:
                myResult = Difference.isEqual(pDebit, pCredit.getParent());
                break;
            /* Mortgage payment must be to the owner of the mortgage */
            case Mortgage:
                myResult = Difference.isEqual(pCredit, pDebit.getParent());
                break;
            default:
                break;
        }

        /* Return the result */
        return myResult;
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
     * Determines whether an event is asset related.
     * @return asset-related to the account true/false
     */
    public boolean isAssetRelated() {
        boolean myResult = false;

        /* Check credit and debit accounts */
        if (!getCredit().isExternal()) {
            myResult = true;
        } else if (!getDebit().isExternal()) {
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
        return (((myCredit != null) && (myCredit.isClosed())) || ((myDebit != null) && (myDebit.isClosed())));
    }

    /**
     * Determines whether an event is a dividend re-investment.
     * @return dividend re-investment true/false
     */
    public boolean isDividendReInvestment() {
        /* Check for dividend re-investment */
        if ((getCategoryType() != null)
            && (!getCategoryType().isDividend())) {
            return false;
        }
        return ((getCredit() != null) && (getCredit().isPriced()));
    }

    /**
     * Determines whether an event is an interest payment.
     * @return interest true/false
     */
    public boolean isInterest() {
        /* Check for interest */
        return ((getCategoryType() != null) && (getCategoryType().isInterest()));
    }

    /**
     * Determines whether an event is a stock split.
     * @return stock split true/false
     */
    public final boolean isStockSplit() {
        /* Check for stock split */
        return ((getCategoryType() != null) && (getCategoryType().isStockSplit()));
    }

    /**
     * Determines whether an event is an Admin Charge.
     * @return admin charge true/false
     */
    public final boolean isAdminCharge() {
        /* Check for Admin charge */
        return ((getCategoryType() != null) && (getCategoryType().isAdminCharge()));
    }

    /**
     * Determines whether an event needs a tax credit.
     * @param pCategory the category type
     * @param pDebit the debit account
     * @return needs tax credit true/false
     */
    public static boolean needsTaxCredit(final EventCategoryType pCategory,
                                         final Account pDebit) {
        /* Handle null categoryType */
        if (pCategory == null) {
            return false;
        }

        /* Switch on category type */
        switch (pCategory.getCategoryClass()) {
        /* If this is a Taxable Gain/TaxedIncome we need a tax credit */
            case TaxableGain:
            case TaxedIncome:
                return true;
                /* Check for dividend/interest */
            case Dividend:
            case Interest:
                return (pDebit != null)
                       && !pDebit.isTaxFree();
            default:
                return false;
        }
    }

    /**
     * Determines whether an event needs a dilution factor.
     * @param pCategory the category type
     * @return needs dilution factor true/false
     */
    public static boolean needsDilution(final EventCategoryType pCategory) {
        /* Handle null categoryType */
        if (pCategory == null) {
            return false;
        }

        /* Switch on category type */
        switch (pCategory.getCategoryClass()) {
        /* If this is a Stock Operation we need a dilution factor */
            case StockDeMerger:
            case StockRightsTaken:
            case StockRightsWaived:
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
     * Set a new categoryType.
     * @param pCategory the categoryType
     */
    public void setCategoryType(final EventCategoryType pCategory) {
        setValueCategoryType(pCategory);
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
     * Set a new amount.
     * @param pAmount the amount
     * @throws JDataException on error
     */
    public void setAmount(final JMoney pAmount) throws JDataException {
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

    /**
     * Mark active items.
     */
    protected void markActiveItems() {
        /* mark the category type referred to */
        getCategoryType().touchItem(this);

        /* Mark the credit and debit accounts */
        getDebit().touchItem(this);
        getCredit().touchItem(this);
    }

    /**
     * Validate the event.
     */
    @Override
    public void validate() {
        JDateDay myDate = getDate();
        String myDesc = getDesc();
        Account myDebit = getDebit();
        Account myCredit = getCredit();
        JMoney myAmount = getAmount();
        EventCategoryType myCategory = getCategoryType();

        /* Determine date range to check for */
        EventBaseList<?> myList = getList();
        JDateDayRange myRange = myList.getValidDateRange();

        /* The date must be non-null */
        if (myDate == null) {
            addError("Null date is not allowed", FIELD_DATE);

            /* The date must be in-range */
        } else if (myRange.compareTo(myDate) != 0) {
            addError("Date must be within range", FIELD_DATE);
        }

        /* CategoryType must be non-null */
        if (myCategory == null) {
            addError("CategoryType must be non-null", FIELD_CATTYP);
            /* Must be enabled */
        } else if (!myCategory.getEnabled()) {
            addError("CategoryType must be enabled", FIELD_CATTYP);
            /* Must not be hidden */
        } else if (myCategory.isHiddenType()) {
            addError("Hidden category types are not allowed", FIELD_CATTYP);
        }

        /* The description must be non-null */
        if (myDesc == null) {
            addError("Description must be non-null", FIELD_DESC);
            /* and not too long */
        } else if (myDesc.length() > DESCLEN) {
            addError("Description is too long", FIELD_DESC);
        }

        /* Credit account must be non-null */
        if (myCredit == null) {
            addError("Credit account must be non-null", FIELD_CREDIT);
            /* And valid for category type */
        } else if ((myCategory != null)
                   && (!isValidEvent(myCategory, myCredit.getActType(), true))) {
            addError("Invalid credit account for transaction", FIELD_CREDIT);
        }

        /* Debit account must be non-null */
        if (myDebit == null) {
            addError("Debit account must be non-null", FIELD_DEBIT);
            /* And valid for category type */
        } else if ((myCategory != null)
                   && (!isValidEvent(myCategory, myDebit.getActType(), false))) {
            addError("Invalid debit account for transaction", FIELD_DEBIT);
        }

        /* Check valid Credit/Debit combination */
        if ((myCategory != null)
            && (myCredit != null)
            && (myDebit != null)
            && (!isValidEvent(myCategory, myDebit, myCredit))) {
            addError("Invalid Debit/Credit combination account for transaction", FIELD_DEBIT);
            addError("Invalid Debit/Credit combination account for transaction", FIELD_CREDIT);
        }

        /* Money must not be null/negative */
        if (myAmount == null) {
            addError("Amount must be non-null", FIELD_AMOUNT);
        } else if (!myAmount.isPositive()) {
            addError("Amount cannot be negative", FIELD_AMOUNT);
        }

        /* Money must be zero for stock split/demerger */
        if ((myAmount != null)
            && (myAmount.isNonZero())
            && (myCategory != null)
            && ((myCategory.isStockDemerger())
                || (myCategory.isStockSplit()) || (myCategory.isStockTakeover()))) {
            addError("Amount must be zero for Stock Split/Demerger/Takeover", FIELD_AMOUNT);
        }
    }

    /**
     * Update base event from an edited event.
     * @param pEvent the edited event
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pEvent) {
        /* Can only update from an event */
        if (!(pEvent instanceof EventBase)) {
            return false;
        }

        EventBase myEvent = (EventBase) pEvent;

        /* Store the current detail into history */
        pushHistory();

        /* Update the Date if required */
        if (!Difference.isEqual(getDate(), myEvent.getDate())) {
            setValueDate(myEvent.getDate());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), myEvent.getDesc())) {
            setValueDesc(myEvent.getDescField());
        }

        /* Update the category type if required */
        if (!Difference.isEqual(getCategoryType(), myEvent.getCategoryType())) {
            setValueCategoryType(myEvent.getCategoryType());
        }

        /* Update the debit account if required */
        if (!Difference.isEqual(getDebit(), myEvent.getDebit())) {
            setValueDebit(myEvent.getDebit());
        }

        /* Update the credit account if required */
        if (!Difference.isEqual(getCredit(), myEvent.getCredit())) {
            setValueCredit(myEvent.getCredit());
        }

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), myEvent.getAmount())) {
            setValueAmount(myEvent.getAmountField());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * The Event List class.
     * @param <T> the dataType
     */
    public abstract static class EventBaseList<T extends EventBase>
            extends EncryptedList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventBaseList.class.getSimpleName(), DataList.FIELD_DEFS);

        /**
         * Range field id.
         */
        public static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField("Range");

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
        public FinanceData getDataSet() {
            return (FinanceData) super.getDataSet();
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
        protected EventBaseList(final FinanceData pData,
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
