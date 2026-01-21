/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.tethys.swing.factory;

import java.awt.Color;
import javax.swing.JFrame;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIcon;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIIconId;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIProgram;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIType;
import io.github.tonywasher.joceanus.tethys.api.button.TethysUIButtonFactory;
import io.github.tonywasher.joceanus.tethys.api.chart.TethysUIChartFactory;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControlFactory;
import io.github.tonywasher.joceanus.tethys.api.dialog.TethysUIDialogFactory;
import io.github.tonywasher.joceanus.tethys.api.field.TethysUIFieldFactory;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIMenuFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIPaneFactory;
import io.github.tonywasher.joceanus.tethys.api.table.TethysUITableFactory;
import io.github.tonywasher.joceanus.tethys.api.thread.TethysUIThreadFactory;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingDataFieldAdjust;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingDataFieldAdjust.TethysUISwingFieldAdjustSupplier;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingUtils;
import io.github.tonywasher.joceanus.tethys.swing.button.TethysUISwingButtonFactory;
import io.github.tonywasher.joceanus.tethys.swing.chart.TethysUISwingChartFactory;
import io.github.tonywasher.joceanus.tethys.swing.control.TethysUISwingControlFactory;
import io.github.tonywasher.joceanus.tethys.swing.dialog.TethysUISwingDialogFactory;
import io.github.tonywasher.joceanus.tethys.swing.field.TethysUISwingFieldFactory;
import io.github.tonywasher.joceanus.tethys.swing.menu.TethysUISwingMenuFactory;
import io.github.tonywasher.joceanus.tethys.swing.pane.TethysUISwingPaneFactory;
import io.github.tonywasher.joceanus.tethys.swing.table.TethysUISwingTableFactory;
import io.github.tonywasher.joceanus.tethys.swing.thread.TethysUISwingThreadFactory;

/**
 * javafx Factory.
 */
public class TethysUISwingFactory
        extends TethysUICoreFactory<Color>
        implements TethysUISwingFieldAdjustSupplier {
    /**
     * The field adjust.
     */
    private final TethysUISwingDataFieldAdjust theFieldAdjust;

    /**
     * The pane factory.
     */
    private final TethysUISwingPaneFactory thePaneFactory;

    /**
     * The button factory.
     */
    private final TethysUISwingButtonFactory theButtonFactory;

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
     * The field factory.
     */
    private final TethysUISwingFieldFactory theFieldFactory;

    /**
     * The menu factory.
     */
    private final TethysUISwingMenuFactory theMenuFactory;

    /**
     * The table factory.
     */
    private final TethysUISwingTableFactory theTableFactory;

    /**
     * The thread factory.
     */
    private final TethysUISwingThreadFactory theThreadFactory;

    /**
     * Constructor.
     *
     * @param pProgram the program definitions
     */
    public TethysUISwingFactory(final TethysUIProgram pProgram) {
        super(pProgram);
        theFieldAdjust = new TethysUISwingDataFieldAdjust(this);
        thePaneFactory = new TethysUISwingPaneFactory(this);
        theButtonFactory = new TethysUISwingButtonFactory(this);
        theChartFactory = new TethysUISwingChartFactory(this);
        theControlFactory = new TethysUISwingControlFactory(this, theFieldAdjust);
        theDialogFactory = new TethysUISwingDialogFactory(this);
        theFieldFactory = new TethysUISwingFieldFactory(this);
        theMenuFactory = new TethysUISwingMenuFactory(this);
        theTableFactory = new TethysUISwingTableFactory(this);
        theThreadFactory = new TethysUISwingThreadFactory(this, pProgram.useSliderStatus());
        establishLogSink();
    }

    /**
     * Set the frame.
     *
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
    public TethysUIType getGUIType() {
        return TethysUIType.SWING;
    }

    @Override
    public TethysUIPaneFactory paneFactory() {
        return thePaneFactory;
    }

    @Override
    public TethysUIButtonFactory<Color> buttonFactory() {
        return theButtonFactory;
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
    public TethysUIDialogFactory dialogFactory() {
        return theDialogFactory;
    }

    @Override
    public TethysUIFieldFactory fieldFactory() {
        return theFieldFactory;
    }

    @Override
    public TethysUIMenuFactory menuFactory() {
        return theMenuFactory;
    }

    @Override
    public TethysUIThreadFactory threadFactory() {
        return theThreadFactory;
    }

    @Override
    public TethysUITableFactory tableFactory() {
        return theTableFactory;
    }

    @Override
    public TethysUISwingDataFieldAdjust getFieldAdjuster() {
        return theFieldAdjust;
    }
}
