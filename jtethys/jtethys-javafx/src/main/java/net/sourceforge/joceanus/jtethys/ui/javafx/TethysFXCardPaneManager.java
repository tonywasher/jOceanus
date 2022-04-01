/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.scene.layout.BorderPane;

import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;

/**
 * Tethys FX Card panel manager.
 *
 * @param <P> the card panel type
 */
public class TethysFXCardPaneManager<P extends TethysComponent>
        extends TethysCardPaneManager<P> {
    /**
     * The node.
     */
    private final TethysFXNode theNode;

    /**
     * The panel.
     */
    private final BorderPane theCardPane;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    TethysFXCardPaneManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theCardPane = new BorderPane();
        theNode = new TethysFXNode(theCardPane);
    }

    @Override
    public TethysFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theCardPane.setDisable(!pEnabled);
        super.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void addCard(final String pName,
                        final P pCard) {
        final boolean isActiveCard = getActiveCard() == null;
        super.addCard(pName, pCard);
        if (isActiveCard) {
            theCardPane.setCenter(TethysFXNode.getNode(pCard));
        }
    }

    @Override
    public boolean selectCard(final String pName) {
        /* Determine the card to select */
        final boolean isSelected = super.selectCard(pName);
        if (isSelected) {
            /* Show selected card */
            theCardPane.setCenter(TethysFXNode.getNode(getActiveCard()));
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
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        super.setBorderTitle(pTitle);
        theNode.createWrapperPane(getBorderTitle(), getBorderPadding());
    }
}
