/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmetis.toolkit;

import net.sourceforge.joceanus.jmetis.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.jmetis.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.help.MetisHelpWindow;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.profile.TethysProfile;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIProgram;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadEvent;
import net.sourceforge.joceanus.jtethys.ui.api.thread.TethysUIThreadManager;

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
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MetisToolkit.class);

    /**
     * Viewer Manager.
     */
    private final MetisViewerManager theViewerManager;

    /**
     * Preference Manager.
     */
    private final MetisPreferenceManager thePreferenceManager;

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
    private final MetisColorPreferences theColorPreferences;

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

        /* Create the preference manager */
        thePreferenceManager = new MetisPreferenceManager(theViewerManager);

        /* Extend the formatter */
        getFormatter().extendFormatter(new MetisDataFormatter(getFormatter()));

        /* create the thread manager */
        theThreadManager = theGuiFactory.threadFactory().newThreadManager();
        attachToThreadManager();

        /* Access the Colour Preferences */
        theColorPreferences = thePreferenceManager.getPreferenceSet(MetisColorPreferences.class);

        /* Process the colour preferences */
        processColorPreferences();

        /* Create listener */
        final TethysEventRegistrar<MetisPreferenceEvent> myRegistrar = theColorPreferences.getEventRegistrar();
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
    public TethysUIDataFormatter getFormatter() {
        return getGuiFactory().getDataFormatter();
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
    private void setProfile(final TethysProfile pProfile) {
        /* Update the Profile Viewer entry */
        theProfileEntry.setObject(pProfile);
    }

    /**
     * Create new profile.
     * @param pTask the name of the task
     * @return the new profile
     */
    public TethysProfile getNewProfile(final String pTask) {
        /* Create a new profile */
        final TethysProfile myProfile = theGuiFactory.getNewProfile(pTask);
        setProfile(myProfile);

        /* Return the new profile */
        return myProfile;
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public TethysProfile getActiveProfile() {
        return theGuiFactory.getActiveProfile();
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public TethysProfile getActiveTask() {
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
        final TethysEventRegistrar<TethysUIThreadEvent> myRegistrar = theThreadManager.getEventRegistrar();

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
