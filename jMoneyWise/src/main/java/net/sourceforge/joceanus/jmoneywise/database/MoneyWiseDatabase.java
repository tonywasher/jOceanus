/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.database;

import java.util.logging.Logger;

import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.preferences.DatabasePreferences;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Database extension for MoneyWiseData.
 * @author Tony Washer
 */
public class MoneyWiseDatabase
        extends Database<MoneyWiseData> {
    /**
     * Construct a new Database class for load.
     * @param pLogger the logger
     * @param pPreferences the preferences
     * @throws JOceanusException on error
     */
    public MoneyWiseDatabase(final Logger pLogger,
                             final DatabasePreferences pPreferences) throws JOceanusException {
        /* Call super-constructor */
        super(pLogger, pPreferences);

        /* Add additional tables */
        declareTables();
    }

    /**
     * Declare tables.
     */
    private void declareTables() {
        /* Add additional tables */
        addTable(new TableAccountCategoryType(this));
        addTable(new TableEventCategoryType(this));
        addTable(new TableTaxBasis(this));
        addTable(new TableTaxCategory(this));
        addTable(new TableAccountCurrency(this));
        addTable(new TableTaxRegime(this));
        addTable(new TableFrequency(this));
        addTable(new TableTaxYearInfoType(this));
        addTable(new TableAccountInfoType(this));
        addTable(new TableEventInfoType(this));
        addTable(new TableEventClass(this));
        addTable(new TableAccountCategory(this));
        addTable(new TableEventCategory(this));
        addTable(new TableTaxYear(this));
        addTable(new TableTaxYearInfo(this));
        addTable(new TableExchangeRate(this));
        addTable(new TableAccount(this));
        addTable(new TableAccountRate(this));
        addTable(new TableAccountPrice(this));
        addTable(new TableAccountInfo(this));
        addTable(new TableEvent(this));
        addTable(new TableEventInfo(this));
        addTable(new TablePattern(this));
        addTable(new TableEventClassLink(this));
    }
}
