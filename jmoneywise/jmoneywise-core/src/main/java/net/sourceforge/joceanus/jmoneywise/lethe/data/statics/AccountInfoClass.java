/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.data.statics;

import net.sourceforge.joceanus.jmetis.data.MetisDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoClass;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of Account Info Classes.
 */
public enum AccountInfoClass implements DataInfoClass {
    /**
     * Maturity Date.
     */
    MATURITY(1, 0, MetisDataType.DATE),

    /**
     * Opening Balance.
     */
    OPENINGBALANCE(2, 1, MetisDataType.MONEY),

    /**
     * AutoExpense Category.
     */
    AUTOEXPENSE(3, 2, MetisDataType.LINK),

    /**
     * AutoExpense Payee.
     */
    AUTOPAYEE(4, 3, MetisDataType.LINK),

    /**
     * WebSite.
     */
    WEBSITE(5, 4, MetisDataType.CHARARRAY),

    /**
     * Customer #.
     */
    CUSTOMERNO(6, 5, MetisDataType.CHARARRAY),

    /**
     * User Id.
     */
    USERID(7, 6, MetisDataType.CHARARRAY),

    /**
     * Password.
     */
    PASSWORD(8, 7, MetisDataType.CHARARRAY),

    /**
     * SortCode.
     */
    SORTCODE(9, 8, MetisDataType.CHARARRAY),

    /**
     * Account.
     */
    ACCOUNT(10, 9, MetisDataType.CHARARRAY),

    /**
     * Reference.
     */
    REFERENCE(11, 10, MetisDataType.CHARARRAY),

    /**
     * Notes.
     */
    NOTES(12, 11, MetisDataType.CHARARRAY),

    /**
     * Symbol.
     */
    SYMBOL(13, 12, MetisDataType.STRING),

    /**
     * Region.
     */
    REGION(14, 13, MetisDataType.LINK),

    /**
     * UnderlyingStock.
     */
    UNDERLYINGSTOCK(15, 14, MetisDataType.LINK),

    /**
     * OptionPrice.
     */
    OPTIONPRICE(16, 15, MetisDataType.PRICE);

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
     * @param uId the Id
     * @param uOrder the default order.
     * @param pDataType the data type
     */
    AccountInfoClass(final int uId,
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
        return theDataType == MetisDataType.LINK;
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
     * @throws OceanusException on error
     */
    public static AccountInfoClass fromId(final int id) throws OceanusException {
        for (AccountInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.ACCOUNTINFOTYPE.toString() + ":" + id);
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
            case SYMBOL:
                return SecurityType.SYMBOL_LEN;
            default:
                return 0;
        }
    }
}
