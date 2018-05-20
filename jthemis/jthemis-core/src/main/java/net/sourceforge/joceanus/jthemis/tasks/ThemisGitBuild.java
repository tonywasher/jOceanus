/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.tasks;

import java.io.File;
import java.util.Date;
import java.util.Iterator;

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

import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisLogicException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitBranch;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitComponent;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitRevision;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmOwner;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnTag;
import net.sourceforge.joceanus.jthemis.tasks.ThemisGitAnalyser.ThemisGitAnchorMap;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnBranchExtractPlan;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractAnchor;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnExtractView;
import net.sourceforge.joceanus.jthemis.tasks.ThemisSvnExtract.ThemisSvnTagExtractPlan;

/**
 * Migrate a SubVersion Component to a new Git Repository.
 */
public class ThemisGitBuild {
    /**
     * GitComponent.
     */
    private final ThemisGitComponent theComponent;

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
    private final ThemisSvnExtract thePlan;

    /**
     * The Anchor Map.
     */
    private final ThemisGitAnchorMap theAnchorMap;

    /**
     * Constructor.
     * @param pSource the source subversion component
     * @param pExtract the extract plan
     * @throws OceanusException on error
     */
    public ThemisGitBuild(final ThemisSvnComponent pSource,
                          final ThemisSvnExtract pExtract) throws OceanusException {
        /* Access the gitRepository and component */
        final ThemisGitRepository myGitRepo = pExtract.getGitRepository();
        ThemisGitComponent myTarget = pExtract.getGitComponent();

        /* Create the target component if it does not exist */
        if (myTarget == null) {
            myTarget = myGitRepo.createComponent(pSource.getName());
        }
        theComponent = myTarget;

        /* Access the work directory */
        theWorkDir = theComponent.getWorkingDir();

        /* Access the Git instance */
        final Repository myRepo = theComponent.getGitRepo();
        theGit = new Git(myRepo);
        theCommitter = new PersonIdent(myRepo);

        /* Store the extract plan for the component */
        thePlan = pExtract;
        theAnchorMap = thePlan.getGitAnchorMap();
    }

    /**
     * Build the repository.
     * @param pReport the report status
     * @throws OceanusException on error
     */
    public void buildRepository(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Report start of analysis */
        pReport.initTask("Building Git Component");

        /* Report number of stages */
        pReport.setNumStages(thePlan.numPlans());

        /* Report stage */
        pReport.initTask("Building Trunk");

        /* Build the trunk if required */
        if (thePlan.hasTrunk()) {
            buildTrunk(pReport);
        }

        /* Loop to build the branches */
        SvnExtractStatus myStatus = SvnExtractStatus.REPEAT;
        while (myStatus.doRetry()) {
            /* Build the branches */
            myStatus = tryBuildBranches(pReport);

            /* If we are not cancelled */
            if (!myStatus.isCancelled()) {
                /* Build the tags */
                myStatus = myStatus.combineStatus(tryBuildTags(pReport));
            }
        }

        /* Note failure */
        if (myStatus.isBlocked()) {
            throw new ThemisLogicException("Blocked on extract");
        }

        /* If we are complete */
        if (myStatus.isComplete()) {
            /* Report stage */
            pReport.initTask("Collecting garbage");

            /* Perform garbage collection */
            garbageCollect();

            /* re-discover gitComponent */
            theComponent.reDiscover(pReport);
        }
    }

    /**
     * Try to build the branches.
     * @param pReport the report status
     * @return the extract status
     * @throws OceanusException on error
     */
    private SvnExtractStatus tryBuildBranches(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Create flags */
        boolean isBlocked = false;
        boolean isExtracted = false;

        /* Build the branches */
        final Iterator<ThemisSvnBranchExtractPlan> myBranchIterator = thePlan.branchIterator();
        while (myBranchIterator.hasNext()) {
            final ThemisSvnBranchExtractPlan myPlan = myBranchIterator.next();

            /* Ignore if we have already extracted this plan */
            if (myPlan.haveExtracted()) {
                continue;
            }

            /* Access anchor point */
            final ThemisSvnExtractAnchor myAnchor = myPlan.getAnchor();
            if (myAnchor == null) {
                throw new ThemisLogicException("Unanchored branch");
            }

            /* If we have seen this anchor point */
            final ThemisGitRevision myLastCommit = theAnchorMap.getCommit(myAnchor);
            if (myLastCommit != null) {
                /* Report stage */
                pReport.initTask("Building branch " + myPlan.getOwner());

                /* Build the branch */
                buildBranch(pReport, myPlan, myLastCommit);

                /* Note completion */
                myPlan.markExtracted();
                isExtracted = true;

                /* else note blockage */
            } else {
                isBlocked = true;
            }
        }

        /* return status */
        return SvnExtractStatus.determineStatus(isExtracted, isBlocked);
    }

