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
package net.sourceforge.joceanus.jprometheus;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;

/**
 * JOceanus Utility Set.
 */
public abstract class JOceanusUtilitySet {
    /**
     * Secure Manager.
     */
    private final GordianHashManager theSecureMgr;

    /**
     * Preference Manager.
     */
    private final MetisPreferenceManager thePreferenceMgr;

    /**
     * Data Formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * Constructor.
     * @param pSecureMgr the secure manager
     * @param pPrefMgr the preference manager
     */
    protected JOceanusUtilitySet(final GordianHashManager pSecureMgr,
                                 final MetisPreferenceManager pPrefMgr) {
        /* Store parameters */
        theSecureMgr = pSecureMgr;
        thePreferenceMgr = pPrefMgr;

        /* Create components */
        theFormatter = new MetisDataFormatter();
    }

    /**
     * Obtain the secure manager.
     * @return the secure manager
     */
    public GordianHashManager getSecureManager() {
        return theSecureMgr;
    }

    /**
     * Obtain the preference manager.
     * @return the preference manager
     */
    public MetisPreferenceManager getPreferenceManager() {
        return thePreferenceMgr;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public MetisDataFormatter getDataFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the viewer manager.
     * @return the manager
     */
    public abstract MetisViewerManager getViewerManager();
}
