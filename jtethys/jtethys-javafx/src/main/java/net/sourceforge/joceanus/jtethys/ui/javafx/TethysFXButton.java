/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;

import net.sourceforge.joceanus.jtethys.ui.TethysArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.TethysButton;
import net.sourceforge.joceanus.jtethys.ui.TethysIcon;
import net.sourceforge.joceanus.jtethys.ui.TethysIconId;

/**
 * Tethys FX Button.
 */
public class TethysFXButton
        extends TethysButton {
    /**
     * The node.
     */
    private final TethysFXNode theNode;

    /**
     * The button.
     */
    private final Button theButton;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysFXButton(final TethysFXGuiFactory pFactory) {
        super(pFactory);
        theButton = new Button();
        theNode = new TethysFXNode(theButton);
        theButton.setOnAction(e -> handlePressed());
    }

    @Override
    public TethysFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theButton.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setText(final String pText) {
        theButton.setText(pText);
    }

    @Override
    public void setIcon(final TethysArrowIconId pIcon) {
        setIcon(TethysFXArrowIcon.getIconForId(pIcon));
    }

    @Override
    public <K extends Enum<K> & TethysIconId> void setIcon(final K pId) {
        setIcon(TethysFXGuiUtils.getIconAtSize(pId, getIconWidth()));
    }

    @Override
    public void setIcon(final TethysIcon pIcon) {
        theButton.setGraphic(TethysFXIcon.getIcon(pIcon));
    }

    @Override
    public void setToolTip(final String pTip) {
        final Tooltip myToolTip = pTip == null
                                  ? null
                                  : new Tooltip(pTip);
        theButton.setTooltip(myToolTip);
    }

    @Override
    public void setNullMargins() {
        theButton.setPadding(Insets.EMPTY);
    }

    @Override
    public void setIconOnly() {
        theButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        theButton.setAlignment(Pos.CENTER);
        theButton.setMaxWidth(Double.MAX_VALUE);
    }

    @Override
    public void setTextAndIcon() {
        theButton.setContentDisplay(ContentDisplay.RIGHT);
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

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theButton.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theButton.setPrefHeight(pHeight);
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
