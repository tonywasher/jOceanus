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
import net.sourceforge.joceanus.jmoneywise.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfo;
import net.sourceforge.joceanus.jmoneywise.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataInfo extension for DepositInfo.
 * @author Tony Washer
 */
public class SheetDepositInfo
        extends SheetDataInfo<DepositInfo, MoneyWiseDataType> {
    /**
     * NamedArea for DepositInfo.
     */
    private static final String AREA_DEPOSITINFO = DepositInfo.LIST_NAME;

    /**
     * Deposits data list.
     */
    private DepositList theDeposits = null;

    /**
     * DepositInfo data list.
     */
    private final DepositInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetDepositInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_DEPOSITINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pReader.getData();
        theDeposits = myData.getDeposits();
        theList = myData.getDepositInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDepositInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_DEPOSITINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pWriter.getData();
        theList = myData.getDepositInfo();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(DepositInfo.OBJECT_NAME);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve ValueLinks and validate */
        theList.resolveValueLinks();
        theList.validateOnLoad();

        /* Validate the deposits */
        theDeposits.validateOnLoad();
    }
}
