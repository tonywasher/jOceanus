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

import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons;
import net.sourceforge.joceanus.jtethys.event.ActionDetailEvent;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.DefaultIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;

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
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MoneyWiseIcons.class.getName());

    /**
     * GoTo Button ToolTip.
     */
    private static final String TIP_GOTO = NLS_BUNDLE.getString("ButtonGoTo");

    /**
     * Edit Button ToolTip.
     */
    private static final String TIP_EDIT = NLS_BUNDLE.getString("ButtonEdit");

    /**
     * Commit Button ToolTip.
     */
    private static final String TIP_COMMIT = NLS_BUNDLE.getString("ButtonCommit");

    /**
     * UnDo Button ToolTip.
     */
    private static final String TIP_UNDO = NLS_BUNDLE.getString("ButtonUnDo");

    /**
     * Reset Button ToolTip.
     */
    private static final String TIP_RESET = NLS_BUNDLE.getString("ButtonReset");

    /**
     * Cancel Button ToolTip.
     */
    private static final String TIP_CANCEL = NLS_BUNDLE.getString("ButtonCancel");

    /**
     * Reconcile Button ToolTip.
     */
    private static final String TIP_RECONCILE = NLS_BUNDLE.getString("ButtonReconcile");

    /**
     * Release Button ToolTip.
     */
    private static final String TIP_RELEASE = NLS_BUNDLE.getString("ButtonRelease");

    /**
     * Frozen Button ToolTip.
     */
    private static final String TIP_FROZEN = NLS_BUNDLE.getString("ButtonFrozen");

    /**
     * Locked Button ToolTip.
     */
    private static final String TIP_LOCKED = NLS_BUNDLE.getString("ButtonLocked");

    /**
     * Lock Button ToolTip.
     */
    private static final String TIP_LOCK = NLS_BUNDLE.getString("ButtonLock");

    /**
     * UnLock Button ToolTip.
     */
    private static final String TIP_UNLOCK = NLS_BUNDLE.getString("ButtonUnLock");

    /**
     * Private constructor to prevent instantiation.
     */
    private MoneyWiseIcons() {
    }

    /**
     * Obtain goTo icon ScrollButton.
     * @return the scroll button
     */
    public static JScrollButton<ActionDetailEvent> getGotoButton() {
        JScrollButton<ActionDetailEvent> myButton = new JScrollButton<ActionDetailEvent>(ICON_GOTO);
        myButton.setToolTipText(TIP_GOTO);
        return myButton;
    }

    /**
     * Build locked button state.
     * @param pState the button state
     */
    public static void buildLockedButton(final ComplexIconButtonState<Boolean, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(Boolean.FALSE, ICON_LOCKABLE);
        pState.setIconForValue(Boolean.TRUE, ICON_UNLOCKABLE);
        pState.setTooltipForValue(Boolean.FALSE, TIP_LOCK);
        pState.setTooltipForValue(Boolean.TRUE, TIP_UNLOCK);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(Boolean.TRUE, ICON_LOCKED);
        pState.setTooltipForValue(Boolean.FALSE, TIP_ACTIVE);
        pState.setTooltipForValue(Boolean.TRUE, TIP_LOCKED);
    }

    /**
     * Build reconciled button state.
     * @param pState the button state
     */
    public static void buildReconciledButton(final ComplexIconButtonState<Boolean, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(Boolean.TRUE, ICON_RECONCILED);
        pState.setTooltipForValue(Boolean.FALSE, TIP_RECONCILE);
        pState.setTooltipForValue(Boolean.TRUE, TIP_RELEASE);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(Boolean.TRUE, ICON_FROZEN_RECONCILED);
        pState.setTooltipForValue(Boolean.TRUE, TIP_FROZEN);
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
        pState.setTooltipForValue(Boolean.TRUE, TIP_EDIT);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }

    /**
     * Build commit button state.
     * @param pState the button state
     */
    public static void buildCommitButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_RECONCILED);
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
     * Build cancel button state.
     * @param pState the button state
     */
    public static void buildCancelButton(final DefaultIconButtonState<Boolean> pState) {
        /* Configure the delete iconButton */
        pState.setIconForValue(Boolean.TRUE, ICON_CANCEL);
        pState.setTooltipForValue(Boolean.TRUE, TIP_CANCEL);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
    }
}
