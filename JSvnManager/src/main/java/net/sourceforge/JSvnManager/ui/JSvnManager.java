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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataManager.JDataWindow;
import net.sourceforge.JPreferenceSet.PreferenceManager;
import net.sourceforge.JSvnManager.data.Repository;
import net.sourceforge.JSvnManager.data.WorkingCopy.WorkingCopySet;
import net.sourceforge.JSvnManager.threads.DiscoverData;

/**
 * Top level JSvnManager window.
 * @author Tony
 * 
 */
public final class JSvnManager {
    /**
     * Logger.
     */
    // private static Logger theLogger = Logger.getAnonymousLogger();

    /**
     * The Frame.
     */
    private final JFrame theFrame;

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
    private final ExecutorService theExecutor = Executors.newSingleThreadExecutor();

    /**
     * The DataManager menuItem.
     */
    private final JMenuItem theShowDataMgr;

    /**
     * The Started data window.
     */
    private JDataWindow theDataWdw = null;

    /**
     * The Window Close handler.
     */
    private WindowClose theCloseHandler = new WindowClose();

    /**
     * The repository.
     */
    private Repository theRepository;

    /**
     * The working copy set.
     */
    private WorkingCopySet theWorkingSet;

    /**
     * Constructor.
     */
    protected JSvnManager() {
        /* Create the frame */
        theFrame = new JFrame(JSvnManager.class.getSimpleName());

        /* Create the panel */
        JSvnStatusWindow myPanel = new JSvnStatusWindow(this);

        /* Create the menu bar and listener */
        JMenuBar myMainMenu = new JMenuBar();
        MenuListener myMenuListener = new MenuListener();

        /* Create the JDataWindow menuItem */
        theShowDataMgr = new JMenuItem("DataManager");
        theShowDataMgr.addActionListener(myMenuListener);
        myMainMenu.add(theShowDataMgr);

        /* Add the Menu bar */
        theFrame.setJMenuBar(myMainMenu);

        /* Attach the panel to the frame */
        myPanel.setOpaque(true);
        theFrame.setContentPane(myPanel);
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(theCloseHandler);

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Create and run discoverData thread */
        DiscoverData myThread = new DiscoverData(thePreferenceMgr, myPanel);
        theExecutor.execute(myThread);
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

    /**
     * MenuListener class.
     */
    private final class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* If this is the DataManager window */
            if (theShowDataMgr.equals(evt.getSource())) {
                /* Create the data window */
                theDataWdw = new JDataWindow(theFrame, theDataMgr);

                /* Listen for its closure */
                theDataWdw.addWindowListener(theCloseHandler);

                /* Disable the menu item */
                theShowDataMgr.setEnabled(false);

                /* Display it */
                theDataWdw.showDialog();
            }
        }
    }

    /**
     * Window Close Adapter.
     */
    private class WindowClose extends WindowAdapter {
        @Override
        public void windowClosing(final WindowEvent evt) {
            Object o = evt.getSource();

            /* If this is the frame that is closing down */
            if (theFrame.equals(o)) {
                /* terminate the executor */
                theExecutor.shutdown();

                /* Dispose of the data/help Windows if they exist */
                if (theDataWdw != null) {
                    theDataWdw.dispose();
                }

                /* Dispose of the frame */
                theFrame.dispose();

                /* Exit the application */
                System.exit(0);

                /* else if this is the Data Window shutting down */
            } else if (o.equals(theDataWdw)) {
                /* Re-enable the help menu item */
                theShowDataMgr.setEnabled(true);
                theDataWdw.dispose();
                theDataWdw = null;

                /* Notify data manager */
                theDataMgr.declareWindow(null);
            }
        }
    }
}
