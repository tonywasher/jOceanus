/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.threads;

import net.sourceforge.joceanus.jgordianknot.crypto.GordianParameters;
import net.sourceforge.joceanus.jgordianknot.manager.GordianHashManager;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisErrorPanel;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisFieldColours.MetisColorPreferences;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisFieldSetPanelPair;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisPreferenceView;
import net.sourceforge.joceanus.jmetis.atlas.ui.MetisTableManager;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem.MetisFieldTableItem;
import net.sourceforge.joceanus.jmetis.list.MetisListEditSession;
import net.sourceforge.joceanus.jmetis.list.MetisListIndexed;
import net.sourceforge.joceanus.jmetis.list.MetisListKey;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceEvent;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceSecurity.MetisSecurityPreferences;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.profile.MetisProgram;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerStandardEntry;
import net.sourceforge.joceanus.jmetis.viewer.MetisViewerWindow;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.help.TethysHelpWindow;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;
import net.sourceforge.joceanus.jtethys.ui.TethysValueSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Metis Toolkit.
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisToolkit<N, I> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(MetisToolkit.class);

    /**
     * Formatter.
     */
    private final MetisDataFormatter theFormatter;

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
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * Security Manager.
     */
    private final GordianHashManager theHashManager;

    /**
     * Thread Manager.
     */
    private final MetisThreadManager<N, I> theThreadManager;

    /**
     * The Profile Viewer Entry.
     */
    private final MetisViewerEntry theProfileEntry;

    /**
     * Colour Preferences.
     */
    private final MetisColorPreferences theColorPreferences;

    /**
     * Program Definition.
     */
    private final TethysProgram theProgram;

    /**
     * The Active Profile.
     */
    private MetisProfile theProfile;

    /**
     * Constructor.
     * @param pInfo the program info
     * @param pSlider use slider status
     * @throws OceanusException on error
     */
    protected MetisToolkit(final MetisProgram pInfo,
                           final boolean pSlider) throws OceanusException {
        /* Store program definitions */
        theProgram = pInfo == null
                                   ? null
                                   : pInfo.getProgramDefinitions();

        /* Create the formatter */
        theFormatter = new MetisDataFormatter();

        /* Create the viewer */
        theViewerManager = new MetisViewerManager();

        /* Access the profile entry */
        theProfileEntry = theViewerManager.getStandardEntry(MetisViewerStandardEntry.PROFILE);

        /* Record the profile */
        setProfile(pInfo == null
                                 ? new MetisProfile("StartUp")
                                 : pInfo.getProfile());

        /* Create the preference manager */
        thePreferenceManager = new MetisPreferenceManager(theViewerManager);

        /* Create the guiFactory */
        theGuiFactory = newGuiFactory();

        /* Create the hashManager */
        final MetisSecurityPreferences myPreferences = thePreferenceManager.getPreferenceSet(MetisSecurityPreferences.class);
        theHashManager = newSecurityManager(myPreferences.getParameters());

        /* create the thread manager */
        theThreadManager = newThreadManager(pSlider);

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
    public TethysProgram getProgramDefinitions() {
        return theProgram;
    }

    /**
     * Obtain the Data Formatter.
     * @return the formatter
     */
    public MetisDataFormatter getFormatter() {
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
    public TethysGuiFactory<N, I> getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the Security Manager.
     * @return the security manager
     */
    public GordianHashManager getSecurityManager() {
        return theHashManager;
    }

    /**
     * Obtain the Thread Manager.
     * @return the factory
     */
    public MetisThreadManager<N, I> getThreadManager() {
        return theThreadManager;
    }

    /**
     * Create a GUI Factory.
     * @return the factory
     */
    protected abstract TethysGuiFactory<N, I> newGuiFactory();

    /**
     * Create a Thread Manager.
     * @param pSlider use slider status
     * @return the thread manager
     */
    protected abstract MetisThreadManager<N, I> newThreadManager(boolean pSlider);

    /**
     * Create a Thread Slider Status.
     * @param pManager the thread manager
     * @return the thread status manager
     */
    protected abstract MetisThreadProgressStatus<N, I> newThreadSliderStatus(MetisThreadManager<N, I> pManager);

    /**
     * Create a Thread TextArea Status.
     * @param pManager the thread manager
     * @return the thread status manager
     */
    protected MetisThreadTextAreaStatus<N, I> newThreadTextAreaStatus(final MetisThreadManager<N, I> pManager) {
        return new MetisThreadTextAreaStatus<>(pManager, theGuiFactory);
    }

    /**
     * Create a Security Manager.
     * @param pParameters the parameters
     * @return the manager
     * @throws OceanusException on error
     */
    protected abstract GordianHashManager newSecurityManager(GordianParameters pParameters) throws OceanusException;

    /**
     * Create a Help Window.
     * @return the help Window
     */
    public abstract TethysHelpWindow<N, I> newHelpWindow();

    /**
     * Create a ReadOnly TableManager.
     * @param <R> the row type
     * @param pClazz the cxlass of the item
     * @param pList the base list
     * @return the table manager
     */
    public abstract <R extends MetisFieldTableItem> MetisTableManager<R, N, I> newTableManager(Class<R> pClazz,
                                                                                               MetisListIndexed<R> pList);

    /**
     * Create an editable TableManager.
     * @param <R> the row type
     * @param pItemType the itemType of the item
     * @param pSession the editSession
     * @return the table manager
     */
    public abstract <R extends MetisFieldTableItem> MetisTableManager<R, N, I> newTableManager(MetisListKey pItemType,
                                                                                               MetisListEditSession pSession);

    /**
     * Create an ErrorPanel.
     * @param pParent the parent viewer entry
     * @return the error panel
     */
    public MetisErrorPanel<N, I> newErrorPanel(final MetisViewerEntry pParent) {
        return new MetisErrorPanel<>(theGuiFactory, theViewerManager, pParent);
    }

    /**
     * Create a Viewer Window.
     * @return the viewer Window
     * @throws OceanusException on error
     */
    public abstract MetisViewerWindow<N, I> newViewerWindow() throws OceanusException;

    /**
     * Create a new Preference View.
     * @return the view
     */
    public MetisPreferenceView<N, I> newPreferenceView() {
        return new MetisPreferenceView<>(getGuiFactory(), thePreferenceManager);
    }

    /**
     * Create a new FieldSetPanelPair.
     * @return the panelPair
     */
    public MetisFieldSetPanelPair<N, I> newFieldSetPanelPair() {
        return new MetisFieldSetPanelPair<>(getGuiFactory());
    }

    /**
     * Process Colour preferences.
     */
    private void processColorPreferences() {
        /* Update the value Set with the preferences */
        final TethysGuiFactory<?, ?> myFactory = getGuiFactory();
        final TethysValueSet myValueSet = myFactory.getValueSet();
        theColorPreferences.updateValueSet(myValueSet);
    }

    /**
     * Set profile.
     * @param pProfile the profile
     */
    private void setProfile(final MetisProfile pProfile) {
        /* Create a new profile */
        theProfile = pProfile;

        /* Update the Profile Viewer entry */
        theProfileEntry.setObject(theProfile);
    }

    /**
     * Create new profile.
     * @param pTask the name of the task
     * @return the new profile
     */
    public MetisProfile getNewProfile(final String pTask) {
        /* Create a new profile */
        setProfile(new MetisProfile(pTask));

        /* Return the new profile */
        return theProfile;
    }

    /**
     * Obtain the active profile.
     * @return the active profile
     */
    public MetisProfile getActiveProfile() {
        return theProfile;
    }

    /**
     * Obtain the active task.
     * @return the active task
     */
    public MetisProfile getActiveTask() {
        /* Create a new profile */
        return theProfile == null
                                  ? null
                                  : theProfile.getActiveTask();
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
}
