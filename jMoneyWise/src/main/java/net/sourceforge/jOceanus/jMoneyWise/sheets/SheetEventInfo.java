/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * SheetDataInfo extension for EventInfo.
 * @author Tony Washer
 */
public class SheetEventInfo
        extends SheetDataInfo<EventInfo> {
    /**
     * NamedArea for EventInfo.
     */
    private static final String AREA_EVENTINFO = EventInfo.LIST_NAME;

    /**
     * Event data list.
     */
    private final EventList theEvents;

    /**
     * EventInfo data list.
     */
    private final EventInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetEventInfo(final FinanceReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_EVENTINFO);

        /* Access the InfoType list */
        FinanceData myData = pReader.getData();
        theEvents = myData.getEvents();
        theList = myData.getEventInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventInfo(final FinanceWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_EVENTINFO);

        /* Access the InfoType list */
        theEvents = null;
        theList = pWriter.getData().getEventInfo();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final Integer pId,
                                     final Integer pControlId,
                                     final Integer pInfoTypeId,
                                     final Integer pOwnerId,
                                     final byte[] pValue) throws JDataException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Validate the events */
        theEvents.validateOnLoad();
    }
}
