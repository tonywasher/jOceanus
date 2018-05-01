/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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

import java.time.DayOfWeek;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.Frequency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Pattern for Schedule.
 */
public abstract class SchedulePattern
        implements MetisDataObjectFormat {
    /**
     * No Items.
     */
    private static final String ITEM_NONE = MoneyWiseDataResource.SCHEDULE_PATTERN_NONE.getValue();

    /**
     * Last Item.
     */
    private static final String ITEM_LAST = MoneyWiseDataResource.SCHEDULE_PATTERN_LAST.getValue();

    /**
     * Item separator.
     */
    private static final String ITEM_SEP = ",";

    /**
     * The pattern.
     */
    private final Integer thePattern;

    /**
     * Constructor.
     * @param pPattern the integer pattern value
     */
    protected SchedulePattern(final Integer pPattern) {
        thePattern = pPattern;
    }

    /**
     * Constructor.
     */
    protected SchedulePattern() {
        this(0);
    }

    /**
     * Obtain the pattern value.
     * @return the value
     */
    public Integer getPatternValue() {
        return thePattern;
    }

    /**
     * Is the flag set?
     * @param pFlag the flag value.
     * @return true/false
     */
    protected boolean isFlagSet(final int pFlag) {
        return (thePattern & pFlag) != 0;
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
        if (!(pThat instanceof SchedulePattern)
            || !getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Compare the Patterns */
        final SchedulePattern myThat = (SchedulePattern) pThat;
        return getPatternValue().equals(myThat.getPatternValue());
    }

    @Override
    public int hashCode() {
        return thePattern;
    }

    /**
     * allocate pattern.
     * @param pFrequency the frequency
     * @return the resulting pattern
     */
    public static SchedulePattern allocatePattern(final Frequency pFrequency) {
        return allocatePattern(pFrequency, 0);
    }

    /**
     * allocate pattern.
     * @param pFrequency the frequency
     * @param pPattern the integer pattern value
     * @return the resulting pattern
     */
    public static SchedulePattern allocatePattern(final Frequency pFrequency,
                                                  final Integer pPattern) {
        switch (pFrequency.getFrequency()) {
            case WEEKLY:
                return new WeeklyPattern(pPattern);
            case MONTHLY:
                return new MonthlyPattern(pPattern);
            default:
                return null;
        }
    }

    /**
     * parse a string representation of a pattern.
     * @param pFrequency the frequency
     * @param pPattern the string pattern value
     * @return the resulting pattern
     * @throws OceanusException one error
     */
    public static SchedulePattern parsePattern(final Frequency pFrequency,
                                               final String pPattern) throws OceanusException {
        switch (pFrequency.getFrequency()) {
            case WEEKLY:
                return parseWeeklyPattern(pPattern);
            case MONTHLY:
                return parseMonthlyPattern(pPattern);
            default:
                return null;
        }
    }

    /**
     * parse a string representation of a Weekly pattern.
     * @param pPattern the string pattern value
     * @return the resulting pattern
     * @throws OceanusException one error
     */
    private static WeeklyPattern parseWeeklyPattern(final String pPattern) throws OceanusException {
        final StringBuilder myBuilder = new StringBuilder(pPattern);
        int myValue = 0;
        for (int iIndex = myBuilder.indexOf(ITEM_SEP); iIndex != -1; myBuilder.delete(0, iIndex + 1)) {
            myValue |= WeeklyPattern.parseFlag(myBuilder.substring(0, iIndex));
        }
        myValue |= WeeklyPattern.parseFlag(myBuilder.toString());
        return new WeeklyPattern(myValue);
    }

    /**
     * parse a string representation of a Monthly pattern.
     * @param pPattern the string pattern value
     * @return the resulting pattern
     * @throws OceanusException one error
     */
    private static MonthlyPattern parseMonthlyPattern(final String pPattern) throws OceanusException {
        final StringBuilder myBuilder = new StringBuilder(pPattern);
        int myValue = 0;
        for (int iIndex = myBuilder.indexOf(ITEM_SEP); iIndex != -1; myBuilder.delete(0, iIndex + 1)) {
            myValue |= MonthlyPattern.parseFlag(myBuilder.substring(0, iIndex));
        }
        myValue |= MonthlyPattern.parseFlag(myBuilder.toString());
        return new MonthlyPattern(myValue);
    }

    /**
     * Weekly pattern.
     */
    public static final class WeeklyPattern
            extends SchedulePattern {
        /**
         * Constructor.
         * @param pPattern the integer pattern value
         */
        protected WeeklyPattern(final Integer pPattern) {
            super(pPattern);
        }

        /**
         * Constructor.
         */
        protected WeeklyPattern() {
        }

        /**
         * Set a day.
         * @param pDay the day of the week to set.
         * @return the new pattern
         */
        public WeeklyPattern setSelectedDay(final DayOfWeek pDay) {
            /* Obtain flag value */
            final int myFlag = getFlag(pDay);
            return isFlagSet(myFlag)
                                     ? this
                                     : new WeeklyPattern(getPatternValue() | myFlag);
        }

        /**
         * Clear a day.
         * @param pDay the day of the week to clear.
         * @return the new pattern
         */
        public WeeklyPattern clearSelectedDay(final DayOfWeek pDay) {
            /* Obtain flag value */
            final int myFlag = getFlag(pDay);
            return isFlagSet(myFlag)
                                     ? new WeeklyPattern(getPatternValue() & ~myFlag)
                                     : this;
        }

        /**
         * Is the day selected?
         * @param pDay the day of the week to check.
         * @return true/false
         */
        public boolean isSelectedDay(final DayOfWeek pDay) {
            /* Obtain flag value */
            final int myFlag = getFlag(pDay);
            return isFlagSet(myFlag);
        }

        /**
         * Get flag value.
         * @param pDay the day of the week
         * @return the flag
         */
        private static int getFlag(final DayOfWeek pDay) {
            final int myValue = pDay.getValue();
            return 1 << myValue - 1;
        }

        /**
         * parse flag.
         * @param pName the flag name
         * @return the flag value
         * @throws OceanusException on error
         */
        private static int parseFlag(final String pName) throws OceanusException {
            /* handle special cases */
            if (pName.equals(ITEM_NONE)) {
                return 0;
            }

            /* Parse the value */
            try {
                final DayOfWeek myValue = DayOfWeek.valueOf(pName);
                return getFlag(myValue);
            } catch (IllegalArgumentException e) {
                throw new MoneyWiseDataException(pName, DataItem.ERROR_RESOLUTION, e);
            }
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            /* Initialise variables */
            boolean myFirst = true;
            final Locale myLocale = Locale.getDefault();
            final StringBuilder myBuilder = new StringBuilder();

            /* Loop through the Days of the week */
            for (DayOfWeek myDay : DayOfWeek.values()) {
                /* If the day is active */
                if (isSelectedDay(myDay)) {
                    /* If this is not the first day */
                    if (!myFirst) {
                        /* Add separator */
                        myBuilder.append(ITEM_SEP);
                    }

                    /* Add the day */
                    myBuilder.append(myDay.getDisplayName(TextStyle.SHORT, myLocale));
                    myFirst = false;
                }
            }

            /* If we had no selected days */
            if (myFirst) {
                myBuilder.append(ITEM_NONE);
            }

            /* Return the string */
            return myBuilder.toString();
        }
    }

    /**
     * Monthly pattern.
     */
    public static final class MonthlyPattern
            extends SchedulePattern {
        /**
         * The Last day of the month.
         */
        public static final int LAST_DAY = -1;

        /**
         * Constructor.
         * @param pPattern the integer pattern value
         */
        protected MonthlyPattern(final Integer pPattern) {
            super(pPattern);
        }

        /**
         * Constructor.
         */
        protected MonthlyPattern() {
        }

        /**
         * Set a day.
         * @param pDay the day of the month to set.
         * @return the new pattern
         */
        public MonthlyPattern setSelectedDay(final int pDay) {
            /* Obtain flag value */
            final int myFlag = getFlag(pDay);
            return isFlagSet(myFlag)
                                     ? this
                                     : new MonthlyPattern(getPatternValue() | myFlag);
        }

        /**
         * Clear a day.
         * @param pDay the day of the month to clear.
         * @return the new pattern
         */
        public MonthlyPattern clearSelectedDay(final int pDay) {
            /* Obtain flag value */
            final int myFlag = getFlag(pDay);
            return isFlagSet(myFlag)
                                     ? new MonthlyPattern(getPatternValue() & ~myFlag)
                                     : this;
        }

        /**
         * Is the day selected?
         * @param pDay the day of the month.
         * @return true/false
         */
        public boolean isSelectedDay(final int pDay) {
            /* Obtain flag value */
            final int myFlag = getFlag(pDay);
            return isFlagSet(myFlag);
        }

        /**
         * Get flag value.
         * @param pDay the day of the month
         * @return the flag
         */
        private static int getFlag(final int pDay) {
            return pDay == LAST_DAY
                                    ? 1
                                    : 1 << pDay;
        }

        /**
         * parse flag.
         * @param pName the flag name
         * @return the value
         * @throws OceanusException on error
         */
        private static int parseFlag(final String pName) throws OceanusException {
            /* handle special cases */
            if (pName.equals(ITEM_NONE)) {
                return 0;
            } else if (pName.equals(ITEM_LAST)) {
                return getFlag(LAST_DAY);
            }

            /* Parse the integer */
            try {
                final int myValue = Integer.parseInt(pName);
                return getFlag(myValue);
            } catch (NumberFormatException e) {
                throw new MoneyWiseDataException(pName, DataItem.ERROR_RESOLUTION, e);
            }
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            /* Initialise variables */
            boolean myFirst = true;
            final StringBuilder myBuilder = new StringBuilder();

            /* Loop through the Days of the month */
            final int myLength = Month.JANUARY.length(false);
            for (int myDay = 1; myDay <= myLength; myDay++) {
                /* If the day is active */
                if (isSelectedDay(myDay)) {
                    /* If this is not the first day */
                    if (!myFirst) {
                        /* Add separator */
                        myBuilder.append(ITEM_SEP);
                    }

                    /* Add the day */
                    myBuilder.append(myDay);
                    myFirst = false;
                }
            }

            /* If Last is selected */
            if (isSelectedDay(LAST_DAY)) {
                /* If this is not the first day */
                if (!myFirst) {
                    /* Add separator */
                    myBuilder.append(ITEM_SEP);
                }

                /* Add indicator */
                myBuilder.append(ITEM_LAST);
                myFirst = false;
            }

            /* If we had no selected days */
            if (myFirst) {
                myBuilder.append(ITEM_NONE);
            }

            /* Return the string */
            return myBuilder.toString();
        }
    }
}
