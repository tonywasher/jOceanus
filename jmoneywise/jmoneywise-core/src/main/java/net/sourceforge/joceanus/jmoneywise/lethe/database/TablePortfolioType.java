/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PortfolioType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableStaticData extension for PortfolioType.
 * @author Tony Washer
 */
public class TablePortfolioType
        extends PrometheusTableStaticData<PortfolioType, MoneyWiseDataType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = PortfolioType.LIST_NAME;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected TablePortfolioType(final PrometheusDataStore<MoneyWiseData> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getPortfolioTypes());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(PortfolioType.OBJECT_NAME);
    }
}