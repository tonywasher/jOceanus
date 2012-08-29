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
package net.sourceforge.JSvnManager.tasks;

import java.io.File;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JPreferenceSet.PreferenceManager;
import net.sourceforge.JSvnManager.data.Branch;
import net.sourceforge.JSvnManager.data.Branch.BranchList;
import net.sourceforge.JSvnManager.data.Component;
import net.sourceforge.JSvnManager.data.Repository;
import net.sourceforge.JSvnManager.data.SubVersionPreferences;
import net.sourceforge.JSvnManager.data.Tag;
import net.sourceforge.JSvnManager.data.Tag.TagList;
import net.sourceforge.JSvnManager.project.ProjectDefinition;
import net.sourceforge.JiraAccess.data.Issue;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAdapter;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

/**
 * Handles creating branches/tags in subversion.
 * @author Tony Washer
 */
public class VersionMgr {
    /**
     * Subversion Work Directory.
     */
    private static final String DIR_SVNWORK = "svnTempWork";

    /**
     * The Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * The Preferences.
     */
    private final SubVersionPreferences thePreferences;

    /**
     * The Client Manager.
     */
    private final SVNClientManager theMgr;

    /**
     * Repository.
     */
    private final Repository theRepository;

    /**
     * Constructor.
     * @param pRepository the repository
     */
    public VersionMgr(final Repository pRepository) {
        /* Store Parameters */
        theRepository = pRepository;
        thePreferenceMgr = theRepository.getPreferenceMgr();
        theMgr = theRepository.getClientManager();
        thePreferences = thePreferenceMgr.getPreferenceSet(SubVersionPreferences.class);
    }

    @Override
    public void finalize() throws Throwable {
        /* Release the client manager */
        theRepository.releaseClientManager(theMgr);
        super.finalize();
    }

    /**
     * Create major branch.
     * @param pSource the tag to base the branch on
     * @param pIssue the issue to make changes against
     * @return the new branch
     * @throws JDataException on error
     */
    protected Branch createMajorBranch(final Tag pSource,
                                       final Issue pIssue) throws JDataException {
        /* Obtain the component that we are creating the branch in */
        Component myComponent = pSource.getComponent();
        BranchList myList = myComponent.getBranchList();

        /* Determine the branch to create */
        Branch myBranch = myList.nextMajorBranch();

        /* Create the branch */
        createBranch(myBranch, pSource, pIssue);

        /* re-discover branches */
        myList.discover();

        /* Return the newly created branch */
        return myList.locateBranch(myBranch);
    }

    /**
     * Create minor branch.
     * @param pSource the tag to base the branch on
     * @param pIssue the issue to make changes against
     * @return the new branch
     * @throws JDataException on error
     */
    protected Branch createMinorBranch(final Tag pSource,
                                       final Issue pIssue) throws JDataException {
        /* Obtain the component that we are creating the branch in */
        Component myComponent = pSource.getComponent();
        BranchList myList = myComponent.getBranchList();

        /* Determine the branch to create */
        Branch myBranch = myList.nextMinorBranch(pSource.getBranch());

        /* Create the branch */
        createBranch(myBranch, pSource, pIssue);

        /* re-discover branches */
        myList.discover();

        /* Return the newly created branch */
        return myList.locateBranch(myBranch);
    }

    /**
     * Create delta branch.
     * @param pSource the tag to base the branch on
     * @param pIssue the issue to make changes against
     * @return the new branch
     * @throws JDataException on error
     */
    protected Branch createDeltaBranch(final Tag pSource,
                                       final Issue pIssue) throws JDataException {
        /* Obtain the component that we are creating the branch in */
        Component myComponent = pSource.getComponent();
        BranchList myList = myComponent.getBranchList();

        /* Determine the branch to create */
        Branch myBranch = myList.nextDeltaBranch(pSource.getBranch());

        /* Create the branch */
        createBranch(myBranch, pSource, pIssue);

        /* re-discover branches */
        myList.discover();

        /* Return the newly created branch */
        return myList.locateBranch(myBranch);
    }

