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
public final class TestDecimal {
    /**
     * Constructor.
     */
    private TestDecimal() {
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
    private static JMoney testDecimal() {
        /* Create the required presentation formatter and parser */
        JDecimalParser myParser = new JDecimalParser();
        JDecimalFormatter myFormatter = new JDecimalFormatter();

        /* Create a USD value */
        JMoney myUSD = new JMoney(Currency.getInstance("GBP"));
        myUSD.setValue(-1000, myUSD.scale());
        String myX = myUSD.toString();
        myX = myFormatter.toCurrencyString(myUSD);
        JMoney myRes = myParser.parseMoneyValue(myX);
        if (!myRes.equals(myUSD)) {
            myRes = null;
        }

        /* Parse a rate and money */
        JRate myRate = myParser.parseRateValue("15%");
        JMoney myMoney = myParser.parseMoneyValue("5000.00");

        /* Adjust accounting width */
        myFormatter.setAccountingWidth(11);

        /* Format the rate */
        JMoney myInterest = myMoney.valueAtRate(myRate);
        myMoney.addAmount(myInterest);
        String myFormat = myFormatter.formatMoney(myMoney);
        myMoney.setZero();
        myFormat = myFormatter.formatMoney(myMoney);

        myRate = JRate.getWholePercentage(5);
        myMoney = JMoney.getWholeUnits(10);
        JPrice myPrice = myParser.parsePriceValue("5.280843");
        JDilutedPrice myDPrice = myParser.parseDilutedPriceValue("4.3969");
        JDilution myDilution = new JDilution("0.5");
        myPrice = myDPrice.getPrice(myDilution);

        /* Set to null */
        myFormat.trim();
        myPrice.setZero();

        /* Use otherwise dead variables */
        return myMoney.valueAtRate(myRate);
    }
}
