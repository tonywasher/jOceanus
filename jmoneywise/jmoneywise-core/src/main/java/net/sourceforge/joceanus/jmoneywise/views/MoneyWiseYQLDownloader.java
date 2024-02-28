/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.http.MetisHTTPYQLClient;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseSpotExchangeRate.MoneyWiseSpotExchangeList;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseSpotSecurityPrice.MoneyWiseSpotSecurityList;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * YQL DownLoader.
 */
public final class MoneyWiseYQLDownloader {
    /**
     * Private constructor.
     */
    private MoneyWiseYQLDownloader() {
    }

    /**
     * Download prices.
     * @param pPrices the prices list
     * @return changeMade true/false
     * @throws OceanusException on error
     */
    public static boolean downloadPrices(final MoneyWiseSpotSecurityList pPrices) throws OceanusException {
        /* Determine currency for the prices */
        final MoneyWiseDataSet myData = pPrices.getDataSet();
        final MoneyWiseCurrency myCurrency = myData.getReportingCurrency();
        boolean hasChanges = false;

        /* If we have a default currency */
        if (myCurrency != null) {
            /* Create the http client */
            final MetisHTTPYQLClient myClient = new MetisHTTPYQLClient(myData.getDataFormatter());

            /* Build the symbols list */
            final List<String> mySymbols = new ArrayList<>();
            Iterator<MoneyWiseSpotSecurityPrice> myIterator = pPrices.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSpotSecurityPrice mySpot = myIterator.next();

                /* Ignore closed prices */
                if (!mySpot.isDisabled()) {
                    /* Add the symbol to the list */
                    final MoneyWiseSecurity mySecurity = mySpot.getSecurity();
                    mySymbols.add(mySecurity.getSymbol());
                }
            }

            /* Access the prices */
            final Map<String, TethysPrice> myPrices = myClient.obtainSecurityPrices(mySymbols, myCurrency.getCurrency());

            /* re-loop through the securities */
            myIterator = pPrices.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSpotSecurityPrice mySpot = myIterator.next();

                /* Ignore closed prices */
                if (!mySpot.isDisabled()) {
                    /* Lookup the price */
                    final MoneyWiseSecurity mySecurity = mySpot.getSecurity();
                    final TethysPrice myPrice = myPrices.get(mySecurity.getSymbol());

                    /* If we found a price */
                    if (myPrice != null) {
                        /* Push history */
                        mySpot.pushHistory();

                        /* Set it */
                        mySpot.setPrice(myPrice);
                        hasChanges = true;
                    }
                }
            }
        }

        /* Return change indication */
        return hasChanges;
    }

    /**
     * Download rates.
     * @param pRates the rates list
     * @return changeMade true/false
     * @throws OceanusException on error
     */
    public static boolean downloadRates(final MoneyWiseSpotExchangeList pRates) throws OceanusException {
        /* Determine currency for the prices */
        final MoneyWiseDataSet myData = pRates.getDataSet();
        final MoneyWiseCurrency myCurrency = myData.getReportingCurrency();
        boolean hasChanges = false;

        /* If we have a default currency */
        if (myCurrency != null) {
            /* Create the client */
            final MetisHTTPYQLClient myClient = new MetisHTTPYQLClient(myData.getDataFormatter());

            /* Build the currency list */
            final List<Currency> myCurrencies = new ArrayList<>();
            Iterator<MoneyWiseSpotExchangeRate> myIterator = pRates.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSpotExchangeRate mySpot = myIterator.next();

                /* Add the currency to the list */
                final MoneyWiseCurrency myToCurr = mySpot.getToCurrency();
                myCurrencies.add(myToCurr.getCurrency());
            }

            /* Access the rates */
            final Map<Currency, TethysRatio> myRates = myClient.obtainExchangeRates(myCurrency.getCurrency(), myCurrencies);

            /* re-loop through the rates */
            myIterator = pRates.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseSpotExchangeRate mySpot = myIterator.next();

                /* Lookup the rate */
                final MoneyWiseCurrency myCurr = mySpot.getToCurrency();
                final TethysRatio myRate = myRates.get(myCurr.getCurrency());

                /* If we found a rate */
                if (myRate != null) {
                    /* Push history */
                    mySpot.pushHistory();

                    /* Set it */
                    mySpot.setExchangeRate(myRate);
                    hasChanges = true;
                }
            }
        }

        /* Return change indication */
        return hasChanges;
    }
}
