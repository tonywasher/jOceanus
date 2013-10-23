/*******************************************************************************
 * jSvnManager: Java SubVersion Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jSvnManager.tasks;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jSvnManager.data.Branch;
import net.sourceforge.jOceanus.jSvnManager.data.Branch.BranchList;
import net.sourceforge.jOceanus.jSvnManager.data.Branch.BranchOpType;
import net.sourceforge.jOceanus.jSvnManager.data.Component;
import net.sourceforge.jOceanus.jSvnManager.data.JSvnReporter.ReportStatus;
import net.sourceforge.jOceanus.jSvnManager.data.Repository;
import net.sourceforge.jOceanus.jSvnManager.data.Tag;
import net.sourceforge.jOceanus.jSvnManager.project.ProjectDefinition;
import net.sourceforge.jOceanus.jSvnManager.project.ProjectId;

import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCopyClient;
import org.tmatesoft.svn.core.wc.SVNCopySource;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

/**
 * Handles creating branches/tags in subversion.
 * @author Tony Washer
 */
public class VersionMgr {
    /**
     * The Client Manager.
     */
    private final SVNClientManager theMgr;

    /**
     * Repository.
     */
    private final Repository theRepository;

    /**
     * Location.
     */
    private final File theLocation;

    /**
     * Report object.
     */
    private final ReportStatus theReport;

    /**
     * Event Handler.
     */
    private final VersionHandler theHandler = new VersionHandler();

    /**
     * Constructor.
     * @param pRepository the repository
     * @param pLocation the location
     * @param pReport the report object
     */
    public VersionMgr(final Repository pRepository,
                      final File pLocation,
                      final ReportStatus pReport) {
        /* Store Parameters */
        theRepository = pRepository;
        theLocation = pLocation;
        theReport = pReport;
        theMgr = theRepository.getClientManager();
        theMgr.setEventHandler(theHandler);
    }

    /**
     * Dispose of resources.
     */
    public void dispose() {
        /* Release the client manager */
        theRepository.releaseClientManager(theMgr);
    }

