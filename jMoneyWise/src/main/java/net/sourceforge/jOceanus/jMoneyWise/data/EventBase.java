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
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.EncryptedItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.jOceanus.jGordianKnot.EncryptedValueSet;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountNew.AccountNewList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType.TransTypeList;

/**
 * Event data type.
 * @author Tony Washer
 */
public abstract class EventBase extends EncryptedItem implements Comparable<EventBase> {
    /**
     * Event Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventBase.class.getSimpleName(),
            EncryptedItem.FIELD_DEFS);

    /**
     * Date Field Id.
     */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField("Date");

    /**
     * Description Field Id.
     */
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");

    /**
     * TransType Field Id.
     */
    public static final JDataField FIELD_TRNTYP = FIELD_DEFS.declareEqualityValueField("TransType");

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
     * Obtain transaction Type.
     * @return the tranType
     */
    public final TransactionType getTransType() {
        return getTransType(getValueSet());
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
    public AccountNew getDebit() {
        return getDebit(getValueSet());
    }

    /**
     * Obtain Credit account.
     * @return the credit
     */
    public AccountNew getCredit() {
        return getCredit(getValueSet());
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
     * Obtain Transaction type.
     * @param pValueSet the valueSet
     * @return the tranType
     */
    public static TransactionType getTransType(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TRNTYP, TransactionType.class);
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
    public static AccountNew getDebit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEBIT, AccountNew.class);
    }

    /**
     * Obtain Credit Account.
     * @param pValueSet the valueSet
     * @return the Credit Account
     */
    public static AccountNew getCredit(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CREDIT, AccountNew.class);
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
     * Set transType value.
     * @param pValue the value
     */
    private void setValueTransType(final TransactionType pValue) {
        getValueSet().setValue(FIELD_TRNTYP, pValue);
    }

