/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.core.dialog;

import java.util.Objects;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIColorPicker;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Colour Picker Core.
 * @param <C> the color type
 */
public abstract class TethysUICoreColorPicker<C>
        extends TethysUICoreComponent
        implements TethysUIColorPicker<C> {
    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The Value.
     */
    private String theValue;

    /**
     * Constructor.
     * @param pFactory the gui factory
     */
    protected TethysUICoreColorPicker(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
        theEventManager = new OceanusEventManager<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public OceanusEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    @Override
    public void setValue(final String pValue) {
        theValue = pValue;
    }

    @Override
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
            theEventManager.fireEvent(TethysUIEvent.NEWVALUE, pValue);
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
        theEventManager.fireEvent(TethysUIEvent.WINDOWCLOSED);
    }
}
