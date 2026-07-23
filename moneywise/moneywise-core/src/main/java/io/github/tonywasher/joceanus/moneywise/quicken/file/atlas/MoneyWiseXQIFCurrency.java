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

import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseCurrency;

/**
 * Class representing a XQIF Security record.
 */
public class MoneyWiseXQIFCurrency
        implements Comparable<MoneyWiseXQIFCurrency> {
    /**
     * The Currency.
     */
    private final String theName;

    /**
     * Constructor.
     *
     * @param pCurrency the Currency
     */
    public MoneyWiseXQIFCurrency(final MoneyWiseCurrency pCurrency) {
        /* Store data */
        theName = pCurrency.getName();
    }


    @Override
    public String toString() {
        return getName();
    }

    /**
     * Obtain the Name.
     *
     * @return the Name
     */
    public String getName() {
        return theName;
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
        final MoneyWiseXQIFCurrency myCurr = (MoneyWiseXQIFCurrency) pThat;

        /* Check value */
        return theName.equals(myCurr.getName());

    }

    @Override
    public int hashCode() {
        return theName.hashCode();
    }

    @Override
    public int compareTo(final MoneyWiseXQIFCurrency pThat) {
        return theName.compareTo(pThat.getName());
    }
}
