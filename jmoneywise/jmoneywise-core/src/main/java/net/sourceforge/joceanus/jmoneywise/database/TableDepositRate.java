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

import javax.swing.SortOrder;

import net.sourceforge.joceanus.jmetis.data.MetisEncryptedData;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.PrometheusColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.database.PrometheusTableEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableEncrypted extension for DepositRate.
 * @author Tony Washer
 */
public class TableDepositRate
        extends PrometheusTableEncrypted<DepositRate, MoneyWiseDataType> {
    /**
     * The name of the Rates table.
     */
    protected static final String TABLE_NAME = DepositRate.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableDepositRate(final PrometheusDataStore<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        PrometheusColumnDefinition myActCol = myTableDef.addReferenceColumn(DepositRate.FIELD_DEPOSIT, TableDeposit.TABLE_NAME);
        myTableDef.addEncryptedColumn(DepositRate.FIELD_RATE, MetisEncryptedData.RATELEN);
        myTableDef.addNullEncryptedColumn(DepositRate.FIELD_BONUS, MetisEncryptedData.RATELEN);
        PrometheusColumnDefinition myDateCol = myTableDef.addNullDateColumn(DepositRate.FIELD_ENDDATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.DESCENDING);
        myActCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getDepositRates());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(DepositRate.OBJECT_NAME);
        myValues.addValue(DepositRate.FIELD_DEPOSIT, myTableDef.getIntegerValue(DepositRate.FIELD_DEPOSIT));
        myValues.addValue(DepositRate.FIELD_RATE, myTableDef.getBinaryValue(DepositRate.FIELD_RATE));
        myValues.addValue(DepositRate.FIELD_BONUS, myTableDef.getBinaryValue(DepositRate.FIELD_BONUS));
        myValues.addValue(DepositRate.FIELD_ENDDATE, myTableDef.getDateValue(DepositRate.FIELD_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final DepositRate pItem,
                                 final MetisField iField) throws OceanusException {
        /* Switch on field id */
        PrometheusTableDefinition myTableDef = getTableDef();
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
