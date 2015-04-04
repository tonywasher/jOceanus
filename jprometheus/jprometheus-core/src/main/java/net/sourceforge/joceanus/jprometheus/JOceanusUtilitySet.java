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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jprometheus/src/main/java/net/sourceforge/joceanus/jprometheus/JPrometheusCancelException.java $
 * $Revision: 543 $
 * $Author: Tony $
 * $Date: 2014-10-13 12:12:02 +0100 (Mon, 13 Oct 2014) $
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus;

import net.sourceforge.joceanus.jgordianknot.manager.SecureManager;
import net.sourceforge.joceanus.jmetis.data.JDataFormatter;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.ViewerManager;

/**
 * JOceanus Utility Set.
 */
public abstract class JOceanusUtilitySet {
    /**
     * Secure Manager.
     */
    private final SecureManager theSecureMgr;

    /**
     * Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Data Formatter.
     */
    private final JDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pSecureMgr the secure manager
     * @param pPrefMgr the preference manager
     */
    protected JOceanusUtilitySet(final SecureManager pSecureMgr,
                                 final PreferenceManager pPrefMgr) {
        /* Store parameters */
        theSecureMgr = pSecureMgr;
        thePreferenceMgr = pPrefMgr;

        /* Create components */
        theFormatter = new JDataFormatter();
    }

    /**
     * Obtain the secure manager.
     * @return the secure manager
     */
    public SecureManager getSecureManager() {
        return theSecureMgr;
    }

    /**
     * Obtain the preference manager.
     * @return the preference manager
     */
    public PreferenceManager getPreferenceManager() {
        return thePreferenceMgr;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public JDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the viewer manager.
     * @return the manager
     */
    public abstract ViewerManager getViewerManager();
}
