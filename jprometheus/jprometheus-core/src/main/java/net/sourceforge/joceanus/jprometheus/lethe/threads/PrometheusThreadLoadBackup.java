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
import net.sourceforge.joceanus.jmetis.atlas.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.lethe.views.DataControl;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysFileSelector;

/**
 * Thread to load changes from an encrypted backup. Once the backup is loaded, the current database
 * is loaded and the backup is re-based onto the database so that a correct list of additions,
 * changes and deletions is built. These changes remain in memory and should be committed to the
 * database later.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data type enum class
 * @param <N> the node type
 * @param <I> the icon type
 */
public class PrometheusThreadLoadBackup<T extends DataSet<T, E>, E extends Enum<E>, N, I>
        implements MetisThread<T, N, I> {
    /**
     * Select Backup Task.
     */
    private static final String TASK_SELECTFILE = PrometheusThreadResource.TASK_SELECT_BACKUP.getValue();

    /**
     * Data control.
     */
    private final DataControl<T, E, ?, ?> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public PrometheusThreadLoadBackup(final DataControl<T, E, N, I> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RESTOREBACKUP.toString();
    }

    @Override
    public T performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        final MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        final GordianHashManager mySecurityMgr = pToolkit.getSecurityManager();

        /* Initialise the status window */
        myManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        final MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        final PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        final File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        final TethysFileSelector myDialog = pToolkit.getGuiFactory().newFileSelector();
        myDialog.setTitle(TASK_SELECTFILE);
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianZipReadFile.ZIPFILE_EXT);
        final File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            myManager.throwCancelException();
        }

        /* Load workbook */
        final PrometheusSpreadSheet<T> mySheet = theControl.getSpreadSheet();
        final T myData = theControl.getNewData();
        mySheet.loadBackup(myManager, mySecurityMgr, myData, myFile);

        /* Create interface */
        final PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        final T myStore = theControl.getNewData();
        myDatabase.loadDatabase(myManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(myManager);

        /* Re-base the loaded backup onto the database image */
        myData.reBase(myManager, myStore);

        /* State that we have completed */
        myManager.setCompletion();

        /* Return the Data */
        return myData;
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
