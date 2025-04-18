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
package net.sourceforge.joceanus.moneywise.data.builder;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrencyClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;

/**
 * ExchangeRate Builder.
 */
public class MoneyWiseXchgRateBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The Currency.
     */
    private MoneyWiseCurrency theCurrency;

    /**
     * The Date.
     */
    private OceanusDate theDate;

    /**
     * The Rate.
     */
    private OceanusRatio theRate;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseXchgRateBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getExchangeRates().ensureMap();
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder currency(final MoneyWiseCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder currency(final MoneyWiseCurrencyClass pCurrency) {
        theCurrency = lookupCurrency(pCurrency);
        return this;
    }

    /**
     * Obtain the currency for the class.
     * @param pCurrency the class currency of the cash.
     * @return the currency
     */
    private MoneyWiseCurrency lookupCurrency(final MoneyWiseCurrencyClass pCurrency) {
        return theDataSet.getAccountCurrencies().findItemByClass(pCurrency);
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder rate(final OceanusRatio pRate) {
        theRate = pRate;
        return this;
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder rate(final String pRate) {
        return rate(new OceanusRatio(pRate));
    }

    /**
     * Set the date.
     * @param pDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder date(final OceanusDate pDate) {
        theDate = pDate;
        return this;
    }

    /**
     * Set the date.
     * @param pDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder date(final String pDate) {
        return date(new OceanusDate(pDate));
    }

    /**
     * Build the Rate.
     * @return the new Rate
     * @throws OceanusException on error
     */
    public MoneyWiseExchangeRate build() throws OceanusException {
        /* Create the rate */
        final MoneyWiseExchangeRate myRate = theDataSet.getExchangeRates().addNewItem();
        myRate.setToCurrency(theCurrency);
        myRate.setFromCurrency(theDataSet.getReportingCurrency());
        myRate.setExchangeRate(theRate);
        myRate.setDate(theDate);

        /* Reset the values */
        reset();

        /* Check for errors */
        myRate.adjustMapForItem();
        myRate.validate();
        if (myRate.hasErrors()) {
            myRate.removeItem();
            throw new MoneyWiseDataException(myRate, "Failed validation");
        }

        /* Return the rate */
        return myRate;
    }

    /**
     * Reset the builder.
     */
    public void reset() {
        /* Reset values */
        theCurrency = null;
        theRate = null;
        theDate = null;
    }
}
