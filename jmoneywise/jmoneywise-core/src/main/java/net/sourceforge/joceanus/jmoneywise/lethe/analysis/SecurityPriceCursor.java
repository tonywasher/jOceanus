/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Currency;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityPrice.SecurityPriceDataMap;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInstanceMap;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;

/**
 * Quick access to dated security Prices on an analysis pass.
 */
public class SecurityPriceCursor {
    /**
     * SecurityPrice data map.
     */
    private final SecurityPriceDataMap<SecurityPrice> theDataMap;

    /**
     * Cursor map.
     */
    private final Map<Security, SecurityCursor> theCursorMap;

    /**
     * Constructor.
     * @param pData the dataSet
     */
    protected SecurityPriceCursor(final MoneyWiseData pData) {
        /* Obtain the data map */
        theDataMap = pData.getSecurityPriceDataMap();

        /* Create the cursor map */
        theCursorMap = new HashMap<>();
    }

    /**
     * Obtain price for security and date.
     * @param pSecurity the security
     * @param pDate the date
     * @return the security price
     */
    public TethysPrice getSecurityPrice(final Security pSecurity,
                                        final TethysDate pDate) {
        /* Access cursor and return rate */
        final SecurityCursor myCursor = getCursor(pSecurity);
        return myCursor.getSecurityPrice(pDate);
    }

    /**
     * Obtain security cursor.
     * @param pSecurity the security
     * @return the cursor
     */
    private SecurityCursor getCursor(final Security pSecurity) {
        /* Look for existing cursor */
        SecurityCursor myCursor = theCursorMap.get(pSecurity);

        /* If we do not have a cursor */
        if (myCursor == null) {
            /* Allocate new cursor */
            myCursor = new SecurityCursor(pSecurity);
            theCursorMap.put(pSecurity, myCursor);
        }

        /* return the cursor */
        return myCursor;
    }

    /**
     * Security Cursor class.
     */
    private final class SecurityCursor {
        /**
         * SecurityPrice iterator.
         */
        private final ListIterator<SecurityPrice> theIterator;

        /**
         * The current price.
         */
        private TethysPrice theCurrent;

        /**
         * The next date.
         */
        private TethysDate theNextDate;

        /**
         * The next price.
         */
        private TethysPrice theNextPrice;

        /**
         * Constructor.
         * @param pSecurity the security
         */
        private SecurityCursor(final Security pSecurity) {
            /* Create the default price */
            final Currency myCurrency = pSecurity.getCurrency();
            theCurrent = TethysPrice.getWholeUnits(DataInstanceMap.ONE, myCurrency);

            /* Access the Price list */
            theIterator = theDataMap.priceIterator(pSecurity);

            /* If we have an iterator */
            if (theIterator != null) {
                /* move the cursor */
                moveCursor();
            }
        }

        /**
         * Move the cursor.
         */
        private void moveCursor() {
            /* If we have an iterator */
            if (theIterator.hasPrevious()) {
                /* access next rate and record */
                final SecurityPrice myPrice = theIterator.previous();
                theNextPrice = myPrice.getPrice();
                theNextDate = myPrice.getDate();
            } else {
                theNextDate = null;
                theNextPrice = null;
            }
        }

        /**
         * Obtain security price for date.
         * @param pDate the date
         * @return the security price
         */
        private TethysPrice getSecurityPrice(final TethysDate pDate) {
            /* if we have a later price */
            if (theNextDate != null) {
                /* while we need to move the cursor */
                while (pDate.compareTo(theNextDate) >= 0) {
                    /* store price */
                    theCurrent = theNextPrice;

                    /* move the cursor */
                    moveCursor();
                }
            }

            /* Return the price */
            return theCurrent;
        }
    }
}
