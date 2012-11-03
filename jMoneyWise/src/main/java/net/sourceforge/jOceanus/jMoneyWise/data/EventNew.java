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
import java.util.Iterator;

import net.sourceforge.jOceanus.jDataManager.Difference;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataList;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountNew.AccountNewList;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventDateRange;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYearNew.TaxYearNewList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;

/**
 * New version of Event DataItem utilising EventInfo.
 * @author Tony Washer
 */
public class EventNew extends EventBase {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = EventNew.class.getSimpleName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME + "s";

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EventBase.FIELD_DEFS);

    @Override
    public JDataFields declareFields() {
        return FIELD_DEFS;
    }

    /**
     * EventInfoSet field Id.
     */
    public static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField("InfoSet");

    /**
     * DebitUnits Field Id.
     */
    public static final JDataField FIELD_DEBTUNITS = FIELD_DEFS.declareEqualityValueField("DebitUnits");

    /**
     * CreditUnits Field Id.
     */
    public static final JDataField FIELD_CREDUNITS = FIELD_DEFS.declareEqualityValueField("CreditUnits");

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
     * NatInsurance Field Id.
     */
    public static final JDataField FIELD_NATINS = FIELD_DEFS.declareEqualityValueField("NatInsurance");

    /**
     * Benefit Field Id.
     */
    public static final JDataField FIELD_BENEFIT = FIELD_DEFS.declareEqualityValueField("Benefit");

    /**
     * Pension Field Id.
     */
    public static final JDataField FIELD_PENSION = FIELD_DEFS.declareEqualityValueField("Pension");

    /**
     * XferDelay Field Id.
     */
    public static final JDataField FIELD_XFERDELAY = FIELD_DEFS.declareEqualityValueField("XferDelay");

    /**
     * ThirdParty Field Id.
     */
    public static final JDataField FIELD_THIRDPARTY = FIELD_DEFS.declareEqualityValueField("ThirdParty");

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet ? theInfoSet : JDataFieldValue.SkipField;
        }
        if (FIELD_DEBTUNITS.equals(pField)) {
            return getInfoSetValue(EventInfoClass.DebitUnits);
        }
        if (FIELD_CREDUNITS.equals(pField)) {
            return getInfoSetValue(EventInfoClass.CreditUnits);
        }
        if (FIELD_TAXCREDIT.equals(pField)) {
            return getInfoSetValue(EventInfoClass.TaxCredit);
        }
        if (FIELD_DILUTION.equals(pField)) {
            return getInfoSetValue(EventInfoClass.Dilution);
        }
        if (FIELD_YEARS.equals(pField)) {
            return getInfoSetValue(EventInfoClass.QualifyYears);
        }
        if (FIELD_NATINS.equals(pField)) {
            return getInfoSetValue(EventInfoClass.NatInsurance);
        }
        if (FIELD_BENEFIT.equals(pField)) {
            return getInfoSetValue(EventInfoClass.Benefit);
        }
        if (FIELD_PENSION.equals(pField)) {
            return getInfoSetValue(EventInfoClass.Pension);
        }
        if (FIELD_XFERDELAY.equals(pField)) {
            return getInfoSetValue(EventInfoClass.XferDelay);
        }
        if (FIELD_THIRDPARTY.equals(pField)) {
            return getInfoSetValue(EventInfoClass.ThirdParty);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Do we have an InfoSet.
     */
    private final boolean hasInfoSet;

    /**
     * EventInfoSet.
     */
    private final EventNewInfoSet theInfoSet;

    /**
     * Obtain InfoSet.
     * @return the infoSet
     */
    protected EventNewInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Debit Units.
     * @return the Debit Units
     */
    public JUnits getDebitUnits() {
        return hasInfoSet ? theInfoSet.getValue(EventInfoClass.DebitUnits, JUnits.class) : null;
    }

    /**
     * Obtain Credit Units.
     * @return the Credit Units
     */
    public JUnits getCreditUnits() {
        return hasInfoSet ? theInfoSet.getValue(EventInfoClass.CreditUnits, JUnits.class) : null;
    }

    /**
     * Obtain Tax Credit.
     * @return the Tax Credit
     */
    public JMoney getTaxCredit() {
        return hasInfoSet ? theInfoSet.getValue(EventInfoClass.TaxCredit, JMoney.class) : null;
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public JDilution getDilution() {
        return hasInfoSet ? theInfoSet.getValue(EventInfoClass.Dilution, JDilution.class) : null;
    }

    /**
     * Obtain Qualifying Years.
     * @return the Years
     */
    public Integer getYears() {
        return hasInfoSet ? theInfoSet.getValue(EventInfoClass.QualifyYears, Integer.class) : null;
    }

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pEvent The Event to copy
     */
    public EventNew(final EventNewList pList,
                    final EventNew pEvent) {
        /* Set standard values */
        super(pList, pEvent);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new EventNewInfoSet(this, pList.getEventInfoTypes());
                theInfoSet.setInfoList(pList.getEventInfo());
                hasInfoSet = true;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                break;
        }
    }

    /**
     * Construct a new event from an Account pattern.
     * @param pList the list to build into
     * @param pLine The Line to copy
     * @throws JDataException on error
     */
    // protected EventNew(final EventBaseList<? extends EventBase> pList,
    // final Pattern pLine) throws JDataException {
    // /* Set standard values */
    // super(pList, pLine);

    // /* If we need a tax Credit */
    // if (needsTaxCredit(getTransType(), getDebit())) {
    // /* Calculate the tax credit */
    // setTaxCredit(calculateTaxCredit());
    // }
    // }

    /**
     * Edit constructor.
     * @param pList the list
     */
    public EventNew(final EventNewList pList) {
        super(pList);
        setControlKey(pList.getControlKey());

        /* Build InfoSet */
        theInfoSet = new EventNewInfoSet(this, pList.getEventInfoTypes());
        theInfoSet.setInfoList(pList.getEventInfo());
        hasInfoSet = true;
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
    protected EventNew(final EventNewList pList,
                       final Integer uId,
                       final Integer uControlId,
                       final Date pDate,
                       final byte[] pDesc,
                       final Integer uDebit,
                       final Integer uCredit,
                       final Integer uTransType,
                       final byte[] pAmount) throws JDataException {
        /* Initialise item */
        super(pList, uId, uControlId, pDate, pDesc, uDebit, uCredit, uTransType, pAmount);

        /* Create the InfoSet */
        theInfoSet = new EventNewInfoSet(this, pList.getEventInfoTypes());
        theInfoSet.setInfoList(pList.getEventInfo());
        hasInfoSet = true;
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
    protected EventNew(final EventNewList pList,
                       final Integer uId,
                       final Date pDate,
                       final String pDesc,
                       final AccountNew pDebit,
                       final AccountNew pCredit,
                       final TransactionType pTransType,
                       final String pAmount) throws JDataException {
        /* Initialise item */
        super(pList, uId, pDate, pDesc, pDebit, pCredit, pTransType, pAmount);

        /* Create the InfoSet */
        theInfoSet = new EventNewInfoSet(this, pList.getEventInfoTypes());
        theInfoSet.setInfoList(pList.getEventInfo());
        hasInfoSet = true;
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
                                       final AccountNew pDebit,
                                       final AccountNew pCredit) {
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
     * Validate the event.
     */
    @Override
    public void validate() {
        JDateDay myDate = getDate();
        String myDesc = getDesc();
        AccountNew myDebit = getDebit();
        AccountNew myCredit = getCredit();
        JMoney myAmount = getAmount();
        TransactionType myTransType = getTransType();
        JUnits myUnits = getDebitUnits();
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
        // if (!(this instanceof Pattern)) {
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
                    addError("Units are only allowed involving assets", FIELD_DEBTUNITS);
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
                                 FIELD_DEBTUNITS);
                    }

                    /* Dividend between priced requires identical credit/debit */
                    if ((myTransType != null) && (myTransType.isDividend())
                            && (!Difference.isEqual(myCredit, myDebit))) {
                        addError("Unit Dividends between assets must be between same asset", FIELD_DEBTUNITS);
                    }
                }
            }

            /* Units must be non-zero */
            if (!myUnits.isNonZero()) {
                addError("Units must be non-Zero", FIELD_DEBTUNITS);
            }

            /* Units must not be negative unless it is stock split */
            if ((!myUnits.isPositive())
                    && ((myTransType == null) || ((!myTransType.isStockSplit()) && (!myTransType
                            .isAdminCharge())))) {
                addError("Units must be positive unless this is a StockSplit/AdminCharge", FIELD_DEBTUNITS);
            }

            /* Else check for required units */
        } else {
            if (isStockSplit()) {
                addError("Stock Split requires non-zero Units", FIELD_DEBTUNITS);
            } else if (isAdminCharge()) {
                addError("Admin Charge requires non-zero Units", FIELD_DEBTUNITS);
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
        // }

        /* Set validation flag */
        if (!hasErrors()) {
            setValidEdit();
        }
    }

    /**
     * Calculate the tax credit for an event.
     * @return the calculated tax credit
     */
    public JMoney calculateTaxCredit() {
        FinanceData myData = getDataSet();
        TaxYearNewList myList = myData.getNewTaxYears();

        /* Ignore unless tax credit is null/zero */
        if ((getTaxCredit() != null) && (getTaxCredit().isNonZero())) {
            return getTaxCredit();
        }

        /* Ignore unless transaction type is interest/dividend */
        if ((getTransType() == null) || ((!getTransType().isInterest()) && (!getTransType().isDividend()))) {
            return getTaxCredit();
        }

        /* Access the relevant tax year */
        TaxYearNew myTax = myList.findTaxYearForDate(getDate());

        /* Determine the tax credit rate */
        JRate myRate = (getTransType().isInterest()) ? myTax.getIntTaxRate() : myTax.getDivTaxRate();

        /* Calculate the tax credit */
        return getAmount().taxCreditAtRate(myRate);
    }

    /**
     * Set a new Debit Units.
     * @param pUnits the new units
     * @throws JDataException on error
     */
    public void setDebitUnits(final JUnits pUnits) throws JDataException {
        setInfoSetValue(EventInfoClass.DebitUnits, pUnits);
    }

    /**
     * Set a new Credit Units.
     * @param pUnits the new units
     * @throws JDataException on error
     */
    public void setCreditUnits(final JUnits pUnits) throws JDataException {
        setInfoSetValue(EventInfoClass.CreditUnits, pUnits);
    }

    /**
     * Set a new TaxCredit.
     * @param pCredit the new credit
     * @throws JDataException on error
     */
    public void setTaxCredit(final JMoney pCredit) throws JDataException {
        setInfoSetValue(EventInfoClass.TaxCredit, pCredit);
    }

    /**
     * Set a new Dilution.
     * @param pDilution the new dilution
     * @throws JDataException on error
     */
    public void setDilution(final JDilution pDilution) throws JDataException {
        setInfoSetValue(EventInfoClass.Dilution, pDilution);
    }

    /**
     * Set a new Qualifying Years.
     * @param pYears the new years
     * @throws JDataException on error
     */
    public void setYears(final Integer pYears) throws JDataException {
        setInfoSetValue(EventInfoClass.QualifyYears, pYears);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JDataException on error
     */
    private void setInfoSetValue(final EventInfoClass pInfoClass,
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
    private Object getInfoSetValue(final EventInfoClass pInfoClass) {
        /* Access value of object */
        Object myValue = hasInfoSet ? theInfoSet.getField(pInfoClass) : null;

        /* Return the value */
        return (myValue != null) ? myValue : JDataFieldValue.SkipField;
    }

    /**
     * The Event List class.
     */
    public static class EventNewList extends EventBaseList<EventNew> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventNewList.class.getSimpleName(),
                DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * The EventInfo List.
         */
        private EventInfoList theInfoList = null;

        /**
         * The EventInfoType list.
         */
        private EventInfoTypeList theInfoTypeList = null;

        /**
         * Obtain the eventInfoList.
         * @return the event info list
         */
        public EventInfoList getEventInfo() {
            if (theInfoList == null) {
                theInfoList = getDataSet().getEventInfo();
            }
            return theInfoList;
        }

        /**
         * Obtain the eventInfoTypeList.
         * @return the event info type list
         */
        public EventInfoTypeList getEventInfoTypes() {
            if (theInfoTypeList == null) {
                theInfoTypeList = getDataSet().getEventInfoTypes();
            }
            return theInfoTypeList;
        }

        /**
         * Construct an empty CORE event list.
         * @param pData the DataSet for the list
         */
        protected EventNewList(final FinanceData pData) {
            super(pData, EventNew.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventNewList(final EventNewList pSource) {
            super(pSource);
        }

        @Override
        protected EventNewList getEmptyList() {
            return new EventNewList(this);
        }

        @Override
        public EventNewList cloneList(final DataSet<?> pDataSet) {
            return (EventNewList) super.cloneList(pDataSet);
        }

        @Override
        public EventNewList deriveList(final ListStyle pStyle) {
            return (EventNewList) super.deriveList(pStyle);
        }

        @Override
        public EventNewList deriveDifferences(final DataList<EventNew> pOld) {
            return (EventNewList) super.deriveDifferences(pOld);
        }

        /**
         * Get an EditList for a range.
         * @return the edit list
         */
        public EventNewList getViewList() {
            /* Build an empty List */
            EventNewList myList = getEmptyList();
            myList.setStyle(ListStyle.COPY);

            /* Return it */
            return myList;
        }

        /**
         * Get an EditList for a range.
         * @param pRange the range
         * @return the edit list
         */
        public EventNewList deriveEditList(final JDateDayRange pRange) {
            /* Build an empty List */
            EventNewList myList = getEmptyList();
            myList.setStyle(ListStyle.EDIT);
            myList.setRange(pRange);

            /* Store InfoType list */
            myList.theInfoTypeList = getEventInfoTypes();

            /* Create info List */
            EventInfoList myEventInfo = getEventInfo();
            myList.theInfoList = myEventInfo.getEmptyList();
            myList.theInfoList.setBase(myEventInfo);

            /* Loop through the Events extracting relevant elements */
            Iterator<EventNew> myIterator = iterator();
            while (myIterator.hasNext()) {
                EventNew myCurr = myIterator.next();

                /* Check the range */
                int myResult = pRange.compareTo(myCurr.getDate());

                /* Handle out of range */
                if (myResult == 1) {
                    continue;
                } else if (myResult == -1) {
                    break;
                }

                /* Build the new linked event and add it to the extract */
                EventNew myEvent = new EventNew(myList, myCurr);
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
        // public EventNewList deriveEditList(final TaxYearNew pTaxYear) throws JDataException {
        // /* Build an empty List */
        // EventNewList myList = getEmptyList();
        // myList.setStyle(ListStyle.EDIT);
        // myList.theRange = pTaxYear.getRange();

        /* Access the underlying data */
        // PatternList myPatterns = getDataSet().getPatterns();
        // Iterator<Pattern> myIterator = myPatterns.iterator();
        // Event myEvent;

        /* Loop through the Patterns */
        // while (myIterator.hasNext()) {
        // Pattern myCurr = (Pattern) myIterator.next();

        /* Access a copy of the base date */
        // JDateDay myDate = new JDateDay(myCurr.getDate());

        /* Loop while we have an event to add */
        // while ((myEvent = myCurr.nextEvent(myList, pTaxYear, myDate)) != null) {
        // /* Add it to the extract */
        // myList.append(myEvent);
        // }
        // }

        /* Sort the list */
        // myList.reSort();

        /* Return the List */
        // return myList;
        // }

        /**
         * Validate an extract.
         */
        @Override
        public void validate() {
            /* Clear the errors */
            clearErrors();

            /* Access the underlying data */
            Iterator<EventNew> myIterator = listIterator();

            /* Loop through the lines */
            while (myIterator.hasNext()) {
                EventNew myCurr = myIterator.next();

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
        public EventNew addCopyItem(final DataItem pItem) {
            if (pItem instanceof EventNew) {
                EventNew myEvent = new EventNew(this, (EventNew) pItem);
                add(myEvent);
                return myEvent;
                // } else if (pItem instanceof StatementLine) {
                // EventNew myEvent = new EventNew(this, (StatementLine) pItem);
                // add(myEvent);
                // return myEvent;
            }
            return null;
        }

        /**
         * Add a new item to the edit list.
         * @return the newly added item
         */
        @Override
        public EventNew addNewItem() {
            /* Create a new Event */
            EventNew myEvent = new EventNew(this);

            /* Set the Date as the start of the range */
            myEvent.setDate(getValidDateRange().getStart());

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
         * @param pTransType the transaction type
         * @return the new event
         * @throws JDataException on error
         */
        public EventNew addOpenItem(final Integer uId,
                                    final Date pDate,
                                    final String pDesc,
                                    final String pAmount,
                                    final String pDebit,
                                    final String pCredit,
                                    final String pTransType) throws JDataException {
            /* Access the accounts */
            FinanceData myData = getDataSet();
            JDataFormatter myFormatter = myData.getDataFormatter();
            AccountNewList myAccounts = myData.getNewAccounts();

            /* Look up the Transaction Type */
            TransactionType myTransType = myData.getTransTypes().findItemByName(pTransType);
            if (myTransType == null) {
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + myFormatter.formatObject(new JDateDay(pDate)) + "] has invalid Transact Type ["
                        + pTransType + "]");
            }

            /* Look up the Credit Account */
            AccountNew myCredit = myAccounts.findItemByName(pCredit);
            if (myCredit == null) {
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + myFormatter.formatObject(new JDateDay(pDate)) + "] has invalid Credit account ["
                        + pCredit + "]");
            }

            /* Look up the Debit Account */
            AccountNew myDebit = myAccounts.findItemByName(pDebit);
            if (myDebit == null) {
                throw new JDataException(ExceptionClass.DATA, "Event on ["
                        + myFormatter.formatObject(new JDateDay(pDate)) + "] has invalid Debit account ["
                        + pDebit + "]");
            }

            /* Create the new Event */
            EventNew myEvent = new EventNew(this, uId, pDate, pDesc, myDebit, myCredit, myTransType, pAmount);

            /* Validate the event */
            myEvent.validate();

            /* Handle validation failure */
            if (myEvent.hasErrors()) {
                throw new JDataException(ExceptionClass.VALIDATE, myEvent, "Failed validation");
            }

            /* Add the Event to the list */
            append(myEvent);
            return myEvent;
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
         * @param uTransId the transaction id
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer uId,
                                  final Integer uControlId,
                                  final Date pDate,
                                  final byte[] pDesc,
                                  final byte[] pAmount,
                                  final Integer uDebitId,
                                  final Integer uCreditId,
                                  final Integer uTransId) throws JDataException {
            /* Create the new Event */
            EventNew myEvent = new EventNew(this, uId, uControlId, pDate, pDesc, uDebitId, uCreditId,
                    uTransId, pAmount);

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
    }
}
