/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.sheets;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCash;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCash.MoneyWiseCashList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Cash.
 * @author Tony Washer
 */
public class MoneyWiseSheetCash
        extends PrometheusSheetEncrypted<MoneyWiseCash> {
    /**
     * NamedArea for Cash.
     */
    private static final String AREA_CASH = MoneyWiseCash.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_NAME + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_CATEGORY + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_DESC + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_CURRENCY + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetCash(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_CASH);

        /* Access the Cash list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getCash());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetCash(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_CASH);

        /* Access the Cash list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getCash());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseCash.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, loadInteger(COL_CATEGORY));
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseCash pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_CURRENCY, pItem.getAssetCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    /**
     * Process cash row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processCash(final MoneyWiseArchiveLoader pLoader,
                                      final MoneyWiseDataSet pData,
                                      final PrometheusSheetView pView,
                                      final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Skip parent, alias, portfolio, and maturity columns */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;

        /* Handle opening balance which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myBalance = null;
        if (myCell != null) {
            myBalance = myCell.getString();
        }

        /* Skip symbol and region columns */
        ++iAdjust;
        ++iAdjust;

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        MoneyWiseCurrency myCurrency = pData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = pData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Handle autoExpense which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myAutoExpense = null;
        String myAutoPayee = null;
        if (myCell != null) {
            myAutoExpense = myCell.getString();
            myAutoPayee = myName + "Expense";
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseCash.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseCashList myList = pData.getCash();
        final MoneyWiseCash myCash = myList.addValuesItem(myValues);

        /* Add information relating to the cash */
        final MoneyWiseCashInfoList myInfoList = pData.getCashInfo();
        myInfoList.addInfoItem(null, myCash, MoneyWiseAccountInfoClass.AUTOEXPENSE, myAutoExpense);
        myInfoList.addInfoItem(null, myCash, MoneyWiseAccountInfoClass.AUTOPAYEE, myAutoPayee);
        myInfoList.addInfoItem(null, myCash, MoneyWiseAccountInfoClass.OPENINGBALANCE, myBalance);

        /* Declare the cash */
        pLoader.declareAsset(myCash);
    }

    /**
     * Process cashPayee row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processCashPayee(final MoneyWiseArchiveLoader pLoader,
                                           final MoneyWiseDataSet pData,
                                           final PrometheusSheetView pView,
                                           final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip type, class */
        ++iAdjust;
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /*
         * Skip parent, alias, portfolio, maturity, openingBalance, symbol, region and currency
         * columns
         */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;

        /* Handle autoExpense which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        if (myCell != null) {
            final String myAutoPayee = myName + "Expense";

            /* Build values */
            final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWisePayee.OBJECT_NAME);
            myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myAutoPayee);
            myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, MoneyWisePayeeClass.PAYEE.toString());
            myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

            /* Add the value into the list */
            final MoneyWisePayeeList myPayeeList = pData.getPayees();
            myPayeeList.addValuesItem(myValues);
        }
    }
}
