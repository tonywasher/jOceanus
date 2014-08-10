/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;

/**
 * Utility class to manage status icon buttons.
 */
public final class MoneyWiseIcons
        extends PrometheusIcons {
    /**
     * The locked icon.
     */
    private static final Icon ICON_LOCKED = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("AccountLocked.png")));

    /**
     * The unlock-able icon.
     */
    private static final Icon ICON_UNLOCKABLE = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("PadLock-icon.png")));

    /**
     * The lock-able icon.
     */
    private static final Icon ICON_LOCKABLE = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("Lock-icon.png")));

    /**
     * The reconciled icon.
     */
    private static final Icon ICON_RECONCILED = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("GreenJellyCheck.png")), ICON_XTRA_SIZE);

    /**
     * The frozen reconciled icon.
     */
    private static final Icon ICON_FROZEN_RECONCILED = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("BlueJellyCheck.png")), ICON_XTRA_SIZE);

    /**
     * The cleared check box.
     */
    private static final Icon ICON_BOXCLEAR = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("GreenJellyClearBox.png")));

    /**
     * The set check box.
     */
    private static final Icon ICON_BOXCHECK = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("GreenJellyCheckBox.png")));

    /**
     * The frozen cleared check box.
     */
    private static final Icon ICON_FROZEN_BOXCLEAR = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("BlueJellyClearBox.png")));

    /**
     * The frozen set check box.
     */
    private static final Icon ICON_FROZEN_BOXCHECK = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("BlueJellyCheckBox.png")));

    /**
     * The undo arrow.
     */
    private static final Icon ICON_UNDO = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("OrangeJellyArrowLeft.png")), ICON_XTRA_SIZE);

    /**
     * The reset arrow.
     */
    private static final Icon ICON_RESET = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("OrangeJellyDoubleArrowLeft.png")), ICON_XTRA_SIZE);

    /**
     * The cancel arrow.
     */
    private static final Icon ICON_CANCEL = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("OrangeJellyUndo.png")), ICON_XTRA_SIZE);

    /**
     * The edit arrow.
     */
    private static final Icon ICON_EDIT = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("ItemEdit.png")), ICON_XTRA_SIZE);

    /**
     * The goto arrow.
     */
    private static final Icon ICON_GOTO = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("BlueJellyGoTo.png")), ICON_XTRA_SIZE);

    /**
     * The new icon.
     */
    private static final Icon ICON_NEW = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("GreenJellyPlus.png")));

    /**
     * Private constructor to prevent instantiation.
     */
    private MoneyWiseIcons() {
    }

    /**
     * Build locked button state.
     * @param pState the button state
     */
    public static void buildLockedButton(final ComplexIconButtonState<Boolean, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(Boolean.FALSE, ICON_LOCKABLE);
        pState.setIconForValue(Boolean.TRUE, ICON_UNLOCKABLE);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(Boolean.TRUE, ICON_LOCKED);
    }

    /**
     * Build reconciled button state.
     * @param pState the button state
     */
    public static void buildReconciledButton(final ComplexIconButtonState<Boolean, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(Boolean.TRUE, ICON_RECONCILED);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(Boolean.TRUE, ICON_FROZEN_RECONCILED);
    }

    /**
     * Build option button state.
     * @param pState the button state
     */
    public static void buildOptionButton(final ComplexIconButtonState<Boolean, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(Boolean.TRUE, ICON_BOXCHECK);
        pState.setIconForValue(Boolean.FALSE, ICON_BOXCLEAR);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(Boolean.TRUE, ICON_FROZEN_BOXCHECK);
        pState.setIconForValue(Boolean.FALSE, ICON_FROZEN_BOXCLEAR);
    }

    /**
     * Build edit button state.
     * @param pState the button state
     */
    public static void buildEditButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_EDIT);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build commit button state.
     * @param pState the button state
     */
    public static void buildCommitButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_RECONCILED);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build undo button state.
     * @param pState the button state
     */
    public static void buildUndoButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_UNDO);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build reset button state.
     * @param pState the button state
     */
    public static void buildResetButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_RESET);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build cancel button state.
     * @param pState the button state
     */
    public static void buildCancelButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_CANCEL);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }
}
