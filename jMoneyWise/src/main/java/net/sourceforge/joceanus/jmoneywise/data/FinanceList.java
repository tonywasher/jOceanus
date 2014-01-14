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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.ResourceBundle;

/**
 * FinanceList Types.
 */
public enum FinanceList {
    /**
     * AccountTypes.
     */
    ACCOUNTTYPES,

    /**
     * EventTypes.
     */
    EVENTTYPES,

    /**
     * TaxBases.
     */
    TAXBASES,

    /**
     * TaxTypes.
     */
    TAXTYPES,

    /**
     * Currencies.
     */
    CURRENCIES,

    /**
     * TaxRegimes.
     */
    TAXREGIMES,

    /**
     * Frequencies.
     */
    FREQUENCIES,

    /**
     * TaxInfoTypes.
     */
    TAXINFOTYPES,

    /**
     * AccountInfoTypes.
     */
    ACCOUNTINFOTYPES,

    /**
     * EventInfoTypes.
     */
    EVENTINFOTYPES,

    /**
     * TaxYears.
     */
    TAXYEARS,

    /**
     * TaxYearsInfo.
     */
    TAXYEARINFO,

    /**
     * EventClasses.
     */
    EVENTCLASSES,

    /**
     * AccountCategories.
     */
    ACCOUNTCATEGORIES,

    /**
     * EventCategories.
     */
    EVENTCATEGORIES,

    /**
     * ExchangeRates.
     */
    EXCHANGERATES,

    /**
     * Accounts.
     */
    ACCOUNTS,

    /**
     * AccountInfo.
     */
    ACCOUNTINFO,

    /**
     * AccountRates.
     */
    ACCOUNTRATES,

    /**
     * AccountPrices.
     */
    ACCOUNTPRICES,

    /**
     * Events.
     */
    EVENTS,

    /**
     * EventInfo.
     */
    EVENTINFO,

    /**
     * EventClassLinks.
     */
    EVENTCLASSLINKS,

    /**
     * Patterns.
     */
    PATTERNS;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(FinanceList.class.getName());

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }
}
