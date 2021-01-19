/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2021 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.viewer;

import java.util.EnumMap;
import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for JMetis viewer.
 */
public enum MetisViewerResource
        implements TethysBundleId, MetisDataFieldId {
    /**
     * Viewer window title.
     */
    VIEWER_TITLE("window.Title"),

    /**
     * Field column title.
     */
    VIEWER_COLUMN_FIELD("column.Field"),

    /**
     * Value column title.
     */
    VIEWER_COLUMN_VALUE("column.Value"),

    /**
     * Key column title.
     */
    VIEWER_COLUMN_KEY("column.Key"),

    /**
     * Link to next.
     */
    VIEWER_LINK_NEXT("link.Next"),

    /**
     * Link to previous.
     */
    VIEWER_LINK_PREV("link.Prev"),

    /**
     * Link table title.
     */
    VIEWER_LINK_TITLE("link.Title"),

    /**
     * Link to forward.
     */
    VIEWER_LINK_FORWARD("link.Forward"),

    /**
     * Link to backward.
     */
    VIEWER_LINK_BACKWARD("link.Backward"),

    /**
     * Map table.
     */
    VIEWER_TABLE_MAP("table.Map"),

    /**
     * Sections table.
     */
    VIEWER_TABLE_SECTIONS("table.Sections"),

    /**
     * StackTrace table.
     */
    VIEWER_TABLE_STACKTRACE("table.StackTrace"),

    /**
     * ItemSelect item text.
     */
    VIEWER_SELECT_ITEM("itemSelect.Item"),

    /**
     * ItemSelect Of Text.
     */
    VIEWER_SELECT_OF("itemSelect.Of"),

    /**
     * ItemSelect ShowHdr text.
     */
    VIEWER_SELECT_SHOWHDR("itemSelect.showHdr"),

    /**
     * ItemSelect ShowItem text.
     */
    VIEWER_SELECT_SHOWITEMS("itemSelect.showItems"),

    /**
     * Error Entry.
     */
    VIEWER_ENTRY_ERROR("stdEntry.error"),

    /**
     * Profile Entry.
     */
    VIEWER_ENTRY_PROFILE("stdEntry.profile"),

    /**
     * Data Entry.
     */
    VIEWER_ENTRY_DATA("stdEntry.data"),

    /**
     * Updates Entry.
     */
    VIEWER_ENTRY_UPDATES("stdEntry.updates"),

    /**
     * View Entry.
     */
    VIEWER_ENTRY_VIEW("stdEntry.view"),

    /**
     * Preferences Entry.
     */
    VIEWER_ENTRY_PREF("stdEntry.pref"),

    /**
     * Exception Class.
     */
    VIEWER_ERROR_CLASS("exception.Class"),

    /**
     * Exception Message.
     */
    VIEWER_ERROR_MESSAGE("exception.Message"),

    /**
     * Exception Origin.
     */
    VIEWER_ERROR_ORIGIN("exception.Origin"),

    /**
     * Exception Cause.
     */
    VIEWER_ERROR_CAUSE("exception.Cause"),

    /**
     * Exception Object.
     */
    VIEWER_ERROR_OBJECT("exception.Object"),

    /**
     * Exception Stack.
     */
    VIEWER_ERROR_STACK("exception.Stack");

    /**
     * The stdEntry Map.
     */
    private static final Map<MetisViewerStandardEntry, TethysBundleId> ENTRY_MAP = buildEntryMap();

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(MetisDataException.class.getCanonicalName(),
            ResourceBundle::getBundle);

    /**
     * The Id.
     */
    private final String theKeyName;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pKeyName the key name
     */
    MetisViewerResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.viewer";
    }

    @Override
    public String getValue() {
        /* If we have not initialised the value */
        if (theValue == null) {
            /* Derive the value */
            theValue = LOADER.getValue(this);
        }

        /* return the value */
        return theValue;
    }

    @Override
    public String getId() {
        return getValue();
    }

    /**
     * Build entry map.
     * @return the map
     */
    private static Map<MetisViewerStandardEntry, TethysBundleId> buildEntryMap() {
        /* Create the map and return it */
        final Map<MetisViewerStandardEntry, TethysBundleId> myMap = new EnumMap<>(MetisViewerStandardEntry.class);
        myMap.put(MetisViewerStandardEntry.ERROR, VIEWER_ENTRY_ERROR);
        myMap.put(MetisViewerStandardEntry.PROFILE, VIEWER_ENTRY_PROFILE);
        myMap.put(MetisViewerStandardEntry.DATA, VIEWER_ENTRY_DATA);
        myMap.put(MetisViewerStandardEntry.UPDATES, VIEWER_ENTRY_UPDATES);
        myMap.put(MetisViewerStandardEntry.VIEW, VIEWER_ENTRY_VIEW);
        myMap.put(MetisViewerStandardEntry.PREFERENCES, VIEWER_ENTRY_PREF);
        return myMap;
    }

    /**
     * Obtain key for stdEntry.
     * @param pEntry the entry
     * @return the resource key
     */
    protected static TethysBundleId getKeyForStdEntry(final MetisViewerStandardEntry pEntry) {
        return TethysBundleLoader.getKeyForEnum(ENTRY_MAP, pEntry);
    }
}
