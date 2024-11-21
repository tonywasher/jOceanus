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
package net.sourceforge.joceanus.tethys.ui.swing.control;

import javax.swing.JPasswordField;

import net.sourceforge.joceanus.tethys.ui.core.control.TethysUICorePasswordField;
import net.sourceforge.joceanus.tethys.ui.core.factory.TethysUICoreFactory;
import net.sourceforge.joceanus.tethys.ui.swing.base.TethysUISwingNode;

/**
 * Swing Password Field.
 */
public class TethysUISwingPasswordField
        extends TethysUICorePasswordField {
    /**
     * The node.
     */
    private final TethysUISwingNode theNode;

    /**
     * PasswordField.
     */
    private final JPasswordField theField;

    /**
     * Constructor.
     * @param pFactory the GUI Factory
     */
    TethysUISwingPasswordField(final TethysUICoreFactory<?> pFactory) {
        super(pFactory);
        theField = new JPasswordField();
        theField.addActionListener(e -> fireEvent());
        theNode = new TethysUISwingNode(theField);
    }

    @Override
    public TethysUISwingNode getNode() {
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
