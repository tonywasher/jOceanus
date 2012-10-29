/*******************************************************************************
 * jDateDay: Java Date Day
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
package net.sourceforge.jOceanus.jDateDay;

/**
 * Represents a contiguous Range of dates.
 */
public class JDateDayRange implements Comparable<JDateDayRange> {
    /**
     * Unbounded range description.
     */
    protected static final String DESC_UNBOUNDED = "Unbounded";

    /**
     * link range description.
     */
    protected static final String DESC_LINK = " to ";

    /**
     * The Start Date for the range.
     */
    private JDateDay theStart = null;

    /**
     * The End Date for the range.
     */
    private JDateDay theEnd = null;

    /**
     * Get the start date for the range.
     * @return the Start date
     */
    public JDateDay getStart() {
        return theStart;
    }

    /**
     * Get the end date for the range.
     * @return the End date
     */
    public JDateDay getEnd() {
        return theEnd;
    }

    /**
     * Construct a Range from a Start Date and an End Date.
     * @param pStart the start date
     * @param pEnd the end date
     */
    public JDateDayRange(final JDateDay pStart,
                         final JDateDay pEnd) {
        if (pStart != null) {
            theStart = new JDateDay(pStart);
        }
        if (pEnd != null) {
            theEnd = new JDateDay(pEnd);
        }
    }

    /**
     * Construct a range from another range.
     * @param pRange the range to copy from
     */
    public JDateDayRange(final JDateDayRange pRange) {
        this(pRange.getStart(), pRange.getEnd());
    }

    /**
     * Determine whether a Date is within the date range.
     * @param pDate the date to test
     * @return -1, 0, 1 if early, in range or late
     */
    public short compareTo(final JDateDay pDate) {
        if ((theStart != null) && (theStart.compareTo(pDate) > 0)) {
            return 1;
        }
        if ((theEnd != null) && (theEnd.compareTo(pDate) < 0)) {
            return -1;
        }
        return 0;
    }

    @Override
    public int compareTo(final JDateDayRange that) {
        int result;

        /* Handle the trivial cases */
        if (this == that) {
            return 0;
        }
        if (that == null) {
            return -1;
        }

        /* If start dates differ */
        if (this.getStart() != that.getStart()) {
            /* Handle nulls */
            if (this.getStart() == null) {
                return 1;
            }
            if (that.getStart() == null) {
                return -1;
            }

            /* Compare the start dates */
            result = theStart.compareTo(that.theStart);
            if (result != 0) {
                return result;
            }
        }

        /* If end dates differ */
        if (this.getEnd() != that.getEnd()) {
            /* Handle nulls */
            if (this.getEnd() == null) {
                return 1;
            }
            if (that.getEnd() == null) {
                return -1;
            }

            /* Compare the end dates */
            result = theEnd.compareTo(that.theEnd);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        /* Build range description */
        String myFormat = (theStart == null) ? DESC_UNBOUNDED : theStart.toString();
        myFormat += DESC_LINK;
        myFormat += (theEnd == null) ? DESC_UNBOUNDED : theEnd.toString();

        /* return the format */
        return myFormat;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle the trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Make sure that the object is a JDateDayRange */
        if (pThat.getClass() != this.getClass()) {
            return false;
        }

        /* Access the object as a JDateDayRange */
        JDateDayRange myThat = (JDateDayRange) pThat;

        /* Check components */
        if (theStart == null) {
            if (myThat.getStart() != null) {
                return false;
            }
        } else if (!theStart.equals(myThat.getStart())) {
            return false;
        }
        if (theEnd == null) {
            if (myThat.getEnd() != null) {
                return false;
            }
        } else if (!theEnd.equals(myThat.getEnd())) {
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        /* Calculate hash based on Start/End */
        int iHash = 1;
        if (theStart != null) {
            iHash *= JDateDay.HASH_PRIME;
            iHash += theStart.hashCode();
        }
        if (theEnd != null) {
            iHash *= JDateDay.HASH_PRIME;
            iHash += theEnd.hashCode();
        }
        return iHash;
    }

    /**
     * Determine whether two DateDay objects differ.
     * @param pCurr The current Date
     * @param pNew The new Date
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(final JDateDayRange pCurr,
                                      final JDateDayRange pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return (pNew != null);
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return true;
        }

        /* Handle Standard cases */
        return !pCurr.equals(pNew);
    }
}
