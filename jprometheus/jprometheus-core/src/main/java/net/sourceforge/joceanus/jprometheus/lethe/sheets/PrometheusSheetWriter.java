/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.sheets;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jmetis.atlas.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Write control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class PrometheusSheetWriter<T extends DataSet<T, ?>> {
    /**
     * Report.
     */
    private final MetisThreadStatusReport theReport;

    /**
     * Writable spreadsheet.
     */
    private MetisDataWorkBook theWorkBook;

    /**
     * The DataSet.
     */
    private T theData;

    /**
     * The WorkSheets.
     */
    private List<PrometheusSheetDataItem<?, ?>> theSheets;

    /**
     * Constructor.
     * @param pReport the report
     */
    protected PrometheusSheetWriter(final MetisThreadStatusReport pReport) {
        theReport = pReport;
    }

    /**
     * get report.
     * @return the report
     */
    protected MetisThreadStatusReport getReport() {
        return theReport;
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
    protected void addSheet(final PrometheusSheetDataItem<?, ?> pSheet) {
        theSheets.add(pSheet);
    }

    /**
     * Create a Backup Workbook.
     * @param pData Data to write out
     * @param pFile the backup file to write to
     * @param pType the workBookType
     * @throws OceanusException on error
     */
    public void createBackup(final T pData,
                             final File pFile,
                             final MetisWorkBookType pType) throws OceanusException {
        /* Obtain the active profile */
        MetisProfile myTask = theReport.getActiveTask();
        myTask = myTask.startTask("Writing");

        /* Create a similar security control */
        final GordianHashManager mySecure = pData.getSecurity();
        final GordianKeySetHash myBase = pData.getKeySetHash();
        final GordianKeySetHash myHash = mySecure.similarKeySetHash(myBase);

        /* Assume failure */
        final String myName = PrometheusSpreadSheet.FILE_NAME + pType.getExtension();

        /* Protect the workbook access */
        boolean doDelete = true;
        try (GordianZipWriteFile myZipFile = new GordianZipWriteFile(myHash, pFile);
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
            doDelete = false;

        } catch (IOException e) {
            /* Report the error */
            throw new PrometheusIOException("Failed to create Backup Workbook: " + pFile.getName(), e);
        } finally {
            /* Try to delete the file if required */
            if (doDelete) {
                MetisToolkit.cleanUpFile(pFile);
            }
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
    private void initialiseWorkBook(final MetisWorkBookType pType) throws OceanusException {
        /* Create the workbook attached to the output stream */
        theWorkBook = new MetisDataWorkBook(pType);

        /* Initialise the list */
        theSheets = new ArrayList<>();

        /* Add security details */
        theSheets.add(new PrometheusSheetControlKey(this));
        theSheets.add(new PrometheusSheetDataKeySet(this));
        theSheets.add(new PrometheusSheetDataKey(this));
        theSheets.add(new PrometheusSheetControlData(this));

        /* register additional sheets */
        registerSheets();
    }

    /**
     * Write the WorkBook.
     * @param pStream the output stream
     * @throws OceanusException on error
     */
    private void writeWorkBook(final OutputStream pStream) throws OceanusException {
        /* Obtain the active profile */
        final MetisProfile myTask = theReport.getActiveTask();

        /* Declare the number of stages */
        theReport.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        final Iterator<PrometheusSheetDataItem<?, ?>> myIterator = theSheets.iterator();
        while (myIterator.hasNext()) {
            /* Access the next sheet */
            final PrometheusSheetDataItem<?, ?> mySheet = myIterator.next();

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
