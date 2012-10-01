/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.data.statics;

import net.sourceforge.JDataManager.DataType;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.StaticData.StaticInterface;

/**
 * Enumeration of Tax Year Info Classes.
 */
public enum TaxYearInfoClass implements StaticInterface {
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
    RENTALALLOW(4, 3, DataType.MONEY),

    /**
     * Capital Allowance.
     */
    CAPITALALLOW(5, 4, DataType.MONEY),

    /**
     * Low Age Allowance.
     */
    LOAGEALLOW(6, 5, DataType.MONEY),

    /**
     * High Age Allowance.
     */
    HIAGEALLOW(7, 6, DataType.MONEY),

    /**
     * Age Allowance Limit.
     */
    AGEALLOWLIMIT(8, 7, DataType.MONEY),

    /**
     * Additional Allowance Limit.
     */
    ADDALLOWLIMIT(9, 8, DataType.MONEY),

    /**
     * Additional Income Threshold.
     */
    ADDINCOMETHOLD(10, 9, DataType.MONEY),

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
    INTTAXRATE(14, 13, DataType.RATE),

    /**
     * Dividend Tax Rate.
     */
    DIVTAXRATE(15, 14, DataType.RATE),

    /**
     * High Dividend Tax Rate.
     */
    HIDIVTAXRATE(16, 15, DataType.RATE),

    /**
     * Additional Tax Rate.
     */
    ADDTAXRATE(17, 16, DataType.RATE),

    /**
     * Additional Dividend Tax Rate.
     */
    ADDDIVTAXRATE(18, 17, DataType.RATE),

    /**
     * Capital Tax Rate.
     */
    CAPTAXRATE(19, 18, DataType.RATE),

    /**
     * High Capital Tax Rate.
     */
    HICAPTAXRATE(20, 19, DataType.RATE);

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
     * Obtain Class Id.
     * @return the class id
     */
    @Override
    public int getClassId() {
        return theId;
    }

    /**
     * Obtain Class Order.
     * @return the class order
     */
    @Override
    public int getOrder() {
        return theOrder;
    }

    /**
     * Obtain Data Type.
     * @return the date type
     */
    public DataType getDataType() {
        return theDataType;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     * @param pDataType the data type
     */
    private TaxYearInfoClass(final int uId,
                             final int uOrder,
                             final DataType pDataType) {
        theId = uId;
        theOrder = uOrder;
        theDataType = pDataType;
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static TaxYearInfoClass fromId(final int id) throws JDataException {
        for (TaxYearInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid TaxYear Info Class Id: " + id);
    }
}
