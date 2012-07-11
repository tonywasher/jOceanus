/*******************************************************************************
 * JFinanceApp: Finance Application
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
package net.sourceforge.JFinanceApp.database;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * Database extension for FinanceData.
 * @author Tony Washer
 */
public class FinanceDatabase extends Database<FinanceData> {
    /**
     * Construct a new Database class for load.
     * @throws JDataException on error
     */
    public FinanceDatabase() throws JDataException {
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
        addTable(new TableEventInfoType(this));
        addTable(new TableTaxYear(this));
        addTable(new TableAccount(this));
        addTable(new TableAccountRate(this));
        addTable(new TableAccountPrice(this));
        addTable(new TablePattern(this));
        addTable(new TableEvent(this));
        addTable(new TableEventData(this));
        addTable(new TableEventValues(this));
    }
}
