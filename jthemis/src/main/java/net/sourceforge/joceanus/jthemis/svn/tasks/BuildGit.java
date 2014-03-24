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
package net.sourceforge.joceanus.jthemis.svn.tasks;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.JThemisLogicException;
import net.sourceforge.joceanus.jthemis.git.data.GitBranch;
import net.sourceforge.joceanus.jthemis.git.data.GitComponent;
import net.sourceforge.joceanus.jthemis.git.data.GitRepository;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.svn.data.SvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.SvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract.SvnBranchExtractPlan;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract.SvnExtractAnchor;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract.SvnExtractMigratedView;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract.SvnExtractView;
import net.sourceforge.joceanus.jthemis.svn.data.SvnExtract.SvnTagExtractPlan;
import net.sourceforge.joceanus.jthemis.svn.data.SvnTag;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.GarbageCollectCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.StatusCommand;
import org.eclipse.jgit.api.TagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

/**
 * Migrate a SubVersion Component to a new Git Repository.
 */
public class BuildGit {
    /**
     * The Work directory for the Git Component.
     */
    private final File theWorkDir;

    /**
     * The Git instance for the target.
     */
    private final Git theGit;

    /**
     * The Commit Id.
     */
    private final PersonIdent theCommitter;

    /**
     * The Subversion Extract Plan.
     */
    private final SvnExtract thePlan;

    /**
     * The Commit Map.
     */
    private final Map<String, RevCommit> theCommitMap;

    /**
     * Constructor.
     * @param pSource the source subversion component
     * @param pGitRepo the target Git repository
     * @throws JOceanusException on error
     */
    public BuildGit(final SvnComponent pSource,
                    final GitRepository pGitRepo) throws JOceanusException {
        /* Create the target component */
        GitComponent myTarget = pGitRepo.createComponent(pSource.getName());

        /* Access the work directory */
        theWorkDir = myTarget.getWorkingDir();

        /* Access the Git instance */
        Repository myRepo = myTarget.getGitRepo();
        theGit = new Git(myRepo);
        theCommitter = new PersonIdent(myRepo);

        /* Obtain the extract plan for the component */
        thePlan = new SvnExtract(pSource);

        /* Create the commit map */
        theCommitMap = new HashMap<String, RevCommit>();
    }

    /**
     * Build the repository.
     * @param pReport the report status
     * @throws JOceanusException on error
     */
    public void buildRepository(final ReportStatus pReport) throws JOceanusException {
        /* Report start of analysis */
        pReport.initTask("Building Git Component");

        /* Report number of stages */
        if (!pReport.setNumStages(thePlan.numPlans())) {
            return;
        }

        /* Report stage */
        pReport.initTask("Building Trunk");

        /* Build the trunk */
        buildTrunk(pReport);

        /* Build the branches */
        Iterator<SvnBranchExtractPlan> myBranchIterator = thePlan.branchIterator();
        while (myBranchIterator.hasNext()) {
            SvnBranchExtractPlan myPlan = myBranchIterator.next();

            /* Report stage */
            if (!pReport.initTask("Building branch " + myPlan.getOwner())) {
                return;
            }

            /* Build the branch */
            buildBranch(pReport, myPlan);
        }

        /* Build the tags */
        Iterator<SvnTagExtractPlan> myTagIterator = thePlan.tagIterator();
        while (myTagIterator.hasNext()) {
            SvnTagExtractPlan myPlan = myTagIterator.next();

            /* Report stage */
            if (!pReport.initTask("Building tag " + myPlan.getOwner())) {
                return;
            }

            /* Build the tag */
            buildTag(pReport, myPlan);
        }

        /* Report stage */
        if (!pReport.initTask("Collecting garbage")) {
            return;
        }

        /* Perform garbage collection */
        garbageCollect();
    }

    /**
     * Build the trunk.
     * @param pReport the report status
     * @throws JOceanusException on error
     */
    private void buildTrunk(final ReportStatus pReport) throws JOceanusException {
        /* Access the plan */
        SvnBranchExtractPlan myPlan = thePlan.getTrunkPlan();
        Object myOwner = myPlan.getOwner();

        /* Report plan steps */
        if (pReport.setNumSteps(myPlan.numViews())) {
            /* Commit the plan */
            commitPlan(pReport, myOwner, null, myPlan.viewIterator());
        }
    }

    /**
     * Build the branch.
     * @param pReport the report status
     * @param pBranchPlan the branch to build
     * @throws JOceanusException on error
     */
    private void buildBranch(final ReportStatus pReport,
                             final SvnBranchExtractPlan pBranchPlan) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan owner */
            SvnBranch myOwner = pBranchPlan.getOwner();

            /* Access anchor point */
            SvnExtractAnchor myAnchor = pBranchPlan.getAnchor();
            if (myAnchor == null) {
                throw new JThemisLogicException("Unanchored branch");
            }
            RevCommit myLastCommit = theCommitMap.get(myAnchor.toString());
            if (myLastCommit == null) {
                throw new JThemisLogicException("Branch Anchor not found");
            }

            /* Check that we are starting with a clean directory */
            StatusCommand myStatusCmd = theGit.status();
            Status myStatus = myStatusCmd.call();
            if (!myStatus.isClean()) {
                throw new JThemisLogicException("Dirty working directory");
            }

