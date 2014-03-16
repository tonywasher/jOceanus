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
package net.sourceforge.joceanus.jthemis.git.data;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmTag;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListTagCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;

/**
 * Represents a tag of a branch.
 */
public final class GitTag
        extends ScmTag<GitTag, GitBranch, GitComponent, GitRepository> {
    /**
     * Tag References Prefix.
     */
    protected static final String REF_TAGS = "refs/tags/";

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(GitTag.class.getSimpleName(), ScmTag.FIELD_DEFS);

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Component field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
     * Parent Repository.
     */
    private final GitRepository theRepository;

    /**
     * Parent Component.
     */
    private final GitComponent theComponent;

    /**
     * Object Id of the commit.
     */
    private final ObjectId theCommitId;

    /**
     * Get the commit id.
     * @return the commit id
     */
    public ObjectId getCommitId() {
        return theCommitId;
    }

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     * @param pCommitId the commitId
     */
    protected GitTag(final GitBranch pParent,
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
    private GitTag(final GitBranch pParent,
                   final int pTag) {
        /* Call super constructor */
        super(pParent, pTag);

        /* Store values */
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
        theCommitId = null;
    }

    /**
     * Get the repository for this tag.
     * @return the repository
     */
    public GitRepository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this tag.
     * @return the component
     */
    public GitComponent getComponent() {
        return theComponent;
    }

    /**
     * List of tags.
     */
    public static class GitTagList
            extends ScmTagList<GitTag, GitBranch, GitComponent, GitRepository>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(GitTagList.class.getSimpleName(), ScmTagList.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Parent Component.
         */
        private final GitComponent theComponent;

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected GitTagList(final GitBranch pParent) {
            /* Call super constructor */
            super(GitTag.class, pParent);

            /* Store parent for use by entry handler */
            theComponent = (pParent == null)
                                            ? null
                                            : pParent.getComponent();
        }

        @Override
        protected GitTag createNewTag(final GitBranch pBranch,
                                      final int pTag) {
            return new GitTag(pBranch, pTag);
        }

        /**
         * Discover tag list from repository.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        public void discover(final ReportStatus pReport) throws JOceanusException {
            /* Reset the list */
            clear();

            /* Access a Git instance */
            Git myGit = new Git(theComponent.getGitRepo());

            /* Discover tags */
            discoverTags(myGit);

            /* Analyse tags */
            analyseTags(pReport);
        }

        /**
         * Discover tag list from repository.
         * @param pGit git instance
         * @throws JOceanusException on error
         */
        private void discoverTags(final Git pGit) throws JOceanusException {
            /* Protect against exceptions */
            try {
                /* Access list of branches */
                ListTagCommand myCommand = pGit.tagList();
                List<Ref> myTags = myCommand.call();
                String myBranch = getBranch().getBranchName() + GitTag.PREFIX_TAG;

                /* Loop through the tags */
                Iterator<Ref> myIterator = myTags.iterator();
                while (myIterator.hasNext()) {
                    Ref myRef = myIterator.next();

                    /* Access tag details */
                    String myName = myRef.getName();
                    ObjectId myCommitId = myRef.getObjectId();
                    if (myName.startsWith(REF_TAGS)) {
                        myName = myName.substring(REF_TAGS.length());
                    }

                    /* If this looks like a valid branch */
                    if (myName.startsWith(myBranch)) {
                        /* Strip prefix */
                        myName = myName.substring(myBranch.length());

                        /* Determine tag */
                        int myTagNo = Integer.parseInt(myName);

                        /* Create the new tag and add it */
                        GitTag myTag = new GitTag(getBranch(), myTagNo, myCommitId);
                        add(myTag);
                    }
                }
            } catch (GitAPIException e) {
                throw new JThemisIOException("Failed to list tags", e);
            }
        }

        /**
         * Analyses tags.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        private void analyseTags(final ReportStatus pReport) throws JOceanusException {
            /* Access repository */
            GitRepository myRepo = theComponent.getRepository();

            /* Loop through the entries */
            Iterator<GitTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next tag */
                GitTag myTag = myIterator.next();

                /* Report stage */
                pReport.setNewStage("Analysing tag " + myTag.getTagName());

                /* Parse project file */
                MvnProjectDefinition myProject = theComponent.parseProjectObject(myTag.getCommitId(), "");
                myTag.setProjectDefinition(myProject);

                /* Register the tag */
                if (myProject != null) {
                    myRepo.registerTag(myProject.getDefinition(), myTag);
                }
            }
        }
    }
}
