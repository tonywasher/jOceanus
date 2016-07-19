/*******************************************************************************
 * jTethys: Java Utilities
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.CardLayout;
import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysCardPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Tethys Swing Card panel manager.
 * @param <P> the card panel type
 */
public class TethysSwingCardPaneManager<P extends TethysNode<JComponent>>
        extends TethysCardPaneManager<JComponent, Icon, P> {
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
    protected TethysSwingCardPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        thePanel = new JPanel();
        theLayout = new CardLayout();
        thePanel.setLayout(theLayout);
    }

    @Override
    public JComponent getNode() {
        return thePanel;
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
        thePanel.add(pCard.getNode(), pName);
    }

    @Override
    public boolean selectCard(final String pName) {
        /* Determine the card to select */
        boolean isSelected = super.selectCard(pName);
        if (isSelected) {
            /* Show selected card */
            theLayout.show(thePanel, pName);
        }
        return isSelected;
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = thePanel.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        thePanel.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = thePanel.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        thePanel.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), thePanel);
    }
}
