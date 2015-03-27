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

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.list.OrderedList;
import net.sourceforge.joceanus.jthemis.scm.data.ScmBranch.ScmBranchList;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 * @param <C> the component data type
 * @param <R> the repository data type
 */
public abstract class ScmComponent<C extends ScmComponent<C, R>, R extends ScmRepository<R>>
        implements JDataContents, Comparable<C> {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(ScmComponent.class.getSimpleName());

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Name field id.
     */
    private static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Branches field id.
     */
    private static final JDataField FIELD_BRAN = FIELD_DEFS.declareLocalField("Branches");

    @Override
    public String formatObject() {
        return theName;
    }

    @Override
    public String toString() {
        return formatObject();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
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
        return JDataFieldValue.UNKNOWN;
    }

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

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     */
    protected ScmComponent(final R pParent,
                           final String pName) {
        /* Store values */
        theName = pName;
        theRepository = pParent;
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
        if (!(pThat instanceof ScmComponent)) {
            return false;
        }
        ScmComponent<?, ?> myThat = (ScmComponent<?, ?>) pThat;

        /* Compare fields */
        if (!theRepository.equals(myThat.getRepository())) {
            return false;
        }
        return theName.equals(myThat.getName());
    }

    @Override
    public int hashCode() {
        return (theRepository.hashCode() * ScmRepository.HASH_PRIME) + theName.hashCode();
    }

    /**
     * List of components.
     * @param <C> the component type
     * @param <R> the repository data type
     */
    public abstract static class ScmComponentList<C extends ScmComponent<C, R>, R extends ScmRepository<R>>
            extends OrderedList<C>
            implements JDataContents {
        /**
         * Report fields.
         */
        protected static final JDataFields FIELD_DEFS = new JDataFields(ScmComponentList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return "ComponentList(" + size() + ")";
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
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * Constructor.
         * @param pClazz the class of the component
         */
        public ScmComponentList(final Class<C> pClazz) {
            /* Call super constructor */
            super(pClazz);
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
        protected ScmBranch<?, C, R> locateBranch(final String pComponent,
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
        protected ScmTag<?, ?, C, R> locateTag(final String pComponent,
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
