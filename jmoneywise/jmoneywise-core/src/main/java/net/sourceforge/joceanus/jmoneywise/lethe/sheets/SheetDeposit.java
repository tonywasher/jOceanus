/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit.DepositList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.DepositInfo.DepositInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * SheetDataItem extension for Deposit.
 * @author Tony Washer
 */
public class SheetDeposit
        extends PrometheusSheetEncrypted<Deposit, MoneyWiseDataType> {
    /**
     * NamedArea for Deposits.
     */
    private static final String AREA_DEPOSITS = Deposit.LIST_NAME;

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
    protected SheetDeposit(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_DEPOSITS);

        /* Access the Deposits list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getDeposits());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDeposit(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_DEPOSITS);

        /* Access the Deposits list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getDeposits());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Deposit.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Deposit.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Deposit.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Deposit.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Deposit.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Deposit pItem) throws OceanusException {
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
    protected static void processDeposit(final ArchiveLoader pLoader,
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

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Skip alias and portfolio columns */
        ++iAdjust;
        ++iAdjust;

        /* Handle maturity which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        TethysDate myMaturity = null;
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
        AssetCurrency myCurrency = pData.getDefaultCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = pData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(Deposit.OBJECT_NAME);
        myValues.addValue(Deposit.FIELD_NAME, myName);
        myValues.addValue(Deposit.FIELD_CATEGORY, myType);
        myValues.addValue(Deposit.FIELD_CURRENCY, myCurrency);
        myValues.addValue(Deposit.FIELD_PARENT, myParent);
        myValues.addValue(Deposit.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        final DepositList myList = pData.getDeposits();
        final Deposit myDeposit = myList.addValuesItem(myValues);

        /* Add information relating to the deposit */
        final DepositInfoList myInfoList = pData.getDepositInfo();
        myInfoList.addInfoItem(null, myDeposit, AccountInfoClass.MATURITY, myMaturity);
        myInfoList.addInfoItem(null, myDeposit, AccountInfoClass.OPENINGBALANCE, myBalance);

        /* Declare the deposit */
        pLoader.declareAsset(myDeposit);
    }
}
