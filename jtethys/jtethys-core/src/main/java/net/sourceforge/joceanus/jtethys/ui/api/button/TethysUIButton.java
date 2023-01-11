/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui.api.button;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;

/**
 * Tethys Button.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.PRESSED is fired when a button is pressed
 * </ul>
 */
public interface TethysUIButton
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain width.
     * @return the width
     */
    int getIconWidth();

    /**
     * Set the width.
     * @param pWidth the width to set
     */
    void setIconWidth(int pWidth);

    /**
     * Set text for button.
     * @param pText the text
     */
    void setText(String pText);

    /**
     * Set icon for button.
     * @param pId the icon Id
     */
    void setIcon(TethysUIIconId pId);

    /**
     * Set icon for button.
     * @param pIcon the icon
     */
    void setIcon(TethysUIArrowIconId pIcon);

    /**
     * Set icon for button.
     * @param pIcon the icon
     */
    void setIcon(TethysUIIcon pIcon);

    /**
     * Set toolTip for button.
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
     * Set Text Only.
     */
    void setTextOnly();
}
