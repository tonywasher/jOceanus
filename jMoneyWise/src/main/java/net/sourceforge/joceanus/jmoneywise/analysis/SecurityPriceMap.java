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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFormat;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdateday.JDateDayRange;
import net.sourceforge.joceanus.jdecimal.JPrice;
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountPrice;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jsortedlist.NestedHashMap;

/**
 * Map of Security prices indexed by Security Id.
 */
public class SecurityPriceMap
        extends NestedHashMap<Integer, List<AccountPrice>>
        implements JDataFormat {

    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -4423424113521596997L;

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Constructor.
     * @param pData the dataSet
     */
    protected SecurityPriceMap(final FinanceData pData) {
        /* Loop through the prices */
        Iterator<AccountPrice> myIterator = pData.getPrices().iterator();
        while (myIterator.hasNext()) {
            AccountPrice myPrice = myIterator.next();

            /* Add to the map */
            addPriceToMap(myPrice);
        }
    }

    /**
     * Add price to map.
     * @param pPrice the price to add.
     */
    private void addPriceToMap(final AccountPrice pPrice) {
        /* Access security prices */
        Account mySecurity = pPrice.getAccount();
        List<AccountPrice> myList = get(mySecurity.getId());

        /* If the list is new */
        if (myList == null) {
            /* Allocate list and add to map */
            myList = new ArrayList<AccountPrice>();
            put(mySecurity.getId(), myList);
        }

        /* Add the price to the list */
        myList.add(pPrice);
    }

    /**
     * Obtain price for date.
     * @param pSecurity the security
     * @param pDate the date
     * @return the latest price for the date.
     */
    public JPrice getPriceForDate(final Account pSecurity,
                                  final JDateDay pDate) {
        /* Initialise price */
        JPrice myPrice = new JPrice();

        /* Access list for security */
        List<AccountPrice> myList = get(pSecurity.getId());
        if (myList != null) {
            /* Loop through the prices */
            Iterator<AccountPrice> myIterator = myList.iterator();
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();

                /* Break if this is later than the date */
                if (pDate.compareTo(myCurr.getDate()) > 0) {
                    break;
                }

                /* Record as best price */
                myPrice = myCurr.getPrice();
            }
        }

        /* return price */
        return myPrice;
    }

    /**
     * Obtain prices for range.
     * @param pSecurity the security
     * @param pRange the date range
     * @return the two deep array of prices for the range.
     */
    public JPrice[] getPricesForRange(final Account pSecurity,
                                      final JDateDayRange pRange) {
        /* Set price */
        JPrice myFirst = new JPrice();
        JPrice myLatest = new JPrice();

        /* Access list for security */
        List<AccountPrice> myList = get(pSecurity.getId());
        if (myList != null) {
            /* Loop through the prices */
            Iterator<AccountPrice> myIterator = myList.iterator();
            while (myIterator.hasNext()) {
                AccountPrice myCurr = myIterator.next();

                /* Check for the range of the date */
                int iComp = pRange.compareTo(myCurr.getDate());

                /* Break if this is later than the date */
                if (iComp > 0) {
                    break;
                }

                /* Record as best price */
                myLatest = myCurr.getPrice();

                /* Record early date if required */
                if (iComp < 0) {
                    myFirst = myLatest;
                }
            }
        }

        /* Return the prices */
        return new JPrice[] { myFirst, myLatest };
    }
}
