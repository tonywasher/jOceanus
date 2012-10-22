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
package net.sourceforge.JFinanceApp.database;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataModels.data.DataSet;
import net.sourceforge.JDataModels.database.Database;
import net.sourceforge.JDataModels.database.TableDataInfo;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.TaxYearInfo;
import net.sourceforge.JFinanceApp.data.TaxYearInfo.TaxInfoList;

/**
 * TableDataInfo extension for TaxYearInfo.
 * @author Tony Washer
 */
public class TableTaxYearInfo extends TableDataInfo<TaxYearInfo> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = TaxYearInfo.LIST_NAME;

    /**
     * The TaxInfo list.
     */
    private TaxInfoList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTaxYearInfo(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME, TableTaxYearInfoType.TABLE_NAME, TableTaxYear.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theList = myData.getTaxInfo();
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
