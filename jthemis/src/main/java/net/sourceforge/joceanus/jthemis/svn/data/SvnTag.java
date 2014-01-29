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

import net.sourceforge.joceanus.jmetis.list.OrderedList;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisDataException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.svn.project.MvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.project.MvnProjectId;
import net.sourceforge.joceanus.jthemis.svn.project.MvnProjectId.ProjectStatus;

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
 * @author Tony
 */
public final class SvnTag
        implements JDataContents, Comparable<SvnTag> {
    /**
     * The tag prefix.
     */
    private static final String PREFIX_TAG = "-b";

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(SvnTag.class.getSimpleName());

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Component field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    /**
     * Branch field id.
     */
    private static final JDataField FIELD_BRAN = FIELD_DEFS.declareEqualityField("Branch");

    /**
     * Name field id.
     */
    private static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Project definition field id.
     */
    private static final JDataField FIELD_PROJECT = FIELD_DEFS.declareLocalField("Project");

    /**
     * Dependencies field id.
     */
    private static final JDataField FIELD_DEPENDS = FIELD_DEFS.declareLocalField("Dependencies");

    /**
     * Revision field id.
     */
    private static final JDataField FIELD_LREV = FIELD_DEFS.declareLocalField("Revision");

    @Override
    public String formatObject() {
        return getTagName();
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
        if (FIELD_BRAN.equals(pField)) {
            return theBranch;
        }
        if (FIELD_NAME.equals(pField)) {
            return getTagName();
        }
        if (FIELD_PROJECT.equals(pField)) {
            return theProject;
        }
        if (FIELD_DEPENDS.equals(pField)) {
            return (theDependencies.isEmpty())
                                              ? JDataFieldValue.SKIP
                                              : theDependencies;
        }
        if (FIELD_LREV.equals(pField)) {
            return theRevision;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
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
     * The Branch to which this Tag belongs.
     */
    private final SvnBranch theBranch;

    /**
     * The Tag number.
     */
    private final int theTag;

    /**
     * The project definition.
     */
    private MvnProjectDefinition theProject = null;

    /**
     * The dependency map.
     */
    private Map<SvnComponent, SvnTag> theDependencies;

    /**
     * Project status.
     */
    private ProjectStatus theProjectStatus = ProjectStatus.RAW;

    /**
     * The Last Changed Revision.
     */
    private final long theRevision;

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     * @param pRevision the revision that created this tag
     */
    private SvnTag(final SvnBranch pParent,
                   final int pTag,
                   final long pRevision) {
        /* Store values */
        theBranch = pParent;
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
        theTag = pTag;
        theRevision = pRevision;
        theDependencies = new HashMap<SvnComponent, SvnTag>();
    }

    /**
     * Get the tag name for this tag.
     * @return the tag name
     */
    public String getTagName() {
        return theBranch.getBranchName() + PREFIX_TAG + theTag;
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
     * Get the branch for this tag.
     * @return the branch
     */
    public SvnBranch getBranch() {
        return theBranch;
    }

    /**
     * Get Project Definition.
     * @return the project definition
     */
    public MvnProjectDefinition getProjectDefinition() {
        return theProject;
    }

    /**
     * Get Dependencies.
     * @return the dependencies
     */
    public Map<SvnComponent, SvnTag> getDependencies() {
        return theDependencies;
    }

    /**
     * Get the revision for this tag.
     * @return the revision
     */
    public long getRevision() {
        return theRevision;
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

    @Override
    public int compareTo(final SvnTag pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the branches */
        iCompare = theBranch.compareTo(pThat.theBranch);
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare tag numbers */
        if (theTag > pThat.theTag) {
            return -1;
        }
        if (theTag < pThat.theTag) {
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
        if (pThat instanceof SvnTag) {
            return false;
        }
        SvnTag myThat = (SvnTag) pThat;

        /* Compare fields */
        if (!theBranch.equals(myThat.theBranch)) {
            return false;
        }
        return theTag == myThat.theTag;
    }

    @Override
    public int hashCode() {
        return (theBranch.hashCode() * SvnRepository.HASH_PRIME) + theTag;
    }

    /**
     * Clone the definition.
     * @param pDefinition the definition to clone
     * @throws JOceanusException on error
     */
    public void cloneDefinition(final MvnProjectDefinition pDefinition) throws JOceanusException {
        /* clone the project definition */
        theProject = new MvnProjectDefinition(pDefinition);
        theProject.setSnapshotVersion(getTagName());
    }

    /**
     * Obtain full tag list including dependencies.
     * @return the full tag list.
     */
    public Map<SvnComponent, SvnTag> getAllTags() {
        /* Create a new map and add self to map */
        Map<SvnComponent, SvnTag> myMap = new HashMap<SvnComponent, SvnTag>(theDependencies);
        myMap.put(theComponent, this);

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
        Map<SvnComponent, SvnTag> myNew = new HashMap<SvnComponent, SvnTag>(theDependencies);

        /* Loop through our dependencies */
        for (SvnTag myDep : theDependencies.values()) {
            /* Resolve dependencies */
            myDep.resolveDependencies(pReport);

            /* Loop through underlying dependencies */
            for (SvnTag mySub : myDep.getDependencies().values()) {
                /* Access underlying component */
                SvnComponent myComp = mySub.getComponent();

                /* Access existing dependency */
                SvnTag myExisting = myNew.get(myComp);

                /* If we have an existing dependency */
                if (myExisting != null) {
                    /* Check it is identical */
                    if (!myExisting.equals(mySub)) {
                        throw new JThemisDataException(this, "Inconsistent dependency for Tag");
                    }
                } else {
                    /* Add dependency */
                    myNew.put(myComp, mySub);
                }
            }
        }

        /* Check that we are not dependent on a different version of this component */
        if (myNew.get(theComponent) != null) {
            throw new JThemisDataException(this, "Inconsistent dependency for Tag");
        }

        /* Store new dependencies and mark as resolved */
        theDependencies = myNew;
        theProjectStatus = ProjectStatus.FINAL;
    }

    /**
     * Obtain merged and validated tag map.
     * @param pTags the core tags
     * @return the tag map
     * @throws JOceanusException on error
     */
    public static Map<SvnComponent, SvnTag> getTagMap(final SvnTag[] pTags) throws JOceanusException {
        /* Set default map */
        Map<SvnComponent, SvnTag> myResult = null;
        SvnRepository myRepo = null;

        /* Loop through the tags */
        for (SvnTag myTag : pTags) {
            /* Access map */
            Map<SvnComponent, SvnTag> myMap = myTag.getAllTags();

            /* If this is the first tag */
            if (myResult == null) {
                /* Store as result */
                myResult = myMap;
                myRepo = myTag.getRepository();
                continue;
            }

            /* Check this is the same repository */
            if (!myRepo.equals(myTag.getRepository())) {
                /* throw exception */
                throw new JThemisDataException("Different repository for tag");
            }

            /* Loop through map elements */
            for (Map.Entry<SvnComponent, SvnTag> myEntry : myMap.entrySet()) {
                /* Obtain any existing entry */
                SvnTag myExisting = myResult.get(myEntry.getKey());

                /* If this entry doesn't exist */
                if (myExisting == null) {
                    /* Add to map */
                    myResult.put(myEntry.getKey(), myEntry.getValue());

                    /* else if the tag differs */
                } else if (!myExisting.equals(myEntry.getValue())) {
                    /* throw exception */
                    throw new JThemisDataException("Conflicting version for tag");
                }
            }
        }

        /* Return the result */
        return myResult;
    }

    /**
     * List of tags.
     */
    public static final class SvnTagList
            extends OrderedList<SvnTag>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnTagList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Last Revision field id.
         */
        private static final JDataField FIELD_LREV = FIELD_DEFS.declareLocalField("LastRevision");

        @Override
        public String formatObject() {
            return "TagList(" + size() + ")";
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
            if (FIELD_LREV.equals(pField)) {
                return theLastRevision;
            }

            /* Unknown */
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Parent Component.
         */
        private final SvnComponent theComponent;

        /**
         * The parent branch.
         */
        private final SvnBranch theBranch;

        /**
         * The prefix.
         */
        private final String thePrefix;

        /**
         * The last revision.
         */
        private long theLastRevision = -1;

        /**
         * Get the last revision for this tag list.
         * @return the last revision
         */
        public long getLastRevision() {
            return theLastRevision;
        }

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected SvnTagList(final SvnBranch pParent) {
            /* Call super constructor */
            super(SvnTag.class);

            /* Store parent for use by entry handler */
            theBranch = pParent;
            theComponent = (pParent == null)
                                            ? null
                                            : pParent.getComponent();

            /* Build prefix */
            thePrefix = (pParent == null)
                                         ? null
                                         : theBranch.getBranchName() + PREFIX_TAG;
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
                throw new JThemisIOException("Failed to discover tags for " + theBranch.getBranchName(), e);
            } finally {
                myRepo.releaseClientManager(myMgr);
            }

            /* Access list iterator */
            Iterator<SvnTag> myIterator = iterator();

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                SvnTag myTag = myIterator.next();

                /* Report stage */
                pReport.setNewStage("Analysing tag " + myTag.getTagName());

                /* Parse project file */
                MvnProjectDefinition myProject = myRepo.parseProjectURL(myTag.getURLPath());
                myTag.theProject = myProject;

                /* Register the tag */
                if (myProject != null) {
                    myRepo.registerTag(myProject.getDefinition(), myTag);
                }
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
            Iterator<SvnTag> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Tag */
                SvnTag myTag = myIterator.next();
                MvnProjectDefinition myDef = myTag.getProjectDefinition();
                Map<SvnComponent, SvnTag> myDependencies = myTag.getDependencies();

                /* If we have a project definition */
                if (myDef != null) {
                    /* Loop through the dependencies */
                    Iterator<MvnProjectId> myProjIterator = myDef.getDependencies().iterator();
                    while (myProjIterator.hasNext()) {
                        /* Access project id */
                        MvnProjectId myId = myProjIterator.next();

                        /* Locate dependency branch */
                        SvnTag myDependency = myRepo.locateTag(myId);
                        if (myDependency != null) {
                            /* Access component */
                            SvnComponent myComponent = myDependency.getComponent();

                            /* Check that the dependency does not already exist */
                            if (myDependencies.get(myComponent) == null) {
                                /* Add to the dependency map */
                                myDependencies.put(myComponent, myDependency);
                            } else {
                                /* Throw exception */
                                throw new JThemisDataException(myTag, "Duplicate component dependency");
                            }
                        }
                    }
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
            Iterator<SvnTag> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Tag and resolve dependencies */
                SvnTag myTag = myIterator.next();
                myTag.resolveDependencies(pReport);
            }
        }

        /**
         * Locate tag.
         * @param pTag the tag
         * @return the relevant tag or Null
         */
        public SvnTag locateTag(final SvnTag pTag) {
            /* Access list iterator */
            Iterator<SvnTag> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Tag */
                SvnTag myTag = myIterator.next();

                /* If this is the correct tag */
                int iCompare = myTag.compareTo(pTag);
                if (iCompare > 0) {
                    break;
                }
                if (iCompare < 0) {
                    continue;
                }
                return myTag;
            }

            /* Not found */
            return null;
        }

        /**
         * Locate Tag.
         * @param pTag the tag to locate
         * @return the relevant tag or Null
         */
        protected SvnTag locateTag(final int pTag) {
            /* Access list iterator */
            Iterator<SvnTag> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Tag */
                SvnTag myTag = myIterator.next();

                /* If this is the correct tag */
                if (pTag == myTag.theTag) {
                    /* return the tag */
                    return myTag;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Determine latest tag.
         * @return the latestt tag
         */
        public SvnTag latestTag() {
            /* Access list iterator */
            Iterator<SvnTag> myIterator = iterator();
            SvnTag myTag = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next tag */
                myTag = myIterator.next();
            }

            /* Return the tag */
            return myTag;
        }

        /**
         * Determine next tag.
         * @return the next tag
         */
        public SvnTag nextTag() {
            /* Access latest tag */
            SvnTag myTag = latestTag();

            /* Determine the largest current tag */
            int myTagNo = (myTag == null)
                                         ? 0
                                         : myTag.theTag;

            /* Create the tag */
            return new SvnTag(theBranch, myTagNo + 1, -1);
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
                if (!myName.startsWith(thePrefix)) {
                    return;
                }
                myName = myName.substring(thePrefix.length());

                /* Determine tag and last revision */
                int myTagNo = Integer.parseInt(myName);
                long myRev = pEntry.getRevision();

                /* Adjust last revision */
                theLastRevision = Math.max(theLastRevision, myRev);

                /* Create the tag and add to the list */
                SvnTag myTag = new SvnTag(theBranch, myTagNo, myRev);
                add(myTag);
            }
        }
    }
}