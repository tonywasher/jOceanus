/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jMoneyWise.data.EventData;
import net.sourceforge.jOceanus.jMoneyWise.data.EventData.EventDataList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * SheetDataItem extension for EventData.
 * @author Tony Washer
 */
public class SheetEventData extends SheetDataItem<EventData> {
    /**
     * NamedArea for Events.
     */
    private static final String AREA_EVENTDATA = EventData.LIST_NAME;

    /**
     * Event column.
     */
    private static final int COL_EVENT = COL_CONTROLID + 1;

    /**
     * InfoType column.
     */
    private static final int COL_INFOTYPE = COL_EVENT + 1;

    /**
     * Data column.
     */
    private static final int COL_DATA = COL_INFOTYPE + 1;

    /**
     * Events data list.
     */
    private EventDataList theList = null;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventData(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_EVENTDATA);

        /* Access the Lists */
        FinanceData myData = pReader.getData();
        theList = myData.getEventData();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventData(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_EVENTDATA);

        /* Access the Events list */
        theList = pWriter.getData().getEventData();
        setDataList(theList);
    }

    /**
     * Load an item from the spreadsheet.
     * @throws JDataException on error
     */
    @Override
    protected void loadSecureItem() throws JDataException {
        /* Access the IDs */
        Integer myID = loadInteger(COL_ID);
        Integer myControlId = loadInteger(COL_CONTROLID);
        Integer myEventId = loadInteger(COL_EVENT);
        Integer myInfoId = loadInteger(COL_INFOTYPE);

        /* Access the binary values */
        byte[] myValue = loadBytes(COL_DATA);

        /* Load the item */
        theList.addSecureItem(myID, myControlId, myInfoId, myEventId, myValue);
    }

    @Override
    protected void insertSecureItem(final EventData pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_ID, pItem.getId());
        writeInteger(COL_CONTROLID, pItem.getControlKey().getId());
        writeInteger(COL_EVENT, pItem.getEvent().getId());
        writeInteger(COL_INFOTYPE, pItem.getInfoType().getId());
        writeBytes(COL_DATA, pItem.getValueBytes());
    }

    @Override
    protected void postProcessOnWrite() throws JDataException {
        /* Set the range */
        nameRange(COL_DATA);
    }
}
