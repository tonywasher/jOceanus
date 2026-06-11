/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.sheets;

import io.github.tonywasher.joceanus.gordianknot.api.base.GordianException;
import io.github.tonywasher.joceanus.gordianknot.api.factory.GordianFactory.GordianFactoryLock;
import io.github.tonywasher.joceanus.gordianknot.api.zip.GordianZipFactory;
import io.github.tonywasher.joceanus.gordianknot.api.zip.GordianZipLock;
import io.github.tonywasher.joceanus.gordianknot.api.zip.GordianZipWriteFile;
import io.github.tonywasher.joceanus.metis.toolkit.MetisToolkit;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.profile.OceanusProfile;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import io.github.tonywasher.joceanus.prometheus.exc.PrometheusIOException;
import io.github.tonywasher.joceanus.prometheus.exc.PrometheusSecurityException;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetProvider;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import io.github.tonywasher.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Write control for spreadsheets.
 *
 * @author Tony Washer
 */
public abstract class PrometheusSheetWriter
        implements PrometheusSheetControl {
    /**
     * Report.
     */
    private final TethysUIThreadStatusReport theReport;

    /**
     * Writable spreadsheet.
     */
    private PrometheusSheetWorkBook theWorkBook;

    /**
     * The DataSet.
     */
    private PrometheusDataSet theData;

    /**
     * The Sheet Type.
     */
    private PrometheusSheetWorkBookType theType;

    /**
     * The WorkSheets.
     */
    private List<PrometheusSheetDataItem<?>> theSheets;

    /**
     * Constructor.
     *
     * @param pReport the report
     */
    protected PrometheusSheetWriter(final TethysUIThreadStatusReport pReport) {
        theReport = pReport;
    }

    @Override
    public TethysUIThreadStatusReport getReport() {
        return theReport;
    }

    @Override
    public PrometheusSheetWorkBook getWorkBook() {
        return theWorkBook;
    }

    @Override
    public PrometheusDataSet getData() {
        return theData;
    }

    /**
     * Add Sheet to list.
     *
     * @param pSheet the sheet
     */
    protected void addSheet(final PrometheusSheetDataItem<?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Create a Backup Workbook.
     *
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final PrometheusDataSet pData,
                             final File pFile,
                             final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Protect against exceptions */
        boolean writeFailed = false;
        try (FileOutputStream myOutputStream = new FileOutputStream(pFile)) {
            /* Create the backup */
            createBackup(pData, myOutputStream, pType);

            /* Handle exceptions */
        } catch (IOException
                 | OceanusException e) {
            writeFailed = true;
            throw new PrometheusIOException("Failed to create backup Workbook", e);

            /* Handle cleanup */
        } finally {
            /* Try to delete the file if required */
            if (writeFailed) {
                MetisToolkit.cleanUpFile(pFile);
            }
        }
    }

    /**
     * Create a Backup Workbook.
     *
     * @param pData      Data to write out
     * @param pZipStream the output stream
     * @param pType      the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final PrometheusDataSet pData,
                             final OutputStream pZipStream,
                             final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Record details */
        theData = pData;
        theType = pType;

        /* Create the backup */
        createBackup(pZipStream);
    }

    /**
     * Create a Backup Workbook.
     *
     * @param pZipStream the backup file to write to
     * @throws OceanusException on error
     */
    public void createBackup(final OutputStream pZipStream) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = theReport.getActiveTask();
        myTask = myTask.startTask("Writing");

        /* Protect against exceptions */
        try {
            /* Create a similar security control */
            final PrometheusSecurityPasswordManager myPasswordMgr = theData.getPasswordMgr();
            final GordianFactoryLock myBase = theData.getFactoryLock();
            final GordianFactoryLock myLock = myPasswordMgr.similarFactoryLock(myBase);
            final GordianZipFactory myZips = myPasswordMgr.getSecurityFactory().getZipFactory();
            final GordianZipLock myZipLock = myZips.zipLock(myLock);

            /* Create the backup */
            createBackup(myZipLock, pZipStream);

            /* Handle exceptions */
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }

        /* Complete task */
        myTask.end();
    }

    /**
     * Create a Backup Workbook.
     *
     * @param pZipLock   the zipLock
     * @param pZipStream the backup file to write to
     * @throws OceanusException on error
     */
    public void createBackup(final GordianZipLock pZipLock,
                             final OutputStream pZipStream) throws OceanusException {
        /* Access Zip factory */
        final PrometheusSecurityPasswordManager myPasswordMgr = theData.getPasswordMgr();
        final GordianZipFactory myZips = myPasswordMgr.getSecurityFactory().getZipFactory();

        /* Assume failure */
        final String myName = PrometheusSheetConstants.FILE_NAME + theType.getExtension();

        /* Protect the workbook access */
        try (GordianZipWriteFile myZipFile = myZips.createZipFile(pZipLock, pZipStream);
             OutputStream myStream = myZipFile.createOutputStream(new File(myName), false)) {
            /* Initialise the WorkBook */
            initialiseWorkBook(theType);

            /* Write the data to the work book */
            writeWorkBook(myStream);

        } catch (IOException
                 | OceanusException e) {
            /* Report the error */
            throw new PrometheusIOException("Failed to create Backup Workbook", e);
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

    /**
     * Create the list of sheets to write.
     *
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    private void initialiseWorkBook(final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Create the workbook attached to the output stream */
        theWorkBook = PrometheusSheetProvider.newWorkBook(pType);

        /* Initialise the list */
        theSheets = new ArrayList<>();

        /* Loop through the list types */
        for (PrometheusCryptographyDataType myType : PrometheusCryptographyDataType.values()) {
            /* Create the sheet */
            theSheets.add(newSheet(myType));
        }

        /* register additional sheets */
        registerSheets();
    }

    /**
     * Create new sheet of required type.
     *
     * @param pListType the list type
     * @return the new sheet
     */
    private PrometheusSheetDataItem<?> newSheet(final PrometheusCryptographyDataType pListType) {
        /* Switch on list Type */
        return switch (pListType) {
            case CONTROLDATA -> new PrometheusSheetControlData(this);
            case CONTROLKEY -> new PrometheusSheetControlKey(this);
            case CONTROLKEYSET -> new PrometheusSheetControlKeySet(this);
            case DATAKEYSET -> new PrometheusSheetDataKeySet(this);
            default -> throw new IllegalArgumentException(pListType.toString());
        };
    }

    /**
     * Write the WorkBook.
     *
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeWorkBook(final OutputStream pStream) throws OceanusException {
        /* Obtain the active profile */
        final OceanusProfile myTask = theReport.getActiveTask();

        /* Declare the number of stages */
        theReport.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        for (PrometheusSheetDataItem<?> mySheet : theSheets) {
            /* Access the next sheet */
            /* Write data for the sheet */
            myTask.startTask(mySheet.toString());
            mySheet.writeSpreadSheet();
        }

        /* If we have built all the sheets */
        theReport.setNewStage("Writing");

        /* If we have created the workbook OK */
        /* Write it out to disk and close the stream */
        myTask.startTask("Saving");
        theWorkBook.saveToStream(pStream);
    }
}
