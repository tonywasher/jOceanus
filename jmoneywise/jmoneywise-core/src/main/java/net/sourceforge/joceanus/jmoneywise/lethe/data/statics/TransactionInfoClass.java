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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of TransactionInfo Classes..
 */
public enum TransactionInfoClass implements DataInfoClass {
    /**
     * Tax Credit.
     */
    TAXCREDIT(1, 0, MetisDataType.MONEY),

    /**
     * National Insurance.
     */
    NATINSURANCE(2, 1, MetisDataType.MONEY),

    /**
     * Deemed Benefit.
     */
    DEEMEDBENEFIT(3, 2, MetisDataType.MONEY),

    /**
     * QualifyingYears.
     */
    QUALIFYYEARS(4, 3, MetisDataType.INTEGER),

    /**
     * Account Delta Units.
     */
    ACCOUNTDELTAUNITS(5, 4, MetisDataType.UNITS),

    /**
     * Partner Delta Units.
     */
    PARTNERDELTAUNITS(6, 5, MetisDataType.UNITS),

    /**
     * Dilution.
     */
    DILUTION(7, 6, MetisDataType.DILUTION),

    /**
     * Reference.
     */
    REFERENCE(8, 7, MetisDataType.STRING),

    /**
     * Withheld.
     */
    WITHHELD(9, 8, MetisDataType.MONEY),

    /**
     * Partner Amount.
     */
    PARTNERAMOUNT(10, 9, MetisDataType.MONEY),

    /**
     * ThirdParty Amount.
     */
    RETURNEDCASH(11, 10, MetisDataType.MONEY),

    /**
     * ReturnedCashAccount.
     */
    RETURNEDCASHACCOUNT(12, 11, MetisDataType.LINK),

    /**
     * Comments.
     */
    COMMENTS(13, 12, MetisDataType.STRING),

    /**
     * TransactionTag.
     */
    TRANSTAG(14, 13, MetisDataType.LINKSET);

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
     * @param uId the id
     * @param uOrder the default order
     * @param pDataType the data type
     */
    TransactionInfoClass(final int uId,
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
        switch (theDataType) {
            case LINK:
            case LINKSET:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isLinkSet() {
        return theDataType == MetisDataType.LINKSET;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = StaticDataResource.getKeyForTransInfo(this).getValue();
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
    public static TransactionInfoClass fromId(final int id) throws OceanusException {
        for (TransactionInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TRANSINFOTYPE.toString() + ":" + id);
    }

    /**
     * Obtain maximum length for infoType.
     * @return the maximum length
     */
    public int getMaximumLength() {
        switch (this) {
            case REFERENCE:
                return TransactionInfoType.DATA_LEN;
            case COMMENTS:
                return TransactionInfoType.COMMENT_LEN;
            default:
                return 0;
        }
    }
}
