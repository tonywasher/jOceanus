/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2020 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSecurity.MetisSecurityPreferences;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadData;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;

/**
 * Prometheus Toolkit.
 */
public abstract class PrometheusToolkit
        implements MetisThreadData {
    /**
     * Toolkit.
     */
    private final MetisToolkit theToolkit;

    /**
     * Secure Manager.
     */
    private final GordianSecurityManager theSecureMgr;

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
    private final TethysGuiFactory theGuiFactory;

    /**
     * Viewer Manager.
     */
    private final MetisViewerManager theViewerMgr;

    /**
     * Thread Manager.
     */
    private final MetisThreadManager theThreadMgr;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    protected PrometheusToolkit(final MetisToolkit pToolkit) throws OceanusException {
        /* Access components */
        theToolkit = pToolkit;
        thePreferenceMgr = pToolkit.getPreferenceManager();
        theFormatter = pToolkit.getFormatter();
        theGuiFactory = pToolkit.getGuiFactory();
        theViewerMgr = pToolkit.getViewerManager();
        theThreadMgr = pToolkit.getThreadManager();

        /* Create the hashManager */
        final MetisSecurityPreferences myPreferences = thePreferenceMgr.getPreferenceSet(MetisSecurityPreferences.class);
        theSecureMgr = newSecurityManager(myPreferences.getFactoryType(),
                myPreferences.getSecurityPhrase(), myPreferences.getKeySetHashSpec());

        /* Set this as the threadData */
        theThreadMgr.setThreadData(this);
    }

    /**
     * Obtain the secure manager.
     * @return the secure manager
     */
    public GordianSecurityManager getSecureManager() {
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
    public TethysGuiFactory getGuiFactory() {
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
    public MetisThreadManager getThreadManager() {
        return theThreadMgr;
    }

    /**
     * Obtain the Program Definitions.
     * @return the definitions
     */
    public TethysProgram getProgramDefinitions() {
        return theToolkit.getProgramDefinitions();
    }

    /**
     * Obtain the Toolkit.
     * @return the toolkit
     */
    public MetisToolkit getToolkit() {
        return theToolkit;
    }

    /**
     * Create a Security Manager.
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @param pKeySetSpec the keySetHashSpec
     * @return the manager
     * @throws OceanusException on error
     */
    protected abstract GordianSecurityManager newSecurityManager(GordianFactoryType pFactoryType,
                                                                 char[] pSecurityPhrase,
                                                                 GordianKeySetHashSpec pKeySetSpec) throws OceanusException;
}
