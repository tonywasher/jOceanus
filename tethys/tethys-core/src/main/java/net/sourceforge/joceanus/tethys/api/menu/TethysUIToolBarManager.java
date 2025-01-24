/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.menu;

import net.sourceforge.joceanus.oceanus.event.OceanusEvent.TethysEventListener;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIIconId;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;

/**
 * ToolBar Manager.
 */
public interface TethysUIToolBarManager
        extends TethysUIComponent {
    /**
     * ToolBarId.
     */
    interface TethysUIToolBarId
            extends TethysUIIconId {
    }

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
     * Set visible state for element.
     * @param pId the id of the element
     * @param pVisible true/false
     */
    void setVisible(TethysUIToolBarId pId,
                    boolean pVisible);

    /**
     * Set enabled state for element.
     * @param pId the id of the element
     * @param pEnabled true/false
     */
    void setEnabled(TethysUIToolBarId pId,
                    boolean pEnabled);

    /**
     * Configure Icon.
     * @param pId the IconId
     * @param pText the item Text
     * @param pListener the item listener
     */
    void newIcon(TethysUIToolBarId pId,
                 String pText,
                 TethysEventListener<TethysUIEvent> pListener);

    /**
     * Add a new Icon element.
     * @param pId the id of the element
     * @return the new element
     */
    TethysUIToolElement newIcon(TethysUIToolBarId pId);

    /**
     * Add Separator.
     */
    void newSeparator();

    /**
     * Look up icon.
     * @param pId the id of the element
     * @return the subMenu
     */
    TethysUIToolElement lookUpIcon(TethysUIToolBarId pId);

    /**
     * ToolElement.
     */
    interface TethysUIToolElement
            extends TethysEventProvider<TethysUIEvent> {
        /**
         * Set text for icon.
         * @param pText the text
         */
        void setText(String pText);

        /**
         * Set toolTip for icon.
         * @param pTip the toolTip
         */
        void setToolTip(String pTip);

        /**
         * Obtain the id.
         * @return the id
         */
        TethysUIToolBarId getId();

        /**
         * Is the icon enabled?
         * @return true/false
         */
        boolean isEnabled();

        /**
         * Set the enabled state of the menu.
         * @param pEnabled true/false
         */
        void setEnabled(boolean pEnabled);

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
}
