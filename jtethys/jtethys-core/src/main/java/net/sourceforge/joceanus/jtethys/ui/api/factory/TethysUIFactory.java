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
package net.sourceforge.joceanus.jtethys.ui.api.factory;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIValueSet;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.chart.TethysUIChartFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControlFactory;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIDialogFactory;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIMenuFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIPaneFactory;

/**
 * UI Factory.
 * @param <C> the color
 */
public interface TethysUIFactory<C> {
    /**
     * Obtain the formatter.
     * @return the formatter
     */
    TethysUIDataFormatter getDataFormatter();

    /**
     * Obtain the valueSet.
     * @return the valueSet
     */
    TethysUIValueSet getValueSet();

    /**
     * Resolve Icon.
     * @param pIconId the mapped IconId
     * @param pWidth the icon width
     * @return the icon
     */
    TethysUIIcon resolveIcon(TethysUIIconId pIconId, int pWidth);

    /**
     * Obtain the button factory.
     * @return the factory
     */
    TethysUIButtonFactory buttonFactory();

    /**
     * Obtain the chart factory.
     * @return the factory
     */
    TethysUIChartFactory chartFactory();

    /**
     * Obtain the control factory.
     * @return the factory
     */
    TethysUIControlFactory controlFactory();

    /**
     * Obtain the dialog factory.
     * @return the factory
     */
    TethysUIDialogFactory<C> dialogFactory();

    /**
     * Obtain the menu factory.
     * @return the factory
     */
    TethysUIMenuFactory menuFactory();

    /**
     * Obtain the pane factory.
     * @return the factory
     */
    TethysUIPaneFactory paneFactory();
}