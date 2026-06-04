/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.data.statics;

import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseDataException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusStaticDataClass;

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
     *
     * @param uId    the id
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
            theName = bundleIdForTaxClass(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * get value from id.
     *
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
     *
     * @return true/false
     */
    public boolean analyseAccounts() {
        return switch (this) {
            case SALARY, ROOMRENTAL, RENTALINCOME, OTHERINCOME, TAXEDINTEREST, UNTAXEDINTEREST, DIVIDEND,
                 UNITTRUSTDIVIDEND, FOREIGNDIVIDEND, CHARGEABLEGAINS, CAPITALGAINS, RESIDENTIALGAINS, TAXFREE,
                 PEER2PEERINTEREST -> true;
            default -> false;
        };
    }

    /**
     * Is the basis an expense basis?
     *
     * @return true/false
     */
    public boolean isExpense() {
        return switch (this) {
            case EXPENSE, TAXPAID, VIRTUAL -> true;
            default -> false;
        };
    }

    /**
     * Obtain the resource bundleId for the tax class.
     *
     * @param pClass the tax class
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForTaxClass(final MoneyWiseTaxClass pClass) {
        /* Create the map and return it */
        return switch (pClass) {
            case SALARY -> MoneyWiseStaticResource.TAXBASIS_SALARY;
            case ROOMRENTAL -> MoneyWiseStaticResource.TAXBASIS_ROOMRENTAL;
            case RENTALINCOME -> MoneyWiseStaticResource.TAXBASIS_RENTALINCOME;
            case OTHERINCOME -> MoneyWiseStaticResource.TAXBASIS_OTHERINCOME;
            case TAXEDINTEREST -> MoneyWiseStaticResource.TAXBASIS_TAXEDINTEREST;
            case UNTAXEDINTEREST -> MoneyWiseStaticResource.TAXBASIS_UNTAXEDINTEREST;
            case DIVIDEND -> MoneyWiseStaticResource.TAXBASIS_DIVIDEND;
            case UNITTRUSTDIVIDEND -> MoneyWiseStaticResource.TAXBASIS_UTDIVIDEND;
            case FOREIGNDIVIDEND -> MoneyWiseStaticResource.TAXBASIS_FOREIGNDIVIDEND;
            case CHARGEABLEGAINS -> MoneyWiseStaticResource.TAXBASIS_CHARGEABLEGAINS;
            case RESIDENTIALGAINS -> MoneyWiseStaticResource.TAXBASIS_RESIDENTIALGAINS;
            case CAPITALGAINS -> MoneyWiseStaticResource.TAXBASIS_CAPITALGAINS;
            case PEER2PEERINTEREST -> MoneyWiseStaticResource.TRANSTYPE_PEER2PEERINTEREST;
            case TAXPAID -> MoneyWiseStaticResource.TAXBASIS_TAXPAID;
            case MARKET -> MoneyWiseStaticResource.TAXBASIS_MARKET;
            case TAXFREE -> MoneyWiseStaticResource.TAXBASIS_TAXFREE;
            case EXPENSE -> MoneyWiseStaticResource.TAXBASIS_EXPENSE;
            case VIRTUAL -> MoneyWiseStaticResource.TAXBASIS_VIRTUAL;
            default -> throw new IllegalArgumentException();
        };
    }
}
