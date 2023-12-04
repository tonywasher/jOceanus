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
package net.sourceforge.joceanus.jmoneywise.atlas.data.statics;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of Frequency Classes.
 */
public enum MoneyWiseFrequencyClass
        implements PrometheusStaticDataClass {
    /**
     * Once.
     */
    ONCE(1, 0),

    /**
     * Daily Frequency.
     */
    DAILY(2, 1),

    /**
     * Weekly Frequency.
     */
    WEEKLY(3, 2),

    /**
     * Monthly Frequency.
     */
    MONTHLY(4, 3),

    /**
     * Annual Frequency.
     */
    ANNUALLY(5, 4),

    /**
     * Every Week/Month.
     */
    EVERY(6, 5),

    /**
     * Alternate Week/Month.
     */
    ALTERNATE(7, 6),

    /**
     * Every Third Week/Month.
     */
    EVERYTHIRD(8, 7),

    /**
     * Every Fourth Day/Week/Month.
     */
    EVERYFOURTH(9, 8),

    /**
     * Every Sixth Day/Week/Month.
     */
    EVERYSIXTH(10, 9),

    /**
     * First Week in month.
     */
    FIRSTWEEK(11, 10),

    /**
     * Second Week in month.
     */
    SECONDWEEK(12, 11),

    /**
     * Third Week in month.
     */
    THIRDWEEK(13, 12),

    /**
     * Fourth Week in month.
     */
    FOURTHWEEK(14, 13),

    /**
     * Last Week in month.
     */
    LASTWEEK(15, 14);

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the default order
     */
    MoneyWiseFrequencyClass(final int uId,
                            final int uOrder) {
        theId = uId;
        theOrder = uOrder;
    }

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseStaticResource.getKeyForFrequency(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws OceanusException on error
     */
    public static MoneyWiseFrequencyClass fromId(final int id) throws OceanusException {
        for (MoneyWiseFrequencyClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.FREQUENCY.toString() + ":" + id);
    }

    /**
     * Is this a base frequency?
     * @return true/false
     */
    public boolean isBaseFrequency() {
        switch (this) {
            case ONCE:
            case DAILY:
            case WEEKLY:
            case MONTHLY:
            case ANNUALLY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does this have a repeat frequency?
     * @return true/false
     */
    public boolean hasRepeatFrequency() {
        switch (this) {
            case WEEKLY:
            case MONTHLY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does this have a repeat interval?
     * @return true/false
     */
    public boolean hasRepeatInterval() {
        switch (this) {
            case DAILY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Does this have a pattern?
     * @return true/false
     */
    public boolean hasPattern() {
        switch (this) {
            case WEEKLY:
            case MONTHLY:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this a valid repeat for frequency?
     * @param pFrequency the overall frequency
     * @return true/false
     */
    public boolean isValidRepeat(final MoneyWiseFrequencyClass pFrequency) {
        switch (pFrequency) {
            case WEEKLY:
                return isValidWeeklyRepeat();
            case MONTHLY:
                return isValidMonthlyRepeat();
            default:
                return false;
        }
    }

    /**
     * Is this a valid weekly repeat frequency?
     * @return true/false
     */
    private boolean isValidWeeklyRepeat() {
        switch (this) {
            case ONCE:
            case EVERY:
            case ALTERNATE:
            case EVERYFOURTH:
            case FIRSTWEEK:
            case SECONDWEEK:
            case THIRDWEEK:
            case FOURTHWEEK:
            case LASTWEEK:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is this a valid monthly repeat frequency?
     * @return true/false
     */
    private boolean isValidMonthlyRepeat() {
        switch (this) {
            case ONCE:
            case EVERY:
            case ALTERNATE:
            case EVERYTHIRD:
            case EVERYFOURTH:
            case EVERYSIXTH:
                return true;
            default:
                return false;
        }
    }
}
