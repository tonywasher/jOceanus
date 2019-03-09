/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitCommitId;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitRevisionHistory.ThemisGitRevision;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitTag.ThemisGitTagList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class ThemisGitBranch
        extends ThemisScmBranch
        implements ThemisGitOwner {
    /**
     * Master branch.
     */
    public static final String BRN_MASTER = "master";

    /**
     * Origin remote.
     */
    public static final String REMOTE_ORIGIN = "origin";

    /**
     * Local Branch Prefix.
     */
    private static final String REF_BRANCHES = "refs/heads/";

    /**
     * Remote branch Prefix.
     */
    private static final String REF_REMOTE_BRANCHES = "refs/remotes/";

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisGitBranch> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitBranch.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_REPOSITORY, ThemisGitBranch::getRepository);
        FIELD_DEFS.declareLocalField(ThemisResource.GIT_COMMITID, ThemisGitBranch::getCommitId);
        FIELD_DEFS.declareLocalField(ThemisResource.GIT_REMOTE, ThemisGitBranch::isRemote);
        FIELD_DEFS.declareLocalField(ThemisResource.GIT_REVISION, ThemisGitBranch::getRevision);
    }

    /**
     * The local fields.
     */
    private final MetisFieldSet<ThemisGitBranch> theLocalFields;

    /**
     * Parent Repository.
     */
    private final ThemisGitRepository theRepository;

    /**
     * Object Id of the commit.
     */
    private final ThemisGitCommitId theCommitId;

    /**
     * Revision details of the commit.
     */
    private ThemisGitRevision theRevision;

    /**
     * Is this a remote branch?
     */
    private boolean isRemote;

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     * @param pCommitId the commitId
     */
    protected ThemisGitBranch(final ThemisGitComponent pParent,
                              final String pVersion,
                              final ThemisGitCommitId pCommitId) {
        /* Call super constructor */
        super(pParent, pVersion);

        /* Store values */
        theRepository = pParent.getRepository();
        theCommitId = pCommitId;

        /* Allocate the local fields */
        theLocalFields = MetisFieldSet.newFieldSet(this);

        /* Create tag list */
        final ThemisGitTagList myTags = new ThemisGitTagList(this);
        setTags(myTags);
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pDelta the delta version
     */
    ThemisGitBranch(final ThemisGitComponent pParent,
                    final int pMajor,
                    final int pMinor,
                    final int pDelta) {
        /* Call super constructor */
        super(pParent, pMajor, pMinor, pDelta);

        /* Store values */
        theRepository = pParent.getRepository();

        /* Allocate the local fields */
        theLocalFields = MetisFieldSet.newFieldSet(this);

        /* Create tag list */
        final ThemisGitTagList myTags = new ThemisGitTagList(this);
        setTags(myTags);

        /* Set as virtual */
        theCommitId = null;
        setVirtual();
    }

    @Override
    public MetisFieldSet<ThemisGitBranch> getDataFieldSet() {
        return theLocalFields;
    }

    /**
     * Get the repository for this branch.
     * @return the repository
     */
    public ThemisGitRepository getRepository() {
        return theRepository;
    }

    @Override
    public ThemisGitCommitId getCommitId() {
        return theCommitId;
    }

    @Override
    public boolean isRemote() {
        return isRemote;
    }

    @Override
    public ThemisGitRevision getRevision() {
        return theRevision;
    }

    @Override
    public void setRevision(final ThemisGitRevision pRevision) {
        theRevision = pRevision;
    }

    /**
     * Set the remote flag.
     * @param pRemote true/false
     */
    public void setRemote(final boolean pRemote) {
        isRemote = pRemote;
    }

    @Override
    public ThemisGitTagList getTagList() {
        return (ThemisGitTagList) super.getTagList();
    }

    @Override
    public ThemisGitTag nextTag() {
        /* Determine the next tag */
        return (ThemisGitTag) super.nextTag();
    }

    @Override
    public ThemisGitComponent getComponent() {
        return (ThemisGitComponent) super.getComponent();
    }

    /**
     * Is this the master branch?
     * @param pName the branch name
     * @return true/false
     */
    public static boolean isMaster(final String pName) {
        return BRN_MASTER.equals(pName);
    }

    /**
     * Obtain local branch name (if it is local).
     * @param pRef the reference
     * @return the local branch name (or null if not a local branch)
     */
    public static String getLocalBranchName(final Ref pRef) {
        final String myName = pRef.getName();
        return myName.startsWith(REF_BRANCHES)
                                               ? myName.substring(REF_BRANCHES.length())
                                               : null;
    }

    /**
     * Obtain remote branch name (if it is remote).
     * @param pRef the reference
     * @param pRemote the remote name
     * @return the remote branch name (or null if not a remote branch)
     */
    public static String getRemoteBranchName(final Ref pRef,
                                             final String pRemote) {
        /* Determine the remote prefix */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(REF_REMOTE_BRANCHES)
                .append(pRemote)
                .append("/");
        final String myPrefix = myBuilder.toString();

        /* Check name */
        final String myName = pRef.getName();
        return myName.startsWith(myPrefix)
                                           ? myName.substring(myPrefix.length())
                                           : null;
    }

    /**
     * Declare a tag.
     * @param pTag the tag
     */
    void declareTag(final ThemisGitTag pTag) {
        /* Determine the name of the field */
        final ThemisGitTagList myList = getTagList();
        String myName = ThemisResource.SCM_TAG.getValue();
        if (!myList.isEmpty()) {
            myName += (myList.size() + 1);
        }

        /* Declare the field and add the tag */
        theLocalFields.declareLocalField(myName, f -> pTag);
        myList.add(pTag);
    }

    /**
     * List of branches.
     */
    public static final class ThemisGitBranchList
            extends ThemisScmBranchList {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisGitBranchList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisGitBranchList.class);

        /**
         * The parent component.
         */
        private final ThemisGitComponent theComponent;

        /**
         * The revision history.
         */
        private final ThemisGitRevisionHistory theHistory;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected ThemisGitBranchList(final ThemisGitComponent pParent) {
            /* Call super constructor */
            super(pParent);

            /* Store parent for use by entry handler */
            theComponent = pParent;
            theHistory = theComponent.getRevisionHistory();
        }

        @Override
        public MetisFieldSet<ThemisGitBranchList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected ThemisGitBranch createNewBranch(final ThemisScmComponent pComponent,
                                                  final int pMajor,
                                                  final int pMinor,
                                                  final int pDelta) {
            return new ThemisGitBranch((ThemisGitComponent) pComponent, pMajor, pMinor, pDelta);
        }

        /**
         * Discover branch list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Access a Git instance */
            try (Git myGit = new Git(theComponent.getGitRepo())) {
                /* Discover branches */
                discoverBranches(myGit);

                /* Discover remote branches */
                pReport.checkForCancellation();
                discoverRemoteBranches(myGit);

                /* Discover virtual branches */
                pReport.checkForCancellation();
                discoverVirtualBranches(myGit);

                /* Discover tags for the branches */
                pReport.checkForCancellation();
                discoverTags(pReport);

                /* Process the history */
                theComponent.processHistory();
            }
        }

        /**
         * Discover branch list from repository.
         * @param pGit gitInstance
         * @throws OceanusException on error
         */
        private void discoverBranches(final Git pGit) throws OceanusException {
            /* Protect against exceptions */
            try (RevWalk myRevWalk = new RevWalk(theComponent.getGitRepo())) {
                /* Access list of branches */
                final ListBranchCommand myCommand = pGit.branchList();
                final List<Ref> myBranches = myCommand.call();

                /* Loop through the branches */
                final Iterator<Ref> myIterator = myBranches.iterator();
                while (myIterator.hasNext()) {
                    final Ref myRef = myIterator.next();

                    /* Convert to local branch name */
                    final String myName = getLocalBranchName(myRef);
                    if (myName == null) {
                        continue;
                    }

                    /* Process the branch */
                    processBranch(myName, myRef, false, myRevWalk);
                }

            } catch (GitAPIException e) {
                throw new ThemisIOException("Failed to list branches", e);
            }
        }

        /**
         * Process branch.
         * @param pName the name of the branch
         * @param pRef the reference
         * @param pRemote is this a remote branch
         * @param pRevWalk the revWalk instance
         * @throws OceanusException on error
         */
        private void processBranch(final String pName,
                                   final Ref pRef,
                                   final boolean pRemote,
                                   final RevWalk pRevWalk) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Access local copy of name */
                String myName = pName;

                /* Access branch details */
                final ObjectId myObjectId = pRef.getObjectId();
                final RevCommit myCommit = pRevWalk.parseCommit(myObjectId);
                final ThemisGitCommitId myCommitId = new ThemisGitCommitId(myCommit);
                final boolean isMaster = isMaster(myName);

                /* If this is a remote branch */
                if (pRemote) {
                    /* Check for existing branch */
                    final boolean isExisting = isMaster
                                                        ? locateTrunk() != null
                                                        : locateBranch(myName) != null;

                    /* No need to process if the branch already exists */
                    if (isExisting) {
                        return;
                    }
                }

                /* If this is the master branch */
                if (isMaster) {
                    /* Parse project file */
                    final ThemisMvnProjectDefinition myProject = theComponent.parseProjectObject(myCommitId, "");

                    /* If we have a project definition */
                    if (myProject != null) {
                        /* Access the version */
                        final String myVers = myProject.getDefinition().getVersion();

                        /* If we have a valid prefix */
                        if (myVers.startsWith(BRANCH_PREFIX)) {
                            /* Determine true name of branch */
                            myName = myVers.substring(BRANCH_PREFIX.length());

                            /* Create the new branch and add it */
                            final ThemisGitBranch myBranch = new ThemisGitBranch(theComponent, myName, myCommitId);
                            theComponent.declareBranch(myBranch);
                            myBranch.setTrunk();
                            myBranch.setRemote(pRemote);

                            /* Register the branch */
                            myBranch.setProjectDefinition(myProject);
                            theComponent.getRepository().registerBranch(myProject.getDefinition(), myBranch);
                        }
                    }

                    /* If this looks like a valid branch */
                } else if (myName.startsWith(BRANCH_PREFIX)) {
                    /* Strip prefix */
                    myName = myName.substring(BRANCH_PREFIX.length());

                    /* Create the new branch and add it */
                    final ThemisGitBranch myBranch = new ThemisGitBranch(theComponent, myName, myCommitId);
                    theComponent.declareBranch(myBranch);
                    myBranch.setRemote(pRemote);

                    /* If this looks like a valid branch */
                } else if (!pRemote || !myName.equals("HEAD")) {
                    /* Create the new branch and add it */
                    final ThemisGitBranch myBranch = new ThemisGitBranch(theComponent, myName, myCommitId);
                    theComponent.declareBranch(myBranch);
                    myBranch.setRemote(pRemote);
                }

            } catch (IOException e) {
                throw new ThemisIOException("Failed to process branch", e);
            }
        }

        /**
         * Discover remote branch list from repository.
         * @param pGit git instance
         * @throws OceanusException on error
         */
        private void discoverRemoteBranches(final Git pGit) throws OceanusException {
            /* Protect against exceptions */
            try (RevWalk myRevWalk = new RevWalk(theComponent.getGitRepo())) {
                /* Access list of branches */
                final ListBranchCommand myCommand = pGit.branchList();
                myCommand.setListMode(ListMode.REMOTE);
                final List<Ref> myBranches = myCommand.call();

                /* Loop through the branches */
                final Iterator<Ref> myIterator = myBranches.iterator();
                while (myIterator.hasNext()) {
                    final Ref myRef = myIterator.next();

                    /* Convert to remote branch name */
                    final String myName = getRemoteBranchName(myRef, REMOTE_ORIGIN);
                    if (myName == null) {
                        continue;
                    }

                    /* Process the branch */
                    processBranch(myName, myRef, true, myRevWalk);
                }
            } catch (GitAPIException e) {
                throw new ThemisIOException("Failed to list branches", e);
            }
        }

        /**
         * Discover virtual branch list from repository.
         * @param pGit git instance
         * @throws OceanusException on error
         */
        private void discoverVirtualBranches(final Git pGit) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Access list of tags */
                final ListTagCommand myCommand = pGit.tagList();
                final List<Ref> myTags = myCommand.call();

                /* Determine prefix */
                final String myPrefix = ThemisGitTag.REF_TAGS + BRANCH_PREFIX;

                /* Loop through the tags */
                final Iterator<Ref> myIterator = myTags.iterator();
                while (myIterator.hasNext()) {
                    final Ref myRef = myIterator.next();

                    /* Access tag details */
                    String myName = myRef.getName();

                    /* If this looks like a valid branch */
                    if (myName.startsWith(myPrefix)) {
                        /* Strip prefix */
                        myName = myName.substring(ThemisGitTag.REF_TAGS.length());

                        /* Locate the tag separator */
                        final int iIndex = myName.indexOf(ThemisGitTag.PREFIX_TAG);
                        if (iIndex == -1) {
                            continue;
                        }

                        /* Access branch name */
                        myName = myName.substring(0, iIndex);

                        /* If this is an unknown branch */
                        if (locateBranch(myName) == null) {
                            /* Strip prefix */
                            myName = myName.substring(BRANCH_PREFIX.length());

                            /* Create the new branch and add it */
                            final ThemisGitBranch myBranch = new ThemisGitBranch(theComponent, myName, null);
                            theComponent.declareBranch(myBranch);

                            /* Set virtual flag */
                            myBranch.setVirtual();
                        }
                    }
                }

            } catch (GitAPIException e) {
                throw new ThemisIOException("Failed to list tags", e);
            }
        }

        /**
         * Discover tag lists from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        private void discoverTags(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Access repository */
            final ThemisGitRepository myRepo = theComponent.getRepository();

            /* Access trunk */
            final ThemisGitBranch myTrunk = locateTrunk();
            if (myTrunk != null) {
                /* Report stage */
                pReport.setNewStage("Analysing branch " + myTrunk.getName());

                /* Parse the revision history */
                theHistory.parseCommitHistory(myTrunk, myTrunk.getCommitId());

                /* Discover tags */
                myTrunk.getTagList().discover(pReport, theHistory);
            }

            /* Loop through the entries */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                final ThemisGitBranch myBranch = (ThemisGitBranch) myIterator.next();

                /* Skip trunk branch */
                if (myBranch.isTrunk()) {
                    continue;
                }

                /* Report stage */
                pReport.setNewStage("Analysing branch " + myBranch.getName());

                /* If this is a real branch */
                if (!myBranch.isVirtual()) {
                    /* Parse project file */
                    final ThemisMvnProjectDefinition myProject = theComponent.parseProjectObject(myBranch.getCommitId(), "");
                    myBranch.setProjectDefinition(myProject);

                    /* Register the branch */
                    if (myProject != null) {
                        myRepo.registerBranch(myProject.getDefinition(), myBranch);
                    }

                    /* Parse the revision history */
                    theHistory.parseCommitHistory(myBranch, myBranch.getCommitId());
                }

                /* Discover tags */
                myBranch.getTagList().discover(pReport, theHistory);
            }
        }

        @Override
        protected ThemisGitBranch locateTrunk() {
            return (ThemisGitBranch) super.locateTrunk();
        }

        @Override
        protected ThemisGitTag locateTag(final String pVersion,
                                         final int pTag) {
            return (ThemisGitTag) super.locateTag(pVersion, pTag);
        }
    }
}
