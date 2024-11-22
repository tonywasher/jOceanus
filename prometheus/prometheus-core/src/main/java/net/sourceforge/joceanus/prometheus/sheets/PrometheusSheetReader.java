/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.prometheus.sheets;

import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFileContents;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFileEntry;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipReadFile;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.PrometheusIOException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetProvider;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.thread.TethysUIThreadStatusReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Load control for spreadsheets.
 * @author Tony Washer
 */
public abstract class PrometheusSheetReader {
    /**
     * Gui Factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * Report.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * The password manager.
     */
    private final PrometheusSecurityPasswordManager thePasswordMgr;

    /**
     * Spreadsheet.
     */
    private PrometheusSheetWorkBook theWorkBook;

    /**
     * DataSet.
     */
    private PrometheusDataSet theData;

    /**
     * The WorkSheets.
     */
    private List<PrometheusSheetDataItem<?>> theSheets;

    /**
     * Constructor.
     * @param pFactory the gui factory
     * @param pReport the report
     * @param pPasswordMgr the password manager
     */
    protected PrometheusSheetReader(final TethysUIFactory<?> pFactory,
                                    final TethysUIThreadStatusReport pReport,
                                    final PrometheusSecurityPasswordManager pPasswordMgr) {
        theGuiFactory = pFactory;
        theReport = pReport;
        thePasswordMgr = pPasswordMgr;
    }

    /**
     * get report.
     * @return the report
     */
    protected TethysUIThreadStatusReport getReport() {
        return theReport;
    }

    /**
     * get data.
     * @return the data
     */
    public PrometheusDataSet getData() {
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
    protected void addSheet(final PrometheusSheetDataItem<?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Load a Backup Workbook.
     * @param pData DataSet to load into
     * @param pFile the file to load
     * @throws OceanusException on error
     */
    public void loadBackup(final File pFile,
                           final PrometheusDataSet pData) throws OceanusException {
        try {
            loadBackup(new FileInputStream(pFile), pData, pFile.getName());
        } catch (IOException e) {
            throw new PrometheusIOException("Failed to access Backup", e);
        }
    }

    /**
     * Load a Backup Workbook.
     * @param pInStream the zip input stream
     * @param pData the data to load into
     * @param pName the filename
     * @throws OceanusException on error
     */
    public void loadBackup(final InputStream pInStream,
                           final PrometheusDataSet pData,
                           final String pName) throws OceanusException {
        /* Start the task */
        OceanusProfile myTask = theReport.getActiveTask();
        myTask = myTask.startTask("Loading");
        theData = pData;

        /* Access the zip file */
        final GordianZipFactory myZips = thePasswordMgr.getSecurityFactory().getZipFactory();
        final GordianZipReadFile myFile = myZips.openZipFile(pInStream);

        /* Obtain the lock from the file */
        final GordianZipLock myLock = myFile.getLock();

        /* Resolve the lock */
        thePasswordMgr.resolveZipLock(myLock, pName);

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
            final OceanusProfile myTask = theReport.getActiveTask();
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

        /* Loop through the list types */
        for (PrometheusCryptographyDataType myType : PrometheusCryptographyDataType.values()) {
            /* Create the sheet */
            theSheets.add(newSheet(myType));
        }

        /* register additional sheets */
        registerSheets();

        /* Declare the number of stages */
        theReport.setNumStages(theSheets.size() + 2);

        /* Note the stage */
        theReport.setNewStage("Loading");

        /* Access the workbook from the stream */
        theWorkBook = PrometheusSheetProvider.loadFromStream(pType, theGuiFactory, pStream);
    }

    /**
     * Create new sheet of required type.
     * @param pListType the list type
     * @return the new sheet
     */
    private PrometheusSheetDataItem<?> newSheet(final PrometheusCryptographyDataType pListType) {
        /* Switch on list Type */
        switch (pListType) {
            case CONTROLDATA:
                return new PrometheusSheetControlData(this);
            case CONTROLKEY:
                return new PrometheusSheetControlKey(this);
            case CONTROLKEYSET:
                return new PrometheusSheetControlKeySet(this);
            case DATAKEYSET:
                return new PrometheusSheetDataKeySet(this);
            default:
                throw new IllegalArgumentException(pListType.toString());
        }
    }

    /**
     * Load the WorkBook.
     * @throws OceanusException on error
     */
    private void loadWorkBook() throws OceanusException {
        /* Obtain the active profile */
        final OceanusProfile myTask = theReport.getActiveTask();

        /* Declare the number of stages */
        theReport.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        for (PrometheusSheetDataItem<?> mySheet : theSheets) {
            /* Access the next sheet */
            /* Load data for the sheet */
            myTask.startTask(mySheet.toString());
            mySheet.loadSpreadSheet();
        }
    }
}
