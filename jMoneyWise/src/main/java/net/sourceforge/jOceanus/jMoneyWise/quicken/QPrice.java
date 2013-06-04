/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDecimal.JDecimal;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountPrice;

/**
 * Quicken Price.
 */
public class QPrice {
    /**
     * Item type.
     */
    private static final String QIF_ITEM = "Prices";

    /**
     * Quicken Comma.
     */
    private static final char QIF_COMMA = ',';

    /**
     * The Price.
     */
    private final AccountPrice thePrice;

    /**
     * Constructor.
     * @param pPrice the account price
     */
    protected QPrice(final AccountPrice pPrice) {
        /* Store the price */
        thePrice = pPrice;
    }

    /**
     * build QIF format.
     * @param pFormatter the formatter
     * @return the QIF format
     */
    protected String buildQIF(final JDataFormatter pFormatter) {
        StringBuilder myBuilder = new StringBuilder();
        Account myAccount = thePrice.getAccount();

        /* Add the Item type */
        myBuilder.append(QDataSet.QIF_ITEMTYPE);
        myBuilder.append(QIF_ITEM);
        myBuilder.append(QDataSet.QIF_EOL);

        /* Add the Ticker Symbol */
        myBuilder.append(QDataSet.QIF_QUOTE);
        myBuilder.append(myAccount.getSymbol());
        myBuilder.append(QDataSet.QIF_QUOTE);
        myBuilder.append(QIF_COMMA);

        /* Add the Price (as a simple decimal) */
        JDecimal myValue = new JDecimal(thePrice.getPrice());
        myBuilder.append(pFormatter.formatObject(myValue));
        myBuilder.append(QIF_COMMA);

        /* Add the Date */
        myBuilder.append(QDataSet.QIF_QUOTE);
        myBuilder.append(pFormatter.formatObject(thePrice.getDate()));
        myBuilder.append(QDataSet.QIF_QUOTE);
        myBuilder.append(QDataSet.QIF_EOL);

        /* Add the End indicator */
        myBuilder.append(QDataSet.QIF_EOI);
        myBuilder.append(QDataSet.QIF_EOL);

        /* Return the builder */
        return myBuilder.toString();
    }
}
