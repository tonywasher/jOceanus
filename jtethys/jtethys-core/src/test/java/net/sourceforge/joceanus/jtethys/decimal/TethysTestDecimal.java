/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2013 Tony Washer
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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.decimal;

import java.util.Currency;

/**
 * JDecimal test class.
 */
public final class TethysTestDecimal {
    /**
     * Constructor.
     */
    private TethysTestDecimal() {
    }

    /**
     * Main entry point.
     * @param pArgs the arguments
     */
    public static void main(final String[] pArgs) {
        /* run the test */
        testDecimal();
    }

    /**
     * Main test.
     * @return a monetary value
     */
    private static TethysMoney testDecimal() {
        /* Create the required presentation formatter and parser */
        TethysDecimalParser myParser = new TethysDecimalParser();
        TethysDecimalFormatter myFormatter = new TethysDecimalFormatter();

        /* Create a USD value */
        TethysMoney myUSD = new TethysMoney(Currency.getInstance("GBP"));
        myUSD.setValue(-1000, myUSD.scale());
        String myX = myUSD.toString();
        myX = myFormatter.toCurrencyString(myUSD);
        TethysMoney myRes = myParser.parseMoneyValue(myX);
        if (!myRes.equals(myUSD)) {
            myRes = null;
        }

        /* Parse a rate and money */
        TethysRate myRate = myParser.parseRateValue("15%");
        TethysMoney myMoney = myParser.parseMoneyValue("5000.00");

        /* Adjust accounting width */
        myFormatter.setAccountingWidth(11);

        /* Format the rate */
        TethysMoney myInterest = myMoney.valueAtRate(myRate);
        myMoney.addAmount(myInterest);
        String myFormat = myFormatter.formatMoney(myMoney);
        myMoney.setZero();
        myFormat = myFormatter.formatMoney(myMoney);

        myRate = TethysRate.getWholePercentage(5);
        myMoney = TethysMoney.getWholeUnits(10);
        TethysPrice myPrice = myParser.parsePriceValue("5.280843");
        TethysDilutedPrice myDPrice = myParser.parseDilutedPriceValue("4.3969");
        TethysDilution myDilution = new TethysDilution("0.5");
        myPrice = myDPrice.getPrice(myDilution);

        /* Set to null */
        myFormat = myFormat.trim();
        myPrice.setZero();

        /* Try to parse a few odd values */
        TethysDecimal myDec = myParser.parseDecimalValue("0.0", 8);
        myDec = myParser.parseDecimalValue("0.0587891", 8);
        myDec = myParser.parseDecimalValue("5.105e-04", 8);
        myDec = myParser.parseDecimalValue("5.105e04", 8);
        myDec = myParser.parseDecimalValue("5e01", 8);
        myDec = myParser.parseDecimalValue("5e-02", 8);

        /* Use otherwise dead variables */
        myDec.doubleValue();
        return myMoney.valueAtRate(myRate);
    }
}
