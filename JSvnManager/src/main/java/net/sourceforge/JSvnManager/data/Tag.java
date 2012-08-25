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

import java.util.ListIterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JSortedList.OrderedList;

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
 * Represents a tag of a branch.
 * @author Tony
 */
public final class Tag implements JDataContents, Comparable<Tag> {
    /**
     * The tag prefix.
     */
    private static final String PREFIX_TAG = "-b";

    /**
     * The buffer length.
     */
    private static final int BUFFER_LEN = 100;

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(Tag.class.getSimpleName());

    /**
     * Repository field id.
     */
    private static final JDataField FIELD_REPO = FIELD_DEFS.declareEqualityField("Repository");

    /**
     * Component field id.
     */
    private static final JDataField FIELD_COMP = FIELD_DEFS.declareEqualityField("Component");

    /**
     * Branch field id.
     */
    private static final JDataField FIELD_BRAN = FIELD_DEFS.declareEqualityField("Branch");

    /**
     * Name field id.
     */
    private static final JDataField FIELD_NAME = FIELD_DEFS.declareEqualityField("Name");

    /**
     * Revision field id.
     */
    private static final JDataField FIELD_LREV = FIELD_DEFS.declareLocalField("Revision");

    @Override
    public String formatObject() {
        return getTagName();
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
        if (FIELD_COMP.equals(pField)) {
            return theComponent;
        }
        if (FIELD_BRAN.equals(pField)) {
            return theBranch;
        }
        if (FIELD_NAME.equals(pField)) {
            return getTagName();
        }
        if (FIELD_LREV.equals(pField)) {
            return theRevision;
        }

        /* Unknown */
        return JDataFieldValue.UnknownField;
    }

    /**
     * Parent Repository.
     */
    private final Repository theRepository;

    /**
     * Parent Component.
     */
    private final Component theComponent;

    /**
     * The Branch to which this Tag belongs.
     */
    private final Branch theBranch;

    /**
     * The Tag number.
     */
    private final int theTag;

    /**
     * The Last Changed Revision.
     */
    private final long theRevision;

    /**
     * Constructor.
     * @param pParent the Parent branch
     * @param pTag the tag number
     * @param pRevision the revision that created this tag
     */
    private Tag(final Branch pParent,
                final int pTag,
                final long pRevision) {
        /* Store values */
        theBranch = pParent;
        theRepository = pParent.getRepository();
        theComponent = pParent.getComponent();
        theTag = pTag;
        theRevision = pRevision;
    }

    /**
     * Get the tag name for this tag.
     * @return the tag name
     */
    public String getTagName() {
        return theBranch.getBranchName() + PREFIX_TAG + theTag;
    }

    /**
     * Get the repository for this tag.
     * @return the repository
     */
    public Repository getRepository() {
        return theRepository;
    }

    /**
     * Get the component for this tag.
     * @return the component
     */
    public Component getComponent() {
        return theComponent;
    }

    /**
     * Get the branch for this tag.
     * @return the branch
     */
    public Branch getBranch() {
        return theBranch;
    }

    /**
     * Get the revision for this tag.
     * @return the revision
     */
    public long getRevision() {
        return theRevision;
    }

    /**
     * Obtain repository path for the tag.
     * @return the Repository path for this tag
     */
    public String getURLPath() {
        /* Allocate a builder */
        StringBuilder myBuilder = new StringBuilder(BUFFER_LEN);

        /* Add the tags directory */
        myBuilder.append(theComponent.getTagsPath());
        myBuilder.append(Repository.SEP_URL);

        /* Build the tag directory */
        myBuilder.append(getTagName());

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
    public int compareTo(final Tag pThat) {
        int iCompare;

        /* Handle trivial cases */
        if (this == pThat) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Compare the branches */
        iCompare = theBranch.compareTo(pThat.theBranch);
        if (iCompare != 0) {
            return iCompare;
        }

        /* Compare tag numbers */
        if (theTag > pThat.theTag) {
            return -1;
        }
        if (theTag < pThat.theTag) {
            return 1;
        }
        return 0;
    }

    /**
     * List of tags.
     */
    public static final class TagList extends OrderedList<Tag> implements JDataContents {
        /**
         * Report fields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(TagList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Last Revision field id.
         */
        private static final JDataField FIELD_LREV = FIELD_DEFS.declareLocalField("LastRevision");

        @Override
        public String formatObject() {
            return "TagList(" + size() + ")";
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
            if (FIELD_LREV.equals(pField)) {
                return theLastRevision;
            }

            /* Unknown */
            return JDataFieldValue.UnknownField;
        }

        /**
         * Parent Component.
         */
        private final Component theComponent;

        /**
         * The parent branch.
         */
        private final Branch theBranch;

        /**
         * The prefix.
         */
        private final String thePrefix;

        /**
         * The last revision.
         */
        private long theLastRevision = -1;

        /**
         * Get the last revision for this tag list.
         * @return the last revision
         */
        public long getLastRevision() {
            return theLastRevision;
        }

        /**
         * Constructor.
         * @param pParent the parent branch
         */
        protected TagList(final Branch pParent) {
            /* Call super constructor */
            super(Tag.class);

            /* Store parent for use by entry handler */
            theBranch = pParent;
            theComponent = pParent.getComponent();

            /* Build prefix */
            thePrefix = theBranch.getBranchName() + PREFIX_TAG;
        }

        /**
         * Discover tag list from repository.
         * @throws JDataException on error
         */
        public void discover() throws JDataException {
            /* Reset the list */
            clear();

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
                                SVNDirEntry.DIRENT_ALL, new ListDirHandler());

                /* Release the client manager */
                myRepo.releaseClientManager(myMgr);
            } catch (SVNException e) {
                throw new JDataException(ExceptionClass.SUBVERSION, "Failed to discover tags for "
                        + theBranch.getBranchName(), e);
            }
        }

        /**
         * Locate tag.
         * @param pTag the tag
         * @return the relevant tag or Null
         */
        public Tag locateTag(final Tag pTag) {
            /* Access list iterator */
            ListIterator<Tag> myIterator = listIterator();

            /* While we have entries */
            while (myIterator.hasNext()) {
                /* Access the Tag */
                Tag myTag = myIterator.next();

                /* If this is the correct tag */
                int iCompare = myTag.compareTo(pTag);
                if (iCompare > 0) {
                    break;
                }
                if (iCompare < 0) {
                    continue;
                }
                return myTag;
            }

            /* Not found */
            return null;
        }

        /**
         * Determine next tag.
         * @return the next tag
         */
        public Tag nextTag() {
            /* Access list iterator */
            ListIterator<Tag> myIterator = listIterator();
            Tag myTag = null;

            /* Loop to the last entry */
            while (myIterator.hasNext()) {
                /* Access the next tag */
                myTag = myIterator.next();
            }

            /* Determine the largest current tag */
            int myTagNo = (myTag == null) ? 0 : myTag.theTag;

            /* Create the tag */
            return new Tag(theBranch, myTagNo + 1, -1);
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

                /* Access the name and ignore if it does not start with correct prefix */
                String myName = pEntry.getName();
                if (!myName.startsWith(thePrefix)) {
                    return;
                }
                myName = myName.substring(thePrefix.length());

                /* Determine tag and last revision */
                int myTagNo = Integer.parseInt(myName);
                long myRev = pEntry.getRevision();

                /* Adjust last revision */
                theLastRevision = Math.max(theLastRevision, myRev);

                /* Create the tag and add to the list */
                Tag myTag = new Tag(theBranch, myTagNo, myRev);
                add(myTag);
            }
        }
    }
}
