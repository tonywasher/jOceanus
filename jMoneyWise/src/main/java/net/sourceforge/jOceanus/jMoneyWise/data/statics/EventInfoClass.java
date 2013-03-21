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

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoClass;

/**
 * Enumeration of EventInfo Classes..
 */
public enum EventInfoClass implements DataInfoClass {
    /**
     * Tax Credit.
     */
    TaxCredit(1, 0, DataType.MONEY),

    /**
     * National Insurance.
     */
    NatInsurance(2, 1, DataType.MONEY),

    /**
     * Benefit.
     */
    Benefit(3, 2, DataType.MONEY),

    /**
     * Pension.
     */
    Pension(4, 3, DataType.MONEY),

    /**
     * QualifyingYears.
     */
    QualifyYears(5, 4, DataType.INTEGER),

    /**
     * TransferDelay.
     */
    XferDelay(6, 5, DataType.INTEGER),

    /**
     * Credit Units.
     */
    CreditUnits(7, 6, DataType.UNITS),

    /**
     * Debit Units.
     */
    DebitUnits(8, 7, DataType.UNITS),

    /**
     * Dilution.
     */
    Dilution(9, 8, DataType.DILUTION),

    /**
     * Reference.
     */
    Reference(10, 9, DataType.STRING),

    /**
     * Donation.
     */
    Donation(11, 10, DataType.MONEY),

    /**
     * ThirdParty.
     */
    ThirdParty(12, 11);

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
     * Is this a Link?.
     */
    private final boolean isLink;

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
        return isLink;
    }

    /**
     * Constructor.
     * @param uId the id
     * @param uOrder the default order
     * @param pDataType the data type
     */
    private EventInfoClass(final int uId,
                           final int uOrder,
                           final DataType pDataType) {
        theId = uId;
        theOrder = uOrder;
        theDataType = pDataType;
        isLink = false;
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
        theDataType = DataType.INTEGER;
        isLink = true;
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
        throw new JDataException(ExceptionClass.DATA, "Invalid EventInfo Class Id: "
                                                      + id);
    }
}
