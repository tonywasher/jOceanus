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
package io.github.tonywasher.joceanus.tethys.javafx.control;

import javafx.scene.control.TextArea;

import io.github.tonywasher.joceanus.tethys.core.factory.TethysUICoreFactory;
import io.github.tonywasher.joceanus.tethys.core.control.TethysUICoreTextArea;
import io.github.tonywasher.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * JavaFX TextArea.
 */
public class TethysUIFXTextArea
        extends TethysUICoreTextArea {
    /**
     * The node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The Slider.
     */
    private final TextArea theArea;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysUIFXTextArea(final TethysUICoreFactory<?> pFactory) {
        /* Create resources */
        super(pFactory);
        theArea = new TextArea();
        theNode = new TethysUIFXNode(theArea);
        theArea.setEditable(false);
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theArea.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theArea.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theArea.setPrefHeight(pHeight);
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

    @Override
    public void setText(final String pText) {
        theArea.setText(pText);
    }

    @Override
    public void appendText(final String pText) {
        theArea.appendText(pText);
    }

    @Override
    public void insertText(final String pText,
                           final int pPos) {
        theArea.insertText(pPos, pText);
    }

    @Override
    public void replaceText(final String pText,
                            final int pStart,
                            final int pEnd) {
        theArea.replaceText(pStart, pEnd, pText);
    }

    @Override
    public void setCaretPosition(final int pPos) {
        theArea.positionCaret(pPos);
    }

    @Override
    public int getCaretPosition() {
        return theArea.getCaretPosition();
    }

    @Override
    public int getTextLength() {
        return theArea.getLength();
    }
}
