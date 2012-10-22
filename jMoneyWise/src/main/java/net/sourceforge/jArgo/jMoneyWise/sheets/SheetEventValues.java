/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.sheets;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.sheets.SheetDataItem;
import net.sourceforge.JFinanceApp.data.EventValue;
import net.sourceforge.JFinanceApp.data.EventValue.EventValueList;
import net.sourceforge.JFinanceApp.data.FinanceData;

/**
 * SheetDataItem extension for Event Values.
 * @author Tony Washer
 */
public class SheetEventValues extends SheetDataItem<EventValue> {
    /**
     * NamedArea for EventValues.
     */
    private static final String AREA_EVENTVALUES = EventValue.LIST_NAME;

    /**
     * Event column.
     */
    private static final int COL_EVENT = COL_ID + 1;

    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = COL_EVENT + 1;

    /**
     * Value column.
     */
    private static final int COL_VALUE = COL_INFOTYPE + 1;

    /**
     * EventValue data list.
     */
    private EventValueList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventValues(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTVALUES);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getEventValues();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventValues(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTVALUES);

        /* Access the TaxYears list */
        theList = pWriter.getData().getEventValues();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myEventId = loadInteger(COL_EVENT);
        Integer myInfoId = loadInteger(COL_INFOTYPE);
        Integer myValue = loadInteger(COL_VALUE);

        /* Add the Value */
        theList.addOpenItem(myID, myInfoId, myEventId, myValue);
    }

    @Override
    protected void insertSecureItem(final EventValue pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_INFOTYPE, pItem.getInfoType().getId());
        writeInteger(COL_EVENT, pItem.getEvent().getId());
        writeInteger(COL_VALUE, pItem.getValue());
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_VALUE);
    }
}
