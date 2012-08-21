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

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.StaticData.StaticInterface;

/**
 * Enumeration of EventInfo Classes..
 */
public enum EventInfoClass implements StaticInterface {
    /**
     * Tax Credit.
     */
    TaxCredit(1, 0),

    /**
     * National Insurance.
     */
    NatInsurance(2, 1),

    /**
     * Benefit.
     */
    Benefit(3, 2),

    /**
     * Pension.
     */
    Pension(4, 3),

    /**
     * QualifyingYears.
     */
    QualifyYears(5, 4),

    /**
     * TransferDelay.
     */
    XferDelay(6, 5),

    /**
     * Credit Units.
     */
    CreditUnits(7, 6),

    /**
     * Debit Units.
     */
    DebitUnits(8, 7),

    /**
     * Dilution.
     */
    Dilution(9, 8),

    /**
     * CashConsideration.
     */
    CashConsider(10, 9),

    /**
     * ThirdParty Account.
     */
    // ThirdParty(11, 10);
    CashAccount(11, 10);

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
     * @param uId the id
     * @param uOrder the default order
     */
    private EventInfoClass(final int uId,
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
    public static EventInfoClass fromId(final int id) throws JDataException {
        for (EventInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid EventInfo Class Id: " + id);
    }
}
