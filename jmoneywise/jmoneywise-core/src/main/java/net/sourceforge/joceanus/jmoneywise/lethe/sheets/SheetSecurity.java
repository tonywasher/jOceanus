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
import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityInfo.SecurityInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Security.
 * @author Tony Washer
 */
public class SheetSecurity
        extends PrometheusSheetEncrypted<Security, MoneyWiseDataType> {
    /**
     * NamedArea for Securities.
     */
    private static final String AREA_SECURITIES = Security.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Parent column.
     */
    private static final int COL_PARENT = COL_TYPE + 1;

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
    protected SheetSecurity(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_SECURITIES);

        /* Access the Securities list */
        final MoneyWiseData myData = pReader.getData();
        setDataList(myData.getSecurities());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetSecurity(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_SECURITIES);

        /* Access the Securities list */
        final MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getSecurities());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(Security.OBJECT_NAME);
        myValues.addValue(Security.FIELD_SECTYPE, loadInteger(COL_TYPE));
        myValues.addValue(Security.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Security.FIELD_CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(Security.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Security.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Security.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Security pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getSecurityTypeId());
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
     * Process security row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processSecurity(final ArchiveLoader pLoader,
                                          final MoneyWiseData pData,
                                          final PrometheusSheetView pView,
                                          final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Look for separator in category */
        final int iIndex = myType.indexOf(TransactionCategory.STR_SEP);
        if (iIndex == -1) {
            throw new MoneyWiseLogicException("Unexpected Security Class " + myType);
        }

        /* Access subCategory as security type */
        final String mySecType = myType.substring(iIndex + 1);

        /* Skip class */
        ++iAdjust;

        /* Handle closed which may be missing */
        PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Access the list */
        final SecurityList myList = pData.getSecurities();

        /* Access Parent account */
        final String myParent = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Access the alias account */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myAlias = null;
        if (myCell != null) {
            myAlias = myCell.getString();
        }

        /* Access Portfolio */
        final String myPortfolio = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* If we have an alias */
        if (myAlias != null) {
            /* Declare the security alias */
            pLoader.declareAliasHolding(myName, myAlias, myPortfolio);

            /* return */
            return;
        }

        /* Skip maturity and opening columns */
        ++iAdjust;
        ++iAdjust;

        /* Access Symbol which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String mySymbol = null;
        if (myCell != null) {
            mySymbol = myCell.getString();
        }

        /* Handle region which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        String myRegion = null;
        if (myCell != null) {
            myRegion = myCell.getString();
        }

        /* Handle currency which may be missing */
        myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        AssetCurrency myCurrency = pData.getDefaultCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = pData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = new DataValues<>(Security.OBJECT_NAME);
        myValues.addValue(Security.FIELD_NAME, myName);
        myValues.addValue(Security.FIELD_SECTYPE, mySecType);
        myValues.addValue(Security.FIELD_CURRENCY, myCurrency);
        myValues.addValue(Security.FIELD_PARENT, myParent);
        myValues.addValue(Security.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        final Security mySecurity = myList.addValuesItem(myValues);

        /* Add information relating to the security */
        final SecurityInfoList myInfoList = pData.getSecurityInfo();
        myInfoList.addInfoItem(null, mySecurity, AccountInfoClass.SYMBOL, mySymbol);
        myInfoList.addInfoItem(null, mySecurity, AccountInfoClass.REGION, myRegion);

        /* Declare the security holding */
        pLoader.declareSecurityHolding(mySecurity, myPortfolio);
    }
}
