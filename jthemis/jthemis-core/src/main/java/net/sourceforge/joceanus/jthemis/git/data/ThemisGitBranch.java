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
package net.sourceforge.joceanus.jthemis.git.data;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.git.data.ThemisGitTag.GitTagList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class ThemisGitBranch
        extends ThemisScmBranch<ThemisGitBranch, ThemisGitComponent, ThemisGitRepository> {
    /**
     * Master branch.
     */
    public static final String BRN_MASTER = "master";

    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(ThemisGitBranch.class, ThemisScmBranch.getBaseFieldSet());

    /**
     * Repository field id.
     */
    private static final MetisDataField FIELD_REPO = FIELD_DEFS.declareLocalField(ThemisResource.SCM_REPOSITORY);

    /**
     * Parent Repository.
     */
    private final ThemisGitRepository theRepository;

    /**
     * Object Id of the commit.
     */
    private final ObjectId theCommitId;

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     * @param pCommitId the commitId
     */
    protected ThemisGitBranch(final ThemisGitComponent pParent,
                              final String pVersion,
                              final ObjectId pCommitId) {
        /* Call super constructor */
        super(pParent, pVersion);

        /* Store values */
        theRepository = pParent.getRepository();
        theCommitId = pCommitId;

        /* Create tag list */
        final GitTagList myTags = new GitTagList(this);
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
        final GitTagList myTags = new GitTagList(this);
        setTags(myTags);

        /* Set as virtual */
        theCommitId = null;
        setVirtual();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_REPO.equals(pField)) {
            return theRepository;
        }

        /* Unknown */
        return super.getFieldValue(pField);
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
    public ObjectId getCommitId() {
        return theCommitId;
    }

    @Override
    public GitTagList getTagList() {
        return (GitTagList) super.getTagList();
    }

    @Override
    public ThemisGitTag nextTag() {
        /* Determine the next tag */
        return (ThemisGitTag) super.nextTag();
    }

    /**
     * List of branches.
     */
    public static final class GitBranchList
            extends ScmBranchList<ThemisGitBranch, ThemisGitComponent, ThemisGitRepository> {
        /**
         * Branch references Prefix.
         */
        private static final String REF_BRANCHES = "refs/heads/";

        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(GitBranchList.class, ScmBranchList.getBaseFieldSet());

        /**
         * The parent component.
         */
        private final ThemisGitComponent theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected GitBranchList(final ThemisGitComponent pParent) {
            /* Call super constructor */
            super(pParent);

            /* Store parent for use by entry handler */
            theComponent = pParent;
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected ThemisGitBranch createNewBranch(final ThemisGitComponent pComponent,
                                                  final int pMajor,
                                                  final int pMinor,
                                                  final int pDelta) {
            return new ThemisGitBranch(pComponent, pMajor, pMinor, pDelta);
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
            try {
                /* Access list of branches */
                final ListBranchCommand myCommand = pGit.branchList();
                myCommand.setListMode(ListMode.ALL);
                final List<Ref> myBranches = myCommand.call();

                /* Loop through the branches */
                final Iterator<Ref> myIterator = myBranches.iterator();
                while (myIterator.hasNext()) {
                    final Ref myRef = myIterator.next();

                    /* Access branch details */
                    String myName = myRef.getName();
                    final ObjectId myCommitId = myRef.getObjectId();
                    if (myName.startsWith(REF_BRANCHES)) {
                        myName = myName.substring(REF_BRANCHES.length());
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
                            }
                        }

                        /* If this looks like a valid branch */
                    } else if (myName.startsWith(BRANCH_PREFIX)) {
                        /* Strip prefix */
                        myName = myName.substring(BRANCH_PREFIX.length());

                        /* Create the new branch and add it */
                        final ThemisGitBranch myBranch = new ThemisGitBranch(theComponent, myName, myCommitId);
                        add(myBranch);
                    }
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
                pReport.setNewStage("Analysing branch " + myTrunk.getBranchName());

                /* Parse project file */
                final ThemisMvnProjectDefinition myProject = theComponent.parseProjectObject(myTrunk.getCommitId(), "");
                myTrunk.setProjectDefinition(myProject);

                /* Register the branch */
                if (myProject != null) {
                    myRepo.registerBranch(myProject.getDefinition(), myTrunk);
                }

                /* Discover tags */
                myTrunk.getTagList().discover(pReport);
            }

            /* Loop through the entries */
            final Iterator<ThemisGitBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                final ThemisGitBranch myBranch = myIterator.next();

                /* Skip trunk branch */
                if (myBranch.isTrunk()) {
                    continue;
                }

                /* Report stage */
                pReport.setNewStage("Analysing branch " + myBranch.getBranchName());

                /* If this is a real branch */
                if (!myBranch.isVirtual()) {
                    /* Parse project file */
                    final ThemisMvnProjectDefinition myProject = theComponent.parseProjectObject(myBranch.getCommitId(), "");
                    myBranch.setProjectDefinition(myProject);

                    /* Register the branch */
                    if (myProject != null) {
                        myRepo.registerBranch(myProject.getDefinition(), myBranch);
                    }
                }

                /* Discover tags */
                myBranch.getTagList().discover(pReport);
            }
        }

        /**
         * Locate Tag.
         * @param pVersion the version to locate
         * @param pTag the tag to locate
         * @return the relevant tag or Null
         */
        @Override
        protected ThemisGitTag locateTag(final String pVersion,
                                         final int pTag) {
            /* Access list iterator */
            return (ThemisGitTag) super.locateTag(pVersion, pTag);
        }
    }
}
