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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataFormat;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.internal.util.SVNDate;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * Methods to access revision history.
 * @author Tony Washer
 */
public class RevisionHistory
        implements JDataContents {
    /**
     * DataFields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(RevisionHistory.class.getSimpleName());

    /**
     * Date field.
     */
    private static final JDataField FIELD_DATE = FIELD_DEFS.declareLocalField("Date");

    /**
     * Revision field.
     */
    private static final JDataField FIELD_REVISION = FIELD_DEFS.declareLocalField("Revision");

    /**
     * Message field.
     */
    private static final JDataField FIELD_MESSAGE = FIELD_DEFS.declareLocalField("LogMessage");

    /**
     * Origin field.
     */
    private static final JDataField FIELD_ORIGIN = FIELD_DEFS.declareLocalField("Origin");

    /**
     * CopyDirs field.
     */
    private static final JDataField FIELD_COPYDIRS = FIELD_DEFS.declareLocalField("CopyDirs");

    /**
     * BasedOn field.
     */
    private static final JDataField FIELD_BASEDON = FIELD_DEFS.declareLocalField("BasedOn");

    /**
     * The revisionKey.
     */
    private final RevisionKey theRevisionKey;

    /**
     * The date.
     */
    private final String theDate;

    /**
     * The revision.
     */
    private final SVNRevision theRevision;

    /**
     * The log message.
     */
    private final String theLogMessage;

    /**
     * The underlying log entry.
     */
    private final SVNLogEntry theUnderlying;

    /**
     * Has file changes.
     */
    private boolean hasFileChanges;

    /**
     * Is Origin.
     */
    private boolean isOrigin;

    /**
     * The origin.
     */
    private RevisionKey theOrigin;

    /**
     * The basedOn history.
     */
    private RevisionHistory theBasedOn;

    /**
     * The CopyDirs.
     */
    private final CopyDirList theCopyDirs;

    /**
     * Obtain the revisionKey.
     * @return the key
     */
    public RevisionKey getRevisionKey() {
        return theRevisionKey;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public String getDate() {
        return theDate;
    }

    /**
     * Obtain the revision.
     * @return the revision
     */
    public SVNRevision getRevision() {
        return theRevision;
    }

    /**
     * Obtain the log message.
     * @return the log message
     */
    public String getLogMessage() {
        return theLogMessage;
    }

    /**
     * Obtain the log entry.
     * @return the log entry
     */
    protected SVNLogEntry getUnderlying() {
        return theUnderlying;
    }

    /**
     * Has file changes?
     * @return true/false
     */
    public boolean hasFileChanges() {
        return hasFileChanges;
    }

    /**
     * Has CopyDirs?
     * @return true/false
     */
    public boolean hasCopyDirs() {
        return !theCopyDirs.isEmpty();
    }

    /**
     * Is the origin?
     * @return true/false
     */
    public boolean isOrigin() {
        return isOrigin;
    }

    /**
     * Obtain the origin key.
     * @return the key
     */
    public RevisionKey getOrigin() {
        return theOrigin;
    }

    /**
     * Obtain the basedOn link.
     * @return the log entry
     */
    public RevisionHistory getBasedOn() {
        return theBasedOn;
    }

    /**
     * Obtain the copyDirs iterator.
     * @return the iterator
     */
    public Iterator<CopyDir> copyDirIterator() {
        return theCopyDirs.iterator();
    }

    /**
     * Set the history.
     * @param pBasedOn the history base
     */
    protected void setBasedOn(final RevisionHistory pBasedOn) {
        theBasedOn = pBasedOn;
    }

    /**
     * Is the revision relevant?
     * @return true/false
     */
    public boolean isRelevant() {
        return hasFileChanges || isOrigin || !theCopyDirs.isEmpty();
    }

    /**
     * Constructor.
     * @param pPath the location path
     * @param pEntry the log source
     */
    protected RevisionHistory(final String pPath,
                              final SVNLogEntry pEntry) {
        /* Record details */
        theRevision = SVNRevision.create(pEntry.getRevision());
        theLogMessage = pEntry.getMessage();
        theDate = SVNDate.formatDate(pEntry.getDate());
        theUnderlying = pEntry;

        /* Allocate the copyDir list */
        theCopyDirs = new CopyDirList();

        /* Set the revision key */
        theRevisionKey = new RevisionKey(pPath, theRevision);

        /* Iterate through the file changes */
        for (Entry<String, SVNLogEntryPath> myEntry : pEntry.getChangedPaths().entrySet()) {
            /* Access the key */
            String myPath = myEntry.getKey();
            SVNLogEntryPath myDetail = myEntry.getValue();
            String myCopyPath = myDetail.getCopyPath();

            /* If the entry starts with our path */
            if (myPath.startsWith(pPath)) {
                /* Access the detail */
                SVNNodeKind myKind = myDetail.getKind();

                /* If this is a file */
                if (myKind.equals(SVNNodeKind.FILE)) {
                    /* Record the file change */
                    hasFileChanges = true;

                    /* else if this is a directory */
                } else if (myKind.equals(SVNNodeKind.DIR)) {
                    /* Access the copy path */
                    String myDir = myDetail.getPath();

                    /* If this a copy of a directory */
                    if (myCopyPath != null) {
                        /* If this is an initialisation of the directory */
                        if (myDir.equals(pPath)) {
                            /* Check that this is not a second origin */
                            if (isOrigin) {
                                throw new UnsupportedOperationException("second origin for path");
                            }

                            /* Record the origin */
                            theOrigin = new RevisionKey(myDetail);
                            isOrigin = true;

                            /* else if this a copy into the directory */
                        } else if (!myCopyPath.startsWith(pPath)) {
                            /* Record the copyDir */
                            theCopyDirs.add(new CopyDir(myDir, myCopyPath, myDetail.getCopyRevision()));
                        }
                    }
                }

                /* else if this is a copy of a root of the directory */
            } else if ((pPath.startsWith(myPath))
                       && (myCopyPath != null)) {
                /* Check that this is not a second origin */
                if (isOrigin) {
                    throw new UnsupportedOperationException("second origin for path");
                }

                /* Determine the new name for the directory */
                StringBuilder myBuilder = new StringBuilder(pPath);
                myBuilder.delete(0, myPath.length());
                myBuilder.insert(0, myCopyPath);

                /* Record the origin */

                theOrigin = new RevisionKey(myBuilder.toString(), SVNRevision.create(myDetail.getCopyRevision()));
                isOrigin = true;
            }
        }
    }

    @Override
    public String toString() {
        /* Create a stringBuilder */
        StringBuilder myBuilder = new StringBuilder();

        /* Output the title */
        myBuilder.append("\n\nRevision ");
        myBuilder.append(theRevisionKey.toString());
        myBuilder.append("\n");
        myBuilder.append(theLogMessage);

        /* Add CopyDirs */
        Iterator<CopyDir> myIterator = theCopyDirs.iterator();
        while (myIterator.hasNext()) {
            CopyDir myDir = myIterator.next();

            /* Add to output */
            myBuilder.append(myDir.toString());
        }

        /* Return the details */
        return myBuilder.toString();
    }

    @Override
    public String formatObject() {
        return toString();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_DATE.equals(pField)) {
            return theDate;
        }
        if (FIELD_REVISION.equals(pField)) {
            return theRevisionKey;
        }
        if (FIELD_MESSAGE.equals(pField)) {
            return theLogMessage;
        }
        if (FIELD_ORIGIN.equals(pField)) {
            return theOrigin != null
                                    ? theOrigin
                                    : JDataFieldValue.SKIP;
        }
        if (FIELD_BASEDON.equals(pField)) {
            return theBasedOn != null
                                     ? theBasedOn
                                     : JDataFieldValue.SKIP;
        }
        if (FIELD_COPYDIRS.equals(pField)) {
            return theCopyDirs.isEmpty()
                                        ? JDataFieldValue.SKIP
                                        : theCopyDirs;
        }
        return JDataFieldValue.UNKNOWN;
    }

    /**
     * RevisionKey.
     */
    public static class RevisionKey
            implements JDataFormat {
        /**
         * Hash prime.
         */
        private static final int HASH_PRIME = 17;

        /**
         * Revision.
         */
        private final SVNRevision theRevision;

        /**
         * Path.
         */
        private final String thePath;

        /**
         * Obtain the revision.
         * @return the revision
         */
        public SVNRevision getRevision() {
            return theRevision;
        }

        /**
         * Obtain the path.
         * @return the path
         */
        public String getPath() {
            return thePath;
        }

        /**
         * Constructor.
         * @param pRevision the SVNRevision
         * @param pPath the path
         */
        protected RevisionKey(final String pPath,
                              final SVNRevision pRevision) {
            /* Store parameters */
            theRevision = pRevision;
            thePath = pPath;
        }

        /**
         * Constructor.
         * @param pEntry the log entry
         */
        private RevisionKey(final SVNLogEntryPath pEntry) {
            /* Store parameters */
            theRevision = SVNRevision.create(pEntry.getCopyRevision());
            thePath = pEntry.getCopyPath();
        }

        @Override
        public String toString() {
            return thePath + ":" + theRevision;
        }

        @Override
        public String formatObject() {
            return toString();
        }

        @Override
        public boolean equals(final Object pThat) {
            /* Handle trivial examples */
            if (this == pThat) {
                return true;
            }
            if (pThat == null) {
                return false;
            }

            /* Handle bad instance */
            if (!(pThat instanceof RevisionKey)) {
                return false;
            }

            /* Access correctly */
            RevisionKey myThat = (RevisionKey) pThat;

            /* Check values */
            return theRevision.equals(myThat.getRevision())
                   && thePath.equals(myThat.getPath());
        }

        @Override
        public int hashCode() {
            return (int) theRevision.getNumber()
                   + (thePath.hashCode() * HASH_PRIME);
        }
    }

    /**
     * CopyDir entry.
     */
    public static final class CopyDir
            implements JDataContents {
        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CopyDir.class.getSimpleName());

        /**
         * Path field.
         */
        private static final JDataField FIELD_PATH = FIELD_DEFS.declareLocalField("Path");

        /**
         * Source field.
         */
        private static final JDataField FIELD_SOURCE = FIELD_DEFS.declareLocalField("Source");

        /**
         * BasedOn field.
         */
        private static final JDataField FIELD_BASEDON = FIELD_DEFS.declareLocalField("BasedOn");

        /**
         * Path.
         */
        private final String thePath;

        /**
         * Source RevisionKey.
         */
        private final RevisionKey theSource;

        /**
         * The basedOn revision.
         */
        private RevisionHistory theBasedOn;

        /**
         * Obtain the Path.
         * @return the path
         */
        public String getPath() {
            return thePath;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        public RevisionKey getSource() {
            return theSource;
        }

        /**
         * Obtain the basedOn link.
         * @return the log entry
         */
        public RevisionHistory getBasedOn() {
            return theBasedOn;
        }

        /**
         * Set the history.
         * @param pBasedOn the history base
         */
        protected void setBasedOn(final RevisionHistory pBasedOn) {
            theBasedOn = pBasedOn;
        }

        /**
         * Constructor.
         * @param pPath the source path
         * @param pSource the copyFrom path
         * @param pRevision the source revision
         */
        private CopyDir(final String pPath,
                        final String pSource,
                        final long pRevision) {
            /* Store parameters */
            thePath = pPath;
            theSource = new RevisionKey(pSource, SVNRevision.create(pRevision));
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            StringBuilder myBuilder = new StringBuilder();

            /* Add to output */
            myBuilder.append("\nCopy ");
            myBuilder.append(thePath);
            myBuilder.append("\nFrom ");
            myBuilder.append(theSource);

            /* Return the details */
            return myBuilder.toString();
        }

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_PATH.equals(pField)) {
                return thePath;
            }
            if (FIELD_SOURCE.equals(pField)) {
                return theSource;
            }
            if (FIELD_BASEDON.equals(pField)) {
                return theBasedOn != null
                                         ? theBasedOn
                                         : JDataFieldValue.SKIP;
            }
            return JDataFieldValue.UNKNOWN;
        }
    }

    /**
     * Copy Directory list.
     */
    public static class CopyDirList
            extends ArrayList<CopyDir>
            implements JDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = -1628220857405866559L;

        /**
         * DataFields.
         */
        private static final JDataFields FIELD_DEFS = new JDataFields(CopyDirList.class.getSimpleName());

        /**
         * Size field.
         */
        private static final JDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public JDataFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final JDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return JDataFieldValue.UNKNOWN;
        }
    }
}
