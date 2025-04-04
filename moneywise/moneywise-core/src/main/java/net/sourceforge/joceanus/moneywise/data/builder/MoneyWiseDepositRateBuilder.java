/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositRate;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;

/**
 * DepositRate Builder.
 */
public class MoneyWiseDepositRateBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The Deposit.
     */
    private MoneyWiseDeposit theDeposit;

    /**
     * The Date.
     */
    private OceanusDate theEndDate;

    /**
     * The Rate.
     */
    private OceanusRate theRate;

    /**
     * The Bonus.
     */
    private OceanusRate theBonus;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseDepositRateBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theDataSet.getDepositRates().ensureMap();
    }

    /**
     * Set Deposit.
     * @param pDeposit the deposit.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder deposit(final MoneyWiseDeposit pDeposit) {
        theDeposit = pDeposit;
        return this;
    }

    /**
     * Set Deposit.
     * @param pDeposit the deposit.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder deposit(final String pDeposit) {
        return deposit(theDataSet.getDeposits().findItemByName(pDeposit));
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder rate(final OceanusRate pRate) {
        theRate = pRate;
        return this;
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder rate(final String pRate) {
        return rate(new OceanusRate(pRate));
    }

    /**
     * Set the bonus.
     * @param pBonus the bonus.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder bonus(final OceanusRate pBonus) {
        theBonus = pBonus;
        return this;
    }

    /**
     * Set the bonus.
     * @param pBonus the bonus.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder bonus(final String pBonus) {
        return bonus(new OceanusRate(pBonus));
    }

    /**
     * Set the endDate.
     * @param pEndDate the endDate of the rate.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder endDate(final OceanusDate pEndDate) {
        theEndDate = pEndDate;
        return this;
    }

    /**
     * Set the endDate.
     * @param pEndDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder endDate(final String pEndDate) {
        return endDate(new OceanusDate(pEndDate));
    }

    /**
     * Build the Rate.
     * @return the new Rate
     * @throws OceanusException on error
     */
    public MoneyWiseDepositRate build() throws OceanusException {
        /* Create the rate */
        final MoneyWiseDepositRate myRate = theDataSet.getDepositRates().addNewItem();
        myRate.setDeposit(theDeposit);
        myRate.setRate(theRate);
        myRate.setBonus(theBonus);
        myRate.setEndDate(theEndDate);

        /* Reset the values */
        reset();

        /* Check for errors */
        myRate.adjustMapForItem();
        myRate.validate();
        if (myRate.hasErrors()) {
            myRate.removeItem();
            throw new MoneyWiseDataException(myRate, "Failed validation");
        }

        /* Return the rate */
        return myRate;
    }

    /**
     * Reset the builder.
     */
    public void reset() {
        /* Reset values */
        theDeposit = null;
        theRate = null;
        theBonus = null;
        theEndDate = null;
    }
}
