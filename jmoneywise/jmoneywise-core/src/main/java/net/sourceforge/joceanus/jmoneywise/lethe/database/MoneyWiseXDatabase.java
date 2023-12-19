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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseXDatabase
        extends PrometheusXDataStore {
    /**
     * Construct a new Database class for load.
     * @param pPreferences the preferences
     * @throws OceanusException on error
     */
    public MoneyWiseXDatabase(final PrometheusDatabasePreferences pPreferences) throws OceanusException {
        /* Call super-constructor */
        super(pPreferences);

        /* Add additional tables */
        declareTables();
    }

    /**
     * Declare tables.
     */
    private void declareTables() {
        /* Add additional tables */
        addTable(new TableDepositCategoryType(this));
        addTable(new TableCashCategoryType(this));
        addTable(new TableLoanCategoryType(this));
        addTable(new TablePayeeType(this));
        addTable(new TablePortfolioType(this));
        addTable(new TableSecurityType(this));
        addTable(new TableTransCategoryType(this));
        addTable(new TableTaxBasis(this));
        addTable(new TableAssetCurrency(this));
        addTable(new TableFrequency(this));
        addTable(new TableAccountInfoType(this));
        addTable(new TableTransInfoType(this));
        addTable(new TableTransTag(this));
        addTable(new TableRegion(this));
        addTable(new TableDepositCategory(this));
        addTable(new TableCashCategory(this));
        addTable(new TableLoanCategory(this));
        addTable(new TableTransCategory(this));
        addTable(new TableExchangeRate(this));
        addTable(new TablePayee(this));
        addTable(new TablePayeeInfo(this));
        addTable(new TableSecurity(this));
        addTable(new TableSecurityInfo(this));
        addTable(new TableSecurityPrice(this));
        addTable(new TableDeposit(this));
        addTable(new TableDepositInfo(this));
        addTable(new TableDepositRate(this));
        addTable(new TableCash(this));
        addTable(new TableCashInfo(this));
        addTable(new TableLoan(this));
        addTable(new TableLoanInfo(this));
        addTable(new TablePortfolio(this));
        addTable(new TablePortfolioInfo(this));
        addTable(new TableTransaction(this));
        addTable(new TableTransactionInfo(this));
        addTable(new TableSchedule(this));
    }
}