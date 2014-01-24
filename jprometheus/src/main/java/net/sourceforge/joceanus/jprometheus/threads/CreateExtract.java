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

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.sheet.WorkBookType;
import net.sourceforge.joceanus.jprometheus.JPrometheusDataException;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.preferences.BackupPreferences;
import net.sourceforge.joceanus.jprometheus.sheets.SpreadSheet;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Thread to create a extract spreadsheet of a data set.
 * @author Tony Washer
 * @param <T> the DataSet type
 * @param <E> the data list enum class
 */
public class CreateExtract<T extends DataSet<T, E>, E extends Enum<E>>
        extends WorkerThread<Void> {
    /**
     * Task description.
     */
    private static final String TASK_NAME = "Extract Creation";

    /**
     * Data Control.
     */
    private final DataControl<T, E> theControl;

    /**
     * Thread Status.
     */
    private final ThreadStatus<T, E> theStatus;

    /**
     * Constructor (Event Thread).
     * @param pStatus the thread status
     */
    public CreateExtract(final ThreadStatus<T, E> pStatus) {
        /* Call super-constructor */
        super(TASK_NAME, pStatus);

        /* Store passed parameters */
        theStatus = pStatus;
        theControl = pStatus.getControl();

        /* Show the status window */
        showStatusBar();
    }

    @Override
    public Void performTask() throws JOceanusException {
        T myData = null;
        boolean doDelete = false;
        File myFile = null;

        /* Catch Exceptions */
        try {
            /* Initialise the status window */
            theStatus.initTask("Creating Extract");

            /* Access the Sheet preferences */
            PreferenceManager myMgr = theControl.getPreferenceMgr();
            BackupPreferences myProperties = myMgr.getPreferenceSet(BackupPreferences.class);

            /* Determine the archive name */
            File myBackupDir = new File(myProperties.getStringValue(BackupPreferences.NAME_BACKUP_DIR));
            String myPrefix = myProperties.getStringValue(BackupPreferences.NAME_BACKUP_PFIX);
            WorkBookType myType = myProperties.getEnumValue(BackupPreferences.NAME_BACKUP_TYPE, WorkBookType.class);

            /* Determine the name of the file to build */
            myFile = new File(myBackupDir.getPath() + File.separator + myPrefix + myType.getExtension());

            /* Create extract */
            SpreadSheet<T> mySheet = theControl.getSpreadSheet();
            mySheet.createExtract(theStatus, theControl.getData(), myFile);

            /* File created, so delete on error */
            doDelete = true;

            /* Initialise the status window */
            theStatus.initTask("Reading Extract");

            /* Load workbook */
            myData = mySheet.loadExtract(theStatus, myFile);

            /* Initialise the status window */
            theStatus.initTask("Re-applying Security");

            /* Initialise the security, from the original data */
            myData.initialiseSecurity(theStatus, theControl.getData());

            /* Initialise the status window */
            theStatus.initTask("Verifying Extract");

            /* Create a difference set between the two data copies */
            DataSet<T, ?> myDiff = myData.getDifferenceSet(theControl.getData());

            /* If the difference set is non-empty */
            if (!myDiff.isEmpty()) {
                /* Throw an exception */
                throw new JPrometheusDataException(myDiff, "Extract is inconsistent");
            }

            /* OK so switch off flag */
            doDelete = false;

            /* Catch any exceptions */
        } finally {
            /* Delete the file */
            if ((doDelete) && (!myFile.delete())) {
                doDelete = false;
            }
        }

        /* Return nothing */
        return null;
    }
}
