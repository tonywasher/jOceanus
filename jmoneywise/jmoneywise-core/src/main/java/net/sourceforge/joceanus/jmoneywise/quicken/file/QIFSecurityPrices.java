/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.list.MetisOrderedList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityPrice;

/**
 * Security Price List.
 * @author Tony Washer
 */
public class QIFSecurityPrices
        implements Comparable<QIFSecurityPrices> {
    /**
     * The QIF File.
     */
    private final QIFFile theFile;

    /**
     * The Security.
     */
    private final QIFSecurity theSecurity;

    /**
     * The Price List.
     */
    private final MetisOrderedList<QIFPrice> thePrices;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the security.
     */
    protected QIFSecurityPrices(final QIFFile pFile,
                                final Security pSecurity) {
        /* Store parameters */
        theFile = pFile;
        theSecurity = new QIFSecurity(pFile, pSecurity);

        /* Create the list */
        thePrices = new MetisOrderedList<>(QIFPrice.class);
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pSecurity the security.
     */
    protected QIFSecurityPrices(final QIFFile pFile,
                                final QIFSecurity pSecurity) {
        /* Store parameters */
        theFile = pFile;
        theSecurity = pSecurity;

        /* Create the list */
        thePrices = new MetisOrderedList<>(QIFPrice.class);
    }

    /**
     * Obtain the security.
     * @return the security
     */
    public QIFSecurity getSecurity() {
        return theSecurity;
    }

    /**
     * Obtain the prices.
     * @return the prices
     */
    public List<QIFPrice> getPrices() {
        return thePrices;
    }

    /**
     * Add price.
     * @param pPrice the price to add
     */
    protected void addPrice(final SecurityPrice pPrice) {
        /* Allocate price */
        QIFPrice myPrice = new QIFPrice(theFile, theSecurity, pPrice);

        /* Add to the list */
        thePrices.append(myPrice);
    }

    /**
     * Add price.
     * @param pPrice the price to add
     */
    protected void addPrice(final QIFPrice pPrice) {
        /* Add to the list */
        thePrices.append(pPrice);
    }

    /**
     * Sort the prices.
     */
    protected void sortPrices() {
        thePrices.reSort();
    }

    /**
     * Format prices.
     * @param pFormatter the formatter
     * @param pBuilder the string builder
     */
    protected void formatPrices(final MetisDataFormatter pFormatter,
                                final StringBuilder pBuilder) {
        /* Loop through the prices */
        Iterator<QIFPrice> myIterator = thePrices.iterator();
        while (myIterator.hasNext()) {
            QIFPrice myPrice = myIterator.next();

            /* Format Item Type header */
            QIFRecord.formatItemType(QIFPrice.QIF_ITEM, pBuilder);

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
        QIFSecurityPrices myPrices = (QIFSecurityPrices) pThat;

        /* Check security */
        if (!theSecurity.equals(myPrices.getSecurity())) {
            return false;
        }

        /* Check prices */
        return thePrices.equals(myPrices.getPrices());
    }

    @Override
    public int hashCode() {
        int myResult = QIFFile.HASH_BASE * theSecurity.hashCode();
        return myResult + thePrices.hashCode();
    }

    @Override
    public int compareTo(final QIFSecurityPrices pThat) {
        return theSecurity.compareTo(pThat.getSecurity());
    }
}
