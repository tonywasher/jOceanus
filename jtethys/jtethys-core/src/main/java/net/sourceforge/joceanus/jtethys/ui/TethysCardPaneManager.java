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
 * @param <I> the Icon Type
 * @param <P> the card panel type
 */
public abstract class TethysCardPaneManager<N, I, P extends TethysNode<N>>
        implements TethysNode<N> {
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
     * @param pFactory the GUI factory
     */
    protected TethysCardPaneManager(final TethysGuiFactory<N, I> pFactory) {
        theId = pFactory.getNextId();
        theMap = new HashMap<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Set the Border Title.
     * @param pTitle the title text
     */
    public abstract void setBorderTitle(final String pTitle);

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
    public P getActiveCard() {
        return theActiveCard;
    }

    /**
     * Add Card.
     * @param pName the name of the card.
     * @param pCard the card
     */
    public void addCard(final String pName,
                        final P pCard) {
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
        P myCard = theMap.get(pName);
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
        for (TethysNode<N> myPane : theMap.values()) {
            myPane.setEnabled(pEnabled);
        }
    }
}
