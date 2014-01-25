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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.ResourceBundle;

/**
 * MoneyWise Item Types.
 */
public enum MoneyWiseDataType {
    /**
     * AccountType.
     */
    ACCOUNTTYPE,

    /**
     * SecurityType.
     */
    SECURITYTYPE,

    /**
     * PayeeType.
     */
    PAYEETYPE,

    /**
     * EventType.
     */
    EVENTTYPE,

    /**
     * TaxBasis.
     */
    TAXBASIS,

    /**
     * TaxType.
     */
    TAXTYPE,

    /**
     * Currency.
     */
    CURRENCY,

    /**
     * TaxRegime.
     */
    TAXREGIME,

    /**
     * Frequency.
     */
    FREQUENCY,

    /**
     * TaxInfoType.
     */
    TAXINFOTYPE,

    /**
     * AccountInfoType.
     */
    ACCOUNTINFOTYPE,

    /**
     * EventInfoType.
     */
    EVENTINFOTYPE,

    /**
     * TaxYear.
     */
    TAXYEAR,

    /**
     * TaxYearInfo.
     */
    TAXYEARINFO,

    /**
     * EventClass.
     */
    EVENTCLASS,

    /**
     * AccountCategory.
     */
    ACCOUNTCATEGORY,

    /**
     * EventCategory.
     */
    EVENTCATEGORY,

    /**
     * ExchangeRate.
     */
    EXCHANGERATE,

    /**
     * Payee.
     */
    PAYEE,

    /**
     * Securities.
     */
    SECURITY,

    /**
     * Account.
     */
    ACCOUNT,

    /**
     * Portfolio.
     */
    PORTFOLIO,

    /**
     * AccountInfo.
     */
    ACCOUNTINFO,

    /**
     * AccountRate.
     */
    ACCOUNTRATE,

    /**
     * SecurityPrice.
     */
    SECURITYPRICE,

    /**
     * Event.
     */
    EVENT,

    /**
     * EventInfo.
     */
    EVENTINFO,

    /**
     * EventClassLink.
     */
    EVENTCLASSLINK,

    /**
     * Pattern.
     */
    PATTERN;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MoneyWiseDataType.class.getName());

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
