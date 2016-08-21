/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataInfoClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of Tax Year Info Classes.
 */
public enum TaxYearInfoClass implements DataInfoClass {
    /**
     * Personal Allowance.
     */
    ALLOWANCE(1, 0, MetisDataType.MONEY),

    /**
     * Low Tax Band.
     */
    LOTAXBAND(2, 1, MetisDataType.MONEY),

    /**
     * Basic Tax Band.
     */
    BASICTAXBAND(3, 2, MetisDataType.MONEY),

    /**
     * Rental Allowance.
     */
    RENTALALLOWANCE(4, 3, MetisDataType.MONEY),

    /**
     * Capital Allowance.
     */
    CAPITALALLOWANCE(5, 4, MetisDataType.MONEY),

    /**
     * Savings Allowance.
     */
    SAVINGSALLOWANCE(6, 5, MetisDataType.MONEY),

    /**
     * HiSavings Allowance.
     */
    HISAVINGSALLOWANCE(7, 6, MetisDataType.MONEY),

    /**
     * Dividend Allowance.
     */
    DIVIDENDALLOWANCE(8, 7, MetisDataType.MONEY),

    /**
     * Low Age Allowance.
     */
    LOAGEALLOWANCE(9, 8, MetisDataType.MONEY),

    /**
     * High Age Allowance.
     */
    HIAGEALLOWANCE(10, 9, MetisDataType.MONEY),

    /**
     * Age Allowance Limit.
     */
    AGEALLOWANCELIMIT(11, 10, MetisDataType.MONEY),

    /**
     * Additional Allowance Limit.
     */
    ADDITIONALALLOWANCELIMIT(12, 11, MetisDataType.MONEY),

    /**
     * Additional Income Threshold.
     */
    ADDITIONALINCOMETHRESHOLD(13, 12, MetisDataType.MONEY),

    /**
     * Low Tax Rate.
     */
    LOTAXRATE(14, 13, MetisDataType.RATE),

    /**
     * Basic Tax Rate.
     */
    BASICTAXRATE(15, 14, MetisDataType.RATE),

    /**
     * High Tax Rate.
     */
    HITAXRATE(16, 15, MetisDataType.RATE),

    /**
     * Interest Tax Rate.
     */
    INTERESTTAXRATE(17, 16, MetisDataType.RATE),

    /**
     * Dividend Tax Rate.
     */
    DIVIDENDTAXRATE(18, 17, MetisDataType.RATE),

    /**
     * High Dividend Tax Rate.
     */
    HIDIVIDENDTAXRATE(19, 18, MetisDataType.RATE),

    /**
     * Additional Tax Rate.
     */
    ADDITIONALTAXRATE(20, 19, MetisDataType.RATE),

    /**
     * Additional Dividend Tax Rate.
     */
    ADDITIONALDIVIDENDTAXRATE(21, 20, MetisDataType.RATE),

    /**
     * Capital Tax Rate.
     */
    CAPITALTAXRATE(22, 21, MetisDataType.RATE),

    /**
     * High Capital Tax Rate.
     */
    HICAPITALTAXRATE(23, 22, MetisDataType.RATE),

    /**
     * Residential Tax Rate.
     */
    RESIDENTIALTAXRATE(24, 23, MetisDataType.RATE),

    /**
     * High Residential Tax Rate.
     */
    HIRESIDENTIALTAXRATE(25, 24, MetisDataType.RATE);

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
     * Data Type.
     */
    private final MetisDataType theDataType;

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     * @param pDataType the data type
     */
    TaxYearInfoClass(final int uId,
                     final int uOrder,
                     final MetisDataType pDataType) {
        theId = uId;
        theOrder = uOrder;
        theDataType = pDataType;
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
    public MetisDataType getDataType() {
        return theDataType;
    }

    @Override
    public boolean isLink() {
        return false;
    }

    @Override
    public boolean isLinkSet() {
        return false;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = StaticDataResource.getKeyForTaxInfo(this).getValue();
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
    public static TaxYearInfoClass fromId(final int id) throws OceanusException {
        for (TaxYearInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TAXINFOTYPE.toString() + ":" + id);
    }
}
