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
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmTag;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Represents a tag of a branch.
 */
public final class ThemisGitTag
        extends ThemisScmTag<ThemisGitTag, ThemisGitBranch, ThemisGitComponent, ThemisGitRepository> {
    /**
     * Tag References Prefix.
     */
    static final String REF_TAGS = "refs/tags/";

    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(ThemisGitTag.class, ThemisScmTag.getBaseFieldSet());

    /**
     * Repository field id.
     */
    private static final MetisDataField FIELD_REPO = FIELD_DEFS.declareLocalField(ThemisResource.SCM_REPOSITORY);

    /**
     * Component field id.
     */
    private static final MetisDataField FIELD_COMP = FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT);

    /**
     * Parent Repository.
     */
    private final ThemisGitRepository theRepository;

    /**
     * Parent Component.
     */
    private final ThemisGitComponent theComponent;

    /**
     * Object Id of the commit.
     */
    private final ObjectId theCommitId;

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     * @param pCommitId the commitId
     */
    protected ThemisGitTag(final ThemisGitBranch pParent,
                           final int pTag,
                           final ObjectId pCommitId) {
        /* Call super constructor */
        super(pParent, pTag);

        /* Store values */
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
        theCommitId = pCommitId;
    }

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     */
    ThemisGitTag(final ThemisGitBranch pParent,
                 final int pTag) {
        /* Call super constructor */
        super(pParent, pTag);

        /* Store values */
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
        theCommitId = null;
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
        if (FIELD_COMP.equals(pField)) {
            return theComponent;
        }

        /* pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Get the commit id.
     * @return the commit id
     */
    public ObjectId getCommitId() {
        return theCommitId;
    }

    /**
     * Get the repository for this tag.
     * @return the repository
     */
    public ThemisGitRepository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this tag.
     * @return the component
     */
    public ThemisGitComponent getComponent() {
        return theComponent;
    }

    /**
     * List of tags.
     */
    public static class GitTagList
            extends ScmTagList<ThemisGitTag, ThemisGitBranch, ThemisGitComponent, ThemisGitRepository> {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(GitTagList.class, ScmTagList.getBaseFieldSet());

        /**
         * Parent Component.
         */
        private final ThemisGitComponent theComponent;

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected GitTagList(final ThemisGitBranch pParent) {
            /* Call super constructor */
            super(pParent);

            /* Store parent for use by entry handler */
            theComponent = (pParent == null)
                                             ? null
                                             : pParent.getComponent();
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected ThemisGitTag createNewTag(final ThemisGitBranch pBranch,
                                            final int pTag) {
            return new ThemisGitTag(pBranch, pTag);
        }

        /**
         * Discover tag list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Access a Git instance */
            final Git myGit = new Git(theComponent.getGitRepo());

            /* Discover tags */
            discoverTags(myGit);

            /* Analyse tags */
            analyseTags(pReport);
        }

        /**
         * Discover tag list from repository.
         * @param pGit git instance
         * @throws OceanusException on error
         */
        private void discoverTags(final Git pGit) throws OceanusException {
            /* Protect against exceptions */
            try {
                /* Access list of branches */
                final ListTagCommand myCommand = pGit.tagList();
                final List<Ref> myTags = myCommand.call();
                final String myBranch = getBranch().getBranchName() + ThemisGitTag.PREFIX_TAG;

                /* Loop through the tags */
                final Iterator<Ref> myIterator = myTags.iterator();
                while (myIterator.hasNext()) {
                    final Ref myRef = myIterator.next();

                    /* Access tag details */
                    String myName = myRef.getName();
                    final ObjectId myCommitId = myRef.getObjectId();
                    if (myName.startsWith(REF_TAGS)) {
                        myName = myName.substring(REF_TAGS.length());
                    }

                    /* If this looks like a valid branch */
                    if (myName.startsWith(myBranch)) {
                        /* Strip prefix */
                        myName = myName.substring(myBranch.length());

                        /* Determine tag */
                        final int myTagNo = Integer.parseInt(myName);

                        /* Create the new tag and add it */
                        final ThemisGitTag myTag = new ThemisGitTag(getBranch(), myTagNo, myCommitId);
                        add(myTag);
                    }
                }

                /* Sort the list */
                getUnderlyingList().sort(null);

            } catch (GitAPIException e) {
                throw new ThemisIOException("Failed to list tags", e);
            }
        }

        /**
         * Analyses tags.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        private void analyseTags(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Access repository */
            final ThemisGitRepository myRepo = theComponent.getRepository();

            /* Loop through the entries */
            final Iterator<ThemisGitTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next tag */
                final ThemisGitTag myTag = myIterator.next();

                /* Report stage */
                pReport.setNewStage("Analysing tag " + myTag.getTagName());

                /* Parse project file */
                final ThemisMvnProjectDefinition myProject = theComponent.parseProjectObject(myTag.getCommitId(), "");
                myTag.setProjectDefinition(myProject);

                /* Register the tag */
                if (myProject != null) {
                    myRepo.registerTag(myProject.getDefinition(), myTag);
                }
            }
        }
    }
}
