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
package net.sourceforge.joceanus.jprometheus.lethe.ui.swing;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusGoToEvent;
import net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusUIResource;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JIconButton.DefaultIconButtonState;
import net.sourceforge.joceanus.jtethys.lethe.ui.swing.JScrollButton;

/**
 * Utility class to manage status icon buttons.
 * @deprecated as of 1.5.0 use {@link net.sourceforge.joceanus.jprometheus.lethe.ui.PrometheusIcon}
 */
@Deprecated
public abstract class PrometheusIcons {
    /**
     * Default icon extra size.
     */
    protected static final int ICON_XTRA_SIZE = 8;

    /**
     * The active icon.
     */
    private static final Icon ICON_ACTIVE = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/GreenJellySymbolActive.png")));

    /**
     * The delete icon.
     */
    private static final Icon ICON_DELETE = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/OrangeJellyAlphaDelete.png")),
            ICON_XTRA_SIZE);

    /**
     * The disabled icon.
     */
    private static final Icon ICON_DISABLED = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/OrangeJellyAlphaDisabled.png")));

    /**
     * The new icon.
     */
    private static final Icon ICON_NEW = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/GreenJellyPlus.png")));

    /**
     * The commit icon.
     */
    protected static final Icon ICON_COMMIT = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/GreenJellyCheck.png")), ICON_XTRA_SIZE);

    /**
     * The undo arrow.
     */
    protected static final Icon ICON_UNDO = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/OrangeJellyArrowLeft.png")), ICON_XTRA_SIZE);

    /**
     * The reset arrow.
     */
    protected static final Icon ICON_RESET = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/OrangeJellyDoubleArrowLeft.png")),
            ICON_XTRA_SIZE);

    /**
     * The edit arrow.
     */
    private static final Icon ICON_EDIT = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/GreenJellyBusinessEdit.png")), ICON_XTRA_SIZE);

    /**
     * The goto arrow.
     */
    private static final Icon ICON_GOTO = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/BlueJellyGoTo.png")), ICON_XTRA_SIZE);

    /**
     * The cancel arrow.
     */
    private static final Icon ICON_CANCEL = resizeImage(new ImageIcon(PrometheusUIResource.class.getResource("icons/OrangeJellyUndo.png")), ICON_XTRA_SIZE);

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
    protected static final String TIP_ACTIVE = PrometheusUIResource.ICON_TIP_ACTIVE.getValue();

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
     * Resize an icon to the row height.
     * @param pSource the source icon
     * @return the resized icon
     */
    protected static Icon resizeImage(final ImageIcon pSource) {
        return resizeImage(pSource, 0);
    }

    /**
     * Obtain new icon ScrollButton.
     * @param <T> the scroll button data type
     * @return the scroll button
     */
    public static <T> JScrollButton<T> getNewScrollButton() {
        JScrollButton<T> myButton = new JScrollButton<>(ICON_NEW);
        myButton.setToolTipText(TIP_NEW);
        return myButton;
    }

    /**
     * Obtain new icon ScrollButton.
     * @return the scroll button
     */
    public static JButton getNewButton() {
        JButton myButton = new JButton(ICON_NEW);
        myButton.setToolTipText(TIP_NEW);
        return myButton;
    }

    /**
     * Obtain goTo icon ScrollButton.
     * @return the scroll button
     */
    public static JScrollButton<PrometheusGoToEvent<?>> getGotoButton() {
        JScrollButton<PrometheusGoToEvent<?>> myButton = new JScrollButton<>(ICON_GOTO);
        myButton.setToolTipText(TIP_GOTO);
        return myButton;
    }

    /**
     * Resize an icon to the row height.
     * @param pSource the source icon
     * @param pExtraSize the extra size
     * @return the resized icon
     */
    protected static Icon resizeImage(final ImageIcon pSource,
                                      final int pExtraSize) {
        Image myImage = pSource.getImage();
        Image myNewImage = myImage.getScaledInstance(JDataTable.ROW_HEIGHT + pExtraSize,
                JDataTable.ROW_HEIGHT + pExtraSize,
                Image.SCALE_SMOOTH);
        return new ImageIcon(myNewImage);
    }

    /**
     * Build status button state.
     * @param pState the button state
     */
    public static void buildStatusButton(final DefaultIconButtonState<ActionType> pState) {
        /* Configure the status iconButton */
        pState.setIconForValue(ActionType.DELETE, ICON_DELETE);
        pState.setIconForValue(ActionType.ACTIVE, ICON_ACTIVE);
        pState.setIconForValue(ActionType.INSERT, ICON_NEW);
        pState.setTooltipForValue(ActionType.DELETE, TIP_DELETE);
        pState.setTooltipForValue(ActionType.ACTIVE, TIP_ACTIVE);
        pState.setTooltipForValue(ActionType.INSERT, TIP_NEW);
        pState.setNewValueForValue(ActionType.DELETE, ActionType.DO);
        pState.setNewValueForValue(ActionType.INSERT, ActionType.DO);
    }

    /**
     * Build status button state.
     * @param pState the button state
     */
    public static void buildDeleteButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_DELETE);
        pState.setTooltipForValue(Boolean.TRUE, TIP_DELETE);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build enabled button state.
     * @param pState the button state
     */
    public static void buildEnabledButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the status iconButton */
        pState.setIconForValue(Boolean.FALSE, ICON_DISABLED);
        pState.setIconForValue(Boolean.TRUE, ICON_ACTIVE);
        pState.setTooltipForValue(Boolean.FALSE, TIP_ENABLE);
        pState.setTooltipForValue(Boolean.TRUE, TIP_DISABLE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build commit button state.
     * @param pState the button state
     */
    public static void buildCommitButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_COMMIT);
        pState.setTooltipForValue(Boolean.TRUE, TIP_COMMIT);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build undo button state.
     * @param pState the button state
     */
    public static void buildUndoButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_UNDO);
        pState.setTooltipForValue(Boolean.TRUE, TIP_UNDO);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build reset button state.
     * @param pState the button state
     */
    public static void buildResetButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_RESET);
        pState.setTooltipForValue(Boolean.TRUE, TIP_RESET);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build edit button state.
     * @param pState the button state
     */
    public static void buildEditButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_EDIT);
        pState.setTooltipForValue(Boolean.TRUE, TIP_EDIT);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build cancel button state.
     * @param pState the button state
     */
    public static void buildCancelButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_CANCEL);
        pState.setTooltipForValue(Boolean.TRUE, TIP_CANCEL);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Action types.
     */
    public enum ActionType {
        /**
         * None.
         */
        DO,

        /**
         * None.
         */
        ACTIVE,

        /**
         * Delete.
         */
        DELETE,

        /**
         * Insert.
         */
        INSERT;
    }
}
