/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.javafx;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Separator;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysToolBarManager;

/**
 * JavaFX ToolBar Manager.
 */
public class TethysFXToolBarManager
        extends TethysToolBarManager<Node, Node> {
    /**
     * The ToolBar.
     */
    private final ToolBar theToolBar;

    /**
     * Constructor.
     * @param pFactory the factory
     */
    public TethysFXToolBarManager(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theToolBar = new ToolBar();
    }

    @Override
    public ToolBar getNode() {
        return theToolBar;
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
    public <I> TethysFXIconElement<I> newIcon(final I pId) {
        final TethysFXIconElement<I> myIcon = new TethysFXIconElement<>(this, pId);
        theToolBar.getItems().add(myIcon.getButton());
        return myIcon;
    }

    @Override
    public void newSeparator() {
        theToolBar.getItems().add(new Separator());
    }

    /**
     * JavaFXIcon.
     * @param <S> the id type
     */
    public static class TethysFXIconElement<S>
            extends TethysToolElement<S, Node, Node> {
        /**
         * The Button.
         */
        private final Button theButton;

        /**
         * Constructor.
         * @param pManager the manager
         * @param pId the Id
         */
        protected TethysFXIconElement(final TethysFXToolBarManager pManager,
                                      final S pId) {
            super(pManager, pId);
            theButton = new Button();
            theButton.setOnAction(e -> handlePressed());
        }

        /**
         * Obtain the button.
         * @return the button
         */
        protected Button getButton() {
            return theButton;
        }

        @Override
        protected TethysFXToolBarManager getManager() {
            return (TethysFXToolBarManager) super.getManager();
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

        @Override
        public <K extends Enum<K> & TethysIconId> void setIcon(final K pId) {
            setIcon(TethysFXGuiUtils.getIconAtSize(pId, getManager().getIconWidth()));
        }

        @Override
        public void setIcon(final Node pIcon) {
            theButton.setGraphic(pIcon);
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
