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
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
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
     * NameList for Portfolios.
     */
    protected static final String AREA_PORTFOLIONAMES = Portfolio.OBJECT_NAME + "Names";

    /**
     * Name column.
     */
    private static final int COL_NAME = COL_CONTROLID + 1;

    /**
     * Description column.
     */
    private static final int COL_DESC = COL_NAME + 1;

    /**
     * Holding column.
     */
    private static final int COL_HOLDING = COL_DESC + 1;

    /**
     * TaxFree column.
     */
    private static final int COL_TAXFREE = COL_HOLDING + 1;

    /**
     * Closed column.
     */
    private static final int COL_CLOSED = COL_TAXFREE + 1;

    /**
     * Portfolio data list.
     */
    private final PortfolioList theList;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetPortfolio(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_PORTFOLIOS);

        /* Access the Portfolios list */
        theList = pReader.getData().getPortfolios();
        setDataList(theList);
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetPortfolio(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_PORTFOLIOS);

        /* Access the Portfolios list */
        theList = pWriter.getData().getPortfolios();
        setDataList(theList);
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws JOceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(Portfolio.OBJECT_NAME);
        myValues.addValue(Portfolio.FIELD_HOLDING, loadInteger(COL_HOLDING));
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
        writeInteger(COL_HOLDING, pItem.getHoldingId());
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

    @Override
    protected void postProcessOnLoad() throws JOceanusException {
        /* Resolve links and reSort */
        theList.resolveDataSetLinks();
        theList.reSort();
    }

    /**
     * Load the Portfolios from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @return continue to load <code>true/false</code>
     * @throws JOceanusException on error
     */
    protected static boolean loadArchive(final TaskControl<MoneyWiseData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final MoneyWiseData pData) throws JOceanusException {
        /* Access the list of portfolios */
        PortfolioList myList = pData.getPortfolios();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            DataView myView = pWorkBook.getRangeView(AREA_PORTFOLIOS);

            /* Access the number of reporting steps */
            int mySteps = pTask.getReportingSteps();
            int myCount = 0;

            /* Declare the new stage */
            if (!pTask.setNewStage(Portfolio.LIST_NAME)) {
                return false;
            }

            /* Count the number of portfolios */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            if (!pTask.setNumSteps(myTotal)) {
                return false;
            }

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                DataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access name */
                DataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myName = myCell.getStringValue();

                /* Access holding name */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myHolding = myCell.getStringValue();

                /* Access TaxFree Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isTaxFree = myCell.getBooleanValue();

                /* Access Closed Flag */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                Boolean isClosed = myCell.getBooleanValue();

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<MoneyWiseDataType>(Portfolio.OBJECT_NAME);
                myValues.addValue(Portfolio.FIELD_NAME, myName);
                myValues.addValue(Portfolio.FIELD_HOLDING, myHolding);
                myValues.addValue(Portfolio.FIELD_TAXFREE, isTaxFree);
                myValues.addValue(Portfolio.FIELD_CLOSED, isClosed);

                /* Add the value into the list */
                myList.addValuesItem(myValues);
                /* Add the value into the finance tables */

                /* Report the progress */
                myCount++;
                if (((myCount % mySteps) == 0) && (!pTask.setStepsDone(myCount))) {
                    return false;
                }
            }

            /* Resolve links and reSort */
            myList.resolveDataSetLinks();
            myList.reSort();

            /* Touch underlying items */
            myList.touchUnderlyingItems();

            /* Validate the event categories */
            myList.validateOnLoad();

            /* Handle exceptions */
        } catch (JOceanusException e) {
            throw new JMoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }

        /* Return to caller */
        return true;
    }
}
