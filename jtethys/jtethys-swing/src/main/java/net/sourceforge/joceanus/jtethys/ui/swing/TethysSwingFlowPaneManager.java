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
package net.sourceforge.joceanus.jtethys.ui.swing;

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysFlowPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;

/**
 * Swing Flow Pane Manager.
 */
public class TethysSwingFlowPaneManager
        extends TethysFlowPaneManager<JComponent, Icon> {
    /**
     * The Node.
     */
    private final JPanel thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingFlowPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        thePanel = new JPanel();
    }

    @Override
    public JComponent getNode() {
        return thePanel;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysNode<JComponent> pNode) {
        super.addNode(pNode);
        thePanel.add(pNode.getNode());
    }

    @Override
    public void setChildVisible(final TethysNode<JComponent> pChild,
                                final boolean pVisible) {
        pChild.getNode().setVisible(pVisible);
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
