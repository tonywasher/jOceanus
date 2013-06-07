/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
 * Enumeration of Account Info Classes.
 */
public enum AccountInfoClass implements DataInfoClass {
    /**
     * Maturity Date.
     */
    Maturity(1, 0, DataType.DATEDAY),

    /**
     * Parent Id.
     */
    Parent(2, 1),

    /**
     * Alias Id.
     */
    Alias(3, 2),

    /**
     * Holding Account.
     */
    Holding(4, 3),

    /**
     * Comments.
     */
    Comments(5, 4, DataType.STRING),

    /**
     * Symbol.
     */
    Symbol(6, 5, DataType.STRING),

    /**
     * Opening Balance.
     */
    OpeningBalance(7, 6, DataType.MONEY),

    /**
     * AutoExpense Category.
     */
    AutoExpense(8, 7),

    /**
     * Currency.
     */
    Currency(9, 8),

    /**
     * WebSite.
     */
    WebSite(10, 9, DataType.CHARARRAY),

    /**
     * Customer #.
     */
    CustomerNo(11, 10, DataType.CHARARRAY),

    /**
     * User Id.
     */
    UserId(12, 11, DataType.CHARARRAY),

    /**
     * Password.
     */
    Password(13, 12, DataType.CHARARRAY),

    /**
     * SortCode.
     */
    SortCode(14, 13, DataType.CHARARRAY),

    /**
     * Account.
     */
    Account(15, 14, DataType.CHARARRAY),

    /**
     * Reference.
     */
    Reference(16, 15, DataType.CHARARRAY),

    /**
     * Notes.
     */
    Notes(17, 16, DataType.CHARARRAY);

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
     * @param uId the Id
     * @param uOrder the default order.
     * @param pDataType the data type
     */
    private AccountInfoClass(final int uId,
                             final int uOrder,
                             final DataType pDataType) {
        theId = uId;
        theOrder = uOrder;
        theDataType = pDataType;
        isLink = false;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private AccountInfoClass(final int uId,
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
    public static AccountInfoClass fromId(final int id) throws JDataException {
        for (AccountInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Account Info Class Id: "
                                                      + id);
    }

    /**
     * Obtain maximum length for infoType.
     * @return the maximum length
     */
    public int getMaximumLength() {
        switch (this) {
            case WebSite:
                return AccountInfoType.WEBSITE_LEN;
            case CustomerNo:
            case UserId:
            case Password:
            case SortCode:
            case Account:
            case Reference:
                return AccountInfoType.DATA_LEN;
            case Notes:
                return AccountInfoType.NOTES_LEN;
            case Comments:
                return AccountInfoType.COMMENT_LEN;
            default:
                return 0;
        }
    }
}
