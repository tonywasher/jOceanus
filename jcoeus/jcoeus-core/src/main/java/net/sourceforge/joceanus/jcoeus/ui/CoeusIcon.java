/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jcoeus.ui;

import java.io.InputStream;

import net.sourceforge.joceanus.jtethys.ui.TethysIconId;

/**
 * Coeus Icon IDs.
 */
public enum CoeusIcon implements TethysIconId {
    /**
     * The small program icon.
     */
    SMALL("icons/CoeusSmall.png"),

    /**
     * The big program icon.
     */
    BIG("icons/CoeusBig.png"),

    /**
     * The splash program icon.
     */
    SPLASH("icons/CoeusSplash.png");

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    CoeusIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    @Override
    public InputStream getInputStream() {
        return CoeusIcon.class.getResourceAsStream(theSource);
    }
}
