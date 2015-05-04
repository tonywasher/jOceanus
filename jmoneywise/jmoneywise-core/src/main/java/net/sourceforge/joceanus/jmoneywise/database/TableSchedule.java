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

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Schedule;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.ColumnDefinition;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.DatabaseTable;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TabelEncrypted extension for Schedule.
 * @author Tony Washer
 */
public class TableSchedule
        extends DatabaseTable<Schedule, MoneyWiseDataType> {
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
        myTableDef.addDateColumn(Schedule.FIELD_STARTDATE);
        myTableDef.addNullDateColumn(Schedule.FIELD_ENDDATE);
        myTableDef.addReferenceColumn(Schedule.FIELD_FREQ, TableFrequency.TABLE_NAME);
        myTableDef.addNullIntegerColumn(Schedule.FIELD_REPFREQ);
        myTableDef.addNullIntegerColumn(Schedule.FIELD_PATTERN);
        ColumnDefinition myDateCol = myTableDef.addNullDateColumn(Schedule.FIELD_NEXTDATE);

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
        myValues.addValue(Schedule.FIELD_STARTDATE, myTableDef.getDateValue(Schedule.FIELD_STARTDATE));
        myValues.addValue(Schedule.FIELD_ENDDATE, myTableDef.getDateValue(Schedule.FIELD_ENDDATE));
        myValues.addValue(Schedule.FIELD_FREQ, myTableDef.getIntegerValue(Schedule.FIELD_FREQ));
        myValues.addValue(Schedule.FIELD_REPFREQ, myTableDef.getIntegerValue(Schedule.FIELD_REPFREQ));
        myValues.addValue(Schedule.FIELD_PATTERN, myTableDef.getIntegerValue(Schedule.FIELD_PATTERN));
        myValues.addValue(Schedule.FIELD_NEXTDATE, myTableDef.getDateValue(Schedule.FIELD_NEXTDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final Schedule pItem,
                                 final JDataField iField) throws JOceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (Schedule.FIELD_STARTDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getStartDate());
        } else if (Schedule.FIELD_ENDDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getEndDate());
        } else if (Schedule.FIELD_FREQ.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getFrequencyId());
        } else if (Schedule.FIELD_REPFREQ.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getRepeatFrequencyId());
        } else if (Schedule.FIELD_PATTERN.equals(iField)) {
            myTableDef.setIntegerValue(iField, pItem.getPatternValue());
        } else if (Schedule.FIELD_NEXTDATE.equals(iField)) {
            myTableDef.setDateValue(iField, pItem.getNextDate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
