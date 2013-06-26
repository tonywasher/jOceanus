/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetDataItem;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JRatio;
import net.sourceforge.jOceanus.jMoneyWise.data.ExchangeRate;
import net.sourceforge.jOceanus.jMoneyWise.data.ExchangeRate.ExchangeRateList;

/**
 * SheetDataItem extension for ExchangeRate.
 * @author Tony Washer
 */
public class SheetExchangeRate
        extends SheetDataItem<ExchangeRate> {
    /**
     * NamedArea for Rates.
     */
    protected static final String AREA_XCHGRATES = "ExchangeRates";

    /**
     * Date column.
     */
    private static final int COL_DATE = COL_ID + 1;

    /**
     * From column.
     */
    private static final int COL_FROM = COL_DATE + 1;

    /**
     * To column.
     */
    private static final int COL_TO = COL_FROM + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_TO + 1;

    /**
     * ExchangeRate list.
     */
    private final ExchangeRateList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetExchangeRate(final FinanceReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_XCHGRATES);

        /* Access the Rates list */
        theList = pReader.getData().getExchangeRates();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetExchangeRate(final FinanceWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_XCHGRATES);

        /* Access the Rates list */
        theList = pWriter.getData().getExchangeRates();
        setDataList(theList);
    }

    @Override
    protected void loadSecureItem(final Integer pId) throws JDataException {
        /* Access the IDs */
        Integer myFromId = loadInteger(COL_FROM);
        Integer myToId = loadInteger(COL_TO);

        /* Access the Date and Rate */
        JDateDay myDate = loadDate(COL_DATE);
        JRatio myRate = loadRatio(COL_RATE);

        /* Load the item */
        theList.addSecureItem(pId, myDate, myFromId, myToId, myRate);
    }

    @Override
    protected void loadOpenItem(final Integer pId) throws JDataException {
        /* Access the links */
        String myFrom = loadString(COL_FROM);
        String myTo = loadString(COL_TO);

        /* Access the date and rate */
        JDateDay myDate = loadDate(COL_DATE);
        JRatio myRate = loadRatio(COL_RATE);

        /* Load the item */
        theList.addOpenItem(pId, myDate, myFrom, myTo, myRate);
    }

    @Override
    protected void insertSecureItem(final ExchangeRate pItem) throws JDataException {
        /* Set the fields */
        writeInteger(COL_FROM, pItem.getFromCurrencyId());
        writeInteger(COL_TO, pItem.getToCurrencyId());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_RATE, pItem.getExchangeRate());
    }

    @Override
    protected void insertOpenItem(final ExchangeRate pItem) throws JDataException {
        /* Set the fields */
        writeString(COL_FROM, pItem.getFromCurrencyName());
        writeString(COL_TO, pItem.getToCurrencyName());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_RATE, pItem.getExchangeRate());
    }

    @Override
    protected void prepareSheet() throws JDataException {
        /* Write titles */
        writeHeader(COL_FROM, ExchangeRate.FIELD_FROM.getName());
        writeHeader(COL_TO, ExchangeRate.FIELD_TO.getName());
        writeHeader(COL_DATE, ExchangeRate.FIELD_DATE.getName());
        writeHeader(COL_RATE, ExchangeRate.FIELD_RATE.getName());
    }

    @Override
    protected void formatSheet() throws JDataException {
        /* Set the column types */
        setDateColumn(COL_DATE);
        setStringColumn(COL_FROM);
        setStringColumn(COL_TO);
        setRatioColumn(COL_RATE);

        /* Set validation */
        applyDataValidation(COL_FROM, SheetAccountCurrency.AREA_ACCOUNTCURRNAMES);
        applyDataValidation(COL_TO, SheetAccountCurrency.AREA_ACCOUNTCURRNAMES);
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_RATE;
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the account categories */
        theList.validateOnLoad();
    }
}
