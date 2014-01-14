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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.quicken;

import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDecimal;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;

/**
 * Quicken Price.
 */
public class QPrice
        extends QElement {
    /**
     * Item type.
     */
    private static final String QIF_ITEM = "Prices";

    /**
     * Quicken Quote.
     */
    protected static final char QIF_QUOTE = '"';

    /**
     * Quicken Comma.
     */
    private static final char QIF_COMMA = ',';

    /**
     * The Price.
     */
    private final AccountPrice thePrice;

    /**
     * Obtain Security Name.
     * @return the security name
     */
    public String getSecurity() {
        return thePrice.getAccountName();
    }

    /**
     * Obtain Price Date.
     * @return the date
     */
    public JDateDay getDate() {
        return thePrice.getDate();
    }

    /**
     * Obtain Price.
     * @return the price
     */
    public JPrice getSpotPrice() {
        return thePrice.getPrice();
    }

    /**
     * Obtain Price.
     * @return the price
     */
    public AccountPrice getPrice() {
        return thePrice;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pPrice the account price
     */
    protected QPrice(final QAnalysis pAnalysis,
                     final AccountPrice pPrice) {
        /* Call super constructor */
        super(pAnalysis.getFormatter(), pAnalysis.getQIFType());

        /* Store the price */
        thePrice = pPrice;
    }

    /**
     * build QIF format.
     * @return the QIF format
     */
    protected String buildQIF() {
        /* Access the account */
        Account myAccount = thePrice.getAccount();

        /* Reset the builder */
        reset();

        /* Add the Item type */
        append(QIF_ITEMTYPE);
        append(QIF_ITEM);
        endLine();

        /* Add the Ticker Symbol */
        append(QIF_QUOTE);
        append(myAccount.getSymbol());
        append(QIF_QUOTE);
        append(QIF_COMMA);

        /* Add the Price (as a simple decimal) */
        JDecimal myValue = new JDecimal(thePrice.getPrice());
        append(QIF_QUOTE);
        addSimpleDecimal(myValue);
        append(QIF_QUOTE);
        append(QIF_COMMA);

        /* Add the Date */
        append(QIF_QUOTE);
        addDate(thePrice.getDate());
        append(QIF_QUOTE);
        endLine();

        /* Return the result */
        return completeItem();
    }

    @Override
    public String toString() {
        return buildQIF();
    }
}
