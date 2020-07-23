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
package net.sourceforge.joceanus.jprometheus.lethe.swing;

import net.sourceforge.joceanus.jgordianknot.api.factory.GordianFactoryType;
import net.sourceforge.joceanus.jgordianknot.api.keyset.GordianKeySetHashSpec;
import net.sourceforge.joceanus.jgordianknot.api.password.GordianPasswordManager;
import net.sourceforge.joceanus.jgordianknot.api.swing.GordianSwingPasswordManager;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldConfig;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisSwingFieldManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.profile.MetisState;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingThreadManager;
import net.sourceforge.joceanus.jmetis.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.PrometheusToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * Prometheus Swing Toolkit.
 */
public class PrometheusSwingToolkit
        extends PrometheusToolkit {
    /**
     * Field Manager.
     */
    private final MetisSwingFieldManager theEosFieldManager;

    /**
     * Colour Preferences.
     */
    private final MetisColorPreferences theColorPreferences;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public PrometheusSwingToolkit() throws OceanusException {
        this(null, true);
    }

    /**
     * Constructor.
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public PrometheusSwingToolkit(final boolean pSlider) throws OceanusException {
        this(null, pSlider);
    }

    /**
     * Constructor.
     * @param pInfo the program info
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    public PrometheusSwingToolkit(final MetisState pInfo,
                                  final boolean pSlider) throws OceanusException {
        /* Create Toolkit */
        this(new MetisSwingToolkit(pInfo, pSlider));
    }

    /**
     * Constructor.
     * @param pToolkit the metis toolkit
     * @throws OceanusException on error
     */
    public PrometheusSwingToolkit(final MetisSwingToolkit pToolkit) throws OceanusException {
        /* Create Toolkit */
        super(pToolkit);

        /* Access the Colour Preferences */
        theColorPreferences = getPreferenceManager().getPreferenceSet(MetisColorPreferences.class);

        /* Allocate the EosFieldManager */
        theEosFieldManager = new MetisSwingFieldManager(getGuiFactory(), new MetisSwingFieldConfig(theColorPreferences));

        /* Process the colour preferences */
        processColorPreferences();

        /* Create listener */
        final TethysEventRegistrar<MetisPreferenceEvent> myRegistrar = theColorPreferences.getEventRegistrar();
        myRegistrar.addEventListener(e -> processColorPreferences());
    }

    /**
     * Process colour preferences.
     */
    private void processColorPreferences() {
        /* Update the field manager */
        theEosFieldManager.setConfig(new MetisSwingFieldConfig(theColorPreferences));
    }

    @Override
    public TethysSwingGuiFactory getGuiFactory() {
        return (TethysSwingGuiFactory) super.getGuiFactory();
    }

    @Override
    public MetisSwingThreadManager getThreadManager() {
        return (MetisSwingThreadManager) super.getThreadManager();
    }

    @Override
    public MetisSwingToolkit getToolkit() {
        return (MetisSwingToolkit) super.getToolkit();
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public MetisSwingFieldManager getFieldManager() {
        return theEosFieldManager;
    }

    @Override
    protected GordianPasswordManager newPasswordManager(final GordianFactoryType pFactoryType,
                                                        final char[] pSecurityPhrase,
                                                        final GordianKeySetHashSpec pKeySetSpec) throws OceanusException {
        return GordianSwingPasswordManager.newPasswordManager(getGuiFactory(), pFactoryType, pSecurityPhrase, pKeySetSpec);
    }
}
