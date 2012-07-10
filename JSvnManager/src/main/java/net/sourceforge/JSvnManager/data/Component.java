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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JSvnManager.data.Branch.BranchList;

import org.tmatesoft.svn.core.ISVNDirEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
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
public final class Component implements Comparable<Component> {
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
        myBuilder.append(getPath());

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
        myBuilder.append(getPath());

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
    public String getPath() {
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
            return SVNURL.parseURIDecoded(getPath());
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
    public static final class ComponentList {
        /**
         * The list of components.
         */
        private final List<Component> theList;

        /**
         * Parent Repository.
         */
        private final Repository theRepository;

        /**
         * Obtain the component list.
         * @return the component list
         */
        public List<Component> getList() {
            return theList;
        }

        /**
         * Constructor.
         * @param pParent the parent repository
         */
        public ComponentList(final Repository pParent) {
            /* Create the list */
            theList = new ArrayList<Component>();

            /* Store parent/manager for use by entry handler */
            theRepository = pParent;
        }

        /**
         * Discover component list from repository.
         * @throws JDataException on error
         */
        public void discover() throws JDataException {
            /* Reset the list */
            theList.clear();

            /* Access a LogClient */
            SVNClientManager myMgr = theRepository.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the component directory URL */
                SVNURL myURL = SVNURL.parseURIDecoded(theRepository.getPath());

                /* List the component directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES,
                                SVNDirEntry.DIRENT_ALL, new ComponentHandler());

                /* Release the client manager */
                theRepository.releaseClientManager(myMgr);
            } catch (SVNException e) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Failed to discover components for "
                        + theRepository.getName(), e);
            }
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected Branch locateBranch(final SVNURL pURL) {
            /* Access list iterator */
            ListIterator<Component> myIterator = theList.listIterator();

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
         * The Directory Entry Handler.
         */
        private final class ComponentHandler implements ISVNDirEntryHandler {

            @Override
            public void handleDirEntry(final SVNDirEntry pEntry) throws SVNException {
                /* Protect against exceptions */
                try {
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
                    theList.add(myComp);

                    /* Discover tags */
                    myComp.getBranchList().discover();
                } catch (JDataException e) {
                    /* Pass back as SVNException */
                    throw new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN), e);
                }
            }
        }
    }
}
