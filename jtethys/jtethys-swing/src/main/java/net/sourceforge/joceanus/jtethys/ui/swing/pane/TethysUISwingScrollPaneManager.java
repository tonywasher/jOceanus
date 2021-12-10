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
package net.sourceforge.joceanus.jtethys.ui.swing.pane;

import javax.swing.JScrollPane;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.core.base.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.core.pane.TethysUICoreScrollPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.base.TethysUISwingNode;

/**
 * swing Scroll Pane Manager.
 */
public class TethysUISwingScrollPaneManager
        extends TethysUICoreScrollPaneManager {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The pane.
     */
    private final JScrollPane theScrollPane;

    /**
     * Content.
     */
    private TethysUIComponent theContent;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    protected TethysUISwingScrollPaneManager(final TethysUICoreFactory pFactory) {
        super(pFactory);
        theScrollPane = new JScrollPane();
        theNode = new TethysUISwingNode(theScrollPane);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
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
        theNode.setVisible(pVisible);
    }

    @Override
    public void setContent(final TethysUIComponent pNode) {
        theContent = pNode;
        theScrollPane.setViewportView(pNode == null
                ? null
                : TethysUISwingNode.getComponent(pNode));
    }

    @Override
    public TethysUIComponent getContent() {
        return theContent;
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
