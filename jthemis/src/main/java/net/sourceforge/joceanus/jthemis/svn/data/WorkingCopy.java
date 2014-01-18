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

import java.io.File;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.logging.Logger;

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataObject.JDataContents;
import net.sourceforge.joceanus.jsortedlist.OrderedList;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jthemis.JThemisIOException;
import net.sourceforge.joceanus.jthemis.svn.data.JSvnReporter.ReportStatus;
import net.sourceforge.joceanus.jthemis.svn.data.UpdateStatus.UpdateStatusList;
import net.sourceforge.joceanus.jthemis.svn.project.ProjectDefinition;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;

/**
 * Represents a Working extract copy of subversion.
 * @author Tony Washer
 */
public final class WorkingCopy
        implements JDataContents, Comparable<WorkingCopy> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(WorkingCopy.class.getSimpleName());

    /**
     * Component field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    /**
     * Branch field id.
     */
    private static final JDataField FIELD_BRAN = FIELD_DEFS.declareEqualityField("Branch");

    /**
     * Alias field id.
     */
    private static final JDataField FIELD_ALIAS = FIELD_DEFS.declareLocalField("Alias");

    /**
     * Revision field id.
     */
    private static final JDataField FIELD_REVISION = FIELD_DEFS.declareLocalField("Revision");

    /**
     * Project field id.
     */
    private static final JDataField FIELD_PROJECT = FIELD_DEFS.declareLocalField("Project");

    /**
     * Update list field id.
     */
    private static final JDataField FIELD_UPDATES = FIELD_DEFS.declareLocalField("Updates");

    @Override
    public String formatObject() {
        return getFullName();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_ALIAS.equals(pField)) {
            return getComponentName().equals(theAlias)
                    ? JDataFieldValue.SKIP
                    : theAlias;
        }
        if (FIELD_BRAN.equals(pField)) {
            return theBranch;
        }
        if (FIELD_COMP.equals(pField)) {
            return theBranch.getComponent();
        }
        if (FIELD_PROJECT.equals(pField)) {
            return theProject;
        }
        if (FIELD_REVISION.equals(pField)) {
            return theRevision;
        }
        if (FIELD_UPDATES.equals(pField)) {
            return (theUpdates.isEmpty())
                    ? JDataFieldValue.SKIP
                    : theUpdates;
        }

        /* Unknown */
        return JDataFieldValue.UNKNOWN;
    }

    /**
     * The branch associated with the working copy.
     */
    private final Branch theBranch;

    /**
     * The alias of the branch that is checked out.
     */
    private final String theAlias;

    /**
     * The path at which the branch is checked out.
     */
    private final File theLocation;

    /**
     * The revision at which the branch is checked out.
     */
    private final long theRevision;

    /**
     * The updates.
     */
    private final UpdateStatusList theUpdates;

    /**
     * The project definition.
     */
    private ProjectDefinition theProject = null;

    /**
     * Get branch.
     * @return the branch
     */
    public Branch getBranch() {
        return theBranch;
    }

    /**
     * Get component name.
     * @return the component name
     */
    public String getComponentName() {
        return theBranch.getComponent().getName();
    }

    /**
     * Get full name.
     * @return the full name
     */
    public String getFullName() {
        return getComponentName()
               + "_"
               + theBranch.getBranchName();
    }

    /**
     * Get alias.
     * @return the alias
     */
    public String getAlias() {
        return theAlias;
    }

    /**
     * Get Location.
     * @return the location
     */
    public File getLocation() {
        return theLocation;
    }

    /**
     * Get check out revision.
     * @return the branch
     */
    public long getRevision() {
        return theRevision;
    }

    /**
     * Get Project Definition.
     * @return the project definition
     */
    public ProjectDefinition getProjectDefinition() {
        return theProject;
    }

    /**
     * Constructor.
     * @param pLocation the location
     * @param pBranch the branch
     * @param pRevision the checked out revision
     * @throws JOceanusException on error
     */
    private WorkingCopy(final File pLocation,
                        final Branch pBranch,
                        final SVNRevision pRevision) throws JOceanusException {
        /* Store parameters */
        theBranch = pBranch;
        theLocation = pLocation;
        theAlias = theLocation.getName();
        theRevision = pRevision.getNumber();
        theUpdates = new UpdateStatusList();

        /* Access the logger */
        Logger myLogger = pBranch.getRepository().getLogger();

        /* Determine the location of the project definition */
        File myPom = ProjectDefinition.getProjectDefFile(theLocation);
        if (myPom != null) {
            theProject = ProjectDefinition.parseProjectFile(myLogger, myPom);
        }
    }

    /**
     * Discover updates.
     * @param pReport the report object
     * @throws JOceanusException on error
     */
    public void discoverUpdates(final ReportStatus pReport) throws JOceanusException {
        /* Access client */
        Repository myRepository = theBranch.getRepository();
        SVNClientManager myMgr = myRepository.getClientManager();
        SVNStatusClient myClient = myMgr.getStatusClient();

        /* Protect against exceptions */
        try {
            /* Access status of the directory */
            myClient.doStatus(theLocation, SVNRevision.HEAD, SVNDepth.INFINITY, false, false, false, false, new UpdateHandler(this), null);
        } catch (SVNException e) {
            throw new JThemisIOException("Unable to get status", e);
        } finally {
            myRepository.releaseClientManager(myMgr);
        }
    }

    /**
     * Status Handler.
     */
    private final class UpdateHandler
            implements ISVNStatusHandler {
        /**
         * The Working copy.
         */
        private final WorkingCopy theCopy;

        /**
         * Constructor.
         * @param pCopy the working copy
         */
        private UpdateHandler(final WorkingCopy pCopy) {
            theCopy = pCopy;
        }

        @Override
        public void handleStatus(final SVNStatus pStatus) throws SVNException {
            /* Create the new updateStatus and add to the list */
            UpdateStatus myStatus = new UpdateStatus(theCopy, pStatus);
            theUpdates.add(myStatus);
        }
    }

    @Override
    public int compareTo(final WorkingCopy pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the branches */
        return theBranch.compareTo(pThat.theBranch);
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
        if (pThat instanceof WorkingCopy) {
            return false;
        }
        WorkingCopy myThat = (WorkingCopy) pThat;

        /* Compare fields */
        return theBranch.equals(myThat.theBranch);
    }

    @Override
    public int hashCode() {
        return theBranch.hashCode();
    }

    /**
     * Get status for a file in a working copy.
     * @param pRepo the repository
     * @param pFile the file to get status for
     * @return the status (null if not under VC)
     * @throws JOceanusException on error
     */
    public static SVNStatus getFileStatus(final Repository pRepo,
                                          final File pFile) throws JOceanusException {
        /* File must exist */
        if (!pFile.exists()) {
            return null;
        }

        /* Access client */
        SVNClientManager myMgr = pRepo.getClientManager();
        SVNStatusClient myClient = myMgr.getStatusClient();

        /* Initialise the status */
        SVNStatus myStatus;

        /* Protect against exceptions */
        try {
            /* Access status of the file */
            myStatus = myClient.doStatus(pFile, false);
        } catch (SVNException e) {
            /* Access the error code */
            SVNErrorCode myCode = e.getErrorMessage().getErrorCode();

            /* Allow file/directory exists but is not WC */
            if ((myCode != SVNErrorCode.WC_NOT_FILE)
                && (myCode != SVNErrorCode.WC_NOT_DIRECTORY)) {
                throw new JThemisIOException("Unable to get status", e);
            }

            /* Set status to null */
            myStatus = null;
        }

        /* Release the client manager */
        pRepo.releaseClientManager(myMgr);
        return myStatus;
    }

    /**
     * Working Copy Set.
     */
    public static final class WorkingCopySet
            extends OrderedList<WorkingCopy>
            implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(WorkingCopySet.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Repository field id.
         */
        private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

        /**
         * Location field id.
         */
        private static final JDataField FIELD_LOC = FIELD_DEFS.declareLocalField("Location");

        @Override
        public String formatObject() {
            return "WorkingCopySet("
                   + size()
                   + ")";
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
            if (FIELD_REPO.equals(pField)) {
                return theRepository;
            }
            if (FIELD_LOC.equals(pField)) {
                return theLocation.getAbsolutePath();
            }

            /* Unknown */
            return JDataFieldValue.UNKNOWN;
        }

        /**
         * The repository for which these are working sets.
         */
        private final Repository theRepository;

        /**
         * The base location for the working sets.
         */
        private final File theLocation;

        /**
         * Get Location.
         * @return the location
         */
        public File getLocation() {
            return theLocation;
        }

        /**
         * Get Repository.
         * @return the repository
         */
        public Repository getRepository() {
            return theRepository;
        }

        /**
         * Constructor.
         * @param pRepository the repository
         * @param pLocation the location
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        public WorkingCopySet(final Repository pRepository,
                              final File pLocation,
                              final ReportStatus pReport) throws JOceanusException {
            /* Call super constructor */
            super(WorkingCopy.class);

            /* Store parameters */
            theRepository = pRepository;
            theLocation = pLocation;

            /* Analyse the WorkingCopySet */
            analyseWorkingCopySet(pReport);
        }

        /**
         * Constructor.
         * @param pRepository the repository
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        public WorkingCopySet(final Repository pRepository,
                              final ReportStatus pReport) throws JOceanusException {
            /* Call super constructor */
            super(WorkingCopy.class);

            /* Store parameters */
            theRepository = pRepository;

            /* Access preferences */
            SubVersionPreferences myPrefs = theRepository.getPreferences();
            String myLocation = myPrefs.getStringValue(SubVersionPreferences.NAME_SVN_WORK);
            theLocation = new File(myLocation);

            /* Analyse the WorkingCopySet */
            analyseWorkingCopySet(pReport);
        }

        /**
         * Analyse WorkingSet.
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        private void analyseWorkingCopySet(final ReportStatus pReport) throws JOceanusException {
            /* Report start of analysis */
            pReport.initTask("Analysing Working Copies");

            /* Locate working directories */
            locateWorkingDirectories(theLocation, pReport);

            /* Report number of stages */
            pReport.setNumStages(size() + 2);

            /* Access list iterator */
            Iterator<WorkingCopy> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Component */
                WorkingCopy myCopy = myIterator.next();

                /* Report stage of analysis */
                pReport.setNewStage("Analysing WC at "
                                    + myCopy.getLocation().getName());

                /* Discover updates */
                myCopy.discoverUpdates(pReport);
            }

            /* Report end of analysis */
            pReport.initTask("WorkingCopy Analysis complete");
        }

        /**
         * Locate working copies.
         * @param pLocation location
         * @param pReport the report object
         * @throws JOceanusException on error
         */
        private void locateWorkingDirectories(final File pLocation,
                                              final ReportStatus pReport) throws JOceanusException {
            /* Return if file is not a directory */
            if (!pLocation.isDirectory()) {
                return;
            }

            /* Access underlying files */
            for (File myFile : pLocation.listFiles()) {
                /* Ignore if file is not a directory */
                if (!myFile.isDirectory()) {
                    continue;
                }

                /* Ignore if file is special file/directory */
                if (myFile.getName().startsWith(".")) {
                    continue;
                }

                /* Access status for the file */
                SVNStatus myStatus = getFileStatus(theRepository, myFile);

                /* If this is a working copy */
                if (myStatus != null) {
                    /* Obtain the repository URL */
                    SVNURL myURL = myStatus.getRemoteURL();

                    /* Obtain the relevant branch in the repository */
                    Branch myBranch = theRepository.locateBranch(myURL);

                    /* If we found the branch */
                    if (myBranch != null) {
                        /* Create the working copy */
                        WorkingCopy myCopy = new WorkingCopy(myFile, myBranch, myStatus.getRevision());

                        /* Add to the list */
                        add(myCopy);
                    }
                }
            }
        }

        /**
         * Obtain locations array.
         * @return locations array
         */
        public File[] getLocationsArray() {
            /* Allocate array */
            File[] myFiles = new File[size()];
            int myFile = 0;

            /* Allocate the iterator */
            ListIterator<WorkingCopy> myIterator = listIterator();

            /* While there are entries */
            while (myIterator.hasNext()) {
                /* Access copy and add to files */
                WorkingCopy myCopy = myIterator.next();
                myFiles[myFile++] = myCopy.getLocation();
            }

            /* Return the array */
            return myFiles;
        }

        /**
         * Obtain active branch for component in working set.
         * @param pComponent the component
         * @return the active branch
         */
        public Branch getActiveBranch(final String pComponent) {
            /* Allocate the iterator */
            ListIterator<WorkingCopy> myIterator = listIterator();

            /* While there are entries */
            while (myIterator.hasNext()) {
                /* Access copy and obtain branch/component */
                WorkingCopy myCopy = myIterator.next();
                Branch myBranch = myCopy.getBranch();
                Component myComp = myBranch.getComponent();

                /* If this is the right component */
                if (myComp.getName().equals(pComponent)) {
                    /* return the branch */
                    return myBranch;
                }
            }

            /* Return null */
            return null;
        }
    }
}
