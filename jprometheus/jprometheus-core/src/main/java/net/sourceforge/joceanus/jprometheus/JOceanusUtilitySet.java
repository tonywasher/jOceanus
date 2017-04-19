/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus;

import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.lethe.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;

/**
 * JOceanus Utility Set.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class JOceanusUtilitySet<N, I> {
    /**
     * Toolkit.
     */
    private final MetisToolkit<N, I> theToolkit;

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
     * GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * Viewer Manager.
     */
    private final MetisViewerManager theViewerMgr;

    /**
     * Thread Manager.
     */
    private final MetisThreadManager<N, I> theThreadMgr;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     */
    protected JOceanusUtilitySet(final MetisToolkit<N, I> pToolkit) {
        /* Access components */
        theToolkit = pToolkit;
        theSecureMgr = pToolkit.getSecurityManager();
        thePreferenceMgr = pToolkit.getPreferenceManager();
        theFormatter = pToolkit.getFormatter();
        theGuiFactory = pToolkit.getGuiFactory();
        theViewerMgr = pToolkit.getViewerManager();
        theThreadMgr = pToolkit.getThreadManager();
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
     * Obtain the GUI Factory.
     * @return the factory
     */
    public TethysGuiFactory<N, I> getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the viewer manager.
     * @return the manager
     */
    public MetisViewerManager getViewerManager() {
        return theViewerMgr;
    }

    /**
     * Obtain the Thread Manager.
     * @return the thread manager
     */
    public MetisThreadManager<N, I> getThreadManager() {
        return theThreadMgr;
    }

    /**
     * Obtain the Toolkit.
     * @return the toolkit
     */
    public MetisToolkit<N, I> getToolkit() {
        return theToolkit;
    }
}
