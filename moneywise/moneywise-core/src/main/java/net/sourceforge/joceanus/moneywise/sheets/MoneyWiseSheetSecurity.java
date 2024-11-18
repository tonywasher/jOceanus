/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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

import net.sourceforge.joceanus.moneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity.MoneyWiseSecurityList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityInfo.MoneyWiseSecurityInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.jprometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Security.
 * @author Tony Washer
 */
public class MoneyWiseSheetSecurity
        extends PrometheusSheetEncrypted<MoneyWiseSecurity> {
    /**
     * NamedArea for Securities.
     */
    private static final String AREA_SECURITIES = MoneyWiseSecurity.LIST_NAME;

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
    protected MoneyWiseSheetSecurity(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_SECURITIES);

        /* Access the Securities list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getSecurities());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetSecurity(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_SECURITIES);

        /* Access the Securities list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getSecurities());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseSecurity.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, loadInteger(COL_TYPE));
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, loadInteger(COL_CURRENCY));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWiseSecurity pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryId());
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
    protected static void processSecurity(final MoneyWiseArchiveLoader pLoader,
                                          final MoneyWiseDataSet pData,
                                          final PrometheusSheetView pView,
                                          final PrometheusSheetRow pRow) throws OceanusException {
        /* Access name and type */
        int iAdjust = -1;
        final String myName = pView.getRowCellByIndex(pRow, ++iAdjust).getString();
        final String myType = pView.getRowCellByIndex(pRow, ++iAdjust).getString();

        /* Look for separator in category */
        final int iIndex = myType.indexOf(MoneyWiseTransCategory.STR_SEP);
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
        final MoneyWiseSecurityList myList = pData.getSecurities();

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
        MoneyWiseCurrency myCurrency = pData.getReportingCurrency();
        if (myCell != null) {
            final String myCurrName = myCell.getString();
            myCurrency = pData.getAccountCurrencies().findItemByName(myCurrName);
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWiseSecurity.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, mySecType);
        myValues.addValue(MoneyWiseStaticDataType.CURRENCY, myCurrency);
        myValues.addValue(MoneyWiseBasicResource.ASSET_PARENT, myParent);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWiseSecurity mySecurity = myList.addValuesItem(myValues);

        /* Add information relating to the security */
        final MoneyWiseSecurityInfoList myInfoList = pData.getSecurityInfo();
        myInfoList.addInfoItem(null, mySecurity, MoneyWiseAccountInfoClass.SYMBOL, mySymbol);
        myInfoList.addInfoItem(null, mySecurity, MoneyWiseAccountInfoClass.REGION, myRegion);

        /* Declare the security holding */
        pLoader.declareSecurityHolding(mySecurity, myPortfolio);
    }
}
