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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jprometheus/src/main/java/net/sourceforge/joceanus/jprometheus/JPrometheusCancelException.java $
 * $Revision: 543 $
 * $Author: Tony $
 * $Date: 2014-10-13 12:12:02 +0100 (Mon, 13 Oct 2014) $
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.swing;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.swing.GordianSwingHashManager;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.MetisSwingViewerManager;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jprometheus.preference.SecurityPreferences;
import net.sourceforge.joceanus.jprometheus.preference.swing.JFieldPreferences;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEvent.TethysChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistration.TethysChangeRegistration;

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
     * Field Preferences.
     */
    private final JFieldPreferences theFieldPreferences;

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

        /* Access the Field Preferences */
        theFieldPreferences = pPrefMgr.getPreferenceSet(JFieldPreferences.class);

        /* Allocate the FieldManager */
        theFieldManager = new MetisFieldManager(theFieldPreferences.getConfiguration());

        /* Create components */
        theViewerManager = new MetisSwingViewerManager(theFieldManager);

        /* Create listener */
        new PreferenceListener();
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
        SecurityPreferences myPrefs = myPrefMgr.getPreferenceSet(SecurityPreferences.class);

        /* Build new utility set */
        return new JOceanusSwingUtilitySet(myPrefs.getParameters(), myPrefMgr);
    }

    @Override
    public MetisSwingViewerManager getViewerManager() {
        return theViewerManager;
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public MetisFieldManager getFieldManager() {
        return theFieldManager;
    }

    /**
     * Preference listener class.
     */
    private final class PreferenceListener
            implements TethysChangeEventListener {
        /**
         * UpdateSet Registration.
         */
        private final TethysChangeRegistration thePrefReg;

        /**
         * Constructor.
         */
        private PreferenceListener() {
            thePrefReg = theFieldPreferences.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChange(final TethysChangeEvent pEvent) {
            /* If we are performing a rewind */
            if (thePrefReg.isRelevant(pEvent)) {
                /* Update new configuration */
                theFieldManager.setConfig(theFieldPreferences.getConfiguration());
            }
        }
    }
}
