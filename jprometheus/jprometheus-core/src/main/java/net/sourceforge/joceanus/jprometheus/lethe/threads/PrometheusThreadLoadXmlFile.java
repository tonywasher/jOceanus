/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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

import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianUtilities;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;
import net.sourceforge.joceanus.jtethys.ui.TethysThread;
import net.sourceforge.joceanus.jtethys.ui.TethysThreadManager;

/**
 * LoaderThread extension to load an XML backup.
 * @param <T> the DataSet type
 * @param <E> the Data list type
 */
public class PrometheusThreadLoadXmlFile<T extends DataSet<T, E>, E extends Enum<E>>
        implements TethysThread<T> {
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
    public T performTask(final TethysThreadManager pManager) throws OceanusException {
        /* Access the thread manager */
        final PrometheusToolkit myPromToolkit = (PrometheusToolkit) pManager.getThreadData();
        final GordianPasswordManager myPasswordMgr = myPromToolkit.getPasswordManager();

        /* Initialise the status window */
        pManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        final File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        final TethysFileSelector myDialog = myPromToolkit.getToolkit().getGuiFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianUtilities.SECUREZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            pManager.throwCancelException();
        }

        /* Create a new formatter */
        final DataValuesFormatter<T, E> myFormatter = new DataValuesFormatter<>(pManager, myPasswordMgr);

        /* Load data */
        final T myNewData = theControl.getNewData();
        myFormatter.loadZipFile(myNewData, myFile);

        /* Create interface */
        final PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        final T myStore = theControl.getNewData();
        myDatabase.loadDatabase(pManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(pManager);

        /* Initialise the security, either from database or with a new security control */
        myNewData.initialiseSecurity(pManager, myStore);

        /* Re-base the loaded backup onto the database image */
        myNewData.reBase(pManager, myStore);

        /* State that we have completed */
        pManager.setCompletion();

        /* Return the Data */
        return myNewData;
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
