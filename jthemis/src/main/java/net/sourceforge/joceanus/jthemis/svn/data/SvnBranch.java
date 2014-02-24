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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisDataException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmBranch;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectId;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectId.ProjectStatus;
import net.sourceforge.joceanus.jthemis.svn.data.JSvnReporter.ReportStatus;
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
     * Dependencies field id.
     */
    private static final JDataField FIELD_DEPENDS = FIELD_DEFS.declareLocalField("Dependencies");

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
        if (FIELD_DEPENDS.equals(pField)) {
            return (theDependencies.isEmpty())
                                              ? JDataFieldValue.SKIP
                                              : theDependencies;
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
     * The dependency map.
     */
    private Map<SvnComponent, SvnBranch> theDependencies;

    /**
     * Project status.
     */
    private ProjectStatus theProjectStatus = ProjectStatus.RAW;

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
     * Get Dependencies.
     * @return the dependencies
     */
    public Map<SvnComponent, SvnBranch> getDependencies() {
        return theDependencies;
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    private SvnBranch(final SvnComponent pParent,
                      final String pVersion) {
        /* Call super constructor */
        super(pParent, pVersion);

        /* Store values */
        theRepository = pParent.getRepository();

        /* Create tag list */
        SvnTagList myTags = new SvnTagList(this);
        setTags(myTags);

        /* Create dependency map */
        theDependencies = new HashMap<SvnComponent, SvnBranch>();
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

        /* Create dependency map */
        theDependencies = new HashMap<SvnComponent, SvnBranch>();
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
     * Obtain full branch list including dependencies.
     * @return the full branch list.
     */
    public Map<SvnComponent, SvnBranch> getAllBranches() {
        /* Create a new list and add self to list */
        Map<SvnComponent, SvnBranch> myMap = new HashMap<SvnComponent, SvnBranch>(theDependencies);
        myMap.put(getComponent(), this);

        /* return the map */
        return myMap;
    }

    /**
     * resolveDependencies.
     * @param pReport the report object
     * @throws JOceanusException on error
     */
    private void resolveDependencies(final ReportStatus pReport) throws JOceanusException {
        /* Switch on status */
        switch (theProjectStatus) {
            case FINAL:
                return;
            case MERGING:
                throw new JThemisDataException(this, "IllegalState for Tag");
            default:
                break;
        }

        /* If we have no dependencies */
        if (theDependencies.isEmpty()) {
            /* Set as merged and return */
            theProjectStatus = ProjectStatus.FINAL;
            return;
        }

        /* Set project status to merging to prevent circular dependency */
        theProjectStatus = ProjectStatus.MERGING;

        /* Allocate a new map */
        Map<SvnComponent, SvnBranch> myNew = new HashMap<SvnComponent, SvnBranch>(theDependencies);

        /* Loop through our dependencies */
        for (SvnBranch myDep : theDependencies.values()) {
            /* Resolve dependencies */
            myDep.resolveDependencies(pReport);

            /* Loop through underlying dependencies */
            for (SvnBranch mySub : myDep.getDependencies().values()) {
                /* Access underlying component */
                SvnComponent myComp = mySub.getComponent();

                /* Access existing dependency */
                SvnBranch myExisting = myNew.get(myComp);

                /* If we have an existing dependency */
                if (myExisting != null) {
                    /* Check it is identical */
                    if (!myExisting.equals(mySub)) {
                        throw new JThemisDataException(this, "Inconsistent dependency for Branch");
                    }
                } else {
                    /* Add dependency */
                    myNew.put(myComp, mySub);
                }
            }
        }

        /* Check that we are not dependent on a different version of this component */
        if (myNew.get(getComponent()) != null) {
            throw new JThemisDataException(this, "Inconsistent dependency for Branch");
        }

        /* Store new dependencies and mark as resolved */
        theDependencies = myNew;
        theProjectStatus = ProjectStatus.FINAL;
    }

    /**
     * Obtain merged and validated branch map.
     * @param pBranches the core branches
     * @return the branch map
     * @throws JOceanusException on error
     */
    public static Map<SvnComponent, SvnBranch> getBranchMap(final SvnBranch[] pBranches) throws JOceanusException {
        /* Set default map */
        Map<SvnComponent, SvnBranch> myResult = null;
        SvnRepository myRepo = null;

        /* Loop through the branches */
        for (SvnBranch myBranch : pBranches) {
            /* Access map */
            Map<SvnComponent, SvnBranch> myMap = myBranch.getAllBranches();

            /* If this is the first branch */
            if (myResult == null) {
                /* Store as result */
                myResult = myMap;
                myRepo = myBranch.getRepository();
                continue;
            }

            /* Check this is the same repository */
            if (!myRepo.equals(myBranch.getRepository())) {
                /* throw exception */
                throw new JThemisDataException("Different repository for branch");
            }

            /* Loop through map elements */
            for (Map.Entry<SvnComponent, SvnBranch> myEntry : myMap.entrySet()) {
                /* Obtain any existing entry */
                SvnBranch myExisting = myResult.get(myEntry.getKey());

                /* If this entry doesn't exist */
                if (myExisting == null) {
                    /* Add to map */
                    myResult.put(myEntry.getKey(), myEntry.getValue());

                    /* else if the branch differs */
                } else if (!myExisting.equals(myEntry.getValue())) {
                    /* throw exception */
                    throw new JThemisDataException("Conflicting version for branch");
                }
            }
        }

        /* Return the result */
        return myResult;
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
                MvnProjectDefinition myProject = myRepo.parseProjectURL(theComponent.getTrunkPath());

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
                    MvnProjectDefinition myProject = myRepo.parseProjectURL(myBranch.getURLPath());
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
         * registerDependencies.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        protected void registerDependencies(final ReportStatus pReport) throws JOceanusException {
            /* Access list iterator */
            SvnRepository myRepo = theComponent.getRepository();
            Iterator<SvnBranch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                SvnBranch myBranch = myIterator.next();
                MvnProjectDefinition myDef = myBranch.getProjectDefinition();
                Map<SvnComponent, SvnBranch> myDependencies = myBranch.getDependencies();

                /* If we have a project definition */
                if (myDef != null) {
                    /* Loop through the dependencies */
                    Iterator<MvnProjectId> myProjIterator = myDef.getDependencies().iterator();
                    while (myProjIterator.hasNext()) {
                        /* Access project id */
                        MvnProjectId myId = myProjIterator.next();

                        /* Locate dependency branch */
                        SvnBranch myDependency = myRepo.locateBranch(myId);
                        if (myDependency != null) {
                            /* Access component */
                            SvnComponent myComponent = myDependency.getComponent();

                            /* Check that the dependency does not already exist */
                            if (myDependencies.get(myComponent) == null) {
                                /* Add to the dependency map */
                                myDependencies.put(myComponent, myDependency);
                            } else {
                                /* Throw exception */
                                throw new JThemisDataException(myBranch, "Duplicate component dependency");
                            }
                        }
                    }

                    /* register dependencies */
                    SvnTagList myTags = myBranch.getTagList();
                    myTags.registerDependencies(pReport);
                }
            }
        }

        /**
         * propagateDependencies.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        protected void propagateDependencies(final ReportStatus pReport) throws JOceanusException {
            /* Access list iterator */
            Iterator<SvnBranch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch and resolve dependencies */
                SvnBranch myBranch = myIterator.next();
                myBranch.resolveDependencies(pReport);

                /* Resolve dependencies for the tags */
                SvnTagList myTags = myBranch.getTagList();
                myTags.propagateDependencies(pReport);
            }
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected SvnBranch locateBranch(final SVNURL pURL) {
            /* Access list iterator */
            Iterator<SvnBranch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
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
