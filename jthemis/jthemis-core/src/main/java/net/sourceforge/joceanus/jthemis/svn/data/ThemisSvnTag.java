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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.Iterator;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmTag;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistoryMap.ThemisSvnRevisionPath;

/**
 * Represents a tag of a branch.
 */
public final class ThemisSvnTag
        extends ThemisScmTag {
    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSvnTag> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnTag.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_REPOSITORY, ThemisSvnTag::getRepository);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT, ThemisSvnTag::getComponent);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISIONPATH, ThemisSvnTag::getRevisionPath);
    }

    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(ThemisSvnTag.class);

    /**
     * Parent Repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * Parent Component.
     */
    private final ThemisSvnComponent theComponent;

    /**
     * RevisionPath.
     */
    private ThemisSvnRevisionPath theRevisionPath;

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     */
    protected ThemisSvnTag(final ThemisSvnBranch pParent,
                           final int pTag) {
        /* Call super constructor */
        super(pParent, pTag);

        /* Store values */
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
    }

    @Override
    public MetisFieldSet<ThemisSvnTag> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Get the repository for this tag.
     * @return the repository
     */
    public ThemisSvnRepository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this tag.
     * @return the component
     */
    public ThemisSvnComponent getComponent() {
        return theComponent;
    }

    /**
     * Get the revision path for this branch.
     * @return the revision path
     */
    public ThemisSvnRevisionPath getRevisionPath() {
        return theRevisionPath;
    }

    /**
     * Obtain repository path for the tag.
     * @return the Repository path for this tag
     */
    public String getURLPath() {
        /* Allocate a builder */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Add the tags directory */
        myBuilder.append(theComponent.getTagsPath());
        myBuilder.append(ThemisSvnRepository.SEP_URL);

        /* Build the tag directory */
        myBuilder.append(getName());

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain URL.
     * @return the URL
     */
    public SVNURL getURL() {
        /* Build the URL */
        try {
            return SVNURL.parseURIEncoded(getURLPath());
        } catch (SVNException e) {
            LOGGER.error("Parse Failure", e);
            return null;
        }
    }

    /**
     * Discover HistoryPath.
     * @throws OceanusException on error
     */
    private void discoverHistory() throws OceanusException {
        /* Access history map */
        final ThemisSvnRevisionHistoryMap myHistMap = theRepository.getHistoryMap();

        /* Determine the next major branch */
        theRevisionPath = myHistMap.discoverTag(this);
    }

    @Override
    public ThemisSvnBranch getBranch() {
        return (ThemisSvnBranch) super.getBranch();
    }

    /**
     * List of tags.
     */
    public static class ThemisSvnTagList
            extends ThemisScmTagList {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisSvnTagList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnTagList.class);

        /**
         * Parent Component.
         */
        private final ThemisSvnComponent theComponent;

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected ThemisSvnTagList(final ThemisSvnBranch pParent) {
            /* Call super constructor */
            super(pParent);

            /* Store parent for use by entry handler */
            theComponent = (pParent == null)
                                             ? null
                                             : pParent.getComponent();
        }

        @Override
        public MetisFieldSet<ThemisSvnTagList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public ThemisSvnBranch getBranch() {
            return (ThemisSvnBranch) super.getBranch();
        }

        @Override
        protected ThemisSvnTag createNewTag(final ThemisScmBranch pBranch,
                                            final int pTag) {
            return new ThemisSvnTag((ThemisSvnBranch) pBranch, pTag);
        }

        /**
         * Discover tag list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Obtain the active profile */
            final MetisProfile myBaseTask = pReport.getActiveTask();
            MetisProfile myTask = myBaseTask.startTask("discoverTags");

            /* Access a LogClient */
            final ThemisSvnRepository myRepo = theComponent.getRepository();
            final SVNClientManager myMgr = myRepo.getClientManager();
            final SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the tags directory URL */
                final SVNURL myURL = SVNURL.parseURIEncoded(theComponent.getTagsPath());

                /* List the tag directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler());
            } catch (SVNException e) {
                throw new ThemisIOException("Failed to discover tags for " + getBranch().getName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* Loop through the tags */
            final Iterator<ThemisScmTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnTag myTag = (ThemisSvnTag) myIterator.next();

                /* Start the discoverTag task */
                myTask = myBaseTask.startTask("discoverTag:" + myTag.getName());

                /* Report stage */
                pReport.setNewStage("Analysing tag " + myTag.getName());

                /* Start parse task */
                myTask.startTask("parseProject");

                /* Parse project file */
                final ThemisMvnProjectDefinition myProject = theComponent.parseProjectURL(myTag.getURLPath());
                myTag.setProjectDefinition(myProject);

                /* Register the tag */
                if (myProject != null) {
                    myRepo.registerTag(myProject.getDefinition(), myTag);
                }

                /* Discover History */
                final MetisProfile mySubTask = myTask.startTask("discoverHistory");

                /* Analyse history map */
                myTag.discoverHistory();

                /* End the subTask */
                mySubTask.end();
            }

            /* Complete the task */
            myTask.end();
        }

        /**
         * The Directory Entry Handler.
         */
        private final class ListDirHandler
                implements ISVNDirEntryHandler {

            @Override
            public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
                /* Ignore if not a directory and if it is top-level */
                if (!SVNNodeKind.DIR.equals(pEntry.getKind())) {
                    return;
                }
                if (pEntry.getRelativePath().length() == 0) {
                    return;
                }

                /* Access the name and ignore if it does not start with correct prefix */
                String myName = pEntry.getName();
                if (!myName.startsWith(getPrefix())) {
                    return;
                }
                myName = myName.substring(getPrefix().length());

                /* Determine tag and last revision */
                final int myTagNo = Integer.parseInt(myName);

                /* Create the tag and add to the list */
                final ThemisSvnTag myTag = new ThemisSvnTag(getBranch(), myTagNo);
                add(myTag);
            }
        }
    }
}
