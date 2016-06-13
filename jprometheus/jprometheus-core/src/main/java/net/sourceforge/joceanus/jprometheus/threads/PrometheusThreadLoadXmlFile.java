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
package net.sourceforge.joceanus.jprometheus.threads;

import java.io.File;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jgordianknot.zip.GordianZipReadFile;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.PrometheusCancelException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValuesFormatter;
import net.sourceforge.joceanus.jprometheus.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferenceKey;
import net.sourceforge.joceanus.jprometheus.preference.PrometheusBackup.PrometheusBackupPreferences;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
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
        myDialog.setTitle("Select Backup to restore");
        myDialog.setInitialDirectory(myBackupDir);
        myDialog.setExtension(GordianZipReadFile.ZIPFILE_EXT);
        File myFile = myDialog.selectFile();

        /* If we did not select a file */
        if (myFile == null) {
            /* Throw cancelled exception */
            throw new PrometheusCancelException("Operation Cancelled");
        }

        /* Create a new formatter */
        DataValuesFormatter<T, E> myFormatter = new DataValuesFormatter<>(myManager, mySecurity);

        /* Load data */
        T myNewData = theControl.getNewData();
        boolean bContinue = myFormatter.loadZipFile(myNewData, myFile);

        /* Check for cancellation */
        if (!bContinue) {
            throw new PrometheusCancelException("Operation Cancelled");
        }

        /* Initialise the status window */
        myManager.initTask("Accessing DataStore");

        /* Create interface */
        PrometheusDataStore<T> myDatabase = theControl.getDatabase();

        /* Load underlying database */
        T myStore = theControl.getNewData();
        myDatabase.loadDatabase(myManager, myStore);

        /* Check security on the database */
        myStore.checkSecurity(myManager);

        /* Initialise the status window */
        myManager.initTask("Re-applying Security");

        /* Initialise the security, either from database or with a new security control */
        myNewData.initialiseSecurity(myManager, myStore);

        /* Re-base the loaded backup onto the database image */
        myNewData.reBase(myManager, myStore);

        /* Return the Data */
        return myNewData;
    }

    @Override
    public void processResult(final T pResult) {
        theControl.setData(pResult);
    }
}
