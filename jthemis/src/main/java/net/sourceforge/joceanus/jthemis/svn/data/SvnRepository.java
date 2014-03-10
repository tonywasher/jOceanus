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
package net.sourceforge.joceanus.jthemis.svn.data;

import net.sourceforge.joceanus.jmetis.preference.PreferenceManager;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.data.ScmRepository;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectId;
import net.sourceforge.joceanus.jthemis.svn.data.SvnComponent.SvnComponentList;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public class SvnRepository
        extends ScmRepository<SvnRepository> {
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
    private static final JDataFields FIELD_DEFS = new JDataFields(SvnRepository.class.getSimpleName(), ScmRepository.FIELD_DEFS);

    /**
     * Base field id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareEqualityField("Base");

    /**
     * HistoryMap field id.
     */
    private static final JDataField FIELD_HISTMAP = FIELD_DEFS.declareLocalField("HistoryMap");

    @Override
    public String formatObject() {
        return getPath();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_BASE.equals(pField)) {
            return theBase;
        }
        if (FIELD_HISTMAP.equals(pField)) {
            return theRevisionHistory;
        }

        /* pass call on */
        return super.getFieldValue(pField);
    }

    /**
     * The Preferences.
     */
    private final SubVersionPreferences thePreferences;

    /**
     * Repository Base.
     */
    private final String theBase;

    /**
     * The Client Manager.
     */
    private final SvnClientManager theClientMgrPool;

    /**
     * RevisionHistory Map.
     */
    private final SvnRevisionHistoryMap theRevisionHistory;

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
    public SubVersionPreferences getPreferences() {
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
    public SvnComponentList getComponents() {
        return (SvnComponentList) super.getComponents();
    }

    /**
     * Get the revisionHistoryMap for this repository.
     * @return the historyMap
     */
    public SvnRevisionHistoryMap getHistoryMap() {
        return theRevisionHistory;
    }

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pReport the report object
     * @throws JOceanusException on error
     */
    public SvnRepository(final PreferenceManager pPreferenceMgr,
                         final ReportStatus pReport) throws JOceanusException {
        /* Call super constructor */
        super(pPreferenceMgr);

        /* Access the Repository base */
        thePreferences = pPreferenceMgr.getPreferenceSet(SubVersionPreferences.class);

        /* Access the Repository base */
        theBase = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_REPO);
        setName(thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_NAME));

        /* Create a client manager pool */
        theClientMgrPool = new SvnClientManager(thePreferences);

        /* Create component list */
        SvnComponentList myComponents = new SvnComponentList(this);
        setComponents(myComponents);

        /* Create RevisionHistoryMap */
        theRevisionHistory = new SvnRevisionHistoryMap(this);

        /* Report start of analysis */
        pReport.initTask("Analysing components");

        /* Discover components */
        myComponents.discover(pReport);

        /* Report completion of pass */
        pReport.initTask("Component Analysis complete");
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
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the repository */
        myBuilder.append(theBase);

        /* Build the prefix directory */
        myBuilder.append(SEP_URL);
        myBuilder.append(PFIX_URL);

        /* Build the component directory */
        myBuilder.append(SEP_URL);
        myBuilder.append(getName());

        /* Return the path */
        return myBuilder.toString();
    }

    /**
     * Build URL.
     * @param pPath the path
     * @return the Repository path
     * @throws JOceanusException on error
     */
    public SVNURL getURL(final String pPath) throws JOceanusException {
        /* Protect against exceptions */
        try {
            /* Build the underlying string */
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the repository */
            myBuilder.append(getPath());

            /* Build the prefix directory */
            myBuilder.append(SEP_URL);
            myBuilder.append(pPath);

            /* Return the path */
            return SVNURL.parseURIEncoded(myBuilder.toString());
        } catch (SVNException e) {
            throw new JThemisIOException("Failed to parse path " + pPath, e);
        }
    }

    @Override
    public int compareTo(final SvnRepository pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare bases */
        int iResult = theBase.compareTo(pThat.theBase);
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
        if (!(pThat instanceof SvnRepository)) {
            return false;
        }
        SvnRepository myThat = (SvnRepository) pThat;

        /* Compare fields */
        if (!theBase.equals(myThat.theBase)) {
            return false;
        }
        return super.equals(myThat);
    }

    @Override
    public int hashCode() {
        return (theBase.hashCode() * HASH_PRIME) + super.hashCode();
    }

    @Override
    public SvnComponent locateComponent(final String pName) {
        /* Locate component in component list */
        return (SvnComponent) super.locateComponent(pName);
    }

    @Override
    public SvnBranch locateBranch(final String pComponent,
                                  final String pVersion) {
        /* Locate branch in component list */
        return (SvnBranch) super.locateBranch(pComponent, pVersion);
    }

    @Override
    public SvnTag locateTag(final String pComponent,
                            final String pVersion,
                            final int pTag) {
        /* Locate Tag in component list */
        return (SvnTag) super.locateTag(pComponent, pVersion, pTag);
    }

    /**
     * Locate branch.
     * @param pURL the URL to locate
     * @return the relevant branch or Null
     */
    protected SvnBranch locateBranch(final SVNURL pURL) {
        /* Locate branch in component list */
        return getComponents().locateBranch(pURL);
    }

    @Override
    protected SvnBranch locateBranch(final MvnProjectId pId) {
        /* Lookup mapping */
        return (SvnBranch) super.locateBranch(pId);
    }

    @Override
    protected SvnTag locateTag(final MvnProjectId pId) {
        /* Lookup mapping */
        return (SvnTag) super.locateTag(pId);
    }
}