    /**
     * Try to build the tags.
     * @param pReport the report status
     * @return the extract status
     * @throws OceanusException on error
     */
    private SvnExtractStatus tryBuildTags(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Create flags */
        boolean isBlocked = false;
        boolean isExtracted = false;

        /* Build the tags */
        final Iterator<ThemisSvnTagExtractPlan> myTagIterator = thePlan.tagIterator();
        while (myTagIterator.hasNext()) {
            final ThemisSvnTagExtractPlan myPlan = myTagIterator.next();

            /* Ignore if we have already extracted this plan */
            if (myPlan.haveExtracted()) {
                continue;
            }

            /* Access anchor point */
            final ThemisSvnExtractAnchor myAnchor = myPlan.getAnchor();
            if (myAnchor == null) {
                throw new ThemisLogicException("Unanchored tag");
            }

            /* If we have seen this anchor point */
            final ThemisGitRevision myLastCommit = theAnchorMap.getCommit(myAnchor);
            if (myLastCommit != null) {
                /* Report stage */
                pReport.initTask("Building tag " + myPlan.getOwner());

                /* Build the tag */
                buildTag(pReport, myPlan, myLastCommit);

                /* Note completion */
                myPlan.markExtracted();
                isExtracted = true;

                /* else note blockage */
            } else {
                isBlocked = true;
            }
        }

        /* return status */
        return SvnExtractStatus.determineStatus(isExtracted, isBlocked);
    }

