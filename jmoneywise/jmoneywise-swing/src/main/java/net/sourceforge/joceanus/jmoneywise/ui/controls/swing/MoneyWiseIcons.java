/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls.swing;

import java.awt.Image;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.PrometheusIcons;
import net.sourceforge.joceanus.jtethys.ui.swing.JIconButton.ComplexIconButtonState;

/**
 * Utility class to manage status icon buttons.
 * @deprecated as of 1.5.0 use {@link net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon}
 */
@Deprecated
public final class MoneyWiseIcons
        extends PrometheusIcons {
    /**
     * The program icon.
     */
    private static final ImageIcon ICON_PROGRAM = new ImageIcon(MoneyWiseUIResource.class.getResource("icons/MoneyWiseIcon.png"));

    /**
     * The program splash icon.
     */
    private static final ImageIcon ICON_SPLASH = new ImageIcon(MoneyWiseUIResource.class.getResource("icons/MoneyWiseBig.png"));

    /**
     * The locked icon.
     */
    private static final Icon ICON_LOCKED = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyClosedBook.png")));

    /**
     * The unlocked icon.
     */
    private static final Icon ICON_UNLOCKED = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyOpenBook.png")));

    /**
     * The unlock-able icon.
     */
    private static final Icon ICON_UNLOCKABLE = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/GreenJellyClosedBook.png")));

    /**
     * The lock-able icon.
     */
    private static final Icon ICON_LOCKABLE = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/GreenJellyOpenBook.png")));

    /**
     * The DirectionTo locked icon.
     */
    private static final Icon ICON_DIRTOLOCKED = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyDirectionTo.png")));

    /**
     * The DirectionFrom locked icon.
     */
    private static final Icon ICON_DIRFROMLOCKED = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyDirectionFrom.png")));

    /**
     * The DirectionTo icon.
     */
    private static final Icon ICON_DIRTO = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/GreenJellyDirectionTo.png")));

    /**
     * The DirectionFrom icon.
     */
    private static final Icon ICON_DIRFROM = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/GreenJellyDirectionFrom.png")));

    /**
     * The frozen reconciled icon.
     */
    private static final Icon ICON_FROZEN_RECONCILED = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyCheck.png")),
            ICON_XTRA_SIZE);

    /**
     * The cleared check box.
     */
    private static final Icon ICON_BOXCLEAR = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/GreenJellyClearBox.png")));

    /**
     * The set check box.
     */
    private static final Icon ICON_BOXCHECK = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/GreenJellyCheckBox.png")));

    /**
     * The frozen cleared check box.
     */
    private static final Icon ICON_FROZEN_BOXCLEAR = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyClearBox.png")));

    /**
     * The frozen set check box.
     */
    private static final Icon ICON_FROZEN_BOXCHECK = resizeImage(new ImageIcon(MoneyWiseUIResource.class.getResource("icons/BlueJellyCheckBox.png")));

    /**
     * Reconcile Button ToolTip.
     */
    private static final String TIP_RECONCILE = MoneyWiseUIResource.ICON_RECONCILE.getValue();

    /**
     * Release Button ToolTip.
     */
    private static final String TIP_RELEASE = MoneyWiseUIResource.ICON_RELEASE.getValue();

    /**
     * Frozen Button ToolTip.
     */
    private static final String TIP_FROZEN = MoneyWiseUIResource.ICON_FROZEN.getValue();

    /**
     * Locked Button ToolTip.
     */
    private static final String TIP_LOCKED = MoneyWiseUIResource.ICON_LOCKED.getValue();

    /**
     * Lock Button ToolTip.
     */
    private static final String TIP_LOCK = MoneyWiseUIResource.ICON_LOCK.getValue();

    /**
     * UnLock Button ToolTip.
     */
    private static final String TIP_UNLOCK = MoneyWiseUIResource.ICON_UNLOCK.getValue();

    /**
     * DirectionTo Button ToolTip.
     */
    private static final String TIP_DIRTO = MoneyWiseUIResource.ICON_DIRTO.getValue();

    /**
     * DirectionFrom Button ToolTip.
     */
    private static final String TIP_DIRFROM = MoneyWiseUIResource.ICON_DIRFROM.getValue();

    /**
     * Private constructor to prevent instantiation.
     */
    private MoneyWiseIcons() {
    }

    /**
     * Obtain the program Image.
     * @return the program Image
     */
    public static List<Image> getProgramImages() {
        List<Image> myList = new ArrayList<>();
        myList.add(ICON_PROGRAM.getImage());
        myList.add(ICON_SPLASH.getImage());
        return myList;
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
        pState.setIconForValue(Boolean.FALSE, ICON_UNLOCKED);
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
     * Build direction button state.
     * @param pState the button state
     */
    public static void buildDirectionButton(final ComplexIconButtonState<AssetDirection, Boolean> pState) {
        pState.setState(Boolean.TRUE);
        pState.setIconForValue(AssetDirection.TO, ICON_DIRTO);
        pState.setIconForValue(AssetDirection.FROM, ICON_DIRFROM);
        pState.setTooltipForValue(AssetDirection.TO, TIP_DIRTO);
        pState.setTooltipForValue(AssetDirection.FROM, TIP_DIRFROM);
        pState.setNewValueForValue(AssetDirection.TO, AssetDirection.FROM);
        pState.setNewValueForValue(AssetDirection.FROM, AssetDirection.TO);
        pState.setState(Boolean.FALSE);
        pState.setIconForValue(AssetDirection.TO, ICON_DIRTOLOCKED);
        pState.setIconForValue(AssetDirection.FROM, ICON_DIRFROMLOCKED);
        pState.setTooltipForValue(AssetDirection.TO, TIP_DIRTO);
        pState.setTooltipForValue(AssetDirection.FROM, TIP_DIRFROM);
        pState.setNewValueForValue(AssetDirection.TO, AssetDirection.TO);
        pState.setNewValueForValue(AssetDirection.FROM, AssetDirection.FROM);
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
