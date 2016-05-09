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
package net.sourceforge.joceanus.jthemis.scm.data;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedList;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch.ScmBranchList;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 * @param <C> the component data type
 * @param <R> the repository data type
 */
public abstract class ThemisScmComponent<C extends ThemisScmComponent<C, R>, R extends ThemisScmRepository<R>>
        implements MetisDataContents, Comparable<C> {
    /**
     * Report fields.
     */
    protected static final MetisFields FIELD_DEFS = new MetisFields(ThemisScmComponent.class.getSimpleName());

    /**
     * Repository field id.
     */
    private static final MetisField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Name field id.
     */
    private static final MetisField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Branches field id.
     */
    private static final MetisField FIELD_BRAN = FIELD_DEFS.declareLocalField("Branches");

    /**
     * Parent Repository.
     */
    private final R theRepository;

    /**
     * Component Name.
     */
    private final String theName;

    /**
     * BranchList.
     */
    private ScmBranchList<?, C, R> theBranches;

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     */
    protected ThemisScmComponent(final R pParent,
                                 final String pName) {
        /* Store values */
        theName = pName;
        theRepository = pParent;
    }

    @Override
    public String formatObject() {
        return theName;
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_REPO.equals(pField)) {
            return theRepository;
        }
        if (FIELD_NAME.equals(pField)) {
            return theName;
        }
        if (FIELD_BRAN.equals(pField)) {
            return theBranches;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Get the repository for this component.
     * @return the repository
     */
    public R getRepository() {
        return theRepository;
    }

    /**
     * Obtain the component name.
     * @return the component name
     */
    public String getName() {
        return theName;
    }

    /**
     * Get the branch list for this component.
     * @return the branch list
     */
    public ScmBranchList<?, C, R> getBranches() {
        return theBranches;
    }

    /**
     * Set the branch list.
     * @param pBranches the branch list
     */
    protected void setBranches(final ScmBranchList<?, C, R> pBranches) {
        theBranches = pBranches;
    }

    @Override
    public int compareTo(final C pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the repositories */
        iCompare = theRepository.compareTo(pThat.getRepository());
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare names */
        return theName.compareTo(pThat.getName());
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
        if (!(pThat instanceof ThemisScmComponent)) {
            return false;
        }
        ThemisScmComponent<?, ?> myThat = (ThemisScmComponent<?, ?>) pThat;

        /* Compare fields */
        if (!theRepository.equals(myThat.getRepository())) {
            return false;
        }
        return theName.equals(myThat.getName());
    }

    @Override
    public int hashCode() {
        return (theRepository.hashCode() * ThemisScmRepository.HASH_PRIME) + theName.hashCode();
    }

    /**
     * List of components.
     * @param <C> the component type
     * @param <R> the repository data type
     */
    public abstract static class ScmComponentList<C extends ThemisScmComponent<C, R>, R extends ThemisScmRepository<R>>
            extends MetisOrderedList<C>
            implements MetisDataContents {
        /**
         * Report fields.
         */
        protected static final MetisFields FIELD_DEFS = new MetisFields(ScmComponentList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Constructor.
         * @param pClazz the class of the component
         */
        public ScmComponentList(final Class<C> pClazz) {
            /* Call super constructor */
            super(pClazz);
        }

        @Override
        public String formatObject() {
            return "ComponentList(" + size() + ")";
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
         * Locate Component.
         * @param pName the component to locate
         * @return the relevant component or Null
         */
        protected C locateComponent(final String pName) {
            /* Loop through the entries */
            Iterator<C> myIterator = iterator();
            while (myIterator.hasNext()) {
                C myComponent = myIterator.next();

                /* If this is the correct component */
                if (pName.equals(myComponent.getName())) {
                    /* Return the component */
                    return myComponent;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate Branch.
         * @param pComponent the component to locate
         * @param pVersion the version to locate
         * @return the relevant branch or Null
         */
        protected ThemisScmBranch<?, C, R> locateBranch(final String pComponent,
                                                        final String pVersion) {
            /* While we have entries */
            Iterator<C> myIterator = iterator();
            while (myIterator.hasNext()) {
                C myComponent = myIterator.next();

                /* If this is the correct component */
                if (pComponent.equals(myComponent.getName())) {
                    /* Search in this components branches */
                    return myComponent.getBranches().locateBranch(pVersion);
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate Tag.
         * @param pComponent the component to locate
         * @param pVersion the version to locate
         * @param pTag the tag to locate
         * @return the relevant tag or Null
         */
        protected ThemisScmTag<?, ?, C, R> locateTag(final String pComponent,
                                                     final String pVersion,
                                                     final int pTag) {
            /* While we have entries */
            Iterator<C> myIterator = iterator();
            while (myIterator.hasNext()) {
                C myComponent = myIterator.next();

                /* If this is the correct component */
                if (pComponent.equals(myComponent.getName())) {
                    /* Search in this components branches */
                    return myComponent.getBranches().locateTag(pVersion, pTag);
                }
            }

            /* Not found */
            return null;
        }
    }
}
