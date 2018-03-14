/*******************************************************************************
 * jMetis: Java Data Framework
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
package net.sourceforge.joceanus.jmetis.ui;

import net.sourceforge.joceanus.jtethys.resource.TethysResourceBuilder;
import net.sourceforge.joceanus.jtethys.resource.TethysResourceId;

/**
 * Resource IDs for jMetis UI Fields.
 */
public enum MetisUIResource implements TethysResourceId {
    /**
     * Download ToolTip.
     */
    ICON_TIP_DOWNLOAD("icons.Tip.Download"),

    /**
     * Print ToolTip.
     */
    ICON_TIP_PRINT("icons.Tip.Print"),

    /**
     * Save ToolTip.
     */
    ICON_TIP_SAVE("icons.Tip.SaveToFile"),

    /**
     * Icon Commit Tip.
     */
    ICON_TIP_COMMIT("icons.Tip.Commit"),

    /**
     * Icon UnDo Tip.
     */
    ICON_TIP_UNDO("icons.Tip.UnDo"),

    /**
     * Icon Reset Tip.
     */
    ICON_TIP_RESET("icons.Tip.Reset"),

    /**
     * Icon Cancel Tip.
     */
    ICON_TIP_CANCEL("icons.Tip.Cancel"),

    /**
     * Icon Edit Tip.
     */
    ICON_TIP_EDIT("icons.Tip.Edit"),

    /**
     * Icon New Tip.
     */
    ICON_TIP_NEW("icons.Tip.New"),

    /**
     * Icon Delete Tip.
     */
    ICON_TIP_DELETE("icons.Tip.Delete"),

    /**
     * ErrorPanel Clear Button.
     */
    ERROR_BUTTON_CLEAR("ErrorPanel.Button.Clear"),

    /**
     * ErrorPanel Title.
     */
    ERROR_TITLE("ErrorPanel.Title");

    /**
     * The Resource Builder.
     */
    private static final TethysResourceBuilder BUILDER = TethysResourceBuilder.getResourceBuilder(MetisUIResource.class.getCanonicalName());

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
    MetisUIResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.ui";
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
