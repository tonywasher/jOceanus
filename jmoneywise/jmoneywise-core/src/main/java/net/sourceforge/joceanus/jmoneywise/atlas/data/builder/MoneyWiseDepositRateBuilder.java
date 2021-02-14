/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

/**
 * DepositRate Builder.
 */
public class MoneyWiseDepositRateBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The Deposit.
     */
    private Deposit theDeposit;

    /**
     * The Date.
     */
    private TethysDate theEndDate;

    /**
     * The Rate.
     */
    private TethysRate theRate;

    /**
     * The Bonus.
     */
    private TethysRate theBonus;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseDepositRateBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
    }

    /**
     * Set Deposit.
     * @param pDeposit the deposit.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder deposit(final Deposit pDeposit) {
        theDeposit = pDeposit;
        return this;
    }

    /**
     * Set the rate.
     * @param pRate the rate.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder rate(final TethysRate pRate) {
        theRate = pRate;
        return this;
    }

    /**
     * Set the bonus.
     * @param pBonus the bonus.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder bonus(final TethysRate pBonus) {
        theBonus = pBonus;
        return this;
    }

    /**
     * Set the endDate.
     * @param pEndDate the endDate of the rate.
     * @return the builder
     */
    public MoneyWiseDepositRateBuilder endDate(final TethysDate pEndDate) {
        theEndDate = pEndDate;
        return this;
    }

    /**
     * Build the Rate.
     * @return the new Rate
     * @throws OceanusException on error
     */
    public DepositRate build() throws OceanusException {
        /* Create the rate */
        final DepositRate myRate = theDataSet.getDepositRates().addNewItem();
        myRate.setDeposit(theDeposit);
        myRate.setRate(theRate);
        myRate.setBonus(theBonus);
        myRate.setEndDate(theEndDate);
        myRate.validate();

        /* Reset values */
        theDeposit = null;
        theRate = null;
        theBonus = null;
        theEndDate = null;

        /* Return the rate */
        return myRate;
    }
}
