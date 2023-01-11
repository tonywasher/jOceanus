/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisValueSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency.FrequencyList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataMapItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Scheduled event.
 * @author Tony Washer
 */
public class Schedule
        extends DataItem
        implements Comparable<Schedule> {
    /**
     * The name of the object.
     */
    public static final String OBJECT_NAME = MoneyWiseDataType.SCHEDULE.getItemName();

    /**
     * The name of the object.
     */
    public static final String LIST_NAME = MoneyWiseDataType.SCHEDULE.getListName();

    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(OBJECT_NAME, DataItem.FIELD_DEFS);

    /**
     * StartDate Field Id.
     */
    public static final MetisLetheField FIELD_STARTDATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SCHEDULE_STARTDATE.getValue(), MetisDataType.DATE);

    /**
     * EndDate Field Id.
     */
    public static final MetisLetheField FIELD_ENDDATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SCHEDULE_ENDDATE.getValue(), MetisDataType.DATE);

    /**
     * Frequency Field Id.
     */
    public static final MetisLetheField FIELD_FREQ = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataType.FREQUENCY.getItemName(), MetisDataType.LINK);

    /**
     * RepeatFrequency Field Id.
     */
    public static final MetisLetheField FIELD_REPFREQ = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SCHEDULE_REPEATFREQ.getValue(), MetisDataType.LINK);

    /**
     * Pattern Field Id.
     */
    public static final MetisLetheField FIELD_PATTERN = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SCHEDULE_PATTERN.getValue(), MetisDataType.INTEGER);

    /**
     * NextDate Field Id.
     */
    public static final MetisLetheField FIELD_NEXTDATE = FIELD_DEFS.declareEqualityValueField(MoneyWiseDataResource.SCHEDULE_NEXTDATE.getValue(), MetisDataType.DATE);

    /**
     * Bad Frequency error.
     */
    private static final String ERROR_BADFREQ = MoneyWiseDataResource.SCHEDULE_ERROR_FREQINVALID.getValue();

    /**
     * Before Start Date error.
     */
    private static final String ERROR_BEFORESTART = MoneyWiseDataResource.SCHEDULE_ERROR_BEFORESTARTDATE.getValue();

    /**
     * After End Date error.
     */
    private static final String ERROR_AFTEREND = MoneyWiseDataResource.SCHEDULE_ERROR_AFTERENDDATE.getValue();

    /**
     * Copy Constructor.
     * @param pList the list
     * @param pSchedule The Schedule
     */
    protected Schedule(final ScheduleList pList,
                       final Schedule pSchedule) {
        /* Simply initialise as Schedule */
        super(pList, pSchedule);
    }

    /**
     * Values constructor.
     * @param pList the List to add to
     * @param pValues the values
     * @throws OceanusException on error
     */
    private Schedule(final ScheduleList pList,
                     final DataValues pValues) throws OceanusException {
        /* Initialise the item */
        super(pList, pValues);

        /* Access parsers */
        final TethysUIDataFormatter myFormatter = getDataSet().getDataFormatter();
        final TethysDateFormatter myParser = myFormatter.getDateFormatter();

        /* Protect against exceptions */
        try {
            /* Store the Frequency */
            Object myValue = pValues.getValue(FIELD_FREQ);
            if (myValue instanceof Frequency) {
                setValueFrequency((Frequency) myValue);
            } else if (myValue instanceof Integer) {
                setValueFrequency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueFrequency((String) myValue);
            }

            /* Store the Repeat Frequency */
            myValue = pValues.getValue(FIELD_REPFREQ);
            if (myValue instanceof Frequency) {
                setValueRepeatFrequency((Frequency) myValue);
            } else if (myValue instanceof Integer) {
                setValueRepeatFrequency((Integer) myValue);
            } else if (myValue instanceof String) {
                setValueRepeatFrequency((String) myValue);
            }

            /* Store the Pattern */
            myValue = pValues.getValue(FIELD_PATTERN);
            if (myValue instanceof String) {
                setValuePattern((String) myValue);
            } else if (myValue instanceof Integer) {
                setValuePattern((Integer) myValue);
            }

            /* Store the Date */
            myValue = pValues.getValue(FIELD_STARTDATE);
            if (myValue instanceof TethysDate) {
                setValueStartDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                setValueStartDate(myParser.parseDate((String) myValue));
            }

            /* Store the EndDate */
            myValue = pValues.getValue(FIELD_ENDDATE);
            if (myValue instanceof TethysDate) {
                setValueEndDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                setValueEndDate(myParser.parseDate((String) myValue));
            }

            /* Store the NextDate */
            myValue = pValues.getValue(FIELD_NEXTDATE);
            if (myValue instanceof TethysDate) {
                setValueNextDate((TethysDate) myValue);
            } else if (myValue instanceof String) {
                setValueNextDate(myParser.parseDate((String) myValue));
            }

            /* Catch Exceptions */
        } catch (IllegalArgumentException e) {
            /* Pass on exception */
            throw new MoneyWiseDataException(this, ERROR_CREATEITEM, e);
        }
    }

    @Override
    public MetisFields declareFields() {
        return FIELD_DEFS;
    }

    @Override
    public boolean includeXmlField(final MetisLetheField pField) {
        /* Determine whether fields should be included */
        if (FIELD_STARTDATE.equals(pField)) {
            return true;
        }
        if (FIELD_FREQ.equals(pField)) {
            return true;
        }
        if (FIELD_REPFREQ.equals(pField)) {
            return hasRepeatFrequency() || hasRepeatInterval();
        }
        if (FIELD_PATTERN.equals(pField)) {
            return hasPattern();
        }
        if (FIELD_ENDDATE.equals(pField)) {
            return true;
        }
        if (FIELD_NEXTDATE.equals(pField)) {
            return true;
        }

        /* Pass call on */
        return super.includeXmlField(pField);
    }

    @Override
    public final MoneyWiseData getDataSet() {
        return (MoneyWiseData) super.getDataSet();
    }

    /**
     * Obtain StartDate.
     * @return the date
     */
    public TethysDate getStartDate() {
        return getStartDate(getValueSet());
    }

    /**
     * Obtain EndDate.
     * @return the date
     */
    public TethysDate getEndDate() {
        return getEndDate(getValueSet());
    }

    /**
     * Do we have a repeat frequency?
     * @return true/false
     */
    public boolean hasRepeatFrequency() {
        final Frequency myFreq = getFrequency();
        return myFreq != null
               && myFreq.hasRepeatFrequency();
    }

    /**
     * Do we have a frequency interval?
     * @return true/false
     */
    public boolean hasRepeatInterval() {
        final Frequency myFreq = getFrequency();
        return myFreq != null
               && myFreq.hasRepeatInterval();
    }

    /**
     * Do we have a pattern?
     * @return true/false
     */
    public boolean hasPattern() {
        final Frequency myFreq = getFrequency();
        return myFreq != null
               && myFreq.hasPattern();
    }

    /**
     * Obtain Frequency.
     * @return the frequency
     */
    public Frequency getFrequency() {
        return getFrequency(getValueSet());
    }

    /**
     * Obtain FrequencyId.
     * @return the frequencyId
     */
    public Integer getFrequencyId() {
        final Frequency myFreq = getFrequency();
        return (myFreq == null)
                                ? null
                                : myFreq.getId();
    }

    /**
     * Obtain Repeat Frequency.
     * @return the repeat frequency
     */
    public Frequency getRepeatFrequency() {
        return hasRepeatFrequency()
                                    ? getRepeatFrequency(getValueSet())
                                    : null;
    }

    /**
     * Obtain RepeatFrequencyId.
     * @return the repeat frequencyId
     */
    public Integer getRepeatFrequencyId() {
        final Frequency myFreq = getRepeatFrequency();
        return (myFreq == null)
                                ? getRepeatInterval()
                                : myFreq.getId();
    }

    /**
     * Obtain Repeat Interval.
     * @return the repeat interval
     */
    public Integer getRepeatInterval() {
        return hasRepeatInterval()
                                   ? getRepeatInterval(getValueSet())
                                   : null;
    }

    /**
     * Obtain Pattern.
     * @return the pattern
     */
    public SchedulePattern getPattern() {
        return getPattern(getValueSet());
    }

    /**
     * Obtain FrequencyId.
     * @return the frequencyId
     */
    public Integer getPatternValue() {
        final SchedulePattern myPattern = getPattern();
        return (myPattern == null)
                                   ? null
                                   : myPattern.getPatternValue();
    }

    /**
     * Obtain NextDate.
     * @return the date
     */
    public TethysDate getNextDate() {
        return getNextDate(getValueSet());
    }

    /**
     * Obtain StartDate.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static TethysDate getStartDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_STARTDATE, TethysDate.class);
    }

    /**
     * Obtain NextDate.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static TethysDate getEndDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_ENDDATE, TethysDate.class);
    }

    /**
     * Obtain Frequency.
     * @param pValueSet the valueSet
     * @return the Frequency
     */
    public static Frequency getFrequency(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_FREQ, Frequency.class);
    }

    /**
     * Obtain Repeat Frequency.
     * @param pValueSet the valueSet
     * @return the Repeat Frequency
     */
    public static Frequency getRepeatFrequency(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_REPFREQ, Frequency.class);
    }

    /**
     * Obtain Repeat Interval.
     * @param pValueSet the valueSet
     * @return the Frequency Interval
     */
    public static Integer getRepeatInterval(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_REPFREQ, Integer.class);
    }

    /**
     * Obtain Pattern.
     * @param pValueSet the valueSet
     * @return the Pattern
     */
    public static SchedulePattern getPattern(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_PATTERN, SchedulePattern.class);
    }

    /**
     * Obtain NextDate.
     * @param pValueSet the valueSet
     * @return the date
     */
    public static TethysDate getNextDate(final MetisValueSet pValueSet) {
        return pValueSet.getValue(FIELD_NEXTDATE, TethysDate.class);
    }

    /**
     * Set start date value.
     * @param pValue the start date
     */
    private void setValueStartDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_STARTDATE, pValue);
    }

    /**
     * Set end date value.
     * @param pValue the end date
     */
    private void setValueEndDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_ENDDATE, pValue);
    }

    /**
     * Set frequency value.
     * @param pValue the frequency
     */
    private void setValueFrequency(final Frequency pValue) {
        getValueSet().setValue(FIELD_FREQ, pValue);
    }

    /**
     * Set frequency id.
     * @param pId the frequency id
     */
    private void setValueFrequency(final Integer pId) {
        getValueSet().setValue(FIELD_FREQ, pId);
    }

    /**
     * Set frequency name.
     * @param pName the frequency name
     */
    private void setValueFrequency(final String pName) {
        getValueSet().setValue(FIELD_FREQ, pName);
    }

    /**
     * Set repeat frequency value.
     * @param pValue the frequency
     */
    private void setValueRepeatFrequency(final Frequency pValue) {
        getValueSet().setValue(FIELD_REPFREQ, pValue);
    }

    /**
     * Set repeat frequency value.
     * @param pValue the frequency
     */
    private void setValueRepeatFrequency(final Integer pValue) {
        getValueSet().setValue(FIELD_REPFREQ, pValue);
    }

    /**
     * Set repeat frequency value.
     * @param pValue the frequency
     */
    private void setValueRepeatFrequency(final String pValue) {
        getValueSet().setValue(FIELD_REPFREQ, pValue);
    }

    /**
     * Set frequency interval value.
     * @param pValue the interval
     */
    private void setValueRepeatInterval(final Integer pValue) {
        getValueSet().setValue(FIELD_REPFREQ, pValue);
    }

    /**
     * Set pattern value.
     * @param pValue the pattern
     */
    private void setValuePattern(final SchedulePattern pValue) {
        getValueSet().setValue(FIELD_PATTERN, pValue);
    }

    /**
     * Set pattern value.
     * @param pValue the pattern
     */
    private void setValuePattern(final Integer pValue) {
        getValueSet().setValue(FIELD_PATTERN, pValue);
    }

    /**
     * Set pattern value.
     * @param pValue the pattern
     */
    private void setValuePattern(final String pValue) {
        getValueSet().setValue(FIELD_PATTERN, pValue);
    }

    /**
     * Set start date value.
     * @param pValue the start date
     */
    private void setValueNextDate(final TethysDate pValue) {
        getValueSet().setValue(FIELD_NEXTDATE, pValue);
    }

    @Override
    public Schedule getBase() {
        return (Schedule) super.getBase();
    }

    @Override
    public ScheduleList getList() {
        return (ScheduleList) super.getList();
    }

    @Override
    public int compareTo(final Schedule pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Check the next date */
        int iDiff = MetisDataDifference.compareObject(getNextDate(), pThat.getNextDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the start date */
        iDiff = MetisDataDifference.compareObject(getStartDate(), pThat.getStartDate());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Check the frequency */
        iDiff = MetisDataDifference.compareObject(getFrequency(), pThat.getFrequency());
        if (iDiff != 0) {
            return iDiff;
        }

        /* Compare IDs if all else fails */
        return super.compareId(pThat);
    }

    @Override
    public void resolveDataSetLinks() throws OceanusException {
        /* Update the Event details */
        super.resolveDataSetLinks();

        /* Access data */
        final MoneyWiseData myData = getDataSet();
        final TethysUIDataFormatter myFormatter = myData.getDataFormatter();
        final FrequencyList myFreqs = myData.getFrequencys();
        final MetisValueSet myValues = getValueSet();

        /* Resolve dataLinks */
        resolveDataLink(FIELD_FREQ, myFreqs);
        if (hasRepeatFrequency()) {
            resolveDataLink(FIELD_REPFREQ, myFreqs);

            /* resolve repeat interval */
        } else if (hasRepeatInterval()) {
            final Object myValue = myValues.getValue(FIELD_REPFREQ);
            if (myValue instanceof String) {
                final Integer myInt = myFormatter.parseValue((String) myValue, Integer.class);
                setValueRepeatInterval(myInt);
            }
        }

        /* Resolve Pattern */
        final Object myValue = myValues.getValue(FIELD_PATTERN);
        if (myValue instanceof Integer) {
            /* Store pattern */
            final SchedulePattern myPattern = SchedulePattern.allocatePattern(getFrequency(), (Integer) myValue);
            setValuePattern(myPattern);
        } else if (myValue instanceof String) {
            /* Store pattern */
            final SchedulePattern myPattern = SchedulePattern.parsePattern(getFrequency(), (String) myValue);
            setValuePattern(myPattern);
        }
    }

    @Override
    public void validate() {
        /* Access data */
        final TethysDate myStart = getStartDate();
        final TethysDate myEnd = getEndDate();
        final TethysDate myNext = getNextDate();
        final Frequency myFrequency = getFrequency();

        /* Check that startDate is non-null */
        if (myStart == null) {
            addError(ERROR_MISSING, FIELD_STARTDATE);

            /* If startDate exists */
        } else {
            /* If there is an endDate, it must not be earlier than the startDate */
            if (myEnd != null
                && myEnd.compareTo(myStart) < 0) {
                addError(ERROR_BEFORESTART, FIELD_ENDDATE);
            }

            /* If there is a nextDate */
            if (myNext != null) {
                /* It must not be earlier than the startDate */
                if (myNext.compareTo(myStart) < 0) {
                    addError(ERROR_BEFORESTART, FIELD_NEXTDATE);
                }

                /* It must not be later than the endDate */
                if (myEnd != null
                    && myNext.compareTo(myEnd) > 0) {
                    addError(ERROR_AFTEREND, FIELD_NEXTDATE);
                }
            }
        }

        /* Check that frequency is non-null, enabled and base */
        if (myFrequency == null) {
            addError(ERROR_MISSING, FIELD_FREQ);
        } else if (!myFrequency.getEnabled()) {
            addError(ERROR_DISABLED, FIELD_FREQ);
        } else if (!myFrequency.isBaseFrequency()) {
            addError(ERROR_BADFREQ, FIELD_FREQ);
        }

        /* If we have a repeat frequency */
        if (hasRepeatFrequency()) {
            /* Check that frequency is non-null, enabled and relevant */
            final Frequency myRepeat = getRepeatFrequency();
            if (myRepeat == null) {
                addError(ERROR_MISSING, FIELD_REPFREQ);
            } else if (!myRepeat.getEnabled()) {
                addError(ERROR_DISABLED, FIELD_REPFREQ);
            } else if (myRepeat.isValidRepeat(myFrequency)) {
                addError(ERROR_BADFREQ, FIELD_REPFREQ);
            }

            /* else if we have a repeat interval */
        } else if (hasRepeatInterval()) {
            /* Check that interval is non-null, nonZero and positive */
            final Integer myInterval = getRepeatInterval();
            if (myInterval == null) {
                addError(ERROR_MISSING, FIELD_REPFREQ);
            } else if (myInterval < 0) {
                addError(ERROR_NEGATIVE, FIELD_REPFREQ);
            }

            /* else must be null */
        } else if (getRepeatFrequencyId() != null) {
            addError(ERROR_EXIST, FIELD_REPFREQ);
        }

        /* If we have a repeat frequency */
        final SchedulePattern myPattern = getPattern();
        if (hasPattern()) {
            if (myPattern == null) {
                addError(ERROR_MISSING, FIELD_PATTERN);
            }

            /* else must be null */
        } else if (myPattern != null) {
            addError(ERROR_EXIST, FIELD_PATTERN);
        }

        /* Validate it */
        super.validate();
    }

    /**
     * Set a new start date.
     * @param pDate the date
     */
    public void setStartDate(final TethysDate pDate) {
        setValueStartDate(pDate);
    }

    /**
     * Set a new end date.
     * @param pDate the date
     */
    public void setEndDate(final TethysDate pDate) {
        setValueEndDate(pDate);
    }

    /**
     * Set a new frequency.
     * @param pFrequency the frequency
     */
    public void setFrequency(final Frequency pFrequency) {
        setValueFrequency(pFrequency);
    }

    /**
     * Set a new repeat frequency.
     * @param pFrequency the frequency
     */
    public void setRepeatFrequency(final Frequency pFrequency) {
        setValueRepeatFrequency(pFrequency);
    }

    /**
     * Set a new repeat interval.
     * @param pInterval the interval
     */
    public void setRepeatInterval(final Integer pInterval) {
        setValueRepeatInterval(pInterval);
    }

    /**
     * Set a new pattern.
     * @param pPattern the pattern
     */
    public void setPattern(final SchedulePattern pPattern) {
        setValuePattern(pPattern);
    }

    /**
     * Set a new next date.
     * @param pDate the date
     */
    protected void setNextDate(final TethysDate pDate) {
        setValueNextDate(pDate);
    }

    /**
     * Update Schedule from a schedule extract.
     * @param pSchedule the schedule extract
     * @return whether changes have been made
     */
    @Override
    public boolean applyChanges(final DataItem pSchedule) {
        /* Can only update from Pattern */
        if (!(pSchedule instanceof Schedule)) {
            return false;
        }
        final Schedule mySchedule = (Schedule) pSchedule;

        /* Store the current detail into history */
        pushHistory();

        /* Update the start/end dates if required */
        if (!MetisDataDifference.isEqual(getStartDate(), mySchedule.getStartDate())) {
            setStartDate(mySchedule.getStartDate());
        }
        if (!MetisDataDifference.isEqual(getEndDate(), mySchedule.getEndDate())) {
            setEndDate(mySchedule.getEndDate());
        }

        /* Update the frequency if required */
        if (!MetisDataDifference.isEqual(getFrequency(), mySchedule.getFrequency())) {
            setFrequency(mySchedule.getFrequency());
        }

        /* Update the repeat frequency if required */
        if (!MetisDataDifference.isEqual(getRepeatFrequency(), mySchedule.getRepeatFrequency())) {
            setRepeatFrequency(mySchedule.getRepeatFrequency());
        }
        if (!MetisDataDifference.isEqual(getRepeatInterval(), mySchedule.getRepeatInterval())) {
            setRepeatInterval(mySchedule.getRepeatInterval());
        }

        /* Update the pattern if required */
        if (!MetisDataDifference.isEqual(getPattern(), mySchedule.getPattern())) {
            setPattern(mySchedule.getPattern());
        }

        /* Update the next date if required */
        if (!MetisDataDifference.isEqual(getNextDate(), mySchedule.getNextDate())) {
            setNextDate(mySchedule.getNextDate());
        }

        /* Check for changes */
        return checkForHistory();
    }

    @Override
    public void touchUnderlyingItems() {
        /* touch underlying items */
        super.touchUnderlyingItems();

        /* mark the frequency referred to */
        getFrequency().touchItem(this);
    }

    /**
     * The list.
     */
    public static class ScheduleList
            extends DataList<Schedule> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ScheduleList> FIELD_DEFS = MetisFieldSet.newFieldSet(ScheduleList.class);

        /**
         * Construct an empty CORE pattern list.
         * @param pData the DataSet for the list
         */
        protected ScheduleList(final MoneyWiseData pData) {
            super(Schedule.class, pData, MoneyWiseDataType.SCHEDULE, ListStyle.CORE);
        }

        /**
         * Constructor for a cloned List.
         * @param pSource the source List
         */
        private ScheduleList(final ScheduleList pSource) {
            super(pSource);
        }

        @Override
        public MetisFieldSet<ScheduleList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public String listName() {
            return LIST_NAME;
        }

        @Override
        public MetisFields getItemFields() {
            return Schedule.FIELD_DEFS;
        }

        @Override
        protected ScheduleList getEmptyList(final ListStyle pStyle) {
            final ScheduleList myList = new ScheduleList(this);
            myList.setStyle(pStyle);
            return myList;
        }

        /**
         * Construct an edit extract of a Pattern list.
         * @return the edit list
         */
        public ScheduleList deriveEditList() {
            /* Build an empty Update */
            final ScheduleList myList = getEmptyList(ListStyle.EDIT);

            /* Loop through the Schedule */
            final Iterator<Schedule> myIterator = iterator();
            while (myIterator.hasNext()) {
                final Schedule myCurr = myIterator.next();

                /* Copy the item */
                final Schedule myItem = new Schedule(myList, myCurr);
                myList.add(myItem);
            }

            /* Sort the list */
            myList.reSort();

            /* Return the List */
            return myList;
        }

        /**
         * Add a new item to the core list.
         * @param pSchedule item
         * @return the newly added item
         */
        @Override
        public Schedule addCopyItem(final DataItem pSchedule) {
            /* Can only clone from Schedule */
            if (!(pSchedule instanceof Schedule)) {
                throw new UnsupportedOperationException();
            }

            final Schedule mySchedule = new Schedule(this, (Schedule) pSchedule);
            add(mySchedule);
            return mySchedule;
        }

        @Override
        public Schedule addValuesItem(final DataValues pValues) throws OceanusException {
            /* Create the schedule */
            final Schedule mySchedule = new Schedule(this, pValues);

            /* Check that this ScheduleId has not been previously added */
            if (!isIdUnique(mySchedule.getId())) {
                mySchedule.addError(ERROR_DUPLICATE, FIELD_ID);
                throw new MoneyWiseDataException(mySchedule, ERROR_VALIDATION);
            }

            /* Add to the list */
            add(mySchedule);

            /* Return it */
            return mySchedule;
        }

        @Override
        protected DataMapItem<Schedule> allocateDataMap() {
            /* Unused */
            throw new UnsupportedOperationException();
        }

        @Override
        public void postProcessOnLoad() throws OceanusException {
            /* Resolve links and sort the data */
            super.resolveDataSetLinks();
            reSort();

            /* Validate the schedules */
            validateOnLoad();
        }

        @Override
        protected void ensureMap() {
            /* Null operation */
        }

        @Override
        public Schedule addNewItem() {
            /* Unused */
            throw new UnsupportedOperationException();
        }
    }
}
