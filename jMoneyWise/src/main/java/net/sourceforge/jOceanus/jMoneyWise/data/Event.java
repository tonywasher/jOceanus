/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayRange;
import net.sourceforge.jOceanus.jDecimal.JDilution;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JRate;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.Pattern.PatternList;
import net.sourceforge.jOceanus.jMoneyWise.data.TaxYear.TaxYearList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType.EventInfoTypeList;

/**
 * New version of Event DataItem utilising EventInfo.
 * @author Tony Washer
 */
public class Event
        extends EventBase {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = Event.class.getSimpleName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = OBJECT_NAME
                                           + "s";

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

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_INFOSET.equals(pField)) {
            return hasInfoSet
                    ? theInfoSet
                    : JDataFieldValue.SkipField;
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
     * EventInfoSet.
     */
    private final EventInfoSet theInfoSet;

    /**
     * Obtain InfoSet.
     * @return the infoSet
     */
    public EventInfoSet getInfoSet() {
        return theInfoSet;
    }

    /**
     * Obtain Debit Units.
     * @return the Debit Units
     */
    public JUnits getDebitUnits() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.DebitUnits, JUnits.class)
                : null;
    }

    /**
     * Obtain Credit Units.
     * @return the Credit Units
     */
    public JUnits getCreditUnits() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.CreditUnits, JUnits.class)
                : null;
    }

    /**
     * Obtain Tax Credit.
     * @return the Tax Credit
     */
    public JMoney getTaxCredit() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.TaxCredit, JMoney.class)
                : null;
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public JDilution getDilution() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.Dilution, JDilution.class)
                : null;
    }

    /**
     * Obtain Qualifying Years.
     * @return the Years
     */
    public Integer getYears() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.QualifyYears, Integer.class)
                : null;
    }

    /**
     * Obtain xferDelay.
     * @return the xferDelay
     */
    public Integer getXferDelay() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.XferDelay, Integer.class)
                : null;
    }

    /**
     * Obtain National Insurance.
     * @return the NatInsurance
     */
    public JMoney getNatInsurance() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.NatInsurance, JMoney.class)
                : null;
    }

    /**
     * Obtain Benefit.
     * @return the Benefit
     */
    public JMoney getBenefit() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.Benefit, JMoney.class)
                : null;
    }

    /**
     * Obtain Pension.
     * @return the Pension
     */
    public JMoney getPension() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.Pension, JMoney.class)
                : null;
    }

    /**
     * Obtain Donation.
     * @return the Donation
     */
    public JMoney getDonation() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.CharityDonation, JMoney.class)
                : null;
    }

    /**
     * Obtain Reference.
     * @return the Reference
     */
    public String getReference() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.Reference, String.class)
                : null;
    }

    /**
     * Obtain ThirdParty.
     * @return the ThirdParty
     */
    public Account getThirdParty() {
        return hasInfoSet
                ? theInfoSet.getAccount(EventInfoClass.ThirdParty)
                : null;
    }

    /**
     * Obtain Credit Amount.
     * @return the Credit Amount
     */
    public JMoney getCreditAmount() {
        return hasInfoSet
                ? theInfoSet.getValue(EventInfoClass.CreditAmount, JMoney.class)
                : null;
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
        EventInfoClass myClass = EventInfoSet.getClassForField(pField);
        if (myClass != null) {
            return (useInfoSet)
                    ? theInfoSet.fieldChanged(myClass)
                    : Difference.Identical;
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

    /**
     * Copy Constructor.
     * @param pList the event list
     * @param pEvent The Event to copy
     */
    public Event(final BaseEventList<? extends Event> pList,
                 final Event pEvent) {
        /* Set standard values */
        super(pList, pEvent);

        /* switch on list type */
        switch (getList().getStyle()) {
            case EDIT:
                theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
                theInfoSet.cloneDataInfoSet(pEvent.getInfoSet());
                hasInfoSet = true;
                useInfoSet = true;
                break;
            case CLONE:
            case CORE:
                theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
                hasInfoSet = true;
                useInfoSet = false;
                break;
            default:
                theInfoSet = null;
                hasInfoSet = false;
                useInfoSet = false;
                break;
        }
    }

    /**
     * Construct a new event from an Account pattern.
     * @param pList the list to build into
     * @param pLine The Line to copy
     * @throws JDataException on error
     */
    protected Event(final EventList pList,
                    final Pattern pLine) throws JDataException {
        /* Set standard values */
        super(pList, pLine);
        theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
        hasInfoSet = true;
        useInfoSet = true;

        /* If we need a tax Credit */
        if (needsTaxCredit(getCategory(), getDebit())) {
            /* Calculate the tax credit */
            setTaxCredit(calculateTaxCredit());
        }
    }

    /**
     * Edit constructor.
     * @param pList the list
     */
    public Event(final BaseEventList<? extends Event> pList) {
        super(pList);
        setControlKey(pList.getControlKey());

        /* Build InfoSet */
        theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
        hasInfoSet = true;
        useInfoSet = true;
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
     * @param pDesc the description
     * @throws JDataException on error
     */
    protected Event(final EventList pList,
                    final Integer pId,
                    final Integer pControlId,
                    final JDateDay pDate,
                    final Integer pDebit,
                    final Integer pCredit,
                    final byte[] pAmount,
                    final Integer pCategory,
                    final Boolean pReconciled,
                    final byte[] pDesc) throws JDataException {
        /* Initialise item */
        super(pList, pId, pControlId, pDate, pDebit, pCredit, pAmount, pCategory, pReconciled, pDesc);

        /* Create the InfoSet */
        theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Open constructor.
     * @param pList the list
     * @param pId the id
     * @param pDate the date
     * @param pDebit the debit account
     * @param pCredit the credit account
     * @param pAmount the amount
     * @param pCategory the category
     * @param pReconciled is the event reconciled
     * @param pDesc the description
     * @throws JDataException on error
     */
    protected Event(final EventList pList,
                    final Integer pId,
                    final JDateDay pDate,
                    final String pDebit,
                    final String pCredit,
                    final String pAmount,
                    final String pCategory,
                    final Boolean pReconciled,
                    final String pDesc) throws JDataException {
        /* Initialise item */
        super(pList, pId, pDate, pDebit, pCredit, pAmount, pCategory, pReconciled, pDesc);

        /* Create the InfoSet */
        theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    /**
     * Validate the event.
     */
    @Override
    public void validate() {
        Account myDebit = getDebit();
        Account myCredit = getCredit();
        EventCategory myCategory = getCategory();
        JUnits myDebitUnits = getDebitUnits();
        JUnits myCreditUnits = getCreditUnits();
        JMoney myTaxCred = getTaxCredit();
        // JMoney myBenefit = getBenefit();
        // JMoney myNatIns = getNatInsurance();
        // JMoney myDonation = getDonation();
        Integer myYears = getYears();
        JDilution myDilution = getDilution();

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Perform underlying checks */
        super.validate();

        /* Check for valid priced credit account */
        if ((myCredit != null)
            && (myCredit.hasUnits())) {
            /* If the date of this event is prior to the first price */
            AccountPrice myPrice = myCredit.getInitPrice();
            if ((myPrice != null)
                && (getDate().compareTo(myPrice.getDate()) < 0)) {
                addError("Event Date is prior to first priced date for Credit Account", FIELD_DATE);
            }
        }

        /* Check for valid priced debit account */
        if ((myDebit != null)
            && (myDebit.hasUnits())
            && (!Difference.isEqual(myCredit, myDebit))) {
            /* If the date of this event is prior to the first price */
            AccountPrice myPrice = myDebit.getInitPrice();
            if ((myPrice != null)
                && (getDate().compareTo(myPrice.getDate()) < 0)) {
                addError("Event Date is prior to first priced date for Debit Account", FIELD_DATE);
            }
        }

        /* If we have Credit/Debit Units */
        if ((myDebitUnits != null)
            || (myCreditUnits != null)) {
            /* If we have debit units */
            if ((myDebit != null)
                && (myDebitUnits != null)) {
                /* Debit Units are only allowed if debit is priced */
                if (!myDebit.hasUnits()) {
                    addError("Units are only allowed involving assets", EventInfoSet.getFieldForClass(EventInfoClass.DebitUnits));
                }

                /* Category of dividend cannot debit units */
                if ((myCategory != null)
                    && (isDividend())) {
                    addError("Units cannot be debited for a dividend", EventInfoSet.getFieldForClass(EventInfoClass.DebitUnits));
                }

                /* Units must be non-zero and positive */
                if ((!myDebitUnits.isNonZero())
                    || (!myDebitUnits.isPositive())) {
                    addError("Units must be non-Zero and positive", EventInfoSet.getFieldForClass(EventInfoClass.DebitUnits));
                }
            }

            /* If we have Credit units */
            if ((myCredit != null)
                && (myCreditUnits != null)) {
                /* Credit Units are only allowed if credit is priced */
                if (!myCredit.hasUnits()) {
                    addError("Units are only allowed involving assets", EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits));
                }

                /* Units must be non-zero and positive */
                if ((!myCreditUnits.isNonZero())
                    || (!myCreditUnits.isPositive())) {
                    addError("Units must be non-Zero and positive", EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits));
                }
            }
            /* If both credit/debit are both priced */
            if ((myCredit != null)
                && (myDebit != null)
                && (myCredit.hasUnits())
                && (myDebit.hasUnits())) {
                /* Category must be stock split or dividend between same account */
                if ((myCategory == null)
                    || ((!isDividend()) && (!myCategory.getCategoryTypeClass().isStockAdjustment()))) {
                    addError("Units can only refer to a single priced asset unless "
                             + "transaction is StockSplit/Adjust/Demerger/Takeover or Dividend", EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits));
                    addError("Units can only refer to a single priced asset unless "
                             + "transaction is StockSplit/Adjust/Demerger/Takeover or Dividend", EventInfoSet.getFieldForClass(EventInfoClass.DebitUnits));
                }

                /* Dividend between priced requires identical credit/debit */
                if ((myCategory != null)
                    && (isDividend())
                    && (!Difference.isEqual(myCredit, myDebit))) {
                    addError("Unit Dividends between assets must be between same asset", EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits));
                }

                /* Cannot have Credit and Debit if accounts are identical */
                if ((myCreditUnits != null)
                    && (myDebitUnits != null)
                    && (Difference.isEqual(myCredit, myDebit))) {
                    addError("Cannot credit and debit same account", EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits));
                }
            }

            /* Else check for required units */
        } else {
            if (isCategoryClass(EventCategoryClass.StockSplit)) {
                addError("Stock Split requires non-zero Units", EventInfoSet.getFieldForClass(EventInfoClass.CreditUnits));
            } else if (isCategoryClass(EventCategoryClass.StockAdjust)) {
                addError("Stock Adjustment requires non-zero Units", EventInfoSet.getFieldForClass(EventInfoClass.DebitUnits));
            }
        }

        /* If we have a dilution */
        if (myDilution != null) {
            /* If the dilution is not allowed */
            if (!needsDilution(myCategory)) {
                addError("Dilution factor given where not allowed", EventInfoSet.getFieldForClass(EventInfoClass.Dilution));
            }

            /* If the dilution is out of range */
            if (myDilution.outOfRange()) {
                addError("Dilution factor value is outside allowed range (0-1)", EventInfoSet.getFieldForClass(EventInfoClass.Dilution));
            }

            /* else if we are missing a required dilution factor */
        } else if (needsDilution(myCategory)) {
            addError("Dilution factor missing where required", EventInfoSet.getFieldForClass(EventInfoClass.Dilution));
        }

        /* If we are a taxable gain */
        if ((myCategory != null)
            && (myCategory.getCategoryTypeClass() == EventCategoryClass.TaxableGain)) {
            /* Years must be positive */
            if ((myYears == null)
                || (myYears <= 0)) {
                addError("Years must be non-zero and positive", EventInfoSet.getFieldForClass(EventInfoClass.QualifyYears));
            }

            /* Tax Credit must be non-null and positive */
            if ((myTaxCred == null)
                || (!myTaxCred.isPositive())) {
                addError("TaxCredit must be non-null", EventInfoSet.getFieldForClass(EventInfoClass.TaxCredit));
            }

            /* If we need a tax credit */
        } else if ((myCategory != null)
                   && (needsTaxCredit(myCategory, myDebit))) {
            /* Tax Credit must be non-null and positive */
            if ((myTaxCred == null)
                || (!myTaxCred.isPositive())) {
                addError("TaxCredit must be non-null", EventInfoSet.getFieldForClass(EventInfoClass.TaxCredit));
            }

            /* Years must be null */
            if (myYears != null) {
                addError("Years must be null", EventInfoSet.getFieldForClass(EventInfoClass.QualifyYears));
            }

            /* else we should not have a tax credit */
        } else if (myCategory != null) {
            /* Tax Credit must be null */
            if (myTaxCred != null) {
                addError("TaxCredit must be null", EventInfoSet.getFieldForClass(EventInfoClass.TaxCredit));
            }

            /* Years must be null */
            if (myYears != null) {
                addError("Years must be null", EventInfoSet.getFieldForClass(EventInfoClass.QualifyYears));
            }
        }

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
        TaxYearList myList = myData.getTaxYears();

        /* Ignore unless tax credit is null/zero */
        if ((getTaxCredit() != null)
            && (getTaxCredit().isNonZero())) {
            return getTaxCredit();
        }

        /* Ignore unless category is interest/dividend */
        if ((getCategory() == null)
            || ((!isInterest()) && (!isDividend()))) {
            return getTaxCredit();
        }

        /* Access the relevant tax year */
        TaxYear myTax = myList.findTaxYearForDate(getDate());

        /* Determine the tax credit rate */
        JRate myRate = (isInterest())
                ? myTax.getIntTaxRate()
                : myTax.getDivTaxRate();

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
     * Set a new xferDelay.
     * @param pXferDelay the new xferDelay
     * @throws JDataException on error
     */
    public void setXferDelay(final Integer pXferDelay) throws JDataException {
        setInfoSetValue(EventInfoClass.QualifyYears, pXferDelay);
    }

    /**
     * Set a new NatInsurance.
     * @param pNatIns the new insurance
     * @throws JDataException on error
     */
    public void setNatInsurance(final JMoney pNatIns) throws JDataException {
        setInfoSetValue(EventInfoClass.NatInsurance, pNatIns);
    }

    /**
     * Set a new Benefit.
     * @param pBenefit the new benefit
     * @throws JDataException on error
     */
    public void setBenefit(final JMoney pBenefit) throws JDataException {
        setInfoSetValue(EventInfoClass.Benefit, pBenefit);
    }

    /**
     * Set a new Pension.
     * @param pPension the new pension
     * @throws JDataException on error
     */
    public void setPension(final JMoney pPension) throws JDataException {
        setInfoSetValue(EventInfoClass.Pension, pPension);
    }

    /**
     * Set a new Donation.
     * @param pDonation the new donation
     * @throws JDataException on error
     */
    public void setDonation(final JMoney pDonation) throws JDataException {
        setInfoSetValue(EventInfoClass.CharityDonation, pDonation);
    }

    /**
     * Set a new Credit Amount.
     * @param pValue the new credit amount
     * @throws JDataException on error
     */
    public void setCreditAmount(final JMoney pValue) throws JDataException {
        setInfoSetValue(EventInfoClass.CreditAmount, pValue);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws JDataException on error
     */
    public void setReference(final String pReference) throws JDataException {
        setInfoSetValue(EventInfoClass.Reference, pReference);
    }

    /**
     * Set a new ThirdParty.
     * @param pParty the new thirdParty
     * @throws JDataException on error
     */
    public void setThirdParty(final Account pParty) throws JDataException {
        setInfoSetValue(EventInfoClass.ThirdParty, pParty);
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

    @Override
    public void touchUnderlyingItems() {
        /* touch underlying items */
        super.touchUnderlyingItems();

        /* touch infoSet items */
        theInfoSet.touchUnderlyingItems();
    }

    /**
     * The BaseEvent List class.
     * @param <T> the Event type
     */
    public abstract static class BaseEventList<T extends Event>
            extends EventBaseList<T> {
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
         * Set the eventInfoList.
         * @param pList the event info list
         */
        protected void setEventInfos(final EventInfoList pList) {
            theInfoList = pList;
        }

        /**
         * Set the eventInfoTypeList.
         * @param pList the event info type list
         */
        protected void setEventInfoTypes(final EventInfoTypeList pList) {
            theInfoTypeList = pList;
        }

        /**
         * Construct an empty CORE event list.
         * @param pData the DataSet for the list
         * @param pClass the item class
         */
        protected BaseEventList(final FinanceData pData,
                                final Class<T> pClass) {
            super(pData, pClass);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected BaseEventList(final BaseEventList<T> pSource) {
            super(pSource);
        }
    }

    /**
     * The Event List class.
     */
    public static class EventList
            extends BaseEventList<Event> {
        /**
         * Local Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(EventList.class.getSimpleName(), DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        /**
         * Construct an empty CORE event list.
         * @param pData the DataSet for the list
         */
        protected EventList(final FinanceData pData) {
            super(pData, Event.class);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        protected EventList(final EventList pSource) {
            super(pSource);
        }

        @Override
        protected EventList getEmptyList(final ListStyle pStyle) {
            EventList myList = new EventList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        @Override
        public EventList cloneList(final DataSet<?> pDataSet) throws JDataException {
            return (EventList) super.cloneList(pDataSet);
        }

        @Override
        public EventList deriveList(final ListStyle pStyle) throws JDataException {
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
            EventList myList = getEmptyList(ListStyle.COPY);
            myList.setStyle(ListStyle.COPY);

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
            EventList myList = getEmptyList(ListStyle.EDIT);
            myList.setRange(pRange);

            /* Store InfoType list */
            myList.setEventInfoTypes(getEventInfoTypes());

            /* Create info List */
            EventInfoList myEventInfo = getEventInfo();
            myList.setEventInfos(myEventInfo.getEmptyList(ListStyle.EDIT));

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
            EventList myList = getEmptyList(ListStyle.EDIT);
            myList.setRange(pTaxYear.getRange());

            /* Store InfoType list */
            myList.setEventInfoTypes(getEventInfoTypes());

            /* Create info List */
            EventInfoList myEventInfo = getEventInfo();
            myList.setEventInfos(myEventInfo.getEmptyList(ListStyle.EDIT));

            /* Access the underlying data */
            PatternList myPatterns = getDataSet().getPatterns();
            Iterator<Pattern> myIterator = myPatterns.iterator();
            Event myEvent;

            /* Loop through the Patterns */
            while (myIterator.hasNext()) {
                Pattern myCurr = myIterator.next();

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
         * Add a new item to the list.
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public Event addCopyItem(final DataItem pItem) {
            if (pItem instanceof Event) {
                Event myEvent = new Event(this, (Event) pItem);
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
            myEvent.setDate(getValidDateRange().getStart());

            /* Add to list and return */
            add(myEvent);
            return myEvent;
        }

        /**
         * Allow an event to be added.
         * @param pId the id
         * @param pDate the date
         * @param pDebit the debit account
         * @param pCredit the credit account
         * @param pAmount the amount
         * @param pCategory the category
         * @param pReconciled is the event reconciled
         * @param pDesc the description
         * @return the new event
         * @throws JDataException on error
         */
        public Event addOpenItem(final Integer pId,
                                 final JDateDay pDate,
                                 final String pDebit,
                                 final String pCredit,
                                 final String pAmount,
                                 final String pCategory,
                                 final Boolean pReconciled,
                                 final String pDesc) throws JDataException {
            /* Create the new Event */
            Event myEvent = new Event(this, pId, pDate, pDebit, pCredit, pAmount, pCategory, pReconciled, pDesc);

            /* Check that this EventId has not been previously added */
            if (!isIdUnique(pId)) {
                myEvent.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myEvent, ERROR_VALIDATION);
            }

            /* Add the Event to the list */
            append(myEvent);
            return myEvent;
        }

        /**
         * Allow an event to be added.
         * @param pId the id
         * @param pControlId the control Id
         * @param pDate the date
         * @param pDebitId the debit id
         * @param pCreditId the credit id
         * @param pAmount the amount
         * @param pCatId the category id
         * @param pReconciled is the event reconciled
         * @param pDesc the description
         * @throws JDataException on error
         */
        public void addSecureItem(final Integer pId,
                                  final Integer pControlId,
                                  final JDateDay pDate,
                                  final Integer pDebitId,
                                  final Integer pCreditId,
                                  final byte[] pAmount,
                                  final Integer pCatId,
                                  final Boolean pReconciled,
                                  final byte[] pDesc) throws JDataException {
            /* Create the new Event */
            Event myEvent = new Event(this, pId, pControlId, pDate, pDebitId, pCreditId, pAmount, pCatId, pReconciled, pDesc);

            /* Check that this EventId has not been previously added */
            if (!isIdUnique(pId)) {
                myEvent.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JDataException(ExceptionClass.DATA, myEvent, ERROR_VALIDATION);
            }

            /* Add the Event to the list */
            append(myEvent);
        }
    }
}
