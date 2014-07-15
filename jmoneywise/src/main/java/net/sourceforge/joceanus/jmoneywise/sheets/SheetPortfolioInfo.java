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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfo;
import net.sourceforge.joceanus.jmoneywise.data.PortfolioInfo.PortfolioInfoList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataInfo extension for PortfolioInfo.
 * @author Tony Washer
 */
public class SheetPortfolioInfo
        extends SheetDataInfo<PortfolioInfo, MoneyWiseDataType> {
    /**
     * NamedArea for PortfolioInfo.
     */
    private static final String AREA_PORTFOLIOINFO = PortfolioInfo.LIST_NAME;

    /**
     * Portfolios data list.
     */
    private PortfolioList thePortfolios = null;

    /**
     * PorfolioInfo data list.
     */
    private final PortfolioInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPortfolioInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_PORTFOLIOINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pReader.getData();
        thePortfolios = myData.getPortfolios();
        theList = myData.getPortfolioInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPortfolioInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_PORTFOLIOINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pWriter.getData();
        theList = myData.getPortfolioInfo();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(PortfolioInfo.OBJECT_NAME);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* validate */
        theList.validateOnLoad();

        /* Validate the portfolios */
        thePortfolios.validateOnLoad();
    }
}