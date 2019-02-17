/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.threads;

import net.sourceforge.joceanus.jgordianknot.api.impl.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnBackup;

/**
 * Thread to handle subVersion backups.
 */
public class ThemisSubversionBackup
        implements MetisThread<Void> {
    @Override
    public String getTaskName() {
        return ThemisThreadId.BACKUPSVN.toString();
    }

    @Override
    public Void performTask(final MetisToolkit pToolkit) throws OceanusException {
        /* Access details from toolkit */
        final MetisThreadManager myManager = pToolkit.getThreadManager();
        final MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();
        final GordianSecurityManager mySecureMgr = pToolkit.getSecurityManager();

        /* Create backup */
        final ThemisSvnBackup myAccess = new ThemisSvnBackup(myManager, myPreferences);
        myAccess.backUpRepositories(mySecureMgr);

        /* Return nothing */
        return null;
    }
}
