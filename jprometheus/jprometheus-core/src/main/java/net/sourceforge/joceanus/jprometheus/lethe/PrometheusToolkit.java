/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.util.GordianGenerator;
import net.sourceforge.joceanus.jmetis.toolkit.MetisToolkit;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSecurity.MetisSecurityPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

/**
 * Prometheus Toolkit.
 */
public class PrometheusToolkit {
    /**
     * Toolkit.
     */
    private final MetisToolkit theToolkit;

    /**
     * Password Manager.
     */
    private final GordianPasswordManager thePasswordMgr;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     * @throws OceanusException on error
     */
    public PrometheusToolkit(final MetisToolkit pToolkit) throws OceanusException {
        /* Store parameters */
        theToolkit = pToolkit;

        /* Access components */
        final MetisPreferenceManager myPreferenceMgr = pToolkit.getPreferenceManager();
        final TethysUIThreadManager myThreadMgr = pToolkit.getThreadManager();

        /* Create the passwordManager */
        final MetisSecurityPreferences myPreferences = myPreferenceMgr.getPreferenceSet(MetisSecurityPreferences.class);
        thePasswordMgr = newPasswordManager(myPreferences.getFactoryType(),
                myPreferences.getSecurityPhrase(), myPreferences.getKeySetHashSpec());

        /* Set this as the threadData */
        myThreadMgr.setThreadData(this);
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
     * @param pKeySetSpec the keySetHashSpec
     * @return the manager
     * @throws OceanusException on error
     */
    private GordianPasswordManager newPasswordManager(final GordianFactoryType pFactoryType,
                                                        final char[] pSecurityPhrase,
                                                        final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        return GordianGenerator.newPasswordManager(getToolkit().getGuiFactory(), pFactoryType, pSecurityPhrase, pKeySetSpec);
    }
}
