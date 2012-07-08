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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;

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

import uk.co.tolcroft.subversion.data.Tag.TagList;

/**
 * Represents a branch of a component in the repository.
 * @author Tony Washer
 */
public final class Branch implements Comparable<Branch> {
    /**
     * The branch prefix.
     */
    private static final String BRANCH_PREFIX = "v";

    /**
     * The branch prefix.
     */
    private static final String BRANCH_SEP = ".";

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Number of version parts.
     */
    private static final int NUM_VERS_PARTS = 3;

    /**
     * Parent Repository.
     */
    private final Repository theRepository;

    /**
     * Parent Component.
     */
    private final Component theComponent;

    /**
     * Major version.
     */
    private final int theMajorVersion;

    /**
     * Minor version.
     */
    private final int theMinorVersion;

    /**
     * Revision.
     */
    private final int theRevision;

    /**
     * TagList.
     */
    private final TagList theTags;

    /**
     * Get the repository for this branch.
     * @return the repository
     */
    public Repository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this branch.
     * @return the component
     */
    public Component getComponent() {
        return theComponent;
    }

    /**
     * Get the tag list for this branch.
     * @return the tag list
     */
    public TagList getTagList() {
        return theTags;
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pVersion the version string
     */
    private Branch(final Component pParent,
                   final String pVersion) {
        /* Store values */
        theComponent = pParent;
        theRepository = pParent.getRepository();

        /* Parse the version */
        String[] myParts = pVersion.split("\\" + BRANCH_SEP);

        /* If we do not have three parts reject it */
        if (myParts.length != NUM_VERS_PARTS) {
            throw new IllegalArgumentException();
        }

        /* Determine values */
        theMajorVersion = Integer.parseInt(myParts[0]);
        theMinorVersion = Integer.parseInt(myParts[1]);
        theRevision = Integer.parseInt(myParts[2]);

        /* Create tag list */
        theTags = new TagList(this);
    }

    /**
     * Constructor.
     * @param pParent the Parent component
     * @param pMajor the major version
     * @param pMinor the minor version
     * @param pRevision the revision
     */
    private Branch(final Component pParent,
                   final int pMajor,
                   final int pMinor,
                   final int pRevision) {
        /* Store values */
        theComponent = pParent;
        theRepository = pParent.getRepository();

        /* Determine values */
        theMajorVersion = pMajor;
        theMinorVersion = pMinor;
        theRevision = pRevision;

        /* Create tag list */
        theTags = new TagList(this);
    }

    /**
     * Get the branch name for this tag.
     * @return the branch name
     */
    public String getBranchName() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the version directory */
        myBuilder.append(BRANCH_PREFIX);
        myBuilder.append(theMajorVersion);
        myBuilder.append(BRANCH_SEP);
        myBuilder.append(theMinorVersion);
        myBuilder.append(BRANCH_SEP);
        myBuilder.append(theRevision);

        /* Return the branch name */
        return myBuilder.toString();
    }

