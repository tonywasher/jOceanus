/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
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

import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetCell;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetRow;
import net.sourceforge.joceanus.jmetis.service.sheet.MetisSheetView;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Cash.
 * @author Tony Washer
 */
public class SheetCash
        extends PrometheusSheetEncrypted<Cash, MoneyWiseDataType> {
    /**
     * NamedArea for Cash.
     */
    private static final String AREA_CASH = Cash.LIST_NAME;

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
    protected SheetCash(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_CASH);

        /* Access the Cash list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getCash());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetCash(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_CASH);

        /* Access the Cash list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getCash());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Cash.OBJECT_NAME);
        myValues.addValue(Cash.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Cash.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Cash.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Cash.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Cash.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Cash pItem) throws OceanusException {
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
    protected static void processCash(final ArchiveLoader pLoader,
                                      final MoneyWiseData pData,
                                      final MetisSheetView pView,
                                      final MetisSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        MetisSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
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
        AssetCurrency myCurrency = pData.getDefaultCurrency();
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
        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(Cash.OBJECT_NAME);
        myValues.addValue(Cash.FIELD_NAME, myName);
        myValues.addValue(Cash.FIELD_CATEGORY, myType);
        myValues.addValue(Cash.FIELD_CURRENCY, myCurrency);
        myValues.addValue(Cash.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        final CashList myList = pData.getCash();
        final Cash myCash = myList.addValuesItem(myValues);

        /* Add information relating to the cash */
        final CashInfoList myInfoList = pData.getCashInfo();
        myInfoList.addInfoItem(null, myCash, AccountInfoClass.AUTOEXPENSE, myAutoExpense);
        myInfoList.addInfoItem(null, myCash, AccountInfoClass.AUTOPAYEE, myAutoPayee);
        myInfoList.addInfoItem(null, myCash, AccountInfoClass.OPENINGBALANCE, myBalance);

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
    protected static void processCashPayee(final ArchiveLoader pLoader,
                                           final MoneyWiseData pData,
                                           final MetisSheetView pView,
                                           final MetisSheetRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip type, class */
        ++iAdjust;
        ++iAdjust;

        /* Handle closed which may be missing */
        MetisSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
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
            final DataValues<MoneyWiseDataType> myValues = new DataValues<>(Payee.OBJECT_NAME);
            myValues.addValue(Payee.FIELD_NAME, myAutoPayee);
            myValues.addValue(Payee.FIELD_PAYEETYPE, PayeeTypeClass.PAYEE.toString());
            myValues.addValue(Payee.FIELD_CLOSED, isClosed);

            /* Add the value into the list */
            final PayeeList myPayeeList = pData.getPayees();
            myPayeeList.addValuesItem(myValues);
        }
    }
}
