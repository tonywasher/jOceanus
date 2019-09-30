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
package net.sourceforge.joceanus.jthemis.scm.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmTag.ThemisScmTagList;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public abstract class ThemisScmBranch
        implements MetisFieldItem, ThemisScmOwner, Comparable<ThemisScmBranch> {
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
    private static final MetisFieldSet<ThemisScmBranch> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmBranch.class);

    /*
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_NAME, ThemisScmBranch::getName);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT, ThemisScmBranch::getComponent);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_PROJECT, ThemisScmBranch::getProjectDefinition);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_VIRTUAL, ThemisScmBranch::isVirtual);
    }

    /**
     * Component.
     */
    private final ThemisScmComponent theComponent;

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
    private ThemisScmTagList theTags;

    /**
     * The branch name.
     */
    private String theName;

    /**
     * Is this the trunk branch.
     */
    private boolean isTrunk;

    /**
     * Is this a virtual branch.
     */
    private boolean isVirtual;

    /**
     * Is this a non-standard branch.
     */
    private boolean isNonStd;

    /**
     * The project definition.
     */
    private ThemisMvnProjectDefinition theProject;

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    protected ThemisScmBranch(final ThemisScmComponent pParent,
                              final String pVersion) {
        /* Store the component */
        theComponent = pParent;

        /* Parse the version */
        final String[] myParts = pVersion.split("\\" + BRANCH_SEP);

        /* If we do not have three parts reject it */
        if (myParts.length != NUM_VERS_PARTS) {
            isNonStd = true;
            theName = pVersion;
            theMajorVersion = -1;
            theMinorVersion = -1;
            theDeltaVersion = -1;
        } else {
            /* Determine values */
            theMajorVersion = Integer.parseInt(myParts[0]);
            theMinorVersion = Integer.parseInt(myParts[1]);
            theDeltaVersion = Integer.parseInt(myParts[2]);
        }
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pDelta the delta version
     */
    protected ThemisScmBranch(final ThemisScmComponent pParent,
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
        return getName();
    }

    @Override
    public String getBranchName() {
        return getName();
    }

    @Override
    public boolean isBranch() {
        return true;
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
    public Boolean isVirtual() {
        return isVirtual;
    }

    /**
     * Get the tag list for this branch.
     * @return the tag list
     */
    public ThemisScmTagList getTagList() {
        return theTags;
    }

    /**
     * Get Component.
     * @return the component
     */
    public ThemisScmComponent getComponent() {
        return theComponent;
    }

    /**
     * Get Component Branches.
     * @return the component branch list
     */
    public ThemisScmBranchList getComponentBranchList() {
        return theComponent.getBranches();
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
    protected void setTags(final ThemisScmTagList pTags) {
        theTags = pTags;
    }

    /**
     * Set Project Definition.
     * @param pProject the project definition
     */
    protected void setProjectDefinition(final ThemisMvnProjectDefinition pProject) {
        theProject = pProject;
    }

    @Override
    public String getName() {
        /* Return previously formatted name */
        if (theName == null) {
            /* Build the underlying string */
            final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the version directory */
            myBuilder.append(BRANCH_PREFIX)
                    .append(theMajorVersion)
                    .append(BRANCH_SEP)
                    .append(theMinorVersion)
                    .append(BRANCH_SEP)
                    .append(theDeltaVersion);
            theName = myBuilder.toString();
        }

        /* Return the branch name */
        return theName;
    }

    /**
     * Obtain the base name.
     * @return the baseName
     */
    public String getBaseName() {
        /* Strip off branch prefix */
        return getName().substring(BRANCH_PREFIX.length());
    }

    @Override
    public int compareTo(final ThemisScmBranch pThat) {
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
        final ThemisScmBranch myThat = (ThemisScmBranch) pThat;

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
        return theComponent.hashCode() * ThemisScmRepository.HASH_PRIME
               + getVersionHash();
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
    public ThemisScmBranch nextMajorBranch() {
        /* Determine the next major branch */
        final ThemisScmBranchList myBranches = getComponentBranchList();
        return myBranches.nextMajorBranch();
    }

    /**
     * Determine next minor branch.
     * @return the next minor branch
     */
    public ThemisScmBranch nextMinorBranch() {
        /* Determine the next major branch */
        final ThemisScmBranchList myBranches = getComponentBranchList();
        return myBranches.nextMinorBranch(this);
    }

    /**
     * Determine next delta branch.
     * @return the next delta branch
     */
    public ThemisScmBranch nextDeltaBranch() {
        /* Determine the next major branch */
        final ThemisScmBranchList myBranches = getComponentBranchList();
        return myBranches.nextDeltaBranch(this);
    }

    /**
     * Determine next tag.
     * @return the next tag
     */
    public ThemisScmTag nextTag() {
        /* Determine the next tag */
        return theTags.nextTag();
    }

    /**
     * List of branches.
     */
    public abstract static class ThemisScmBranchList
            implements MetisFieldItem, MetisDataList<ThemisScmBranch> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisScmBranchList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmBranchList.class);

        /*
         * Size field id.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisScmBranchList::size);
        }

        /**
         * Branch List.
         */
        private final List<ThemisScmBranch> theList;

        /**
         * The parent component.
         */
        private final ThemisScmComponent theComponent;

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected ThemisScmBranchList(final ThemisScmComponent pParent) {
            /* Store parent for use by entry handler */
            theComponent = pParent;
            theList = new ArrayList<>();
        }

        @Override
        public List<ThemisScmBranch> getUnderlyingList() {
            return theList;
        }

        @Override
        public String toString() {
            return getDataFieldSet().getName();
        }

        /**
         * Locate branch.
         * @param pBranch the branch
         * @return the relevant branch or Null
         */
        public ThemisScmBranch locateBranch(final ThemisScmBranch pBranch) {
            /* Loop through the entries */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myBranch = myIterator.next();

                /* If this is the correct branch */
                final int iCompare = myBranch.compareTo(pBranch);
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
        protected ThemisScmBranch locateBranch(final String pVersion) {
            /* Loop through the entries */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myBranch = myIterator.next();

                /* If this is the correct branch */
                if (pVersion.equals(myBranch.getName())) {
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
        protected ThemisScmBranch locateTrunk() {
            /* Loop through the entries */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myBranch = myIterator.next();

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
        protected ThemisScmTag locateTag(final String pVersion,
                                         final int pTag) {
            /* Loop through the entries */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myBranch = myIterator.next();

                /* If this is the correct branch */
                if (pVersion.equals(myBranch.getName())) {
                    /* Search in this branches tags */
                    return myBranch.getTagList().locateTag(pTag);
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate Tag.
         * @param pOwner the owner to locate
         * @return the relevant tag or Null
         */
        protected ThemisScmTag locateTag(final ThemisScmOwner pOwner) {
            /* Loop through the entries */
            final String myName = pOwner.getBranchName();
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myBranch = myIterator.next();

                /* If this is the correct branch */
                if (myName.equals(myBranch.getName())) {
                    /* Search in this branches tags */
                    return myBranch.getTagList().locateTag(pOwner);
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
        public ThemisScmBranch nextBranch(final ThemisScmBranch pBase,
                                          final ThemisScmBranchIncrement pBranchType) {
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
        ThemisScmBranch nextMajorBranch() {
            /* Loop to the last entry */
            ThemisScmBranch myBranch = null;
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the next branch */
                myBranch = myIterator.next();
            }

            /* Determine the largest current major version */
            final int myMajor = myBranch == null
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
        ThemisScmBranch nextMinorBranch(final ThemisScmBranch pBase) {
            /* Access major version */
            final int myMajor = pBase.getMajorVersion();

            /* Access list iterator */
            ThemisScmBranch myBranch = null;

            /* Loop to the last entry */
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myTest = myIterator.next();

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
            final int myMinor = myBranch == null
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
        ThemisScmBranch nextDeltaBranch(final ThemisScmBranch pBase) {
            /* Access major/minor version */
            final int myMajor = pBase.getMajorVersion();
            final int myMinor = pBase.getMinorVersion();

            /* Loop to the last entry */
            ThemisScmBranch myBranch = null;
            final Iterator<ThemisScmBranch> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmBranch myTest = myIterator.next();

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
            final int myDelta = myBranch == null
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
        protected abstract ThemisScmBranch createNewBranch(ThemisScmComponent pComponent,
                                                           int pMajor,
                                                           int pMinor,
                                                           int pDelta);
    }

    /**
     * Branch operation.
     */
    public enum ThemisScmBranchIncrement {
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
