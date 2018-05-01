/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012, 2018 Tony Washer
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
package net.sourceforge.joceanus.jmetis.viewer;

import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;

/**
 * Viewer Icon IDs.
 */
public enum MetisViewerIcon implements TethysIconId {
    /**
     * Parent.
     */
    PARENT("BlueJellyParent.png"),

    /**
     * Next.
     */
    NEXT("BlueJellyNext.png"),

    /**
     * Disabled.
     */
    PREV("BlueJellyPrevious.png");

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    MetisViewerIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }
}
