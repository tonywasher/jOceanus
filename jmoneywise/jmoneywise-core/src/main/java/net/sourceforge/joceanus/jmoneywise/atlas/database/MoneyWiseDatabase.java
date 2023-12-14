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
package net.sourceforge.joceanus.jmoneywise.atlas.database;

import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusDatabase.PrometheusDatabasePreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseDatabase
        extends PrometheusDataStore {
    /**
     * Construct a new Database class for load.
     * @param pPreferences the preferences
     * @throws OceanusException on error
     */
    public MoneyWiseDatabase(final PrometheusDatabasePreferences pPreferences) throws OceanusException {
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
        addTable(new MoneyWiseTableDepositCategoryType(this));
        addTable(new MoneyWiseTableCashCategoryType(this));
        addTable(new MoneyWiseTableLoanCategoryType(this));
        addTable(new MoneyWiseTablePayeeType(this));
        addTable(new MoneyWiseTablePortfolioType(this));
        addTable(new MoneyWiseTableSecurityType(this));
        addTable(new MoneyWiseTableTransCategoryType(this));
        addTable(new MoneyWiseTableTaxBasis(this));
        addTable(new MoneyWiseTableCurrency(this));
        addTable(new MoneyWiseTableAccountInfoType(this));
        addTable(new MoneyWiseTableTransInfoType(this));
        addTable(new MoneyWiseTableTransTag(this));
        addTable(new MoneyWiseTableRegion(this));
        addTable(new MoneyWiseTableDepositCategory(this));
        addTable(new MoneyWiseTableCashCategory(this));
        addTable(new MoneyWiseTableLoanCategory(this));
        addTable(new MoneyWiseTableTransCategory(this));
        addTable(new MoneyWiseTableExchangeRate(this));
        addTable(new MoneyWiseTablePayee(this));
        addTable(new MoneyWiseTablePayeeInfo(this));
        addTable(new MoneyWiseTableSecurity(this));
        addTable(new MoneyWiseTableSecurityInfo(this));
        addTable(new MoneyWiseTableSecurityPrice(this));
        addTable(new MoneyWiseTableDeposit(this));
        addTable(new MoneyWiseTableDepositInfo(this));
        addTable(new MoneyWiseTableDepositRate(this));
        addTable(new MoneyWiseTableCash(this));
        addTable(new MoneyWiseTableCashInfo(this));
        addTable(new MoneyWiseTableLoan(this));
        addTable(new MoneyWiseTableLoanInfo(this));
        addTable(new MoneyWiseTablePortfolio(this));
        addTable(new MoneyWiseTablePortfolioInfo(this));
        addTable(new MoneyWiseTableTransaction(this));
        addTable(new MoneyWiseTableTransInfo(this));
    }
}
