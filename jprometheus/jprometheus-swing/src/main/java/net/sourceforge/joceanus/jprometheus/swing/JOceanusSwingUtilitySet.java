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

import net.sourceforge.joceanus.jgordianknot.crypto.SecurityParameters;
import net.sourceforge.joceanus.jgordianknot.manager.swing.SwingSecureManager;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.swing.SwingViewerManager;
import net.sourceforge.joceanus.jprometheus.JOceanusUtilitySet;
import net.sourceforge.joceanus.jprometheus.preference.SecurityPreferences;
import net.sourceforge.joceanus.jprometheus.preference.swing.JFieldPreferences;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;

/**
 * JOceanus Swing Utility Set.
 */
public class JOceanusSwingUtilitySet
        extends JOceanusUtilitySet {
    /**
     * Viewer Manager.
     */
    private final SwingViewerManager theViewerManager;

    /**
     * Field Manager.
     */
    private final JFieldManager theFieldManager;

    /**
     * Field Preferences.
     */
    private final JFieldPreferences theFieldPreferences;

    /**
     * Constructor.
     * @throws JOceanusException on error
     */
    public JOceanusSwingUtilitySet() throws JOceanusException {
        this(new SecurityParameters());
    }

    /**
     * Constructor.
     * @param pParameters the security parameters
     * @throws JOceanusException on error
     */
    public JOceanusSwingUtilitySet(final SecurityParameters pParameters) throws JOceanusException {
        this(pParameters, new PreferenceManager());
    }

    /**
     * Constructor.
     * @param pParameters the security parameters
     * @param pPrefMgr the preference manager
     * @throws JOceanusException on error
     */
    public JOceanusSwingUtilitySet(final SecurityParameters pParameters,
                                   final PreferenceManager pPrefMgr) throws JOceanusException {
        /* Create secure manager */
        super(new SwingSecureManager(pParameters), pPrefMgr);

        /* Access the Field Preferences */
        theFieldPreferences = pPrefMgr.getPreferenceSet(JFieldPreferences.class);

        /* Allocate the FieldManager */
        theFieldManager = new JFieldManager(theFieldPreferences.getConfiguration());

        /* Create components */
        theViewerManager = new SwingViewerManager(theFieldManager);

        /* Create listener */
        new PreferenceListener();
    }

    /**
     * Create default UtilitySet.
     * @return the utility set
     * @throws JOceanusException on error
     */
    public static JOceanusSwingUtilitySet createDefault() throws JOceanusException {
        /* Preference Manager */
        PreferenceManager myPrefMgr = new PreferenceManager();

        /* Access security preferences */
        SecurityPreferences myPrefs = myPrefMgr.getPreferenceSet(SecurityPreferences.class);

        /* Build new utility set */
        return new JOceanusSwingUtilitySet(myPrefs.getParameters(), myPrefMgr);
    }

    @Override
    public SwingViewerManager getViewerManager() {
        return theViewerManager;
    }

    /**
     * Obtain the field manager.
     * @return the field manager
     */
    public JFieldManager getFieldManager() {
        return theFieldManager;
    }

    /**
     * Preference listener class.
     */
    private final class PreferenceListener
            implements JOceanusChangeEventListener {
        /**
         * UpdateSet Registration.
         */
        private final JOceanusChangeRegistration thePrefReg;

        /**
         * Constructor.
         */
        private PreferenceListener() {
            thePrefReg = theFieldPreferences.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If we are performing a rewind */
            if (thePrefReg.isRelevant(pEvent)) {
                /* Update new configuration */
                theFieldManager.setConfig(theFieldPreferences.getConfiguration());
            }
        }
    }
}
