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
package net.sourceforge.joceanus.jthemis.scm.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedList;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;

/**
 * Core representation of a tag.
 * @param <T> the tag data type
 * @param <B> the branch data type
 * @param <C> the component data type
 * @param <R> the repository data type
 */
public abstract class ThemisScmTag<T extends ThemisScmTag<T, B, C, R>, B extends ThemisScmBranch<B, C, R>, C extends ThemisScmComponent<C, R>, R extends ThemisScmRepository<R>>
        implements MetisDataContents, Comparable<T> {
    /**
     * The tag prefix.
     */
    public static final String PREFIX_TAG = "-b";

    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(ThemisScmTag.class.getSimpleName());

    /**
     * Branch field id.
     */
    private static final MetisField FIELD_BRAN = FIELD_DEFS.declareEqualityField("Branch");

    /**
     * Name field id.
     */
    private static final MetisField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Project definition field id.
     */
    private static final MetisField FIELD_PROJECT = FIELD_DEFS.declareLocalField("Project");

    /**
     * The Branch to which this Tag belongs.
     */
    private final B theBranch;

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
    protected ThemisScmTag(final B pParent,
                           final int pTag) {
        /* Store values */
        theBranch = pParent;
        theTag = pTag;
    }

    @Override
    public String formatObject() {
        return getTagName();
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_BRAN.equals(pField)) {
            return theBranch;
        }
        if (FIELD_NAME.equals(pField)) {
            return getTagName();
        }
        if (FIELD_PROJECT.equals(pField)) {
            return theProject;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
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
    public B getBranch() {
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
    public int compareTo(final T pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the branches */
        iCompare = theBranch.compareTo(pThat.getBranch());
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
        ThemisScmTag<?, ?, ?, ?> myThat = (ThemisScmTag<?, ?, ?, ?>) pThat;

        /* Compare fields */
        if (!theBranch.equals(myThat.getBranch())) {
            return false;
        }
        return theTag == myThat.getTagNo();
    }

    @Override
    public int hashCode() {
        return (theBranch.hashCode() * ThemisScmRepository.HASH_PRIME) + theTag;
    }

    /**
     * List of tags.
     * @param <T> the tag data type
     * @param <B> the branch data type
     * @param <C> the component data type
     * @param <R> the repository data type
     */
    public abstract static class ScmTagList<T extends ThemisScmTag<T, B, C, R>, B extends ThemisScmBranch<B, C, R>, C extends ThemisScmComponent<C, R>, R extends ThemisScmRepository<R>>
            extends MetisOrderedList<T>
            implements MetisDataContents {
        /**
         * Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(ScmTagList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * The parent branch.
         */
        private final B theBranch;

        /**
         * The prefix.
         */
        private final String thePrefix;

        /**
         * Constructor.
         * @param pClazz the tag class
         * @param pParent the parent branch
         */
        protected ScmTagList(final Class<T> pClazz,
                             final B pParent) {
            /* Call super constructor */
            super(pClazz);

            /* Store parent for use by entry handler */
            theBranch = pParent;

            /* Build prefix */
            thePrefix = (pParent == null)
                                          ? null
                                          : theBranch.getBranchName() + PREFIX_TAG;
        }

        @Override
        public String formatObject() {
            return "TagList(" + size() + ")";
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }

            /* Unknown */
            return MetisFieldValue.UNKNOWN;
        }

        /**
         * Get the parent branch.
         * @return the branch
         */
        public B getBranch() {
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
        public T locateTag(final T pTag) {
            /* Loop through the entries */
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myTag = myIterator.next();

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
        protected T locateTag(final int pTag) {
            /* Loop through the entries */
            Iterator<T> myIterator = iterator();
            while (myIterator.hasNext()) {
                T myTag = myIterator.next();

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
        public T latestTag() {
            /* Declare default */
            T myTag = null;

            /* Loop to the last entry */
            Iterator<T> myIterator = iterator();
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
        public T nextTag() {
            /* Access latest tag */
            T myTag = latestTag();

            /* Determine the largest current tag */
            int myTagNo = (myTag == null)
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
        protected abstract T createNewTag(B pBranch,
                                          int pTag);
    }
}
