/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.data.statics;

import java.util.Currency;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.StaticInterface;

/**
 * Enumeration of AccountCurrency Classes.
 */
public enum AccountCurrencyClass implements StaticInterface {
    /**
     * British Pounds.
     */
    GBP(1, 0),

    /**
     * US Dollars.
     */
    USD(2, 1),

    /**
     * Euro.
     */
    EUR(3, 2),

    /**
     * Canadian Dollars.
     */
    CND(4, 3),

    /**
     * Australian Dollars.
     */
    AUD(5, 4),

    /**
     * NewZealand Dollars.
     */
    NZD(6, 5),

    /**
     * Chinese Yuan.
     */
    CNY(7, 6),

    /**
     * Japanese Yen.
     */
    JPY(8, 7),

    /**
     * HongKong Dollars.
     */
    HKD(9, 8),

    /**
     * Swedish Krona.
     */
    SEK(10, 9),

    /**
     * Turkish Lira.
     */
    TRY(11, 10);

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Class Order.
     */
    private final int theOrder;

    /**
     * Currency.
     */
    private final Currency theCurrency;

    @Override
    public int getClassId() {
        return theId;
    }

    @Override
    public int getOrder() {
        return theOrder;
    }

    /**
     * Obtain currency.
     * @return the currency
     */
    public Currency getCurrency() {
        return theCurrency;
    }

    /**
     * Constructor.
     * @param uId the Id
     * @param uOrder the default order.
     */
    private AccountCurrencyClass(final int uId,
                                 final int uOrder) {
        theId = uId;
        theOrder = uOrder;
        theCurrency = Currency.getInstance(name());
    }

    /**
     * get value from id.
     * @param id the id value
     * @return the corresponding enum object
     * @throws JDataException on error
     */
    public static AccountCurrencyClass fromId(final int id) throws JDataException {
        for (AccountCurrencyClass myClass : values()) {
            if (myClass.getClassId() == id) {
                return myClass;
            }
        }
        throw new JDataException(ExceptionClass.DATA, "Invalid Account Currency Class Id: "
                                                      + id);
    }
}
