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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.text.html.HTML.Tag;

/**
 * Document Builder for source.
 */
public class ThemisXAnalysisUISourceDocument
        extends ThemisXAnalysisUIBaseDocument {
    /**
     * Constructor.
     *
     * @throws OceanusException on error
     */
    ThemisXAnalysisUISourceDocument() throws OceanusException {
        super();
    }

    /**
     * Create document for element.
     *
     * @param pElement the element
     */
    public String formatElement(final ThemisXAnalysisInstance pElement) {
        /* Create new document and obtain the body */
        final Element myBody = newDocument();
        final Document myDoc = getDocument();

        /* Create the code element */
        final Element myCode = myDoc.createElement(Tag.CODE.toString());
        myBody.appendChild(myCode);
        myCode.setTextContent(pElement.toString());

        return formatXML();
    }
}
