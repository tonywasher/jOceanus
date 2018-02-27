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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataList;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jthemis.ThemisResource;

/**
 * Status record in Working Copy.
 * @author Tony Washer
 */
public class ThemisSvnUpdateStatus
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisSvnUpdateStatus> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisSvnUpdateStatus.class);

    /**
     * fieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisResource.SCM_NAME, ThemisSvnUpdateStatus::getName);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_STATUS, ThemisSvnUpdateStatus::getStatus);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_PROPSTATUS, ThemisSvnUpdateStatus::getPropStatus);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_NODEKIND, ThemisSvnUpdateStatus::getKind);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_COPYFROM, ThemisSvnUpdateStatus::getCopyFrom);
        FIELD_DEFS.declareLocalField(ThemisResource.SVN_COPYREVISION, ThemisSvnUpdateStatus::getCopyRevision);
    }

    /**
     * Name of item.
     */
    private final String theName;

    /**
     * Status of item.
     */
    private final SVNStatus theStatus;

    /**
     * Constructor.
     * @param pCopy the working copy for the status
     * @param pStatus the underlying status
     */
    protected ThemisSvnUpdateStatus(final ThemisSvnWorkingCopy pCopy,
                                    final SVNStatus pStatus) {
        /* Copy interesting fields */
        theStatus = pStatus;

        /* Determine the name */
        final String myLocation = pCopy.getLocation()
                                  + File.separator;
        final String myName = theStatus.getFile().getAbsolutePath();
        if (myName.startsWith(myLocation)) {
            theName = myName.substring(myLocation.length());
        } else {
            theName = myName;
        }
    }

    @Override
    public String toString() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFieldSet<ThemisSvnUpdateStatus> getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Obtain the name.
     * @return the name
     */
    private String getName() {
        return theName;
    }

    /**
     * Obtain the status.
     * @return the status
     */
    private String getStatus() {
        return theStatus.getContentsStatus().toString();
    }

    /**
     * Obtain the property status.
     * @return the status
     */
    private String getPropStatus() {
        final SVNStatusType myStatus = theStatus.getPropertiesStatus();
        return myStatus.equals(SVNStatusType.STATUS_NORMAL)
                                                            ? null
                                                            : myStatus.toString();
    }

    /**
     * Obtain the kind.
     * @return the kind
     */
    private String getKind() {
        return theStatus.getKind().toString();
    }

    /**
     * Obtain the copyFrom.
     * @return the copyFrom
     */
    private String getCopyFrom() {
        return theStatus.isCopied()
                                    ? theStatus.getCopyFromURL()
                                    : null;
    }

    /**
     * Obtain the copyRevision.
     * @return the revision
     */
    private Long getCopyRevision() {
        return theStatus.isCopied()
                                    ? theStatus.getCopyFromRevision().getNumber()
                                    : null;
    }

    /**
     * List class.
     */
    public static class ThemisUpdateStatusList
            implements MetisFieldItem, MetisDataList<ThemisSvnUpdateStatus> {
        /**
         * Report fields.
         */
        private static final MetisFieldSet<ThemisUpdateStatusList> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisUpdateStatusList.class);

        /**
         * Size field id.
         */
        static {
            FIELD_DEFS.declareLocalField(ThemisResource.LIST_SIZE, ThemisUpdateStatusList::size);
        }

        /**
         * Status List.
         */
        private final List<ThemisSvnUpdateStatus> theStatusList;

        /**
         * Constructor.
         */
        protected ThemisUpdateStatusList() {
            theStatusList = new ArrayList<>();
        }

        @Override
        public List<ThemisSvnUpdateStatus> getUnderlyingList() {
            return theStatusList;
        }

        @Override
        public String toString() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFieldSet<ThemisUpdateStatusList> getDataFieldSet() {
            return FIELD_DEFS;
        }
    }
}
