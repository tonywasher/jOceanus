/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2024 Tony Washer
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

import java.util.List;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIListButton;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * ListButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>NEWVALUE
 * <dd>fired when the dialog is closed with new values selected. <br>
 * Detail is the new set of values.
 * <dt>EDITFOCUSLOST
 * <dd>fired when the dialog is cancelled without a value being selected.
 * </dl>
 * @param <T> the object type
 */
public interface TethysUIListButtonManager<T extends Comparable<? super T>>
        extends TethysUIListButton<T>, TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain menu.
     * @return the menu
     */
    TethysUIScrollMenu<T> getMenu();

    /**
     * Set Text.
     * @param pText the text
     */
    void setText(String pText);

    /**
     * Set the value.
     * @param pValue the value
     */
    void setValue(List<T> pValue);

    /**
     * Obtain the value.
     * @return the value
     */
    List<T> getValue();

    /**
     * handleMenuRequest.
     */
    void handleMenuRequest();

    /**
     * buildMenu.
     * @return is menu display-able?
     */
    boolean buildMenu();

    /**
     * Obtain the text value.
     * @return the formatted values
     */
    String getText();
}
