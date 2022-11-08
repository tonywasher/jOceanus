/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.StaticDataClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of Portfolio Type Classes.
 */
public enum PortfolioTypeClass implements StaticDataClass {
    /**
     * Standard.
     * <p>
     * This is a standard portfolio.
     */
    STANDARD(1, 0),

    /**
     * ISA.
     * <p>
     * This is a TaxFree portfolio.
     */
    TAXFREE(2, 1),

    /**
     * Pension.
     * <p>
     * This is a Pension. and is singular
     */
    PENSION(3, 2),

    /**
     * SIPP.
     * <p>
     * This is a SIPP Portfolio.
     */
    SIPP(4, 3);

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

    /**
     * Determine whether the PortfolioType is tax free.
     * @return <code>true</code> if the PortfolioTtype is tax free, <code>false</code> otherwise.
     */
    public boolean isTaxFree() {
        return this != STANDARD;
    }

    /**
     * Determine whether the PortfolioType is a pension.
     * @return <code>true</code> if the PortfolioType is pension, <code>false</code> otherwise.
     */
    public boolean isPension() {
        switch (this) {
            case PENSION:
            case SIPP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the PortfolioType owns securities.
     * @return <code>true</code> if the PortfolioType owns securities, <code>false</code> otherwise.
     */
    public boolean holdsSecurities() {
        switch (this) {
            case STANDARD:
            case TAXFREE:
            case SIPP:
                return true;
            default:
                return false;
        }
    }

    /**
     * Determine whether the PortfolioType can hold pension securities.
     * @return <code>true</code> if the PortfolioType owns pensions, <code>false</code> otherwise.
     */
    public boolean holdsPensions() {
        return this == PENSION;
    }

    /**
     * Is this a singular portfolio?.
     * @return <code>true</code> if the PortfolioType is singular, <code>false</code> otherwise.
     */
    public boolean isSingular() {
        return holdsPensions();
    }
}
