/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Loan.
 * @author Tony Washer
 */
public class SheetLoan
        extends PrometheusSheetEncrypted<Loan, MoneyWiseDataType> {
    /**
     * NamedArea for Loans.
     */
    private static final String AREA_LOANS = Loan.LIST_NAME;

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
    protected SheetLoan(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_LOANS);

        /* Access the Loans list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getLoans());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetLoan(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_LOANS);

        /* Access the Loans list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getLoans());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Loan.OBJECT_NAME);
        myValues.addValue(Loan.FIELD_CATEGORY, loadInteger(COL_CATEGORY));
        myValues.addValue(Loan.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Loan.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Loan.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Loan.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Loan.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Loan pItem) throws OceanusException {
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
     * Process loan row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processLoan(final ArchiveLoader pLoader,
                                      final MoneyWiseData pData,
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

        /* Skip alias, portfolio, maturity, openingBalance, symbol and region columns */
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
        ++iAdjust;
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
        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(Loan.OBJECT_NAME);
        myValues.addValue(Loan.FIELD_NAME, myName);
        myValues.addValue(Loan.FIELD_CATEGORY, myType);
        myValues.addValue(Loan.FIELD_CURRENCY, myCurrency);
        myValues.addValue(Loan.FIELD_PARENT, myParent);
        myValues.addValue(Loan.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        final LoanList myList = pData.getLoans();
        final Loan myLoan = myList.addValuesItem(myValues);

        /* Declare the loan */
        pLoader.declareAsset(myLoan);
    }
}
