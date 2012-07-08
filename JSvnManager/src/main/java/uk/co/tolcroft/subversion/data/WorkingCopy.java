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
package uk.co.tolcroft.subversion.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
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
public class WorkingCopy {
    /**
     * The branch associated with the working copy.
     */
    private final Branch theBranch;

    /**
     * The sub-path of the branch that is checked out.
     */
    private final String theSubPath;

    /**
     * The path at which the branch is checked out.
     */
    private final File theLocation;

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
     * Get subPath.
     * @return the subPath
     */
    public String getSubPath() {
        return theSubPath;
    }

    /**
     * Get Location.
     * @return the location
     */
    public File getLocation() {
        return theLocation;
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
     * @param pSource the source URL
     * @throws JDataException on error
     */
    protected WorkingCopy(final File pLocation,
                          final Branch pBranch,
                          final SVNURL pSource) throws JDataException {
        /* Store parameters */
        theBranch = pBranch;
        theLocation = pLocation;

        /* Access branch path and source path */
        String myPath = theBranch.getPath();
        String myURL = pSource.toDecodedString();

        /* Access the SubPath */
        theSubPath = myURL.substring(myPath.length());

        /* Determine the location of the project definition */
        File myPom = ProjectDefinition.getProjectDefFile(theLocation);
        if (myPom != null) {
            theProject = new ProjectDefinition(myPom);
        }
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
        SVNClientManager myMgr = pRepo.getClientManager();
        SVNStatusClient myClient = myMgr.getStatusClient();

        /* File must exist */
        if (!pFile.exists()) {
            return null;
        }

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
    public static final class WorkingCopySet {
        /**
         * The repository for which these are working sets.
         */
        private final Repository theRepository;

        /**
         * The base location for the working sets.
         */
        private final File theLocation;

        /**
         * The list of WorkingCopys.
         */
        private final List<WorkingCopy> theList;

        /**
         * Constructor.
         * @param pRepository the repository
         * @param pLocation the location
         * @throws JDataException on error
         */
        public WorkingCopySet(final Repository pRepository,
                              final String pLocation) throws JDataException {
            /* Store parameters */
            theRepository = pRepository;
            theLocation = new File(pLocation);

            /* Allocate the list */
            theList = new ArrayList<WorkingCopy>();

            /* Locate working directories */
            locateWorkingDirectories(theLocation);
        }

        /**
         * Locate working copies.
         * @param pLocation location
         * @throws JDataException on error
         */
        private void locateWorkingDirectories(final File pLocation) throws JDataException {
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
                        WorkingCopy myCopy = new WorkingCopy(myFile, myBranch, myURL);

                        /* Add to the list */
                        theList.add(myCopy);
                    }

                    /* else try under this directory */
                } else {
                    locateWorkingDirectories(myFile);
                }
            }
        }

        /**
         * Obtain locations array.
         * @return locations array
         */
        protected File[] getLocationsArray() {
            /* Allocate array */
            File[] myFiles = new File[theList.size()];
            int myFile = 0;

            /* Allocate the iterator */
            ListIterator<WorkingCopy> myIterator = theList.listIterator();

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
            ListIterator<WorkingCopy> myIterator = theList.listIterator();

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
