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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * TableEncrypted extension for DepositRate.
 * @author Tony Washer
 */
public class TableDepositRate
        extends PrometheusXTableEncrypted<DepositRate> {
    /**
     * The name of the Rates table.
     */
    protected static final String TABLE_NAME = DepositRate.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableDepositRate(final PrometheusXDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        final PrometheusXColumnDefinition myActCol = myTableDef.addReferenceColumn(DepositRate.FIELD_DEPOSIT, TableDeposit.TABLE_NAME);
        myTableDef.addEncryptedColumn(DepositRate.FIELD_RATE, TethysDecimal.BYTE_LEN);
        myTableDef.addNullEncryptedColumn(DepositRate.FIELD_BONUS, TethysDecimal.BYTE_LEN);
        final PrometheusXColumnDefinition myDateCol = myTableDef.addNullDateColumn(DepositRate.FIELD_ENDDATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.DESCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getDepositRates());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(DepositRate.OBJECT_NAME);
        myValues.addValue(DepositRate.FIELD_DEPOSIT, myTableDef.getIntegerValue(DepositRate.FIELD_DEPOSIT));
        myValues.addValue(DepositRate.FIELD_RATE, myTableDef.getBinaryValue(DepositRate.FIELD_RATE));
        myValues.addValue(DepositRate.FIELD_BONUS, myTableDef.getBinaryValue(DepositRate.FIELD_BONUS));
        myValues.addValue(DepositRate.FIELD_ENDDATE, myTableDef.getDateValue(DepositRate.FIELD_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final DepositRate pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
        if (DepositRate.FIELD_DEPOSIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDepositId());
        } else if (DepositRate.FIELD_RATE.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getRateBytes());
        } else if (DepositRate.FIELD_BONUS.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getBonusBytes());
        } else if (DepositRate.FIELD_ENDDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getEndDate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
