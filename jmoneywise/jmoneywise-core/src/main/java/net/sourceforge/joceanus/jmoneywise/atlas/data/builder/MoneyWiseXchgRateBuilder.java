/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.builder;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrencyClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * ExchangeRate Builder.
 */
public class MoneyWiseXchgRateBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The Currency.
     */
    private AssetCurrency theCurrency;

    /**
     * The Date.
     */
    private TethysDate theDate;

    /**
     * The Rate.
     */
    private TethysRatio theRate;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseXchgRateBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getExchangeRates().ensureMap();
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder currency(final AssetCurrency pCurrency) {
        theCurrency = pCurrency;
        return this;
    }

    /**
     * Set the currency.
     * @param pCurrency the currency of the cash.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder currency(final AssetCurrencyClass pCurrency) {
        theCurrency = lookupCurrency(pCurrency);
        return this;
    }

    /**
     * Obtain the currency for the class.
     * @param pCurrency the class currency of the cash.
     * @return the currency
     */
    private AssetCurrency lookupCurrency(final AssetCurrencyClass pCurrency) {
        return theDataSet.getAccountCurrencies().findItemByClass(pCurrency);
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder rate(final TethysRatio pRate) {
        theRate = pRate;
        return this;
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder rate(final String pRate) {
        return rate(new TethysRatio(pRate));
    }

    /**
     * Set the date.
     * @param pDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder date(final TethysDate pDate) {
        theDate = pDate;
        return this;
    }

    /**
     * Set the date.
     * @param pDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseXchgRateBuilder date(final String pDate) {
        return date(new TethysDate(pDate));
    }

    /**
     * Build the Rate.
     * @return the new Rate
     * @throws OceanusException on error
     */
    public ExchangeRate build() throws OceanusException {
        /* Create the rate */
        final ExchangeRate myRate = theDataSet.getExchangeRates().addNewItem();
        myRate.setToCurrency(theCurrency);
        myRate.setFromCurrency(theDataSet.getDefaultCurrency());
        myRate.setExchangeRate(theRate);
        myRate.setDate(theDate);

        /* Check for errors */
        myRate.adjustMapForItem();
        myRate.validate();
        if (myRate.hasErrors()) {
            theDataSet.getExchangeRates().remove(myRate);
            throw new MoneyWiseDataException(myRate, "Failed validation");
        }

        /* Reset values */
        theCurrency = null;
        theRate = null;
        theDate = null;

        /* Return the rate */
        return myRate;
    }
}
