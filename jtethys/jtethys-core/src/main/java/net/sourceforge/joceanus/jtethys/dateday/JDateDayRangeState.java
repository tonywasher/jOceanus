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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jtethys/jtethys-swing/src/main/java/net/sourceforge/joceanus/jtethys/dateday/swing/JDateDayRangeSelect.java $
 * $Revision: 580 $
 * $Author: Tony $
 * $Date: 2015-03-25 14:52:24 +0000 (Wed, 25 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.dateday;

import java.util.Locale;

/**
 * State for JDateDayRange Selection panel.
 */
public class JDateDayRangeState {
    /**
     * base date is start of period?
     */
    private final boolean isBaseStartOfPeriod;

    /**
     * The Overall Range.
     */
    private JDateDayRange theOverallRange;

    /**
     * The Range.
     */
    private JDateDayRange theRange;

    /**
     * The start Date.
     */
    private JDateDay theStartDate;

    /**
     * The end Date.
     */
    private JDateDay theEndDate;

    /**
     * The base Date.
     */
    private JDateDay theBaseDate;

    /**
     * The Date Period.
     */
    private JDatePeriod thePeriod;

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
    public JDateDayRangeState(final boolean pBaseIsStart) {
        /* Record parameters */
        isBaseStartOfPeriod = pBaseIsStart;

        /* Default the period and overall range */
        thePeriod = JDatePeriod.ONEMONTH;
        theOverallRange = new JDateDayRange();

        /* Record the initial date */
        theBaseDate = new JDateDay();

        /* build the range */
        buildRange();
    }

    /**
     * Constructor.
     * @param pState state to copy from
     */
    public JDateDayRangeState(final JDateDayRangeState pState) {
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
    public JDateDayRange getRange() {
        return theRange;
    }

    /**
     * Get the overall range.
     * @return the start date
     */
    public JDateDayRange getOverallRange() {
        return theOverallRange;
    }

    /**
     * Get the start date.
     * @return the start date
     */
    public JDateDay getStartDate() {
        return theStartDate;
    }

    /**
     * Get the end date.
     * @return the end date
     */
    public JDateDay getEndDate() {
        return theEndDate;
    }

    /**
     * Get the base date.
     * @return the base date
     */
    public JDateDay getBaseDate() {
        return theBaseDate;
    }

    /**
     * Get the DatePeriod.
     * @return the date period
     */
    public JDatePeriod getPeriod() {
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
        return thePeriod == JDatePeriod.DATESUPTO;
    }

    /**
     * Is this a custom range?
     * @return true/false
     */
    public boolean isCustom() {
        return thePeriod == JDatePeriod.CUSTOM;
    }

    /**
     * Is this a full range?
     * @return true/false
     */
    public boolean isFull() {
        return thePeriod == JDatePeriod.ALLDATES;
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
    public void setPeriod(final JDatePeriod pPeriod) {
        /* Adjust the period and build the new range */
        thePeriod = pPeriod;
        buildRange();
    }

    /**
     * Set new Start Date.
     * @param pStartDate the new start date
     */
    public void setStartDate(final JDateDay pStartDate) {
        /* Adjust the start date and build the new range */
        theStartDate = pStartDate;
        buildRange();
    }

    /**
     * Set new End Date.
     * @param pEndDate the new end date
     */
    public void setEndDate(final JDateDay pEndDate) {
        /* Adjust the end date and build the new range */
        theEndDate = pEndDate;
        buildRange();
    }

    /**
     * Set new base Date.
     * @param pBaseDate the new base date
     */
    public void setBaseDate(final JDateDay pBaseDate) {
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
    public void adjustOverallRange(final JDateDayRange pRange) {
        /* record the range */
        theOverallRange = new JDateDayRange(pRange);

        /* Access range parameters */
        JDateDay myFirst = theOverallRange.getStart();
        JDateDay myLast = theOverallRange.getEnd();

        /* Make sure that no dates are past the final date */
        if (myLast != null) {
            if (theBaseDate.compareTo(myLast) > 0) {
                theBaseDate = myLast;
            }
            if (theStartDate.compareTo(myLast) > 0) {
                theStartDate = myLast;
            }
            if (theEndDate.compareTo(myLast) > 0) {
                theEndDate = myLast;
            }
        }

        /* Make sure that no dates are before the first date */
        if (myFirst != null) {
            if (theBaseDate.compareTo(myFirst) < 0) {
                theBaseDate = myFirst;
            }
            if (theStartDate.compareTo(myFirst) < 0) {
                theStartDate = myFirst;
            }
            if (theEndDate.compareTo(myFirst) < 0) {
                theEndDate = myFirst;
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
                if (isBaseStartOfPeriod) {
                    buildStartingRange();
                } else {
                    buildEndingRange();
                }
                break;
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
        theRange = new JDateDayRange(theOverallRange);
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
        theRange = new JDateDayRange(theStartDate, theEndDate);
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
        theRange = new JDateDayRange(theStartDate, theEndDate);
    }

    /**
     * Build the containing range.
     */
    private void buildContainingRange() {
        /* Initialise the start date */
        theStartDate = new JDateDay(theBaseDate);

        /* Adjust the date to the start of the relevant period */
        theStartDate.startPeriod(thePeriod);

        /* Initialise the end date */
        theEndDate = new JDateDay(theStartDate);

        /* Adjust the date to cover the relevant period */
        theEndDate.adjustForwardByPeriod(thePeriod);
        theEndDate.adjustDay(-1);

        /* Reset anomalies */
        resetBaseAnomalies();

        /* Create the range */
        theRange = new JDateDayRange(theStartDate, theEndDate);
    }

    /**
     * Reset anomalies.
     */
    private void resetBaseAnomalies() {
        /* Reset anomalies */
        JDateDay myFirstDate = theOverallRange.getStart();
        if (myFirstDate != null
            && theStartDate.compareTo(myFirstDate) < 0) {
            theStartDate = myFirstDate;
        }
        JDateDay myLastDate = theOverallRange.getEnd();
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
        theEndDate = new JDateDay(theBaseDate);

        /* Adjust the date to cover the relevant period */
        theEndDate.adjustForwardByPeriod(thePeriod);
        theEndDate.adjustDay(-1);

        /* Reset anomalies */
        resetBaseAnomalies();

        /* Create the range */
        theRange = new JDateDayRange(theStartDate, theEndDate);
    }

    /**
     * Build the ending range.
     */
    private void buildEndingRange() {
        /* Initialise the end date */
        theEndDate = theBaseDate;

        /* Initialise the start date */
        theStartDate = new JDateDay(theBaseDate);

        /* Adjust the date to cover the relevant period */
        theStartDate.adjustBackwardByPeriod(thePeriod);
        theStartDate.adjustDay(1);

        /* Reset anomalies */
        resetBaseAnomalies();

        /* Create the range */
        theRange = new JDateDayRange(theStartDate, theEndDate);
    }

    /**
     * Adjust forwards to the next period.
     */
    private void nextPeriod() {
        /* Initialise the date */
        JDateDay myDate = new JDateDay(theBaseDate);

        /* Adjust the date appropriately */
        myDate.adjustForwardByPeriod(thePeriod);

        /* Make sure that we do not go beyond the date range */
        JDateDay myLastDate = theOverallRange.getEnd();
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
        JDateDay myDate = new JDateDay(theBaseDate);

        /* Adjust the date appropriately */
        myDate.adjustBackwardByPeriod(thePeriod);

        /* Make sure that we do not go beyond the date range */
        JDateDay myFirstDate = theOverallRange.getStart();
        if (myFirstDate != null
            && myDate.compareTo(myFirstDate) < 0) {
            myDate = myFirstDate;
        }

        /* Store the date */
        theBaseDate = myDate;
    }
}
