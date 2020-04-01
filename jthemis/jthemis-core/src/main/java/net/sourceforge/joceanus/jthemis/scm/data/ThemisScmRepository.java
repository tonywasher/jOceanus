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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataMap;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent.ThemisScmComponentList;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectId;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public abstract class ThemisScmRepository
        implements MetisFieldItem, Comparable<ThemisScmRepository> {
    /**
     * The Hash Prime.
     */
    public static final int HASH_PRIME = 17;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisScmRepository> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisScmRepository.class);

    /*
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_NAME, ThemisScmRepository::getName);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_BRANCHES, ThemisScmRepository::getBranches);
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_TAGS, ThemisScmRepository::getTags);
    }

    /**
     * The Preference Manager.
     */
    private final MetisPreferenceManager thePreferenceMgr;

    /**
     * Repository Base.
     */
    private String theBase;

    /**
     * Repository Name.
     */
    private String theName;

    /**
     * ComponentList.
     */
    private ThemisScmComponentList theComponents;

    /**
     * Branch Map.
     */
    private final ThemisBranchMap theBranchMap;

    /**
     * Tag Map.
     */
    private final ThemisTagMap theTagMap;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     */
    public ThemisScmRepository(final MetisPreferenceManager pPreferenceMgr) {
        /* Store the preference manager */
        thePreferenceMgr = pPreferenceMgr;

        /* Allocate the maps */
        theBranchMap = new ThemisBranchMap();
        theTagMap = new ThemisTagMap();
    }

    /**
     * Obtain the repository base.
     * @return the name
     */
    public String getBase() {
        return theBase;
    }

    /**
     * Obtain the repository name.
     * @return the name
     */
    public String getName() {
        return theName;
    }

    /**
     * Obtain the preference manager.
     * @return the preference manager
     */
    public MetisPreferenceManager getPreferenceMgr() {
        return thePreferenceMgr;
    }

    /**
     * Get the component list for this repository.
     * @return the component list
     */
    public ThemisScmComponentList getComponents() {
        return theComponents;
    }

    /**
     * Get the branch map list for this repository.
     * @return the branch map
     */
    private ThemisBranchMap getBranches() {
        return theBranchMap;
    }

    /**
     * Get the tag map for this repository.
     * @return the tag map
     */
    private ThemisTagMap getTags() {
        return theTagMap;
    }

    /**
     * Set the base.
     * @param pBase the repository base
     */
    protected void setBase(final String pBase) {
        theBase = pBase;
    }

    /**
     * Set the name.
     * @param pName the repository name
     */
    protected void setName(final String pName) {
        theName = pName;
    }

    /**
     * Set the component list.
     * @param pComponents the component list
     */
    protected void setComponents(final ThemisScmComponentList pComponents) {
        theComponents = pComponents;
    }

    @Override
    public int compareTo(final ThemisScmRepository pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare bases */
        final int iResult = theBase.compareTo(pThat.theBase);
        if (iResult != 0) {
            return iResult;
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
        if (!(pThat instanceof ThemisScmRepository)) {
            return false;
        }
        final ThemisScmRepository myThat = (ThemisScmRepository) pThat;

        /* Compare fields */
        if (!theBase.equals(myThat.theBase)) {
            return false;
        }
        return theName.equals(myThat.getName());
    }

    @Override
    public int hashCode() {
        return theBase.hashCode() * HASH_PRIME
               +  theName.hashCode();
    }

    /**
     * Locate Component.
     * @param pName the component to locate
     * @return the relevant component or Null
     */
    public ThemisScmComponent locateComponent(final String pName) {
        /* Locate component in component list */
        return theComponents.locateComponent(pName);
    }

    /**
     * Locate Branch.
     * @param pComponent the component to locate
     * @param pVersion the version to locate
     * @return the relevant branch or Null
     */
    public ThemisScmBranch locateBranch(final String pComponent,
                                        final String pVersion) {
        /* Locate branch in component list */
        return theComponents.locateBranch(pComponent, pVersion);
    }

    /**
     * Locate Branch.
     * @param pComponent the component to locate
     * @param pVersion the version to locate
     * @param pTag the tag to locate
     * @return the relevant branch or Null
     */
    public ThemisScmTag locateTag(final String pComponent,
                                  final String pVersion,
                                  final int pTag) {
        /* Locate Tag in component list */
        return theComponents.locateTag(pComponent, pVersion, pTag);
    }

    /**
     * Locate branch.
     * @param pId the project id for the branch
     * @return the branch
     */
    protected ThemisScmBranch locateBranch(final ThemisMvnProjectId pId) {
        /* Lookup mapping */
        return theBranchMap.get(pId);
    }

    /**
     * Locate tag.
     * @param pId the project id for the tag
     * @return the tag
     */
    protected ThemisScmTag locateTag(final ThemisMvnProjectId pId) {
        /* Lookup mapping */
        return theTagMap.get(pId);
    }

    /**
     * Register branch.
     * @param pId the project id for the branch
     * @param pBranch the branch
     */
    public void registerBranch(final ThemisMvnProjectId pId,
                               final ThemisScmBranch pBranch) {
        /* Store mapping */
        theBranchMap.put(pId, pBranch);
    }

    /**
     * Register tag.
     * @param pId the project id for the branch
     * @param pTag the tag
     */
    public void registerTag(final ThemisMvnProjectId pId,
                            final ThemisScmTag pTag) {
        /* Store mapping */
        theTagMap.put(pId, pTag);
    }

    /**
     * Branch Map.
     */
    private final class ThemisBranchMap
            implements MetisDataObjectFormat, MetisDataMap<ThemisMvnProjectId, ThemisScmBranch> {
        /**
         * The map.
         */
        private final Map<ThemisMvnProjectId, ThemisScmBranch> theMap;

        /**
         * Constructor.
         */
        ThemisBranchMap() {
            theMap = new HashMap<>();
        }

        @Override
        public Map<ThemisMvnProjectId, ThemisScmBranch> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }

    /**
     * Tag Map.
     */
    private final class ThemisTagMap
            implements MetisDataObjectFormat, MetisDataMap<ThemisMvnProjectId, ThemisScmTag> {
        /**
         * The map.
         */
        private final Map<ThemisMvnProjectId, ThemisScmTag> theMap;

        /**
         * Constructor.
         */
        ThemisTagMap() {
            theMap = new HashMap<>();
        }

        @Override
        public Map<ThemisMvnProjectId, ThemisScmTag> getUnderlyingMap() {
            return theMap;
        }

        @Override
        public String toString() {
            return getClass().getSimpleName();
        }
    }
}
