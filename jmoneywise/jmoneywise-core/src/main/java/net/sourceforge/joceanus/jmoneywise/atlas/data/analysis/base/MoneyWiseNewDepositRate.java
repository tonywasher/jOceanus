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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDepositRate.MoneyWiseDepositRateList;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * New format of DepositRate.
 */
public class MoneyWiseNewDepositRate {
    /**
     * The date.
     */
    private final TethysDate theDate;

    /**
     * The Deposit.
     */
    private final MoneyWiseDeposit theDeposit;

    /**
     * The RAte.
     */
    private final TethysRate theRate;

    /**
     * Constructor.
     * @param pDate the date
     * @param pDeposit the deposit
     * @param pRate the rate
     */
    MoneyWiseNewDepositRate(final TethysDate pDate,
                            final MoneyWiseDeposit pDeposit,
                            final TethysRate pRate) {
        theDate = pDate;
        theDeposit = pDeposit;
        theRate = pRate;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain the deposit.
     * @return the deposit
     */
    public MoneyWiseDeposit getDeposit() {
        return theDeposit;
    }

    /**
     * Obtain the rate.
     * @return the rate
     */
    public TethysRate getRate() {
        return theRate;
    }

    /**
     * NewRateList.
     */
    public static class MoneyWiseNewDepositRateList
            extends ArrayList<MoneyWiseNewDepositRate> {
        /**
         * Constructor.
         * @param pEditSet the editSet
         */
        public MoneyWiseNewDepositRateList(final PrometheusEditSet pEditSet) {
            /* Create the map */
            final Map<MoneyWiseDeposit, TethysDate> myPending = new HashMap<>();

            /* Determine the starting date */
            final TethysDate myStart = ((MoneyWiseDataSet) pEditSet.getDataSet()).getDateRange().getStart();

            /* Loop through the depositRates */
            final MoneyWiseDepositRateList mySource = pEditSet.getDataList(MoneyWiseBasicDataType.DEPOSITRATE, MoneyWiseDepositRateList.class);
            final ListIterator<MoneyWiseDepositRate> myIterator = mySource.listIterator(mySource.size());
            while (myIterator.hasPrevious()) {
                /* Access pending date */
                final MoneyWiseDepositRate myRate = myIterator.previous();
                final MoneyWiseDeposit myDeposit = myRate.getDeposit();
                TethysDate myDate = myPending.get(myDeposit);
                if (myDate == null) {
                    myDate = myStart;
                }

                /* Add new element */
                add(new MoneyWiseNewDepositRate(myDate, myDeposit, myRate.getRate()));

                /* Store the pending date */
                myPending.put(myDeposit, myRate.getEndDate());
            }
        }
    }
}
