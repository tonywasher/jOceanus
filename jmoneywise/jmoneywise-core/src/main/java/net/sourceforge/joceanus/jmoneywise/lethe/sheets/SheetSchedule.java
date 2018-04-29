/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Schedule;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetDataItem;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Schedule.
 * @author Tony Washer
 */
public class SheetSchedule
        extends PrometheusSheetDataItem<Schedule, MoneyWiseDataType> {
    /**
     * NamedArea for Schedules.
     */
    private static final String AREA_SCHEDULES = Schedule.LIST_NAME;

    /**
     * StartDate column.
     */
    private static final int COL_STARTDATE = COL_ID + 1;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = COL_STARTDATE + 1;

    /**
     * Frequency column.
     */
    private static final int COL_FREQ = COL_ENDDATE + 1;

    /**
     * Repeat Frequency column.
     */
    private static final int COL_REPFREQ = COL_FREQ + 1;

    /**
     * Pattern column.
     */
    private static final int COL_PATTERN = COL_REPFREQ + 1;

    /**
     * NextDate column.
     */
    private static final int COL_NEXTDATE = COL_PATTERN + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetSchedule(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_SCHEDULES);

        /* Access the Lists */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getSchedules());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetSchedule(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_SCHEDULES);

        /* Access the Schedules list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getSchedules());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Schedule.OBJECT_NAME);
        myValues.addValue(Schedule.FIELD_STARTDATE, loadDate(COL_STARTDATE));
        myValues.addValue(Schedule.FIELD_ENDDATE, loadDate(COL_ENDDATE));
        myValues.addValue(Schedule.FIELD_FREQ, loadInteger(COL_FREQ));
        myValues.addValue(Schedule.FIELD_REPFREQ, loadInteger(COL_REPFREQ));
        myValues.addValue(Schedule.FIELD_PATTERN, loadInteger(COL_PATTERN));
        myValues.addValue(Schedule.FIELD_NEXTDATE, loadDate(COL_NEXTDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Schedule pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_STARTDATE, pItem.getStartDate());
        writeDate(COL_ENDDATE, pItem.getEndDate());
        writeInteger(COL_FREQ, pItem.getFrequencyId());
        writeInteger(COL_REPFREQ, pItem.getRepeatFrequencyId());
        writeInteger(COL_PATTERN, pItem.getPatternValue());
        writeDate(COL_NEXTDATE, pItem.getNextDate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_NEXTDATE;
    }
}
