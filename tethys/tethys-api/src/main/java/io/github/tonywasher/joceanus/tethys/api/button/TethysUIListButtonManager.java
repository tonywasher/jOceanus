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
package io.github.tonywasher.joceanus.tethys.api.button;

import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIEvent;
import io.github.tonywasher.joceanus.tethys.api.control.TethysUIControl.TethysUIListButton;
import io.github.tonywasher.joceanus.tethys.api.menu.TethysUIScrollMenu;

import java.util.List;

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
 *
 * @param <T> the object type
 */
public interface TethysUIListButtonManager<T extends Comparable<? super T>>
        extends TethysUIListButton<T>, OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain menu.
     *
     * @return the menu
     */
    TethysUIScrollMenu<T> getMenu();

    /**
     * Set Text.
     *
     * @param pText the text
     */
    void setText(String pText);

    /**
     * Set the value.
     *
     * @param pValue the value
     */
    void setValue(List<T> pValue);

    /**
     * Obtain the value.
     *
     * @return the value
     */
    List<T> getValue();

    /**
     * handleMenuRequest.
     */
    void handleMenuRequest();

    /**
     * buildMenu.
     *
     * @return is menu display-able?
     */
    boolean buildMenu();

    /**
     * Obtain the text value.
     *
     * @return the formatted values
     */
    String getText();
}
