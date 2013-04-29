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
package net.sourceforge.jOceanus.jMoneyWise.database;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataErrorList;
import net.sourceforge.jOceanus.jDataModels.data.DataItem;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.database.Database;
import net.sourceforge.jOceanus.jDataModels.database.TableDataInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.Event.EventList;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;

/**
 * TableDataInfo extension for EventInfo.
 * @author Tony Washer
 */
public class TableEventInfo
        extends TableDataInfo<EventInfo> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = EventInfo.LIST_NAME;

    /**
     * Events data list.
     */
    private EventList theEvents = null;

    /**
     * The EventInfo list.
     */
    private EventInfoList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableEventInfo(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME, TableEventInfoType.TABLE_NAME, TableEvent.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theEvents = myData.getEvents();
        theList = myData.getEventInfo();
        setList(theList);
    }

    @Override
    protected void loadTheItem(final Integer pId,
                               final Integer pControlId,
                               final Integer pInfoTypeId,
                               final Integer pOwnerId,
                               final byte[] pValue) throws JDataException {
        /* Add into the list */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Validate the events */
        DataErrorList<DataItem> myErrors = theEvents.validate();
        if (myErrors != null) {
            throw new JDataException(ExceptionClass.VALIDATE, myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
