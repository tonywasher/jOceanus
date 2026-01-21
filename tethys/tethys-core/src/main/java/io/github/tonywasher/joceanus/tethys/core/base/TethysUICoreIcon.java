/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.tethys.core.base;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;

import java.io.InputStream;

/**
 * Scroll Icon IDs.
 */
public enum TethysUICoreIcon
        implements TethysUIIconId {
    /**
     * CheckMark.
     */
    DYNAMICSPINNER("DynamicSpinner.gif");

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     *
     * @param pSourceName the source name
     */
    TethysUICoreIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return TethysUICoreIcon.class.getResourceAsStream(theSource);
    }
}
