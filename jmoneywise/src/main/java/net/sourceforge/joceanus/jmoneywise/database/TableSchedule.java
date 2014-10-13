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

import net.sourceforge.joceanus.jmetis.viewer.EncryptedData;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Schedule;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TabelEncrypted extension for Schedule.
 * @author Tony Washer
 */
public class TableSchedule
        extends TableEncrypted<Schedule, MoneyWiseDataType> {
    /**
     * The name of the Schedules table.
     */
    protected static final String TABLE_NAME = Schedule.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSchedule(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(Schedule.FIELD_DATE);
        myTableDef.addIntegerColumn(Schedule.FIELD_PAIR);
        myTableDef.addIntegerColumn(Schedule.FIELD_DEBIT);
        myTableDef.addIntegerColumn(Schedule.FIELD_CREDIT);
        myTableDef.addEncryptedColumn(Schedule.FIELD_AMOUNT, EncryptedData.MONEYLEN);
        myTableDef.addReferenceColumn(Schedule.FIELD_CATEGORY, TableTransCategory.TABLE_NAME);
        myTableDef.addReferenceColumn(Schedule.FIELD_FREQ, TableFrequency.TABLE_NAME);
        myTableDef.addBooleanColumn(Schedule.FIELD_SPLIT);
        myTableDef.addNullReferenceColumn(Schedule.FIELD_PARENT, TABLE_NAME);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getSchedules());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Schedule.OBJECT_NAME);
        myValues.addValue(Schedule.FIELD_DATE, myTableDef.getDateValue(Schedule.FIELD_DATE));
        myValues.addValue(Schedule.FIELD_PAIR, myTableDef.getIntegerValue(Schedule.FIELD_PAIR));
        myValues.addValue(Schedule.FIELD_CATEGORY, myTableDef.getIntegerValue(Schedule.FIELD_CATEGORY));
        myValues.addValue(Schedule.FIELD_DEBIT, myTableDef.getIntegerValue(Schedule.FIELD_DEBIT));
        myValues.addValue(Schedule.FIELD_CREDIT, myTableDef.getIntegerValue(Schedule.FIELD_CREDIT));
        myValues.addValue(Schedule.FIELD_AMOUNT, myTableDef.getBinaryValue(Schedule.FIELD_AMOUNT));
        myValues.addValue(Schedule.FIELD_SPLIT, myTableDef.getBooleanValue(Schedule.FIELD_SPLIT));
        myValues.addValue(Schedule.FIELD_PARENT, myTableDef.getIntegerValue(Schedule.FIELD_PARENT));
        myValues.addValue(Schedule.FIELD_FREQ, myTableDef.getIntegerValue(Schedule.FIELD_FREQ));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Schedule pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Schedule.FIELD_DATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getDate());
        } else if (Schedule.FIELD_PAIR.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getAssetPairId());
        } else if (Schedule.FIELD_AMOUNT.equals(iField)) {
            myTableDef.setBinaryValue(iField, pItem.getAmountBytes());
        } else if (Schedule.FIELD_DEBIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getDebitId());
        } else if (Schedule.FIELD_CREDIT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCreditId());
        } else if (Schedule.FIELD_CATEGORY.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getCategoryId());
        } else if (Schedule.FIELD_FREQ.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getFrequencyId());
        } else if (Schedule.FIELD_SPLIT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isSplit());
        } else if (Schedule.FIELD_PARENT.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getParentId());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
