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

import net.sourceforge.joceanus.jmetis.data.DataType;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataInfoClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of Tax Year Info Classes.
 */
public enum TaxYearInfoClass implements DataInfoClass {
    /**
     * Personal Allowance.
     */
    ALLOWANCE(1, 0, DataType.MONEY),

    /**
     * Low Tax Band.
     */
    LOTAXBAND(2, 1, DataType.MONEY),

    /**
     * Basic Tax Band.
     */
    BASICTAXBAND(3, 2, DataType.MONEY),

    /**
     * Rental Allowance.
     */
    RENTALALLOWANCE(4, 3, DataType.MONEY),

    /**
     * Capital Allowance.
     */
    CAPITALALLOWANCE(5, 4, DataType.MONEY),

    /**
     * Low Age Allowance.
     */
    LOAGEALLOWANCE(6, 5, DataType.MONEY),

    /**
     * High Age Allowance.
     */
    HIAGEALLOWANCE(7, 6, DataType.MONEY),

    /**
     * Age Allowance Limit.
     */
    AGEALLOWANCELIMIT(8, 7, DataType.MONEY),

    /**
     * Additional Allowance Limit.
     */
    ADDITIONALALLOWANCELIMIT(9, 8, DataType.MONEY),

    /**
     * Additional Income Threshold.
     */
    ADDITIONALINCOMETHRESHOLD(10, 9, DataType.MONEY),

    /**
     * Low Tax Rate.
     */
    LOTAXRATE(11, 10, DataType.RATE),

    /**
     * Basic Tax Rate.
     */
    BASICTAXRATE(12, 11, DataType.RATE),

    /**
     * High Tax Rate.
     */
    HITAXRATE(13, 12, DataType.RATE),

    /**
     * Interest Tax Rate.
     */
    INTERESTTAXRATE(14, 13, DataType.RATE),

    /**
     * Dividend Tax Rate.
     */
    DIVIDENDTAXRATE(15, 14, DataType.RATE),

    /**
     * High Dividend Tax Rate.
     */
    HIDIVIDENDTAXRATE(16, 15, DataType.RATE),

    /**
     * Additional Tax Rate.
     */
    ADDITIONALTAXRATE(17, 16, DataType.RATE),

    /**
     * Additional Dividend Tax Rate.
     */
    ADDITIONALDIVIDENDTAXRATE(18, 17, DataType.RATE),

    /**
     * Capital Tax Rate.
     */
    CAPITALTAXRATE(19, 18, DataType.RATE),

    /**
     * High Capital Tax Rate.
     */
    HICAPITALTAXRATE(20, 19, DataType.RATE);

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
    private final DataType theDataType;

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     * @param pDataType the data type
     */
    TaxYearInfoClass(final int uId,
                     final int uOrder,
                     final DataType pDataType) {
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
    public DataType getDataType() {
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
     * @throws JOceanusException on error
     */
    public static TaxYearInfoClass fromId(final int id) throws JOceanusException {
        for (TaxYearInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TAXINFOTYPE.toString() + ":" + id);
    }
}
