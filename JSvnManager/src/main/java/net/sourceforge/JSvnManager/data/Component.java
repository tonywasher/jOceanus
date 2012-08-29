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

import java.util.Iterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JSortedList.OrderedList;
import net.sourceforge.JSvnManager.data.Branch.BranchList;
import net.sourceforge.JSvnManager.data.JSvnReporter.ReportStatus;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Represents a component in the repository.
 * @author Tony Washer
 */
public final class Component implements JDataContents, Comparable<Component> {
    /**
     * The branches directory.
     */
    private static final String DIR_BRANCHES = "branches";

    /**
     * The tags directory.
     */
    private static final String DIR_TAGS = "tags";

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Component.class.getSimpleName());

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Name field id.
     */
    private static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Branches field id.
     */
    private static final JDataField FIELD_BRAN = FIELD_DEFS.declareLocalField("Branches");

    @Override
    public String formatObject() {
        return theName;
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle standard fields */
        if (FIELD_REPO.equals(pField)) {
            return theRepository;
        }
        if (FIELD_NAME.equals(pField)) {
            return theName;
        }
        if (FIELD_BRAN.equals(pField)) {
            return theBranches;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Parent Repository.
     */
    private final Repository theRepository;

    /**
     * Component Name.
     */
    private final String theName;

    /**
     * BranchList.
     */
    private final BranchList theBranches;

    /**
     * Get the repository for this component.
     * @return the repository
     */
    public Repository getRepository() {
        return theRepository;
    }

    /**
     * Obtain the component name.
     * @return the component name
     */
    public String getName() {
        return theName;
    }

    /**
     * Get the branch list for this branch.
     * @return the branch list
     */
    public BranchList getBranchList() {
        return theBranches;
    }

    /**
     * Constructor.
     * @param pParent the Parent repository
     * @param pName the component name
     */
    private Component(final Repository pParent,
                      final String pName) {
        /* Store values */
        theName = pName;
        theRepository = pParent;

        /* Create branch list */
        theBranches = new BranchList(this);
    }

    /**
     * Obtain repository path for the branches.
     * @return the Branches path for the component
     */
    public String getBranchesPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Access the branch path */
        myBuilder.append(getURLPath());

        /* Add the tags directory */
        myBuilder.append(Repository.SEP_URL);
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
        myBuilder.append(Repository.SEP_URL);
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
        myBuilder.append(theRepository.getPath());

        /* Build the component directory */
        myBuilder.append(Repository.SEP_URL);
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
            return SVNURL.parseURIDecoded(getURLPath());
        } catch (SVNException e) {
            return null;
        }
    }

    @Override
    public int compareTo(final Component pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the repositories */
        iCompare = theRepository.compareTo(pThat.theRepository);
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare names */
        return theName.compareTo(pThat.theName);
    }

    /**
     * List of components.
     */
    public static final class ComponentList extends OrderedList<Component> implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(ComponentList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return "ComponentList(" + size() + ")";
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

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        /**
         * Parent Repository.
         */
        private final Repository theRepository;

        /**
         * Constructor.
         * @param pParent the parent repository
         */
        public ComponentList(final Repository pParent) {
            /* Call super constructor */
            super(Component.class);

            /* Store parent/manager for use by entry handler */
            theRepository = pParent;
        }

        /**
         * Discover component list from repository.
         * @param pReport the report object
         * @throws JDataException on error
         */
        public void discover(final ReportStatus pReport) throws JDataException {
            /* Reset the list */
            clear();

            /* Access a LogClient */
            SVNClientManager myMgr = theRepository.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the component directory URL */
                SVNURL myURL = SVNURL.parseURIDecoded(theRepository.getPath());

                /* List the component directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES,
                                SVNDirEntry.DIRENT_ALL, new ListDirHandler());

                /* Release the client manager */
                theRepository.releaseClientManager(myMgr);
            } catch (SVNException e) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Failed to discover components for "
                        + theRepository.getName(), e);
            }

            /* Access list iterator */
            Iterator<Component> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Component */
                Component myComponent = myIterator.next();
                BranchList myBranches = myComponent.getBranchList();

                /* Report discovery of component */
                pReport.reportStatus("Analysing component " + myComponent.getName());

                /* Discover branches for the component */
                myBranches.discover();
            }

            /* Register dependencies */
            registerDependencies();

            /* Propagate dependencies */
            propagateDependencies();
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected Branch locateBranch(final SVNURL pURL) {
            /* Access list iterator */
            Iterator<Component> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Component */
                Component myComponent = myIterator.next();

                /* Access branch URL */
                SVNURL myCompURL = myComponent.getURL();

                /* Skip if the repositories are different */
                if (!pURL.getHost().equals(myCompURL.getHost())) {
                    continue;
                }

                /* If this is parent of the passed URL */
                if (pURL.getPath().startsWith(myCompURL.getPath())) {
                    /* Look at this components branches */
                    return myComponent.getBranchList().locateBranch(pURL);
                }
            }

            /* Not found */
            return null;
        }

        /**
         * registerDependencies.
         * @throws JDataException on error
         */
        private void registerDependencies() throws JDataException {
            /* Access list iterator */
            Iterator<Component> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Component */
                Component myComponent = myIterator.next();

                /* register dependencies */
                BranchList myBranches = myComponent.getBranchList();
                myBranches.registerDependencies();
            }
        }

        /**
         * propagateDependencies.
         * @throws JDataException on error
         */
        protected void propagateDependencies() throws JDataException {
            /* Access list iterator */
            Iterator<Component> myIterator = iterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Component and resolve dependencies */
                Component myComp = myIterator.next();
                BranchList myBranches = myComp.getBranchList();
                myBranches.propagateDependencies();
            }
        }

        /**
         * The Directory Entry Handler.
         */
        private final class ListDirHandler implements ISVNDirEntryHandler {
            @Override
            public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
                /* Ignore if not a directory and if it is top-level */
                if (pEntry.getKind() != SVNNodeKind.DIR) {
                    return;
                }
                if (pEntry.getRelativePath().length() == 0) {
                    return;
                }

                /* Access the name */
                String myName = pEntry.getName();

                /* Create the tag and add to the list */
                Component myComp = new Component(theRepository, myName);
                add(myComp);
            }
        }
    }
}
