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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Tethys FX Card panel manager.
 * @param <C> the card item type
 */
public class TethysFXCardPaneManager<C extends TethysNode<Node>>
        extends TethysCardPaneManager<Node, C> {
    /**
     * The panel.
     */
    private final BorderPane thePanel;

    /**
     * Constructor.
     */
    public TethysFXCardPaneManager() {
        thePanel = new BorderPane();
    }

    @Override
    public Node getNode() {
        return thePanel;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setDisable(!pEnabled);
        C myCard = getActiveCard();
        if (myCard != null) {
            myCard.setEnabled(pEnabled);
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addCard(final String pName,
                        final C pCard) {
        super.addCard(pName, pCard);
        if (getActiveCard() == null) {
            thePanel.setCenter(pCard.getNode());
        }
    }

    @Override
    public boolean selectCard(final String pName) {
        /* Determine the card to select */
        boolean isSelected = super.selectCard(pName);
        if (isSelected) {
            /* Show selected card */
            thePanel.setCenter(getActiveCard().getNode());
        }
        return isSelected;
    }
}
