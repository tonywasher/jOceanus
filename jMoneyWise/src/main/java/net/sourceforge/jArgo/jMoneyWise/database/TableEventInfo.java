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
package net.sourceforge.jArgo.jMoneyWise.database;

import net.sourceforge.jArgo.jDataManager.JDataException;
import net.sourceforge.jArgo.jDataModels.data.DataSet;
import net.sourceforge.jArgo.jDataModels.database.Database;
import net.sourceforge.jArgo.jDataModels.database.TableDataInfo;
import net.sourceforge.jArgo.jMoneyWise.data.EventInfo;
import net.sourceforge.jArgo.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jArgo.jMoneyWise.data.FinanceData;

/**
 * TableDataInfo extension for EventInfo.
 * @author Tony Washer
 */
public class TableEventInfo extends TableDataInfo<EventInfo> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = EventInfo.LIST_NAME;

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
}
