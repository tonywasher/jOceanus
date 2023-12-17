/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jprometheus.lethe.data.ControlData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Database table class for ControlData.
 */
public class PrometheusXTableControlData
        extends PrometheusXTableDataItem<ControlData> {
    /**
     * The name of the Static table.
     */
    protected static final String TABLE_NAME = ControlData.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected PrometheusXTableControlData(final PrometheusXDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Define the columns */
        myTableDef.addIntegerColumn(ControlData.FIELD_DATAVERSION);
        myTableDef.addReferenceColumn(ControlData.FIELD_CONTROLKEY, PrometheusXTableControlKeys.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet pData) {
        setList(pData.getControlData());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access table definition */
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(ControlData.OBJECT_NAME);
        myValues.addValue(ControlData.FIELD_DATAVERSION, myTableDef.getIntegerValue(ControlData.FIELD_DATAVERSION));
        myValues.addValue(ControlData.FIELD_CONTROLKEY, myTableDef.getIntegerValue(ControlData.FIELD_CONTROLKEY));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final ControlData pItem,
                                 final MetisLetheField pField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (ControlData.FIELD_DATAVERSION.equals(pField)) {
            myTableDef.setIntegerValue(pField, pItem.getDataVersion());
        } else if (ControlData.FIELD_CONTROLKEY.equals(pField)) {
            myTableDef.setIntegerValue(pField, pItem.getControlKeyId());
        } else {
            super.setFieldValue(pItem, pField);
        }
    }
}
