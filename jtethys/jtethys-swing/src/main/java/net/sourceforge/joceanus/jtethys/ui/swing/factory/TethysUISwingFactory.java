/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.factory;

import java.awt.Color;
import javax.swing.JFrame;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingDataFieldAdjust;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingUtils;
import net.sourceforge.joceanus.jtethys.ui.swing.chart.TethysUISwingChartFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.control.TethysUISwingControlFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.dialog.TethysUISwingDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.menu.TethysUISwingMenuFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.pane.TethysUISwingPaneFactory;

/**
 * javafx Factory.
 */
public class TethysUISwingFactory
        extends TethysUICoreFactory<Color> {
    /**
     * The field adjust.
     */
    private final TethysUISwingDataFieldAdjust theFieldAdjust;

    /**
     * The pane factory.
     */
    private final TethysUISwingPaneFactory thePaneFactory;

    /**
     * The chart factory.
     */
    private final TethysUISwingChartFactory theChartFactory;

    /**
     * The control factory.
     */
    private final TethysUISwingControlFactory theControlFactory;

    /**
     * The dialog factory.
     */
    private final TethysUISwingDialogFactory theDialogFactory;

    /**
     * The menu factory.
     */
    private final TethysUISwingMenuFactory theMenuFactory;

    /**
     * Constructor.
     */
    TethysUISwingFactory() {
        theFieldAdjust = new TethysUISwingDataFieldAdjust(this);
        thePaneFactory = new TethysUISwingPaneFactory(this);
        theChartFactory = new TethysUISwingChartFactory(this);
        theControlFactory = new TethysUISwingControlFactory(this, theFieldAdjust);
        theDialogFactory = new TethysUISwingDialogFactory();
        theMenuFactory = new TethysUISwingMenuFactory(this);
    }

    /**
     * Set the frame.
     * @param pFrame the frame
     */
    public void setFrame(final JFrame pFrame) {
        theDialogFactory.setFrame(pFrame);
    }

    @Override
    public TethysUIIcon resolveIcon(final TethysUIIconId pIconId,
                                    final int pWidth) {
        return pIconId == null
                ? null
                : TethysUISwingUtils.getIconAtSize(pIconId, pWidth);
    }

    @Override
    public TethysUIPaneFactory paneFactory() {
        return thePaneFactory;
    }

    @Override
    public TethysUIButtonFactory buttonFactory() {
        return null;
    }

    @Override
    public TethysUIChartFactory chartFactory() {
        return theChartFactory;
    }

    @Override
    public TethysUIControlFactory controlFactory() {
        return theControlFactory;
    }

    @Override
    public TethysUIDialogFactory<Color> dialogFactory() {
        return theDialogFactory;
    }

    @Override
    public TethysUIMenuFactory menuFactory() {
        return theMenuFactory;
    }
}
