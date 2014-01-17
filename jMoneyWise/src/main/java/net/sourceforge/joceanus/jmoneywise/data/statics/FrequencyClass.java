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
package net.sourceforge.joceanus.jmoneywise.data.statics;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamodels.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of Frequency Classes.
 */
public enum FrequencyClass implements StaticInterface {
    /**
     * Weekly Frequency.
     */
    WEEKLY(1, 0, 7),

    /**
     * Fortnightly Frequency.
     */
    FORTNIGHTLY(2, 1, 14),

    /**
     * Monthly Frequency.
     */
    MONTHLY(3, 2, 1),

    /**
     * Monthly Frequency (at end of month).
     */
    ENDOFMONTH(4, 3, 1),

    /**
     * Quarterly Frequency.
     */
    QUARTERLY(5, 4, 3),

    /**
     * Half Yearly Frequency.
     */
    HALFYEARLY(6, 5, 6),

    /**
     * Annual Frequency.
     */
    ANNUALLY(7, 6, 0),

    /**
     * Only on Maturity.
     */
    MATURITY(8, 7, 0);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(FrequencyClass.class.getName());

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

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
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
     * @throws JOceanusException on error
     */
    public static FrequencyClass fromId(final int id) throws JOceanusException {
        for (FrequencyClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JOceanusException("Invalid Frequency Class Id: "
                                    + id);
    }
}