    /**
     * Set transType id.
     * @param pId the id
     */
    private void setValueTransType(final Integer pId) {
        getValueSet().setValue(FIELD_TRNTYP, pId);
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
    private void setValueDebit(final AccountNew pValue) {
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
    private void setValueCredit(final AccountNew pValue) {
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
     * Construct a new event from an Account pattern.
     * @param pList the list to build into
     * @param pLine The Line to copy
     * @throws JDataException on error
     */
    // protected EventBase(final EncryptedList<? extends EventBase> pList,
    // final Pattern pLine) throws JDataException {
    /* Set standard values */
    // super(pList, pLine);

    /* Create a new EventInfoSet if required */
    // if (requiredInfoSet()) {
    // theInfoSet = new EventInfoSet(this);
    // }

    /* If we need a tax Credit */
    // if (needsTaxCredit(getTransType(), getDebit())) {
    /* Calculate the tax credit */
    // setTaxCredit(calculateTaxCredit());
    // }
    // }

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
     * @param uTransType the transType id
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
                        final Integer uTransType,
                        final byte[] pAmount) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access account list */
            FinanceData myData = getDataSet();
            AccountNewList myAccounts = myData.getNewAccounts();

            /* Store the IDs that we will look up */
            setValueDebit(uDebit);
            setValueCredit(uCredit);
            setValueTransType(uTransType);
            setControlKey(uControlId);

            /* Create the date */
            setValueDate(new JDateDay(pDate));

            /* Look up the Debit Account */
            AccountNew myAccount = myAccounts.findItemById(uDebit);
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

            /* Look up the Transaction Type */
            TransactionType myTransType = myData.getTransTypes().findItemById(uTransType);
            if (myTransType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Transaction Type Id");
            }
            setValueTransType(myTransType);

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
     * @param pTransType the transaction type
     * @param pAmount the amount
     * @throws JDataException on error
     */
    protected EventBase(final EventBaseList<? extends EventBase> pList,
                        final Integer uId,
                        final Date pDate,
                        final String pDesc,
                        final AccountNew pDebit,
                        final AccountNew pCredit,
                        final TransactionType pTransType,
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
            setValueTransType(pTransType);
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
     * @return (-1,0,1) depending of whether this object is before, equal, or after the passed object in the
     *         sort order
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
            return isHeader() ? -1 : 1;
        }

        /* If the dates differ */
        int iDiff = Difference.compareObject(getDate(), pThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* If the transaction types differ */
        iDiff = Difference.compareObject(getTransType(), pThat.getTransType());
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
    protected void relinkToDataSet() {
        /* Update the Encryption details */
        super.relinkToDataSet();

        /* Access Lists */
        FinanceData myData = getDataSet();
        AccountNewList myAccounts = myData.getNewAccounts();
        TransTypeList myTranTypes = myData.getTransTypes();

        /* Update credit to use the local copy of the Accounts */
        AccountNew myAct = getCredit();
        AccountNew myNewAct = myAccounts.findItemById(myAct.getId());
        setValueCredit(myNewAct);

        /* Update debit to use the local copy of the Accounts */
        myAct = getDebit();
        myNewAct = myAccounts.findItemById(myAct.getId());
        setValueDebit(myNewAct);

        /* Update transtype to use the local copy */
        TransactionType myTran = getTransType();
        TransactionType myNewTran = myTranTypes.findItemById(myTran.getId());
        setValueTransType(myNewTran);
    }

    /**
     * Determines whether an event can be valid.
     * @param pTrans The transaction type of the event
     * @param pType The account type of the event
     * @param pCredit is the account a credit or a debit
     * @return valid true/false
     */
    public static boolean isValidEvent(final TransactionType pTrans,
                                       final AccountType pType,
                                       final boolean pCredit) {
        boolean myResult = false;
        boolean isCredit = pCredit;

        /* Market is always false */
        if (pType.isMarket()) {
            return false;
        }

        /* Switch on the TransType */
        switch (pTrans.getTranClass()) {
            case TAXFREEINCOME:
                if (!isCredit) {
                    myResult = (pType.isExternal() && !pType.isCash());
                } else {
                    myResult = !pType.isExternal();
                }
                break;
            case TAXABLEGAIN:
                if (!isCredit) {
                    myResult = pType.isLifeBond();
                } else {
                    myResult = pType.isMoney();
                }
                break;
            case ADMINCHARGE:
                myResult = pType.isLifeBond();
                break;
            case DIVIDEND:
                if (!isCredit) {
                    myResult = pType.isDividend();
                } else {
                    myResult = (pType.isMoney() || pType.isCapital() || pType.isDeferred());
                }
                break;
            case STOCKDEMERGER:
            case STOCKSPLIT:
            case STOCKTAKEOVER:
                myResult = pType.isShares();
                break;
            case STOCKRIGHTWAIVED:
            case CASHTAKEOVER:
                isCredit = !isCredit;
            case STOCKRIGHTTAKEN:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDeferred());
                } else {
                    myResult = pType.isShares();
                }
                break;
            case INTEREST:
                if (!isCredit) {
                    myResult = pType.isMoney();
                } else {
                    myResult = pType.isMoney();
                }
                break;
            case TAXEDINCOME:
                if (!isCredit) {
                    myResult = pType.isEmployer();
                } else {
                    myResult = ((pType.isMoney()) || (pType.isDeferred()));
                }
                break;
            case NATINSURANCE:
                if (!isCredit) {
                    myResult = pType.isEmployer();
                } else {
                    myResult = pType.isTaxMan();
                }
                break;
            case TRANSFER:
                myResult = !pType.isExternal();
                if (isCredit) {
                    myResult &= !pType.isEndowment();
                }
                break;
            case ENDOWMENT:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDebt());
                } else {
                    myResult = pType.isEndowment();
                }
                break;
            case CASHPAYMENT:
                isCredit = !isCredit;
            case CASHRECOVERY:
                if (!isCredit) {
                    myResult = ((pType.isExternal()) && (!pType.isCash()));
                } else {
                    myResult = pType.isCash();
                }
                break;
            case INHERITED:
                if (!isCredit) {
                    myResult = pType.isInheritance();
                } else {
                    myResult = !pType.isExternal();
                }
                break;
            case BENEFIT:
                if (!isCredit) {
                    myResult = pType.isEmployer();
                } else {
                    myResult = pType.isBenefit();
                }
                break;
            case RECOVERED:
                isCredit = !isCredit;
            case EXPENSE:
                if (!isCredit) {
                    myResult = !pType.isExternal();
                } else {
                    myResult = pType.isExternal();
                }
                break;
            case EXTRATAX:
            case INSURANCE:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDebt());
                } else {
                    myResult = (pType.isExternal() && !pType.isCash());
                }
                break;
            case MORTGAGE:
                if (!isCredit) {
                    myResult = pType.isDebt();
                } else {
                    myResult = (pType.isExternal() && !pType.isCash());
                }
                break;
            case TAXREFUND:
                isCredit = !isCredit;
            case TAXOWED:
                if (!isCredit) {
                    myResult = (pType.isMoney() || pType.isDeferred());
                } else {
                    myResult = pType.isTaxMan();
                }
                break;
            case TAXRELIEF:
                if (!isCredit) {
                    myResult = pType.isTaxMan();
                } else {
                    myResult = pType.isDebt();
                }
                break;
            case DEBTINTEREST:
            case RENTALINCOME:
                if (!isCredit) {
                    myResult = (pType.isExternal() && !pType.isCash());
                } else {
                    myResult = pType.isDebt();
                }
                break;
            case WRITEOFF:
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
        AccountNew myCredit = getCredit();
        AccountNew myDebit = getDebit();

        /* Check credit and debit accounts */
        return (((myCredit != null) && (myCredit.isClosed())) || ((myDebit != null) && (myDebit.isClosed())));
    }

    /**
     * Determines whether an event is a dividend re-investment.
     * @return dividend re-investment true/false
     */
    public boolean isDividendReInvestment() {
        /* Check for dividend re-investment */
        if ((getTransType() != null) && (!getTransType().isDividend())) {
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
        return ((getTransType() != null) && (getTransType().isInterest()));
    }

    /**
     * Determines whether an event is a stock split.
     * @return stock split true/false
     */
    public final boolean isStockSplit() {
        /* Check for stock split */
        return ((getTransType() != null) && (getTransType().isStockSplit()));
    }

    /**
     * Determines whether an event is an Admin Charge.
     * @return admin charge true/false
     */
    public final boolean isAdminCharge() {
        /* Check for Admin charge */
        return ((getTransType() != null) && (getTransType().isAdminCharge()));
    }

    /**
     * Determines whether an event needs a tax credit.
     * @param pTrans the transaction type
     * @param pDebit the debit account
     * @return needs tax credit true/false
     */
    public static boolean needsTaxCredit(final TransactionType pTrans,
                                         final AccountNew pDebit) {
        /* Handle null transType */
        if (pTrans == null) {
            return false;
        }

        /* Switch on transaction type */
        switch (pTrans.getTranClass()) {
        /* If this is a Taxable Gain/TaxedIncome we need a tax credit */
            case TAXABLEGAIN:
            case TAXEDINCOME:
                return true;
                /* Check for dividend/interest */
            case DIVIDEND:
            case INTEREST:
                return (pDebit != null) && !pDebit.isTaxFree();
            default:
                return false;
        }
    }

    /**
     * Determines whether an event needs a dilution factor.
     * @param pTrans the transaction type
     * @return needs dilution factor true/false
     */
    public static boolean needsDilution(final TransactionType pTrans) {
        /* Handle null transType */
        if (pTrans == null) {
            return false;
        }

        /* Switch on transaction type */
        switch (pTrans.getTranClass()) {
        /* If this is a Stock Operation we need a dilution factor */
            case STOCKDEMERGER:
            case STOCKRIGHTTAKEN:
            case STOCKRIGHTWAIVED:
                return true;
            default:
                return false;
        }
    }

    /**
     * Set a new debit account.
     * @param pDebit the debit account
     */
    public void setDebit(final AccountNew pDebit) {
        setValueDebit(pDebit);
    }

    /**
     * Set a new credit account.
     * @param pCredit the credit account
     */
    public void setCredit(final AccountNew pCredit) {
        setValueCredit(pCredit);
    }

    /**
     * Set a new transType.
     * @param pTransType the transType
     */
    public void setTransType(final TransactionType pTransType) {
        setValueTransType(pTransType);
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
        setValueDate((pDate == null) ? null : new JDateDay(pDate));
    }

    /**
     * The Event List class.
     * @param <T> the dataType
     */
    public abstract static class EventBaseList<T extends EventBase> extends EncryptedList<T> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventBaseList.class.getSimpleName(),
                DataList.FIELD_DEFS);

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