            /* Create a branch from this commit and check it out */
            CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setStartPoint(myLastCommit);
            myCheckout.setName(myOwner.getBranchName());
            myCheckout.setCreateBranch(true);
            myCheckout.call();

            /* Report plan steps */
            if (pReport.setNumSteps(pBranchPlan.numViews())) {
                /* Commit the plan */
                commitPlan(pReport, myOwner, myLastCommit, pBranchPlan.viewIterator());
            }

            /* Catch Git exceptions */
        } catch (GitAPIException e) {
            throw new JThemisIOException("Failed to build branch", e);
        }
    }

    /**
     * Build the tag.
     * @param pReport the report status
     * @param pTagPlan the tag to build
     * @throws JOceanusException on error
     */
    private void buildTag(final ReportStatus pReport,
                          final SvnTagExtractPlan pTagPlan) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan owner */
            SvnTag myOwner = pTagPlan.getOwner();

            /* Access anchor point */
            SvnExtractAnchor myAnchor = pTagPlan.getAnchor();
            if (myAnchor == null) {
                throw new JThemisLogicException("Unanchored tag");
            }
            RevCommit myLastCommit = theCommitMap.get(myAnchor.toString());
            if (myLastCommit == null) {
                throw new JThemisLogicException("Tag Anchor not found");
            }

            /* If there are changes in working directory */
            StatusCommand myStatusCmd = theGit.status();
            Status myStatus = myStatusCmd.call();
            if (!myStatus.isClean()) {
                throw new JThemisLogicException("Dirty working directory");
            }

            /* Check the commit out as a head-less checkout */
            CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setName(myLastCommit.name());
            myCheckout.call();

            /* Report plan steps */
            if (pReport.setNumSteps(pTagPlan.numViews())) {
                /* Commit the plan */
                commitPlan(pReport, myOwner, myLastCommit, pTagPlan.viewIterator());

                /* Create tag if no cancellation */
                if (!pReport.isCancelled()) {
                    /* Create the tag */
                    TagCommand myTag = theGit.tag();
                    myTag.setName(myOwner.getTagName());
                    myTag.setAnnotated(true);
                    myTag.setTagger(new PersonIdent(theCommitter, new Date()));
                    myTag.call();
                }
            }

            /* Catch Git exceptions */
        } catch (GitAPIException e) {
            throw new JThemisIOException("Failed to build tag", e);
        }
    }

    /**
     * Commit a plan.
     * @param pReport the report status
     * @param pOwner the owner
     * @param pBaseCommit the base commit
     * @param pIterator the view iterator
     * @throws JOceanusException on error
     */
    private void commitPlan(final ReportStatus pReport,
                            final Object pOwner,
                            final RevCommit pBaseCommit,
                            final Iterator<SvnExtractView> pIterator) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Determine the base commit */
            RevCommit myLastCommit = pBaseCommit;

            /* Access a date formatter */
            JDateDayFormatter myFormatter = new JDateDayFormatter();

            /* Iterate through the elements */
            while (pIterator.hasNext()) {
                SvnExtractView myView = pIterator.next();

                /* Report plan step */
                String myFormat = myFormatter.formatDate(myView.getDate());
                if (!pReport.setNewStep(myFormat)) {
                    return;
                }

                /* Extract the details to the directory, preserving the GitDir */
                myView.extractItem(theWorkDir, GitComponent.NAME_GITDIR);

                /* If there are changes to commit */
                StatusCommand myStatusCmd = theGit.status();
                Status myStatus = myStatusCmd.call();
                if (!myStatus.isClean()) {
                    /* Ensure that all items are staged */
                    AddCommand myAdd = theGit.add();
                    myAdd.addFilepattern(".");
                    myAdd.call();

                    /* Commit the changes */
                    CommitCommand myCommit = theGit.commit();
                    myCommit.setAll(true);
                    myCommit.setCommitter(new PersonIdent(theCommitter, myView.getDate()));
                    myCommit.setMessage(myView.getLogMessage());
                    myLastCommit = myCommit.call();
                }

                /* Check that we have ended up with a clean directory */
                myStatusCmd = theGit.status();
                myStatus = myStatusCmd.call();
                if (!myStatus.isClean()) {
                    throw new JThemisLogicException("Uncommitted changes");
                }

                /* Determine owner */
                Object myOwner = pOwner;
                if (myView instanceof SvnExtractMigratedView) {
                    SvnExtractMigratedView myMigrate = (SvnExtractMigratedView) myView;
                    myOwner = myMigrate.getOwner();
                }

                /* Store details of the commit */
                SvnExtractAnchor myAnchor = new SvnExtractAnchor(myOwner, myView.getRevision());
                theCommitMap.put(myAnchor.toString(), myLastCommit);
            }

            /* Catch Git Exceptions */
        } catch (GitAPIException e) {
            throw new JThemisIOException("Failed to commit plan", e);
        }
    }

    /**
     * Perform garbage collection.
     * @throws JOceanusException on error
     */
    private void garbageCollect() throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Switch back to master */
            CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setName(GitBranch.BRN_MASTER);
            myCheckout.setForce(true);
            myCheckout.call();

            /* Call garbage collection */
            GarbageCollectCommand myCmd = theGit.gc();
            myCmd.call();

            /* Catch git exceptions */
        } catch (GitAPIException e) {
            throw new JThemisIOException("Failed to garbage collect", e);
        }
    }
}
