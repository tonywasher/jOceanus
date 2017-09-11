/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2017 Tony Washer
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

import net.sourceforge.joceanus.jmetis.atlas.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisLogicException;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitBranch;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitComponent;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRepository;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract.SvnBranchExtractPlan;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract.SvnExtractAnchor;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract.SvnExtractMigratedView;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract.SvnExtractView;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnExtract.SvnTagExtractPlan;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnTag;

/**
 * Migrate a SubVersion Component to a new Git Repository.
 */
public class ThemisBuildGit {
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
     * The Commit Map.
     */
    private final SvnRevisionCommitMap theCommitMap;

    /**
     * Constructor.
     * @param pSource the source subversion component
     * @param pGitRepo the target Git repository
     * @throws OceanusException on error
     */
    public ThemisBuildGit(final ThemisSvnComponent pSource,
                          final ThemisGitRepository pGitRepo) throws OceanusException {
        /* Create the target component */
        final ThemisGitComponent myTarget = pGitRepo.createComponent(pSource.getName());

        /* Access the work directory */
        theWorkDir = myTarget.getWorkingDir();

        /* Access the Git instance */
        final Repository myRepo = myTarget.getGitRepo();
        theGit = new Git(myRepo);
        theCommitter = new PersonIdent(myRepo);

        /* Obtain the extract plan for the component */
        thePlan = new ThemisSvnExtract(pSource);

        /* Create the commit map */
        theCommitMap = new SvnRevisionCommitMap();
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

        /* Build the trunk */
        buildTrunk(pReport);

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
        final Iterator<SvnBranchExtractPlan> myBranchIterator = thePlan.branchIterator();
        while (myBranchIterator.hasNext()) {
            final SvnBranchExtractPlan myPlan = myBranchIterator.next();

            /* Ignore if we have already extracted this plan */
            if (myPlan.haveExtracted()) {
                continue;
            }

            /* Access anchor point */
            final SvnExtractAnchor myAnchor = myPlan.getAnchor();
            if (myAnchor == null) {
                throw new ThemisLogicException("Unanchored branch");
            }

            /* If we have seen this anchor point */
            final RevCommit myLastCommit = theCommitMap.getCommit(myAnchor);
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
        final Iterator<SvnTagExtractPlan> myTagIterator = thePlan.tagIterator();
        while (myTagIterator.hasNext()) {
            final SvnTagExtractPlan myPlan = myTagIterator.next();

            /* Ignore if we have already extracted this plan */
            if (myPlan.haveExtracted()) {
                continue;
            }

            /* Access anchor point */
            final SvnExtractAnchor myAnchor = myPlan.getAnchor();
            if (myAnchor == null) {
                throw new ThemisLogicException("Unanchored tag");
            }

            /* If we have seen this anchor point */
            final RevCommit myLastCommit = theCommitMap.getCommit(myAnchor);
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
        /* Access the plan */
        final SvnBranchExtractPlan myPlan = thePlan.getTrunkPlan();
        final Object myOwner = myPlan.getOwner();

        /* Report plan steps */
        pReport.setNumSteps(myPlan.numViews());

        /* Commit the plan */
        commitPlan(pReport, myOwner, null, myPlan.viewIterator());
    }

    /**
     * Build the branch.
     * @param pReport the report status
     * @param pBranchPlan the branch to build
     * @param pLastCommit the commit to branch from
     * @throws OceanusException on error
     */
    private void buildBranch(final MetisThreadStatusReport pReport,
                             final SvnBranchExtractPlan pBranchPlan,
                             final RevCommit pLastCommit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan owner */
            final ThemisSvnBranch myOwner = pBranchPlan.getOwner();

            /* Check that we are starting with a clean directory */
            final StatusCommand myStatusCmd = theGit.status();
            final Status myStatus = myStatusCmd.call();
            if (!myStatus.isClean()) {
                throw new ThemisLogicException("Dirty working directory");
            }

            /* Create a branch from this commit and check it out */
            final CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setStartPoint(pLastCommit);
            myCheckout.setName(myOwner.getBranchName());
            myCheckout.setCreateBranch(true);
            myCheckout.call();

            /* Report plan steps */
            pReport.setNumSteps(pBranchPlan.numViews());

            /* Commit the plan */
            commitPlan(pReport, myOwner, pLastCommit, pBranchPlan.viewIterator());

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
                          final SvnTagExtractPlan pTagPlan,
                          final RevCommit pLastCommit) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Access the plan owner */
            final ThemisSvnTag myOwner = pTagPlan.getOwner();

            /* If there are changes in working directory */
            final StatusCommand myStatusCmd = theGit.status();
            final Status myStatus = myStatusCmd.call();
            if (!myStatus.isClean()) {
                throw new ThemisLogicException("Dirty working directory");
            }

            /* Check the commit out as a head-less checkout */
            final CheckoutCommand myCheckout = theGit.checkout();
            myCheckout.setName(pLastCommit.name());
            myCheckout.call();

            /* Report plan steps */
            pReport.setNumSteps(pTagPlan.numViews());
            /* Commit the plan */
            commitPlan(pReport, myOwner, pLastCommit, pTagPlan.viewIterator());

            /* Create tag if no cancellation */
            pReport.checkForCancellation();

            /* Create the tag */
            final TagCommand myTag = theGit.tag();
            myTag.setName(myOwner.getTagName());
            myTag.setAnnotated(true);
            myTag.setTagger(new PersonIdent(theCommitter, new Date()));
            myTag.call();

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
     * @throws OceanusException on error
     */
    private void commitPlan(final MetisThreadStatusReport pReport,
                            final Object pOwner,
                            final RevCommit pBaseCommit,
                            final Iterator<SvnExtractView> pIterator) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Determine the base commit */
            RevCommit myLastCommit = pBaseCommit;

            /* Access a date formatter */
            final TethysDateFormatter myFormatter = new TethysDateFormatter();

            /* Iterate through the elements */
            while (pIterator.hasNext()) {
                final SvnExtractView myView = pIterator.next();

                /* Report plan step */
                final String myFormat = myFormatter.formatJavaDate(myView.getDate());
                pReport.setNextStep(myFormat);

                /* Extract the details to the directory, preserving the GitDir */
                myView.extractItem(theWorkDir, ThemisGitComponent.NAME_GITDIR);

                /* If there are changes to commit */
                StatusCommand myStatusCmd = theGit.status();
                Status myStatus = myStatusCmd.call();
                if (!myStatus.isClean()) {
                    /* Ensure that all items are staged */
                    final AddCommand myAdd = theGit.add();
                    myAdd.addFilepattern(".");
                    myAdd.call();

                    /* Commit the changes */
                    final CommitCommand myCommit = theGit.commit();
                    myCommit.setAll(true);
                    myCommit.setCommitter(new PersonIdent(theCommitter, myView.getDate()));
                    myCommit.setMessage(myView.getLogMessage());
                    myLastCommit = myCommit.call();
                }

                /* Check that we have ended up with a clean directory */
                myStatusCmd = theGit.status();
                myStatus = myStatusCmd.call();
                if (!myStatus.isClean()) {
                    throw new ThemisLogicException("Uncommitted changes");
                }

                /* Determine owner */
                Object myOwner = pOwner;
                if (myView instanceof SvnExtractMigratedView) {
                    final SvnExtractMigratedView myMigrate = (SvnExtractMigratedView) myView;
                    myOwner = myMigrate.getOwner();
                }

                /* Store details of the commit */
                final SvnExtractAnchor myAnchor = new SvnExtractAnchor(myOwner, myView.getRevision());
                theCommitMap.addAnchor(myAnchor, myLastCommit);
            }

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
            myCheckout.setForce(true);
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
     * Revision Commit entry.
     */
    private static final class SvnRevisionCommit {
        /**
         * The revision.
         */
        private final long theRevision;

        /**
         * The commit.
         */
        private final RevCommit theCommit;

        /**
         * Constructor.
         * @param pRevision the revision
         * @param pCommit the commit
         */
        SvnRevisionCommit(final long pRevision,
                          final RevCommit pCommit) {
            theRevision = pRevision;
            theCommit = pCommit;
        }

        /**
         * Obtain the revision.
         * @return the revision
         */
        long getRevision() {
            return theRevision;
        }

        /**
         * Obtain the commit.
         * @return the commit
         */
        RevCommit getCommit() {
            return theCommit;
        }
    }

    /**
     * Revision Commit List.
     */
    private static final class SvnRevisionCommitList
            extends ArrayList<SvnRevisionCommit> {
        /**
         * SerialId.
         */
        private static final long serialVersionUID = -401436905814909199L;

        /**
         * Obtain the commit for the revision.
         * @param pRevision the revision
         * @return the commit
         * @throws OceanusException on error
         */
        RevCommit getCommit(final long pRevision) throws OceanusException {
            /* Note commit */
            RevCommit myCommit = null;

            /* Loop through the revisions */
            final Iterator<SvnRevisionCommit> myIterator = iterator();
            while (myIterator.hasNext()) {
                final SvnRevisionCommit myRevision = myIterator.next();

                /* If this revision is past the required revision */
                if (myRevision.getRevision() > pRevision) {
                    break;
                }

                /* Note the commit */
                myCommit = myRevision.getCommit();
            }
            /* If there is no relevant revision */
            if (myCommit == null) {
                throw new ThemisLogicException("Missing anchor" + pRevision);
            }

            /* Return the commit */
            return myCommit;
        }
    }

    /**
     * Revision Commit Map.
     */
    private static final class SvnRevisionCommitMap
            extends HashMap<Object, SvnRevisionCommitList> {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 6482318288739116836L;

        /**
         * Add mapping.
         * @param pAnchor the anchor
         * @param pCommit the commit
         */
        void addAnchor(final SvnExtractAnchor pAnchor,
                       final RevCommit pCommit) {
            /* Access the list */
            final Object myOwner = pAnchor.getOwner();
            SvnRevisionCommitList myList = get(myOwner);
            if (myList == null) {
                /* Create the list and store it */
                myList = new SvnRevisionCommitList();
                put(myOwner, myList);
            }

            /* Add item */
            myList.add(new SvnRevisionCommit(pAnchor.getRevision().getNumber(), pCommit));
        }

        /**
         * Obtain the commit for the anchor.
         * @param pAnchor the anchor
         * @return the commit
         * @throws OceanusException on error
         */
        RevCommit getCommit(final SvnExtractAnchor pAnchor) throws OceanusException {
            /* Access the list */
            final SvnRevisionCommitList myList = get(pAnchor.getOwner());
            if (myList != null) {
                /* Access the commit */
                return myList.getCommit(pAnchor.getRevision().getNumber());
            }

            /* Not yet extracted */
            return null;
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
            return pBlocked
                            ? pExtracted
                                         ? OBSCURED
                                         : BLOCKED
                            : pExtracted
                                         ? COMPLETED
                                         : FINISHED;
        }
    }
}
