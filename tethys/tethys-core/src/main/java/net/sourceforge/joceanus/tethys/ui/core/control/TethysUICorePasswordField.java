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
package net.sourceforge.joceanus.tethys.ui.core.control;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.ui.api.control.TethysUIPasswordField;
import net.sourceforge.joceanus.tethys.ui.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;

/**
 * Core Password Field.
 */
public abstract class TethysUICorePasswordField
        extends TethysUICoreComponent
        implements TethysUIPasswordField {
    /**
     * The Bullet character.
     */
    public static final char BULLET = '\u2022';

    /**
     * Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysUICorePasswordField(final TethysUICoreFactory<?> pFactory) {
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

    /**
     * fire Event.
     */
    protected void fireEvent() {
        theEventManager.fireEvent(TethysUIEvent.PRESSED);
    }
}
