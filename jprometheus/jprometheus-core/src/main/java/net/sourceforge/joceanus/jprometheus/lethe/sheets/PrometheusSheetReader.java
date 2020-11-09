/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.sheets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianLock;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileContents;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.jgordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetProvider;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.jprometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Load control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class PrometheusSheetReader<T extends DataSet<T, ?>> {
    /**
     * Report.
     */
    private final MetisThreadStatusReport theReport;

    /**
     * The password manager.
     */
    private final GordianPasswordManager thePasswordMgr;

    /**
     * Spreadsheet.
     */
    private PrometheusSheetWorkBook theWorkBook;

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
     * @param pPasswordMgr the password manager
     */
    public PrometheusSheetReader(final MetisThreadStatusReport pReport,
                                 final GordianPasswordManager pPasswordMgr) {
        theReport = pReport;
        thePasswordMgr = pPasswordMgr;
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
    protected PrometheusSheetWorkBook getWorkBook() {
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
        final GordianZipFactory myZips = thePasswordMgr.getSecurityFactory().getZipFactory();
        final GordianZipReadFile myFile = myZips.openZipFile(pFile);

        /* Obtain the lock from the file */
        final GordianLock myLock = myFile.getLock();

        /* Resolve the lock */
        thePasswordMgr.resolveZipLock(myLock, pFile.getName());

        /* Access ZipFile contents */
        final GordianZipFileContents myContents = myFile.getContents();

        /* Loop through the file entries */
        final Iterator<GordianZipFileEntry> myIterator = myContents.iterator();
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
        try (InputStream myStream = pFile.createInputStream(pEntry)) {
            /* Obtain the active profile */
            final MetisProfile myTask = theReport.getActiveTask();
            myTask.startTask("Parsing");

            /* Initialise the workbook */
            initialiseWorkBook(myStream, PrometheusSheetWorkBookType.determineType(pEntry.getFileName()));

            /* Load the workbook */
            myTask.startTask("Reading");
            loadWorkBook();

            /* Close the Stream to force out errors */
            myTask.startTask("Closing");

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
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    private void initialiseWorkBook(final InputStream pStream,
                                    final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Initialise the list */
        theSheets = new ArrayList<>();

        /* Add security details */
        theSheets.add(new PrometheusSheetControlKey(this));
        theSheets.add(new PrometheusSheetDataKeySet(this));
        theSheets.add(new PrometheusSheetControlData(this));

        /* register additional sheets */
        registerSheets();

        /* Declare the number of stages */
        theReport.setNumStages(theSheets.size() + 2);

        /* Note the stage */
        theReport.setNewStage("Loading");

        /* Access the workbook from the stream */
        theWorkBook = PrometheusSheetProvider.loadFromStream(pType, pStream);
    }

    /**
     * Load the WorkBook.
     * @throws OceanusException on error
     */
    private void loadWorkBook() throws OceanusException {
        /* Obtain the active profile */
        final MetisProfile myTask = theReport.getActiveTask();

        /* Declare the number of stages */
        theReport.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        final Iterator<PrometheusSheetDataItem<?, ?>> myIterator = theSheets.iterator();
        while (myIterator.hasNext()) {
            /* Access the next sheet */
            final PrometheusSheetDataItem<?, ?> mySheet = myIterator.next();

            /* Load data for the sheet */
            myTask.startTask(mySheet.toString());
            mySheet.loadSpreadSheet();
        }
    }
}
