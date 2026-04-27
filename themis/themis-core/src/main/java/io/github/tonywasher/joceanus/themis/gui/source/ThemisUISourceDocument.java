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

package io.github.tonywasher.joceanus.themis.gui.source;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIBaseDocument;
import io.github.tonywasher.joceanus.themis.gui.base.ThemisUIHTMLTag;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import org.w3c.dom.Element;

/**
 * Document Builder for source.
 */
public class ThemisUISourceDocument
        extends ThemisUIBaseDocument {
    /**
     * Constructor.
     *
     * @throws OceanusException on error
     */
    ThemisUISourceDocument() throws OceanusException {
        super();
    }

    /**
     * Create document for element.
     *
     * @param pElement the element
     * @return the formatted document
     */
    public String formatElement(final ThemisInstance pElement) {
        /* Create new document and obtain the body */
        final Element myBody = newDocument();

        /* Create the code element */
        final Element myCode = createElement(ThemisUIHTMLTag.PRE);
        myBody.appendChild(myCode);
        myCode.setTextContent(pElement.toString());

        /* Return the formatted HTML */
        return formatXML();
    }
}
