/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jprometheus.lethe.ui;

import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jprometheus.lethe.data.PrometheusAction;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollButtonManager;

/**
 * Prometheus Icon IDs.
 */
public enum PrometheusIcon implements TethysIconId {
    /**
     * Active.
     */
    ACTIVE("icons/GreenJellySymbolActive.png"),

    /**
     * Disabled.
     */
    DISABLED("icons/OrangeJellyAlphaDisabled.png"),

    /**
     * Goto.
     */
    GOTO("icons/BlueJellyGoTo.png");

    /**
     * Active Button ToolTip.
     */
    public static final String TIP_ACTIVE = PrometheusUIResource.ICON_TIP_ACTIVE.getValue();

    /**
     * Enable Button ToolTip.
     */
    private static final String TIP_ENABLE = PrometheusUIResource.ICON_TIP_ENABLE.getValue();

    /**
     * Disable Button ToolTip.
     */
    private static final String TIP_DISABLE = PrometheusUIResource.ICON_TIP_DISABLE.getValue();

    /**
     * GoTo Button ToolTip.
     */
    private static final String TIP_GOTO = PrometheusUIResource.ICON_TIP_GOTO.getValue();

    /**
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    PrometheusIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    /**
     * Configure goto scroll button.
     * @param pButton the button manager
     */
    public static void configureGoToScrollButton(final TethysScrollButtonManager<?, ?, ?> pButton) {
        pButton.setNullMargins();
        pButton.setSimpleDetails(GOTO, MetisIcon.ICON_SIZE, TIP_GOTO);
    }

    /**
     * Configure status icon button.
     * @return the mapSet configuration
     */
    public static TethysIconMapSet<PrometheusAction> configureStatusIconButton() {
        final TethysIconMapSet<PrometheusAction> myMapSet = new TethysIconMapSet<>();
        myMapSet.setMappingsForValue(PrometheusAction.ACTIVE, PrometheusAction.ACTIVE, ACTIVE, TIP_ACTIVE);
        myMapSet.setMappingsForValue(PrometheusAction.DELETE, PrometheusAction.DO, MetisIcon.DELETE, MetisIcon.TIP_DELETE);
        myMapSet.setMappingsForValue(PrometheusAction.INSERT, PrometheusAction.DO, MetisIcon.NEW, MetisIcon.TIP_NEW);
        return myMapSet;
    }

    /**
     * Configure enabled icon button.
     * @return the mapSet configuration
     */
    public static TethysIconMapSet<Boolean> configureEnabledIconButton() {
        final TethysIconMapSet<Boolean> myMapSet = new TethysIconMapSet<>();
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, ACTIVE, TIP_DISABLE);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, DISABLED, TIP_ENABLE);
        return myMapSet;
    }
}
