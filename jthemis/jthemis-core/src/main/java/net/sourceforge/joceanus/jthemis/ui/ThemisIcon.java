/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jthemis.ui;

import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.ui.TethysIconId;

/**
 * Themis Icon IDs.
 */
public enum ThemisIcon implements TethysIconId {
    /**
     * The small program icon.
     */
    SMALL("icons/ThemisSmall.png"),

    /**
     * The big program icon.
     */
    BIG("icons/ThemisBig.png"),

    /**
     * The splash program icon.
     */
    SPLASH("icons/ThemisSplash.png");

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    ThemisIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return ThemisIcon.class.getResourceAsStream(theSource);
    }
}
