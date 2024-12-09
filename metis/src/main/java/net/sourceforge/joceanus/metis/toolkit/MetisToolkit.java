/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.toolkit;

import net.sourceforge.joceanus.metis.data.MetisDataFormatter;
import net.sourceforge.joceanus.metis.help.MetisHelpWindow;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.metis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.metis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.metis.ui.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.metis.ui.MetisPreferenceView;
import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.metis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.tethys.api.base.TethysUIProgram;
import net.sourceforge.joceanus.tethys.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.tethys.api.thread.TethysUIThreadManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Metis Toolkit.
 */
public class MetisToolkit {
    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MetisToolkit.class);

    /**
     * Viewer Manager.
     */
    private final MetisViewerManager theViewerManager;

    /**
     * Preference Manager.
     */
    private MetisPreferenceManager thePreferenceManager;

    /**
     * GUI Factory.
     */
    private final TethysUIFactory<?> theGuiFactory;

    /**
     * Thread Manager.
     */
    private final TethysUIThreadManager theThreadManager;

    /**
     * The Profile Viewer Entry.
     */
    private final MetisViewerEntry theProfileEntry;

    /**
     * The Error Viewer Entry.
     */
    private final MetisViewerEntry theErrorEntry;

    /**
     * Colour Preferences.
     */
    private MetisColorPreferences theColorPreferences;

    /**
     * The formatter.
     */
    private final OceanusDataFormatter theFormatter;

    /**
     * Program Definition.
     */
    private final TethysUIProgram theProgram;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @throws OceanusException on error
     */
    public MetisToolkit(final TethysUIFactory<?> pFactory) throws OceanusException {
        this(pFactory, true);
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pPreference creat preference manager
     * @throws OceanusException on error
     */
    public MetisToolkit(final TethysUIFactory<?> pFactory,
                        final boolean pPreference) throws OceanusException {
        /* Store parameters */
        theGuiFactory = pFactory;

        /* Store program definitions */
        theProgram = theGuiFactory.getProgramDefinitions();

        /* Create the viewer */
        theViewerManager = new MetisViewerManager();

        /* Access the profile entries */
        theProfileEntry = theViewerManager.getStandardEntry(MetisViewerStandardEntry.PROFILE);
        theErrorEntry = theViewerManager.getStandardEntry(MetisViewerStandardEntry.ERROR);

        /* Record the profile */
        setProfile(theGuiFactory.getActiveProfile());

        /* Access and extend the formatter */
        theFormatter = pFactory.getDataFormatter();
        theFormatter.extendFormatter(new MetisDataFormatter(theFormatter));

        /* create the thread manager */
        theThreadManager = theGuiFactory.threadFactory().newThreadManager();
        attachToThreadManager();

        /* If we are setting up a MetisPreferenceManager */
        if (pPreference) {
            /* Create the preference manager */
            thePreferenceManager = new MetisPreferenceManager(theViewerManager);

            /* Set up colors */
            setUpColors(thePreferenceManager);
        }
    }

    /**
     * Set up colors.
     * @param pPreferenceMgr the preference manager
     */
    public void setUpColors(final MetisPreferenceManager pPreferenceMgr) {
        /* Access the Colour Preferences */
        thePreferenceManager = pPreferenceMgr;
        theColorPreferences = pPreferenceMgr.getPreferenceSet(MetisColorPreferences.class);

        /* Process the colour preferences */
        processColorPreferences();

        /* Create listener */
        final OceanusEventRegistrar<MetisPreferenceEvent> myRegistrar = theColorPreferences.getEventRegistrar();
        myRegistrar.addEventListener(e -> processColorPreferences());
    }

    /**
     * Obtain the Program Definitions.
     * @return the definitions
     */
    public TethysUIProgram getProgramDefinitions() {
        return theProgram;
    }

    /**
     * Obtain the Data Formatter.
     * @return the formatter
     */
    public OceanusDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the Viewer Manager.
     * @return the viewer
     */
    public MetisViewerManager getViewerManager() {
        return theViewerManager;
    }

    /**
     * Obtain the Preference Manager.
     * @return the preferences
     */
    public MetisPreferenceManager getPreferenceManager() {
        return thePreferenceManager;
    }

    /**
     * Obtain the GUI Factory.
     * @return the factory
     */
    public TethysUIFactory<?> getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the Thread Manager.
     * @return the factory
     */
    public TethysUIThreadManager getThreadManager() {
        return theThreadManager;
    }

    /**
     * Create a Help Window.
     * @return the help Window
     */
    public MetisHelpWindow newHelpWindow() {
        return new MetisHelpWindow(getGuiFactory());
    }

    /**
     * Create an ErrorPanel.
     * @param pParent the parent viewer entry
     * @return the error panel
     */
    public MetisErrorPanel newErrorPanel(final MetisViewerEntry pParent) {
        return new MetisErrorPanel(theGuiFactory, theViewerManager, pParent);
    }

    /**
     * Create a Viewer Window.
     * @return the viewer Window
     * @throws OceanusException on error
     */
    public MetisViewerWindow newViewerWindow() throws OceanusException {
        return new MetisViewerWindow(getGuiFactory(), theViewerManager);
    }

    /**
     * Create a new Preference View.
     * @return the view
     */
    public MetisPreferenceView newPreferenceView() {
        return new MetisPreferenceView(getGuiFactory(), thePreferenceManager);
    }

    /**
     * Process Colour preferences.
     */
    private void processColorPreferences() {
        /* Update the value Set with the preferences */
        final TethysUIFactory<?> myFactory = getGuiFactory();
        final TethysUIValueSet myValueSet = myFactory.getValueSet();
        theColorPreferences.updateValueSet(myValueSet);
    }

    /**
     * Set profile.
     * @param pProfile the profile
     */
    private void setProfile(final OceanusProfile pProfile) {
        /* Update the Profile Viewer entry */
        theProfileEntry.setObject(pProfile);
    }

    /**
     * Create new profile.
     * @param pTask the name of the task
     * @return the new profile
     */
    public OceanusProfile getNewProfile(final String pTask) {
        /* Create a new profile */
        final OceanusProfile myProfile = theGuiFactory.getNewProfile(pTask);
        setProfile(myProfile);

        /* Return the new profile */
        return myProfile;
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public OceanusProfile getActiveProfile() {
        return theGuiFactory.getActiveProfile();
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public OceanusProfile getActiveTask() {
        return theGuiFactory.getActiveTask();
    }

    /**
     * Delete a file on error exit.
     * @param pFile the file to delete
     */
    public static void cleanUpFile(final File pFile) {
        try {
            final Path myPath = pFile.toPath();
            Files.delete(myPath);
        } catch (IOException e) {
            LOGGER.error("Failed to delete File", e);
        }
    }

    /**
     * Attach to threadManager.
     */
    private void attachToThreadManager() {
        /* Access the event registrar */
        final OceanusEventRegistrar<TethysUIThreadEvent> myRegistrar = theThreadManager.getEventRegistrar();

        /* Add Thread start support */
        myRegistrar.addEventListener(TethysUIThreadEvent.THREADSTART, e -> {
            /* Tasks for event handler */
            theErrorEntry.setObject(null);
            theErrorEntry.setVisible(false);
        });

        /* Add Thread end support */
        myRegistrar.addEventListener(TethysUIThreadEvent.THREADEND, e -> {
            /* Tasks for event handler */
            theProfileEntry.setObject(theThreadManager.getActiveProfile());
            theProfileEntry.setFocus();
        });

        /* Add Thread error support */
        myRegistrar.addEventListener(TethysUIThreadEvent.THREADERROR, e -> {
            /* Tasks for event handler */
            theErrorEntry.setObject(e.getDetails());
            theErrorEntry.setVisible(true);
            theErrorEntry.setFocus();
        });
    }
}
