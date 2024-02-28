/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys.ui.javafx.button;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Tooltip;

import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIArrowIconId;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIcon;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIIconId;
import net.sourceforge.joceanus.jtethys.ui.core.button.TethysUICoreButton;
import net.sourceforge.joceanus.jtethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXIcon;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXNode;
import net.sourceforge.joceanus.jtethys.ui.javafx.base.TethysUIFXUtils;

/**
 * Tethys FX Button.
 */
public class TethysUIFXButton
        extends TethysUICoreButton {
    /**
     * The "Null" Margins for a button.
     */
    private static final int NULL_MARGIN = 5;
    /**
     * The node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The button.
     */
    private final Button theButton;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysUIFXButton(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theButton = new Button();
        theNode = new TethysUIFXNode(theButton);
        theButton.setOnAction(e -> handlePressed());
    }

    @Override
    public TethysUIFXNode getNode() {
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
    public void setIcon(final TethysUIArrowIconId pIcon) {
        setIcon(TethysUIFXArrowIcon.getIconForId(pIcon));
    }

    @Override
    public void setIcon(final TethysUIIconId pId) {
        setIcon(TethysUIFXUtils.getIconAtSize(pId, getIconWidth()));
    }

    @Override
    public void setIcon(final TethysUIIcon pIcon) {
        theButton.setGraphic(TethysUIFXIcon.getIcon(pIcon));
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
        theButton.setPadding(new Insets(NULL_MARGIN, NULL_MARGIN, NULL_MARGIN, NULL_MARGIN));
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
