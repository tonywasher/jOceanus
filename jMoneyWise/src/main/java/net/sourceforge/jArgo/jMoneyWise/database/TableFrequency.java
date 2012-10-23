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
import net.sourceforge.jArgo.jDataModels.database.TableStaticData;
import net.sourceforge.jArgo.jMoneyWise.data.FinanceData;
import net.sourceforge.jArgo.jMoneyWise.data.statics.Frequency;
import net.sourceforge.jArgo.jMoneyWise.data.statics.Frequency.FrequencyList;

/**
 * TableStaticData extension for Frequency.
 * @author Tony Washer
 */
public class TableFrequency extends TableStaticData<Frequency> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = Frequency.LIST_NAME;

    /**
     * The frequency list.
     */
    private FrequencyList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableFrequency(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getFrequencys();
        setList(theList);
    }

    @Override
    protected void loadTheItem(final Integer pId,
                               final Integer pControlId,
                               final Boolean isEnabled,
                               final Integer iOrder,
                               final byte[] pFreq,
                               final byte[] pDesc) throws JDataException {
        /* Add into the list */
        theList.addSecureItem(pId, pControlId, isEnabled, iOrder, pFreq, pDesc);
    }
}
