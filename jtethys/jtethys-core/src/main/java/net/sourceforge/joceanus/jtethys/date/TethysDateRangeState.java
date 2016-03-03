/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.date;

import java.util.Locale;

/**
 * State for JDateDayRange Selection panel.
 */
public class TethysDateRangeState {
    /**
     * base date is start of period?
     */
    private final boolean isBaseStartOfPeriod;

    /**
     * The Overall Range.
     */
    private TethysDateRange theOverallRange;

    /**
     * The Range.
     */
    private TethysDateRange theRange;

    /**
     * The start Date.
     */
    private TethysDate theStartDate;

    /**
     * The end Date.
     */
    private TethysDate theEndDate;

    /**
     * The base Date.
     */
    private TethysDate theBaseDate;

    /**
     * The Date Period.
     */
    private TethysDatePeriod thePeriod;

    /**
     * Is the period locked?
     */
    private boolean isLocked;

    /**
     * Can we select a next period within the available range.
     */
    private boolean isNextOK;

    /**
     * Can we select a previous period within the available range.
     */
    private boolean isPrevOK;

    /**
     * Constructor.
     * @param pBaseIsStart is the baseDate the start of the period? (true/false)
     */
    public TethysDateRangeState(final boolean pBaseIsStart) {
        /* Record parameters */
        isBaseStartOfPeriod = pBaseIsStart;

        /* Default the period and overall range */
        thePeriod = TethysDatePeriod.ONEMONTH;
        theOverallRange = new TethysDateRange();

        /* Record the initial date */
        theBaseDate = new TethysDate();

        /* build the range */
        buildRange();
    }

    /**
     * Constructor.
     * @param pState state to copy from
     */
    public TethysDateRangeState(final TethysDateRangeState pState) {
        /* Copy values */
        isBaseStartOfPeriod = pState.isBaseStartOfPeriod();
        thePeriod = pState.getPeriod();
        theOverallRange = pState.getOverallRange();
        theStartDate = pState.getStartDate();
        theEndDate = pState.getEndDate();
        theBaseDate = pState.getBaseDate();
        theRange = pState.getRange();
        isLocked = pState.isLocked();
        isNextOK = pState.isNextOK();
        isPrevOK = pState.isPrevOK();
    }

    /**
     * Is the baseDate the start of the range.
     * @return the range
     */
    public boolean isBaseStartOfPeriod() {
        return isBaseStartOfPeriod;
    }

    /**
     * Get the range.
     * @return the range
     */
    public TethysDateRange getRange() {
        return theRange;
    }

    /**
     * Get the overall range.
     * @return the start date
     */
    public TethysDateRange getOverallRange() {
        return theOverallRange;
    }

    /**
     * Get the start date.
     * @return the start date
     */
    public TethysDate getStartDate() {
        return theStartDate;
    }

    /**
     * Get the end date.
     * @return the end date
     */
    public TethysDate getEndDate() {
        return theEndDate;
    }

    /**
     * Get the base date.
     * @return the base date
     */
    public TethysDate getBaseDate() {
        return theBaseDate;
    }

    /**
     * Get the DatePeriod.
     * @return the date period
     */
    public TethysDatePeriod getPeriod() {
        return thePeriod;
    }

    /**
     * Can we select a next period within the available range.
     * @return true/false
     */
    public boolean isNextOK() {
        return isNextOK;
    }

    /**
     * Can we select a previous period within the available range.
     * @return true/false
     */
    public boolean isPrevOK() {
        return isPrevOK;
    }

    /**
     * Is the period locked?
     * @return true/false
     */
    public boolean isLocked() {
        return isLocked;
    }

    /**
     * Is this an UpTo range?
     * @return true/false
     */
    public boolean isUpTo() {
        return thePeriod == TethysDatePeriod.DATESUPTO;
    }

    /**
     * Is this a custom range?
     * @return true/false
     */
    public boolean isCustom() {
        return thePeriod == TethysDatePeriod.CUSTOM;
    }

    /**
     * Is this a full range?
     * @return true/false
     */
    public boolean isFull() {
        return thePeriod == TethysDatePeriod.ALLDATES;
    }

    /**
     * Is this an adjustable range?
     * @return true/false
     */
    public boolean isAdjustable() {
        return thePeriod.adjustPeriod();
    }

    /**
     * Is this a containing range?
     * @return true/false
     */
    public boolean isContaining() {
        return thePeriod.isContaining();
    }

