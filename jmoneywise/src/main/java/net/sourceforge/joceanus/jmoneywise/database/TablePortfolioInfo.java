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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfo;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * TableDataInfo extension for PortfolioInfo.
 * @author Tony Washer
 */
public class TablePortfolioInfo
        extends TableDataInfo<PortfolioInfo, MoneyWiseDataType> {
    /**
     * The name of the table.
     */
    protected static final String TABLE_NAME = PortfolioInfo.LIST_NAME;

    /**
     * Portfolios data list.
     */
    private PortfolioList thePortfolios = null;

    /**
     * The PortfolioInfo list.
     */
    private PortfolioInfoList theList = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TablePortfolioInfo(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME, TableAccountInfoType.TABLE_NAME, TablePortfolio.TABLE_NAME);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        thePortfolios = myData.getPortfolios();
        theList = myData.getPortfolioInfo();
        setList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(PortfolioInfo.OBJECT_NAME);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Validate */
        theList.validateOnLoad();

        /* Validate the portfolios */
        thePortfolios.validateOnLoad();
    }
}
