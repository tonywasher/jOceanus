/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing;

import javax.swing.JPasswordField;

import net.sourceforge.joceanus.jtethys.ui.TethysPasswordField;

/**
 * Swing Password Field.
 */
public class TethysSwingPasswordField
        extends TethysPasswordField {
    /**
     * The node.
     */
    private TethysSwingNode theNode;

    /**
     * PasswordField.
     */
    private final JPasswordField theField;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysSwingPasswordField(final TethysSwingGuiFactory pFactory) {
        super(pFactory);
        theField = new JPasswordField();
        theField.addActionListener(e -> fireEvent());
        theNode = new TethysSwingNode(theField);
    }

    @Override
    public TethysSwingNode getNode() {
        return theNode;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theField.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setPassword(final char[] pPassword) {
        theField.setText(pPassword == null
                                           ? null
                                           : new String(pPassword));
    }

    @Override
    public char[] getPassword() {
        char[] myPassword = theField.getPassword();
        if (myPassword != null
            && myPassword.length == 0) {
            myPassword = null;
        }
        return myPassword;
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
