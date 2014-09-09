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

import java.awt.Image;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcons;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;

/**
 * Utility class to manage status icon buttons.
 */
public final class MoneyWiseIcons
        extends PrometheusIcons {
    /**
     * The program icon.
     */
    private static final ImageIcon ICON_PROGRAM = new ImageIcon(MoneyWiseIcons.class.getResource("icons/MoneyWiseIcon.png"));

    /**
     * The locked icon.
     */
    private static final Icon ICON_LOCKED = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/AccountLocked.png")));

    /**
     * The unlock-able icon.
     */
    private static final Icon ICON_UNLOCKABLE = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/PadLock-icon.png")));

    /**
     * The lock-able icon.
     */
    private static final Icon ICON_LOCKABLE = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/Lock-icon.png")));

    /**
     * The frozen reconciled icon.
     */
    private static final Icon ICON_FROZEN_RECONCILED = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/BlueJellyCheck.png")),
            ICON_XTRA_SIZE);

    /**
     * The cleared check box.
     */
    private static final Icon ICON_BOXCLEAR = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/GreenJellyClearBox.png")));

    /**
     * The set check box.
     */
    private static final Icon ICON_BOXCHECK = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/GreenJellyCheckBox.png")));

    /**
     * The frozen cleared check box.
     */
    private static final Icon ICON_FROZEN_BOXCLEAR = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/BlueJellyClearBox.png")));

    /**
     * The frozen set check box.
     */
    private static final Icon ICON_FROZEN_BOXCHECK = resizeImage(new ImageIcon(MoneyWiseIcons.class.getResource("icons/BlueJellyCheckBox.png")));

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MoneyWiseIcons.class.getName());

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
     * Obtain the program Image.
     * @return the program Image
     */
    public static Image getProgramImage() {
        return ICON_PROGRAM.getImage();
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
        pState.setNewValueForValue(Boolean.TRUE, Boolean.TRUE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.FALSE);
    }

    /**
     * Build reconciled button state.
     * @param pState the button state
     */
    public static void buildReconciledButton(final ComplexIconButtonState<Boolean, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(Boolean.TRUE, ICON_COMMIT);
        pState.setTooltipForValue(Boolean.FALSE, TIP_RECONCILE);
        pState.setTooltipForValue(Boolean.TRUE, TIP_RELEASE);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.FALSE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.TRUE);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(Boolean.TRUE, ICON_FROZEN_RECONCILED);
        pState.setTooltipForValue(Boolean.TRUE, TIP_FROZEN);
        pState.setNewValueForValue(Boolean.TRUE, Boolean.TRUE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.FALSE);
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
        pState.setNewValueForValue(Boolean.TRUE, Boolean.TRUE);
        pState.setNewValueForValue(Boolean.FALSE, Boolean.FALSE);
    }
}
