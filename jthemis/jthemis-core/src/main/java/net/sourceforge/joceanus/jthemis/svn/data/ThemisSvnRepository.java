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
package net.sourceforge.joceanus.jthemis.svn.data;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.preference.MetisPreferenceManager;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.ThemisResource;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmRepository;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectId;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnComponent.ThemisSvnComponentList;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public class ThemisSvnRepository
        extends ThemisScmRepository<ThemisSvnRepository> {
    /**
     * URL separator character.
     */
    public static final char SEP_URL = '/';

    /**
     * URL prefix.
     */
    public static final String PFIX_URL = "svn";

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSvnRepository> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnRepository.class);

    /**
     * Base field id.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_BASE, ThemisSvnRepository::getBase);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_HISTORY, ThemisSvnRepository::getHistoryMap);
    }

    /**
     * The Preferences.
     */
    private final ThemisSvnPreferences thePreferences;

    /**
     * Repository Base.
     */
    private final String theBase;

    /**
     * The Client Manager.
     */
    private final ThemisSvnClientManager theClientMgrPool;

    /**
     * RevisionHistory Map.
     */
    private final ThemisSvnRevisionHistoryMap theRevisionHistory;

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pReport the report object
     * @throws OceanusException on error
     */
    public ThemisSvnRepository(final MetisPreferenceManager pPreferenceMgr,
                               final MetisThreadStatusReport pReport) throws OceanusException {
        /* Call super constructor */
        super(pPreferenceMgr);

        /* Access the Repository base */
        thePreferences = pPreferenceMgr.getPreferenceSet(ThemisSvnPreferences.class);

        /* Access the Repository base */
        theBase = thePreferences.getStringValue(ThemisSvnPreferenceKey.BASE);
        setName(thePreferences.getStringValue(ThemisSvnPreferenceKey.NAME));

        /* Create a client manager pool */
        theClientMgrPool = new ThemisSvnClientManager(thePreferences);

        /* Create component list */
        final ThemisSvnComponentList myComponents = new ThemisSvnComponentList(this);
        setComponents(myComponents);

        /* Create RevisionHistoryMap */
        theRevisionHistory = new ThemisSvnRevisionHistoryMap(this);

        /* Report start of analysis */
        pReport.initTask("Analysing components");

        /* Discover components */
        myComponents.discover(pReport);
    }

    @Override
    public String toString() {
        return getPath();
    }

    @Override
    public MetisFieldSet<ThemisSvnRepository> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the repository base.
     * @return the name
     */
    public String getBase() {
        return theBase;
    }

    /**
     * Obtain the preferences.
     * @return the preferences
     */
    public ThemisSvnPreferences getPreferences() {
        return thePreferences;
    }

    /**
     * Obtain a client manager.
     * @return the manager
     */
    public SVNClientManager getClientManager() {
        return theClientMgrPool.getClientMgr();
    }

    /**
     * Release a client manager.
     * @param pMgr the manager
     */
    public void releaseClientManager(final SVNClientManager pMgr) {
        theClientMgrPool.releaseClientMgr(pMgr);
    }

    @Override
    public ThemisSvnComponentList getComponents() {
        return (ThemisSvnComponentList) super.getComponents();
    }

    /**
     * Get the revisionHistoryMap for this repository.
     * @return the historyMap
     */
    public ThemisSvnRevisionHistoryMap getHistoryMap() {
        return theRevisionHistory;
    }

    /**
     * Dispose of any client links.
     */
    public void dispose() {
        /* Dispose of any connections */
        theClientMgrPool.dispose();
    }

    /**
     * Build URL.
     * @return the Repository path
     */
    public String getPath() {
        /* Build the underlying string */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the repository */
        myBuilder.append(theBase)
                .append(SEP_URL)
                .append(PFIX_URL)
                .append(SEP_URL)
                .append(getName());

        /* Return the path */
        return myBuilder.toString();
    }

    /**
     * Build URL.
     * @param pPath the path
     * @return the Repository path
     * @throws OceanusException on error
     */
    public SVNURL getURL(final String pPath) throws OceanusException {
        /* Protect against exceptions */
        try {
            /* Build the underlying string */
            final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the repository directory */
            myBuilder.append(getPath())
                    .append(SEP_URL)
                    .append(pPath);

            /* Return the path */
            return SVNURL.parseURIEncoded(myBuilder.toString());
        } catch (SVNException e) {
            throw new ThemisIOException("Failed to parse path " + pPath, e);
        }
    }

    @Override
    public int compareTo(final ThemisSvnRepository pThat) {
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
        return super.compareTo(pThat);
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
        if (!(pThat instanceof ThemisSvnRepository)) {
            return false;
        }
        final ThemisSvnRepository myThat = (ThemisSvnRepository) pThat;

        /* Compare fields */
        if (!theBase.equals(myThat.theBase)) {
            return false;
        }
        return super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return theBase.hashCode() * HASH_PRIME
               + super.hashCode();
    }

    @Override
    public ThemisSvnComponent locateComponent(final String pName) {
        /* Locate component in component list */
        return (ThemisSvnComponent) super.locateComponent(pName);
    }

    @Override
    public ThemisSvnBranch locateBranch(final String pComponent,
                                        final String pVersion) {
        /* Locate branch in component list */
        return (ThemisSvnBranch) super.locateBranch(pComponent, pVersion);
    }

    @Override
    public ThemisSvnTag locateTag(final String pComponent,
                                  final String pVersion,
                                  final int pTag) {
        /* Locate Tag in component list */
        return (ThemisSvnTag) super.locateTag(pComponent, pVersion, pTag);
    }

    /**
     * Locate branch.
     * @param pURL the URL to locate
     * @return the relevant branch or Null
     */
    protected ThemisSvnBranch locateBranch(final SVNURL pURL) {
        /* Locate branch in component list */
        return getComponents().locateBranch(pURL);
    }

    @Override
    protected ThemisSvnBranch locateBranch(final ThemisMvnProjectId pId) {
        /* Lookup mapping */
        return (ThemisSvnBranch) super.locateBranch(pId);
    }

    @Override
    protected ThemisSvnTag locateTag(final ThemisMvnProjectId pId) {
        /* Lookup mapping */
        return (ThemisSvnTag) super.locateTag(pId);
    }
}
