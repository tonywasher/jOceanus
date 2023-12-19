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

import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWisePayeeType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusTableStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableStaticData extension for PayeeType.
 * @author Tony Washer
 */
public class MoneyWiseTablePayeeType
        extends PrometheusTableStaticData<MoneyWisePayeeType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = MoneyWisePayeeType.LIST_NAME;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected MoneyWiseTablePayeeType(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getPayeeTypes());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Build data values */
        return getRowValues(MoneyWisePayeeType.OBJECT_NAME);
    }
}