/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.swing.dialog;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.sourceforge.joceanus.jtethys.ui.api.dialog.TethysUIAlert;

/**
 * Swing Alert.
 */
public class TethysUISwingAlert
    implements TethysUIAlert {
    /**
     * The Frame.
     */
    private final JFrame theFrame;

    /**
     * The Title.
     */
    private String theTitle;

    /**
     * The Message.
     */
    private String theMessage;

    /**
     * Constructor.
     * @param pFrame the Frame
     */
    TethysUISwingAlert(final JFrame pFrame) {
        if (pFrame == null) {
            throw new IllegalArgumentException("Cannot create Dialog during initialisation");
        }
        theFrame = pFrame;
    }

    @Override
    public void setTitle(final String pTitle) {
        theTitle = pTitle;
    }

    @Override
    public void setMessage(final String pMessage) {
        theMessage = pMessage;
    }

    @Override
    public boolean confirmYesNo() {
        final int myResult = JOptionPane.showConfirmDialog(theFrame, theMessage, theTitle, JOptionPane.YES_NO_OPTION);
        return myResult == JOptionPane.YES_OPTION;
    }

    @Override
    public boolean confirmOKCancel() {
        final int myResult = JOptionPane.showConfirmDialog(theFrame, theMessage, theTitle, JOptionPane.OK_CANCEL_OPTION);
        return myResult == JOptionPane.OK_OPTION;
    }

    @Override
    public void showError() {
        JOptionPane.showMessageDialog(theFrame, theMessage, theTitle, JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void showWarning() {
        JOptionPane.showMessageDialog(theFrame, theMessage, theTitle, JOptionPane.WARNING_MESSAGE);
    }

    @Override
    public void showInfo() {
        JOptionPane.showMessageDialog(theFrame, theMessage, theTitle, JOptionPane.INFORMATION_MESSAGE);
    }
}
