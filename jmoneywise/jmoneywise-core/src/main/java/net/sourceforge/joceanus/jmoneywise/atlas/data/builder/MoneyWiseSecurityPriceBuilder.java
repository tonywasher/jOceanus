/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.builder;

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * SecurityPrice Builder.
 */
public class MoneyWiseSecurityPriceBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The Security.
     */
    private Security theSecurity;

    /**
     * The Date.
     */
    private TethysDate theDate;

    /**
     * The Price.
     */
    private TethysPrice thePrice;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseSecurityPriceBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Set Security.
     * @param pSecurity the security
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder security(final Security pSecurity) {
        theSecurity = pSecurity;
        return this;
    }

    /**
     * Set the price.
     * @param pPrice the price.
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder rate(final TethysPrice pPrice) {
        thePrice = pPrice;
        return this;
    }

    /**
     * Set the date.
     * @param pDate the date of the price.
     * @return the builder
     */
    public MoneyWiseSecurityPriceBuilder date(final TethysDate pDate) {
        theDate = pDate;
        return this;
    }

    /**
     * Build the Rate.
     * @return the new Rate
     * @throws OceanusException on error
     */
    public SecurityPrice build() throws OceanusException {
        /* Create the price */
        final SecurityPrice myPrice = theDataSet.getSecurityPrices().addNewItem();
        myPrice.setSecurity(theSecurity);
        myPrice.setPrice(thePrice);
        myPrice.setDate(theDate);
        myPrice.validate();

        /* Reset values */
        theSecurity = null;
        thePrice = null;
        theDate = null;

        /* Return the price */
        return myPrice;
    }
}
