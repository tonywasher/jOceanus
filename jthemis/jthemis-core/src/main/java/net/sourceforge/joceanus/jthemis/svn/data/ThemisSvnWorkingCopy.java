/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.list.MetisOrderedList;
import net.sourceforge.joceanus.jmetis.threads.MetisThreadStatusReport;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisIOException;
import net.sourceforge.joceanus.jthemis.scm.maven.ThemisMvnProjectDefinition;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferenceKey;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnPreference.ThemisSvnPreferences;
import net.sourceforge.joceanus.jthemis.svn.data.ThemisSvnUpdateStatus.UpdateStatusList;

/**
 * Represents a Working extract copy of subversion.
 * @author Tony Washer
 */
public final class ThemisSvnWorkingCopy
        implements MetisDataContents, Comparable<ThemisSvnWorkingCopy> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(ThemisSvnWorkingCopy.class.getSimpleName());

    /**
     * Component field id.
     */
    private static final MetisField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    /**
     * Branch field id.
     */
    private static final MetisField FIELD_BRAN = FIELD_DEFS.declareEqualityField("Branch");

    /**
     * Alias field id.
     */
    private static final MetisField FIELD_ALIAS = FIELD_DEFS.declareLocalField("Alias");

    /**
     * Revision field id.
     */
    private static final MetisField FIELD_REVISION = FIELD_DEFS.declareLocalField("Revision");

    /**
     * Project field id.
     */
    private static final MetisField FIELD_PROJECT = FIELD_DEFS.declareLocalField("Project");

    /**
     * Update list field id.
     */
    private static final MetisField FIELD_UPDATES = FIELD_DEFS.declareLocalField("Updates");

    /**
     * The branch associated with the working copy.
     */
    private final ThemisSvnBranch theBranch;

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
    private ThemisMvnProjectDefinition theProject = null;

    /**
     * Constructor.
     * @param pLocation the location
     * @param pBranch the branch
     * @param pRevision the checked out revision
     * @throws OceanusException on error
     */
    private ThemisSvnWorkingCopy(final File pLocation,
                                 final ThemisSvnBranch pBranch,
                                 final SVNRevision pRevision) throws OceanusException {
        /* Store parameters */
        theBranch = pBranch;
        theLocation = pLocation;
        theAlias = theLocation.getName();
        theRevision = pRevision.getNumber();
        theUpdates = new UpdateStatusList();

        /* Determine the location of the project definition */
        File myPom = ThemisMvnProjectDefinition.getProjectDefFile(theLocation);
        if (myPom != null) {
            theProject = ThemisMvnProjectDefinition.parseProjectFile(myPom);
        }
    }

    @Override
    public String formatObject() {
        return getFullName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle standard fields */
        if (FIELD_ALIAS.equals(pField)) {
            return getComponentName().equals(theAlias)
                                                       ? MetisFieldValue.SKIP
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
            return theUpdates.isEmpty()
                                        ? MetisFieldValue.SKIP
                                        : theUpdates;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * Get branch.
     * @return the branch
     */
    public ThemisSvnBranch getBranch() {
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
    public ThemisMvnProjectDefinition getProjectDefinition() {
        return theProject;
    }

    /**
     * Discover updates.
     * @param pReport the report object
     * @throws OceanusException on error
     */
    public void discoverUpdates(final MetisThreadStatusReport pReport) throws OceanusException {
        /* Access client */
        ThemisSvnRepository myRepository = theBranch.getRepository();
        SVNClientManager myMgr = myRepository.getClientManager();
        SVNStatusClient myClient = myMgr.getStatusClient();

        /* Protect against exceptions */
        try {
            /* Access status of the directory */
            myClient.doStatus(theLocation, SVNRevision.HEAD, SVNDepth.INFINITY, false, false, false, false, new UpdateHandler(this), null);
        } catch (SVNException e) {
            throw new ThemisIOException("Unable to get status", e);
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
        private final ThemisSvnWorkingCopy theCopy;

        /**
         * Constructor.
         * @param pCopy the working copy
         */
        private UpdateHandler(final ThemisSvnWorkingCopy pCopy) {
            theCopy = pCopy;
        }

        @Override
        public void handleStatus(final SVNStatus pStatus) throws SVNException {
            /* Create the new updateStatus and add to the list */
            ThemisSvnUpdateStatus myStatus = new ThemisSvnUpdateStatus(theCopy, pStatus);
            theUpdates.add(myStatus);
        }
    }

    @Override
    public int compareTo(final ThemisSvnWorkingCopy pThat) {
        /* Handle trivial cases */
        if (this.equals(pThat)) {
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
        if (pThat instanceof ThemisSvnWorkingCopy) {
            return false;
        }
        ThemisSvnWorkingCopy myThat = (ThemisSvnWorkingCopy) pThat;

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
     * @throws OceanusException on error
     */
    public static SVNStatus getFileStatus(final ThemisSvnRepository pRepo,
                                          final File pFile) throws OceanusException {
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
            if (!myCode.equals(SVNErrorCode.WC_NOT_FILE)
                && !myCode.equals(SVNErrorCode.WC_NOT_DIRECTORY)) {
                throw new ThemisIOException("Unable to get status", e);
            }

            /* Set status to null */
            myStatus = null;
        } finally {
            /* Release the client manager */
            pRepo.releaseClientManager(myMgr);
        }

        /* Return the status */
        return myStatus;
    }

    /**
     * Working Copy Set.
     */
    public static final class SvnWorkingCopySet
            extends MetisOrderedList<ThemisSvnWorkingCopy>
            implements MetisDataContents {
        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(SvnWorkingCopySet.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Repository field id.
         */
        private static final MetisField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

        /**
         * Location field id.
         */
        private static final MetisField FIELD_LOC = FIELD_DEFS.declareLocalField("Location");

        /**
         * The repository for which these are working sets.
         */
        private final ThemisSvnRepository theRepository;

        /**
         * The base location for the working sets.
         */
        private final File theLocation;

        /**
         * Constructor.
         * @param pRepository the repository
         * @param pLocation the location
         * @param pReport the report object
         * @throws OceanusException on error
         */
        public SvnWorkingCopySet(final ThemisSvnRepository pRepository,
                                 final File pLocation,
                                 final MetisThreadStatusReport pReport) throws OceanusException {
            /* Call super constructor */
            super(ThemisSvnWorkingCopy.class);

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
         * @throws OceanusException on error
         */
        public SvnWorkingCopySet(final ThemisSvnRepository pRepository,
                                 final MetisThreadStatusReport pReport) throws OceanusException {
            /* Call super constructor */
            super(ThemisSvnWorkingCopy.class);

            /* Store parameters */
            theRepository = pRepository;

            /* Access preferences */
            ThemisSvnPreferences myPrefs = theRepository.getPreferences();
            String myLocation = myPrefs.getStringValue(ThemisSvnPreferenceKey.WORK);
            theLocation = new File(myLocation);

            /* Analyse the WorkingCopySet */
            analyseWorkingCopySet(pReport);
        }

        @Override
        public String formatObject() {
            return "WorkingCopySet(" + size() + ")";
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
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
            return MetisFieldValue.UNKNOWN;
        }

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
        public ThemisSvnRepository getRepository() {
            return theRepository;
        }

        /**
         * Analyse WorkingSet.
         * @param pReport the report object
         * @throws OceanusException on error
         */
        private void analyseWorkingCopySet(final MetisThreadStatusReport pReport) throws OceanusException {
            /* Report start of analysis */
            pReport.initTask("Analysing Working Copies");

            /* Locate working directories */
            locateWorkingDirectories(theLocation);

            /* Report number of stages */
            pReport.setNumStages(size() + 2);

            /* Access list iterator */
            Iterator<ThemisSvnWorkingCopy> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Component */
                ThemisSvnWorkingCopy myCopy = myIterator.next();

                /* Report stage of analysis */
                pReport.setNewStage("Analysing WC at " + myCopy.getLocation().getName());

                /* Discover updates */
                myCopy.discoverUpdates(pReport);
            }

            /* Report end of analysis */
            pReport.initTask("WorkingCopy Analysis complete");
        }

        /**
         * Locate working copies.
         * @param pLocation location
         * @throws OceanusException on error
         */
        private void locateWorkingDirectories(final File pLocation) throws OceanusException {
            /* Return if file is not a directory */
            if (!pLocation.isDirectory()) {
                return;
            }

            /* Check for top-level checkout */
            registerBranch(pLocation);

            /* Access underlying files */
            for (File myFile : pLocation.listFiles()) {
                /* Ignore if file is not a directory */
                registerBranch(myFile);
            }
        }

        /**
         * Register a check out branch if represented by file.
         * @param pFile the file
         * @throws OceanusException on error
         */
        private void registerBranch(final File pFile) throws OceanusException {
            /* Ignore if file is not a directory */
            if (!pFile.isDirectory()) {
                return;
            }

            /* Ignore if file is special file/directory */
            if (pFile.getName().startsWith(".")) {
                return;
            }

            /* Access status for the directory */
            SVNStatus myStatus = getFileStatus(theRepository, pFile);

            /* If this is a working copy */
            if ((myStatus != null) && myStatus.isVersioned()) {
                /* Obtain the repository URL */
                SVNURL myURL = myStatus.getRemoteURL();

                /* Obtain the relevant branch in the repository */
                ThemisSvnBranch myBranch = theRepository.locateBranch(myURL);

                /* If we found the branch */
                if (myBranch != null) {
                    /* Create the working copy */
                    ThemisSvnWorkingCopy myCopy = new ThemisSvnWorkingCopy(pFile, myBranch, myStatus.getRevision());

                    /* If the element is not already in the list */
                    if (indexOf(myCopy) == -1) {
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
            ListIterator<ThemisSvnWorkingCopy> myIterator = listIterator();

            /* While there are entries */
            while (myIterator.hasNext()) {
                /* Access copy and add to files */
                ThemisSvnWorkingCopy myCopy = myIterator.next();
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
        public ThemisSvnBranch getActiveBranch(final String pComponent) {
            /* Allocate the iterator */
            ListIterator<ThemisSvnWorkingCopy> myIterator = listIterator();

            /* While there are entries */
            while (myIterator.hasNext()) {
                /* Access copy and obtain branch/component */
                ThemisSvnWorkingCopy myCopy = myIterator.next();
                ThemisSvnBranch myBranch = myCopy.getBranch();
                ThemisSvnComponent myComp = myBranch.getComponent();

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
