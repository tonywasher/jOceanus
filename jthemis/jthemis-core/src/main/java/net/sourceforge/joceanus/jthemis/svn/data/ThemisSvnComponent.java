/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jthemis.svn.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.ListIterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmetis.profile.MetisProfile;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmBranch;
import net.sourceforge.joceanus.jthemis.scm.data.ThemisScmComponent;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition.ThemisMvnSubModule;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnBranch.ThemisSvnBranchList;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public final class ThemisSvnComponent
        extends
        ThemisScmComponent {
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
    private static final MetisFieldSet<ThemisSvnComponent> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnComponent.class);

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ThemisSvnComponent.class);

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     */
    protected ThemisSvnComponent(final ThemisSvnRepository pParent,
                                 final String pName) {
        /* Call super constructor */
        super(pParent, pName);

        /* Create branch list */
        final ThemisSvnBranchList myBranches = new ThemisSvnBranchList(this);
        setBranches(myBranches);
    }

    @Override
    public MetisFieldSet<ThemisSvnComponent> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public ThemisSvnRepository getRepository() {
        return (ThemisSvnRepository) super.getRepository();
    }

    @Override
    public ThemisSvnBranchList getBranches() {
        return (ThemisSvnBranchList) super.getBranches();
    }

    /**
     * Get the branch iterator for this component.
     * @return the iterator
     */
    public Iterator<ThemisScmBranch> branchIterator() {
        return getBranches().iterator();
    }

    /**
     * Get the branch listIterator for this component.
     * @return the iterator
     */
    public ListIterator<ThemisScmBranch> branchListIterator() {
        final ThemisSvnBranchList myBranches = getBranches();
        return myBranches.listIterator(myBranches.size());
    }

    /**
     * Get the trunk branch list for this component.
     * @return the trunk branch
     */
    public ThemisSvnBranch getTrunk() {
        return getBranches().getTrunk();
    }

    /**
     * Obtain repository path for the trunk.
     * @return the Trunk path for the component
     */
    protected String getTrunkPath() {
        /* Allocate a builder */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the base path */
        myBuilder.append(getURLPath())
                .append(ThemisSvnRepository.SEP_URL)
                .append(DIR_TRUNK);

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path for the branches.
     * @return the Branches path for the component
     */
    public String getBranchesPath() {
        /* Allocate a builder */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the base path */
        myBuilder.append(getURLPath())
                .append(ThemisSvnRepository.SEP_URL)
                .append(DIR_BRANCHES);

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path for the tags.
     * @return the Tags path for the component
     */
    public String getTagsPath() {
        /* Allocate a builder */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the branch path */
        myBuilder.append(getURLPath())
                .append(ThemisSvnRepository.SEP_URL)
                .append(DIR_TAGS);

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain own path.
     * @return the path for this component
     */
    public String getURLPath() {
        /* Build the underlying string */
        final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the repository */
        myBuilder.append(getRepository().getPath())
                .append(ThemisSvnRepository.SEP_URL)
                .append(getName());

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
     * @throws OceanusException on error
     */
    public ThemisMvnProjectDefinition parseProjectURL(final String pPath) throws OceanusException {
        InputStream myInput = null;
        /* Build the URL */
        try {
            /* Build the underlying string */
            final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

            /* Build the initial path */
            myBuilder.append(pPath)
                    .append(ThemisSvnRepository.SEP_URL)
                    .append(ThemisMvnProjectDefinition.POM_NAME);

            /* Create the repository path */
            final SVNURL myURL = SVNURL.parseURIEncoded(myBuilder.toString());

            /* Access URL as input stream */
            myInput = getFileURLasInputStream(myURL);

            /* Parse the project definition and return it */
            return myInput == null
                                   ? null
                                   : new ThemisMvnProjectDefinition(myInput);

        } catch (SVNException e) {
            throw new ThemisIOException("Failed to parse project file for " + pPath, e);
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
     * @param pPath the base URL path
     * @param pProject the project definition
     * @throws OceanusException on error
     */
    public void parseSubProjects(final String pPath,
                                 final ThemisMvnProjectDefinition pProject) throws OceanusException {
        /* Loop through the subModules */
        final Iterator<ThemisMvnSubModule> myIterator = pProject.subIterator();
        while (myIterator.hasNext()) {
            final ThemisMvnSubModule myModule = myIterator.next();

            /* If the module has not got a project definition */
            if (myModule.getProjectDefinition() == null) {
                /* Build the underlying string */
                final StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);
                myBuilder.append(pPath);
                myBuilder.append(ThemisSvnRepository.SEP_URL);
                myBuilder.append(myModule.getName());
                final String myPath = myBuilder.toString();

                /* Parse the project URL */
                final ThemisMvnProjectDefinition mySubDef = parseProjectURL(myPath);
                myModule.setProjectDefinition(mySubDef);
                parseSubProjects(myPath, mySubDef);
            }
        }
    }

    /**
     * Get FileURL as input stream.
     * @param pURL the URL to stream
     * @return the stream of null if file does not exists
     * @throws OceanusException on error
     */
    public InputStream getFileURLasInputStream(final SVNURL pURL) throws OceanusException {
        /* Access client */
        final ThemisSvnRepository myRepo = getRepository();
        final SVNClientManager myMgr = myRepo.getClientManager();
        final SVNWCClient myClient = myMgr.getWCClient();

        /* Create the byte array stream */
        final ByteArrayOutputStream myBaos = new ByteArrayOutputStream(BUFFER_STREAM);

        /* Protect against exceptions */
        try {
            /* Read the entry into the outputStream and create an input stream from it */
            myClient.doGetFileContents(pURL, SVNRevision.HEAD, SVNRevision.HEAD, true, myBaos);
            return new ByteArrayInputStream(myBaos.toByteArray());
        } catch (SVNException e) {
            /* Access the error code */
            final SVNErrorCode myCode = e.getErrorMessage().getErrorCode();

            /* Allow file not existing */
            if (!myCode.equals(SVNErrorCode.FS_NOT_FOUND)) {
                throw new ThemisIOException("Unable to read File URL", e);
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
    public static final class ThemisSvnComponentList
            extends
            ThemisScmComponentList {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisSvnComponentList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnComponentList.class);

        /**
         * Parent Repository.
         */
        private final ThemisSvnRepository theRepository;

        /**
         * Constructor.
         * @param pParent the parent repository
         */
        public ThemisSvnComponentList(final ThemisSvnRepository pParent) {
            /* Store parent/manager for use by entry handler */
            theRepository = pParent;
        }

        @Override
        public MetisFieldSet<ThemisSvnComponentList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Discover component list from repository.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public void discover(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Reset the list */
            clear();

            /* Obtain the active profile */
            final MetisProfile myBaseTask = pReport.getActiveTask();
            MetisProfile myTask = myBaseTask.startTask("discoverComponents");

            /* Access a LogClient */
            final SVNClientManager myMgr = theRepository.getClientManager();
            final SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the component directory URL */
                final SVNURL myURL = SVNURL.parseURIEncoded(theRepository.getPath());

                /* List the component directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES, SVNDirEntry.DIRENT_ALL, new ListDirHandler());
            } catch (SVNException e) {
                throw new ThemisIOException("Failed to discover components for " + theRepository.getName(), e);
            } finally {
                theRepository.releaseClientManager(myMgr);
            }

            /* Report number of stages */
            pReport.setNumStages(size() + 2);

            /* Sort the list */
            getUnderlyingList().sort(null);

            /* Loop through the components */
            final Iterator<ThemisScmComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                /* Access the Component */
                final ThemisSvnComponent myComponent = (ThemisSvnComponent) myIterator.next();
                final ThemisSvnBranchList myBranches = myComponent.getBranches();

                /* Start the discoverComponent task */
                myTask = myBaseTask.startTask("discoverComponent:" + myComponent.getName());

                /* Report discovery of component */
                pReport.setNewStage("Analysing component " + myComponent.getName());

                /* Discover branches for the component */
                myBranches.discover(pReport);
            }

            /* Complete the task */
            myTask.end();
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected ThemisSvnBranch locateBranch(final SVNURL pURL) {
            /* Loop through the components */
            final Iterator<ThemisScmComponent> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnComponent myComponent = (ThemisSvnComponent) myIterator.next();

                /* Access branch URL */
                final SVNURL myCompURL = myComponent.getURL();

                /* Skip if the repositories are different */
                if (myCompURL == null
                    || !pURL.getHost().equals(myCompURL.getHost())) {
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
        protected ThemisSvnBranch locateBranch(final String pComponent,
                                               final String pVersion) {
            /* Pass call on */
            return (ThemisSvnBranch) super.locateBranch(pComponent, pVersion);
        }

        @Override
        protected ThemisSvnTag locateTag(final String pComponent,
                                         final String pVersion,
                                         final int pTag) {
            /* Pass call on */
            return (ThemisSvnTag) super.locateTag(pComponent, pVersion, pTag);
        }

        /**
         * The Directory Entry Handler.
         */
        private final class ListDirHandler
                implements
                ISVNDirEntryHandler {
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
                final String myName = pEntry.getName();

                /* Create the component and add to the list */
                final ThemisSvnComponent myComp = new ThemisSvnComponent(theRepository, myName);
                add(myComp);
            }
        }
    }
}
