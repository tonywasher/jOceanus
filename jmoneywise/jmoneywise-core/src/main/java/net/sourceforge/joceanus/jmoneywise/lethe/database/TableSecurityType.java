/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityType;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableStaticData extension for SecurityType.
 * @author Tony Washer
 */
public class TableSecurityType
        extends PrometheusTableStaticData<SecurityType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = SecurityType.LIST_NAME;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected TableSecurityType(final PrometheusDataStore<MoneyWiseData> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getSecurityTypes());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(SecurityType.OBJECT_NAME);
    }
}
