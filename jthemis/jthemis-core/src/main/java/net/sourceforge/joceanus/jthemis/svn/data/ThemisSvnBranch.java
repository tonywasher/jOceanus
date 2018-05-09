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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmTag;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnRevisionHistoryMap.ThemisSvnRevisionPath;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnTag.ThemisSvnTagList;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class ThemisSvnBranch
        extends ThemisScmBranch {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSvnBranch> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnBranch.class);

    /**
     * Repository field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_REPOSITORY, ThemisSvnBranch::getRepository);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISIONPATH, ThemisSvnBranch::getRevisionPath);
    }

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ThemisSvnBranch.class);

    /**
     * Parent Repository.
     */
    private final ThemisSvnRepository theRepository;

    /**
     * RevisionPath.
     */
    private ThemisSvnRevisionPath theRevisionPath;

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
        final ThemisSvnTagList myTags = new ThemisSvnTagList(this);
        setTags(myTags);
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pDelta the delta version
     */
    ThemisSvnBranch(final ThemisSvnComponent pParent,
                    final int pMajor,
                    final int pMinor,
                    final int pDelta) {
        /* Call super constructor */
        super(pParent, pMajor, pMinor, pDelta);

        /* Store values */
        theRepository = pParent.getRepository();

        /* Create tag list */
        final ThemisSvnTagList myTags = new ThemisSvnTagList(this);
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
    public ThemisSvnRevisionPath getRevisionPath() {
        return theRevisionPath;
    }

    /**
     * Get the tag iterator for this branch.
     * @return the iterator
     */
    public Iterator<ThemisScmTag> tagIterator() {
        return getTagList().iterator();
    }

    @Override
    public ThemisSvnTagList getTagList() {
        return (ThemisSvnTagList) super.getTagList();
    }

    @Override
    public MetisFieldSet<ThemisSvnBranch> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain repository path without prefix.
     * @return the Repository path for this branch
     */
    public String getPath() {
        /* Build the underlying string */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

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
            myBuilder.append(getName());
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
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

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
            myBuilder.append(getName());
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
    void discoverHistory() throws OceanusException {
        /* Access history map */
        final ThemisSvnRevisionHistoryMap myHistMap = theRepository.getHistoryMap();

        /* Determine the next major branch */
        theRevisionPath = myHistMap.discoverBranch(this);
    }

    @Override
    public ThemisSvnComponent getComponent() {
        return (ThemisSvnComponent) super.getComponent();
    }

    /**
     * List of branches.
     */
    public static final class ThemisSvnBranchList
            extends ThemisScmBranchList {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisSvnBranchList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnBranchList.class);

        /**
         * The parent component.
         */
        private final ThemisSvnComponent theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected ThemisSvnBranchList(final ThemisSvnComponent pParent) {
            /* Call super constructor */
            super(pParent);

            /* Store parent for use by entry handler */
            theComponent = pParent;
        }

        @Override
        public MetisFieldSet<ThemisSvnBranchList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        protected ThemisSvnBranch createNewBranch(final ThemisScmComponent pComponent,
                                                  final int pMajor,
                                                  final int pMinor,
                                                  final int pDelta) {
            return new ThemisSvnBranch((ThemisSvnComponent) pComponent, pMajor, pMinor, pDelta);
        }

        /**
         * Discover branch list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Obtain the active profile */
            final MetisProfile myBaseTask = pReport.getActiveTask();
            MetisProfile myTask = myBaseTask.startTask("discoverBranches");

            /* Access a LogClient */
            final ThemisSvnRepository myRepo = theComponent.getRepository();
            final SVNClientManager myMgr = myRepo.getClientManager();
            final SVNLogClient myClient = myMgr.getLogClient();

            /* Trunk branch */
            ThemisSvnBranch myTrunk = null;

            /* Protect against exceptions */
            try {
                /* Start parse task */
                MetisProfile mySubTask = myTask.startTask("parseTrunkProject");

                /* Parse project file for trunk */
                final ThemisMvnProjectDefinition myProject = theComponent.parseProjectURL(theComponent.getTrunkPath());

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

                /* Start task */
                mySubTask = myTask.startTask("searchForBranches");

                /* Access the branch directory URL */
                SVNURL myURL = SVNURL.parseURIEncoded(theComponent.getBranchesPath());

                /* List the branch directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false,
                        SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler(false));

                /* Start task */
                mySubTask = myTask.startTask("searchForTags");

                /* Access the tags directory URL */
                myURL = SVNURL.parseURIEncoded(theComponent.getTagsPath());

                /* List the tags directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false,
                        SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler(true));

                /* End the subTask */
                mySubTask.end();

            } catch (SVNException e) {
                throw new ThemisIOException("Failed to discover branches for " + theComponent.getName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* If we have a trunk */
            if (myTrunk != null) {
                /* Start the discoverTrunk task */
                myTask = myBaseTask.startTask("processTrunk");

                /* Report stage */
                pReport.setNewStage("Analysing branch " + myTrunk.getName());

                /* Start parse task */
                myTask.startTask("discoverHistory");

                /* Analyse history map */
                myTrunk.discoverHistory();

                /* Start tags task */
                final MetisProfile mySubTask = myTask.startTask("processTags");

                /* Discover trunk tags */
                myTrunk.getTagList().discover(pReport);

                /* End the subTask */
                mySubTask.end();
            }

            /* Sort the list */
            getUnderlyingList().sort(null);

            /* Loop to the last entry */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                final ThemisSvnBranch myBranch = (ThemisSvnBranch) myIterator.next();

                /* Skip trunk branch */
                if (myBranch.isTrunk()) {
                    continue;
                }

                /* Start the discoverBranch task */
                myTask = myBaseTask.startTask("discoverBranch:" + myBranch.getName());

                /* Report stage */
                pReport.setNewStage("Analysing branch " + myBranch.getName());

                /* If this is not a virtual branch */
                if (!myBranch.isVirtual()) {
                    /* Start parse task */
                    myTask.startTask("parseProject");

                    /* Parse project file */
                    final ThemisMvnProjectDefinition myProject = theComponent.parseProjectURL(myBranch.getURLPath());
                    myBranch.setProjectDefinition(myProject);

                    /* Register the branch */
                    if (myProject != null) {
                        myRepo.registerBranch(myProject.getDefinition(), myBranch);
                    }

                    /* Start parse task */
                    myTask.startTask("discoverHistory");

                    /* Analyse history map */
                    myBranch.discoverHistory();
                }

                /* Start tags task */
                final MetisProfile mySubTask = myTask.startTask("processTags");

                /* Discover tags */
                myBranch.getTagList().discover(pReport);

                /* End the subTask */
                mySubTask.end();
            }

            /* Complete the task */
            myTask.end();
        }

        /**
         * Obtain trunk branch.
         * @return the trunk branch or Null
         */
        protected ThemisSvnBranch getTrunk() {
            /* Loop through the entries */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnBranch myBranch = (ThemisSvnBranch) myIterator.next();

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
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnBranch myBranch = (ThemisSvnBranch) myIterator.next();

                /* Access branch URL */
                final SVNURL myBranchURL = myBranch.getURL();

                /* Skip if we cannot access the branch */
                if (myBranchURL == null) {
                    continue;
                }

                /* If this is parent of the passed URL */
                if (pURL.getPath().equals(myBranchURL.getPath())
                    || pURL.getPath().startsWith(myBranchURL.getPath() + "/")) {
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
            ListDirHandler(final boolean pTags) {
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
                    final int iIndex = myName.indexOf('-');
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
                final ThemisSvnBranch myBranch = new ThemisSvnBranch(theComponent, myName);
                if (isTags) {
                    myBranch.setVirtual();
                }
                add(myBranch);
            }
        }
    }
}
