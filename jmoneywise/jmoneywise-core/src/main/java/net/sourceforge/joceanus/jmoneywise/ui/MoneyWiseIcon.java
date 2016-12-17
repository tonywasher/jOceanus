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
package net.sourceforge.joceanus.jmoneywise.ui;

import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jprometheus.ui.PrometheusIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconBuilder.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysStateIconButtonManager;

/**
 * MoneyWise Icon IDs.
 */
public enum MoneyWiseIcon implements TethysIconId {
    /**
     * The program icon.
     */
    PROGRAM("icons/MoneyWiseIcon.png"),

    /**
     * The program splash icon.
     */
    SPLASH("icons/MoneyWiseBig.png"),

    /**
     * The locked icon.
     */
    LOCKED("icons/BlueJellyClosedBook.png"),

    /**
     * The unlocked icon.
     */
    UNLOCKED("icons/BlueJellyOpenBook.png"),

    /**
     * The unlock-able icon.
     */
    UNLOCKABLE("icons/GreenJellyClosedBook.png"),

    /**
     * The lock-able icon.
     */
    LOCKABLE("icons/GreenJellyOpenBook.png"),

    /**
     * The DirectionTo locked icon.
     */
    DIRTOLOCKED("icons/BlueJellyDirectionTo.png"),

    /**
     * The DirectionFrom locked icon.
     */
    DIRFROMLOCKED("icons/BlueJellyDirectionFrom.png"),

    /**
     * The DirectionTo icon.
     */
    DIRTO("icons/GreenJellyDirectionTo.png"),

    /**
     * The DirectionFrom icon.
     */
    DIRFROM("icons/GreenJellyDirectionFrom.png"),

    /**
     * The frozen reconciled icon.
     */
    FROZENRECONCILED("icons/BlueJellyCheck.png"),

    /**
     * The cleared check box.
     */
    BOXCLEAR("icons/GreenJellyClearBox.png"),

    /**
     * The set check box.
     */
    BOXCHECK("icons/GreenJellyCheckBox.png"),

    /**
     * The frozen cleared check box.
     */
    FROZENBOXCLEAR("icons/BlueJellyClearBox.png"),

    /**
     * The frozen set check box.
     */
    FROZENBOXCHECK("icons/BlueJellyCheckBox.png");

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
     * Source name.
     */
    private final String theSource;

    /**
     * Constructor.
     * @param pSourceName the source name
     */
    MoneyWiseIcon(final String pSourceName) {
        theSource = pSourceName;
    }

    @Override
    public String getSourceName() {
        return theSource;
    }

    /**
     * Configure locked icon button.
     * @param pButton the button manager
     */
    public static void configureLockedIconButton(final TethysStateIconButtonManager<Boolean, Boolean, ?, ?> pButton) {
        pButton.setMachineState(Boolean.TRUE);
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.FALSE, UNLOCKABLE, TIP_UNLOCK);
        pButton.setDetailsForValue(Boolean.FALSE, Boolean.TRUE, LOCKABLE, TIP_LOCK);
        pButton.setMachineState(Boolean.FALSE);
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.TRUE, LOCKED, TIP_LOCKED);
        pButton.setDetailsForValue(Boolean.FALSE, Boolean.FALSE, UNLOCKED, PrometheusIcon.TIP_ACTIVE);
    }

    /**
     * Configure reconciled icon button.
     * @param pButton the button manager
     */
    public static void configureReconciledIconButton(final TethysStateIconButtonManager<Boolean, Boolean, ?, ?> pButton) {
        pButton.setWidth(MetisIcon.ICON_SIZE);
        pButton.setMachineState(Boolean.TRUE);
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.FALSE, PrometheusIcon.COMMIT, TIP_RELEASE);
        pButton.setDetailsForValue(Boolean.FALSE, Boolean.TRUE, TIP_RECONCILE);
        pButton.setMachineState(Boolean.FALSE);
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.TRUE, FROZENRECONCILED, TIP_FROZEN);
    }

    /**
     * Configure direction icon button.
     * @param pButton the button manager
     */
    public static void configureDirectionIconButton(final TethysStateIconButtonManager<AssetDirection, Boolean, ?, ?> pButton) {
        pButton.setMachineState(Boolean.TRUE);
        pButton.setDetailsForValue(AssetDirection.TO, AssetDirection.FROM, DIRTO, TIP_DIRTO);
        pButton.setDetailsForValue(AssetDirection.FROM, AssetDirection.TO, DIRFROM, TIP_DIRFROM);
        pButton.setMachineState(Boolean.FALSE);
        pButton.setDetailsForValue(AssetDirection.TO, AssetDirection.TO, DIRTOLOCKED, TIP_DIRTO);
        pButton.setDetailsForValue(AssetDirection.FROM, AssetDirection.FROM, DIRFROMLOCKED, TIP_DIRFROM);
    }

    /**
     * Configure option icon button.
     * @param pButton the button manager
     */
    public static void configureOptionIconButton(final TethysStateIconButtonManager<Boolean, Boolean, ?, ?> pButton) {
        pButton.setMachineState(Boolean.TRUE);
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.FALSE, BOXCHECK);
        pButton.setDetailsForValue(Boolean.FALSE, Boolean.TRUE, BOXCLEAR);
        pButton.setMachineState(Boolean.FALSE);
        pButton.setDetailsForValue(Boolean.TRUE, Boolean.TRUE, FROZENBOXCHECK);
        pButton.setDetailsForValue(Boolean.FALSE, Boolean.FALSE, FROZENBOXCLEAR);
    }
}
