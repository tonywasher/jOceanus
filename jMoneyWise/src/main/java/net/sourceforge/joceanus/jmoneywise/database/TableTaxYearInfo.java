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

import net.sourceforge.joceanus.jprometheus.data.DataErrorList;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDataInfo;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.TaxYear.TaxYearList;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo;
import net.sourceforge.joceanus.jmoneywise.data.TaxYearInfo.TaxInfoList;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableDataInfo extension for TaxYearInfo.
 * @author Tony Washer
 */
public class TableTaxYearInfo
        extends TableDataInfo<TaxYearInfo> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = TaxYearInfo.LIST_NAME;

    /**
     * The DataSet.
     */
    private MoneyWiseData theData = null;

    /**
     * TaxYear data list.
     */
    private TaxYearList theTaxYears = null;

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
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        theData = myData;
        theTaxYears = myData.getTaxYears();
        theList = myData.getTaxInfo();
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
        /* Calculate the date range */
        theData.calculateDateRange();

        /* touch underlying items */
        theTaxYears.touchUnderlyingItems();

        /* Validate the tax years */
        DataErrorList<DataItem> myErrors = theTaxYears.validate();
        if (myErrors != null) {
            throw new JMoneyWiseDataException(myErrors, DataItem.ERROR_VALIDATION);
        }
    }
}
