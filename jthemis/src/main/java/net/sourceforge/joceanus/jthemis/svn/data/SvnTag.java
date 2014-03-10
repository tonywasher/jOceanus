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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.Iterator;
import java.util.logging.Level;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmTag;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.data.SvnRevisionHistoryMap.SvnRevisionPath;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Represents a tag of a branch.
 */
public final class SvnTag
        extends ScmTag<SvnTag, SvnBranch, SvnComponent, SvnRepository> {
    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SvnTag.class.getSimpleName(), ScmTag.FIELD_DEFS);

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Component field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    /**
     * RevisionPath.
     */
    private static final JDataField FIELD_REVPATH = FIELD_DEFS.declareLocalField("RevisionPath");

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
        if (FIELD_REVPATH.equals(pField)) {
            return theRevisionPath;
        }

        /* pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Parent Repository.
     */
    private final SvnRepository theRepository;

    /**
     * Parent Component.
     */
    private final SvnComponent theComponent;

    /**
     * RevisionPath.
     */
    private SvnRevisionPath theRevisionPath;

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     */
    protected SvnTag(final SvnBranch pParent,
                     final int pTag) {
        /* Call super constructor */
        super(pParent, pTag);

        /* Store values */
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
    }

    /**
     * Get the repository for this tag.
     * @return the repository
     */
    public SvnRepository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this tag.
     * @return the component
     */
    public SvnComponent getComponent() {
        return theComponent;
    }

    /**
     * Get the revision path for this branch.
     * @return the revision path
     */
    public SvnRevisionPath getRevisionPath() {
        return theRevisionPath;
    }

    /**
     * Obtain repository path for the tag.
     * @return the Repository path for this tag
     */
    public String getURLPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Add the tags directory */
        myBuilder.append(theComponent.getTagsPath());
        myBuilder.append(SvnRepository.SEP_URL);

        /* Build the tag directory */
        myBuilder.append(getTagName());

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
            theRepository.getLogger().log(Level.SEVERE, "Parse Failure", e);
            return null;
        }
    }

    /**
     * Discover HistoryPath.
     * @throws JOceanusException on error
     */
    private void discoverHistory() throws JOceanusException {
        /* Access history map */
        SvnRevisionHistoryMap myHistMap = theRepository.getHistoryMap();

        /* Determine the next major branch */
        theRevisionPath = myHistMap.discoverTag(this);
    }

    /**
     * List of tags.
     */
    public static class SvnTagList
            extends ScmTagList<SvnTag, SvnBranch, SvnComponent, SvnRepository>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnTagList.class.getSimpleName(), ScmTagList.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Parent Component.
         */
        private final SvnComponent theComponent;

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected SvnTagList(final SvnBranch pParent) {
            /* Call super constructor */
            super(SvnTag.class, pParent);

            /* Store parent for use by entry handler */
            theComponent = (pParent == null)
                                            ? null
                                            : pParent.getComponent();
        }

        @Override
        protected SvnTag createNewTag(final SvnBranch pBranch,
                                      final int pTag) {
            return new SvnTag(pBranch, pTag);
        }

        /**
         * Discover tag list from repository.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        public void discover(final ReportStatus pReport) throws JOceanusException {
            /* Reset the list */
            clear();

            /* Access a LogClient */
            SvnRepository myRepo = theComponent.getRepository();
            SVNClientManager myMgr = myRepo.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the tags directory URL */
                SVNURL myURL = SVNURL.parseURIEncoded(theComponent.getTagsPath());

                /* List the tag directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler());
            } catch (SVNException e) {
                throw new JThemisIOException("Failed to discover tags for " + getBranch().getBranchName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* Loop through the tags */
            Iterator<SvnTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                SvnTag myTag = myIterator.next();

                /* Report stage */
                pReport.setNewStage("Analysing tag " + myTag.getTagName());

                /* Parse project file */
                MvnProjectDefinition myProject = theComponent.parseProjectURL(myTag.getURLPath());
                myTag.setProjectDefinition(myProject);

                /* Register the tag */
                if (myProject != null) {
                    myRepo.registerTag(myProject.getDefinition(), myTag);
                }

                /* Analyse history map */
                myTag.discoverHistory();
            }
        }

        /**
         * The Directory Entry Handler.
         */
        private final class ListDirHandler
                implements ISVNDirEntryHandler {

            @Override
            public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
                /* Ignore if not a directory and if it is top-level */
                if (pEntry.getKind() != SVNNodeKind.DIR) {
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
                int myTagNo = Integer.parseInt(myName);

                /* Create the tag and add to the list */
                SvnTag myTag = new SvnTag(getBranch(), myTagNo);
                add(myTag);
            }
        }
    }
}
