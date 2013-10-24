/*******************************************************************************
 * jSvnManager: Java SubVersion Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jsvnmanager.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataFieldValue;
import net.sourceforge.joceanus.jsortedlist.OrderedList;
import net.sourceforge.joceanus.jsvnmanager.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jsvnmanager.data.Tag.TagList;
import net.sourceforge.joceanus.jsvnmanager.project.ProjectDefinition;
import net.sourceforge.joceanus.jsvnmanager.project.ProjectId;
import net.sourceforge.joceanus.jsvnmanager.project.ProjectId.ProjectStatus;

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
public final class Branch
        implements JDataContents, Comparable<Branch> {
    /**
     * The branch prefix.
     */
    private static final String BRANCH_PREFIX = "v";

    /**
     * The branch prefix.
     */
    private static final String BRANCH_SEP = ".";

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * The version shift.
     */
    private static final int VERSION_SHIFT = 10;

    /**
     * Number of version parts.
     */
    private static final int NUM_VERS_PARTS = 3;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Branch.class.getSimpleName());

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Component field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    /**
     * Name field id.
     */
    private static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Tags field id.
     */
    private static final JDataField FIELD_TAGS = FIELD_DEFS.declareLocalField("Tags");

    /**
     * Project definition field id.
     */
    private static final JDataField FIELD_PROJECT = FIELD_DEFS.declareLocalField("Project");

    /**
     * Dependencies field id.
     */
    private static final JDataField FIELD_DEPENDS = FIELD_DEFS.declareLocalField("Dependencies");

    /**
     * Number of elements field id.
     */
    private static final JDataField FIELD_NUMEL = FIELD_DEFS.declareLocalField("TotalElements");

    /**
     * Last Revision field id.
     */
    private static final JDataField FIELD_LREV = FIELD_DEFS.declareLocalField("LastRevision");

    /**
     * Last Tag Revision field id.
     */
    private static final JDataField FIELD_LTREV = FIELD_DEFS.declareLocalField("LastTagRevision");

    @Override
    public String formatObject() {
        return getBranchName();
    }

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
        if (FIELD_NAME.equals(pField)) {
            return getBranchName();
        }
        if (FIELD_TAGS.equals(pField)) {
            return theTags.size() > 0
                    ? theTags
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_PROJECT.equals(pField)) {
            return theProject;
        }
        if (FIELD_DEPENDS.equals(pField)) {
            return (theDependencies.size() > 0)
                    ? theDependencies
                    : JDataFieldValue.SkipField;
        }
        if (FIELD_NUMEL.equals(pField)) {
            return theNumElements;
        }
        if (FIELD_LREV.equals(pField)) {
            return theLastRevision;
        }
        if (FIELD_LTREV.equals(pField)) {
            long myRev = theTags.getLastRevision();
            return (myRev < theLastRevision)
                    ? myRev
                    : JDataFieldValue.SkipField;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Parent Repository.
     */
    private final Repository theRepository;

    /**
     * Parent Component.
     */
    private final Component theComponent;

    /**
     * Major version.
     */
    private final int theMajorVersion;

    /**
     * Minor version.
     */
    private final int theMinorVersion;

    /**
     * Delta version.
     */
    private final int theDeltaVersion;

    /**
     * TagList.
     */
    private final TagList theTags;

    /**
     * Last Change Revision.
     */
    private long theLastRevision = -1;

    /**
     * Number of elements.
     */
    private int theNumElements = 0;

    /**
     * Is this the trunk branch.
     */
    private boolean isTrunk = false;

    /**
     * The project definition.
     */
    private ProjectDefinition theProject = null;

    /**
     * The dependency map.
     */
    private Map<Component, Branch> theDependencies;

    /**
     * Project status.
     */
    private ProjectStatus theProjectStatus = ProjectStatus.RAW;

    /**
     * Get the repository for this branch.
     * @return the repository
     */
    public Repository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this branch.
     * @return the component
     */
    public Component getComponent() {
        return theComponent;
    }

    /**
     * Get the tag list for this branch.
     * @return the tag list
     */
    public TagList getTagList() {
        return theTags;
    }

    /**
     * Get the last revision for the branch.
     * @return the last revision
     */
    public long getLastRevision() {
        return theLastRevision;
    }

    /**
     * Get the number of elements in the branch.
     * @return the number of elements
     */
    public int getNumElements() {
        return theNumElements;
    }

    /**
     * Get Project Definition.
     * @return the project definition
     */
    public ProjectDefinition getProjectDefinition() {
        return theProject;
    }

    /**
     * Get Dependencies.
     * @return the dependencies
     */
    public Map<Component, Branch> getDependencies() {
        return theDependencies;
    }

    /**
     * Is this branch available for tagging.
     * @return true if there are changes since last tag was created, false otherwise.
     */
    public boolean isTaggable() {
        /* If we have changes since the last tag, return true */
        if (theLastRevision > theTags.getLastRevision()) {
            return true;
        }

        /* Loop through the dependencies */
        Iterator<Branch> myIterator = theDependencies.values().iterator();
        while (myIterator.hasNext()) {
            Branch myBranch = myIterator.next();

            /* If we are dependent on a tag-able branch, return true */
            if (myBranch.isTaggable()) {
                return true;
            }
        }

        /* Not tag-able */
        return false;
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    private Branch(final Component pParent,
                   final String pVersion) {
        /* Store values */
        theComponent = pParent;
        theRepository = pParent.getRepository();

        /* Parse the version */
        String[] myParts = pVersion.split("\\"
                                          + BRANCH_SEP);

        /* If we do not have three parts reject it */
        if (myParts.length != NUM_VERS_PARTS) {
            throw new IllegalArgumentException();
        }

        /* Determine values */
        theMajorVersion = Integer.parseInt(myParts[0]);
        theMinorVersion = Integer.parseInt(myParts[1]);
        theDeltaVersion = Integer.parseInt(myParts[2]);

        /* Create tag list */
        theTags = new TagList(this);
        theDependencies = new HashMap<Component, Branch>();
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pDelta the delta version
     */
    private Branch(final Component pParent,
                   final int pMajor,
                   final int pMinor,
                   final int pDelta) {
        /* Store values */
        theComponent = pParent;
        theRepository = pParent.getRepository();

        /* Determine values */
        theMajorVersion = pMajor;
        theMinorVersion = pMinor;
        theDeltaVersion = pDelta;

        /* Create tag list */
        theTags = new TagList(this);
        theDependencies = new HashMap<Component, Branch>();
    }

    /**
     * Get the branch name for this tag.
     * @return the branch name
     */
    public String getBranchName() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the version directory */
        myBuilder.append(BRANCH_PREFIX);
        myBuilder.append(theMajorVersion);
        myBuilder.append(BRANCH_SEP);
        myBuilder.append(theMinorVersion);
        myBuilder.append(BRANCH_SEP);
        myBuilder.append(theDeltaVersion);

        /* Return the branch name */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path without prefix.
     * @return the Repository path for this branch
     */
    public String getPath() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* If this is the trunk branch */
        if (isTrunk) {
            /* Build the initial path */
            myBuilder.append(theComponent.getTrunkPath());
            myBuilder.delete(0, theRepository.getBase().length());

            /* else its a standard branch */
        } else {
            /* Build the initial path */
            myBuilder.append(theComponent.getBranchesPath());
            myBuilder.delete(0, theRepository.getBase().length());
            myBuilder.append(Repository.SEP_URL);

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
        if (isTrunk) {
            /* Build the initial path */
            myBuilder.append(theComponent.getTrunkPath());

            /* else its a standard branch */
        } else {
            /* Build the initial path */
            myBuilder.append(theComponent.getBranchesPath());
            myBuilder.append(Repository.SEP_URL);

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
            return null;
        }
    }

    @Override
    public int compareTo(final Branch pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the components */
        iCompare = theComponent.compareTo(pThat.theComponent);
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare versions numbers */
        if (theMajorVersion < pThat.theMajorVersion) {
            return -1;
        }
        if (theMajorVersion > pThat.theMajorVersion) {
            return 1;
        }
        if (theMinorVersion < pThat.theMinorVersion) {
            return -1;
        }
        if (theMinorVersion > pThat.theMinorVersion) {
            return 1;
        }
        if (theDeltaVersion < pThat.theDeltaVersion) {
            return -1;
        }
        if (theDeltaVersion > pThat.theDeltaVersion) {
            return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        }
        if (pThat == null) {
            return false;
        }

        /* Check that the classes are the same */
        if ((pThat instanceof Branch)) {
            return false;
        }
        Branch myThat = (Branch) pThat;

        /* Compare fields */
        if (!theComponent.equals(myThat.theComponent)) {
            return false;
        }
        if (theMajorVersion != myThat.theMajorVersion) {
            return false;
        }
        if (theMinorVersion != myThat.theMinorVersion) {
            return false;
        }
        return theDeltaVersion == myThat.theDeltaVersion;
    }

    @Override
    public int hashCode() {
        return (theComponent.hashCode() * Repository.HASH_PRIME)
               + getVersionHash();
    }

    /**
     * Obtain hash of version #.
     * @return the version hash
     */
    private int getVersionHash() {
        int myVers = theMajorVersion
                     * VERSION_SHIFT;
        myVers += theMinorVersion;
        myVers *= VERSION_SHIFT;
        return myVers
               + theDeltaVersion;
    }

    /**
     * Clone the definition.
     * @param pDefinition the definition to clone
     * @throws JDataException on error
     */
    public void cloneDefinition(final ProjectDefinition pDefinition) throws JDataException {
        /* clone the project definition */
        theProject = new ProjectDefinition(pDefinition);
        theProject.setSnapshotVersion(getBranchName());
    }

    /**
     * Determine next major branch.
     * @return the next major branch
     */
    public Branch nextMajorBranch() {
        /* Determine the next major branch */
        BranchList myBranches = theComponent.getBranchList();
        return myBranches.nextMajorBranch();
    }

    /**
     * Determine next minor branch.
     * @return the next minor branch
     */
    public Branch nextMinorBranch() {
        /* Determine the next major branch */
        BranchList myBranches = theComponent.getBranchList();
        return myBranches.nextMinorBranch(this);
    }

    /**
     * Determine next delta branch.
     * @return the next delta branch
     */
    public Branch nextDeltaBranch() {
        /* Determine the next major branch */
        BranchList myBranches = theComponent.getBranchList();
        return myBranches.nextDeltaBranch(this);
    }

    /**
     * Determine next tag.
     * @return the next tag
     */
    public Tag nextTag() {
        /* Determine the next tag */
        return theTags.nextTag();
    }

    /**
     * Obtain full branch list including dependencies.
     * @return the full branch list.
     */
    public Map<Component, Branch> getAllBranches() {
        /* Create a new list and add self to list */
        Map<Component, Branch> myMap = new HashMap<Component, Branch>(theDependencies);
        myMap.put(theComponent, this);

        /* return the map */
        return myMap;
    }

    /**
     * Discover last change from repository.
     * @throws JDataException on error
     */
    protected void discoverLastRevision() throws JDataException {
        /* Access a LogClient */
        SVNClientManager myMgr = theRepository.getClientManager();
        SVNLogClient myClient = myMgr.getLogClient();

        /* Initialise the counts */
        theLastRevision = -1;
        theNumElements = 0;

        /* Protect against exceptions */
        try {
            /* Access the URL */
            SVNURL myURL = getURL();

            /* List the members directories */
            myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.INFINITY, SVNDirEntry.DIRENT_ALL, new BranchDirHandler());
        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to discover lastRevision for "
                                                                + getBranchName(), e);
        } finally {
            theRepository.releaseClientManager(myMgr);
        }
    }

    /**
     * resolveDependencies.
     * @param pReport the report object
     * @throws JDataException on error
     */
    private void resolveDependencies(final ReportStatus pReport) throws JDataException {
        /* Switch on status */
        switch (theProjectStatus) {
            case FINAL:
                return;
            case MERGING:
                throw new JDataException(ExceptionClass.DATA, this, "IllegalState for Tag");
            default:
                break;
        }

        /* If we have no dependencies */
        if (theDependencies.size() == 0) {
            /* Set as merged and return */
            theProjectStatus = ProjectStatus.FINAL;
            return;
        }

        /* Set project status to merging to prevent circular dependency */
        theProjectStatus = ProjectStatus.MERGING;

        /* Allocate a new map */
        Map<Component, Branch> myNew = new HashMap<Component, Branch>(theDependencies);

        /* Loop through our dependencies */
        for (Branch myDep : theDependencies.values()) {
            /* Resolve dependencies */
            myDep.resolveDependencies(pReport);

            /* Loop through underlying dependencies */
            for (Branch mySub : myDep.getDependencies().values()) {
                /* Access underlying component */
                Component myComp = mySub.getComponent();

                /* Access existing dependency */
                Branch myExisting = myNew.get(myComp);

                /* If we have an existing dependency */
                if (myExisting != null) {
                    /* Check it is identical */
                    if (!myExisting.equals(mySub)) {
                        throw new JDataException(ExceptionClass.DATA, this, "Inconsistent dependency for Branch");
                    }
                } else {
                    /* Add dependency */
                    myNew.put(myComp, mySub);
                }
            }
        }

        /* Check that we are not dependent on a different version of this component */
        if (myNew.get(theComponent) != null) {
            throw new JDataException(ExceptionClass.DATA, this, "Inconsistent dependency for Branch");
        }

        /* Store new dependencies and mark as resolved */
        theDependencies = myNew;
        theProjectStatus = ProjectStatus.FINAL;
    }

    /**
     * The Directory Entry Handler.
     */
    private final class BranchDirHandler
            implements ISVNDirEntryHandler {

        @Override
        public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
            /* Update the revision */
            long myRev = pEntry.getRevision();
            theLastRevision = Math.max(theLastRevision, myRev);
            theNumElements++;
        }
    }

    /**
     * Obtain merged and validated branch map.
     * @param pBranches the core branches
     * @return the branch map
     * @throws JDataException on error
     */
    public static Map<Component, Branch> getBranchMap(final Branch[] pBranches) throws JDataException {
        /* Set default map */
        Map<Component, Branch> myResult = null;
        Repository myRepo = null;

        /* Loop through the branches */
        for (Branch myBranch : pBranches) {
            /* Access map */
            Map<Component, Branch> myMap = myBranch.getAllBranches();

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
                throw new JDataException(ExceptionClass.DATA, "Different repository for branch");
            }

            /* Loop through map elements */
            for (Map.Entry<Component, Branch> myEntry : myMap.entrySet()) {
                /* Obtain any existing entry */
                Branch myExisting = myResult.get(myEntry.getKey());

                /* If this entry doesn't exist */
                if (myExisting == null) {
                    /* Add to map */
                    myResult.put(myEntry.getKey(), myEntry.getValue());

                    /* else if the branch differs */
                } else if (!myExisting.equals(myEntry.getValue())) {
                    /* throw exception */
                    throw new JDataException(ExceptionClass.DATA, "Conflicting version for branch");
                }
            }
        }

        /* Return the result */
        return myResult;
    }

    /**
     * List of branches.
     */
    public static final class BranchList
            extends OrderedList<Branch>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(BranchList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return "BranchList("
                   + size()
                   + ")";
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        /**
         * The parent component.
         */
        private final Component theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected BranchList(final Component pParent) {
            /* Call super constructor */
            super(Branch.class);

            /* Store parent for use by entry handler */
            theComponent = pParent;
        }

        /**
         * Discover branch list from repository.
         * @param pReport the report object
         * @throws JDataException on error
         */
        public void discover(final ReportStatus pReport) throws JDataException {
            /* Reset the list */
            clear();

            /* Access a LogClient */
            Repository myRepo = theComponent.getRepository();
            SVNClientManager myMgr = myRepo.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Parse project file for trunk */
                ProjectDefinition myProject = myRepo.parseProjectURL(theComponent.getTrunkPath());

                /* If we have a project definition */
                if (myProject != null) {
                    /* Access the name and ignore if it does not start with branch prefix */
                    String myName = myProject.getDefinition().getVersion();
                    if (myName.startsWith(BRANCH_PREFIX)) {
                        /* Strip prefix */
                        myName = myName.substring(BRANCH_PREFIX.length());

                        /* Create the branch and add to the list */
                        Branch myBranch = new Branch(theComponent, myName);
                        myBranch.isTrunk = true;
                        add(myBranch);
                    }
                }

                /* Access the branch directory URL */
                SVNURL myURL = SVNURL.parseURIEncoded(theComponent.getBranchesPath());

                /* List the branch directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler());

            } catch (SVNException e) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Failed to discover branches for "
                                                                    + theComponent.getName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                Branch myBranch = myIterator.next();

                /* Report stage */
                pReport.setNewStage("Analysing branch "
                                    + myBranch.getBranchName());

                /* Parse project file */
                ProjectDefinition myProject = myRepo.parseProjectURL(myBranch.getURLPath());
                myBranch.theProject = myProject;

                /* Register the branch */
                if (myProject != null) {
                    myRepo.registerBranch(myProject.getDefinition(), myBranch);
                }

                /* Discover tags and last revision */
                myBranch.discoverLastRevision();
                myBranch.getTagList().discover(pReport);
            }
        }

        /**
         * registerDependencies.
         * @param pReport the report object
         * @throws JDataException on error
         */
        protected void registerDependencies(final ReportStatus pReport) throws JDataException {
            /* Access list iterator */
            Repository myRepo = theComponent.getRepository();
            Iterator<Branch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();
                ProjectDefinition myDef = myBranch.getProjectDefinition();
                Map<Component, Branch> myDependencies = myBranch.getDependencies();

                /* If we have a project definition */
                if (myDef != null) {
                    /* Loop through the dependencies */
                    Iterator<ProjectId> myProjIterator = myDef.getDependencies().iterator();
                    while (myProjIterator.hasNext()) {
                        /* Access project id */
                        ProjectId myId = myProjIterator.next();

                        /* Locate dependency branch */
                        Branch myDependency = myRepo.locateBranch(myId);
                        if (myDependency != null) {
                            /* Access component */
                            Component myComponent = myDependency.getComponent();

                            /* Check that the dependency does not already exist */
                            if (myDependencies.get(myComponent) == null) {
                                /* Add to the dependency map */
                                myDependencies.put(myComponent, myDependency);
                            } else {
                                /* Throw exception */
                                throw new JDataException(ExceptionClass.DATA, myBranch, "Duplicate component dependency");
                            }
                        }
                    }

                    /* register dependencies */
                    TagList myTags = myBranch.getTagList();
                    myTags.registerDependencies(pReport);
                }
            }
        }

        /**
         * propagateDependencies.
         * @param pReport the report object
         * @throws JDataException on error
         */
        protected void propagateDependencies(final ReportStatus pReport) throws JDataException {
            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch and resolve dependencies */
                Branch myBranch = myIterator.next();
                myBranch.resolveDependencies(pReport);

                /* Resolve dependencies for the tags */
                TagList myTags = myBranch.getTagList();
                myTags.propagateDependencies(pReport);
            }
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected Branch locateBranch(final SVNURL pURL) {
            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();

                /* Access branch URL */
                SVNURL myBranchURL = myBranch.getURL();

                /* If this is parent of the passed URL */
                if ((pURL.getPath().equals(myBranchURL.getPath()))
                    || (pURL.getPath().startsWith(myBranchURL.getPath()
                                                  + "/"))) {
                    /* This is the correct branch */
                    return myBranch;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate branch.
         * @param pBranch the branch
         * @return the relevant branch or Null
         */
        public Branch locateBranch(final Branch pBranch) {
            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();

                /* If this is the correct branch */
                int iCompare = myBranch.compareTo(pBranch);
                if (iCompare > 0) {
                    break;
                }
                if (iCompare < 0) {
                    continue;
                }
                return myBranch;
            }

            /* Not found */
            return null;
        }

        /**
         * Locate Branch.
         * @param pVersion the version to locate
         * @return the relevant branch or Null
         */
        protected Branch locateBranch(final String pVersion) {
            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();

                /* If this is the correct branch */
                if (pVersion.equals(myBranch.getBranchName())) {
                    /* Return it */
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
        protected Tag locateTag(final String pVersion,
                                final int pTag) {
            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();

                /* If this is the correct branch */
                if (pVersion.equals(myBranch.getBranchName())) {
                    /* Search in this branches tags */
                    return myBranch.getTagList().locateTag(pTag);
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Determine next branch.
         * @param pBase the branch to base from
         * @param pBranchType the type of branch to create
         * @return the next branch
         */
        public Branch nextBranch(final Branch pBase,
                                 final BranchOpType pBranchType) {
            /* Switch on branch type */
            switch (pBranchType) {
                case MAJOR:
                    return nextMajorBranch();
                case MINOR:
                    return nextMinorBranch(pBase);
                case DELTA:
                default:
                    return nextDeltaBranch(pBase);
            }
        }

        /**
         * Determine next major branch.
         * @return the major branch
         */
        private Branch nextMajorBranch() {
            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();
            Branch myBranch = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                myBranch = myIterator.next();
            }

            /* Determine the largest current major version */
            int myMajor = (myBranch == null)
                    ? 0
                    : myBranch.theMajorVersion;

            /* Create the major revision */
            return new Branch(theComponent, myMajor + 1, 0, 0);
        }

        /**
         * Determine next minor branch.
         * @param pBase the branch to base from
         * @return the minor branch
         */
        private Branch nextMinorBranch(final Branch pBase) {
            /* Access major version */
            int myMajor = pBase.theMajorVersion;

            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();
            Branch myBranch = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                Branch myTest = myIterator.next();

                /* Handle wrong major version */
                if (myTest.theMajorVersion > myMajor) {
                    break;
                }
                if (myTest.theMajorVersion < myMajor) {
                    continue;
                }

                /* Record branch */
                myBranch = myTest;
            }

            /* Determine the largest current minor version */
            int myMinor = (myBranch == null)
                    ? 0
                    : myBranch.theMinorVersion;

            /* Create the minor revision */
            return new Branch(theComponent, myMajor, myMinor + 1, 0);
        }

        /**
         * Determine next delta branch.
         * @param pBase the branch to base from
         * @return the delta branch
         */
        private Branch nextDeltaBranch(final Branch pBase) {
            /* Access major/minor version */
            int myMajor = pBase.theMajorVersion;
            int myMinor = pBase.theMinorVersion;

            /* Access list iterator */
            Iterator<Branch> myIterator = iterator();
            Branch myBranch = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                Branch myTest = myIterator.next();

                /* Handle wrong major/minor version */
                if (myTest.theMajorVersion > myMajor) {
                    break;
                }
                if (myTest.theMajorVersion < myMajor) {
                    continue;
                }
                if (myTest.theMinorVersion > myMinor) {
                    break;
                }
                if (myTest.theMinorVersion < myMinor) {
                    continue;
                }

                /* Record branch */
                myBranch = myTest;
            }

            /* Determine the largest current revision */
            int myDelta = (myBranch == null)
                    ? 0
                    : myBranch.theDeltaVersion;

            /* Create the minor revision */
            return new Branch(theComponent, myMajor, myMinor, myDelta + 1);
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

                /* Access the name and ignore if it does not start with branch prefix */
                String myName = pEntry.getName();
                if (!myName.startsWith(BRANCH_PREFIX)) {
                    return;
                }

                /* Strip prefix */
                myName = myName.substring(BRANCH_PREFIX.length());

                /* Create the branch and add to the list */
                Branch myBranch = new Branch(theComponent, myName);
                add(myBranch);
            }
        }
    }

    /**
     * Branch operation.
     */
    public enum BranchOpType {
        /**
         * Major branch. Increment major version
         */
        MAJOR,

        /**
         * Minor branch. Increment minor version
         */
        MINOR,

        /**
         * Delta branch. Increment delta version
         */
        DELTA;
    }
}
