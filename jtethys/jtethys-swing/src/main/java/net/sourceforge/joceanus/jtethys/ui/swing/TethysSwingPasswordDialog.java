/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.TethysPasswordDialog;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public class TethysSwingPasswordDialog
        extends TethysPasswordDialog {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(TethysSwingPasswordDialog.class);

    /**
     * Dialog.
     */
    private final JDialog theDialog;

    /**
     * Constructor.
     *
     * @param pFactory     the GUI Factory
     * @param pFrame       the frame
     * @param pTitle       the title
     * @param pNeedConfirm true/false
     */
    TethysSwingPasswordDialog(final TethysSwingGuiFactory pFactory,
                              final JFrame pFrame,
                              final String pTitle,
                              final boolean pNeedConfirm) {
        /* Initialise underlying class */
        super(pFactory, pNeedConfirm);

        /* Initialise the dialog */
        theDialog = new JDialog(pFrame, pTitle, true);

        /* If we are confirming */
        if (pNeedConfirm) {
            /* Set a focus traversal policy */
            final JComponent myNode = TethysSwingNode.getComponent(getContainer());
            myNode.setFocusTraversalPolicy(new TraversalPolicy());
            myNode.setFocusTraversalPolicyProvider(true);
        }

        /* Set this to be the main panel */
        theDialog.add(TethysSwingNode.getComponent(getContainer()));
        theDialog.pack();

        /* Set the relative location */
        theDialog.setLocationRelativeTo(pFrame);
    }

    @Override
    protected TethysSwingBorderPaneManager getContainer() {
        return (TethysSwingBorderPaneManager) super.getContainer();
    }

    @Override
    protected void closeDialog() {
        theDialog.setVisible(false);
    }

    @Override
    protected void reSizeDialog() {
        theDialog.pack();
    }

    /**
     * show the dialog.
     */
    private void showTheDialog() {
        /* Show the dialog */
        theDialog.setVisible(true);
        theDialog.toFront();
    }

    @Override
    public boolean showDialog() {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            showTheDialog();

            /* else we must use invokeAndWait */
        } else {
            try {
                SwingUtilities.invokeAndWait(this::showDialog);
            } catch (InvocationTargetException e) {
                LOGGER.error("Failed to display dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /* Return to caller */
        return isPasswordSet();
    }

    /**
     * Focus traversal policy, used so that you tab straight to confirm from password.
     */
    private class TraversalPolicy
            extends FocusTraversalPolicy {
        @Override
        public Component getComponentAfter(final Container pRoot,
                                           final Component pCurrent) {
            /* Handle field order */
            if (pCurrent.equals(TethysSwingNode.getComponent(getPasswordNode()))) {
                return TethysSwingNode.getComponent(getConfirmNode());
            }
            if (pCurrent.equals(TethysSwingNode.getComponent(getConfirmNode()))) {
                return TethysSwingNode.getComponent(getOKButtonNode());
            }
            if (pCurrent.equals(TethysSwingNode.getComponent(getOKButtonNode()))) {
                return TethysSwingNode.getComponent(getCancelButtonNode());
            }
            if (pCurrent.equals(TethysSwingNode.getComponent(getCancelButtonNode()))) {
                return TethysSwingNode.getComponent(getPasswordNode());
            }

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getComponentBefore(final Container pRoot,
                                            final Component pCurrent) {
            /* Handle field order */
            if (pCurrent.equals(TethysSwingNode.getComponent(getPasswordNode()))) {
                return TethysSwingNode.getComponent(getCancelButtonNode());
            }
            if (pCurrent.equals(TethysSwingNode.getComponent(getConfirmNode()))) {
                return TethysSwingNode.getComponent(getPasswordNode());
            }
            if (pCurrent.equals(TethysSwingNode.getComponent(getOKButtonNode()))) {
                return TethysSwingNode.getComponent(getConfirmNode());
            }
            if (pCurrent.equals(TethysSwingNode.getComponent(getCancelButtonNode()))) {
                return TethysSwingNode.getComponent(getOKButtonNode());
            }

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getDefaultComponent(final Container pRoot) {
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getFirstComponent(final Container pRoot) {
            return TethysSwingNode.getComponent(getPasswordNode());
        }

        @Override
        public Component getLastComponent(final Container pRoot) {
            return TethysSwingNode.getComponent(getCancelButtonNode());
        }
    }
}
