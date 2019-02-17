/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.CardLayout;

import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;

/**
 * Tethys Swing Card panel manager.
 * @param <P> the card panel type
 */
public class TethysSwingCardPaneManager<P extends TethysComponent>
        extends TethysCardPaneManager<P> {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The layout.
     */
    private final CardLayout theLayout;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    TethysSwingCardPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);

        /* Create the panel */
        thePanel = new JPanel();
        theLayout = new CardLayout();
        thePanel.setLayout(theLayout);

        /* Create the node */
        theNode = new TethysSwingNode(thePanel);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
        super.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addCard(final String pName,
                        final P pCard) {
        super.addCard(pName, pCard);
        thePanel.add(TethysSwingNode.getComponent(pCard), pName);
    }

    @Override
    public boolean selectCard(final String pName) {
        /* Determine the card to select */
        final boolean isSelected = super.selectCard(pName);
        if (isSelected) {
            /* Show selected card */
            theLayout.show(thePanel, pName);
        }
        return isSelected;
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theNode.setPreferredWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theNode.setPreferredHeight(pHeight);
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
