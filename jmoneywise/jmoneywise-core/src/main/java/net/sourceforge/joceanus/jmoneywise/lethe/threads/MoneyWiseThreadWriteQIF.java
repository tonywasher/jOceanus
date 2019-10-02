/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.threads;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadData;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseIOException;
import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFPreference.MoneyWiseQIFPreferenceKey;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFPreference.MoneyWiseQIFPreferences;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QIFType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFFile;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFParser;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFStreamWriter;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFWriter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.MoneyWiseView;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;

/**
 * WorkerThread extension to create a QIF archive.
 */
public class MoneyWiseThreadWriteQIF
        implements MetisThread<Void> {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseThreadWriteQIF.class);

    /**
     * Data View.
     */
    private final MoneyWiseView theView;

    /**
     * Constructor (Event Thread).
     * @param pView the view
     */
    public MoneyWiseThreadWriteQIF(final MoneyWiseView pView) {
        theView = pView;
    }

    @Override
    public String getTaskName() {
        return MoneyWiseThreadId.CREATEQIF.toString();
    }

    @Override
    public Void performTask(final MetisThreadData pThreadData) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myToolkit = (PrometheusToolkit) pThreadData;
        final MetisThreadManager myManager = myToolkit.getThreadManager();

        /* Initialise the status window */
        myManager.initTask("Analysing Data");

        /* Load configuration */
        final MetisPreferenceManager myMgr = theView.getPreferenceManager();
        final MoneyWiseQIFPreferences myPrefs = myMgr.getPreferenceSet(MoneyWiseQIFPreferences.class);

        /* Obtain the analysis */
        final Analysis myAnalysis = theView.getAnalysisManager().getAnalysis();

        /* Create QIF file */
        final QIFFile myQFile = QIFFile.buildQIFFile(theView.getData(), myAnalysis, myPrefs);

        /* Initialise the status window */
        myManager.initTask("Writing QIF file");

        /* Determine name of output file */
        final String myDirectory = myPrefs.getStringValue(MoneyWiseQIFPreferenceKey.QIFDIR);
        final QIFType myType = myPrefs.getEnumValue(MoneyWiseQIFPreferenceKey.QIFTYPE, QIFType.class);

        /* Determine the output name */
        final File myOutFile = new File(myDirectory + File.separator + myType.getFileName());

        /* Create the Writer */
        final QIFWriter myQWriter = new QIFWriter(myManager, myQFile);

        /* Protect against exceptions */
        boolean writeFailed = false;
        try (QIFStreamWriter myWriter = new QIFStreamWriter(myOutFile)) {
            /* Output the data */
            myQWriter.writeFile(myWriter);

        } catch (IOException e) {
            /* Report the error */
            writeFailed = true;
            throw new MoneyWiseIOException("Failed to write to file: " + myOutFile.getName(), e);
        } finally {
            /* Try to delete the file if required */
            if (writeFailed) {
                MetisToolkit.cleanUpFile(myOutFile);
            }
        }

        /* Create the Parser */
        final QIFParser myQParser = new QIFParser(myQFile.getFileType());

        /* Protect against exceptions */
        try (FileInputStream myInput = new FileInputStream(myOutFile);
             InputStreamReader myReader = new InputStreamReader(myInput, StandardCharsets.ISO_8859_1);
             BufferedReader myBuffer = new BufferedReader(myReader)) {

            /* Load the data */
            myQParser.loadFile(myBuffer);

            /* Check that we successfully parsed the file */
            final QIFFile myNewFile = myQParser.getFile();
            if (!myNewFile.equals(myQFile)) {
                LOGGER.error("Parsed file does not match source");
            }

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to load to file: " + myOutFile.getName(), e);
        }

        /* State that we have completed */
        myManager.setCompletion();

        /* Return nothing */
        return null;
    }
}
