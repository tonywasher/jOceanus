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

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmComponent.ScmComponentList;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectId;

/**
 * Represents a repository.
 * @author Tony Washer
 * @param <R> the data type
 */
public abstract class ScmRepository<R extends ScmRepository<R>>
        implements JDataContents, Comparable<R> {
    /**
     * The Hash Prime.
     */
    public static final int HASH_PRIME = 17;

    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(ScmRepository.class.getSimpleName());

    /**
     * Name field id.
     */
    private static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Components field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareLocalField("Components");

    /**
     * BranchMap field id.
     */
    private static final JDataField FIELD_BRNMAP = FIELD_DEFS.declareLocalField("BranchMap");

    /**
     * TagMap field id.
     */
    private static final JDataField FIELD_TAGMAP = FIELD_DEFS.declareLocalField("TagMap");

    /**
     * The Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * Repository Name.
     */
    private String theName;

    /**
     * ComponentList.
     */
    private ScmComponentList<?, R> theComponents;

    /**
     * Branch Map.
     */
    private final Map<MvnProjectId, ScmBranch<?, ?, R>> theBranchMap;

    /**
     * Tag Map.
     */
    private final Map<MvnProjectId, ScmTag<?, ?, ?, R>> theTagMap;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @throws JOceanusException on error
     */
    public ScmRepository(final PreferenceManager pPreferenceMgr) throws JOceanusException {
        /* Store the preference manager */
        thePreferenceMgr = pPreferenceMgr;

        /* Allocate the maps */
        theBranchMap = new HashMap<MvnProjectId, ScmBranch<?, ?, R>>();
        theTagMap = new HashMap<MvnProjectId, ScmTag<?, ?, ?, R>>();
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_NAME.equals(pField)) {
            return theName;
        }
        if (FIELD_COMP.equals(pField)) {
            return theComponents;
        }
        if (FIELD_BRNMAP.equals(pField)) {
            return theBranchMap;
        }
        if (FIELD_TAGMAP.equals(pField)) {
            return theTagMap;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
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
    public PreferenceManager getPreferenceMgr() {
        return thePreferenceMgr;
    }

    /**
     * Get the component list for this repository.
     * @return the component list
     */
    public ScmComponentList<?, R> getComponents() {
        return theComponents;
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
    protected void setComponents(final ScmComponentList<?, R> pComponents) {
        theComponents = pComponents;
    }

    @Override
    public int compareTo(final R pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
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
        if (!(pThat instanceof ScmRepository)) {
            return false;
        }
        ScmRepository<?> myThat = (ScmRepository<?>) pThat;

        /* Compare fields */
        return theName.equals(myThat.getName());
    }

    @Override
    public int hashCode() {
        return theName.hashCode();
    }

    /**
     * Locate Component.
     * @param pName the component to locate
     * @return the relevant component or Null
     */
    public ScmComponent<?, R> locateComponent(final String pName) {
        /* Locate component in component list */
        return theComponents.locateComponent(pName);
    }

    /**
     * Locate Branch.
     * @param pComponent the component to locate
     * @param pVersion the version to locate
     * @return the relevant branch or Null
     */
    public ScmBranch<?, ?, R> locateBranch(final String pComponent,
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
    public ScmTag<?, ?, ?, R> locateTag(final String pComponent,
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
    protected ScmBranch<?, ?, R> locateBranch(final MvnProjectId pId) {
        /* Lookup mapping */
        return theBranchMap.get(pId);
    }

    /**
     * Locate tag.
     * @param pId the project id for the tag
     * @return the tag
     */
    protected ScmTag<?, ?, ?, R> locateTag(final MvnProjectId pId) {
        /* Lookup mapping */
        return theTagMap.get(pId);
    }

    /**
     * Register branch.
     * @param pId the project id for the branch
     * @param pBranch the branch
     */
    public void registerBranch(final MvnProjectId pId,
                               final ScmBranch<?, ?, R> pBranch) {
        /* Store mapping */
        theBranchMap.put(pId, pBranch);
    }

    /**
     * Register tag.
     * @param pId the project id for the branch
     * @param pTag the tag
     */
    public void registerTag(final MvnProjectId pId,
                            final ScmTag<?, ?, ?, R> pTag) {
        /* Store mapping */
        theTagMap.put(pId, pTag);
    }
}
