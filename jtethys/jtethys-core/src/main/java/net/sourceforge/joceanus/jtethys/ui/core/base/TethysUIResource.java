/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.core.base;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for TethysUI package.
 */
public enum TethysUIXResource implements TethysBundleId {
    /**
     * Bad value on parse.
     */
    PARSE_BADVALUE("parse.BadValue"),

    /**
     * AboutBox version.
     */
    ABOUT_VERSION("about.Version"),

    /**
     * AboutBox Revision.
     */
    ABOUT_REVISION("about.Revision"),

    /**
     * AboutBox BuilyOn.
     */
    ABOUT_BUILTON("about.BuiltOn"),

    /**
     * Button OK.
     */
    BUTTON_OK("button.OK"),

    /**
     * Button Cancel.
     */
    BUTTON_CANCEL("button.cancel"),

    /**
     * Label Password.
     */
    PASS_LABEL_PASSWORD("pass.label.password"),

    /**
     * Label Confirm.
     */
    PASS_LABEL_CONFIRM("pass.label.confirm"),

    /**
     * Title for password.
     */
    PASS_TITLE_PASSWORD("pass.title.password"),

    /**
     * Title for new password.
     */
    PASS_TITLE_NEWPASS("pass.title.newPassword"),

    /**
     * Title for error.
     */
    PASS_TITLE_ERROR("pass.title.error"),

    /**
     * Error Bad Password.
     */
    PASS_ERROR_BADPASS("pass.error.badPassword"),

    /**
     * Error Confirm.
     */
    PASS_ERROR_CONFIRM("pass.error.confirm"),

    /**
     * Error length 1.
     */
    PASS_ERROR_LENGTH1("pass.error.length1"),

    /**
     * Error length 2.
     */
    PASS_ERROR_LENGTH2("pass.error.length2");

    /**
     * The Resource Loader.
     */
    private static final TethysBundleLoader LOADER = TethysBundleLoader.getPackageLoader(OceanusException.class.getCanonicalName(),
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
    TethysUIXResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "ui";
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
}
