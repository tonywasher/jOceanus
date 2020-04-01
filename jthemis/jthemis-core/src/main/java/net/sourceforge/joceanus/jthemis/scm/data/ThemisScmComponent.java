/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2020 Tony Washer
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
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch.ThemisScmBranchList;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public abstract class ThemisScmComponent
        implements MetisFieldItem, Comparable<ThemisScmComponent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisScmComponent> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmComponent.class);

    /*
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_REPOSITORY, ThemisScmComponent::getRepository);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_NAME, ThemisScmComponent::getName);
    }

    /**
     * Parent Repository.
     */
    private final ThemisScmRepository theRepository;

    /**
     * Component Name.
     */
    private final String theName;

    /**
     * BranchList.
     */
    private ThemisScmBranchList theBranches;

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     */
    protected ThemisScmComponent(final ThemisScmRepository pParent,
                                 final String pName) {
        /* Store values */
        theName = pName;
        theRepository = pParent;
    }

    @Override
    public String toString() {
        return theName;
    }

    /**
     * Get the repository for this component.
     * @return the repository
     */
    public ThemisScmRepository getRepository() {
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
    public ThemisScmBranchList getBranches() {
        return theBranches;
    }

    /**
     * Set the branch list.
     * @param pBranches the branch list
     */
    protected void setBranches(final ThemisScmBranchList pBranches) {
        theBranches = pBranches;
    }

    @Override
    public int compareTo(final ThemisScmComponent pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the repositories */
        final int iCompare = theRepository.compareTo(pThat.getRepository());
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
        final ThemisScmComponent myThat = (ThemisScmComponent) pThat;

        /* Compare fields */
        if (!theRepository.equals(myThat.getRepository())) {
            return false;
        }
        return theName.equals(myThat.getName());
    }

    @Override
    public int hashCode() {
        return theRepository.hashCode() * ThemisScmRepository.HASH_PRIME
               + theName.hashCode();
    }

    /**
     * Locate Branch.
     * @param pOwner the owner to locate
     * @return the relevant branch or Null
     */
    public ThemisScmOwner locateOwner(final ThemisScmOwner pOwner) {
        /* If the owner is a branch */
        if (pOwner.isBranch()) {
            return theBranches.locateBranch(pOwner.getName());

            /* else must be a tag */
        } else {
            return theBranches.locateTag(pOwner);
        }
    }

    /**
     * List of components.
     */
    public abstract static class ThemisScmComponentList
            implements MetisFieldItem, MetisDataList<ThemisScmComponent> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisScmComponentList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmComponentList.class);

        /*
         * Size field id.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisScmComponentList::size);
        }

        /**
         * Component List.
         */
        private final List<ThemisScmComponent> theList;

        /**
         * Constructor.
         */
        public ThemisScmComponentList() {
            theList = new ArrayList<>();
        }

        @Override
        public List<ThemisScmComponent> getUnderlyingList() {
            return theList;
        }

        @Override
        public String toString() {
            return getDataFieldSet().getName();
        }

        /**
         * Locate Component.
         * @param pName the component to locate
         * @return the relevant component or Null
         */
        protected ThemisScmComponent locateComponent(final String pName) {
            /* Loop through the entries */
            final Iterator<ThemisScmComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmComponent myComponent = myIterator.next();

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
        protected ThemisScmBranch locateBranch(final String pComponent,
                                               final String pVersion) {
            /* While we have entries */
            final Iterator<ThemisScmComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmComponent myComponent = myIterator.next();

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
        protected ThemisScmTag locateTag(final String pComponent,
                                         final String pVersion,
                                         final int pTag) {
            /* While we have entries */
            final Iterator<ThemisScmComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisScmComponent myComponent = myIterator.next();

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
