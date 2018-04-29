/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jthemis.svn.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.ThemisResource;

/**
 * Methods to access revision history.
 * @author Tony Washer
 */
public class ThemisSvnRevisionHistory
        implements
        MetisFieldItem {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ThemisSvnRevisionHistory.class);

    /**
     * DataFields.
     */
    private static final MetisFieldSet<ThemisSvnRevisionHistory> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnRevisionHistory.class);

    /**
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_OWNER, ThemisSvnRevisionHistory::getOwner);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_DATE, ThemisSvnRevisionHistory::getDate);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISION, ThemisSvnRevisionHistory::getRevision);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_LOGMSG, ThemisSvnRevisionHistory::getLogMessage);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_ORIGIN, ThemisSvnRevisionHistory::getOrigin);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_ORIGINDEF, ThemisSvnRevisionHistory::getOriginDefinition);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_SOURCEDIRS, ThemisSvnRevisionHistory::sourceDirs);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_BASEDON, ThemisSvnRevisionHistory::getBasedOn);
    }

    /**
     * The owner.
     */
    private final Object theOwner;

    /**
     * The revisionKey.
     */
    private final ThemisSvnRevisionKey theRevisionKey;

    /**
     * The date.
     */
    private final Date theDate;

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
    private ThemisSvnRevisionKey theOrigin;

    /**
     * The origin source definition.
     */
    private ThemisSvnSourceDefinition theOriginDef;

    /**
     * The basedOn history.
     */
    private ThemisSvnRevisionHistory theBasedOn;

    /**
     * The Source Directory list.
     */
    private final ThemisSvnSourceDirList theSourceDirs;

    /**
     * Constructor.
     * @param pOwner the owner
     * @param pPath the location path
     * @param pEntry the log source
     * @throws OceanusException on error
     */
    protected ThemisSvnRevisionHistory(final Object pOwner,
                                       final String pPath,
                                       final SVNLogEntry pEntry) throws OceanusException {
        /* Record details */
        theOwner = pOwner;
        theRevision = SVNRevision.create(pEntry.getRevision());
        theLogMessage = pEntry.getMessage();
        theDate = pEntry.getDate();
        theUnderlying = pEntry;

        /* Allocate the sourceDir list */
        theSourceDirs = new ThemisSvnSourceDirList();

        /* Set the revision key */
        theRevisionKey = new ThemisSvnRevisionKey(pPath, theRevision);

        /* Log instance */
        LOGGER.debug("Path {} @ Rev{}", pPath, pEntry.getRevision());

        /* Iterate through the file changes */
        for (final Entry<String, SVNLogEntryPath> myEntry : pEntry.getChangedPaths().entrySet()) {
            /* Access the key */
            final String myPath = myEntry.getKey();
            final SVNLogEntryPath myDetail = myEntry.getValue();
            final String myCopyPath = myDetail.getCopyPath();

            /* If the entry starts with our path */
            if (myPath.startsWith(pPath)) {
                /* Access the detail */
                final SVNNodeKind myKind = myDetail.getKind();

                /* If this is a file */
                if (myKind.equals(SVNNodeKind.FILE)) {
                    /* Record the file change */
                    hasFileChanges = true;

                    /* else if this is a directory */
                } else if (myKind.equals(SVNNodeKind.DIR)) {
                    /* Access the copy path */
                    final String myDir = myDetail.getPath();

                    /* If this a copy of a directory */
                    if (myCopyPath != null) {
                        /* If this is an initialisation of the directory */
                        if (myDir.equals(pPath)) {
                            /* Check that this is not a second origin */
                            if (isOrigin) {
                                throw new ThemisDataException(theRevisionKey, "second origin for path");
                            }

                            /* Log origin copy */
                            LOGGER.debug("O:{} <- {}", myDetail, myCopyPath);

                            /* Record the origin */
                            theOrigin = new ThemisSvnRevisionKey(myDetail);
                            theOriginDef = new ThemisSvnSourceDefinition(myCopyPath);
                            isOrigin = true;

                            /* else if this a copy into the directory from another location */
                        } else if (!myCopyPath.startsWith(pPath)) {
                            /* Record the file change */
                            hasFileChanges = true;

                            /* Check whether we should copy this component */
                            if (checkComponentCopy(pPath, myDir, myCopyPath)) {
                                /* Log subDir copy */
                                LOGGER.debug("S:{} <- {}@{}", myDetail, myCopyPath, myDetail.getCopyRevision());

                                /* Record the copyDir */
                                theSourceDirs.addItem(new ThemisSvnSourceDir(myCopyPath, myDetail.getCopyRevision()));
                            }

                            /* This is a copy within the directory */
                        } else {
                            /* Record the file change */
                            hasFileChanges = true;
                        }
                    }
                }

                /* else if this is a copy of a root of the directory */
            } else if (pPath.startsWith(myPath)
                       && myCopyPath != null) {
                /* Check that this is not a second origin */
                if (isOrigin) {
                    throw new ThemisDataException(theRevisionKey, "second origin for path");
                }

                /* Determine the new name for the directory */
                final StringBuilder myBuilder = new StringBuilder(pPath);
                myBuilder.delete(0, myPath.length());
                myBuilder.insert(0, myCopyPath);

                /* Record the origin */
                final String myNewPath = myBuilder.toString();
                theOrigin = new ThemisSvnRevisionKey(myNewPath, SVNRevision.create(myDetail.getCopyRevision()));
                theOriginDef = new ThemisSvnSourceDefinition(myNewPath);
                isOrigin = true;

                /* Log root origin copy */
                LOGGER.debug("R:{} <- {}@{}", myDetail, myNewPath, myDetail.getCopyRevision());
            }
        }

        /* Log completion */
        LOGGER.debug("Complete Path {} @ Rev{}", pPath, pEntry.getRevision());
    }

    /**
     * Check for valid component copy.
     * @param pPath the component path
     * @param pTarget the target path
     * @param pSource the source path
     * @return should we copy path true/false
     */
    private boolean checkComponentCopy(final String pPath,
                                       final String pTarget,
                                       final String pSource) {
        /* Look for a standard component copy */
        final String myInnerPath = pTarget.substring(pPath.length());
        if (pSource.startsWith(myInnerPath)) {
            return true;
        }

        /* Look for creation of component */
        return pSource.startsWith("/branches/");
    }

    /**
     * Obtain the revisionKey.
     * @return the key
     */
    public ThemisSvnRevisionKey getRevisionKey() {
        return theRevisionKey;
    }

    /**
     * Obtain the owner.
     * @return the owner
     */
    public Object getOwner() {
        return theOwner;
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public Date getDate() {
        final Date myDate = new Date();
        myDate.setTime(theDate.getTime());
        return myDate;
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
     * Has SourceDirs?
     * @return true/false
     */
    public boolean hasSourceDirs() {
        return !theSourceDirs.isEmpty();
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
    public ThemisSvnRevisionKey getOrigin() {
        return theOrigin;
    }

    /**
     * Obtain the origin definition.
     * @return the key
     */
    public ThemisSvnSourceDefinition getOriginDefinition() {
        return theOriginDef;
    }

    /**
     * Obtain the basedOn link.
     * @return the log entry
     */
    public ThemisSvnRevisionHistory getBasedOn() {
        return theBasedOn;
    }

    /**
     * Obtain the copyDirs list.
     * @return the list
     */
    private ThemisSvnSourceDirList sourceDirs() {
        return theSourceDirs;
    }

    /**
     * Obtain the copyDirs iterator.
     * @return the iterator
     */
    public Iterator<ThemisSvnSourceDir> sourceDirIterator() {
        return theSourceDirs.iterator();
    }

    /**
     * Set the history.
     * @param pBasedOn the history base
     */
    protected void setBasedOn(final ThemisSvnRevisionHistory pBasedOn) {
        theBasedOn = pBasedOn;
    }

    /**
     * Is the revision relevant?
     * @return true/false
     */
    public boolean isRelevant() {
        return hasFileChanges || isOrigin || !theSourceDirs.isEmpty();
    }

    @Override
    public String toString() {
        /* Create a stringBuilder */
        final StringBuilder myBuilder = new StringBuilder();

        /* Output the title */
        myBuilder.append("\n\nRevision ")
                .append(theRevisionKey.toString())
                .append('\n')
                .append(theLogMessage);

        /* Add SourceDirs */
        final Iterator<ThemisSvnSourceDir> myIterator = theSourceDirs.iterator();
        while (myIterator.hasNext()) {
            final ThemisSvnSourceDir myDir = myIterator.next();

            /* Add to output */
            myBuilder.append(myDir.toString());
        }

        /* Return the details */
        return myBuilder.toString();
    }

    @Override
    public MetisFieldSet<ThemisSvnRevisionHistory> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * RevisionKey.
     */
    public static class ThemisSvnRevisionKey
            implements
            MetisDataObjectFormat {
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
         * Constructor.
         * @param pRevision the SVNRevision
         * @param pPath the path
         */
        protected ThemisSvnRevisionKey(final String pPath,
                                       final SVNRevision pRevision) {
            /* Store parameters */
            theRevision = pRevision;
            thePath = pPath;
        }

        /**
         * Constructor.
         * @param pEntry the log entry
         */
        ThemisSvnRevisionKey(final SVNLogEntryPath pEntry) {
            /* Store parameters */
            theRevision = SVNRevision.create(pEntry.getCopyRevision());
            thePath = pEntry.getCopyPath();
        }

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

        @Override
        public String toString() {
            return thePath + ":" + theRevision;
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
            if (!(pThat instanceof ThemisSvnRevisionKey)) {
                return false;
            }

            /* Access correctly */
            final ThemisSvnRevisionKey myThat = (ThemisSvnRevisionKey) pThat;

            /* Check values */
            return theRevision.equals(myThat.getRevision())
                   && thePath.equals(myThat.getPath());
        }

        @Override
        public int hashCode() {
            return (int) theRevision.getNumber()
                   + thePath.hashCode() * HASH_PRIME;
        }
    }

    /**
     * SvnSourceDir entry.
     */
    public static final class ThemisSvnSourceDir
            implements
            MetisFieldItem {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnSourceDir> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnSourceDir.class);

        /**
         * Component field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT, ThemisSvnSourceDir::getComponent);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_BASEDON, ThemisSvnSourceDir::getBasedOn);
        }

        /**
         * Component.
         */
        private final String theComponent;

        /**
         * Source RevisionKey.
         */
        private final ThemisSvnRevisionKey theSource;

        /**
         * The basedOn revision.
         */
        private ThemisSvnRevisionHistory theBasedOn;

        /**
         * Constructor.
         * @param pSource the copyFrom path
         * @param pRevision the source revision
         * @throws OceanusException on error
         */
        ThemisSvnSourceDir(final String pSource,
                           final long pRevision) throws OceanusException {
            /* Split according to directory parts */
            final ThemisSvnSourceDefinition mySource = new ThemisSvnSourceDefinition(pSource);

            /* Store details */
            theComponent = mySource.getComponent();
            theSource = new ThemisSvnRevisionKey(mySource.getSource(), SVNRevision.create(pRevision));
        }

        /**
         * Obtain the Component.
         * @return the path
         */
        public String getComponent() {
            return theComponent;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        public ThemisSvnRevisionKey getSource() {
            return theSource;
        }

        /**
         * Obtain the basedOn link.
         * @return the log entry
         */
        public ThemisSvnRevisionHistory getBasedOn() {
            return theBasedOn;
        }

        /**
         * Set the history.
         * @param pBasedOn the history base
         */
        void setBasedOn(final ThemisSvnRevisionHistory pBasedOn) {
            theBasedOn = pBasedOn;
        }

        @Override
        public String toString() {
            /* Create a stringBuilder */
            final StringBuilder myBuilder = new StringBuilder();

            /* Add to output */
            myBuilder.append("\nSource ")
                    .append(theComponent)
                    .append("\nFrom ")
                    .append(theSource);

            /* Return the details */
            return myBuilder.toString();
        }

        @Override
        public String formatObject(final MetisDataFormatter pFormatter) {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnSourceDir> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }

    /**
     * Source Directory list.
     */
    public static final class ThemisSvnSourceDirList
            implements
            MetisFieldItem,
            MetisDataList<ThemisSvnSourceDir> {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnSourceDirList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnSourceDirList.class);

        /**
         * Size field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisSvnSourceDirList::size);
        }

        /**
         * Directory List.
         */
        private final List<ThemisSvnSourceDir> theDirList;

        /**
         * Constructor.
         */
        ThemisSvnSourceDirList() {
            theDirList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnSourceDir> getUnderlyingList() {
            return theDirList;
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnSourceDirList> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Add item to list (discarding duplicates).
         * @param pDir the directory to add.
         * @throws OceanusException on error
         */
        void addItem(final ThemisSvnSourceDir pDir) throws OceanusException {
            /* Loop through the existing items */
            final Iterator<ThemisSvnSourceDir> myIterator = iterator();
            while (myIterator.hasNext()) {
                final ThemisSvnSourceDir myEntry = myIterator.next();

                /* If we have matching component */
                if (MetisDataDifference.isEqual(myEntry.getComponent(), pDir.getComponent())) {
                    /* Reject if different path */
                    if (!MetisDataDifference.isEqual(myEntry.getSource(), pDir.getSource())) {
                        throw new ThemisDataException(myEntry, "Conflicting sources");
                    }

                    /* Already added!! */
                    return;
                }
            }

            /* Add the unique entry */
            add(pDir);
        }
    }

    /**
     * Source type.
     */
    public enum ThemisSvnSourceType {
        /**
         * Trunk.
         */
        TRUNK,

        /**
         * Branch.
         */
        BRANCH,

        /**
         * TAG.
         */
        TAG,

        /**
         * UNKNOWN.
         */
        UNKNOWN;

        /**
         * Obtain branch type.
         * @param pPath the path type
         * @return the branch type
         */
        private static ThemisSvnSourceType getSourceType(final String pPath) {
            /* Check for structure */
            if (pPath.equals(ThemisSvnComponent.DIR_TRUNK)) {
                return ThemisSvnSourceType.TRUNK;
            }
            if (pPath.equals(ThemisSvnComponent.DIR_BRANCHES)) {
                return ThemisSvnSourceType.BRANCH;
            }
            if (pPath.equals(ThemisSvnComponent.DIR_TAGS)) {
                return ThemisSvnSourceType.TAG;
            }
            return ThemisSvnSourceType.UNKNOWN;
        }
    }

    /**
     * Source definition.
     */
    public static class ThemisSvnSourceDefinition
            implements
            MetisFieldItem {
        /**
         * DataFields.
         */
        private static final MetisFieldSet<ThemisSvnSourceDefinition> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnSourceDefinition.class);

        /**
         * Component field.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT, ThemisSvnSourceDefinition::getComponent);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_TYPE, ThemisSvnSourceDefinition::getSourceType);
            FIELD_DEFS.declareLocalField(ThemisResource.SVN_SOURCE, ThemisSvnSourceDefinition::getSource);
        }

        /**
         * Component.
         */
        private final String theComponent;

        /**
         * Branch type.
         */
        private final ThemisSvnSourceType theType;

        /**
         * Source.
         */
        private final String theSource;

        /**
         * Constructor.
         * @param pSource the source path
         * @throws OceanusException on error
         */
        public ThemisSvnSourceDefinition(final String pSource) throws OceanusException {
            /* First character must be Separator */
            if (pSource.charAt(0) != ThemisSvnRepository.SEP_URL) {
                throw new ThemisDataException(pSource, "Invalid source");
            }

            /* Split according to directory parts */
            final String[] mySplit = pSource.substring(1).split(Character.toString(ThemisSvnRepository.SEP_URL));

            /* Must have at least two parts (first null) */
            if (mySplit.length > 1) {
                /* Access path components */
                String mySubComp = null;
                String myBase = mySplit[0];
                String myVers = mySplit[1];
                int myLen = 0;

                /* Obtain source type assuming null component */
                ThemisSvnSourceType mySrcType = ThemisSvnSourceType.getSourceType(myBase);

                /* If there must be a component */
                if (mySrcType == ThemisSvnSourceType.UNKNOWN) {
                    /* Switch things around */
                    mySubComp = myBase;
                    myBase = myVers;

                    /* Obtain source type assuming component */
                    mySrcType = ThemisSvnSourceType.getSourceType(myBase);

                    /* Unknown source type implies that we are outside the standard structure. */
                    if (mySrcType == ThemisSvnSourceType.UNKNOWN) {
                        /* Record as trunk with no base */
                        mySrcType = ThemisSvnSourceType.TRUNK;
                        myBase = null;

                        /* else record version */
                    } else if (mySplit.length > 2) {
                        myVers = mySplit[2];
                    }

                    /* Set length */
                    myLen = mySubComp.length() + 1;
                }

                /* Adjust length */
                if (myBase != null) {
                    myLen += myBase.length() + 1;
                }
                if (mySrcType != ThemisSvnSourceType.TRUNK) {
                    myLen += myVers.length() + 1;
                }

                /* Store details */
                theComponent = mySubComp;
                theType = mySrcType;
                theSource = pSource.substring(0, myLen);

            } else {
                throw new ThemisDataException(pSource, "invalid source");
            }
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisSvnSourceDefinition> getDataFieldSet() {
            return FIELD_DEFS;
        }

        /**
         * Obtain the component.
         * @return the component
         */
        public String getComponent() {
            return theComponent;
        }

        /**
         * Obtain the source type.
         * @return the type
         */
        public ThemisSvnSourceType getSourceType() {
            return theType;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        public String getSource() {
            return theSource;
        }
    }
}
