/*******************************************************************************
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
package uk.co.tolcroft.finance.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;

import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.views.DilutionEvent.DilutionEventList;
import uk.co.tolcroft.models.data.ControlData.ControlDataList;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.data.TaskControl;
import uk.co.tolcroft.models.sheets.BackupPreferences;
import uk.co.tolcroft.models.sheets.SheetReader;
import uk.co.tolcroft.models.sheets.SheetReader.SheetHelper;
import uk.co.tolcroft.models.sheets.SheetWriter;
import uk.co.tolcroft.models.sheets.SpreadSheet;
import uk.co.tolcroft.models.threads.ThreadStatus;
import uk.co.tolcroft.models.views.DataControl;

public class FinanceSheet extends SpreadSheet<FinanceData> {
    /**
     * Obtain a sheet reader
     * @param pTask Thread Control for task
     * @return the sheet reader
     */
    @Override
    protected SheetReader<FinanceData> getSheetReader(TaskControl<FinanceData> pTask) {
        /* Create a Finance Reader object and return it */
        return new FinanceReader(pTask);
    }

    /**
     * Obtain a sheet writer
     * @param pTask Task Control for task
     * @return the sheet writer
     */
    @Override
    protected SheetWriter<FinanceData> getSheetWriter(TaskControl<FinanceData> pTask) {
        /* Create a Finance Writer object and return it */
        return new FinanceWriter(pTask);
    }

    /**
     * NamedRange for Static
     */
    private static final String YearRange = "YearRange";

    /**
     * Simple class to hold YearRange
     */
    protected static class YearRange {
        private int theMinYear = 0;
        private int theMaxYear = 0;

        protected int getMinYear() {
            return theMinYear;
        }

        protected int getMaxYear() {
            return theMaxYear;
        }

        protected void setMinYear(int pYear) {
            theMinYear = pYear;
        }

        protected void setMaxYear(int pYear) {
            theMaxYear = pYear;
        }
    }

    /**
     * Load the Static from an archive
     * @param pThread the thread status control
     * @param pHelper the sheet helper
     * @param pData the data set to load into
     * @param pRange the range of tax years
     * @return continue to load <code>true/false</code>
     * @throws JDataException
     */
    protected static boolean loadArchive(ThreadStatus<FinanceData> pThread,
                                         SheetHelper pHelper,
                                         FinanceData pData,
                                         YearRange pRange) throws JDataException {
        /* Local variables */
        AreaReference myRange;
        Sheet mySheet;
        CellReference myTop;
        Cell myCell;
        int myCol;
        int myStages;
        ControlDataList myStatic;

        /* Find the range of cells */
        myRange = pHelper.resolveAreaReference(YearRange);

        /* If we found the range OK */
        if (myRange != null) {
            /* Access the relevant sheet and Cell references */
            myTop = myRange.getFirstCell();
            mySheet = pHelper.getSheetByName(myTop.getSheetName());
            myCol = myTop.getCol();

            /* Access the Year Range */
            Row myRow = mySheet.getRow(myTop.getRow() + 1);
            myCell = myRow.getCell(myCol);
            pRange.setMinYear(pHelper.parseIntegerCell(myCell));
            myCell = myRow.getCell(myCol + 1);
            pRange.setMaxYear(pHelper.parseIntegerCell(myCell));

            /* Access the static */
            myStatic = pData.getControlData();

            /* Add the value into the finance tables (with no security as yet) */
            myStatic.addItem(0, 0);
        }

        /* Calculate the number of stages */
        myStages = 14 + pRange.getMaxYear() - pRange.getMinYear();

        /* Declare the number of stages */
        return pThread.setNumStages(myStages);
    }

    /**
     * Load an Archive Workbook
     * @param pThread Thread Control for task
     * @return the newly loaded data
     * @throws JDataException
     */
    public static FinanceData loadArchive(ThreadStatus<FinanceData> pThread) throws JDataException {
        InputStream myStream = null;
        FileInputStream myInFile = null;
        File myArchive = null;
        FinanceData myData;

        /* Protect the workbook retrieval */
        try {
            /* Access the Backup properties */
            BackupPreferences myPreferences = (BackupPreferences) PreferenceManager
                    .getPreferenceSet(BackupPreferences.class);

            /* Determine the archive name */
            myArchive = new File(myPreferences.getStringValue(BackupPreferences.NAME_ARCHIVE_FILE));

            /* Create an input stream to the file */
            myInFile = new FileInputStream(myArchive);
            myStream = new BufferedInputStream(myInFile);

            /* Load the data from the stream */
            myData = loadArchiveStream(pThread, myStream);

            /* Close the Stream to force out errors */
            myStream.close();
        }

        catch (Throwable e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Workbook: " + myArchive.getName(),
                    e);
        }

        /* Return the new DataSet */
        return myData;
    }

    /**
     * Load an Archive Workbook from a stream
     * @param pThread Thread Control for task
     * @param pStream Input stream to load from
     * @return the newly loaded data
     * @throws JDataException
     */
    private static FinanceData loadArchiveStream(ThreadStatus<FinanceData> pThread,
                                                 InputStream pStream) throws JDataException {
        boolean bContinue;
        HSSFWorkbook myWorkbook = null;
        FinanceData myData = null;
        DataControl<FinanceData> myControl = null;
        YearRange myRange = null;
        DilutionEventList myDilution = null;
        SheetHelper myHelper = null;

        /* Protect the workbook retrieval */
        try {
            /* Create the Data */
            myControl = pThread.getControl();
            myData = myControl.getNewData();

            /* Access the workbook from the stream */
            myWorkbook = new HSSFWorkbook(pStream);

            /* Set the missing Cell Policy */
            myWorkbook.setMissingCellPolicy(Row.RETURN_BLANK_AS_NULL);

            /* Create the helper */
            myHelper = new SheetHelper(myWorkbook);

            /* Create a YearRange */
            myRange = new YearRange();

            /* Create the dilution event list */
            myDilution = new DilutionEventList(myData);

            /* Determine Year Range */
            bContinue = loadArchive(pThread, myHelper, myData, myRange);

            /* Load Tables */
            if (bContinue)
                bContinue = SheetAccountType.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetTransactionType.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetTaxType.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetTaxRegime.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetFrequency.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetEventInfoType.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetTaxYear.loadArchive(pThread, myHelper, myData, myRange);
            if (bContinue)
                myData.calculateDateRange();
            if (bContinue)
                bContinue = SheetAccount.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetAccountRate.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                bContinue = SheetDilution.loadArchive(pThread, myHelper, myData, myDilution);
            if (bContinue)
                bContinue = SheetAccountPrice.loadArchive(pThread, myHelper, myData, myDilution);
            if (bContinue)
                bContinue = SheetPattern.loadArchive(pThread, myHelper, myData);
            if (bContinue)
                myData.getAccounts().validateLoadedAccounts();
            if (bContinue)
                bContinue = SheetEvent.loadArchive(pThread, myHelper, myData, myRange);

            /* Close the stream */
            pStream.close();

            /* Set the next stage */
            if (!pThread.setNewStage("Refreshing data"))
                bContinue = false;

            /* Check for cancellation */
            if (!bContinue)
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
        }

        catch (Throwable e) {
            /* Show we are cancelled */
            bContinue = false;

            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Workbook", e);
        }

        /* Return the new data */
        return (bContinue) ? myData : null;
    }
}
