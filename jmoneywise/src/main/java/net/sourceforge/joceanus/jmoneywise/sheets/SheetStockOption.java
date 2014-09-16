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
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for StockOption.
 * @author Tony Washer
 */
public class SheetStockOption
        extends SheetEncrypted<StockOption, MoneyWiseDataType> {
    /**
     * NamedArea for StockOptions.
     */
    private static final String AREA_STOCKOPTIONS = StockOption.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Portfolio column.
     */
    private static final int COL_PORTFOLIO = COL_NAME + 1;

    /**
     * Security column.
     */
    private static final int COL_SECURITY = COL_PORTFOLIO + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_SECURITY + 1;

    /**
     * GrantDate column.
     */
    private static final int COL_GRANT = COL_DESC + 1;

    /**
     * ExpireDate column.
     */
    private static final int COL_EXPIRE = COL_GRANT + 1;

    /**
     * Price column.
     */
    private static final int COL_PRICE = COL_EXPIRE + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_PRICE + 1;

    /**
     * StockOption data list.
     */
    private final StockOptionList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetStockOption(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_STOCKOPTIONS);

        /* Access the Loans list */
        theList = pReader.getData().getStockOptions();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetStockOption(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_STOCKOPTIONS);

        /* Access the Loans list */
        theList = pWriter.getData().getStockOptions();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(StockOption.OBJECT_NAME);
        myValues.addValue(StockOption.FIELD_PORTFOLIO, loadInteger(COL_PORTFOLIO));
        myValues.addValue(StockOption.FIELD_SECURITY, loadInteger(COL_SECURITY));
        myValues.addValue(StockOption.FIELD_GRANTDATE, loadDate(COL_GRANT));
        myValues.addValue(StockOption.FIELD_EXPIREDATE, loadDate(COL_EXPIRE));
        myValues.addValue(StockOption.FIELD_PRICE, loadBytes(COL_PRICE));
        myValues.addValue(StockOption.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(StockOption.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(StockOption.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final StockOption pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_PORTFOLIO, pItem.getPortfolioId());
        writeInteger(COL_SECURITY, pItem.getSecurityId());
        writeDate(COL_GRANT, pItem.getGrantDate());
        writeDate(COL_EXPIRE, pItem.getExpiryDate());
        writeBytes(COL_PRICE, pItem.getPriceBytes());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();
    }
}
