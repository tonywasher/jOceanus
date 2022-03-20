/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIScrollButton;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * ScrollButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>NEWVALUE
 * <dd>fired when a new value is selected. <br>
 * Detail is new value
 * <dt>EDITFOCUSLOST
 * <dd>fired when the dialog is cancelled without a value being selected.
 * </dl>
 * @param <T> the object type
 */
public interface TethysUIScrollButtonManager<T>
        extends TethysUIScrollButton<T>, TethysEventProvider<TethysUIXEvent>, TethysUIComponent {
    /**
     * Obtain value.
     * @return the value
     */
    T getValue();

    /**
     * Obtain menu.
     * @return the menu
     */
    TethysUIScrollMenu<T> getMenu();

    /**
     * Set the value.
     * @param pValue the value to set.
     */
    void setValue(T pValue);

    /**
     * Set fixed text for the button.
     * @param pText the fixed text.
     */
    void setFixedText(String pText);

    /**
     * Set the value.
     * @param pValue the value to set.
     * @param pName the display name
     */
    void setValue(T pValue,
                  String pName);

    /**
     * Set simple details.
     * @param pId the mapped IconId
     * @param pWidth the icon width
     * @param pToolTip the toolTip for value
     */
    void setSimpleDetails(TethysUIIconId pId,
                          int pWidth,
                          String pToolTip);

    /**
     * Set Null Margins.
     */
    void setNullMargins();

    /**
     * Refresh Text from item.
     */
    void refreshText();

    /**
     * handleMenuRequest.
     */
    void handleMenuRequest();
}
