/*******************************************************************************
 * Tethys: GUI Utilities
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

import java.util.function.Supplier;

import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIControl.TethysUIIconMapSet;

/**
 * IconButton Manager.
 * <p>
 * Provides the following events.
 * <dl>
 * <dt>NEWVALUE
 * <dd>fired when a new value is selected. <br>
 * Detail is new value
 * </dl>
 * @param <T> the object type
 */
public interface TethysUIIconButtonManager<T>
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Obtain value.
     * @return the value
     */
    T getValue();

    /**
     * Obtain value class.
     * @return the class
     */
    Class<T> getValueClass();

    /**
     * Set the value.
     * @param pValue the value to set
     */
    void setValue(T pValue);

    /**
     * Set the mapSet selector.
     * @param pSelector the selector
     */
    void setIconMapSet(Supplier<TethysUIIconMapSet<T>> pSelector);

    /**
     * Get the mapSet selector.
     * @return the selector
     */
    Supplier<TethysUIIconMapSet<T>> getIconMapSet();

    /**
     * Set Null Margins.
     */
    void setNullMargins();

    /**
     * Apply button state.
     */
    void applyButtonState();

    /**
     * Progress state.
     */
    void progressToNextState();
}
