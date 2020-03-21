/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2019 Tony Washer
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
import net.sourceforge.joceanus.jgordianknot.api.javafx.GordianFXSecurityManager;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.util.GordianSecurityManager;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXThreadManager;
import net.sourceforge.joceanus.jmetis.threads.javafx.MetisFXToolkit;
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
    public PrometheusFXToolkit(final MetisProgram pInfo,
                               final boolean pSlider) throws OceanusException {
        /* Create Toolkit */
        super(new MetisFXToolkit(pInfo, pSlider));
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
    protected GordianSecurityManager newSecurityManager(final GordianFactoryType pFactoryType,
                                                        final char[] pSecurityPhrase,
                                                        final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        return new GordianFXSecurityManager(getGuiFactory(), pFactoryType, pSecurityPhrase, pKeySetSpec);
    }
}
