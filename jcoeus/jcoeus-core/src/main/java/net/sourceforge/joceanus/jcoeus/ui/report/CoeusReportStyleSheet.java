/*******************************************************************************
 * Coeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.ui.report;

import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.ui.TethysHTMLManager.TethysStyleSheetId;

/**
 * Report StyleSheets.
 */
public enum CoeusReportStyleSheet implements TethysStyleSheetId {
    /**
     * Report StyleSheet.
     */
    CSS_REPORT("CoeusReports.css");

    /**
     * The Source.
     */
    private String theSource;

    /**
     * Constructor.
     * @param pSource the source
     */
    CoeusReportStyleSheet(final String pSource) {
        theSource = pSource;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return CoeusReportStyleSheet.class.getResourceAsStream(theSource);
    }
}
