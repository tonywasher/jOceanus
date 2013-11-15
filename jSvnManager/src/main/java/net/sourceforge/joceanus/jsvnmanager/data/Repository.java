/*******************************************************************************
 * jSvnManager: Java SubVersion Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jsvnmanager.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataException.ExceptionClass;
import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jpreferenceset.PreferenceManager;
import net.sourceforge.joceanus.jsvnmanager.data.Component.ComponentList;
import net.sourceforge.joceanus.jsvnmanager.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jsvnmanager.project.ProjectDefinition;
import net.sourceforge.joceanus.jsvnmanager.project.ProjectId;

import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * Represents a repository.
 * @author Tony Washer
 */
public class Repository
        implements JDataContents, Comparable<Repository> {
    /**
     * The Hash Prime.
     */
    protected static final int HASH_PRIME = 17;

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
     * The buffer length.
     */
    private static final int BUFFER_STREAM = 1000;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Repository.class.getSimpleName());

    /**
     * Base field id.
     */
    private static final JDataField FIELD_BASE = FIELD_DEFS.declareEqualityField("Base");

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
     * The Logger.
     */
    private final Logger theLogger;

    /**
     * The Preference Manager.
     */
    private final PreferenceManager thePreferenceMgr;

    /**
     * The Preferences.
     */
    private final SubVersionPreferences thePreferences;

    /**
     * Repository Name.
     */
    private final String theName;

    /**
     * Repository Base.
     */
    private final String theBase;

    /**
     * The Client Manager.
     */
    private final ClientManager theClientMgrPool;

    /**
     * ComponentList.
     */
    private final ComponentList theComponents;

    /**
     * Branch Map.
     */
    private final Map<ProjectId, Branch> theBranchMap;

    /**
     * Tag Map.
     */
    private final Map<ProjectId, Tag> theTagMap;

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
     * Obtain logger.
     * @return the logger
     */
    public Logger getLogger() {
        return theLogger;
    }

    /**
     * Obtain the preference manager.
     * @return the preference manager
     */
    public PreferenceManager getPreferenceMgr() {
        return thePreferenceMgr;
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

    /**
     * Get the component list for this repository.
     * @return the component list
     */
    public ComponentList getComponentList() {
        return theComponents;
    }

    /**
     * Constructor.
     * @param pPreferenceMgr the preference manager
     * @param pReport the report object
     * @throws JDataException on error
     */
    public Repository(final PreferenceManager pPreferenceMgr,
                      final ReportStatus pReport) throws JDataException {
        /* Store the preference manager */
        thePreferenceMgr = pPreferenceMgr;
        theLogger = thePreferenceMgr.getLogger();

        /* Allocate the maps */
        theBranchMap = new HashMap<ProjectId, Branch>();
        theTagMap = new HashMap<ProjectId, Tag>();

        /* Access the Repository base */
        thePreferences = thePreferenceMgr.getPreferenceSet(SubVersionPreferences.class);

        /* Access the Repository base */
        theBase = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_REPO);
        theName = thePreferences.getStringValue(SubVersionPreferences.NAME_SVN_NAME);

        /* Create a client manager pool */
        theClientMgrPool = new ClientManager(thePreferences);

        /* Create component list */
        theComponents = new ComponentList(this);

        /* Report start of analysis */
        pReport.initTask("Analysing components");

        /* Discover components */
        theComponents.discover(pReport);

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

    @Override
    public int compareTo(final Repository pThat) {
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
        return theName.compareTo(pThat.theName);
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
        if (pThat instanceof Repository) {
            return false;
        }
        Repository myThat = (Repository) pThat;

        /* Compare fields */
        if (!theBase.equals(myThat.theBase)) {
            return false;
        }
        return theName.equals(myThat.theName);
    }

    @Override
    public int hashCode() {
        return (theBase.hashCode() * HASH_PRIME)
               + theName.hashCode();
    }

    /**
     * Locate Component.
     * @param pName the component to locate
     * @return the relevant component or Null
     */
    public Component locateComponent(final String pName) {
        /* Locate component in component list */
        return theComponents.locateComponent(pName);
    }

    /**
     * Locate Branch.
     * @param pComponent the component to locate
     * @param pVersion the version to locate
     * @return the relevant branch or Null
     */
    public Branch locateBranch(final String pComponent,
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
    public Tag locateTag(final String pComponent,
                         final String pVersion,
                         final int pTag) {
        /* Locate Tag in component list */
        return theComponents.locateTag(pComponent, pVersion, pTag);
    }

    /**
     * Locate branch.
     * @param pURL the URL to locate
     * @return the relevant branch or Null
     */
    protected Branch locateBranch(final SVNURL pURL) {
        /* Locate branch in component list */
        return theComponents.locateBranch(pURL);
    }

    /**
     * Locate branch.
     * @param pId the project id for the branch
     * @return the branch
     */
    protected Branch locateBranch(final ProjectId pId) {
        /* Lookup mapping */
        return theBranchMap.get(pId);
    }

    /**
     * Locate tag.
     * @param pId the project id for the tag
     * @return the tag
     */
    protected Tag locateTag(final ProjectId pId) {
        /* Lookup mapping */
        return theTagMap.get(pId);
    }

    /**
     * Register branch.
     * @param pId the project id for the branch
     * @param pBranch the branch
     */
    protected void registerBranch(final ProjectId pId,
                                  final Branch pBranch) {
        /* Store mapping */
        theBranchMap.put(pId, pBranch);
    }

    /**
     * Register tag.
     * @param pId the project id for the branch
     * @param pTag the tag
     */
    protected void registerTag(final ProjectId pId,
                               final Tag pTag) {
        /* Store mapping */
        theTagMap.put(pId, pTag);
    }

    /**
     * Get FileURL as input stream.
     * @param pPath the base URL path
     * @return the stream of null if file does not exists
     * @throws JDataException on error
     */
    public ProjectDefinition parseProjectURL(final String pPath) throws JDataException {
        InputStream myInput = null;
        /* Build the URL */
        try {
            /* Build the underlying string */
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the initial path */
            myBuilder.append(pPath);
            myBuilder.append(SEP_URL);

            /* Build the POM name */
            myBuilder.append(ProjectDefinition.POM_NAME);

            /* Create the repository path */
            SVNURL myURL = SVNURL.parseURIEncoded(myBuilder.toString());

            /* Access URL as input stream */
            myInput = getFileURLasInputStream(myURL);
            if (myInput == null) {
                return null;
            }

            /* Parse the project definition and return it */
            ProjectDefinition myProject = new ProjectDefinition(myInput);

            /* Return the definition */
            return myProject;

        } catch (SVNException e) {
            throw new JDataException(ExceptionClass.SUBVERSION, "Failed to parse project file for "
                                                                + pPath, e);
        } finally {
            if (myInput != null) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    theLogger.log(Level.SEVERE, "Close Failure", e);
                }
            }
        }
    }

    /**
     * Get FileURL as input stream.
     * @param pURL the URL to stream
     * @return the stream of null if file does not exists
     * @throws JDataException on error
     */
    public InputStream getFileURLasInputStream(final SVNURL pURL) throws JDataException {
        /* Access client */
        SVNClientManager myMgr = getClientManager();
        SVNWCClient myClient = myMgr.getWCClient();
        ByteArrayInputStream myStream = null;

        /* Create the byte array stream */
        ByteArrayOutputStream myBaos = new ByteArrayOutputStream(BUFFER_STREAM);

        /* Protect against exceptions */
        try {
            /* Read the entry into the outputStream and create an input stream from it */
            myClient.doGetFileContents(pURL, SVNRevision.HEAD, SVNRevision.HEAD, true, myBaos);
            myStream = new ByteArrayInputStream(myBaos.toByteArray());
        } catch (SVNException e) {
            /* Access the error code */
            SVNErrorCode myCode = e.getErrorMessage().getErrorCode();

            /* Allow file not existing */
            if (myCode != SVNErrorCode.FS_NOT_FOUND) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Unable to read File URL", e);
            }

            /* Set stream to null */
            myStream = null;
        }

        /* Release the client manager */
        releaseClientManager(myMgr);

        /* Return the stream */
        return myStream;
    }
}
