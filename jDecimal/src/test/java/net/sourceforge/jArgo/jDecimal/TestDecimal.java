/*******************************************************************************
 * JDecimal: Decimals represented by long values
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jArgo.jDecimal;


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
        /* Create the required presentation formatter and parser */
        JDecimalParser myParser = new JDecimalParser();
        JDecimalFormatter myFormatter = new JDecimalFormatter();

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
    }
}
