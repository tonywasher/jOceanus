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

import java.awt.Image;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;

/**
 * Utility class to manage status icon buttons.
 */
public abstract class PrometheusIcons {
    /**
     * Default icon extra size.
     */
    protected static final int ICON_XTRA_SIZE = 8;

    /**
     * The active icon.
     */
    private static final Icon ICON_ACTIVE = resizeImage(new ImageIcon(PrometheusIcons.class.getResource("Active.png")), ICON_XTRA_SIZE);

    /**
     * The delete icon.
     */
    private static final Icon ICON_DELETE = resizeImage(new ImageIcon(PrometheusIcons.class.getResource("RedDelete.png")), ICON_XTRA_SIZE);

    /**
     * The disabled icon.
     */
    private static final Icon ICON_DISABLED = resizeImage(new ImageIcon(PrometheusIcons.class.getResource("Disabled.png")));

    /**
     * The new icon.
     */
    private static final Icon ICON_NEW = resizeImage(new ImageIcon(PrometheusIcons.class.getResource("GreenJellyPlus.png")));

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(PrometheusIcons.class.getName());

    /**
     * Delete Button ToolTip.
     */
    private static final String TIP_DELETE = NLS_BUNDLE.getString("ButtonDelete");

    /**
     * New Button ToolTip.
     */
    private static final String TIP_NEW = NLS_BUNDLE.getString("ButtonNew");

    /**
     * Active Button ToolTip.
     */
    protected static final String TIP_ACTIVE = NLS_BUNDLE.getString("ButtonActive");

    /**
     * Enable Button ToolTip.
     */
    private static final String TIP_ENABLE = NLS_BUNDLE.getString("ButtonEnable");

    /**
     * Disable Button ToolTip.
     */
    private static final String TIP_DISABLE = NLS_BUNDLE.getString("ButtonDisable");

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
        JScrollButton<T> myButton = new JScrollButton<T>(ICON_NEW);
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
