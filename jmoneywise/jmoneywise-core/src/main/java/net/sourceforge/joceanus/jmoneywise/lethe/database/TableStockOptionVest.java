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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.StockOptionVest;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for StockOptionVest.
 */
public class TableStockOptionVest
        extends PrometheusTableEncrypted<StockOptionVest, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = StockOptionVest.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableStockOptionVest(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addReferenceColumn(StockOptionVest.FIELD_OPTION, TableStockOption.TABLE_NAME);
        myTableDef.addDateColumn(StockOptionVest.FIELD_DATE);
        myTableDef.addEncryptedColumn(StockOptionVest.FIELD_UNITS, MetisEncryptedData.UNITSLEN);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getStockOptionVests());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(StockOptionVest.OBJECT_NAME);
        myValues.addValue(StockOptionVest.FIELD_OPTION, myTableDef.getIntegerValue(StockOptionVest.FIELD_OPTION));
        myValues.addValue(StockOptionVest.FIELD_DATE, myTableDef.getDateValue(StockOptionVest.FIELD_DATE));
        myValues.addValue(StockOptionVest.FIELD_UNITS, myTableDef.getBinaryValue(StockOptionVest.FIELD_UNITS));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final StockOptionVest pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        PrometheusTableDefinition myTableDef = getTableDef();
        if (StockOptionVest.FIELD_OPTION.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getStockOptionId());
        } else if (StockOptionVest.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (StockOptionVest.FIELD_UNITS.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getUnitsBytes());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
