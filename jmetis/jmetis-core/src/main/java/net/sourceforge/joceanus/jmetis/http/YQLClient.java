/*******************************************************************************
 * jMetis: Java Data Framework
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmetis/jmetis-core/src/main/java/net/sourceforge/joceanus/jmetis/field/JFieldModel.java $
 * $Revision: 587 $
 * $Author: Tony $
 * $Date: 2015-03-31 14:44:28 +0100 (Tue, 31 Mar 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.http;

import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.JMetisDataException;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.decimal.JDecimal;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Client to query YQL.
 * @author Tony Washer
 */
public class YQLClient
        extends DataClient {
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
    private final JDecimalParser theParser;

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @throws JOceanusException on error
     */
    public YQLClient(final JDataFormatter pFormatter) throws JOceanusException {
        super(YQL_WEBSITE);
        theParser = pFormatter.getDecimalParser();
    }

    /**
     * Obtain price for individual security.
     * @param pSymbol the security symbol
     * @param pCurrency the currency for the price
     * @return the price
     * @throws JOceanusException on error
     */
    public JPrice obtainSecurityPrice(final String pSymbol,
                                      final Currency pCurrency) throws JOceanusException {
        /* Build the query string */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_PRICES);
        myBuilder.append(YQLSEL_SYMBOL);
        myBuilder.append(pSymbol);
        myBuilder.append(YQLSEL_END);

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the single result */
            JSONArray myArray = myJSON.getJSONArray(YQLRES_QUOTE);
            JSONObject myEntry = myArray.getJSONObject(0);
            String myStrPrice = myEntry.getString(YQLFLD_PRICE);

            /* Parse the price and convert from minor units */
            JPrice myPrice = theParser.parsePriceValue(myStrPrice, pCurrency);
            for (int iNumDigits = pCurrency.getDefaultFractionDigits(); iNumDigits > 0; iNumDigits--) {
                myPrice.divide(JDecimal.RADIX_TEN);
            }
            return myPrice;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new JMetisDataException(YQLERROR_PARSE, e);
        }
    }

    /**
     * Obtain price for individual security.
     * @param pSymbols the security symbols
     * @param pCurrency the currency for the price
     * @return the price
     * @throws JOceanusException on error
     */
    public Map<String, JPrice> obtainSecurityPrices(final List<String> pSymbols,
                                                    final Currency pCurrency) throws JOceanusException {
        /* Build the query string */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_PRICES);
        myBuilder.append(YQLSEL_SYMBOL);

        /* Build the symbol set */
        Iterator<String> myIterator = pSymbols.iterator();
        boolean bFirst = true;
        while (myIterator.hasNext()) {
            String mySymbol = myIterator.next();
            if (!bFirst) {
                myBuilder.append(YQLSEL_MID);
            }
            myBuilder.append(mySymbol);
            bFirst = false;
        }
        myBuilder.append(YQLSEL_END);

        /* Create the result map */
        Map<String, JPrice> myMap = new HashMap<String, JPrice>();

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the result array */
            JSONArray myArray = myJSON.getJSONArray(YQLRES_QUOTE);
            int myNumPrices = myArray.length();
            for (int i = 0; i < myNumPrices; i++) {
                /* Access the details */
                JSONObject myEntry = myArray.getJSONObject(i);
                String mySymbol = myEntry.getString(YQLFLD_SYMBOL);
                String myStrPrice = myEntry.getString(YQLFLD_PRICE);

                /* Parse the price and convert from minor units */
                JPrice myPrice = theParser.parsePriceValue(myStrPrice, pCurrency);
                for (int iNumDigits = pCurrency.getDefaultFractionDigits(); iNumDigits > 0; iNumDigits--) {
                    myPrice.divide(JDecimal.RADIX_TEN);
                }

                /* Add the the map */
                myMap.put(mySymbol, myPrice);
            }

            /* Return the map */
            return myMap;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new JMetisDataException(YQLERROR_PARSE, e);
        }
    }

    /**
     * Obtain exchange rate for currency pair.
     * @param pFrom the from currency
     * @param pTo the to currency
     * @return the price
     * @throws JOceanusException on error
     */
    public JRatio obtainExchangeRate(final Currency pFrom,
                                     final Currency pTo) throws JOceanusException {
        /* Build the query string */
        StringBuilder myBuilder = new StringBuilder();
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
            JSONArray myArray = myJSON.getJSONArray(YQLRES_RATE);
            JSONObject myEntry = myArray.getJSONObject(0);
            String myRate = myEntry.getString(YQLFLD_RATE);

            /* Parse the rate */
            return new JRatio(myRate);

        } catch (JSONException e) {
            /* Notify of failure */
            throw new JMetisDataException(YQLERROR_PARSE, e);
        }
    }

    /**
     * Obtain exchange rate for currency pairs.
     * @param pFrom the from currency
     * @param pToList the list of to currencies
     * @return the price
     * @throws JOceanusException on error
     */
    public Map<Currency, JRatio> obtainExchangeRates(final Currency pFrom,
                                                     final List<Currency> pToList) throws JOceanusException {
        /* Build the query string */
        StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(YQL_SELECT);
        myBuilder.append(YQLDB_RATES);
        myBuilder.append(YQLSEL_PAIR);

        /* Build the pair set */
        String myFrom = pFrom.getCurrencyCode();
        Iterator<Currency> myIterator = pToList.iterator();
        boolean bFirst = true;
        while (myIterator.hasNext()) {
            Currency myCurr = myIterator.next();
            if (!bFirst) {
                myBuilder.append(YQLSEL_MID);
            }
            myBuilder.append(myFrom);
            myBuilder.append(myCurr.getCurrencyCode());
            bFirst = false;
        }
        myBuilder.append(YQLSEL_END);

        /* Create the result map */
        Map<Currency, JRatio> myMap = new HashMap<Currency, JRatio>();

        /* Perform the query */
        JSONObject myJSON = queryJSONObjectWithTrailer(myBuilder.toString(), YQL_TAIL);

        /* Protect against exceptions */
        try {
            /* Shift down to the results */
            myJSON = myJSON.getJSONObject(YQLRES_QUERY);
            myJSON = myJSON.getJSONObject(YQLRES_RESULTS);

            /* Access the results */
            JSONArray myArray = myJSON.getJSONArray(YQLRES_RATE);
            int myNumPrices = myArray.length();
            for (int i = 0; i < myNumPrices; i++) {
                /* Access the details */
                JSONObject myEntry = myArray.getJSONObject(i);
                String myId = myEntry.getString(YQLFLD_ID);
                String myStrRate = myEntry.getString(YQLFLD_RATE);

                /* Determine currency and rate */
                myId = myId.substring(myFrom.length());
                Currency myCurr = Currency.getInstance(myId);
                JRatio myRate = new JRatio(myStrRate);

                /* Add the the map */
                myMap.put(myCurr, myRate);
            }

            /* Return the map */
            return myMap;

        } catch (JSONException e) {
            /* Notify of failure */
            throw new JMetisDataException(YQLERROR_PARSE, e);
        }
    }
}