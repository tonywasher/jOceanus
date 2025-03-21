/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.control;

import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.oceanus.resource.OceanusResourceId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;

/**
 * Tree Manager.
 */
public interface TethysUIHTMLManager
        extends OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * StyleSheetId.
     */
    interface TethysUIStyleSheetId extends OceanusResourceId {
    }

    /**
     * Process selected reference.
     * @param pReference the reference
     */
    void processReference(String pReference);

    /**
     * Set the HTML.
     * @param pHTMLString the HTML content.
     * @param pReference the reference
     */
    void setHTMLContent(String pHTMLString,
                        String pReference);

    /**
     * Set the CSS.
     * @param pStyleSheet the CSS content.
     * @throws OceanusException on error
     */
    void setCSSContent(TethysUIStyleSheetId pStyleSheet) throws OceanusException;

    /**
     * Scroll to reference.
     * @param pReference the reference
     */
    void scrollToReference(String pReference);

    /**
     * Print the contents.
     */
    void printIt();

    /**
     * SaveToFile.
     */
    void saveToFile();
}
