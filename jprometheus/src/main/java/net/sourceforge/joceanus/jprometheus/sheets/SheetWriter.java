/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.sheets;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import net.sourceforge.joceanus.jgordianknot.crypto.PasswordHash;
import net.sourceforge.joceanus.jgordianknot.crypto.SecureManager;
import net.sourceforge.joceanus.jgordianknot.zip.ZipWriteFile;
import net.sourceforge.joceanus.jmetis.sheet.DataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jprometheus.JPrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.JPrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Write control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SheetWriter<T extends DataSet<T, ?>> {
    /**
     * Delete error text.
     */
    private static final String ERROR_DELETE = "Failed to delete file";

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
    private List<SheetDataItem<?, ?>> theSheets = null;

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
    protected void addSheet(final SheetDataItem<?, ?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Create a Backup Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws JOceanusException on error
     */
    public void createBackup(final T pData,
                             final File pFile,
                             final WorkBookType pType) throws JOceanusException {
        /* Create a clone of the security control */
        SecureManager mySecure = pData.getSecurity();
        PasswordHash myBase = pData.getPasswordHash();
        PasswordHash myHash = mySecure.clonePasswordHash(myBase);

        /* Assume failure */
        boolean bSuccess = false;
        String myName = SpreadSheet.FILE_NAME + pType.getExtension();

        /* Protect the workbook access */
        try (ZipWriteFile myZipFile = new ZipWriteFile(myHash, pFile);
             OutputStream myStream = myZipFile.getOutputStream(new File(myName))) {
            /* Record the DataSet */
            theData = pData;

            /* Initialise the WorkBook */
            initialiseWorkBook(pType);

            /* Write the data to the work book */
            writeWorkBook(myStream);

            /* Close the Stream to force out errors */
            myStream.close();

            /* Close the Zip file */
            myZipFile.close();

            /* Set success to avoid deleting file */
            bSuccess = true;

        } catch (IOException e) {
            /* Report the error */
            throw new JPrometheusIOException("Failed to create Backup Workbook: " + pFile.getName(), e);
        } finally {
            /* Delete the file on error */
            if ((!bSuccess) && (!pFile.delete())) {
                /* Nothing that we can do. At least we tried */
                theTask.getLogger().log(Level.SEVERE, ERROR_DELETE);
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
     * @throws JOceanusException on error
     */
    private void initialiseWorkBook(final WorkBookType pType) throws JOceanusException {
        /* Create the workbook attached to the output stream */
        theWorkBook = new DataWorkBook(pType);

        /* Initialise the list */
        theSheets = new ArrayList<SheetDataItem<?, ?>>();

        /* Add security details */
        theSheets.add(new SheetControlKey(this));
        theSheets.add(new SheetDataKeySet(this));
        theSheets.add(new SheetDataKey(this));
        theSheets.add(new SheetControlData(this));

        /* register additional sheets */
        registerSheets();
    }

    /**
     * Write the WorkBook.
     * @param pStream the output stream
     * @throws JOceanusException on error
     * @throws IOException on write error
     */
    private void writeWorkBook(final OutputStream pStream) throws JOceanusException, IOException {
        SheetDataItem<?, ?> mySheet;

        /* Access the iterator for the list */
        Iterator<SheetDataItem<?, ?>> myIterator = theSheets.iterator();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        while ((bContinue) && (myIterator.hasNext())) {
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
            throw new JPrometheusCancelException("Operation Cancelled");
        }
    }
}
