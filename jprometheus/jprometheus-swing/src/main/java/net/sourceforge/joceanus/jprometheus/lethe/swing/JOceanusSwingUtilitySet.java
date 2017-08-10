/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.lethe.swing;

import javax.swing.Icon;
import javax.swing.JComponent;

import net.sourceforge.joceanus.jmetis.lethe.field.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldConfig;
import net.sourceforge.joceanus.jmetis.lethe.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.lethe.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.lethe.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.lethe.threads.swing.MetisSwingThreadManager;
import net.sourceforge.joceanus.jmetis.lethe.threads.swing.MetisSwingToolkit;
import net.sourceforge.joceanus.jprometheus.lethe.JOceanusUtilitySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * JOceanus Swing Utility Set.
 */
public class JOceanusSwingUtilitySet
        extends JOceanusUtilitySet<JComponent, Icon> {
    /**
     * Field Manager.
     */
    private final MetisFieldManager theEosFieldManager;

    /**
     * Colour Preferences.
     */
    private final MetisColorPreferences theColorPreferences;

    /**
     * Constructor.
     * @param pInfo the program info
     * @throws OceanusException on error
     */
    public JOceanusSwingUtilitySet(final MetisProgram pInfo) throws OceanusException {
        /* Create Toolkit */
        super(new MetisSwingToolkit(pInfo, true));

        /* Access the Colour Preferences */
        theColorPreferences = getPreferenceManager().getPreferenceSet(MetisColorPreferences.class);

        /* Allocate the EosFieldManager */
        theEosFieldManager = new MetisFieldManager(getGuiFactory(), new MetisFieldConfig(theColorPreferences));

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
        theEosFieldManager.setConfig(new MetisFieldConfig(theColorPreferences));
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
    public MetisFieldManager getFieldManager() {
        return theEosFieldManager;
    }
}
