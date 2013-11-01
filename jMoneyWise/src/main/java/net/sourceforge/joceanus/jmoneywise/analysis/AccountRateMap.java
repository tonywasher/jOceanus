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
import net.sourceforge.joceanus.jmoneywise.data.Account;
import net.sourceforge.joceanus.jmoneywise.data.AccountRate;
import net.sourceforge.joceanus.jmoneywise.data.FinanceData;
import net.sourceforge.joceanus.jsortedlist.NestedHashMap;

/**
 * Map of Account Rates indexed by Security Id.
 */
public class AccountRateMap
        extends NestedHashMap<Integer, List<AccountRate>>
        implements JDataFormat {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7840526844888764877L;

    @Override
    public String formatObject() {
        return getClass().getSimpleName();
    }

    /**
     * Constructor.
     * @param pData the dataSet
     */
    protected AccountRateMap(final FinanceData pData) {
        /* Loop through the rates */
        Iterator<AccountRate> myIterator = pData.getRates().iterator();
        while (myIterator.hasNext()) {
            AccountRate myPrice = myIterator.next();

            /* Add to the map */
            addRateToMap(myPrice);
        }
    }

    /**
     * Add rate to map.
     * @param pRate the rate to add.
     */
    private void addRateToMap(final AccountRate pRate) {
        /* Access security prices */
        Account myAccount = pRate.getAccount();
        List<AccountRate> myList = get(myAccount.getId());

        /* If the list is new */
        if (myList == null) {
            /* Allocate list and add to map */
            myList = new ArrayList<AccountRate>();
            put(myAccount.getId(), myList);
        }

        /* Add the rate to the list */
        myList.add(pRate);
    }

    /**
     * Obtain rate for date.
     * @param pAccount the account
     * @param pDate the date
     * @return the latest rate for the date.
     */
    public AccountRate getRateForDate(final Account pAccount,
                                      final JDateDay pDate) {
        /* Access list for security */
        List<AccountRate> myList = get(pAccount.getId());
        if (myList != null) {
            /* Loop through the rates */
            Iterator<AccountRate> myIterator = myList.iterator();
            while (myIterator.hasNext()) {
                AccountRate myCurr = myIterator.next();

                /* Access the date */
                JDateDay myDate = myCurr.getDate();

                /* break loop if we have the correct record */
                if ((myDate == null)
                    || (myDate.compareTo(pDate) >= 0)) {
                    return myCurr;
                }
            }
        }

        /* return null */
        return null;
    }
}
