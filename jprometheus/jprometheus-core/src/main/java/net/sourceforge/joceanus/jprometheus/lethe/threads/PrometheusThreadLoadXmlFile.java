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
package net.sourceforge.joceanus.jprometheus.lethe.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.lethe.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;

/**
 * LoaderThread extension to load an XML backup.
 * @param <T> the DataSet type
 * @param <E> the Data list type
 * @param <N> the node type
 * @param <I> the icon type
 */
public class PrometheusThreadLoadXmlFile<T extends DataSet<T, E>, E extends Enum<E>, N, I>
        implements MetisThread<T, N, I> {
    /**
     * Select Backup Task.
     */
    private static final String TASK_SELECTFILE = PrometheusThreadResource.TASK_SELECT_BACKUP.getValue();

    /**
     * Data control.
     */
    private final DataControl<T, E, N, I> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadLoadXmlFile(final DataControl<T, E, N, I> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RESTOREXML.toString();
    }

    @Override
    public T performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        GordianHashManager mySecurity = pToolkit.getSecurityManager();

        /* Initialise the status window */
        myManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        TethysFileSelector myDialog = pToolkit.getGuiFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianZipReadFile.ZIPFILE_EXT);
        File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            myManager.throwCancelException();
        }

        /* Create a new formatter */
        DataValuesFormatter<T, E> myFormatter = new DataValuesFormatter<>(myManager, mySecurity);

        /* Load data */
        T myNewData = theControl.getNewData();
        myFormatter.loadZipFile(myNewData, myFile);

        /* Create interface */
        PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        T myStore = theControl.getNewData();
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