    /**
     * Create next tag for branch.
     * @param pSource the branch to tag
     * @param pIssue the issue to make changes against
     * @return the new tag
     * @throws JDataException on error
     */
    public Tag createNextTag(final Branch pSource,
                             final Issue pIssue) throws JDataException {
        /* Access tag list */
        TagList myList = pSource.getTagList();

        /* Determine the tag to create */
        Tag myTag = myList.nextTag();

        /* Create the branch */
        createTag(myTag, pSource, pIssue);

        /* re-discover tags */
        myList.discover();

        /* Return the newly created tag */
        return myList.locateTag(myTag);
    }

    /**
     * Count objects in URL.
     * @param pURL the URL
     * @return the object count
     */
    private Integer countObjects(final SVNURL pURL) {
        /* Access a LogClient */
        SVNLogClient myClient = theMgr.getLogClient();
        CountHandler myHandler = new CountHandler();

        /* Protect against exceptions */
        try {
            /* List the objects recursively */
            myClient.doList(pURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.INFINITY,
                            SVNDirEntry.DIRENT_ALL, myHandler);

            /* Return the count */
            return myHandler.getObjectCount();
        } catch (SVNException e) {
            return null;
        }
    }

    /**
     * The Count Handler.
     */
    protected static final class CountHandler implements ISVNDirEntryHandler {
        /**
         * The object count.
         */
        private int theCount = 0;

        /**
         * Obtain the object count.
         * @return the object count
         */
        public int getObjectCount() {
            return theCount;
        }

        @Override
        public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
            /* Ignore if is top-level */
            if (pEntry.getRelativePath().length() == 0) {
                return;
            }

