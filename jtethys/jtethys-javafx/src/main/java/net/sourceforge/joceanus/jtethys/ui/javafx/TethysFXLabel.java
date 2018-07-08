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
import javafx.scene.control.Label;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.paint.Color;

import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;

/**
 * Tethys FX Label.
 */
public final class TethysFXLabel
        extends TethysLabel {
    /**
     * The node.
     */
    private final TethysFXNode theNode;

    /**
     * The Label.
     */
    private final Label theLabel;

    /**
     * The Context Menu.
     */
    private TethysFXScrollContextMenu<?> theContextMenu;

    /**
     * Has the context menu handler been set.
     */
    private boolean menuListenerSet;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysFXLabel(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theLabel = new Label();
        theNode = new TethysFXNode(theLabel);
        setAlignment(TethysAlignment.WEST);
    }

    @Override
    public void setText(final String pText) {
        theLabel.setText(pText);
    }

    @Override
    public void setErrorText() {
        theLabel.setTextFill(Color.RED);
    }

    @Override
    public void setAlignment(final TethysAlignment pAlign) {
        theLabel.setAlignment(determineAlignment(pAlign));
    }

    @Override
    public TethysFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theLabel.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    /**
     * Set context menu.
     *
     * @param pMenu the context menu.
     */
    public void setContextMenu(final TethysFXScrollContextMenu<?> pMenu) {
        /* Record the menu */
        theContextMenu = pMenu;

        /* If the listener has not been set */
        if (!menuListenerSet) {
            /* Set the handler */
            theLabel.setOnContextMenuRequested(this::handleContextMenu);
            menuListenerSet = true;
        }
    }

    /**
     * Translate alignment.
     *
     * @param pAlign the alignment
     * @return the FX alignment
     */
    private Pos determineAlignment(final TethysAlignment pAlign) {
        switch (pAlign) {
            case NORTHWEST:
                return Pos.TOP_LEFT;
            case NORTH:
                return Pos.TOP_CENTER;
            case NORTHEAST:
                return Pos.TOP_RIGHT;
            case WEST:
                return Pos.CENTER_LEFT;
            case CENTRE:
                return Pos.CENTER;
            case EAST:
                return Pos.CENTER_RIGHT;
            case SOUTHWEST:
                return Pos.BOTTOM_LEFT;
            case SOUTH:
                return Pos.BOTTOM_CENTER;
            case SOUTHEAST:
            default:
                return Pos.BOTTOM_RIGHT;
        }
    }

    @Override
    public Integer getWidth() {
        return (int) theLabel.getWidth();
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theLabel.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theLabel.setPrefHeight(pHeight);
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

    /**
     * handleContextMenu.
     *
     * @param pEvent the event
     */
    private void handleContextMenu(final ContextMenuEvent pEvent) {
        if (theContextMenu != null) {
            theContextMenu.showMenuAtPosition(theLabel, pEvent.getX(), pEvent.getY());
        }
    }
}
