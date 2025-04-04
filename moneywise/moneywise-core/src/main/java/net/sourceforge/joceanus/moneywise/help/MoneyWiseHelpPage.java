/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.help;

import java.io.InputStream;

import net.sourceforge.joceanus.metis.help.MetisHelpModule.MetisHelpId;

/**
 * Help Page definitions.
 */
public enum MoneyWiseHelpPage implements MetisHelpId {
    /**
     * Deposits.
     */
    HELP_DEPOSITS("Deposits.html"),

    /**
     * Loans.
     */
    HELP_LOANS("Loans.html"),

    /**
     * AccountTypes.
     */
    HELP_ACCOUNTTYPES("Deposits.html"),

    /**
     * TransactionTypes.
     */
    HELP_TRANTYPES("TransactionTypes.html");

    /**
     * The Source.
     */
    private String theSource;

    /**
     * Constructor.
     * @param pSource the source
     */
    MoneyWiseHelpPage(final String pSource) {
        theSource = pSource;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return MoneyWiseHelpPage.class.getResourceAsStream(theSource);
    }
}
