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

import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.Event.EventList;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseDataType;
import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableDataInfo extension for EventInfo.
 * @author Tony Washer
 */
public class TableEventInfo
        extends TableDataInfo<EventInfo, MoneyWiseDataType> {
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
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theEvents = myData.getEvents();
        theList = myData.getEventInfo();
        setList(theList);
    }

    @Override
    protected void loadTheItem(final Integer pId,
                               final Integer pControlId,
                               final Integer pInfoTypeId,
                               final Integer pOwnerId,
                               final byte[] pValue) throws JOceanusException {
        /* Add into the list */
        theList.addSecureItem(pId, pControlId, pInfoTypeId, pOwnerId, pValue);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Validate the events */
        DataErrorList<DataItem<MoneyWiseDataType>> myErrors = theEvents.validate();
        if (myErrors != null) {
            throw new JMoneyWiseDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
