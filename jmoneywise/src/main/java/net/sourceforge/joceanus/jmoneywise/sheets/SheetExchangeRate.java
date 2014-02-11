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
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate;
import net.sourceforge.joceanus.jmoneywise.data.ExchangeRate.ExchangeRateList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetDataItem;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for ExchangeRate.
 * @author Tony Washer
 */
public class SheetExchangeRate
        extends SheetDataItem<ExchangeRate, MoneyWiseDataType> {
    /**
     * NamedArea for Rates.
     */
    protected static final String AREA_XCHGRATES = ExchangeRate.LIST_NAME;

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
    protected SheetExchangeRate(final MoneyWiseReader pReader) {
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
    protected SheetExchangeRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_XCHGRATES);

        /* Access the Rates list */
        theList = pWriter.getData().getExchangeRates();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(ExchangeRate.OBJECT_NAME);
        myValues.addValue(ExchangeRate.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(ExchangeRate.FIELD_FROM, loadInteger(COL_FROM));
        myValues.addValue(ExchangeRate.FIELD_TO, loadInteger(COL_TO));
        myValues.addValue(ExchangeRate.FIELD_RATE, loadRatio(COL_RATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadOpenValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(ExchangeRate.OBJECT_NAME);
        myValues.addValue(ExchangeRate.FIELD_DATE, loadDate(COL_DATE));
        myValues.addValue(ExchangeRate.FIELD_FROM, loadString(COL_FROM));
        myValues.addValue(ExchangeRate.FIELD_TO, loadString(COL_TO));
        myValues.addValue(ExchangeRate.FIELD_RATE, loadRatio(COL_RATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final ExchangeRate pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_FROM, pItem.getFromCurrencyId());
        writeInteger(COL_TO, pItem.getToCurrencyId());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_RATE, pItem.getExchangeRate());
    }

    @Override
    protected void insertOpenItem(final ExchangeRate pItem) throws JOceanusException {
        /* Set the fields */
        super.insertOpenItem(pItem);
        writeString(COL_FROM, pItem.getFromCurrencyName());
        writeString(COL_TO, pItem.getToCurrencyName());
        writeDate(COL_DATE, pItem.getDate());
        writeDecimal(COL_RATE, pItem.getExchangeRate());
    }

    @Override
    protected void prepareSheet() throws JOceanusException {
        /* Write titles */
        writeHeader(COL_FROM, ExchangeRate.FIELD_FROM.getName());
        writeHeader(COL_TO, ExchangeRate.FIELD_TO.getName());
        writeHeader(COL_DATE, ExchangeRate.FIELD_DATE.getName());
        writeHeader(COL_RATE, ExchangeRate.FIELD_RATE.getName());
    }

    @Override
    protected void formatSheet() throws JOceanusException {
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
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();

        /* Touch underlying items */
        theList.touchUnderlyingItems();

        /* Validate the exchange Rates */
        theList.validateOnLoad();
    }
}
