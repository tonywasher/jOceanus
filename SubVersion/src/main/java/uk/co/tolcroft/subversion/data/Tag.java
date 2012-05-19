/*******************************************************************************
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

import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;

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
 * Represents a tag of a branch
 * @author Tony
 */
public class Tag {
    /**
     * The tag prefix
     */
    protected static final String tagPrefix = "-b";

    /**
     * Parent Repository
     */
    private final Repository theRepository;

    /**
     * Parent Component
     */
    private final Component theComponent;

    /**
     * The Branch to which this Tag belongs
     */
    private final Branch theBranch;

    /**
     * The Tag number
     */
    private final int theTag;

    /**
     * Constructor
     * @param pParent the Parent branch
     * @param pTag the tag number
     */
    private Tag(Branch pParent,
                int pTag) {
        /* Store values */
        theBranch = pParent;
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
        theTag = pTag;
    }

    /**
     * Get the tag name for this tag
     * @return the tag name
     */
    public String getTagName() {
        return theBranch.getBranchName() + tagPrefix + theTag;
    }

    /**
     * Get the repository for this tag
     * @return the repository
     */
    public Repository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this tag
     * @return the component
     */
    public Component getComponent() {
        return theComponent;
    }

    /**
     * Get the branch for this tag
     * @return the branch
     */
    public Branch getBranch() {
        return theBranch;
    }

    /**
     * Obtain repository path for the tag
     * @return the Repository path for this tag
     */
    public String getPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(100);

        /* Add the tags directory */
        myBuilder.append(theComponent.getTagsPath());
        myBuilder.append(Repository.theURLSep);

        /* Build the tag directory */
        myBuilder.append(getTagName());

        /* Create the repository path */
        return myBuilder.toString();
    }

    /**
     * Obtain URL
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

    /**
     * Compare this tag to another tag
     * @param pThat the other tag
     * @return -1 if earlier tag, 0 if equal tag, 1 if later tag
     */
    public int compareTo(Tag pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this == pThat)
            return 0;
        if (pThat == null)
            return -1;

        /* Compare the branches */
        iCompare = theBranch.compareTo(pThat.theBranch);
        if (iCompare != 0)
            return iCompare;

        /* Compare tag numbers */
        if (theTag > pThat.theTag)
            return -1;
        if (theTag < pThat.theTag)
            return 1;
        return 0;
    }

    /**
     * List of tags
     */
    public static class TagList {
        /**
         * The list of tags
         */
        private final List<Tag> theList;

        /**
         * Parent Component
         */
        private final Component theComponent;

        /**
         * The parent branch
         */
        private final Branch theBranch;

        /**
         * The prefix
         */
        private final String thePrefix;

        /**
         * Obtain the tag list
         * @return the tag list
         */
        public List<Tag> getList() {
            return theList;
        }

        /**
         * Discover tag list from repository
         * @param pParent the parent branch
         */
        protected TagList(Branch pParent) {
            /* Create the list */
            theList = new ArrayList<Tag>();

            /* Store parent for use by entry handler */
            theBranch = pParent;
            theComponent = pParent.getComponent();

            /* Build prefix */
            thePrefix = theBranch.getBranchName() + tagPrefix;
        }

        /**
         * Discover tag list from repository
         * @throws ModelException
         */
        public void discover() throws ModelException {
            /* Reset the list */
            theList.clear();

            /* Access a LogClient */
            Repository myRepo = theComponent.getRepository();
            SVNClientManager myMgr = myRepo.getClientManager();
            SVNLogClient myClient = myMgr.getLogClient();

            /* Protect against exceptions */
            try {
                /* Access the tags directory URL */
                SVNURL myURL = SVNURL.parseURIDecoded(theComponent.getTagsPath());

                /* List the tag directories */
                myClient.doList(myURL, SVNRevision.HEAD, SVNRevision.HEAD, false, SVNDepth.IMMEDIATES,
                                SVNDirEntry.DIRENT_ALL, new TagHandler());

                /* Release the client manager */
                myRepo.releaseClientManager(myMgr);
            }

            catch (SVNException e) {
                throw new ModelException(ExceptionClass.SUBVERSION, "Failed to discover tags for "
                        + theBranch.getBranchName(), e);
            }
        }

        /**
         * Locate tag
         * @param pTag
         * @return the relevant tag or Null
         */
        public Tag locateTag(Tag pTag) {
            /* Access list iterator */
            ListIterator<Tag> myIterator = theList.listIterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Tag */
                Tag myTag = myIterator.next();

                /* If this is the correct tag */
                int iCompare = myTag.compareTo(pTag);
                if (iCompare > 0)
                    break;
                if (iCompare < 0)
                    continue;
                return myTag;
            }

            /* Not found */
            return null;
        }

        /**
         * Determine next tag
         * @return the next tag
         */
        public Tag nextTag() {
            /* Access list iterator */
            ListIterator<Tag> myIterator = theList.listIterator();
            Tag myTag = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next tag */
                myTag = myIterator.next();
            }

            /* Determine the largest current tag */
            int myTagNo = (myTag == null) ? 0 : myTag.theTag;

            /* Create the major revision */
            return new Tag(theBranch, myTagNo + 1);
        }

        /**
         * The Directory Entry Handler
         */
        private class TagHandler implements ISVNDirEntryHandler {

            @Override
            public void handleDirEntry(SVNDirEntry pEntry) throws SVNException {
                /* Ignore if not a directory and if it is top-level */
                if (pEntry.getKind() != SVNNodeKind.DIR)
                    return;
                if (pEntry.getRelativePath().length() == 0)
                    return;

                /* Access the name and ignore if it does not start with correct prefix */
                String myName = pEntry.getName();
                if (!myName.startsWith(thePrefix))
                    return;
                myName = myName.substring(thePrefix.length());

                /* Determine tag */
                int myTagNo = Integer.parseInt(myName);

                /* Create the tag and add to the list */
                Tag myTag = new Tag(theBranch, myTagNo);
                theList.add(myTag);
            }
        }
    }
}
