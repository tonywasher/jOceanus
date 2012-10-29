/*******************************************************************************
 * jSvnManager: Java SubVersion Management
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
package net.sourceforge.jOceanus.jSvnManager.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import net.sourceforge.jOceanus.jDataManager.JDataManager;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jDataManager.JDataWindow;
import net.sourceforge.jOceanus.jPreferenceSet.PreferenceManager;
import net.sourceforge.jOceanus.jSvnManager.data.Branch;
import net.sourceforge.jOceanus.jSvnManager.data.Branch.BranchOpType;
import net.sourceforge.jOceanus.jSvnManager.data.Repository;
import net.sourceforge.jOceanus.jSvnManager.data.Tag;
import net.sourceforge.jOceanus.jSvnManager.data.WorkingCopy.WorkingCopySet;
import net.sourceforge.jOceanus.jSvnManager.threads.CreateBranchTags;
import net.sourceforge.jOceanus.jSvnManager.threads.CreateNewBranch;
import net.sourceforge.jOceanus.jSvnManager.threads.CreateTagExtract;
import net.sourceforge.jOceanus.jSvnManager.threads.CreateWorkingCopy;
import net.sourceforge.jOceanus.jSvnManager.threads.DiscoverData;
import net.sourceforge.jOceanus.jSvnManager.threads.RevertWorkingCopy;
import net.sourceforge.jOceanus.jSvnManager.threads.UpdateWorkingCopy;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Top level JSvnManager window.
 * @author Tony
 */
public final class JSvnManager {
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
     * The DataManager menuItem.
     */
    private final JMenuItem theShowDataMgr;

    /**
     * The Tasks menu.
     */
    private final JMenu theTasks;

    /**
     * The ExtractTag menuItem.
     */
    private final JMenuItem theExtractTag;

    /**
     * The CreateWorkingCopy menuItem.
     */
    private final JMenuItem theCreateWC;

    /**
     * The UpdateWorkingCopy menuItem.
     */
    private final JMenuItem theUpdateWC;

    /**
     * The RevertWorkingCopy menuItem.
     */
    private final JMenuItem theRevertWC;

    /**
     * The CreateBranchTags menuItem.
     */
    private final JMenuItem theCreateBrnTags;

    /**
     * The CreateNewBranch menuItem.
     */
    private final JMenuItem theCreateNewBrn;

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
     * Status panel.
     */
    private final JSvnStatusWindow theStatusPanel;

