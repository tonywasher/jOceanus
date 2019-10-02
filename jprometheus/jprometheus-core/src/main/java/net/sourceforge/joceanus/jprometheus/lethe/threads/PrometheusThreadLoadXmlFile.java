/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.lethe.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadData;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;

/**
 * LoaderThread extension to load an XML backup.
 * @param <T> the DataSet type
 * @param <E> the Data list type
 */
public class PrometheusThreadLoadXmlFile<T extends DataSet<T, E>, E extends Enum<E>>
        implements MetisThread<T> {
    /**
     * Select Backup Task.
     */
    private static final String TASK_SELECTFILE = PrometheusThreadResource.TASK_SELECT_BACKUP.getValue();

    /**
     * Data control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadLoadXmlFile(final DataControl<T, E> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RESTOREXML.toString();
    }

    @Override
    public T performTask(final MetisThreadData pThreadData) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myToolkit = (PrometheusToolkit) pThreadData;
        final MetisThreadManager myManager = myToolkit.getThreadManager();
        final GordianSecurityManager mySecurity = myToolkit.getSecureManager();

        /* Initialise the status window */
        myManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        final File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        final TethysFileSelector myDialog = myToolkit.getGuiFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianSecurityManager.SECUREZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            myManager.throwCancelException();
        }

        /* Create a new formatter */
        final DataValuesFormatter<T, E> myFormatter = new DataValuesFormatter<>(myManager, mySecurity);

        /* Load data */
        final T myNewData = theControl.getNewData();
        myFormatter.loadZipFile(myNewData, myFile);

        /* Create interface */
        final PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        final T myStore = theControl.getNewData();
        myDatabase.loadDatabase(myManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(myManager);

        /* Initialise the security, either from database or with a new security control */
        myNewData.initialiseSecurity(myManager, myStore);

        /* Re-base the loaded backup onto the database image */
        myNewData.reBase(myManager, myStore);

        /* State that we have completed */
        myManager.setCompletion();

        /* Return the Data */
        return myNewData;
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
