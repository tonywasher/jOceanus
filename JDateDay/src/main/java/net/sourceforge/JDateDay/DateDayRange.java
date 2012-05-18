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
package net.sourceforge.JDateDay;

/**
 * Represents a contiguous Range of dates
 */
public class DateDayRange implements Comparable<DateDayRange> {
    /**
     * The Start Date for the range
     */
    private DateDay theStart = null;

    /**
     * The End Date for the range
     */
    private DateDay theEnd = null;

    /**
     * Get the start date for the range
     * @return the Start date
     */
    public DateDay getStart() {
        return theStart;
    }

    /**
     * Get the end date for the range
     * @return the End date
     */
    public DateDay getEnd() {
        return theEnd;
    }

    /**
     * Construct a Range from a Start Date and an End Date
     * @param pStart the start date
     * @param pEnd the end date
     */
    public DateDayRange(DateDay pStart, DateDay pEnd) {
        if (pStart != null)
            theStart = new DateDay(pStart);
        if (pEnd != null)
            theEnd = new DateDay(pEnd);
    }

    /**
     * Construct a range from another range
     * @param pRange the range to copy from
     */
    public DateDayRange(DateDayRange pRange) {
        this(pRange.getStart(), pRange.getEnd());
    }

    /**
     * Determine whether a Date is within the date range
     * @param pDate the date to test
     * @return -1, 0, 1 if early, in range or late
     */
    public short compareTo(DateDay pDate) {
        if ((theStart != null) && (theStart.compareTo(pDate) > 0))
            return 1;
        if ((theEnd != null) && (theEnd.compareTo(pDate) < 0))
            return -1;
        return 0;
    }

    @Override
    public int compareTo(DateDayRange that) {
        int result;
        if (this == that)
            return 0;
        if (that == null)
            return -1;
        if (this.getStart() != that.getStart()) {
            if (this.getStart() == null)
                return 1;
            if (that.getStart() == null)
                return -1;
            result = theStart.compareTo(that.theStart);
            if (result != 0)
                return result;
        }
        if (this.getEnd() != that.getEnd()) {
            if (this.getEnd() == null)
                return 1;
            if (that.getEnd() == null)
                return -1;
            result = theEnd.compareTo(that.theEnd);
            if (result != 0)
                return result;
        }
        return 0;
    }

    @Override
    public String toString() {
        String myFormat;
        String myUnBounded = "Unbounded";

        /* Build range description */
        myFormat = (theStart == null) ? myUnBounded : theStart.toString();
        myFormat += " to ";
        myFormat += (theEnd == null) ? myUnBounded : theEnd.toString();

        /* return the format */
        return myFormat;
    }

    @Override
    public boolean equals(Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat)
            return true;
        if (pThat == null)
            return false;

        /* Make sure that the object is a DateDayRange */
        if (pThat.getClass() != this.getClass())
            return false;

        /* Access the object as a DateDayRange */
        DateDayRange myThat = (DateDayRange) pThat;

        /* Check components */
        if (theStart == null) {
            if (myThat.getStart() != null)
                return false;
        } else if (!theStart.equals(myThat.getStart()))
            return false;
        if (theEnd == null) {
            if (myThat.getEnd() != null)
                return false;
        } else if (!theEnd.equals(myThat.getEnd()))
            return false;
        return true;

    }

    @Override
    public int hashCode() {
        /* Calculate hash based on Start/End */
        int iHash = 0;
        if (theStart != null)
            iHash += 17 * theStart.hashCode();
        if (theEnd != null)
            iHash += theEnd.hashCode();
        return iHash;
    }

    /**
     * Determine whether two DateDay objects differ.
     * @param pCurr The current Date
     * @param pNew The new Date
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(DateDayRange pCurr,
                                      DateDayRange pNew) {
        /* Handle case where current value is null */
        if (pCurr == null)
            return (pNew != null);

        /* Handle case where new value is null */
        if (pNew == null)
            return true;

        /* Handle Standard cases */
        return !pCurr.equals(pNew);
    }
}
