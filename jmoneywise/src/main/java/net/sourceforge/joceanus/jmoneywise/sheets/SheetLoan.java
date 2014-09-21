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

import net.sourceforge.joceanus.jmetis.sheet.DataCell;
import net.sourceforge.joceanus.jmetis.sheet.DataRow;
import net.sourceforge.joceanus.jmetis.sheet.DataView;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Loan;
import net.sourceforge.joceanus.jmoneywise.data.Loan.LoanList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Loan.
 * @author Tony Washer
 */
public class SheetLoan
        extends SheetEncrypted<Loan, MoneyWiseDataType> {
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
        MoneyWiseData myData = pReader.getData();
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
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getLoans());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Loan.OBJECT_NAME);
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
    protected void insertSecureItem(final Loan pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_CATEGORY, pItem.getCategoryId());
        writeInteger(COL_PARENT, pItem.getParentId());
        writeInteger(COL_CURRENCY, pItem.getLoanCurrencyId());
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
     * @throws JOceanusException on error
     */
    protected static void processLoan(final ArchiveLoader pLoader,
                                      final MoneyWiseData pData,
                                      final DataView pView,
                                      final DataRow pRow) throws JOceanusException {
        /* Access name and type */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();
        String myType = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Skip class, taxFree and gross */
        iAdjust++;
        iAdjust++;
        iAdjust++;

        /* Handle closed which may be missing */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Access Parent account */
        String myParent = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Loan.OBJECT_NAME);
        myValues.addValue(Loan.FIELD_NAME, myName);
        myValues.addValue(Loan.FIELD_CATEGORY, myType);
        myValues.addValue(Loan.FIELD_CURRENCY, pData.getDefaultCurrency());
        myValues.addValue(Loan.FIELD_PARENT, myParent);
        myValues.addValue(Loan.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        LoanList myList = pData.getLoans();
        Loan myLoan = myList.addValuesItem(myValues);

        /* Declare the loan */
        pLoader.declareAsset(myLoan);
    }
}
