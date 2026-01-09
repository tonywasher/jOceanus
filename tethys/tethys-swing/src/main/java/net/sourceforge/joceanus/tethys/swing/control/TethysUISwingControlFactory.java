/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.swing.control;

import java.awt.Color;

import net.sourceforge.joceanus.tethys.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.tethys.api.control.TethysUIHTMLManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUILabel;
import net.sourceforge.joceanus.tethys.api.control.TethysUIPasswordField;
import net.sourceforge.joceanus.tethys.api.control.TethysUIProgressBar;
import net.sourceforge.joceanus.tethys.api.control.TethysUISlider;
import net.sourceforge.joceanus.tethys.api.control.TethysUISplitTreeManager;
import net.sourceforge.joceanus.tethys.api.control.TethysUITextArea;
import net.sourceforge.joceanus.tethys.api.control.TethysUITreeManager;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.swing.base.TethysUISwingDataFieldAdjust;

/**
 * Swing control factory.
 */
public class TethysUISwingControlFactory
        implements TethysUIControlFactory {
    /**
     * The factory.
     */
    private final TethysUICoreFactory<Color> theFactory;

    /**
     * The field adjust.
     */
    private final TethysUISwingDataFieldAdjust theFieldAdjust;

    /**
     * Constructor.
     * @param pFactory the factory.
     * @param pAdjust the field adjuster
     */
    public TethysUISwingControlFactory(final TethysUICoreFactory<Color> pFactory,
                                       final TethysUISwingDataFieldAdjust pAdjust) {
        /* Store parameters */
        theFactory = pFactory;
        theFieldAdjust = pAdjust;
    }

    @Override
    public TethysUILabel newLabel() {
        return new TethysUISwingLabel(theFactory);
    }

    @Override
    public TethysUICheckBox newCheckBox() {
        return new TethysUISwingCheckBox(theFactory, theFieldAdjust);
    }

    @Override
    public TethysUITextArea newTextArea() {
        return new TethysUISwingTextArea(theFactory);
    }

    @Override
    public TethysUIPasswordField newPasswordField() {
        return new TethysUISwingPasswordField(theFactory);
    }

    @Override
    public TethysUIProgressBar newProgressBar() {
        return new TethysUISwingProgressBar(theFactory, theFieldAdjust);
    }

    @Override
    public TethysUISlider newSlider() {
        return new TethysUISwingSlider(theFactory);
    }

    @Override
    public TethysUIHTMLManager newHTMLManager() {
        return new TethysUISwingHTMLManager(theFactory);
    }

    @Override
    public <T> TethysUITreeManager<T> newTreeManager() {
        return new TethysUISwingTreeManager<>(theFactory);
    }

    @Override
    public <T> TethysUISplitTreeManager<T> newSplitTreeManager() {
        return new TethysUISwingSplitTreeManager<>(theFactory);
    }
}
