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

import net.sourceforge.joceanus.jmetis.sheet.MetisDataCell;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataRow;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataView;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Cash;
import net.sourceforge.joceanus.jmoneywise.data.Cash.CashList;
import net.sourceforge.joceanus.jmoneywise.data.CashInfo.CashInfoList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.PayeeTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetEncrypted;
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
        MoneyWiseData myData = pReader.getData();
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
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getCash());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Cash.OBJECT_NAME);
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
                                      final MetisDataView pView,
                                      final MetisDataRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        String myType = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Skip class, taxFree and gross */
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle closed which may be missing */
        MetisDataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Skip parent, alias, portfolio, maturity, openingBalance and symbol columns */
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle autoExpense which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        String myAutoExpense = null;
        String myAutoPayee = null;
        if (myCell != null) {
            myAutoExpense = myCell.getStringValue();
            myAutoPayee = myName + "Expense";
        }

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = new DataValues<>(Cash.OBJECT_NAME);
        myValues.addValue(Cash.FIELD_NAME, myName);
        myValues.addValue(Cash.FIELD_CATEGORY, myType);
        myValues.addValue(Cash.FIELD_CURRENCY, pData.getDefaultCurrency());
        myValues.addValue(Cash.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        CashList myList = pData.getCash();
        Cash myCash = myList.addValuesItem(myValues);

        /* Add information relating to the cash */
        CashInfoList myInfoList = pData.getCashInfo();
        myInfoList.addInfoItem(null, myCash, AccountInfoClass.AUTOEXPENSE, myAutoExpense);
        myInfoList.addInfoItem(null, myCash, AccountInfoClass.AUTOPAYEE, myAutoPayee);

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
                                           final MetisDataView pView,
                                           final MetisDataRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Skip type, class, taxFree and gross */
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle closed which may be missing */
        MetisDataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Skip parent, alias, portfolio, maturity, openingBalance and symbol columns */
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle autoExpense which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        if (myCell != null) {
            String myAutoPayee = myName + "Expense";

            /* Build values */
            DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Payee.OBJECT_NAME);
            myValues.addValue(Payee.FIELD_NAME, myAutoPayee);
            myValues.addValue(Payee.FIELD_PAYEETYPE, PayeeTypeClass.PAYEE.toString());
            myValues.addValue(Payee.FIELD_CLOSED, isClosed);

            /* Add the value into the list */
            PayeeList myPayeeList = pData.getPayees();
            myPayeeList.addValuesItem(myValues);
        }
    }
}
