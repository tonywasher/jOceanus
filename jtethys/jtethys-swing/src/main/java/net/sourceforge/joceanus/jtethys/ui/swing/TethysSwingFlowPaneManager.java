/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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

import javax.swing.JPanel;

import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysFlowPaneManager;

/**
 * Swing Flow Pane Manager.
 */
public class TethysSwingFlowPaneManager
        extends TethysFlowPaneManager {
    /**
     * The Node.
     */
    private final TethysSwingNode theNode;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    TethysSwingFlowPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        thePanel = new JPanel();
        theNode = new TethysSwingNode(thePanel);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysComponent pNode) {
        super.addNode(pNode);
        thePanel.add(TethysSwingNode.getComponent(pNode));
    }

    @Override
    public void setChildVisible(final TethysComponent pChild,
                                final boolean pVisible) {
        TethysSwingNode.getComponent(pChild).setVisible(pVisible);
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
