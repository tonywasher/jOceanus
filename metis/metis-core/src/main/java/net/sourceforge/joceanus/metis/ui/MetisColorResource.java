/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.metis.ui;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.metis.MetisDataException;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleId;
import net.sourceforge.joceanus.jtethys.resource.TethysBundleLoader;

/**
 * Resource IDs for JMetis Field.
 */
public enum MetisColorResource implements TethysBundleId {
    /**
     * FieldColor Standard.
     */
    FIELDCOLOR_PREFS("colorpref.name"),

    /**
     * FieldColor Standard.
     */
    FIELDCOLOR_STANDARD("colorpref.display.STANDARD"),

    /**
     * FieldColor Background.
     */
    FIELDCOLOR_BACKGROUND("colorpref.display.BACKGROUND"),

    /**
     * FieldColor Error.
     */
    FIELDCOLOR_ERROR("colorpref.display.ERROR"),

    /**
     * FieldColor Changed.
     */
    FIELDCOLOR_CHANGED("colorpref.display.CHANGED"),

    /**
     * FieldColor Zebra.
     */
    FIELDCOLOR_ZEBRA("colorpref.display.ZEBRA"),

    /**
     * FieldColor Disabled.
     */
    FIELDCOLOR_DISABLED("colorpref.display.DISABLED"),

    /**
     * FieldColor Link.
     */
    FIELDCOLOR_LINK("colorpref.display.LINK"),

    /**
     * FieldColor Value.
     */
    FIELDCOLOR_VALUE("colorpref.display.VALUE"),

    /**
     * FieldColor Negative.
     */
    FIELDCOLOR_NEGATIVE("colorpref.display.NEGATIVE"),

    /**
     * FieldColor PROGRESS.
     */
    FIELDCOLOR_PROGRESS("colorpref.display.PROGRESS"),

    /**
     * FieldColor SECURITY.
     */
    FIELDCOLOR_SECURITY("colorpref.display.SECURITY"),

    /**
     * FieldColor HEADER.
     */
    FIELDCOLOR_HEADER("colorpref.display.HEADER");

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
    MetisColorResource(final String pKeyName) {
        theKeyName = pKeyName;
    }

    @Override
    public String getKeyName() {
        return theKeyName;
    }

    @Override
    public String getNameSpace() {
        return "jMetis.field";
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
