/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.launch;

import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.ui.panel.MoneyWiseMainTab;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUILaunchProgram;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIMainPanel;

/**
 * MoneyWise Application definition.
 */
public class MoneyWiseApp
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
    public MoneyWiseApp() {
        super(MoneyWiseApp.class.getResourceAsStream("MoneyWiseApp.properties"));
    }

    @Override
    public boolean useSliderStatus() {
        return true;
    }

    @Override
    public TethysUIIconId[] getIcons() {
        return new TethysUIIconId[]
        { MoneyWiseIcon.SMALL, MoneyWiseIcon.BIG };
    }

    @Override
    public TethysUIIconId getSplash() {
        return MoneyWiseIcon.SPLASH;
    }

    @Override
    public int[] getPanelDimensions() {
        return new int[] { WIDTH_SCENE, HEIGHT_SCENE };
    }

    @Override
    public TethysUIMainPanel createMainPanel(final TethysUIFactory<?> pFactory) throws OceanusException {
        return new MoneyWiseMainTab(pFactory);
    }
}
