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
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmBranch;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.data.RevisionHistoryMap.RevisionPath;
import net.sourceforge.joceanus.jthemis.svn.data.SvnTag.SvnTagList;

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
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class SvnBranch
        extends ScmBranch<SvnBranch, SvnComponent, SvnRepository> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SvnBranch.class.getSimpleName(), ScmBranch.FIELD_DEFS);

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

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
        if (FIELD_REVPATH.equals(pField)) {
            return theRevisionPath;
        }

        /* Unknown */
        return super.getFieldValue(pField);
    }

    /**
     * Parent Repository.
     */
    private final SvnRepository theRepository;

    /**
     * RevisionPath.
     */
    private RevisionPath theRevisionPath;

    /**
     * Get the repository for this branch.
     * @return the repository
     */
    public SvnRepository getRepository() {
        return theRepository;
    }

    @Override
    public SvnTagList getTagList() {
        return (SvnTagList) super.getTagList();
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    protected SvnBranch(final SvnComponent pParent,
                        final String pVersion) {
        /* Call super constructor */
        super(pParent, pVersion);

        /* Store values */
        theRepository = pParent.getRepository();

        /* Create tag list */
        SvnTagList myTags = new SvnTagList(this);
        setTags(myTags);
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pDelta the delta version
     */
    private SvnBranch(final SvnComponent pParent,
                      final int pMajor,
                      final int pMinor,
                      final int pDelta) {
        /* Call super constructor */
        super(pParent, pMajor, pMinor, pDelta);

        /* Store values */
        theRepository = pParent.getRepository();

        /* Create tag list */
        SvnTagList myTags = new SvnTagList(this);
        setTags(myTags);
    }

    /**
     * Obtain repository path without prefix.
     * @return the Repository path for this branch
     */
    public String getPath() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* If this is the trunk branch */
        if (isTrunk()) {
            /* Build the initial path */
            myBuilder.append(getComponent().getTrunkPath());
            myBuilder.delete(0, theRepository.getBase().length());

            /* else its a standard branch */
        } else {
            /* Build the initial path */
            myBuilder.append(getComponent().getBranchesPath());
            myBuilder.delete(0, theRepository.getBase().length());
            myBuilder.append(SvnRepository.SEP_URL);

            /* Build the version directory */
            myBuilder.append(getBranchName());
        }

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path.
     * @return the Repository path for this branch
     */
    public String getURLPath() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* If this is the trunk branch */
        if (isTrunk()) {
            /* Build the initial path */
            myBuilder.append(getComponent().getTrunkPath());

            /* else its a standard branch */
        } else {
            /* Build the initial path */
            myBuilder.append(getComponent().getBranchesPath());
            myBuilder.append(SvnRepository.SEP_URL);

            /* Build the version directory */
            myBuilder.append(getBranchName());
        }

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

    @Override
    public SvnTag nextTag() {
        /* Determine the next tag */
        return (SvnTag) super.nextTag();
    }

    /**
     * Discover HistoryPath.
     * @throws JOceanusException on error
     */
    private void discoverHistory() throws JOceanusException {
        /* Access history map */
        RevisionHistoryMap myHistMap = theRepository.getHistoryMap();

        /* Determine the next major branch */
        theRevisionPath = myHistMap.discoverBranch(this);
    }

    /**
     * List of branches.
     */
    public static final class SvnBranchList
            extends ScmBranchList<SvnBranch, SvnComponent, SvnRepository> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnBranchList.class.getSimpleName(), ScmBranchList.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * The parent component.
         */
        private final SvnComponent theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected SvnBranchList(final SvnComponent pParent) {
            /* Call super constructor */
            super(SvnBranch.class, pParent);

            /* Store parent for use by entry handler */
            theComponent = pParent;
        }

        @Override
        protected SvnBranch createNewBranch(final SvnComponent pComponent,
                                            final int pMajor,
                                            final int pMinor,
                                            final int pDelta) {
            return new SvnBranch(pComponent, pMajor, pMinor, pDelta);
        }

        /**
         * Discover branch list from repository.
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
                /* Parse project file for trunk */
                MvnProjectDefinition myProject = theComponent.parseProjectURL(theComponent.getTrunkPath());

                /* If we have a project definition */
                if (myProject != null) {
                    /* Access the name and ignore if it does not start with branch prefix */
                    String myName = myProject.getDefinition().getVersion();
                    if (myName.startsWith(BRANCH_PREFIX)) {
                        /* Strip prefix */
                        myName = myName.substring(BRANCH_PREFIX.length());

                        /* Create the branch and add to the list */
                        SvnBranch myBranch = new SvnBranch(theComponent, myName);
                        myBranch.setTrunk();
                        add(myBranch);
                    }
                }

                /* Access the branch directory URL */
                SVNURL myURL = SVNURL.parseURIEncoded(theComponent.getBranchesPath());

                /* List the branch directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false,
                        SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler(false));

                /* Access the tags directory URL */
                myURL = SVNURL.parseURIEncoded(theComponent.getTagsPath());

                /* List the branch directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false,
                        SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler(true));

            } catch (SVNException e) {
                throw new JThemisIOException("Failed to discover branches for " + theComponent.getName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* Loop to the last entry */
            Iterator<SvnBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                SvnBranch myBranch = myIterator.next();

                /* Report stage */
                pReport.setNewStage("Analysing branch " + myBranch.getBranchName());

                /* If this is a real branch */
                if (!myBranch.isVirtual()) {
                    /* Parse project file */
                    MvnProjectDefinition myProject = theComponent.parseProjectURL(myBranch.getURLPath());
                    myBranch.setProjectDefinition(myProject);

                    /* Register the branch */
                    if (myProject != null) {
                        myRepo.registerBranch(myProject.getDefinition(), myBranch);
                    }

                    /* Analyse history map */
                    myBranch.discoverHistory();
                }

                /* Discover tags */
                myBranch.getTagList().discover(pReport);
            }
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected SvnBranch locateBranch(final SVNURL pURL) {
            /* Loop through the entries */
            Iterator<SvnBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                SvnBranch myBranch = myIterator.next();

                /* Access branch URL */
                SVNURL myBranchURL = myBranch.getURL();

                /* If this is parent of the passed URL */
                if ((pURL.getPath().equals(myBranchURL.getPath())) || (pURL.getPath().startsWith(myBranchURL.getPath() + "/"))) {
                    /* This is the correct branch */
                    return myBranch;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate Tag.
         * @param pVersion the version to locate
         * @param pTag the tag to locate
         * @return the relevant tag or Null
         */
        @Override
        protected SvnTag locateTag(final String pVersion,
                                   final int pTag) {
            /* Access list iterator */
            return (SvnTag) super.locateTag(pVersion, pTag);
        }

        /**
         * The Directory Entry Handler.
         */
        private final class ListDirHandler
                implements ISVNDirEntryHandler {
            /**
             * Are we looking at tags?
             */
            private final boolean isTags;

            /**
             * Constructor.
             * @param pTags is this a search for tags?
             */
            private ListDirHandler(final boolean pTags) {
                isTags = pTags;
            }

            @Override
            public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
                /* Ignore if not a directory and if it is top-level */
                if (pEntry.getKind() != SVNNodeKind.DIR) {
                    return;
                }
                if (pEntry.getRelativePath().length() == 0) {
                    return;
                }

                /* Access the name and ignore if it does not start with branch prefix */
                String myName = pEntry.getName();
                if (!myName.startsWith(BRANCH_PREFIX)) {
                    return;
                }

                /* If this is tags */
                if (isTags) {
                    /* Locate the tag separator */
                    int iIndex = myName.indexOf('-');
                    if (iIndex == -1) {
                        return;
                    }

                    /* Access branch name */
                    myName = myName.substring(0, iIndex);

                    /* Ignore if branch is already known */
                    if (locateBranch(myName) != null) {
                        return;
                    }
                }

                /* Strip prefix */
                myName = myName.substring(BRANCH_PREFIX.length());

                /* Create the branch and add to the list */
                SvnBranch myBranch = new SvnBranch(theComponent, myName);
                if (isTags) {
                    myBranch.setVirtual();
                }
                add(myBranch);
            }
        }
    }
}
