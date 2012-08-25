/*******************************************************************************
 * Subversion: Java SubVersion Management
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JSvnManager.data;

import java.io.File;
import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JSortedList.OrderedList;
import net.sourceforge.JSvnManager.data.JSvnReporter.ReportStatus;
import net.sourceforge.JSvnManager.data.UpdateStatus.UpdateStatusList;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;

/**
 * Represents a Working extract copy of subversion.
 * @author Tony Washer
 */
public class WorkingCopy implements JDataContents, Comparable<WorkingCopy> {
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
            return getComponentName().equals(theAlias) ? JDataFieldValue.SkipField : theAlias;
        }
        if (FIELD_BRAN.equals(pField)) {
            return theBranch;
        }
        if (FIELD_COMP.equals(pField)) {
            return theBranch.getComponent();
        }
        if (FIELD_REVISION.equals(pField)) {
            return theRevision;
        }
        if (FIELD_UPDATES.equals(pField)) {
            return (theUpdates.size() == 0) ? JDataFieldValue.SkipField : theUpdates;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
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
        return getComponentName() + "_" + theBranch.getBranchName();
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
     * @throws JDataException on error
     */
    protected WorkingCopy(final File pLocation,
                          final Branch pBranch,
                          final SVNRevision pRevision) throws JDataException {
        /* Store parameters */
        theBranch = pBranch;
        theLocation = pLocation;
        theAlias = theLocation.getName();
        theRevision = pRevision.getNumber();
        theUpdates = new UpdateStatusList();

        /* Determine the location of the project definition */
        File myPom = ProjectDefinition.getProjectDefFile(theLocation);
        if (myPom != null) {
            theProject = new ProjectDefinition(myPom);
        }
    }

    /**
     * Discover updates.
     * @throws JDataException on error
     */
    public void discoverUpdates() throws JDataException {
        /* Access client */
        Repository myRepository = theBranch.getRepository();
        SVNClientManager myMgr = myRepository.getClientManager();
        SVNStatusClient myClient = myMgr.getStatusClient();

        /* Protect against exceptions */
        try {
            /* Access status of the directory */
            myClient.doStatus(theLocation, SVNRevision.HEAD, SVNDepth.INFINITY, false, false, false, false,
                              new UpdateHandler(this), null);
        } catch (SVNException e) {
            /* Allow file/directory exists but is not WC */
            throw new JDataException(ExceptionClass.SUBVERSION, "Unable to get status", e);
        }

        /* Release the client manager */
        myRepository.releaseClientManager(myMgr);
    }

    /**
     * Status Handler.
     */
    private final class UpdateHandler implements ISVNStatusHandler {
        /**
         * The Working copy.
         */
        private final WorkingCopy theCopy;

        /**
         * Constructor
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

    /**
     * Get status for a file in a working copy.
     * @param pRepo the repository
     * @param pFile the file to get status for
     * @return the status (null if not under VC)
     * @throws JDataException on error
     */
    public static SVNStatus getFileStatus(final Repository pRepo,
                                          final File pFile) throws JDataException {
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
            if ((myCode != SVNErrorCode.WC_NOT_FILE) && (myCode != SVNErrorCode.WC_NOT_DIRECTORY)) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Unable to get status", e);
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
    public static final class WorkingCopySet extends OrderedList<WorkingCopy> implements JDataContents {
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
            return "WorkingCopySet(" + size() + ")";
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
            return JDataFieldValue.UnknownField;
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
         * Constructor.
         * @param pRepository the repository
         * @param pLocation the location
         * @param pReport the report object
         * @throws JDataException on error
         */
        public WorkingCopySet(final Repository pRepository,
                              final String pLocation,
                              final ReportStatus pReport) throws JDataException {
            /* Call super constructor */
            super(WorkingCopy.class);

            /* Store parameters */
            theRepository = pRepository;
            theLocation = new File(pLocation);

            /* Locate working directories */
            locateWorkingDirectories(theLocation, pReport);
        }

        /**
         * Constructor.
         * @param pRepository the repository
         * @param pReport the report object
         * @throws JDataException on error
         */
        public WorkingCopySet(final Repository pRepository,
                              final ReportStatus pReport) throws JDataException {
            /* Call super constructor */
            super(WorkingCopy.class);

            /* Store parameters */
            theRepository = pRepository;

            /* Access preferences */
            SubVersionPreferences myPrefs = theRepository.getPreferences();
            String myLocation = myPrefs.getStringValue(SubVersionPreferences.NAME_SVN_WORK);
            theLocation = new File(myLocation);

            /* Report start of analysis */
            pReport.reportStatus("Analysing Working Copies");

            /* Locate working directories */
            locateWorkingDirectories(theLocation, pReport);

            /* Report end of analysis */
            pReport.reportStatus("WorkingCopy Analysis complete");
        }

        /**
         * Locate working copies.
         * @param pLocation location
         * @param pReport the report object
         * @throws JDataException on error
         */
        private void locateWorkingDirectories(final File pLocation,
                                              final ReportStatus pReport) throws JDataException {
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

                        /* Report end of analysis */
                        pReport.reportStatus("Analysing WC at " + myFile.getName());

                        /* Discover updates */
                        myCopy.discoverUpdates();
                    }
                }
            }
        }

        /**
         * Obtain locations array.
         * @return locations array
         */
        protected File[] getLocationsArray() {
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

        /**
         * Revert changes across working set.
         * @throws SVNException on error
         */
        public void revertChanges() throws SVNException {
            /* Access the array of locations */
            File[] myLocations = getLocationsArray();

            /* Access WorkingCopy client */
            SVNWCClient myClient = theRepository.getClientManager().getWCClient();

            /* Revert changes */
            myClient.doRevert(myLocations, SVNDepth.INFINITY, null);
        }

        /**
         * Access updates across working set.
         * @throws SVNException on error
         */
        public void updateWorkingSets() throws SVNException {
            /* Access the array of locations */
            File[] myLocations = getLocationsArray();

            /* Access Update client */
            SVNUpdateClient myClient = theRepository.getClientManager().getUpdateClient();

            /* Refresh changes */
            myClient.doUpdate(myLocations, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
        }
    }
}