    /**
     * Constructor.
     */
    protected JSvnManager() {
        /* Create the frame */
        theFrame = new JFrame(JSvnManager.class.getSimpleName());

        /* Create the panel */
        theStatusPanel = new JSvnStatusWindow(this);

        /* Create the menu bar and listener */
        JMenuBar myMainMenu = new JMenuBar();
        MenuListener myMenuListener = new MenuListener();

        /* Create the Tasks menu */
        theTasks = new JMenu("Tasks");
        myMainMenu.add(theTasks);

        /* Create the JDataWindow menuItem */
        theShowDataMgr = new JMenuItem("DataManager");
        theShowDataMgr.addActionListener(myMenuListener);
        myMainMenu.add(theShowDataMgr);

        /* Create the CreateWC menuItem */
        theCreateWC = new JMenuItem("CreateWorkingCopy");
        theCreateWC.addActionListener(myMenuListener);
        theTasks.add(theCreateWC);

        /* Create the ExtractTag menuItem */
        theExtractTag = new JMenuItem("CreateTagExtract");
        theExtractTag.addActionListener(myMenuListener);
        theTasks.add(theExtractTag);

        /* Create the CreateWC menuItem */
        theUpdateWC = new JMenuItem("UpdateWorkingCopy");
        theUpdateWC.addActionListener(myMenuListener);
        theTasks.add(theUpdateWC);

        /* Create the CreateWC menuItem */
        theRevertWC = new JMenuItem("RevertWorkingCopy");
        theRevertWC.addActionListener(myMenuListener);
        theTasks.add(theRevertWC);

        /* Create the CreateBranchTags menuItem */
        theCreateBrnTags = new JMenuItem("CreateBranchTags");
        theCreateBrnTags.addActionListener(myMenuListener);
        theTasks.add(theCreateBrnTags);

        /* Create the CreateNewBranch menuItem */
        theCreateNewBrn = new JMenuItem("CreateNewBranch");
        theCreateNewBrn.addActionListener(myMenuListener);
        theTasks.add(theCreateNewBrn);

        /* Add the Menu bar */
        theFrame.setJMenuBar(myMainMenu);

        /* Attach the panel to the frame */
        theStatusPanel.setOpaque(true);
        theFrame.setContentPane(theStatusPanel);
        theFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        theFrame.addWindowListener(theCloseHandler);

        /* Show the frame */
        theFrame.pack();
        theFrame.setLocationRelativeTo(null);
        theFrame.setVisible(true);

        /* Create and run discoverData thread */
        DiscoverData myThread = new DiscoverData(thePreferenceMgr, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
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
     * Complete thread task.
     * @param pTask the task that has completed
     */
    public void completeTask(final Object pTask) {
        /* If this is the discoverData thread */
        if (pTask instanceof DiscoverData) {
            /* Access correctly */
            DiscoverData myThread = (DiscoverData) pTask;

            /* If there was no error */
            if (myThread.getError() == null) {
                /* Report data to manager */
                setData(myThread);
            }
        }

        /* Enable other tasks */
        theTasks.setEnabled(true);
    }

    /**
     * Create and run the CheckOutWC thread.
     */
    private void runCheckOutWC() {
        /* Create and run createWorkingCopy thread */
        Branch myBranch = theRepository.locateBranch("JFinanceApp", "v1.1.0");
        Branch[] myList = new Branch[] { myBranch };
        CreateWorkingCopy myThread = new CreateWorkingCopy(myList, SVNRevision.HEAD, new File(
                "c:\\Users\\Tony\\TestWC"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the TagExtract thread.
     */
    private void runCreateTagExtract() {
        /* Create and run createTagExtract thread */
        Tag myTag = theRepository.locateTag("JFinanceApp", "v1.0.0", 1);
        Tag[] myList = new Tag[] { myTag };
        CreateTagExtract myThread = new CreateTagExtract(myList, new File("c:\\Users\\Tony\\TestXT"),
                theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the UpdateWC thread.
     */
    private void runUpdateWC() {
        /* Create and run updateWorkingCopy thread */
        UpdateWorkingCopy myThread = new UpdateWorkingCopy(theWorkingSet, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the RevertWC thread.
     */
    private void runRevertWC() {
        /* Create and run revertWorkingCopy thread */
        RevertWorkingCopy myThread = new RevertWorkingCopy(theWorkingSet, theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateBranchTags thread.
     */
    private void runCreateBranchTags() {
        /* Create and run createBranchTags thread */
        Branch myBranch = theRepository.locateBranch("JFinanceApp", "v1.1.0");
        Branch[] myList = new Branch[] { myBranch };
        CreateBranchTags myThread = new CreateBranchTags(myList, new File("c:\\Users\\Tony\\TestBT"),
                theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateNewBranch thread.
     */
    private void runCreateNewBranch() {
        /* Create and run createBranchTags thread */
        Tag myTag = theRepository.locateTag("JFinanceApp", "v1.0.0", 1);
        Tag[] myList = new Tag[] { myTag };
        CreateNewBranch myThread = new CreateNewBranch(myList, BranchOpType.MAJOR, new File(
                "c:\\Users\\Tony\\TestNB"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * MenuListener class.
     */
    private final class MenuListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this is the DataManager window */
            if (theShowDataMgr.equals(o)) {
                /* Create the data window */
                theDataWdw = new JDataWindow(theFrame, theDataMgr);

                /* Listen for its closure */
                theDataWdw.addWindowListener(theCloseHandler);

                /* Disable the menu item */
                theShowDataMgr.setEnabled(false);

                /* Display it */
                theDataWdw.showDialog();

                /* If this is the CreateWC task */
            } else if (theCreateWC.equals(o)) {
                /* run the thread */
                runCheckOutWC();

                /* If this is the ExtractTag task */
            } else if (theExtractTag.equals(o)) {
                /* run the thread */
                runCreateTagExtract();

                /* If this is the UpdateWC task */
            } else if (theUpdateWC.equals(o)) {
                /* run the thread */
                runUpdateWC();

                /* If this is the RevertWC task */
            } else if (theRevertWC.equals(o)) {
                /* run the thread */
                runRevertWC();

                /* If this is the CreateBranchTags task */
            } else if (theCreateBrnTags.equals(o)) {
                /* run the thread */
                runCreateBranchTags();

                /* If this is the CreateNewBranch task */
            } else if (theCreateNewBrn.equals(o)) {
                /* run the thread */
                runCreateNewBranch();
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
                theStatusPanel.shutdown();

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
