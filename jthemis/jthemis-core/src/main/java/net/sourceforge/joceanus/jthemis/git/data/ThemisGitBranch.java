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
package net.sourceforge.joceanus.jthemis.git.data;

import java.io.IOException;
import java.util.ArrayList;
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
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitTag.ThemisGitTagList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class ThemisGitBranch
        extends ThemisScmBranch {
    /**
     * Master branch.
     */
    public static final String BRN_MASTER = "master";

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
    }

    /**
     * Parent Repository.
     */
    private final ThemisGitRepository theRepository;

    /**
     * Object Id of the commit.
     */
    private final ThemisGitCommitId theCommitId;

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

        /* Create tag list */
        final ThemisGitTagList myTags = new ThemisGitTagList(this);
        setTags(myTags);

        /* Set as virtual */
        theCommitId = null;
        setVirtual();
    }

    @Override
    public MetisFieldSet<ThemisGitBranch> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Get the repository for this branch.
     * @return the repository
     */
    public ThemisGitRepository getRepository() {
        return theRepository;
    }

    /**
     * Get the commit id.
     * @return the commit id
     */
    public ThemisGitCommitId getCommitId() {
        return theCommitId;
    }

    @Override
    public boolean isRemote() {
        return isRemote;
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
     * List of branches.
     */
    public static final class ThemisGitBranchList
            extends ThemisScmBranchList {
        /**
         * Branch references Prefix.
         */
        private static final String REF_BRANCHES = "refs/heads/";

        /**
         * Remote branch references Prefix.
         */
        private static final String REF_REMOTE_BRANCHES = "refs/remotes/origin/";

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
            final Git myGit = new Git(theComponent.getGitRepo());

            /* Discover branches */
            discoverBranches(myGit);

            /* Discover virtual branches */
            pReport.checkForCancellation();
            discoverVirtualBranches(myGit);

            /* Discover tags for the branches */
            pReport.checkForCancellation();
            discoverTags(pReport);
        }

        /**
         * Discover branch list from repository.
         * @param pGit git instance
         * @throws OceanusException on error
         */
        private void discoverBranches(final Git pGit) throws OceanusException {
            /* Protect against exceptions */
            try (RevWalk myRevWalk = new RevWalk(theComponent.getGitRepo())) {
                /* Access list of branches */
                final ListBranchCommand myCommand = pGit.branchList();
                myCommand.setListMode(ListMode.ALL);
                final List<Ref> myBranches = myCommand.call();
                final List<String> myFound = new ArrayList<>();

                /* Loop through the branches */
                final Iterator<Ref> myIterator = myBranches.iterator();
                while (myIterator.hasNext()) {
                    final Ref myRef = myIterator.next();

                    /* Access branch details */
                    String myName = myRef.getName();
                    boolean isRemote = false;
                    final ObjectId myObjectId = myRef.getObjectId();
                    final RevCommit myCommit = myRevWalk.parseCommit(myObjectId);
                    final ThemisGitCommitId myCommitId = new ThemisGitCommitId(myCommit);
                    if (myName.startsWith(REF_BRANCHES)) {
                        myName = myName.substring(REF_BRANCHES.length());
                    }
                    if (myName.startsWith(REF_REMOTE_BRANCHES)) {
                        myName = myName.substring(REF_REMOTE_BRANCHES.length());
                        isRemote = true;
                    }

                    /* Check for branch that is both local and remote */
                    if (myFound.contains(myName)) {
                        /* Skip the second reference (which we assume is the remote reference) */
                        if (!isRemote) {
                            System.out.println("Help");
                        }
                        continue;
                    }

                    /* If this is the master branch */
                    if (BRN_MASTER.equals(myName)) {
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
                                add(myBranch);
                                myBranch.setTrunk();

                                /* Register the branch */
                                myBranch.setProjectDefinition(myProject);
                                theComponent.getRepository().registerBranch(myProject.getDefinition(), myBranch);
                                myFound.add(myName);
                                myBranch.setRemote(isRemote);
                            }
                        }

                        /* If this looks like a valid branch */
                    } else if (myName.startsWith(BRANCH_PREFIX)) {
                        /* Strip prefix */
                        myName = myName.substring(BRANCH_PREFIX.length());

                        /* Create the new branch and add it */
                        final ThemisGitBranch myBranch = new ThemisGitBranch(theComponent, myName, myCommitId);
                        add(myBranch);
                        myFound.add(myName);
                        myBranch.setRemote(isRemote);
                    }
                }
            } catch (IOException
                    | GitAPIException e) {
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
                            add(myBranch);

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
