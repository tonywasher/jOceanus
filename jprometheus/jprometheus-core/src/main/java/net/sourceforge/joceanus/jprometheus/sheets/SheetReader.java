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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jprometheus.JPrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.JPrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.TaskControl;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Load control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class SheetReader<T extends DataSet<T, ?>> {
    /**
     * Close error text.
     */
    protected static final String ERROR_CLOSE = "Close failure";

    /**
     * Cancel error text.
     */
    private static final String ERROR_CANCEL = "Operation cancelled";

    /**
     * Task control.
     */
    private final TaskControl<T> theTask;

    /**
     * Spreadsheet.
     */
    private MetisDataWorkBook theWorkBook = null;

    /**
     * The DataSet.
     */
    private T theData = null;

    /**
     * The WorkSheets.
     */
    private List<SheetDataItem<?, ?>> theSheets = null;

    /**
     * Constructor.
     * @param pTask the Task control
     */
    public SheetReader(final TaskControl<T> pTask) {
        theTask = pTask;
    }

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
    protected MetisDataWorkBook getWorkBook() {
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
     * Add Sheet to list.
     * @param pSheet the sheet
     */
    protected void addSheet(final SheetDataItem<?, ?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Load a Backup Workbook.
     * @param pFile the backup file to load from
     * @return the loaded DataSet
     * @throws OceanusException on error
     */
    public T loadBackup(final File pFile) throws OceanusException {
        /* Start the task */
        MetisProfile myTask = theTask.getActiveTask();
        myTask = myTask.startTask("Loading");

        /* Access the zip file */
        GordianZipReadFile myFile = new GordianZipReadFile(pFile);

        /* Obtain the hash bytes from the file */
        byte[] myHashBytes = myFile.getHashBytes();

        /* Access the Security manager */
        GordianHashManager mySecurity = theTask.getSecurity();

        /* Obtain the initialised keySetHash */
        GordianKeySetHash myHash = mySecurity.resolveKeySetHash(myHashBytes, pFile.getName());

        /* Associate this keySetHash with the ZipFile */
        myFile.setKeySetHash(myHash);

        /* Access ZipFile contents */
        GordianZipFileContents myContents = myFile.getContents();

        /* Loop through the file entries */
        Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
        GordianZipFileEntry myEntry = null;
        while (myIterator.hasNext()) {
            /* Access the entry */
            myEntry = myIterator.next();

            /* Break loop if we have the right entry */
            if (myEntry.getFileName().startsWith(SpreadSheet.FILE_NAME)) {
                break;
            }
        }

        /* Load the workBook */
        loadEntry(myFile, myEntry);

        /* Complete the task */
        myTask.end();

        /* Return the new DataSet */
        return theData;
    }

    /**
     * Load a Backup Workbook.
     * @param pFile the zip file
     * @param pEntry the zip file entry
     * @throws OceanusException on error
     */
    public void loadEntry(final GordianZipReadFile pFile,
                          final GordianZipFileEntry pEntry) throws OceanusException {
        /* Protect the workbook retrieval */
        try (InputStream myStream = pFile.getInputStream(pEntry)) {
            /* Obtain the active profile */
            MetisProfile myTask = theTask.getActiveTask();
            myTask.startTask("Parsing");

            /* Initialise the workbook */
            boolean bContinue = initialiseWorkBook(myStream, MetisWorkBookType.determineType(pEntry.getFileName()));

            /* Load the workbook */
            if (bContinue) {
                myTask.startTask("Reading");
                bContinue = loadWorkBook();
            }

            /* Close the Stream to force out errors */
            myTask.startTask("Closing");
            myStream.close();

            /* Check for cancellation */
            if (!bContinue) {
                throw new JPrometheusCancelException(ERROR_CANCEL);
            }
        } catch (IOException e) {
            /* Report the error */
            throw new JPrometheusIOException("Failed to load Backup Workbook: " + pEntry.getFileName(), e);
        }
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
     * @throws OceanusException on error
     * @throws IOException on read error
     */
    private boolean initialiseWorkBook(final InputStream pStream,
                                       final MetisWorkBookType pType) throws OceanusException, IOException {
        /* Create the new DataSet */
        theData = newDataSet();

        /* Initialise the list */
        theSheets = new ArrayList<SheetDataItem<?, ?>>();

        /* Add security details */
        theSheets.add(new SheetControlKey(this));
        theSheets.add(new SheetDataKeySet(this));
        theSheets.add(new SheetDataKey(this));
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
            theWorkBook = new MetisDataWorkBook(pStream, pType);
        }

        /* Return continue status */
        return bContinue;
    }

    /**
     * Load the WorkBook.
     * @return continue true/false
     * @throws OceanusException on error
     */
    private boolean loadWorkBook() throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theTask.getActiveTask();

        /* Declare the number of stages */
        boolean bContinue = theTask.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        Iterator<SheetDataItem<?, ?>> myIterator = theSheets.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            /* Access the next sheet */
            SheetDataItem<?, ?> mySheet = myIterator.next();

            /* Load data for the sheet */
            myTask.startTask(mySheet.toString());
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
