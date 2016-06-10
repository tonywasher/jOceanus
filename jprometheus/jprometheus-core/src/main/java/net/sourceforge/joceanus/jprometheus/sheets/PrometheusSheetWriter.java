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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianKeySetHash;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipWriteFile;
import net.sourceforge.joceanus.jmetis.data.MetisProfile;
import net.sourceforge.joceanus.jmetis.sheet.MetisDataWorkBook;
import net.sourceforge.joceanus.jmetis.sheet.MetisWorkBookType;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jprometheus.PrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.PrometheusIOException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Write control for spreadsheets.
 * @author Tony Washer
 * @param <T> the DataSet type
 */
public abstract class PrometheusSheetWriter<T extends DataSet<T, ?>> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(PrometheusSheetWriter.class);

    /**
     * Delete error text.
     */
    private static final String ERROR_DELETE = "Failed to delete file";

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
        GordianHashManager mySecure = pData.getSecurity();
        GordianKeySetHash myBase = pData.getKeySetHash();
        GordianKeySetHash myHash = mySecure.similarKeySetHash(myBase);

        /* Assume failure */
        boolean bSuccess = false;
        String myName = PrometheusSpreadSheet.FILE_NAME + pType.getExtension();

        /* Protect the workbook access */
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

            /* Set success to avoid deleting file */
            bSuccess = true;

        } catch (IOException e) {
            /* Report the error */
            throw new PrometheusIOException("Failed to create Backup Workbook: " + pFile.getName(), e);
        } finally {
            /* Delete the file on error */
            if ((!bSuccess) && (!pFile.delete())) {
                /* Nothing that we can do. At least we tried */
                LOGGER.error(ERROR_DELETE);
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
     * @throws IOException on write error
     */
    private void writeWorkBook(final OutputStream pStream) throws OceanusException, IOException {
        /* Obtain the active profile */
        MetisProfile myTask = theReport.getActiveTask();

        /* Declare the number of stages */
        boolean bContinue = theReport.setNumStages(theSheets.size() + 1);

        /* Loop through the sheets */
        Iterator<PrometheusSheetDataItem<?, ?>> myIterator = theSheets.iterator();
        while ((bContinue) && (myIterator.hasNext())) {
            /* Access the next sheet */
            PrometheusSheetDataItem<?, ?> mySheet = myIterator.next();

            /* Write data for the sheet */
            myTask.startTask(mySheet.toString());
            bContinue = mySheet.writeSpreadSheet();
        }

        /* If we have built all the sheets */
        if (bContinue) {
            bContinue = theReport.setNewStage("Writing");
        }

        /* If we have created the workbook OK */
        if (bContinue) {
            /* Write it out to disk and close the stream */
            myTask.startTask("Saving");
            theWorkBook.saveToStream(pStream);
        }

        /* Check for cancellation */
        if (!bContinue) {
            throw new PrometheusCancelException("Operation Cancelled");
        }
    }
}
