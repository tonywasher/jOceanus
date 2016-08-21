/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.StaticInterface;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Enumeration of Tax Basis Classes.
 */
public enum TaxBasisClass implements StaticInterface {
    /**
     * Salary Income.
     */
    SALARY(1, 0),

    /**
     * RoomRental.
     */
    ROOMRENTAL(2, 1),

    /**
     * Rental Income.
     */
    RENTALINCOME(3, 2),

    /**
     * Taxed Interest Income.
     */
    TAXEDINTEREST(4, 3),

    /**
     * UnTaxed Interest Income.
     */
    UNTAXEDINTEREST(5, 4),

    /**
     * Dividend Income.
     */
    DIVIDEND(6, 5),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(7, 6),

    /**
     * Foreign Dividend Income.
     */
    FOREIGNDIVIDEND(8, 7),

    /**
     * Taxable gains.
     */
    TAXABLEGAINS(9, 8),

    /**
     * Residential gains.
     */
    RESIDENTIALGAINS(10, 9),

    /**
     * Capital gains.
     */
    CAPITALGAINS(11, 10),

    /**
     * Tax Free Income.
     */
    TAXFREE(12, 11),

    /**
     * BadDebt.
     */
    BADDEBT(13, 12),

    /**
     * Market Growth.
     */
    MARKET(14, 13),

    /**
     * Total Tax Paid.
     */
    TAXPAID(15, 14),

    /**
     * Gross Expense.
     */
    EXPENSE(16, 15),

    /**
     * Virtual Income.
     */
    VIRTUAL(17, 16);

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
     * Constructor.
     * @param uId the id
     * @param uOrder the order
     */
    TaxBasisClass(final int uId,
                  final int uOrder) {
        /* Set values */
        theId = uId;
        theOrder = uOrder;
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
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = StaticDataResource.getKeyForTaxBasis(this).getValue();
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
    public static TaxBasisClass fromId(final int id) throws OceanusException {
        for (TaxBasisClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseDataType.TAXBASIS.toString() + ":" + id);
    }

    /**
     * Should we analyse accounts?
     * @return true/false
     */
    public boolean analyseAccounts() {
        switch (this) {
            case SALARY:
            case ROOMRENTAL:
            case RENTALINCOME:
            case TAXEDINTEREST:
            case UNTAXEDINTEREST:
            case DIVIDEND:
            case UNITTRUSTDIVIDEND:
            case FOREIGNDIVIDEND:
            case TAXABLEGAINS:
            case CAPITALGAINS:
            case RESIDENTIALGAINS:
            case TAXFREE:
            case BADDEBT:
                return true;
            default:
                return false;
        }
    }

    /**
     * Is the basis an expense basis?
     * @return true/false
     */
    public boolean isExpense() {
        switch (this) {
            case BADDEBT:
            case EXPENSE:
            case TAXPAID:
            case VIRTUAL:
                return true;
            default:
                return false;
        }
    }
}
