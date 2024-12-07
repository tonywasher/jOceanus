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

import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataValues;
import net.sourceforge.joceanus.prometheus.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetCell;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetRow;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetView;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * SheetDataItem extension for Payee.
 * @author Tony Washer
 */
public class MoneyWiseSheetPayee
        extends PrometheusSheetEncrypted<MoneyWisePayee> {
    /**
     * NamedArea for Payees.
     */
    private static final String AREA_PAYEES = MoneyWisePayee.LIST_NAME;

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_KEYSETID + 1;

    /**
     * Type column.
     */
    private static final int COL_TYPE = COL_NAME + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_TYPE + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_DESC + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected MoneyWiseSheetPayee(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PAYEES);

        /* Access the Payees list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pReader.getData();
        setDataList(myData.getPayees());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected MoneyWiseSheetPayee(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PAYEES);

        /* Access the Payees list */
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pWriter.getData();
        setDataList(myData.getPayees());
    }

    @Override
    protected PrometheusDataValues loadSecureValues() throws OceanusException {
        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWisePayee.OBJECT_NAME);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, loadInteger(COL_TYPE));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final MoneyWisePayee pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getCategoryId());
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
     * Process payee row from archive.
     * @param pLoader the archive loader
     * @param pData the DataSet
     * @param pView the spreadsheet view
     * @param pRow the spreadsheet row
     * @throws OceanusException on error
     */
    protected static void processPayee(final MoneyWiseArchiveLoader pLoader,
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
        final PrometheusSheetCell myCell = pView.getRowCellByIndex(pRow, ++iAdjust);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBoolean();
        }

        /* Build data values */
        final PrometheusDataValues myValues = new PrometheusDataValues(MoneyWisePayee.OBJECT_NAME);
        myValues.addValue(PrometheusDataResource.DATAITEM_FIELD_NAME, myName);
        myValues.addValue(MoneyWiseBasicResource.CATEGORY_NAME, myType);
        myValues.addValue(MoneyWiseBasicResource.ASSET_CLOSED, isClosed);

        /* Add the value into the list */
        final MoneyWisePayeeList myList = pData.getPayees();
        final MoneyWisePayee myPayee = myList.addValuesItem(myValues);

        /* Declare the payee */
        pLoader.declareAsset(myPayee);
    }
}
