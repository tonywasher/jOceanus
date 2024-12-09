/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.pane;

import javafx.scene.control.ScrollPane;

import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.core.pane.TethysUICoreScrollPaneManager;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * javaFX Scroll Pane Manager.
 */
public class TethysUIFXScrollPaneManager
        extends TethysUICoreScrollPaneManager {
    /**
     * The node.
     */
    private final TethysUIFXNode theNode;

    /**
     * ScrollPane.
     */
    private final ScrollPane theScrollPane;

    /**
     * Content.
     */
    private TethysUIComponent theContent;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    TethysUIFXScrollPaneManager(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theScrollPane = new ScrollPane();
        theNode = new TethysUIFXNode(theScrollPane);
        theScrollPane.setFitToHeight(true);
        theScrollPane.setFitToWidth(true);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theScrollPane.setDisable(!pEnabled);
        if (theContent != null) {
            theContent.setEnabled(pEnabled);
        }
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setContent(final TethysUIComponent pNode) {
        theContent = pNode;
        theScrollPane.setContent(pNode == null
                ? null
                : TethysUIFXNode.getNode(pNode));
    }

    @Override
    public TethysUIComponent getContent() {
        return theContent;
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theScrollPane.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theScrollPane.setPrefHeight(pHeight);
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
