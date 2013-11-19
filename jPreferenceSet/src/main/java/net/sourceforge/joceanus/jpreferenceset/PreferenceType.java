/*******************************************************************************
 * jPreferenceSet: PreferenceSet Management
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jpreferenceset;

import java.util.ResourceBundle;

/**
 * Enum class for preference types.
 */
public enum PreferenceType {
    /**
     * String.
     */
    STRING,

    /**
     * Integer.
     */
    INTEGER,

    /**
     * Boolean.
     */
    BOOLEAN,

    /**
     * Date.
     */
    DATE,

    /**
     * File.
     */
    FILE,

    /**
     * Directory.
     */
    DIRECTORY,

    /**
     * Enum.
     */
    ENUM,

    /**
     * Colour.
     */
    COLOR,

    /**
     * Font.
     */
    FONT;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PreferenceType.class.getName());

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = NLS_BUNDLE.getString(name());
        }

        /* return the name */
        return theName;
    }
}