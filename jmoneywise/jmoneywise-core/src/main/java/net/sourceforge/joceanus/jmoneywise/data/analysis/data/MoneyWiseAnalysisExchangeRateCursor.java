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
package net.sourceforge.joceanus.jmoneywise.data.analysis.data;

import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseExchangeRate.MoneyWiseExchangeRateDataMap;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

/**
 * Quick access to dated exchange Rates on an analysis pass.
 */
public class MoneyWiseAnalysisExchangeRateCursor {
    /**
     * ExchangeRate data map.
     */
    private final MoneyWiseExchangeRateDataMap theDataMap;

    /**
     * Cursor map.
     */
    private final Map<MoneyWiseCurrency, CurrencyCursor> theCursorMap;

    /**
     * Constructor.
     * @param pData the dataSet
     */
    protected MoneyWiseAnalysisExchangeRateCursor(final MoneyWiseDataSet pData) {
        /* Obtain the data map */
        theDataMap = pData.getExchangeRates().getDataMap();

        /* Create the cursor map */
        theCursorMap = new HashMap<>();
    }

    /**
     * Obtain exchange rate for currency and date.
     * @param pCurrency the currency
     * @param pDate the date
     * @return the exchange rate
     */
    public TethysRatio getExchangeRate(final MoneyWiseCurrency pCurrency,
                                       final TethysDate pDate) {
        /* Access cursor and return rate */
        final CurrencyCursor myCursor = getCursor(pCurrency);
        return myCursor.getExchangeRate(pDate);
    }

    /**
     * Obtain currency cursor.
     * @param pCurrency the currency
     * @return the cursor
     */
    private CurrencyCursor getCursor(final MoneyWiseCurrency pCurrency) {
        /* Look for existing cursor */
        CurrencyCursor myCursor = theCursorMap.get(pCurrency);

        /* If we do not have a cursor */
        if (myCursor == null) {
            /* Allocate new cursor */
            myCursor = new CurrencyCursor(pCurrency);
            theCursorMap.put(pCurrency, myCursor);
        }

        /* return the cursor */
        return myCursor;
    }

    /**
     * Currency Cursor class.
     */
    private final class CurrencyCursor {
        /**
         * Exchange Rate iterator.
         */
        private final ListIterator<MoneyWiseExchangeRate> theIterator;

        /**
         * The current exchange rate.
         */
        private TethysRatio theCurrent = TethysRatio.ONE;

        /**
         * The next date.
         */
        private TethysDate theNextDate;

        /**
         * The next exchange rate.
         */
        private TethysRatio theNextRate;

        /**
         * Constructor.
         * @param pCurrency the currency
         */
        private CurrencyCursor(final MoneyWiseCurrency pCurrency) {
            /* Access the Rate list */
            theIterator = theDataMap.rateIterator(pCurrency);

            /* If we have an iterator */
            if (theIterator != null) {
                /* move the cursor */
                moveCursor();
            }
        }

        /**
         * Move the cursor.
         */
        private void moveCursor() {
            /* If we have an iterator */
            if (theIterator.hasPrevious()) {
                /* access next rate and record */
                final MoneyWiseExchangeRate myRate = theIterator.previous();
                theNextRate = myRate.getExchangeRate();
                theNextDate = myRate.getDate();
            } else {
                theNextDate = null;
                theNextRate = null;
            }
        }

        /**
         * Obtain exchange rate for date.
         * @param pDate the date
         * @return the exchange rate
         */
        private TethysRatio getExchangeRate(final TethysDate pDate) {
            /* if we have a later rate */
            if (theNextDate != null) {
                /* while we need to move the cursor */
                while (pDate.compareTo(theNextDate) >= 0) {
                    /* store rate */
                    theCurrent = theNextRate;

                    /* move the cursor */
                    moveCursor();
                }
            }

            /* Return the rate */
            return theCurrent;
        }
    }
}