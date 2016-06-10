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

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.PrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.PrometheusSpreadSheet;
import net.sourceforge.joceanus.jprometheus.threads.PrometheusThreadId;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
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
public class LoadBackup<T extends DataSet<T, E>, E extends Enum<E>, N, I>
        implements MetisThread<T, N, I> {
    /**
     * Data control.
     */
    private final DataControl<T, E, ?, ?> theControl;

    /**
     * Constructor (Event Thread).
     * @param pControl data control
     */
    public LoadBackup(final DataControl<T, E, N, I> pControl) {
        theControl = pControl;
    }

    @Override
    public String getTaskName() {
        return PrometheusThreadId.RESTOREBACKUP.toString();
    }

    @Override
    public T performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access the thread manager */
        MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        GordianHashManager mySecurityMgr = pToolkit.getSecurityManager();

        /* Initialise the status window */
        myManager.initTask(getTaskName());

        /* Access the Sheet preferences */
        MetisPreferenceManager myMgr = theControl.getPreferenceManager();
        PrometheusBackupPreferences myProperties = myMgr.getPreferenceSet(PrometheusBackupPreferences.class);

        /* Determine the archive name */
        File myBackupDir = new File(myProperties.getStringValue(PrometheusBackupPreferenceKey.BACKUPDIR));

        /* Determine the name of the file to load */
        TethysFileSelector myDialog = pToolkit.getGuiFactory().newFileSelector();
        myDialog.setTitle("Select Backup to restore");
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianZipReadFile.ZIPFILE_EXT);
        File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new PrometheusCancelException("Operation Cancelled");
        }

        /* Load workbook */
        PrometheusSpreadSheet<T> mySheet = theControl.getSpreadSheet();
        T myData = theControl.getNewData();
        mySheet.loadBackup(myManager, mySecurityMgr, myData, myFile);

        /* Initialise the status window */
        myManager.initTask("Accessing DataStore");

        /* Create interface */
        PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        T myStore = theControl.getNewData();
        myDatabase.loadDatabase(myManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(myManager);

        /* Re-base the loaded backup onto the database image */
        myData.reBase(myManager, myStore);

        /* Return the Data */
        return myData;
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
