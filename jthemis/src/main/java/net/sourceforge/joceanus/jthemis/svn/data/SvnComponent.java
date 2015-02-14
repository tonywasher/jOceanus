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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ListIterator;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ScmComponent;
import net.sourceforge.joceanus.jthemis.scm.data.ScmReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.scm.maven.MvnProjectDefinition.MvnSubModule;
import net.sourceforge.joceanus.jthemis.svn.data.SvnBranch.SvnBranchList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public final class SvnComponent
        extends ScmComponent<SvnComponent, SvnRepository> {
    /**
     * The trunk directory.
     */
    public static final String DIR_TRUNK = "trunk";

    /**
     * The branches directory.
     */
    public static final String DIR_BRANCHES = "branches";

    /**
     * The tags directory.
     */
    public static final String DIR_TAGS = "tags";

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
    private static final JDataFields FIELD_DEFS = new JDataFields(SvnComponent.class.getSimpleName(), ScmComponent.FIELD_DEFS);

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SvnComponent.class);

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     */
    protected SvnComponent(final SvnRepository pParent,
                           final String pName) {
        /* Call super constructor */
        super(pParent, pName);

        /* Create branch list */
        SvnBranchList myBranches = new SvnBranchList(this);
        setBranches(myBranches);
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public SvnBranchList getBranches() {
        return (SvnBranchList) super.getBranches();
    }

    /**
     * Get the branch iterator for this component.
     * @return the iterator
     */
    public ListIterator<SvnBranch> branchIterator() {
        return getBranches().listIterator();
    }

    /**
     * Get the trunk branch list for this component.
     * @return the trunk branch
     */
    public SvnBranch getTrunk() {
        return getBranches().getTrunk();
    }

    /**
     * Obtain repository path for the trunk.
     * @return the Trunk path for the component
     */
    protected String getTrunkPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the base path */
        myBuilder.append(getURLPath());

        /* Add the trunk directory */
        myBuilder.append(SvnRepository.SEP_URL);
        myBuilder.append(DIR_TRUNK);

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path for the branches.
     * @return the Branches path for the component
     */
    public String getBranchesPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the base path */
        myBuilder.append(getURLPath());

        /* Add the branches directory */
        myBuilder.append(SvnRepository.SEP_URL);
        myBuilder.append(DIR_BRANCHES);

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path for the tags.
     * @return the Tags path for the component
     */
    public String getTagsPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the branch path */
        myBuilder.append(getURLPath());

        /* Add the tags directory */
        myBuilder.append(SvnRepository.SEP_URL);
        myBuilder.append(DIR_TAGS);

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain own path.
     * @return the path for this component
     */
    public String getURLPath() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the repository */
        myBuilder.append(getRepository().getPath());

        /* Build the component directory */
        myBuilder.append(SvnRepository.SEP_URL);
        myBuilder.append(getName());

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain URL.
     * @return the URL
     */
    public SVNURL getURL() {
        /* Build the URL */
        try {
            return SVNURL.parseURIEncoded(getURLPath());
        } catch (SVNException e) {
            LOGGER.error("Parse Failure", e);
            return null;
        }
    }

    /**
     * Get FileURL as input stream.
     * @param pPath the base URL path
     * @return the stream of null if file does not exists
     * @throws JOceanusException on error
     */
    public MvnProjectDefinition parseProjectURL(final String pPath) throws JOceanusException {
        InputStream myInput = null;
        /* Build the URL */
        try {
            /* Build the underlying string */
            StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the initial path */
            myBuilder.append(pPath);
            myBuilder.append(SvnRepository.SEP_URL);

            /* Build the POM name */
            myBuilder.append(MvnProjectDefinition.POM_NAME);

            /* Create the repository path */
            SVNURL myURL = SVNURL.parseURIEncoded(myBuilder.toString());

            /* Access URL as input stream */
            myInput = getFileURLasInputStream(myURL);
            if (myInput == null) {
                return null;
            }

            /* Parse the project definition and return it */
            MvnProjectDefinition myProject = new MvnProjectDefinition(myInput);

            /* Loop through the subModules */
            Iterator<MvnSubModule> myIterator = myProject.subIterator();
            while (myIterator.hasNext()) {
                MvnSubModule myModule = myIterator.next();

                /* Reset the string buffer */
                myBuilder.setLength(0);

                /* Build the path name */
                myBuilder.append(pPath);
                myBuilder.append(SvnRepository.SEP_URL);
                myBuilder.append(myModule.getName());

                /* Parse the project URL */
                MvnProjectDefinition mySubDef = parseProjectURL(myBuilder.toString());
                myModule.setProjectDefinition(mySubDef);
            }

            /* Return the definition */
            return myProject;

        } catch (SVNException e) {
            throw new JThemisIOException("Failed to parse project file for " + pPath, e);
        } finally {
            if (myInput != null) {
                try {
                    myInput.close();
                } catch (IOException e) {
                    LOGGER.error("Close Failure", e);
                }
            }
        }
    }

    /**
     * Get FileURL as input stream.
     * @param pURL the URL to stream
     * @return the stream of null if file does not exists
     * @throws JOceanusException on error
     */
    public InputStream getFileURLasInputStream(final SVNURL pURL) throws JOceanusException {
        /* Access client */
        SvnRepository myRepo = getRepository();
        SVNClientManager myMgr = myRepo.getClientManager();
        SVNWCClient myClient = myMgr.getWCClient();

        /* Create the byte array stream */
        ByteArrayOutputStream myBaos = new ByteArrayOutputStream(BUFFER_STREAM);

        /* Protect against exceptions */
        try {
            /* Read the entry into the outputStream and create an input stream from it */
            myClient.doGetFileContents(pURL, SVNRevision.HEAD, SVNRevision.HEAD, true, myBaos);
            return new ByteArrayInputStream(myBaos.toByteArray());
        } catch (SVNException e) {
            /* Access the error code */
            SVNErrorCode myCode = e.getErrorMessage().getErrorCode();

            /* Allow file not existing */
            if (!myCode.equals(SVNErrorCode.FS_NOT_FOUND)) {
                throw new JThemisIOException("Unable to read File URL", e);
            }

            /* Set stream to null */
            return null;
        } finally {
            /* Release the client manager */
            myRepo.releaseClientManager(myMgr);
        }
    }

    /**
     * List of components.
     */
    public static final class SvnComponentList
            extends ScmComponentList<SvnComponent, SvnRepository> {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(SvnComponentList.class.getSimpleName(), ScmComponentList.FIELD_DEFS);

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        /**
         * Parent Repository.
         */
        private final SvnRepository theRepository;

        /**
         * Constructor.
         * @param pParent the parent repository
         */
        public SvnComponentList(final SvnRepository pParent) {
            /* Call super constructor */
            super(SvnComponent.class);

            /* Store parent/manager for use by entry handler */
            theRepository = pParent;
        }

        /**
         * Discover component list from repository.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        public void discover(final ReportStatus pReport) throws JOceanusException {
            /* Reset the list */
            clear();

            /* Access a LogClient */
            SVNClientManager myMgr = theRepository.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the component directory URL */
                SVNURL myURL = SVNURL.parseURIEncoded(theRepository.getPath());

                /* List the component directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler());
            } catch (SVNException e) {
                throw new JThemisIOException("Failed to discover components for " + theRepository.getName(), e);
            } finally {
                theRepository.releaseClientManager(myMgr);
            }

            /* Report number of stages */
            if (!pReport.setNumStages(size() + 2)) {
                return;
            }

            /* Loop through the components */
            Iterator<SvnComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the Component */
                SvnComponent myComponent = myIterator.next();
                SvnBranchList myBranches = myComponent.getBranches();

                /* Report discovery of component */
                if (!pReport.setNewStage("Analysing component " + myComponent.getName())) {
                    break;
                }

                /* Discover branches for the component */
                myBranches.discover(pReport);
            }
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected SvnBranch locateBranch(final SVNURL pURL) {
            /* Loop through the components */
            Iterator<SvnComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                SvnComponent myComponent = myIterator.next();

                /* Access branch URL */
                SVNURL myCompURL = myComponent.getURL();

                /* Skip if the repositories are different */
                if (!pURL.getHost().equals(myCompURL.getHost())) {
                    continue;
                }

                /* If this is parent of the passed URL */
                if (pURL.getPath().startsWith(myCompURL.getPath())) {
                    /* Look at this components branches */
                    return myComponent.getBranches().locateBranch(pURL);
                }
            }

            /* Not found */
            return null;
        }

        @Override
        protected SvnBranch locateBranch(final String pComponent,
                                         final String pVersion) {
            /* Pass call on */
            return (SvnBranch) super.locateBranch(pComponent, pVersion);
        }

        @Override
        protected SvnTag locateTag(final String pComponent,
                                   final String pVersion,
                                   final int pTag) {
            /* Pass call on */
            return (SvnTag) super.locateTag(pComponent, pVersion, pTag);
        }

        /**
         * The Directory Entry Handler.
         */
        private final class ListDirHandler
                implements ISVNDirEntryHandler {
            @Override
            public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
                /* Ignore if not a directory and if it is top-level */
                if (!SVNNodeKind.DIR.equals(pEntry.getKind())) {
                    return;
                }
                if (pEntry.getRelativePath().length() == 0) {
                    return;
                }

                /* Access the name */
                String myName = pEntry.getName();

                /* Create the component and add to the list */
                SvnComponent myComp = new SvnComponent(theRepository, myName);
                add(myComp);
            }
        }
    }
}
