/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jtethys.test.ui;

import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;

/**
 * Helper Icon IDs.
 */
public enum TethysTestIcon implements TethysUIIconId {
    /**
     * Spinner.
     */
    SPINNER("PinkJellySpinner.png"),

    /**
     * OpenTrue.
     */
    OPENTRUE("GreenJellyOpenTrue.png"),

    /**
     * OpenFalse.
     */
    OPENFALSE("GreenJellyOpenFalse.png"),

    /**
     * ClosedTrue.
     */
    CLOSEDTRUE("BlueJellyClosedTrue.png");

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    TethysTestIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return TethysTestIcon.class.getResourceAsStream(theSource);
    }
}
