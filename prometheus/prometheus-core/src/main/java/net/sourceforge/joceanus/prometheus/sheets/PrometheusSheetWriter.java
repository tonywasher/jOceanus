/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.gordianknot.api.base.GordianException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipFactory;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipLock;
import net.sourceforge.joceanus.gordianknot.api.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.metis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.exc.PrometheusIOException;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet.PrometheusCryptographyDataType;
import net.sourceforge.joceanus.prometheus.exc.PrometheusSecurityException;
import net.sourceforge.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetProvider;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBook;
import net.sourceforge.joceanus.prometheus.service.sheet.PrometheusSheetWorkBookType;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadStatusReport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Write control for spreadsheets.
 * @author Tony Washer
 */
public abstract class PrometheusSheetWriter {
    /**
     * Gui Factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

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
     * The WorkSheets.
     */
    private List<PrometheusSheetDataItem<?>> theSheets;

    /**
     * Constructor.
     * @param pFactory the gui factory
     * @param pReport the report
     */
    protected PrometheusSheetWriter(final TethysUIFactory<?> pFactory,
                                    final TethysUIThreadStatusReport pReport) {
        theGuiFactory = pFactory;
        theReport = pReport;
    }

    /**
     * get report.
     * @return the report
     */
    protected TethysUIThreadStatusReport getReport() {
        return theReport;
    }

    /**
     * get workbook.
     * @return the workbook
     */
    protected PrometheusSheetWorkBook getWorkBook() {
        return theWorkBook;
    }

    /**
     * get dataSet.
     * @return the dataSet
     */
    public PrometheusDataSet getData() {
        return theData;
    }

    /**
     * Add Sheet to list.
     * @param pSheet the sheet
     */
    protected void addSheet(final PrometheusSheetDataItem<?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Create a Backup Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final PrometheusDataSet pData,
                             final File pFile,
                             final PrometheusSheetWorkBookType pType) throws OceanusException {
        boolean writeFailed = false;
        try {
            createBackup(pData, new FileOutputStream(pFile), pType);
        } catch (IOException
                 | OceanusException e) {
            writeFailed = true;
            throw new PrometheusIOException("Failed to create backup Workbook", e);
        } finally {
            /* Try to delete the file if required */
            if (writeFailed) {
                MetisToolkit.cleanUpFile(pFile);
            }
        }
    }

    /**
     * Create a Backup Workbook.
     * @param pData Data to write out
     * @param pZipStream the backup file to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final PrometheusDataSet pData,
                             final OutputStream pZipStream,
                             final PrometheusSheetWorkBookType pType) throws OceanusException {
        /* Obtain the active profile */
        OceanusProfile myTask = theReport.getActiveTask();
        myTask = myTask.startTask("Writing");

        /* Protect against exceptions */
        try {
            /* Create a similar security control */
            final PrometheusSecurityPasswordManager myPasswordMgr = pData.getPasswordMgr();
            final GordianFactoryLock myBase = pData.getFactoryLock();
            final GordianFactoryLock myLock = myPasswordMgr.similarFactoryLock(myBase);
            final GordianZipFactory myZips = myPasswordMgr.getSecurityFactory().getZipFactory();
            final GordianZipLock myZipLock = myZips.zipLock(myLock);

            /* Assume failure */
            final String myName = PrometheusSpreadSheet.FILE_NAME + pType.getExtension();

            /* Protect the workbook access */
            try (GordianZipWriteFile myZipFile = myZips.createZipFile(myZipLock, pZipStream);
                 OutputStream myStream = myZipFile.createOutputStream(new File(myName), false)) {
                /* Record the DataSet */
                theData = pData;

                /* Initialise the WorkBook */
                initialiseWorkBook(pType);

                /* Write the data to the work book */
                writeWorkBook(myStream);

            } catch (IOException
                     | OceanusException e) {
                /* Report the error */
                throw new PrometheusIOException("Failed to create Backup Workbook", e);
            }
        } catch (GordianException e) {
            throw new PrometheusSecurityException(e);
        }

        /* Complete task */
        myTask.end();
    }

    /**
     * Register sheets.
     */
    protected abstract void registerSheets();

    /**
     * Create the list of sheets to write.
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
     * Write the WorkBook.
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
