/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.sheets;

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDepositInfo.MoneyWiseDepositInfoList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;

/**
 * SheetDataItem extension for Deposit.
 * @author Tony Washer
 */
public class MoneyWiseSheetDeposit
        extends PrometheusSheetEncrypted<MoneyWiseDeposit> {
    /**
     * NamedArea for Deposits.
     */
    private static final String AREA_DEPOSITS = MoneyWiseDeposit.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Category column.
     */
    private static final int COL_CATEGORY = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_CATEGORY + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_PARENT + 1;

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
    protected MoneyWiseSheetDeposit(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_DEPOSITS);

        /* Access the Deposits list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getDeposits());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetDeposit(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_DEPOSITS);

        /* Access the Deposits list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getDeposits());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseDeposit.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, loadInteger(COL_CATEGORY));
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseDeposit pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_PARENT, pItem.getParentId());
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
     * Process deposit row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processDeposit(final MoneyWiseArchiveLoader pLoader,
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

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip alias and portfolio columns */
        ++iAdjust;
        ++iAdjust;

        /* Handle maturity which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        OceanusDate myMaturity = null;
        if (myCell != null) {
            myMaturity = myCell.getDate();
        }

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

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseDeposit.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myParent);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseDepositList myList = pData.getDeposits();
        final MoneyWiseDeposit myDeposit = myList.addValuesItem(myValues);

        /* Add information relating to the deposit */
        final MoneyWiseDepositInfoList myInfoList = pData.getDepositInfo();
        myInfoList.addInfoItem(null, myDeposit, MoneyWiseAccountInfoClass.MATURITY, myMaturity);
        myInfoList.addInfoItem(null, myDeposit, MoneyWiseAccountInfoClass.OPENINGBALANCE, myBalance);

        /* Declare the deposit */
        pLoader.declareAsset(myDeposit);
    }
}
