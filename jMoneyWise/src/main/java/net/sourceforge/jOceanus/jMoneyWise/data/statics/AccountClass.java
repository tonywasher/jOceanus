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
import net.sourceforge.jOceanus.jDataModels.data.StaticData.StaticInterface;

/**
 * Enumeration of Account Type Classes.
 */
public enum AccountClass implements StaticInterface {
    /**
     * Current Banking Account.
     */
    CURRENT(1, 0),

    /**
     * Instant Access Savings Account.
     */
    INSTANT(2, 1),

    /**
     * Savings Account Requiring Notice for Withdrawals.
     */
    NOTICE(3, 2),

    /**
     * Fixed Rate Savings Bond.
     */
    BOND(4, 3),

    /**
     * Instant Access Cash ISA Account.
     */
    CASHISA(5, 4),

    /**
     * Fixed Rate Cash ISA Bond.
     */
    ISABOND(6, 5),

    /**
     * Index Linked Bond.
     */
    TAXFREEBOND(7, 6),

    /**
     * Equity Bond.
     */
    EQUITYBOND(8, 7),

    /**
     * Shares.
     */
    SHARES(9, 8),

    /**
     * Unit Trust or OEIC.
     */
    UNITTRUST(10, 9),

    /**
     * Life Bond.
     */
    LIFEBOND(11, 10),

    /**
     * Unit Trust or OEIC in ISA wrapper.
     */
    UNITISA(12, 11),

    /**
     * Car.
     */
    CAR(13, 12),

    /**
     * House.
     */
    HOUSE(14, 13),

    /**
     * Debts.
     */
    DEBTS(15, 16),

    /**
     * CreditCard.
     */
    CREDITCARD(16, 15),

    /**
     * WriteOff.
     */
    WRITEOFF(17, 22),

    /**
     * External Account.
     */
    EXTERNAL(18, 24),

    /**
     * Employer Account.
     */
    EMPLOYER(19, 18),

    /**
     * Asset Owner Account.
     */
    OWNER(20, 25),

    /**
     * Market.
     */
    MARKET(21, 26),

    /**
     * Inland Revenue.
     */
    TAXMAN(22, 20),

    /**
     * Cash.
     */
    CASH(23, 19),

    /**
     * Inheritance.
     */
    INHERITANCE(24, 21),

    /**
     * Endowment.
     */
    ENDOWMENT(25, 14),

    /**
     * Benefit.
     */
    BENEFIT(26, 23),

    /**
     * Deferred between tax years.
     */
    DEFERRED(27, 17);

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
    private AccountClass(final int uId,
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
    public static AccountClass fromId(final int id) throws JDataException {
        for (AccountClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Account Class Id: " + id);
    }
}
