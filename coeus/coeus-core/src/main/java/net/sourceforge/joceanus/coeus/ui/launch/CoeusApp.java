/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.coeus.ui.launch;

import net.sourceforge.joceanus.coeus.ui.CoeusIcon;
import net.sourceforge.joceanus.coeus.ui.panels.CoeusMainPanel;
import net.sourceforge.joceanus.oceanus.OceanusException;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUILaunchProgram;
import net.sourceforge.joceanus.tethys.ui.api.factory.TethysUIMainPanel;

/**
 * Coeus Application definition.
 */
public class CoeusApp
        extends TethysUILaunchProgram {
    /**
     * Width for main panel.
     */
    private static final int WIDTH_SCENE = 1300;

    /**
     * Height for main panel.
     */
    private static final int HEIGHT_SCENE = 800;

    /**
     * Constructor.
     */
    public CoeusApp() {
        super(CoeusApp.class.getResourceAsStream("CoeusApp.properties"));
    }

    @Override
    public TethysUIIconId[] getIcons() {
        return new TethysUIIconId[]
        { CoeusIcon.SMALL, CoeusIcon.BIG };
    }

    @Override
    public TethysUIIconId getSplash() {
        return CoeusIcon.SPLASH;
    }

    @Override
    public int[] getPanelDimensions() {
        return new int[] { WIDTH_SCENE, HEIGHT_SCENE };
    }

    @Override
    public TethysUIMainPanel createMainPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        return new CoeusMainPanel(pFactory);
    }
}
