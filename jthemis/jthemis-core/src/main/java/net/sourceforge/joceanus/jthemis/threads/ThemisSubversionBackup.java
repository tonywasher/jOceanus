/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jthemis.threads;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThread;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.svn.tasks.ThemisBackup;

/**
 * Thread to handle subVersion backups.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class ThemisSubversionBackup<N, I>
        implements MetisThread<Void, N, I> {
    @Override
    public String getTaskName() {
        return ThemisThreadId.BACKUPSVN.toString();
    }

    @Override
    public Void performTask(final MetisToolkit<N, I> pToolkit) throws OceanusException {
        /* Access details from toolkit */
        MetisThreadManager<N, I> myManager = pToolkit.getThreadManager();
        MetisPreferenceManager myPreferences = pToolkit.getPreferenceManager();
        GordianHashManager mySecureMgr = pToolkit.getSecurityManager();

        /* Create backup */
        ThemisBackup myAccess = new ThemisBackup(myManager, myPreferences);
        myAccess.backUpRepositories(mySecureMgr);

        /* Return nothing */
        return null;
    }
}
