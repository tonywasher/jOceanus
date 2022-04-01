/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.sheets;

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.LoanInfo;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetDataInfo;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataInfo extension for LoanInfo.
 * @author Tony Washer
 */
public class SheetLoanInfo
        extends PrometheusSheetDataInfo<LoanInfo, MoneyWiseDataType> {
    /**
     * NamedArea for DepositInfo.
     */
    private static final String AREA_LOANINFO = LoanInfo.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetLoanInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_LOANINFO);

        /* Access the InfoType list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getLoanInfo());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetLoanInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_LOANINFO);

        /* Access the InfoType list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getLoanInfo());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        return getRowValues(LoanInfo.OBJECT_NAME);
    }
}
