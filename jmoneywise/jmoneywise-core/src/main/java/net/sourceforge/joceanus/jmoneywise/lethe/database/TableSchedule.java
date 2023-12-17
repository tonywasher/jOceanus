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
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Schedule;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXColumnDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDataItem;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusXTableDefinition.SortOrder;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TabelEncrypted extension for Schedule.
 * @author Tony Washer
 */
public class TableSchedule
        extends PrometheusXTableDataItem<Schedule> {
    /**
     * The name of the Schedules table.
     */
    protected static final String TABLE_NAME = Schedule.LIST_NAME;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableSchedule(final PrometheusXDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Declare the columns */
        myTableDef.addDateColumn(Schedule.FIELD_STARTDATE);
        myTableDef.addNullDateColumn(Schedule.FIELD_ENDDATE);
        myTableDef.addReferenceColumn(Schedule.FIELD_FREQ, TableFrequency.TABLE_NAME);
        myTableDef.addNullIntegerColumn(Schedule.FIELD_REPFREQ);
        myTableDef.addNullIntegerColumn(Schedule.FIELD_PATTERN);
        final PrometheusXColumnDefinition myDateCol = myTableDef.addNullDateColumn(Schedule.FIELD_NEXTDATE);

        /* Declare Sort Columns */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getSchedules());
    }

    @Override
    protected DataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusXTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues myValues = getRowValues(Schedule.OBJECT_NAME);
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
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusXTableDefinition myTableDef = getTableDef();
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
