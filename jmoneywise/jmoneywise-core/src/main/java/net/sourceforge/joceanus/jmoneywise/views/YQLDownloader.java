/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/jmoneywise-core/src/main/java/net/sourceforge/joceanus/jmoneywise/views/AnalysisView.java $
 * $Revision: 600 $
 * $Author: Tony $
 * $Date: 2015-04-22 08:08:41 +0100 (Wed, 22 Apr 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.views;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.http.YQLClient;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.views.SpotExchangeRate.SpotExchangeList;
import net.sourceforge.joceanus.jmoneywise.views.SpotSecurityPrice.SpotSecurityList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;

/**
 * YQL DownLoader.
 */
public final class YQLDownloader {
    /**
     * Private constructor.
     */
    private YQLDownloader() {
    }

    /**
     * Download prices.
     * @param pPrices the prices list
     * @return changeMade true/false
     * @throws JOceanusException on error
     */
    public static boolean downloadPrices(final SpotSecurityList pPrices) throws JOceanusException {
        /* Determine currency for the prices */
        MoneyWiseData myData = pPrices.getDataSet();
        AssetCurrency myCurrency = myData.getDefaultCurrency();
        boolean hasChanges = false;

        /* If we have a default currency */
        if (myCurrency != null) {
            /* Protect against exceptions */
            try (YQLClient myClient = new YQLClient(myData.getDataFormatter())) {
                /* Build the symbols list */
                List<String> mySymbols = new ArrayList<String>();
                Iterator<SpotSecurityPrice> myIterator = pPrices.iterator();
                while (myIterator.hasNext()) {
                    SpotSecurityPrice mySpot = myIterator.next();

                    /* Ignore closed prices */
                    if (!mySpot.isDisabled()) {
                        /* Add the symbol to the list */
                        Security mySecurity = mySpot.getSecurity();
                        mySymbols.add(mySecurity.getSymbol());
                    }
                }

                /* Access the prices */
                Map<String, JPrice> myPrices = myClient.obtainSecurityPrices(mySymbols, myCurrency.getCurrency());

                /* re-loop through the securities */
                myIterator = pPrices.iterator();
                while (myIterator.hasNext()) {
                    SpotSecurityPrice mySpot = myIterator.next();

                    /* Ignore closed prices */
                    if (!mySpot.isDisabled()) {
                        /* Lookup the price */
                        Security mySecurity = mySpot.getSecurity();
                        JPrice myPrice = myPrices.get(mySecurity.getSymbol());

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

                /* Catch exceptions */
            } catch (JOceanusException | IOException e) {
                throw new JMoneyWiseIOException("Failed to download prices", e);
            }
        }

        /* Return change indication */
        return hasChanges;
    }

    /**
     * Download rates.
     * @param pRates the rates list
     * @return changeMade true/false
     * @throws JOceanusException on error
     */
    public static boolean downloadRates(final SpotExchangeList pRates) throws JOceanusException {
        /* Determine currency for the prices */
        MoneyWiseData myData = pRates.getDataSet();
        AssetCurrency myCurrency = myData.getDefaultCurrency();
        boolean hasChanges = false;

        /* If we have a default currency */
        if (myCurrency != null) {
            /* Protect against exceptions */
            try (YQLClient myClient = new YQLClient(myData.getDataFormatter())) {
                /* Build the currency list */
                List<Currency> myCurrencies = new ArrayList<Currency>();
                Iterator<SpotExchangeRate> myIterator = pRates.iterator();
                while (myIterator.hasNext()) {
                    SpotExchangeRate mySpot = myIterator.next();

                    /* Add the currency to the list */
                    AssetCurrency myToCurr = mySpot.getToCurrency();
                    myCurrencies.add(myToCurr.getCurrency());
                }

                /* Access the rates */
                Map<Currency, JRatio> myRates = myClient.obtainExchangeRates(myCurrency.getCurrency(), myCurrencies);

                /* re-loop through the rates */
                myIterator = pRates.iterator();
                while (myIterator.hasNext()) {
                    SpotExchangeRate mySpot = myIterator.next();

                    /* Lookup the rate */
                    AssetCurrency myCurr = mySpot.getToCurrency();
                    JRatio myRate = myRates.get(myCurr.getCurrency());

                    /* If we found a rate */
                    if (myRate != null) {
                        /* Push history */
                        mySpot.pushHistory();

                        /* Set it */
                        mySpot.setExchangeRate(myRate);
                        hasChanges = true;
                    }
                }

                /* Catch exceptions */
            } catch (JOceanusException | IOException e) {
                throw new JMoneyWiseIOException("Failed to download rates", e);
            }
        }
        /* Return change indication */
        return hasChanges;
    }
}