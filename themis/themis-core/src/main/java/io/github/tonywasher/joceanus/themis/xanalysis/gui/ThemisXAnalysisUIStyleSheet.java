/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.xanalysis.gui;

import io.github.tonywasher.joceanus.tethys.api.control.TethysUIHTMLManager.TethysUIStyleSheetId;
import io.github.tonywasher.joceanus.themis.lethe.ui.ThemisDSMStyleSheet;

import java.io.InputStream;

public enum ThemisXAnalysisUIStyleSheet
        implements TethysUIStyleSheetId {
    /**
     * Themis StyleSheet.
     */
    CSS("themis.css");

    /**
     * The Source.
     */
    private final String theSource;

    /**
     * Constructor.
     *
     * @param pSource the source
     */
    ThemisXAnalysisUIStyleSheet(final String pSource) {
        theSource = pSource;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return ThemisDSMStyleSheet.class.getResourceAsStream(theSource);
    }
}
