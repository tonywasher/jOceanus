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
import javafx.scene.layout.Region;
import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Tethys FX Card panel manager.
 * @param <P> the card panel type
 */
public class TethysFXCardPaneManager<P extends TethysNode<Node>>
        extends TethysCardPaneManager<Node, Node, P> {
    /**
     * The node.
     */
    private Region theNode;

    /**
     * The panel.
     */
    private final BorderPane theCardPane;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysFXCardPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theCardPane = new BorderPane();
        theNode = theCardPane;
    }

    @Override
    public Region getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theCardPane.setDisable(!pEnabled);
        super.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void addCard(final String pName,
                        final P pCard) {
        super.addCard(pName, pCard);
        if (getActiveCard() == null) {
            theCardPane.setCenter(pCard.getNode());
        }
    }

    @Override
    public boolean selectCard(final String pName) {
        /* Determine the card to select */
        boolean isSelected = super.selectCard(pName);
        if (isSelected) {
            /* Show selected card */
            theCardPane.setCenter(getActiveCard().getNode());
        }
        return isSelected;
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theCardPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theCardPane.setPrefHeight(pHeight);
    }

    @Override
    public void setBorderPadding(final Integer pPadding) {
        super.setBorderPadding(pPadding);
        createWrapperPane();
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        createWrapperPane();
    }

    /**
     * create wrapper pane.
     */
    private void createWrapperPane() {
        theNode = TethysFXGuiUtils.getBorderedPane(getBorderTitle(), getBorderPadding(), theCardPane);
    }
}