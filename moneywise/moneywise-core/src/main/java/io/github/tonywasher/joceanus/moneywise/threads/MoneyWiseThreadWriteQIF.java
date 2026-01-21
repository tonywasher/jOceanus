/*
 * MoneyWise: Finance Application
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
package io.github.tonywasher.joceanus.moneywise.threads;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogManager;
import io.github.tonywasher.joceanus.oceanus.logger.OceanusLogger;
import io.github.tonywasher.joceanus.metis.preference.MetisPreferenceManager;
import io.github.tonywasher.joceanus.metis.toolkit.MetisToolkit;
import io.github.tonywasher.joceanus.moneywise.exc.MoneyWiseIOException;
import io.github.tonywasher.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferenceKey;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFPreference.MoneyWiseQIFPreferences;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFFile;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFParser;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFStreamWriter;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFWriter;
import io.github.tonywasher.joceanus.moneywise.views.MoneyWiseView;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThread;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * WorkerThread extension to create a QIF archive.
 */
public class MoneyWiseThreadWriteQIF
        implements TethysUIThread<Void> {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseThreadWriteQIF.class);

    /**
     * Data View.
     */
    private final MoneyWiseView theView;

    /**
     * Constructor (Event Thread).
     *
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
    public Void performTask(final TethysUIThreadManager pManager) throws OceanusException {
        /* Initialise the status window */
        pManager.initTask("Analysing Data");

        /* Load configuration */
        final MetisPreferenceManager myMgr = theView.getPreferenceManager();
        final MoneyWiseQIFPreferences myPrefs = myMgr.getPreferenceSet(MoneyWiseQIFPreferences.class);

        /* Obtain the analysis */
        final MoneyWiseAnalysis myAnalysis = theView.getAnalysisManager().getAnalysis();

        /* Create QIF file */
        final MoneyWiseQIFFile myQFile = MoneyWiseQIFFile.buildQIFFile(theView.getData(), myAnalysis, myPrefs);

        /* Initialise the status window */
        pManager.initTask("Writing QIF file");

        /* Determine name of output file */
        final String myDirectory = myPrefs.getStringValue(MoneyWiseQIFPreferenceKey.QIFDIR);
        final MoneyWiseQIFType myType = myPrefs.getEnumValue(MoneyWiseQIFPreferenceKey.QIFTYPE, MoneyWiseQIFType.class);

        /* Determine the output name */
        final File myOutFile = new File(myDirectory + File.separator + myType.getFileName());

        /* Create the Writer */
        final MoneyWiseQIFWriter myQWriter = new MoneyWiseQIFWriter(theView.getGuiFactory(), pManager, myQFile);

        /* Protect against exceptions */
        boolean writeFailed = false;
        try (MoneyWiseQIFStreamWriter myWriter = new MoneyWiseQIFStreamWriter(myOutFile)) {
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
        final MoneyWiseQIFParser myQParser = new MoneyWiseQIFParser(theView.getGuiFactory(), myQFile.getFileType());

        /* Protect against exceptions */
        try (FileInputStream myInput = new FileInputStream(myOutFile);
             InputStreamReader myReader = new InputStreamReader(myInput, StandardCharsets.ISO_8859_1);
             BufferedReader myBuffer = new BufferedReader(myReader)) {

            /* Load the data */
            myQParser.loadFile(myBuffer);

            /* Check that we successfully parsed the file */
            final MoneyWiseQIFFile myNewFile = myQParser.getFile();
            if (!myNewFile.equals(myQFile)) {
                LOGGER.error("Parsed file does not match source");
            }

        } catch (IOException e) {
            /* Report the error */
            throw new MoneyWiseIOException("Failed to load to file: " + myOutFile.getName(), e);
        }

        /* State that we have completed */
        pManager.setCompletion();

        /* Return nothing */
        return null;
    }
}
