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
package net.sourceforge.joceanus.jprometheus.swing;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.swing.GordianSwingHashManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldConfig;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSecurity.MetisSecurityPreferences;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;

/**
 * JOceanus Swing Utility Set.
 */
public class JOceanusSwingUtilitySet
        extends JOceanusUtilitySet {
    /**
     * Viewer Manager.
     */
    private final MetisSwingViewerManager theViewerManager;

    /**
     * Field Manager.
     */
    private final MetisFieldManager theFieldManager;

    /**
     * GUI Factory.
     */
    private final TethysSwingGuiFactory theGuiFactory;

    /**
     * Constructor.
     * @throws OceanusException on error
     */
    public JOceanusSwingUtilitySet() throws OceanusException {
        this(new GordianParameters());
    }

    /**
     * Constructor.
     * @param pParameters the security parameters
     * @throws OceanusException on error
     */
    public JOceanusSwingUtilitySet(final GordianParameters pParameters) throws OceanusException {
        this(pParameters, new MetisPreferenceManager());
    }

    /**
     * Constructor.
     * @param pParameters the security parameters
     * @param pPrefMgr the preference manager
     * @throws OceanusException on error
     */
    public JOceanusSwingUtilitySet(final GordianParameters pParameters,
                                   final MetisPreferenceManager pPrefMgr) throws OceanusException {
        /* Create secure manager */
        super(new GordianSwingHashManager(pParameters), pPrefMgr);

        /* Allocate the FieldManager */
        theFieldManager = new MetisFieldManager(new MetisFieldConfig(getColorPreferences()));

        /* Create components */
        theViewerManager = new MetisSwingViewerManager(theFieldManager);

        /* Create the GUI Factory */
        theGuiFactory = new TethysSwingGuiFactory(new MetisDataFormatter());

        /* Process the colour preferences */
        processColorPreferences();
    }

    /**
     * Create default UtilitySet.
     * @return the utility set
     * @throws OceanusException on error
     */
    public static JOceanusSwingUtilitySet createDefault() throws OceanusException {
        /* Preference Manager */
        MetisPreferenceManager myPrefMgr = new MetisPreferenceManager();

        /* Access security preferences */
        MetisSecurityPreferences myPrefs = myPrefMgr.getPreferenceSet(MetisSecurityPreferences.class);

        /* Build new utility set */
        return new JOceanusSwingUtilitySet(myPrefs.getParameters(), myPrefMgr);
    }

    @Override
    protected void processColorPreferences() {
        /* Call underlying class */
        super.processColorPreferences();

        /* Update the field manager */
        theFieldManager.setConfig(new MetisFieldConfig(getColorPreferences()));
    }

    @Override
    public MetisSwingViewerManager getViewerManager() {
        return theViewerManager;
    }

    @Override
    public TethysSwingGuiFactory getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public MetisFieldManager getFieldManager() {
        return theFieldManager;
    }
}
