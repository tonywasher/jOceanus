/*******************************************************************************
 * jPrometheus: Application Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.ui;

import net.sourceforge.joceanus.jprometheus.data.PrometheusAction;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysSimpleIconButtonManager;
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
     * Delete.
     */
    DELETE("icons/OrangeJellyAlphaDelete.png"),

    /**
     * Disabled.
     */
    DISABLED("icons/OrangeJellyAlphaDisabled.png"),

    /**
     * New.
     */
    NEW("icons/GreenJellyPlus.png"),

    /**
     * Commit.
     */
    COMMIT("icons/GreenJellyCheck.png"),

    /**
     * Undo.
     */
    UNDO("icons/OrangeJellyArrowLeft.png"),

    /**
     * Reset.
     */
    RESET("icons/OrangeJellyDoubleArrowLeft.png"),

    /**
     * Edit.
     */
    EDIT("icons/GreenJellyBusinessEdit.png"),

    /**
     * Goto.
     */
    GOTO("icons/BlueJellyGoTo.png"),

    /**
     * Cancel.
     */
    CANCEL("icons/OrangeJellyUndo.png");

    /**
     * Default icon size.
     */
    public static final int ICON_SIZE = 24;

    /**
     * Delete Button ToolTip.
     */
    private static final String TIP_DELETE = PrometheusUIResource.ICON_TIP_DELETE.getValue();

    /**
     * New Button ToolTip.
     */
    private static final String TIP_NEW = PrometheusUIResource.ICON_TIP_NEW.getValue();

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
     * Commit Button ToolTip.
     */
    private static final String TIP_COMMIT = PrometheusUIResource.ICON_TIP_COMMIT.getValue();

    /**
     * UnDo Button ToolTip.
     */
    private static final String TIP_UNDO = PrometheusUIResource.ICON_TIP_UNDO.getValue();

    /**
     * Reset Button ToolTip.
     */
    private static final String TIP_RESET = PrometheusUIResource.ICON_TIP_RESET.getValue();

    /**
     * GoTo Button ToolTip.
     */
    private static final String TIP_GOTO = PrometheusUIResource.ICON_TIP_GOTO.getValue();

    /**
     * Edit Button ToolTip.
     */
    private static final String TIP_EDIT = PrometheusUIResource.ICON_TIP_EDIT.getValue();

    /**
     * Cancel Button ToolTip.
     */
    private static final String TIP_CANCEL = PrometheusUIResource.ICON_TIP_CANCEL.getValue();

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
     * Configure simple icon button.
     * @param pButton the button manager
     */
    private static void configureSimpleIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        pButton.setWidth(ICON_SIZE);
        pButton.setNullMargins();
    }

    /**
     * Configure new scroll button.
     * @param pButton the button manager
     */
    public static void configureNewScrollButton(final TethysScrollButtonManager<?, ?, ?> pButton) {
        pButton.setSimpleDetails(NEW, TethysIconButtonManager.DEFAULT_ICONWIDTH, TIP_NEW);
    }

    /**
     * Configure goto scroll button.
     * @param pButton the button manager
     */
    public static void configureGoToScrollButton(final TethysScrollButtonManager<?, ?, ?> pButton) {
        pButton.setNullMargins();
        pButton.setSimpleDetails(GOTO, ICON_SIZE, TIP_GOTO);
    }

    /**
     * Configure new icon button.
     * @param pButton the button manager
     */
    public static void configureNewIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        pButton.setValue(Boolean.TRUE);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, NEW, TIP_NEW);
    }

    /**
     * Configure edit icon button.
     * @param pButton the button manager
     */
    public static void configureEditIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        configureSimpleIconButton(pButton);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, EDIT, TIP_EDIT);
        pButton.setValue(Boolean.TRUE);
    }

    /**
     * Configure delete icon button.
     * @param pButton the button manager
     */
    public static void configureDeleteIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        configureSimpleIconButton(pButton);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, DELETE, TIP_DELETE);
        pButton.setValue(Boolean.TRUE);
    }

    /**
     * Configure commit icon button.
     * @param pButton the button manager
     */
    public static void configureCommitIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        configureSimpleIconButton(pButton);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, COMMIT, TIP_COMMIT);
        pButton.setValue(Boolean.TRUE);
    }

    /**
     * Configure undo icon button.
     * @param pButton the button manager
     */
    public static void configureUndoIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        configureSimpleIconButton(pButton);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, UNDO, TIP_UNDO);
        pButton.setValue(Boolean.TRUE);
    }

    /**
     * Configure reset icon button.
     * @param pButton the button manager
     */
    public static void configureResetIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        configureSimpleIconButton(pButton);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, RESET, TIP_RESET);
        pButton.setValue(Boolean.TRUE);
    }

    /**
     * Configure cancel icon button.
     * @param pButton the button manager
     */
    public static void configureCancelIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        configureSimpleIconButton(pButton);
        pButton.setSimpleDetailsForValue(Boolean.TRUE, CANCEL, TIP_CANCEL);
        pButton.setValue(Boolean.TRUE);
    }

    /**
     * Configure status icon button.
     * @param pButton the button manager
     */
    public static void configureStatusIconButton(final TethysSimpleIconButtonManager<PrometheusAction, ?, ?> pButton) {
        pButton.setSimpleDetailsForValue(PrometheusAction.ACTIVE, ACTIVE, TIP_ACTIVE);
        pButton.setSimpleDetailsForValue(PrometheusAction.DELETE, DELETE, TIP_DELETE);
        pButton.setSimpleDetailsForValue(PrometheusAction.INSERT, NEW, TIP_NEW);
    }

    /**
     * Configure enabled icon button.
     * @param pButton the button manager
     */
    public static void configureEnabledIconButton(final TethysSimpleIconButtonManager<Boolean, ?, ?> pButton) {
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.FALSE, ACTIVE, TIP_DISABLE);
        pButton.setDetailsForValue(Boolean.FALSE, Boolean.TRUE, DISABLED, TIP_ENABLE);
    }
}
