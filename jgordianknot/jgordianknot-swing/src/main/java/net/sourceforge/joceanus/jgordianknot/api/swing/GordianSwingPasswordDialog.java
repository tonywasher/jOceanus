/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jgordianknot.api.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import net.sourceforge.joceanus.jgordianknot.api.impl.GordianPasswordDialog;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingNode;

/**
 * Dialog to request a password. Will also ask for password confirmation if required.
 */
public class GordianSwingPasswordDialog
        extends GordianPasswordDialog {
    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(GordianSwingPasswordDialog.class);

    /**
     * Dialog.
     */
    private final JDialog theDialog;

    /**
     * Constructor.
     *
     * @param pFactory     the GUI Factory
     * @param pTitle       the title
     * @param pNeedConfirm true/false
     */
    GordianSwingPasswordDialog(final TethysSwingGuiFactory pFactory,
                               final String pTitle,
                               final boolean pNeedConfirm) {
        /* Initialise underlying class */
        super(new TethysSwingGuiFactory(), pNeedConfirm);

        /* Initialise the dialog */
        final JFrame myParent = pFactory.getFrame();
        theDialog = new JDialog(myParent, pTitle, true);

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
        theDialog.setLocationRelativeTo(myParent);
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
    public void showDialog() {
        /* Show the dialog */
        theDialog.setVisible(true);
        theDialog.toFront();
    }

    /**
     * Show the dialog under an invokeAndWait clause.
     *
     * @param pDialog the dialog to show
     * @return successful dialog usage true/false
     */
    protected static boolean showTheDialog(final GordianSwingPasswordDialog pDialog) {
        /* If this is the event dispatcher thread */
        if (SwingUtilities.isEventDispatchThread()) {
            /* invoke the dialog directly */
            pDialog.showDialog();

            /* else we must use invokeAndWait */
        } else {
            try {
                SwingUtilities.invokeAndWait(pDialog::showDialog);
            } catch (InvocationTargetException e) {
                LOGGER.error("Failed to display dialog", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        /* Return to caller */
        return pDialog.isPasswordSet();
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
            if (pCurrent.equals(getPasswordNode())) {
                return TethysSwingNode.getComponent(getConfirmNode());
            }
            if (pCurrent.equals(getConfirmNode())) {
                return TethysSwingNode.getComponent(getOKButtonNode());
            }
            if (pCurrent.equals(getOKButtonNode())) {
                return TethysSwingNode.getComponent(getCancelButtonNode());
            }
            if (pCurrent.equals(getCancelButtonNode())) {
                return TethysSwingNode.getComponent(getPasswordNode());
            }

            /* Return a default value */
            return getFirstComponent(pRoot);
        }

        @Override
        public Component getComponentBefore(final Container pRoot,
                                            final Component pCurrent) {
            /* Handle field order */
            if (pCurrent.equals(getPasswordNode())) {
                return TethysSwingNode.getComponent(getCancelButtonNode());
            }
            if (pCurrent.equals(getConfirmNode())) {
                return TethysSwingNode.getComponent(getPasswordNode());
            }
            if (pCurrent.equals(getOKButtonNode())) {
                return TethysSwingNode.getComponent(getConfirmNode());
            }
            if (pCurrent.equals(getCancelButtonNode())) {
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
