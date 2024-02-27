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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Security Price List.
 * @author Tony Washer
 */
public class MoneyWiseQIFSecurityPrices
        implements Comparable<MoneyWiseQIFSecurityPrices> {
    /**
     * The QIF File.
     */
    private final MoneyWiseQIFFile theFile;

    /**
     * The Security.
     */
    private final MoneyWiseQIFSecurity theSecurity;

    /**
     * The Price List.
     */
    private final List<MoneyWiseQIFPrice> thePrices;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the security.
     */
    protected MoneyWiseQIFSecurityPrices(final MoneyWiseQIFFile pFile,
                                         final MoneyWiseSecurity pSecurity) {
        this(pFile, new MoneyWiseQIFSecurity(pFile, pSecurity));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the security.
     */
    protected MoneyWiseQIFSecurityPrices(final MoneyWiseQIFFile pFile,
                                         final MoneyWiseQIFSecurity pSecurity) {
        /* Store parameters */
        theFile = pFile;
        theSecurity = pSecurity;

        /* Create the list */
        thePrices = new ArrayList<>();
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public MoneyWiseQIFSecurity getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the prices.
     * @return the prices
     */
    public List<MoneyWiseQIFPrice> getPrices() {
        return thePrices;
    }

    /**
     * Add price.
     * @param pPrice the price to add
     */
    protected void addPrice(final MoneyWiseSecurityPrice pPrice) {
        /* Allocate price */
        final MoneyWiseQIFPrice myPrice = new MoneyWiseQIFPrice(theFile, theSecurity, pPrice);

        /* Add to the list */
        thePrices.add(myPrice);
    }

    /**
     * Add price.
     * @param pPrice the price to add
     */
    protected void addPrice(final MoneyWiseQIFPrice pPrice) {
        /* Add to the list */
        thePrices.add(pPrice);
    }

    /**
     * Sort the prices.
     */
    protected void sortPrices() {
        Collections.sort(thePrices);
    }

    /**
     * Format prices.
     * @param pFormatter the formatter
     * @param pBuilder the string builder
     */
    protected void formatPrices(final TethysUIDataFormatter pFormatter,
                                final StringBuilder pBuilder) {
        /* Loop through the prices */
        for (MoneyWiseQIFPrice myPrice : thePrices) {
            /* Format Item Type header */
            MoneyWiseQIFRecord.formatItemType(MoneyWiseQIFPrice.QIF_ITEM, pBuilder);

            /* Format the record */
            myPrice.formatRecord(pFormatter, pBuilder);
        }
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check class */
        if (!getClass().equals(pThat.getClass())) {
            return false;
        }

        /* Cast correctly */
        final MoneyWiseQIFSecurityPrices myPrices = (MoneyWiseQIFSecurityPrices) pThat;

        /* Check security */
        if (!theSecurity.equals(myPrices.getSecurity())) {
            return false;
        }

        /* Check prices */
        return thePrices.equals(myPrices.getPrices());
    }

    @Override
    public int hashCode() {
        final int myResult = MoneyWiseQIFFile.HASH_BASE * theSecurity.hashCode();
        return myResult + thePrices.hashCode();
    }

    @Override
    public int compareTo(final MoneyWiseQIFSecurityPrices pThat) {
        return theSecurity.compareTo(pThat.getSecurity());
    }
}
