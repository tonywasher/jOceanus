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
package net.sourceforge.joceanus.jprometheus.javafx;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.javafx.GordianFXPasswordManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jmetis.profile.MetisState;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXThreadManager;
import net.sourceforge.joceanus.jmetis.launch.javafx.MetisFXToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.javafx.TethysFXGuiFactory;

/**
 * Prometheus JavaFX Toolkit.
 */
public class PrometheusFXToolkit
        extends PrometheusToolkit {
    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public PrometheusFXToolkit() throws OceanusException {
        this(null, true);
    }

    /**
     * Constructor.
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public PrometheusFXToolkit(final boolean pSlider) throws OceanusException {
        this(null, pSlider);
    }

    /**
     * Constructor.
     * @param pInfo the program info
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public PrometheusFXToolkit(final MetisState pInfo,
                               final boolean pSlider) throws OceanusException {
        /* Create Toolkit */
        this(new MetisFXToolkit(pInfo, pSlider));
    }

    /**
     * Constructor.
     * @param pToolkit the metis toolkit
     * @throws OceanusException on error
     */
    public PrometheusFXToolkit(final MetisFXToolkit pToolkit) throws OceanusException {
        /* Create Toolkit */
        super(pToolkit);
    }

    @Override
    public TethysFXGuiFactory getGuiFactory() {
        return (TethysFXGuiFactory) super.getGuiFactory();
    }

    @Override
    public MetisFXThreadManager getThreadManager() {
        return (MetisFXThreadManager) super.getThreadManager();
    }

    @Override
    public MetisFXToolkit getToolkit() {
        return (MetisFXToolkit) super.getToolkit();
    }

    @Override
    protected GordianPasswordManager newPasswordManager(final GordianFactoryType pFactoryType,
                                                        final char[] pSecurityPhrase,
                                                        final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        return GordianFXPasswordManager.newPasswordManager(getGuiFactory(), pFactoryType, pSecurityPhrase, pKeySetSpec);
    }
}
