/*******************************************************************************
 * jDataModels: Data models
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jDataModels.sheets;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataModels.data.DataSet;
import net.sourceforge.jOceanus.jDataModels.data.TaskControl;
import net.sourceforge.jOceanus.jGordianKnot.PasswordHash;
import net.sourceforge.jOceanus.jGordianKnot.SecureManager;
import net.sourceforge.jOceanus.jGordianKnot.ZipFile.ZipFileContents;
import net.sourceforge.jOceanus.jGordianKnot.ZipFile.ZipFileEntry;
import net.sourceforge.jOceanus.jGordianKnot.ZipFile.ZipReadFile;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook;
import net.sourceforge.jOceanus.jSpreadSheetManager.DataWorkBook.WorkBookType;

/**
 * Load control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SheetReader<T extends DataSet<T>> {
    /**
     * Task control.
     */
    private final TaskControl<T> theTask;

    /**
     * Spreadsheet.
     */
    private DataWorkBook theWorkBook = null;

    /**
     * The DataSet.
     */
    private T theData = null;

    /**
     * The WorkSheets.
     */
    private List<SheetDataItem<?>> theSheets = null;

    /**
     * Is this a backup sheet.
     */
    private boolean isBackup = false;

    /**
     * get task control.
     * @return the task control
     */
    protected TaskControl<T> getTask() {
        return theTask;
    }

    /**
     * get workbook.
     * @return the workbook
     */
    protected DataWorkBook getWorkBook() {
        return theWorkBook;
    }

    /**
     * Is the sheet a backup or editable sheet.
     * @return true/false
     */
    protected boolean isBackup() {
        return isBackup;
    }

    /**
     * get dataSet.
     * @return the dataSet
     */
    public T getData() {
        return theData;
    }

    /**
     * Constructor.
     * @param pTask the Task control
     */
    public SheetReader(final TaskControl<T> pTask) {
        theTask = pTask;
    }

    /**
     * Add Sheet to list.
     * @param pSheet the sheet
     */
    protected void addSheet(final SheetDataItem<?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Load a Backup Workbook.
     * @param pFile the backup file to write to
     * @return the loaded DataSet
     * @throws JDataException on error
     */
    public T loadBackup(final File pFile) throws JDataException {
        InputStream myStream = null;

        /* Protect the workbook retrieval */
        try {
            /* Note the type of file */
            isBackup = true;

            /* Access the zip file */
            ZipReadFile myFile = new ZipReadFile(pFile);

            /* Obtain the hash bytes from the file */
            byte[] myHashBytes = myFile.getHashBytes();

            /* Access the Security manager */
            SecureManager mySecurity = theTask.getSecurity();

            /* Obtain the initialised password hash */
            PasswordHash myHash = mySecurity.resolvePasswordHash(myHashBytes, pFile.getName());

            /* Associate this password hash with the ZipFile */
            myFile.setPasswordHash(myHash);

            /* Access ZipFile contents */
            ZipFileContents myContents = myFile.getContents();

            /* Loop through the file entries */
            Iterator<ZipFileEntry> myIterator = myContents.iterator();
            ZipFileEntry myEntry = null;
            while (myIterator.hasNext()) {
                /* Access the entry */
                myEntry = myIterator.next();

                /* Break loop if we have the right entry */
                if (myEntry.getFileName().startsWith(SpreadSheet.FILE_NAME)) {
                    break;
                }
            }

            /* Access the input stream for the relevant file */
            myStream = myFile.getInputStream(myEntry);

            /* Initialise the workbook */
            boolean bContinue = initialiseWorkBook(myStream, WorkBookType.determineType(myEntry.getFileName()));

            /* Load the workbook */
            if (bContinue) {
                bContinue = loadWorkBook();
            }

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Check for cancellation */
            if (!bContinue) {
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
            }
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Backup Workbook: "
                                                           + pFile.getName(), e);
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

        /* Return the new DataSet */
        return theData;
    }

    /**
     * Load an Extract Workbook.
     * @param pFile the Extract file to load from
     * @return the loaded DataSet
     * @throws JDataException on error
     */
    public T loadExtract(final File pFile) throws JDataException {
        InputStream myStream = null;

        /* Protect the workbook retrieval */
        try {
            /* Note the type of file */
            isBackup = false;

            /* Create an input stream to the file */
            FileInputStream myInFile = new FileInputStream(pFile);
            myStream = new BufferedInputStream(myInFile);

            /* Determine the type of the workbook */
            WorkBookType myType = WorkBookType.determineType(pFile.getName());

            /* Initialise the workbook */
            boolean bContinue = initialiseWorkBook(myStream, myType);

            /* Load the workbook */
            if (bContinue) {
                bContinue = loadWorkBook();
            }

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Check for cancellation */
            if (!bContinue) {
                throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
            }
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to load Edit-able Workbook: "
                                                           + pFile.getName(), e);
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

        /* Return the new DataSet */
        return theData;
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

    /**
     * Obtain empty DataSet.
     * @return the dataSet
     */
    protected abstract T newDataSet();

    /**
     * Create the list of sheets to load.
     * @param pStream the input stream
     * @return continue true/false
     * @param pType the workBookType
     * @throws JDataException on error
     * @throws IOException on read error
     */
    private boolean initialiseWorkBook(final InputStream pStream,
                                       final WorkBookType pType) throws JDataException, IOException {
        /* Create the new DataSet */
        theData = newDataSet();

        /* Initialise the list */
        theSheets = new ArrayList<SheetDataItem<?>>();

        /* If this is a backup */
        if (isBackup()) {
            /* Add security details */
            theSheets.add(new SheetControlKey(this));
            theSheets.add(new SheetDataKey(this));
        }

        /* Add the items */
        theSheets.add(new SheetControlData(this));

        /* register additional sheets */
        registerSheets();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 2);

        /* Note the stage */
        if (bContinue) {
            bContinue = theTask.setNewStage("Loading");
        }

        /* Access the workbook from the stream */
        if (bContinue) {
            theWorkBook = new DataWorkBook(pStream, pType);
        }

        /* Return continue status */
        return bContinue;
    }

    /**
     * Load the WorkBook.
     * @return continue true/false
     * @throws JDataException on error
     */
    private boolean loadWorkBook() throws JDataException {
        SheetDataItem<?> mySheet;

        /* Access the iterator for the list */
        Iterator<SheetDataItem<?>> myIterator = theSheets.iterator();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        while ((bContinue)
               && (myIterator.hasNext())) {
            /* Access the next sheet */
            mySheet = myIterator.next();

            /* Load data for the sheet */
            bContinue = mySheet.loadSpreadSheet();
        }

        /* Analyse the data */
        if (!theTask.setNewStage("Refreshing data")) {
            bContinue = false;
        }

        /* Return continue status */
        return bContinue;
    }
}