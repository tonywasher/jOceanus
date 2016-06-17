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
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadCancelException;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate;
import net.sourceforge.joceanus.jmoneywise.data.DepositRate.DepositRateList;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSheetEncrypted;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;

/**
 * SheetDataItem extension for AccountRate.
 * @author Tony Washer
 */
public class SheetDepositRate
        extends PrometheusSheetEncrypted<DepositRate, MoneyWiseDataType> {
    /**
     * NamedArea for Rates.
     */
    private static final String AREA_RATES = DepositRate.LIST_NAME;

    /**
     * Deposit column.
     */
    private static final int COL_DEPOSIT = COL_KEYSETID + 1;

    /**
     * Rate column.
     */
    private static final int COL_RATE = COL_DEPOSIT + 1;

    /**
     * Bonus column.
     */
    private static final int COL_BONUS = COL_RATE + 1;

    /**
     * EndDate column.
     */
    private static final int COL_ENDDATE = COL_BONUS + 1;

    /**
     * Constructor for loading a spreadsheet.
     * @param pReader the spreadsheet reader
     */
    protected SheetDepositRate(final MoneyWiseReader pReader) {
        /* Call super constructor */
        super(pReader, AREA_RATES);

        /* Access the Rates list */
        MoneyWiseData myData = pReader.getData();
        setDataList(myData.getDepositRates());
    }

    /**
     * Constructor for creating a spreadsheet.
     * @param pWriter the spreadsheet writer
     */
    protected SheetDepositRate(final MoneyWiseWriter pWriter) {
        /* Call super constructor */
        super(pWriter, AREA_RATES);

        /* Access the Rates list */
        MoneyWiseData myData = pWriter.getData();
        setDataList(myData.getDepositRates());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadSecureValues() throws OceanusException {
        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(DepositRate.OBJECT_NAME);
        myValues.addValue(DepositRate.FIELD_DEPOSIT, loadInteger(COL_DEPOSIT));
        myValues.addValue(DepositRate.FIELD_RATE, loadBytes(COL_RATE));
        myValues.addValue(DepositRate.FIELD_BONUS, loadBytes(COL_BONUS));
        myValues.addValue(DepositRate.FIELD_ENDDATE, loadDate(COL_ENDDATE));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void insertSecureItem(final DepositRate pItem) throws OceanusException {
        /* Set the fields */
        super.insertSecureItem(pItem);
        writeInteger(COL_DEPOSIT, pItem.getDepositId());
        writeBytes(COL_RATE, pItem.getRateBytes());
        writeBytes(COL_BONUS, pItem.getBonusBytes());
        writeDate(COL_ENDDATE, pItem.getEndDate());
    }

    @Override
    protected int getLastColumn() {
        /* Return the last column */
        return COL_ENDDATE;
    }

    /**
     * Load the DepositRates from an archive.
     * @param pReport the report
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @throws OceanusException on error
     */
    protected static void loadArchive(final MetisThreadStatusReport pReport,
                                      final MetisDataWorkBook pWorkBook,
                                      final MoneyWiseData pData) throws OceanusException {
        /* Access the list of rates */
        DepositRateList myList = pData.getDepositRates();

        /* Protect against exceptions */
        try {
            /* Find the range of cells */
            MetisDataView myView = pWorkBook.getRangeView(AREA_RATES);

            /* Declare the new stage */
            pReport.setNewStage(AREA_RATES);

            /* Count the number of Rates */
            int myTotal = myView.getRowCount();

            /* Declare the number of steps */
            pReport.setNumSteps(myTotal);

            /* Loop through the rows of the table */
            for (int i = 0; i < myTotal; i++) {
                /* Access the cell by reference */
                MetisDataRow myRow = myView.getRowByIndex(i);
                int iAdjust = 0;

                /* Access deposit */
                MetisDataCell myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myDeposit = myCell.getStringValue();

                /* Handle Rate */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myRate = myCell.getStringValue();

                /* Handle bonus which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                String myBonus = null;
                if (myCell != null) {
                    myBonus = myCell.getStringValue();
                }

                /* Handle expiration which may be missing */
                myCell = myView.getRowCellByIndex(myRow, iAdjust++);
                TethysDate myExpiry = null;
                if (myCell != null) {
                    myExpiry = myCell.getDateValue();
                }

                /* Build data values */
                DataValues<MoneyWiseDataType> myValues = new DataValues<>(DepositRate.OBJECT_NAME);
                myValues.addValue(DepositRate.FIELD_DEPOSIT, myDeposit);
                myValues.addValue(DepositRate.FIELD_RATE, myRate);
                myValues.addValue(DepositRate.FIELD_BONUS, myBonus);
                myValues.addValue(DepositRate.FIELD_ENDDATE, myExpiry);

                /* Add the value into the list */
                myList.addValuesItem(myValues);

                /* Report the progress */
                pReport.setNextStep();
            }

            /* PostProcess the rates */
            myList.postProcessOnLoad();

            /* Handle exceptions */
        } catch (MetisThreadCancelException e) {
            throw e;
        } catch (OceanusException e) {
            throw new MoneyWiseIOException("Failed to Load " + myList.getItemType().getListName(), e);
        }
    }
}
