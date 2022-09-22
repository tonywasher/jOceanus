/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.ui;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.ui.MetisIcon;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.button.TethysUIButtonFactory;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;

/**
 * MoneyWise Icon IDs.
 */
public enum MoneyWiseIcon implements TethysUIIconId {
    /**
     * The program icon.
     */
    SMALL("icons/MoneyWiseSmall.png"),

    /**
     * The program icon.
     */
    BIG("icons/MoneyWiseBig.png"),

    /**
     * The splash program icon.
     */
    SPLASH("icons/MoneyWiseSplash.png"),

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

    @Override
    public InputStream getInputStream() {
        return MoneyWiseIcon.class.getResourceAsStream(theSource);
    }

    /**
     * Configure locked icon button.
     * @param pFactory the gui factory
     * @return the mapSet configuration
     */
    public static Map<Boolean, TethysUIIconMapSet<Boolean>> configureLockedIconButton(final TethysUIFactory<?> pFactory) {
        /* Create the map */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMap = new HashMap<>();

        /* Create the TRUE state */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        TethysUIIconMapSet<Boolean> myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, UNLOCKABLE, TIP_UNLOCK);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, LOCKABLE, TIP_LOCK);
        myMap.put(Boolean.TRUE, myMapSet);

        /* Create the FALSE state */
        myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.TRUE, LOCKED, TIP_LOCKED);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.FALSE, UNLOCKED, MetisIcon.TIP_ACTIVE);
        myMap.put(Boolean.FALSE, myMapSet);

        /* Return the map */
        return myMap;
    }

    /**
     * Configure reconciled icon button.
     * @param pFactory the gui factory
     * @return the mapSet configuration
     */
    public static Map<Boolean, TethysUIIconMapSet<Boolean>> configureReconciledIconButton(final TethysUIFactory<?> pFactory) {
        /* Create the map */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMap = new HashMap<>();

        /* Create the FALSE state */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        TethysUIIconMapSet<Boolean> myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, MetisIcon.COMMIT, TIP_RELEASE);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, TIP_RECONCILE);
        myMap.put(Boolean.FALSE, myMapSet);

        /* Create the TRUE state */
        myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.TRUE, FROZENRECONCILED, TIP_FROZEN);
        myMap.put(Boolean.TRUE, myMapSet);

        /* Return the map */
        return myMap;
    }

    /**
     * Configure direction icon button.
     * @param pFactory the gui factory
     * @return the mapSet configuration
     */
    public static Map<Boolean, TethysUIIconMapSet<AssetDirection>> configureDirectionIconButton(final TethysUIFactory<?> pFactory) {
        /* Create the map */
        final Map<Boolean, TethysUIIconMapSet<AssetDirection>> myMap = new HashMap<>();

        /* Create the FALSE state */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        TethysUIIconMapSet<AssetDirection> myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(AssetDirection.TO, AssetDirection.FROM, DIRTO, TIP_DIRTO);
        myMapSet.setMappingsForValue(AssetDirection.FROM, AssetDirection.TO, DIRFROM, TIP_DIRFROM);
        myMap.put(Boolean.FALSE, myMapSet);

        /* Create the TRUE state */
        myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(AssetDirection.TO, AssetDirection.TO, DIRTOLOCKED, TIP_DIRTO);
        myMapSet.setMappingsForValue(AssetDirection.FROM, AssetDirection.FROM, DIRFROMLOCKED, TIP_DIRFROM);
        myMap.put(Boolean.TRUE, myMapSet);

        /* Return the map */
        return myMap;
    }

    /**
     * Configure option icon button.
     * @param pFactory the gui factory
     * @return the mapSet configuration
     */
    public static Map<Boolean, TethysUIIconMapSet<Boolean>> configureOptionIconButton(final TethysUIFactory<?> pFactory) {
        /* Create the map */
        final Map<Boolean, TethysUIIconMapSet<Boolean>> myMap = new HashMap<>();

        /* Create the TRUE state */
        final TethysUIButtonFactory<?> myButtons = pFactory.buttonFactory();
        TethysUIIconMapSet<Boolean> myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.FALSE, BOXCHECK);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.TRUE, BOXCLEAR);
        myMap.put(Boolean.TRUE, myMapSet);

        /* Create the FALSE state */
        myMapSet = myButtons.newIconMapSet(MetisIcon.ICON_SIZE);
        myMapSet.setMappingsForValue(Boolean.TRUE, Boolean.TRUE, FROZENBOXCHECK);
        myMapSet.setMappingsForValue(Boolean.FALSE, Boolean.FALSE, FROZENBOXCLEAR);
        myMap.put(Boolean.FALSE, myMapSet);

        /* Return the map */
        return myMap;
    }
}
