/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistoryMap.SvnRevisionPath;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnTag.SvnTagList;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class ThemisSvnBranch
        extends ThemisScmBranch<ThemisSvnBranch, ThemisSvnComponent, ThemisSvnRepository> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(ThemisSvnBranch.class.getSimpleName(), ThemisScmBranch.FIELD_DEFS);

    /**
     * Repository field id.
     */
    private static final MetisField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * RevisionPath.
     */
    private static final MetisField FIELD_REVPATH = FIELD_DEFS.declareLocalField("RevisionPath");

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThemisSvnBranch.class);

    /**
     * Parent Repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * RevisionPath.
     */
    private SvnRevisionPath theRevisionPath;

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    protected ThemisSvnBranch(final ThemisSvnComponent pParent,
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
    private ThemisSvnBranch(final ThemisSvnComponent pParent,
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
     * Get the repository for this branch.
     * @return the repository
     */
    public ThemisSvnRepository getRepository() {
        return theRepository;
    }

    /**
     * Get the revision path for this branch.
     * @return the revision path
     */
    public SvnRevisionPath getRevisionPath() {
        return theRevisionPath;
    }

    /**
     * Get the tag iterator for this branch.
     * @return the iterator
     */
    public Iterator<ThemisSvnTag> tagIterator() {
        return getTagList().iterator();
    }

    @Override
    public SvnTagList getTagList() {
        return (SvnTagList) super.getTagList();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
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
            myBuilder.append(ThemisSvnRepository.SEP_URL);

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
            myBuilder.append(ThemisSvnRepository.SEP_URL);

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
            LOGGER.error("Parse Failure", e);
            return null;
        }
    }

    @Override
    public ThemisSvnTag nextTag() {
        /* Determine the next tag */
        return (ThemisSvnTag) super.nextTag();
    }

    /**
     * Discover HistoryPath.
     * @throws OceanusException on error
     */
    private void discoverHistory() throws OceanusException {
        /* Access history map */
        ThemisSvnRevisionHistoryMap myHistMap = theRepository.getHistoryMap();

        /* Determine the next major branch */
        theRevisionPath = myHistMap.discoverBranch(this);
    }

    /**
     * List of branches.
     */
    public static final class SvnBranchList
            extends ScmBranchList<ThemisSvnBranch, ThemisSvnComponent, ThemisSvnRepository> {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(SvnBranchList.class.getSimpleName(), ScmBranchList.FIELD_DEFS);

        /**
         * The parent component.
         */
        private final ThemisSvnComponent theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected SvnBranchList(final ThemisSvnComponent pParent) {
            /* Call super constructor */
            super(ThemisSvnBranch.class, pParent);

            /* Store parent for use by entry handler */
            theComponent = pParent;
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        protected ThemisSvnBranch createNewBranch(final ThemisSvnComponent pComponent,
                                                  final int pMajor,
                                                  final int pMinor,
                                                  final int pDelta) {
            return new ThemisSvnBranch(pComponent, pMajor, pMinor, pDelta);
        }

        /**
         * Discover branch list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Access a LogClient */
            ThemisSvnRepository myRepo = theComponent.getRepository();
            SVNClientManager myMgr = myRepo.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Trunk branch */
            ThemisSvnBranch myTrunk = null;

            /* Protect against exceptions */
            try {
                /* Parse project file for trunk */
                ThemisMvnProjectDefinition myProject = theComponent.parseProjectURL(theComponent.getTrunkPath());

                /* If we have a project definition */
                if (myProject != null) {
                    /* Access the name and ignore if it does not start with branch prefix */
                    String myName = myProject.getDefinition().getVersion();
                    if (myName.startsWith(BRANCH_PREFIX)) {
                        /* Strip prefix */
                        myName = myName.substring(BRANCH_PREFIX.length());

                        /* Create the branch and add to the list */
                        myTrunk = new ThemisSvnBranch(theComponent, myName);
                        myTrunk.setTrunk();
                        add(myTrunk);

                        /* Register the branch */
                        myTrunk.setProjectDefinition(myProject);
                        myRepo.registerBranch(myProject.getDefinition(), myTrunk);
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
                throw new ThemisIOException("Failed to discover branches for " + theComponent.getName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* If we have a trunk */
            if (myTrunk != null) {
                /* Report stage */
                pReport.setNewStage("Analysing branch " + myTrunk.getBranchName());

                /* Analyse history map */
                myTrunk.discoverHistory();

                /* Discover trunk tags */
                myTrunk.getTagList().discover(pReport);
            }

            /* Loop to the last entry */
            Iterator<ThemisSvnBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                ThemisSvnBranch myBranch = myIterator.next();

                /* Skip trunk branch */
                if (myBranch.isTrunk()) {
                    continue;
                }

                /* Report stage */
                pReport.setNewStage("Analysing branch " + myBranch.getBranchName());

                /* If this is not a virtual branch */
                if (!myBranch.isVirtual()) {
                    /* Parse project file */
                    ThemisMvnProjectDefinition myProject = theComponent.parseProjectURL(myBranch.getURLPath());
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
         * Obtain trunk branch.
         * @return the trunk branch or Null
         */
        protected ThemisSvnBranch getTrunk() {
            /* Loop through the entries */
            Iterator<ThemisSvnBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                ThemisSvnBranch myBranch = myIterator.next();

                /* If this is the trunk */
                if (myBranch.isTrunk()) {
                    /* This is the correct branch */
                    return myBranch;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected ThemisSvnBranch locateBranch(final SVNURL pURL) {
            /* Loop through the entries */
            Iterator<ThemisSvnBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                ThemisSvnBranch myBranch = myIterator.next();

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
        protected ThemisSvnTag locateTag(final String pVersion,
                                         final int pTag) {
            /* Access list iterator */
            return (ThemisSvnTag) super.locateTag(pVersion, pTag);
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
                if (!SVNNodeKind.DIR.equals(pEntry.getKind())) {
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
                ThemisSvnBranch myBranch = new ThemisSvnBranch(theComponent, myName);
                if (isTags) {
                    myBranch.setVirtual();
                }
                add(myBranch);
            }
        }
    }
}
