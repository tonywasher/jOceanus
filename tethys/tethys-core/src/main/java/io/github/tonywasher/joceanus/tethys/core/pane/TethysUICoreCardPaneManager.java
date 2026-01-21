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
package io.github.tonywasher.joceanus.tethys.core.pane;

import java.util.HashMap;
import java.util.Map;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUICardPaneManager;
import io.github.tonywasher.joceanus.tethys.core.base.TethysUICoreComponent;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;

/**
 * Tethys Card panel manager.
 *
 * @param <P> the card panel type
 */
public abstract class TethysUICoreCardPaneManager<P extends TethysUIComponent>
        extends TethysUICoreComponent
        implements TethysUICardPaneManager<P> {
    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The panel map.
     */
    private final Map<String, P> theMap;

    /**
     * The active name.
     */
    private String theActiveName;

    /**
     * The active card.
     */
    private P theActiveCard;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    protected TethysUICoreCardPaneManager(final TethysUICoreFactory<?> pFactory) {
        theId = pFactory.getNextId();
        theMap = new HashMap<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }


    @Override
    public String getActiveName() {
        return theActiveName;
    }

    @Override
    public P getActiveCard() {
        return theActiveCard;
    }

    @Override
    public void addCard(final String pName,
                        final P pCard) {
        theMap.put(pName, pCard);
        if (theActiveName == null) {
            theActiveName = pName;
            theActiveCard = pCard;
        }
    }

    @Override
    public boolean selectCard(final String pName) {
        /* If the name is valid */
        final P myCard = theMap.get(pName);
        if (myCard != null) {
            /* Record selection */
            theActiveName = pName;
            theActiveCard = myCard;
            return true;
        }
        return false;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        for (TethysUIComponent myPane : theMap.values()) {
            myPane.setEnabled(pEnabled);
        }
    }
}
