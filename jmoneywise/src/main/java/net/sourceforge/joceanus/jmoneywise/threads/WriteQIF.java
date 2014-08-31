/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.threads;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseCancelException;
import net.sourceforge.joceanus.jmoneywise.JMoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFPreference;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFFile;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFParser;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFWriter;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.threads.WorkerThread;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * WorkerThread extension to create a QIF archive.
 */
public class WriteQIF
        extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "QIF Creation";

    /**
     * Windows CharacterSet.
     */
    private static final String NAME_CHARSET = "Windows-1252";

    /**
     * Delete error text.
     */
    private static final String ERROR_DELETE = "Failed to delete file";

    /**
     * Data View.
     */
    private final View theView;

    /**
     * Thread Status.
     */
    private final MoneyWiseStatus theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public WriteQIF(final MoneyWiseStatus pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theView = pStatus.getView();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JOceanusException {
        /* Initialise the status window */
        theStatus.initTask("Analysing Data");

        /* Assume failure */
        boolean bSuccess = false;

        /* Load configuration */
        PreferenceManager myMgr = theView.getPreferenceMgr();
        QIFPreference myPrefs = myMgr.getPreferenceSet(QIFPreference.class);

        /* Create QIF file */
        QIFFile myQFile = QIFFile.buildQIFFile(theView, myPrefs);

        /* Initialise the status window */
        theStatus.initTask("Writing QIF file");

        /* Determine name of output file */
        String myDirectory = myPrefs.getStringValue(QIFPreference.NAME_QIFDIR);
        QIFType myType = myPrefs.getEnumValue(QIFPreference.NAME_QIFTYPE, QIFType.class);

        /* Determine the output name */
        File myOutFile = new File(myDirectory + File.separator + myType.getFileName());

        /* Create the Writer */
        QIFWriter myQWriter = new QIFWriter(theStatus, myQFile);

        /* Protect against exceptions */
        try (FileOutputStream myOutput = new FileOutputStream(myOutFile);
             BufferedOutputStream myBuffer = new BufferedOutputStream(myOutput);
             OutputStreamWriter myWriter = new OutputStreamWriter(myBuffer, NAME_CHARSET)) {

            /* Output the data */
            boolean isSuccess = myQWriter.writeFile(myWriter);
            myWriter.close();
            bSuccess = isSuccess;

            /* Check for cancellation */
            if (!isSuccess) {
                throw new JMoneyWiseCancelException("Operation Cancelled");
            }

        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to write to file: " + myOutFile.getName(), e);
        } finally {
            /* Delete the file */
            if ((!bSuccess) && (!myOutFile.delete())) {
                /* Nothing that we can do. At least we tried */
                theStatus.getLogger().error(ERROR_DELETE);
            }
        }

        /* Check for cancellation */
        if (!bSuccess) {
            throw new JMoneyWiseCancelException("Operation Cancelled");
        }

        /* Create the Parser */
        QIFParser myQParser = new QIFParser(myQFile.getFileType());

        /* Protect against exceptions */
        try (FileInputStream myInput = new FileInputStream(myOutFile);
             InputStreamReader myReader = new InputStreamReader(myInput, NAME_CHARSET);
             BufferedReader myBuffer = new BufferedReader(myReader)) {

            /* Load the data */
            myQParser.loadFile(myBuffer);

            /* Check that we successfully parsed the file */
            QIFFile myNewFile = myQParser.getFile();
            if (!myNewFile.equals(myQFile)) {
                theStatus.getLogger().error("Parsed file does not match source");
            }

        } catch (IOException e) {
            /* Report the error */
            throw new JMoneyWiseIOException("Failed to load to file: " + myOutFile.getName(), e);
        }

        /* Return nothing */
        return null;
    }
}
