/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.tethys.javafx.menu;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;

import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.core.menu.TethysUICoreToolBarManager;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXIcon;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXUtils;

/**
 * JavaFX ToolBar Manager.
 */
public class TethysUIFXToolBarManager
        extends TethysUICoreToolBarManager {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The ToolBar.
     */
    private final ToolBar theToolBar;

    /**
     * Constructor.
     *
     * @param pFactory the factory
     */
    TethysUIFXToolBarManager(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theToolBar = new ToolBar();
        theNode = new TethysUIFXNode(theToolBar);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theToolBar.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theToolBar.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theToolBar.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theToolBar.setPrefHeight(pHeight);
    }

    @Override
    public TethysUIFXIconElement newIcon(final TethysUIToolBarId pId) {
        final TethysUIFXIconElement myIcon = new TethysUIFXIconElement(this, pId);
        theToolBar.getItems().add(myIcon.getButton());
        return myIcon;
    }

    @Override
    public void newSeparator() {
        theToolBar.getItems().add(new Separator());
    }

    /**
     * JavaFXIcon.
     */
    public static class TethysUIFXIconElement
            extends TethysUICoreToolElement {
        /**
         * The Button.
         */
        private final Button theButton;

        /**
         * Constructor.
         *
         * @param pManager the manager
         * @param pId      the Id
         */
        TethysUIFXIconElement(final TethysUIFXToolBarManager pManager,
                              final TethysUIToolBarId pId) {
            super(pManager, pId);
            theButton = new Button();
            theButton.setOnAction(e -> handlePressed());
            setIcon(pId);
        }

        /**
         * Obtain the button.
         *
         * @return the button
         */
        protected Button getButton() {
            return theButton;
        }

        @Override
        protected TethysUIFXToolBarManager getManager() {
            return (TethysUIFXToolBarManager) super.getManager();
        }

        @Override
        protected void setVisible(final boolean pVisible) {
            theButton.setVisible(pVisible);
        }

        @Override
        protected void enableItem(final boolean pEnabled) {
            theButton.setDisable(!pEnabled);
        }

        @Override
        public void setText(final String pText) {
            theButton.setText(pText);
        }

        @Override
        public void setToolTip(final String pTip) {
            final Tooltip myToolTip = pTip == null
                    ? null
                    : new Tooltip(pTip);
            theButton.setTooltip(myToolTip);
        }

        /**
         * Set the icon.
         * @param pIcon the icon
         */
        private void setIcon(final TethysUIToolBarId pIcon) {
            theButton.setGraphic(TethysUIFXIcon.getIcon(TethysUIFXUtils.getIconAtSize(pIcon, getManager().getIconSize())));
        }

        @Override
        public void setIconOnly() {
            theButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            theButton.setAlignment(Pos.CENTER);
            theButton.setMaxWidth(Double.MAX_VALUE);
        }

        @Override
        public void setTextAndIcon() {
            theButton.setContentDisplay(ContentDisplay.TOP);
            theButton.setAlignment(Pos.CENTER);
            theButton.setMaxWidth(Double.MAX_VALUE);
            theButton.setMaxHeight(Double.MAX_VALUE);
        }

        @Override
        public void setTextOnly() {
            theButton.setContentDisplay(ContentDisplay.TEXT_ONLY);
            theButton.setAlignment(Pos.CENTER);
            theButton.setMaxWidth(Double.MAX_VALUE);
        }
    }
}
