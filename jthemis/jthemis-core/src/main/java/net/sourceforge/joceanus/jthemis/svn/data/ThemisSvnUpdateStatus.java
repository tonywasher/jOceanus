/*******************************************************************************
 * jThemis: Java Project Framework
 * Copyright 2012,2016 Tony Washer
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

import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import net.sourceforge.joceanus.jmetis.data.MetisDataObject.MetisDataContents;
import net.sourceforge.joceanus.jmetis.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisFields;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;

/**
 * Status record in Working Copy.
 * @author Tony Washer
 */
public class ThemisSvnUpdateStatus
        implements MetisDataContents {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(ThemisSvnUpdateStatus.class.getSimpleName());

    /**
     * Name field id.
     */
    private static final MetisField FIELD_NAME = FIELD_DEFS.declareLocalField("Name");

    /**
     * Status field id.
     */
    private static final MetisField FIELD_STATUS = FIELD_DEFS.declareLocalField("Status");

    /**
     * PropertyStatus field id.
     */
    private static final MetisField FIELD_PSTATUS = FIELD_DEFS.declareLocalField("PropStatus");

    /**
     * NodeKind field id.
     */
    private static final MetisField FIELD_KIND = FIELD_DEFS.declareLocalField("NodeKind");

    /**
     * CopyFromNode field id.
     */
    private static final MetisField FIELD_COPYFROM = FIELD_DEFS.declareLocalField("CopyFrom");

    /**
     * CopyFromRevision field id.
     */
    private static final MetisField FIELD_COPYREV = FIELD_DEFS.declareLocalField("CopyRevision");

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
        String myLocation = pCopy.getLocation()
                            + File.separator;
        String myName = theStatus.getFile().getAbsolutePath();
        if (myName.startsWith(myLocation)) {
            theName = myName.substring(myLocation.length());
        } else {
            theName = myName;
        }
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle fields */
        if (FIELD_NAME.equals(pField)) {
            return theName;
        }
        if (FIELD_STATUS.equals(pField)) {
            return theStatus.getContentsStatus().toString();
        }
        if (FIELD_PSTATUS.equals(pField)) {
            SVNStatusType myStatus = theStatus.getPropertiesStatus();
            return myStatus.equals(SVNStatusType.STATUS_NORMAL)
                                                                ? MetisFieldValue.SKIP
                                                                : myStatus.toString();
        }
        if (FIELD_KIND.equals(pField)) {
            return theStatus.getKind().toString();
        }
        if (FIELD_COPYFROM.equals(pField)) {
            return theStatus.isCopied()
                                        ? theStatus.getCopyFromURL()
                                        : MetisFieldValue.SKIP;
        }
        if (FIELD_COPYREV.equals(pField)) {
            return theStatus.isCopied()
                                        ? theStatus.getCopyFromRevision().getNumber()
                                        : MetisFieldValue.SKIP;
        }

        /* Unknown */
        return MetisFieldValue.UNKNOWN;
    }

    /**
     * List class.
     */
    public static class UpdateStatusList
            extends ArrayList<ThemisSvnUpdateStatus>
            implements MetisDataContents {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 2584865977496622102L;

        /**
         * Report fields.
         */
        private static final MetisFields FIELD_DEFS = new MetisFields(UpdateStatusList.class.getSimpleName());

        /**
         * Size field id.
         */
        private static final MetisField FIELD_SIZE = FIELD_DEFS.declareLocalField("Size");

        @Override
        public String formatObject() {
            return FIELD_DEFS.getName();
        }

        @Override
        public MetisFields getDataFields() {
            return FIELD_DEFS;
        }

        @Override
        public Object getFieldValue(final MetisField pField) {
            /* Handle standard fields */
            if (FIELD_SIZE.equals(pField)) {
                return size();
            }

            /* Unknown */
            return MetisFieldValue.UNKNOWN;
        }
    }
}