    /**
     * Build the trunk.
     * @param pReport the report status
     * @throws OceanusException on error
     */
    private void buildTrunk(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan */
            final ThemisSvnBranchExtractPlan myPlan = thePlan.getTrunkPlan();
            final ThemisSvnBranch myOwner = myPlan.getOwner();

            /* Report plan steps */
            pReport.setNumSteps(myPlan.numViews());

            /* Obtain the active profile */
            MetisProfile myTask = pReport.getActiveTask();
            myTask = myTask.startTask("buildTrunk");

            /* If the master branch exists */
            if (myPlan.isExisting()) {
                /* Ensure that we are on master branch */
                final CheckoutCommand myCheckout = theGit.checkout();
                myCheckout.setName(ThemisGitBranch.BRN_MASTER);
                myCheckout.call();
            } else {
                /* Declare the branch */
                theComponent.declareNewBranch(myOwner);
            }

            /* Commit the plan */
            commitPlan(pReport, myOwner, null, myPlan.viewIterator());

            /* Complete the task */
            myTask.end();

            /* Catch Git exceptions */
        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to build trunk", e);
        }
    }

    /**
     * Build the branch.
     * @param pReport the report status
     * @param pBranchPlan the branch to build
     * @param pLastCommit the commit to branch from
     * @throws OceanusException on error
     */
    private void buildBranch(final MetisThreadStatusReport pReport,
                             final ThemisSvnBranchExtractPlan pBranchPlan,
                             final ThemisGitRevision pLastCommit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan owner */
            final ThemisSvnBranch myOwner = pBranchPlan.getOwner();

            /* Obtain the active profile */
            MetisProfile myTask = pReport.getActiveTask();
            myTask = myTask.startTask("buildBranch:" + myOwner.getName());

            /* Check that we are starting with a clean directory */
            final StatusCommand myStatusCmd = theGit.status();
            final Status myStatus = myStatusCmd.call();
            if (!myStatus.isClean()) {
                throw new ThemisLogicException("Dirty working directory");
            }

            /* Create a checkoutCommand for this branch */
            final CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setName(myOwner.getName());

            /* If we need to create the branch */
            if (!pBranchPlan.isExisting()) {
                /* Ask to create the branch from the anchor point */
                myCheckout.setStartPoint(pLastCommit.getCommit());
                myCheckout.setCreateBranch(true);

                /* Declare the branch */
                theComponent.declareNewBranch(myOwner);
            }

            /* CheckOut the branch */
            myCheckout.call();

            /* Report plan steps */
            pReport.setNumSteps(pBranchPlan.numViews());

            /* Commit the plan */
            commitPlan(pReport, myOwner, pLastCommit, pBranchPlan.viewIterator());

            /* Complete the task */
            myTask.end();

            /* Catch Git exceptions */
        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to build branch", e);
        }
    }

    /**
     * Build the tag.
     * @param pReport the report status
     * @param pTagPlan the tag to build
     * @param pLastCommit the commit to branch from
     * @throws OceanusException on error
     */
    private void buildTag(final MetisThreadStatusReport pReport,
                          final ThemisSvnTagExtractPlan pTagPlan,
                          final ThemisGitRevision pLastCommit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan owner */
            final ThemisSvnTag myOwner = pTagPlan.getOwner();

            /* Obtain the active profile */
            MetisProfile myTask = pReport.getActiveTask();
            myTask = myTask.startTask("buildBranch:" + myOwner.getName());

            /* If there are changes in working directory */
            final StatusCommand myStatusCmd = theGit.status();
            final Status myStatus = myStatusCmd.call();
            if (!myStatus.isClean()) {
                throw new ThemisLogicException("Dirty working directory");
            }

            /* Check the commit out as a head-less checkout */
            final CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setName(pLastCommit.getCommit().name());
            myCheckout.call();

            /* Declare the tag if needed */
            if (!pTagPlan.isExisting()) {
                theComponent.declareNewTag(myOwner);
            }

            /* Report plan steps */
            pReport.setNumSteps(pTagPlan.numViews());

            /* Commit the plan */
            final Date myTagDate = commitPlan(pReport, myOwner, pLastCommit, pTagPlan.viewIterator());

            /* Create tag if no cancellation */
            pReport.checkForCancellation();

            /* Create the tag */
            final TagCommand myTag = theGit.tag();
            myTag.setName(myOwner.getName());
            myTag.setAnnotated(true);
            myTag.setTagger(new PersonIdent(theCommitter, myTagDate));
            myTag.call();

            /* Complete the task */
            myTask.end();

            /* Catch Git exceptions */
        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to build tag", e);
        }
    }

    /**
     * Commit a plan.
     * @param pReport the report status
     * @param pOwner the owner
     * @param pBaseCommit the base commit
     * @param pIterator the view iterator
     * @return the date of the last Commit
     * @throws OceanusException on error
     */
    private Date commitPlan(final MetisThreadStatusReport pReport,
                            final ThemisScmOwner pOwner,
                            final ThemisGitRevision pBaseCommit,
                            final Iterator<ThemisSvnExtractView> pIterator) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Determine the base commit */
            ThemisGitRevision myLastCommit = pBaseCommit;

            /* Access a date formatter */
            final TethysDateFormatter myFormatter = new TethysDateFormatter();

            /* Iterate through the elements */
            while (pIterator.hasNext()) {
                final ThemisSvnExtractView myView = pIterator.next();

                /* Report plan step */
                final String myFormat = myFormatter.formatJavaDate(myView.getDate());
                pReport.setNextStep(myFormat);

                /* Obtain revision string */
                final String myRevString = myView.getGitRevisionNo();

                /* Obtain the active profile */
                final MetisProfile myBaseTask = pReport.getActiveTask();
                MetisProfile myTask = myBaseTask.startTask("extractCode:" + myRevString);

                /* Extract the details to the directory, preserving the GitDir */
                myView.extractItem(theWorkDir, ThemisGitComponent.NAME_GITDIR);

                /* Note whether this is the last extract */
                final boolean isLast = !pIterator.hasNext();

                /* If there are changes to commit */
                StatusCommand myStatusCmd = theGit.status();
                Status myStatus = myStatusCmd.call();
                final boolean isClean = myStatus.isClean();
                if (!isClean || isLast) {
                    /* Start the commit task */
                    myTask = myBaseTask.startTask("commitCode:" + myRevString);

                    /* If we are not clean */
                    if (!isClean) {
                        /* Ensure that all items are staged */
                        final AddCommand myAdd = theGit.add();
                        myAdd.addFilepattern(".");
                        myAdd.call();
                    }

                    /* Build the commit command */
                    final CommitCommand myCommit = theGit.commit();
                    myCommit.setAll(true);
                    myCommit.setCommitter(new PersonIdent(theCommitter, myView.getDate()));
                    myCommit.setMessage(ThemisGitRevisionHistory.createGitLogMessage(myRevString, myView.getLogMessage()));
                    if (isClean) {
                        myCommit.setAllowEmpty(true);
                    }

                    /* Commit the changes */
                    final RevCommit myRevision = myCommit.call();

                    /* Record the commit */
                    myLastCommit = theComponent.getGitRevisionForNewCommit(pOwner, myRevString, myRevision);
                    myView.setGitRevision(myLastCommit);
                }

                /* Complete the task */
                myTask.end();

                /* Check that we have ended up with a clean directory */
                myStatusCmd = theGit.status();
                myStatus = myStatusCmd.call();
                if (!myStatus.isClean()) {
                    throw new ThemisLogicException("Uncommitted changes");
                }

                /* Store details of the commit */
                final ThemisSvnExtractAnchor myAnchor = myView.getAnchor();
                theAnchorMap.addAnchor(myAnchor, myLastCommit);
            }

            /* Return the date of the tag */
            return myLastCommit.getCommit().getAuthorIdent().getWhen();

            /* Catch Git Exceptions */
        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to commit plan", e);
        }
    }

    /**
     * Perform garbage collection.
     * @throws OceanusException on error
     */
    private void garbageCollect() throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Switch back to master */
            final CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setName(ThemisGitBranch.BRN_MASTER);
            myCheckout.call();

            /* Call garbage collection */
            final GarbageCollectCommand myCmd = theGit.gc();
            myCmd.call();

            /* Catch git exceptions */
        } catch (GitAPIException e) {
            throw new ThemisIOException("Failed to garbage collect", e);
        }
    }

    /**
     * Extract status.
     */
    private enum SvnExtractStatus {
        /**
         * All plans blocked.
         */
        BLOCKED,

        /**
         * Some plans blocked.
         */
        OBSCURED,

        /**
         * All plans now extracted.
         */
        COMPLETED,

        /**
         * All plans already extracted.
         */
        FINISHED,

        /**
         * Plans need to be repeated.
         */
        REPEAT,

        /**
         * Extract cancelled.
         */
        CANCELLED;

        /**
         * Combine status.
         * @param pStatus the secondary status
         * @return the new status
         */
        SvnExtractStatus combineStatus(final SvnExtractStatus pStatus) {
            switch (pStatus) {
                case OBSCURED:
                    return isComplete()
                                        ? BLOCKED
                                        : REPEAT;
                case COMPLETED:
                    return isComplete()
                                        ? FINISHED
                                        : REPEAT;
                case FINISHED:
                    return isComplete()
                                        ? FINISHED
                                        : BLOCKED;
                case CANCELLED:
                    return CANCELLED;
                case BLOCKED:
                default:
                    return BLOCKED;
            }
        }

        /**
         * Is the extract complete?
         * @return true/false
         */
        boolean isComplete() {
            switch (this) {
                case FINISHED:
                case COMPLETED:
                    return true;
                default:
                    return false;
            }
        }

        /**
         * Should we retry?
         * @return true/false
         */
        boolean doRetry() {
            return this == REPEAT;
        }

        /**
         * Is the extract cancelled?
         * @return true/false
         */
        boolean isCancelled() {
            return this == CANCELLED;
        }

        /**
         * Is the extract blocked?
         * @return true/false
         */
        boolean isBlocked() {
            return this == BLOCKED;
        }

        /**
         * Determine status.
         * @param pExtracted were plans extracted?
         * @param pBlocked were plans blocked?
         * @return the status
         */
        static SvnExtractStatus determineStatus(final boolean pExtracted,
                                                final boolean pBlocked) {
            if (pBlocked) {
                return pExtracted
                                  ? OBSCURED
                                  : BLOCKED;
            } else {
                return pExtracted
                                  ? COMPLETED
                                  : FINISHED;
            }
        }
    }
}
