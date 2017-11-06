/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2017 Tony Washer
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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.wc.SVNRevision;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataObjectFormat;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.ThemisDataException;
import net.sourceforge.joceanus.jthemis.ThemisResource;

/**
 * Methods to access revision history.
 * @author Tony Washer
 */
public class ThemisSvnRevisionHistory
        implements MetisDataFieldItem {
    /**
     * DataFields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(ThemisSvnRevisionHistory.class);

    /**
     * Owner field.
     */
    private static final MetisDataField FIELD_OWNER = FIELD_DEFS.declareLocalField(ThemisResource.SVN_OWNER);

    /**
     * Date field.
     */
    private static final MetisDataField FIELD_DATE = FIELD_DEFS.declareLocalField(ThemisResource.SVN_DATE);

    /**
     * Revision field.
     */
    private static final MetisDataField FIELD_REVISION = FIELD_DEFS.declareLocalField(ThemisResource.SVN_REVISION);

    /**
     * Message field.
     */
    private static final MetisDataField FIELD_MESSAGE = FIELD_DEFS.declareLocalField(ThemisResource.SVN_LOGMSG);

    /**
     * Origin field.
     */
    private static final MetisDataField FIELD_ORIGIN = FIELD_DEFS.declareLocalField(ThemisResource.SVN_ORIGIN);

    /**
     * Origin definition.
     */
    private static final MetisDataField FIELD_ORIGINDEF = FIELD_DEFS.declareLocalField(ThemisResource.SVN_ORIGINDEF);

    /**
     * CopyDirs field.
     */
    private static final MetisDataField FIELD_SOURCEDIRS = FIELD_DEFS.declareLocalField(ThemisResource.SVN_SOURCEDIRS);

    /**
     * BasedOn field.
     */
    private static final MetisDataField FIELD_BASEDON = FIELD_DEFS.declareLocalField(ThemisResource.SVN_BASEDON);

    /**
     * The owner.
     */
    private final Object theOwner;

    /**
     * The revisionKey.
     */
    private final SvnRevisionKey theRevisionKey;

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
    private SvnRevisionKey theOrigin;

    /**
     * The origin source definition.
     */
    private SvnSourceDefinition theOriginDef;

    /**
     * The basedOn history.
     */
    private ThemisSvnRevisionHistory theBasedOn;

    /**
     * The Source Directory list.
     */
    private final SvnSourceDirList theSourceDirs;

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
        theSourceDirs = new SvnSourceDirList();

        /* Set the revision key */
        theRevisionKey = new SvnRevisionKey(pPath, theRevision);

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

                            /* Record the origin */
                            theOrigin = new SvnRevisionKey(myDetail);
                            theOriginDef = new SvnSourceDefinition(myCopyPath);
                            isOrigin = true;

                            /* else if this a copy into the directory */
                        } else if (!myCopyPath.startsWith(pPath)) {
                            /* Record the copyDir */
                            theSourceDirs.addItem(new SvnSourceDir(myCopyPath, myDetail.getCopyRevision()));
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
                theOrigin = new SvnRevisionKey(myNewPath, SVNRevision.create(myDetail.getCopyRevision()));
                theOriginDef = new SvnSourceDefinition(myNewPath);
                isOrigin = true;
            }
        }
    }

    /**
     * Obtain the revisionKey.
     * @return the key
     */
    public SvnRevisionKey getRevisionKey() {
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
    public SvnRevisionKey getOrigin() {
        return theOrigin;
    }

    /**
     * Obtain the origin definition.
     * @return the key
     */
    public SvnSourceDefinition getOriginDefinition() {
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
     * Obtain the copyDirs iterator.
     * @return the iterator
     */
    public Iterator<SvnSourceDir> sourceDirIterator() {
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
        final Iterator<SvnSourceDir> myIterator = theSourceDirs.iterator();
        while (myIterator.hasNext()) {
            final SvnSourceDir myDir = myIterator.next();

            /* Add to output */
            myBuilder.append(myDir.toString());
        }

        /* Return the details */
        return myBuilder.toString();
    }

    @Override
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        if (FIELD_OWNER.equals(pField)) {
            return theOwner;
        }
        if (FIELD_DATE.equals(pField)) {
            return getDate();
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
                                     : MetisDataFieldValue.SKIP;
        }
        if (FIELD_ORIGINDEF.equals(pField)) {
            return theOriginDef != null
                                        ? theOriginDef
                                        : MetisDataFieldValue.SKIP;
        }
        if (FIELD_BASEDON.equals(pField)) {
            return theBasedOn != null
                                      ? theBasedOn
                                      : MetisDataFieldValue.SKIP;
        }
        if (FIELD_SOURCEDIRS.equals(pField)) {
            return theSourceDirs.isEmpty()
                                           ? MetisDataFieldValue.SKIP
                                           : theSourceDirs;
        }
        return MetisDataFieldValue.UNKNOWN;
    }

    /**
     * RevisionKey.
     */
    public static class SvnRevisionKey
            implements MetisDataObjectFormat {
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
        protected SvnRevisionKey(final String pPath,
                                 final SVNRevision pRevision) {
            /* Store parameters */
            theRevision = pRevision;
            thePath = pPath;
        }

        /**
         * Constructor.
         * @param pEntry the log entry
         */
        SvnRevisionKey(final SVNLogEntryPath pEntry) {
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
            if (!(pThat instanceof SvnRevisionKey)) {
                return false;
            }

            /* Access correctly */
            final SvnRevisionKey myThat = (SvnRevisionKey) pThat;

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
    public static final class SvnSourceDir
            implements MetisDataFieldItem {
        /**
         * DataFields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(SvnSourceDir.class);

        /**
         * Component field.
         */
        private static final MetisDataField FIELD_COMPONENT = FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT);

        /**
         * Source field.
         */
        private static final MetisDataField FIELD_SOURCE = FIELD_DEFS.declareLocalField(ThemisResource.SVN_SOURCE);

        /**
         * BasedOn field.
         */
        private static final MetisDataField FIELD_BASEDON = FIELD_DEFS.declareLocalField(ThemisResource.SVN_BASEDON);

        /**
         * Component.
         */
        private final String theComponent;

        /**
         * Source RevisionKey.
         */
        private final SvnRevisionKey theSource;

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
        SvnSourceDir(final String pSource,
                     final long pRevision) throws OceanusException {
            /* Split according to directory parts */
            final SvnSourceDefinition mySource = new SvnSourceDefinition(pSource);

            /* Store details */
            theComponent = mySource.getComponent();
            theSource = new SvnRevisionKey(mySource.getSource(), SVNRevision.create(pRevision));
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
        public SvnRevisionKey getSource() {
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
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            if (FIELD_COMPONENT.equals(pField)) {
                return theComponent;
            }
            if (FIELD_SOURCE.equals(pField)) {
                return theSource;
            }
            if (FIELD_BASEDON.equals(pField)) {
                return theBasedOn != null
                                          ? theBasedOn
                                          : MetisDataFieldValue.SKIP;
            }
            return MetisDataFieldValue.UNKNOWN;
        }
    }

    /**
     * Source Directory list.
     */
    public static final class SvnSourceDirList
            implements MetisDataFieldItem, MetisDataList<SvnSourceDir> {
        /**
         * DataFields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(SvnSourceDirList.class);

        /**
         * Size field.
         */
        private static final MetisDataField FIELD_SIZE = FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE);

        /**
         * Directory List.
         */
        private final List<SvnSourceDir> theDirList;

        /**
         * Constructor.
         */
        SvnSourceDirList() {
            theDirList = new ArrayList<>();
        }

        @Override
        public List<SvnSourceDir> getUnderlyingList() {
            return theDirList;
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }
            return MetisDataFieldValue.UNKNOWN;
        }

        /**
         * Add item to list (discarding duplicates).
         * @param pDir the directory to add.
         * @throws OceanusException on error
         */
        void addItem(final SvnSourceDir pDir) throws OceanusException {
            /* Loop through the existing items */
            final Iterator<SvnSourceDir> myIterator = iterator();
            while (myIterator.hasNext()) {
                final SvnSourceDir myEntry = myIterator.next();

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
    public enum SvnSourceType {
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
        private static SvnSourceType getSourceType(final String pPath) {
            /* Check for structure */
            if (pPath.equals(ThemisSvnComponent.DIR_TRUNK)) {
                return SvnSourceType.TRUNK;
            }
            if (pPath.equals(ThemisSvnComponent.DIR_BRANCHES)) {
                return SvnSourceType.BRANCH;
            }
            if (pPath.equals(ThemisSvnComponent.DIR_TAGS)) {
                return SvnSourceType.TAG;
            }
            return SvnSourceType.UNKNOWN;
        }
    }

    /**
     * Source definition.
     */
    public static class SvnSourceDefinition
            implements MetisDataFieldItem {
        /**
         * DataFields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(SvnSourceDefinition.class);

        /**
         * Component field.
         */
        private static final MetisDataField FIELD_COMP = FIELD_DEFS.declareLocalField(ThemisResource.SCM_COMPONENT);

        /**
         * Type field.
         */
        private static final MetisDataField FIELD_TYPE = FIELD_DEFS.declareLocalField(ThemisResource.SVN_TYPE);

        /**
         * Source field.
         */
        private static final MetisDataField FIELD_SOURCE = FIELD_DEFS.declareLocalField(ThemisResource.SVN_SOURCE);

        /**
         * Component.
         */
        private final String theComponent;

        /**
         * Branch type.
         */
        private final SvnSourceType theType;

        /**
         * Source.
         */
        private final String theSource;

        /**
         * Constructor.
         * @param pSource the source path
         * @throws OceanusException on error
         */
        public SvnSourceDefinition(final String pSource) throws OceanusException {
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
                SvnSourceType mySrcType = SvnSourceType.getSourceType(myBase);

                /* If there must be a component */
                if (mySrcType == SvnSourceType.UNKNOWN) {
                    /* Switch things around */
                    mySubComp = myBase;
                    myBase = myVers;

                    /* Obtain source type assuming component */
                    mySrcType = SvnSourceType.getSourceType(myBase);

                    /* Unknown source type implies that we are outside the standard structure. */
                    if (mySrcType == SvnSourceType.UNKNOWN) {
                        /* Record as trunk with no base */
                        mySrcType = SvnSourceType.TRUNK;
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
                if (mySrcType != SvnSourceType.TRUNK) {
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
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            if (FIELD_COMP.equals(pField)) {
                return theComponent;
            }
            if (FIELD_TYPE.equals(pField)) {
                return theType;
            }
            if (FIELD_SOURCE.equals(pField)) {
                return theSource;
            }
            return MetisDataFieldValue.UNKNOWN;
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
        public SvnSourceType getSourceType() {
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
