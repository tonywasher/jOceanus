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
package net.sourceforge.joceanus.tethys.api.button;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;

/**
 * Tethys Button.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a button is pressed
 * </ul>
 */
public interface TethysUIButton
        extends OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain icon size.
     *
     * @return the size
     */
    int getIconSize();

    /**
     * Set the icon size.
     *
     * @param pSize the size to set
     */
    void setIconSize(int pSize);

    /**
     * Set text for button.
     *
     * @param pText the text
     */
    void setText(String pText);

    /**
     * Set icon for button.
     *
     * @param pId the icon Id
     */
    void setIcon(TethysUIIconId pId);

    /**
     * Set icon for button.
     *
     * @param pIcon the icon
     */
    void setIcon(TethysUIArrowIconId pIcon);

    /**
     * Set icon for button.
     *
     * @param pIcon the icon
     */
    void setIcon(TethysUIIcon pIcon);

    /**
     * Set toolTip for button.
     *
     * @param pTip the toolTip
     */
    void setToolTip(String pTip);

    /**
     * Set Null Margins.
     */
    void setNullMargins();

    /**
     * Set Icon only.
     */
    void setIconOnly();

    /**
     * Set Text And Icon.
     */
    void setTextAndIcon();

    /**
     * Set Icon and Text.
     */
    void setIconAndText();

    /**
     * Set Text Only.
     */
    void setTextOnly();
}
