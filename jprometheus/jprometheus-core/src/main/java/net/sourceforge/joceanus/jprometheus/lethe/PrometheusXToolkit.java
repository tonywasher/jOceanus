/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2023 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jmetis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceManager;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceSecurity.PrometheusSecurityPreferences;
import net.sourceforge.joceanus.jprometheus.atlas.preference.PrometheusPreferenceView;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Prometheus Toolkit.
 */
public class PrometheusXToolkit {
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
    private final GordianPasswordManager thePasswordMgr;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @throws OceanusException on error
     */
    public PrometheusXToolkit(final TethysUIFactory<?> pFactory) throws OceanusException {
        /* Store parameters */
        theToolkit = new MetisToolkit(pFactory, false);

        /* Access components */
        thePreferenceManager = new PrometheusPreferenceManager(theToolkit.getViewerManager());
        theToolkit.setUpColors(thePreferenceManager);
        final TethysUIThreadManager myThreadMgr = theToolkit.getThreadManager();

        /* Create the passwordManager */
        final PrometheusSecurityPreferences myPreferences = thePreferenceManager.getPreferenceSet(PrometheusSecurityPreferences.class);
        thePasswordMgr = newPasswordManager(myPreferences.getFactoryType(), myPreferences.getSecurityPhrase());

        /* Set this as the threadData */
        myThreadMgr.setThreadData(this);
    }

    /**
     * Obtain the preference manager.
     * @return the preference manager
     */
    public PrometheusPreferenceManager getPreferenceManager() {
        return thePreferenceManager;
    }

    /**
     * Obtain the password manager.
     * @return the password manager
     */
    public GordianPasswordManager getPasswordManager() {
        return thePasswordMgr;
    }

    /**
     * Obtain the Program Definitions.
     * @return the definitions
     */
    public TethysUIProgram getProgramDefinitions() {
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
     * Create a Password Manager.
     * @param pFactoryType the factoryType
     * @param pSecurityPhrase the security phrase
     * @return the manager
     * @throws OceanusException on error
     */
    private GordianPasswordManager newPasswordManager(final GordianFactoryType pFactoryType,
                                                      final char[] pSecurityPhrase) throws OceanusException {
        return GordianGenerator.newPasswordManager(getToolkit().getGuiFactory(), pFactoryType, pSecurityPhrase);
    }

    /**
     * Create a new Preference View.
     * @return the view
     */
    public PrometheusPreferenceView newPreferenceView() {
        return new PrometheusPreferenceView(getToolkit().getGuiFactory(), thePreferenceManager);
    }
}