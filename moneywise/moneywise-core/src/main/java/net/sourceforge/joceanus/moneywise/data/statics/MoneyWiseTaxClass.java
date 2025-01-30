/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.statics;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.prometheus.data.PrometheusStaticDataClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * Enumeration of Tax Basis Classes.
 */
public enum MoneyWiseTaxClass
        implements PrometheusStaticDataClass {
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
     * Other Income.
     */
    OTHERINCOME(4, 3),

    /**
     * Taxed Interest Income.
     */
    TAXEDINTEREST(5, 4),

    /**
     * UnTaxed Interest Income.
     */
    UNTAXEDINTEREST(6, 5),

    /**
     * Peer2PeerInterest.
     */
    PEER2PEERINTEREST(7, 6),

    /**
     * Dividend Income.
     */
    DIVIDEND(8, 7),

    /**
     * Unit Trust Dividend Income.
     */
    UNITTRUSTDIVIDEND(9, 8),

    /**
     * Foreign Dividend Income.
     */
    FOREIGNDIVIDEND(10, 9),

    /**
     * Chargeable gains.
     */
    CHARGEABLEGAINS(11, 10),

    /**
     * Residential gains.
     */
    RESIDENTIALGAINS(12, 11),

    /**
     * Capital gains.
     */
    CAPITALGAINS(13, 12),

    /**
     * Tax Free Income.
     */
    TAXFREE(14, 13),

    /**
     * Market Growth.
     */
    MARKET(15, 14),

    /**
     * Total Tax Paid.
     */
    TAXPAID(16, 15),

    /**
     * Gross Expense.
     */
    EXPENSE(17, 16),

    /**
     * Virtual Income.
     */
    VIRTUAL(18, 17);

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
    MoneyWiseTaxClass(final int uId,
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
            theName = MoneyWiseStaticResource.getKeyForTaxBasis(this).getValue();
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
    public static MoneyWiseTaxClass fromId(final int id) throws OceanusException {
        for (MoneyWiseTaxClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new MoneyWiseDataException("Invalid ClassId for " + MoneyWiseStaticDataType.TAXBASIS.toString() + ":" + id);
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
            case OTHERINCOME:
            case TAXEDINTEREST:
            case UNTAXEDINTEREST:
            case DIVIDEND:
            case UNITTRUSTDIVIDEND:
            case FOREIGNDIVIDEND:
            case CHARGEABLEGAINS:
            case CAPITALGAINS:
            case RESIDENTIALGAINS:
            case TAXFREE:
            case PEER2PEERINTEREST:
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
            case EXPENSE:
            case TAXPAID:
            case VIRTUAL:
                return true;
            default:
                return false;
        }
    }
}
