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
import net.sourceforge.joceanus.jmoneywise.data.StockOptionVest;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for StockOptionVest.
 * @author Tony Washer
 */
public class SheetStockOptionVest
        extends SheetEncrypted<StockOptionVest, MoneyWiseDataType> {
    /**
     * NamedArea for StockOptionVests.
     */
    private static final String AREA_STOCKOPTIONVESTS = StockOptionVest.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_OPTION = COL_KEYSETID + 1;

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_OPTION + 1;

    /**
     * Units column.
     */
    private static final int COL_UNITS = COL_DATE + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetStockOptionVest(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_STOCKOPTIONVESTS);

        /* Access the Vest list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getStockOptionVests());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetStockOptionVest(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_STOCKOPTIONVESTS);

        /* Access the Vest list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getStockOptionVests());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(StockOptionVest.OBJECT_NAME);
        myValues.addValue(StockOptionVest.FIELD_OPTION, loadInteger(COL_OPTION));
        myValues.addValue(StockOptionVest.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(StockOptionVest.FIELD_UNITS, loadBytes(COL_UNITS));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final StockOptionVest pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_OPTION, pItem.getStockOptionId());
        writeDate(COL_DATE, pItem.getDate());
        writeBytes(COL_UNITS, pItem.getUnitsBytes());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_UNITS;
    }
}
