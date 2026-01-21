/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.tethys.core.menu;

/**
 * Core Scroll Menu.
 */
public final class TethysUICoreScrollMenu {
    /**
     * Private constructor.
     */
    private TethysUICoreScrollMenu() {
    }

    /**
     * Default number of items for scroll window.
     */
    public static final int DEFAULT_ITEMCOUNT = 15;

    /**
     * Initial scroll delay when hovering over icon.
     */
    public static final int INITIAL_SCROLLDELAY = 1000;

    /**
     * Default scroll delay when hovering over icon.
     */
    public static final int REPEAT_SCROLLDELAY = 150;

    /**
     * MaxDisplayItems error.
     */
    public static final String ERROR_MAXITEMS = "Maximum Display items must be greater than 0";
}
