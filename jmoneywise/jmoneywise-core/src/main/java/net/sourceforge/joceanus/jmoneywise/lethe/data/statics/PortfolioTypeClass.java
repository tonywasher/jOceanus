/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of Portfolio Type Classes.
 */
public enum PortfolioTypeClass implements StaticInterface {
    /**
     * Standard.
     * <p>
     * This is a standard portfolio.
     */
    STANDARD(1, 0),

    /**
     * ISA.
     * <p>
     * This is an ISA portfolio.
     */
    ISA(2, 1),

    /**
     * SIPP.
     * <p>
     * This is a SIPP Portfolio.
     */
    SIPP(3, 2),

    /**
     * Pension.
     * <p>
     * This is a Pension Portfolio and contains only PensionPots and AnnuityPots.
     */
    PENSION(4, 3);

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
     * @param uId the Id
     * @param uOrder the default order.
     */
    PortfolioTypeClass(final int uId,
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
            theName = StaticDataResource.getKeyForPortfolioType(this).getValue();
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
    public static PortfolioTypeClass fromId(final int id) throws OceanusException {
        for (PortfolioTypeClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.PORTFOLIOTYPE.toString() + ":" + id);
    }
}
