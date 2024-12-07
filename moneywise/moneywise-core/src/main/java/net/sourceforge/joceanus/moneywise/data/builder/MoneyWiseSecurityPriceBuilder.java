/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.moneywise.data.builder;

import net.sourceforge.joceanus.moneywise.exc.MoneyWiseDataException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;

/**
 * SecurityPrice Builder.
 */
public class MoneyWiseSecurityPriceBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The Security.
     */
    private MoneyWiseSecurity theSecurity;

    /**
     * The Date.
     */
    private OceanusDate theDate;

    /**
     * The Price.
     */
    private OceanusPrice thePrice;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    public MoneyWiseSecurityPriceBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getSecurityPrices().ensureMap();
    }

    /**
     * Set Security.
     * @param pSecurity the security
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder security(final MoneyWiseSecurity pSecurity) {
        theSecurity = pSecurity;
        return this;
    }

    /**
     * Set Security.
     * @param pSecurity the security
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder security(final String pSecurity) {
        return security(theDataSet.getSecurities().findItemByName(pSecurity));
    }

    /**
     * Set the price.
     * @param pPrice the price.
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder price(final OceanusPrice pPrice) {
        thePrice = pPrice;
        return this;
    }

    /**
     * Set the price.
     * @param pPrice the price
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder price(final String pPrice) {
        return price(new OceanusPrice(pPrice, theSecurity.getAssetCurrency().getCurrency()));
    }

    /**
     * Set the date.
     * @param pDate the date of the price.
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder date(final OceanusDate pDate) {
        theDate = pDate;
        return this;
    }

    /**
     * Set the date.
     * @param pDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder date(final String pDate) {
        return date(new OceanusDate(pDate));
    }

    /**
     * Build the Rate.
     * @return the new Rate
     * @throws OceanusException on error
     */
    public MoneyWiseSecurityPrice build() throws OceanusException {
        /* Create the price */
        final MoneyWiseSecurityPrice myPrice = theDataSet.getSecurityPrices().addNewItem();
        myPrice.setSecurity(theSecurity);
        myPrice.setPrice(thePrice);
        myPrice.setDate(theDate);

        /* Check for errors */
        myPrice.adjustMapForItem();
        myPrice.validate();
        if (myPrice.hasErrors()) {
            myPrice.removeItem();
            throw new MoneyWiseDataException(myPrice, "Failed validation");
        }

        /* Reset values */
        theSecurity = null;
        thePrice = null;
        theDate = null;

        /* Return the price */
        return myPrice;
    }
}
