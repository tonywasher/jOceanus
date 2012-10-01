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

import net.sourceforge.JDataManager.DataType;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataModels.data.StaticData.StaticInterface;

/**
 * Enumeration of Account Info Classes.
 */
public enum AccountInfoClass implements StaticInterface {
    /**
     * Maturity Date.
     */
    MATURITY(1, 0, DataType.DATEDAY),

    /**
     * Parent Id.
     */
    PARENT(2, 1),

    /**
     * Alias Id.
     */
    ALIAS(3, 2),

    /**
     * WebSite.
     */
    WEBSITE(4, 3, DataType.CHARARRAY),

    /**
     * Customer #.
     */
    CUSTNO(5, 4, DataType.CHARARRAY),

    /**
     * User Id.
     */
    USERID(6, 5, DataType.CHARARRAY),

    /**
     * Password.
     */
    PASSWORD(7, 6, DataType.CHARARRAY),

    /**
     * Account.
     */
    ACCOUNT(8, 7, DataType.CHARARRAY),

    /**
     * Notes.
     */
    NOTES(9, 8, DataType.CHARARRAY);

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
     * Obtain Data Type.
     * @return the date type
     */
    public DataType getDataType() {
        return theDataType;
    }

    /**
     * is this a Link?
     * @return true/false
     */
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
        throw new JDataException(ExceptionClass.DATA, "Invalid Account Info Class Id: " + id);
    }
}
