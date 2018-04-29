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
package net.sourceforge.joceanus.jthemis.scm.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Core representation of a tag.
 */
public abstract class ThemisScmTag
        implements MetisFieldItem, Comparable<ThemisScmTag> {
    /**
     * The tag prefix.
     */
    public static final String PREFIX_TAG = "-b";

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisScmTag> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmTag.class);

    /**
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_BRANCH, ThemisScmTag::getBranch);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_NAME, ThemisScmTag::getTagName);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_PROJECT, ThemisScmTag::getProjectDefinition);
    }

    /**
     * The Branch to which this Tag belongs.
     */
    private final ThemisScmBranch theBranch;

    /**
     * The Tag number.
     */
    private final int theTag;

    /**
     * The project definition.
     */
    private ThemisMvnProjectDefinition theProject;

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     */
    protected ThemisScmTag(final ThemisScmBranch pParent,
                           final int pTag) {
        /* Store values */
        theBranch = pParent;
        theTag = pTag;
    }

    @Override
    public String toString() {
        return getTagName();
    }

    /**
     * Get the tag name for this tag.
     * @return the tag name
     */
    public String getTagName() {
        return theBranch.getBranchName() + PREFIX_TAG + theTag;
    }

    /**
     * Get the branch for this tag.
     * @return the branch
     */
    public ThemisScmBranch getBranch() {
        return theBranch;
    }

    /**
     * Get the tag number for this tag.
     * @return the tag
     */
    public int getTagNo() {
        return theTag;
    }

    /**
     * Get Project Definition.
     * @return the project definition
     */
    public ThemisMvnProjectDefinition getProjectDefinition() {
        return theProject;
    }

    /**
     * Set Project Definition.
     * @param pProject the project definition
     */
    protected void setProjectDefinition(final ThemisMvnProjectDefinition pProject) {
        theProject = pProject;
    }

    @Override
    public int compareTo(final ThemisScmTag pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the branches */
        final int iCompare = theBranch.compareTo(pThat.getBranch());
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare tag numbers */
        if (theTag < pThat.getTagNo()) {
            return -1;
        }
        if (theTag > pThat.getTagNo()) {
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
        if (!(pThat instanceof ThemisScmTag)) {
            return false;
        }
        final ThemisScmTag myThat = (ThemisScmTag) pThat;

        /* Compare fields */
        if (!theBranch.equals(myThat.getBranch())) {
            return false;
        }
        return theTag == myThat.getTagNo();
    }

    @Override
    public int hashCode() {
        return theBranch.hashCode() * ThemisScmRepository.HASH_PRIME
               + theTag;
    }

    /**
     * List of tags.
     */
    public abstract static class ThemisScmTagList
            implements MetisFieldItem, MetisDataList<ThemisScmTag> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisScmTagList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmTagList.class);

        /**
         * fieldIds.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisScmTagList::size);
        }

        /**
         * Tag List.
         */
        private final List<ThemisScmTag> theList;

        /**
         * The parent branch.
         */
        private final ThemisScmBranch theBranch;

        /**
         * The prefix.
         */
        private final String thePrefix;

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected ThemisScmTagList(final ThemisScmBranch pParent) {
            /* Store parent for use by entry handler */
            theBranch = pParent;
            theList = new ArrayList<>();

            /* Build prefix */
            thePrefix = (pParent == null)
                                          ? null
                                          : theBranch.getBranchName() + PREFIX_TAG;
        }

        @Override
        public List<ThemisScmTag> getUnderlyingList() {
            return theList;
        }

        @Override
        public String toString() {
            return getDataFieldSet().getName();
        }

        /**
         * Get the parent branch.
         * @return the branch
         */
        public ThemisScmBranch getBranch() {
            return theBranch;
        }

        /**
         * Get the prefix.
         * @return the prefix
         */
        public String getPrefix() {
            return thePrefix;
        }

        /**
         * Locate tag.
         * @param pTag the tag
         * @return the relevant tag or Null
         */
        public ThemisScmTag locateTag(final ThemisScmTag pTag) {
            /* Loop through the entries */
            final Iterator<ThemisScmTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmTag myTag = myIterator.next();

                /* If this is the correct tag */
                final int iCompare = myTag.compareTo(pTag);
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
        protected ThemisScmTag locateTag(final int pTag) {
            /* Loop through the entries */
            final Iterator<ThemisScmTag> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmTag myTag = myIterator.next();

                /* If this is the correct tag */
                if (pTag == myTag.getTagNo()) {
                    /* return the tag */
                    return myTag;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Determine latest tag.
         * @return the latest tag
         */
        public ThemisScmTag latestTag() {
            /* Declare default */
            ThemisScmTag myTag = null;

            /* Loop to the last entry */
            final Iterator<ThemisScmTag> myIterator = iterator();
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
        public ThemisScmTag nextTag() {
            /* Access latest tag */
            final ThemisScmTag myTag = latestTag();

            /* Determine the largest current tag */
            final int myTagNo = myTag == null
                                              ? 0
                                              : myTag.getTagNo();

            /* Create the tag */
            return createNewTag(theBranch, myTagNo + 1);
        }

        /**
         * Create new Tag.
         * @param pBranch the branch
         * @param pTag the tag number
         * @return the new tag
         */
        protected abstract ThemisScmTag createNewTag(ThemisScmBranch pBranch,
                                                     int pTag);
    }
}
