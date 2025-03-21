/*******************************************************************************
 * Tethys: Java Utilities
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
package net.sourceforge.joceanus.tethys.javafx.control;

import javafx.scene.control.PasswordField;

import net.sourceforge.joceanus.tethys.core.control.TethysUICorePasswordField;
import net.sourceforge.joceanus.tethys.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.javafx.base.TethysUIFXNode;

/**
 * javaFX Password Field.
 */
public class TethysUIFXPasswordField
        extends TethysUICorePasswordField {
    /**
     * The Node.
     */
    private final TethysUIFXNode theNode;

    /**
     * The Password field.
     */
    private final PasswordField theField;

    /**
     * Constructor.
     *
     * @param pFactory the GUI Factory
     */
    TethysUIFXPasswordField(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theField = new PasswordField();
        theNode = new TethysUIFXNode(theField);
        theField.setOnAction(e -> fireEvent());
    }

    @Override
    public void setPassword(final char[] pPassword) {
        theField.setText(pPassword == null
                ? null
                : new String(pPassword));
    }

    @Override
    public char[] getPassword() {
        final String myText = theField.getText();
        return myText == null
                || myText.length() == 0
                ? null
                : myText.toCharArray();
    }

    @Override
    public TethysUIFXNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theField.setDisable(!pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setManaged(pVisible);
        theNode.setVisible(pVisible);
    }

    @Override
    public void setPreferredWidth(final Integer pWidth) {
        theField.setPrefWidth(pWidth);
    }

    @Override
    public void setPreferredHeight(final Integer pHeight) {
        theField.setPrefHeight(pHeight);
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
