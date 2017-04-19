/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisEncryptedData;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for StockOption.
 */
public class TableStockOption
        extends PrometheusTableEncrypted<StockOption, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = StockOption.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableStockOption(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addIntegerColumn(StockOption.FIELD_STOCKHOLDING);
        myTableDef.addDateColumn(StockOption.FIELD_GRANTDATE);
        myTableDef.addDateColumn(StockOption.FIELD_EXPIREDATE);
        myTableDef.addEncryptedColumn(StockOption.FIELD_PRICE, MetisEncryptedData.PRICELEN);
        myTableDef.addEncryptedColumn(StockOption.FIELD_NAME, StockOption.NAMELEN);
        myTableDef.addNullEncryptedColumn(StockOption.FIELD_DESC, StockOption.DESCLEN);
        myTableDef.addBooleanColumn(StockOption.FIELD_CLOSED);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getStockOptions());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(StockOption.OBJECT_NAME);
        myValues.addValue(StockOption.FIELD_NAME, myTableDef.getBinaryValue(StockOption.FIELD_NAME));
        myValues.addValue(StockOption.FIELD_DESC, myTableDef.getBinaryValue(StockOption.FIELD_DESC));
        myValues.addValue(StockOption.FIELD_STOCKHOLDING, myTableDef.getIntegerValue(StockOption.FIELD_STOCKHOLDING));
        myValues.addValue(StockOption.FIELD_GRANTDATE, myTableDef.getDateValue(StockOption.FIELD_GRANTDATE));
        myValues.addValue(StockOption.FIELD_EXPIREDATE, myTableDef.getDateValue(StockOption.FIELD_EXPIREDATE));
        myValues.addValue(StockOption.FIELD_PRICE, myTableDef.getBinaryValue(StockOption.FIELD_PRICE));
        myValues.addValue(StockOption.FIELD_CLOSED, myTableDef.getBooleanValue(StockOption.FIELD_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final StockOption pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        PrometheusTableDefinition myTableDef = getTableDef();
        if (StockOption.FIELD_STOCKHOLDING.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getStockHoldingId());
        } else if (StockOption.FIELD_GRANTDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getGrantDate());
        } else if (StockOption.FIELD_EXPIREDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getExpiryDate());
        } else if (StockOption.FIELD_PRICE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getPriceBytes());
        } else if (StockOption.FIELD_NAME.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getNameBytes());
        } else if (StockOption.FIELD_DESC.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getDescBytes());
        } else if (StockOption.FIELD_CLOSED.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isClosed());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
