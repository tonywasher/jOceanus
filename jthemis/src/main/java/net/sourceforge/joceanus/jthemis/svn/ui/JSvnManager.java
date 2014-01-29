/*******************************************************************************
 * jThemis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.svn.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.WindowConstants;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jmetis.viewer.JDataWindow;
import net.sourceforge.joceanus.jthemis.svn.data.SubVersionPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.SvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.SvnBranch.BranchOpType;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRepository;
import net.sourceforge.joceanus.jthemis.svn.data.SvnTag;
import net.sourceforge.joceanus.jthemis.svn.data.SvnWorkingCopy.SvnWorkingCopySet;
import net.sourceforge.joceanus.jthemis.svn.threads.CreateBranchTags;
import net.sourceforge.joceanus.jthemis.svn.threads.CreateNewBranch;
import net.sourceforge.joceanus.jthemis.svn.threads.CreateTagExtract;
import net.sourceforge.joceanus.jthemis.svn.threads.CreateWorkingCopy;
import net.sourceforge.joceanus.jthemis.svn.threads.DiscoverData;
import net.sourceforge.joceanus.jthemis.svn.threads.RevertWorkingCopy;
import net.sourceforge.joceanus.jthemis.svn.threads.UpdateWorkingCopy;

import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Top level JSvnManager window.
 */
public final class JSvnManager {
    /**
     * The Base component.
     */
    private static final String BASE_COMP = "jmoneywise";

    /**
     * The Base version.
     */
    private static final String BASE_BRANCH = "v1.2.1";

    /**
     * The Frame.
     */
    private final JFrame theFrame;

    /**
     * The Data Manager.
     */
    private final JDataManager theDataMgr;

    /**
     * Preferences.
     */
    private final SubVersionPreferences thePreferences;

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
    private SvnRepository theRepository;

    /**
     * The working copy set.
     */
    private SvnWorkingCopySet theWorkingSet;

    /**
     * Status panel.
     */
    private final JSvnStatusWindow theStatusPanel;

    /**
     * Constructor.
     * @param pLogger the logger
     */
    protected JSvnManager(final Logger pLogger) {
        /* Create the data manager */
        theDataMgr = new JDataManager(pLogger);

        /* Create the preference manager */
        PreferenceManager myPreferenceMgr = new PreferenceManager(pLogger);
        thePreferences = myPreferenceMgr.getPreferenceSet(SubVersionPreferences.class);

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
        DiscoverData myThread = new DiscoverData(myPreferenceMgr, theStatusPanel);
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
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.pathSeparator;

        /* Create and run createWorkingCopy thread */
        SvnBranch myBranch = theRepository.locateBranch(BASE_COMP, "v1.2.0");
        SvnBranch[] myList = new SvnBranch[]
        { myBranch };
        CreateWorkingCopy myThread = new CreateWorkingCopy(myList, SVNRevision.HEAD, new File(myPath + "TestWC"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the TagExtract thread.
     */
    private void runCreateTagExtract() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.pathSeparator;

        /* Create and run createTagExtract thread */
        SvnTag myTag = theRepository.locateTag(BASE_COMP, BASE_BRANCH, 1);
        SvnTag[] myList = new SvnTag[]
        { myTag };
        CreateTagExtract myThread = new CreateTagExtract(myList, new File(myPath + "TestXT"), theStatusPanel);
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
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.pathSeparator;

        /* Create and run createBranchTags thread */
        SvnBranch myBranch = theRepository.locateBranch(BASE_COMP, BASE_BRANCH);
        SvnBranch[] myList = new SvnBranch[]
        { myBranch };
        CreateBranchTags myThread = new CreateBranchTags(myList, new File(myPath + "TestBT"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * Create and run the CreateNewBranch thread.
     */
    private void runCreateNewBranch() {
        /* Access work directory */
        String myPath = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD) + File.pathSeparator;

        /* Create and run createBranchTags thread */
        SvnTag myTag = theRepository.locateTag(BASE_COMP, "v1.0.0", 1);
        SvnTag[] myList = new SvnTag[]
        { myTag };
        CreateNewBranch myThread = new CreateNewBranch(myList, BranchOpType.MAJOR, new File(myPath + "TestNB"), theStatusPanel);
        theTasks.setEnabled(false);
        theStatusPanel.runThread(myThread);
    }

    /**
     * MenuListener class.
     */
    private final class MenuListener
            implements ActionListener {

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
    private class WindowClose
            extends WindowAdapter {
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
                theFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                theFrame.dispose();

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
