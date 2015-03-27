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
 * Enumeration of TransactionInfo Classes..
 */
public enum TransactionInfoClass implements DataInfoClass {
    /**
     * Tax Credit.
     */
    TAXCREDIT(1, 0, DataType.MONEY),

    /**
     * National Insurance.
     */
    NATINSURANCE(2, 1, DataType.MONEY),

    /**
     * Deemed Benefit.
     */
    DEEMEDBENEFIT(3, 2, DataType.MONEY),

    /**
     * Pension.
     */
    PENSION(4, 3, DataType.MONEY),

    /**
     * QualifyingYears.
     */
    QUALIFYYEARS(5, 4, DataType.INTEGER),

    /**
     * CreditDate.
     */
    CREDITDATE(6, 5, DataType.DATEDAY),

    /**
     * Credit Units.
     */
    CREDITUNITS(7, 6, DataType.UNITS),

    /**
     * Debit Units.
     */
    DEBITUNITS(8, 7, DataType.UNITS),

    /**
     * Dilution.
     */
    DILUTION(9, 8, DataType.DILUTION),

    /**
     * Reference.
     */
    REFERENCE(10, 9, DataType.STRING),

    /**
     * Charity Donation.
     */
    CHARITYDONATION(11, 10, DataType.MONEY),

    /**
     * Credit Amount.
     */
    CREDITAMOUNT(12, 11, DataType.MONEY),

    /**
     * ThirdParty.
     */
    THIRDPARTY(13, 12, DataType.LINK),

    /**
     * Comments.
     */
    COMMENTS(14, 13, DataType.STRING),

    /**
     * Price.
     */
    PRICE(15, 14, DataType.PRICE),

    /**
     * Comments.
     */
    COMMISSION(16, 15, DataType.MONEY),

    /**
     * Grant.
     */
    OPTIONSGRANT(17, 16, DataType.LINK),

    /**
     * TransactionTag.
     */
    TRANSTAG(18, 17, DataType.LINKSET);

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
     * @param uId the id
     * @param uOrder the default order
     * @param pDataType the data type
     */
    private TransactionInfoClass(final int uId,
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
        return theDataType == DataType.LINKSET;
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
     * @throws JOceanusException on error
     */
    public static TransactionInfoClass fromId(final int id) throws JOceanusException {
        for (TransactionInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TRANSINFOTYPE.toString() + ":" + id);
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
