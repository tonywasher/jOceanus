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

package io.github.tonywasher.joceanus.themis.gui.base;

import org.w3c.dom.Element;
import org.w3c.dom.Text;

/**
 * DocBuilder interface.
 */
public interface ThemisUIDocBuilder {
    /**
     * Create element for Tag.
     *
     * @param pTag the tag
     * @return the element
     */
    Element createElement(ThemisUIHTMLTag pTag);

    /**
     * Create text node.
     *
     * @param pText the text
     * @return the node
     */
    Text createTextNode(String pText);

    /**
     * Create text node.
     *
     * @param pChar the character
     * @return the node
     */
    Text createTextNode(char pChar);

    /**
     * Set attribute for element.
     *
     * @param pElement the element
     * @param pAttr    the attribute
     * @param pValue   the value
     */
    void setAttribute(Element pElement,
                      ThemisUIHTMLAttr pAttr,
                      String pValue);
}
