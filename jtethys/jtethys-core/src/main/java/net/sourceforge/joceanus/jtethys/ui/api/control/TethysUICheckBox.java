/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.control;

import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;

/**
 * Check Box.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.NEWVALUE is fired when the checkBox value is changed.
 * </ul>
 */
public interface TethysUICheckBox
        extends TethysEventProvider<TethysUIXEvent>, TethysUIComponent {
    /**
     * handle selected.
     * @param pText the text.
     */
    void setText(String pText);

    /**
     * Set the changed status.
     * @param pChanged is the checkBox changed?
     */
    void setChanged(boolean pChanged);

    /**
     * set selected.
     * @param pSelected is the box selected?
     */
    void setSelected(boolean pSelected);

    /**
     * Is the box selected?
     * @return true/false
     */
    boolean isSelected();
}
