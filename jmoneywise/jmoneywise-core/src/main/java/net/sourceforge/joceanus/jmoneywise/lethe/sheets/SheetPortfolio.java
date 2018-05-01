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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Portfolio.
 * @author Tony Washer
 */
public class SheetPortfolio
        extends PrometheusSheetEncrypted<Portfolio, MoneyWiseDataType> {
    /**
     * NamedArea for Portfolios.
     */
    private static final String AREA_PORTFOLIOS = Portfolio.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_NAME + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_DESC + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

    /**
     * Currency column.
     */
    private static final int COL_CURRENCY = COL_PARENT + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_CURRENCY + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_TAXFREE + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPortfolio(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PORTFOLIOS);

        /* Access the Portfolios list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getPortfolios());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPortfolio(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PORTFOLIOS);

        /* Access the Portfolios list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getPortfolios());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_PORTTYPE, loadInteger(COL_TYPE));
        myValues.addValue(Portfolio.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Portfolio.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Portfolio.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Portfolio.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Portfolio.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Portfolio pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getPortfolioTypeId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getAssetCurrencyId());
        writeBytes(COL_NAME, pItem.getNameBytes());
        writeBytes(COL_DESC, pItem.getDescBytes());
        writeBoolean(COL_TAXFREE, pItem.isTaxFree());
        writeBoolean(COL_CLOSED, pItem.isClosed());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_CLOSED;
    }

    /**
     * Process portfolio row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processPortfolio(final ArchiveLoader pLoader,
                                           final MoneyWiseData pData,
                                           final MetisSheetView pView,
                                           final MetisSheetRow pRow) throws OceanusException {
        /* Access name */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

        /* Access portfolio type */
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

        /* Look for separator in category */
        final int iIndex = myType.indexOf(TransactionCategory.STR_SEP);
        if (iIndex == -1) {
            throw new MoneyWiseLogicException("Unexpected Portfolio Class " + myType);
        }

        /* Access subCategory as portfolio type */
        final String myPortType = myType.substring(iIndex + 1);

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        MetisSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getStringValue();

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
            final String myCurrName = myCell.getStringValue();
            myCurrency = pData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_NAME, myName);
        myValues.addValue(Portfolio.FIELD_PORTTYPE, myPortType);
        myValues.addValue(Portfolio.FIELD_PARENT, myParent);
        myValues.addValue(Portfolio.FIELD_CURRENCY, myCurrency);
        myValues.addValue(Portfolio.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        final PortfolioList myList = pData.getPortfolios();
        final Portfolio myPortfolio = myList.addValuesItem(myValues);

        /* Declare the portfolio */
        pLoader.declareAsset(myPortfolio);
    }
}
