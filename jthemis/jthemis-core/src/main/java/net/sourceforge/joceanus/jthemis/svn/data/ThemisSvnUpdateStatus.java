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

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldSet;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataFieldItem;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataItem.MetisDataList;

/**
 * Status record in Working Copy.
 * @author Tony Washer
 */
public class ThemisSvnUpdateStatus
        implements MetisDataFieldItem {
    /**
     * Report fields.
     */
    private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(ThemisSvnUpdateStatus.class);

    /**
     * Name field id.
     */
    private static final MetisDataField FIELD_NAME = FIELD_DEFS.declareLocalField("Name");

    /**
     * Status field id.
     */
    private static final MetisDataField FIELD_STATUS = FIELD_DEFS.declareLocalField("Status");

    /**
     * PropertyStatus field id.
     */
    private static final MetisDataField FIELD_PSTATUS = FIELD_DEFS.declareLocalField("PropStatus");

    /**
     * NodeKind field id.
     */
    private static final MetisDataField FIELD_KIND = FIELD_DEFS.declareLocalField("NodeKind");

    /**
     * CopyFromNode field id.
     */
    private static final MetisDataField FIELD_COPYFROM = FIELD_DEFS.declareLocalField("CopyFrom");

    /**
     * CopyFromRevision field id.
     */
    private static final MetisDataField FIELD_COPYREV = FIELD_DEFS.declareLocalField("CopyRevision");

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
    public MetisDataFieldSet getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisDataField pField) {
        /* Handle fields */
        if (FIELD_NAME.equals(pField)) {
            return theName;
        }
        if (FIELD_STATUS.equals(pField)) {
            return theStatus.getContentsStatus().toString();
        }
        if (FIELD_PSTATUS.equals(pField)) {
            final SVNStatusType myStatus = theStatus.getPropertiesStatus();
            return myStatus.equals(SVNStatusType.STATUS_NORMAL)
                                                                ? MetisDataFieldValue.SKIP
                                                                : myStatus.toString();
        }
        if (FIELD_KIND.equals(pField)) {
            return theStatus.getKind().toString();
        }
        if (FIELD_COPYFROM.equals(pField)) {
            return theStatus.isCopied()
                                        ? theStatus.getCopyFromURL()
                                        : MetisDataFieldValue.SKIP;
        }
        if (FIELD_COPYREV.equals(pField)) {
            return theStatus.isCopied()
                                        ? theStatus.getCopyFromRevision().getNumber()
                                        : MetisDataFieldValue.SKIP;
        }

        /* Unknown */
        return MetisDataFieldValue.UNKNOWN;
    }

    /**
     * List class.
     */
    public static class UpdateStatusList
            implements MetisDataFieldItem, MetisDataList<ThemisSvnUpdateStatus> {
        /**
         * Report fields.
         */
        private static final MetisDataFieldSet FIELD_DEFS = new MetisDataFieldSet(UpdateStatusList.class);

        /**
         * Size field id.
         */
        private static final MetisDataField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        /**
         * Status List.
         */
        private final List<ThemisSvnUpdateStatus> theStatusList;

        /**
         * Constructor.
         */
        protected UpdateStatusList() {
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
        public MetisDataFieldSet getDataFieldSet() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisDataField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }

            /* Unknown */
            return MetisDataFieldValue.UNKNOWN;
        }
    }
}
