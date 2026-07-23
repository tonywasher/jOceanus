/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;

import java.util.Objects;

/**
 * Class representing a XQIF Rate record.
 */
public class MoneyWiseXQIFRate
        implements Comparable<MoneyWiseXQIFRate> {
    /**
     * The security.
     */
    private final MoneyWiseXQIFCurrency theCurrency;

    /**
     * The date.
     */
    private final OceanusDate theDate;

    /**
     * The rate.
     */
    private final OceanusRatio theRate;

    /**
     * Constructor.
     *
     * @param pCurrency the currency
     * @param pRate     the rate
     */
    public MoneyWiseXQIFRate(final MoneyWiseXQIFCurrency pCurrency,
                             final MoneyWiseExchangeRate pRate) {
        /* Store data */
        theCurrency = pCurrency;
        theDate = pRate.getDate();
        theRate = pRate.getExchangeRate();
    }

    /**
     * Obtain the currency.
     *
     * @return the currency
     */
    public MoneyWiseXQIFCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Obtain the date.
     *
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain the rate.
     *
     * @return the rate
     */
    public OceanusRatio getRate() {
        return theRate;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        final MoneyWiseXQIFRate myRate = (MoneyWiseXQIFRate) pThat;

        /* Check currency */
        if (!getCurrency().equals(myRate.getCurrency())) {
            return false;
        }

        /* Check rate */
        if (!getRate().equals(myRate.getRate())) {
            return false;
        }

        /* Check date */
        return theDate.equals(myRate.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theCurrency, theRate, theDate);
    }

    @Override
    public int compareTo(final MoneyWiseXQIFRate pThat) {
        return theDate.compareTo(pThat.getDate());
    }
}
