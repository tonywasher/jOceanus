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
import net.sourceforge.joceanus.jprometheus.data.DataInfoClass;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
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
     * Parent Id.
     */
    PARENT(2, 1),

    /**
     * Alias Id.
     */
    ALIAS(3, 2),

    /**
     * Portfolio Account.
     */
    PORTFOLIO(4, 3),

    /**
     * Holding Account.
     */
    HOLDING(5, 4),

    /**
     * Comments.
     */
    COMMENTS(6, 5, DataType.STRING),

    /**
     * Symbol.
     */
    SYMBOL(7, 6, DataType.STRING),

    /**
     * Opening Balance.
     */
    OPENINGBALANCE(8, 7, DataType.MONEY),

    /**
     * AutoExpense Category.
     */
    AUTOEXPENSE(9, 8),

    /**
     * WebSite.
     */
    WEBSITE(10, 9, DataType.CHARARRAY),

    /**
     * Customer #.
     */
    CUSTOMERNO(11, 10, DataType.CHARARRAY),

    /**
     * User Id.
     */
    USERID(12, 11, DataType.CHARARRAY),

    /**
     * Password.
     */
    PASSWORD(13, 12, DataType.CHARARRAY),

    /**
     * SortCode.
     */
    SORTCODE(14, 13, DataType.CHARARRAY),

    /**
     * Account.
     */
    ACCOUNT(15, 14, DataType.CHARARRAY),

    /**
     * Reference.
     */
    REFERENCE(16, 15, DataType.CHARARRAY),

    /**
     * Notes.
     */
    NOTES(17, 16, DataType.CHARARRAY);

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(AccountInfoClass.class.getName());

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
     * @throws JOceanusException on error
     */
    public static AccountInfoClass fromId(final int id) throws JOceanusException {
        for (AccountInfoClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JMoneyWiseDataException("Invalid Account Info Class Id: "
                                          + id);
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
            case COMMENTS:
                return AccountInfoType.COMMENT_LEN;
            default:
                return 0;
        }
    }
}
