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
package net.sourceforge.joceanus.jdatamodels.sheets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamodels.data.DataSet;
import net.sourceforge.joceanus.jdatamodels.data.TaskControl;
import net.sourceforge.joceanus.jgordianknot.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.SecureManager;
import net.sourceforge.joceanus.jgordianknot.zipfile.ZipWriteFile;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook;
import net.sourceforge.joceanus.jspreadsheetmanager.DataWorkBook.WorkBookType;

/**
 * Write control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SheetWriter<T extends DataSet<T>> {
    /**
     * Task control.
     */
    private final TaskControl<T> theTask;

    /**
     * Writable spreadsheet.
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
     * get thread status.
     * @return the status
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
    protected SheetWriter(final TaskControl<T> pTask) {
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
     * Create a Backup Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws JDataException on error
     */
    public void createBackup(final T pData,
                             final File pFile,
                             final WorkBookType pType) throws JDataException {
        OutputStream myStream = null;
        ZipWriteFile myZipFile = null;
        boolean bSuccess = false;

        /* Protect the workbook access */
        try {
            /* Note the type of file */
            isBackup = true;

            /* Record the DataSet */
            theData = pData;

            /* Create a clone of the security control */
            SecureManager mySecure = pData.getSecurity();
            PasswordHash myBase = pData.getPasswordHash();
            PasswordHash myHash = mySecure.clonePasswordHash(myBase);

            /* Create the new output Zip file */
            myZipFile = new ZipWriteFile(myHash, pFile);
            String myName = SpreadSheet.FILE_NAME
                            + pType.getExtension();
            myStream = myZipFile.getOutputStream(new File(myName));

            /* Initialise the WorkBook */
            initialiseWorkBook(pType);

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();
            myStream = null;

            /* Close the Zip file */
            myZipFile.close();

            /* Set success to avoid deleting file */
            bSuccess = true;
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to create Backup Workbook: "
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

            /* Delete the file on error */
            if ((!bSuccess)
                && (!pFile.delete())) {
                /* Nothing that we can do. At least we tried */
                myStream = null;
            }
        }
    }

    /**
     * Create an Extract Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @throws JDataException on error
     */
    public void createExtract(final T pData,
                              final File pFile) throws JDataException {
        /* Declare variables */
        OutputStream myStream = null;
        FileOutputStream myOutFile = null;
        boolean bSuccess = false;

        /* Protect the workbook access */
        try {
            /* Note the type of file */
            isBackup = false;

            /* Record the DataSet */
            theData = pData;

            /* Create an output stream to the file */
            myOutFile = new FileOutputStream(pFile);
            myStream = new BufferedOutputStream(myOutFile);

            /* Determine the type of the workbook */
            WorkBookType myType = WorkBookType.determineType(pFile.getName());

            /* Initialise the WorkBook */
            initialiseWorkBook(myType);

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();

            /* Set success to avoid deleting file */
            bSuccess = true;
        } catch (IOException e) {
            /* Report the error */
            throw new JDataException(ExceptionClass.EXCEL, "Failed to create Editable Workbook: "
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

            /* Delete the file on error */
            if ((!bSuccess)
                && (!pFile.delete())) {
                /* Nothing that we can do. At least we tried */
                myStream = null;
            }
        }
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

    /**
     * Create the list of sheets to write.
     * @param pType the workBookType
     * @throws JDataException on error
     */
    private void initialiseWorkBook(final WorkBookType pType) throws JDataException {
        /* Create the workbook attached to the output stream */
        theWorkBook = new DataWorkBook(pType);

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
    }

    /**
     * Write the WorkBook.
     * @param pStream the output stream
     * @throws JDataException on error
     * @throws IOException on write error
     */
    private void writeWorkBook(final OutputStream pStream) throws JDataException, IOException {
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

            /* Write data for the sheet */
            bContinue = mySheet.writeSpreadSheet();
        }

        /* If we have built all the sheets */
        if (bContinue) {
            bContinue = theTask.setNewStage("Writing");
        }

        /* If we have created the workbook OK */
        if (bContinue) {
            /* Write it out to disk and close the stream */
            theWorkBook.saveToStream(pStream);
        }

        /* Check for cancellation */
        if (!bContinue) {
            throw new JDataException(ExceptionClass.EXCEL, "Operation Cancelled");
        }
    }
}