    /**
     * Lock the period.
     * @param pLocked true/false
     */
    public void lockPeriod(final boolean pLocked) {
        isLocked = pLocked;
    }

    /**
     * Set new Period.
     * @param pPeriod the new period
     */
    public void setPeriod(final TethysDatePeriod pPeriod) {
        /* Adjust the period and build the new range */
        thePeriod = pPeriod;
        buildRange();
    }

    /**
     * Set new Start Date.
     * @param pStartDate the new start date
     */
    public void setStartDate(final TethysDate pStartDate) {
        /* Adjust the start date and build the new range */
        theStartDate = pStartDate;
        buildRange();
    }

    /**
     * Set new End Date.
     * @param pEndDate the new end date
     */
    public void setEndDate(final TethysDate pEndDate) {
        /* Adjust the end date and build the new range */
        theEndDate = pEndDate;
        buildRange();
    }

    /**
     * Set new base Date.
     * @param pBaseDate the new base date
     */
    public void setBaseDate(final TethysDate pBaseDate) {
        /* Adjust the base date and build the new range */
        theBaseDate = pBaseDate;
        buildRange();
    }

    /**
     * Set next Date.
     */
    public void setNextDate() {
        /* Adjust the date and build the new range */
        nextPeriod();
        buildRange();
    }

    /**
     * Set previous Date.
     */
    public void setPreviousDate() {
        /* Adjust the date and build the new range */
        previousPeriod();
        buildRange();
    }

