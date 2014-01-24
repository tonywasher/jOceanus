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

import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfo;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jtethys.JOceanusException;

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
    protected SheetEventInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_EVENTINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pReader.getData();
        theEvents = myData.getEvents();
        theList = myData.getEventInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetEventInfo(final MoneyWiseWriter pWriter) {
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
                                     final byte[] pValue) throws JOceanusException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Validate the events */
        theEvents.validateOnLoad();
    }
}
