/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2014 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Tethys Card panel manager.
 * @param <N> the node type
 * @param <C> the card item type
 */
public abstract class TethysCardPaneManager<N, C extends TethysNode<N>>
        implements TethysNode<N> {
    /**
     * The panel map.
     */
    private final Map<String, C> theMap;

    /**
     * The active name.
     */
    private String theActiveName;

    /**
     * The active card.
     */
    private C theActiveCard;

    /**
     * Constructor.
     */
    protected TethysCardPaneManager() {
        theMap = new HashMap<>();
    }

    /**
     * Get Active Name.
     * @return the name
     */
    public String getActiveName() {
        return theActiveName;
    }

    /**
     * Get Active Card.
     * @return the card
     */
    public C getActiveCard() {
        return theActiveCard;
    }

    /**
     * Add Card.
     * @param pName the name of the card.
     * @param pCard the card
     */
    public void addCard(final String pName,
                        final C pCard) {
        theMap.put(pName, pCard);
        if (theActiveName == null) {
            theActiveName = pName;
            theActiveCard = pCard;
        }
    }

    /**
     * Select Card.
     * @param pName the name of the card.
     * @return was card selected? true/false
     */
    public boolean selectCard(final String pName) {
        /* If the name is valid */
        C myCard = theMap.get(pName);
        if (myCard != null) {
            /* Record selection */
            theActiveName = pName;
            theActiveCard = myCard;
            return true;
        }
        return false;
    }
}
