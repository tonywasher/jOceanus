/*******************************************************************************
 * Subversion: Java SubVersion Management
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JSvnManager.ui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JPreferenceSet.PreferenceManager;
import net.sourceforge.JSvnManager.data.Repository;
import net.sourceforge.JSvnManager.data.WorkingCopy.WorkingCopySet;
import net.sourceforge.JSvnManager.threads.DiscoverData;

public class JSvnManager {
    /**
     * Logger.
     */
    private static Logger theLogger = Logger.getAnonymousLogger();

    /**
     * The Data Manager.
     */
    private final JDataManager theDataMgr = new JDataManager();

    /**
     * The Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr = new PreferenceManager();

    /**
     * The Render Manager.
     */
    // private final RenderManager theRenderMgr = new RenderManager(theDataMgr, new RenderConfig());

    /**
     * The Thread executor.
     */
    private ExecutorService theExecutor = Executors.newSingleThreadExecutor();

    /**
     * The repository.
     */
    private Repository theRepository;

    /**
     * The working copy set.
     */
    private WorkingCopySet theWorkingSet;

    /**
     * Create and show the GUI.
     */
    private static void createAndShowGUI() {
        // try {
        /* Create the frame */
        JFrame myFrame = new JFrame(JSvnManager.class.getSimpleName());

        /* Create the SvnManager program */
        JSvnManager myManager = new JSvnManager();

        /* Create the panel */
        JSvnStatusWindow myPanel = new JSvnStatusWindow(myManager);

        /* Attach the panel to the frame */
        myPanel.setOpaque(true);
        myFrame.setContentPane(myPanel);
        myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        /* Show the frame */
        myFrame.pack();
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);

        /* Create and run discoverData thread */
        DiscoverData myThread = new DiscoverData(myManager.thePreferenceMgr, myPanel);
        myManager.theExecutor.execute(myThread);

        /* Create the data window */
        // JDataWindow myDataWindow = new JDataWindow(myFrame, myManager.theDataMgr);

        /* Display it */
        // myDataWindow.showDialog();

        // } catch (JDataException e) {
        // theLogger.log(Level.SEVERE, "createGUI didn't complete successfully", e);
        // }
    }

    /**
     * Main function.
     * @param args the arguments
     */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * Constructor.
     */
    private JSvnManager() {
    }

    /**
     * Make Panel.
     * @param pData the discover thread
     */
    protected void setData(final DiscoverData pData) {
        /* Create Data Entries */
        JDataEntry myRepEntry = theDataMgr.new JDataEntry("Repository");
        myRepEntry.addAsRootChild();
        JDataEntry mySetEntry = theDataMgr.new JDataEntry("WorkingSet");
        mySetEntry.addAsRootChild();

        /* Access the repository and declare to data manager */
        theRepository = pData.getRepository();
        myRepEntry.setObject(theRepository);
        myRepEntry.setFocus();

        /* Build the WorkingCopySet */
        theWorkingSet = pData.getWorkingCopySet();
        mySetEntry.setObject(theWorkingSet);
    }
}