    /**
     * Create branch in temporary working copy.
     * @param pTarget the branch to create
     * @param pSource the tag to base the branch on
     * @throws JDataException on error
     */
    protected void createBranch(final Branch pTarget,
                                final Tag pSource) throws JDataException {
        /* Access details */
        Component myComp = pTarget.getComponent();

        /* Access clients */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();
        SVNCopyClient myCopy = theMgr.getCopyClient();

        /* Protect against exceptions */
        try {
            /* Access the work and target */
            File myWork = new File(theLocation, myComp.getName());
            File myTarget = new File(myWork, pTarget.getBranchName());

            /* Determine the URL for the branches path */
            String myBase = myComp.getBranchesPath();
            SVNURL myURL = SVNURL.parseURIEncoded(myBase);

            /* Check out the path */
            myUpdate.doCheckout(myURL, myWork, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.EMPTY, false);

            /* Determine the source to copy from */
            SVNURL mySrcURL = pSource.getURL();
            SVNCopySource mySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, mySrcURL);
            SVNCopySource[] mySrcs = new SVNCopySource[] { mySource };

            /* Copy the source tag to the new branch */
            myCopy.doCopy(mySrcs, myTarget, false, false, true);

            /* Clone the definition */
            pTarget.cloneDefinition(pSource.getProjectDefinition());
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to create branch "
                    + pTarget.getBranchName(), e);
        }
    }

    /**
     * Create tag in temporary working copy.
     * @param pTarget the tag to create
     * @param pSource the branch to create the tag for
     * @throws JDataException on error
     */
    private void createTag(final Tag pTarget,
                           final Branch pSource) throws JDataException {
        /* Access details */
        Component myComp = pTarget.getComponent();

        /* Access clients */
        SVNUpdateClient myUpdate = theMgr.getUpdateClient();
        SVNCopyClient myCopy = theMgr.getCopyClient();

        /* Protect against exceptions */
        try {
            /* Access the work and target */
            File myWork = new File(theLocation, myComp.getName());
            File myTarget = new File(myWork, pTarget.getTagName());

            /* Determine the URL for the tags path */
            String myBase = myComp.getTagsPath();
            SVNURL myURL = SVNURL.parseURIEncoded(myBase);

            /* Check out the path */
            myUpdate.doCheckout(myURL, myWork, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.EMPTY, false);

            /* Determine the source to copy from */
            SVNURL mySrcURL = pSource.getURL();
            SVNCopySource mySource = new SVNCopySource(SVNRevision.HEAD, SVNRevision.HEAD, mySrcURL);
            SVNCopySource[] mySrcs = new SVNCopySource[] { mySource };

            /* Copy the source branch to the new tag */
            myCopy.doCopy(mySrcs, myTarget, false, false, true);

            /* Clone the definition */
            pTarget.cloneDefinition(pSource.getProjectDefinition());
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to create tag "
                    + pTarget.getTagName(), e);
        }
    }

    /**
     * Create new Tags to the specified directory.
     * @param pBranches the branches to tag.
     * @return the list of tags that were created
     * @throws JDataException on error
     */
    public List<Tag> createTags(final Collection<Branch> pBranches) throws JDataException {
        /* Create the list of tags */
        List<Tag> myList = new ArrayList<Tag>();
        Tag myTag;

        /* Loop through branches */
        for (Branch myBranch : pBranches) {
            /* If the branch is tag-gable */
            if (myBranch.isTaggable()) {
                /* Determine the new tag */
                myTag = myBranch.nextTag();

                /* Create the tag */
                createTag(myTag, myBranch);

                /* else not tag-able */
            } else {
                /* Just use latest tag */
                myTag = myBranch.getTagList().latestTag();
            }

            /* Add it to the list */
            myList.add(myTag);
        }

        /* Adjust the dependencies */
        adjustTagDependencies(myList);

        /* Return the list */
        return myList;
    }

    /**
     * Create new Major branches to the specified directory.
     * @param pTags the tags to branch from.
     * @param pBranchType the type of branches to create
     * @return the list of branches that were created
     * @throws JDataException on error
     */
    public List<Branch> createBranches(final Collection<Tag> pTags,
                                       final BranchOpType pBranchType) throws JDataException {
        /* Create the list of branches */
        List<Branch> myList = new ArrayList<Branch>();

        /* Loop through tags */
        for (Tag myTag : pTags) {
            /* Determine the new branch */
            Branch myBranch = myTag.getBranch();
            Component myComp = myBranch.getComponent();
            BranchList myBranches = myComp.getBranchList();
            myBranch = myBranches.nextBranch(myTag.getBranch(), pBranchType);

            /* Create the branch */
            createBranch(myBranch, myTag);

            /* Add it to the list */
            myList.add(myBranch);
        }

        /* Adjust the dependencies */
        adjustBranchDependencies(myList);

        /* Return the list */
        return myList;
    }

    /**
     * Adjust dependencies.
     * @param pBranches the branches to adjust.
     * @throws JDataException on error
     */
    private void adjustBranchDependencies(final List<Branch> pBranches) throws JDataException {
        /* Loop through the branches */
        for (Branch myBranch : pBranches) {
            /* Access version */
            ProjectDefinition myTargDef = myBranch.getProjectDefinition();

            /* Loop through the branches */
            Iterator<Branch> myIterator = pBranches.iterator();
            while (myIterator.hasNext()) {
                Branch mySource = myIterator.next();

                /* Skip if we are the same branch */
                if (mySource.equals(myBranch)) {
                    continue;
                }

                /* Set new version */
                ProjectDefinition mySrcDef = mySource.getProjectDefinition();
                ProjectId myId = mySrcDef.getDefinition();
                myTargDef.setNewVersion(myId);
            }

            /* Write out the new definition */
            File myTarget = new File(theLocation, myBranch.getComponent().getName());
            myTargDef.writeToFile(myTarget);
        }
    }

    /**
     * Adjust dependencies.
     * @param pTags the tags to adjust.
     * @throws JDataException on error
     */
    private void adjustTagDependencies(final List<Tag> pTags) throws JDataException {
        /* Loop through the tags */
        for (Tag myTag : pTags) {
            /* Access version */
            ProjectDefinition myTargDef = myTag.getProjectDefinition();

            /* Loop through the branches */
            Iterator<Tag> myIterator = pTags.iterator();
            while (myIterator.hasNext()) {
                Tag mySource = myIterator.next();

                /* Skip if we are the same tag */
                if (mySource.equals(myTag)) {
                    continue;
                }

                /* Set new version */
                ProjectDefinition mySrcDef = mySource.getProjectDefinition();
                ProjectId myId = mySrcDef.getDefinition();
                myTargDef.setNewVersion(myId);
            }

            /* Write out the new definition */
            File myTarget = new File(theLocation, myTag.getComponent().getName());
            myTargDef.writeToFile(myTarget);
        }
    }

    /**
     * EventHandler.
     */
    private final class VersionHandler implements ISVNEventHandler {

        @Override
        public void checkCancelled() throws SVNCancelException {
            if (theReport.isCancelled()) {
                throw new SVNCancelException();
            }
        }

        @Override
        public void handleEvent(final SVNEvent pEvent,
                                final double pProgress) throws SVNException {
            /* Report activity */
            theReport.setNewStage(pEvent.getFile().getPath());
        }
    }
}
