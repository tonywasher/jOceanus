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
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Payee;
import net.sourceforge.joceanus.jmoneywise.data.Payee.PayeeList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * SheetDataItem extension for Payee.
 * @author Tony Washer
 */
public class SheetPayee
        extends SheetEncrypted<Payee, MoneyWiseDataType> {
    /**
     * NamedArea for Payees.
     */
    private static final String AREA_PAYEES = Payee.LIST_NAME;

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
    protected SheetPayee(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PAYEES);

        /* Access the Payees list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getPayees());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPayee(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PAYEES);

        /* Access the Payees list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getPayees());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Payee.OBJECT_NAME);
        myValues.addValue(Payee.FIELD_PAYEETYPE, loadInteger(COL_TYPE));
        myValues.addValue(Payee.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Payee.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Payee.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Payee pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_TYPE, pItem.getPayeeTypeId());
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
    protected static void processPayee(final ArchiveLoader pLoader,
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

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Payee.OBJECT_NAME);
        myValues.addValue(Payee.FIELD_NAME, myName);
        myValues.addValue(Payee.FIELD_PAYEETYPE, myType);
        myValues.addValue(Payee.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        PayeeList myList = pData.getPayees();
        Payee myPayee = myList.addValuesItem(myValues);

        /* Declare the payee */
        pLoader.declareAsset(myPayee);
    }
}
