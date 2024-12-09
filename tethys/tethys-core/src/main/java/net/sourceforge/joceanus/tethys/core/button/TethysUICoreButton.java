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
package net.sourceforge.joceanus.tethys.core.button;

import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.tethys.api.base.TethysUIConstant;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.button.TethysUIButton;
import net.sourceforge.joceanus.tethys.core.base.TethysUICoreComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Tethys Core Button.
 */
public abstract class TethysUICoreButton
        extends TethysUICoreComponent
        implements TethysUIButton {
    /**
     * Default icon width.
     */
    protected static final int DEFAULT_ICONWIDTH = TethysUIConstant.DEFAULT_ICONWIDTH;

    /**
     * Event Manager.
     */
    private final OceanusEventManager<TethysUIEvent> theEventManager;

    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The icon Width.
     */
    private int theWidth;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysUICoreButton(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
        theEventManager = new OceanusEventManager<>();
        theWidth = DEFAULT_ICONWIDTH;
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
    public int getIconWidth() {
        return theWidth;
    }

    @Override
    public void setIconWidth(final int pWidth) {
        /* Store the width */
        theWidth = pWidth;
    }

    /**
     * handle pressed.
     */
    protected void handlePressed() {
        theEventManager.fireEvent(TethysUIEvent.PRESSED);
    }
}
