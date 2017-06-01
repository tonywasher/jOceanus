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
package net.sourceforge.joceanus.jthemis.scm.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmTag.ScmTagList;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 * @param <B> the branch data type
 * @param <C> the component data type
 * @param <R> the repository data type
 */
public abstract class ThemisScmBranch<B extends ThemisScmBranch<B, C, R>, C extends ThemisScmComponent<C, R>, R extends ThemisScmRepository<R>>
        implements MetisDataFieldItem, Comparable<B> {
    /**
     * The branch prefix.
     */
    protected static final String BRANCH_PREFIX = "v";

    /**
     * The branch separator.
     */
    private static final String BRANCH_SEP = ".";

    /**
     * The buffer length.
     */
    protected static final int BUFFER_LEN = 100;

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
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(ThemisScmBranch.class);

    /**
     * Name field id.
     */
    private static final MetisDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Component field id.
     */
    private static final MetisDataField FIELD_COMPONENT = FIELD_DEFS.declareEqualityField("Component");

    /**
     * Tags field id.
     */
    private static final MetisDataField FIELD_TAGS = FIELD_DEFS.declareLocalField("Tags");

    /**
     * Project definition field id.
     */
    private static final MetisDataField FIELD_PROJECT = FIELD_DEFS.declareLocalField("Project");

    /**
     * Component.
     */
    private final C theComponent;

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
    private ScmTagList<?, B, C, R> theTags;

    /**
     * Is this the trunk branch.
     */
    private boolean isTrunk;

    /**
     * Is this a virtual branch.
     */
    private boolean isVirtual;

    /**
     * The project definition.
     */
    private ThemisMvnProjectDefinition theProject;

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    protected ThemisScmBranch(final C pParent,
                              final String pVersion) {
        /* Store the component */
        theComponent = pParent;

        /* Parse the version */
        String[] myParts = pVersion.split("\\" + BRANCH_SEP);

        /* If we do not have three parts reject it */
        if (myParts.length != NUM_VERS_PARTS) {
            throw new IllegalArgumentException();
        }

        /* Determine values */
        theMajorVersion = Integer.parseInt(myParts[0]);
        theMinorVersion = Integer.parseInt(myParts[1]);
        theDeltaVersion = Integer.parseInt(myParts[2]);
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pDelta the delta version
     */
    protected ThemisScmBranch(final C pParent,
                              final int pMajor,
                              final int pMinor,
                              final int pDelta) {
        /* Store the component */
        theComponent = pParent;

        /* Determine values */
        theMajorVersion = pMajor;
        theMinorVersion = pMinor;
        theDeltaVersion = pDelta;
    }

    @Override
    public String toString() {
        return getBranchName();
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle standard fields */
        if (FIELD_NAME.equals(pField)) {
            return getBranchName();
        }
        if (FIELD_COMPONENT.equals(pField)) {
            return theComponent;
        }
        if (FIELD_TAGS.equals(pField)) {
            return theTags.isEmpty()
                                     ? MetisDataFieldValue.SKIP
                                     : theTags;
        }
        if (FIELD_PROJECT.equals(pField)) {
            return theProject;
        }

        /* Unknown */
        return MetisDataFieldValue.UNKNOWN;
    }

    /**
     * Obtain the data fields.
     * @return the data fields
     */
    protected static MetisDataFieldSet getBaseFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Is this the trunk branch?
     * @return true/false
     */
    public boolean isTrunk() {
        return isTrunk;
    }

    /**
     * Is this a virtual branch?
     * @return true/false
     */
    public boolean isVirtual() {
        return isVirtual;
    }

    /**
     * Get the tag list for this branch.
     * @return the tag list
     */
    public ScmTagList<?, B, C, R> getTagList() {
        return theTags;
    }

    /**
     * Get Component.
     * @return the component
     */
    public C getComponent() {
        return theComponent;
    }

    /**
     * Get Component Branches.
     * @return the component branch list
     */
    @SuppressWarnings("unchecked")
    public ScmBranchList<B, C, R> getComponentBranchList() {
        return (ScmBranchList<B, C, R>) theComponent.getBranches();
    }

    /**
     * Get Project Definition.
     * @return the project definition
     */
    public ThemisMvnProjectDefinition getProjectDefinition() {
        return theProject;
    }

    /**
     * Obtain the major version.
     * @return the major version
     */
    protected int getMajorVersion() {
        return theMajorVersion;
    }

    /**
     * Obtain the major version.
     * @return the major version
     */
    protected int getMinorVersion() {
        return theMinorVersion;
    }

    /**
     * Obtain the delta version.
     * @return the delta version
     */
    protected int getDeltaVersion() {
        return theDeltaVersion;
    }

    /**
     * Set the trunk indication.
     */
    protected void setTrunk() {
        isTrunk = true;
    }

    /**
     * Set the virtual indication.
     */
    protected void setVirtual() {
        isVirtual = true;
    }

    /**
     * Set the tag list.
     * @param pTags the tag list
     */
    protected void setTags(final ScmTagList<?, B, C, R> pTags) {
        theTags = pTags;
    }

    /**
     * Set Project Definition.
     * @param pProject the project definition
     */
    protected void setProjectDefinition(final ThemisMvnProjectDefinition pProject) {
        theProject = pProject;
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

    @Override
    public int compareTo(final B pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare versions numbers */
        if (theMajorVersion < pThat.getMajorVersion()) {
            return -1;
        }
        if (theMajorVersion > pThat.getMajorVersion()) {
            return 1;
        }
        if (theMinorVersion < pThat.getMinorVersion()) {
            return -1;
        }
        if (theMinorVersion > pThat.getMinorVersion()) {
            return 1;
        }
        if (theDeltaVersion < pThat.getDeltaVersion()) {
            return -1;
        }
        if (theDeltaVersion > pThat.getDeltaVersion()) {
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
        if (!(pThat instanceof ThemisScmBranch)) {
            return false;
        }
        ThemisScmBranch<?, ?, ?> myThat = (ThemisScmBranch<?, ?, ?>) pThat;

        /* Compare fields */
        if (theMajorVersion != myThat.getMajorVersion()) {
            return false;
        }
        if (theMinorVersion != myThat.getMinorVersion()) {
            return false;
        }
        return theDeltaVersion == myThat.getDeltaVersion();
    }

    @Override
    public int hashCode() {
        return (theComponent.hashCode() * ThemisScmRepository.HASH_PRIME) + getVersionHash();
    }

    /**
     * Obtain hash of version #.
     * @return the version hash
     */
    private int getVersionHash() {
        int myVers = theMajorVersion * VERSION_SHIFT;
        myVers += theMinorVersion;
        myVers *= VERSION_SHIFT;
        return myVers + theDeltaVersion;
    }

    /**
     * Determine next major branch.
     * @return the next major branch
     */
    public B nextMajorBranch() {
        /* Determine the next major branch */
        ScmBranchList<B, C, R> myBranches = getComponentBranchList();
        return myBranches.nextMajorBranch();
    }

    /**
     * Determine next minor branch.
     * @return the next minor branch
     */
    public B nextMinorBranch() {
        /* Determine the next major branch */
        ScmBranchList<B, C, R> myBranches = getComponentBranchList();
        return myBranches.nextMinorBranch(this);
    }

    /**
     * Determine next delta branch.
     * @return the next delta branch
     */
    public B nextDeltaBranch() {
        /* Determine the next major branch */
        ScmBranchList<B, C, R> myBranches = getComponentBranchList();
        return myBranches.nextDeltaBranch(this);
    }

    /**
     * Determine next tag.
     * @return the next tag
     */
    public ThemisScmTag<?, B, C, R> nextTag() {
        /* Determine the next tag */
        return theTags.nextTag();
    }

    /**
     * List of branches.
     * @param <B> the branch data type
     * @param <C> the component data type
     * @param <R> the repository data type
     */
    public abstract static class ScmBranchList<B extends ThemisScmBranch<B, C, R>, C extends ThemisScmComponent<C, R>, R extends ThemisScmRepository<R>>
            implements MetisDataFieldItem, MetisDataList<B> {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(ScmBranchList.class);

        /**
         * Size field id.
         */
        private static final MetisDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Branch List.
         */
        private final List<B> theList;

        /**
         * The parent component.
         */
        private final C theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected ScmBranchList(final C pParent) {
            /* Store parent for use by entry handler */
            theComponent = pParent;
            theList = new ArrayList<>();
        }

        @Override
        public List<B> getUnderlyingList() {
            return theList;
        }

        @Override
        public String toString() {
            return getDataFieldSet().getName();
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }

            /* Unknown */
            return MetisDataFieldValue.UNKNOWN;
        }

        /**
         * Obtain the data fields.
         * @return the data fields
         */
        protected static MetisDataFieldSet getBaseFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Locate branch.
         * @param pBranch the branch
         * @return the relevant branch or Null
         */
        public B locateBranch(final B pBranch) {
            /* Loop through the entries */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myBranch = myIterator.next();

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
        protected B locateBranch(final String pVersion) {
            /* Loop through the entries */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myBranch = myIterator.next();

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
         * Locate Trunk.
         * @return the trunk branch or Null
         */
        protected B locateTrunk() {
            /* Loop through the entries */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myBranch = myIterator.next();

                /* If this is the correct branch */
                if (myBranch.isTrunk()) {
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
        protected ThemisScmTag<?, B, C, R> locateTag(final String pVersion,
                                                     final int pTag) {
            /* Loop through the entries */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myBranch = myIterator.next();

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
        public B nextBranch(final B pBase,
                            final ScmBranchOpType pBranchType) {
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
        private B nextMajorBranch() {
            /* Loop to the last entry */
            B myBranch = null;
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                myBranch = myIterator.next();
            }

            /* Determine the largest current major version */
            int myMajor = (myBranch == null)
                                             ? 0
                                             : myBranch.getMajorVersion();

            /* Create the major revision */
            return createNewBranch(theComponent, myMajor + 1, 0, 0);
        }

        /**
         * Determine next minor branch.
         * @param pBase the branch to base from
         * @return the minor branch
         */
        private B nextMinorBranch(final ThemisScmBranch<?, C, R> pBase) {
            /* Access major version */
            int myMajor = pBase.getMajorVersion();

            /* Access list iterator */
            B myBranch = null;

            /* Loop to the last entry */
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myTest = myIterator.next();

                /* Handle wrong major version */
                if (myTest.getMajorVersion() > myMajor) {
                    break;
                }
                if (myTest.getMajorVersion() < myMajor) {
                    continue;
                }

                /* Record branch */
                myBranch = myTest;
            }

            /* Determine the largest current minor version */
            int myMinor = (myBranch == null)
                                             ? 0
                                             : myBranch.getMinorVersion();

            /* Create the minor revision */
            return createNewBranch(theComponent, myMajor, myMinor + 1, 0);
        }

        /**
         * Determine next delta branch.
         * @param pBase the branch to base from
         * @return the delta branch
         */
        private B nextDeltaBranch(final ThemisScmBranch<?, C, R> pBase) {
            /* Access major/minor version */
            int myMajor = pBase.getMajorVersion();
            int myMinor = pBase.getMinorVersion();

            /* Loop to the last entry */
            B myBranch = null;
            Iterator<B> myIterator = iterator();
            while (myIterator.hasNext()) {
                B myTest = myIterator.next();

                /* Handle wrong major/minor version */
                if (myTest.getMajorVersion() > myMajor) {
                    break;
                }
                if (myTest.getMajorVersion() < myMajor) {
                    continue;
                }
                if (myTest.getMinorVersion() > myMinor) {
                    break;
                }
                if (myTest.getMinorVersion() < myMinor) {
                    continue;
                }

                /* Record branch */
                myBranch = myTest;
            }

            /* Determine the largest current revision */
            int myDelta = (myBranch == null)
                                             ? 0
                                             : myBranch.getDeltaVersion();

            /* Create the delta revision */
            return createNewBranch(theComponent, myMajor, myMinor, myDelta + 1);
        }

        /**
         * Create new Branch.
         * @param pComponent the component
         * @param pMajor the major version
         * @param pMinor the minor version
         * @param pDelta the delta version
         * @return the new branch
         */
        protected abstract B createNewBranch(C pComponent,
                                             int pMajor,
                                             int pMinor,
                                             int pDelta);
    }

    /**
     * Branch operation.
     */
    public enum ScmBranchOpType {
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
