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
package net.sourceforge.joceanus.tethys.ui.api.button;

import net.sourceforge.joceanus.tethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;

/**
 * Colour Picker.
 * @param <C> the color type
 * <p>
 * Simply initialise and set visible. The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.NEWVALUE is fired when a colour is selected.
 *    <li>TethysUIEvent.WINDOWCLOSED is fired when the window is closed.
 * </ul>
 */
public interface TethysUIColorPicker<C>
        extends TethysUIComponent, TethysEventProvider<TethysUIEvent> {
    /**
     * Set the value.
     * @param pValue the value
     */
    void setValue(String pValue);

    /**
     * Obtain the value.
     * @return the value
     */
    String getValue();

    @Override
    void setVisible(boolean pVisible);

    /**
     * Obtain the colour.
     *
     * @return the colour
     */
     C getColour();
}
