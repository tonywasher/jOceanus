/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.control;

import net.sourceforge.joceanus.tethys.api.base.TethysUIAlignment;
import net.sourceforge.joceanus.tethys.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIcon;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

/**
 * Label.
 */
public interface TethysUILabel
        extends TethysUIComponent {
    /**
     * Set Text.
     * @param pText the text
     */
    void setText(String pText);

    /**
     * Set error text colour.
     */
    void setErrorText();

    /**
     * Obtain the width.
     * @return the width
     */
    Integer getWidth();

    /**
     * Set Alignment.
     * @param pAlign the alignment
     */
    void setAlignment(TethysUIAlignment pAlign);

    /**
     * Set context menu.
     * @param pMenu the context menu.
     */
    void setContextMenu(TethysUIScrollMenu<?> pMenu);

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

    /**
     * Set icon for label.
     * @param pId the icon Id
     */
    void setIcon(TethysUIIconId pId);

    /**
     * Set icon for label.
     * @param pIcon the icon
     */
    void setIcon(TethysUIArrowIconId pIcon);

    /**
     * Set icon for label.
     * @param pIcon the icon
     */
    void setIcon(TethysUIIcon pIcon);

    /**
     * Obtain icon size.
     * @return the size
     */
    int getIconSize();

    /**
     * Set the icon size.
     * @param pSize the size to set
     */
    void setIconSize(int pSize);
}
