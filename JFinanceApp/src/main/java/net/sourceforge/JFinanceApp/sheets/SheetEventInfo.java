/*******************************************************************************
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
import net.sourceforge.JDataModels.sheets.SheetDataInfo;
import net.sourceforge.JFinanceApp.data.EventInfo;
import net.sourceforge.JFinanceApp.data.EventInfo.EventInfoList;

/**
 * SheetDataInfo extension for EventInfo.
 * @author Tony Washer
 */
public class SheetEventInfo extends SheetDataInfo<EventInfo> {
    /**
     * NamedArea for EventInfo.
     */
    private static final String AREA_EVENTINFO = EventInfo.LIST_NAME;

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
        theList = pReader.getData().getEventInfo();
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
        theList = pWriter.getData().getEventInfo();
        setDataList(theList);
    }

    @Override
    protected void loadEncryptedItem(final int pId,
                                     final int pControlId,
                                     final int pInfoTypeId,
                                     final int pOwnerId,
                                     final byte[] pValue) throws JDataException {
        /* Create the item */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }
}