            /* Increment the count */
            theCount++;
        }
    }

    /**
     * Create branch in temporary working copy.
     * @param pTarget the branch to create
     * @param pSource the tag to base the branch on
     * @param pIssue the issue to make changes against
     * @throws JDataException on error
     */
    private void createBranch(final Branch pTarget,
                              final Tag pSource,
                              final Issue pIssue) throws JDataException {
        /* Access details */
        Component myComp = pTarget.getComponent();

        /* Access clients */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();
        SVNCopyClient myCopy = theMgr.getCopyClient();
        // SVNCommitClient myCommit = theMgr.getCommitClient();

        /* Protect against exceptions */
        try {
            /* Access the work and target */
            File myWork = prepareWorkDir(thePreferences);
            File myTarget = new File(myWork, pTarget.getBranchName());

            /* Determine the URL for the branches path */
            String myBase = myComp.getBranchesPath();
            SVNURL myURL = SVNURL.parseURIDecoded(myBase);

            /* Check out the path */
            myUpdate.doCheckout(myURL, myWork, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.EMPTY, false);

            /* Determine the source to copy from */
            SVNURL mySrcURL = pSource.getURL();
            SVNCopySource mySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, mySrcURL);
            SVNCopySource[] mySrcs = new SVNCopySource[] { mySource };

            /* Count the objects that we are to copy */
            Integer myCount = countObjects(mySrcURL);
            CopyHandler myCopyHandler = new CopyHandler();
            myCopy.setEventHandler(myCopyHandler);

            /* Copy the source tag to the new branch */
            myCopy.doCopy(mySrcs, myTarget, false, false, true);

            /* Determine count of objects copied */
            Integer myCopied = myCopyHandler.getCopyCount();

            /* Determine the location of the project definition */
            File myPom = ProjectDefinition.getProjectDefFile(myTarget);
            if (myPom != null) {
                ProjectDefinition myProject = ProjectDefinition.parseProjectFile(myPom);
            }

            /* Commit the changes */
            // SVNCommitPacket myPacket = myCommit.doCollectCommitItems(new File[] { myWork }, false, false,
            // SVNDepth.INFINITY, null);
            // SVNCommitInfo myResult = myCommit.doCommit(myPacket, false, "Created branch " +
            // pTarget.getBranchName() +
            // " from tag " + pSource.getTagName());

            /* Remove work directory */
            // Utils.removeDirectory(myWork);
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to create branch "
                    + pTarget.getBranchName(), e);
        }
    }

    /**
     * Create tag in temporary working copy.
     * @param pTarget the tag to create
     * @param pSource the branch to create the tag for
     * @param pIssue the issue to make changes against
     * @throws JDataException on error
     */
    private void createTag(final Tag pTarget,
                           final Branch pSource,
                           final Issue pIssue) throws JDataException {
        /* Access details */
        Component myComp = pTarget.getComponent();

        /* Access clients */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();
        SVNCopyClient myCopy = theMgr.getCopyClient();
        // SVNCommitClient myCommit = theMgr.getCommitClient();

        /* Protect against exceptions */
        try {
            /* Access the work and target */
            File myWork = prepareWorkDir(thePreferences);
            File myTarget = new File(myWork, pTarget.getTagName());

            /* Determine the URL for the tags path */
            String myBase = myComp.getTagsPath();
            SVNURL myURL = SVNURL.parseURIDecoded(myBase);

            /* Check out the path */
            myUpdate.doCheckout(myURL, myWork, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.EMPTY, false);

            /* Determine the source to copy from */
            SVNURL mySrcURL = pSource.getURL();
            SVNCopySource mySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, mySrcURL);
            SVNCopySource[] mySrcs = new SVNCopySource[] { mySource };

            /* Count the objects that we are to copy */
            Integer myCount = countObjects(mySrcURL);
            CopyHandler myCopyHandler = new CopyHandler();
            myCopy.setEventHandler(myCopyHandler);

            /* Copy the source branch to the new tag */
            myCopy.doCopy(mySrcs, myTarget, false, false, true);

            /* Determine count of objects copied */
            Integer myCopied = myCopyHandler.getCopyCount();

            /* Determine the location of the project definition */
            File myPom = ProjectDefinition.getProjectDefFile(myTarget);
            if (myPom != null) {
                ProjectDefinition myProject = ProjectDefinition.parseProjectFile(myPom);
            }

            /* Commit the changes */
            // SVNCommitPacket myPacket = myCommit.doCollectCommitItems(new File[] { myWork }, false, false,
            // SVNDepth.INFINITY, null);
            // SVNCommitInfo myResult = myCommit.doCommit(myPacket, false, "Created tag " +
            // pTarget.getTagName());

            /* Remove work directory */
            // Utils.removeDirectory(myWork);
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to create tag "
                    + pTarget.getTagName(), e);
        }
    }

    /**
     * Prepare temporary work directory.
     * @param pPreferences the preferences
     * @return the work directory
     */
    private static File prepareWorkDir(final SubVersionPreferences pPreferences) {
        /* Determine the name of the work directory */
        String myBase = pPreferences.getStringValue(SubVersionPreferences.NAME_SVN_BUILD);
        File myWork = new File(myBase + File.separator + DIR_SVNWORK);

        /* Remove the directory */
        if (!removeDirectory(myWork)) {
            return null;
        }

        /* Create the directory */
        if (!myWork.mkdir()) {
            return null;
        }

        /* Return the directory */
        return myWork;
    }

    /**
     * Remove a directory and all of its contents.
     * @param pDir the directory to remove
     * @return success/failure
     */
    public static boolean removeDirectory(final File pDir) {
        /* Clear the directory */
        if (!clearDirectory(pDir)) {
            return false;
        }

        /* Delete the directory itself */
        return (!pDir.exists()) || (pDir.delete());
    }

    /**
     * Clear a directory of all of its contents.
     * @param pDir the directory to clear
     * @return success/failure
     */
    public static boolean clearDirectory(final File pDir) {
        /* Handle trivial operations */
        if ((pDir == null) || (!pDir.exists())) {
            return true;
        }
        if (!pDir.isDirectory()) {
            return false;
        }

        /* Loop through all items */
        for (File myFile : pDir.listFiles()) {
            /* If the file is a directory */
            if (myFile.isDirectory()) {
                /* Remove it recursively */
                if (!removeDirectory(myFile)) {
                    return false;
                }

                /* else remove the file */
            } else if (!myFile.delete()) {
                return false;
            }
        }

        /* All cleared */
        return true;
    }

    /**
     * Copy Event Handler class.
     */
    private final class CopyHandler extends SVNEventAdapter {
        /**
         * Copy count.
         */
        private int theCount = 0;

        /**
         * Obtain the object count.
         * @return the copy count
         */
        public int getCopyCount() {
            return theCount;
        }

        @Override
        public void checkCancelled() throws SVNCancelException {
            // if (theStatus.isCancelled())
            // throw new SVNCancelException();
        }

        @Override
        public void handleEvent(final SVNEvent arg0,
                                final double arg1) throws SVNException {
            /* Increment the count */
            theCount++;
        }
    }
}
