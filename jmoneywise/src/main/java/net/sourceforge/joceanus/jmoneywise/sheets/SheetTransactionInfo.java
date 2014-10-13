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
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataInfo;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataInfo extension for TransactionInfo.
 * @author Tony Washer
 */
public class SheetTransactionInfo
        extends SheetDataInfo<TransactionInfo, MoneyWiseDataType> {
    /**
     * NamedArea for TransactionInfo.
     */
    private static final String AREA_TRANSINFO = TransactionInfo.LIST_NAME;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetTransactionInfo(final MoneyWiseReader pReader) {
        /* Call super-constructor */
        super(pReader, AREA_TRANSINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getTransactionInfo());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetTransactionInfo(final MoneyWiseWriter pWriter) {
        /* Call super-constructor */
        super(pWriter, AREA_TRANSINFO);

        /* Access the InfoType list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getTransactionInfo());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        return getRowValues(TransactionInfo.OBJECT_NAME);
    }
}
