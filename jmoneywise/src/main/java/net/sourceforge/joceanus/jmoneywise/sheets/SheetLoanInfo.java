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
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo;
import net.sourceforge.joceanus.jmoneywise.data.LoanInfo.LoanInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataInfo extension for LoanInfo.
 * @author Tony Washer
 */
public class SheetLoanInfo
        extends SheetDataInfo<LoanInfo, MoneyWiseDataType> {
    /**
     * NamedArea for DepositInfo.
     */
    private static final String AREA_LOANINFO = LoanInfo.LIST_NAME;

    /**
     * Loans data list.
     */
    private LoanList theLoans = null;

    /**
     * LoanInfo data list.
     */
    private final LoanInfoList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetLoanInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_LOANINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pReader.getData();
        theLoans = myData.getLoans();
        theList = myData.getLoanInfo();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetLoanInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_LOANINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pWriter.getData();
        theList = myData.getLoanInfo();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(LoanInfo.OBJECT_NAME);
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve ValueLinks and validate */
        theList.validateOnLoad();

        /* Validate the loans */
        theLoans.validateOnLoad();
    }
}
