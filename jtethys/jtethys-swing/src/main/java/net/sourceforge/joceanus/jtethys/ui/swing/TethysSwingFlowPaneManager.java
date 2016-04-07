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

import javax.swing.BorderFactory;
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
    private final JPanel theNode;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected TethysSwingFlowPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theNode = new JPanel();
    }

    @Override
    public JComponent getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysNode<JComponent> pNode) {
        super.addNode(pNode);
        theNode.add(pNode.getNode());
    }

    @Override
    public void setChildVisible(final TethysNode<JComponent> pChild,
                                final boolean pVisible) {
        pChild.getNode().setVisible(pVisible);
    }

    @Override
    public void setBorderTitle(final String pTitle) {
        theNode.setBorder(BorderFactory.createTitledBorder(pTitle));
    }
}
