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
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Currency Rate List.
 *
 * @author Tony Washer
 */
public class MoneyWiseXQIFCurrencyRates
        implements Comparable<MoneyWiseXQIFCurrencyRates> {
    /**
     * The Currency.
     */
    private final MoneyWiseXQIFCurrency theCurrency;

    /**
     * The Price List.
     */
    private final List<MoneyWiseXQIFRate> theRates;

    /**
     * Constructor.
     *
     * @param pCurrency the currency.
     */
    public MoneyWiseXQIFCurrencyRates(final MoneyWiseCurrency pCurrency) {
        this(new MoneyWiseXQIFCurrency(pCurrency));
    }

    /**
     * Constructor.
     *
     * @param pCurrency the currency.
     */
    public MoneyWiseXQIFCurrencyRates(final MoneyWiseXQIFCurrency pCurrency) {
        /* Store parameters */
        theCurrency = pCurrency;

        /* Create the list */
        theRates = new ArrayList<>();
    }

    /**
     * Obtain the security.
     *
     * @return the security
     */
    public MoneyWiseXQIFCurrency getCurrency() {
        return theCurrency;
    }

    /**
     * Obtain the rates.
     *
     * @return the rates
     */
    public List<MoneyWiseXQIFRate> getRates() {
        return theRates;
    }

    /**
     * Add rate.
     *
     * @param pRate the rate to add
     */
    public void addRate(final MoneyWiseExchangeRate pRate) {
        /* Allocate price */
        final MoneyWiseXQIFRate myRate = new MoneyWiseXQIFRate(theCurrency, pRate);

        /* Add to the list */
        theRates.add(myRate);
    }

    /**
     * Sort the rates.
     */
    public void sortRates() {
        Collections.sort(theRates);
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
        final MoneyWiseXQIFCurrencyRates myRates = (MoneyWiseXQIFCurrencyRates) pThat;

        /* Check currency */
        if (!theCurrency.equals(myRates.getCurrency())) {
            return false;
        }

        /* Check rates */
        return theRates.equals(myRates.getRates());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theCurrency, theRates);
    }

    @Override
    public int compareTo(final MoneyWiseXQIFCurrencyRates pThat) {
        return theCurrency.compareTo(pThat.getCurrency());
    }
}
