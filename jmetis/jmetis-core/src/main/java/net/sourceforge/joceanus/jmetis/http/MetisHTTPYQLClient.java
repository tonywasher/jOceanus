/*******************************************************************************
 * Metis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.http;

import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Client to query YQL.
 * @author Tony Washer
 */
public class MetisHTTPYQLClient
        extends MetisHTTPDataClient {
    /**
     * The YQL webSite.
     */
    private static final String YQL_WEBSITE = "http://query.yahooapis.com/v1/public/yql?q=";

    /**
     * YQL trailer.
     */
    private static final String YQL_TAIL = "&format=json&lang=en_GB&env=store://datatables.org/alltableswithkeys";

    /**
     * Prices database.
     */
    private static final String YQLDB_PRICES = "yahoo.finance.quote";

    /**
     * Rates database.
     */
    private static final String YQLDB_RATES = "yahoo.finance.xchange";

    /**
     * YQL select statement.
     */
    private static final String YQL_SELECT = "select * from ";

    /**
     * YQL symbol select statement.
     */
    private static final String YQLSEL_SYMBOL = " where symbol in (\"";

    /**
     * YQL pair select statement.
     */
    private static final String YQLSEL_PAIR = " where pair in (\"";

    /**
     * YQL select end statement.
     */
    private static final String YQLSEL_END = "\")";

    /**
     * YQL select middle statement.
     */
    private static final String YQLSEL_MID = "\",\"";

    /**
     * YQL result query.
     */
    private static final String YQLRES_QUERY = "query";

    /**
     * YQL result query.
     */
    private static final String YQLRES_RESULTS = "results";

    /**
     * YQL result quote.
     */
    private static final String YQLRES_QUOTE = "quote";

    /**
     * YQL result rate.
     */
    private static final String YQLRES_RATE = "rate";

    /**
     * YQL field Symbol.
     */
    private static final String YQLFLD_SYMBOL = "Symbol";

    /**
     * YQL field Price.
     */
    private static final String YQLFLD_PRICE = "LastTradePriceOnly";

    /**
     * YQL field Id.
     */
    private static final String YQLFLD_ID = "id";

    /**
     * YQL field Rate.
     */
    private static final String YQLFLD_RATE = "Rate";

    /**
     * YQL parse error.
     */
    private static final String YQLERROR_PARSE = "Failed to parse results";

    /**
     * Decimal parser.
     */
    private final TethysDecimalParser theParser;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     */
    public MetisHTTPYQLClient(final TethysUIDataFormatter pFormatter) {
        super(YQL_WEBSITE);
        theParser = pFormatter.getDecimalParser();
    }

    /**
     * Obtain price for individual security.
     * @param pSymbol the security symbol
     * @param pCurrency the currency for the price
     * @return the price
     * @throws OceanusException on error
     */
    public TethysPrice obtainSecurityPrice(final String pSymbol,
                                           final Currency pCurrency) throws OceanusException {
        /* Build the query string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_PRICES);
        myBuilder.append(YQLSEL_SYMBOL);
        myBuilder.append(pSymbol);
        myBuilder.append(YQLSEL_END);

        /* Determine price divisor */
        int myDivisor = 1;
        for (int iNumDigits = pCurrency.getDefaultFractionDigits(); iNumDigits > 0; iNumDigits--) {
            myDivisor *= TethysDecimal.RADIX_TEN;
        }

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the single result */
            final JSONArray myArray = myJSON.getJSONArray(YQLRES_QUOTE);
            final JSONObject myEntry = myArray.getJSONObject(0);
            final String myStrPrice = myEntry.optString(YQLFLD_PRICE, null);

            /* If we found the price */
            if (myStrPrice != null) {
                /* Parse the price and convert from minor units */
                final TethysPrice myPrice = theParser.parsePriceValue(myStrPrice, pCurrency);
                myPrice.divide(myDivisor);
                return myPrice;
            }

            /* No price found */
            return null;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new MetisDataException(YQLERROR_PARSE, e);
        }
    }

    /**
     * Obtain price for individual security.
     * @param pSymbols the security symbols
     * @param pCurrency the currency for the price
     * @return the price
     * @throws OceanusException on error
     */
    public Map<String, TethysPrice> obtainSecurityPrices(final List<String> pSymbols,
                                                         final Currency pCurrency) throws OceanusException {
        /* Build the query string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_PRICES);
        myBuilder.append(YQLSEL_SYMBOL);

        /* Determine price divisor */
        int myDivisor = 1;
        for (int iNumDigits = pCurrency.getDefaultFractionDigits(); iNumDigits > 0; iNumDigits--) {
            myDivisor *= TethysDecimal.RADIX_TEN;
        }

        /* Build the symbol set */
        final Iterator<String> myIterator = pSymbols.iterator();
        boolean bFirst = true;
        while (myIterator.hasNext()) {
            final String mySymbol = myIterator.next();
            if (!bFirst) {
                myBuilder.append(YQLSEL_MID);
            }
            myBuilder.append(mySymbol);
            bFirst = false;
        }
        myBuilder.append(YQLSEL_END);

        /* Create the result map */
        final Map<String, TethysPrice> myMap = new HashMap<>();

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the result array */
            final JSONArray myArray = myJSON.getJSONArray(YQLRES_QUOTE);
            final int myNumPrices = myArray.length();
            for (int i = 0; i < myNumPrices; i++) {
                /* Access the details */
                final JSONObject myEntry = myArray.getJSONObject(i);
                final String myStrPrice = myEntry.optString(YQLFLD_PRICE, null);

                /* If we have a price */
                if (myStrPrice != null) {
                    /* Parse and convert to proper units */
                    final TethysPrice myPrice = theParser.parsePriceValue(myStrPrice, pCurrency);
                    myPrice.divide(myDivisor);

                    /* Add the the map */
                    final String mySymbol = myEntry.getString(YQLFLD_SYMBOL);
                    myMap.put(mySymbol, myPrice);
                }
            }

            /* Return the map */
            return myMap;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new MetisDataException(YQLERROR_PARSE, e);
        }
    }

    /**
     * Obtain exchange rate for currency pair.
     * @param pFrom the from currency
     * @param pTo the to currency
     * @return the price
     * @throws OceanusException on error
     */
    public TethysRatio obtainExchangeRate(final Currency pFrom,
                                          final Currency pTo) throws OceanusException {
        /* Build the query string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_RATES);
        myBuilder.append(YQLSEL_PAIR);
        myBuilder.append(pFrom.getCurrencyCode());
        myBuilder.append(pTo.getCurrencyCode());
        myBuilder.append(YQLSEL_END);

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the single result */
            final JSONArray myArray = myJSON.getJSONArray(YQLRES_RATE);
            final JSONObject myEntry = myArray.getJSONObject(0);
            final String myRate = myEntry.optString(YQLFLD_RATE, null);

            /* return parsed rate if possible */
            return (myRate != null)
                                    ? new TethysRatio(myRate)
                                    : null;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new MetisDataException(YQLERROR_PARSE, e);
        }
    }

    /**
     * Obtain exchange rate for currency pairs.
     * @param pFrom the from currency
     * @param pToList the list of to currencies
     * @return the price
     * @throws OceanusException on error
     */
    public Map<Currency, TethysRatio> obtainExchangeRates(final Currency pFrom,
                                                          final List<Currency> pToList) throws OceanusException {
        /* Build the query string */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_RATES);
        myBuilder.append(YQLSEL_PAIR);

        /* Build the pair set */
        final String myFrom = pFrom.getCurrencyCode();
        final Iterator<Currency> myIterator = pToList.iterator();
        boolean bFirst = true;
        while (myIterator.hasNext()) {
            final Currency myCurr = myIterator.next();
            if (!bFirst) {
                myBuilder.append(YQLSEL_MID);
            }
            myBuilder.append(myFrom);
            myBuilder.append(myCurr.getCurrencyCode());
            bFirst = false;
        }
        myBuilder.append(YQLSEL_END);

        /* Create the result map */
        final Map<Currency, TethysRatio> myMap = new HashMap<>();

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the results */
            final JSONArray myArray = myJSON.getJSONArray(YQLRES_RATE);
            final int myNumPrices = myArray.length();
            for (int i = 0; i < myNumPrices; i++) {
                /* Access the details */
                final JSONObject myEntry = myArray.getJSONObject(i);
                final String myStrRate = myEntry.optString(YQLFLD_RATE, null);

                /* If we have a rate */
                if (myStrRate != null) {
                    /* Determine currency and rate */
                    String myId = myEntry.getString(YQLFLD_ID);
                    myId = myId.substring(myFrom.length());
                    final Currency myCurr = Currency.getInstance(myId);
                    final TethysRatio myRate = new TethysRatio(myStrRate);

                    /* Add the the map */
                    myMap.put(myCurr, myRate);
                }
            }

            /* Return the map */
            return myMap;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new MetisDataException(YQLERROR_PARSE, e);
        }
    }
}
