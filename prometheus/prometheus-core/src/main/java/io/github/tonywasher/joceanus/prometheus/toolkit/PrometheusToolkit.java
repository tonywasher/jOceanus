/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.toolkit;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.gordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.gordianknot.api.lock.GordianPasswordLockSpec;
import io.github.tonywasher.joceanus.metis.toolkit.MetisToolkit;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusPreferenceManager;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusPreferenceSecurity.PrometheusSecurityPreferences;
import io.github.tonywasher.joceanus.prometheus.preference.PrometheusPreferenceView;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityGenerator;
import io.github.tonywasher.joceanus.prometheus.security.PrometheusSecurityPasswordManager;
import net.sourceforge.joceanus.tethys.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;

/**
 * Prometheus Toolkit.
 */
public class PrometheusToolkit {
    /**
     * Toolkit.
     */
    private final MetisToolkit theToolkit;

    /**
     * Preference Manager.
     */
    private final PrometheusPreferenceManager thePreferenceManager;

    /**
     * Password Manager.
     */
    private final PrometheusSecurityPasswordManager thePasswordMgr;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     * @throws OceanusException on error
     */
    public PrometheusToolkit(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Store parameters */
        theToolkit = new MetisToolkit(pFactory, false);

        /* Access components */
        thePreferenceManager = new PrometheusPreferenceManager(theToolkit.getViewerManager());
        theToolkit.setUpColors(thePreferenceManager);

        /* Create the passwordManager */
        final PrometheusSecurityPreferences myPreferences = thePreferenceManager.getPreferenceSet(PrometheusSecurityPreferences.class);
        thePasswordMgr = newPasswordManager(myPreferences.getFactoryType(), myPreferences.getPasswordLockSpec());

        /* Set this as the threadData */
        final TethysUIThreadManager myThreadMgr = theToolkit.getThreadManager();
        myThreadMgr.setThreadData(this);
    }

    /**
     * Obtain the preference manager.
     *
     * @return the preference manager
     */
    public PrometheusPreferenceManager getPreferenceManager() {
        return thePreferenceManager;
    }

    /**
     * Obtain the password manager.
     *
     * @return the password manager
     */
    public PrometheusSecurityPasswordManager getPasswordManager() {
        return thePasswordMgr;
    }

    /**
     * Obtain the Program Definitions.
     *
     * @return the definitions
     */
    public TethysUIProgram getProgramDefinitions() {
        return theToolkit.getProgramDefinitions();
    }

    /**
     * Obtain the Toolkit.
     *
     * @return the toolkit
     */
    public MetisToolkit getToolkit() {
        return theToolkit;
    }

    /**
     * Create a Password Manager.
     *
     * @param pFactoryType the factoryType
     * @param pLockSpec    the lockSpec
     * @return the manager
     * @throws OceanusException on error
     */
    private PrometheusSecurityPasswordManager newPasswordManager(final GordianFactoryType pFactoryType,
                                                                 final GordianPasswordLockSpec pLockSpec) throws OceanusException {
        return PrometheusSecurityGenerator.newPasswordManager(getToolkit().getGuiFactory(), pFactoryType, pLockSpec);
    }

    /**
     * Create a new Preference View.
     *
     * @return the view
     */
    public PrometheusPreferenceView newPreferenceView() {
        return new PrometheusPreferenceView(getToolkit().getGuiFactory(), thePreferenceManager);
    }
}
