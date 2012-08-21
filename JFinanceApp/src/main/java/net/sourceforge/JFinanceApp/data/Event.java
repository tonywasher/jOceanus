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

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataFormatter;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDataModels.data.DataItem;
import net.sourceforge.JDataModels.data.DataList;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.data.EncryptedItem;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDateDay.JDateDayRange;
import net.sourceforge.JDecimal.JDecimalParser;
import net.sourceforge.JDecimal.JDilution;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JDecimal.JRate;
import net.sourceforge.JDecimal.JUnits;
import net.sourceforge.JFinanceApp.data.Account.AccountList;
import net.sourceforge.JFinanceApp.data.Pattern.PatternList;
import net.sourceforge.JFinanceApp.data.TaxYear.TaxYearList;
import net.sourceforge.JFinanceApp.data.statics.AccountType;
import net.sourceforge.JFinanceApp.data.statics.EventInfoClass;
import net.sourceforge.JFinanceApp.data.statics.TransactionType;
import net.sourceforge.JFinanceApp.data.statics.TransactionType.TransTypeList;
import net.sourceforge.JFinanceApp.views.Statement.StatementLine;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDilution;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedUnits;
import net.sourceforge.JGordianKnot.EncryptedValueSet;

/**
 * Event data type.
 * @author Tony Washer
 */
