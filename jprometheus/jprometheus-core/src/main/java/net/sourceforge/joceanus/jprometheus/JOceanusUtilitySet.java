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
import net.sourceforge.joceanus.jmetis.field.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysValueSet;

/**
 * JOceanus Utility Set.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class JOceanusUtilitySet<N, I> {
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
     * Colour Preferences.
     */
    private final MetisColorPreferences theColorPreferences;

    /**
     * Constructor.
     * @param pToolkit the toolkit
     */
    protected JOceanusUtilitySet(final MetisToolkit<N, I> pToolkit) {
        /* Access components */
        theSecureMgr = pToolkit.getSecurityManager();
        thePreferenceMgr = pToolkit.getPreferenceManager();
        theFormatter = pToolkit.getFormatter();
        theGuiFactory = pToolkit.getGuiFactory();

        /* Access the Colour Preferences */
        theColorPreferences = thePreferenceMgr.getPreferenceSet(MetisColorPreferences.class);

        /* Create listener */
        TethysEventRegistrar<MetisPreferenceEvent> myRegistrar = theColorPreferences.getEventRegistrar();
        myRegistrar.addEventListener(e -> processColorPreferences());
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
     * Obtain the colour preferences.
     * @return the colour preferences
     */
    protected MetisColorPreferences getColorPreferences() {
        return theColorPreferences;
    }

    /**
     * Process Colour preferences.
     */
    protected void processColorPreferences() {
        /* Update the value Set with the preferences */
        TethysGuiFactory<?, ?> myFactory = getGuiFactory();
        TethysValueSet myValueSet = myFactory.getValueSet();
        theColorPreferences.updateValueSet(myValueSet);
    }

    /**
     * Obtain the viewer manager.
     * @return the manager
     */
    public abstract MetisViewerManager getViewerManager();
}
