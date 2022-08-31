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
package net.sourceforge.joceanus.jmoneywise.lethe.ui;

import net.sourceforge.joceanus.jmetis.launch.MetisMainPanel;
import net.sourceforge.joceanus.jmetis.launch.MetisProgram;
import net.sourceforge.joceanus.jmetis.threads.MetisToolkit;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;

/**
 * MoneyWise Application definition.
 */
public class MoneyWiseApp
        extends TethysProgram
        implements MetisProgram {
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
    public TethysIconId[] getIcons() {
        return new TethysIconId[]
        { MoneyWiseIcon.SMALL, MoneyWiseIcon.BIG };
    }

    @Override
    public TethysIconId getSplash() {
        return MoneyWiseIcon.SPLASH;
    }

    @Override
    public int[] getPanelDimensions() {
        return new int[] { WIDTH_SCENE, HEIGHT_SCENE };
    }

    @Override
    public MetisMainPanel createMainPanel(final MetisToolkit pToolkit) throws OceanusException {
        return null;
    }
}