public class Event extends EncryptedItem implements Comparable<Event> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = Event.class.getSimpleName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Event Description length.
     */
    public static final int DESCLEN = 50;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

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
     * Units Field Id.
     */
    public static final JDataField FIELD_UNITS = FIELD_DEFS.declareEqualityValueField("Units");

    /**
     * TaxCredit Field Id.
     */
    public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareEqualityValueField("TaxCredit");

    /**
     * Dilution Field Id.
     */
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityValueField("Dilution");

    /**
     * Years Field Id.
     */
    public static final JDataField FIELD_YEARS = FIELD_DEFS.declareEqualityValueField("Years");

    /**
     * InfoSet Field Id.
     */
    public static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField("InfoSet");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_INFOSET.equals(pField)) {
            return requiredInfoSet() ? theInfoSet : JDataFieldValue.SkipField;
        }
        return super.getFieldValue(pField);
    }

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
    public Account getDebit() {
        return getDebit(getValueSet());
    }

    /**
     * Obtain Credit account.
     * @return the credit
     */
    public Account getCredit() {
        return getCredit(getValueSet());
    }

    /**
     * Obtain Units.
     * @return the units
     */
    public JUnits getUnits() {
        return getUnits(getValueSet());
    }

    /**
     * Obtain encrypted units.
     * @return the bytes
     */
    public byte[] getUnitsBytes() {
        return getUnitsBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Units Field.
     * @return the Field
     */
    private EncryptedUnits getUnitsField() {
        return getUnitsField(getValueSet());
    }

    /**
     * Obtain Tax Credit.
     * @return the tax credit
     */
    public JMoney getTaxCredit() {
        return getTaxCredit(getValueSet());
    }

    /**
     * Obtain Encrypted tax credit.
     * @return the bytes
     */
    public byte[] getTaxCreditBytes() {
        return getTaxCreditBytes(getValueSet());
    }

    /**
     * Obtain Encrypted TaxCredit Field.
     * @return the Field
     */
    private EncryptedMoney getTaxCreditField() {
        return getTaxCreditField(getValueSet());
    }

    /**
     * Obtain Dilution.
     * @return the dilution
     */
    public JDilution getDilution() {
        return getDilution(getValueSet());
    }

    /**
     * Obtain Encrypted dilution.
     * @return the bytes
     */
    public byte[] getDilutionBytes() {
        return getDilutionBytes(getValueSet());
    }

    /**
     * Obtain Encrypted Dilution Field.
     * @return the Field
     */
    private EncryptedDilution getDilutionField() {
        return getDilutionField(getValueSet());
    }

    /**
     * Obtain Qualifying years.
     * @return the years
     */
    public Integer getYears() {
        return getYears(getValueSet());
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
     * Obtain Units.
     * @param pValueSet the valueSet
     * @return the Units
     */
    public static JUnits getUnits(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_UNITS, JUnits.class);
    }

    /**
     * Obtain Encrypted Units.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getUnitsBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_UNITS);
    }

    /**
     * Obtain Encrypted Units field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedUnits getUnitsField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_UNITS, EncryptedUnits.class);
    }

    /**
     * Obtain Tax Credit.
     * @param pValueSet the valueSet
     * @return the Account
     */
    public static JMoney getTaxCredit(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_TAXCREDIT, JMoney.class);
    }

    /**
     * Obtain Encrypted TaxCredit.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getTaxCreditBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_TAXCREDIT);
    }

    /**
     * Obtain Encrypted taxCredit field.
     * @param pValueSet the valueSet
     * @return the Field
     */
    private static EncryptedMoney getTaxCreditField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXCREDIT, EncryptedMoney.class);
    }

    /**
     * Obtain Dilution.
     * @param pValueSet the valueSet
     * @return the Dilution
     */
    public static JDilution getDilution(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DILUTION, JDilution.class);
    }

    /**
     * Obtain Encrypted dilution.
     * @param pValueSet the valueSet
     * @return the bytes
     */
    public static byte[] getDilutionBytes(final EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DILUTION);
    }

    /**
     * Obtain Encrypted dilution field.
     * @param pValueSet the valueSet
     * @return the field
     */
    private static EncryptedDilution getDilutionField(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTION, EncryptedDilution.class);
    }

    /**
     * Obtain Years.
     * @param pValueSet the valueSet
     * @return the Years
     */
    public static Integer getYears(final ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_YEARS, Integer.class);
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
    private void setValueDebit(final Account pValue) {
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
    private void setValueCredit(final Account pValue) {
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
     * Set units value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueUnits(final JUnits pValue) throws JDataException {
        setEncryptedValue(FIELD_UNITS, pValue);
    }

    /**
     * Set units value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueUnits(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_UNITS, pBytes, JUnits.class);
    }

    /**
     * Set units value.
     * @param pValue the value
     */
    private void setValueUnits(final EncryptedUnits pValue) {
        getValueSet().setValue(FIELD_UNITS, pValue);
    }

    /**
     * Set taxCredit value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueTaxCredit(final JMoney pValue) throws JDataException {
        setEncryptedValue(FIELD_TAXCREDIT, pValue);
    }

    /**
     * Set tax Credit value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueTaxCredit(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_TAXCREDIT, pBytes, JMoney.class);
    }

    /**
     * Set taxCredit value.
     * @param pValue the value
     */
    private void setValueTaxCredit(final EncryptedMoney pValue) {
        getValueSet().setValue(FIELD_TAXCREDIT, pValue);
    }

    /**
     * Set dilution value.
     * @param pValue the value
     * @throws JDataException on error
     */
    private void setValueDilution(final JDilution pValue) throws JDataException {
        setEncryptedValue(FIELD_DILUTION, pValue);
    }

    /**
     * Set dilution value.
     * @param pBytes the value
     * @throws JDataException on error
     */
    private void setValueDilution(final byte[] pBytes) throws JDataException {
        setEncryptedValue(FIELD_DILUTION, pBytes, JDilution.class);
    }

    /**
     * Set dilution value.
     * @param pValue the value
     */
    private void setValueDilution(final EncryptedDilution pValue) {
        getValueSet().setValue(FIELD_DILUTION, pValue);
    }

    /**
     * Set years value.
     * @param pValue the value
     */
    private void setValueYears(final Integer pValue) {
        getValueSet().setValue(FIELD_YEARS, pValue);
    }

    /**
     * Event Info Set.
     */
    private EventInfoSet theInfoSet = null;

    /**
     * Obtain infoSet.
     * @return the infoSet
     */
    public EventInfoSet getInfoSet() {
        return theInfoSet;
    }

    @Override
    public final FinanceData getDataSet() {
        return (FinanceData) super.getDataSet();
    }

    @Override
    public Event getBase() {
        return (Event) super.getBase();
    }

    /**
     * Does this class require an EvenInfoSet?
     * @return true/false
     */
    protected boolean requiredInfoSet() {
        return true;
    }

    @Override
    protected void addError(final String pError,
                            final JDataField pField) {
        super.addError(pError, pField);
    }

    /**
     * Construct a copy of an Event.
     * @param pList the event list
     * @param pEvent The Event to copy
     */
    public Event(final EncryptedList<? extends Event> pList,
                 final Event pEvent) {
        /* Set standard values */
        super(pList, pEvent);

        /* Create a new infoSet if required */
        if (requiredInfoSet()) {
            theInfoSet = new EventInfoSet(this);
        }
    }

    /**
     * Construct a new event from an Account pattern.
     * @param pList the list to build into
     * @param pLine The Line to copy
     * @throws JDataException on error
     */
    protected Event(final EncryptedList<? extends Event> pList,
                    final Pattern pLine) throws JDataException {
        /* Set standard values */
        super(pList, pLine);

        /* Create a new EventInfoSet if required */
        if (requiredInfoSet()) {
            theInfoSet = new EventInfoSet(this);
        }

        /* If we need a tax Credit */
        if (needsTaxCredit(getTransType(), getDebit())) {
            /* Calculate the tax credit */
            setTaxCredit(calculateTaxCredit());
        }
    }

    /**
     * Standard constructor for a newly inserted event.
     * @param pList the list
     */
    public Event(final EncryptedList<? extends Event> pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());

        /* Create a new EventInfoSet if required */
        if (requiredInfoSet()) {
            theInfoSet = new EventInfoSet(this);
        }
    }

    /**
     * constructor for load from encrypted.
     * @param pList the list
     * @param uId the id
     * @param uControlId the controlId
     * @param pDate the date
     * @param pDesc the description
     * @param uDebit the debit id
     * @param uCredit the credit id
     * @param uTransType the transType id
     * @param pAmount the amount
     * @param pUnits the units
     * @param pTaxCredit the tax credit
     * @param pDilution the dilution
     * @param pYears the years
     * @throws JDataException on error
     */
    protected Event(final EncryptedList<? extends Event> pList,
                    final int uId,
                    final int uControlId,
                    final Date pDate,
                    final byte[] pDesc,
                    final int uDebit,
                    final int uCredit,
                    final int uTransType,
                    final byte[] pAmount,
                    final byte[] pUnits,
                    final byte[] pTaxCredit,
                    final byte[] pDilution,
                    final Integer pYears) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Access account list */
            FinanceData myData = getDataSet();
            AccountList myAccounts = myData.getAccounts();

            /* Create a new EventInfoSet if required */
            if (requiredInfoSet()) {
                theInfoSet = new EventInfoSet(this);
            }

            /* Store the IDs that we will look up */
            setValueDebit(uDebit);
            setValueCredit(uCredit);
            setValueTransType(uTransType);
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

            /* Look up the Transaction Type */
            TransactionType myTransType = myData.getTransTypes().findItemById(uTransType);
            if (myTransType == null) {
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Transaction Type Id");
            }
            setValueTransType(myTransType);

            /* Set the years */
            setValueYears(pYears);

            /* Record the encrypted values */
            setValueDesc(pDesc);
            setValueAmount(pAmount);
            setValueUnits(pUnits);
            setValueTaxCredit(pTaxCredit);
            setValueDilution(pDilution);

            /* Catch Exceptions */
        } catch (JDataException e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /**
     * Standard constructor.
     * @param pList the list
     * @param uId the id
     * @param pDate the date
     * @param pDesc the description
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pTransType the transaction type
     * @param pAmount the amount
     * @param pUnits the units
     * @param pTaxCredit the tax credit
     * @param pDilution the dilution
     * @param pYears the years
     * @throws JDataException on error
     */
    protected Event(final EncryptedList<? extends Event> pList,
                    final int uId,
                    final Date pDate,
                    final String pDesc,
                    final Account pDebit,
                    final Account pCredit,
                    final TransactionType pTransType,
                    final String pAmount,
                    final String pUnits,
                    final String pTaxCredit,
                    final String pDilution,
                    final Integer pYears) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Create a new EventInfoSet if required */
            if (requiredInfoSet()) {
                theInfoSet = new EventInfoSet(this);
            }

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

            /* If Units exist */
            if (pUnits != null) {
                /* Create the data */
                JUnits myUnits = myParser.parseUnitsValue(pUnits);
                JUnits myValue = myUnits;
                setValueUnits(myUnits);
                boolean isCredit = pCredit.isPriced();
                if ((isStockSplit() || isAdminCharge()) && (!myUnits.isPositive())) {
                    myValue = new JUnits(myValue);
                    myValue.negate();
                    isCredit = false;
                }
                EventData myData = theInfoSet.getNewData(isCredit
                                                                 ? EventInfoClass.CreditUnits
                                                                 : EventInfoClass.DebitUnits);
                myData.setUnits(myValue);
            }

            /* If TaxCredit exist */
            if (pTaxCredit != null) {
                /* Create the data */
                JMoney myTaxCredit = myParser.parseMoneyValue(pTaxCredit);
                setValueTaxCredit(myTaxCredit);

                /* Create the data */
                EventData myData = theInfoSet.getNewData(EventInfoClass.TaxCredit);
                myData.setMoney(myTaxCredit);
            }

            /* If Dilution exist */
            if (pDilution != null) {
                /* Create the data */
                JDilution myDilution = myParser.parseDilutionValue(pDilution);
                setValueDilution(myDilution);

                /* Create the data */
                EventData myData = theInfoSet.getNewData(EventInfoClass.Dilution);
                myData.setDilution(myDilution);
            }

            /* If Years exist */
            if (pYears != null) {
                /* Record the value */
                setValueYears(pYears);

                /* Create the value */
                EventValue myValue = theInfoSet.getNewValue(EventInfoClass.QualifyYears);
                myValue.setValue(pYears);
            }
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
    public int compareTo(final Event pThat) {
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
        AccountList myAccounts = myData.getAccounts();
        TransTypeList myTranTypes = myData.getTransTypes();

        /* Update credit to use the local copy of the Accounts */
        Account myAct = getCredit();
        Account myNewAct = myAccounts.findItemById(myAct.getId());
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
     * Is an event allowed between these two accounts, used for more detailed analysis once the event is
     * deemed valid based on the account types.
     * @param pTrans The transaction type of the event
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @return true/false
     */
    public static boolean isValidEvent(final TransactionType pTrans,
                                       final Account pDebit,
                                       final Account pCredit) {
        /* Generally we must not be recursive */
        boolean myResult = !Difference.isEqual(pDebit, pCredit);

        /* Switch on the TransType */
        switch (pTrans.getTranClass()) {
        /* Dividend */
            case DIVIDEND:
                /* If the credit account is capital */
                if (pCredit.isCapital()) {
                    /* Debit and credit accounts must be identical */
                    myResult = !myResult;
                }
                break;
            /* AdminCharge/StockSplit */
            case ADMINCHARGE:
            case STOCKSPLIT:
                /* Debit and credit accounts must be identical */
                myResult = !myResult;
                break;
            /* Interest can be recursive */
            case INTEREST:
                myResult = true;
                break;
            /* Debt Interest and Rental Income must come from the owner of the debt */
            case RENTALINCOME:
            case DEBTINTEREST:
                myResult = Difference.isEqual(pDebit, pCredit.getParent());
                break;
            /* Mortgage payment must be to the owner of the mortgage */
            case MORTGAGE:
                myResult = Difference.isEqual(pCredit, pDebit.getParent());
                break;
            default:
                break;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * EventDateRange interface.
     */
    public interface EventDateRange {
        /**
         * Obtain valid date range.
         * @return the valid date range.
         */
        JDateDayRange getValidDateRange();
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
        TransactionType myTransType = getTransType();
        JUnits myUnits = getUnits();
        JMoney myTaxCred = getTaxCredit();
        Integer myYears = getYears();
        JDilution myDilution = getDilution();

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Determine date range to check for */
        DataList<?> myList = getList();
        JDateDayRange myRange;
        if (myList instanceof EventDateRange) {
            /* Access valid range */
            EventDateRange myValid = (EventDateRange) myList;
            myRange = myValid.getValidDateRange();
        } else {
            /* Use default range */
            myRange = getDataSet().getDateRange();
        }

        /* The date must be non-null */
        if (myDate == null) {
            addError("Null date is not allowed", FIELD_DATE);

            /* The date must be in-range */
        } else if (myRange.compareTo(myDate) != 0) {
            addError("Date must be within range", FIELD_DATE);
        }

        /* TransType must be non-null */
        if (myTransType == null) {
            addError("TransType must be non-null", FIELD_TRNTYP);
            /* Must be enabled */
        } else if (!myTransType.getEnabled()) {
            addError("TransType must be enabled", FIELD_TRNTYP);
            /* Must not be hidden */
        } else if (myTransType.isHiddenType()) {
            addError("Hidden transaction types are not allowed", FIELD_TRNTYP);
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
            /* And valid for transaction type */
        } else if ((myTransType != null) && (!isValidEvent(myTransType, myCredit.getActType(), true))) {
            addError("Invalid credit account for transaction", FIELD_CREDIT);
        }

        /* Debit account must be non-null */
        if (myDebit == null) {
            addError("Debit account must be non-null", FIELD_DEBIT);
            /* And valid for transaction type */
        } else if ((myTransType != null) && (!isValidEvent(myTransType, myDebit.getActType(), false))) {
            addError("Invalid debit account for transaction", FIELD_DEBIT);
        }

        /* Check valid Credit/Debit combination */
        if ((myTransType != null) && (myCredit != null) && (myDebit != null)
                && (!isValidEvent(myTransType, myDebit, myCredit))) {
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
                && (myTransType != null)
                && ((myTransType.isStockDemerger()) || (myTransType.isStockSplit()) || (myTransType
                        .isStockTakeover()))) {
            addError("Amount must be zero for Stock Split/Demerger/Takeover", FIELD_AMOUNT);
        }

        /* Ignore remaining checks for Patterns */
        if (!(this instanceof Pattern)) {
            /* Check for valid priced credit account */
            if ((myCredit != null) && (myCredit.isPriced())) {
                /* If the date of this event is prior to the first price */
                AccountPrice myPrice = myCredit.getInitPrice();
                if ((myPrice != null) && (getDate().compareTo(myPrice.getDate()) < 0)) {
                    addError("Event Date is prior to first priced date for Credit Account", FIELD_DATE);
                }
            }

            /* Check for valid priced debit account */
            if ((myDebit != null) && (myDebit.isPriced()) && (!Difference.isEqual(myCredit, myDebit))) {
                /* If the date of this event is prior to the first price */
                AccountPrice myPrice = myDebit.getInitPrice();
                if ((myPrice != null) && (getDate().compareTo(myPrice.getDate()) < 0)) {
                    addError("Event Date is prior to first priced date for Debit Account", FIELD_DATE);
                }
            }

            /* If we have units */
            if (myUnits != null) {
                /* If we have credit/debit accounts */
                if ((myDebit != null) && (myCredit != null)) {
                    /* Units are only allowed if credit or debit is priced */
                    if ((!myCredit.isPriced()) && (!myDebit.isPriced())) {
                        addError("Units are only allowed involving assets", FIELD_UNITS);
                    }

                    /* If both credit/debit are both priced */
                    if ((myCredit.isPriced()) && (myDebit.isPriced())) {
                        /* TranType must be stock split or dividend between same account */
                        if ((myTransType == null)
                                || ((!myTransType.isDividend()) && (!myTransType.isStockSplit())
                                        && (!myTransType.isAdminCharge()) && (!myTransType.isStockDemerger()) && (!myTransType
                                            .isStockTakeover()))) {
                            addError("Units can only refer to a single priced asset unless "
                                             + "transaction is StockSplit/AdminCharge/Demerger/Takeover or Dividend",
                                     FIELD_UNITS);
                        }

                        /* Dividend between priced requires identical credit/debit */
                        if ((myTransType != null) && (myTransType.isDividend())
                                && (!Difference.isEqual(myCredit, myDebit))) {
                            addError("Unit Dividends between assets must be between same asset", FIELD_UNITS);
                        }
                    }
                }

                /* Units must be non-zero */
                if (!myUnits.isNonZero()) {
                    addError("Units must be non-Zero", FIELD_UNITS);
                }

                /* Units must not be negative unless it is stock split */
                if ((!myUnits.isPositive())
                        && ((myTransType == null) || ((!myTransType.isStockSplit()) && (!myTransType
                                .isAdminCharge())))) {
                    addError("Units must be positive unless this is a StockSplit/AdminCharge", FIELD_UNITS);
                }

                /* Else check for required units */
            } else {
                if (isStockSplit()) {
                    addError("Stock Split requires non-zero Units", FIELD_UNITS);
                } else if (isAdminCharge()) {
                    addError("Admin Charge requires non-zero Units", FIELD_UNITS);
                }
            }

            /* If we have a dilution */
            if (myDilution != null) {
                /* If the dilution is not allowed */
                if ((!needsDilution(myTransType)) && (!myTransType.isStockSplit())) {
                    addError("Dilution factor given where not allowed", FIELD_DILUTION);
                }

                /* If the dilution is out of range */
                if (myDilution.outOfRange()) {
                    addError("Dilution factor value is outside allowed range (0-1)", FIELD_DILUTION);
                }

                /* else if we are missing a required dilution factor */
            } else if (needsDilution(myTransType)) {
                addError("Dilution factor missing where required", FIELD_DILUTION);
            }

            /* If we are a taxable gain */
            if ((myTransType != null) && (myTransType.isTaxableGain())) {
                /* Years must be positive */
                if ((myYears == null) || (myYears <= 0)) {
                    addError("Years must be non-zero and positive", FIELD_YEARS);
                }

                /* Tax Credit must be non-null and positive */
                if ((myTaxCred == null) || (!myTaxCred.isPositive())) {
                    addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
                }

                /* If we need a tax credit */
            } else if ((myTransType != null) && (needsTaxCredit(myTransType, myDebit))) {
                /* Tax Credit must be non-null and positive */
                if ((myTaxCred == null) || (!myTaxCred.isPositive())) {
                    addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
                }

                /* Years must be null */
                if (myYears != null) {
                    addError("Years must be null", FIELD_YEARS);
                }

                /* else we should not have a tax credit */
            } else if (myTransType != null) {
                /* Tax Credit must be null */
                if (myTaxCred != null) {
                    addError("TaxCredit must be null", FIELD_TAXCREDIT);
                }

                /* Years must be null */
                if (myYears != null) {
                    addError("Years must be null", FIELD_YEARS);
                }
            }
        }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
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
                                         final Account pDebit) {
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
     * Calculate the tax credit for an event.
     * @return the calculated tax credit
     */
    public JMoney calculateTaxCredit() {
        FinanceData myData = getDataSet();
        TaxYearList myList = myData.getTaxYears();

        /* Ignore unless tax credit is null/zero */
        if ((getTaxCredit() != null) && (getTaxCredit().isNonZero())) {
            return getTaxCredit();
        }

        /* Ignore unless transaction type is interest/dividend */
        if ((getTransType() == null) || ((!getTransType().isInterest()) && (!getTransType().isDividend()))) {
            return getTaxCredit();
        }

        /* Access the relevant tax year */
        TaxYear myTax = myList.findTaxYearForDate(getDate());

        /* Determine the tax credit rate */
        JRate myRate = (getTransType().isInterest()) ? myTax.getIntTaxRate() : myTax.getDivTaxRate();

        /* Calculate the tax credit */
        return getAmount().taxCreditAtRate(myRate);
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
     * Set a new units.
     * @param pUnits the units
     * @throws JDataException on error
     */
    public void setUnits(final JUnits pUnits) throws JDataException {
        setValueUnits(pUnits);
    }

    /**
     * Set a new date.
     * @param pDate the new date
     */
    public void setDate(final JDateDay pDate) {
        setValueDate((pDate == null) ? null : new JDateDay(pDate));
    }

    /**
     * Set a new tax credit amount.
     * @param pAmount the tax credit amount
     * @throws JDataException on error
     */
    public void setTaxCredit(final JMoney pAmount) throws JDataException {
        setValueTaxCredit(pAmount);
    }

    /**
     * Set a new years value.
     * @param pYears the years
     */
    public void setYears(final Integer pYears) {
        setValueYears(pYears);
    }

    /**
     * Set a new dilution value.
     * @param pDilution the dilution
     * @throws JDataException on error
     */
    public void setDilution(final JDilution pDilution) throws JDataException {
        setValueDilution(pDilution);
    }

    /**
     * Update event from an element.
     * @param pItem the changed element
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pItem) {
        boolean bChanged = false;
        if (pItem instanceof Event) {
            Event myEvent = (Event) pItem;
            bChanged = applyChanges(myEvent);
        }
        return bChanged;
    }

    /**
     * Update event from an Event extract.
     * @param pEvent the changed event
     * @return whether changes have been made
     */
    private boolean applyChanges(final Event pEvent) {
        /* Store the current detail into history */
        pushHistory();

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), pEvent.getDate())) {
            setDate(pEvent.getDate());
        }

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), pEvent.getDesc())) {
            setValueDesc(pEvent.getDescField());
        }

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), pEvent.getAmount())) {
            setValueAmount(pEvent.getAmountField());
        }

        /* Update the units if required */
        if (!Difference.isEqual(getUnits(), pEvent.getUnits())) {
            setValueUnits(pEvent.getUnitsField());
        }

        /* Update the tranType if required */
        if (!Difference.isEqual(getTransType(), pEvent.getTransType())) {
            setValueTransType(pEvent.getTransType());
        }

        /* Update the debit if required */
        if (!Difference.isEqual(getDebit(), pEvent.getDebit())) {
            setValueDebit(pEvent.getDebit());
        }

        /* Update the credit if required */
        if (!Difference.isEqual(getCredit(), pEvent.getCredit())) {
            setValueCredit(pEvent.getCredit());
        }

        /* Update the tax credit if required */
        if (!Difference.isEqual(getTaxCredit(), pEvent.getTaxCredit())) {
            setValueTaxCredit(pEvent.getTaxCreditField());
        }

        /* Update the dilution if required */
        if (!Difference.isEqual(getDilution(), pEvent.getDilution())) {
            setValueDilution(pEvent.getDilutionField());
        }

        /* Update the years if required */
        if (!Difference.isEqual(getYears(), pEvent.getYears())) {
            setValueYears(pEvent.getYears());
        }

        /* Check for changes */
        return checkForHistory();
    }

    /**
     * List class for Events.
     */
    public static class EventList extends EncryptedList<Event> implements EventDateRange {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        /**
         * Range field id.
         */
        public static final JDataField FIELD_RANGE = FIELD_DEFS.declareLocalField("Range");

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

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

        @Override
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
         * Construct an empty CORE event list.
         * @param pData the DataSet for the list
         */
        protected EventList(final FinanceData pData) {
            super(Event.class, pData);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventList(final EventList pSource) {
            super(pSource);
        }

        @Override
        protected EventList getEmptyList() {
            return new EventList(this);
        }

        @Override
        public EventList cloneList(final DataSet<?> pDataSet) {
            return (EventList) super.cloneList(pDataSet);
        }

        @Override
        public EventList deriveList(final ListStyle pStyle) {
            return (EventList) super.deriveList(pStyle);
        }

        @Override
        public EventList deriveDifferences(final DataList<Event> pOld) {
            return (EventList) super.deriveDifferences(pOld);
        }

        /**
         * Get an EditList for a range.
         * @return the edit list
         */
        public EventList getViewList() {
            /* Build an empty List */
            EventList myList = getEmptyList();
            myList.setStyle(ListStyle.VIEW);

            /* Return it */
            return myList;
        }

        /**
         * Get an EditList for a range.
         * @param pRange the range
         * @return the edit list
         */
        public EventList deriveEditList(final JDateDayRange pRange) {
            /* Build an empty List */
            EventList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);
            myList.theRange = pRange;

            /* Loop through the Events extracting relevant elements */
            Iterator<Event> myIterator = iterator();
            while (myIterator.hasNext()) {
                Event myCurr = myIterator.next();

                /* Check the range */
                int myResult = pRange.compareTo(myCurr.getDate());

                /* Handle out of range */
                if (myResult == 1) {
                    continue;
                } else if (myResult == -1) {
                    break;
                }

                /* Build the new linked event and add it to the extract */
                Event myEvent = new Event(myList, myCurr);
                myList.append(myEvent);
            }

            /* Return the List */
            return myList;
        }

        /**
         * Get an EditList for a new TaxYear.
         * @param pTaxYear the new TaxYear
         * @return the edit list
         * @throws JDataException on error
         */
        public EventList deriveEditList(final TaxYear pTaxYear) throws JDataException {
            /* Build an empty List */
            EventList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);
            myList.theRange = pTaxYear.getRange();

            /* Access the underlying data */
            PatternList myPatterns = getDataSet().getPatterns();
            Iterator<Pattern> myIterator = myPatterns.iterator();
            Event myEvent;

            /* Loop through the Patterns */
            while (myIterator.hasNext()) {
                Pattern myCurr = (Pattern) myIterator.next();

                /* Access a copy of the base date */
                JDateDay myDate = new JDateDay(myCurr.getDate());

                /* Loop while we have an event to add */
                while ((myEvent = myCurr.nextEvent(myList, pTaxYear, myDate)) != null) {
                    /* Add it to the extract */
                    myList.append(myEvent);
                }
            }

            /* Sort the list */
            myList.reSort();

            /* Return the List */
            return myList;
        }

        /**
         * Validate an extract.
         */
        @Override
        public void validate() {
            /* Clear the errors */
            clearErrors();

            /* Access the underlying data */
            Iterator<Event> myIterator = listIterator();

            /* Loop through the lines */
            while (myIterator.hasNext()) {
                Event myCurr = myIterator.next();
                /* Validate it */
                myCurr.validate();
            }
        }

        /**
         * Add a new item to the list.
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public Event addNewItem(final DataItem pItem) {
            if (pItem instanceof Event) {
                Event myEvent = new Event(this, (Event) pItem);
                add(myEvent);
                return myEvent;
            } else if (pItem instanceof StatementLine) {
                Event myEvent = new Event(this, (StatementLine) pItem);
                add(myEvent);
                return myEvent;
            }
            return null;
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public Event addNewItem() {
            /* Create a new Event */
            Event myEvent = new Event(this);

            /* Set the Date as the start of the range */
            myEvent.setDate(theRange.getStart());

            /* Add to list and return */
            add(myEvent);
            return myEvent;
        }

        /**
         * Allow an event to be added.
         * @param uId the id
         * @param pDate the date
         * @param pDesc the description
         * @param pAmount the amount
         * @param pDebit the debit account
         * @param pCredit the credit account
         * @param pUnits the units
         * @param pTransType the transaction type
         * @param pTaxCredit the tax credit
         * @param pDilution the dilution
         * @param pYears the years
         * @throws JDataException on error
         */
        public void addOpenItem(final int uId,
                                final Date pDate,
                                final String pDesc,
                                final String pAmount,
                                final String pDebit,
                                final String pCredit,
                                final String pUnits,
                                final String pTransType,
                                final String pTaxCredit,
                                final String pDilution,
                                final Integer pYears) throws JDataException {
            /* Access the accounts */
            FinanceData myData = getDataSet();
            JDataFormatter myFormatter = myData.getDataFormatter();
            AccountList myAccounts = myData.getAccounts();

            /* Look up the Transaction Type */
            TransactionType myTransType = myData.getTransTypes().findItemByName(pTransType);
            if (myTransType == null) {
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + myFormatter.formatObject(new JDateDay(pDate)) + "] has invalid Transact Type ["
                        + pTransType + "]");
            }

            /* Look up the Credit Account */
            Account myCredit = myAccounts.findItemByName(pCredit);
            if (myCredit == null) {
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + myFormatter.formatObject(new JDateDay(pDate)) + "] has invalid Credit account ["
                        + pCredit + "]");
            }

            /* Look up the Debit Account */
            Account myDebit = myAccounts.findItemByName(pDebit);
            if (myDebit == null) {
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + myFormatter.formatObject(new JDateDay(pDate)) + "] has invalid Debit account ["
                        + pDebit + "]");
            }

            /* Create the new Event */
            Event myEvent = new Event(this, uId, pDate, pDesc, myDebit, myCredit, myTransType, pAmount,
                    pUnits, pTaxCredit, pDilution, pYears);

            /* Validate the event */
            myEvent.validate();

            /* Handle validation failure */
            if (myEvent.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myEvent, "Failed validation");
            }

            /* Add the Event to the list */
            append(myEvent);
        }

        /**
         * Allow an event to be added.
         * @param uId the id
         * @param uControlId the control Id
         * @param pDate the date
         * @param pDesc the description
         * @param pAmount the amount
         * @param uDebitId the debit id
         * @param uCreditId the credit id
         * @param pUnits the units
         * @param uTransId the transaction id
         * @param pTaxCredit the tax credit
         * @param pDilution the dilution
         * @param pYears the years
         * @throws JDataException on error
         */
        public void addSecureItem(final int uId,
                                  final int uControlId,
                                  final Date pDate,
                                  final byte[] pDesc,
                                  final byte[] pAmount,
                                  final int uDebitId,
                                  final int uCreditId,
                                  final byte[] pUnits,
                                  final int uTransId,
                                  final byte[] pTaxCredit,
                                  final byte[] pDilution,
                                  final Integer pYears) throws JDataException {
            /* Create the new Event */
            Event myEvent = new Event(this, uId, uControlId, pDate, pDesc, uDebitId, uCreditId, uTransId,
                    pAmount, pUnits, pTaxCredit, pDilution, pYears);

            /* Check that this EventId has not been previously added */
            if (!isIdUnique(uId)) {
                throw new JDataException(ExceptionClass.DATA, myEvent, "Duplicate EventId");
            }

            /* Validate the event */
            myEvent.validate();

            /* Handle validation failure */
            if (myEvent.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myEvent, "Failed validation");
            }

            /* Add the Event to the list */
            append(myEvent);
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }
    }
}
