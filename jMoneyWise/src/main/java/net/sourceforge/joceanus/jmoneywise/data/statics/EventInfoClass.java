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

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jdatamanager.DataType;
import net.sourceforge.joceanus.jdatamodels.data.DataInfoClass;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Enumeration of EventInfo Classes..
 */
public enum EventInfoClass implements DataInfoClass {
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
    THIRDPARTY(13, 12),

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
    OPTIONSGRANT(17, 16);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventInfoClass.class.getName());

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

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
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
     * @throws JOceanusException on error
     */
    public static EventInfoClass fromId(final int id) throws JOceanusException {
        for (EventInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JOceanusException("Invalid EventInfo Class Id: "
                                    + id);
    }

    /**
     * Obtain maximum length for infoType.
     * @return the maximum length
     */
    public int getMaximumLength() {
        switch (this) {
            case REFERENCE:
                return EventInfoType.DATA_LEN;
            case COMMENTS:
                return EventInfoType.COMMENT_LEN;
            default:
                return 0;
        }
    }
}
