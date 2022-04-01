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
package net.sourceforge.joceanus.jtethys.ui.core.dialog;

import java.util.Objects;

import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIXEvent;
import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIColorPicker;

/**
 * Colour Picker Core.
 * @param <C> the color type
 */
public abstract class TethysUICoreColorPicker<C>
        implements TethysUIColorPicker<C> {
    /**
     * The Event Manager.
     */
    private final TethysEventManager<TethysUIXEvent> theEventManager;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     */
    protected TethysUICoreColorPicker() {
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIXEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Set the value.
     * @param pValue the value
     */
    public void setValue(final String pValue) {
        theValue = pValue;
    }

    /**
     * Obtain the value.
     * @return the value
     */
    public String getValue() {
        return theValue;
    }

    /**
     * handle new value.
     * @param pValue the new value
     */
    protected void handleNewValue(final String pValue) {
        /* Only fire if the selection has changed */
        if (valueChanged(pValue)) {
            /* record selection and fire event */
            theValue = pValue;
            theEventManager.fireEvent(TethysUIXEvent.NEWVALUE, pValue);
        }
    }

    /**
     * has value changed?
     * @param pNew the new value
     * @return true/false
     */
    private boolean valueChanged(final String pNew) {
        return !Objects.equals(theValue, pNew);
    }

    /**
     * handle focus loss.
     */
    protected void handleFocusLoss() {
        theEventManager.fireEvent(TethysUIXEvent.WINDOWCLOSED);
    }
}
