/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.ControlData.ControlDataList;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jDataModels.preferences.BackupPreferences;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetReader;
import net.sourceforge.jOceanus.jDataModels.sheets.SheetWriter;
import net.sourceforge.jOceanus.jDataModels.sheets.SpreadSheet;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataCell;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataRow;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataView;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;

/**
 * SpreadSheet extension for FinanceData.
 * @author Tony Washer
 */
public class FinanceSheet
        extends SpreadSheet<FinanceData> {
    /**
     * Number of base archive load areas. 8xStatic,Dilution,Pattern,Rate,Price,Account,TaxYear,Range+Event.
     */
    private static final int NUM_ARCHIVE_AREAS = 16;

    /**
     * Obtain a sheet reader.
     * @param pTask Thread Control for task
     * @return the sheet reader
     */
    @Override
    protected SheetReader<FinanceData> getSheetReader(final TaskControl<FinanceData> pTask) {
        /* Create a Finance Reader object and return it */
        return new FinanceReader(pTask);
    }

    /**
     * Obtain a sheet writer.
     * @param pTask Task Control for task
     * @return the sheet writer
     */
    @Override
    protected SheetWriter<FinanceData> getSheetWriter(final TaskControl<FinanceData> pTask) {
        /* Create a Finance Writer object and return it */
        return new FinanceWriter(pTask);
    }

    /**
     * NamedRange for Static.
     */
    private static final String AREA_YEARRANGE = "YearRange";

    /**
     * Simple class to hold YearRange.
     */
    protected static final class YearRange {
        /**
         * The minimum Year.
         */
        private int theMinYear = 0;

        /**
         * The maximum Year.
         */
        private int theMaxYear = 0;

        /**
         * Get the minimum Year.
         * @return the year
         */
        protected int getMinYear() {
            return theMinYear;
        }

        /**
         * Get the maximum Year.
         * @return the year
         */
        protected int getMaxYear() {
            return theMaxYear;
        }

        /**
         * Set the minimum Year.
         * @param pYear the year
         */
        protected void setMinYear(final int pYear) {
            theMinYear = pYear;
        }

        /**
         * Set the maximum Year.
         * @param pYear the year
         */
        protected void setMaxYear(final int pYear) {
            theMaxYear = pYear;
        }
    }

    /**
     * Load the Static from an archive.
     * @param pTask the task control
     * @param pWorkBook the workbook
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException on error
     */
    protected static boolean loadArchive(final TaskControl<FinanceData> pTask,
                                         final DataWorkBook pWorkBook,
                                         final FinanceData pData,
                                         final YearRange pRange) throws JDataException {
        /* Find the range of cells */
        DataView myView = pWorkBook.getRangeView(AREA_YEARRANGE);

        /* Access the Year Range */
        DataRow myRow = myView.getRowByIndex(0);
        DataCell myCell = myView.getRowCellByIndex(myRow, 0);
        pRange.setMinYear(myCell.getIntegerValue());
        myCell = myView.getRowCellByIndex(myRow, 1);
        pRange.setMaxYear(myCell.getIntegerValue());

        /* Access the static */
        ControlDataList myStatic = pData.getControlData();

        /* Add the value into the finance tables (with no security as yet) */
        myStatic.addOpenItem(0, 0);

        /* Calculate the number of stages */
        int myStages = NUM_ARCHIVE_AREAS
                       + pRange.getMaxYear()
                       - pRange.getMinYear();

        /* Declare the number of stages */
        return pTask.setNumStages(myStages);
    }

    /**
     * Load an Archive Workbook.
     * @param pTask Task Control for task
     * @param pPreferences the backup preferences
     * @return the newly loaded data
     * @throws JDataException on error
     */
    public static FinanceData loadArchive(final TaskControl<FinanceData> pTask,
                                          final BackupPreferences pPreferences) throws JDataException {
        InputStream myStream = null;

        /* Determine the archive name */
        String myName = pPreferences.getStringValue(BackupPreferences.NAME_ARCHIVE_FILE);
        File myArchive = new File(myName);

        /* Protect the workbook retrieval */
        try {
            /* Create an input stream to the file */
            FileInputStream myInFile = new FileInputStream(myArchive);
            myStream = new BufferedInputStream(myInFile);

            /* Determine the WorkBookType */
            WorkBookType myType = WorkBookType.determineType(myName);

            /* Load the data from the stream */
            FinanceData myData = loadArchiveStream(pTask, myStream, myType);

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;
            return myData;
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Workbook: "
                                                           + myArchive.getName(), e);
        } finally {
            /* Protect while cleaning up */
            try {
                /* Close the output stream */
                if (myStream != null) {
                    myStream.close();
                }

                /* Ignore errors */
            } catch (IOException ex) {
                myStream = null;
            }
        }
    }

    /**
     * Load an Archive Workbook from a stream.
     * @param pTask Task Control for task
     * @param pStream Input stream to load from
     * @param pType the workBookType
     * @return the newly loaded data
     * @throws JDataException on error
     */
    private static FinanceData loadArchiveStream(final TaskControl<FinanceData> pTask,
                                                 final InputStream pStream,
                                                 final WorkBookType pType) throws JDataException {
        /* Protect the workbook retrieval */
        try {
            /* Create the Data */
            FinanceData myData = pTask.getNewDataSet();

            /* Access the workbook from the stream */
            DataWorkBook myWorkbook = new DataWorkBook(pStream, pType);

            /* Create a YearRange */
            YearRange myRange = new YearRange();

            /* Determine Year Range */
            boolean bContinue = loadArchive(pTask, myWorkbook, myData, myRange);

            /* Load Tables */
            if (bContinue) {
                bContinue = SheetAccountType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTransactionType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxRegime.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetFrequency.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetTaxYearInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountInfoType.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetEventInfoType.loadArchive(pTask, myWorkbook, myData);
            }

            if (bContinue) {
                bContinue = SheetTaxYear.loadArchive(pTask, myWorkbook, myData, myRange);
            }
            if (bContinue) {
                myData.calculateDateRange();
            }

            if (bContinue) {
                bContinue = SheetAccount.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountRate.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetAccountPrice.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                bContinue = SheetPattern.loadArchive(pTask, myWorkbook, myData);
            }
            if (bContinue) {
                myData.getAccounts().markActiveItems();
            }

            if (bContinue) {
                bContinue = SheetEvent.loadArchive(pTask, myWorkbook, myData, myRange);
            }

            /* Close the stream */
            pStream.close();

            /* Set the next stage */
            if (!pTask.setNewStage("Refreshing data")) {
                bContinue = false;
            }

            /* Check for cancellation */
            if (!bContinue) {
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
            }

            /* Return the data */
            return myData;
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Workbook", e);
        }
    }
}
