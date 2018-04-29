/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.date;

import java.util.Locale;

/**
 * Represents a contiguous Range of dates.
 */
public class TethysDateRange
        implements Comparable<TethysDateRange> {
    /**
     * Unbounded range description.
     */
    protected static final String DESC_UNBOUNDED = TethysDateResource.RANGE_UNBOUNDED.getValue();
    
    /**
     * link range description.
     */
    protected static final String DESC_LINK = TethysDateResource.RANGE_TO.getValue();

    /**
     * link range description.
     */
    protected static final char CHAR_BLANK = ' ';

    /**
     * The Start Date for the range.
     */
    private TethysDate theStart;

    /**
     * The End Date for the range.
     */
    private TethysDate theEnd;

    /**
     * Construct a Range from a Start Date and an End Date.
     * @param pStart the start date
     * @param pEnd the end date
     */
    public TethysDateRange(final TethysDate pStart,
                           final TethysDate pEnd) {
        if (pStart != null) {
            theStart = new TethysDate(pStart);
        }
        if (pEnd != null) {
            theEnd = new TethysDate(pEnd);
        }
    }

    /**
     * Construct a range from another range.
     * @param pRange the range to copy from
     */
    public TethysDateRange(final TethysDateRange pRange) {
        this(pRange.getStart(), pRange.getEnd());
    }

    /**
     * Construct an unbounded Range.
     */
    public TethysDateRange() {
        this(null, null);
    }

    /**
     * Get the start date for the range.
     * @return the Start date
     */
    public TethysDate getStart() {
        return theStart;
    }

    /**
     * Get the end date for the range.
     * @return the End date
     */
    public TethysDate getEnd() {
        return theEnd;
    }

    /**
     * Determine whether a Date is within this range.
     * @param pDate the date to test
     * @return -1 if the date is after the range, 0 if the date is within the range, 1 if the date
     * is before the range
     */
    public int compareToDate(final TethysDate pDate) {
        /* Check start date */
        if (theStart != null
            && theStart.compareTo(pDate) > 0) {
            return 1;
        }

        /* Check end date */
        if (theEnd != null
            && theEnd.compareTo(pDate) < 0) {
            return -1;
        }

        /* Date must be within range */
        return 0;
    }

    @Override
    public int compareTo(final TethysDateRange pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Access target start date */
        final TethysDate myStart = pThat.getStart();

        /* If our start is null */
        if (theStart == null) {
            /* Handle non-null target start */
            if (myStart != null) {
                return 1;
            }

            /* else start is non-null */
        } else {
            /* Handle null target start */
            if (myStart == null) {
                return -1;
            }

            /* Compare the start dates */
            final int result = theStart.compareTo(myStart);
            if (result != 0) {
                return result;
            }
        }

        /* Access target end date */
        final TethysDate myEnd = pThat.getEnd();

        /* If our end is null */
        if (theEnd == null) {
            /* Handle non-null target end */
            if (myEnd != null) {
                return 1;
            }

            /* else start is non-null */
        } else {
            /* Handle null target end */
            if (myStart == null) {
                return -1;
            }

            /* Compare the end dates */
            final int result = theEnd.compareTo(myEnd);
            if (result != 0) {
                return result;
            }
        }

        /* Ranges are identical */
        return 0;
    }

    @Override
    public String toString() {
        /* Build range description */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(theStart == null
                                          ? DESC_UNBOUNDED
                                          : theStart.toString());
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(DESC_LINK);
        myBuilder.append(CHAR_BLANK);
        myBuilder.append(theEnd == null
                                        ? DESC_UNBOUNDED
                                        : theEnd.toString());

        /* return the format */
        return myBuilder.toString();
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

        /* Access the object as a DateRange */
        final TethysDateRange myThat = (TethysDateRange) pThat;

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
            iHash *= TethysDate.HASH_PRIME;
            iHash += theStart.hashCode();
        }
        if (theEnd != null) {
            iHash *= TethysDate.HASH_PRIME;
            iHash += theEnd.hashCode();
        }
        return iHash;
    }

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    public void setLocale(final Locale pLocale) {
        if (theStart != null) {
            theStart.setLocale(pLocale);
        }
        if (theEnd != null) {
            theEnd.setLocale(pLocale);
        }
    }

    /**
     * Determine whether two DateDay objects differ.
     * @param pCurr The current Date
     * @param pNew The new Date
     * @return <code>true</code> if the objects differ, <code>false</code> otherwise
     */
    public static boolean isDifferent(final TethysDateRange pCurr,
                                      final TethysDateRange pNew) {
        /* Handle case where current value is null */
        if (pCurr == null) {
            return pNew != null;
        }

        /* Handle case where new value is null */
        if (pNew == null) {
            return true;
        }

        /* Handle Standard cases */
        return !pCurr.equals(pNew);
    }
}
