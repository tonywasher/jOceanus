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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.DataState;
import net.sourceforge.joceanus.jmetis.viewer.Difference;
import net.sourceforge.joceanus.jmetis.viewer.EditState;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.Pattern.PatternList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoItem;
import net.sourceforge.joceanus.jprometheus.data.DataValues.InfoSetItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JRate;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * New version of Event DataItem utilising EventInfo.
 * @author Tony Washer
 */
public class Event
        extends EventBase
        implements InfoSetItem<MoneyWiseDataType> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.EVENT.getItemName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = MoneyWiseDataType.EVENT.getListName();

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(Event.class.getName());

    /**
     * Local Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(OBJECT_NAME, EventBase.FIELD_DEFS);

    /**
     * EventInfoSet field Id.
     */
    private static final JDataField FIELD_INFOSET = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataInfoSet"));

    /**
     * Bad InfoSet Error Text.
     */
    private static final String ERROR_BADINFOSET = NLS_BUNDLE.getString("ErrorBadInfoSet");

    /**
     * Early Date Error Text.
     */
    private static final String ERROR_BADDATE = NLS_BUNDLE.getString("ErrorBadDate");

    /**
     * Circular update Error Text.
     */
    private static final String ERROR_CIRCULAR = NLS_BUNDLE.getString("ErrorCircular");

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

    @Override
    public EventInfoSet getInfoSet() {
        return theInfoSet;
    }

    @Override
    public Event getParent() {
        return (Event) super.getParent();
    }

    @Override
    public Iterator<Event> childIterator() {
        /* No iterator if we are not a group or else we are a child */
        if (!isSplit() || isChild()) {
            return null;
        }

        return getList().getGroup(this).iterator();
    }

    /**
     * Obtain Debit Units.
     * @return the Debit Units
     */
    public final JUnits getDebitUnits() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.DEBITUNITS, JUnits.class)
                         : null;
    }

    /**
     * Obtain Credit Units.
     * @return the Credit Units
     */
    public final JUnits getCreditUnits() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.CREDITUNITS, JUnits.class)
                         : null;
    }

    /**
     * Obtain Tax Credit.
     * @return the Tax Credit
     */
    public final JMoney getTaxCredit() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.TAXCREDIT, JMoney.class)
                         : null;
    }

    /**
     * Obtain Dilution.
     * @return the Dilution
     */
    public final JDilution getDilution() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.DILUTION, JDilution.class)
                         : null;
    }

    /**
     * Obtain Qualifying Years.
     * @return the Years
     */
    public final Integer getYears() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.QUALIFYYEARS, Integer.class)
                         : null;
    }

    /**
     * Obtain credit date.
     * @return the credit date
     */
    public final JDateDay getCreditDate() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.CREDITDATE, JDateDay.class)
                         : null;
    }

    /**
     * Obtain National Insurance.
     * @return the NatInsurance
     */
    public final JMoney getNatInsurance() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.NATINSURANCE, JMoney.class)
                         : null;
    }

    /**
     * Obtain Deemed Benefit.
     * @return the Benefit
     */
    public final JMoney getDeemedBenefit() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.DEEMEDBENEFIT, JMoney.class)
                         : null;
    }

    /**
     * Obtain Pension.
     * @return the Pension
     */
    public final JMoney getPension() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.PENSION, JMoney.class)
                         : null;
    }

    /**
     * Obtain Donation.
     * @return the Donation
     */
    public final JMoney getCharityDonation() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.CHARITYDONATION, JMoney.class)
                         : null;
    }

    /**
     * Obtain Reference.
     * @return the Reference
     */
    public final String getReference() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.REFERENCE, String.class)
                         : null;
    }

    /**
     * Obtain Comments.
     * @return the Comments
     */
    public final String getComments() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.COMMENTS, String.class)
                         : null;
    }

    /**
     * Obtain ThirdParty.
     * @return the ThirdParty
     */
    public final Account getThirdParty() {
        return hasInfoSet
                         ? theInfoSet.getAccount(EventInfoClass.THIRDPARTY)
                         : null;
    }

    /**
     * Obtain Credit Amount.
     * @return the Credit Amount
     */
    public final JMoney getCreditAmount() {
        return hasInfoSet
                         ? theInfoSet.getValue(EventInfoClass.CREDITAMOUNT, JMoney.class)
                         : null;
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
        EventInfoClass myClass = EventInfoSet.getClassForField(pField);
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
    public Event getBase() {
        return (Event) super.getBase();
    }

    @Override
    public EventList getList() {
        return (EventList) super.getList();
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
     * @throws JOceanusException on error
     */
    protected Event(final EventList pList,
                    final Pattern pLine) throws JOceanusException {
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

        /* Build InfoSet */
        theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
        hasInfoSet = true;
        useInfoSet = true;
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values
     * @throws JOceanusException on error
     */
    private Event(final EventList pList,
                  final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Create the InfoSet */
        theInfoSet = new EventInfoSet(this, pList.getEventInfoTypes(), pList.getEventInfo());
        hasInfoSet = true;
        useInfoSet = false;
    }

    @Override
    public void resolveDataSetLinks() throws JOceanusException {
        /* Update the Event details */
        super.resolveDataSetLinks();

        /* Resolve data links */
        resolveDataLink(FIELD_PARENT, getList());
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

        /* Header is always valid */
        if (isHeader()) {
            setValidEdit();
            return;
        }

        /* Perform underlying checks */
        super.validate();

        /* Check for valid priced credit account */
        if ((myCredit != null) && (myCredit.hasUnits())) {
            /* If the date of this event is prior to the first price */
            SecurityPrice myPrice = myCredit.getInitPrice();
            if ((myPrice != null) && (getDate().compareTo(myPrice.getDate()) < 0)) {
                addError(ERROR_BADDATE, FIELD_CREDIT);
            }
        }

        /* Check for valid priced debit account */
        if ((myDebit != null) && (myDebit.hasUnits()) && (!Difference.isEqual(myCredit, myDebit))) {
            /* If the date of this event is prior to the first price */
            SecurityPrice myPrice = myDebit.getInitPrice();
            if ((myPrice != null) && (getDate().compareTo(myPrice.getDate()) < 0)) {
                addError(ERROR_BADDATE, FIELD_DEBIT);
            }
        }

        /* Cannot have Credit and Debit if accounts are identical */
        if ((myCreditUnits != null) && (myDebitUnits != null) && (Difference.isEqual(myCredit, myDebit))) {
            addError(ERROR_CIRCULAR, EventInfoSet.getFieldForClass(EventInfoClass.CREDITUNITS));
        }

        /* If we have a category and an infoSet */
        if ((myCategory != null) && (theInfoSet != null)) {
            /* Validate the InfoSet */
            theInfoSet.validate();
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
    public final JMoney calculateTaxCredit() {
        MoneyWiseData myData = getDataSet();
        TaxYearList myList = myData.getTaxYears();

        /* Ignore unless tax credit is null/zero */
        if ((getTaxCredit() != null) && (getTaxCredit().isNonZero())) {
            return getTaxCredit();
        }

        /* Ignore unless category is interest/dividend */
        if ((getCategory() == null) || ((!isInterest()) && (!isDividend()))) {
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
     * @throws JOceanusException on error
     */
    public final void setDebitUnits(final JUnits pUnits) throws JOceanusException {
        setInfoSetValue(EventInfoClass.DEBITUNITS, pUnits);
    }

    /**
     * Set a new Credit Units.
     * @param pUnits the new units
     * @throws JOceanusException on error
     */
    public final void setCreditUnits(final JUnits pUnits) throws JOceanusException {
        setInfoSetValue(EventInfoClass.CREDITUNITS, pUnits);
    }

    /**
     * Set a new TaxCredit.
     * @param pCredit the new credit
     * @throws JOceanusException on error
     */
    public final void setTaxCredit(final JMoney pCredit) throws JOceanusException {
        setInfoSetValue(EventInfoClass.TAXCREDIT, pCredit);
    }

    /**
     * Set a new Dilution.
     * @param pDilution the new dilution
     * @throws JOceanusException on error
     */
    public final void setDilution(final JDilution pDilution) throws JOceanusException {
        setInfoSetValue(EventInfoClass.DILUTION, pDilution);
    }

    /**
     * Set a new Qualifying Years.
     * @param pYears the new years
     * @throws JOceanusException on error
     */
    public final void setYears(final Integer pYears) throws JOceanusException {
        setInfoSetValue(EventInfoClass.QUALIFYYEARS, pYears);
    }

    /**
     * Set a new Credit Date.
     * @param pCreditDate the new credit date
     * @throws JOceanusException on error
     */
    public final void setCreditDate(final JDateDay pCreditDate) throws JOceanusException {
        setInfoSetValue(EventInfoClass.CREDITDATE, pCreditDate);
    }

    /**
     * Set a new NatInsurance.
     * @param pNatIns the new insurance
     * @throws JOceanusException on error
     */
    public final void setNatInsurance(final JMoney pNatIns) throws JOceanusException {
        setInfoSetValue(EventInfoClass.NATINSURANCE, pNatIns);
    }

    /**
     * Set a new Benefit.
     * @param pBenefit the new benefit
     * @throws JOceanusException on error
     */
    public final void setBenefit(final JMoney pBenefit) throws JOceanusException {
        setInfoSetValue(EventInfoClass.DEEMEDBENEFIT, pBenefit);
    }

    /**
     * Set a new Pension.
     * @param pPension the new pension
     * @throws JOceanusException on error
     */
    public final void setPension(final JMoney pPension) throws JOceanusException {
        setInfoSetValue(EventInfoClass.PENSION, pPension);
    }

    /**
     * Set a new Donation.
     * @param pDonation the new donation
     * @throws JOceanusException on error
     */
    public final void setDonation(final JMoney pDonation) throws JOceanusException {
        setInfoSetValue(EventInfoClass.CHARITYDONATION, pDonation);
    }

    /**
     * Set a new Credit Amount.
     * @param pValue the new credit amount
     * @throws JOceanusException on error
     */
    public final void setCreditAmount(final JMoney pValue) throws JOceanusException {
        setInfoSetValue(EventInfoClass.CREDITAMOUNT, pValue);
    }

    /**
     * Set a new Reference.
     * @param pReference the new reference
     * @throws JOceanusException on error
     */
    public final void setReference(final String pReference) throws JOceanusException {
        setInfoSetValue(EventInfoClass.REFERENCE, pReference);
    }

    /**
     * Set new Comments.
     * @param pComments the new comments
     * @throws JOceanusException on error
     */
    public final void setComments(final String pComments) throws JOceanusException {
        setInfoSetValue(EventInfoClass.COMMENTS, pComments);
    }

    /**
     * Set a new ThirdParty.
     * @param pParty the new thirdParty
     * @throws JOceanusException on error
     */
    public final void setThirdParty(final Account pParty) throws JOceanusException {
        setInfoSetValue(EventInfoClass.THIRDPARTY, pParty);
    }

    /**
     * Set an infoSet value.
     * @param pInfoClass the class of info to set
     * @param pValue the value to set
     * @throws JOceanusException on error
     */
    private void setInfoSetValue(final EventInfoClass pInfoClass,
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

    @Override
    public boolean relatesTo(final Account pAccount) {
        /* Determine standard relations */
        boolean myResult = super.relatesTo(pAccount);

        /* If not currently related, check thirdParty */
        if (!myResult) {
            myResult = Difference.isEqual(pAccount, getThirdParty());
        }

        /* Return the result */
        return myResult;
    }

    /**
     * Update base event from an edited event.
     * @param pEvent the edited event
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem<?> pEvent) {
        /* Can only update from an event */
        if (!(pEvent instanceof Event)) {
            return false;
        }
        Event myEvent = (Event) pEvent;

        /* Store the current detail into history */
        pushHistory();

        /* Apply basic changes */
        applyBasicChanges(myEvent);

        /* Check for changes */
        return checkForHistory();
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
        protected BaseEventList(final MoneyWiseData pData,
                                final Class<T> pClass) {
            super(pData, pClass, MoneyWiseDataType.EVENT);
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
        protected static final JDataFields FIELD_DEFS = new JDataFields(LIST_NAME, DataList.FIELD_DEFS);

        @Override
        public JDataFields declareFields() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public JDataFields getItemFields() {
            return Event.FIELD_DEFS;
        }

        /**
         * EventGroupList field Id.
         */
        private static final JDataField FIELD_EVENTGROUPS = FIELD_DEFS.declareLocalField(NLS_BUNDLE.getString("DataGroups"));

        /**
         * EventGroupMap.
         */
        private final Map<Integer, EventGroup<Event>> theGroups = new HashMap<Integer, EventGroup<Event>>();

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_EVENTGROUPS.equals(pField)) {
                return theGroups.isEmpty()
                                          ? JDataFieldValue.SKIP
                                          : theGroups;
            }

            /* Pass onwards */
            return super.getFieldValue(pField);
        }

        /**
         * Construct an empty CORE event list.
         * @param pData the DataSet for the list
         */
        protected EventList(final MoneyWiseData pData) {
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
        public EventList cloneList(final DataSet<?, ?> pDataSet) throws JOceanusException {
            return (EventList) super.cloneList(pDataSet);
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

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Check the range */
                int myResult = pRange.compareTo(myCurr.getDate());

                /* Handle out of range */
                if (myResult > 0) {
                    continue;
                } else if (myResult < 0) {
                    break;
                }

                /* Build the new linked event and add it to the list */
                Event myEvent = new Event(myList, myCurr);
                myList.append(myEvent);

                /* If this is a child event */
                if (myEvent.isChild()) {
                    /* Register child against parent (in this edit list) */
                    myList.registerChild(myEvent);
                }
            }

            /* Return the List */
            return myList;
        }

        /**
         * Get an EditList for a new TaxYear.
         * @param pTaxYear the new TaxYear
         * @return the edit list
         * @throws JOceanusException on error
         */
        public EventList deriveEditList(final TaxYear pTaxYear) throws JOceanusException {
            /* Build an empty List */
            EventList myList = getEmptyList(ListStyle.EDIT);
            myList.setRange(pTaxYear.getDateRange());

            /* Store InfoType list */
            myList.setEventInfoTypes(getEventInfoTypes());

            /* Create info List */
            EventInfoList myEventInfo = getEventInfo();
            myList.setEventInfos(myEventInfo.getEmptyList(ListStyle.EDIT));

            /* Loop through the Patterns */
            PatternList myPatterns = getDataSet().getPatterns();
            Iterator<Pattern> myIterator = myPatterns.iterator();
            while (myIterator.hasNext()) {
                Pattern myCurr = myIterator.next();

                /* Ignore deleted patterns */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Access a copy of the base date */
                JDateDay myDate = new JDateDay(myCurr.getDate());

                /* Loop while we have an event to add */
                for (;;) {
                    /* Access next event and break loop if no more */
                    Event myEvent = myCurr.nextEvent(myList, pTaxYear, myDate);
                    if (myEvent == null) {
                        break;
                    }

                    /* Add it to the extract */
                    myList.append(myEvent);

                    /* If this is a child event */
                    if (myEvent.isChild()) {
                        /* Register child against parent (in this edit list) */
                        myList.registerChild(myEvent);
                    }
                }
            }

            /* Sort the list */
            myList.reSort();

            /* Return the List */
            return myList;
        }

        /**
         * Register child into event group.
         * @param pChild the child to register
         */
        public void registerChild(final Event pChild) {
            /* Access parent */
            Event myParent = pChild.getParent();
            Integer myId = myParent.getId();
            myParent = findItemById(myId);

            /* Access EventGroup */
            EventGroup<Event> myGroup = theGroups.get(myId);
            if (myGroup == null) {
                myGroup = new EventGroup<Event>(myParent, Event.class);
                theGroups.put(myId, myGroup);
            }

            /* Register the child */
            myGroup.registerChild(pChild);
        }

        /**
         * Obtain the group for a parent.
         * @param pParent the parent event
         * @return the group
         */
        public EventGroup<Event> getGroup(final Event pParent) {
            return theGroups.get(pParent.getId());
        }

        /**
         * Reset groups.
         */
        public void resetGroups() {
            theGroups.clear();
        }

        /**
         * Add a new item to the list.
         * @param pItem the item to add
         * @return the newly added item
         */
        @Override
        public Event addCopyItem(final DataItem<?> pItem) {
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

        @Override
        public Event addValuesItem(final DataValues<MoneyWiseDataType> pValues) throws JOceanusException {
            /* If the item has children */
            if (pValues.hasChildren()) {
                /* Note that the item is split */
                pValues.addValue(FIELD_SPLIT, Boolean.TRUE);
            }

            /* Create the event */
            Event myEvent = new Event(this, pValues);

            /* Check that this EventId has not been previously added */
            if (!isIdUnique(myEvent.getId())) {
                myEvent.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new JMoneyWiseDataException(myEvent, ERROR_VALIDATION);
            }

            /* Add to the list */
            append(myEvent);

            /* Loop through the info items */
            if (pValues.hasInfoItems()) {
                /* Loop through the items */
                Iterator<InfoItem<MoneyWiseDataType>> myIterator = pValues.infoIterator();
                while (myIterator.hasNext()) {
                    InfoItem<MoneyWiseDataType> myItem = myIterator.next();

                    /* Build info */
                    DataValues<MoneyWiseDataType> myValues = myItem.getValues(myEvent);
                    getEventInfo().addValuesItem(myValues);
                }
            }

            /* Loop through the children */
            if (pValues.hasChildren()) {
                /* Loop through the items */
                Iterator<DataValues<MoneyWiseDataType>> myIterator = pValues.childIterator();
                while (myIterator.hasNext()) {
                    DataValues<MoneyWiseDataType> myValues = myIterator.next();

                    /* Note that the item is split */
                    myValues.addValue(FIELD_SPLIT, Boolean.TRUE);
                    myValues.addValue(FIELD_PARENT, myEvent);

                    /* Copy missing values from parent */
                    myValues.addValue(FIELD_DATE, pValues.getValue(FIELD_DATE));
                    if (myValues.getValue(FIELD_DEBIT) == null) {
                        myValues.addValue(FIELD_DEBIT, pValues.getValue(FIELD_DEBIT));
                    }
                    if (myValues.getValue(FIELD_CREDIT) == null) {
                        myValues.addValue(FIELD_CREDIT, pValues.getValue(FIELD_CREDIT));
                    }

                    /* Build item */
                    addValuesItem(myValues);
                }
            }

            /* Return it */
            return myEvent;
        }
    }
}
