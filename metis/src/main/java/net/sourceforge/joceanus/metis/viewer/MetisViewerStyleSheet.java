/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.viewer;

import java.io.InputStream;

import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager.TethysUIStyleSheetId;

/**
 * Viewer StyleSheets.
 */
public enum MetisViewerStyleSheet
        implements TethysUIStyleSheetId {
    /**
     * Viewer StyleSheet.
     */
    CSS_VIEWER("MetisViewer.css");

    /**
     * The Source.
     */
    private String theSource;

    /**
     * Constructor.
     * @param pSource the source
     */
    MetisViewerStyleSheet(final String pSource) {
        theSource = pSource;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return MetisViewerStyleSheet.class.getResourceAsStream(theSource);
    }
}
