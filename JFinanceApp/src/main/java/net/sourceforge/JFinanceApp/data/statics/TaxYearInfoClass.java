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
 * $URL: http://tony-hp/svn/Finance/JFinanceApp/branches/v1.1.0/src/main/java/net/sourceforge/JFinanceApp/data/statics/AccountClass.java $
 * $Revision: 147 $
 * $Author: Tony $
 * $Date: 2012-08-21 09:54:34 +0100 (Tue, 21 Aug 2012) $
 ******************************************************************************/
package net.sourceforge.JFinanceApp.data.statics;

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
    ALLOWANCE(1, 0),

    /**
     * Low Tax Band.
     */
    LOTAXBAND(2, 1),

    /**
     * Basic Tax Band.
     */
    BASICTAXBAND(3, 2),

    /**
     * Rental Allowance.
     */
    RENTALALLOW(4, 3),

    /**
     * Capital Allowance.
     */
    CAPITALALLOW(5, 4),

    /**
     * Low Age Allowance.
     */
    LOAGEALLOW(6, 5),

    /**
     * High Age Allowance.
     */
    HIAGEALLOW(7, 6),

    /**
     * Age Allowance Limit.
     */
    AGEALLOWLIMIT(8, 7),

    /**
     * Additional Allowance Limit.
     */
    ADDALLOWLIMIT(9, 8),

    /**
     * Additional Income Threshold.
     */
    ADDINCOMETHOLD(10, 9),

    /**
     * Low Tax Rate.
     */
    LOTAXRATE(11, 10),

    /**
     * Basic Tax Rate.
     */
    BASICTAXRATE(12, 11),

    /**
     * High Tax Rate.
     */
    HITAXRATE(13, 12),

    /**
     * Interest Tax Rate.
     */
    INTTAXRATE(14, 13),

    /**
     * Dividend Tax Rate.
     */
    DIVTAXRATE(15, 14),

    /**
     * High Dividend Tax Rate.
     */
    HIDIVTAXRATE(16, 15),

    /**
     * Additional Tax Rate.
     */
    ADDTAXRATE(17, 16),

    /**
     * Additional Dividend Tax Rate.
     */
    ADDDIVTAXRATE(18, 17),

    /**
     * Capital Tax Rate.
     */
    CAPTAXRATE(19, 18),

    /**
     * High Capital Tax Rate.
     */
    HICAPTAXRATE(20, 19);

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

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
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private TaxYearInfoClass(final int uId,
                             final int uOrder) {
        theId = uId;
        theOrder = uOrder;
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
