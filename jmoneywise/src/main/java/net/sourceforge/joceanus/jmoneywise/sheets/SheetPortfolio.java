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
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.SheetEncrypted;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * SheetDataItem extension for Portfolio.
 * @author Tony Washer
 */
public class SheetPortfolio
        extends SheetEncrypted<Portfolio, MoneyWiseDataType> {
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
     * Parent column.
     */
    private static final int COL_PARENT = COL_DESC + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_PARENT + 1;

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
        MoneyWiseData myData = pReader.getData();
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
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getPortfolios());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_PARENT, loadInteger(COL_PARENT));
        myValues.addValue(Portfolio.FIELD_NAME, loadBytes(COL_NAME));
        myValues.addValue(Portfolio.FIELD_DESC, loadBytes(COL_DESC));
        myValues.addValue(Portfolio.FIELD_TAXFREE, loadBoolean(COL_TAXFREE));
        myValues.addValue(Portfolio.FIELD_CLOSED, loadBoolean(COL_CLOSED));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final Portfolio pItem) throws JOceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_PARENT, pItem.getParentId());
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
     * @throws JOceanusException on error
     */
    protected static void processPortfolio(final ArchiveLoader pLoader,
                                           final MoneyWiseData pData,
                                           final DataView pView,
                                           final DataRow pRow) throws JOceanusException {
        /* Access name */
        int iAdjust = 0;
        String myName = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Skip type and class */
        iAdjust++;
        iAdjust++;

        /* Handle taxFree which may be missing */
        DataCell myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isTaxFree = Boolean.FALSE;
        if (myCell != null) {
            isTaxFree = myCell.getBooleanValue();
        }

        /* Skip gross column */
        iAdjust++;

        /* Handle closed which may be missing */
        myCell = pView.getRowCellByIndex(pRow, iAdjust++);
        Boolean isClosed = Boolean.FALSE;
        if (myCell != null) {
            isClosed = myCell.getBooleanValue();
        }

        /* Access Parent account */
        String myParent = pView.getRowCellByIndex(pRow, iAdjust++).getStringValue();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_NAME, myName);
        myValues.addValue(Portfolio.FIELD_PARENT, myParent);
        myValues.addValue(Portfolio.FIELD_TAXFREE, isTaxFree);
        myValues.addValue(Portfolio.FIELD_CLOSED, isClosed);

        /* Add the value into the list */
        PortfolioList myList = pData.getPortfolios();
        Portfolio myPortfolio = myList.addValuesItem(myValues);

        /* Declare the portfolio */
        pLoader.declareAsset(myPortfolio);
    }
}
