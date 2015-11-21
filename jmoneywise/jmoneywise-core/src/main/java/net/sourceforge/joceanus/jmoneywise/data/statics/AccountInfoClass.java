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
 * Enumeration of Account Info Classes.
 */
public enum AccountInfoClass implements DataInfoClass {
    /**
     * Maturity Date.
     */
    MATURITY(1, 0, DataType.DATEDAY),

    /**
     * Opening Balance.
     */
    OPENINGBALANCE(2, 1, DataType.MONEY),

    /**
     * AutoExpense Category.
     */
    AUTOEXPENSE(3, 2, DataType.LINK),

    /**
     * AutoExpense Payee.
     */
    AUTOPAYEE(4, 3, DataType.LINK),

    /**
     * WebSite.
     */
    WEBSITE(5, 4, DataType.CHARARRAY),

    /**
     * Customer #.
     */
    CUSTOMERNO(6, 5, DataType.CHARARRAY),

    /**
     * User Id.
     */
    USERID(7, 6, DataType.CHARARRAY),

    /**
     * Password.
     */
    PASSWORD(8, 7, DataType.CHARARRAY),

    /**
     * SortCode.
     */
    SORTCODE(9, 8, DataType.CHARARRAY),

    /**
     * Account.
     */
    ACCOUNT(10, 9, DataType.CHARARRAY),

    /**
     * Reference.
     */
    REFERENCE(11, 10, DataType.CHARARRAY),

    /**
     * Notes.
     */
    NOTES(12, 11, DataType.CHARARRAY);

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
    AccountInfoClass(final int uId,
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
        return theDataType == DataType.LINK;
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
            theName = StaticDataResource.getKeyForAccountInfo(this).getValue();
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
    public static AccountInfoClass fromId(final int id) throws JOceanusException {
        for (AccountInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.ACCOUNTINFOTYPE.toString() + ":" + id);
    }

    /**
     * Obtain maximum length for infoType.
     * @return the maximum length
     */
    public int getMaximumLength() {
        switch (this) {
            case WEBSITE:
                return AccountInfoType.WEBSITE_LEN;
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
                return AccountInfoType.DATA_LEN;
            case NOTES:
                return AccountInfoType.NOTES_LEN;
            default:
                return 0;
        }
    }
}
