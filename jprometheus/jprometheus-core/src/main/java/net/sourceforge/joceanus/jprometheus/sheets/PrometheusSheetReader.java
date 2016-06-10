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
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.PrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Load control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class PrometheusSheetReader<T extends DataSet<T, ?>> {
    /**
     * Close error text.
     */
    protected static final String ERROR_CLOSE = "Close failure";

    /**
     * Cancel error text.
     */
    private static final String ERROR_CANCEL = "Operation cancelled";

    /**
     * Report.
     */
    private final MetisThreadStatusReport theReport;

    /**
     * The security manager.
     */
    private final GordianHashManager theSecurityMgr;

    /**
     * Spreadsheet.
     */
    private MetisDataWorkBook theWorkBook;

    /**
     * DataSet.
     */
    private T theData;

    /**
     * The WorkSheets.
     */
    private List<PrometheusSheetDataItem<?, ?>> theSheets;

    /**
     * Constructor.
     * @param pReport the report
     * @param pSecureMgr the security manager
     */
    public PrometheusSheetReader(final MetisThreadStatusReport pReport,
                                 final GordianHashManager pSecureMgr) {
        theReport = pReport;
        theSecurityMgr = pSecureMgr;
    }

    /**
     * get report.
     * @return the report
     */
    protected MetisThreadStatusReport getReport() {
        return theReport;
    }

    /**
     * get data.
     * @return the data
     */
    public T getData() {
        return theData;
    }

    /**
     * get workbook.
     * @return the workbook
     */
    protected MetisDataWorkBook getWorkBook() {
        return theWorkBook;
    }

    /**
     * Add Sheet to list.
     * @param pSheet the sheet
     */
    protected void addSheet(final PrometheusSheetDataItem<?, ?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Load a Backup Workbook.
     * @param pFile the backup file to load from
     * @param pData the data to load into
     * @throws OceanusException on error
     */
    public void loadBackup(final File pFile,
                           final T pData) throws OceanusException {
        /* Start the task */
        MetisProfile myTask = theReport.getActiveTask();
        myTask = myTask.startTask("Loading");
        theData = pData;

        /* Access the zip file */
        GordianZipReadFile myFile = new GordianZipReadFile(pFile);

        /* Obtain the hash bytes from the file */
        byte[] myHashBytes = myFile.getHashBytes();

        /* Obtain the initialised keySetHash */
        GordianKeySetHash myHash = theSecurityMgr.resolveKeySetHash(myHashBytes, pFile.getName());

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
            if (myEntry.getFileName().startsWith(PrometheusSpreadSheet.FILE_NAME)) {
                break;
            }
        }

        /* Load the workBook */
        loadEntry(myFile, myEntry);

        /* Complete the task */
        myTask.end();
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
            MetisProfile myTask = theReport.getActiveTask();
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
                throw new PrometheusCancelException(ERROR_CANCEL);
            }
        } catch (IOException e) {
            /* Report the error */
            throw new PrometheusIOException("Failed to load Backup Workbook: " + pEntry.getFileName(), e);
        }
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

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
        /* Initialise the list */
        theSheets = new ArrayList<>();

        /* Add security details */
        theSheets.add(new PrometheusSheetControlKey(this));
        theSheets.add(new PrometheusSheetDataKeySet(this));
        theSheets.add(new PrometheusSheetDataKey(this));
        theSheets.add(new PrometheusSheetControlData(this));

        /* register additional sheets */
        registerSheets();

        /* Declare the number of stages */
        boolean bContinue = theReport.setNumStages(theSheets.size() + 2);

        /* Note the stage */
        if (bContinue) {
            bContinue = theReport.setNewStage("Loading");
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
        MetisProfile myTask = theReport.getActiveTask();

        /* Declare the number of stages */
        boolean bContinue = theReport.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        Iterator<PrometheusSheetDataItem<?, ?>> myIterator = theSheets.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            /* Access the next sheet */
            PrometheusSheetDataItem<?, ?> mySheet = myIterator.next();

            /* Load data for the sheet */
            myTask.startTask(mySheet.toString());
            bContinue = mySheet.loadSpreadSheet();
        }

        /* Analyse the data */
        if (!theReport.setNewStage("Refreshing data")) {
            bContinue = false;
        }

        /* Return continue status */
        return bContinue;
    }
}
