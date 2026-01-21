/*
 * Tethys: GUI Utilities
 * Copyright 2012-2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.tonywasher.joceanus.tethys.swing.pane;

import javax.swing.JPanel;

import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.core.pane.TethysUICoreFlowPaneManager;
import io.github.tonywasher.joceanus.tethys.swing.base.TethysUISwingNode;

/**
 * Swing Flow Pane Manager.
 */
public class TethysUISwingFlowPaneManager
        extends TethysUICoreFlowPaneManager {
    /**
     * The Node.
     */
    private final TethysUISwingNode theNode;

    /**
     * The Panel.
     */
    private final JPanel thePanel;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    TethysUISwingFlowPaneManager(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        thePanel = new JPanel();
        theNode = new TethysUISwingNode(thePanel);
    }

    @Override
    public TethysUISwingNode getNode() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
    }

    @Override
    public void addNode(final TethysUIComponent pNode) {
        super.addNode(pNode);
        thePanel.add(TethysUISwingNode.getComponent(pNode));
    }

    @Override
    public void setChildVisible(final TethysUIComponent pChild,
                                final boolean pVisible) {
        TethysUISwingNode.getComponent(pChild).setVisible(pVisible);
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
