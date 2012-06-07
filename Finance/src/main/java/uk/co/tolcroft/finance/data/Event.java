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
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDataManager.ValueSet;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayRange;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Rate;
import net.sourceforge.JDecimal.Units;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedDilution;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedMoney;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedString;
import net.sourceforge.JGordianKnot.EncryptedData.EncryptedUnits;
import net.sourceforge.JGordianKnot.EncryptedValueSet;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.Pattern.PatternList;
import uk.co.tolcroft.finance.data.StaticClass.EventInfoClass;
import uk.co.tolcroft.finance.views.Statement;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList.ListStyle;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EncryptedItem;

public class Event extends EncryptedItem<Event> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = "Event";

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Event Description length
     */
    public final static int DESCLEN = 50;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EncryptedItem.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /* Field IDs */
    public static final JDataField FIELD_DATE = FIELD_DEFS.declareEqualityValueField("Date");
    public static final JDataField FIELD_DESC = FIELD_DEFS.declareEqualityValueField("Description");
    public static final JDataField FIELD_TRNTYP = FIELD_DEFS.declareEqualityValueField("TransType");
    public static final JDataField FIELD_AMOUNT = FIELD_DEFS.declareEqualityValueField("Date");
    public static final JDataField FIELD_DEBIT = FIELD_DEFS.declareEqualityValueField("Debit");
    public static final JDataField FIELD_CREDIT = FIELD_DEFS.declareEqualityValueField("Credit");
    public static final JDataField FIELD_UNITS = FIELD_DEFS.declareEqualityValueField("Units");
    public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareEqualityValueField("TaxCredit");
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityValueField("Dilution");
    public static final JDataField FIELD_YEARS = FIELD_DEFS.declareEqualityValueField("Years");
    public static final JDataField FIELD_INFOSET = FIELD_DEFS.declareEqualityField("InfoSet");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (pField == FIELD_INFOSET) {
            return requiredInfoSet() ? theInfoSet : JDataObject.FIELD_SKIP;
        }
        return super.getFieldValue(pField);
    }

    /**
     * The active set of values
     */
    private EncryptedValueSet theValueSet;

    @Override
    public void declareValues(EncryptedValueSet pValues) {
        super.declareValues(pValues);
        theValueSet = pValues;
    }

    /* Access methods */
    public DateDay getDate() {
        return getDate(theValueSet);
    }

    public String getDesc() {
        return getDesc(theValueSet);
    }

    public byte[] getDescBytes() {
        return getDescBytes(theValueSet);
    }

    protected EncryptedString getDescField() {
        return getDescField(theValueSet);
    }

    public TransactionType getTransType() {
        return getTransType(theValueSet);
    }

    public Money getAmount() {
        return getAmount(theValueSet);
    }

    public byte[] getAmountBytes() {
        return getAmountBytes(theValueSet);
    }

    protected EncryptedMoney getAmountField() {
        return getAmountField(theValueSet);
    }

    public Account getDebit() {
        return getDebit(theValueSet);
    }

    public Account getCredit() {
        return getCredit(theValueSet);
    }

    public Units getUnits() {
        return getUnits(theValueSet);
    }

    public byte[] getUnitsBytes() {
        return getUnitsBytes(theValueSet);
    }

    private EncryptedUnits getUnitsField() {
        return getUnitsField(theValueSet);
    }

    public Money getTaxCredit() {
        return getTaxCredit(theValueSet);
    }

    public byte[] getTaxCreditBytes() {
        return getTaxCreditBytes(theValueSet);
    }

    private EncryptedMoney getTaxCreditField() {
        return getTaxCreditField(theValueSet);
    }

    public Dilution getDilution() {
        return getDilution(theValueSet);
    }

    public byte[] getDilutionBytes() {
        return getDilutionBytes(theValueSet);
    }

    private EncryptedDilution getDilutionField() {
        return getDilutionField(theValueSet);
    }

    public Integer getYears() {
        return getYears(theValueSet);
    }

    public static DateDay getDate(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DATE, DateDay.class);
    }

    public static String getDesc(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DESC, String.class);
    }

    public static byte[] getDescBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DESC);
    }

    private static EncryptedString getDescField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DESC, EncryptedString.class);
    }

    public static TransactionType getTransType(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TRNTYP, TransactionType.class);
    }

    public static Money getAmount(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_AMOUNT, Money.class);
    }

    public static byte[] getAmountBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_AMOUNT);
    }

    private static EncryptedMoney getAmountField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_AMOUNT, EncryptedMoney.class);
    }

    public static Account getDebit(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DEBIT, Account.class);
    }

    public static Account getCredit(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_CREDIT, Account.class);
    }

    public static Units getUnits(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_UNITS, Units.class);
    }

    public static byte[] getUnitsBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_UNITS);
    }

    private static EncryptedUnits getUnitsField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_UNITS, EncryptedUnits.class);
    }

    public static Money getTaxCredit(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_TAXCREDIT, Money.class);
    }

    public static byte[] getTaxCreditBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_TAXCREDIT);
    }

    private static EncryptedMoney getTaxCreditField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_TAXCREDIT, EncryptedMoney.class);
    }

    public static Dilution getDilution(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldValue(FIELD_DILUTION, Dilution.class);
    }

    public static byte[] getDilutionBytes(EncryptedValueSet pValueSet) {
        return pValueSet.getEncryptedFieldBytes(FIELD_DILUTION);
    }

    private static EncryptedDilution getDilutionField(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_DILUTION, EncryptedDilution.class);
    }

    public static Integer getYears(ValueSet pValueSet) {
        return pValueSet.getValue(FIELD_YEARS, Integer.class);
    }

    private void setValueDate(DateDay pDate) {
        theValueSet.setValue(FIELD_DATE, pDate);
    }

    private void setValueDesc(String pDesc) throws JDataException {
        setEncryptedValue(FIELD_DESC, pDesc);
    }

    private void setValueDesc(byte[] pDesc) throws JDataException {
        setEncryptedValue(FIELD_DESC, pDesc, String.class);
    }

    protected void setValueDesc(EncryptedString pDesc) {
        theValueSet.setValue(FIELD_DESC, pDesc);
    }

    private void setValueTransType(TransactionType pTransType) {
        theValueSet.setValue(FIELD_TRNTYP, pTransType);
    }

    private void setValueTransType(Integer pId) {
        theValueSet.setValue(FIELD_TRNTYP, pId);
    }

    private void setValueAmount(Money pAmount) throws JDataException {
        setEncryptedValue(FIELD_AMOUNT, pAmount);
    }

    private void setValueAmount(byte[] pAmount) throws JDataException {
        setEncryptedValue(FIELD_AMOUNT, pAmount, Money.class);
    }

    protected void setValueAmount(EncryptedMoney pAmount) {
        theValueSet.setValue(FIELD_AMOUNT, pAmount);
    }

    private void setValueDebit(Account pDebit) {
        theValueSet.setValue(FIELD_DEBIT, pDebit);
    }

    private void setValueDebit(Integer pId) {
        theValueSet.setValue(FIELD_DEBIT, pId);
    }

    private void setValueCredit(Account pCredit) {
        theValueSet.setValue(FIELD_CREDIT, pCredit);
    }

    private void setValueCredit(Integer pId) {
        theValueSet.setValue(FIELD_CREDIT, pId);
    }

    private void setValueUnits(Units pUnits) throws JDataException {
        setEncryptedValue(FIELD_UNITS, pUnits);
    }

    private void setValueUnits(byte[] pUnits) throws JDataException {
        setEncryptedValue(FIELD_UNITS, pUnits, Units.class);
    }

    private void setValueUnits(EncryptedUnits pUnits) {
        theValueSet.setValue(FIELD_UNITS, pUnits);
    }

    private void setValueTaxCredit(Money pTaxCredit) throws JDataException {
        setEncryptedValue(FIELD_TAXCREDIT, pTaxCredit);
    }

    private void setValueTaxCredit(byte[] pTaxCredit) throws JDataException {
        setEncryptedValue(FIELD_TAXCREDIT, pTaxCredit, Money.class);
    }

    private void setValueTaxCredit(EncryptedMoney pTaxCredit) {
        theValueSet.setValue(FIELD_TAXCREDIT, pTaxCredit);
    }

    private void setValueDilution(Dilution pDilution) throws JDataException {
        setEncryptedValue(FIELD_DILUTION, pDilution);
    }

    private void setValueDilution(byte[] pDilution) throws JDataException {
        setEncryptedValue(FIELD_DILUTION, pDilution, Dilution.class);
    }

    private void setValueDilution(EncryptedDilution pDilution) {
        theValueSet.setValue(FIELD_DILUTION, pDilution);
    }

    private void setValueYears(Integer pYears) {
        theValueSet.setValue(FIELD_YEARS, pYears);
    }

    /**
     * Event Info Set
     */
    private EventInfoSet theInfoSet = null;

    public EventInfoSet getInfoSet() {
        return theInfoSet;
    }

    /* Linking methods */
    @Override
    public Event getBase() {
        return (Event) super.getBase();
    }

    /**
     * Does this class require an EvenInfoSet
     * @return true/false
     */
    protected boolean requiredInfoSet() {
        return true;
    }

    /* Virtual Field IDs */
    @Override
    protected void addError(String pError,
                            JDataField pField) {
        super.addError(pError, pField);
    }

    /**
     * Construct a copy of an Event
     * @param pList the event list
     * @param pEvent The Event to copy
     */
    public Event(EventList pList,
                 Event pEvent) {
        /* Set standard values */
        super(pList, pEvent);
        ListStyle myOldStyle = pEvent.getStyle();

        /* Determine whether infoSet is needed */
        boolean bNeedInfoSet = requiredInfoSet();

        /* Switch on the ListStyle */
        switch (getStyle()) {
            case EDIT:
                /* Create a copy of the infoSet if required */
                if (bNeedInfoSet)
                    theInfoSet = new EventInfoSet(this, pEvent.theInfoSet);

                /* If this is a view creation */
                if (myOldStyle == ListStyle.CORE) {
                    /* Event is based on the original element */
                    setBase(pEvent);
                    pList.setNewId(this);
                    break;
                }

                /* Else this is a duplication so treat as new item */
                setId(0);
                pList.setNewId(this);

                break;
            case CLONE:
                reBuildLinks(pList.getData());
            case COPY:
            case CORE:
                /* Create a new infoSet if required */
                if (bNeedInfoSet)
                    theInfoSet = new EventInfoSet(this);

                /* Reset Id if this is an insert from a view */
                if (myOldStyle == ListStyle.EDIT)
                    setId(0);
                pList.setNewId(this);
                break;
            case UPDATE:
                setBase(pEvent);
                setState(pEvent.getState());
                break;
        }
    }

    /**
     * Construct a new event from an Account pattern
     * @param pList the list to build into
     * @param pLine The Line to copy
     */
    protected Event(EventList pList,
                    Pattern pLine) {
        /* Set standard values */
        super(pList, pLine);

        /* Create a new EventInfoSet if required */
        if (requiredInfoSet())
            theInfoSet = new EventInfoSet(this);

        /* Allocate the id */
        pList.setNewId(this);
    }

    /* Standard constructor for a newly inserted event */
    public Event(EventList pList) {
        super(pList, 0);
        setControlKey(pList.getControlKey());

        /* Create a new EventInfoSet if required */
        if (requiredInfoSet())
            theInfoSet = new EventInfoSet(this);

        pList.setNewId(this);
    }

    /* constructor for load from encrypted */
    protected Event(EventList pList,
                    int uId,
                    int uControlId,
                    Date pDate,
                    byte[] pDesc,
                    int uDebit,
                    int uCredit,
                    int uTransType,
                    byte[] pAmount,
                    byte[] pUnits,
                    byte[] pTaxCredit,
                    byte[] pDilution,
                    Integer pYears) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Local variables */
            TransactionType myTransType;
            Account myAccount;
            AccountList myAccounts;

            /* Access account list */
            FinanceData myData = pList.getData();
            myAccounts = myData.getAccounts();

            /* Create a new EventInfoSet if required */
            if (requiredInfoSet())
                theInfoSet = new EventInfoSet(this);

            /* Store the IDs that we will look up */
            setValueDebit(uDebit);
            setValueCredit(uCredit);
            setValueTransType(uTransType);
            setControlKey(uControlId);

            /* Create the date */
            setValueDate(new DateDay(pDate));

            /* Look up the Debit Account */
            myAccount = myAccounts.searchFor(uDebit);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Debit Account Id");
            setValueDebit(myAccount);

            /* Look up the Debit Account */
            myAccount = myAccounts.searchFor(uCredit);
            if (myAccount == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Credit Account Id");
            setValueCredit(myAccount);

            /* Look up the Transaction Type */
            myTransType = myData.getTransTypes().searchFor(uTransType);
            if (myTransType == null)
                throw new JDataException(ExceptionClass.DATA, this, "Invalid Transaction Type Id");
            setValueTransType(myTransType);

            /* Set the years */
            setValueYears(pYears);

            /* Record the encrypted values */
            setValueDesc(pDesc);
            setValueAmount(pAmount);
            setValueUnits(pUnits);
            setValueTaxCredit(pTaxCredit);
            setValueDilution(pDilution);

            /* Allocate the id */
            pList.setNewId(this);

            /* Catch Exceptions */
        } catch (Exception e) {
            /* Pass on exception */
            throw new JDataException(ExceptionClass.DATA, this, "Failed to create item", e);
        }
    }

    /* Standard constructor */
    protected Event(EventList pList,
                    int uId,
                    Date pDate,
                    String pDesc,
                    Account pDebit,
                    Account pCredit,
                    TransactionType pTransType,
                    String pAmount,
                    String pUnits,
                    String pTaxCredit,
                    String pDilution,
                    Integer pYears) throws JDataException {
        /* Initialise item */
        super(pList, uId);

        /* Protect against exceptions */
        try {
            /* Create a new EventInfoSet if required */
            if (requiredInfoSet())
                theInfoSet = new EventInfoSet(this);

            /* Record the encrypted values */
            setValueDesc(pDesc);
            setValueDebit(pDebit);
            setValueCredit(pCredit);
            setValueTransType(pTransType);
            setValueDate(new DateDay(pDate));

            setValueAmount(new Money(pAmount));

            /* Allocate the id */
            pList.setNewId(this);

            /* If Units exist */
            if (pUnits != null) {
                /* Create the data */
                Units myUnits = new Units(pUnits);
                setValueUnits(myUnits);
                boolean isCredit = pCredit.isPriced();
                if ((isStockSplit() || isAdminCharge()) && (!myUnits.isPositive())) {
                    myUnits.negate();
                    isCredit = false;
                }
                EventData myData = theInfoSet.getNewData(isCredit
                                                                 ? EventInfoClass.CreditUnits
                                                                 : EventInfoClass.DebitUnits);
                myData.setUnits(myUnits);
            }

            /* If TaxCredit exist */
            if (pTaxCredit != null) {
                /* Create the data */
                Money myTaxCredit = new Money(pTaxCredit);
                setValueTaxCredit(myTaxCredit);

                /* Create the data */
                EventData myData = theInfoSet.getNewData(EventInfoClass.TaxCredit);
                myData.setMoney(myTaxCredit);
            }

            /* If Dilution exist */
            if (pDilution != null) {
                /* Create the data */
                Dilution myDilution = new Dilution(pDilution);
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
        } catch (Exception e) {
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
    public int compareTo(Object pThat) {
        int iDiff;

        /* Handle the trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Make sure that the object is an Event */
        if (pThat.getClass() != this.getClass())
            return -1;

        /* Access the object as an Event */
        Event myThat = (Event) pThat;

        /* If the dates differ */
        if (this.getDate() != myThat.getDate()) {
            /* Handle null dates */
            if (this.getDate() == null)
                return 1;
            if (myThat.getDate() == null)
                return -1;

            /* Compare the dates */
            iDiff = getDate().compareTo(myThat.getDate());
            if (iDiff != 0)
                return iDiff;
        }

        /* If the transaction types differ */
        if (this.getTransType() != myThat.getTransType()) {
            /* Handle nulls */
            if (this.getTransType() == null)
                return 1;
            if (myThat.getTransType() == null)
                return -1;

            /* Compare transaction types */
            iDiff = getTransType().compareTo(myThat.getTransType());
            if (iDiff != 0)
                return iDiff;
        }

        /* If the descriptions differ */
        if (this.getDesc() != myThat.getDesc()) {
            /* Handle null descriptions */
            if (this.getDesc() == null)
                return 1;
            if (myThat.getDesc() == null)
                return -1;

            /* Compare the descriptions */
            iDiff = getDesc().compareTo(myThat.getDesc());
            if (iDiff < 0)
                return -1;
            if (iDiff > 0)
                return 1;
        }

        /* Compare ids */
        iDiff = (int) (getId() - myThat.getId());
        if (iDiff < 0)
            return -1;
        if (iDiff > 0)
            return 1;
        return 0;
    }

    /**
     * Rebuild Links to partner data
     * @param pData the DataSet
     */
    protected void reBuildLinks(FinanceData pData) {
        /* Update the Encryption details */
        super.reBuildLinks(pData);

        /* Access Lists */
        Account.AccountList myAccounts = pData.getAccounts();
        TransactionType.TransTypeList myTranTypes = pData.getTransTypes();

        /* Update credit to use the local copy of the Accounts */
        Account myAct = getCredit();
        Account myNewAct = myAccounts.searchFor(myAct.getId());
        setValueCredit(myNewAct);

        /* Update debit to use the local copy of the Accounts */
        myAct = getDebit();
        myNewAct = myAccounts.searchFor(myAct.getId());
        setValueDebit(myNewAct);

        /* Update transtype to use the local copy */
        TransactionType myTran = getTransType();
        TransactionType myNewTran = myTranTypes.searchFor(myTran.getId());
        setValueTransType(myNewTran);
    }

    /**
     * Determines whether an event can be valid
     * 
     * @param pTrans The transaction type of the event
     * @param pType The account type of the event
     * @param pCredit is the account a credit or a debit
     * @return valid true/false
     */
    public static boolean isValidEvent(TransactionType pTrans,
                                       AccountType pType,
                                       boolean pCredit) {
        boolean myResult = false;
        boolean isCredit = pCredit;

        /* Market is always false */
        if (pType.isMarket())
            return false;

        /* Switch on the TransType */
        switch (pTrans.getTranClass()) {
            case TAXFREEINCOME:
                if (!isCredit)
                    myResult = (pType.isExternal() && !pType.isCash());
                else
                    myResult = !pType.isExternal();
                break;
            case TAXABLEGAIN:
                if (!isCredit)
                    myResult = pType.isLifeBond();
                else
                    myResult = pType.isMoney();
                break;
            case ADMINCHARGE:
                myResult = pType.isLifeBond();
                break;
            case DIVIDEND:
                if (!isCredit)
                    myResult = pType.isDividend();
                else
                    myResult = (pType.isMoney() || pType.isCapital() || pType.isDeferred());
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
                if (!isCredit)
                    myResult = (pType.isMoney() || pType.isDeferred());
                else
                    myResult = pType.isShares();
                break;
            case INTEREST:
                if (!isCredit)
                    myResult = pType.isMoney();
                else
                    myResult = pType.isMoney();
                break;
            case TAXEDINCOME:
                if (!isCredit)
                    myResult = pType.isEmployer();
                else
                    myResult = ((pType.isMoney()) || (pType.isDeferred()));
                break;
            case NATINSURANCE:
                if (!isCredit)
                    myResult = pType.isEmployer();
                else
                    myResult = pType.isTaxMan();
                break;
            case TRANSFER:
                myResult = !pType.isExternal();
                if (isCredit)
                    myResult &= !pType.isEndowment();
                break;
            case ENDOWMENT:
                if (!isCredit)
                    myResult = (pType.isMoney() || pType.isDebt());
                else
                    myResult = pType.isEndowment();
                break;
            case CASHPAYMENT:
                isCredit = !isCredit;
            case CASHRECOVERY:
                if (!isCredit)
                    myResult = ((pType.isExternal()) && (!pType.isCash()));
                else
                    myResult = pType.isCash();
                break;
            case INHERITED:
                if (!isCredit)
                    myResult = pType.isInheritance();
                else
                    myResult = !pType.isExternal();
                break;
            case BENEFIT:
                if (!isCredit)
                    myResult = pType.isEmployer();
                else
                    myResult = pType.isBenefit();
                break;
            case RECOVERED:
                isCredit = !isCredit;
            case EXPENSE:
                if (!isCredit)
                    myResult = !pType.isExternal();
                else
                    myResult = pType.isExternal();
                break;
            case EXTRATAX:
            case INSURANCE:
                if (!isCredit)
                    myResult = (pType.isMoney() || pType.isDebt());
                else
                    myResult = (pType.isExternal() && !pType.isCash());
                break;
            case MORTGAGE:
                if (!isCredit)
                    myResult = pType.isDebt();
                else
                    myResult = (pType.isExternal() && !pType.isCash());
                break;
            case TAXREFUND:
                isCredit = !isCredit;
            case TAXOWED:
                if (!isCredit)
                    myResult = (pType.isMoney() || pType.isDeferred());
                else
                    myResult = pType.isTaxMan();
                break;
            case TAXRELIEF:
                if (!isCredit)
                    myResult = pType.isTaxMan();
                else
                    myResult = pType.isDebt();
                break;
            case DEBTINTEREST:
                if (!isCredit)
                    myResult = (pType.isExternal() && !pType.isCash());
                else
                    myResult = pType.isDebt();
                break;
            case WRITEOFF:
                if (!isCredit)
                    myResult = pType.isDebt();
                else
                    myResult = pType.isWriteOff();
                break;
            case RENTALINCOME:
                if (!isCredit)
                    myResult = (pType.isExternal() && !pType.isCash());
                else
                    myResult = pType.isDebt();
                break;
            default:
                break;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Is an event allowed between these two accounts, used for more detailed analysis once the event is
     * deemed valid based on the account types
     * @param pTrans The transaction type of the event
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @return true/false
     */
    public static boolean isValidEvent(TransactionType pTrans,
                                       Account pDebit,
                                       Account pCredit) {
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
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Validate the event
     */
    @Override
    public void validate() {
        EventList myList = (EventList) getList();
        DateDay myDate = getDate();
        String myDesc = getDesc();
        Account myDebit = getDebit();
        Account myCredit = getCredit();
        Money myAmount = getAmount();
        TransactionType myTransType = getTransType();
        Units myUnits = getUnits();
        Money myTaxCred = getTaxCredit();
        Integer myYears = getYears();
        Dilution myDilution = getDilution();

        /* The date must be non-null */
        if (myDate == null) {
            addError("Null date is not allowed", FIELD_DATE);
        }

        /* The date must be in-range */
        else if (myList.getRange().compareTo(myDate) != 0) {
            addError("Date must be within range", FIELD_DATE);
        }

        /* Debit must be non-null */
        if (myDebit == null) {
            addError("Debit account must be non-null", FIELD_DEBIT);
        }

        /* Credit must be non-null */
        if (myCredit == null) {
            addError("Credit account must be non-null", FIELD_CREDIT);
        }

        /* TransType must be non-null */
        if (myTransType == null) {
            addError("TransType must be non-null", FIELD_TRNTYP);
        } else if (!myTransType.getEnabled())
            addError("TransType must be enabled", FIELD_TRNTYP);

        /* The description must be non-null */
        if (myDesc == null) {
            addError("Description must be non-null", FIELD_DESC);
        }

        /* The description must not be too long */
        else if (myDesc.length() > DESCLEN) {
            addError("Description is too long", FIELD_DESC);
        }

        /* Hidden Events are not allowed */
        if ((myTransType != null) && (myTransType.isHiddenType())) {
            addError("Hidden transaction types are not allowed", FIELD_TRNTYP);
        }

        /* Check credit account */
        if ((myTransType != null) && (myCredit != null)
                && (!isValidEvent(myTransType, myCredit.getActType(), true)))
            addError("Invalid credit account for transaction", FIELD_CREDIT);

        /* Check debit account */
        if ((myTransType != null) && (myDebit != null)
                && (!isValidEvent(myTransType, myDebit.getActType(), false)))
            addError("Invalid debit account for transaction", FIELD_DEBIT);

        /* Check valid Credit/Debit combination */
        if ((myTransType != null) && (myCredit != null) && (myDebit != null)
                && (!isValidEvent(myTransType, myDebit, myCredit))) {
            addError("Invalid Debit/Credit combination account for transaction", FIELD_DEBIT);
            addError("Invalid Debit/Credit combination account for transaction", FIELD_CREDIT);
        }

        /* Money must not be null/negative */
        if (myAmount == null)
            addError("Amount must be non-null", FIELD_AMOUNT);
        else if (!myAmount.isPositive())
            addError("Amount cannot be negative", FIELD_AMOUNT);

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
                if ((myCredit.getInitPrice() != null)
                        && (getDate().compareTo(myCredit.getInitPrice().getDate()) < 0))
                    addError("Event Date is prior to first priced date for Credit Account", FIELD_DATE);
            }

            /* Check for valid priced debit account */
            if ((myDebit != null) && (myDebit.isPriced()) && (!Difference.isEqual(myCredit, myDebit))) {
                /* If the date of this event is prior to the first price */
                if ((myDebit.getInitPrice() != null)
                        && (getDate().compareTo(myDebit.getInitPrice().getDate()) < 0))
                    addError("Event Date is prior to first priced date for Debit Account", FIELD_DATE);
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
            }

            /* Else check for required units */
            else {
                if (isStockSplit())
                    addError("Stock Split requires non-zero Units", FIELD_UNITS);
                else if (isAdminCharge())
                    addError("Admin Charge requires non-zero Units", FIELD_UNITS);
            }

            /* If we have a dilution */
            if (myDilution != null) {
                /* If the dilution is not allowed */
                if ((!needsDilution(myTransType)) && (!myTransType.isStockSplit()))
                    addError("Dilution factor given where not allowed", FIELD_DILUTION);

                /* If the dilution is out of range */
                if (myDilution.outOfRange())
                    addError("Dilution factor value is outside allowed range (0-1)", FIELD_DILUTION);
            }

            /* else if we are missing a required dilution factor */
            else if (needsDilution(myTransType)) {
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
            }

            /* If we need a tax credit */
            else if ((myTransType != null) && (needsTaxCredit(myTransType, myDebit))) {
                /* Tax Credit must be non-null and positive */
                if ((myTaxCred == null) || (!myTaxCred.isPositive())) {
                    addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
                }

                /* Years must be null */
                if (myYears != null) {
                    addError("Years must be null", FIELD_YEARS);
                }
            }

            /* else we should not have a tax credit */
            else if (myTransType != null) {
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
        if (!hasErrors())
            setValidEdit();
    }

    /**
     * Determines whether an event relates to an account
     * 
     * @param pAccount The account to check relations with
     * @return related to the account true/false
     */
    public boolean relatesTo(Account pAccount) {
        boolean myResult = false;

        /* Check credit and debit accounts */
        if (getCredit().compareTo(pAccount) == 0)
            myResult = true;
        else if (getDebit().compareTo(pAccount) == 0)
            myResult = true;

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether an event is asset related
     * 
     * @return asset-related to the account true/false
     */
    public boolean isAssetRelated() {
        boolean myResult = false;

        /* Check credit and debit accounts */
        if (!getCredit().isExternal())
            myResult = true;
        else if (!getDebit().isExternal())
            myResult = true;

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether a line is locked to updates
     * 
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
     * Determines whether an event is a dividend re-investment
     * 
     * @return dividend re-investment true/false
     */
    public boolean isDividendReInvestment() {
        boolean myResult = false;

        /* Check for dividend re-investment */
        if ((getTransType() != null) && (getTransType().isDividend()) && (getCredit() != null)
                && (getCredit().isPriced()))
            myResult = true;

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether an event is an interest payment
     * 
     * @return interest true/false
     */
    public boolean isInterest() {
        boolean myResult = false;

        /* Check for interest */
        if ((getTransType() != null) && (getTransType().isInterest()))
            myResult = true;

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether an event is a stock split
     * 
     * @return stock split true/false
     */
    public boolean isStockSplit() {
        boolean myResult = false;

        /* Check for stock split */
        if ((getTransType() != null) && (getTransType().isStockSplit()))
            myResult = true;

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether an event is an Admin Charge
     * 
     * @return admin charge true/false
     */
    public boolean isAdminCharge() {
        boolean myResult = false;

        /* Check for admin charge */
        if ((getTransType() != null) && (getTransType().isAdminCharge()))
            myResult = true;

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether an event needs a tax credit
     * @param pTrans
     * @param pDebit
     * @return needs tax credit true/false
     */
    public static boolean needsTaxCredit(TransactionType pTrans,
                                         Account pDebit) {
        boolean myResult = false;

        /* Handle null transtype */
        if (pTrans == null)
            return myResult;

        /* Switch on transaction type */
        switch (pTrans.getTranClass()) {
        /* If this is a Taxable Gain/TaxedIncome we need a tax credit */
            case TAXABLEGAIN:
            case TAXEDINCOME:
                myResult = true;
                break;
            /* Check for dividend/interest */
            case DIVIDEND:
            case INTEREST:
                myResult = (pDebit != null) && !pDebit.isTaxFree();
                break;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Determines whether an event needs a dilution factor
     * @param pTrans
     * @return needs dilution factor true/false
     */
    public static boolean needsDilution(TransactionType pTrans) {
        boolean myResult = false;

        /* Handle null transtype */
        if (pTrans == null)
            return myResult;

        /* Switch on transaction type */
        switch (pTrans.getTranClass()) {
        /* If this is a Stock Operation we need a dilution factor */
            case STOCKDEMERGER:
            case STOCKRIGHTTAKEN:
            case STOCKRIGHTWAIVED:
                myResult = true;
                break;
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Calculate the tax credit for an event
     * @return the calculated tax credit
     */
    public Money calculateTaxCredit() {
        FinanceData myData = ((EventList) getList()).getData();
        TaxYear.TaxYearList myList = myData.getTaxYears();
        TaxYear myTax;
        Rate myRate;
        Money myCredit;

        /* Ignore unless tax credit is null/zero */
        if ((getTaxCredit() != null) && (getTaxCredit().isNonZero()))
            return getTaxCredit();

        /* Ignore unless transaction type is interest/dividend */
        if ((getTransType() == null) || ((!getTransType().isInterest()) && (!getTransType().isDividend())))
            return getTaxCredit();

        /* Access the relevant tax year */
        myTax = myList.searchFor(getDate());

        /* Determine the tax credit rate */
        if (getTransType().isInterest())
            myRate = myTax.getIntTaxRate();
        else
            myRate = myTax.getDivTaxRate();

        /* Calculate the tax credit */
        myCredit = getAmount().taxCreditAtRate(myRate);

        /* Return the tax credit */
        return myCredit;
    }

    /**
     * Set a new debit account
     * 
     * @param pDebit the debit account
     */
    public void setDebit(Account pDebit) {
        setValueDebit(pDebit);
    }

    /**
     * Set a new credit account
     * 
     * @param pCredit the credit account
     */
    public void setCredit(Account pCredit) {
        setValueCredit(pCredit);
    }

    /**
     * Set a new transtype
     * 
     * @param pTransType the transtype
     */
    public void setTransType(TransactionType pTransType) {
        setValueTransType(pTransType);
    }

    /**
     * Set a new description
     * 
     * @param pDesc the description
     * @throws JDataException
     */
    public void setDescription(String pDesc) throws JDataException {
        setValueDesc(pDesc);
    }

    /**
     * Set a new amount
     * 
     * @param pAmount the amount
     * @throws JDataException
     */
    public void setAmount(Money pAmount) throws JDataException {
        setValueAmount(pAmount);
    }

    /**
     * Set a new units
     * 
     * @param pUnits the units
     * @throws JDataException
     */
    public void setUnits(Units pUnits) throws JDataException {
        setValueUnits(pUnits);
    }

    /**
     * Set a new date
     * 
     * @param pDate the new date
     */
    public void setDate(DateDay pDate) {
        setValueDate((pDate == null) ? null : new DateDay(pDate));
    }

    /**
     * Set a new tax credit amount
     * 
     * @param pAmount the tax credit amount
     * @throws JDataException
     */
    public void setTaxCredit(Money pAmount) throws JDataException {
        setValueTaxCredit(pAmount);
    }

    /**
     * Set a new years value
     * 
     * @param pYears the years
     */
    public void setYears(Integer pYears) {
        setValueYears(pYears);
    }

    /**
     * Set a new dilution value
     * 
     * @param pDilution the dilution
     * @throws JDataException
     */
    public void setDilution(Dilution pDilution) throws JDataException {
        setValueDilution(pDilution);
    }

    /**
     * Update event from an element
     * @param pItem the changed element
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(DataItem<?> pItem) {
        boolean bChanged = false;
        if (pItem instanceof Event) {
            Event myEvent = (Event) pItem;
            bChanged = applyChanges(myEvent);
        }
        return bChanged;
    }

    /**
     * Update event from an Event extract
     * @param pEvent the changed event
     * @return whether changes have been made
     */
    private boolean applyChanges(Event pEvent) {
        boolean bChanged = false;

        /* Store the current detail into history */
        pushHistory();

        /* Update the date if required */
        if (!Difference.isEqual(getDate(), pEvent.getDate()))
            setDate(pEvent.getDate());

        /* Update the description if required */
        if (!Difference.isEqual(getDesc(), pEvent.getDesc()))
            setValueDesc(pEvent.getDescField());

        /* Update the amount if required */
        if (!Difference.isEqual(getAmount(), pEvent.getAmount()))
            setValueAmount(pEvent.getAmountField());

        /* Update the units if required */
        if (!Difference.isEqual(getUnits(), pEvent.getUnits()))
            setValueUnits(pEvent.getUnitsField());

        /* Update the tranType if required */
        if (!Difference.isEqual(getTransType(), pEvent.getTransType()))
            setValueTransType(pEvent.getTransType());

        /* Update the debit if required */
        if (!Difference.isEqual(getDebit(), pEvent.getDebit()))
            setValueDebit(pEvent.getDebit());

        /* Update the credit if required */
        if (!Difference.isEqual(getCredit(), pEvent.getCredit()))
            setValueCredit(pEvent.getCredit());

        /* Update the tax credit if required */
        if (!Difference.isEqual(getTaxCredit(), pEvent.getTaxCredit()))
            setValueTaxCredit(pEvent.getTaxCreditField());

        /* Update the dilution if required */
        if (!Difference.isEqual(getDilution(), pEvent.getDilution()))
            setValueDilution(pEvent.getDilutionField());

        /* Update the years if required */
        if (!Difference.isEqual(getYears(), pEvent.getYears()))
            setValueYears(pEvent.getYears());

        /* Check for changes */
        if (checkForHistory()) {
            /* Mark as changed */
            setState(DataState.CHANGED);
            bChanged = true;
        }

        /* Return to caller */
        return bChanged;
    }

    /**
     * Format an Event
     * @param pEvent the event to format
     * @return the formatted event
     */
    public static String format(Event pEvent) {
        String myFormat;
        myFormat = (pEvent != null) ? pEvent.getDesc() : "null";
        return myFormat;
    }

    /**
     * List class for Events
     */
    public static class EventList extends EncryptedList<EventList, Event> {
        /* Members */
        private DateDayRange theRange = null;

        /* Access DataSet correctly */
        @Override
        public FinanceData getData() {
            return (FinanceData) super.getData();
        }

        protected DateDayRange getRange() {
            return theRange;
        }

        /* Set the range */
        protected void setRange(DateDayRange pRange) {
            theRange = pRange;
        }

        /**
         * Construct an empty CORE event list
         * @param pData the DataSet for the list
         */
        protected EventList(FinanceData pData) {
            super(EventList.class, Event.class, pData);
        }

        /**
         * Constructor for a cloned List
         * @param pSource the source List
         */
        protected EventList(EventList pSource) {
            super(pSource);
        }

        /**
         * Construct an update extract for the List.
         * @param pStyle the style
         * @return the update Extract
         */
        private EventList getExtractList(ListStyle pStyle) {
            /* Build an empty Extract List */
            EventList myList = new EventList(this);

            /* Obtain underlying updates */
            myList.populateList(pStyle);

            /* Return the list */
            return myList;
        }

        /* Obtain extract lists. */
        @Override
        public EventList getUpdateList() {
            return getExtractList(ListStyle.UPDATE);
        }

        @Override
        public EventList getEditList() {
            return getExtractList(ListStyle.EDIT);
        }

        @Override
        public EventList getShallowCopy() {
            return getExtractList(ListStyle.COPY);
        }

        @Override
        public EventList getDeepCopy(DataSet<?> pDataSet) {
            /* Build an empty Extract List */
            EventList myList = new EventList(this);
            myList.setData(pDataSet);
            myList.setRange(theRange);

            /* Obtain underlying clones */
            myList.populateList(ListStyle.CLONE);
            myList.setStyle(ListStyle.CORE);

            /* Return the list */
            return myList;
        }

        /**
         * Construct a difference Event list
         * @param pOld the old Event list
         */
        @Override
        protected EventList getDifferences(EventList pOld) {
            /* Build an empty Difference List */
            EventList myList = new EventList(this);

            /* Calculate the differences */
            myList.getDifferenceList(this, pOld);

            /* Return the list */
            return myList;
        }

        /**
         * Get an EditList for a range
         * @return the edit list
         */
        public EventList getViewList() {
            /* Build an empty List */
            EventList myList = new EventList(this);

            /* Make this list the correct style */
            myList.setStyle(ListStyle.VIEW);

            /* Return it */
            return myList;
        }

        /**
         * Get an EditList for a range
         * @param pRange the range
         * @return the edit list
         */
        public EventList getEditList(DateDayRange pRange) {
            /* Build an empty List */
            EventList myList = new EventList(this);

            /* Make this list the correct style */
            myList.setStyle(ListStyle.EDIT);

            /* local variable */
            DataListIterator<Event> myIterator;
            Event myCurr;
            Event myEvent;
            int myResult;

            /* Record range and initialise the list */
            myList.theRange = pRange;

            /* Loop through the Events extracting relevant elements */
            myIterator = listIterator(true);
            while ((myCurr = myIterator.next()) != null) {
                /* Check the range */
                myResult = pRange.compareTo(myCurr.getDate());

                /* Handle out of range */
                if (myResult == 1)
                    continue;
                if (myResult == -1)
                    break;

                /* Build the new linked event and add it to the extract */
                myEvent = new Event(myList, myCurr);
                myList.add(myEvent);
            }

            /* Return the List */
            return myList;
        }

        /**
         * Get an EditList for a new TaxYear
         * @param pTaxYear the new TaxYear
         * @return the edit list
         * @throws JDataException
         */
        public EventList getEditList(TaxYear pTaxYear) throws JDataException {
            /* Build an empty List */
            EventList myList = new EventList(this);

            /* Make this list the correct style */
            myList.setStyle(ListStyle.EDIT);

            /* Local variables */
            DataListIterator<Event> myIterator;
            PatternList myPatterns;
            Event myCurr;
            Pattern myPattern;
            Event myEvent;
            DateDay myDate;

            /* Record range and initialise the list */
            myList.theRange = pTaxYear.getRange();

            /* Access the underlying data */
            myPatterns = getData().getPatterns();
            myIterator = myPatterns.listIterator();

            /* Loop through the Patterns */
            while ((myCurr = myIterator.next()) != null) {
                /* Access as pattern */
                myPattern = (Pattern) myCurr;

                /* Access a copy of the base date */
                myDate = new DateDay(myCurr.getDate());

                /* Loop while we have an event to add */
                while ((myEvent = myPattern.nextEvent(myList, pTaxYear, myDate)) != null) {
                    /* Add it to the extract */
                    myList.add(myEvent);
                }
            }

            /* Return the List */
            return myList;
        }

        /**
         * Validate an extract
         */
        @Override
        public void validate() {
            DataListIterator<Event> myIterator;
            Event myCurr;

            /* Clear the errors */
            clearErrors();

            /* Access the underlying data */
            myIterator = listIterator();

            /* Loop through the lines */
            while ((myCurr = myIterator.next()) != null) {
                /* Validate it */
                myCurr.validate();
            }
        }

        /**
         * Add a new item to the list
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public Event addNewItem(DataItem<?> pItem) {
            if (pItem instanceof Event) {
                Event myEvent = new Event(this, (Event) pItem);
                add(myEvent);
                return myEvent;
            } else if (pItem instanceof Statement.StatementLine) {
                Event myEvent = new Event(this, (Statement.StatementLine) pItem);
                add(myEvent);
                return myEvent;
            } else
                return null;
        }

        /**
         * Add a new item to the edit list
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
         * Obtain the type of the item
         * @return the type of the item
         */
        public String itemType() {
            return LIST_NAME;
        }

        /**
         * Allow an event to be added
         * @param uId
         * @param pDate
         * @param pDesc
         * @param pAmount
         * @param pDebit
         * @param pCredit
         * @param pUnits
         * @param pTransType
         * @param pTaxCredit
         * @param pDilution
         * @param pYears
         * @throws JDataException
         */
        public void addItem(int uId,
                            Date pDate,
                            String pDesc,
                            String pAmount,
                            String pDebit,
                            String pCredit,
                            String pUnits,
                            String pTransType,
                            String pTaxCredit,
                            String pDilution,
                            Integer pYears) throws JDataException {
            FinanceData myData;
            Account.AccountList myAccounts;
            Account myDebit;
            Account myCredit;
            TransactionType myTransType;
            Event myEvent;

            /* Access the accounts */
            myData = getData();
            myAccounts = myData.getAccounts();

            /* Look up the Transaction Type */
            myTransType = myData.getTransTypes().searchFor(pTransType);
            if (myTransType == null)
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Transact Type ["
                        + pTransType + "]");

            /* Look up the Credit Account */
            myCredit = myAccounts.searchFor(pCredit);
            if (myCredit == null)
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Credit account ["
                        + pCredit + "]");

            /* Look up the Debit Account */
            myDebit = myAccounts.searchFor(pDebit);
            if (myDebit == null)
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + JDataObject.formatField(new DateDay(pDate)) + "] has invalid Debit account ["
                        + pDebit + "]");

            /* Create the new Event */
            myEvent = new Event(this, uId, pDate, pDesc, myDebit, myCredit, myTransType, pAmount, pUnits,
                    pTaxCredit, pDilution, pYears);

            /* Validate the event */
            myEvent.validate();

            /* Handle validation failure */
            if (myEvent.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myEvent, "Failed validation");

            /* Add the Event to the list */
            add(myEvent);
        }

        /**
         * Allow an event to be added
         * @param uId
         * @param uControlId
         * @param pDate
         * @param pDesc
         * @param pAmount
         * @param uDebitId
         * @param uCreditId
         * @param pUnits
         * @param uTransId
         * @param pTaxCredit
         * @param pDilution
         * @param pYears
         * @throws JDataException
         */
        public void addItem(int uId,
                            int uControlId,
                            Date pDate,
                            byte[] pDesc,
                            byte[] pAmount,
                            int uDebitId,
                            int uCreditId,
                            byte[] pUnits,
                            int uTransId,
                            byte[] pTaxCredit,
                            byte[] pDilution,
                            Integer pYears) throws JDataException {
            Event myEvent;

            /* Create the new Event */
            myEvent = new Event(this, uId, uControlId, pDate, pDesc, uDebitId, uCreditId, uTransId, pAmount,
                    pUnits, pTaxCredit, pDilution, pYears);

            /* Check that this EventId has not been previously added */
            if (!isIdUnique(uId))
                throw new JDataException(ExceptionClass.DATA, myEvent, "Duplicate EventId");

            /* Validate the event */
            myEvent.validate();

            /* Handle validation failure */
            if (myEvent.hasErrors())
                throw new JDataException(ExceptionClass.VALIDATE, myEvent, "Failed validation");

            /* Add the Event to the list */
            add(myEvent);
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }
    }
}
