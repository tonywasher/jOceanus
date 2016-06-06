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
package net.sourceforge.joceanus.jprometheus.threads.swing;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.swing.MetisFileSelector;
import net.sourceforge.joceanus.jprometheus.PrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.threads.ThreadStatus;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * LoaderThread extension to load an XML backup.
 * @param <T> the DataSet type
 * @param <E> the Data list type
 */
public class LoadXmlFile<T extends DataSet<T, E>, E extends Enum<E>>
        extends LoaderThread<T, E> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Load Xml Backup";

    /**
     * Cancel error text.
     */
    private static final String ERROR_CANCEL = "Operation cancelled";

    /**
     * Data control.
     */
    private final DataControl<T, E, ?, ?> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T, E> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public LoadXmlFile(final ThreadStatus<T, E> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the Status bar */
        showStatusBar();
    }

    @Override
    public T performTask() throws OceanusException {
        /* Initialise the status window */
        theStatus.initTask(TASK_NAME);

        /* Access the Sheet preferences */
        MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));
        String myPrefix = myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPPFIX)
                          + CreateXmlFile.SUFFIX_FILE;

        /* Determine the name of the file to load */
        MetisFileSelector myDialog = new MetisFileSelector(theControl.getFrame(), "Select Backup to load", myBackupDir, myPrefix, GordianZipReadFile.ZIPFILE_EXT);
        myDialog.showDialog();
        File myFile = myDialog.getSelectedFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new PrometheusCancelException(ERROR_CANCEL);
        }

        /* Create a new formatter */
        DataValuesFormatter<T, E> myFormatter = new DataValuesFormatter<>(theStatus);

        /* Load data */
        T myNewData = theControl.getNewData();
        boolean bContinue = myFormatter.loadZipFile(myNewData, myFile);

        /* Check for cancellation */
        if (!bContinue) {
            throw new PrometheusCancelException(ERROR_CANCEL);
        }

        /* Initialise the status window */
        theStatus.initTask("Accessing DataStore");

        /* Create interface */
        PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        T myStore = myDatabase.loadDatabase(theStatus);

        /* Check security on the database */
        myStore.checkSecurity(theStatus);

        /* Initialise the status window */
        theStatus.initTask("Re-applying Security");

        /* Initialise the security, either from database or with a new security control */
        myNewData.initialiseSecurity(theStatus, myStore);

        /* Re-base the loaded backup onto the database image */
        myNewData.reBase(theStatus, myStore);

        /* Return the Data */
        return myNewData;
    }
}