    /**
     * adjust the overall range.
     * @param pRange the overallRange
     */
    public void adjustOverallRange(final TethysDateRange pRange) {
        /* record the range */
        theOverallRange = new TethysDateRange(pRange);

        /* Access range parameters */
        TethysDate myFirst = theOverallRange.getStart();
        TethysDate myLast = theOverallRange.getEnd();

        /* Make sure that no dates are before the first date */
        if (myFirst != null) {
            if (theBaseDate.compareTo(myFirst) < 0) {
                theBaseDate = myFirst;
            }
            if ((theStartDate == null)
                || (theStartDate.compareTo(myFirst) < 0)) {
                theStartDate = myFirst;
            }
            if ((theEndDate != null)
                && (theEndDate.compareTo(myFirst) < 0)) {
                theEndDate = myFirst;
            }
        }

        /* Make sure that no dates are past the final date */
        if (myLast != null) {
            if (theBaseDate.compareTo(myLast) > 0) {
                theBaseDate = myLast;
            }
            if ((theStartDate != null)
                && (theStartDate.compareTo(myLast) > 0)) {
                theStartDate = myLast;
            }
            if ((theEndDate == null)
                || (theEndDate.compareTo(myLast) > 0)) {
                theEndDate = myLast;
            }
        }

        /* Build the range */
        buildRange();
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public void setLocale(final Locale pLocale) {
        theStartDate.setLocale(pLocale);
        theEndDate.setLocale(pLocale);
        theBaseDate.setLocale(pLocale);
        theOverallRange.setLocale(pLocale);
        theRange.setLocale(pLocale);
    }

    /**
     * Build the range represented by the selection.
     */
    private void buildRange() {
        /* Determine action for period */
        switch (thePeriod) {
            case ALLDATES:
                buildFullRange();
                break;
            case CUSTOM:
                buildCustomRange();
                break;
            case DATESUPTO:
                buildUpToRange();
                break;
            case CALENDARMONTH:
            case CALENDARQUARTER:
            case CALENDARYEAR:
            case FISCALYEAR:
                buildContainingRange();
                break;
            default:
                buildStandardRange();
                break;
        }
    }

    /**
     * Build the standard range.
     */
    private void buildStandardRange() {
        if (isBaseStartOfPeriod) {
            buildStartingRange();
        } else {
            buildEndingRange();
        }
    }

    /**
     * Build the full range.
     */
    private void buildFullRange() {
        /* Previous and next are not allowed */
        isPrevOK = false;
        isNextOK = false;

        /* Create the range */
        theRange = new TethysDateRange(theOverallRange);
    }

    /**
     * Build the custom range.
     */
    private void buildCustomRange() {
        /* Previous and next are disallowed */
        isPrevOK = false;
        isNextOK = false;

        /* Reset anomalies */
        if (theEndDate.compareTo(theStartDate) < 0) {
            theEndDate = theStartDate;
        }
        if (theBaseDate.compareTo(theStartDate) < 0) {
            theBaseDate = theStartDate;
        }
        if (theBaseDate.compareTo(theEndDate) < 0) {
            theBaseDate = theEndDate;
        }

        /* Create the range */
        theRange = new TethysDateRange(theStartDate, theEndDate);
    }

    /**
     * Build the upTo range.
     */
    private void buildUpToRange() {
        /* Previous and next are disallowed */
        isPrevOK = false;
        isNextOK = false;

        /* Record start and end dates */
        theStartDate = theOverallRange.getStart();
        theEndDate = theBaseDate;

        /* Create the range */
        theRange = new TethysDateRange(theStartDate, theEndDate);
    }

    /**
     * Build the containing range.
     */
    private void buildContainingRange() {
        /* Initialise the start date */
        theStartDate = new TethysDate(theBaseDate);

        /* Adjust the date to the start of the relevant period */
        theStartDate.startPeriod(thePeriod);

        /* Initialise the end date */
        theEndDate = new TethysDate(theStartDate);

        /* Adjust the date to cover the relevant period */
        theEndDate.adjustForwardByPeriod(thePeriod);
        theEndDate.adjustDay(-1);

        /* Reset anomalies */
        resetBaseAnomalies();

        /* Create the range */
        theRange = new TethysDateRange(theStartDate, theEndDate);
    }

    /**
     * Reset anomalies.
     */
    private void resetBaseAnomalies() {
        /* Reset anomalies */
        TethysDate myFirstDate = theOverallRange.getStart();
        if (myFirstDate != null
            && theStartDate.compareTo(myFirstDate) < 0) {
            theStartDate = myFirstDate;
        }
        TethysDate myLastDate = theOverallRange.getEnd();
        if (myLastDate != null
            && theEndDate.compareTo(myLastDate) > 0) {
            theEndDate = myLastDate;
        }

        /* Previous is only allowed if we have not hit the first date */
        isPrevOK = (myFirstDate == null)
                   || (myFirstDate.compareTo(theStartDate) != 0);

        /* Next is only allowed if we have not hit the last date */
        isNextOK = (myLastDate == null)
                   || (myLastDate.compareTo(theEndDate) != 0);
    }

    /**
     * Build the starting range.
     */
    private void buildStartingRange() {
        /* Initialise the start date */
        theStartDate = theBaseDate;

        /* Initialise the end date */
        theEndDate = new TethysDate(theBaseDate);

        /* Adjust the date to cover the relevant period */
        theEndDate.adjustForwardByPeriod(thePeriod);
        theEndDate.adjustDay(-1);

        /* Reset anomalies */
        resetBaseAnomalies();

        /* Create the range */
        theRange = new TethysDateRange(theStartDate, theEndDate);
    }

    /**
     * Build the ending range.
     */
    private void buildEndingRange() {
        /* Initialise the end date */
        theEndDate = theBaseDate;

        /* Initialise the start date */
        theStartDate = new TethysDate(theBaseDate);

        /* Adjust the date to cover the relevant period */
        theStartDate.adjustBackwardByPeriod(thePeriod);
        theStartDate.adjustDay(1);

        /* Reset anomalies */
        resetBaseAnomalies();

        /* Create the range */
        theRange = new TethysDateRange(theStartDate, theEndDate);
    }

    /**
     * Adjust forwards to the next period.
     */
    private void nextPeriod() {
        /* Initialise the date */
        TethysDate myDate = new TethysDate(theBaseDate);

        /* Adjust the date appropriately */
        myDate.adjustForwardByPeriod(thePeriod);

        /* Make sure that we do not go beyond the date range */
        TethysDate myLastDate = theOverallRange.getEnd();
        if (myLastDate != null
            && myDate.compareTo(myLastDate) > 0) {
            myDate = myLastDate;
        }

        /* Store the date */
        theBaseDate = myDate;
    }

    /**
     * Adjust backwards to the previous period.
     */
    private void previousPeriod() {
        /* Initialise the date */
        TethysDate myDate = new TethysDate(theBaseDate);

        /* Adjust the date appropriately */
        myDate.adjustBackwardByPeriod(thePeriod);

        /* Make sure that we do not go beyond the date range */
        TethysDate myFirstDate = theOverallRange.getStart();
        if (myFirstDate != null
            && myDate.compareTo(myFirstDate) < 0) {
            myDate = myFirstDate;
        }

        /* Store the date */
        theBaseDate = myDate;
    }
}
