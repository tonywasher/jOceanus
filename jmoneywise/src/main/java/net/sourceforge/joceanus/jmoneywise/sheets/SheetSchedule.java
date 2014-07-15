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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Schedule;
import net.sourceforge.joceanus.jmoneywise.data.Schedule.ScheduleList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Schedule.
 * @author Tony Washer
 */
public class SheetSchedule
        extends SheetEncrypted<Schedule, MoneyWiseDataType> {
    /**
     * NamedArea for Schedules.
     */
    private static final String AREA_SCHEDULES = Schedule.LIST_NAME;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_KEYSETID + 1;

    /**
     * Pair column.
     */
    private static final int COL_PAIR = COL_DATE + 1;

    /**
     * Debit column.
     */
    private static final int COL_DEBIT = COL_PAIR + 1;

    /**
     * Credit column.
     */
    private static final int COL_CREDIT = COL_DEBIT + 1;

    /**
     * Amount column.
     */
    private static final int COL_AMOUNT = COL_CREDIT + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_AMOUNT + 1;

    /**
     * Frequency column.
     */
    private static final int COL_FREQ = COL_CATEGORY + 1;

    /**
     * Split column.
     */
    private static final int COL_SPLIT = COL_CATEGORY + 1;

    /**
     * Reconciled column.
     */
    private static final int COL_PARENT = COL_SPLIT + 1;

    /**
     * Schedule data list.
     */
    private final ScheduleList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetSchedule(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_SCHEDULES);

        /* Access the Lists */
        theList = pReader.getData().getSchedules();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetSchedule(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_SCHEDULES);

        /* Access the Schedules list */
        theList = pWriter.getData().getSchedules();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Schedule.OBJECT_NAME);
        myValues.addValue(Schedule.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(Schedule.FIELD_PAIR, loadInteger(COL_PAIR));
        myValues.addValue(Schedule.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Schedule.FIELD_DEBIT, loadInteger(COL_DEBIT));
        myValues.addValue(Schedule.FIELD_CREDIT, loadInteger(COL_CREDIT));
        myValues.addValue(Schedule.FIELD_AMOUNT, loadBytes(COL_AMOUNT));
        myValues.addValue(Schedule.FIELD_SPLIT, loadBoolean(COL_SPLIT));
        myValues.addValue(Schedule.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Schedule.FIELD_FREQ, loadInteger(COL_FREQ));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Schedule pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeDate(COL_DATE, pItem.getDate());
        writeInteger(COL_PAIR, pItem.getAssetPairId());
        writeInteger(COL_DEBIT, pItem.getDebitId());
        writeInteger(COL_CREDIT, pItem.getCreditId());
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_FREQ, pItem.getFrequencyId());
        writeBytes(COL_AMOUNT, pItem.getAmountBytes());
        writeBoolean(COL_SPLIT, pItem.isSplit());
        writeInteger(COL_PARENT, pItem.getParentId());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_PARENT;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the schedules */
        theList.validateOnLoad();
    }
}