/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.HashMap;
import java.util.Map;

/**
 * Tethys Card panel manager.
 * @param <P> the card panel type
 */
public abstract class TethysCardPaneManager<P extends TethysComponent>
        implements TethysComponent {
    /**
     * The id.
     */
    private final Integer theId;

    /**
     * The panel map.
     */
    private final Map<String, P> theMap;

    /**
     * The Padding.
     */
    private Integer thePadding;

    /**
     * The Title.
     */
    private String theTitle;

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
    protected TethysCardPaneManager(final TethysGuiFactory pFactory) {
        theId = pFactory.getNextId();
        theMap = new HashMap<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    /**
     * Obtain the Border Padding.
     * @return the Padding.
     */
    protected Integer getBorderPadding() {
        return thePadding;
    }

    /**
     * Obtain the Border Title.
     * @return the Title.
     */
    protected String getBorderTitle() {
        return theTitle;
    }

    /**
     * Set the Border Padding.
     * @param pPadding the border padding
     */
    public void setBorderPadding(final Integer pPadding) {
        thePadding = pPadding;
    }

    /**
     * Set the Border Title.
     * @param pTitle the border title
     */
    public void setBorderTitle(final String pTitle) {
        theTitle = pTitle;
    }

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    public abstract void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    public abstract void setPreferredHeight(Integer pHeight);

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
        for (TethysComponent myPane : theMap.values()) {
            myPane.setEnabled(pEnabled);
        }
    }
}
