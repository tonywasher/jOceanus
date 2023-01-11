/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.test.decimal;

import java.util.Currency;

import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;

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
        final TethysDecimalParser myParser = new TethysDecimalParser();
        final TethysDecimalFormatter myFormatter = new TethysDecimalFormatter();

        /* Create a ratio of 21/20 */
        TethysRatio myRatio1 = myParser.parseRatioValue("1.05");
        TethysRate myResult = new TethysRate(myRatio1);
        final TethysRatio myRatio2 = myParser.parseRatioValue("0.90");
        myResult = new TethysRate(myRatio2);
        final TethysRatio myRatio3 = myRatio2.multiplyBy(myRatio1);
        myRatio1 = myParser.parseRatioValue("1.21");
        TethysRate myRate = myRatio1.annualise(500);

        /* Create a USD value */
        final TethysMoney myUSD = TethysMoney.getWholeUnits(-1000, Currency.getInstance("GBP"));
        String myX = myUSD.toString();
        myX = myFormatter.toCurrencyString(myUSD);
        TethysMoney myRes = myParser.parseMoneyValue(myX);
        if (!myRes.equals(myUSD)) {
            myRes = null;
        }

        /* Parse a rate and money */
        myRate = myParser.parseRateValue("15%");
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
