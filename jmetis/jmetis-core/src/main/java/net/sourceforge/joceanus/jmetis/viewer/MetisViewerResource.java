/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.viewer;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for JMetis viewer.
 */
public enum MetisViewerResource implements TethysResourceId {
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
    VIEWER_SELECT_SHOWITEMS("itemSelect.showItems");

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getPackageResourceBuilder(MetisDataException.class.getCanonicalName());

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
            theValue = BUILDER.getValue(this);
        }

        /* return the value */
        return theValue;
    }
}
