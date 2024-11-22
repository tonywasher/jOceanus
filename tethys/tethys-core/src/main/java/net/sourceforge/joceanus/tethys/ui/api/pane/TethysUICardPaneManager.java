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
package net.sourceforge.joceanus.tethys.ui.api.pane;

import net.sourceforge.joceanus.tethys.ui.api.base.TethysUIComponent;

/**
 * Tethys Card panel manager.
 * @param <P> the card panel type
 */
public interface TethysUICardPaneManager<P extends TethysUIComponent>
        extends TethysUIComponent {
    /**
     * Get Active Name.
     * @return the name
     */
    String getActiveName();

    /**
     * Get Active Card.
     * @return the card
     */
    P getActiveCard();

    /**
     * Add Card.
     * @param pName the name of the card.
     * @param pCard the card
     */
    void addCard(String pName,
                 P pCard);

    /**
     * Select Card.
     * @param pName the name of the card.
     * @return was card selected? true/false
     */
    boolean selectCard(String pName);
}