    /**
     * Obtain repository path.
     * @return the Repository path for this branch
     */
    public String getPath() {
        /* Build the underlying string */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Build the initial path */
        myBuilder.append(theComponent.getBranchesPath());
        myBuilder.append(Repository.SEP_URL);

        /* Build the version directory */
        myBuilder.append(getBranchName());

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
    public int compareTo(final Branch pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the components */
        iCompare = theComponent.compareTo(pThat.theComponent);
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare versions numbers */
        if (theMajorVersion < pThat.theMajorVersion) {
            return -1;
        }
        if (theMajorVersion > pThat.theMajorVersion) {
            return 1;
        }
        if (theMinorVersion < pThat.theMinorVersion) {
            return -1;
        }
        if (theMinorVersion > pThat.theMinorVersion) {
            return 1;
        }
        if (theRevision < pThat.theRevision) {
            return -1;
        }
        if (theRevision > pThat.theRevision) {
            return 1;
        }
        return 0;
    }

    /**
     * List of branches.
     */
    public static final class BranchList {
        /**
         * The list of branches.
         */
        private final List<Branch> theList;

        /**
         * The parent component.
         */
        private final Component theComponent;

        /**
         * Obtain the branch list.
         * @return the branch list
         */
        public List<Branch> getList() {
            return theList;
        }

        /**
         * Discover branch list from repository.
         * @param pParent the parent component
         */
        protected BranchList(final Component pParent) {
            /* Create the list */
            theList = new ArrayList<Branch>();

            /* Store parent for use by entry handler */
            theComponent = pParent;
        }

        /**
         * Discover branch list from repository.
         * @throws JDataException on error
         */
        public void discover() throws JDataException {
            /* Reset the list */
            theList.clear();

            /* Access a LogClient */
            Repository myRepo = theComponent.getRepository();
            SVNClientManager myMgr = myRepo.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the branch directory URL */
                SVNURL myURL = SVNURL.parseURIDecoded(theComponent.getBranchesPath());

                /* List the branch directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES,
                                SVNDirEntry.DIRENT_ALL, new BranchHandler());

                /* Release the client manager */
                myRepo.releaseClientManager(myMgr);
            } catch (SVNException e) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Failed to discover branches for "
                        + theComponent.getName(), e);
            }
        }

        /**
         * Locate branch.
         * @param pURL the URL to locate
         * @return the relevant branch or Null
         */
        protected Branch locateBranch(final SVNURL pURL) {
            /* Access list iterator */
            ListIterator<Branch> myIterator = theList.listIterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();

                /* Access branch URL */
                SVNURL myBranchURL = myBranch.getURL();

                /* If this is parent of the passed URL */
                if ((pURL.getPath().equals(myBranchURL.getPath()))
                        || (pURL.getPath().startsWith(myBranchURL.getPath() + "/"))) {
                    /* This is the correct branch */
                    return myBranch;
                }
            }

            /* Not found */
            return null;
        }

        /**
         * Locate branch.
         * @param pBranch the branch
         * @return the relevant branch or Null
         */
        public Branch locateBranch(final Branch pBranch) {
            /* Access list iterator */
            ListIterator<Branch> myIterator = theList.listIterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Branch */
                Branch myBranch = myIterator.next();

                /* If this is the correct branch */
                int iCompare = myBranch.compareTo(pBranch);
                if (iCompare > 0) {
                    break;
                }
                if (iCompare < 0) {
                    continue;
                }
                return myBranch;
            }

            /* Not found */
            return null;
        }

        /**
         * Determine next major branch.
         * @return the major branch
         */
        public Branch nextMajorBranch() {
            /* Access list iterator */
            ListIterator<Branch> myIterator = theList.listIterator();
            Branch myBranch = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                myBranch = myIterator.next();
            }

            /* Determine the largest current major version */
            int myMajor = (myBranch == null) ? 0 : myBranch.theMajorVersion;

            /* Create the major revision */
            return new Branch(theComponent, myMajor + 1, 0, 0);
        }

        /**
         * Determine next minor branch.
         * @param pBase the branch to base from
         * @return the minor branch
         */
        public Branch nextMinorBranch(final Branch pBase) {
            /* Access major version */
            int myMajor = pBase.theMajorVersion;

            /* Access list iterator */
            ListIterator<Branch> myIterator = theList.listIterator();
            Branch myBranch = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                Branch myTest = myIterator.next();

                /* Handle wrong major version */
                if (myTest.theMajorVersion > myMajor) {
                    break;
                }
                if (myTest.theMajorVersion < myMajor) {
                    continue;
                }

                /* Record branch */
                myBranch = myTest;
            }

            /* Determine the largest current minor version */
            int myMinor = (myBranch == null) ? 0 : myBranch.theMinorVersion;

            /* Create the minor revision */
            return new Branch(theComponent, myMajor, myMinor + 1, 0);
        }

        /**
         * Determine next revision branch.
         * @param pBase the branch to base from
         * @return the minor branch
         */
        public Branch nextRevisionBranch(final Branch pBase) {
            /* Access major/minor version */
            int myMajor = pBase.theMajorVersion;
            int myMinor = pBase.theMinorVersion;

            /* Access list iterator */
            ListIterator<Branch> myIterator = theList.listIterator();
            Branch myBranch = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next branch */
                Branch myTest = myIterator.next();

                /* Handle wrong major/minor version */
                if (myTest.theMajorVersion > myMajor) {
                    break;
                }
                if (myTest.theMajorVersion < myMajor) {
                    continue;
                }
                if (myTest.theMinorVersion > myMinor) {
                    break;
                }
                if (myTest.theMinorVersion < myMinor) {
                    continue;
                }

                /* Record branch */
                myBranch = myTest;
            }

            /* Determine the largest current revision */
            int myRev = (myBranch == null) ? 0 : myBranch.theRevision;

            /* Create the minor revision */
            return new Branch(theComponent, myMajor, myMinor, myRev + 1);
        }

        /**
         * The Directory Entry Handler.
         */
        private final class BranchHandler implements ISVNDirEntryHandler {

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

                    /* Access the name and ignore if it does not start with v */
                    String myName = pEntry.getName();
                    if (!myName.startsWith(BRANCH_PREFIX)) {
                        return;
                    }
                    myName = myName.substring(1);

                    /* Create the branch and add to the list */
                    Branch myBranch = new Branch(theComponent, myName);
                    theList.add(myBranch);

                    /* Discover tags */
                    myBranch.getTagList().discover();
                } catch (JDataException e) {
                    /* Pass back as SVNException */
                    throw new SVNException(SVNErrorMessage.create(SVNErrorCode.UNKNOWN), e);
                }
            }
        }
    }
}
