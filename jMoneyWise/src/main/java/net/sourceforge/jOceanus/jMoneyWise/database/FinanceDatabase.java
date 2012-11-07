/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.database;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.preferences.DatabasePreferences;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * Database extension for FinanceData.
 * @author Tony Washer
 */
public class FinanceDatabase
        extends Database<FinanceData> {
    /**
     * Construct a new Database class for load.
     * @param pPreferences the preferences
     * @throws JDataException on error
     */
    public FinanceDatabase(final DatabasePreferences pPreferences) throws JDataException {
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
        addTable(new TableAccountType(this));
        addTable(new TableTransactionType(this));
        addTable(new TableTaxType(this));
        addTable(new TableTaxRegime(this));
        addTable(new TableFrequency(this));
        addTable(new TableTaxYearInfoType(this));
        addTable(new TableAccountInfoType(this));
        addTable(new TableEventInfoType(this));
        addTable(new TableTaxYear(this));
        addTable(new TableTaxYearInfo(this));
        addTable(new TableAccount(this));
        addTable(new TableAccountRate(this));
        addTable(new TableAccountPrice(this));
        addTable(new TablePattern(this));
        addTable(new TablePatternNew(this));
        addTable(new TableAccountInfo(this));
        addTable(new TableEvent(this));
        addTable(new TableEventNew(this));
        addTable(new TableEventInfo(this));
        addTable(new TableEventData(this));
        addTable(new TableEventValues(this));
    }
}
