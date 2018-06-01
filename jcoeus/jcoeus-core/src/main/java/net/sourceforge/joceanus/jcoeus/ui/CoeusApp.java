/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2018 Tony Washer
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

import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;

/**
 * Coeus Application definition.
 */
public class CoeusApp
        extends TethysProgram {
    /**
     * Constructor.
     */
    public CoeusApp() {
        super(CoeusApp.class.getResourceAsStream("CoeusApp.properties"));
    }

    @Override
    public TethysIconId[] getIcons() {
        return new TethysIconId[]
        { CoeusIcon.SMALL, CoeusIcon.BIG };
    }

    @Override
    public TethysIconId getSplash() {
        return CoeusIcon.SPLASH;
    }
}
