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
package net.sourceforge.joceanus.jtethys.ui.core.control;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUICheckBox;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;

/**
 * Check Box.
 * <p>
 * The EventProvider fires the following events.
 * <ul>
 *    <li>TethysUIEvent.NEWVALUE is fired when the checkBox value is changed.
 * </ul>
 */
public abstract class TethysUICoreCheckBox
        extends TethysUICoreComponent
        implements TethysUICheckBox {
    /**
     * Event Manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * is the CheckBox selected?
     */
    private boolean isSelected;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysUICoreCheckBox(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setSelected(final boolean pSelected) {
        isSelected = pSelected;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Set handle selected.
     * @param pSelected is the box selected.
     */
    protected void handleSelected(final Boolean pSelected) {
        /* Only fire if the selection has changed */
        if (Boolean.TRUE.equals(pSelected) != isSelected) {
            /* record selection and fire event */
            isSelected = pSelected;
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pSelected);
        }
    }
}
