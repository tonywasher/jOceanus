/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.StaticInterface;

/**
 * Enumeration of Frequency Classes.
 */
public enum FrequencyClass implements StaticInterface {
    /**
     * Weekly Frequency.
     */
    Weekly(1, 0, 7),

    /**
     * Fortnightly Frequency.
     */
    Fortnightly(2, 1, 14),

    /**
     * Monthly Frequency.
     */
    Monthly(3, 2, 1),

    /**
     * Monthly Frequency (at end of month).
     */
    EndOfMonth(4, 3, 1),

    /**
     * Quarterly Frequency.
     */
    Quarterly(5, 4, 3),

    /**
     * Half Yearly Frequency.
     */
    HalfYearly(6, 5, 6),

    /**
     * Annual Frequency.
     */
    Annually(7, 6, 0),

    /**
     * Only on Maturity.
     */
    Maturity(8, 7, 0);

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Adjustment factor.
     */
    private final int theAdjust;

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    /**
     * Obtain Adjustment.
     * @return the adjustment
     */
    public int getAdjustment() {
        return theAdjust;
    }

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the default order
     * @param uAdjust the adjustment
     */
    private FrequencyClass(final int uId,
                           final int uOrder,
                           final int uAdjust) {
        theId = uId;
        theOrder = uOrder;
        theAdjust = uAdjust;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static FrequencyClass fromId(final int id) throws JDataException {
        for (FrequencyClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Frequency Class Id: "
                                                      + id);
    }
}
