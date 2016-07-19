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

import java.awt.Dimension;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollPaneManager;

/**
 * javaFX Scroll Pane Manager.
 */
public class TethysSwingScrollPaneManager
        extends TethysScrollPaneManager<JComponent, Icon> {
    /**
     * The node.
     */
    private final JScrollPane theScrollPane;

    /**
     * Content.
     */
    private TethysNode<JComponent> theContent;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    protected TethysSwingScrollPaneManager(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theScrollPane = new JScrollPane();
    }

    @Override
    public JComponent getNode() {
        return theScrollPane;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theScrollPane.setEnabled(pEnabled);
        if (theContent != null) {
            theContent.setEnabled(pEnabled);
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theScrollPane.setVisible(pVisible);
    }

    @Override
    public void setContent(final TethysNode<JComponent> pNode) {
        theContent = pNode;
        theScrollPane.setViewportView(pNode == null
                                                    ? null
                                                    : pNode.getNode());
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        Dimension myDim = theScrollPane.getPreferredSize();
        myDim = new Dimension(pWidth, myDim.height);
        theScrollPane.setPreferredSize(myDim);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        Dimension myDim = theScrollPane.getPreferredSize();
        myDim = new Dimension(myDim.width, pHeight);
        theScrollPane.setPreferredSize(myDim);
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
        TethysSwingGuiUtils.setPanelBorder(getBorderTitle(), getBorderPadding(), theScrollPane);
    }
}
